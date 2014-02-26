/*<ORACLECOPYRIGHT>
 * Copyright (C) 1994-2013 Oracle and/or its affiliates. All rights reserved.
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.
 * UNIX is a registered trademark of The Open Group.
 *
 * This software and related documentation are provided under a license agreement 
 * containing restrictions on use and disclosure and are protected by intellectual property laws. 
 * Except as expressly permitted in your license agreement or allowed by law, you may not use, copy, 
 * reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, 
 * or display any part, in any form, or by any means. Reverse engineering, disassembly, 
 * or decompilation of this software, unless required by law for interoperability, is prohibited.
 *
 * The information contained herein is subject to change without notice and is not warranted to be error-free. 
 * If you find any errors, please report them to us in writing.
 *
 * U.S. GOVERNMENT RIGHTS Programs, software, databases, and related documentation and technical data delivered to U.S. 
 * Government customers are "commercial computer software" or "commercial technical data" pursuant to the applicable 
 * Federal Acquisition Regulation and agency-specific supplemental regulations. 
 * As such, the use, duplication, disclosure, modification, and adaptation shall be subject to the restrictions and 
 * license terms set forth in the applicable Government contract, and, to the extent applicable by the terms of the 
 * Government contract, the additional rights set forth in FAR 52.227-19, Commercial Computer Software License 
 * (December 2007). Oracle America, Inc., 500 Oracle Parkway, Redwood City, CA 94065.
 *
 * This software or hardware is developed for general use in a variety of information management applications. 
 * It is not developed or intended for use in any inherently dangerous applications, including applications that 
 * may create a risk of personal injury. If you use this software or hardware in dangerous applications, 
 * then you shall be responsible to take all appropriate fail-safe, backup, redundancy, 
 * and other measures to ensure its safe use. Oracle Corporation and its affiliates disclaim any liability for any 
 * damages caused by use of this software or hardware in dangerous applications.
 *
 * This software or hardware and documentation may provide access to or information on content, 
 * products, and services from third parties. Oracle Corporation and its affiliates are not responsible for and 
 * expressly disclaim all warranties of any kind with respect to third-party content, products, and services. 
 * Oracle Corporation and its affiliates will not be responsible for any loss, costs, 
 * or damages incurred due to your access to or use of third-party content, products, or services.
 </ORACLECOPYRIGHT>*/


package atg.projects.store.sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;
import atg.service.util.SortArray;

/**
 * A ProductSorter implementation which sorts a list of product repository items by price.
 * 
 * @author ckearney
 */
public class PriceSorter extends GenericService implements PropertySorter{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/sort/PriceSorter.java#3 $$Change: 788278 $";

  
  //-----------------------------------
  
  //-----------------------------------
  // PROPERTIES
  //-----------------------------------

  //-----------------------------------
  // property: mPricingTools
  private PricingTools mPricingTools;
  /** Gets the PricRangeDroplet component **/
  public PricingTools getPricingTools() {
    return mPricingTools;
  }
  /** Sets the PricRangeDroplet component **/
  public void setPricingTools(PricingTools pPricingTools) {
    mPricingTools = pPricingTools;
  }
  
  //-----------------------------------
  // property: mPriceProperty
  private String mPriceProperty;
  /** @return The name of the property to do the sorting on */
  public String getPriceProperty() {
    return mPriceProperty;
  }
  /** @param pPriceProperty The name of the property to do the sorting on */
  public void setPriceProperty(String pPriceProperty) {
    mPriceProperty = pPriceProperty;
  }
  
  //-----------------------------------
  // METHODS
  //-----------------------------------
  /**
   * Implementation of the SimpleSorter.sort method. Sorts a list of items
   * pItems by price in pDirection. 
   * 
   * @param pItems An unsorted list of RepositoryItems
   * @param pDirection The direction of the sort, "ascending" or "descending"
   * @param pRequest The current http request
   * @param pServlet The calling servlet
   */
  @Override
  public Object[] sort(List pItems, String pDirection, Locale pLocale){    
    
    /*
     * Parameter validation
     */
    if(pItems == null || pItems.size() == 0){
      return new Object[0];
    }
    
    String sortDirection = DESCENDING_SYMBOL;
    if(pDirection != null){
      if(pDirection.equals(ASCENDING)){
        sortDirection = ASCENDING_SYMBOL;
      }
    }
    
    if(pLocale == null){
      pLocale = getNucleus().getDefaultLocale();
    }
    
    /*
     * Retrieve the price for each product and store
     * the product and price in the same object.
     */
    ArrayList productPriceList = new ArrayList();
    
    for(Object product : pItems){
      if(product instanceof RepositoryItem){
        double highestPrice = 0.0;
        double lowestPrice = 0.0;
        
        if(getPricingTools().isUsingPriceLists()){
          try {
            highestPrice = getPricingTools().retrieveHighestPriceListPrice((RepositoryItem)product, null, null);
            lowestPrice = getPricingTools().retrieveLowestPriceListPrice((RepositoryItem)product, null, null);
          }
          catch (PriceListException e) {
            if(isLoggingError()){
              logError("An exception was caught while trying to determine the highest and lowest " 
                + "price of the product from the price lists");
            }
          }
          
        }
        else{
          highestPrice = getPricingTools().retrieveHighestChildSKUPrice((RepositoryItem)product);
          lowestPrice = getPricingTools().retrieveLowestChildSKUPrice((RepositoryItem)product);
        }
        
        // Store the product and price
        ProductPriceWrapper currentNode = null;
        
        // When there are multiple prices and the direction of the
        // sort is ascending we will sort on the lowest price
        if(pDirection.equals(ASCENDING)){
          currentNode = new ProductPriceWrapper((RepositoryItem)product, lowestPrice);
        }
        // When there are multiple prices and the direction of the
        // sort is descending we will sort on the highest price
        else{
          currentNode = new ProductPriceWrapper((RepositoryItem)product, highestPrice);
        }
        
        if(currentNode != null){
          productPriceList.add(currentNode);
        }
      }      
    }
    
    /*
     * Sorting
     */
    SortArray sortArray = new SortArray();      
    sortArray.setSortDirections(sortDirection);
    sortArray.setSortProperties(new String[] {getPriceProperty()});
    sortArray.setLocale(pLocale);
    sortArray.setInputArray(productPriceList.toArray());
    
    Object[] sortedArray = sortArray.getOutputArray();
    
    /*
     * Extract the RepositoryItems from the Wrapper 
     */
    if(sortedArray == null){
      return pItems.toArray();
    }
    else{
      for(int i = 0; i < sortedArray.length; i++){
        sortedArray[i] = ((ProductPriceWrapper)sortedArray[i]).getProduct();
      }
    }
    
    return sortedArray;
  }
}