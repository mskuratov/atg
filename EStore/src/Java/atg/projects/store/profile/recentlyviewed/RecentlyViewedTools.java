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



package atg.projects.store.profile.recentlyviewed;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import atg.multisite.SiteGroupManager;
import atg.nucleus.GenericService;
import atg.projects.store.collections.validator.StartEndDateValidator;
import atg.projects.store.profile.StoreProfileTools;
import atg.projects.store.profile.StorePropertyManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.util.CurrentDate;

/**
 * This class provides the low level functionality for recently viewed item creation/manipulation.  
 * It performs the calls required to read and write information to and from the userProfile repository.
 * <p>
 *   There are also some convenience methods for general recently viewed functionality.
 * </p> 

 * @author David Stewart
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/profile/recentlyviewed/RecentlyViewedTools.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class RecentlyViewedTools extends GenericService {
  
  //----------------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------------

  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/profile/recentlyviewed/RecentlyViewedTools.java#3 $$Change: 788278 $";

  public static final String CURRENT_SITE_SCOPE = "current";
  public static final String ALL_SITE_SCOPE = "all";
  
  //----------------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------------
  
  //-------------------------------------------
  // property: siteGroupManager
  //-------------------------------------------
  protected SiteGroupManager mSiteGroupManager;
  
  /**
   * @return A SiteGroupManager instance.
   */
  protected SiteGroupManager getSiteGroupManager()
  {
    return mSiteGroupManager;
  }
  
  /**
   * @param pSiteGroupManager A SiteGroupManager instance.
   */
  public void setSiteGroupManager(SiteGroupManager pSiteGroupManager)
  {
    mSiteGroupManager = pSiteGroupManager;
  }
  
  //---------------------------------------------
  // property: profileTools
  //---------------------------------------------
  private StoreProfileTools mProfileTools = null;

  /**
   * @return A StoreProfileTools instance.
   */
  public StoreProfileTools getProfileTools() {
    return mProfileTools;
  }

  /**
   * @param pProfileTools A StoreProfileTools instance.
   */
  public void setProfileTools(StoreProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }
  
  //-----------------------------------
  // property: recentlyViewedSize
  //-----------------------------------
  private int mRecentlyViewedSize = -1;

  /**
   * @return The maximum size of a user's recently viewed list.
   */
  public int getRecentlyViewedSize() {
    return mRecentlyViewedSize;
  }

  /**
   * @param pRecentlyViewedSize The maximum size of a user's recently viewed list.
   */
  public void setRecentlyViewedSize(int pRecentlyViewedSize) {
    // -1 indicates that the recently viewed list size should be infinite.
    if (pRecentlyViewedSize == -1) {
      setListSizeInfinite(true);
    } 
    else {
      setListSizeInfinite(false);
    }
    mRecentlyViewedSize = pRecentlyViewedSize;
  }

  //---------------------------------------
  // property: listSizeInfinite
  //---------------------------------------
  private boolean mListSizeInfinite = true;
  
  /**
   * @param pListSizeInfinite A flag determining whether the recently viewed list size should be infinite or not.
   */
  public void setListSizeInfinite(boolean pListSizeInfinite) {
    mListSizeInfinite = pListSizeInfinite;
  }
  
  /**
   * @return A flag determining whether the recently viewed list size should be infinite or not.
   */
  public boolean isListSizeInfinite() {
    return mListSizeInfinite;
  }
  
  //----------------------------
  // property: expiryPeriod
  //----------------------------
  private int mExpiryPeriod = 0;
  
  /**
   * @param pExpiryPeriod The expiry period for a recentlyViewedItem. '0' means that there is no
   * expiry period.
   */
  public void setExpiryPeriod(int pExpiryPeriod) {
    mExpiryPeriod = pExpiryPeriod;
  }
  
  /**
   * @return The expiry period for a recentlyViewedItem. 0 means that there is no
   * expiry period.
   */
  public int getExpiryPeriod() {
    return mExpiryPeriod;
  }
  
  //--------------------------------
  // property: siteScope
  //--------------------------------
  private String mSiteScope = "all";
  
  /**
   * @param pSiteScope The site scope (all/current/shareableId).
   */
  public void setSiteScope(String pSiteScope) {
    mSiteScope = pSiteScope;
  }
  
  /**
   * @return The site scope (all/current/shareableId).
   */
  public String getSiteScope() {
    return mSiteScope;
  }
  
  //--------------------------------------------------
  // itemDateValidator
  //--------------------------------------------------
  private StartEndDateValidator mItemDateValidator = null;
  
  /**
   * @param pItemDateValidator The ItemDateValidator component.
   */
  public void setItemDateValidator(StartEndDateValidator pItemDateValidator) {
    mItemDateValidator = pItemDateValidator;
  }
  
  /**
   * @return The ItemDateValidator component. 
   */
  public StartEndDateValidator getItemDateValidator() {
    return mItemDateValidator;
  }
  
  //-------------------------------
  // currentDate
  //-------------------------------
  private CurrentDate mCurrentDate;
  
  /**
   * @param pCurrentDate The CurrentDate component.
   */
  public void setCurrentDate(CurrentDate pCurrentDate) { 
    mCurrentDate = pCurrentDate; 
  }
  
  /**
   * @return The CurrentDate component.
   */
  public CurrentDate getCurrentDate() { 
    return mCurrentDate; 
  }
  
  
  //----------------------------------------------------------------------------------
  /**
   * Puts product into the list of recently viewed products for the current profile.
   * 
   * This method also takes care of removing duplicate and expired products and
   * maintains 'virtual' lists of products based on siteScope. 
   * 
   * <p>
   *   The term 'virtual' list is used because each user's recently viewed list will contain 
   *   every product browsed no matter what site it was viewed on. The siteScope property
   *   is then taken into account and a conceptual list is maintained for products in that 
   *   particular siteScope and currently viewed site. This is needed to determine how many 
   *   products from each site should be held in the list.
   * </p>
   * 
   * <p>
   *   So for example, if the maximum number of products that can be held in a list is 10. 
   *   If storeA and storeB both share a ShoppingCart shareableType and storeC 
   *   doesn't share anything; storeA and storeB will share a conceptual list of 10 products
   *   and storeC will hold a conceptual list of 10 products. This means that the recently 
   *   viewed user profile list for a a user can hold a total of 20 products. 
   * </p>
   * 
   * @param pItem A repository item to add to the list of recently viewed items.
   * @param pProfile A user profile repository item.
   * @param pSiteId The site id associated with the product to be added.
   * 
   * @RepositoryException RepositoryException If there is a problem adding/removing a product.
   */
  public void addProduct(RepositoryItem pItem, RepositoryItem pProfile, String pSiteId) throws RepositoryException {

    if (getSiteScope() != null && 
        !(getSiteGroupManager().isShareableTypeRegistered(getSiteScope()) ||
        (getSiteScope().equals(CURRENT_SITE_SCOPE)) || (getSiteScope().equals(ALL_SITE_SCOPE)))) {
      
      if (isLoggingDebug()) {
        logDebug(getSiteScope() + " Is not a valid siteScope. No products will be added to user " +
            pProfile.getRepositoryId() + " recently viewed list.");
      }
      return;
    }
    
    // The list of 'RecentlyViewedItem' objects that will be added to 'recentlyViewed' property.
    List<RepositoryItem> recentlyViewedProducts = getProductsForUpdate(pProfile);

    // Remove expired products (if any).
    if (removeExpiredProducts(recentlyViewedProducts, pProfile)) {
      
      if (isLoggingDebug()) {
        logDebug("Expired products have been removed from profile " + 
          pProfile.getRepositoryId() + " recently viewed list.");
      }
      
      // Get the recently viewed list again as it has been modified.
      recentlyViewedProducts = getProductsForUpdate(pProfile);
    }

    if (removeNonExistentProducts(recentlyViewedProducts, pProfile)) {
      
      if (isLoggingDebug()) {
        logDebug("Non-existent product(s) have been removed from profile " + 
          pProfile.getRepositoryId() + " recently viewed list.");
      }
      
      // Get the recently viewed list again as it has been modified.
      recentlyViewedProducts = getProductsForUpdate(pProfile);
    }

    // Repository item to hold the new RecentlyViewedProduct.
    RepositoryItem newProduct = createRecentlyViewedProduct(pItem, pProfile, pSiteId);
    
    // If the new recentlyViewedProduct item's product already exists in the recentlyViewed list, remove it.
    if ((recentlyViewedProducts != null) && (recentlyViewedProducts.size() > 0)) {
      
      RepositoryItem existingDuplicateProduct = 
        getDuplicateRecentlyViewedProduct(pItem, recentlyViewedProducts, pSiteId);
      
      if (existingDuplicateProduct != null) {
        
        if (isLoggingDebug()) {
          logDebug(pItem.getRepositoryId() + " already exists in profile " + 
            pProfile.getRepositoryId() + " recently viewed list and will be removed.");
        }
        
        getProductsForUpdate(pProfile).remove(existingDuplicateProduct);
        // Get the recently viewed list again as it has been modified.
        recentlyViewedProducts = getProductsForUpdate(pProfile);
      }
    }

    if(recentlyViewedProducts != null) {
      
      // The current size of the user's recentlyViewed list.
      int recentlyViewedProductsSize = recentlyViewedProducts.size();

      if (isLoggingDebug()) {
        logDebug("There are " + recentlyViewedProductsSize + " products in profile " 
          + pProfile.getRepositoryId() + " recently viewed list.");
      }
      
      // This will represent our 'virtual' list of products based on siteScope.
      List<RepositoryItem> recentlyViewedBySiteScope = null;
      
      if (recentlyViewedProductsSize > 0) {
      
        recentlyViewedBySiteScope = new ArrayList<RepositoryItem>();
        
        // Get the recentlyViewedProduct siteId property name.
        String recentlyViewedProductSiteIdPropertyName = 
          ((StorePropertyManager) getProfileTools().getPropertyManager()).getSiteIdPropertyName();          
        
        if (isLoggingDebug()) {
          logDebug("Building conceptual list of products based on siteScope '" + 
            getSiteScope() + "' current site '" + pSiteId + "' and each recently viewed product's siteId");
        }
        // Build the 'virtual' list based on siteScope.
        for (RepositoryItem recentlyViewedProduct : recentlyViewedProducts) {
          
          // Get the value of the recentlyViewedProduct item's siteId property.
          String siteId = (String) 
            recentlyViewedProduct.getPropertyValue(recentlyViewedProductSiteIdPropertyName);

          if (isSiteInScope(siteId, pSiteId)) {
            
            recentlyViewedBySiteScope.add(recentlyViewedProduct);
            
            if (isLoggingDebug()) {
              logDebug("recentlyViewedProduct: " + recentlyViewedProduct.getRepositoryId() + 
                " siteId value has a valid siteScope, adding to conceptual list.");
            }
          }
          else {
            if (getSiteScope() != null && 
                getSiteGroupManager().isShareableTypeRegistered(getSiteScope())) {   
              
              if (isLoggingDebug()) {
                logDebug("recentlyViewedProduct: " + recentlyViewedProduct.getRepositoryId() + 
                  " siteId value is not in the " + getSiteScope() + " site group but will still be " +
                  "added to the user's recentlyViewedProduct list as part of a seperate conceptual list.");
              }
            }
          }
        }
      }
      
      // If 'virtual' list size is less than the user defined max list 
      // size (mRecentlyViewedSize) or user defined max list size doesn't 
      // have a value, just add new entry.
      if (recentlyViewedProductsSize == 0 || 
         (recentlyViewedBySiteScope != null && 
          recentlyViewedBySiteScope.size() < getRecentlyViewedSize()) || 
           isListSizeInfinite()) {
        
        // Add new recentlyViewedProduct to start of list.
        recentlyViewedProducts.add(0, newProduct);
        
        if (isLoggingDebug()) {
          logDebug("Either user " + pProfile.getRepositoryId() + " recently viewed list is empty or " +
            "the conceptual product by siteScope list is less than the maximum valid list size. " +
            " Adding " + newProduct.getRepositoryId() + " to recently viewed list.");
        }
      }
      
      // If 'virtual' list size is equal to the user defined max list size (mRecentlyViewedSize).
      else if (recentlyViewedBySiteScope != null && 
               recentlyViewedBySiteScope.size() == getRecentlyViewedSize()){
        
        // Remove first item (defined by virtual siteScope list) from the user's recently viewed list.
        getProductsForUpdate(pProfile).remove(
          recentlyViewedBySiteScope.get((recentlyViewedBySiteScope.size() - 1)));
        recentlyViewedProducts = getProductsForUpdate(pProfile);
        // Add new recentlyViewedProduct to start of list.
        recentlyViewedProducts.add(0, newProduct);
        
        if (isLoggingDebug()) {
          logDebug("The conceptual product by siteScope list is equal to the maximum valid list size. " +
            "The first item (defined by siteScope) has been removed and " + newProduct.getRepositoryId() + 
            " has been added to recently viewed list.");
        }
      } 
      else if (recentlyViewedBySiteScope != null && 
               recentlyViewedBySiteScope.size() > getRecentlyViewedSize()){
        
        // If 'virtual' list size is more then maxlist size (mRecentlyViewedSize). 
        // This might occurs if we change recentlyViewedSize to smaller size and restart 
        // server or edit 'recentlyViewed' property manually, for example via ACC.
        int numItemsToRemove = (recentlyViewedBySiteScope.size() - getRecentlyViewedSize()) + 1;
        
        for (int i = 1; i <= numItemsToRemove; i++) {
          getProductsForUpdate(pProfile).remove(
            recentlyViewedBySiteScope.get(recentlyViewedBySiteScope.size() - i));
        }
        
        // Get the recently viewed list again as it has been modified.
        recentlyViewedProducts = getProductsForUpdate(pProfile);
        // Add new recentlyViewedProduct to start of list.
        recentlyViewedProducts.add(0, newProduct);
        
        if (isLoggingDebug()) {
          logDebug("The conceptual product by siteScope list is greater than the maximum valid list size. " +
            "The oldest products have been removed from the list (defined by siteScope) and " + 
              newProduct.getRepositoryId() + " has been added to recently viewed list.");
        }
      }
    } 
  }


  //----------------------------------------------------------------------------------
  /**
   * This method removes any recentlyViewedProduct items from the recentlyViewedProducts list
   * if a product item doesn't exist any more in the productCatalog repository.
   * 
   * @param pRecentlyViewed The recentlyViewedProducts list that will be examined for 
   *                        expired products.
   * @param pProfile The user who owns the recently viewed list.
   * 
   * @throws RepositoryException If there was a problem removing a product from the repository.
   * 
   * @return true if any recently viewed products were removed, otherwise false.
   */
  public boolean removeNonExistentProducts(List<RepositoryItem>pRecentlyViewed, RepositoryItem pProfile) 
    throws RepositoryException {
    
    boolean productsRemoved = false;
    
    if (pRecentlyViewed != null && pRecentlyViewed.size() > 0) {
      
      String recentlyViewedProductProductName = 
        ((StorePropertyManager) getProfileTools().getPropertyManager()).getProductPropertyName();

      for (RepositoryItem recentlyViewedProduct : pRecentlyViewed) {

        RepositoryItem product = 
          (RepositoryItem) recentlyViewedProduct.getPropertyValue(recentlyViewedProductProductName);
        
        if (product == null) {
          if (isLoggingDebug()) {
            logDebug(recentlyViewedProduct.getRepositoryId() + " product property is null, removing from repository.");
          }
          // Remove product from the repository.
          getProductsForUpdate(pProfile).remove(recentlyViewedProduct); 
          productsRemoved = true;
        }
      }
    }
    return productsRemoved;
  }
  

  //----------------------------------------------------------------------------------
  /**
   * This method checks if a site is valid for a particular siteScope and the current
   * site context.
   * 
   * @param pRecentlyViewedProductSiteId The site id of a recentlyViewedProduct.
   * @param pSiteId The id of the site currently in context.
   * 
   * @return true if the recentlyViewedProduct site is valid for this component's
   *         siteScope and current site context, otherwise false.
   */
  public boolean isSiteInScope(String pRecentlyViewedProductSiteId, String pSiteId) {
    
    // If pSiteId or pRecentlyViewedProductSiteId are null, they must be initialized to 
    // be an empty string so that their 'equals' methods can be invoked. A null value
    // for either of these parameters probably means we're working in a 'nosite' environment.
    String siteId = "";
    String recentlyViewedProductSiteId = "";
    
    if (pSiteId != null) {
      siteId = pSiteId;
    }
    if (pRecentlyViewedProductSiteId != null) {
      recentlyViewedProductSiteId = pRecentlyViewedProductSiteId;
    }
    
    // siteScope is 'current'.
    if (getSiteScope() != null && 
         getSiteScope().equals(CURRENT_SITE_SCOPE) &&
         siteId.equals(recentlyViewedProductSiteId)) {
      
      if (isLoggingDebug()) {
        logDebug("siteScope is '" + CURRENT_SITE_SCOPE + "', the current site id is " + siteId + 
          " and the recentlyViewedProduct siteId is " + recentlyViewedProductSiteId + " returning 'true'.");
      }
      return true;
    }
    
    // siteScope is a registered shareableType id. 
    else if (getSiteScope() != null && getSiteGroupManager().isShareableTypeRegistered(getSiteScope())) {
      
      Set<String> siteGroup = 
        (Set<String>) getSiteGroupManager().getSharingSiteIds(siteId, getSiteScope());
      
      if (siteGroup != null && siteGroup.contains(recentlyViewedProductSiteId)) {
        
        if (isLoggingDebug()) {
          logDebug("siteScope '" + getSiteScope() + "' is a registered shareable type, " +
            "the current site id is " + siteId + " and the recentlyViewedProduct siteId, " + 
            recentlyViewedProductSiteId + " is in the shareable type site group, returning 'true'.");
        }
        return true;
      }
    }
    // siteScope is 'all'. 
    else if (getSiteScope() != null && 
            (getSiteScope().equals(RecentlyViewedTools.ALL_SITE_SCOPE))) {
      
      if (isLoggingDebug()) {
        logDebug("siteScope is '" + ALL_SITE_SCOPE + "',  returning 'true'.");
      }
      return true;
    }
    
    if (isLoggingDebug()) {
      logDebug("siteScope " + getSiteScope() + " is invalid, returning 'false'.");
    }
    // siteScope is invalid.
    return false;
  }


  //----------------------------------------------------------------------------------
  /**
   * Create a new 'recentlyViewedProduct' item and add it to the repository if the user is logged in.
   * 
   * @param pProduct The product that will be set in the recentlyViewedItem 'product' property.
   * @param pProfile The profile item of the user that the recentlyViewedProduct is being created for.
   * @param pSiteId The site id of the site that is currently in context.
   *  
   * @return The newly created recentlyViewedProduct or else null.
   * 
   * @throws RepositoryException If there's a problem creating/adding the item.
   */
  public MutableRepositoryItem createRecentlyViewedProduct(RepositoryItem pProduct, RepositoryItem pProfile, String pSiteId) 
    throws RepositoryException {
    
    MutableRepository profileRepository = getProfileTools().getProfileRepository();
    
    // Get the recentlyViewedProduct item descriptor and property names.
    String recentlyViewedProductItemDescriptorName = 
      ((StorePropertyManager) getProfileTools().getPropertyManager()).getRecentlyViewedProductItemDescriptorName();
    String recentlyViewedProductProductName = 
      ((StorePropertyManager) getProfileTools().getPropertyManager()).getProductPropertyName();
    String recentlyViewedProductSiteIdName = 
      ((StorePropertyManager) getProfileTools().getPropertyManager()).getSiteIdPropertyName();
    String recentlyViewedProductTimeStampName = 
      ((StorePropertyManager) getProfileTools().getPropertyManager()).getTimeStampPropertyName();
    
    MutableRepositoryItem item = profileRepository.createItem(recentlyViewedProductItemDescriptorName);
    
    item.setPropertyValue(recentlyViewedProductProductName, pProduct);
    item.setPropertyValue(recentlyViewedProductSiteIdName, pSiteId);
    
    // Get the current system time and set for the recentlyViewedProduct timestamp value.
    CurrentDate cd = getCurrentDate();
    Date currentDate = cd.getTimeAsDate();
    item.setPropertyValue(recentlyViewedProductTimeStampName, new Timestamp(currentDate.getTime()));
    
    if (isLoggingDebug()) {
      logDebug("New recentlyViewedProduct create with product: " + pProduct.getRepositoryId() +
        ", siteId: " + pSiteId + " and current time: " + currentDate.getTime());
    }
    
    // We don't want to persist the recentlyViewedProduct if the current user is anonymous.
    if (!pProfile.isTransient()) {
      if (isLoggingDebug()) {
        logDebug("The current user " + pProfile.getRepositoryId() + 
          " is logged in, adding new recentlyViewedProduct to the repository.");
      }
      addRecentlyViewedProduct(item);
    }
    return item;
  }


  //----------------------------------------------------------------------------------
  /**
   * This method adds a transient recentlyViewedProduct item to the repository.
   * 
   * @param pRecentlyViewedProduct The transient recentlyViewedProduct to be added.
   * @throws RepositoryException If there's a problem adding the recentlyViewedProduct to the repository. 
   */
  public void addRecentlyViewedProduct(MutableRepositoryItem pRecentlyViewedProduct) throws RepositoryException {
    MutableRepository profileRepository = getProfileTools().getProfileRepository();
    profileRepository.addItem(pRecentlyViewedProduct);
  }
  

  //----------------------------------------------------------------------------------
  /**
   * This method takes a List of recentlyViewedProduct items and adds them a user's
   * recentlyViewedProducts list.
   * 
   * @param pProducts The list of recentlyViewedProducts that are to be added to the list.
   * @param pProfile The profile of the user whose recently viewed list is to be updated.
   * 
   * @throws RepositoryException 
   */
  public void addProductsToList(List<RepositoryItem> pProducts, RepositoryItem pProfile) throws RepositoryException {

    String recentlyViewedProductProductName = 
      ((StorePropertyManager) getProfileTools().getPropertyManager()).getProductPropertyName();
    String recentlyViewedProductSiteIdName = 
      ((StorePropertyManager) getProfileTools().getPropertyManager()).getSiteIdPropertyName();
    
    // In order for the products to be added from one recently viewed list to another in the correct
    // order, we must start from the end of the list and work backwards.
    for (int i = (pProducts.size() - 1); i >= 0; i--) {
      RepositoryItem product = pProducts.get(i);
      String siteId = (String) product.getPropertyValue(recentlyViewedProductSiteIdName);
      addProduct((RepositoryItem)product.getPropertyValue(recentlyViewedProductProductName), pProfile, siteId);
    }
  }
  

  //----------------------------------------------------------------------------------
  /**
   * Gets a user's recentlyViewedProducts property list that can be written to.
   * 
   * @param pProfile The user's profile whose recentlyViewedProducts property is to be modified.
   * 
   * @return A recentlyViewedProducts list property that is ready to be modified. 
   * 
   * @throws RepositoryException If there's a problem retrieving the recently viewed list.
   */
  public List<RepositoryItem> getProductsForUpdate(RepositoryItem pProfile) throws RepositoryException {
    MutableRepository mutRep = (MutableRepository) getProfileTools().getProfileRepository();
    
    if (isLoggingDebug()) {
      logDebug("Getting a modifiable recently viewed list for user: " + pProfile.getRepositoryId());
    }
    
    // Get the recentlyViewedProducts property name.
    String recentlyViewedProductsPropertyName = 
      ((StorePropertyManager) getProfileTools().getPropertyManager()).getRecentlyViewedProductsPropertyName();
    // Get the recentlyViewedProduct item descriptor name.
    String recentlyViewedProductItemDescriptorName = 
      ((StorePropertyManager) getProfileTools().getPropertyManager()).getRecentlyViewedProductItemDescriptorName();
    
    if (pProfile.getPropertyValue(recentlyViewedProductsPropertyName) == null) {
      // Since there are no recentlyViewedProducts in profile, create new list  
      getProfileTools().updateProperty(recentlyViewedProductsPropertyName, new ArrayList<RepositoryItem>(1), pProfile);
    }    
    
    MutableRepositoryItem mutProfile = getProfileTools().getProfileItem(pProfile.getRepositoryId());

    Object value = mutProfile.getPropertyValue(recentlyViewedProductsPropertyName);
    List<RepositoryItem> recentlyViewed = null;
    
    if (value instanceof Collection<?>){
      recentlyViewed = (List<RepositoryItem>)value;
    }
    else {
      recentlyViewed = new ArrayList<RepositoryItem>();
      //recentlyViewedProducts.setPropertyValue(recentlyViewedProductsPropertyName, recentlyViewed);
      mutProfile.setPropertyValue(recentlyViewedProductsPropertyName, recentlyViewed);
    }
    return recentlyViewed;
  }
  

  //----------------------------------------------------------------------------------
  /**
   * Get the specified user's (immutable) recentlyViewedProduct list.
   * 
   * @param pProfile The user whose recentlyViewedProduct list will be retrieved.
   * 
   * @return The list of recentlyViewedItem RepositoryItems associated with the user
   *         in pProfile.
   */
  public List<RepositoryItem> getProducts(RepositoryItem pProfile) {

    if (isLoggingDebug()) {
      logDebug("Getting a non-modifiable recently viewed list for user: " + pProfile.getRepositoryId());
    }
    
    List<RepositoryItem> list = null;
    
    if (pProfile != null) {
      String recentlyViewedProductsPropertyName = 
        ((StorePropertyManager) getProfileTools().getPropertyManager()).getRecentlyViewedProductsPropertyName();
      
      // The list of 'RecentlyViewedItem' objects that will be added to 'recentlyViewed' property.
      list = (List<RepositoryItem>) pProfile.getPropertyValue(recentlyViewedProductsPropertyName);
    }      
    return list;
  }
  

  //----------------------------------------------------------------------------------
  /**
   * Check a recentlyViewed list for a duplicate product.
   * 
   * @param pProduct The actual 'product' item to be added to the list.
   * @param pRecentlyViewed The current list of recentlyViewedProduct items.
   * @param pSiteId The site id of the site that is currently in context. 
   * 
   * @return A duplicate recentlyViewedProduct item (if any), otherwise null.
   */
  public RepositoryItem getDuplicateRecentlyViewedProduct(RepositoryItem pProduct, 
                                                          List<RepositoryItem> pRecentlyViewed, 
                                                          String pSiteId) {
    // If pSiteId is null, siteId must be initialized to be an empty string in order
    // for it's 'equals' method to be invoked.
    String siteId = (pSiteId != null) ? pSiteId : "";
    
    // Get the recentlyViewedProduct items 'product' property name.
    String recentlyViewedProductProductName = 
      ((StorePropertyManager) getProfileTools().getPropertyManager()).getProductPropertyName();
    // Get the recentlyViewedProduct items 'siteId' property name.
    String recentlyViewedProductSiteIdName = 
      ((StorePropertyManager) getProfileTools().getPropertyManager()).getSiteIdPropertyName();
    
    for (RepositoryItem recentlyViewedItem : pRecentlyViewed) {
      
      RepositoryItem existingItem = (RepositoryItem) 
        recentlyViewedItem.getPropertyValue(recentlyViewedProductProductName);
      String existingItemSiteId = (String) 
        recentlyViewedItem.getPropertyValue(recentlyViewedProductSiteIdName);
      
      // If existingItemSiteId is null, it must be initialized to be an empty string so it
      // can be correctly compared to the 'siteId' variable.
      if (existingItemSiteId == null) {
        existingItemSiteId = "";
      }
      
      // Check if the product we want to add already exists in the user's recently viewed list.
      if (pProduct.getRepositoryId().equals(existingItem.getRepositoryId())) {
        
        // Check the siteId here as well because one product can be available on multiple sites.
        if (siteId.equals(existingItemSiteId)) {
          
          if (isLoggingDebug()) {
            logDebug("Returning " + existingItem.getRepositoryId() + " as it is a duplicate product.");
          }
          return recentlyViewedItem;
        }
      }
    }
    if (isLoggingDebug()) {
      logDebug(pProduct.getRepositoryId() + " is not a duplicate product. Returning null.");
    }
    return null;
  }


  //----------------------------------------------------------------------------------
  /**
   * This method removes any products from a user's recently viewed products repository 
   * list that exceed this components expiryPeriod.
   * 
   * @param pRecentlyViewed The recentlyViewedProducts list that will be examined for 
   *                        expired products.
   * 
   * @throws RepositoryException If there was a problem removing a product from the repository.
   * 
   * @return true if any recently viewed products were removed, otherwise false.
   */
  public boolean removeExpiredProducts(List<RepositoryItem> pRecentlyViewed, RepositoryItem pProfile) 
    throws RepositoryException {
    
    boolean productsRemoved = false;

    for (RepositoryItem recentlyViewedProduct : pRecentlyViewed) {

      if (isProductExpired(recentlyViewedProduct)) {
        
        if (isLoggingDebug()) {
          logDebug(recentlyViewedProduct.getRepositoryId() + " is now expired, removing from repository.");
        }
        // Remove product from the repository.
        getProductsForUpdate(pProfile).remove(recentlyViewedProduct); 
        productsRemoved = true;
      }
    }
    return productsRemoved;
  }
  

  //----------------------------------------------------------------------------------
  /**
   * This method checks if an recentlyViewedProduct item's timestamp property exceeds
   * the expiryPeriod.
   * 
   * @param pProduct The recentlyViewedProduct item we want to run the expiry check against.
   * 
   * @return true if product has exceeded expiryPeriod, otherwise false.
   */
  public boolean isProductExpired(RepositoryItem pProduct) {

    // If no expiryPeriod has been defined or expiryPeriod is 0, the product will never expire.
    if (!(getExpiryPeriod()> 0)) {
      if (isLoggingDebug()) {
        logDebug("Products will never expire as expiryPeriod value is 0, returning 'false'.");
      }
      return false;
    }
  
    // Get the recentlyViewedProduct item 'timestamp' property name.
    String recentlyViewedProductTimeStampName = 
      ((StorePropertyManager) getProfileTools().getPropertyManager()).getTimeStampPropertyName();
    
    // Get the timestamp value of the recentlyViewedProduct item. 
    Timestamp itemTimeStamp = (Timestamp) pProduct.getPropertyValue(recentlyViewedProductTimeStampName);
    
    // Get the current system time.
    CurrentDate cd = getCurrentDate();
    Date currentDate = cd.getTimeAsDate();

    // Get the difference between the current time and product time (in milliseconds).
    long diff = currentDate.getTime() - itemTimeStamp.getTime();
    // Convert the time difference from milliseconds to seconds.
    long diffSeconds = (diff / 1000);
    // Convert expiryPeriod (hours) to seconds.
    long expiryPeriodSeconds = (getExpiryPeriod() * (60 * 60));
    
    if (diffSeconds > expiryPeriodSeconds) {
      if (isLoggingDebug()) {
        logDebug("The product has expired, returning 'true'.");
      }
      // product has expired.
      return true;
    }
    if (isLoggingDebug()) {
      logDebug("The product has not expired, returning 'false'.");
    }
    // product has not expired.
    return false;
  }

}
