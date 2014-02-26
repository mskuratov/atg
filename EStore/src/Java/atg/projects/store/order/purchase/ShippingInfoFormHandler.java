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

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupManager;
import atg.commerce.order.purchase.CommerceItemShippingInfo;
import atg.commerce.order.purchase.PurchaseUserMessage;
import atg.commerce.order.purchase.ShippingGroupFormHandler;
import atg.commerce.order.purchase.ShippingGroupMapContainer;
import atg.commerce.pricing.PricingException;
import atg.commerce.profile.CommerceProfileTools;
import atg.core.i18n.PlaceList.Place;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.order.StoreShippingGroupManager;
import atg.projects.store.profile.StorePropertyManager;
import atg.projects.store.util.CountryRestrictionsService;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


/**
 * Form Handler for handling shipping related checkout processes.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/ShippingInfoFormHandler.java#4 $$Change: 789119 $ 
 * @updated $DateTime: 2013/02/08 05:57:44 $$Author: dstewart $
 */
public class ShippingInfoFormHandler extends ShippingGroupFormHandler {
  
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/ShippingInfoFormHandler.java#4 $$Change: 789119 $";


  // -------------------------------------
  // Constants
  // -------------------------------------
  
  protected static final String GROUND_SHIPPING_METHOD = "Ground";
  
  private static final String MSG_NO_SHIPPING_ADDRESS_SELECTED = "noShippingAddressSelected";

  public static final String COUNTRY_KEY_PREFIX = "CountryCode.";

  public static final String COUNTRY_STATE_RESOURCES = "atg.commerce.util.CountryStateResources";
  
  /**
   * Error Message keys
   */
  protected static final String MSG_ERROR_UPDATE_ORDER = "errorUpdatingOrder";
  protected static final String MSG_ERROR_MOVE_TO_BILLING = "errorWithShippingInfo";  
  
  // -------------------------------------
  // Properties
  // -------------------------------------

  /**
   * property: coupon code 
   */
  private String mCouponCode;

  /**
   * @return a coupon code to be claimed
   */
  public String getCouponCode() {
    return mCouponCode;
  }

  /**
   * @param pCouponCode the coupon code to set
   */
  public void setCouponCode(String pCouponCode) {
    mCouponCode = pCouponCode;
  }

  /**
   * property: checkout progress states component
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
   * property: Reference to the ShippingProcessHelper component
   */
  private StoreShippingProcessHelper mShippingHelper;

  /**
   * @return the Shipping Helper component.
   */
  public StoreShippingProcessHelper getShippingHelper() {
    return mShippingHelper;
  }

  /**
   * @param pShippingHelper the shipping helper component to set.
   */
  public void setShippingHelper(StoreShippingProcessHelper pShippingHelper) {
    mShippingHelper = pShippingHelper;
  }

  /**
   * property: default shipping method
   */
  private String mDefaultShippingMethod = GROUND_SHIPPING_METHOD;

  /**
   * @return the default shipping method name.
   */
  public String getDefaultShippingMethod() {
    return mDefaultShippingMethod;
  }

  /**
   * @param pDefaultShippingMethod -
   * the default shipping method to set.
   */
  public void setDefaultShippingMethod(String pDefaultShippingMethod) {
    mDefaultShippingMethod = pDefaultShippingMethod;
  }

  /**
   * property: shipping method to use for current order
   */
  private String mShippingMethod;

  /**
   * @return the shipping method.
   */
  public String getShippingMethod() {
    return mShippingMethod;
  }

  /**
   * @param pShippingMethod - the shipping method to set.
   */
  public void setShippingMethod(String pShippingMethod) {
    mShippingMethod = pShippingMethod;
  }

  /**
   * property: flag indicating if new shipping address needs to be saved or not
   */
  private boolean mSaveShippingAddress = false;

  /**
   * @return true if shipping address should be saved, otherwise false.
  */
  public boolean isSaveShippingAddress() {
    return mSaveShippingAddress;
  }

  /**
   * @param pSaveShippingAddress - true if shipping address should be saved, otherwise false.
   */
  public void setSaveShippingAddress(boolean pSaveShippingAddress) {
    mSaveShippingAddress = pSaveShippingAddress;
  }

  /**
   * property: flag indicating whether shipping will be performed to the new address or not
   */
  private boolean mShipToNewAddress = false;

  /**
   * @return true if shipping will be performed to the new address,
   * false - otherwise.
  */
  public boolean isShipToNewAddress() {
    return mShipToNewAddress;
  }

  /**
   * @param pShipToNewAddress - true if shipping will be performed to the new address, otherwise false.
   */
  public void setShipToNewAddress(boolean pShipToNewAddress) {
    mShipToNewAddress = pShipToNewAddress;
  }

  /**
   * property: Nickname for selected shipping address
   */
  private String mShipToAddressName;

  /**
   * @return shipping address.
   */
  public String getShipToAddressName() {
    return mShipToAddressName;
  }

  /**
   * @param pShipToAddressName - shipping address.
   */
  public void setShipToAddressName(String pShipToAddressName) {
    mShipToAddressName = pShipToAddressName;
  }

  /**
   * property: Nickname for new shipping address
   */
  private String mNewShipToAddressName;

  /**
   * @return new shipping address.
   */
  public String getNewShipToAddressName() {
    return mNewShipToAddressName;
  }

  /**
   * @param pNewShipToAddressName - new shipping address.
   */
  public void setNewShipToAddressName(String pNewShipToAddressName) {
    mNewShipToAddressName = pNewShipToAddressName;

    if (mNewShipToAddressName != null) {
      mNewShipToAddressName = mNewShipToAddressName.trim();
    }
  }

  /**
   * property: Nickname of shipping address being modified
   */
  String mEditShippingAddressNickName;

  /**
   * @return the edit shipping address nickname.
   */
  public String getEditShippingAddressNickName() {
    return mEditShippingAddressNickName;
  }

  /**
   * @param pEditShippingAddressNickName - the edit shipping address nickname to set.
   */
  public void setEditShippingAddressNickName(String pEditShippingAddressNickName) {
    mEditShippingAddressNickName = pEditShippingAddressNickName;
  }

  /**
   * property: Nickname of shipping address being removed
   */
  String mRemoveShippingAddressNickName;

  /**
   * @return the remove shipping address nickname.
   */
  public String getRemoveShippingAddressNickName() {
    return mRemoveShippingAddressNickName;
  }

  /**
   * @param pRemoveShippingAddressNickName - the remove shipping address nickname to set.
   */
  public void setRemoveShippingAddressNickName(String pRemoveShippingAddressNickName) {
    mRemoveShippingAddressNickName = pRemoveShippingAddressNickName;
  }

  /**
   * property: New Nickname of shipping address
   */
  String mShippingAddressNewNickName;

  /**
   * @return the shipping address new nickname.
   */
  public String getShippingAddressNewNickName() {
    return mShippingAddressNewNickName;
  }

  /**
   * @param pShippingAddressNewNickName - the shipping address new nickname to set.
   */
  public void setShippingAddressNewNickName(String pShippingAddressNewNickName) {
    mShippingAddressNewNickName = pShippingAddressNewNickName;
  }

  /**
   * property: shipping address
   */
  private Address mAddress = new ContactInfo();

  /**
   * @return the address.
   */
  public Address getAddress() {
    return mAddress;
  }

  /**
   * @param pAddress - the address to set.
   */
  public void setAddress(Address pAddress) {
    mAddress = pAddress;
  }

  /**
   * property: address to be modified
   */
  private Address mEditAddress = new ContactInfo();

  /**
   * @return the edit address.
   */
  public Address getEditAddress() {
    return mEditAddress;
  }

  /**
   * @param pEditAddress - the edit address to set.
   */
  public void setEditAddress(Address pEditAddress) {
    mEditAddress = pEditAddress;
  }

  /**
   * property: Error URL for Move To Billing process
   */
  private String mShipToMultipleAddressesErrorURL;

  /**
   * @return move to billing error redirect URL.
   */
  public String getShipToMultipleAddressesErrorURL() {
    return mShipToMultipleAddressesErrorURL;
  }

  /**
   * @param pMoveToBillingErrorURL - move to billing error redirect URL.
   */
  public void setShipToMultipleAddressesErrorURL(String pShipToMultipleAddressesErrorURL) {
    mShipToMultipleAddressesErrorURL = pShipToMultipleAddressesErrorURL;
  }

  /**
   * property: Success URL for Move To Billing process
   */
  private String mShipToMultipleAddressesSuccessURL;

  /**
   * @return move to billing success redirect URL.
   */
  public String getShipToMultipleAddressesSuccessURL() {
    return mShipToMultipleAddressesSuccessURL;
  }

  /**
   * @param pMoveToBillingSuccessURL - move to billing success redirect URL.
   */
  public void setShipToMultipleAddressesSuccessURL(String pShipToMultipleAddressesSuccessURL) {
    mShipToMultipleAddressesSuccessURL = pShipToMultipleAddressesSuccessURL;
  }

