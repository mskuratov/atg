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


package atg.projects.store.gifts;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.gifts.GiftlistFormHandler;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.gifts.GiftlistSiteFilter;
import atg.commerce.gifts.GiftlistTools;
import atg.commerce.gifts.InvalidDateException;
import atg.commerce.gifts.InvalidGiftQuantityException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.projects.store.profile.SessionBean;
import atg.projects.store.profile.StoreProfileTools;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.collections.filter.FilterException;
import atg.service.util.CurrentDate;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.PropertyManager;

/**
 * Extensions to the atg.commerce.gifts.GiftlistFormHandler.
 *
 * @see atg.commerce.gifts.GiftlistFormHandler
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/gifts/StoreGiftlistFormHandler.java#3 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreGiftlistFormHandler extends GiftlistFormHandler { 
  
  protected static final String GIFT_LIST_EVENT_NAME_MAP_KEY = "eventName";
  protected static final String GIFT_LIST_MONTH_MAP_KEY = "month";
  protected static final String GIFT_LIST_DATE_MAP_KEY = "date";
  protected static final String GIFT_LIST_YEAR_MAP_KEY = "year";
  protected static final String GIFT_LIST_SHIPPING_ADDRESS_MAP_KEY = "shippingAddressId";
  protected static final String GIFT_LIST_EVENT_TYPE_MAP_KEY = "eventType";
  protected static final String GIFT_LIST_PUBLISHED_MAP_KEY = "isPublished";
  protected static final String GIFT_LIST_DESCRIPTION_MAP_KEY = "description";
  protected static final String GIFT_LIST_INSTRUCTIONS_MAP_KEY = "instructions";
  protected static final String GIFT_LISTS_PROPERTY = "giftlists";
  
  
  private static final String LOGIN_ERROR_PARAMETER_NAME = "error";
  
  //-------------------------------------
  // Constants
  //-------------------------------------
  
  // Resource message keys
  public static final String MSG_INVALID_DATE = "InvalidDate";
  public static final String MSG_ERROR_LONG_DESCRIPTION = "errorLongDescription";
  public static final String MSG_ERROR_LONG_SPECIAL_INSTRUCTIONS = "errorLongSpecialInstructions";
  public static final String MSG_DUPLICATE_EVENT_NAME = "errorDuplicateEventName";
  public static final String GIFT_LIST_NOT_LOGGED_IN = "giftlistNotLoggedIn";
  public static final String WISH_LIST_NOT_LOGGED_IN = "wishlistNotLoggedIn";

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/gifts/StoreGiftlistFormHandler.java#3 $$Change: 788278 $";

  /**
   * Gift list id constant.
   */
  public static final String GIFTLIST_ID = "giftlistId";

  /**
   * Success URL constant.
   */
  public static final String SUCCESS_URL = "successURL";

  /**
   * Sku id constant.
   */
  public static final String SKU_ID = "skuId";

  /**
   * Quantity constant.
   */
  public static final String QUANTITY = "quantity";

  /**
   * Product id constant.
   */
  public static final String PRODUCT_ID = "productId";
  
  //-------------------------------------
  // Properties
  //-------------------------------------
  
  /**
   * property: giftlistSiteFilter
   */
  protected GiftlistSiteFilter mGiftlistSiteFilter;
  
  /**
   * @return the giftlistSiteFilter
   */
  public GiftlistSiteFilter getGiftlistSiteFilter() {
    return mGiftlistSiteFilter;
  }

  /**
   * @param pGiftlistSiteFilter the giftlistSiteFilter to set
   */
  public void setGiftlistSiteFilter(GiftlistSiteFilter pGiftlistSiteFilter) {
    mGiftlistSiteFilter = pGiftlistSiteFilter;
  }

  /**
   * property: addItemToGiftlistLoginURL
   */
  protected String mAddItemToGiftlistLoginURL;
  
  /**
   * @param pAddItemToGiftlistLoginURL - The property to store the URL for where the user should 
   *                                     be redirected if they attempt to add an item to the gift 
   *                                     list without being logged in.
   *                                     
   * @beaninfo description: The property to store the URL for where the user should be redirected
   *                        if they attempt to add an item to the gift list without being logged in.
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
   * property: moveItemsFromCartLoginURL
   */
  protected String mMoveItemsFromCartLoginURL;
  
  /**
   * @param pMoveItemsFromCartLoginURL - the property to store the URL for where the user should 
   *                                     be redirected if they attempt to move items from cart to 
   *                                     the gift list without being logged in.
   * 
   * @beaninfo description:  The property to store the URL for where the user should be redirected if 
   *                         they attempt to move items from cart to the gift list without being logged in.
   */
  public void setMoveItemsFromCartLoginURL(String pMoveItemsFromCartLoginURL) {
    mMoveItemsFromCartLoginURL = pMoveItemsFromCartLoginURL;
  }

  /**
   * @return the move items from cart to gift list login URL.
   */
  public String getMoveItemsFromCartLoginURL() {
    return mMoveItemsFromCartLoginURL;
  }

  /**
   * property: profilePropertyManager
   */
  protected PropertyManager mProfilePropertyManager;
  
  /**
   * @param pProfilePropertyManager - the property manager for profiles, used to see if the user is logged in.
   * 
   * @beaninfo description:  The PropertyManager for profiles, used to see if the user is logged in.
   */
  public void setProfilePropertyManager(PropertyManager pProfilePropertyManager) {
    mProfilePropertyManager = pProfilePropertyManager;
  }

  /**
   * @return profile property manager.
   */
  public PropertyManager getProfilePropertyManager() {
    return mProfilePropertyManager;
  }

  /**
   * property: removeItemsFromGiftlistSuccessURL
   */
  protected String mRemoveItemsFromGiftlistSuccessURL;
  
  /**
   * @param pRemoveItemsFromGiftlistSuccessURL - remove items from gift list success URL.
   * 
   * @beaninfo description:  The property to store the success URL for removeItemsFromGiftlist.
   */
  public void setRemoveItemsFromGiftlistSuccessURL(String pRemoveItemsFromGiftlistSuccessURL) {
    mRemoveItemsFromGiftlistSuccessURL = pRemoveItemsFromGiftlistSuccessURL;
  }

  /**
   * @return remove items from gift list success URL.
   */
  public String getRemoveItemsFromGiftlistSuccessURL() {
    return mRemoveItemsFromGiftlistSuccessURL;
  }

  /**
   * property: removeItemsFromGiftlistErrorURL
   */
  protected String mRemoveItemsFromGiftlistErrorURL;
  
  /**
   * @param pRemoveItemsFromGiftlistErrorURL - remove items from gift list error URL.
   * 
   * @beaninfo description:  The property to store the error URL for removeItemsFromGiftlist.
   */
  public void setRemoveItemsFromGiftlistErrorURL(String pRemoveItemsFromGiftlistErrorURL) {
    mRemoveItemsFromGiftlistErrorURL = pRemoveItemsFromGiftlistErrorURL;
  }

  /**
   * @return remove items from gift list error URL.
   */
  public String getRemoveItemsFromGiftlistErrorURL() {
    return mRemoveItemsFromGiftlistErrorURL;
  }
  
  // property: updateGiftlistAndItemsSuccessURL
  String mUpdateGiftlistAndItemsSuccessURL;

  /**
   * @param pUpdateGiftlistAndItemsSuccessURL - The property to store the Success URL for UpdateGiftlistAndItems.
   * 
   * @beaninfo description:  The property to store the success URL for UpdateGiftlistAndItems.
   */
  public void setUpdateGiftlistAndItemsSuccessURL(String pUpdateGiftlistAndItemsSuccessURL) {
    mUpdateGiftlistAndItemsSuccessURL = pUpdateGiftlistAndItemsSuccessURL;
  }

  /**
   * Returns property UpdateGiftlistAndItemsSuccessURL.
   * 
   * @return The value of the property UpdateGiftlistAndItemsSuccessURL.
   */
  public String getUpdateGiftlistAndItemsSuccessURL() {
    return mUpdateGiftlistAndItemsSuccessURL;
  }

  /** 
   * property: updateGiftlistAndItemsErrorURL
   */
  String mUpdateGiftlistAndItemsErrorURL;

  /**
   * @param pUpdateGiftlistAndItemsErrorURL - The property to store the error URL for UpdateGiftlistAndItems.
   * 
   * @beaninfo description:  The property to store the error URL for UpdateGiftlistAndItems.
   */
  public void setUpdateGiftlistAndItemsErrorURL(String pUpdateGiftlistAndItemsErrorURL) {
    mUpdateGiftlistAndItemsErrorURL = pUpdateGiftlistAndItemsErrorURL;
  }

  /**
   * @return the value of the property UpdateGiftlistAndItemsErrorURL.
   */
  public String getUpdateGiftlistAndItemsErrorURL() {
    return mUpdateGiftlistAndItemsErrorURL;
  }
  
  /** 
   * property: moveToNewGiftListAddressSuccessURL
   */
  String mMoveToNewGiftListAddressSuccessURL;

  /**
   * @param pMoveToNewGiftListAddressSuccessURL - The property to store the Success URL for 
   *                                              MoveToNewGiftListAddress.
   *                                              
   * @beaninfo description:  The property to store the success URL for MoveToNewGiftListAddress.
   */
  public void setMoveToNewGiftListAddressSuccessURL(String pMoveToNewGiftListAddressSuccessURL) {
    mMoveToNewGiftListAddressSuccessURL = pMoveToNewGiftListAddressSuccessURL;
  }

  /**
   * Returns property MoveToNewGiftListAddressSuccessURL.
   * 
   * @return The value of the property MoveToNewGiftListAddressSuccessURL.
   */
  public String getMoveToNewGiftListAddressSuccessURL() {
    return mMoveToNewGiftListAddressSuccessURL;
  }

  /** 
   * property: moveToNewGiftListAddressErrorURL
   */
  String mMoveToNewGiftListAddressErrorURL;

  /**
   * @param pMoveToNewGiftListAddressErrorURL - The property to store the error URL for MoveToNewGiftListAddress.
   * 
   * @beaninfo description: The property to store the error URL for MoveToNewGiftListAddress.
   */
  public void setMoveToNewGiftListAddressErrorURL(String pMoveToNewGiftListAddressErrorURL) {
    mMoveToNewGiftListAddressErrorURL = pMoveToNewGiftListAddressErrorURL;
  }

  /**
   * @return the value of the property MoveToNewGiftListAddressErrorURL.
   */
  public String getMoveToNewGiftListAddressErrorURL() {
    return mMoveToNewGiftListAddressErrorURL;
  }
  
  /**
   * property: sessionBean
   */
  private SessionBean mSessionBean;
  
  /**
   * @return the mSessionBean.
   */
  public SessionBean getSessionBean()
  {
    return mSessionBean;
  }

  /**
   * @param pSessionBean the sessionBean to set.
   */
  public void setSessionBean(SessionBean pSessionBean)
  {
    mSessionBean = pSessionBean;
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
  
  /**
   * unmodifiedEventName property. This property contains current 
   * event name (in case gift list is updated). 
   */
  private String mUnmodifiedEventName = null;
  
  /**
   * @return the unmodifiedEventName
   */
  public String getUnmodifiedEventName() {
    return mUnmodifiedEventName;
  }

  /**
   * @param pUnmodifiedEventName the mUnmodifiedEventName to set
   */
  public void setUnmodifiedEventName(String pUnmodifiedEventName) {
    mUnmodifiedEventName = pUnmodifiedEventName;
  }
   
  //-------------------------------------
  // Methods
  //-------------------------------------
  
  

  /**
   * Saves 'Add Item to Giftlist' related data into session-scoped component.
   * 
   * @param pRedirectURL - the URL to redirect to after login, saved into session-scoped component.
   * 
   * @throws ServletException if anything goes wrong.
   * @throws InvalidParameterException an exception that is thrown when an invalid argument
   *                                   is passed into a method call.
   * @throws CommerceItemNotFoundException is thrown when a CommerceItem cannot be found in a get or remove call.
   */
  @SuppressWarnings("unchecked") //OK to suppress, we know which values (strings) we put on these keys.
  private void saveDataIntoSession(String pRedirectURL) 
    throws ServletException, CommerceItemNotFoundException, InvalidParameterException {
    
    if (getItemIds()!= null && getItemIds().length > 0){
      
      // The commerce item is defined so take SKU ID and Product ID from it.
      String commerceItemId = getItemIds()[0];
      CommerceItem item = getOrder().getCommerceItem(commerceItemId);
      
      if (item != null){
        getSessionBean().getValues().put(SessionBean.COMMERCE_ITEM_ID_PROPERTY_NAME, 
                                         commerceItemId);
        getSessionBean().getValues().put(SessionBean.SKU_ID_TO_GIFTLIST_PROPERTY_NAME, 
                                         item.getCatalogRefId());
        getSessionBean().getValues().put(SessionBean.PRODUCT_ID_TO_GIFTLIST_PROPERTY_NAME,  
                                         item.getAuxiliaryData().getProductId());
        getSessionBean().getValues().put(SessionBean.SITE_ID_PROPERTY_NAME, 
                                         item.getAuxiliaryData().getSiteId());
      }
    }
    else{
      // No commerce item case, SKU Id and product Id are directly passed in. 
      getSessionBean().getValues().put(SessionBean.SKU_ID_TO_GIFTLIST_PROPERTY_NAME, getCatalogRefIds()[0]);
      getSessionBean().getValues().put(SessionBean.PRODUCT_ID_TO_GIFTLIST_PROPERTY_NAME, getProductId());
    }
    
    getSessionBean().getValues().put(SessionBean.GIFTLIST_ID_PROPERTY_NAME, 
                                     getProfile().isTransient() ? null : getGiftlistId());
    getSessionBean().getValues().put(SessionBean.QUANTITY_TO_ADD_TO_GIFTLIST_PROPERTY_NAME, 
                                     Long.valueOf(getQuantity()));
    getSessionBean().getValues().put(SessionBean.REDIRECT_AFTER_LOGIN_URL_PROPERTY_NAME, 
                                     pRedirectURL);
  }

  /**
   * Before adding an item to the giftlist validate quantity and SKU ID, also check 
   * if user is explicitly logged in. If not store product/SKU/gift list info into the session.
   *
   * @param pRequest - the HTTP request.
   * @param pResponse - the HTTP response.
   *
   * @throws ServletException if there was an error while executing the code.
   * @throws IOException if there was an error with servlet io.
   */
  @Override
  public void preAddItemToGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    // Add form exception if quantity less or equal zero.
    if (getQuantity() <= 0) 
    {
      String msg = formatUserMessage(MSG_QUANTITY_LESSTHAN_OR_EQUALTO_ZERO, pRequest, pResponse);
      addFormException(new DropletFormException(msg, MSG_ERROR_ADDING_TO_GIFTLIST));
    }
    
    // Validate SKU Id.
    if (getCatalogRefIds() == null || getCatalogRefIds().length == 0 || 
        getCatalogRefIds()[0] == null) {
      
      addFormException(new DropletException(formatUserMessage(MSG_NO_ITEMS_TO_ADD, pRequest, pResponse)));
    }
    
    StoreProfileTools profileTools = (StoreProfileTools)getProfileTools();
    
    if (!profileTools.isUserLoggedIn(getProfile())){
      
      // No good - the user needs to be explicitly logged in. Throw a dummy exception (the user won't
      // see it anyway), and redirect to the loginURL.
      String notLoggedIn = formatUserMessage(GIFT_LIST_NOT_LOGGED_IN, pRequest, pResponse);
      addFormException(new DropletException(notLoggedIn, notLoggedIn));
      setAddItemToGiftlistErrorURL(getAddItemToGiftlistLoginURL());
      
      // Specify login error parameter to display appropriate error message on the login page.
      addLoginErrorParameter(pRequest, pResponse);

      // If not logged in, save SKU and product to be added into SessionBean properties for future use
      // (after the user has logged in). When the user is logged in, this SKU will be added to user's wish list.
      try {
        saveDataIntoSession(getAddItemToGiftlistSuccessURL());
      } 
      catch (CommerceItemNotFoundException ex) {
        processException(ex, MSG_ERROR_ADDING_TO_GIFTLIST, pRequest, pResponse);
      } 
      catch (InvalidParameterException ex) {
        processException(ex, MSG_ERROR_ADDING_TO_GIFTLIST, pRequest, pResponse);
      }
      
      return;
    }
    
    super.preAddItemToGiftlist(pRequest, pResponse);
  }
  
  /**
   * Adds 'error' query parameter to the request with the resource key to the error message
   * that should be displayed on the login page.
   * 
   * @param pRequest - the HTTP request.
   * @param pResponse - the HTTP response.
   */
  protected void addLoginErrorParameter(DynamoHttpServletRequest pRequest, 
                                        DynamoHttpServletResponse pResponse) {
    if (getGiftlistId()!= null) {
      
      // Specify resource key for error message to display on login page
      // depending on whether we are dealing with gift list or wish list.
      StoreGiftlistManager giftListManager = (StoreGiftlistManager) getGiftlistManager();
      
      String errorMessageKey = GIFT_LIST_NOT_LOGGED_IN;
      
      if (giftListManager.isProfileWishlist(getProfile().getRepositoryId(), getGiftlistId())) {
        errorMessageKey = WISH_LIST_NOT_LOGGED_IN;
      }
      
      pRequest.addQueryParameter(LOGIN_ERROR_PARAMETER_NAME, errorMessageKey);
    }
  }
  
  /**
   * Before moving item to the gift list check if user is explicitly logged in.
   * If not store product/SKU/gift list info into the session.
   * 
   * @param pRequest - HTTP request.
   * @param pResponse - HTTP response.
   * 
   * @throws ServletException if there was an error while executing the code.
   * @throws IOException if there was an error with servlet io.
   */
  @Override
  public void preMoveItemsFromCart(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    StoreProfileTools profileTools = (StoreProfileTools)getProfileTools();
    
    if (!profileTools.isUserLoggedIn(getProfile())) {
      
      // No good - the user needs to be explicitly logged in.  Throw a dummy exception (the user won't
      // see it anyway), and redirect to the loginURL.
      String notLoggedIn = formatUserMessage(GIFT_LIST_NOT_LOGGED_IN, pRequest, pResponse);
      addFormException(new DropletException(notLoggedIn, notLoggedIn));
      setMoveItemsFromCartErrorURL(getMoveItemsFromCartLoginURL());
      
      // Specify login error parameter to display appropriate error message on the login page
      addLoginErrorParameter(pRequest, pResponse);

      // If not logged in, save SKU and product to be added into SessionBean properties for future use
      // (after the user has logged in). When the user is logged in, this SKU will be added to user's wish list.
      try {
        saveDataIntoSession(getMoveItemsFromCartSuccessURL());
      } 
      catch (CommerceItemNotFoundException ex) {
        processException(ex, MSG_ERROR_ADDING_TO_GIFTLIST, pRequest, pResponse);
      } 
      catch (InvalidParameterException ex) {
        processException(ex, MSG_ERROR_ADDING_TO_GIFTLIST, pRequest, pResponse);
      }
      
      return;
    }
    super.preMoveItemsFromCart(pRequest, pResponse);
  }

  /**
   * Initializes all the form values from the provided gift list.
   * 
   * @param pGiftlist - gift list.
   */
  public void setGiftlist(RepositoryItem pGiftlist) {
    
    GiftlistTools tools = getGiftlistManager().getGiftlistTools();
    
    //populate the form handler with values form the gift list
    setGiftlistId(pGiftlist.getRepositoryId());
    /*
     * If UnmodifiedEventName is set, that mean we are in edit mode and
     * validation failed. Don't update gift list name not to lost 
     * last user input of gift list name.
     * 
     */
    if (getUnmodifiedEventName() == null) {
      setEventName((String) pGiftlist.getPropertyValue(tools.getEventNameProperty()));
    }
    setUnmodifiedEventName((String) pGiftlist.getPropertyValue(tools.getEventNameProperty()));
    setEventType((String) pGiftlist.getPropertyValue(tools.getEventTypeProperty()));
    setDescription((String) pGiftlist.getPropertyValue(tools.getDescriptionProperty()));

    Date eventDate = (Date) pGiftlist.getPropertyValue(tools.getEventDateProperty());
    setIsPublished((Boolean) pGiftlist.getPropertyValue(tools.getPublishedProperty()));
    setEventDate(eventDate);

    RepositoryItem shippingaddress = 
      (RepositoryItem) pGiftlist.getPropertyValue(tools.getShippingAddressProperty());

    if (shippingaddress != null) {
      setShippingAddressId(shippingaddress.getRepositoryId());
    }

    setInstructions((String) pGiftlist.getPropertyValue(tools.getInstructionsProperty()));
  }

  /**
   * Clears the form handler property values.
   */
  public void clearForm(){
    // Clear form
    setGiftlistId(null);
    setEventName(null);
    setEventType(null);
    setDescription(null);
    setIsPublished(Boolean.TRUE);
    
    // Get the current system time.
    CurrentDate cd = getCurrentDate();
    Date currentDate = cd.getTimeAsDate();
    
    setEventDate(currentDate);
    setShippingAddressId(null);
    setInstructions(null);    
  }

  /**
   * Clears form errors as part of the cancel operation.
   * 
   * @param pRequest - the HTTP request.
   * @param pResponse - HTTP response.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   * 
   * @return true if successful, otherwise false.
   */
  @Override
  public boolean handleCancel(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    resetFormExceptions();
    clearForm();

    return super.handleCancel(pRequest, pResponse);
  }
  
  /**
   * Stores entered by user data to the sessions-scoped component before moving to
   * Add New Address URL. The stored data can be retrieved later during form initialization.
   * 
   * @param pRequest - the HTTP request.
   * @param pResponse - the HTTP response.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   * 
   * @return true if successful, otherwise false.
   */
  public boolean handleMoveToNewGiftListAddress(DynamoHttpServletRequest pRequest, 
                                                DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    // Ignore not entered required fields.
    resetFormExceptions();
    storeNewGiftListData();
    
    return checkFormRedirect(getMoveToNewGiftListAddressSuccessURL(), 
                             getMoveToNewGiftListAddressErrorURL(), 
                             pRequest, pResponse);
  }
  
  /**
   * Stores entered by user data to the session-scoped component.
   */
  public void storeNewGiftListData() {
    Map sessionBeanValues = getSessionBean().getValues();
    
    sessionBeanValues.put(GIFT_LIST_EVENT_NAME_MAP_KEY, getEventName());
    sessionBeanValues.put(GIFT_LIST_MONTH_MAP_KEY, getMonth());
    sessionBeanValues.put(GIFT_LIST_DATE_MAP_KEY, getDate());
    sessionBeanValues.put(GIFT_LIST_YEAR_MAP_KEY, getYear());
    sessionBeanValues.put(GIFT_LIST_SHIPPING_ADDRESS_MAP_KEY, getShippingAddressId());
    sessionBeanValues.put(GIFT_LIST_EVENT_TYPE_MAP_KEY, getEventType());
    sessionBeanValues.put(GIFT_LIST_PUBLISHED_MAP_KEY, getIsPublished());
    sessionBeanValues.put(GIFT_LIST_DESCRIPTION_MAP_KEY, getDescription());
    sessionBeanValues.put(GIFT_LIST_INSTRUCTIONS_MAP_KEY, getInstructions());
  }
  
  /**
   * Initializes Gift list form with previously entered data stored in the session-scoped component.
   * 
   * @param pRequest the HTTP request.
   * @param pResponse the HTTP response.
   * 
   * @return true
   */
  public boolean handleInitializeGiftListForm(DynamoHttpServletRequest pRequest, 
                                              DynamoHttpServletResponse pResponse) {
    // Initialize gift list form with values previously entered by user in this session.
    Map sessionBeanValues = getSessionBean().getValues();
    setEventName((String)sessionBeanValues.get(GIFT_LIST_EVENT_NAME_MAP_KEY));
    setMonth((Integer)sessionBeanValues.get(GIFT_LIST_MONTH_MAP_KEY));
    setDate((String)sessionBeanValues.get(GIFT_LIST_DATE_MAP_KEY));
    setYear((String)sessionBeanValues.get(GIFT_LIST_YEAR_MAP_KEY));
    setShippingAddressId((String)sessionBeanValues.get(GIFT_LIST_SHIPPING_ADDRESS_MAP_KEY));
    setEventType((String)sessionBeanValues.get(GIFT_LIST_EVENT_TYPE_MAP_KEY));
    setIsPublished((Boolean)sessionBeanValues.get(GIFT_LIST_PUBLISHED_MAP_KEY));
    setDescription((String)sessionBeanValues.get(GIFT_LIST_DESCRIPTION_MAP_KEY));
    setInstructions((String)sessionBeanValues.get(GIFT_LIST_INSTRUCTIONS_MAP_KEY));
    return true;
  }

  /**
   * Overrides base method to clear the form after the new gift list has been successfully saved.
   * 
   * @param pRequest the HTTP request.
   * @param pResponse the HTTP response.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   */
  @Override
  public void postSaveGiftlist(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    super.postSaveGiftlist(pRequest, pResponse);
    if (!getFormError()) {
      // If no error occurred during saving a new gift list clear the form.
      clearForm();      
    }  
  }
  

  /**
   * Calls validatation of event name
   */
  @Override
  public void preSaveGiftlist(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    super.preSaveGiftlist(pRequest, pResponse);
    validateEventName(getEventName(), pRequest, pResponse);
  }


  /**
   * This method validates event name. Name considered invalid if
   * there is already a gift list available on this site. 
   * 
   * If UnmodifiedEventName is set, then we are in edit gift list mode. 
   * No validation should take place if the gift list name wasn't changed.
   * 
   * @param pResponse 
   * @param pRequest 
   * @throws IOException 
   * @throws ServletException 
   */
  protected void validateEventName(String pEventName, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    /*
     *  If UnmodifiedEventName is set, then we are in edit gift list mode
     *  and no validation should take place if the gift list name wasn't changed 
     */  
    if (!pEventName.equals(getUnmodifiedEventName())) {
      // Retrieve users gift lists
      List giftLists =  (List) getProfile().getPropertyValue(GIFT_LISTS_PROPERTY);
      boolean isValidName = true;
    
      try {
        List filteredGiftLists = (List) getGiftlistSiteFilter().filterCollection(giftLists, null, getProfile());
      
        for (Object giftList: filteredGiftLists) {
          String giftListName = (String) ((RepositoryItem)giftList).getPropertyValue(GIFT_LIST_EVENT_NAME_MAP_KEY);
          if (giftListName.equals(pEventName)) {
            isValidName = false;
            break;
          }
        }
      
      
      } catch (FilterException ex) {
        if (isLoggingDebug()) {
          logDebug("Exeption occurs while filetering gift list by sites.", ex);
        }
      }
    
      if (!isValidName) {
        addFormException(MSG_DUPLICATE_EVENT_NAME, pRequest, pResponse);
      }
    
    }
  }

  /**
   * The combined handler that allows to update gift list and its items at the same time.
   * 
   * @param pRequest the servlet's request.
   * @param pResponse the servlet's response.
   * 
   * @return true if successful, otherwise false.
   * 
   * @exception ServletException if there was an error while executing the code.
   * @exception IOException if there was an error with servlet io.
   * @exception CommerceException if there was an error with Commerce.
   */
  public boolean handleUpdateGiftlistAndItems(DynamoHttpServletRequest pRequest, 
                                              DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException {
    
    String pProfileId = (String) getProfile().getRepositoryId();
    
    try {
      // If any form errors found, redirect to error URL:
      if (! checkFormRedirect(null, getUpdateGiftlistAndItemsErrorURL(), pRequest, pResponse)){
        return false;
      }
      // Pre-process update
      preUpdateGiftlistAndItems(pRequest, pResponse);
      
      // If any form errors found, redirect to error URL:
      if (! checkFormRedirect(null, getUpdateGiftlistAndItemsErrorURL(), pRequest, pResponse)){
        return false;
      }
      
      validateGiftListDescription(pRequest, pResponse);
      
      // If any form errors found, redirect to error URL:
      if (! checkFormRedirect(null, getUpdateGiftlistAndItemsErrorURL(), pRequest, pResponse)){
        return false;
      }
      
      validateGiftListInstructions(pRequest, pResponse);
      
      // If any form errors found, redirect to error URL:
      if (! checkFormRedirect(null, getUpdateGiftlistAndItemsErrorURL(), pRequest, pResponse)){
        return false;
      }

      // If new address, create and add to address book.
      if ( getIsNewAddress() ){
        setShippingAddressId(createNewShippingAddress());
      }

      // Call manager class to update list.
      getGiftlistManager().updateGiftlist(pProfileId, 
                                          getGiftlistId(), 
                                          getIsPublished().booleanValue(), 
                                          getEventName(), 
                                          getEventDate(), 
                                          getEventType(), 
                                          getDescription(), 
                                          getComments(), 
                                          getShippingAddressId(), 
                                          getInstructions(),
                                          getSiteId());
      
      // If any form errors found, redirect to error URL:
      if (! checkFormRedirect(null, getUpdateGiftlistAndItemsErrorURL(), pRequest, pResponse)){
        return false;
      }
      
      if (validateGiftlistId(pRequest, pResponse)) {
        
        updateGiftlistItems(pRequest, pResponse);

        // If any form errors found, redirect to error URL:
        if (! checkFormRedirect(null, getUpdateGiftlistAndItemsErrorURL(), pRequest, pResponse)){
          return false;
        }        
      }
 
      postUpdateGiftlistAndItems(pRequest, pResponse);       
    }
    catch (InvalidDateException ide) {
      if(isLoggingError()){
        logError(ide);
      }
      processException(ide, MSG_INVALID_EVENT_DATE, pRequest, pResponse);
    }
    catch (CommerceException ce){
      if(isLoggingError()){
        logError(ce);
      }
      
      processException(ce, MSG_ERROR_SAVING_GIFTLIST, pRequest, pResponse);
    }
    
    // If NO form errors are found, redirect to the success URL.
    // If form errors are found, redirect to the error URL.
    return checkFormRedirect (getUpdateGiftlistAndItemsSuccessURL(), 
                              getUpdateGiftlistAndItemsErrorURL(), 
                              pRequest, pResponse);
  }
  
  /**
   * Check if description length is valid. If description exceeds the valid amount
   * of symbols form exception will be added.
   *
   * @param pRequest - the HTTP request.
   * @param pResponse - the HTTP response.
   * 
   * @throws ServletException if there was an error while executing the code.
   * @throws IOException if there was an error with servlet io.
   */
  private void validateGiftListDescription(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    String description = getDescription();
    
    if (description.length() > 254) {
      addFormException(MSG_ERROR_LONG_DESCRIPTION, pRequest, pResponse);
    }  
  }
  
  /**
   * Check if special instructions length is valid. If special instructions exceeds 
   * the valid amount of symbols form exception will be added.
   * 
   * @param pRequest - the HTTP request.
   * @param pResponse - the HTTP response.
   * @throws ServletException if there was an error while executing the code.
   * @throws IOException if there was an error with servlet io.
   */
  private void validateGiftListInstructions(DynamoHttpServletRequest pRequest,
                                            DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    String instructions = getInstructions();
    
    if (instructions.length() > 254) {
      addFormException(MSG_ERROR_LONG_SPECIAL_INSTRUCTIONS, pRequest, pResponse);
    }  
  }
  
  /**
   * Operation called just before a gift list and gift items are updated.
   * 
   * @param pRequest - the HTTP request.
   * @param pResponse - the HTTP response.
   * 
   * @throws ServletException if there was an error while executing the code.
   * @throws IOException if there was an error with servlet io.
   */
  public void preUpdateGiftlistAndItems(DynamoHttpServletRequest pRequest,
                                        DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    validateEventName(getEventName(), pRequest, pResponse);
  } 
  
  /**
   * Operation called just after a gift list and gift items are updated.
   * 
   * @param pRequest - the HTTP request.
   * @param pResponse - the HTTP response.
   * 
   * @exception ServletException if something went wrong.
   * @exception IOException if something went wrong.
   */
  public void postUpdateGiftlistAndItems(DynamoHttpServletRequest pRequest,
                                         DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    if (!getFormError()) {
      // If no errors occurred during updating a gift list clear the form.
      clearForm();      
    }  
  }

 
  /**
   * Operation called just before items are removed from a a gift list.
   * 
   * @param pRequest the HTTP request.
   * @param pResponse the HTTP response.
   * 
   * @exception ServletException if there was an error while executing the code.
   * @exception IOException if there was an error with servlet io.
   */
  public void preRemoveItemsFromGiftlist(DynamoHttpServletRequest pRequest, 
                                         DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  
  } 
  
  /**
   * Operation called just after items are removed from a a gift list.
   * 
   * @param pRequest the HTTP request.
   * @param pResponse the HTTP response.
   * 
   * @exception ServletException if there was an error while executing the code.
   * @exception IOException if there was an error with servlet io.
   */
  public void postRemoveItemsFromGiftlist(DynamoHttpServletRequest pRequest, 
                                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
  } 
  
  /**
   * HandleRemoveItemsFromGiftlist is called when the user hits the "delete" button on the wish list 
   * page. This handler removes the specified gift Ids from the specified gift list.
   *
   * @param pRequest the HTTP request.
   * @param pResponse the HTTP response.
   *
   * @return true if successful, false otherwise.
   * 
   * @exception ServletException if there was an error while executing the code.
   * @exception IOException if there was an error with servlet io.
   * @exception CommerceException if there was an error with Commerce.
   */
  public boolean handleRemoveItemsFromGiftlist(DynamoHttpServletRequest pRequest, 
                                               DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException {
    
    try {
      // If any form errors found, redirect to error URL:
      if (!checkFormRedirect(null, getRemoveItemsFromGiftlistErrorURL(), pRequest, pResponse)) {
        return false;
      }

      preRemoveItemsFromGiftlist(pRequest, pResponse);
      
      if (validateGiftlistId(pRequest, pResponse)) {
        removeItemsFromGiftlist(pRequest, pResponse);

      }

      postRemoveItemsFromGiftlist(pRequest, pResponse);
    }
    catch (CommerceException oce) {
      processException(oce, MSG_ERROR_ADDING_TO_GIFTLIST, pRequest, pResponse);
    }

    return checkFormRedirect(getRemoveItemsFromGiftlistSuccessURL(), 
                             getRemoveItemsFromGiftlistErrorURL(), 
                             pRequest, pResponse);
  }

   /**
   * Removes the given items to the selected gift list.
   *
   * @param pRequest the HTTP request.
   * @param pResponse the HTTP response.
   * 
   * @exception ServletException if there was an error while executing the code.
   * @exception IOException if there was an error with servlet io.
   * @exception CommerceException if there was an error with Commerce.
   */
  protected void removeItemsFromGiftlist(DynamoHttpServletRequest pRequest, 
                                         DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException {
    
    GiftlistManager mgr = getGiftlistManager();
    String pGiftlistId = getGiftlistId();
    String[] items = getRemoveGiftitemIds();

    if (items == null) {
      return;
    }

    try {
      for (int i = 0; i < items.length; i++) {
        String id = items[i];
        mgr.removeItemFromGiftlist(pGiftlistId, id);
      }
    } 
    catch (RepositoryException ex) {
      processException(ex, MSG_ERROR_UPDATING_GIFTLIST_ITEMS, pRequest, pResponse);
    }
  }

  /**
   * Will update the quantity of the commerceItem passed in. If quantity moved to gift list from 
   * cart equals or is greater than that in cart, it will remove the item from the cart. 
   * Otherwise, it will decrease the number by quantity passed in.
   * 
   * @param pItem the commerce item to update.
   * @param pQuantity the number moved to gift list.
   * @param pRequest the HTTP request.
   * @param pResponse the HTTP response.
   * 
   * @throws InvalidGiftQuantityException - if pQuantity <= 0.
   * 
   * @exception IOException if there was an error with servlet io.
   * @exception ServletException if there was an error with Servlet.
   */
  @Override
  protected void updateOrder(CommerceItem pItem, long pQuantity, 
                             DynamoHttpServletRequest pRequest, 
                             DynamoHttpServletResponse pResponse)
    throws InvalidGiftQuantityException, IOException, ServletException {
    
    super.updateOrder(pItem, pItem.getQuantity(), pRequest, pResponse);
  }
  
  /** 
   * Operation called just after an item has been moved from shopping cart.
   * 
   * @param pRequest the HTTP request.
   * @param pResponse the HTTP response.
   * 
   * @exception ServletException if there was an error while executing the code.
   * @exception IOException if there was an error with servlet io.
   */
  @Override
  public void postMoveItemsFromCart(DynamoHttpServletRequest pRequest, 
                                    DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
  }
 
}
