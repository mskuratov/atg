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



package atg.projects.store.collections.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import atg.projects.store.catalog.StoreCatalogTools;
import atg.projects.store.inventory.StoreInventoryManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.collections.filter.FilterException;
import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.MissingInventoryItemException;
import atg.commerce.catalog.custom.CatalogProperties;
import atg.commerce.catalog.custom.CustomCatalogTools;
import atg.commerce.collections.filter.InventoryFilter;

/**
 * This filter will filter products or skus in the collection based on their inventory
 * availability.
 * <p>
 * The availability state for each sku is checked against the InventoryManager. If any of a product's
 * skus are considered available, the product is considered available and is added to the
 * filtered results.
 * <p>
 * The inventory states that mean a sku is available are configurable using the
 * <tt>inclludeInventoryStates</tt> property. See the InventoryManager for
 * valid states.
 * <p>
 * The InventoryFilter cache should be disabled if your Inventory Manager is already caching
 * inventory status information.
 *
 * @see atg.commerce.inventory.InventoryManager
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/collections/filter/SkuInventoryFilter.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */

public class SkuInventoryFilter extends InventoryFilter

{

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/collections/filter/SkuInventoryFilter.java#3 $$Change: 788278 $";
  
  /**
   * The comparator that will be used to sort filtered collection.
   */
  private Comparator mComparator;
  
  /**
   * Returns the comparator object to use for sorting of filtered collection.
   * @return the comparator object to use for sorting of filtered collection.
   */
  public Comparator getComparator() {
   return mComparator;
  }
  
  /**
   * Sets the comparator object to use for sorting of filtered collection.
   * @param pComparator the comparator object to use for sorting of filtered collection.
   */
  public void setComparator(Comparator pComparator) {
    mComparator = pComparator;
  }

  /**
   * Overrides base method to allow to pass not only collection of products but
   * collection of SKUs too.
   */
  @Override
  protected Collection generateFilteredCollection(Collection pUnfilteredCollection,
                                                String pCollectionIdentifierKey,
                                                RepositoryItem pProfile)
    throws FilterException
  { 
    
    Collection returnCollection = generateNewCollectionObject(pUnfilteredCollection);

    try
    {
      StoreCatalogTools catalogTools = (StoreCatalogTools) getCatalogTools();

      // Get the inventory states that mean a sku is available and create
      // a list for easy lookup.
      Integer [] includeInventoryStates = getIncludeInventoryStates();
      List availableStates;
      if(includeInventoryStates == null || includeInventoryStates.length == 0){
        availableStates = new ArrayList();
      }else{
        availableStates = Arrays.asList(includeInventoryStates);
      }

      if(isLoggingDebug()){
        logDebug("IncludeInventoryStates are: " + availableStates);
      }

      // Loop through the unfiltered collection
      for (Object nextObject : pUnfilteredCollection){
        
        if(isLoggingDebug()){
          logDebug("generateFilteredCollection: nextObject is " + nextObject);
        }
        

        // If the object is SKU repository item just chekc its availability
        if(catalogTools.isSku(nextObject)){
          if (isSkuAvailable((RepositoryItem)nextObject, availableStates)){
            // Sku's inventory state matches the one in include inventory states list.
            // Add it to the return collection and continue.
            returnCollection.add(nextObject);
          }
          
          continue;
        }
        
        // Nor SKU repository item
        // Get item's SKUs
        Collection<RepositoryItem> skus = getItemSkus(nextObject);

        if( skus != null){
          
          for (RepositoryItem sku : skus){
            
            if(isLoggingDebug()){
              logDebug("generateFilteredCollection: checking sku availability " + sku);
            }

            try{
              
              if (isSkuAvailable(sku, nextObject, availableStates)){
                
                // Sku's inventory state matches the one in include inventory states list.
                // Add it to the return collection and continue.
                returnCollection.add(nextObject);
                
                if(isLoggingDebug()){
                  logDebug("generateFilteredCollection: add item to return collection " + nextObject);
                }
                
                break;
              }
              
            }
            catch(MissingInventoryItemException exc){
            
              // SKUs that aren't in the inventory will NOT be filtered out
              if(isLoggingWarning()){
                logWarning(exc);
              }
              
              returnCollection.add(nextObject);
              
              if(isLoggingDebug()){
                logDebug("generateFilteredCollection: add item to return collection " + nextObject);
              }
              
              break;
            }
          }
        }
      }

      // If we didn't filter anything out, just return
      // the original collection. this will optimize the cleanup of the new collection
      // object.
      if(returnCollection.size() == pUnfilteredCollection.size() && getComparator() == null){
        return pUnfilteredCollection;
      }
      else{
        
        // If comparator object is configured for the filter, use it to
        // sort filtered collection, otherwise return it as is.
        if (getComparator()!= null){
          List sortedList = new ArrayList(returnCollection.size());
          sortedList.addAll(returnCollection);
          Collections.sort(sortedList, getComparator());
          return sortedList;
        }
        
        return returnCollection;
      }
    }catch(RepositoryException exc){
      throw new FilterException(exc);
    }
    catch(InventoryException exc){
      throw new FilterException(exc);
    }
    catch(ClassCastException exc){
      throw new FilterException(exc);
    }
  }
  