  /**
   * property: Success URL for Add Shipping Address
   */
  private String mAddShippingAddressSuccessURL;

  /**
   * @return the add shipping address success redirect URL.
   */
  public String getAddShippingAddressSuccessURL() {
    return mAddShippingAddressSuccessURL;
  }

  /**
   * @param pAddShippingAddressSuccessURL - the add shipping address success redirect URL to set.
   */
  public void setAddShippingAddressSuccessURL(String pAddShippingAddressSuccessURL) {
    mAddShippingAddressSuccessURL = pAddShippingAddressSuccessURL;
  }

  /**
   * property: Error URL for Add Shipping Address
   */
  private String mAddShippingAddressErrorURL;

  /**
   * @return the add shipping address error redirect URL.
   */
  public String getAddShippingAddressErrorURL() {
    return mAddShippingAddressErrorURL;
  }

  /**
   * @param pAddShippingAddressErrorURL - the add shipping address error redirect URL to set.
   */
  public void setAddShippingAddressErrorURL(String pAddShippingAddressErrorURL) {
    mAddShippingAddressErrorURL = pAddShippingAddressErrorURL;
  }

  /**
   * property: Success URL for Edit Shipping Address
   */
  String mEditShippingAddressSuccessURL;

 /**
   * @return the edit shipping address success redirect URL.
   */
  public String getEditShippingAddressSuccessURL() {
    return mEditShippingAddressSuccessURL;
  }

  /**
   * @param pEditShippingAddressSuccessURL - the edit shipping address success redirect URL to set.
   */
  public void setEditShippingAddressSuccessURL(String pEditShippingAddressSuccessURL) {
    mEditShippingAddressSuccessURL = pEditShippingAddressSuccessURL;
  }

  /**
   * property: Error URL for Edit Shipping Address
   */
  String mEditShippingAddressErrorURL;

  /**
   * @return the edit shipping address error URL.
   */
  public String getEditShippingAddressErrorURL() {
    return mEditShippingAddressErrorURL;
  }

  /**
   * @param pEditShippingAddressErrorURL - the edit shipping address error redirect URL to set.
   */
  public void setEditShippingAddressErrorURL(String pEditShippingAddressErrorURL) {
    mEditShippingAddressErrorURL = pEditShippingAddressErrorURL;
  }

  /**
   * property: Success URL for Remove Shipping Address
   */
  String mRemoveShippingAddressSuccessURL;

 /**
   * @return the remove shipping address success redirect URL.
   */
  public String getRemoveShippingAddressSuccessURL() {
    return mRemoveShippingAddressSuccessURL;
  }

  /**
   * @param pRemoveShippingAddressSuccessURL - the remove shipping address success redirect URL to set.
   */
  public void setRemoveShippingAddressSuccessURL(String pRemoveShippingAddressSuccessURL) {
    mRemoveShippingAddressSuccessURL = pRemoveShippingAddressSuccessURL;
  }

  /**
   * property: Error URL for Remove Shipping Address
   */
  String mRemoveShippingAddressErrorURL;

  /**
   * @return the remove shipping address error URL.
   */
  public String getRemoveShippingAddressErrorURL() {
    return mRemoveShippingAddressErrorURL;
  }

  /**
   * @param pRemoveShippingAddressErrorURL - the remove shipping address error redirect URL to set.
   */
  public void setRemoveShippingAddressErrorURL(String pRemoveShippingAddressErrorURL) {
    mRemoveShippingAddressErrorURL = pRemoveShippingAddressErrorURL;
  }

  /**
   * property: Success URL for Add Shipping Address and Move to Multiple Shipping
   */
  private String mAddAddressAndMoveToMultipleShippingSuccessURL;

  /**
   * @return the Add Shipping Address and Move to Multiple Shipping success redirect URL.
   */
  public String getAddAddressAndMoveToMultipleShippingSuccessURL() {
    return mAddAddressAndMoveToMultipleShippingSuccessURL;
  }

  /**
   * @param pAddAddressAndMoveToMultipleShippingSuccessURL - 
   *          the Add Shipping Address and Move to Multiple Shipping success redirect URL to set.
   */
  public void setAddAddressAndMoveToMultipleShippingSuccessURL(
    String pAddAddressAndMoveToMultipleShippingSuccessURL) {
    
    mAddAddressAndMoveToMultipleShippingSuccessURL = pAddAddressAndMoveToMultipleShippingSuccessURL;
  }

  /**
   * property: Error URL for Add Shipping Address and Move to Multiple Shipping
   */
  private String mAddAddressAndMoveToMultipleShippingErrorURL;

  /**
   * @return the Add Shipping Address and Move to Multiple Shipping error redirect URL.
   */
  public String getAddAddressAndMoveToMultipleShippingErrorURL() {
    return mAddAddressAndMoveToMultipleShippingErrorURL;
  }

  /**
   * @param pAddAddressAndMoveToMultipleShippingErrorURL - 
   *          the Add Shipping Address and Move to Multiple Shipping error redirect URL to set.
   */
  public void setAddAddressAndMoveToMultipleShippingErrorURL(
    String pAddAddressAndMoveToMultipleShippingErrorURL) {
    
    mAddAddressAndMoveToMultipleShippingErrorURL = pAddAddressAndMoveToMultipleShippingErrorURL;
  }

  /**
   * property: Success URL for Update Shipping method process
   */
  String mUpdateShippingMethodSuccessURL;

  /**
   * @return the update shipping method success redirect URL.
   */
  public String getUpdateShippingMethodSuccessURL() {
    return mUpdateShippingMethodSuccessURL;
  }

  /**
   * @param pUpdateShippingMethodSuccessURL - the update shipping method success redirect URL to set.
   */
  public void setUpdateShippingMethodSuccessURL(String pUpdateShippingMethodSuccessURL) {
    mUpdateShippingMethodSuccessURL = pUpdateShippingMethodSuccessURL;
  }

  /**
   * property: Error URL for Update Shipping method process
   */
  String mUpdateShippingMethodErrorURL;

  /**
   * @return the update shipping method error redirect URL.
   */
  public String getUpdateShippingMethodErrorURL() {
    return mUpdateShippingMethodErrorURL;
  }

  /**
   * @param pUpdateShippingMethodErrorURL - the update shipping method error redirect URL to set.
   */
  public void setUpdateShippingMethodErrorURL(String pUpdateShippingMethodErrorURL) {
    mUpdateShippingMethodErrorURL = pUpdateShippingMethodErrorURL;
  }
  
  // -------------------------------------
  // property: ShipToNewAddressErrorURL
  // -------------------------------------
  private String mShipToNewAddressErrorURL;

  /**
   * @return the ship to new address error URL.
   */
  public String getShipToNewAddressErrorURL() {
    return mShipToNewAddressErrorURL;
  }
  
  /**
   * @param pShipToNewAddressErrorURL - the ship to new address error URL.
   */
  public void setShipToNewAddressErrorURL(String pShipToNewAddressErrorURL) {
    mShipToNewAddressErrorURL = pShipToNewAddressErrorURL;
  }

  // -------------------------------------
  // property: ShipToNewAddressSuccessURL
  // -------------------------------------
  private String mShipToNewAddressSuccessURL;

  /**
   * @return the ship to new address success URL.
   */
  public String getShipToNewAddressSuccessURL() {
    return mShipToNewAddressSuccessURL;
  }

  /**
   * @param - the ship to new address success URL.
   */
  public void setShipToNewAddressSuccessURL(String pShipToNewAddressSuccessURL) {
    mShipToNewAddressSuccessURL = pShipToNewAddressSuccessURL;
  }

  // -------------------------------------
  // property: ShipToExistingAddressSuccessURL
  // -------------------------------------
  private String mShipToExistingAddressSuccessURL;

  /**
   * @return the ship to existing address success URL.
   */
  public String getShipToExistingAddressSuccessURL() {
    return mShipToExistingAddressSuccessURL;
  }

  /**
   * @param pShipToExistingAddressSuccessURL - the ship to existing address success URL.
   */
  public void setShipToExistingAddressSuccessURL(
      String pShipToExistingAddressSuccessURL) {
    mShipToExistingAddressSuccessURL = pShipToExistingAddressSuccessURL;
  }

  // -------------------------------------
  // property: ShipToExistingAddressSuccessURL
  // -------------------------------------
  private String mShipToExistingAddressErrorURL;

  /**
   * @return the ship to existing address error URL.
   */
  public String getShipToExistingAddressErrorURL() {
    return mShipToExistingAddressErrorURL;
  }

  /**
   * @param pShipToExistingAddressErrorURL - the ship to existing address error URL.
   */
  public void setShipToExistingAddressErrorURL(
      String pShipToExistingAddressErrorURL) {
    mShipToExistingAddressErrorURL = pShipToExistingAddressErrorURL;
  }

