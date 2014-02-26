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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import atg.commerce.catalog.custom.CustomCatalogTools;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteGroupManager;
import atg.multisite.SiteManager;
import atg.projects.store.catalog.StoreCatalogProperties;
import atg.projects.store.catalog.StoreCatalogTools;
import atg.projects.store.profile.StoreProfileTools;
import atg.projects.store.profile.StorePropertyManager;
import atg.projects.store.profile.recentlyviewed.RecentlyViewedTools;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.collections.filter.CachedCollectionFilter;
import atg.service.collections.filter.FilterException;
import atg.service.util.CurrentDate;

/**
 * This filter generates a valid collection of recentlyViewedProduct items.
 *  
 * @author David Stewart
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/collections/filter/RecentlyViewedFilter.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class RecentlyViewedFilter extends CachedCollectionFilter
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/collections/filter/RecentlyViewedFilter.java#2 $$Change: 768606 $";

  //----------------------------------------------------------------------------------
  // Constants
  //----------------------------------------------------------------------------------

  private static final String SIZE = "size";
  private static final String EXCLUDE = "exclude";
  
  //----------------------------------------------------------------------------------
  // Properties
  //----------------------------------------------------------------------------------

  //-------------------------------------------
  // property: siteGroupManager
  //-------------------------------------------
  protected SiteGroupManager mSiteGroupManager;
  
  /**
   * Returns the SiteGroupManager instance
   * @return the SiteGroupManager
   */
  protected SiteGroupManager getSiteGroupManager()
  {
    return mSiteGroupManager;
  }
  /**
   * Sets the SiteGroupManager instance
   * @param pSiteGroupManager the siteGroupManager to set
   */
  public void setSiteGroupManager(SiteGroupManager pSiteGroupManager)
  {
    mSiteGroupManager = pSiteGroupManager;
  }

  //-----------------------------------------------
  // property: profileTools
  //-----------------------------------------------
  protected StoreProfileTools mProfileTools = null;

  /**
   * @return the ProfileTools
   */
  public StoreProfileTools getProfileTools() {
    return mProfileTools;
  }
  /**
   * @param pProfileTools the ProfileTools to set
   */
  public void setProfileTools(StoreProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }

  
  //-----------------------------------------------
  // property: catalogTools
  //-----------------------------------------------
  protected StoreCatalogTools mCatalogTools = null;

  /**
   * @return the catalogTools
   */
  public StoreCatalogTools getCatalogTools() {
    return mCatalogTools;
  }
  /**
   * @param pCatalogTools the catalogTools to set
   */
  public void setCatalogTools(StoreCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }
  
  
  //------------------------------------------
  // property: recentlyViewedTools
  //------------------------------------------
  protected RecentlyViewedTools mRecentlyViewedTools = null;

  /**
   * @return the ProfileTools
   */
  public RecentlyViewedTools getRecentlyViewedTools() {
    return mRecentlyViewedTools;
  }

  /**
   * @param pProfileTools the ProfileTools to set
   */
  public void setRecentlyViewedTools(RecentlyViewedTools pRecentlyViewedTools) {
    mRecentlyViewedTools = pRecentlyViewedTools;
  }
  
  //------------------------------
  // property: maxItemsToDisplay
  //------------------------------
  private int mMaxProductsToDisplay = 5;
  
  /**
   * @param pMaxProductsToDisplay The maximum number of items to display.
   */
  public void setMaxProductsToDisplay(int pMaxProductsToDisplay) {
    mMaxProductsToDisplay = pMaxProductsToDisplay;
  }
  /**
   * @return The maximum number of items to display.
   */
  public int getMaxProductsToDisplay() {
    return mMaxProductsToDisplay;
  }
  
  //----------------------------------------------------------------------------------
  //  METHODS
  //----------------------------------------------------------------------------------

  //----------------------------------------------------------------------------------
  /**
   * This method builds a list of recentlyViewedProduct items using the following rules:
   * 
   * <ul>
   *   <li>Add a product if it was viewed on the site relevant to current site context and siteScope.</li>
   *   <li>Products belonging to the 'exclude' key of 'pExtraParameters' Map will not be added to filtered list.</li>
   *   <li>Expired products will be removed from the repository and not added to the filtered list.</li>
   *   <li>If the site that the product was viewed on is now disabled or inactive, don't add to filtered list.</li>
   *   <li>If a product in the unfiltered list is no longer available, don't add to the filtered list.</li>
   *   <li>The size of the filtered list will be defined either by 'mMaxProductsToDisplay' or overridden by
   *       the value of the 'size' key in the 'pExtraParameters' Map.</li>
   * </ul>
   * 
   * The unfiltered collection passed to this method must be a list of recentlyViewedProduct RepositoryItems. 
   * 
   * <p>
   *   @param pUnfilteredCollection Unfiltered collection of elements
   *   @param pCollectionIdentifierKey Identifier key.
   *   @param pProfile Profile The user profile that the filtered collection is being generated for.
   *   @param pExtraParameters A map of extra parameters.
   * 
   *   @return Collection of recentlyViewedProduct items otherwise empty list. 
   * </p>
   */
  @Override
  protected Collection generateFilteredCollection(Collection pUnfilteredCollection, 
                                                  String pCollectionIdentifierKey,
                                                  RepositoryItem pProfile, 
                                                  Map pExtraParameters) throws FilterException {
    
    Collection<RepositoryItem> resultCollection = new ArrayList<RepositoryItem>();
    
    if(pUnfilteredCollection == null || pUnfilteredCollection.isEmpty()){
      if (isLoggingDebug()) {
        logDebug("The unfiltered collection was either null or empty. Returning empty list.");
      }
      return resultCollection;
    }

    if (pUnfilteredCollection instanceof Collection<?> && !(((List)pUnfilteredCollection).get(0) instanceof RepositoryItem)) {
      if (isLoggingDebug()) {
        logDebug("The unfiltered collection did not contain a list of RepositoryItems. Returning empty list.");
      }
      return resultCollection;
    }
    
    List<RepositoryItem> recentlyViewed = (List<RepositoryItem>) pUnfilteredCollection;

    // Only proceed if recentlyViewed property has any items.
    if (recentlyViewed != null && recentlyViewed.size() > 0) {
      String siteScope = getRecentlyViewedTools().getSiteScope();
      
      // Only assign the current site id to this variable if it is not null. An empty
      // string must be used otherwise, so that we can invoke it's 'equals' method.
      String currentSiteId = "";
      
      if (!StringUtils.isEmpty(SiteContextManager.getCurrentSiteId())) {
        currentSiteId = SiteContextManager.getCurrentSiteId();
      }
      
      List<String> excludedProducts = new ArrayList<String>();
      
      if(pExtraParameters != null){
        
        Object excludeParameters = pExtraParameters.get(EXCLUDE);
        
        // If the excluded products parameter is a Collection, it should contain 
        // either 'product' RepositoryItems OR 'product' repository id strings.
        if (excludeParameters instanceof Collection<?>) {
          if (((List)excludeParameters).size() > 0 && 
              ((List)excludeParameters).get(0) instanceof RepositoryItem) {
            List<RepositoryItem> tempList = (List<RepositoryItem>)excludeParameters;
            for (RepositoryItem ri : tempList) {
              if (isLoggingDebug()) {
                logDebug("Adding product " + ri.getRepositoryId() + " to the excluded products list.");
              }
              excludedProducts.add(ri.getRepositoryId());
            }
          }
          else if (((List)excludeParameters).size() > 0 && 
                   ((List)excludeParameters).get(0) instanceof String) {
            excludedProducts = (List<String>)excludeParameters;
            if (isLoggingDebug()) {
              for (String productId : excludedProducts) {
                logDebug(productId + " has been added to excluded products list.");
              }
            }
          }
        }

        // If the excluded products parameter is a String object, it should be a 
        // 'product' repository id string.
        if (excludeParameters instanceof String) {
          excludedProducts.add((String) excludeParameters);
          if (isLoggingDebug()) {
            logDebug((String)excludeParameters + " has been added to the excluded products list.");
          }
        }
        
        String listSize = (String) pExtraParameters.get(SIZE);
        
        if (!StringUtils.isEmpty(listSize)) {
          try {
            int size = Integer.parseInt(listSize);
            setMaxProductsToDisplay(size);
            if (isLoggingDebug()) {
              logDebug("The value of the " + SIZE + " parameter '" + size + "' has been set as the " +
                "maximum number of products to display in the recently viewed list.");
            }
          }
          catch (NumberFormatException nfe) {
            if (isLoggingError()){
              logError("The '" + SIZE + "' parameter is not a number. ", nfe);  
            }
          }
        }
      }

      // Remove non-existent products from the unfiltered collection.
      try {
        if (getRecentlyViewedTools().removeNonExistentProducts(recentlyViewed, pProfile)) {
          if (isLoggingDebug()) {
            logDebug("Non-existent product(s) have been removed from the repository.");
          }
        }
      } 
      catch (RepositoryException re) {
        if (isLoggingError()) {
          logError("There was a problem removing non-existent products from profile " + pProfile.getRepositoryId() + "\nre");
        }
      }

      // Remove expired products from the unfiltered collection.
      try {
        if (getRecentlyViewedTools().removeExpiredProducts(recentlyViewed, pProfile)) {
          if (isLoggingDebug()) {
            logDebug("Expired product(s) have been removed from the repository.");
          }
        }
      } 
      catch (RepositoryException re) {
        if (isLoggingError()) {
          logError("There was a problem removing expired products from profile " + pProfile.getRepositoryId() + "\nre");
        }
      }
      
      String recentlyViewedProductsPropertyName = 
        ((StorePropertyManager) getProfileTools().getPropertyManager()).getRecentlyViewedProductsPropertyName();
      
      // Get the user's recentlyViewedProducts list as expired products may have been removed.
      recentlyViewed = (List<RepositoryItem>) 
        pProfile.getPropertyValue(recentlyViewedProductsPropertyName);
    
      // Get the recentlyViewedProduct 'product' and 'siteId' property names.
      String recentlyViewedProductProductPropertyName = 
        ((StorePropertyManager) getProfileTools().getPropertyManager()).getProductPropertyName();
      String recentlyViewedProductSiteIdPropertyName = 
        ((StorePropertyManager) getProfileTools().getPropertyManager()).getSiteIdPropertyName();
      
      // Iterate through the recentlyViewed item list, ensuring that each item passes a number
      // of validation rules before being added to the filtered list. Once the list gets to the
      // max number of products to display size, return the list.
      
      unfilteredProducts:
      for (int i = 0; i < recentlyViewed.size(); i++) {
        
        // Retrieve the product and siteId values from the current recentlyViewed repository item.
        RepositoryItem product = (RepositoryItem) 
          recentlyViewed.get(i).getPropertyValue(recentlyViewedProductProductPropertyName);
        String siteId = (String) 
          recentlyViewed.get(i).getPropertyValue(recentlyViewedProductSiteIdPropertyName);
        
        // If the siteId varaible is null, an empty string must be used in order for it's
        // 'equals' method to be invoked.
        if (siteId == null) {
          siteId = "";
        }
        
        // If the current product id is in the excluded products list, don't add to filtered list.
        if (excludedProducts != null) {
          if (excludedProducts.contains(product.getRepositoryId()) &&
              currentSiteId.equals(siteId)) {
            if (isLoggingDebug()) {
              logDebug(product.getRepositoryId() + 
                " is in the excluded list and has not been included in the filtered list.");
            }
            continue unfilteredProducts;
          }
        }
        
        // Ensure that the current product's site id has the correct scope to be included in the
        // filtered list, if not, don't add to filtered list.
        if (!getRecentlyViewedTools().isSiteInScope(siteId, currentSiteId)) {
          if (isLoggingDebug()) {
            logDebug("Product: " + product.getRepositoryId() + " is not valid for siteScope '" + siteScope + 
                "' and has not been included in the filtered list.");
          }
          continue unfilteredProducts;
        }
        
        // In a 'nosite' environment, it's not necessary to check whether a site is active/enabled.
        if (!siteId.equals("")) {
          RepositoryItem site = null;
          try {
            site = SiteManager.getSiteManager().getSite(siteId);
          } catch (RepositoryException re) {
            if (isLoggingError()) {
              logError("There was a problem retrieving site. " + siteId + "\n", re);
            }
          }
          
          // If a site is disabled or not available, don't add to filtered list.
          if (site == null || 
              !SiteManager.getSiteManager().isSiteActive(site) || 
              !SiteManager.getSiteManager().isSiteEnabled(site)) {
            if (isLoggingDebug()) {
              logDebug("Site " + siteId + " is not currently active/enabled, not adding " + 
                product.getRepositoryId() + " to the filtered list.");
            }
            continue unfilteredProducts;
          }
        }  

        try {
          // If the product in the recentlyViewedProducts list for some reason doesn't
          // exist any more, don't add to the filtered list.
          if (product != null && 
              getCatalogTools().findProduct(product.getRepositoryId()) == null){
            if (isLoggingDebug()) {
              logDebug("Product " + product.getRepositoryId() + 
                " cannot be found so will not be included in the filtered list.");
            }
            continue unfilteredProducts;
          }
        } 
        catch (RepositoryException re) {
          if (isLoggingError()) {
            logError("There was a problem removing expired products from profile " + 
              pProfile.getRepositoryId(), re);
          }
        }
        
        // Check if the product's start/end date is valid against the current date.
        if (!this.getRecentlyViewedTools().getItemDateValidator().validateObject(product)) {
          if (isLoggingDebug()) {
            logDebug("Product " + product.getRepositoryId() + 
              " - The start/end date is not valid and will not be displayed.");
          }
          continue unfilteredProducts;
        }
        
        if (isLoggingDebug()) {
          logDebug("Adding " + product.getRepositoryId() + " to the filtered list.");
        }
        
        // When we have reached this stage, the recentlyViewedProduct is fine to add
        // to the filtered list.
        resultCollection.add(recentlyViewed.get(i));
        
        // When the filtered list has reached it's max products to display size,
        // break out of loop and return the list.
        if (resultCollection.size() == getMaxProductsToDisplay()) {
          if (isLoggingDebug()) {
            logDebug("The filtered list size has reached its maximum capacity '" + 
              getMaxProductsToDisplay() + "'. Returning filtered list.");
          }
          break unfilteredProducts;
        }
      }
    }
    return resultCollection;
  }
  
}