  /**
   * Returns the collection of product's skus if the passed in item is product
   * otherwise null.
   * @param pItem the repository item to look for the SKUs
   * @return the collection of SKUs
   * @throws RepositoryException
   */
  protected Collection<RepositoryItem> getItemSkus(Object pItem) throws RepositoryException{
    Collection<RepositoryItem> skus = null;
    
    if(getCatalogTools().isProduct(pItem))
    {
      String skuPropertyName = getCatalogServices().getProductSKUPropertyName();
      RepositoryItem productItem = (RepositoryItem) pItem;
      
      // Get product's SKUs
      skus = (Collection<RepositoryItem>) productItem.getPropertyValue(skuPropertyName);
      
    }
    
    return skus;
  }
  
  /**
   * Checks whether the SKU is available according to its inventory status. The list of available
   * inventory states are passed in.
   * @param pSku SKU repository item to check availability of
   * @param pParentItem SKU parent item, e.g. product
   * @param pAvailableStates the list of available inventory states.
   * @return true if the SKU is available.
   * @throws InventoryException
 * @throws RepositoryException 
   */
  protected boolean isSkuAvailable(RepositoryItem pSku, Object pParentItem, List pAvailableStates ) throws InventoryException, RepositoryException{
    if (pAvailableStates == null){
      return false;
    }
    
    CatalogProperties catalogProperties = ((CustomCatalogTools)getCatalogTools()).getCatalogProperties();
    
    // get sku's parent product
    RepositoryItem product = null;
    if (pParentItem != null && getCatalogTools().isProduct(pParentItem)){
      product = (RepositoryItem) pParentItem;
    }else{
      // just get the first parent product
      Collection parentProducts = (Collection) pSku.getPropertyValue(catalogProperties.getParentProductsPropertyName());
      product = (RepositoryItem)parentProducts.iterator().next();
    }
    
    int availabiltyStatus = ((StoreInventoryManager)getInventoryManager()).queryAvailabilityStatus(product, pSku.getRepositoryId());

    if(isLoggingDebug())
      logDebug("generateFilteredCollection: checking sku availability " + pSku + " status is " + availabiltyStatus);

    //if the sku is available, add the product to the filtered collection
    return pAvailableStates.contains(Integer.valueOf(availabiltyStatus));
  }
  
  /**
   * Checks whether the SKU is available according to its inventory status. The list of available
   * inventory states are passed in.
   * @param pSku SKU repository item to check availability of
   * @param pAvailableStates the list of available inventory states.
   * @return true if the SKU is available.
   * @throws InventoryException
 * @throws RepositoryException 
   */
  protected boolean isSkuAvailable(RepositoryItem pSku, List pAvailableStates ) throws InventoryException, RepositoryException{
    return isSkuAvailable(pSku, null, pAvailableStates );
  }
  


}