  /**
   * @return a list of all shipping groups that contain gifts.
   */
  public List getGiftShippingGroups() {
    List giftShippingGroups = null;
    try {
      giftShippingGroups = getGiftlistManager().getGiftShippingGroups(getOrder());
    }
    catch (CommerceException e) {
      if(isLoggingError()){
        if(isLoggingError()) {
          logError(e);       
        }          
      }
    }
    return giftShippingGroups;
  }

  // -------------------------------------
  // Public Methods
  // -------------------------------------
  
  /**
   * Get the List of all the CommerceItemShippingInfos for hardgoods
   * from the CommerceItemShippingInfoMap. If a CommerceItemShippingInfo
   * has no shipping group, assume the item represents hardgoods.
   *
   * @return a <code>List</code> value - All hardgood commerce item shipping information.
   */
  public List getAllHardgoodCommerceItemShippingInfos() {
    if (mAllHardgoodCommerceItemShippingInfos == null) {
      mAllHardgoodCommerceItemShippingInfos = 
        getShippingHelper().getAllHardgoodCommerceItemShippingInfos(
          getShippingGroupMapContainer(), getCommerceItemShippingInfoContainer());
    }
    return mAllHardgoodCommerceItemShippingInfos;
  }

  /**
   * Determines if the total quantity of all non-gift hardgood items is more than one.
   * 
   * @return true if the the non-gift hardgood item quantity is more than one.
   */
  public boolean isMultipleNonGiftHardgoodItems() {
    return getShippingGroupManager().isMultipleNonGiftHardgoodItems(getOrder());
  }
  
  /**
   * Determines if the total quantity of all non-giftwrap items is more than one. 
   * 
   * @return true if the total quantity of all non-giftwrap items is more than one.
   */
  public boolean isMultipleNonGiftWrapItems() {
    return ((StoreShippingGroupManager)getShippingGroupManager()).isMultipleNonGiftWrapItems(getOrder());
  }

  // -------------------------------------
  // HandleX Methods
  // -------------------------------------
  
  /**
   * Override handleCancel to clear form exceptions so that error message about
   * missing required fields is not displayed when a shopper hits "Cancel".
   * 
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @return a <code>boolean</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public boolean handleCancel(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    resetFormExceptions();
    return super.handleCancel(pRequest, pResponse);
  }

  /**
   * This handler method will validate shipping address and apply the shipping groups to
   * the order.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   *            
   * @return a <code>boolean</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public boolean handleShipToMultipleAddresses(DynamoHttpServletRequest pRequest, 
                                               DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    Transaction tr = null;

    try {
      tr = ensureTransaction();

      if (!checkFormRedirect(null, getShipToMultipleAddressesErrorURL(), pRequest, pResponse)) {
        return false;
      }

      synchronized (getOrder()) {
        try {
          preShipToMultipleAddresses(pRequest, pResponse);

          if (getFormError()) {
            if (isLoggingDebug()) {
              logDebug("Redirecting due to form error in preMoveToBilling.");
            }

            return checkFormRedirect(null, getShipToMultipleAddressesErrorURL(), pRequest, pResponse);
          }

          shipToMultipleAddresses(pRequest, pResponse);

          if (getFormError()) {
            if (isLoggingDebug()) {
              logDebug("Redirecting due to form error in moveToBilling");
            }

            return checkFormRedirect(null, getShipToMultipleAddressesErrorURL(), pRequest, pResponse);
          }

          runProcessValidateShippingGroups(getOrder(), 
                                           getUserPricingModels(), 
                                           getUserLocale(pRequest, pResponse),
                                           getProfile(), null);
        } 
        catch (Exception exc) {
          if (isLoggingDebug()) {
            logDebug("Resource bundle being used: " + getResourceBundleName());
          }

          processException(exc, exc.getMessage(), pRequest, pResponse);
        }

        if (getFormError()) {
          if (isLoggingDebug()) {
            logDebug("Redirecting due to form error in runProcessValidateShippingGroups");
          }

          return checkFormRedirect(null, getShipToMultipleAddressesErrorURL(), pRequest, pResponse);
        }

        postShipToMultipleAddresses(pRequest, pResponse);

        try {
          getOrderManager().updateOrder(getOrder());
        } 
        catch (Exception exc) {
          processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        }
      } // synchronized

      // Always commit before a redirect!
      if (tr != null) {
        commitTransaction(tr);
        tr = null;
      }

      // If NO form errors are found, redirect to the success URL.
      // If form errors are found, redirect to the error URL.
      return checkFormRedirect(getShipToMultipleAddressesSuccessURL(), 
                               getShipToMultipleAddressesErrorURL(), 
                               pRequest, pResponse);
    } 
    finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }

  /**
  * This method validates the user inputs for the Move To Billing process.
  * 
  * @param pRequest a <code>DynamoHttpServletRequest</code> value.
  * @param pResponse a <code>DynamoHttpServletResponse</code> value.
  * 
  * @exception ServletException if an error occurs.
  * @exception IOException if an error occurs.
  */
  public void preShipToMultipleAddresses(DynamoHttpServletRequest pRequest, 
                                         DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    tenderCoupon(pRequest, pResponse);

    if (getFormError()) {
      return;
    }

    // Validate addresses.
    validateMultiShippingAddresses(pRequest, pResponse);
      
    if (getFormError()) {
        return;
    }

    validateShippingMethodForContainerShippingGroups(pRequest, pResponse);

    if (getFormError()) {
      return;
    }

    validateShippingRestrictions(pRequest, pResponse);

    preSetupGiftShippingDetails(pRequest, pResponse);
  }

  /**
   * Applies the data in the CommerceItemShippingInfoContainer and ShippingGroupMapContainer to the
   * order.
   * 
   * @see ShippingGroupFormHandler#applyShippingGroups(DynamoHttpServletRequest, DynamoHttpServletResponse)
   * 
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public void shipToMultipleAddresses(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    if (!checkFormRedirect(null, getShipToMultipleAddressesErrorURL(), pRequest, pResponse)) {
      return;
    }

    StoreShippingProcessHelper shippingHelper = getShippingHelper();

     // Put shipping addresses with different shipping methods into separate shipping groups.

    try {
      List splitSgNames = 
        getShippingHelper().splitShippingGroupsByMethod(getShippingGroupMapContainer(), 
                                                        getAllHardgoodCommerceItemShippingInfos());
      
      // If we have items going to the same address using a different shipping method then we create
      // duplicate shipping groups in the shipping group map. Add these to the list of shipping
      // groups we don't want to display so we don't have duplicates when displaying possible
      // addresses on screen.
      ((StoreShippingGroupContainerService)
          getShippingGroupMapContainer()).setNonDisplayableShippingGroups(splitSgNames);
    
    } 
    catch (CommerceException ex) {
      processException(ex, ex.getMessage(), pRequest, pResponse);
    }

    if (getFormError()) {
      return;
    }

    /* 
     * if there are giftwrap items in the container, we must ensure they are in
     * one of the shipping groups containing the rest of the hardgood items.
     */
    shippingHelper.setGiftWrapItemShippingGroupInfos(getAllHardgoodCommerceItemShippingInfos());

    if (getFormError()) {
      return;
    }

    applyShippingGroups(pRequest, pResponse);

    if (getFormError()) {
      return;
    }

    setupGiftShippingDetails(pRequest, pResponse);

