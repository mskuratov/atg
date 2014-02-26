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

package atg.projects.store.assembler.cartridge.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import atg.commerce.catalog.CatalogTools;
import atg.core.util.StringUtils;
import atg.endeca.assembler.AssemblerTools;
import atg.projects.store.assembler.cartridge.CategoryRelatedProductsContentItem;
import atg.projects.store.assembler.cartridge.StoreCartridgeTools;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.collections.filter.CachedCollectionFilter;
import atg.service.collections.filter.FilterException;

import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.assembler.ContentItemInitializer;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;

/**
 * Handler for the CategoryRelatedProducts cartridge. This class is responsible for creating
 * and initializing the CategoryRelatedProductsContentItem. It extends NavigationCartridgeHandler.
 * 
 * The handler stores currently viewed category ID and its related products within the cartridge
 * content item. The related products are retrieved from category repository item properties configured
 * in handler's <code>relatedProductsProperties</code> property. Obtained related products are filtered
 * using the filter component configured within <code>filter</code> property. The handler also limits the number
 * of related products to the number configured in cartridge configuration.
 *
 * @author Natallia Paulouskaya
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/handler/CategoryRelatedProductsHandler.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class CategoryRelatedProductsHandler 
  extends NavigationCartridgeHandler<ContentItem, CategoryRelatedProductsContentItem> {

  //-------------------------------------------
  /** Class version string. */
  protected static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/handler/CategoryRelatedProductsHandler.java#3 $$Change: 788278 $";
      
  //-------------------------------------------------------------------------
  // CONSTANTS
  //-------------------------------------------------------------------------
  
  /** The cartridge numberOfRecords property name */
  public static final String NUMBER_OF_RECORDS = "numberOfRecords";
  /** Default number of records */
  public static final int DEFAULT_NUMBER_OF_RECORDS = -1;
  
  //-------------------------------------------------------------------------
  // PROPERTIES
  //-------------------------------------------------------------------------

  //-------------------------------------------------
  // property: relatedProductsProperties
  //-------------------------------------------------
  private String[] mRelatedProductsProperties = null;
  
  /**
   * @return the list of category properties where category related products are stored.
   */
  public String[] getRelatedProductsProperties() {
    return mRelatedProductsProperties;
  }

  /**
   * @param pRelatedProductsProperties - The list of category properties where category related products are stored.
   */
  public void setRelatedProductsProperties(String[] pRelatedProductsProperties) {
    mRelatedProductsProperties = pRelatedProductsProperties;
  }
  
  //------------------------------------------
  // property: catalogTools
  //------------------------------------------
  protected CatalogTools mCatalogTools = null;

  /**
   * @param pCatalogTools - The CatalogTools object to use when looking up products, categories and skus.
   */
  public void setCatalogTools(CatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  /**
   * @return the CatalogTools object to use when looking up products, categories and skus.
   */
  public CatalogTools getCatalogTools() {
    return mCatalogTools;
  }
  
  //--------------------------------------------------------
  // property: StoreCartridgeTools
  //--------------------------------------------------------
  protected StoreCartridgeTools mStoreCartridgeTools = null;

  /**
   * @param pStoreCartridgeTools - The StoreCartridgeTools helper component.
   */
  public void setStoreCartridgeTools(StoreCartridgeTools pStoreCartridgeTools) {
    mStoreCartridgeTools = pStoreCartridgeTools;
  }

  /**
   * @return The StoreCartridgeTools helper component and skus.
   */
  public StoreCartridgeTools getStoreCartridgeTools() {
    return mStoreCartridgeTools;
  }  
  
  //----------------------------------------------
  // property: filter
  //---------------------------------------------- 
  protected CachedCollectionFilter mFilter = null;
  
  /**
  * @param pFilter The filter that should be used for filtering of feature items.
  */
  public void setFilter(CachedCollectionFilter pFilter) {
    mFilter = pFilter;
  }
  
  /**
   * @return The filter that should be used for filtering of feature items.
   */
  public CachedCollectionFilter getFilter() {
    return mFilter;
  }
  
  //-------------------------------------------------------------------------
  // METHODS
  //-------------------------------------------------------------------------
  
  //-------------------------------------------------------------------------
  /**
   * Creates a new CategoryRelatedProductsContentItem. Populates it with the currently selected category
   * ID and category related products.
   * 
   * @param pCartridgeConfig - This cartridge's configuration content item.
   * 
   * @return a CategoryRelatedProductsContentItem referencing the currently viewed category ID and related products.
   * 
   * @throws CartridgeHandlerException
   */
  @Override
  public CategoryRelatedProductsContentItem process(ContentItem pCartridgeConfig) 
    throws CartridgeHandlerException {   
    
    // Create a CategoryRelatedProductsContentItem content item.
    CategoryRelatedProductsContentItem relatedProducts = new CategoryRelatedProductsContentItem(pCartridgeConfig);
    
    // Retrieve currently viewed category ID using StoreCartridgeTools component.
    String categoryId = getStoreCartridgeTools().getCurrentCategoryId();
    
    // If category ID is found, store category ID and category related products in content item.
    if (!StringUtils.isEmpty(categoryId)){
      
      relatedProducts.setCategoryId(categoryId);
            
      // Get the number of related products that should show up in the cartridge.
      int numberOfItems = 
        ((BasicContentItem)pCartridgeConfig).getIntProperty(NUMBER_OF_RECORDS, DEFAULT_NUMBER_OF_RECORDS);
      
      // Retrieve the specified number of related products for the category.
      List<RepositoryItem> relatedProductsList = getCategoryRelatedProducts(categoryId, numberOfItems);
         
      // Put related products into content item.
      relatedProducts.setRelatedProducts(relatedProductsList);
    }
   
    return relatedProducts;
  }
  
  //-------------------------------------------------------------------------
  /**
   * <p>
   *   Retrieves the specified number of related products from category repository item.
   *   If the <code>pNumberOfItems</code> is set to <code>-1</code> unlimited number
   *   of items is retrieved. 
   * </p>
   * <p>
   *   The related products are filtered using the configured filter.
   * </p>
   *  
   * @param pCategoryId - The category ID to get related products from.
   * @param pNumberOfItems - The number of related products to return.
   * 
   * @return The list of filtered related products limited to specified number.
   */
  protected List<RepositoryItem> getCategoryRelatedProducts(String pCategoryId, int pNumberOfItems) {
    
    // If no category ID is specified, return null.
    if (StringUtils.isEmpty(pCategoryId)){
      return null;
    }
    
    // Look up category repository item for the specified category ID.
    RepositoryItem categoryItem = null;
    
    try {
      categoryItem = getCatalogTools().findCategory(pCategoryId);
    } 
    catch (RepositoryException ex) {
      AssemblerTools.getApplicationLogging().vlogError(
        ex, 
        "CategoryRelatedProductsHandler.getCategoryRelatedProducts: An error occurred when retrieving category for Id: {0}",
        pCategoryId);
    }
  
    if (categoryItem == null) {
      AssemblerTools.getApplicationLogging().vlogError(
        "CategoryRelatedProductsHandler.getCategoryRelatedProducts: No category found for the id: {0}", pCategoryId);
      
      return null;
    }
  
    List<RepositoryItem> relatedProducts = new ArrayList<RepositoryItem>();
  
    // Iterate through specified category properties.
    if (getRelatedProductsProperties() != null) {
      
      for (String propertyName : getRelatedProductsProperties()) {
        // Get the property value. We expect it to be a collection, array or single repository item. 
        Object items = categoryItem.getPropertyValue(propertyName);
        
        try {
          // Handle Collection case.
          if (items instanceof Collection) {
            
            // Filter the list of items and add them to the result list. 
            addRelatedProducts(relatedProducts, (Collection<RepositoryItem>)items, pNumberOfItems);
            
            // If the limit of items number is reached, stop properties iteration.
            if (pNumberOfItems != -1 && relatedProducts.size() == pNumberOfItems) {
              break;
            }
      
            continue;
          }
      
          // Handle Array case.
          if (items instanceof RepositoryItem[]) {
            
            // Filter the list of items and add them to the result list.
            addRelatedProducts(relatedProducts, Arrays.asList(((RepositoryItem[])items)), pNumberOfItems);
            
            // If the limit of items number is reached stop properties iteration.
            if (pNumberOfItems != -1 && relatedProducts.size() == pNumberOfItems) {
              break;
            }
            
            continue;
          }
      
          // Handle single RepositoryItem case.
          if (items instanceof RepositoryItem) {
            
            // Create a single item list in order to pass it to the addFilteredItems method.
            List<RepositoryItem> oneItemList = new ArrayList<RepositoryItem>(1);
            oneItemList.add((RepositoryItem)items);
            
            addRelatedProducts(relatedProducts, oneItemList, pNumberOfItems);
      
            // If the limit of items number is reached, stop properties iteration.
            if (pNumberOfItems != -1 && relatedProducts.size() == pNumberOfItems) {
              break;
            }
          }
      
        } 
        catch (FilterException ex) {
          AssemblerTools.getApplicationLogging().vlogError(
            ex,
            "CategoryRelatedProductsHandler.getCategoryRelatedProducts: An error occurred during related products filtering.");
        }
        
      }
    }
    else {
      AssemblerTools.getApplicationLogging().vlogError(
        "CategoryRelatedProductsHandler.getCategoryRelatedProducts: The relatedProductsProperties is not configured.");
    }
  
    return relatedProducts;
  }
  
  //-------------------------------------------------------------------------
  /**
   * <p>
   *   This method manages adding a collection of repository items to a related products list.
   * </p>
   * <p>
   *   Before adding items, they are filtered using the configured filter. This method also
   *   checks whether the maximum number of items limit is reached.
   * </p>
   * 
   * @param pRelatedProducts - The collection of related products to add items to.
   * @param pItemsToAdd - Items to add to related products collection.
   * @param pMaxNumberOfItems - The maximum number of items allowed in related products collection.
   * 
   * @return the number of items added during the method call.
   * 
   * @throws FilterException thrown when exception occurred during items filtering.
   */
  protected int addRelatedProducts(List<RepositoryItem> pRelatedProducts, 
                                   Collection<RepositoryItem> pItemsToAdd, 
                                   int pMaxNumberOfItems) throws FilterException {
    int itemsCount = 0;
    int initialCount = pRelatedProducts.size();
    
    // if the items number limit is already reached, stop the method execution.
    if (pMaxNumberOfItems != -1 && initialCount == pMaxNumberOfItems) {
      return 0;
    }
    
    Collection<RepositoryItem> filteredCollection = pItemsToAdd;    
    
    // If filter is configured then filter the collection first.
    if (getFilter() != null) {
      filteredCollection = getFilter().filterCollection(pItemsToAdd, null, null);
    }
    
    // Check the number of items in filtered collection and whether we can add all 
    // of the items to the related products list or only some of them.
    
    if (pMaxNumberOfItems == -1 || initialCount + filteredCollection.size() <= pMaxNumberOfItems) {
      pRelatedProducts.addAll(filteredCollection);
      return filteredCollection.size();
    }
    else {
      for (RepositoryItem item : filteredCollection) {
        pRelatedProducts.add(item);
        itemsCount++;
        
        if (initialCount + itemsCount == pMaxNumberOfItems) {
          // We have reached the limit of items.
          break;
        }
      }   
    }
    
    return itemsCount;
  }
  
  //-------------------------------------------------------------------------
  /**
   * Create a new CategoryRelatedProductsConfig using the passed in ContentItem.
   * 
   * 
   * @param pContentItem - The configuration content item for this cartridge handler. This will either be 
   *                       the fully initialized configuration object, if a {@link ContentItemInitializer} 
   *                       has been set, or it will simply be the instance configuration.
   * 
   * @return an instance of <code>ConfigType</code> which wraps the input {@link ContentItem}.
   */
  @Override
  protected ContentItem wrapConfig(ContentItem pContentItem) {
    return new BasicContentItem(pContentItem);
  }
  
}