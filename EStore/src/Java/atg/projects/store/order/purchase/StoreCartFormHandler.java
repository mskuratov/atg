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


package atg.projects.store.order.purchase;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.Relationship;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.commerce.order.purchase.CartModifierFormHandler;
import atg.commerce.promotion.GWPManager;
import atg.commerce.promotion.GiftWithPurchaseSelection;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.projects.store.gifts.StoreGiftlistFormHandler;
import atg.projects.store.gifts.StoreGiftlistManager;
import atg.projects.store.inventory.StoreInventoryManager;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.profile.SessionBean;
import atg.projects.store.promotion.StoreGiftWithPurchaseFormHandler;
import atg.projects.store.ui.AjaxUtils;
import atg.repository.RepositoryItem;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import java.util.HashMap;


/**
 * Extends the default CartModifierFormHandler for custom functionality.
 * This class holds all the handle methods for the buttons on the cart
 * page. Since all buttons need to perform similar functionality,
 * including updating item quantities, adding gift wrap/gift message
 * and moving to the checkout process, all the methods have been
 * captured in this class. In the case of ExpressCheckout, this
 * class does the preliminary duties of modifying cart contents, and
 * then calls the ExpressCheckoutFormHandler to run the express
 * checkout pipeline.
 * <p>
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreCartFormHandler.java#4 $$Change: 788278 $ 
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreCartFormHandler extends CartModifierFormHandler {
    
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreCartFormHandler.java#4 $$Change: 788278 $";

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  
  public static final String DISPLAY_NAME_PROPERTY_NAME = "displayName";
  
  private static final String PRODUCT = "product";
  
  private static final String CLOTHING_SKU_TYPE = "clothing";

  private static final String FURNITURE_SKU_TYPE = "furniture";

  private static final String MSG_SELECT_SKU = "addToCart_selectSku";

  private static final String MSG_SELECT_WOOD_FINISH = "addToCart_selectWoodFinish";

  private static final String MSG_SELECT_COLOR_SIZE = "addToCart_selectColorSize";

  private static final String MSG_SELECT_COLOR = "addToCart_selectColor";

  private static final String MSG_SELECT_SIZE = "addToCart_selectSize";
  
  //Error Keys
  private static final String MSG_ERROR_NO_COLOR_SIZE_SELECTED_WISHLIST = "noColorSizeSelectedWishlist";
  private static final String MSG_ERROR_NO_COLOR_SIZE_SELECTED_GIFTLIST = "noColorSizeSelectedGiftlist";
  private static final String MSG_ERROR_NO_FINISH_SELECTED_WISHLIST = "noFinishSelectedWishlist";
  private static final String MSG_ERROR_NO_FINISH_SELECTED_GIFTLIST = "noFinishSelectedGiftlist";
  
  /* Number format error code - value passed from the eventSender */
  private static final String NUMBER_FORMAT_ERR_CODE = "numberFormatError";
  /* Illegal argument error code - value passed from the eventSender */
  private static final String ILLEGAL_ARGUMENT_ERR_CODE = "illegalArgumentError";

  /*
   * Error messages for order item quantity outside permitted min/max range
   */
  public static final String MSG_LESS_THAN_MIN_QUANTITY = "quantityLessThanMin";
  public static final String MSG_MORE_THAN_MAX_QUANTITY = "quantityMoreThanMax";
  public static final String MSG_ITEM_LESS_THAN_MIN_QUANTITY = "itemQuantityLessThanMin";
  public static final String MSG_ITEM_MORE_THAN_MAX_QUANTITY = "itemQuantityMoreThanMax";

  public static final String MSG_ERROR_REMOVE_SELECTABLE_QUANTITY = 
    StoreGiftWithPurchaseFormHandler.MSG_ERROR_REMOVE_SELECTABLE_QUANTITY;

  /**
   * Promotion resource bundle name.
   */
  protected static String sPromotionBundleName = "atg.commerce.promotion.PromotionResources";

  /**
   * Add item to giftlist success redirect URL.
   */
  String mAddItemToGiftlistSuccessURL;

  /**
   * Add item to giftlist error redirect URL.
   */
  String mAddItemToGiftlistErrorURL;

  /**
   * Add item to gift list login URL.
   */
  String mAddItemToGiftlistLoginURL;
  
  /**
   * AddItemToGift that contains gift list ID to add items to.
   */
  String mAddItemToGiftlist;

  /**
   * Continue shopping success redirect URL.
   */
  private String mContinueShoppingSuccessURL;

  /**
   * Continue shopping error redirect URL.
   */
  private String mContinueShoppingErrorURL;

  /**
   * Update success redirect URL.
   */
  private String mUpdateSuccessURL;

  /**
   * Update error redirect URL.
   */
  private String mUpdateErrorURL;

  /**
   * Express checkout success redirect URL.
   */
  private String mExpressCheckoutSuccessURL;

  /**
   * Express checkout error redirect URL.
   */
  private String mExpressCheckoutErrorURL;

  /**
   * Store express checkout form handler.
   */
  private StoreExpressCheckoutFormHandler mStoreExpressCheckoutFormHandler;

  /**
   * Is gift wrap selected.
   */
  private boolean mGiftWrapSelected;

  /**
   * Gift wrap SKU id.
   */
  private String mGiftWrapSkuId;

  /**
   * Gift wrap product id.
   */
  private String mGiftWrapProductId;

  /**
   * Is gift note selected.
   */
  private boolean mGiftNoteSelected;

  /**
   * Gift message URL.
   */
  private String mGiftMessageUrl;

  /**
   * Shipping info URL.
   */
  private String mShippingInfoURL;

  /**
   * Login during checkout URL.
   */
  private String mLoginDuringCheckoutURL;

  /**
   * Confirmation URL.
   */
  private String mConfirmationURL;

  /**
   * Add item to order success redirect URL.
   */
  protected String mAjaxAddItemToOrderSuccessUrl;

  /**
   * Add item to order error redirect URL.
   */
  protected String mAjaxAddItemToOrderErrorUrl;
  
  /**
   * Should initialize shipping information from profile.
   */
  protected boolean mInitializeShippingInfoFromProfile;

  /**
   * Store shipping group.
   */
  ShippingGroup mStoreShippingGroup = null;

  /**
   * Minimum quantity permitted per order item.
   */
  private long mMinQuantity = -1;
  /**
   * Maximum quantity permitted per order item.
   */
  private long mMaxQuantity = -1;
  
  /**
   * Whether or not an item requires a color option.
   */
  boolean mColorRequired;

  /**
   * Whether or not an item requires a size option.
   */
  boolean mSizeRequired;
  
  /**
   * property: skuType
   */
  private String mSkuType = null;
  
  /**
   * The skuType property is used by form handler when rendering a 'no SKU' exception.
   * Different messages are displayed to the user depending on the skuType property specified.
   * 
   * @return current skuType property value.
   */
  public String getSkuType()
  {
    return mSkuType;
  }

  /**
   * @param pSkuType the SKU type to set.
   */
  public void setSkuType(String pSkuType)
  {
    mSkuType = pSkuType;
  }
  
  /**
   * property: skuUnavailableURL
   */
  private String mSkuUnavailableURL;
  
  /**
   * URL in the event an unavailable item is added to cart using the JavaScript free picker.
   * 
   * @return the SKU unavailable URL.
   */
  public String getSkuUnavailableURL(){
    return mSkuUnavailableURL;
  }
  
  /**
   * URL in the event an unavailable item is added to cart using the JavaScript free picker.
   * 
   * @param pSkuUnavailableURL the SKU unavailable URL to set.
   */
  public void setSkuUnavailableURL(String pSkuUnavailableURL){
    mSkuUnavailableURL = pSkuUnavailableURL;
  }
  
  /**
   * property: inventoryManager.
   */
  protected StoreInventoryManager mInventoryManager;

  /**
   * @return the inventoryManager.
   */
  public StoreInventoryManager getInventoryManager() {
    return mInventoryManager;
  }

  /**
   * @param pInventoryManager - the inventoryManager to set.
   */
  public void setInventoryManager(StoreInventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }
  
  /**
   * property: Reference to the CartProcessHelper component
   */
  private StoreCartProcessHelper mCartHelper;
  
  /**
   * property: checkoutProgressStates
   */
  private CheckoutProgressStates mCheckoutProgressStates;

  /**
   * @return the checkout progress states
   */
  public CheckoutProgressStates getCheckoutProgressStates()
  {
    return mCheckoutProgressStates;
  }

  /**
   * @param pCheckoutProgressStates the checkout progress states to set
   */
  public void setCheckoutProgressStates(CheckoutProgressStates pCheckoutProgressStates)
  {
    mCheckoutProgressStates = pCheckoutProgressStates;
  }

  /**
   * @return the Cart Helper component.
   */
  public StoreCartProcessHelper getCartHelper() {
    return mCartHelper;
  }

  /**
   * @param pCartHelper the cart helper component to set.
   */
  public void setCartHelper(StoreCartProcessHelper pCartHelper) {
    mCartHelper = pCartHelper;
  }
  
  /**
   * Sets property AddItemToGiftlistSuccessURL.
   * 
   * @param pAddItemToGiftlistSuccessURL - The property to store the success URL for addItemToGiftlist.
   * 
   * @beaninfo description: The property to store the success URL for addItemToGiftlist.
   */
  public void setAddItemToGiftlistSuccessURL (String pAddItemToGiftlistSuccessURL) {
    mAddItemToGiftlistSuccessURL = pAddItemToGiftlistSuccessURL;
  }

  /**
   * Returns property AddItemToGiftlistSuccessURL.
   * 
   * @return The value of the property AddItemToGiftlistSuccessURL
   */
  public String getAddItemToGiftlistSuccessURL() {
    return mAddItemToGiftlistSuccessURL;
  }

  /**
   * Sets property AddItemToGiftlistErrorURL.
   * 
   * @param pAddItemToGiftlistErrorURL - The property to store the error URL for addItemToGiftlist.
   * 
   * @beaninfo description: The property to store the error URL for addItemToGiftlist.
   */
  public void setAddItemToGiftlistErrorURL(String pAddItemToGiftlistErrorURL) {
    mAddItemToGiftlistErrorURL = pAddItemToGiftlistErrorURL;
  }

  /**
   * Returns property AddItemToGiftlistErrorURL.
   * 
   * @return The value of the property AddItemToGiftlistErrorURL
   */
  public String getAddItemToGiftlistErrorURL() {
    return mAddItemToGiftlistErrorURL;
  }

  /**
   * Sets property AddItemToGiftlistLoginURL.
   * 
   * @param pAddItemToGiftlistLoginURL -
   *          The property to store the URL for where the user should be redirected
   *          if they attempt to add an item to the gift list without being logged in.
   *        
   * @beaninfo description:  The property to store the URL for where the user should be redirected
   *                         if they attempt to add an item to the gift list without being logged in.
   */
  public void setAddItemToGiftlistLoginURL(String pAddItemToGiftlistLoginURL) {
    mAddItemToGiftlistLoginURL = pAddItemToGiftlistLoginURL;
  }

  /**
   * @return add item to gift list login URL.
   */
  public String getAddItemToGiftlistLoginURL() {
    return mAddItemToGiftlistLoginURL;
  }
  
  /**
   * Sets property AddItemToGiftlist.
   * 
   * @param pAddItemToGiftlist  The property to store the gift list Id to which item should be added.
   *        
   * @beaninfo description: The property to store the gift list Id to which
   *                        item should be added.
   */
  public void setAddItemToGiftlist(String pAddItemToGiftlist) {
    mAddItemToGiftlist = pAddItemToGiftlist;
  }

  /**
   * @return AddItemToGiftlist property.
   */
  public String getAddItemToGiftlist() {
    return mAddItemToGiftlist;
  }

  /**
   * @return the continue shopping success redirect URL.
   */
  public String getContinueShoppingSuccessURL() {
    return mContinueShoppingSuccessURL;
  }

  /**
   * @param pContinueShoppingSuccessURL - the continue shopping success redirect URL.
   */
  public void setContinueShoppingSuccessURL(String pContinueShoppingSuccessURL) {
    mContinueShoppingSuccessURL = pContinueShoppingSuccessURL;
  }

  /**
   * @return the continue shopping error redirect URL.
   */
  public String getContinueShoppingErrorURL() {
    return mContinueShoppingErrorURL;
  }

  /**
   * @param pContinueShoppingErrorURL - the continue shopping error redirect URL.
   */
  public void setContinueShoppingErrorURL(String pContinueShoppingErrorURL) {
    mContinueShoppingErrorURL = pContinueShoppingErrorURL;
  }

  /**
   * @param pRemoveItemFromOrder - the removal commerce ids.
   */
  public void setRemoveItemFromOrder(String pRemoveItemFromOrder) {
    if (!StringUtils.isBlank(pRemoveItemFromOrder)) {
        setRemovalCommerceIds(new String[]{pRemoveItemFromOrder});
    }
    else {
      setRemovalCommerceIds(null);
    }
  }

  /**
   * @return the update success redirect URL.
   */
  public String getUpdateSuccessURL() {
    return mUpdateSuccessURL;
  }

  /**
   * @param pUpdateSuccessURL - the update success redirect URL.
   */
  public void setUpdateSuccessURL(String pUpdateSuccessURL) {
    mUpdateSuccessURL = pUpdateSuccessURL;
  }

  /**
   * @return the update error redirect URL.
   */
  public String getUpdateErrorURL() {
    return mUpdateErrorURL;
  }

  /**
   * @param pUpdateErrorURL - the update error redirect URL.
   */
  public void setUpdateErrorURL(String pUpdateErrorURL) {
    mUpdateErrorURL = pUpdateErrorURL;
  }

  /**
   * @return the express checkout success redirect URL.
   */
  public String getExpressCheckoutSuccessURL() {
    return mExpressCheckoutSuccessURL;
  }

  /**
   * @param pExpressCheckoutSuccessURL - the express checkout success redirect URL.
   */
  public void setExpressCheckoutSuccessURL(String pExpressCheckoutSuccessURL) {
    mExpressCheckoutSuccessURL = pExpressCheckoutSuccessURL;
  }

  /**
   * @return the express checkout error redirect URL.
   */
  public String getExpressCheckoutErrorURL() {
    return mExpressCheckoutErrorURL;
  }

  /**
   * @param pExpressCheckoutErrorURL - the express checkout error redirect URL.
   */
  public void setExpressCheckoutErrorURL(String pExpressCheckoutErrorURL) {
    mExpressCheckoutErrorURL = pExpressCheckoutErrorURL;
  }

  /**
   * @return the Store express checkout form handler.
   */
  public StoreExpressCheckoutFormHandler getStoreExpressCheckoutFormHandler() {
    return mStoreExpressCheckoutFormHandler;
  }

  /**
   * @param pStoreExpressCheckoutFormHandler - the Store express checkout form handler.
   */
  public void setStoreExpressCheckoutFormHandler(
    StoreExpressCheckoutFormHandler pStoreExpressCheckoutFormHandler) {
    
    mStoreExpressCheckoutFormHandler = pStoreExpressCheckoutFormHandler;
  }

  /**
   * @return the gift wrap selected property.
   */
  public boolean isGiftWrapSelected() {
    return mGiftWrapSelected;
  }

  /**
   * @param  pGiftWrapSelected - the gift wrap selected property.
   */
  public void setGiftWrapSelected(boolean pGiftWrapSelected) {
    mGiftWrapSelected = pGiftWrapSelected;
  }

  /**
   * @return the gift wrap SKU id property.
   */
  public String getGiftWrapSkuId() {
    return mGiftWrapSkuId;
  }

  /**
   * Sets the mGiftWrapSkuId property. This is set by the page after getting the SKU id from the targeter.
   * 
   * @param pGiftWrapSkuId - gift wrap sku id property.
   */
  public void setGiftWrapSkuId(String pGiftWrapSkuId) {
    mGiftWrapSkuId = pGiftWrapSkuId;
  }

  /**
   * @return the gift wrap product id property.
   */
  public String getGiftWrapProductId() {
    return mGiftWrapProductId;
  }

  /**
   * @param pGiftWrapProductId - the gift wrap product id property.
   */
  public void setGiftWrapProductId(String pGiftWrapProductId) {
    mGiftWrapProductId = pGiftWrapProductId;
  }

  /**
   * @return the gift note selected property.
   */
  public boolean isGiftNoteSelected() {
    return mGiftNoteSelected;
  }

  /**
   * @param pGiftNoteSelected - the gift note selected property.
   */
  public void setGiftNoteSelected(boolean pGiftNoteSelected) {
    mGiftNoteSelected = pGiftNoteSelected;
  }

  /**
   * @return the gift message URL property.
   */
  public String getGiftMessageUrl() {
    return mGiftMessageUrl;
  }

  /**
   * @param pGiftMessageUrl - the gift message URL property.
   */
  public void setGiftMessageUrl(String pGiftMessageUrl) {
    mGiftMessageUrl = pGiftMessageUrl;
  }

  /**
   * @return the shipping information URL property.
   */
  public String getShippingInfoURL() {
    return mShippingInfoURL;
  }

  /**
   * @param pShippingInfoURL - the shipping information URL property.
   */
  public void setShippingInfoURL(String pShippingInfoURL) {
    mShippingInfoURL = pShippingInfoURL;
  }

  /**
   * @return the login during checkout URL property.
   */
  public String getLoginDuringCheckoutURL() {
    return mLoginDuringCheckoutURL;
  }

  /**
   * @param pLoginDuringCheckoutURL - the login during checkout URL property.
   */
  public void setLoginDuringCheckoutURL(String pLoginDuringCheckoutURL) {
    mLoginDuringCheckoutURL = pLoginDuringCheckoutURL;
  }
  
  private String mCouponCode;

  /**
   * @return a coupon code to be claimed
   */
  public String getCouponCode()
  {
    return mCouponCode;
  }
  
  /**
   * @param pCouponCode the coupon code to set
   */
  public void setCouponCode(String pCouponCode)
  {
    mCouponCode = pCouponCode;
  }

  /**
   * @return the confirmation URL property.
   */
  public String getConfirmationURL() {
    return mConfirmationURL;
  }

  /**
   * @param pConfirmationURL - the confirmation URL property.
   */
  public void setConfirmationURL(String pConfirmationURL) {
    mConfirmationURL = pConfirmationURL;
  }

  /**
   * Gets the AjaxAddItemToOrderSuccessUrl.
   * 
   * @return the add item to order success redirect URL.
   */
  public String getAjaxAddItemToOrderSuccessUrl() {
    return mAjaxAddItemToOrderSuccessUrl;
  }

  /**
   * @param pAjaxAddItemToOrderSuccessUrl -the add item to order success redirect Url to set.
   */
  public void setAjaxAddItemToOrderSuccessUrl(String pAjaxAddItemToOrderSuccessUrl) {
    mAjaxAddItemToOrderSuccessUrl = pAjaxAddItemToOrderSuccessUrl;
  }

  /**
   * Gets the AjaxAddItemToOrderErrorUrl.
   * 
   * @return the add item to order error redirect URL.
   */
  public String getAjaxAddItemToOrderErrorUrl() {
    return mAjaxAddItemToOrderErrorUrl;
  }

  /**
   * @param pAjaxAddItemToOrderErrorUrl - the add item to order error Url to set.
   */
  public void setAjaxAddItemToOrderErrorUrl(String pAjaxAddItemToOrderErrorUrl) {
    mAjaxAddItemToOrderErrorUrl = pAjaxAddItemToOrderErrorUrl;
  }

  /**
   * @return true if shipping info should be initialized from profile property.
   */
  public boolean isInitializeShippingInfoFromProfile() {
    return mInitializeShippingInfoFromProfile;
  }

  /**
   * Set the minimum quantity permitted per order item.
   *
   * @param pMinQuantity minimum quantity permitted per order item.
   */
  public void setMinQuantity(long pMinQuantity) {
      mMinQuantity = pMinQuantity;
  }

  /**
   * Returns the minimum quantity permitted per order item.
   *
   * @return minimum quantity permitted per order item.
   */
  public long getMinQuantity() {
      return mMinQuantity;
  }


  /**
   * Set the maximum quantity permitted per order item.
   * 
   * @param pMaxQuantity maximum quantity permitted per order item.
   */
  public void setMaxQuantity(long pMaxQuantity) {
      mMaxQuantity = pMaxQuantity;
  }

  /**
   * Returns the maximum quantity permitted per order item.
   *
   * @return maximum quantity permitted per order item.
   */
  public long getMaxQuantity() {
      return mMaxQuantity;
  }
  
  /**
   * Returns whether or not an item requires a color option.
   * 
   * @return whether or not an item requires a color option.
   */
  public boolean getColorRequired() {
    return mColorRequired;
  }
  
  /**
   * Sets whether or not an item requires a color option.
   * 
   * @param pColorRequired whether or not an item requires a color option.
   */
  public void setColorRequired(boolean pColorRequired) {
    mColorRequired = pColorRequired;
  }
  
  /**
   * Returns whether or not an item requires a size option.
   * 
   * @return whether or not an item requires a size option.
   */
  public boolean getSizeRequired() {
    return mSizeRequired;
  }
  
  /**
   * Sets whether or not an item requires a size option.
   * 
   * @param pSizeRequired whether or not an item requires a size option.
   */
  public void setSizeRequired(boolean pSizeRequired) {
    mSizeRequired = pSizeRequired;
  }
  
  /**
   * Gift list form handler.
   */
  private StoreGiftlistFormHandler mStoreGiftlistFormHandler;
  
  /**
   * @return the Gift list form handler.
   */
  public StoreGiftlistFormHandler getStoreGiftlistFormHandler() {
    return mStoreGiftlistFormHandler;
  }

  /**
   * @param pStoreGiftlistFormHandler - the Gift list form handler.
   */
  public void setStoreGiftlistFormHandler(StoreGiftlistFormHandler pStoreGiftlistFormHandler) {
    mStoreGiftlistFormHandler = pStoreGiftlistFormHandler;
  }
  
  /**
   * property: requestBean
   */
  private SessionBean mRequestBean;
  
  /**
   * @return the request bean object.
   */
  public SessionBean getRequestBean() {
    return mRequestBean;
  }

  /**
   * @param pRequestBean - the request bean object.
   */
  public void setRequestBean(SessionBean pRequestBean) {
    mRequestBean = pRequestBean;
  }

  //---------------------------------------------------------------------------
  // property: GwpManager

  /**
   * Manager component for gift with purchase promotions
   */
  protected GWPManager mGwpManager;

  /**
   * Setter for the gift with purchase manager property.
   * 
   * @param pGwpManager GWPManager.
   */
  public void setGwpManager(GWPManager pGwpManager) {
    mGwpManager = pGwpManager;
  }

  /**
   * Getter for the gift with purchase manager property.
   * 
   * @return GWPManager
   */
  public GWPManager getGwpManager() {
    return mGwpManager;
  }  
  
  
  //-------------------------------------
  // Methods
  //-------------------------------------

  
  /**
   * This method is called when the user wants to starts the CHECKOUT process
   * for an order.  It will first validates quantity.  If quantity is valid 
   * it checks if order contains only items with zero quantity and update order 
   * (remove all commerce items) if so. After this has happened it will 
   * claim coupons and invokes super.handleMoveToPurchaseInfoByCommerceId method.
   *
   * @param pRequest the servlet's request.
   * @param pResponse the servlet's response.
   * 
   * @return a <code>boolean</code> value.
   * 
   * @exception ServletException if there was an error while executing the code.
   * @exception IOException if there was an error with servlet io.
   */
  public boolean handleCheckout(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    if (!getFormError()) {
      validateQuantity(pRequest, pResponse);
    }
    
    if(!getFormError()) {
      long overallQuantity = evalOverallItemsQuantity(pRequest, pResponse);
      
      // Zero overall quantity means that order contains only items with zero 
      // quantity. We want to mimic 'update' action to remove such items 
      // from the order and return to the empty shopping cart page.
      // Otherwise, we'll get an error later from ProcValidateOrderForCheckout pipeline
      // chain which checks items' quantity.
      if(overallQuantity == 0) {
        return handleUpdate(pRequest, pResponse);
      }
    }

    // claim any coupons entered by the user
    if (!getFormError()) {
      tenderCoupon(pRequest, pResponse);
    }   

    if (getFormError()) {
      if (isLoggingDebug()) {
        logDebug("Not calling handleMoveToPurchaseInfo, form error encountered.");
      }

      return checkFormRedirect(null, getMoveToPurchaseInfoErrorURL(), pRequest, pResponse);
    }
    
    return super.handleMoveToPurchaseInfoByCommerceId(pRequest, pResponse);
  }

  /**
   * The addRemoveGiftServices method should be done after modifying cart contents. 
   * The modifyOrder method will remove the GWP item from the cart.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  @Override
  public void postMoveToPurchaseInfo(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    // add or remove any gift services.
    if (!getFormError()) {
      try {
        addRemoveGiftServices();
      }
      catch (CommerceException ce) {
        processException(ce, StoreCartProcessHelper.MSG_ERROR_MODIFYING_ORDER, pRequest, pResponse);
      }
      // Must call determineSuccessURL after addRemoveGiftServices, otherwise
      // the gift note may not have been added yet, and redirect will not
      // be computed correctly.
      determineSuccessURL(pRequest);
    }
    
    super.postMoveToPurchaseInfo(pRequest, pResponse);
    
    if (mCheckoutProgressStates != null && !getFormError())
    {
      mCheckoutProgressStates.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SHIPPING.toString());
    }
  }

  /**
   * This method will update the cart contents.
   *
   * @param pRequest - HTTP request.
   * @param pResponse - HTTP response.
   *
   * @return true if success, otherwise false.
   *
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   */
  public boolean handleUpdate(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    validateQuantity(pRequest, pResponse);

    if (getFormError()) {
      return checkFormRedirect(null, getUpdateErrorURL(), pRequest, pResponse);
    }

    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleMoveToPurchaseInfoByCommerceId";

    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod))) {
      Transaction tr = null;

      try {
        tr = ensureTransaction();

        if (getUserLocale() == null) {
          setUserLocale(getUserLocale(pRequest, pResponse));
        }

        if (!checkFormRedirect(null, getUpdateErrorURL(), pRequest, pResponse)) {
          return false;
        }

        synchronized (getOrder()) {
          // Claim any coupons entered by the user.
          if (!getFormError()) {
            tenderCoupon(pRequest, pResponse);
          }

          // Now modify the order based upon what the user put in the form.
          if (!getFormError()) {
            modifyOrderByCommerceId(pRequest, pResponse);
          }
          
          // After taking care of the user's modifications, update gift services and gift wrap.
          if (!getFormError()) {
            addRemoveGiftServices();
          }

          updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        
        } // synchronized
      } 
      catch (CommerceException ce) {
        processException(ce, StoreCartProcessHelper.MSG_ERROR_MODIFYING_ORDER, pRequest, pResponse);
      } 
      catch (RunProcessException re) {
        processException(re, StoreCartProcessHelper.MSG_ERROR_MODIFYING_ORDER, pRequest, pResponse);
      } 
      finally {
        if (tr != null) {
          commitTransaction(tr);
        }

        if (rrm != null) {
          rrm.removeRequestEntry(myHandleMethod);
        }
      }
    }

    // If NO form errors are found, redirect to the success URL.
    // If form errors are found, redirect to the error URL.
    return checkFormRedirect(getUpdateSuccessURL(), getUpdateErrorURL(), pRequest, pResponse);
  }

  /**
   * Need to do the exact same thing as handleUpdate, but redirect to different URL. Let page 
   * specify success URL based on Profile.categoryLastBrowsed. Set updateSuccessURL here, and 
   * re-use handleUpdate code.
   *
   * @param pRequest the request.
   * @param pResponse the response.
   * 
   * @return boolean success or failure.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   */
  public boolean handleContinueShopping(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    setUpdateSuccessURL(getContinueShoppingSuccessURL());
    setUpdateErrorURL(getContinueShoppingErrorURL());

    return handleUpdate(pRequest, pResponse);
  }

  /**
   * {@inheritDoc}
   *
   * @param pRequest the request object.
   * @param pResponse the response object.
   * 
   * @return true if the request was handled properly.
   * 
   * @exception IOException if an error occurs.
   * @exception ServletException if an error occurs.
   */
  @Override
  public boolean handleRemoveItemFromOrder(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
    throws IOException, ServletException {
    
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleRemoveItemFromOrder";
    
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod))) {
      
      Transaction tr = null;
      
      try {
        tr = ensureTransaction();
        
        if (getUserLocale() == null) {
          setUserLocale(getUserLocale(pRequest, pResponse));
        }

        if (! checkFormRedirect (null, getRemoveItemFromOrderErrorURL(), pRequest, pResponse)){
          return false;
        }

        synchronized(getOrder()) {
          preRemoveItemFromOrder(pRequest, pResponse);

          if (! checkFormRedirect (null, getRemoveItemFromOrderErrorURL(), pRequest, pResponse)){
            return false;
          }

          if (getRemovalCommerceIds() != null) {
            try {
              deleteItems(pRequest, pResponse);
              Map extraParams = createRepriceParameterMap();
            } 
            catch (Exception exc) {
              processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
            }
          }
          if (! checkFormRedirect (null, getRemoveItemFromOrderErrorURL(), pRequest, pResponse)){
            return false;
          }

          postRemoveItemFromOrder(pRequest, pResponse);

          updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        } // synchronized

        // If NO form errors are found, redirect to the success URL.
        // If form errors are found, redirect to the error URL.
        return checkFormRedirect (getRemoveItemFromOrderSuccessURL(), 
                                  getRemoveItemFromOrderErrorURL(), 
                                  pRequest, pResponse);
      }
      finally {
        if (tr != null) {
          commitTransaction(tr);
        }
        if (rrm != null){
          rrm.removeRequestEntry(myHandleMethod);
        }
      }
    }
    else {
      return false;
    }
  }

  /**
   * Overrides the getQuantity method to return commerce item's current quantity
   * if the item is already in the cart. Also as the regular commerce item and GWP associated
   * with the commerce item are split into different rows in the storefront UI the GWP quantity 
   * is added to the item's quantity.
   * 
   * @param pCatalogRefId the commerce item to process.
   * @param pRequest the request.
   * @param pResponse the response.
   * 
   * @return the commerce item's current quantity.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   * @exception NumberFormatException
   */
  @Override
  public long getQuantityByCatalogRefId(String pCatalogRefId,
                                        DynamoHttpServletRequest pRequest,
                                        DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, NumberFormatException {
    
    long quantity = 0L;
    Order order = getOrder();
    List<CommerceItem> items = null;
    
    try {
      items = order.getCommerceItemsByCatalogRefId(pCatalogRefId);
    }
    catch (CommerceItemNotFoundException ex) {
      // That's OK, item is just not in the cart yet
    }
    catch (InvalidParameterException e) {
      if (isLoggingError()) {
        logError("There was an error getting commerce item for the specified Id: " + pCatalogRefId, e);
      }
    }
    
    if (items == null || items.size() == 0) {
      // Retrieve the quantity from the request parameter
      quantity = super.getQuantity(pCatalogRefId, pRequest, pResponse);
    }
    else {
      // Calculate the quantity according to if this is a regular commerce item or a
      // commerce item with non-GWP and GWP parts split across multiple rows in the store front.
      for (CommerceItem item : items) {
        quantity += getQuantity(item, pCatalogRefId, pRequest, pResponse);
      }
    }
    return quantity;
  }
  
  /**
   * Overrides the getQuantity method to return commerce item's current quantity
   * if the item is already in the cart. Also as the regular commerce item and GWP associated
   * with the commerce item are split into different rows in the store front UI the GWP quantity 
   * is added to the item's quantity.
   * 
   * @param pCommerceId the commerce item to process.
   * @param pRequest the request.
   * @param pResponse the response.
   * 
   * @return the commerce item's current quantity.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   * @exception NumberFormatException
   */
  @Override
  public long getQuantityByCommerceId(String pCommerceId,
                                      DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, NumberFormatException {

    Order order = getOrder();
    CommerceItem item = null;
    try {
      item = order.getCommerceItem(pCommerceId);
    } catch (CommerceItemNotFoundException ex) {
      // That's OK, item is just not in the cart yet
    } catch (InvalidParameterException e) {
      if (isLoggingError()) {
        logError("There was an error getting commerce item for the specified Id: " + pCommerceId, e);
      }
    }
    long quantity = getQuantity(item, pCommerceId, pRequest, pResponse);
    return quantity;
  }
  
  
  /**
   * Overrides the getQuantity method to return commerce item's current quantity
   * if the item is already in the cart. Also as the regular commerce item and GWP associated
   * with the commerce item are split into different rows in the store front UI the GWP quantity 
   * is added to the item's quantity.
   * 
   * @param pRelationshipId the commerce item relationship id of the commerce item to process.
   * @param pRequest the request.
   * @param pResponse the response.
   * 
   * @return the commerce item's current quantity.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   * @exception NumberFormatException
   */
  @Override
  public long getQuantityByRelationshipId(String pRelationshipId,
                                      DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, NumberFormatException {
    
    CommerceItem item = null;
    try {
      List<CommerceItemRelationship> rels = getShippingGroupCommerceItemRelationships(getOrder());
      if (rels != null) {
        for (Relationship rel : rels) {
          if (rel.getId().equals(pRelationshipId)) {
            item = ((CommerceItemRelationship)rel).getCommerceItem();
            break;
          }
        }
      }
    }
    catch (CommerceException e) {
      if (isLoggingError()) {
        logError("There was an error getting order relationships. ", e);
      }              
    }
    long quantity = getQuantity(item, pRelationshipId, pRequest, pResponse);
    return quantity;
  }
  
  /** Helper method that returns the quantity for a commerce item pItem for the given pId which is either a catalogRefId, commerceItemId or relationshipId.
   *  Takes GWP (gift with purchase) into account when calculating the quantity so as to not include the free gift in the quantity.
   * 
   * @param pId the catalog ref id, commerce item id or relationship id that identifies the commerce item.  
   *        The quantity is passed into the request with this id as the key and the quantity as the value.
   * @param pRequest the request.
   * @param pResponse the response.
   * 
   * @return the commerce item's current quantity.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   * @exception NumberFormatException
   */
  protected long getQuantity(CommerceItem pItem, String pId,
                             DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, NumberFormatException {
    
    long quantity = 0L;
    if (pItem == null) {
      quantity = super.getQuantity(pId, pRequest, pResponse);
      return quantity;
    }

    String removeItemGwpId = getRemoveItemGwpId();
    String removeItemNonGwpId = getRemoveItemNonGwpId();
    String commerceId = pItem.getId();

    boolean removeGwp = !StringUtils.isEmpty(removeItemGwpId);
    boolean removeNonGwp = !StringUtils.isEmpty(removeItemNonGwpId);

    // Calculate the quantity according to if this is a regular commerce item or a
    // commerce item with non-GWP and GWP parts split across multiple rows in the store front.
    if (removeGwp && commerceId.equals(removeItemGwpId)) {
      // If 'removeGwpItemId' has been set then this means the shopper has submitted the
      // form by clicking the remove action on a GWP cart item so we need to take this
      // into account when updating the quantity. As all our free gifts are on separate
      // lines we can assume to decrement by 1.
      quantity = pItem.getQuantity() - 1;
    }
    else if (removeNonGwp && commerceId.equals(removeItemNonGwpId)) {
      // If 'removeItemNonGwpId' has been set then this means the shopper has submitted the
      // form by clicking the remove action on a non-GWP part of a cart item that is split with  
      // both non-GWP and GWP parts so we need to decrease the quantity by this amount. 
      quantity = pItem.getQuantity() - Long.parseLong(mItemNonGwpQuantities.get(removeItemNonGwpId));      
    }
    else if ((removeGwp && !commerceId.equals(removeItemGwpId))
             || (removeNonGwp && !commerceId.equals(removeItemNonGwpId))) {
      // A remove action has been initiated by the shopper but not for this commerce item
      // so the quantity should remain untouched.
      quantity = pItem.getQuantity();
    }
    else {
      // This is an update on the quantity of a regular commerce item, so obtain the quantity 
      // from the request parameter, loop through any gwp selections adding their quantity to
      // the item's quantity specified in the request.
      try {
        // Retrieve the quantity from the request parameter
        quantity = super.getQuantity(pId, pRequest, pResponse);

        Collection<GiftWithPurchaseSelection> selections =  getGwpManager().getSelections(getOrder(), pItem);

        for (GiftWithPurchaseSelection selection : selections) {
          long giftQty = selection.getAutomaticQuantity() + 
          selection.getTargetedQuantity() + 
          selection.getSelectedQuantity();
          quantity += giftQty;
        }
      } 
      catch (CommerceException e) {
        if (isLoggingError()) {
          logError("There was an error getting selections from qwp manager. ", e);
        }              
      } 
    }
    return quantity;
  }
  
  /**
   * Need to use the same form handler from the page. Modify cart contents,
   * and then call the ExpressCheckoutFormHandler to use express checkout.
   * 
   * @param pRequest the request.
   * @param pResponse the response.
   * 
   * @return boolean success or failure.
   * 
   * @throws ServletException If servlet exception occurs.
   * @throws IOException If IO exception occurs.
   */
  public boolean handleExpressCheckout(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    Transaction tr = null;

    validateQuantity(pRequest, pResponse);
    
    if (getFormError()) {
      return checkFormRedirect(null, getExpressCheckoutErrorURL(), pRequest, pResponse);
    }

    try {
      tr = ensureTransaction();

      synchronized (getOrder()) {
        // Claim any coupons entered by the user.
        if (!getFormError()) {
          tenderCoupon(pRequest, pResponse);
        }

        // Now modify the order based upon what the user put in the form.
        if (!getFormError()) {
          modifyOrderByCommerceId(pRequest, pResponse);
        } 
        else {
          return checkFormRedirect(getExpressCheckoutSuccessURL(), 
                                   getExpressCheckoutErrorURL(), 
                                   pRequest, pResponse);
        }
        
        // After taking care of the user's modications now do ours start with gift services and gift wrap;
        if (!getFormError()) {
          addRemoveGiftServices();
        }

        // Need to check inventory. Just run moveToConfirmation
        // chain which contains inventory check.
        try {
          // Make sure inventory is valid for checkout
          if (getUserLocale() == null) {
            setUserLocale(getUserLocale(pRequest, pResponse));
          }

          runProcessMoveToPurchaseInfo(getOrder(), 
                                       getUserPricingModels(), 
                                       getUserLocale(), 
                                       getProfile(), 
                                       null);
        } 
        catch (Exception exc) {
          if (isLoggingDebug()) {
            logDebug("exception: ", exc);
          }

          processException(exc, MSG_ERROR_MOVE_TO_PURCHASE_INFO, pRequest, pResponse);
        }

        if (getFormError()) {
          // If NO form errors are found, redirect to the success URL.
          // If form errors are found, redirect to the error URL.
          return checkFormRedirect(getExpressCheckoutSuccessURL(), 
                                   getExpressCheckoutErrorURL(), 
                                   pRequest, pResponse);
        }
      } // synchronized

      StoreExpressCheckoutFormHandler expressCheckoutFormHandler = getStoreExpressCheckoutFormHandler();

      determineExpressCheckoutSuccessURL(pRequest, pResponse);
      expressCheckoutFormHandler.setExpressCheckoutSuccessURL(getExpressCheckoutSuccessURL());
      expressCheckoutFormHandler.setExpressCheckoutErrorURL(getExpressCheckoutErrorURL());

      return expressCheckoutFormHandler.handleExpressCheckout(pRequest, pResponse);
      
    } 
    catch (CommerceException ce) {
      String msg = formatUserMessage(StoreCartProcessHelper.MSG_ERROR_MODIFYING_ORDER, pRequest, pResponse);
      addFormException(new DropletFormException(msg, ce, getCartHelper().MSG_ERROR_MODIFYING_ORDER));
    } 
    catch (RunProcessException rpe) {
      String msg = formatUserMessage(StoreCartProcessHelper.MSG_ERROR_MODIFYING_ORDER, pRequest, pResponse);
      addFormException(new DropletFormException(msg, rpe, StoreCartProcessHelper.MSG_ERROR_MODIFYING_ORDER));
    } 
    finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }

    return checkFormRedirect(getExpressCheckoutSuccessURL(), getExpressCheckoutErrorURL(), pRequest, pResponse);
  }
 
  /**
   * Removes gift services if all items in the order are gift wrap items.
   * 
   * @return true if all items are gift wrap items and gift wrap services were removed
   */
  protected boolean removeGiftServicesWhenAllGiftWrap() throws CommerceException {
    StoreOrderImpl order = (StoreOrderImpl) getOrder();
    
    // If the order is all gift wrap, remove it unconditionally.
    if (getCartHelper().isAllGiftWrap(order)) {
      getCartHelper().addRemoveGiftServices(order, false, false, getGiftWrapSkuId(), getGiftWrapProductId());

      return true;
    }

    return false;
  }

  /**
   * This is a convenience method for adding gift services. This needs to
   * be done when the update, checkout, delete or continue shopping buttons are
   * pushed.
   */
  protected void addRemoveGiftServices() throws CommerceException {
    if (isLoggingDebug()) {
      logDebug("addRemoveGiftService(): giftwrap=" + isGiftWrapSelected() + " giftnote=" + isGiftNoteSelected() +
        " giftwrapsku=" + getGiftWrapSkuId() + " giftwrapprod=" + getGiftWrapProductId() +
        " order.getGiftMessagePopulated(): " + ((StoreOrderImpl) getOrder()).getGiftMessagePopulated());
    }

    StoreOrderImpl order = (StoreOrderImpl) getOrder();

    // If gift wrap is either being added or removed, we need to re-price.
    boolean reprice = getCartHelper().isGiftWrapAddedOrRemoved(order, isGiftWrapSelected());

    if (!removeGiftServicesWhenAllGiftWrap()) {
      getCartHelper().addRemoveGiftServices(order, 
                                            isGiftWrapSelected(), 
                                            isGiftNoteSelected(), 
                                            getGiftWrapSkuId(),
                                            getGiftWrapProductId());
    }

    if(reprice) {
      try {
        runProcessRepriceOrder(getModifyOrderPricingOp(), 
                               order, 
                               getUserPricingModels(), 
                               getUserLocale(), 
                               getProfile(),
                               null);
      } 
      catch (RunProcessException rpe) {
        if (isLoggingError()) {
          logError(LogUtils.formatMajor(rpe.toString()), rpe);
        }
      }
    }
  }
  
  /**
   * Claim the specified coupon, register a form exception if the coupon is invalid or an error occurs.
   *
   * @param pRequest - current HTTP servlet request.
   * @param pResponse - current HTTP servlet response.
   *
   * @throws ServletException if an error occurred during claiming the coupon.
   * @throws IOException if an error occurred during claiming the coupon.
   */
  private void tenderCoupon(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    try {
      boolean couponTendered = 
        ((StorePurchaseProcessHelper) getPurchaseProcessHelper()).tenderCoupon(getCouponCode(),
                                                                               (StoreOrderImpl) getOrder(),
                                                                               getProfile(),
                                                                               getUserPricingModels(),
                                                                               getUserLocale());
      if (!couponTendered) {
        String errorMessage = formatUserMessage(StorePurchaseProcessHelper.MSG_UNCLAIMABLE_COUPON,
                                                pRequest, pResponse);

        addFormException(new DropletFormException(errorMessage,
                                                  "",
                                                  StorePurchaseProcessHelper.MSG_UNCLAIMABLE_COUPON));
      }
    }
    catch (Exception exception) {
      processException(exception,
                       StoreCartProcessHelper.MSG_UNCLAIMABLE_COUPON,
                       pRequest, pResponse);
    }
  }

  /**
   *  <p>
   *    Utility method to create success URL based on if the user and order state.
   *    Not possible to set this in the page b/c we don't know if the
   *    user selects gift message or not when clicking "checkout" button.
   *    Set success URL based on current state of order and login status.
   *  </p>
   *  <br/>
   *  <p>
   *    if ((order doesn't have samples AND user has not chosen to skip samples)
   *          OR (order has gift message))
   *        redirect to samples page.
   *    if (order has samples OR user explicitly chose no samples)
   *       if (user is logged in)
   *          redirect to shipping
   *       else
   *          redirect to login
   *  </p>
   *  
   * @param pRequest - HTTP request.
   */
  protected void determineSuccessURL(DynamoHttpServletRequest pRequest) {
    
    StoreOrderImpl order = (StoreOrderImpl) getOrder();    

    // If order has gift message, but it is not populated yet, then show gift message page.
    if (order.getContainsGiftMessage() || order.isShouldAddGiftNote()) {
      if (!order.getGiftMessagePopulated()) {
        if (isLoggingDebug()) {
          logDebug("User has a gift message that hasn't been filled out yet." + 
            " Sending to gift message page.");
        }

        setMoveToPurchaseInfoSuccessURL(getGiftMessageUrl());

        return;
      }
    }

    // Don't show samples page, choose shipping or login depending on login.
    if (getCartHelper().isLoginUser(getProfile())) {
      setMoveToPurchaseInfoSuccessURL(getShippingInfoURL());
    } else {
      // Not logged in yet.
      setMoveToPurchaseInfoSuccessURL(getLoginDuringCheckoutURL());
    }
  }

  /**
   * Directs to a gift message page if the order contains a gift special instruction and the message
   * hasn't been filled out.
   * 
   * @param pRequest - HTTP request.
   * @param pResponse - HTTP response.
   */
  protected void determineExpressCheckoutSuccessURL(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) {
    StoreOrderImpl order = (StoreOrderImpl) getOrder();

    // Initialize success URL to confirmation page.
    setExpressCheckoutSuccessURL(getConfirmationURL());

    // If order has gift message, then show gifts and samples page so user can update gift message.
    if (order.getContainsGiftMessage() || order.isShouldAddGiftNote()) {
      if (!order.getGiftMessagePopulated()) {
        if (isLoggingDebug()) {
          logDebug("User has a gift message that hasn't been filled out yet." + 
            " Sending to gift message page.");
        }

        setExpressCheckoutSuccessURL(getGiftMessageUrl());
        pRequest.addQueryParameter("express", "true");
        return;
      }
    }
    
    // Choose ExpressShipping or login depending on login.
    if (getCartHelper().isLoginUser(getProfile())) {
      setExpressCheckoutSuccessURL(getConfirmationURL());
    } 
    else {
      // Not logged in yet.
      setExpressCheckoutSuccessURL(getLoginDuringCheckoutURL());
      pRequest.addQueryParameter("express", "true");
    }
  }

  /**
   * Sets form values from request parameters if they are present on the request
   * and haven't already been set in the form handler. This allows add operations
   * to be triggered by a link instead of a form submission.
   *
   * @param pRequest the request object.
   * @param pResponse the response object.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  @Override
  public void preAddItemToOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  
    vlogDebug("Passed giftListID is {0}.", getGiftlistId());
    vlogDebug("Passed giftlistItemId is {0}.", getGiftlistItemId());
    vlogDebug("Passed productId is {0}.", getProductId());
    vlogDebug("Passed quantity is {0}.", getQuantity());

    validateOrderQuantity(pRequest, pResponse);
    if (getFormError()) {
      return;
    }

    validateSelectedSKUs(pRequest, pResponse);
    if (getFormError()) {
      return;
    }
    
    this.checkItemToAddStockLevels(pRequest, pResponse);
  }

  /**
   * Override to set the current transaction for rollback if there are form errors.
   * 
   * @param pRequest - HTTP request.
   * @param pResponse - HTTP response.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   */
  protected void addItemToOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    super.addItemToOrder(pRequest, pResponse);

    if (getFormError()) {
      // Mark the transaction for roll back.
      getCartHelper().rollbackTransaction(getTransactionManager());
    }
  }

  //------------------------------------------
  // method:  handleAddItemToGiftlist
  //------------------------------------------
  /**
   * <p>
   *   Adds items to a gift list based on the AddCommerceItemInfo array. Each item in the
   *   array is added to the selected gift list.
   * </p>
   * <p>
   *   To add items to gift list delegates call to <code>addItemToGiftlist</code> handle
   *   method of <code>GiftlistFormHandler</code>. 
   * </p>
   * 
   * @param pRequest the servlet's request.
   * @param pResponse the servlet's response.
   * 
   * @return true if successful, false otherwise.
   * 
   * @exception ServletException if there was an error while executing the code.
   * @exception IOException if there was an error with servlet io.
   * @exception CommerceException if there was an error with Commerce.
   */
  public boolean handleAddItemToGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException {
    
    // If any form errors found, redirect to error URL:
    if (!checkFormRedirect(null, getAddItemToGiftlistErrorURL(), pRequest, pResponse)) {
      return false;
    }
    
    validateGiftlistCatalogRefIds(pRequest, pResponse);
    
    // If any form errors found, redirect to error URL.
    if (!checkFormRedirect(null, getAddItemToGiftlistErrorURL(), pRequest, pResponse)) {
      return false;
    }
  
    // Prepare input data for GiftlistFormHandler before calling its handle method. 
    prepareAddToGiftlistData(pRequest, pResponse);
    
    try {
      // Call addItemToGiftlist handle method on GiftlistFormhandler.
      return getStoreGiftlistFormHandler().handleAddItemToGiftlist(pRequest, pResponse);
    } 
    catch (CommerceException oce) {
      processException(oce, getCartHelper().MSG_ERROR_ADDING_TO_GIFTLIST, pRequest, pResponse);
    }
    
    return false;
  }
  
  /**
   * Prepares input data that <code>GiftlistFormHandler</code> need to
   * add item to gift list: quantity to add, SKU Id, gift list Id and
   * success URL.
   * 
   * @param pRequest the servlet's request.
   * @param pResponse the servlet's response.
   */
  public void prepareAddToGiftlistData(DynamoHttpServletRequest pRequest, 
                                       DynamoHttpServletResponse pResponse) {
    
    StoreGiftlistFormHandler giftListFormHandler = getStoreGiftlistFormHandler();
    
    // Specify quantity to add to gift list.
    long quantity = 1;
    
    if (getItems()!= null){
      quantity = getItems()[0].getQuantity();
    }
    
    giftListFormHandler.setQuantity(quantity);
  
    // Set SKU Id. We need explicitly set it here for non JavaScript enabled picker.
    if (getCatalogRefIds()!= null && getCatalogRefIds().length > 0){
      giftListFormHandler.setCatalogRefIds(new String[]{getCatalogRefIds()[0]});
    }
    else{
      if (getItems()!=null && getItems().length > 0){
        giftListFormHandler.setCatalogRefIds(new String[]{getItems()[0].getCatalogRefId()});
      }  
    }    
  
    // set gift list ID and success URL
    String giftListId = getAddItemToGiftlist();    
    giftListFormHandler.setGiftlistId(giftListId);
    
    // take add to gift list success URL from the request parameter
    giftListFormHandler.setAddItemToGiftlistSuccessURL(pRequest.getParameter(giftListId));
  }
 
  /**
   * Called after all processing is done by the removeItemFromOrder method.
   *
   * @param pRequest the request object.
   * @param pResponse the response object.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  @Override
  public void postRemoveItemFromOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    if (!getFormError()) {
      try {
        addRemoveGiftServices();
      }
      catch (CommerceException ce) {
        processException(ce, StoreCartProcessHelper.MSG_ERROR_MODIFYING_ORDER, pRequest, pResponse);
      }
    }
  }
  
  /**
   * Override of base class behavior to use a locally defined variable
   * for storing the shipping group.
   * 
   * @param pShippingGroup - shipping group
   */
  @Override
  public void setShippingGroup(ShippingGroup pShippingGroup) {
    mStoreShippingGroup = pShippingGroup;
  }

  /**
   * Override of base class behavior to ensure a non-gift hardgood shipping group
   * is returned if a shipping group hasn't already been set.
   * 
   * @return shipping group.
   */
  @Override
  public ShippingGroup getShippingGroup() {
    if (mStoreShippingGroup != null) {
      return mStoreShippingGroup;
    }

    mStoreShippingGroup = 
      ((StoreOrderTools) getCartHelper().getStoreOrderTools()).getShippingGroup(getOrder());

    return mStoreShippingGroup;
  }

  /**
   * Gets parameter from HTTP request.
   * 
   * @param pRequest HTTP request.
   * @param pName name of the parameter.
   * 
   * @return parameter from HTTP request.
   */
  private String getRequestParameter(DynamoHttpServletRequest pRequest, String pName) {
    String reqParam = pRequest.getParameter(pName);
    return reqParam;
  }
  
  /**
   * Add item to order.
   *
   * @param pRequest - HTTP request.
   * @param pResponse - HTTP response.
   *
   * @return true if successful, otherwise false.
   *
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   */
  @Override
  public boolean handleAddItemToOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    String successUrl = getAddItemToOrderSuccessURL();
    String errorUrl = getAddItemToOrderErrorURL();

    //  If this is an AJAX RichCart request, then handle it as such, sending a JSON response.
    if (AjaxUtils.isAjaxRequest(pRequest)) {
      if (isLoggingDebug()) {
        logDebug("Handling AJAX AddToCart request");
      }

      // This request has been sent from a JavaScript component in the browser that is expecting
      // a JSON response, so we have to make sure that's what we send it.
      // Use the AJAX success/error URLs from this point onwards.       
      successUrl = getAjaxAddItemToOrderSuccessUrl();
      errorUrl = getAjaxAddItemToOrderErrorUrl();
      
      String ajaxAddToCartSuccessReqParam = getRequestParameter(pRequest, "ajaxAddItemToOrderSuccessUrl");
      String ajaxAddToCartErrorReqParam = getRequestParameter(pRequest, "ajaxAddItemToOrderErrorUrl");

      if (StringUtils.isBlank(successUrl) && !StringUtils.isBlank(ajaxAddToCartSuccessReqParam)) {
        successUrl = ajaxAddToCartSuccessReqParam;
        setAjaxAddItemToOrderSuccessUrl(ajaxAddToCartSuccessReqParam);
      }
      if (StringUtils.isBlank(errorUrl) && !StringUtils.isBlank(ajaxAddToCartErrorReqParam)) {
        errorUrl = ajaxAddToCartErrorReqParam;
        setAjaxAddItemToOrderErrorUrl(ajaxAddToCartErrorReqParam);
      }
      
      // Notify that this request has been sent via JavaScript.
      getRequestBean().getValues().put("isAjaxRequest", Boolean.valueOf(true));
    }

    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleAddItemToOrder";

    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod))) {
      Transaction tr = null;

      try {
        tr = ensureTransaction();

        if (getUserLocale() == null) {
          setUserLocale(getUserLocale(pRequest, pResponse));
        }

        // If any form errors found, redirect to error URL:
        if (!checkFormRedirect(null, errorUrl, pRequest, pResponse)) {
          return false;
        }
        
        // If the SKU isn't available redirect to the email page. This is used
        // only by the non JavaScript picker.
        if(getSkuUnavailableURL() != null){
          if(!isSkuAvailable(getSkuUnavailableURL(), pRequest, pResponse)){
            return false;
          }
        }

        synchronized (getOrder()) {
          preAddItemToOrder(pRequest, pResponse);

          // If any form errors found, redirect to error URL.
          if (!checkFormRedirect(null, errorUrl, pRequest, pResponse)) {
            return false;
          }

          addItemToOrder(pRequest, pResponse);

          // If any form errors found, redirect to error URL.
          if (!checkFormRedirect(null, errorUrl, pRequest, pResponse)) {
            return false;
          }

          postAddItemToOrder(pRequest, pResponse);

          updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        } // synchronized

        // If NO form errors are found, redirect to the success URL.
        // If form errors are found, redirect to the error URL.
        return checkFormRedirect(successUrl, errorUrl, pRequest, pResponse);
      } 
      finally {
        if (tr != null) {
          commitTransaction(tr);
        }

        if (rrm != null) {
          rrm.removeRequestEntry(myHandleMethod);
        }
      }
    }

    return false;
  }
  
  /**
   * Custom Form Exception handler that traps number format exceptions and
   * raises a new exception with a more appropriate message.
   * 
   * @param pException the form exception being raised.
   * @param pRequest a Dynamo HTTP request
   * @param pResponse a Dynamo HTTP response
   * 
   * @see atg.droplet.GenericFormHandler#handleFormException(atg.droplet.DropletFormException,
   *                                                         atg.servlet.DynamoHttpServletRequest,
   *                                                         atg.servlet.DynamoHttpServletResponse)
   */
  @Override
  public void handleFormException(DropletFormException pException,
      DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse){

    if (isLoggingDebug()) {
      logDebug("StoreCartFormHandler form Exception ***"
          + pException.getMessage());
    }

    DropletFormException exceptionToReport = pException;

    String errorCode = pException.getErrorCode();

    if (NUMBER_FORMAT_ERR_CODE.equalsIgnoreCase(errorCode)) {
      exceptionToReport = createNewDropletFormException(pException, pRequest,
          pResponse, MSG_INVALID_QUANTITY);

    } else if (ILLEGAL_ARGUMENT_ERR_CODE.equalsIgnoreCase(errorCode)) {

      exceptionToReport = createNewDropletFormException(pException, pRequest,
          pResponse, MSG_AMBIGUOUS_INPUT_FOR_ADD);

    }
    super.handleFormException(exceptionToReport, pRequest, pResponse);
  }
  
  /**
   * Used by the non-JavaScript picker to determine if a product is available. 
   * If we are unable to determine if its in stock assume it is.
   * 
   * @param pUnavailableURL The URL to redirect to in the event a product is not available.
   * @param pRequest - current HTTP servlet request.
   * @param pResponse - current HTTP servlet response.
   * 
   * @return A boolean indicating whether or not a product is available.
   */
  protected boolean isSkuAvailable(String pUnavailableURL,
                                   DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse) {   
    // Get skuId that was set in noJsPickerLayout.jsp.
    Object skuIdParam = getCatalogRefIds()[0];
    
    if(skuIdParam == null) {
      if(isLoggingDebug()) {
        logDebug("Cannot detemine if sku is available as sku is null");
      }
      return true;
    }
    
    String skuType = getSkuType();
       
    // Get productId.
    String productId = getProductId();
    
    if(productId == null) {
      if(isLoggingDebug()) {
        logDebug("Cannot determine if sku " + skuIdParam + " is available as productId is null");
      }
      return true;
    }
    
    try{
      StoreInventoryManager invManager = getInventoryManager();

      // Get product repository item.
      Object productParam = invManager.getCatalogRefRepository().getItem(productId, PRODUCT);
      
      if((productParam == null) || !(productParam instanceof RepositoryItem)) {
        if(isLoggingDebug()) {
          logDebug("Cannot get the product from the repository. Product is " + productParam);
        }
        return true;
      }
            
      // If its in stock back orderable or pre-orderable its not unavailable.
      int availability = 
        invManager.queryAvailabilityStatus((RepositoryItem) productParam, (String) skuIdParam);
      
      if(availability == invManager.getAvailabilityStatusInStockValue() || 
         availability == invManager.getAvailabilityStatusBackorderableValue() ||
         availability == invManager.getAvailabilityStatusPreorderableValue()) {
        return true;
      }
    }
    catch(Exception e) {
      if(isLoggingError()) {
        logError("Error determining if sku was in stock.", e);
      }
    }
    
    try{
      // Update the unavailable url with a flag.
      //pUnavailableURL = pUnavailableURL + SKU_UNAVAILABLE_PARAM + SKU_PARAM + skuIdParam;
      
      // Redirect
      redirectOrForward(pRequest, pResponse, pUnavailableURL);
    }
    catch(Exception e) {
      if(isLoggingError()) {
        logError("Error redirecting to url " + pUnavailableURL, e);
      }
    }
    return false;
  }

  /**
   * This method checks if all items to be added into the shopping cart contain a reference 
   * to a SKU repository item. If no items specified, this method walks through catalogRefIds 
   * references, and if no reference found, creates a 'no SKU' form exception. If there are some 
   * items specified, this method walks through them and checks all references to SKU repository 
   * item. If one or more items has no reference to SKU, this method creates a 'no SKU' form exception.
   * 
   * @param pRequest - current HTTP request.
   * @param pResponse - current HTTP response.
   * 
   * @throws ServletException - if something goes wrong.
   * @throws IOException - if unable to modify HTTP request or response.
   */
  protected void validateSelectedSKUs(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {
    
    // No items? then there should be specified catalogRefIds to be added to cart.
    if (getItems() == null || getItems().length == 0) {
      if ((getCatalogRefIds() == null) || 
          (getCatalogRefIds().length == 0) || 
          (getCatalogRefIds()[0].isEmpty())) {
        
        // We have an error here, no SKUs passed into handler.
        addNoSkuFormException(pRequest, pResponse);
      }         
    }
    // There are some items to be added.
    else {
      for (AddCommerceItemInfo itemInfo: getItems()) {
        if ((itemInfo.getCatalogRefId() == null) || (itemInfo.getCatalogRefId().length() == 0)) {
          addNoSkuFormException(pRequest, pResponse);
        }
      }
    }
  }
  
  /**
   * This method creates a 'no SKU' form exception based on the skuType passed into form handler.
   * <br/>
   * <ul>
   *   <li>If skuType is 'clothing', the 'no sku' exception says 'select color and size'.</li>
   *   <li>If skuType is 'furniture', the 'no sku' exception says 'select wood finish'.</li>
   *   <li>If nothing specified in the skuType property, the 'no sku' exception says 'select sku'.</li>
   * </ul>
   * 
   * @param pRequest - current HTTP request.
   * @param pResponse - current HTTP response.
   * 
   * @throws ServletException - if something goes wrong
   * @throws IOException - if unable to modify HTTP request or response.
   */
  private void addNoSkuFormException(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    if (CLOTHING_SKU_TYPE.equals(getSkuType())) {
      
      // Determine the correct error message.
      String clothingErrorMessage;
      
      if (mColorRequired && mSizeRequired) {
        // Both color and size are required.
        clothingErrorMessage = MSG_SELECT_COLOR_SIZE;
      }
      else if (mColorRequired && !mSizeRequired) {
        // Only color is required.
        clothingErrorMessage = MSG_SELECT_COLOR;
      }
      else if (!mColorRequired && mSizeRequired) {
        // Only size is required.
        clothingErrorMessage = MSG_SELECT_SIZE;
      }
      else {
        // Neither color or size are required.
        clothingErrorMessage = MSG_SELECT_SKU;
      }
      
      addFormException(new DropletException(formatUserMessage(clothingErrorMessage, pRequest, pResponse)));
      
    } 
    else if (FURNITURE_SKU_TYPE.equals(getSkuType())) {
      addFormException(new DropletException(formatUserMessage(MSG_SELECT_WOOD_FINISH, pRequest, pResponse)));
    } 
    else {
      addFormException(new DropletException(formatUserMessage(MSG_SELECT_SKU, pRequest, pResponse)));
    }
  }

  /**
   * Validate quantity.
   *
   * @param pRequest - HTTP request.
   * @param pResponse - HTTP response.
   *
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   */
  protected void validateOrderQuantity(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    long quantity;
    AddCommerceItemInfo[] items = getItems();

    if (items != null) {
      
      long itemsSelected = 0;
      
      for (int i = 0; i < items.length; i++) {
        AddCommerceItemInfo item = (AddCommerceItemInfo) items[i];

        try {
          quantity = item.getQuantity();

          if (isQuantityValid(quantity, pRequest, pResponse)) {
            itemsSelected += quantity;
          }
        } catch (NumberFormatException nfe) {
          addFormException(new DropletFormException(
            formatUserMessage(MSG_INVALID_QUANTITY, pRequest, pResponse), (String) null, MSG_INVALID_QUANTITY));
        }
      }
      if (itemsSelected == 0) {
        addFormException(new DropletException(formatUserMessage(MSG_NO_ITEMS_TO_ADD, pRequest, pResponse)));
      }
    }
    else {
      quantity = getQuantity();
      
      if (isQuantityValid(quantity, pRequest, pResponse)) {
        if (quantity <= 0) {
          addFormException(new DropletFormException(
            formatUserMessage(MSG_INVALID_QUANTITY, pRequest, pResponse), (String) null, MSG_INVALID_QUANTITY));          
        }
      }
    }

    if (getFormError() && isLoggingDebug()) {
      logDebug("validateQuantity(): Quantity is less than -1");
    }
  }

  /**
   * Validate quantity.
   *
   * @param pRequest - HTTP request.
   * @param pResponse - HTTP response.
   *
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   */
  protected void validateQuantity(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    long quantity;
    List items = getOrder().getCommerceItems();

    if (items != null) {
      for (int i = 0; i < items.size(); i++) {
        CommerceItem item = (CommerceItem) items.get(i);
        String commerceItemId = item.getId();
        String productDisplayName = (String)
          ((RepositoryItem)item.getAuxiliaryData().getProductRef()).getPropertyValue(DISPLAY_NAME_PROPERTY_NAME);
        
        try {
          quantity = getQuantityByCommerceId(commerceItemId, pRequest, pResponse);
          
          if (mMinQuantity > -1 && quantity < mMinQuantity) {
            addFormException(new DropletFormException(formatUserMessage(
              MSG_ITEM_LESS_THAN_MIN_QUANTITY, productDisplayName, mMinQuantity, pRequest, pResponse),
              (String) null, MSG_LESS_THAN_MIN_QUANTITY));
          }
          else if (mMaxQuantity > -1 && quantity > mMaxQuantity) {
            addFormException(new DropletFormException(formatUserMessage(
              MSG_ITEM_MORE_THAN_MAX_QUANTITY, productDisplayName, mMaxQuantity, pRequest, pResponse),
              (String) null, MSG_MORE_THAN_MAX_QUANTITY));
          }
          else if (quantity < 0) {
            addFormException(new DropletFormException(formatUserMessage(
              MSG_INVALID_QUANTITY, pRequest, pResponse),
              (String) null, MSG_INVALID_QUANTITY));
          }
          
          String productId = item.getAuxiliaryData().getProductId();  
          String skuId = item.getCatalogRefId();
          
          // Check that the quantity being set isn't greater than the quantity we have.
          this.checkSkuStockLevel(pRequest, pResponse, productId, skuId, quantity);
          
        } catch (NumberFormatException nfe) {
          addFormException(new DropletFormException(formatUserMessage(
            MSG_INVALID_QUANTITY, pRequest, pResponse),
            (String) null, MSG_INVALID_QUANTITY));
        }
      }
    }

    if (getFormError() && isLoggingDebug()) {
      logDebug("validateQuantity(): Quantity is less than -1");
    }
  }
  
  /**
   * Validates whether SKU Ids are specified for adding to gift list.
   * 
   * @param pRequest - HTTP request.
   * @param pResponse - HTTP response.
   * 
   * @throws IOException
   * @throws ServletException 
   */
  public void validateGiftlistCatalogRefIds(DynamoHttpServletRequest pRequest, 
                                            DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException{
    
    // Check whether SKU IDs are specified.    
    if ((getCatalogRefIds() == null || 
         getCatalogRefIds().length == 0 || 
         StringUtils.isEmpty(getCatalogRefIds()[0])) && 
        (getItems() == null || getItems().length == 0 || 
         StringUtils.isEmpty(getItems()[0].getCatalogRefId()))) {
      
      // SKU IDs are not specified. Add form exception.
      
      // Determine whether we are dealing with wish list or ordinary gift list.
      String profileId = getProfile().getRepositoryId();
      String giftListId = getAddItemToGiftlist();
      StoreGiftlistManager giftlistManager = (StoreGiftlistManager)getGiftlistManager();
      
      if (giftlistManager.isProfileWishlist(profileId, giftListId)){
        if (FURNITURE_SKU_TYPE.equals(getSkuType())){
          addFormException(new DropletException(formatUserMessage(
            MSG_ERROR_NO_FINISH_SELECTED_WISHLIST, pRequest, pResponse)));
        }
        else{
          addFormException(new DropletException(formatUserMessage(
            MSG_ERROR_NO_COLOR_SIZE_SELECTED_WISHLIST, pRequest, pResponse)));
        }
      }
      else{
        if (FURNITURE_SKU_TYPE.equals(getSkuType())){
          addFormException(new DropletException(formatUserMessage(
            MSG_ERROR_NO_FINISH_SELECTED_GIFTLIST, pRequest, pResponse)));
        }
        else{
          addFormException(new DropletException(formatUserMessage(
            MSG_ERROR_NO_COLOR_SIZE_SELECTED_GIFTLIST, pRequest, pResponse)));
        }
      }
    }
  }
  
  /**
   * Validates quantity and adds form exceptions if quantity is invalid.
   * 
   * @param pQuantity quantity to validate.
   * @param pRequest - HTTP request.
   * @param pResponse - HTTP response.
   * 
   * @return true is valid, otherwise false.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   */
  protected boolean isQuantityValid(long pQuantity, 
                                    DynamoHttpServletRequest pRequest, 
                                    DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException{
    
    if (mMinQuantity > -1 && pQuantity < mMinQuantity) {
      addFormException(new DropletFormException(formatUserMessage(
        MSG_LESS_THAN_MIN_QUANTITY, mMinQuantity, pRequest, pResponse),
        (String) null, MSG_LESS_THAN_MIN_QUANTITY));
      
      return false;
    }
    else if (mMaxQuantity > -1 && pQuantity > mMaxQuantity) {
      addFormException(new DropletFormException(formatUserMessage(
        MSG_MORE_THAN_MAX_QUANTITY, mMaxQuantity, pRequest, pResponse),
        (String) null, MSG_MORE_THAN_MAX_QUANTITY));
      
      return false;
    }
    else if (pQuantity < 0) {
      addFormException(new DropletFormException(formatUserMessage(
        MSG_INVALID_QUANTITY, pRequest, pResponse),
        (String) null, MSG_INVALID_QUANTITY));
      
      return false;
    }
    
    return true;
  }
  
  /**
   * Determines an overall quantity of commerce items in the order.
   * 
   * @param pRequest a Dynamo HTTP request.
   * @param pResponse a Dynamo HTTP response.
   * 
   * @return sum of commerce items' quantities.
   * 
   * @throws ServletException if an error occurred during claiming the coupon.
   * @throws IOException if an error occurred during claiming the coupon.
   */
  private long evalOverallItemsQuantity(DynamoHttpServletRequest pRequest, 
                                        DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    long overallQuantity = 0;
    List<CommerceItem> items = getOrder().getCommerceItems();

    if(items != null) {
      for(CommerceItem item : items) {
        overallQuantity += getQuantityByCommerceId(item.getId(), pRequest, pResponse);
      }
    }
    
    return overallQuantity;
  }
  
  /**
   * This method create a new droplet form exception, retrieving the message
   * from user messages. If it cannot retrieve the message then it will raise
   * the original exception.
   * 
   * @param pOriginalException - the original exception raised, used if we cannot get the resource
   *                             message for the new exception.
   * @param pRequest a Dynamo HTTP request.
   * @param pResponse a Dynamo HTTP response.
   * @param pMessageKey - the user message key.
   * 
   * @return a new DropletFormException with the appropriate message, or the orignalException passed in.
   */
  private DropletFormException createNewDropletFormException(DropletFormException pOriginalException,
                                                             DynamoHttpServletRequest pRequest, 
                                                             DynamoHttpServletResponse pResponse,
                                                             String pMessageKey) {
    DropletFormException exceptionToReport;
    
    try {
      exceptionToReport = 
        new DropletFormException(
          formatUserMessage(pMessageKey, pRequest, pResponse), (String) null, pMessageKey);
    } 
    catch (IOException e) {

      exceptionToReport = pOriginalException;
      
      if (isLoggingError())
        logError("Unable to get " + pMessageKey, e);

    } 
    catch (ServletException e) {
      exceptionToReport = pOriginalException;
      
      if (isLoggingError())
        logError("Unable to get " + pMessageKey, e);
    }

    return exceptionToReport;
  }
    
  /**
   * Checks the stock and back order levels for items to be added to 
   * ensure the quantity being requests does exceed the back order level. 
   * 
   * @param pRequest a Dynamo HTTP request.
   * @param pResponse a Dynamo HTTP response.
   */
  private void checkItemToAddStockLevels(DynamoHttpServletRequest pRequest,
                                         DynamoHttpServletResponse pResponse) {

    AddCommerceItemInfo[] items = getItems();

    String productId;
    String skuId;
    long quantityRequired = 0;
    long quantityInCart=0;
    
    if (items != null) {

      for (int i = 0; i < getItems().length; i++) {
        AddCommerceItemInfo input = getItems()[i];

        productId = input.getProductId();
        skuId = input.getCatalogRefId();
        quantityRequired = input.getQuantity();

        // Check to see if we had any already in the in the cart.
        quantityInCart = getQuantityInOrder(skuId);
        checkSkuStockLevel(pRequest, pResponse, productId, skuId, (quantityRequired + quantityInCart));
      }

    } else {

      // JavaScript is off so get the SKU from the catalog refs id.
      if (getCatalogRefIds() != null) {

        Object skuIdParam = getCatalogRefIds()[0];

        if (skuIdParam == null) {
          if (isLoggingDebug()) {
            logDebug("Cannot detemine if sku is available as sku is null");
          }
          return;
        }

        // Get productId.
        skuId = (String) skuIdParam;
        productId = getProductId();
        
        if (productId == null) {
          if (isLoggingDebug()) {
            logDebug("Cannot determine if sku " + skuIdParam
              + " is available as productId is null");
          }
          return;
        }

        quantityRequired = getQuantity();
        // check to see if we had any already in the in the cart
        quantityInCart = getQuantityInOrder(skuId);

        checkSkuStockLevel(pRequest, pResponse, productId, skuId, (quantityRequired + quantityInCart));
      }
    }

    return;
  }
  
  /**
   * Method to get the number of items matching a SKU Id in the order.
   * 
   * @param pSkuId the id of the SKU to process.
   * 
   * @return the number of items already in the cart.
   */
  private long getQuantityInOrder(String pSkuId) {
    
    long quantityInBasket = 0; 
    
    List items = getOrder().getCommerceItems();

    if (items != null) {
      for (int i = 0; i < items.size(); i++) {
        
        CommerceItem item = (CommerceItem) items.get(i);
        String itemSkuId = item.getCatalogRefId();
        
        if (pSkuId.equalsIgnoreCase(itemSkuId)){
          quantityInBasket = item.getQuantity();
          break;
        }
      }
    }
    return quantityInBasket;
  }

  /**
   * This method checks the back order level for a SKU. If it is a back order then 
   * ensure the quantity required is less than the back order level displays an error
   * message if it isn't.
   * 
   * @param pRequest a Dynamo HTTP request.
   * @param pResponse a Dynamo HTTP response.
   * @param productId - the product Id.
   * @param skuId - the item's SKU Id.
   * @param quantityRequired - the quantity required.
   * @param productDisplayName - the display name of the product.
   */
  private void checkSkuStockLevel(DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse, 
                                  String productId, 
                                  String skuId,
      long quantityRequired) {
    
    // Get productId

    if (productId == null) {
      if (isLoggingDebug()) {
        logDebug("Cannot determine if sku " + skuId + " is available as productId is null");
      }
      return;
    }
    
    if ((skuId == null) || ( StringUtils.isEmpty(skuId.trim()))){
      if (isLoggingDebug()) {
        logDebug("The skuId is null or an empty string");
      }
      return;
    }

    try {
      StoreInventoryManager invManager = getInventoryManager();

      // get product repository item
      Object productParam = invManager.getCatalogRefRepository().getItem(productId, PRODUCT);
      
      if ((productParam == null) || !(productParam instanceof RepositoryItem)) {
        
        if (isLoggingError()) {
          logError("Cannot get the product from the repository. Product is "
              + productParam);
        }
        return;
      }
 
      int availability = invManager.queryAvailabilityStatus((RepositoryItem) productParam, skuId);

      long stockLevel = invManager.queryStockLevel(skuId);
      long backOrderLevel = invManager.queryBackorderLevel(skuId);
      

      if (isLoggingDebug()) {
        logDebug("Quantity =" + quantityRequired + ";Stock Level =" + 
          stockLevel + "; backOrderLevel=" + backOrderLevel);
      }

      // If it is a back order then ensure the quantity required is less than the back order level;
      // display an error message if it isn't
      if (availability == invManager.getAvailabilityStatusBackorderableValue()) {

        if ((backOrderLevel != -1) && (quantityRequired > backOrderLevel)) {

          String productDisplayName = (String) 
            ((RepositoryItem) productParam).getPropertyValue(DISPLAY_NAME_PROPERTY_NAME);
          
          addFormException(new DropletFormException(formatUserMessage(
              MSG_ITEM_MORE_THAN_MAX_QUANTITY, productDisplayName,  backOrderLevel, pRequest, pResponse), 
              (String) null, MSG_ITEM_MORE_THAN_MAX_QUANTITY));
        }
      }    
    }
    catch (InventoryException ex){
      // Log inventory exception using InventoryManager component
      // InventoryManager logs missing inventory exception only in the case
      // when it's configured to report them.
      getInventoryManager().logInventoryException(ex);
    }
    catch (Exception e) {
      if (isLoggingError()) {
        logError("Error determining if sku was in stock.", e);
      }
    }

    return;
  }


  /**
   * property: removeItemGwpId
   */
  private String mRemoveItemGwpId = "";

  /**
   * @param pRemoveItemGwpId The item ID for the GWP portion of the item to remove.
   */
  public void setRemoveItemGwpId( String pRemoveItemGwpId )
  {
    mRemoveItemGwpId = pRemoveItemGwpId;
  }

  /**
   * @return The item ID for the GWP portion of the item to remove.
   */
  public String getRemoveItemGwpId()
  {
    return ( mRemoveItemGwpId );
  }


  /**
   * @param pRemoveItemGwp The item ID for the GWP portion of the item to remove.
   */
  public void setRemoveItemGwp( String pRemoveItemGwp )
  {
    setRemoveItemGwpId( pRemoveItemGwp );
  }

  /**
   * @return The item ID for the GWP portion of the item to remove.
   */
  public String getRemoveItemGwp()
  {
    return ( getRemoveItemGwpId() );
  }

  /**
   * <p>
   *   Handle method to remove the GWP part of a commerce item that is appearing in the cart both
   *   as a regular item and as GWP items appearing on individual rows.
   * </p>
   * <br />
   * <p>
   *   As the commerce item is split across multiple rows a remove action on any particular row is 
   *   actually a quantity update on the commerce item. This is done by setting the formhandler 
   *   GWP item ID property for the item to remove and calling the regular cart update which will 
   *   take account of the quantity updates.
   * </p>
   *  
   * @param pRequest the servlet's request.
   * @param pResponse the servlet's response.
   * @return boolean Success or failure.
   * @exception ServletException if there was an error while executing the code.
   * @exception IOException if there was an error with servlet io.
   */
  public boolean handleRemoveItemGwp( DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse )
    throws ServletException, IOException
  {
    return handleUpdate( pRequest, pResponse );
  }

  /**
   *  property: itemNonGwpQuantities
   */
  private Map<String, String> mItemNonGwpQuantities = new HashMap<String, String>();
  
  /**
   * @param pItemNonGwpQuantities A map containing commerce ID/quantity entries.
   */
  public void setItemNonGwpQuantities( Map<String, String> pItemNonGwpQuantities )
  {
    mItemNonGwpQuantities = pItemNonGwpQuantities;
  }

  /**
   * @return A map containing commerce ID/quantity entries.
   */
  public Map<String, String> getItemNonGwpQuantities()
  {
    return ( mItemNonGwpQuantities );
  }

  /**
   * property: removeItemNonGwpId
   */
  private String mRemoveItemNonGwpId = "";

  /**
   * @param pRemoveItemNonGwpId The item ID for the non-GWP portion of the item to remove.
   */
  public void setRemoveItemNonGwpId( String pRemoveItemNonGwpId )
  {
    mRemoveItemNonGwpId = pRemoveItemNonGwpId;
  }

  /**
   * @return The item ID for the non-GWP portion of the item to remove.
   */
  public String getRemoveItemNonGwpId()
  {
    return ( mRemoveItemNonGwpId );
  }


  /**
   * @param pRemoveItemNonGwp The item ID for the non-GWP portion of the item to remove.
   */
  public void setRemoveItemNonGwp( String pRemoveItemNonGwp )
  {
    setRemoveItemNonGwpId( pRemoveItemNonGwp );
  }

  /**
   * @return The item ID for the non-GWP portion of the item to remove.
   */
  public String getRemoveItemNonGwp()
  {
    return ( getRemoveItemNonGwpId() );
  }

  /**
   * <p>
   *   Handle method to remove the non-GWP part of a commerce item that is appearing in the cart both
   *   as a regular item and as GWP items appearing on individual rows.
   * </p>
   * <br />
   * <p>
   *   As the commerce item is split across multiple rows a remove action on any particular row is
   *   actually a quantity update on the commerce item. This is done by setting the formhandler
   *   non-GWP item ID property for the item to remove and calling the regular cart update which will
   *   take account of the quantity updates.
   * </p>
   *
   * @param pRequest the servlet's request.
   * @param pResponse the servlet's response.
   * 
   * @return boolean Success or failure.
   * 
   * @exception ServletException if there was an error while executing the code.
   * @exception IOException if there was an error with servlet io.
   */
  public boolean handleRemoveItemNonGwp( DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse )
    throws ServletException, IOException
  {
    return handleUpdate( pRequest, pResponse );
  }

  /**
   * property: gwpPlaceholderPromotionIds
   */
  private String[] mGwpPlaceholderPromotionIds = new String[0];
 
  /**
   * @param pGwpPlaceholderPromotionIds - the GWP place holder promotion id.
   */
  public void setGwpPlaceholderPromotionIds( String[] pGwpPlaceholderPromotionIds )
  {
    mGwpPlaceholderPromotionIds = pGwpPlaceholderPromotionIds;
  }

  public String[] getGwpPlaceholderPromotionIds()
  {
    return ( mGwpPlaceholderPromotionIds );
  }

  /**
   * property: gwpPlaceholderHashCodes
   */
  private int[] mGwpPlaceholderHashCodes = new int[0];

  /**
   * @param pGwpPlaceholderHashCodes - the GWP place holder gift hash code.
   */
  public void setGwpPlaceholderHashCodes( int[] pGwpPlaceholderHashCodes )
  {
    mGwpPlaceholderHashCodes = pGwpPlaceholderHashCodes;
  }

  public int[] getGwpPlaceholderHashCodes()
  {
    return ( mGwpPlaceholderHashCodes );
  }

  /**
   * property: gwpPlaceholderIndex
   */
  private int mGwpPlaceholderIndex = -1;
  
  /**
   * @param pGwpPlaceholderIndex - The array index for the GWP place holder promotion id/hashcode.
   */
  public void setRemoveGwpPlaceholderFromOrder( int pGwpPlaceholderIndex )
  {
    mGwpPlaceholderIndex = pGwpPlaceholderIndex;
  }

  public int getRemoveGwpPlaceholderFromOrder()
  {
    return ( mGwpPlaceholderIndex );
  }

  /**
   * Called before any work is done by the handleRemoveGwpPlaceholderFromOrder method.
   *
   * @param pRequest the request object.
   * @param pResponse the response object.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public void preRemoveGwpPlaceholderFromOrder(DynamoHttpServletRequest pRequest, 
                                               DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  
  }

  /**
   * Called after all work is done by the handleRemoveGwpPlaceholderFromOrder method.
   *
   * @param pRequest the request object.
   * @param pResponse the response object.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public void postRemoveGwpPlaceholderFromOrder(DynamoHttpServletRequest pRequest, 
                                                DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
  }

  /**
   * Method to remove the gift with purchase place-holder item from the order.
   *
   * @param pRequest the servlet's request.
   * @param pResponse the servlet's response.
   * 
   * @exception ServletException if there was an error while executing the code.
   * @exception IOException if there was an error with servlet io.
   */
  protected void removeGwpPlaceholderFromOrder(DynamoHttpServletRequest pRequest, 
                                               DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    try {
      // Remove the GWP item, gifts are displayed as single items on the cart so we can
      // assume we are removing one at a time.
      String promotionId = mGwpPlaceholderPromotionIds[getRemoveGwpPlaceholderFromOrder()];
      int hashCode = mGwpPlaceholderHashCodes[getRemoveGwpPlaceholderFromOrder()];

      getGwpManager().updateSelectableQuantity(getOrder(), 
                                               promotionId, 
                                               hashCode,
                                               1);
    }
    catch ( Exception e ) {
      processException( e, MSG_ERROR_REMOVE_SELECTABLE_QUANTITY, pRequest, pResponse );
    }
  }

  /**
   * Handle method to remove gift with purchase place holder cart item from the order.
   *
   * @param pRequest the servlet's request.
   * @param pResponse the servlet's response.
   * 
   * @return boolean Success or failure.
   * 
   * @exception ServletException if there was an error while executing the code.
   * @exception IOException if there was an error with servlet io.
   */
  public boolean handleRemoveGwpPlaceholderFromOrder(DynamoHttpServletRequest pRequest, 
                                                     DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "StoreCartFormHandler.handleRemoveGwpItemFromOrder";

    if (( rrm == null ) || ( rrm.isUniqueRequestEntry( myHandleMethod ))) {
      
      Transaction tr = null;
      
      try {
        tr = ensureTransaction();
        
        if ( getUserLocale() == null ) {
          setUserLocale(getUserLocale(pRequest, pResponse));
        }

        // If any form errors found, redirect to error URL:
        if (!checkFormRedirect(null, getRemoveItemFromOrderErrorURL(), pRequest, pResponse)) {
          return false;
        }
        
        synchronized (getOrder())
        {
          preRemoveGwpPlaceholderFromOrder(pRequest, pResponse);

          // If any form errors found, redirect to error URL.
          if (!checkFormRedirect( null, getRemoveItemFromOrderErrorURL(), pRequest, pResponse)) {
            return false;
          }

          removeGwpPlaceholderFromOrder(pRequest, pResponse);

          // If any form errors found, redirect to error URL.
          if (!checkFormRedirect(null, getRemoveItemFromOrderErrorURL(), pRequest, pResponse)) {
            return false;
          }
          
          postRemoveGwpPlaceholderFromOrder( pRequest, pResponse );

          updateOrder(getOrder(), MSG_ERROR_REMOVE_SELECTABLE_QUANTITY, pRequest, pResponse);
        } // synchronized

        // If NO form errors are found, redirect to the success URL.
        // If form errors are found, redirect to the error URL.
        return checkFormRedirect( getRemoveItemFromOrderSuccessURL(), 
                                  getRemoveItemFromOrderErrorURL(), 
                                  pRequest, pResponse );
      }
      finally {
        if ( tr != null ) {
          commitTransaction( tr );
        }
        if ( rrm != null ) {
          rrm.removeRequestEntry( myHandleMethod );
        }
      }
    }
    else {
      return false;
    }
  }
}