    if (getFormError()) {
      return;
    }
  }

  /**
  * This method will reprice the order to catch address problems through CyberSource.
  * <p>Initializes the billing address from the shipping address if the user selected
  * that option.
  * <p>Saves addresses in the profile, if the user selected that option.
  *
  * @param pRequest a <code>DynamoHttpServletRequest</code> value.
  * @param pResponse a <code>DynamoHttpServletResponse</code> value.
  * 
  * @exception ServletException if an error occurs.
  * @exception IOException if an error occurs.
  */
  public void postShipToMultipleAddresses(DynamoHttpServletRequest pRequest, 
                                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    if (getFormError()) {
      return;
    }

    // Reprice catches problems reported by CyberSource
    repriceOrder(pRequest, pResponse);

    if (getFormError()) {
      return;
    }
   
    // Set the profile's default shipping method if it isn't already set.
    getShippingHelper().saveDefaultShippingMethod(getProfile(), getShippingMethod());

    postSetupGiftShippingDetails(pRequest, pResponse);

    if (mCheckoutProgressStates != null && !getFormError())
    {
      mCheckoutProgressStates.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.BILLING.toString());
    }
  }

  /**
   * Performs input data validations for new shipping address specified by
   * shopper
   * 
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.
   * @exception ServletException
   *              if an error occurs.
   * @exception IOException
   *              if an error occurs.
   */
  protected void preShipToNewAddress(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    if (isAnyHardgoodShippingGroups()) {
      // If we are in a single shipping group checkout then the user has the option of
      // specifying a new shipping address on the form.

      String addressName = getNewShipToAddressName();
      Address address = getAddress();

      validateShippingAddress(address, pRequest, pResponse);

      // Generate unique default address name if none supplied and
      // validation checks succeeded.
      if (StringUtils.isBlank(addressName) && !getFormError()) {
        /*
         * addressName =
         * getShippingHelper().generateNewShipToAddressNickName(address,
         * addressName, getShippingGroupMapContainer());
         */
        CommerceProfileTools profileTools = getShippingHelper().getStoreOrderTools().getProfileTools();
        
        addressName = profileTools.getUniqueShippingAddressNickname(address,
                                                                    getProfile(), 
                                                                    addressName);
        
        setNewShipToAddressName(addressName);
      }

      // Validate nickname.
      if (isSaveShippingAddress()) {
        validateAddressNicknameForUniqueness(addressName, pRequest, pResponse);
      }
      
      validateShippingRestrictions(pRequest, pResponse);
    } // end if single sg checkout
  }

  /**
   * Setup single shipping details for shipping to a new address.
   * 
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.
   * @exception ServletException
   *              if an error occurs.
   * @exception IOException
   *              if an error occurs.
   */
  protected void shipToNewAddress(DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {

    if (!checkFormRedirect(null, getShipToNewAddressErrorURL(), pRequest,
        pResponse)) {
      return;
    }
    if (isAnyHardgoodShippingGroups()) {
      // If we are in a single shipping group checkout then the user has the option of
      // specifying a new shipping address on the form.

      try {
        // Create a new shipping group with the new address and put the new
        // shipping group in the container.
        getShippingHelper().findOrAddShippingGroupByNickname(getProfile(), getNewShipToAddressName(),
                                                             getAddress(), getShippingGroupMapContainer(),
                                                             isSaveShippingAddress());
      } 
      catch (StorePurchaseProcessException pe) {
        String msg = ResourceUtils.getMsgResource(pe.getMessage(),
                                                  getResourceBundleName(), 
                                                  getResourceBundle(getUserLocale(pRequest,
                                                  pResponse)));
        addFormException(new DropletFormException(msg, "", pe.getMessage()));
        return;
      } 
      catch (CommerceException e) {
        addFormException(new DropletFormException(e.getMessage(), null));
        return;
      } 
      catch (IntrospectionException e) {
        addFormException(new DropletFormException(e.getMessage(), null));
        return;
      }

      // Change commerce item infos to point to new shipping group.
      getShippingHelper().changeShippingGroupForCommerceItemShippingInfos(getAllHardgoodCommerceItemShippingInfos(), 
                                                                          getNewShipToAddressName(),
                                                                          getShippingMethod());
      if (getFormError()) {
        return;
      }

      applyShippingGroups(pRequest, pResponse);
    }
  }

  /**
   * This method initializes the billing address from the shipping address if the user selected 
   * that option. Saves addresses in the profile, if the user selected that option.
   * 
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.
   * @exception ServletException
   *              if an error occurs.
   * @exception IOException
   *              if an error occurs.
   */
  protected void postShipToNewAddress(DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {

    // Set the profile's default shipping method if it isn't already set.
    getShippingHelper().saveDefaultShippingMethod(getProfile(), getShippingMethod());
    
    // Allow to move to the next stage.
    if (mCheckoutProgressStates != null && !getFormError())
    {
      mCheckoutProgressStates.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.BILLING.toString());
    }
  }
  

  /**
   * Perform any pre actions before setting up gift shipping groups.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  protected void preSetupGiftShippingDetails(DynamoHttpServletRequest pRequest, 
                                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    // empty implementation
  }


  /**
   * Setup shipping details for gift shipping groups.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  protected void setupGiftShippingDetails(DynamoHttpServletRequest pRequest, 
                                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    // Gift shipping groups can be treated as all other groups now, do not set shipping method on them.
  }

  /**
   * Perform any post actions after setting up gift shipping groups.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  protected void postSetupGiftShippingDetails(DynamoHttpServletRequest pRequest, 
                                              DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    // empty implementation
  }

  /**
   * Adds a new shipping group to the ShippingGroupMapContainer.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @return true if success, otherwise false.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   */
  public boolean handleAddShippingAddress(DynamoHttpServletRequest pRequest, 
                                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    Transaction tr = null;

    try {
      tr = ensureTransaction();

      if (!checkFormRedirect(null, getAddShippingAddressErrorURL(), pRequest, pResponse)) {
        return false;
      }

      preAddShippingAddress(pRequest, pResponse);

      if (getFormError()) {
        if (isLoggingDebug()) {
          logDebug("Redirecting due to form error in preAddShippingAddress.");
        }

        return checkFormRedirect(null, getAddShippingAddressErrorURL(), pRequest, pResponse);
      }

      addShippingAddress(pRequest, pResponse);

      if (getFormError()) {
        if (isLoggingDebug()) {
          logDebug("Redirecting due to form error in addShippingAddress");
        }

        return checkFormRedirect(null, getAddShippingAddressErrorURL(), pRequest, pResponse);
      }

      postAddShippingAddress(pRequest, pResponse);

      // Always commit before a redirect!
      if (tr != null) {
        commitTransaction(tr);
        tr = null;
      }

      // If NO form errors are found, redirect to the success URL.
      // If form errors are found, redirect to the error URL.
      return checkFormRedirect(getAddShippingAddressSuccessURL(), 
                               getAddShippingAddressErrorURL(), 
                               pRequest, pResponse);
    } 
    finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }

  /**
   * Validates the selected nickname and address properties.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public void preAddShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    String addressName = getNewShipToAddressName();
    Address address = getShippingHelper().trimSpacesFromAddressValues(getAddress());

    // Validate address and nickname.
    if (isSaveShippingAddress()) {
      validateAddressNicknameForUniqueness(getShippingHelper().getStoreOrderTools().getProfileTools().
        getUniqueShippingAddressNickname(address, getProfile(), addressName), pRequest, pResponse);
    }

    validateShippingAddress(address, pRequest, pResponse);
  }

  /**
   * Creates a new shipping group and adds it to the shipping group map container. Optionally 
   * saves the shipping group address to the profile based on the saveShippingAddress property.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public void addShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    try {
      getShippingHelper().addShippingAddress(getProfile(), getNewShipToAddressName(), getAddress(),
        getShippingGroupMapContainer(), isSaveShippingAddress());
    }
    catch (StorePurchaseProcessException bppe) {
      String msg = ResourceUtils.getMsgResource(bppe.getMessage(), 
                                                getResourceBundleName(), 
                                                getResourceBundle(getUserLocale(pRequest, pResponse)));
      
      addFormException(new DropletFormException(msg, "", bppe.getMessage()));
    }
    catch (CommerceException ce) {
      if (isLoggingError()) {
        logError(LogUtils.formatMinor(""), ce);
      }
      addFormException(new DropletFormException(ce.getMessage(), ce, null));
    }
  }


  /**
   * Copies the new shipping group address to the order's credit card payment group address.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public void postAddShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    // empty method
  }

  /**
   * Handler for editing an address.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @return true if success, false - otherwise.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   */
  public boolean handleEditShippingAddress(DynamoHttpServletRequest pRequest, 
                                           DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    Transaction tr = null;

    try {
      tr = ensureTransaction();

      if (!checkFormRedirect(null, getEditShippingAddressErrorURL(), pRequest, pResponse)) {
        return false;
      }

      preEditShippingAddress(pRequest, pResponse);

      if (getFormError()) {
        if (isLoggingDebug()) {
          logDebug("Redirecting due to form error in preEditShippingAddress.");
        }

        return checkFormRedirect(null, getEditShippingAddressErrorURL(), pRequest, pResponse);
      }

      editShippingAddress(pRequest, pResponse);

      if (getFormError()) {
        if (isLoggingDebug()) {
          logDebug("Redirecting due to form error in editShippingAddress");
        }

        return checkFormRedirect(null, getEditShippingAddressErrorURL(), pRequest, pResponse);
      }

      postEditShippingAddress(pRequest, pResponse);

      // Always commit before a redirect!
      if (tr != null) {
        commitTransaction(tr);
        tr = null;
      }

      // If NO form errors are found, redirect to the success URL.
      // If form errors are found, redirect to the error URL.
      return checkFormRedirect(getEditShippingAddressSuccessURL(), 
                               getEditShippingAddressErrorURL(), 
                               pRequest, pResponse);
    } 
    finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }

  /**
   * Validates the address properties.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public void preEditShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    String newNickName = getShippingAddressNewNickName();
    
    if(newNickName != null && !newNickName.equals(getEditShippingAddressNickName())) {
      validateAddressNicknameForUniqueness(newNickName, pRequest, pResponse);
    }
    
    validateShippingAddress(getEditAddress(), pRequest, pResponse);
  }


  /**
   * Edits a shipping group address in the container and saves the changes to the profile if 
   * the address is in the profile's address map.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public void editShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    getShippingHelper().modifyShippingAddress(getEditShippingAddressNickName(), getEditAddress(),
      getShippingGroupMapContainer());

    if (getShippingAddressNewNickName()!= null && 
        !getShippingAddressNewNickName().equals(getEditShippingAddressNickName())) {
      
      getShippingHelper().modifyShippingAddressNickname(getProfile(), 
                                                        getEditShippingAddressNickName(),
                                                        getShippingAddressNewNickName(), 
                                                        getShippingGroupMapContainer());
    }
  }

  /**
   * Post edit shipping address processing. If the address nick name is in the profile's map,
   * the updates are applied to that address too.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public void postEditShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    getShippingHelper().saveModifiedShippingAddressToProfile(getProfile(),
      getShippingAddressNewNickName() != null ? getShippingAddressNewNickName() : getEditShippingAddressNickName(),
      getEditAddress());
  }

  /**
   * Handler for removing a shipping address.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @return true if success, false - otherwise.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   */
  public boolean handleRemoveShippingAddress(DynamoHttpServletRequest pRequest, 
                                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    Transaction tr = null;

    try {
      tr = ensureTransaction();

      if (!checkFormRedirect(null, getRemoveShippingAddressErrorURL(), pRequest, pResponse)) {
        return false;
      }

      preRemoveShippingAddress(pRequest, pResponse);

      if (getFormError()) {
        if (isLoggingDebug()) {
          logDebug("Redirecting due to form error in preRemoveShippingAddress.");
        }

        return checkFormRedirect(null, getRemoveShippingAddressErrorURL(), pRequest, pResponse);
      }

      removeShippingAddress(pRequest, pResponse);

      if (getFormError()) {
        if (isLoggingDebug()) {
          logDebug("Redirecting due to form error in removeShippingAddress");
        }

        return checkFormRedirect(null, getRemoveShippingAddressErrorURL(), pRequest, pResponse);
      }

      postRemoveShippingAddress(pRequest, pResponse);

      // Always commit before a redirect!
      if (tr != null) {
        commitTransaction(tr);
        tr = null;
      }

      // If NO form errors are found, redirect to the success URL.
      // If form errors are found, redirect to the error URL.
      return checkFormRedirect(getRemoveShippingAddressSuccessURL(), 
                               getRemoveShippingAddressErrorURL(), 
                               pRequest, pResponse);
    } 
    finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }

  /**
   * Pre remove shipping address processing.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public void preRemoveShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    // empty implementation
  }


  /**
   * Removes a shipping group address from the container.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public void removeShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    getShippingHelper().removeShippingAddress(getProfile(),getRemoveShippingAddressNickName(),
      getShippingGroupMapContainer());
  }

  /**
   * Post remove shipping address processing. If the address nickname is in the profile's map,
   * the address is removed from the profile too.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public void postRemoveShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    getShippingHelper().removeShippingAddressFromProfile(getProfile(), getRemoveShippingAddressNickName());
  }

  /**
   * This handler method will a new shipping group to the ShippingGroupMapContainer if new shipping address
   * is not empty. And will redirect to the multiple shipping URL. If new shipping address is empty it will
   * redirect to multiple shipping URL without adding new shipping group.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @return a <code>boolean</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public boolean handleAddAddressAndMoveToMultipleShipping(DynamoHttpServletRequest pRequest, 
                                                           DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    if (getShippingHelper().isEmptyNewAddress((ContactInfo)getAddress())){
      
      // Create new address is empty just move to the multiple shipping without saving an address
      return checkFormRedirect(getAddAddressAndMoveToMultipleShippingSuccessURL(), 
                               getAddAddressAndMoveToMultipleShippingErrorURL(), 
                               pRequest, 
                               pResponse);
    }
    setAddShippingAddressSuccessURL(getAddAddressAndMoveToMultipleShippingSuccessURL());
    setAddShippingAddressErrorURL(getAddAddressAndMoveToMultipleShippingErrorURL());
    
    return handleAddShippingAddress(pRequest, pResponse);

  }

  /**
   * Initializes the form handler for single shipping group selection. Initialization only
   * occurs if there are no form errors.
   * 
   * <p>
   * This method expects that the ShippingGroupMapContainer has been initialized with shipping groups.
   * <p>
   * 
   * The following form properties are initialized: <br>
   * 
   * <dl>
   *   <dt>shipToAddressName</dt>
   *     <dd>
   *       initialized to the profile's address nick name used to originally create the address. This is
   *       determined by matching the shipping address to profile addresses. If there is a gift hard good
   *       shipping group in the order, that gift shipping group will be used as the default address.
   *       If there's no match, it is set to <code>NEW_ADDRESS</code>.
   *     </dd>
   *   
   *   <dt>address</dt>
   *     <dd>if the shipping address isn't in the profile this property is initialized with the address</dd>
   * 
   *   <dt>shippingMethod</dt>
   *     <dd>
   *       initialized from the current shipping group's shipping method value, or the profile's default
   *       setting, or the default configured value.
   *     </dd>
   * </dl>
   * 
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @return always true.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   */
  public boolean handleInitSingleShippingForm(DynamoHttpServletRequest pRequest, 
                                              DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    if (!getFormError()) {
      StoreShippingProcessHelper shippingHelper = getShippingHelper();
      StoreOrderTools orderTools = shippingHelper.getStoreOrderTools();
      ShippingGroupManager shippingGroupManager = getShippingGroupManager();
      ShippingGroupMapContainer sgmc = getShippingGroupMapContainer();
      HardgoodShippingGroup hgsg = null;
      String shippingGroupName = null;

      // Retrieve the gift only shipping groups from the order.
      List<ShippingGroup> giftOnlyShippingGroups  = getGiftShippingGroups();

      if (giftOnlyShippingGroups != null) {
        for (ShippingGroup sg : giftOnlyShippingGroups) {
          if (sg instanceof HardgoodShippingGroup) {
            // We only want the first gift hard good shipping group. This should be
            // used as the default selected shipping group when no other shipping group 
            // has been previously selected.
            hgsg = (HardgoodShippingGroup) sg;
            break;
          }
        }
      }

      if (hgsg == null) {
        // Get the shipping groups associated with the order, as this is single shipping there will
        // only be one shipping group associated with the order.
        List hSGroups = shippingGroupManager.getHardgoodShippingGroups(getOrder());
        
        // When a gift hard good shipping group doesn't exist in the order, 
        // just retrieve the order's first hard good shipping group.
        if (hSGroups != null && !hSGroups.isEmpty()) {
          hgsg = (HardgoodShippingGroup) hSGroups.get(0);
        }
      }

      // If we have a shipping group associated with the order, try to figure out where it came from.
      if (hgsg != null) {
        
        // Re initalize the shipToAddressName if its blank or if it isnt in the shipping group map
        if (StringUtils.isBlank(getShipToAddressName()) ||
            !(sgmc.getShippingGroupMap().containsKey(getShipToAddressName()))) {
          
          // Set a default shipToAddressName. shipToAddressName defaults to the first
          // (alphabetically) address on the profile or else "NEW".
          Map addresses = (Map) getProfile()
                                 .getPropertyValue(((StorePropertyManager) orderTools.getProfileTools()
                                 .getPropertyManager()).getSecondaryAddressPropertyName());
          
          TreeMap sortedAddresses = new TreeMap(addresses);
          
          if (sortedAddresses != null && !sortedAddresses.isEmpty()) {
            setShipToAddressName((String)sortedAddresses.firstKey());
          }
          else {
            setShipToAddressName(StoreShippingProcessHelper.NEW_ADDRESS);
          }
          
          // If the orders address is populated (not full of null values) figure out its nickname.
          Address currentaddress = hgsg.getShippingAddress();
          
          if(!StringUtils.isBlank(currentaddress.getAddress1())) {
            
            // The shipping address already in the order might be one of the addresses in the map.
            shippingGroupName = shippingGroupManager.getShippingGroupName(hgsg, sgmc,
                getShippingGroupInitializers().values());

            // Figure out if the name is in the profile's shipping address map.
            if (shippingGroupName != null) {
              if (addresses != null) {
                if (addresses.containsKey(shippingGroupName) || 
                    sgmc.getShippingGroupMap().containsKey(shippingGroupName)) {
                  
                  setShipToAddressName(shippingGroupName);
                }
                else {
                  setAddress(hgsg.getShippingAddress());
                }
              }
            }
            else {
              setAddress(hgsg.getShippingAddress());
            }
          }
          // We have a shipping group, but the address hasn't been filled in.  
          else {
            
            // Set the shipToAddressName to the profiles default address
            HardgoodShippingGroup defaulthgsg = 
              shippingHelper.createShippingGroupFromDefaultAddress(getProfile());
            
            if (defaulthgsg != null) {
              shippingGroupName = 
                shippingGroupManager.getShippingGroupName(defaulthgsg, 
                                                          sgmc, 
                                                          getShippingGroupInitializers().values());
              if (shippingGroupName != null) {
                setShipToAddressName(shippingGroupName);
              }
            }
          }
        }

        
        // Get nickname for not-saved shipping address. It's stored nowhere but in the 
        // shipping group name, take first CISI and look into its shipping group name.
         
        if (StringUtils.isBlank(getNewShipToAddressName()) && isShipToNewAddress()) {
          
          List<CommerceItemShippingInfo> allItems = 
            getCommerceItemShippingInfoContainer().getAllCommerceItemShippingInfos();
          
          if (!allItems.isEmpty()) {
            setNewShipToAddressName(allItems.get(0).getShippingGroupName());
          }
        }

        // Initialize the shipping method if not set yet.
        if (StringUtils.isBlank(getShippingMethod())) {
          String shippingMethod =
            getShippingHelper().initializeShippingMethod(getProfile(), hgsg, getDefaultShippingMethod());
          setShippingMethod(shippingMethod);
        }
      }
    }

    return true;
  }

  /**
   * Initializes the form handler for multiple shipping group selection. Initialization only
   * occurs if there are no form errors.
   * 
   * <p>
   * The following form properties are initialized: <br>
   * 
   * <dl>
   *   <dt>shippingMethod</dt>
   *     <dd>
   *        Initialized from the first shipping group's shipping method value, or the profile's default
   *        setting, or the default configured value. Note that the application only allows one shipping
   *        method to be used per order, so all the shipping groups have the same shipping method.
   *     </dd>
   * </dl>
   * 
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @return always true.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   */
  public boolean handleInitMultipleShippingForm(DynamoHttpServletRequest pRequest, 
                                                DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    // Only initialize if there aren't form errors.
    if (!getFormError()) {
      HardgoodShippingGroup hgsg = getFirstNonGiftHardgoodShippingGroupWithRels();

      // Initialize the shipping method from the first shipping group in the order.
      if (hgsg != null) {
        if (StringUtils.isBlank(getShippingMethod())) {
          String shippingMethod =
            getShippingHelper().initializeShippingMethod(getProfile(), hgsg, getDefaultShippingMethod());
          setShippingMethod(shippingMethod);
        }
      }
    }
    return true;
  }

  /**
   * Initializes the address property with the shipping group address identified by the editNickName.
   * 
   * <p>Initialization only takes place if there aren't any form errors.</p>
   * 
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @return true if success, false - otherwise.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   */
  public boolean handleInitEditAddressForm(DynamoHttpServletRequest pRequest, 
                                           DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    if (!getFormError()) {
      
      String editNickName = getEditShippingAddressNickName();
      
      if (StringUtils.isBlank(editNickName)) {
        return true;
      }

      HardgoodShippingGroup hgsg = (HardgoodShippingGroup) 
        getShippingGroupMapContainer().getShippingGroup(editNickName);
      
      if (hgsg != null) {
        setEditAddress(hgsg.getShippingAddress());
      }
    }
    return true;
  }

  /**
   * Handle 'Ship to new address' case.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @return redirection result.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public boolean handleShipToNewAddress(DynamoHttpServletRequest pRequest,
                                        DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {

    Transaction tr = null;

    try {
      tr = ensureTransaction();

      if (!checkFormRedirect(null, getShipToNewAddressErrorURL(), pRequest, pResponse)) {
        return false;
      }

      synchronized (getOrder()) {
        try {
          preShipToNewAddress(pRequest, pResponse);

          if (getFormError()) {
            if (isLoggingDebug()) {
              logDebug("Redirecting due to form error in preShipToNewAddress() method");
            }

            return checkFormRedirect(null, getShipToNewAddressErrorURL(), pRequest, pResponse);
          }

          shipToNewAddress(pRequest, pResponse);

          if (getFormError()) {
            if (isLoggingDebug()) {
              logDebug("Redirecting due to form error in shipToNewAddress() method");
            }

            return checkFormRedirect(null, getShipToNewAddressErrorURL(), pRequest, pResponse);
          }

          runProcessValidateShippingGroups(getOrder(), 
                                           getUserPricingModels(), 
                                           getUserLocale(pRequest, pResponse), 
                                           getProfile(), 
                                           null);
        } 
        catch (Exception exc) {
          if (isLoggingDebug()) {
            logDebug("Resource bundle is being used: " + getResourceBundleName());
          }

          processException(exc, exc.getMessage(), pRequest, pResponse);
        }

        if (getFormError()) {
          if (isLoggingDebug()) {
            logDebug("Redirecting due to form error in runProcessValidateShippingGroups");
          }

          return checkFormRedirect(null, getShipToNewAddressErrorURL(),
              pRequest, pResponse);
        }

        postShipToNewAddress(pRequest, pResponse);

        try {
          getOrderManager().updateOrder(getOrder());
        } 
        catch (Exception exc) {
          processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        }
      } // synchronized

      // Always commit before a redirect!
      if (tr != null) {
        commitTransaction(tr);
        tr = null;
      }

      //  If no form errors are found, redirect to the success URL, otherwise
      //  redirect to the error URL.

      return checkFormRedirect(getShipToNewAddressSuccessURL(),
                               getShipToNewAddressErrorURL(), 
                               pRequest, pResponse);
    } 
    finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }

  /**
   * Handle 'Ship to existing address' case.
   * 
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @return redirection result.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public boolean handleShipToExistingAddress(DynamoHttpServletRequest pRequest,
                                             DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {

    Transaction tr = null;

    try {
      tr = ensureTransaction();

      if (!checkFormRedirect(null, getShipToExistingAddressErrorURL(),
          pRequest, pResponse)) {
        return false;
      }

      synchronized (getOrder()) {
        try {
          preShipToExistingAddress(pRequest, pResponse);

          if (getFormError()) {
            if (isLoggingDebug()) {
              logDebug("Redirecting due to form error in preShipToNewAddress() method");
            }

            return checkFormRedirect(null, getShipToExistingAddressErrorURL(), pRequest, pResponse);
          }

          shipToExistingAddress(pRequest, pResponse);

          if (getFormError()) {
            if (isLoggingDebug()) {
              logDebug("Redirecting due to form error in shipToNewAddress() method");
            }

            return checkFormRedirect(null, getShipToExistingAddressErrorURL(), pRequest, pResponse);
          }

          runProcessValidateShippingGroups(getOrder(), 
                                           getUserPricingModels(),
                                           getUserLocale(pRequest, pResponse), 
                                           getProfile(), 
                                           null);
        } catch (Exception exc) {
          if (isLoggingDebug()) {
            logDebug("Resource bundle is being used: " + getResourceBundleName());
          }

          processException(exc, exc.getMessage(), pRequest, pResponse);
        }

        if (getFormError()) {
          if (isLoggingDebug()) {
            logDebug("Redirecting due to form error in runProcessValidateShippingGroups");
          }

          return checkFormRedirect(null, getShipToExistingAddressErrorURL(), pRequest, pResponse);
        }

        postShipToExistingAddress(pRequest, pResponse);

        try {
          getOrderManager().updateOrder(getOrder());
        } 
        catch (Exception exc) {
          processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        }
      } // synchronized

      // Always commit before a redirect!
      if (tr != null) {
        commitTransaction(tr);
        tr = null;
      }

      // If no form errors are found, redirect to the success URL, otherwise
      // redirect to the error URL.

      return checkFormRedirect(getShipToExistingAddressSuccessURL(),
                               getShipToExistingAddressErrorURL(), 
                               pRequest, pResponse);
    } 
    finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }
  
  /**
   * Pre-shipping validation. Required fields should be filled in
   * and address should be available for shipping. 
   * 
   * @param pRequest Dynamo HTTP request.
   * @param pResponse Dynamo HTTP response.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  protected void preShipToExistingAddress(DynamoHttpServletRequest pRequest,
                                          DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    if (isAnyNonGiftHardgoodShippingGroups()) {

      ShippingGroupMapContainer sgmc = getShippingGroupMapContainer();

        Address address = getAddress();
        String addressName = getShipToAddressName();

        if (StringUtils.isEmpty(addressName)) {
          addFormException(new DropletException(formatUserMessage(MSG_NO_SHIPPING_ADDRESS_SELECTED, 
                                                                  pRequest, pResponse)));
          return;
        }

        // The shipping group for the selected address should already be in the map.
        HardgoodShippingGroup hgsg = (HardgoodShippingGroup) sgmc.getShippingGroup(addressName);
        address = hgsg.getShippingAddress();
        validateShippingAddress(address, pRequest, pResponse);

        if (getFormError()) {
          return;
        }

        validateShippingRestrictions(pRequest, pResponse);
    } // end if any non gift hgsg

  }

  /**
   * Post-shipping processing.
   * 
   * @param pRequest Dynamo HTTP request.
   * @param pResponse Dynamo HTTP response.
   */
  protected void postShipToExistingAddress(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse) {
    // set the profile's default shipping method if it isn't already set.
    getShippingHelper().saveDefaultShippingMethod(getProfile(), getShippingMethod());
    
    if (mCheckoutProgressStates != null && !getFormError())
    {
      mCheckoutProgressStates.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.BILLING.toString());
    }
  }

  /**
   * Shiping to existing address.
   * 
   * @param pRequest Dynamo HTTP request.
   * @param pResponse Dynamo HTTP response.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  protected void shipToExistingAddress(DynamoHttpServletRequest pRequest,
                                       DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    if (!checkFormRedirect(null, getShipToExistingAddressErrorURL(), pRequest, pResponse)) {
      return;
    }

    StoreShippingProcessHelper shippingHelper = getShippingHelper();

    String shippingMethod = getShippingMethod();
    String addressName = null;

    addressName = getShipToAddressName();

    // Update the commerce item shipping infos for all hardgoods to point to
    // the selected shipping group and method.
    shippingHelper.changeShippingGroupForCommerceItemShippingInfos(
      getAllHardgoodCommerceItemShippingInfos(), addressName, shippingMethod);

      if (getFormError()) {
        return;
      }

      applyShippingGroups(pRequest, pResponse);
  }

  /**
   * Handler for editing shipping group's shipping method.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @return true if success, false - otherwise.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   */
  public boolean handleUpdateShippingMethod(DynamoHttpServletRequest pRequest, 
                                            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    Transaction tr = null;

    try {
      tr = ensureTransaction();

      if (!checkFormRedirect(null, getUpdateShippingMethodErrorURL(), pRequest, pResponse)) {
        return false;
      }

      preUpdateShippingMethod(pRequest, pResponse);

      if (getFormError()) {
        if (isLoggingDebug()) {
          logDebug("Redirecting due to form error in preUpdateShippingMethod.");
        }

        return checkFormRedirect(null, getUpdateShippingMethodErrorURL(), pRequest, pResponse);
      }

      updateShippingMethod(pRequest, pResponse);

      if (getFormError()) {
        if (isLoggingDebug()) {
          logDebug("Redirecting due to form error in UpdateShippingMethod");
        }

        return checkFormRedirect(null, getUpdateShippingMethodErrorURL(), pRequest, pResponse);
      }

      postUpdateShippingMethod(pRequest, pResponse);

      // Always commit before a redirect!
      if (tr != null) {
        commitTransaction(tr);
        tr = null;
      }

      // If NO form errors are found, redirect to the success URL.
      // If form errors are found, redirect to the error URL.
      return checkFormRedirect(getUpdateShippingMethodSuccessURL(), getUpdateShippingMethodErrorURL(),
        pRequest, pResponse);
    } 
    finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }

  /**
   * Validates the shipping group's shipping method.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public void preUpdateShippingMethod(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    tenderCoupon(pRequest, pResponse);
    
    if (getFormError()) {
      return;
    }
    // Get the shipping group
    HardgoodShippingGroup hardgoodShippingGroup = 
      (HardgoodShippingGroup) getOrder().getShippingGroups().get(0);
    
    // validate shipping method
    validateShippingMethod(hardgoodShippingGroup.getShippingAddress(), 
                           getShippingMethod(), 
                           pRequest, pResponse);
  }

  /**
   * Save changes to shipping groups.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public void updateShippingMethod(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    try {
      Order order = getOrder();

      // Just update order to save new setting for shipping groups.
      getOrderManager().updateOrder(order);

      HardgoodShippingGroup object = (HardgoodShippingGroup) order.getShippingGroups().get(0);
      object.setShippingMethod(getShippingMethod());
    }
    catch (Exception exc) {
      processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
    }
  }

  /**
   * Post edit shipping method processing.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  public void postUpdateShippingMethod(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    // Reprice order to update shipping charges.
    repriceOrder(pRequest, pResponse);

    String defaultCarrier = (String) 
      getProfile().getPropertyValue(getCommercePropertyManager().getDefaultShippingMethodPropertyName());
    
    if (StringUtils.isEmpty(defaultCarrier)) {
      List shippingGroups = getShippingGroupManager().getHardgoodShippingGroups(getOrder());
      
      if (shippingGroups != null && shippingGroups.size() > 0){
        getShippingHelper().saveDefaultShippingMethod(getProfile(), 
          ((ShippingGroup)shippingGroups.get(0)).getShippingMethod());
      }
    }
  }

  // -------------------------------------
  // Validation and Utility Mmethods
  // -------------------------------------  
  
  /**
   * Determines if the customer is attempting to ship an item to a country that is restricted.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  protected void validateShippingRestrictions(DynamoHttpServletRequest pRequest, 
                                              DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    // Validate shipping restrictions, if configured.
    if (getShippingHelper().isValidateShippingRestriction()) {
      if (isLoggingDebug()) {
        logDebug("Validating Shipping Restrictions");
      }
      try {
        //calling Shipping Restriction validate method
        List shippingValidationResult = getShippingHelper().checkShippingRestrictions(
          getCommerceItemShippingInfoContainer(), getShippingGroupMapContainer());
        
        if (shippingValidationResult != null && shippingValidationResult.size() > 0) {
          processShippingRestrictionsErrors(shippingValidationResult, pRequest, pResponse);
        }
      } 
      catch (Exception exc) {
        processException(exc, exc.getMessage(), pRequest, pResponse);
      }
    }
  }

  /**
   * Validate address nickname for uniqueness.
   *
   * @param pNickName - nickname.
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  protected void validateAddressNicknameForUniqueness(String pNickName,
                                                      DynamoHttpServletRequest pRequest, 
                                                      DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();

    if (orderTools.getProfileTools().isDuplicateAddressNickName(getProfile(), pNickName)) {
      try {
        String errorMessage = formatUserMessage(StoreShippingProcessHelper.MSG_DUPLICATE_NICKNAME,
                                                pRequest, pResponse);

        addFormException(new DropletFormException(errorMessage,
                                                  "",
                                                  StoreShippingProcessHelper.MSG_DUPLICATE_NICKNAME));
      }
      catch (Exception e) {
        processException(e, e.getMessage(), pRequest, pResponse);
      }
    }
  }

  /**
   * Validates the new address. Check for required properties, and make sure street address 
   * doesn't include PO Box, AFO/FPO.
   *
   * @param pAddress - address.
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  protected void validateShippingAddress(Address pAddress,
                                         DynamoHttpServletRequest pRequest, 
                                         DynamoHttpServletResponse pResponse)
   throws IOException, ServletException {

    ContactInfo shippingAddress = (ContactInfo) pAddress;

    List missingRequiredAddressProperties =
        getShippingHelper().checkForRequiredAddressProperties(shippingAddress);
    
    addAddressValidationFormError(missingRequiredAddressProperties, pRequest, pResponse);

    List invalidStreetPatterns = getShippingHelper().checkForInvalidStreetAddress(shippingAddress);
    
    if (invalidStreetPatterns != null && invalidStreetPatterns.size() >0) {
      
      Iterator streetrator = invalidStreetPatterns.iterator();
      
      while (streetrator.hasNext()) {
        Object[] params = { (String)streetrator.next() };
        
        String msg = 
          formatUserMessage(StoreShippingProcessHelper.MSG_INVALID_STREET_ADDRESS, params, pRequest, pResponse);
        
        addFormException(new DropletFormException(msg, null));
      }
    } 
    else {
      if ((missingRequiredAddressProperties == null || missingRequiredAddressProperties.isEmpty())){
        String country = shippingAddress.getCountry();
        String state = shippingAddress.getState();
        Place[] places = getShippingHelper().getPlaceUtils().getPlaces(country);
       
        if ((places == null && !StringUtils.isEmpty(state)) ||
            (places != null && !getShippingHelper().getPlaceUtils().isPlaceInCountry(country, state))){
          String msg = null;
          try {
            Locale userLocale = 
              getOrderManager().getOrderTools().getProfileTools().getUserLocale(pRequest, pResponse);
            
            java.util.ResourceBundle countryStateBundle = 
              atg.core.i18n.LayeredResourceBundle.getBundle(COUNTRY_STATE_RESOURCES, userLocale);
            
            String countryKey = COUNTRY_KEY_PREFIX + country;
            String countryName = countryStateBundle.getString(countryKey);
            
            msg = formatUserMessage(BillingInfoFormHandler.MSG_ERROR_INCORRECT_STATE, 
                                    countryName, 
                                    pRequest, pResponse);
            
            addFormException(new DropletFormException(msg, BillingInfoFormHandler.MSG_ERROR_INCORRECT_STATE));
          } 
          catch (Exception e) {
            if (isLoggingError()){
              logError("Error validating state", e);
            }
          }
        }
      }
    }

    try {
      getShippingHelper().validateShippingCity(shippingAddress);
    }
    catch (StorePurchaseProcessException bppe) {
      String msg = formatUserMessage(bppe.getMessage(), bppe.getParams(), pRequest, pResponse);
      addFormException(new DropletFormException(msg, null));
    }
  }

  /**
   * Validates the new address - Make sure user isn't trying to Express ship to AK, etc.
   *
   * @param pAddress - address.
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * @param pShippingMethod a shipping method.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  protected void validateShippingMethod(Address pAddress, 
                                        String pShippingMethod,
                                        DynamoHttpServletRequest pRequest, 
                                        DynamoHttpServletResponse pResponse)
    throws IOException, ServletException {
    
    try {
      getShippingHelper().validateShippingMethod(pAddress, pShippingMethod);
    } 
    catch (StorePurchaseProcessException bppe) {
      String msg = formatUserMessage(bppe.getMessage(), bppe.getParams(), pRequest, pResponse);
      addFormException(new DropletFormException(msg, null));
    }
  }

 /**
   * Validates the shipping group addresses against the provided shipping method.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  protected void validateShippingMethodForContainerShippingGroups(DynamoHttpServletRequest pRequest, 
                                                                  DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
     
    Iterator iter = getAllHardgoodCommerceItemShippingInfos().iterator();

    // Loop through all CISIs to find shipping groups with the same address but different shipping methods
    while (iter.hasNext()){
      CommerceItemShippingInfo cisi = (CommerceItemShippingInfo) iter.next();
      String shipGroupName = cisi.getShippingGroupName();
      String shippingMethod = cisi.getShippingMethod();

      // Get shipping group from container for this CISI.
      HardgoodShippingGroup shippingGroup =
        (HardgoodShippingGroup)getShippingGroupMapContainer().getShippingGroup(shipGroupName);
    
      // The shipping group is null for the gift note. 
      if (shippingGroup != null) {
        try { 
          getShippingHelper().validateShippingMethod(shippingGroup.getShippingAddress(),
                                 shippingMethod);
        }  
        catch (StorePurchaseProcessException bppe) {
          String msg = formatUserMessage(bppe.getMessage(), bppe.getParams(), pRequest, pResponse);
          boolean isDuplicate = false;
      
          Vector<Exception> exceptions = (Vector<Exception>) getFormExceptions();
          
          if ((exceptions != null) && (exceptions.size() > 0)) {
            for (Exception ex : exceptions) {
              if ((ex.getMessage() != null) && (ex.getMessage().equals(msg))) {
                isDuplicate = true;
                break;
              } 
            }  
          } 
          if (!isDuplicate) {
            addFormException(new DropletFormException(msg, null));
          }
        }
      }
    }   
  }

  /**
   * Process shipping restriction errors and add them to form exceptions.
   *
   * @param pShippingValidationResult shipping validation results.
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   */
  protected void processShippingRestrictionsErrors(List pShippingValidationResult,
                                                   DynamoHttpServletRequest pRequest, 
                                                   DynamoHttpServletResponse pResponse)
    throws IOException, ServletException {

    //TODO: How to set error messages to be displayed on UI. Check the standard followed.
    if ((pShippingValidationResult != null) && (pShippingValidationResult.size() > 0)) {
      StringTokenizer st = null;
      String productName = null;
      String countryCode = null;
      String countryName = null;
      String msg = null;

      // Tokenizing the Country String and retrieving the country, product info in 
      // given format - ProductCode | countryCode.
      for (int objNo = 0; objNo < pShippingValidationResult.size(); objNo++) {
        
        st = new StringTokenizer((String) pShippingValidationResult.get(objNo), 
                                 StoreShippingProcessHelper.COUNTRY_DELIM);
        productName = st.nextToken();
        countryCode = st.nextToken();
        
        // setting country Name corresponding Country Code
        countryName = 
          CountryRestrictionsService.getCountryName(countryCode, getUserLocale(pRequest, pResponse));

        if (isLoggingDebug()) {
          logDebug("Product [" + productName + "] cannot be shipped to Country [" + 
            countryCode + " - " + countryName + "]");
        }

        Object[] countryAndProductParams = { productName, countryName };
        msg = formatUserMessage(StoreShippingProcessHelper.MSG_RESTRICTED_SHIPPING, 
                                countryAndProductParams, 
                                pRequest, pResponse);
        addFormException(new DropletFormException(msg, null));
      }
    }
  }

  /**
   * Pulls out the CyberSource error message for invalid address.
   *
   * @param pPe - pricing exception.
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @return error message.
   */
  protected String createPricingErrorMessage(PricingException pPe, 
                                             DynamoHttpServletRequest pRequest,
                                             DynamoHttpServletResponse pResponse) {
    String msg = pPe.getMessage();

    if (msg != null) {
      if (msg.indexOf("Invalid address") != -1) {
        try {
          return formatUserMessage(StoreShippingProcessHelper.PRICING_ERROR_ADDRESS, pRequest, pResponse);
        } 
        catch (Exception e) {
          if (isLoggingError()) {
            logError(LogUtils.formatMinor(e.toString()), e);
          }
        }
      }
    }

    try {
      return formatUserMessage(StoreShippingProcessHelper.PRICING_ERROR, pRequest, pResponse);
    } 
    catch (Exception e) {
      if (isLoggingError()) {
        logError(LogUtils.formatMinor(e.toString()), e);
      }

      return "";
    }
  }

  /**
   * Utility method to add form exception.
   *
   * @param pMissingProperties - missing properties list.
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   */
  protected void addAddressValidationFormError(List pMissingProperties,
                                               DynamoHttpServletRequest pRequest, 
                                               DynamoHttpServletResponse pResponse) {
    
    if (pMissingProperties != null && pMissingProperties.size() > 0) {
      
      Map addressPropertyNameMap = getShippingHelper().getAddressPropertyNameMap();
      Iterator properator = pMissingProperties.iterator();
      
      while (properator.hasNext()) {
        String property = (String) properator.next();

        if (isLoggingDebug()) {
          logDebug("Address validation error with: " + addressPropertyNameMap.get(property) + " property.");
        }

        // This is the default message, and will only display if there is an exception 
        // getting the message from the resource bundle.
        String errorMsg = "Required properties are missing from the address.";
        
        try {
          errorMsg = formatUserMessage(StoreShippingProcessHelper.MSG_MISSING_REQUIRED_ADDRESS_PROPERTY,
                                       addressPropertyNameMap.get(property), 
                                       pRequest, pResponse);
        } catch (Exception e) {
          if (isLoggingError()) {
            logError(LogUtils.formatMinor("Error getting error string with key: " + 
              StoreShippingProcessHelper.MSG_MISSING_REQUIRED_ADDRESS_PROPERTY +
              " from resource " + PurchaseUserMessage.RESOURCE_BUNDLE + ": " + e.toString()), e);
          }
        }

        addFormException(new DropletFormException(errorMsg, (String) addressPropertyNameMap.get(property), 
          StoreShippingProcessHelper.MSG_MISSING_REQUIRED_ADDRESS_PROPERTY));
      }
    }
  }
  
  /**
   * Validate the shipping addresses on the multi-shipping page.
   * 
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  protected void validateMultiShippingAddresses(DynamoHttpServletRequest pRequest, 
                                                DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    // Retrieve the collection of all hardgood shipping groups referenced by commerce item infos.
    Collection hardgoodshippingGroups =
      getShippingHelper().getUniqueHardgoodShippingGroups(getShippingGroupMapContainer(),
                                                          getCommerceItemShippingInfoContainer());

    // Iterator through them and verify their addresses.
    Iterator sgerator = hardgoodshippingGroups.iterator();
    HardgoodShippingGroup hgsg;

    while (sgerator.hasNext()) {
      hgsg = (HardgoodShippingGroup) sgerator.next();
      validateShippingAddress(hgsg.getShippingAddress(), pRequest, pResponse);
    }
  }
 
  /**
   * Logic to re-price order, and parse any errors.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception ServletException if an error occurs.
   * @exception IOException if an error occurs.
   */
  protected void repriceOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    try {
      getShippingHelper().repriceOrder(getOrder(), 
                                       getUserPricingModels(), 
                                       getUserLocale(pRequest, pResponse), 
                                       getProfile());
    }
    catch (PricingException pe) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error w/ PricingTools.priceOrderTotal: "), pe);
      }
      String pricingMessage = createPricingErrorMessage(pe, pRequest, pResponse);
      addFormException(new DropletFormException(pricingMessage, pe, ""));
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
        String errorMessage = 
          formatUserMessage(StorePurchaseProcessHelper.MSG_UNCLAIMABLE_COUPON, pRequest, pResponse);

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
  
}
