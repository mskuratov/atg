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


package atg.projects.store.droplet;



import atg.commerce.gifts.GiftlistSiteFilter;
import atg.commerce.gifts.GiftlistTools;
import atg.commerce.inventory.InventoryException;
import atg.multisite.Site;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.projects.store.catalog.StoreCatalogTools;
import atg.projects.store.catalog.comparison.StoreProductComparisonList;
import atg.projects.store.inventory.StoreInventoryManager;
import atg.projects.store.multisite.StoreSitePropertiesManager;
import atg.projects.store.order.purchase.StoreCartFormHandler;
import atg.projects.store.profile.StorePropertyManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.collections.filter.FilterException;
import atg.service.util.CurrentDate;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;

/**
 * This droplet should be used on the Single/Multiple SKU Product Details Pages.
 *
 * <p>This droplet obtains currently viewed product and SKU from the current request and saves into request all necessary data
 * to be displayed on the PDP.</p>
 *
 * <p><b>Input parameters:</b>
 *   <dl>
 *     <dt>product</dt>
 *     <dd>Currently viewed product.</dd>
 *     <dt>skus</dt>
 *     <dd>Product's child SKUs. Will be used if selectedSku is not specified.</dd>
 *     <dt>selectedSku</dt>
 *     <dd>Currently viewed SKU. For a multi-SKU PDP this droplet should be called for each displayed SKU.</dd>
 *   </dl>
 * </p>
 *
 * <p><b>Output parameters:</b>
 *   <dl>
 *     <dt>quantity</dt>
 *     <dd>Default quantity to be displayed on the page.</dd>
 *     <dt>availabilityType</dt>
 *     <dd>Specifies availability of the currently selected SKU. Can be one of the following:
 *       <ol>
 *         <li>available</li>
 *         <li>preorderable</li>
 *         <li>backorderable</li>
 *         <li>unavailable</li>
 *       </ol>
 *     </dd>
 *     <dt>availabilityDate</dt>
 *     <dd>If <code>availabilityType</code> is <code>preorderable</code> or <code>backorderable</code>, this parameter will be set.
 *       Contains date when the SKU specified will be available.</dd>
 *     <dt>comparisonsContainsProduct</dt>
 *     <dd>Flags, if current product is already added to the Comparisons List.</dd>
 *     <dt>showEmailAFriend</dt>
 *     <dd>Flags, if 'Email a Friend' button should be displayed.</dd>
 *     <dt>showGiftlists</dt>
 *     <dd>Flags, if 'Add to Giftlist' button should be displayed.</dd>
 *     <dt>wishlistContainsSku</dt>
 *     <dd>Flags, if user wishlist already contains the SKU specified.</dd>
 *     <dt>giftlists</dt>
 *     <dd>Collection of user's giftlists. Note that these giftlists are filtered by site already.</dd>
 *     <dt>selectedSku</dt>
 *     <dd>The selected SKU, will be the same as passed in. If no SKU is passed and product
 *     contains only one SKU then selectedSku parameter will be set to this SKU.</dd>
 *   </dl>
 * </p>
 *
 * <p><b>Open parameters:</b>
 *   <dl>
 *     <dt>output</dt>
 *     <dd>Always rendered</dd>
 *   </dl>
 * </p>
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/DefaultProductDetailsDroplet.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class DefaultProductDetailsDroplet extends DynamoServlet {
  
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/DefaultProductDetailsDroplet.java#3 $$Change: 788278 $";

  protected static final String AVAILABILITY_STATUS_UNAVAILABLE = "unavailable";
  protected static final String AVAILABILITY_STATUS_AVAILABLE = "available";
  protected static final String AVAILABILITY_STATUS_BACKORDERABLE = "backorderable";
  protected static final String AVAILABILITY_STATUS_PREORDERABLE = "preorderable";
  protected static final String OPEN_PARAMETER_OUTPUT = "output";
  protected static final String PARAMETER_AVAILABILITY_DATE = "availabilityDate";
  protected static final String PARAMETER_AVAILABILITY_TYPE = "availabilityType";
  protected static final String PARAMETER_COMPARISONS_CONTAINS_PRODUCT = "comparisonsContainsProduct";
  protected static final String PARAMETER_PRODUCT = "product";
  protected static final String PARAMETER_QUANTITY = "quantity";
  protected static final String PARAMETER_SELECTED_SKU = "selectedSku";
  protected static final String PARAMETER_SHOW_EMAIL_A_FRIEND = "showEmailAFriend";
  protected static final String PARAMETER_SHOW_GIFTLISTS = "showGiftlists";
  protected static final String PARAMETER_SKUS = "skus";
  protected static final String PARAMETER_WISHLIST_CONTAINS_SKU = "wishlistContainsSku";
  protected static final String PARAMETER_GIFTLISTS = "giftlists";
  protected static final String PARAMETER_GIFTLIST_CONTAINS_SKU = "giftlistsContainingSku";

  private GiftlistSiteFilter mGiftListSiteFilter;
  private StoreInventoryManager mInventoryManager;
  private StorePropertyManager mProfilePropertyManager;
  private GiftlistTools mGiftlistTools;
  private StoreSitePropertiesManager mSitePropertiesManager;
  private StoreCatalogTools mCatalogTools;

 

  /** 
   * @return the giftListSiteFilter
   */
  public GiftlistSiteFilter getGiftListSiteFilter() {
    return mGiftListSiteFilter;
  }

  /**
   * @param pGiftListSiteFilter the giftListSiteFilter to set
   */
  public void setGiftListSiteFilter(GiftlistSiteFilter pGiftListSiteFilter) {
    mGiftListSiteFilter = pGiftListSiteFilter;
  }


  /**
   * @return the inventoryManager
   */
  public StoreInventoryManager getInventoryManager() {
    return mInventoryManager;
  }

  /**
   * @param pInventoryManager the inventoryManager to set
   */
  public void setInventoryManager(StoreInventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }

  /**
   * @return the profilePropertyManager
   */
  public StorePropertyManager getProfilePropertyManager() {
    return mProfilePropertyManager;
  }

  /**
   * @param pProfilePropertyManager the profilePropertyManager to set
   */
  public void setProfilePropertyManager(StorePropertyManager pProfilePropertyManager) {
    mProfilePropertyManager = pProfilePropertyManager;
  }


  /**
   * @return the giftlistTools
   */
  public GiftlistTools getGiftlistTools() {
    return mGiftlistTools;
  }

  /**
   * @param pGiftlistTools the giftlistTools to set
   */
  public void setGiftlistTools(GiftlistTools pGiftlistTools) {
    mGiftlistTools = pGiftlistTools;
  }

  /**
   * @return the sitePropertiesManager
   */
  public StoreSitePropertiesManager getSitePropertiesManager() {
    return mSitePropertiesManager;
  }

  /**
   * @param pSitePropertiesManager the sitePropertiesManager to set
   */
  public void setSitePropertiesManager(StoreSitePropertiesManager pSitePropertiesManager) {
    mSitePropertiesManager = pSitePropertiesManager;
  }


  /**
   * Gets the mCatalogTools
   * @return mCatalogTools
   */
  public StoreCatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  /**
   * Sets the mCatalogTools
   * @param pCatalogTools Value to set
   */
  public void setCatalogTools(StoreCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  /**
   * currentDate
   */
  private CurrentDate mCurrentDate;
  /**
   * Sets the CurrentDate component.
   */
  public void setCurrentDate(CurrentDate pCurrentDate) { 
    mCurrentDate = pCurrentDate; 
  }
  /**
   * Gets the CurrentDate component.
   */
  public CurrentDate getCurrentDate() { 
    return mCurrentDate; 
  }



  //-------------------------------------
  // property: productComparisonList

  private StoreProductComparisonList mProductComparisonList;

  /** Sets property productComparisonList. Our store comparison
   * list. */
  public void setProductComparisonList(StoreProductComparisonList pProductComparisonList) {
    mProductComparisonList = pProductComparisonList;
  }

  /** Returns property productComparisonList. Our store comparison
   * list. */
  public StoreProductComparisonList getProductComparisonList() {
    return mProductComparisonList;
  }



  //-------------------------------------
  // property: cartFormHandler

  private StoreCartFormHandler mCartFormHandler;

  /** Sets property cartFormHandler. Our store cart form handler. */
  public void setCartFormHandler(StoreCartFormHandler pCartFormHandler) {
    mCartFormHandler = pCartFormHandler;
  }

  /** Returns property cartFormHandler. Our store cart form handler. */
  public StoreCartFormHandler getCartFormHandler() {
    return mCartFormHandler;
  }


  //-------------------------------------
  // property: profile

  private Profile mProfile;

  /** Sets property currentProfile. The current profile. */
  public void setProfile(Profile pProfile) {
    mProfile = pProfile;
  }

  /** Returns property currentProfile. The current profile. */
  public Profile getProfile() {
    return mProfile;
  }
  


  /**
   * This method provides the default implementation of service, by dispatching 
   * to conventionally named methods which begin with "do".
   * 
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @throws ServletException an application specific error occurred processing this request
   * @throws IOException an error occurred reading data from the request or writing data to the response.
  */
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    String availabilityType = getAvailabilityType(getSelectedSku(pRequest), getCurrentProduct(pRequest));
    RepositoryItem selectedSkuRepositoryItem = getSelectedSku(pRequest);
    
    if (AVAILABILITY_STATUS_BACKORDERABLE.equals(availabilityType) || AVAILABILITY_STATUS_PREORDERABLE.equals(availabilityType)) {
      pRequest.setParameter(PARAMETER_AVAILABILITY_DATE, getAvailabilityDate(getSelectedSku(pRequest), getCurrentProduct(pRequest), availabilityType));
    }
    
    pRequest.setParameter(PARAMETER_AVAILABILITY_TYPE, availabilityType);
    pRequest.setParameter(PARAMETER_QUANTITY, getQuantity(pRequest));
    pRequest.setParameter(PARAMETER_SHOW_GIFTLISTS, areGiftlistsAvailable(pRequest));
    pRequest.setParameter(PARAMETER_GIFTLISTS, getCurrentProfileGiftlists(pRequest));
    pRequest.setParameter(PARAMETER_WISHLIST_CONTAINS_SKU, isCurrentSkuAddedToWishlist(pRequest));
    pRequest.setParameter(PARAMETER_COMPARISONS_CONTAINS_PRODUCT, isCurrentProductAddedToComparisons(pRequest));
    pRequest.setParameter(PARAMETER_SHOW_EMAIL_A_FRIEND, isEmailEnabled(pRequest));
    pRequest.setParameter(PARAMETER_SELECTED_SKU, selectedSkuRepositoryItem);
    pRequest.setParameter(PARAMETER_GIFTLIST_CONTAINS_SKU, giftlistsContainingSku(selectedSkuRepositoryItem));
    
    serviceContents(pRequest, pResponse);
  }




  /**
   * This method specifies, which open parameters should be serviced. Current implementation services 'output' oparam only.
   * @param pRequest current request.
   * @param pResponse current response.
   * @throws ServletException if something goes wrong.
   * @throws IOException if something goes wrong.
   */
  protected void serviceContents(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    pRequest.serviceLocalParameter(OPEN_PARAMETER_OUTPUT, pRequest, pResponse);
  }



  

  /**
   * This method retrieves current profile from the request specified.
   * @param pRequest current request.
   * @return currently served profile.
   */
  protected Profile getCurrentProfile(DynamoHttpServletRequest pRequest) {
    return getProfile();
  }

  /**
   * This method retrieves current Comparisons List from the request specified.
   * @param pRequest current request.
   * @return currently served comparisons list.
   */
  protected StoreProductComparisonList getCurrentComparisonsList(DynamoHttpServletRequest pRequest) {
    return getProductComparisonList();
  }

  /**
   * This method calculates default quantity to be dislpayed on the PDP.
   * Current implementation takes quantity from the CartFormHandler's <code>items</code> property, i.e. last added quantity.
   * If <code>items</code> are not specified, default value of 1 returned.
   * @param pRequest current request.
   * @return default quantity.
   */
  protected long getQuantity(DynamoHttpServletRequest pRequest) {
    StoreCartFormHandler cartFormHandler = getCartFormHandler();
    
    if (cartFormHandler.getItems() != null && cartFormHandler.getItems().length > 0) {
      return cartFormHandler.getItems()[0].getQuantity();
    } else {
      return 1;
    }
  }

  /**
   * This method takes currently selected SKU from the request. If it's not passed
   * but product contains only single SKU then this SKU is returned.
   * @param pRequest current request.
   * @return currently selected SKU.
   */
  protected RepositoryItem getSelectedSku(DynamoHttpServletRequest pRequest) {
    RepositoryItem selectedSku = (RepositoryItem) pRequest.getObjectParameter(PARAMETER_SELECTED_SKU);
    if (selectedSku == null) {
      RepositoryItem product = getCurrentProduct(pRequest);
      if (product != null){
        Collection<RepositoryItem> skus = getAllSkus(pRequest);
        if (skus!= null && skus.size()==1){
          selectedSku = skus.iterator().next(); 
        }
      }
    }
    return selectedSku;
  }

  /**
   * This method takes currently viewed product from the request specified.
   * @param pRequest current request.
   * @return currently viewed product.
   */
  protected RepositoryItem getCurrentProduct(DynamoHttpServletRequest pRequest) {
    return (RepositoryItem) pRequest.getObjectParameter(PARAMETER_PRODUCT);
  }
  
  /**
   * This method obtains a <code>Collection</code> of all specified product SKUs. These SKU should be specified as <code>skus</code>
   * request parameter.
   * @param pRequest current request.
   * @return product's child SKUs.
   */
  protected Collection<RepositoryItem> getAllSkus(DynamoHttpServletRequest pRequest) {
    return (Collection<RepositoryItem>) pRequest.getObjectParameter(PARAMETER_SKUS);
  }

  /**
   * This method calculates availability date for the product and SKU specified.
   * Current implementation takes availability from the inventory manager.
   * @param pSku currently selected SKU.
   * @param pProduct currently viewed product.
   * @param pAvailabilityType product/SKU availability (i.e. 'available', 'preorderable', etc.)
   * @return availability date.
   */
  protected Date getAvailabilityDate(RepositoryItem pSku, RepositoryItem pProduct, String pAvailabilityType) {
    // If SKU is not selected yet, do not calculate availability date.
    if (pSku == null) {
      return null;
    }
    try {
      // Caluclate this date for backorderable and preorderable SKUs only.
      if (AVAILABILITY_STATUS_BACKORDERABLE.equals(pAvailabilityType)) {
        Date backorderAvailableDate = getInventoryManager().getBackorderAvailabilityDate(pSku.getRepositoryId());
        
        // Get the current system time.
        CurrentDate cd = getCurrentDate();
        Date currentDate = cd.getTimeAsDate();
        
        if (backorderAvailableDate != null && backorderAvailableDate.after(currentDate)) {
          return backorderAvailableDate;
        }
      } else if (AVAILABILITY_STATUS_PREORDERABLE.equals(pAvailabilityType)) {
        return getInventoryManager().getPreorderAvailabilityDate(pProduct);
      }
    } catch (InventoryException ex) {
      // Surpress inventory exception, we can live without availability date set.
      return null;
    }
    return null;
  }

  /**
   * This method calculates availability type for the product/SKU specified.
   * Current implementation takes availability from the inventory manager. Return value may be one of the following:
   * <ol>
   *   <li>{@link #AVAILABILITY_STATUS_AVAILABLE}</li>
   *   <li>{@link #AVAILABILITY_STATUS_BACKORDERABLE}</li>
   *   <li>{@link #AVAILABILITY_STATUS_PREORDERABLE}</li>
   *   <li>{@link #AVAILABILITY_STATUS_UNAVAILABLE}</li>
   * </ol>
   * @param pSku currently selected SKU.
   * @param pProduct currently viewed product.
   * @return availability type in form of <code>String</code>
   */
  protected String getAvailabilityType(RepositoryItem pSku, RepositoryItem pProduct) {
    if (pSku == null) {
      return null;
    }
    try {
      int availabilityType = getInventoryManager().queryAvailabilityStatus(pProduct, pSku.getRepositoryId());
      if (availabilityType == getInventoryManager().getAvailabilityStatusInStockValue()) {
        return AVAILABILITY_STATUS_AVAILABLE;
      } else if (availabilityType == getInventoryManager().getAvailabilityStatusBackorderableValue()) {
        return AVAILABILITY_STATUS_BACKORDERABLE;
      } else if (availabilityType == getInventoryManager().getAvailabilityStatusPreorderableValue()) {
        return AVAILABILITY_STATUS_PREORDERABLE;
      } else {
        return AVAILABILITY_STATUS_UNAVAILABLE;
      }
    } catch (InventoryException ex) {
      // Surpress inventory exception, we can live without availability status.
      return null;
    }
  }

  /**
   * This method calculates all user's giftlists to be displayed on the page.
   * Current implementation filters giftlists created on the current site group only.
   * @param pRequest current request.
   * @return user's giftlists.
   */
  protected Collection<RepositoryItem> getCurrentProfileGiftlists(DynamoHttpServletRequest pRequest) {
    // Get all user's giftlists...
    Collection<RepositoryItem> allGiftlists = (Collection<RepositoryItem>) getCurrentProfile(pRequest).
            getPropertyValue(getProfilePropertyManager().getGiftlistsPropertyName());
    try {
      // ...and filter out giftlists created on wrong sites.
      return getGiftListSiteFilter().filterCollection(allGiftlists, null, getCurrentProfile(pRequest));
    } catch (FilterException ex) {
      // Surpress filtering exception, we can live without giftlists.
      return null;
    }
  }

  /**
   * This method calculates <code>boolean</code> flag, specifying if 'Add to Giftlist' button should be displayed on the page.
   * Current implementation does not show this button to transient users or users without giftlists.
   * @param pRequest current request.
   * @return <code>boolean</code> flag.
   */
  protected boolean areGiftlistsAvailable(DynamoHttpServletRequest pRequest) {
    // User is transient? do not display the button.
    if (getCurrentProfile(pRequest).isTransient()) {
      return false;
    }
    Collection<RepositoryItem> giftlists = getCurrentProfileGiftlists(pRequest);
    // There are no giftlists created by this user? do not dislpay the button.
    if (giftlists == null || giftlists.isEmpty()) {
      return false;
    }
    return true;
  }

  /**
   * This method calculates a <code>boolean</code> flag, specifying if currently selected SKU is already added to the user's wishlist.
   * @param pRequest current request.
   * @return <code>true</code> if SKU already added to the wishlist, <code>false</code> otherwise.
   */
  protected boolean isCurrentSkuAddedToWishlist(DynamoHttpServletRequest pRequest) {
    RepositoryItem currentSku = getSelectedSku(pRequest);
    // No SKU selected? then it's not added yet.
    if (currentSku == null) {
      return false;
    }
    RepositoryItem wishlist = (RepositoryItem) getCurrentProfile(pRequest).
            getPropertyValue(getProfilePropertyManager().getWishlistPropertyName());
    // No wishlist created for user? then SKU is not added yet.
    if (wishlist == null) {
      return false;
    }
    Collection<RepositoryItem> wishlistItems = (Collection<RepositoryItem>) wishlist.getPropertyValue(getGiftlistTools().getGiftlistItemsProperty());
    // Wishlist is created, but it's empty? then SKU is not added yet.
    if (wishlistItems == null || wishlistItems.isEmpty()) {
      return false;
    }
    try {
      // Filter out items came from other sites.
      Collection<RepositoryItem> filteredItems = getGiftListSiteFilter().filterCollection(wishlistItems, null, getCurrentProfile(pRequest));
      // Search for currently selected SKU.
      for (RepositoryItem item : filteredItems) {
        String catalogRefId = (String) item.getPropertyValue(getGiftlistTools().getCatalogRefIdProperty());
        if (catalogRefId != null && catalogRefId.equals(currentSku.getRepositoryId())) {
          // Found?! wishlist contains current SKU.
          return true;
        }
      }
      return false;
    } catch (FilterException ex) {
      // Surpress filtering exception, if unable to filter wishlist items, just do not display wishlist link.
      return false;
    }
  }

  /**
   * This method calculates <code>boolean</code> flag, specifying if currently viewed product is already added to the
   * Comparisons list.
   * @param pRequest current request.
   * @return <code>true</code> if current product is already added to the list, <code>false</code> otherwise.
   */
  protected boolean isCurrentProductAddedToComparisons(DynamoHttpServletRequest pRequest) {
    RepositoryItem currentProduct = getCurrentProduct(pRequest);
    if (currentProduct == null) {
      return false;
    }
    try {
      return getCurrentComparisonsList(pRequest).contains(currentProduct.getRepositoryId());
    } catch (RepositoryException ex) {
      // Surpress repository exception, we can leave without comparisons determined.
      return false;
    }
  }

  /**
   * This method calculates <code>boolean</code> flag, specifying if currently viewed product can be emailed to a friend.
   * @param pRequest current request.
   * @return <code>true</code> if 'Email a Friend' button should be displayed, <code>false</code> otherwise.
   */
  protected boolean isEmailEnabled(DynamoHttpServletRequest pRequest) {
    // For this button to be displayed, both product and current site must have an 'emailAFriendEnabled' property set to true.
    RepositoryItem currentProduct = getCurrentProduct(pRequest);
    Site currentSite = SiteContextManager.getCurrentSite();
    if (currentProduct == null || currentSite == null) {
      return false;
    }
    Boolean siteEmailEnabled = (Boolean) currentSite.getPropertyValue(getSitePropertiesManager().getEmailAFriendEnabledPropertyName());
    Boolean productEmailEnabled = (Boolean) currentProduct.getPropertyValue(getSitePropertiesManager().getEmailAFriendEnabledPropertyName());
    if (siteEmailEnabled == null || productEmailEnabled == null) {
      return false;
    }
    return siteEmailEnabled && productEmailEnabled;
  }
  
  /**
   * This method creates a list of Giftlist Ids by checking if currently selected SKU is already added to the user's giftlist.
   * @param pSelectedSkuRepositoryItem selected SKU.
   * @return The List of  giftlist ids if  SKU is already present in that Giftlist
   */
  protected List giftlistsContainingSku (RepositoryItem pSelectedSkuRepositoryItem) {
    if(pSelectedSkuRepositoryItem == null){
      return Collections.EMPTY_LIST;
    }
    
    List giftlistsContainingSku = new ArrayList();
    String currentSkuId = pSelectedSkuRepositoryItem.getRepositoryId();
    Collection<RepositoryItem> allGiftlists = (Collection<RepositoryItem>) ServletUtil.getCurrentUserProfile().getPropertyValue(getProfilePropertyManager().getGiftlistsPropertyName());
    
    if (allGiftlists != null && allGiftlists.size() > 0) {
      for (RepositoryItem giftList : allGiftlists) { 
        try {
          Object giftItems = giftList.getPropertyValue(getGiftlistTools().getGiftlistItemsProperty());
          // Filter out items came from other sites.
          Collection<RepositoryItem> filteredItems = getGiftListSiteFilter().filterCollection( (Collection) giftItems, null, ServletUtil.getCurrentUserProfile());
          // Search for currently selected SKU.
          for (RepositoryItem item : filteredItems) {
            String catalogRefId = (String) item.getPropertyValue(getGiftlistTools().getCatalogRefIdProperty());
            // Check if the SKU id is same as the selected SKU id
            if(catalogRefId.equals(currentSkuId)){
              // Found?! giftlist contains current SKU.
              giftlistsContainingSku.add(giftList.getRepositoryId());
              break;
            }
          }
        }
        catch(FilterException ex) {
          // Surpress filtering exception, if unable to filter giftlist items, just do not display giftlist link.
          if(isLoggingError()) {
            logError("Unable to filter giftlist items and hence can not display GiftList link ", ex);
          }
        }
      }
      return giftlistsContainingSku;  
    }
    return Collections.EMPTY_LIST;
  }
}
