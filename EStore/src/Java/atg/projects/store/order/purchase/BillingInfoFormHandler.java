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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.order.CreditCard;
import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.PurchaseProcessFormHandler;
import atg.commerce.order.purchase.PurchaseUserMessage;
import atg.core.i18n.PlaceList.Place;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.droplet.DropletFormException;
import atg.payment.creditcard.ExtendableCreditCardTools;
import atg.projects.store.StoreConfiguration;
import atg.projects.store.gifts.StoreGiftlistManager;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.StoreOrderManager;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.profile.StoreProfileTools;
import atg.projects.store.profile.StorePropertyManager;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;


/**
 * Form Handler for taking billing information.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/BillingInfoFormHandler.java#4 $$Change: 788278 $ 
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class BillingInfoFormHandler extends PurchaseProcessFormHandler {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/BillingInfoFormHandler.java#4 $$Change: 788278 $";

  // -------------------------------------
  // Constants
  // -------------------------------------

  public static final String COUNTRY_KEY_PREFIX = "CountryCode.";

  public static final String COUNTRY_STATE_RESOURCES = "atg.commerce.util.CountryStateResources";
  
  /**
   * Error while confirmation message key.
   */
  protected static final String MSG_ERROR_MOVE_TO_CONFIRM = "errorWithBillingInfo";

  /**
   * Error updating order message key.
   */
  protected static final String MSG_ERROR_UPDATE_ORDER = "errorUpdatingOrder";

 /**
  * Error message for incorrect state
  */
  protected static final String MSG_ERROR_INCORRECT_STATE = "stateIsIncorrect";

  // -------------------------------------
  // Properties
  // -------------------------------------
  
  private boolean mCreditCardSelectionInitialized = false;
  private boolean mBillingAddressSelectionInitialized = false;
  
  private CheckoutProgressStates mCheckoutProgressStates;

  /**
   * @return the mCheckoutProgressStates
   */
  public CheckoutProgressStates getCheckoutProgressStates()
  {
    return mCheckoutProgressStates;
  }

  /**
   * @param pCheckoutProgressStates the checkoutProgressStates to set
   */
  public void setCheckoutProgressStates(CheckoutProgressStates pCheckoutProgressStates)
  {
    mCheckoutProgressStates = pCheckoutProgressStates;
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
   * @param pCouponCode a coupon code to set
   */
  public void setCouponCode(String pCouponCode)
  {
    mCouponCode = pCouponCode;
  }

  /**
   * Property BillingHelper
   */
  private StoreBillingProcessHelper mBillingHelper;

  /**
   * @return the mBillingHelper
   */
  public StoreBillingProcessHelper getBillingHelper() {
    return mBillingHelper;
  }

  /**
   * @param pBillingHelper the billingHelper to set
   */
  public void setBillingHelper(StoreBillingProcessHelper pBillingHelper) {
    mBillingHelper = pBillingHelper;
  }

  /**
   * Property for holding the credit card verification number for a stored credit card.
   */
  protected String mCreditCardVerificationNumber;

  /**
   * @return the credit card verification number.
   */
  public String getCreditCardVerificationNumber() {
    return mCreditCardVerificationNumber;
  }

  /**
   * @param pCreditCardVerificationNumber -
   * the credit card verification number to set.
   */
  public void setCreditCardVerificationNumber(String pCreditCardVerificationNumber) {
    mCreditCardVerificationNumber = pCreditCardVerificationNumber;
  }

  /**
   * Property for holding the credit card verification number for a new credit card.
   */
  protected String mNewCreditCardVerificationNumber;

  /**
   * @return the new credit card verification number.
   */
  public String getNewCreditCardVerificationNumber() {
    return mNewCreditCardVerificationNumber;
  }

  /**
   * @param pNewCreditCardVerificationNumber -
   * the credit card verification number to set.
   */
  public void setNewCreditCardVerificationNumber(String pNewCreditCardVerificationNumber) {
    mNewCreditCardVerificationNumber = pNewCreditCardVerificationNumber;
  }

  /**
   * Should save credit card data.
   */
  protected boolean mSaveCreditCard;

  /**
   * @return true if user selected to save the credit card,
   * false - otherwise.
   */
  public boolean isSaveCreditCard() {
    return mSaveCreditCard;
  }

  /**
   * @param pSaveCreditCard -
   * if user selects to save the credit card.
   */
  public void setSaveCreditCard(boolean pSaveCreditCard) {
    mSaveCreditCard = pSaveCreditCard;
  }

  /**
   * Stores credit card name.
   */
  protected String mStoredCreditCardName;

  /**
   * @return stores credit card name.
   */
  public String getStoredCreditCardName() {
    initializeCreditCardSelection();
    return mStoredCreditCardName;
  }

  /**
   * @param pStoredCreditCardName - stored credit card name.
   */
  public void setStoredCreditCardName(String pStoredCreditCardName) {
    mStoredCreditCardName = pStoredCreditCardName;
  }

  /**
   * Stored address selection.
   */
  protected String mStoredAddressSelection;

  /**
   * @return stored address selection.
   */
  public String getStoredAddressSelection() {
    initializeBillingAddressSelection();
    return mStoredAddressSelection;
  }

  /**
   * @param pStoredAddressSelection - stored address selection.
   */
  public void setStoredAddressSelection(String pStoredAddressSelection) {
    mStoredAddressSelection = pStoredAddressSelection;
  }

  /**
   * Move to confirmation chain id.
   */
  protected String mMoveToConfirmationChainId;

  /**
   * @return move to confirmation chain id.
   */
  public String getMoveToConfirmationChainId() {
    return mMoveToConfirmationChainId;
  }

  /**
   * @param pMoveToConfirmationChainId - move
   * to confirmation chain id.
   */
  public void setMoveToConfirmationChainId(String pMoveToConfirmationChainId) {
    mMoveToConfirmationChainId = pMoveToConfirmationChainId;
  }

  /**
   * Billing Address nickname
   */
  protected String mBillingAddressNickname;

  /**
   * @return the billing address nickname.
   */
  public String getBillingAddressNickname() {
    return mBillingAddressNickname;
  }

  /**
   * @param pBillingAddressNickname -
   * the billing address nickname to set.
   */
  public void setBillingAddressNickname(String pBillingAddressNickname) {
    mBillingAddressNickname = pBillingAddressNickname;
  }

  /**
   * Minimum nickname length.
   */
  protected int mMinNickNameLength;

  /**
   * @return minimum nickname length.
   */
  public int getMinNickNameLength() {
    return mMinNickNameLength;
  }

  /**
   * @param pMinNickNameLength - minimum nickname length.
   */
  public void setMinNickNameLength(int pMinNickNameLength) {
    mMinNickNameLength = pMinNickNameLength;
  }

  /**
   * Maximum nickname length.
   */
  protected int mMaxNickNameLength;

  /**
  * @return maximum nickname length.
  */
  public int getMaxNickNameLength() {
    return mMaxNickNameLength;
  }

  /**
   * @param pMaxNickNameLength - maximum nickname length.
   */
  public void setMaxNickNameLength(int pMaxNickNameLength) {
    mMaxNickNameLength = pMaxNickNameLength;
  }

  /**
   * Credit card nickname.
   */
  protected String mCreditCardNickname;

  /**
   * @return the credit card nickname.
   */
  public String getCreditCardNickname() {
    return mCreditCardNickname;
  }

  /**
   * @param pCreditCardNickname -
   * the credit card nickname to set.
   */
  public void setCreditCardNickname(String pCreditCardNickname) {
    mCreditCardNickname = pCreditCardNickname;
  }

  
  /**
   * Credit card type
   */
  protected String mCreditCardType;

  /**
   * @return the credit card ype.
   */
  public String getCreditCardType() {
    return mCreditCardType;
  }

  /**
   * @param pCreditCardType -
   * the credit card type to set.
   */
  public void setCreditCardType(String pCreditCardType) {
    mCreditCardType = pCreditCardType;
  }
  
  /**
   * Credit card number
   */
  protected String mCreditCardNumber; 
  
    
  /**
   * @return the creditCardNumber
   */
  public String getCreditCardNumber() {
    return mCreditCardNumber;
  }

  /**
   * @param pCreditCardNumber the creditCardNumber to set
   */
  public void setCreditCardNumber(String pCreditCardNumber) {
    mCreditCardNumber = pCreditCardNumber;
  }
    
  /**
   * Credit card expiration month
   */
  protected String mCreditCardExpirationMonth; 
  
  /**
   * @return the creditCardExpirationMonth
   */
  public String getCreditCardExpirationMonth() {
    return mCreditCardExpirationMonth;
  }

  /**
   * @param pCreditCardExpirationMonth the creditCardExpirationMonth to set
   */
  public void setCreditCardExpirationMonth(String pCreditCardExpirationMonth) {
    mCreditCardExpirationMonth = pCreditCardExpirationMonth;
  }  
  
  /**
   * Credit card expiration year
   */
  protected String mCreditCardExpirationYear; 
  
  /**
   * @return the creditCardExpirationYear
   */
  public String getCreditCardExpirationYear() {
    return mCreditCardExpirationYear;
  }

  /**
   * @param pCreditCardExpirationYear the creditCardExpirationYear to set
   */
  public void setCreditCardExpirationYear(String pCreditCardExpirationYear) {
    mCreditCardExpirationYear = pCreditCardExpirationYear;
  }
    
  /**
   * Credit card billing address
   */
  protected ContactInfo mCreditCardBillingAddress = new ContactInfo();
   
  /**
   * @return the creditCardBillingAddress
   */
  public ContactInfo getCreditCardBillingAddress() {
    return mCreditCardBillingAddress;
  }

  /**
   * @param pCreditCardBillingAddress the creditCardBillingAddress to set
   */
  public void setCreditCardBillingAddress(ContactInfo pCreditCardBillingAddress) {
    mCreditCardBillingAddress = pCreditCardBillingAddress;
  }
  /**
   * Move to confirm error redirect URL.
   */
  protected String mMoveToConfirmErrorURL;

  /**
   * @return move to confirm error redirect URL.
   */
  public String getMoveToConfirmErrorURL() {
    return mMoveToConfirmErrorURL;
  }

  /**
   * @param pMoveToConfirmErrorURL - move to confirm error redirect URL.
   */
  public void setMoveToConfirmErrorURL(String pMoveToConfirmErrorURL) {
    mMoveToConfirmErrorURL = pMoveToConfirmErrorURL;
  }

  /**
   * Move to confirm success redirect URL.
   */
  protected String mMoveToConfirmSuccessURL;

  /**
   * @return move to confirm error redirect URL.
   */
  public String getMoveToConfirmSuccessURL() {
    return mMoveToConfirmSuccessURL;
  }

  /**
   * @param pMoveToConfirmSuccessURL -
   * move to confirm success redirect URL.
   */
  public void setMoveToConfirmSuccessURL(String pMoveToConfirmSuccessURL) {
    mMoveToConfirmSuccessURL = pMoveToConfirmSuccessURL;
  }

  /**
   * Store configuration.
   */
  protected StoreConfiguration mStoreConfiguration;

  /**
   * @return the store configuration.
   */
  public StoreConfiguration getStoreConfiguration() {
    return mStoreConfiguration;
  }

  /**
   * @param pStoreConfiguration - the store configuration to set.
   */
  public void setStoreConfiguration(StoreConfiguration pStoreConfiguration) {
    mStoreConfiguration = pStoreConfiguration;
  }

  /**
   * Catalog tools.
   */
  protected CatalogTools mCatalogTools;

  /**
   * @return catalog tools.
   */
  public CatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  /**
   * @param pCatalogTools - catalog tools.
   */
  public void setCatalogTools(CatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  /**
   * Is the credit card saved in the user's profile.
   */
  protected boolean mUsingProfileCreditCard = true;

  /**
   * @return Is the credit card saved in the user's profile.
   */
  public boolean isUsingProfileCreditCard() {
    initializeCreditCardSelection();
    return mUsingProfileCreditCard;
  }

  /**
   * @param pUsingProfileCreditCard - is the credit card saved in the user's profile.
   */
  public void setUsingProfileCreditCard(boolean pUsingProfileCreditCard) {
    mUsingProfileCreditCard = pUsingProfileCreditCard;
  }

  /**
   * Is the address in the user's profile.
   */
  protected boolean mUsingSavedAddress = true;

  /**
   * @return Is the address saved in the user's profile.
   */
  public boolean isUsingSavedAddress() {
    initializeBillingAddressSelection();
    return mUsingSavedAddress;
  }

  /**
   * @param pUsingSavedAddress - Is the address saved in the user's profile.
   */
  public void setUsingSavedAddress(boolean pUsingSavedAddress) {
    mUsingSavedAddress = pUsingSavedAddress;
  }

  /**
   * property: flag indicating if new billing address needs to be saved or not
   */
  private boolean mSaveBillingAddress = true;

  /**
   * @return true if billing address should be saved, otherwise false.
  */
  public boolean isSaveBillingAddress() {
    return mSaveBillingAddress;
  }

  /**
   * @param pSaveBillingAddress - true if billing address
   * should be saved, otherwise false.
   */
  public void setSaveBillingAddress(boolean pSaveBillingAddress) {
    mSaveBillingAddress = pSaveBillingAddress;
  }

  /**
   * Is order covered with store credit only.
   */
  protected boolean mUsingStoreCredit = false;

  /**
   * @return Is order covered with store credit only.
   */
  public boolean isUsingStoreCredit() {
    return mUsingStoreCredit;
  }

  /**
   * @param pUsingStoreCredit - Is order covered with store credit only.
   */
  public void setUsingStoreCredit(boolean pUsingStoreCredit) {
    mUsingStoreCredit = pUsingStoreCredit;
  }

  //-------------------------------------
  // property: creditCardTools
  private ExtendableCreditCardTools mCreditCardTools;

  /**
   *
   * @return ExtendableCreditCardTools
   */
  public ExtendableCreditCardTools getCreditCardTools() {
    return mCreditCardTools;
  }

  /**
   * @param pCreditCardTools new ExtendableCreditCardTools
   */
  public void setCreditCardTools(ExtendableCreditCardTools pCreditCardTools) {
    mCreditCardTools = pCreditCardTools;
  }
  
  // -------------------------------------
  // Public Methods
  // -------------------------------------
  
  /**
   * This method to perform any pre-initialization of StoreCredit Payment group for the order 
   * if store credit is used.
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
  public void preSetupStoreCreditPaymentGroupsForOrder(DynamoHttpServletRequest pRequest, 
                                                       DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  }

  /**
   * This method initialize and setup StoreCredit Payment group for the order if store 
   * credit is used.
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
  public void setupStoreCreditPaymentGroupsForOrder(DynamoHttpServletRequest pRequest, 
                                                    DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    try {
      // Initialize the store credit payment group.
      getBillingHelper().setupStoreCreditPaymentGroupsForOrder(getOrder(), getProfile(), 
        getBillingHelper().getStoreCreditIds(getProfile()));
    } catch (StorePurchaseProcessException exc) {

      String msg = ResourceUtils.getMsgResource(exc.getMessage(), getResourceBundleName(), 
        getResourceBundle(getUserLocale(pRequest, pResponse)), exc.getParams());
      addFormException(new DropletFormException(msg,null));

    } catch (CommerceException ce) {
      processException(ce, StoreBillingProcessHelper.STORE_CREDIT_ERROR, pRequest, pResponse);

    } catch (Exception exc) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error while processing Store Credit : "), exc);
      }
    }
  }

  /**
   * This method is post setup of store credit payment groups.
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
  public void postSetupStoreCreditPaymentGroupsForOrder(DynamoHttpServletRequest pRequest, 
                                                        DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  }

  /**
   * This method performs any operations required before the billing address is added to the 
   * credit card.
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
  public void preAddCreditCardBillingAddress(DynamoHttpServletRequest pRequest,
                                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  }

  /**
   * This method add and validates the billing Address to the credit card if order payment
   * is payed by the credit card.
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
  public void addCreditCardBillingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    try {
      boolean paymentGroupOrderRelationShipExist = getBillingHelper().isPaymentGroupOrderRelationShipExist(getCreditCard());
      // True if Order Remaining Amount is payed by Credit Card
      // False if all Order Amount is payed by other payment groups. Like OnlineCredits
      if(paymentGroupOrderRelationShipExist) {

        if(!isUsingProfileCreditCard()){
          // If the user chooses a profile address, copy it to the credit card.
          getBillingHelper().addBillingAddressToCard(getCreditCard(), isUsingSavedAddress(), 
              getStoredAddressSelection(), getProfile(), getOrder());
        }
        validateBillingAddress(pRequest, pResponse);
      }
    } catch (CommerceException ce) {
      addFormException(new DropletFormException(ce.getMessage(),null));
    }
  }

  /**
   * Carry out any operations required after a billing address is added to a
   * credit card. 
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
  public void postAddCreditCardBillingAddress(DynamoHttpServletRequest pRequest, 
                                              DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  }

  /**
   * This method performs any operations required before the credit card authorization
   * number is added.
   *
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.
   *
   * @exception ServletException
   *              if an error occurs.
   * @exception IOException
   *              if an error occurs.
   */
  public void preAddCreditCardAuthorizationNumber(DynamoHttpServletRequest pRequest, 
                                                  DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  }

  /**
   * This method add and validates the credit card authorization number the credit card if order payment
   * is payed by the credit card.
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.
   *
   * @exception ServletException
   *              if an error occurs.
   * @exception IOException
   *              if an error occurs.
   */
  public void addCreditCardAuthorizationNumber(DynamoHttpServletRequest pRequest, 
                                               DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    String creditCardVerificationNumber = null;

    if (isUsingProfileCreditCard()) {
      creditCardVerificationNumber = getCreditCardVerificationNumber();
    } else {

      creditCardVerificationNumber = getNewCreditCardVerificationNumber();
    }

    validateCreditCardAuthorizationNumber(creditCardVerificationNumber, pRequest, pResponse);
    // Ensure supplied CVV number is an non-empty numeric string
    getBillingHelper().addCreditCardAuthorizationNumber(getOrder(), creditCardVerificationNumber);
  }

  /**
   * Carry out any operations required after a credit card authorization number
   * is added.
   *
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.
   *
   * @exception ServletException
   *              if an error occurs.
   * @exception IOException
   *              if an error occurs.
   */
  public void postAddCreditCardAuthorizationNumber(DynamoHttpServletRequest pRequest, 
                                                   DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  }

  /**
   * This method performs any pre-initialization needed to setup the Credit Card Payment 
   * group for the order.
   *
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.
   * @exception ServletException
   *              if an error occurs.
   * @exception IOException
   *              if an error occurs.
   * @throws CommerceException 
   */
  public void preSetupCreditCardPaymentGroupsForOrder(DynamoHttpServletRequest pRequest, 
                                                      DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException {
    
      // Validate credit card details if the user is entering new details
      if(isUsingProfileCreditCard()) {
        //In case of saved credit card, the address is always saved
        setUsingSavedAddress(true);
      }else{
        // Validate the billing address details if the user hasn't chosen an existing address
        if (!isUsingSavedAddress()) {
          //if (!validateBillingAddress(pRequest, pResponse)) {
          //  return checkFormRedirect(null, getMoveToConfirmErrorURL(), pRequest, pResponse);
          //}
          validateBillingAddress(pRequest, pResponse);
        }
        
        setStoredCreditCardName(StoreBillingProcessHelper.NEW_CREDIT_CARD);
      }
      if(!isUsingSavedAddress()) {
        setStoredAddressSelection(StoreBillingProcessHelper.NEW_ADDRESS);
      }

      validateCreditCardInput(pRequest, pResponse);
  }

  /**
   * This method initialize and setup Credit Card Payment group for the order,
   * if store credit is used, then all amount from the order deducted by the store 
   * credit, then by credit card.
   *
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.
   *
   * @exception ServletException
   *              if an error occurs.
   * @exception IOException
   *              if an error occurs.
   */
  public void setupCreditCardPaymentGroupsForOrder(DynamoHttpServletRequest pRequest, 
                                                   DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    Locale userLocale = getUserLocale(pRequest, pResponse);
    
    try {

      if (getStoredCreditCardName() != null) {
        getBillingHelper().setupCreditCardPaymentGroupsForOrder(getOrder(), getProfile(), 
          isUsingProfileCreditCard(), getStoredCreditCardName(), userLocale);
      }
    } catch (java.security.InvalidParameterException exc) {

      // never actually thrown
      addFormException(new DropletFormException(exc.getMessage(), null));
    } catch (CommerceException ce) {

      addFormException(new DropletFormException(ce.getMessage(), null));
    }
    
    addCreditCardBillingAddress(pRequest, pResponse);
    addCreditCardAuthorizationNumber(pRequest, pResponse);
  }

  /**
   * Update checkout states with CONFIRM level. 
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
  public void postSetupCreditCardPaymentGroupsForOrder(DynamoHttpServletRequest pRequest, 
                                                       DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    addCreditCardToProfile(pRequest, pResponse);
    updateCheckoutProgressState();
  }

  /**
   * This method is for any processing required after billing info is entered.
   * It will register the user if the user is required to register, and save
   * the credit card to the profile if the user chose that option.
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
  public void preAddCreditCardToProfile(DynamoHttpServletRequest pRequest, 
                                        DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  }

  /**
   * This method is for any processing required after billing info is entered.
   * It will register the user if the user is required to register, and save
   * the credit card to the profile if the user chose that option.
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
  public void addCreditCardToProfile(DynamoHttpServletRequest pRequest, 
                                     DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    CreditCard card = getCreditCard();

    if (isSaveCreditCard() && !isUsingProfileCreditCard() ) {
      getBillingHelper().saveCreditCardToProfile(getOrder(), getProfile(), getCreditCardNickname());
    }

    if (isSaveBillingAddress()) {
      getBillingHelper().saveBillingAddressToProfile(getOrder(), getProfile(),
          getBillingAddressNickname());
    }
    
    // Make sure user has a default billing address. Use this one
    // if current billingAddress is empty.
    if(!getProfile().isTransient()){
      getBillingHelper().saveDefaultBillingAddress(getProfile(), card);
    }
  }

  /**
   * This method is for any processing required after billing info is entered.
   * It will register the user if the user is required to register, and save
   * the credit card to the profile if the user chose that option.
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
  public void postAddCreditCardToProfile(DynamoHttpServletRequest pRequest, 
                                         DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  }

  /**
   * Run the 'moveToConfirmation' pipeline.
   *
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.
   * @exception ServletException
   *              if an error occurs.
   * @exception IOException
   *              if an error occurs.
   * @throws RunProcessException
   *           if error running pipeline process.
   */
  public void runProcessMoveToConfirmation(DynamoHttpServletRequest pRequest, 
                                           DynamoHttpServletResponse pResponse)
    throws RunProcessException, ServletException, IOException {

    HashMap params = new HashMap(11);

    params.put(PipelineConstants.CATALOGTOOLS, getCatalogTools());
    params.put(PipelineConstants.INVENTORYMANAGER, 
               getOrderManager().getOrderTools().getInventoryManager());

    PipelineResult result = runProcess(getMoveToConfirmationChainId(), getOrder(), getUserPricingModels(),
                                        getUserLocale(pRequest, pResponse), getProfile(), params, null);

    processPipelineErrors(result);
  }
  
  /**
   * This method uses the CreditCardTools to validate the credit card.
   *
   * @return true if success, otherwise false.
   * 
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.
   * @exception ServletException
   *              if an error occurs.
   * @exception IOException
   *              if an error occurs.
   * @exception CommerceException
   *              indicates that a severe error occurred while performing a commerce operation.
   * 
   * @see atg.payment.creditcard.ExtendableCreditCardTools
   */
  public boolean validateCreditCardInput(DynamoHttpServletRequest pRequest, 
                                         DynamoHttpServletResponse pResponse)
    throws CommerceException, IOException, ServletException {
    
    // check to see if a credit card nickname is needed & if so if a valid one is provided
    CreditCard card = getCreditCard();
    Order order = getOrder();
    StoreOrderManager orderManager = (StoreOrderManager) getOrderManager();
    StoreOrderTools orderTools = (StoreOrderTools) orderManager.getOrderTools();

    int validationResult = -1;

    //check for empty required fields
    if (isUsingProfileCreditCard()) {
      if (StringUtils.isEmpty(getStoredCreditCardName()) || 
          StringUtils.isEmpty(getCreditCardVerificationNumber())) {

        addFormException(new DropletFormException("Required form fields are missing", 
                                                  this.getAbsoluteName(), "missingRequiredValue"));
        return false;
      }
    }
    else {
      if (!getBillingHelper().validateCreditCardRequiredFields(card, 
                                                               getNewCreditCardVerificationNumber(), 
                                                               getCreditCardNickname()) ){

        addFormException(new DropletFormException("Required form fields are missing", 
                                                  this.getAbsoluteName(), 
                                                  "missingRequiredValue"));
        return false;
      }
    }

    if (!validateCreditCardNicknameInput(pRequest, pResponse)) {
      return false;
    }

    ExtendableCreditCardTools cardTools = getCreditCardTools();

    validationResult = cardTools.verifyCreditCard(card);

    if (validationResult != cardTools.SUCCESS) {
      
      // Check if credit card validation fails and Paying with Online credits are 
      // insufficient to pay the order.
      boolean isPayingWithOnlineCredit = 
        getBillingHelper().isPayingWithOnlineCredit(getBillingHelper().getStoreCreditIds(getProfile()));
      
      List storeCredits =  orderTools.getStoreCreditPaymentGroups(order);
      
      if (isPayingWithOnlineCredit || (storeCredits != null && storeCredits.size() > 0)) {

        ResourceBundle resourceBundle = getResourceBundle(getUserLocale(pRequest, pResponse));
        String insufficientCredit = 
          ResourceUtils.getMsgResource(StoreBillingProcessHelper.ONLINE_CREDIT_INSUFFICIENT, 
                                       getResourceBundleName(),
                                       resourceBundle);
        addFormException(new DropletFormException(insufficientCredit, "common.additionalInfoRequired"));
      }

      String msg = cardTools.getStatusCodeMessage(validationResult, getUserLocale(pRequest, pResponse));
      String errorKey = Integer.toString(validationResult);
      addFormException(new DropletFormException(msg, "", errorKey));

      return false;
    }

    return true;
  }

  /**
   * This method validates the credit card nickname if one is required.
   *
   * @param pRequest - HTTP request.
   * @param pResponse - HTTP response.
   *
   * @return true if success, otherwise false.
   * 
   * @throws IOException 
   * @throws ServletException 
   */
  public boolean validateCreditCardNicknameInput(DynamoHttpServletRequest pRequest, 
                                                 DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    // Check for a valid nickname only if the save credit card option is checked and the
    // registration is taking place or the user is already logged in.
    Profile profile = (Profile) getProfile();
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
    StoreProfileTools profileTools = (StoreProfileTools) orderTools.getProfileTools();
    StorePropertyManager pmgr = (StorePropertyManager) profileTools.getPropertyManager();

    String securityProperty = pmgr.getSecurityStatusPropertyName();
    int securityStatus = ((Integer) profile.getPropertyValue(securityProperty)).intValue();

    if (securityStatus >= pmgr.getSecurityStatusLogin()) {

      if (isSaveCreditCard() && !isUsingProfileCreditCard()) {
        try {
          getBillingHelper().validateCreditCardNicknameInput(getOrder(), 
                                                             getProfile(), 
                                                             getCreditCardNickname(),
                                                             getMinNickNameLength(), 
                                                             getMaxNickNameLength());
        } catch(StorePurchaseProcessException cex) {

          String msg = ResourceUtils.getMsgResource(cex.getMessage(), 
                                                    getResourceBundleName(), 
                                                    getResourceBundle(getUserLocale(pRequest, pResponse)));
          
          addFormException(new DropletFormException(msg, cex.getMessage()));
          
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Utility method to fetch credit card and set properties from page.
   *
   * @return credit card for this order
   */
  public CreditCard getCreditCard() {
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
    return orderTools.getCreditCard(getOrder());
  }

  /**
   * Verifies that the auth number is valued and a number.
   * 
   * @param pAuthNumber - authentication number.
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.
   * @exception ServletException
   *              if an error occurs.
   * @exception IOException
   *              if an error occurs.
   */
  protected void validateCreditCardAuthorizationNumber(String pAuthNumber, 
                                                       DynamoHttpServletRequest pRequest, 
                                                       DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    if (getStoreConfiguration().isRequireCreditCardVerification()) {

      CreditCard card = getCreditCard();
      boolean paymentGroupOrderRelationShipExist = 
        getBillingHelper().isPaymentGroupOrderRelationShipExist(card);
      
      // True if Order Remaining Amount is payed by Credit Card
      // False if all Order Amount is payed by other payment groups. Like OnlineCredits
      if(paymentGroupOrderRelationShipExist) {

        boolean validAuthorizationNumber = 
          getBillingHelper().validateCreditCardAuthorizationNumber(pAuthNumber);
        if(!validAuthorizationNumber) {
          String msg = formatUserMessage(StoreBillingProcessHelper.VERIFICATION_NUMBER_INVALID, 
                                         pRequest, pResponse);

          addFormException(new DropletFormException(msg, 
                                                   (String) null, 
                                                   StoreBillingProcessHelper.VERIFICATION_NUMBER_INVALID));
        }
      }
    }
  }

  /**
   * Validates the billing address, Not Validate, if Order Amount is not payed by Credit Card.
   *
   * @param pRequest - HTTP request
   * @param pResponse - HTTP response
   * 
   * @return true if billing address is missing, otherwise false. 
   */
  protected boolean validateBillingAddress(DynamoHttpServletRequest pRequest, 
                                           DynamoHttpServletResponse pResponse) {

    List missingRequiredAddressProperties = null;
    ContactInfo billingAddress = null;
    CreditCard card = getCreditCard();

    boolean paymentGroupOrderRelationShipExist = 
      getBillingHelper().isPaymentGroupOrderRelationShipExist(card);
    
    // True if Order Remaining Amount is payed by Credit Card
    // False if all Order Amount is payed by other payment groups. Like OnlineCredits
    if(paymentGroupOrderRelationShipExist) {

      billingAddress = (ContactInfo) card.getBillingAddress();
      missingRequiredAddressProperties = 
        getBillingHelper().checkForRequiredAddressProperties(billingAddress);

      addAddressValidationFormError(missingRequiredAddressProperties, pRequest, pResponse);

      if ((missingRequiredAddressProperties == null || missingRequiredAddressProperties.isEmpty())) {
        
        String country = billingAddress.getCountry();
        String state = billingAddress.getState();
        Place[] places = getBillingHelper().getPlaceUtils().getPlaces(country);
        
        if ((places == null && !StringUtils.isEmpty(state)) ||
            (places != null && !getBillingHelper().getPlaceUtils().isPlaceInCountry(country, state))) {
          
          String msg = null;
          
          try {
            Locale userLocale = getUserLocale(pRequest, pResponse);
            java.util.ResourceBundle countryStateBundle = 
              atg.core.i18n.LayeredResourceBundle.getBundle(COUNTRY_STATE_RESOURCES,
                                                            userLocale);
            String countryKey = COUNTRY_KEY_PREFIX + country;
            String countryName = countryStateBundle.getString(countryKey);
            
            msg = formatUserMessage(MSG_ERROR_INCORRECT_STATE, countryName, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_ERROR_INCORRECT_STATE));
          } catch (Exception e) {
            if (isLoggingError()){
              logError("Error validating state", e);
            }            
          }
        }
      }
    }

    return (missingRequiredAddressProperties == null || missingRequiredAddressProperties.isEmpty());
  }

  /**
   * Move to confirmation using saved credit card.
   * 
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.
   * @return a boolean value           
   * @exception ServletException
   *              if an error occurs.
   * @exception IOException
   *              if an error occurs.
   * @throws CommerceException 
   */
  public boolean handleBillingWithSavedCard(DynamoHttpServletRequest pRequest, 
                                            DynamoHttpServletResponse pResponse)
  throws ServletException, IOException, CommerceException {
    
    setUsingProfileCreditCard(true);
    setSaveBillingAddress(false);
    setUsingSavedAddress(true);
    
    Transaction tr = null;

    try {
      tr = ensureTransaction();

      // Check if session has expired, redirect to sessionExpired URL:
      if (!checkFormRedirect(null, getMoveToConfirmErrorURL(), pRequest, pResponse)) {
        if (isLoggingDebug()) {
          logDebug("Form error at beginning of handleBillingWithSavedCard, redirecting.");
        }
        return false;
      }

      synchronized (getOrder()) {
        tenderCoupon(pRequest , pResponse);
        
        if (getFormError()) {
          return moveToConfirmExceptionHandling(pRequest, pResponse);
        }

        if (getOrder().getPriceInfo().getTotal() > 0) {
          // If order's amount is not covered by store credits
          // add credit card payment groups to order.

          //preSetupCreditCardPaymentGroupsForOrder(pRequest, pResponse);

          preBillingWithSavedCard(pRequest, pResponse);
          
          if (getFormError()) {
            return moveToConfirmExceptionHandling(pRequest, pResponse);
          }
        }

        billingWithSavedCard(pRequest, pResponse);
      }
      
      //postSetupCreditCardPaymentGroupsForOrder(pRequest, pResponse);
      postBillingWithSavedCard(pRequest, pResponse);

      // synchronized
      return checkFormRedirect(getMoveToConfirmSuccessURL(), 
                               getMoveToConfirmErrorURL(), 
                               pRequest, pResponse);
    }
    finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }
  
  /**
   * Setup credit card payment group for billing with saved card
   * and validate credit card input.
   * 
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.        
   * @exception ServletException
   *              if an error occurs.
   * @exception IOException
   *              if an error occurs.
   * @throws CommerceException 
   */
  protected void preBillingWithSavedCard(DynamoHttpServletRequest pRequest, 
                                         DynamoHttpServletResponse pResponse) 
    throws CommerceException, IOException, ServletException {
    
    setupCreditCardPaymentGroupsForOrder(pRequest, pResponse);
    validateCreditCardInput(pRequest, pResponse);
  }  
  
  /**
   * Run 'move to confirmation' pipeline chain and update order.
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
  protected void billingWithSavedCard(DynamoHttpServletRequest pRequest, 
                                      DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    processOrderBilling(pRequest, pResponse);
  }
  
  /**
   * Save billing address as default if user doesn't have one,
   * update checkout level.
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
  protected void postBillingWithSavedCard(DynamoHttpServletRequest pRequest, 
                                          DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    //addCreditCardToProfile(pRequest, pResponse);
    // Make sure user has a default billing address. Use this one
    // if current billingAddress is empty.
    getBillingHelper().saveDefaultBillingAddress(getProfile(), getCreditCard());
    
    updateCheckoutProgressState();
  }
  
  /**
   * Move to confirmation using new billing address and new credit card info.
   * 
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.
   * @return a boolean value.
   * @exception ServletException
   *              if an error occurs.
   * @exception IOException
   *              if an error occurs.
   * @throws CommerceException 
   */
  public boolean handleBillingWithNewAddressAndNewCard(DynamoHttpServletRequest pRequest, 
                                                       DynamoHttpServletResponse pResponse)
  throws ServletException, IOException, CommerceException {
    
    setUsingProfileCreditCard(false);
    setUsingSavedAddress(false);

    Transaction tr = null;

    try {
      tr = ensureTransaction();

      // Check if session has expired, redirect to sessionExpired URL:
      if (!checkFormRedirect(null, getMoveToConfirmErrorURL(), pRequest, pResponse)) {
        if (isLoggingDebug()) {
          logDebug("Form error at beginning of handleBillingWithNewAddressAndNewCard, redirecting.");
        }
        return false;
      }

      synchronized (getOrder()) {
        
        preBillingWithNewAddressAndNewCard(pRequest, pResponse);
        
        if (getFormError()) {
          return moveToConfirmExceptionHandling(pRequest, pResponse);
        }

        billingWithNewAddressAndNewCard(pRequest, pResponse);
        
        if (getFormError()) {
          return moveToConfirmExceptionHandling(pRequest, pResponse);
        }
      }
      
      postBillingWithNewAddressAndNewCard(pRequest, pResponse);
      
      if (getFormError()) {
        return moveToConfirmExceptionHandling(pRequest, pResponse);
      }

      // synchronized
      return checkFormRedirect(getMoveToConfirmSuccessURL(), 
                               getMoveToConfirmErrorURL(), 
                               pRequest, pResponse);
    }
    finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }
  
  /**
   * Setup credit card payment group and validate user input.
   * 
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.        
   * @exception ServletException
   *              if an error occurs.
   * @exception IOException
   *              if an error occurs.
   * @throws CommerceException 
   */
  protected void preBillingWithNewAddressAndNewCard(DynamoHttpServletRequest pRequest, 
                                                    DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException, CommerceException {
    
    tenderCoupon(pRequest , pResponse);

    if (getFormError()) {
      return;
    }

    if (getOrder().getPriceInfo().getTotal() > 0) {
      // if order's amount is not covered by store credits
      // add credit card payment groups to order

      fillCreditCardFieldsWithUserInput(true);
      
      preSetupCreditCardPaymentGroupsForOrder(pRequest, pResponse);
      
      if (getFormError()) {
        return;
      }

      setupCreditCardPaymentGroupsForOrder(pRequest, pResponse);
      
      if (getFormError()) {
        return;
      }

      addCreditCardBillingAddress(pRequest, pResponse);
      
      if (getFormError()) {
        return;
      }

      addCreditCardAuthorizationNumber(pRequest, pResponse);
      
      if (getFormError()) {
        return;
      }
    }
  }
  
  
  /**
   * Prefill credit card details from the fields user input.
   * 
   * @param pUsingNewAddress If new address should be used as credit card billing address.
   */
  private void fillCreditCardFieldsWithUserInput(boolean pUsingNewAddress) {
    CreditCard card = getCreditCard();
    
    card.setCreditCardNumber(getCreditCardNumber());
    card.setCreditCardType(getCreditCardType());
    card.setExpirationMonth(getCreditCardExpirationMonth());
    card.setExpirationYear(getCreditCardExpirationYear());
    
    if (pUsingNewAddress) {
      card.setBillingAddress(getCreditCardBillingAddress());
    }
    
  }

  /**
   * Run 'move to confirmation' pipeline chain and update order.
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
  protected void billingWithNewAddressAndNewCard(DynamoHttpServletRequest pRequest, 
                                                 DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    processOrderBilling(pRequest, pResponse);
  }

  /**
   * Add credit card to user profile, update checkout level.
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
  protected void postBillingWithNewAddressAndNewCard(DynamoHttpServletRequest pRequest, 
                                                     DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    addCreditCardToProfile(pRequest, pResponse);
    updateCheckoutProgressState();
  }

  /**
   * Move to confirmation using saved billing address and new credit card info.
   * 
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.
   * @return a boolean value 
   * @exception ServletException
   *              if an error occurs.
   * @exception IOException
   *              if an error occurs.
   * @throws CommerceException 
   */
  public boolean handleBillingWithSavedAddressAndNewCard(DynamoHttpServletRequest pRequest,
                                                         DynamoHttpServletResponse pResponse)
  throws ServletException, IOException, CommerceException {
    
    setUsingProfileCreditCard(false);
    setUsingSavedAddress(true);
    setSaveBillingAddress(false);

    Transaction tr = null;

    try {
      tr = ensureTransaction();

      // Check if session has expired, redirect to sessionExpired URL:
      if (!checkFormRedirect(null, getMoveToConfirmErrorURL(), pRequest, pResponse)) {
        if (isLoggingDebug()) {
          logDebug("Form error at beginning of handleBillingWithSavedAddressAndNewCard, redirecting.");
        }
        return false;
      }

      synchronized (getOrder()) {
        
        preBillingWithSavedAddressAndNewCard(pRequest, pResponse);
        
        if (getFormError()) {
          return moveToConfirmExceptionHandling(pRequest , pResponse);
        }

        billingWithNewAddressAndNewCard(pRequest, pResponse);
        
        if (getFormError()) {
          return moveToConfirmExceptionHandling(pRequest , pResponse);
        }
      }
      
      postBillingWithSavedAddressAndNewCard(pRequest, pResponse);

      // synchronized
      return checkFormRedirect(getMoveToConfirmSuccessURL(), 
                               getMoveToConfirmErrorURL(), 
                               pRequest, pResponse);
    }
    finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }
  
  /**
   * Setup credit card payment and validate user input.
   * 
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.        
   * @exception ServletException
   *              if an error occurs.
   * @exception IOException
   *              if an error occurs.
   * @throws CommerceException 
   */
  protected void preBillingWithSavedAddressAndNewCard(DynamoHttpServletRequest pRequest,
                                                      DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException, CommerceException {

    tenderCoupon(pRequest, pResponse);

    if (getFormError()) {
      return;
    }

    if (getOrder().getPriceInfo().getTotal() > 0) {
      // if order's amount is not covered by store credits
      // add credit card payment groups to order
      
      fillCreditCardFieldsWithUserInput(false);

      preSetupCreditCardPaymentGroupsForOrder(pRequest, pResponse);
      
      if (getFormError()) {
        return;
      }

      setupCreditCardPaymentGroupsForOrder(pRequest, pResponse);
      
      if (getFormError()) {
        return;
      }

      addCreditCardBillingAddress(pRequest, pResponse);
      
      if (getFormError()) {
        return;
      }

      addCreditCardAuthorizationNumber(pRequest, pResponse);
      
      if (getFormError()) {
        return;
      }
    }
  }
  
  /**
   * Run 'move to confirm' pipeline chain and update order.
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
  protected void billingWithSavedAddressAndNewCard(DynamoHttpServletRequest pRequest, 
                                                   DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    processOrderBilling(pRequest, pResponse);
  }

  /**
   * Add newly created card to profile, update checkout progress level.
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
  protected void postBillingWithSavedAddressAndNewCard(DynamoHttpServletRequest pRequest, 
                                                       DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    addCreditCardToProfile(pRequest, pResponse);
    updateCheckoutProgressState();
  }

  /**
   * Move to confirmation using only store credit as payment method.
   *
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.
   * @return a boolean value 
   * @exception ServletException
   *              if an error occurs.
   * @exception IOException
   *              if an error occurs.
   * @throws CommerceException 
   */
  public boolean handleBillingWithStoreCredit(DynamoHttpServletRequest pRequest, 
                                              DynamoHttpServletResponse pResponse)
  throws ServletException, IOException, CommerceException {
    
    setUsingStoreCredit(true);
    Transaction tr = null;

    try {
      tr = ensureTransaction();

      // Check if session has expired, redirect to sessionExpired URL:
      if (!checkFormRedirect(null, getMoveToConfirmErrorURL(), pRequest, pResponse)) {
        if (isLoggingDebug()) {
          logDebug("Form error at beginning of handleBillingWithStoreCredit, redirecting.");
        }
        return false;
      }

      synchronized (getOrder()) {
        
        preBillingWithStoreCredit(pRequest , pResponse);

        if (getFormError()) {
          return moveToConfirmExceptionHandling(pRequest, pResponse);
        }

        billingWithStoreCredit(pRequest, pResponse);
      }
      
      postBillingWithStoreCredit(pRequest, pResponse);

      // synchronized
      return checkFormRedirect(getMoveToConfirmSuccessURL(), 
                               getMoveToConfirmErrorURL(), 
                               pRequest, pResponse);
    }
    finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }
  
  /**
   * Tender coupon before billing with store credit occurs.
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
  protected void preBillingWithStoreCredit(DynamoHttpServletRequest pRequest, 
                                           DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    tenderCoupon(pRequest , pResponse);
  }
  
  /**
   * Update checkout progress level.
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
  protected void postBillingWithStoreCredit(DynamoHttpServletRequest pRequest, 
                                            DynamoHttpServletResponse pResponse) 
  throws ServletException, IOException {
  
    updateCheckoutProgressState();
  }

  /**
   * Run 'move to confirmation' pipeline chain and update order.
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
  protected void billingWithStoreCredit(DynamoHttpServletRequest pRequest,
                                        DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
  
    processOrderBilling(pRequest, pResponse);
  }

  /**
   * Apply available store credits to order.
   *
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.
   * @return true on success, otherwise false.           
   * @exception ServletException
   *              if an error occurs.
   * @exception IOException
   *              if an error occurs.
   */
  public boolean handleApplyStoreCreditsToOrder(DynamoHttpServletRequest pRequest, 
                                                DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    Transaction tr = null;
    
    try {
      tr = ensureTransaction();
      
      synchronized (getOrder()) {
        preSetupStoreCreditPaymentGroupsForOrder(pRequest, pResponse);

        if (getFormError()) {

          if (isLoggingDebug()) {
            logDebug("Failure in preSetupStoreCreditPaymentGroupsForOrder process.");
          }
          return false;
        }

        setupStoreCreditPaymentGroupsForOrder(pRequest, pResponse);
        
        if (getFormError()) {
          if (isLoggingDebug()) {
            logDebug("Failure in setupStoreCreditPaymentGroupsForOrder process.");
          }
          return false;
        }

        try {
          getOrderManager().updateOrder(getOrder());
        } catch (Exception exc) {
          processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        }
      
        postSetupStoreCreditPaymentGroupsForOrder(pRequest, pResponse);
      }
      
      return true;
    }
    finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }

  /**
   * Claim the specified coupon, register a form exception if the coupon 
   * is invalid or an error occurs.
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
      boolean couponTendered = ((StorePurchaseProcessHelper) 
        getPurchaseProcessHelper()).tenderCoupon(getCouponCode(),
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
   * Deal with error in move to confirm operation.
   * 
   * @param pRequest
   *          a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *          a <code>DynamoHttpServletResponse</code> value.
   * @return If redirect (for whatever reason) to a new page occurred, return false. 
   *         If NO redirect occurred, return true.
   * @exception ServletException
   *                if an error occurs.
   * @exception IOException
   *                if an error occurs.
   */                
  public boolean moveToConfirmExceptionHandling(DynamoHttpServletRequest pRequest, 
                                                DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    try {
      // BUG 151435 - Because the form acts directly upon the payment group object,
      // we must be sure to rollback if anything fails verification
      setTransactionToRollbackOnly();
    } catch (javax.transaction.SystemException e) {
      processException(e, MSG_ERROR_MOVE_TO_CONFIRM, pRequest, pResponse);
    }
    if (isLoggingDebug()) {
      logDebug("Error in MoveToConfirm, redirecting to error URL.");
    }
    return checkFormRedirect(null, getMoveToConfirmErrorURL(), pRequest, pResponse);
  }

  /**
   * Run pipeline chain responsible for order billing and update (save) order.
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
  protected void processOrderBilling(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    
    try {
      runProcessMoveToConfirmation(pRequest, pResponse);
    }
    catch (Exception exc) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error message from exception: "), exc);
      }

      processException(exc, MSG_ERROR_MOVE_TO_CONFIRM, pRequest, pResponse);
    }

    if (getFormError()) {
      if (isLoggingDebug()) {
        logDebug("Failure in pipeline process, returning.");
      }

      return;
    }
    
    try {
      getOrderManager().updateOrder(getOrder());
    }
    catch (Exception exc) {
      processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
    }
  }   

  /**
   * If no errors, update checkout progress state with CONFIRM level. This is needed 
   * to display confirmation page.
   */
  protected void updateCheckoutProgressState() {
    if (mCheckoutProgressStates != null && !getFormError())
    {
      mCheckoutProgressStates.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CONFIRM.toString());
    }
  }
  
  /**
   * Utility method to add form exception.
   *
   * @param pMissingProperties - missing properties list
   * @param pRequest - HTTP request
   * @param pResponse - HTTP response
   */
  public void addAddressValidationFormError(List pMissingProperties,
                                            DynamoHttpServletRequest pRequest, 
                                            DynamoHttpServletResponse pResponse) {

    if (pMissingProperties != null && pMissingProperties.size() > 0) {

      Map addressPropertyNameMap = getBillingHelper().getAddressPropertyNameMap();
      Iterator properator = pMissingProperties.iterator();
      
      while (properator.hasNext()) {

        String property = (String) properator.next();
        
        if (isLoggingDebug()) {
          logDebug("Address validation error with: " + 
                    addressPropertyNameMap.get(property) + " property.");
        }

        // This is the default message, and will only display if there is
        // an exception getting the message from the resource bundle.
        String errorMsg = "Required properties are missing from the address.";
        
        try {
          errorMsg = formatUserMessage(StoreBillingProcessHelper.MSG_MISSING_REQUIRED_ADDRESS_PROPERTY,
            addressPropertyNameMap.get(property), pRequest, pResponse);
        } catch (Exception e) {

          if (isLoggingError()) {
            logError(LogUtils.formatMinor("Error getting error string with key: " 
              + StoreBillingProcessHelper.MSG_MISSING_REQUIRED_ADDRESS_PROPERTY +
              " from resource " + PurchaseUserMessage.RESOURCE_BUNDLE + ": " + e.toString()), e);
          }
        }

        addFormException(new DropletFormException(errorMsg,
          (String) addressPropertyNameMap.get(property), 
          StoreBillingProcessHelper.MSG_MISSING_REQUIRED_ADDRESS_PROPERTY));
      }
    }
  }

  /**
   * Sets initial values to storedCreditCardName and usingProfileCreditCard
   * fields based on the order
   */
  public void initializeCreditCardSelection() {
    if ( !mCreditCardSelectionInitialized && mStoredCreditCardName == null) {
      
      StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
      StoreProfileTools profileTools = (StoreProfileTools) orderTools.getProfileTools();
      CreditCard creditCard = orderTools.getCreditCard(getOrder());
      Map creditCards = profileTools.getCreditCards(getProfile());

      // Initially set the selected credit card as the first entry in the user profile 
      // credit card map, this is superceded by the credit card associated with the order 
      // or the user profile credit card if no credit card is specified on the order.
      TreeMap sortedCreditCards = new TreeMap(creditCards);
      
      if (sortedCreditCards != null && !sortedCreditCards.isEmpty()){
        mStoredCreditCardName = (String)sortedCreditCards.firstKey();
      }
      
      if (!StringUtils.isBlank(creditCard.getCreditCardNumber() )){

        String ccNickname = profileTools.getCreditCardNickname(getProfile(), creditCard);
        
        if (ccNickname != null){
          mStoredCreditCardName = ccNickname;
        }else{
          // Stored credit card is not from profile so set usingProfileCreditCard
          // flag to false.
          setUsingProfileCreditCard(false);
        }
      }else{
        // There is no stored credit card in order, set stored credit card name to 
        // default profile's credit card.
        RepositoryItem defaultCreditCard = profileTools.getDefaultCreditCard(getProfile());
        
        if (defaultCreditCard != null){
          mStoredCreditCardName = profileTools.getCreditCardNickname(getProfile(),defaultCreditCard);
        }
      }

      mCreditCardSelectionInitialized = true;
    }
  }

  /**
   * Sets initial values to storedAddressSelection and usingSavedAddress
   * fields based on the order
   */
  public void initializeBillingAddressSelection() {
    if (!mBillingAddressSelectionInitialized && mStoredAddressSelection == null){      
      
      // ANONYMOUS USER
      if(getProfile().isTransient()) {
        setUsingSavedAddress(false);
        Map shippingGroupMap  = getShippingGroupMapContainer().getShippingGroupMap();
        
        // We don't save credit cards and we don't save addresses for anonymous users so just use the 
        // first non-gift list address in the shipping group map as the default billing address.
        if(mStoredAddressSelection == null) {
          
          TreeMap sortedAddresses = new TreeMap(shippingGroupMap);
          
          for(Object entry : sortedAddresses.entrySet()){
            
            String addressNickname = (String) ((Map.Entry)entry).getKey();
            ShippingGroup shippingGroup = (ShippingGroup) ((Map.Entry)entry).getValue();
            StoreGiftlistManager giftlistManager = (StoreGiftlistManager) getGiftlistManager();

            if( !(giftlistManager.hasGiftAddress(shippingGroup)) ) {
              mStoredAddressSelection = addressNickname;
              break;
            }
          }
        }
      }
      // REGISTERED USER
      else {
        // First try to set the address associated with the credit card on the order to the default
        // billing address for new credit cards
        StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
        StoreProfileTools profileTools = (StoreProfileTools) orderTools.getProfileTools();
        Address billingAddress = orderTools.getCreditCard(getOrder()).getBillingAddress();
        String storedAddressSelection = "";
        
        // If we have a billing address try to match it in the profiles secondaryAddresses
        if (!StringUtils.isBlank(billingAddress.getAddress1())) {
          
          String addrNickname = profileTools.getProfileAddressName(getProfile(), billingAddress);
          
          if (addrNickname != null) {
            storedAddressSelection = addrNickname;
          } 
          else {
            // Stored billing address is not from profile so set usingSavedAddress
            // flag to false
            setUsingSavedAddress(false);
          }
        }
        else {
          // There is no stored billing address in order set billing address name to default 
          // profile's billing address.
          RepositoryItem defaultBillingAddress = profileTools.getDefaultBillingAddress(getProfile());
          
          if (defaultBillingAddress != null) {
            storedAddressSelection = 
              profileTools.getProfileAddressName(getProfile(), defaultBillingAddress);
          }
        }
        
        // If the address is not present in the profile secondary address list then set the
        // selected address to the default shipping address or as the first entry in the
        // secondary address map guaranteeing we have a valid default selection.
        String addressProperty = ((StorePropertyManager) 
          orderTools.getProfileTools().getPropertyManager()).getSecondaryAddressPropertyName();
        
        Map addresses = (Map) getProfile().getPropertyValue(addressProperty);
        
        if (StringUtils.isEmpty(storedAddressSelection) || 
            !(addresses != null && addresses.containsKey(storedAddressSelection))) {

          // Check if the default shipping address is in the secondary addresses.
          storedAddressSelection = profileTools.getDefaultShippingAddressNickname(getProfile());
        
          if (addresses != null && addresses.containsKey(storedAddressSelection)) {
            mStoredAddressSelection = storedAddressSelection;
          }
          else {
            TreeMap sortedAddresses = new TreeMap(addresses);
            
            if (sortedAddresses != null && !sortedAddresses.isEmpty()) {
              mStoredAddressSelection = (String)sortedAddresses.firstKey();
            }
          }
        }
        else {
          mStoredAddressSelection = storedAddressSelection;
        }
      }
      
      mBillingAddressSelectionInitialized = true;
    }
  }
  
}
