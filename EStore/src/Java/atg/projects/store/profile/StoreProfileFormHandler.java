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
package atg.projects.store.profile;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.gifts.GiftlistTools;
import atg.commerce.gifts.InvalidDateException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.ShippingGroupMapContainer;
import atg.commerce.profile.CommerceProfileFormHandler;
import atg.commerce.profile.CommercePropertyManager;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.i18n.LocaleUtils;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.payment.creditcard.CreditCardInfo;
import atg.payment.creditcard.CreditCardTools;
import atg.payment.creditcard.ExtendableCreditCardTools;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreCommerceItemManager;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.purchase.CheckoutProgressStates;
import atg.projects.store.payment.BasicStoreCreditCardInfo;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.security.IdentityManager;
import atg.security.SecurityException;
import atg.service.util.CurrentDate;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.PropertyManager;
import atg.userprofiling.address.AddressTools;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailSender;

/**
 * Profile form handler.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/profile/StoreProfileFormHandler.java#5 $$Change: 789818 $
 * @updated $DateTime: 2013/02/12 11:50:20 $$Author: ckearney $
 *
 * @see atg.commerce.profile.CommerceProfileFormHandler
 */
public class StoreProfileFormHandler extends CommerceProfileFormHandler {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/profile/StoreProfileFormHandler.java#5 $$Change: 789818 $";
  
  //-------------------------------------
  // CONSTANTS
  //-------------------------------------

  protected static final String SECURITY_STATUS_PROPERTY_NAME = "securityStatus";
  protected static final String NO = "no";
  protected static final String YES = "yes";
  
  // Resource bundle.
  protected static final String RESOURCE_BUNDLE = "atg.commerce.profile.UserMessages";

  // Error sending email
  protected static final String MSG_ERR_SENDING_EMAIL = "errorSendingEmail";
  
  // Error creating address message key.
  protected static final String MSG_ERR_CREATING_ADDRESS = "errorCreatingAddress";

  // Error deleting address message key.
  protected static final String MSG_ERR_DELETING_ADDRESS = "errorDeletingAddress";

  // Error updating address message key.
  protected static final String MSG_ERR_UPDATING_ADDRESS = "errorUpdatingAddress";

  // Duplicate address nickname message key.
  protected static final String MSG_DUPLICATE_ADDRESS_NICKNAME = "errorDuplicateNickname";

  // Error while modifying nickname message key.
  protected static final String MSG_ERR_MODIFYING_NICKNAME = "errorModifyingNickname";

  // Error creating credit card message key.
  protected static final String MSG_ERR_CREATING_CC = "errorCreatingCreditCard";

  // Error while updating credit card message key.
  protected static final String MSG_ERR_UPDATING_CREDIT_CARD = "errorUpdatingCreditCard";

  // Missing credit card property message key.
  protected static final String MSG_MISSING_CC_PROPERTY = "missingCreditCardProperty";

  // Duplicate CC nickname message key.
  protected static final String MSG_DUPLICATE_CC_NICKNAME = "errorDuplicateCCNickname";

  // Invalid credit card message key.
  protected static final String MSG_INVALID_CC = "errorInvalidCreditCard";

  // Missing default credit card message key.
  protected static final String MSG_MISSING_DEFAULT_CC = "missingDefaultCreditCard";

  // Duplicate user message key.
  protected static final String MSG_DUPLICATE_USER = "userAlreadyExists";

  // Error while creating e-mail recipient message key.
  protected static final String MSG_ERR_CREATING_EMAIL_RECIPIENT = "errorCreatingEmailRecipient";

  // Error while removing e-mail recipient message key.
  protected  static final String MSG_ERR_REMOVING_EMAIL_RECIPIENT = "errorRemovingEmailRecipient";

  // Error while updating e-mail recipient message key.
  protected static final String MSG_ERR_UPDATING_EMAIL_RECIPIENT = "errorUpdatingEmailRecipient";

  // Invalid password message key.
  protected static final String MSG_INVALID_PASSWORD = "invalidPassword";

  // Already logged in message key.
  protected static final String MSG_ALREADY_LOGGEDIN = "invalidLoginAlreadyLoggedIn";

  // Missing e-mail message key.
  protected static final String MSG_MISSING_EMAIL = "missingEmail";

  // Error while updating status message key.
  protected static final String MSG_ERROR_UPDATE_STATUS = "errorUpdatingSecurityStatus";

  // Invalid e-mail address message key.
  protected static final String MSG_INVALID_EMAIL = "invalidEmailAddress";

  // State required message key.
  protected static final String MSG_STATE_REQUIRED = "stateRequired";

  // State is incorrect message key.
  protected static final String MSG_STATE_IS_INCORRECT = "stateIsIncorrect";

  // Invalid date format message key.
  protected static final String MSG_INVALID_DATE_FORMAT = "invalidDateFormat";
  
  // Error deleting giftlist address message key
  protected static final String MSG_ERR_DELETE_GIFT_ADDRESS = "errorDeletingGiftAddress";
  
  // Error deleting shipping address message key
  protected static final String MSG_ERR_DELETE_SHIPPING_ADDRESS = "errorDeletingShippingAddress";

  // Portal logout URL parameter.
  protected static final String PARAM_PORTAL_LOGOUT_URL = "portalLogoutSuccessURL";
  
  // Date format constant.
  protected static final String DATE_FORMAT = "M/d/yyyy";
  
  // Used in exception messages
  protected static final String EDIT_VALUE = "editValue";
    
  //-------------------------------------
  // PROPERTIES
  //-------------------------------------
  
  //-----------------------------------
  // property: checkoutProgressStates
  private CheckoutProgressStates mCheckoutProgressStates;
  
  /**
   * Get checkout progress states
   * @return the checkoutProgressStates
   */
  public CheckoutProgressStates getCheckoutProgressStates() {
    return mCheckoutProgressStates;
  }

  /**
   * Set checkout progress states
   * @param pCheckoutProgressStates the checkoutProgressStates to set
   */
  public void setCheckoutProgressStates(
      CheckoutProgressStates pCheckoutProgressStates) {
    mCheckoutProgressStates = pCheckoutProgressStates;
  }

  //-----------------------------------
  // property: sendEmailInSeparateThread
  boolean mSendEmailInSeparateThread = true;

  /**
   * Sets boolean indicating whether the email is sent in a separate thread. 
   * @param pSendEmailInSeparateThread boolean indicating whether the email is sent in a separate thread.
   * @beaninfo description: boolean indicating whether the email is sent in a separate thread.
   **/
  public void setSendEmailInSeparateThread(boolean pSendEmailInSeparateThread) {
    mSendEmailInSeparateThread = pSendEmailInSeparateThread;
  }

  /**
   * Returns boolean indicating whether the email is sent in a separate thread.
   **/
  public boolean isSendEmailInSeparateThread() {
      return mSendEmailInSeparateThread;
  }
  
  //-----------------------------------
  // property: persistEmails
  boolean mPersistEmails=false;

  /**
   * Sets boolean indicating whether the email is persisted before it is sent.  
   * @param pPersistEmails boolean indicating whether the email is persisted before it is sent. 
   * @beaninfo description: boolean indicating whether the email is persisted before it is sent. 
   **/
  public void setPersistEmails(boolean pPersistEmails) {
    mPersistEmails = pPersistEmails;
  }

  /**
   * Returns boolean indicating whether the email is persisted before it is sent. 
   **/
  public boolean isPersistEmails() {
      return mPersistEmails;
  }
  
  //-----------------------------------
  // property: templateEmailSender
  TemplateEmailSender mTemplateEmailSender = null;
  
  /**
   * Sets the property TemplateEmailSender
   */
  public void setTemplateEmailSender(TemplateEmailSender pTemplateEmailSender) {
    this.mTemplateEmailSender = pTemplateEmailSender;
  }
  
  /**
   * @return The value of the property TemplateEmailSender
   */
  public TemplateEmailSender getTemplateEmailSender() {
    return mTemplateEmailSender;
  }
  
  //-----------------------------------
  // property: templateEmailInfo
  private TemplateEmailInfo mTemplateEmailInfo;
    
  /**
   * @return the templateEmailInfo
   */
  public TemplateEmailInfo getTemplateEmailInfo() {
    return mTemplateEmailInfo;
  }

  /**
   * @param pTemplateEmailInfo the templateEmailInfo to set
   */
  public void setTemplateEmailInfo(TemplateEmailInfo pTemplateEmailInfo) {
    mTemplateEmailInfo = pTemplateEmailInfo;
  }

  //-----------------------------------
  // property: sessionBean
  private SessionBean mSessionBean;

  /**
   * @return the session bean
   */
  public SessionBean getSessionBean() {
    return mSessionBean;
  }

  /**
   * @param pSessionBean the session bean to set
   */
  public void setSessionBean(SessionBean pSessionBean) {
    mSessionBean = pSessionBean;
  }

  //-----------------------------------
  // property: giftlistManager
  private GiftlistManager mGiftlistManager;
  
  /**
   * @return the giftlist manager
   */
  public GiftlistManager getGiftlistManager() {
    return mGiftlistManager;
  }

  /**
   * @param pGiftlistManager the giftlist manager to set
   */
  public void setGiftlistManager(GiftlistManager pGiftlistManager) {
    mGiftlistManager = pGiftlistManager;
  }
  
  //-----------------------------------
  // property: nicknameValueMapKey
  private String mNicknameValueMapKey = "nickname";

  /**
   * @return A value that indicates the nickname of the address in the 
   * editValues map.
   */
  public String getNicknameValueMapKey() {
    return mNicknameValueMapKey;
  }

  /**
   * @param pNicknameValueMapKey the String to set
   */
  public void setNicknameValueMapKey(String pNicknameValueMapKey) {
    mNicknameValueMapKey = pNicknameValueMapKey;
  }

  //-----------------------------------
  // property: addressIdValueMapKey
  private String mAddressIdValueMapKey = "addressId";

  /**
   * @return The value located under this key will be used to check whether the
   * address being updated is the default address. 
   */
  public String getAddressIdValueMapKey() {
    return mAddressIdValueMapKey;
  }

  /**
   * @param pAddressIdValueMapKey the String to set
   */
  public void setAddressIdValueMapKey(String pAddressIdValueMapKey) {
    mAddressIdValueMapKey = pAddressIdValueMapKey;
  }

  //-----------------------------------
  // property: newNicknameValueMapKey
  private String mNewNicknameValueMapKey = "newNickname";

  /**
   * @return the String
   */
  public String getNewNicknameValueMapKey() {
    return mNewNicknameValueMapKey;
  }

  /**
   * @param pNewNicknameValueMapKey the String to set
   */
  public void setNewNicknameValueMapKey(String pNewNicknameValueMapKey) {
    mNewNicknameValueMapKey = pNewNicknameValueMapKey;
  }

  //-----------------------------------
  // property: shippingAddressNicknameMapKey
  private String mShippingAddressNicknameMapKey = "shippingAddrNickname";

  /**
   * @return the String
   */
  public String getShippingAddressNicknameMapKey() {
    return mShippingAddressNicknameMapKey;
  }

  /**
   * @param pShippingAddressNicknameMapKey the String to set
   */
  public void setShippingAddressNicknameMapKey(
    String pShippingAddressNicknameMapKey) 
  {
    mShippingAddressNicknameMapKey = pShippingAddressNicknameMapKey;
  }
  
  //-----------------------------------
  // property: editValue
  private Map<String, Object> mEditValue = new HashMap<String, Object>();
  
  /**
   * @return This is a map that stores the pending values for an editing 
   * operations on the B2CStore profile.
   */
  public Map<String, Object> getEditValue() {
    return mEditValue;
  }
  
  //-----------------------------------
  // property: addressProperties
  private String[] mAddressProperties = new String[] {"firstName", "middleName", 
    "lastName", "address1","address2", "city", "state", "postalCode", "country",
    "ownerId"};
  
  /**
   * Sets property addressProperties, naming the properties in a
   * secondary address record.
   * 
   * @param pAddressProperties the addressProperties to set
   **/
  public void setAddressProperties(String[] pAddressProperties) {
    mAddressProperties = pAddressProperties;
  }

  /**
   * @return List of address properties that are available in a secondaryAddress
   * (profile address).
   **/
  public String[] getAddressProperties() {
    return mAddressProperties;
  }
  
  //-----------------------------------
  // property: cardProperties
  private String[] mCardProperties = new String[] { "creditCardNumber",
      "creditCardType", "expirationMonth", "expirationYear", "billingAddress" };
  
  /**
   * Sets property cardProperties, naming the properties in a
   * credit card entry.
   * 
   * @param pCardProperties the cardProperties to set
   **/
  public void setCardProperties(String[] pCardProperties) {
    mCardProperties = pCardProperties;
  }

  /**
   * @return property cardProperties, naming the properties in a
   * credit card entry.
   **/
  public String[] getCardProperties() {
    return mCardProperties;
  }
  
  //-----------------------------------
  // property: immutableCardProperties
  private String[] mImmutableCardProperties = new String[] {"creditCardNumber",
    "creditCardType"};
  
  /**
   * Returns immutableCardProperties property value. This property contains 
   * names of credit card's immutable properties. By default CC number and it's
   * type are immutable.
   * @return immutableCardProperties 
   */
  public String[] getImmutableCardProperties() {
    return mImmutableCardProperties;
  }
  
  /**
   * Sets immutableCardProperties property value.
   * @param pImmutableCardProperties property names to be immutable.
   */
  public void setImmutableCardProperties(String[] pImmutableCardProperties) {
    mImmutableCardProperties = pImmutableCardProperties;
  }
   
  //-----------------------------------
  // property: removeAddress
  private String mRemoveAddress;

  /**
   * Sets property removeAddress, naming the address to be removed by
   * handleRemoveAddress().
   * 
   * @param pRemoveAddress the removeAddress to set
   **/
  public void setRemoveAddress(String pRemoveAddress) {
    mRemoveAddress = pRemoveAddress;
  }

  /**
   * @return property removeAddress, naming the address to be removed by
   * handleRemoveAddress().
   **/
  public String getRemoveAddress() {
    return mRemoveAddress;
  }
  
  //-----------------------------------
  // property: editAddress
  private String mEditAddress;

  /**
   * Sets property editAddress, naming the address to be edited
   * 
   * @param pEditAddress the editAddress to set
   **/
  public void setEditAddress(String pEditAddress) {
    mEditAddress = pEditAddress;
  }

  /**
   * @return property editAddress, naming the address to be edited
   **/
  public String getEditAddress() {
    return mEditAddress;
  }
  
  //-----------------------------------
  // property: removeCard
  private String mRemoveCard;

  /**
   * Sets property removeCard, naming the address to be removed by
   * handleRemoveCard().
   * 
   * @param pRemoveCard the removeCard to set
   **/
  public void setRemoveCard(String pRemoveCard) {
    mRemoveCard = pRemoveCard;
  }

  /**
   * @return property removeCard, naming the address to be removed by
   * handleRemoveCard().
   **/
  public String getRemoveCard() {
    return mRemoveCard;
  } 
  
  //-----------------------------------
  // property: dateOfBirth   
  private String mDateOfBirth = null;

  /**
   * @return date of birth.
   */
  public String getDateOfBirth() {
    if (mDateOfBirth == null && mBirthDate != null) {
      mDateOfBirth = getDateByFormat(mBirthDate, DATE_FORMAT );
    }
    
    return mDateOfBirth;
  }

  /**
   * @param pDateOfBirth Set a new date of birth.
   */
  public void setDateOfBirth(String pDateOfBirth) {
    mDateOfBirth = pDateOfBirth;
  }
 
  //-----------------------------------
  // property: month
  private String mMonth = null;

  /**
   * Sets property month.
   * @param pMonth The property to store the month value of the birth date.
   **/
  public void setMonth(String pMonth) {
    mMonth = pMonth;
  }

  /**
   * Returns property month.
   * @return The value of the property month.
   **/
  public String getMonth() { 
    StorePropertyManager propertyManager = getStorePropertyManager();
    Date dateOfBirth = (Date) getProfile().getPropertyValue(propertyManager.getDateOfBirthPropertyName());
    if(dateOfBirth != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(dateOfBirth);
      return (Integer.valueOf(cal.get(Calendar.MONTH)).toString());
    }
    if (mBirthDate != null) {
      return (Integer.valueOf(mBirthDate.get(Calendar.MONTH)).toString());
    }
    return null;
  }

  //-----------------------------------
  // property: date
  private String mDate;

  /**
   * Sets property date.
   * @param pDay The property to store the day of the month value of the birth 
   * date.
   **/
  public void setDate(String pDate) {
    mDate = pDate;
  }

  /**
   * Returns property date.
   * @return The value of the property day.
   **/
  public String getDate() {
    StorePropertyManager propertyManager = getStorePropertyManager();
    Date dateOfBirth = (Date) getProfile().getPropertyValue(propertyManager.getDateOfBirthPropertyName());
    if(dateOfBirth != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(dateOfBirth);
      return (Integer.valueOf(cal.get(Calendar.DATE)).toString());
    }
    if (mBirthDate != null) {
      return (Integer.valueOf(mBirthDate.get(Calendar.DATE)).toString());
    }
    return null;
  }

  //-----------------------------------
  // property: year
  private String mYear;

  /**
   * Sets property year.
   * @param pYear The property to store the year value of the birth date.
   **/
  public void setYear(String pYear) {
    mYear = pYear;
  }

  /**
   * Returns property year.
   * @return The value of the property year.
   **/
  public String getYear() {
    StorePropertyManager propertyManager = getStorePropertyManager();
    Date dateOfBirth = (Date) getProfile().getPropertyValue(propertyManager.getDateOfBirthPropertyName());
    if(dateOfBirth != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(dateOfBirth);
      return (Integer.valueOf(cal.get(Calendar.YEAR)).toString());
    }
    if (mBirthDate != null) {
      return (Integer.valueOf(mBirthDate.get(Calendar.YEAR)).toString());
    }
    return null;
  }
  
  //-----------------------------------
  // property: birthDate
  private Calendar mBirthDate = null;

  /**
   * Sets birthDate property.
   * @param pBirthDate The property to store the event date as a Calendar object.
   */
  public void setBirthDate(Date pBirthDate){
    if (mBirthDate == null) {
      mBirthDate = Calendar.getInstance();
    }
    mBirthDate.setTime(pBirthDate);
  }

  /**
   * Returns birthDate property.
   * @return The value of the property birthDate.
   * @exception InvalidDateException if an invalid date was entered.
   */
  public Date getBirthDate() throws InvalidDateException {
    if (mBirthDate != null) {
      return mBirthDate.getTime();
    }
    return null;
  }

  //-----------------------------------
  // property: dateFormat 
  private String mDateFormat = null;

  /**
  * @return date format.
  */
  public String getDateFormat() {
    return mDateFormat;
  }

  /**
   * @param pDateFormat - date format.
   */
  public void setDateFormat(String pDateFormat) {
    mDateFormat = pDateFormat;
  }

  //-------------------------------------
  // property: billingAddressPropertyList 
  private List mBillingAddressPropertyList = null;

  /**
   * Sets the Address property list, which is a list that mirrors the original
   * design of the AddressProperties property with the property names defined
   * in a configuration file. This List will be created by the
   * initializeAddressPropertyList method creating the appropriate list
   * containing the values from the property manager.
   *
   * @param pBillingAddressPropertyList -
   *            Billing address property list
   */
  public void setBillingAddressPropertyList(List pBillingAddressPropertyList) {
    mBillingAddressPropertyList = pBillingAddressPropertyList;
  }

  /**
   * Returns the BillingAddressPropertyList.
   *
   * @return a List that contains the Address properties that are available
   */
  public List getBillingAddressPropertyList() {
    return mBillingAddressPropertyList;
  }
  
  //-----------------------------------
  // property: defaultCard 
  private String mDefaultCard;

  /**
   * Sets property efaultCard, naming the credit card to be the default.
   *
   * @param pDefaultCard - nickname of default credit card
   */
  public void setDefaultCard(String pDefaultCard) {
    mDefaultCard = pDefaultCard;
  }

  /**
   * Returns property editCard, naming the credit card to be edited.
   *
   * @return Default credit card nickname
   */
  public String getDefaultCard() {
    return mDefaultCard;
  }
  
  //-----------------------------------
  // property: editCard 
  private String mEditCard;
  
  /**
   * Sets property editCard, naming the credit card to be edited.
   *
   * @param pEditCard - nickname of card being edited
   */
  public void setEditCard(String pEditCard) {
    mEditCard = pEditCard;
  }

  /**
   * Returns property editCard, naming the credit card to be edited.
   *
   * @return The nickname of the credit card being edited
   */
  public String getEditCard() {
    return mEditCard;
  }
  
  //-----------------------------------
  // property: billAddrValue 
  private Map mBillAddrValue = new HashMap();

  /**
   * @return The value of the property EditValue. This is a map that stores
   *         the pending values for an editing operations on the B2CStore
   *         profile.
   */
  public Map getBillAddrValue() {
    return mBillAddrValue;
  }

  //-----------------------------------
  // property: defaultShippingAddress 
  private String mDefaultShippingAddress;

  /**
   * @param pDefaultShippingAddress - default shipping address.
   */
  public void setDefaultShippingAddress(String pDefaultShippingAddress) {
    mDefaultShippingAddress = pDefaultShippingAddress;
  }

  /**
   * @return default shipping address.
   */
  public String getDefaultShippingAddress() {
    return mDefaultShippingAddress;
  }
  
  //-----------------------------------
  // property: defaultShippingAddress 
  private boolean mUseShippingAddressAsDefault;

  /**
   * @return mUseShippingAddressAsDefault
   */
  public boolean isUseShippingAddressAsDefault() {
    return mUseShippingAddressAsDefault;
  }

  /**
   * @param pUseShippingAddressAsDefault the boolean value to set
   */
  public void setUseShippingAddressAsDefault(boolean pUseShippingAddressAsDefault) {
    mUseShippingAddressAsDefault = pUseShippingAddressAsDefault;
  }

  //-----------------------------------
  // property: sourceCode 
  private String mSourceCode;

  /**
   * @return source code.
   */
  public String getSourceCode() {
    return mSourceCode;
  }

  /**
   * @param pSourceCode - source code.
   */
  public void setSourceCode(String pSourceCode) {
    mSourceCode = pSourceCode;
  }
  
  //-----------------------------------
  // property: loginEmailAddress 
  private String mLoginEmailAddress;

  /**
   * @return login e-mail address.
   */
  public String getLoginEmailAddress() {
    return mLoginEmailAddress;
  }

  /**
   * @param pLoginEmailAddress - login e-mail address.
   */
  public void setLoginEmailAddress(String pLoginEmailAddress) {
    mLoginEmailAddress = pLoginEmailAddress;
  }
  
  //-----------------------------------
  // property: emailAddress 
  private String mEmailAddress;

  /**
   * @return e-mail address.
   */
  public String getEmailAddress() {
    return mEmailAddress;
  }

  /**
   * @param pEmailAddress - e-mail address.
   */
  public void setEmailAddress(String pEmailAddress) {
    mEmailAddress = pEmailAddress;
  }
  
  //-----------------------------------
  // property: NewCustomerEmailAddress
  private String mNewCustomerEmailAddress;

  /**
   * @return new customer email address
   */
  public String getNewCustomerEmailAddress() {
    return mNewCustomerEmailAddress;
  }

  /**
   * @param pNewCustomerEmailAddress email address
   */
  public void setNewCustomerEmailAddress(String pNewCustomerEmailAddress) {
    mNewCustomerEmailAddress = pNewCustomerEmailAddress;
  }
  
  //-----------------------------------
  // property: AnonymousEmailAddress
  private String mAnonymousEmailAddress;

  /**
   * @return anonymous customer email address
   */
  public String getAnonymousEmailAddress() {
    return mAnonymousEmailAddress;
  }

  /**
   * @param pAnonymousEmailAddress email address
   */
  public void setAnonymousEmailAddress(String pAnonymousEmailAddress) {
    mAnonymousEmailAddress = pAnonymousEmailAddress;
  }
  
  //-----------------------------------
  // property: order 
  private Order mOrder;

  /**
   * Set the Order property.
   *
   * @param pOrder an <code>Order</code> value
   */
  public void setOrder(Order pOrder) {
    mOrder = pOrder;
  }

  /**
   * Return the Order property.
   *
   * @return an <code>Order</code> value
   */
  public Order getOrder() {
    if (mOrder != null) {
      return mOrder;
    } else {
      return getShoppingCart().getCurrent();
    }
  }
 
  //-----------------------------------
  // property: addCommerceItemInfos   
  private List mAddCommerceItemInfos;

  /**
   * @param pAddCommerceItemInfos - add commerce item
   * information.
   */
  public void setAddCommerceItemInfos(List pAddCommerceItemInfos) {
    mAddCommerceItemInfos = pAddCommerceItemInfos;
  }

  /**
   * @return add commerce item information.
   */
  public List getAddCommerceItemInfos() {
    return mAddCommerceItemInfos;
  }
  
  //-----------------------------------
  // property: requiredBillingAddressPropertyList  
  private List mRequiredBillingAddressPropertyList;

  /**
   * @return required billing address property list.
   */
  public List getRequiredBillingAddressPropertyList() {
    return mRequiredBillingAddressPropertyList;
  }

  /**
   * @param pRequiredBillingAddressPropertyList -
   * required billing address property list.
   */
  public void setRequiredBillingAddressPropertyList(
    List pRequiredBillingAddressPropertyList) 
  {
    mRequiredBillingAddressPropertyList = pRequiredBillingAddressPropertyList;
  }
  
  //-------------------------------------
  // property: emailOptIn   
  private boolean mEmailOptIn;

  /**
   * @return true if e-mail opt in is turned on,
   * false - otherwise.
   */
  public boolean isEmailOptIn() {
    return mEmailOptIn;
  }

  /**
   * @param pEmailOptIn - true to turn on e-mail opt in, false - otherwise.
   */
  public void setEmailOptIn(boolean pEmailOptIn) {
    mEmailOptIn = pEmailOptIn;
  }
  
  //-------------------------------------
  // property: previousOptInStatus   
  private boolean mPreviousOptInStatus;

  /**
   * @return previous opt in status.
   */
  public boolean isPreviousOptInStatus() {
    return mPreviousOptInStatus;
  }

  /**
   * @param pPreviousOptInStatus - previous opt in status.
   */
  public void setPreviousOptInStatus(boolean pPreviousOptInStatus) {
    mPreviousOptInStatus = pPreviousOptInStatus;
  }
  
  //-----------------------------------
  // property: previousEmailAddress   
  private String mPreviousEmailAddress;

  /**
   * @return previous email address.
   */
  public String getPreviousEmailAddress() {
    return mPreviousEmailAddress;
  }

  /**
   * @param pPreviousEmailAddress - previous email address.
   */
  public void setPreviousEmailAddress(String pPreviousEmailAddress) {
    mPreviousEmailAddress = pPreviousEmailAddress;
  }
  
  //-----------------------------------
  // property: creditCardTools
  private ExtendableCreditCardTools mCreditCardTools;
  
  /**
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

  //-----------------------------------
  // property: newAddressSuccessURL
  private String mNewAddressSuccessURL;

  /**
   * Sets property newAddressSuccessURL, used to redirect user after
   * successfully creating an address.
   * 
   * @param pNewAddressSuccessURL the newAddressSuccessURL to set
   **/
  public void setNewAddressSuccessURL(String pNewAddressSuccessURL) {
    mNewAddressSuccessURL = pNewAddressSuccessURL;
  }

  /**
   * @return property newAddressSuccessURL, used to redirect user after
   * successfully creating an address.
   **/
  public String getNewAddressSuccessURL() {
    return mNewAddressSuccessURL;
  }

  //-----------------------------------
  // property: newAddressErrorURL
  private String mNewAddressErrorURL;

  /**
   * Sets property newAddressErrorURL, used to redirect user in
   * case of an error creating an address.
   * 
   * @param pNewAddressErrorURL the newAddressErrorURL to set
   **/
  public void setNewAddressErrorURL(String pNewAddressErrorURL) {
    mNewAddressErrorURL = pNewAddressErrorURL;
  }

  /**
   * @return property newAddressErrorURL, used to redirect user when theere was
   * an error creating an address.
   **/
  public String getNewAddressErrorURL() {
    return mNewAddressErrorURL;
  }

  //-----------------------------------
  // property: updateAddressSuccessURL
  private String mUpdateAddressSuccessURL;

  /**
   * Sets property updateAddressSuccessURL, used to redirect user when
   * an address is successfully updated.
   * 
   * @param pUpdateAddressSuccessURL the updateAddressSuccessURL to set
   **/
  public void setUpdateAddressSuccessURL(String pUpdateAddressSuccessURL) {
    mUpdateAddressSuccessURL = pUpdateAddressSuccessURL;
  }

  /**
   * @return property updateAddressSuccessURL, used to redirect user when
   * an address is successfully updated.
   **/
  public String getUpdateAddressSuccessURL() {
    return mUpdateAddressSuccessURL;
  }

  //-----------------------------------
  // property: updateAddressErrorURL
  private String mUpdateAddressErrorURL;

  /**
   * Sets property updateAddressErrorURL, used to redirect user in
   * case of an error updating an address.
   * 
   * @param pUpdateAddressErrorURL the updateAddressErrorURL to set
   **/
  public void setUpdateAddressErrorURL(String pUpdateAddressErrorURL) {
    mUpdateAddressErrorURL = pUpdateAddressErrorURL;
  }

  /**
   * @return property updateAddressErrorURL, used to redirect user in
   * case of an error updating an address.
   **/
  public String getUpdateAddressErrorURL() {
    return mUpdateAddressErrorURL;
  }
  
  //-----------------------------------
  // property: createCardSuccessURL
  private String mCreateCardSuccessURL;

  /**
   * Sets property createCardSuccessURL, used to redirect user if
   * a new credit card was successfully added.
   * 
   * @param pCreateCardSuccessURL the createCardSuccessURL to set
   **/
  public void setCreateCardSuccessURL(String pCreateCardSuccessURL) {
    mCreateCardSuccessURL = pCreateCardSuccessURL;
  }

  /**
   * @return property createCardSuccessURL, used to redirect user if
   * a new credit card was successfully added.
   **/
  public String getCreateCardSuccessURL() {
    return mCreateCardSuccessURL;
  }

  //-----------------------------------
  // property: createCardErrorURL
  private String mCreateCardErrorURL;

  /**
   * Sets property createCardErrorURL, used to redirect user in
   * case of an error adding a new credit card.
   * 
   * @param pCreateCardErrorURL the createCardErrorURL to set
   **/
  public void setCreateCardErrorURL(String pCreateCardErrorURL) {
    mCreateCardErrorURL = pCreateCardErrorURL;
  }

  /**
   * @return property createCardErrorURL, used to redirect user in
   * case of an error adding a new credit card.
   **/
  public String getCreateCardErrorURL() {
    return mCreateCardErrorURL;
  }
  
  //-----------------------------------
  // property: updateCardSuccessURL 
  private String mUpdateCardSuccessURL;
  
  /**
   * Sets property updateCardSuccessURL.
   *
   * @param pUpdateCardSuccessURL -
   *            credit card update success URL
   */
  public void setUpdateCardSuccessURL(String pUpdateCardSuccessURL) {
    mUpdateCardSuccessURL = pUpdateCardSuccessURL;
  }

  /**
   * Returns property updateCardSuccessURL.
   * @return mUpdateCardSuccessURL
   */
  public String getUpdateCardSuccessURL() {
    return mUpdateCardSuccessURL;
  }
  
  //-----------------------------------
  // property: updateCardErrorURL 
  private String mUpdateCardErrorURL;
  
  /**
   * Sets property updateCardErrorURL, used to redirect user in case of an
   * error updating a new credit card.
   *
   * @param pUpdateCardErrorURL - credit card update error URL
   */
  public void setUpdateCardErrorURL(String pUpdateCardErrorURL) {
    mUpdateCardErrorURL = pUpdateCardErrorURL;
  }

  /**
   * Returns property updateCardErrorURL, used to redirect user in case of an
   * error updating a new credit card.
   * @return update credit card error URL
   */
  public String getUpdateCardErrorURL() {
    return mUpdateCardErrorURL;
  }

  //-----------------------------------
  // property: removeCardSuccessURL
  private String mRemoveCardSuccessURL;

  /**
   * Sets property removeCardSuccessURL, used to redirect user when
   * a credit card is successfully removed.
   * 
   * @param pRemoveCardSuccessURL the removeCardSuccessURL to set
   **/
  public void setRemoveCardSuccessURL(String pRemoveCardSuccessURL) {
    mRemoveCardSuccessURL = pRemoveCardSuccessURL;
  }

  /**
   * @return property removeCardSuccessURL, used to redirect user in
   * a credit card is successfully removed.
   **/
  public String getRemoveCardSuccessURL() {
    return mRemoveCardSuccessURL;
  }

  //-----------------------------------
  // property: removeCardErrorURL
  private String mRemoveCardErrorURL;

  /**
   * Sets property RemoveCardErrorURL, used to redirect user in
   * case of an error removing a credit card.
   * 
   * @param pRemoveCardErrorURL the RemoveCardErrorURL to set
   **/
  public void setRemoveCardErrorURL(String pRemoveCardErrorURL) {
    mRemoveCardErrorURL = pRemoveCardErrorURL;
  }

  /**
   * @return property RemoveCardErrorURL, used to redirect user in
   * case of an error removing a credit card.
   **/
  public String getRemoveCardErrorURL() {
    return mRemoveCardErrorURL;
  }  

  //-----------------------------------
  // property: defaultCardSuccessURL
  private String mDefaultCardSuccessURL;

  /**
   * Sets property DefaultCardSuccessURL, used to redirect user when
   * a credit card is successfully removed.
   * 
   * @param pDefaultCardSuccessURL the DefaultCardSuccessURL to set
   **/
  public void setDefaultCardSuccessURL(String pDefaultCardSuccessURL) {
    mDefaultCardSuccessURL = pDefaultCardSuccessURL;
  }

  /**
   * @return property DefaultCardSuccessURL, used to redirect user in
   * a credit card is successfully removed.
   **/
  public String getDefaultCardSuccessURL() {
    return mDefaultCardSuccessURL;
  }

  //-----------------------------------
  // property: defaultCardErrorURL
  private String mDefaultCardErrorURL;

  /**
   * Sets property DefaultCardErrorURL, used to redirect user in
   * case of an error removing a credit card.
   * 
   * @param pDefaultCardErrorURL the DefaultCardErrorURL to set
   **/
  public void setDefaultCardErrorURL(String pDefaultCardErrorURL) {
    mDefaultCardErrorURL = pDefaultCardErrorURL;
  }

  /**
   * @return property DefaultCardErrorURL, used to redirect user in
   * case of an error removing a credit card.
   **/
  public String getDefaultCardErrorURL() {
    return mDefaultCardErrorURL;
  } 
  
  //-----------------------------------
  // property: preRegisterSuccessURL
  private String mPreRegisterSuccessURL;
      
  /**
   * @return the mPreRegisterSuccessURL
   */
  public String getPreRegisterSuccessURL() {
    return mPreRegisterSuccessURL;
  }

  /**
   * @param pPreRegisterSuccessURL the preRegisterSuccessURL to set
   */
  public void setPreRegisterSuccessURL(String pPreRegisterSuccessURL) {
    mPreRegisterSuccessURL = pPreRegisterSuccessURL;
  }

  //-----------------------------------
  // property: preRegisterErrorURL
  private String mPreRegisterErrorURL;
  
  /**
   * @return the mPreRegisterErrorURL
   */
  public String getPreRegisterErrorURL() {
    return mPreRegisterErrorURL;
  }

  /**
   * @param pPreRegisterErrorURL the preRegisterErrorURL to set
   */
  public void setPreRegisterErrorURL(String pPreRegisterErrorURL) {
    mPreRegisterErrorURL = pPreRegisterErrorURL;
  }
  
  //-----------------------------------
  // property: shippingGroupMapContainer
  private ShippingGroupMapContainer mShippingGroupMapContainer;

  /**
   * Set the ShippingGroupMapContainer property.
   * @param pShippingGroupMapContainer a <code>ShippingGroupMapContainer</code> value
   */
  public void setShippingGroupMapContainer(ShippingGroupMapContainer pShippingGroupMapContainer) {
    mShippingGroupMapContainer = pShippingGroupMapContainer;
  }

  /**
   * Return the ShippingGroupMapContainer property.
   * @return a <code>ShippingGroupMapContainer</code> value
   */
  public ShippingGroupMapContainer getShippingGroupMapContainer() {
    return mShippingGroupMapContainer;
  }

  //-----------------------------------
  // property: recentlyViewedProducts
  private List<RepositoryItem> mRecentlyViewedProducts = null;
  
  /**
   * Sets a recently viewed products list. 
   */
  public void setRecentlyViewedProducts(List<RepositoryItem> pRecentlyViewedProducts) {
    mRecentlyViewedProducts = pRecentlyViewedProducts;
  }
  
  /**
   * Gets a recently viewed products list.
   */
  public List<RepositoryItem> getRecentlyViewedProducts() {
    return mRecentlyViewedProducts;
  }

  //-----------------------------------
  // property: currentDate
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
  
  //-----------------------------------
  // property: newAddressId
  private String mNewAddressId;

  /**
   * @return The id of the new address created by this form handler.
   */
  public String getNewAddressId() {
    return mNewAddressId;
  }

  /**
   * @param pNewAddressId Set the id of the newly created address.
   */
  public void setNewAddressId(String pNewAddressId) {
    mNewAddressId = pNewAddressId;
  }

  //-------------------------------------
  // METHODS
  //-------------------------------------

  /**
   * Override OOTB method so that auto-logged in user is not logged out if
   * they provide an invalid password.
   * {@inheritDoc}
   *
   * @see atg.userprofiling.ProfileForm#preLoginUser(atg.servlet.DynamoHttpServletRequest,
   *                                                 atg.servlet.DynamoHttpServletResponse)
   */
  protected void preLoginUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {

    // if the there's already a user logged in, see if this is the same user
    Profile profile = getProfile();

    if (!profile.isTransient()) {
      ProfileTools profileTools = getProfileTools();
      PropertyManager propertyManager = profileTools.getPropertyManager();

      // get the submitted form's login/pwd and hash the pwd
      String loginPropertyName = propertyManager.getLoginPropertyName();
      String login = getStringValueProperty(loginPropertyName).trim().toLowerCase();
      String pwdPropertyName = propertyManager.getPasswordPropertyName();
      String pwd = getStringValueProperty(pwdPropertyName).trim();
      String prLogin = profile.getPropertyValue(loginPropertyName).toString();

      boolean authSuccessful;
      IdentityManager identityManager = getUserLoginManager().getIdentityManager(pRequest);

      try {
        if (isLoggingDebug()) {
          logDebug("Checking user auth with login: " + login + " and pwd: " + pwd);
        }

        authSuccessful = identityManager.checkAuthenticationByPassword(login, pwd);

        if (isLoggingDebug()) {
          logDebug("Auth success: " + authSuccessful);
        }
      } catch (SecurityException e) {
        if (isLoggingDebug()) {
          logDebug("Exception during auth.");
        }

        authSuccessful = false;
      }
      
      // Compare logins ignoring case as logins are considered to be case-insensitive
      if (authSuccessful) {
        if (login.equalsIgnoreCase(prLogin)) {
          boolean addError = true;
  
          // user matches, check the security status,
          // if there's a security status property
          if (profileTools.isEnableSecurityStatus()) {
            int securityStatus = -1;
            String securityStatusPropertyName = propertyManager.getSecurityStatusPropertyName();
            Object ss = profile.getPropertyValue(securityStatusPropertyName);
  
            if (ss != null) {
              securityStatus = ((Integer) ss).intValue();
            }
  
            // See if the user is auto-logged in. If so, this could
            // indicate that this form is re-authenticating the user
            // for access to sensitive content
            addError = !getProfileTools().isAutoLoginSecurityStatus(securityStatus);
  
            // try to reset to securityLogin if it's not set already
            try {
              if (securityStatus != propertyManager.getSecurityStatusLogin()) {
                profileTools.setLoginSecurityStatus(profile, pRequest);
              }
            } catch (RepositoryException exc) {
              addFormException(MSG_ERROR_UPDATE_STATUS, exc, pRequest);
  
              if (isLoggingError()) {
                logError(LogUtils.formatMinor(exc.toString()), exc);
              }
            }
          }
  
          // Only do this if the user is not auto-logged in
          if (addError) {
            // in any event, user's already logged in...          
            Object[] args = new Object[] { login };
            addFormException(MSG_ALREADY_LOGGEDIN, args, getAbsoluteName(), pRequest);          
          }
        } else {
          // User logged in, but tries to re-login as another person. Display an error to him/her.
          // We can re-use this message code, because login page will not be displayed to logged in users anymore
          // hence this method will never be invoked on logged in user
          addFormException(MSG_ALREADY_LOGGEDIN, new Object[] {login}, getAbsoluteName(), pRequest);
        }
      } else {
        // submitted login/pwd do NOT match those of existing session,
        // add form exception
        addFormException(MSG_INVALID_PASSWORD, getAbsoluteName(), pRequest);
      }
    }
    else {
      // Hold a reference to the anonymous user's recently viewed products list. We will
      // use this list to populate the user's recently viewed list in the postLoginUser
      // method if they log in successfully. 
      setRecentlyViewedProducts(
        ((StoreProfileTools)getProfileTools()).getRecentlyViewedTools().getProducts(profile));
    }
  }


  /**
   * Update the logged in user's recentlyViewedProducts property with products
   * viewed as an anonymous user.
   * 
   * @throws ServletException If there was a problem updating the user's list.
   */
  protected void postLoginAddRecentlyViewed(DynamoHttpServletRequest pRequest, 
                                            DynamoHttpServletResponse pResponse) throws ServletException {
    
    List<RepositoryItem> recentlyViewedProducts = getRecentlyViewedProducts();
    MutableRepositoryItem profile = (MutableRepositoryItem) ServletUtil.getCurrentUserProfile();
    
    if ((recentlyViewedProducts != null) && (recentlyViewedProducts.size() > 0)) {
      try {
        ((StoreProfileTools)getProfileTools()).getRecentlyViewedTools().addProductsToList(recentlyViewedProducts, profile);
      } 
      catch (RepositoryException re) {
        if (isLoggingError()) {
          logError("There was a problem adding anonymous user's recentlyViewedProducts to profile: " +
              profile.getRepositoryId(), re);
        }
      }
      finally {
        setRecentlyViewedProducts(null);
      }
    }
  }


  /**
   * This method adds product into giftlist.
   * All neccessary IDs (product ID, SKU ID, giftlist ID etc.) are taken from appropriate <code>values</code> properties of the
   * <code>SessionBean</code> component.
   * @throws ServletException if unable to add item to giftlist
   */
  private void postLoginAddItemToGiftlist() throws ServletException {
    String skuId = (String) getSessionBean().getValues().get(SessionBean.SKU_ID_TO_GIFTLIST_PROPERTY_NAME);
    String productId = (String) getSessionBean().getValues().get(SessionBean.PRODUCT_ID_TO_GIFTLIST_PROPERTY_NAME);
    String giftListId = (String) getSessionBean().getValues().get(SessionBean.GIFTLIST_ID_PROPERTY_NAME);
    giftListId = giftListId != null ? giftListId :
        ((RepositoryItem) getProfile().getPropertyValue(getStorePropertyManager().getWishlistPropertyName())).getRepositoryId();
    String commerceItemId = (String) getSessionBean().getValues().get(SessionBean.COMMERCE_ITEM_ID_PROPERTY_NAME);
    Long quantity = (Long) getSessionBean().getValues().get(SessionBean.QUANTITY_TO_ADD_TO_GIFTLIST_PROPERTY_NAME);
    
    try
    {
      if (skuId != null && productId != null)
      {
        if (giftListId == null){
          // set to profile wish list
          giftListId = ((RepositoryItem) getProfile().getPropertyValue(getStorePropertyManager().getWishlistPropertyName()))
              .getRepositoryId();
        }
        long qty = 1L;
        if (quantity != null){
          qty = quantity.longValue();
        }
        getGiftlistManager().addCatalogItemToGiftlist(skuId, productId, giftListId, SiteContextManager.getCurrentSiteId(), qty);
        
        if (!StringUtils.isEmpty(commerceItemId)){
          // complete moving of item from cart to wish list, remove item from cart.
          // First, find it by catalogRefId, productId and siteId.
          String siteId = (String) getSessionBean().getValues().get(SessionBean.SITE_ID_PROPERTY_NAME);
          CommerceItem itemToRemove = ((StoreCommerceItemManager) getOrderManager().getCommerceItemManager()).
              getCommerceItem(getOrder(), skuId, productId, siteId);
          // If there is such an item, remove it.
          if (itemToRemove != null) {
            getOrderManager().getCommerceItemManager().removeItemFromOrder(getOrder(), itemToRemove.getId());
            getOrderManager().updateOrder(getOrder());
          }
        }
      }
    } catch (CommerceException e)
    {
      throw new ServletException(e);
    } finally
    {
      clearLoginPropertiesFromSession();
    }
  }
  
  /**
   * After logging in the user's session cached promotions are reloaded into 
   * the PricingModelHolder. In addition any non-transient orders are made persistent 
   * and old shopping carts are loaded from the database.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @throws ServletException - if there was an error while executing the code
   * @throws IOException - if there was an error with servlet io
   */
  @SuppressWarnings("unchecked") //ok, we know what to get and put from SessionBean values
  protected void postLoginUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
      IOException
  {
    super.postLoginUser(pRequest, pResponse);
    
    String loginSuccess = pRequest.getParameter(HANDLE_LOGIN);
    if (HANDLE_SUCCESS.toString().equals(loginSuccess))
    {
      String redirectUrl = (String) getSessionBean().getValues().get(SessionBean.REDIRECT_AFTER_LOGIN_URL_PROPERTY_NAME);
      if (redirectUrl != null)
      {
        setLoginSuccessURL(redirectUrl);
      }
      
      postLoginAddItemToGiftlist();
      // Update the logged in user's recently viewed list with products viewed
      // when they were using an anonymous profile (if any). 
      postLoginAddRecentlyViewed(pRequest, pResponse);
    }
  }
  
  //---------------------------------------
  // handleLogoutUser related methods 
  //---------------------------------------
  
  /**
   * Operation called just after the user is logged out.
   *
   * @param pRequest
   *            the servlet's request
   * @param pResponse
   *            the servlet's response
   * @exception ServletException
   *                if there was an error while executing the code
   * @exception IOException
   *                if there was an error with servlet io
   */
  public void postLogoutUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {
    if (isLoggingDebug()) {
      logDebug("User status before postLogout: " + getProfile().getPropertyValue(SECURITY_STATUS_PROPERTY_NAME));
      logDebug("User id before postLogout: " + getProfile().getRepositoryId());
      logDebug("Order id before postLogout: " + getOrder().getId());
      logDebug("before: " + getLogoutSuccessURL());
    }

    String portalLogoutSuccessURL = pRequest.getParameter(PARAM_PORTAL_LOGOUT_URL);

    if (portalLogoutSuccessURL != null) {
      setLogoutSuccessURL(portalLogoutSuccessURL);
    }

    super.postLogoutUser(pRequest, pResponse);

    if (isLoggingDebug()) {
      logDebug("after: " + getLogoutSuccessURL());
      logDebug("User status after postLogout: " + getProfile().getPropertyValue(SECURITY_STATUS_PROPERTY_NAME));
      logDebug("User id after postLogout: " + getProfile().getRepositoryId());
      logDebug("Order id after postLogout: " + getOrder().getId());
    }
  }
  
  //---------------------------------------
  // handleCreateUser related methods 
  //---------------------------------------

  /**
   * Operation called just before the user creation process is started.
   *
   * @param pRequest
   *            the servlet's request
   * @param pResponse
   *            the servlet's response
   * @exception ServletException
   *                if there was an error while executing the code
   * @exception IOException
   *                if there was an error with servlet io
   */
  protected void preCreateUser(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {
    super.preCreateUser(pRequest, pResponse);

    //validation
    validateEmail(pRequest);

    // update properties that didn't update directly
    updateDateOfBirthProperty(pRequest);
    updateReceiveEmailProperty();

    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();

    // Copy over user email to their login field
    StorePropertyManager propertyManager = (StorePropertyManager) profileTools.getPropertyManager();
    String emailPropertyName = propertyManager.getEmailAddressPropertyName();
    String email = getStringValueProperty(emailPropertyName);

    // Store login in lower case to support case-insensitive logins
    String loginPropertyName = propertyManager.getLoginPropertyName();
    setValueProperty(loginPropertyName, email.toLowerCase());
  }

  /**
   * Validates email address
   * 
   * @param pRequest the servlet's request
   */
  private void validateEmail(DynamoHttpServletRequest pRequest) {
    String email = getStringValueProperty(getStorePropertyManager().getEmailAddressPropertyName());
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    
    if (StringUtils.isBlank(email) || !profileTools.validateEmailAddress(email)) {
      addFormException(MSG_INVALID_EMAIL, getAbsoluteName(), pRequest);
    }
  }

  /**
   * Updates 'receiveEmail' profile property.
   */
  private void updateReceiveEmailProperty() {
    StorePropertyManager propertyManager = getStorePropertyManager();
    setValueProperty(propertyManager.getReceiveEmailPropertyName(), YES);
    
    if(isEmailOptIn()) {
      setValueProperty(propertyManager.getReceivePromoEmailPropertyName(), YES);
    } else {
      setValueProperty(propertyManager.getReceivePromoEmailPropertyName(), NO);
    }
  }
 
  /**
   * Override OOTB method so that if the profile has receiveEmail=yes, create
   * an EmailRecipient repository item for Email Campaigns. If there were no errors
   * on the form, then the Registered User email will be send as well.
   * {@inheritDoc}
   *
   * @see atg.userprofiling.ProfileForm#postCreateUser(atg.servlet.DynamoHttpServletRequest,
   *                                                   atg.servlet.DynamoHttpServletResponse)
   */
  protected void postCreateUser(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {

    Profile profile = getProfile();
    StorePropertyManager propertyManager = getStorePropertyManager();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();

    // if the Profile's receiveEmail property is true, create an
    // EmailRecipient
    if (isEmailOptIn()) {
      if (isLoggingDebug()) {
        logDebug("User has opted to receive email");
      }

      String email = (String) profile.getPropertyValue(propertyManager.getEmailAddressPropertyName());
      try {
        profileTools.createEmailRecipient(profile, email, getSourceCode());
      } catch (RepositoryException repositoryExc) {
        addFormException(MSG_ERR_CREATING_EMAIL_RECIPIENT, repositoryExc, pRequest);

        if (isLoggingError()) {
          logError(LogUtils.formatMajor(""), repositoryExc);
        }
      } //try
    }
    
    super.postCreateUser(pRequest, pResponse);

    if (!getFormError())
    {
      String redirectUrl = (String) getSessionBean().getValues().get(SessionBean.REDIRECT_AFTER_LOGIN_URL_PROPERTY_NAME);
      if (redirectUrl != null)
      {
        setCreateSuccessURL(redirectUrl);
      }
      
      postLoginAddItemToGiftlist();
    }
    
    // Send Registered User email
    sendEmail(pRequest, pResponse);        
  }
  
  /**
   * Clears Login Properties From Session
   */
  private void clearLoginPropertiesFromSession()
  {
    getSessionBean().getValues().remove(SessionBean.REDIRECT_AFTER_LOGIN_URL_PROPERTY_NAME);
    getSessionBean().getValues().remove(SessionBean.SKU_ID_TO_GIFTLIST_PROPERTY_NAME);
    getSessionBean().getValues().remove(SessionBean.PRODUCT_ID_TO_GIFTLIST_PROPERTY_NAME);
    getSessionBean().getValues().remove(SessionBean.GIFTLIST_ID_PROPERTY_NAME);
    getSessionBean().getValues().remove(SessionBean.QUANTITY_TO_ADD_TO_GIFTLIST_PROPERTY_NAME);
  }
  
  //---------------------------------------
  // handleUpdateUser related methods 
  //---------------------------------------

  /**
   * This overriding method will check to see if a new email address that is
   * being submitted in an update already belongs to another user profile.
   * {@inheritDoc}
   *
   * @see atg.userprofiling.ProfileForm#preUpdateUser(atg.servlet.DynamoHttpServletRequest,
   *                                                  atg.servlet.DynamoHttpServletResponse)
   */
  protected void preUpdateUser(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {
    
    StorePropertyManager propertyManager = getStorePropertyManager();
    String newEmail = ((String) getValue().get(propertyManager.getEmailAddressPropertyName())).trim();
    String oldEmail = ((String) getProfile().getPropertyValue(propertyManager.getLoginPropertyName())).trim();
    String oldLogin = ((String) getProfile().getPropertyValue(propertyManager.getEmailAddressPropertyName())).trim();

    MutableRepository repository = getProfileTools().getProfileRepository();
    
    //validate the newly provided emailAddress/Login
    //May need to change in future if we ever support separate Login & AlternateEmailAddress for Profile
    if (StringUtils.isBlank(newEmail)
        || !((StoreProfileTools) getProfileTools()).validateEmailAddress(newEmail)) {
      addFormException(MSG_INVALID_EMAIL, getAbsoluteName(), pRequest);
    }

    try {
      // We will update profile login property with new email only in the case
      // when profile has equal email and login properties if they are different
      // only email property will be updated.
      if (oldLogin.equalsIgnoreCase(oldEmail)){
        
        // If login is new check it for duplication
        if(!(newEmail.equalsIgnoreCase(oldLogin))){
          
          // throw a form exception of login exists and if it does
          // not belong to the current profile
          if (userAlreadyExists(newEmail, repository, pRequest, pResponse) ) {
            addFormException(MSG_DUPLICATE_USER, new Object[]{newEmail}, getAbsoluteName(), pRequest);          
          }else{
            setValueProperty(propertyManager.getLoginPropertyName(), newEmail.toLowerCase());
          }
        }
      }
      
    } catch (RepositoryException exc) {
      if (isLoggingError()){
        logError(LogUtils.formatMajor(MSG_DUPLICATE_USER), exc);  
      }      
    }
    
    // Store old email address for later use
    setPreviousEmailAddress(oldEmail);
    
    //validating updated date of birth    
    updateDateOfBirthProperty(pRequest);
    updateReceiveEmailProperty();

    super.preUpdateUser(pRequest, pResponse);
  }

  /**
   * Updates Date Of Birth Property if all entered values (day,
   * mont and year) pass validation.
   * @param pRequest the servlet's request
   */
  private void updateDateOfBirthProperty(DynamoHttpServletRequest pRequest) {
    validateBirthDate();
    String dob = getDateOfBirth();
    if (!StringUtils.isBlank(dob)) {
      Date validDate = validateDateFormat(dob, DATE_FORMAT);
      
      CurrentDate cd = getCurrentDate();
      Date currentDate = cd.getTimeAsDate();
      
      Calendar currDateCal = Calendar.getInstance();
      currDateCal.setTime(currentDate);
      
      if (currDateCal.compareTo(mBirthDate) < 0) {
        addFormException(MSG_INVALID_DATE_FORMAT, getAbsoluteName(), pRequest);
      }

      if (validDate == null) {
        addFormException(MSG_INVALID_DATE_FORMAT, getAbsoluteName(), pRequest);
      } else {
        setValueProperty(getStorePropertyManager().getDateOfBirthPropertyName(), validDate);
      }
    } else {
      setValueProperty(getStorePropertyManager().getDateOfBirthPropertyName(), null);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see atg.userprofiling.ProfileForm#postUpdateUser(atg.servlet.DynamoHttpServletRequest,
   *                                                   atg.servlet.DynamoHttpServletResponse)
   */
  protected void postUpdateUser(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {
    Profile profile = getProfile();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    StorePropertyManager propertyManager = getStorePropertyManager();

    //Capture new and old Logins before updaing the Login with new email address
    String newEmail = ((String) getValue().get(propertyManager.getEmailAddressPropertyName())).trim();    

    //Check if emailOptIn status has changed
    if (isPreviousOptInStatus() != isEmailOptIn()) {
      // Email optIn/Out preference has changed
      if (isEmailOptIn()) {
        // User has chosen to opt in
        // In both cases create new emailRecipient with new email address ('newLogin')
        // If user has changed his email, then 'newLogin' is the updated email address
        // If user has not changed his email, then 'oldLogin' is same as 'newLogin'
        if (isLoggingDebug()) {
          logDebug("User has opted-in to subscribe to email while updating his profile");
        }

        //create Email Recipient
        try {

          profileTools.createEmailRecipient(profile, newEmail, getSourceCode());

        } catch (RepositoryException repositoryExc) {
          addFormException(MSG_ERR_CREATING_EMAIL_RECIPIENT, repositoryExc, pRequest);

          if (isLoggingError()) {
            logError(LogUtils.formatMajor(""), repositoryExc);
          }
        } //try

      } else {
        // User has opted out. In both cases - whether user has changed/not changed his email
        // un-subscribe old email address of shopper from receiving emails
        if (isLoggingDebug()) {
          logDebug("User opted-out to unsubscribe email while updating his profile");
        }

        try {
          profileTools.removeEmailRecipient(getPreviousEmailAddress());
        } catch (RepositoryException repositoryExc) {
          addFormException(MSG_ERR_REMOVING_EMAIL_RECIPIENT, repositoryExc, pRequest);

          if (isLoggingError()) {
            logError(LogUtils.formatMajor(""), repositoryExc);
          }
        } //try        
      }
    } else {
      // Email OptIn/Out preference has not changed
      // Check if user has changed emailAddress while updating profile
      if (!(getPreviousEmailAddress().equalsIgnoreCase(newEmail))) {
        if (isLoggingDebug()) {
          logDebug("User has changed his email, updating the registered emaill address");
        }
        try {
          profileTools.updateEmailRecipient(profile, getPreviousEmailAddress(), newEmail, getSourceCode());
        } catch (RepositoryException repositoryExc) {
          addFormException(MSG_ERR_UPDATING_EMAIL_RECIPIENT, repositoryExc, pRequest);

          if (isLoggingError()) {
            logError(LogUtils.formatMajor(""), repositoryExc);
          }
        } //try       
      }
    }

    super.postUpdateUser(pRequest, pResponse);
  }

 
  
  /**
   * Operation called to validate the Date on the basis of locale format
   *
   * @param pDate
   *            User Date
   * @param pFormat
   *            the Date Format
   * @return date from string
   */
  protected Date validateDateFormat(String pDate, String pFormat) {
    DateFormat df = new SimpleDateFormat(pFormat.trim());
    Date d = null;

    try {
      df.setLenient(false);      
      String formattedDate = df.format(df.parse(pDate));
      
      // We must ensure that the number of year digits match exactly what's
      // defined in the date format pattern. This is needed because for example 
      // if a pattern has 4 year symbols, and the date being validated has 5 year 
      // digits, a 5 digit year will be treated as a valid date and an exception 
      // will probably occur when writing that date to the database. 
      
      String[] date = null;
      String[] dateFormat = null;
      
      if (formattedDate.contains("-")) {
        date = formattedDate.split("-");
        dateFormat = pFormat.split("-");
      }
      else if (formattedDate.contains("/")) {
        date = formattedDate.split("/");
        dateFormat = pFormat.split("/");
      }
      else if (formattedDate.contains(".")) {
        date = formattedDate.split("\\.");
        dateFormat = pFormat.split("\\.");
      }
      
      if ((date != null) && (dateFormat != null)) {
        for (int i = 0; i < dateFormat.length; i++) {
          if (dateFormat[i].contains("y")) {
            
            if(!(dateFormat[i].length() == date[i].length()) || 
                date[i].startsWith("0")) {
              return null;
            }
          }
        }

      }

      d = df.parse(pDate);
    } 
    catch (ParseException e) {
      return null;
    }

    if (isLoggingDebug()) {
      logDebug(pDate + " is a valid date String, returning corresponding Date object.");
    }
    
    return d;
  }
  
  
  //---------------------------------------
  // handleCreateNewCreditCard and related methods
  //---------------------------------------
  
  /**
   * Creates a new credit card using the entries entered in the editValue map
   *
   * @param pRequest
   *            the servlet's request
   * @param pResponse
   *            the servlet's response
   * @exception ServletException
   *                if there was an error while executing the code
   * @exception IOException
   *                if there was an error with servlet io
   * @return true if success, false - otherwise
   */
  public boolean handleCreateNewCreditCardAndAddress(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {
	  
	preCreateNewCreditCardAndAddress(pRequest, pResponse);

    // validate credit card information
    if (!validateCreateCreditCardInformation(pRequest, pResponse, true)) {
      return checkFormRedirect(null, getCreateCardErrorURL(), pRequest, pResponse);
    }

    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    StorePropertyManager propertyManager = getStorePropertyManager();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    Profile profile = getProfile();

    // Get editValue map, containing the credit card properties
    HashMap newCard = (HashMap) getEditValue();
    HashMap newAddress = (HashMap) getBillAddrValue();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      String secondaryAddrNickname = (String) newAddress.get(getShippingAddressNicknameMapKey());

      // Get credit card's nickname
      String cardNickname = (String) newCard.get(propertyManager.getCreditCardNicknamePropertyName());

      try {
        // Create credit card and add to profile
        cardNickname = profileTools.createProfileCreditCard(profile, newCard, cardNickname,
                                                            newAddress, secondaryAddrNickname,
                                                            true);

        String newCreditCard = (String) newCard.get(propertyManager.getNewCreditCard());
        
        if(!StringUtils.isEmpty(newCreditCard) && newCreditCard.equalsIgnoreCase("true")) {
          profileTools.setDefaultCreditCard(profile, cardNickname);
        }


        // empty out the map
        newCard.clear();
        newAddress.clear();
      } catch (RepositoryException repositoryExc) {
        addFormException(MSG_ERR_CREATING_CC, repositoryExc, pRequest);

        if (isLoggingError()) {
          logError(LogUtils.formatMajor(repositoryExc.toString()), repositoryExc);
        }
      } catch (InstantiationException ex) {
        throw new ServletException(ex);
      } catch (IllegalAccessException ex) {
        throw new ServletException(ex);
      } catch (ClassNotFoundException ex) {
        throw new ServletException(ex);
      } catch (IntrospectionException ex) {
        throw new ServletException(ex);
      } catch (PropertyNotFoundException ex) {
        throw new ServletException(ex);
      }

      postCreateNewCreditCardAndAddress(pRequest, pResponse);
      
      return checkFormRedirect(getCreateCardSuccessURL(), getCreateCardErrorURL(), pRequest,
                               pResponse);
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
          if (isLoggingError()) {
            logError("Can't end transaction ", e);
          }
      }
    }
  }
  
  /**
   * Operation is called just before new card with new address be created
   * 
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @throws ServletException if there was an error while executing the code
   * @throws IOException if there was an error with servlet io
   */
  protected void preCreateNewCreditCardAndAddress(DynamoHttpServletRequest pRequest, 
                                                  DynamoHttpServletResponse pResponse)
     throws ServletException, IOException  {
  }
  
  /**
   * Operation is called just after new card with new address be created
   * 
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @throws ServletException if there was an error while executing the code
   * @throws IOException if there was an error with servlet io
   */
  protected void postCreateNewCreditCardAndAddress(DynamoHttpServletRequest pRequest, 
                                                   DynamoHttpServletResponse pResponse)
     throws ServletException, IOException  {
  }

/**
   * Creates a new credit card using the entries entered in the editValue map
   *
   * @param pRequest
   *            the servlet's request
   * @param pResponse
   *            the servlet's response
   * @exception ServletException
   *                if there was an error while executing the code
   * @exception IOException
   *                if there was an error with servlet io
   * @return true if success, false - otherwise
   */
  public boolean handleCreateNewCreditCard(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {
	  
    preCreateNewCreditCard(pRequest, pResponse);
    		
    // validate credit card information
    if (!validateCreateCreditCardInformation(pRequest, pResponse, false)) {
      return checkFormRedirect(null, getCreateCardErrorURL(), pRequest, pResponse);
    }

    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    StorePropertyManager propertyManager = getStorePropertyManager();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    Profile profile = getProfile();

    // Get editValue map, containing the credit card properties
    HashMap newCard = (HashMap) getEditValue();
    HashMap newAddress = (HashMap) getBillAddrValue();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      // Check to see if user is selecting existing address
      String secondaryAddrNickname = (String) newAddress.get(getNewNicknameValueMapKey());

      // Get credit card's nickname
      String cardNickname = (String) newCard.get(propertyManager.getCreditCardNicknamePropertyName());

      try {
        // Create credit card and add to profile
        cardNickname = profileTools.createProfileCreditCard(profile, newCard, cardNickname,
                                                            newAddress, secondaryAddrNickname,
                                                            false);

        String newCreditCard = (String) newCard.get(propertyManager.getNewCreditCard());
        
        if(!StringUtils.isEmpty(newCreditCard) && newCreditCard.equalsIgnoreCase("true")) {
          profileTools.setDefaultCreditCard(profile, cardNickname);
        }

        // empty out the map
        newCard.clear();
        newAddress.clear();
      } catch (RepositoryException repositoryExc) {
        addFormException(MSG_ERR_CREATING_CC, repositoryExc, pRequest);

        if (isLoggingError()) {
          logError(LogUtils.formatMajor(repositoryExc.toString()), repositoryExc);
        }
      } catch (InstantiationException ex) {
        throw new ServletException(ex);
      } catch (IllegalAccessException ex) {
        throw new ServletException(ex);
      } catch (ClassNotFoundException ex) {
        throw new ServletException(ex);
      } catch (IntrospectionException ex) {
        throw new ServletException(ex);
      } catch (PropertyNotFoundException ex) {
        throw new ServletException(ex);
      }

      postCreateNewCreditCard(pRequest, pResponse);
      
      return checkFormRedirect(getCreateCardSuccessURL(), getCreateCardErrorURL(), pRequest,
                               pResponse);
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
        if (isLoggingError()) {
          logError("Can't end transaction ", e);
        }        
      }
    }
  }
  
  /**
   * Operation is called just before new card with existing address be created
   * 
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @throws ServletException if there was an error while executing the code
   * @throws IOException if there was an error with servlet io
   */
  protected void preCreateNewCreditCard(DynamoHttpServletRequest pRequest,
                                        DynamoHttpServletResponse pResponse)
    throws ServletException, IOException  {
  }
  
  /**
   * Operation is called just after new card with existing address be created
   * 
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @throws ServletException if there was an error while executing the code
   * @throws IOException if there was an error with servlet io
   */
  protected void postCreateNewCreditCard(DynamoHttpServletRequest pRequest,
                                         DynamoHttpServletResponse pResponse)
    throws ServletException, IOException  {
  }

/**
   * Validates credit card information entered by user:
   * <ul>
   *  <li>all required fields are specified for credit card
   *  <li>all required fields are specified for new address
   *  <li>country/state combination is valid for new address
   *  <li>card number and expiry date are valid
   *  <li>not duplicate credit card nickname is used
   * </ul>
   * @param pRequest http request
   * @param pResponse http response
   * @param pIsNewAddress true if should validate new address 
   * 
   * @return true is validation succeeded
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected boolean validateCreateCreditCardInformation(DynamoHttpServletRequest pRequest,
                                                        DynamoHttpServletResponse pResponse, 
                                                        boolean pIsNewAddress)
      throws ServletException, IOException {
    
    // return false if there were missing required properties
    if (getFormError()) {
      return false;
    }
    
    HashMap newCard = (HashMap) getEditValue();
    HashMap newAddress = (HashMap) getBillAddrValue();
    
    //validate credit card fields
    validateCreditCardFields(pRequest, pResponse);

    // if new address should be created validate all address properties
    // and country/state combination
    if (pIsNewAddress) {
      validateBillingAddressFields(pRequest, pResponse);
      validateCountryStateCombination(newAddress, pRequest, pResponse);
    }

    if (getFormError()) {
      return false;
    }

    // Verify that card number and expiration date are valid
    ResourceBundle bundle = LayeredResourceBundle.getBundle(RESOURCE_BUNDLE, getLocale(pRequest));
    if (!validateCreditCard(newCard, bundle)) {
      return false;
    }

    // Check that the nickname is not already used for a credit card
    StorePropertyManager propertyManager = getStorePropertyManager();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    Profile profile = getProfile();
    String cardNickname = (String) newCard.get(propertyManager.getCreditCardNicknamePropertyName());
    if (profileTools.isDuplicateCreditCardNickname(profile, cardNickname)) {
      addFormException(MSG_DUPLICATE_CC_NICKNAME, new String[] { cardNickname },
                            getAbsoluteName() + EDIT_VALUE + ".creditCardNickname", pRequest);
      return false;
    }
    return true;
  }
  
  /**
   * Validates that all required credit card's fields are entered by user
   * 
   * @param pRequest http request
   * @param pResponse http response
   * @return true if all required fields are entered
   */
  protected boolean validateCreditCardFields(DynamoHttpServletRequest pRequest,
                                             DynamoHttpServletResponse pResponse) {
    Map newCard = getEditValue();
    String[] cardProps = getCardProperties();
    StorePropertyManager propertyManager = getStorePropertyManager();

    boolean missingFields = false;
    Object property = null;
    String propertyName = null;

    // Verify all required fields entered for credit card
    for (int i = 0; i < cardProps.length; i++) {
      propertyName = cardProps[i];
      
      //not check here billingAddress Property
      if (propertyName.equals(propertyManager.getCreditCardBillingAddressPropertyName())){
        continue;
      }
      property = newCard.get(propertyName);

      if (StringUtils.isBlank((String) property)) {
        ResourceBundle bundle = LayeredResourceBundle.getBundle(RESOURCE_BUNDLE, getLocale(pRequest));
        String[] args = new String[]{bundle.getString(propertyName)};
        addFormException(MSG_MISSING_CC_PROPERTY, args, getAbsoluteName(), pRequest);
        missingFields = true;
      }
    }
    return !missingFields;
  }
  
  /**
   * Validates that all required fields are entered for billing address
   * 
   * @param pRequest http request
   * @param pResponse http response
   * @return true if all required fields are entered
   */
  protected boolean validateBillingAddressFields(DynamoHttpServletRequest pRequest,
                                                 DynamoHttpServletResponse pResponse) {
    Map newAddress = getBillAddrValue();
    Iterator addressPropertyIterator = getRequiredBillingAddressPropertyList().iterator();
    boolean missingFields = false;

    Object property = null;
    String propertyName = null;

    // Check to see all the address fields are entered
    while (addressPropertyIterator.hasNext()) {
      propertyName = (String) addressPropertyIterator.next();
      property = newAddress.get(propertyName);

      if (StringUtils.isBlank((String) property)) {
        ResourceBundle bundle = LayeredResourceBundle.getBundle(RESOURCE_BUNDLE, getLocale(pRequest));
        String[] args = new String[]{bundle.getString(propertyName)};
        addFormException(MSG_MISSING_CC_PROPERTY, args, getAbsoluteName(), pRequest);        
        missingFields = true;
      }
    }
    
    // Check for duplicate address nickname
    String billingAddressNickname = (String) newAddress.get(getShippingAddressNicknameMapKey());
    boolean duplicateNickname = ((StoreProfileTools)getProfileTools()).isDuplicateAddressNickName(getProfile(), billingAddressNickname);
    if (duplicateNickname)
    {
      addFormException(MSG_DUPLICATE_ADDRESS_NICKNAME, new String[] {billingAddressNickname}, getAbsoluteName(), pRequest);
    }
    return !(missingFields || duplicateNickname);
  }
  
  /**
   * Validates the credit card information using CreditCardTools.
   * {@inheritDoc}
   * @see CreditCardTools#verifyCreditCard(CreditCardInfo)
   */
  protected boolean validateCreditCard(Map pCard, ResourceBundle pBundle) {
    StorePropertyManager propertyManager = getStorePropertyManager();
    ExtendableCreditCardTools cardTools = getCreditCardTools();
    
    BasicStoreCreditCardInfo ccInfo = new BasicStoreCreditCardInfo();
    ccInfo.setExpirationYear((String) pCard.get(propertyManager.getCreditCardExpirationYearPropertyName()));
    ccInfo.setExpirationMonth((String) pCard.get(propertyManager.getCreditCardExpirationMonthPropertyName()));

    String ccNumber = (String) pCard.get(propertyManager.getCreditCardNumberPropertyName());

    if (ccNumber != null) {
      ccNumber = StringUtils.removeWhiteSpace(ccNumber);
    }

    ccInfo.setCreditCardNumber(ccNumber);
    ccInfo.setCreditCardType((String) pCard.get(propertyManager.getCreditCardTypePropertyName()));

    int ccreturn = cardTools.verifyCreditCard(ccInfo);

    if (ccreturn != cardTools.SUCCESS) {
      addCreditCardFormException(pBundle, ccreturn);
      return false;
    }

    return true;
  }
  
  /**
   * Adds form exception with the message according to the specified parameters 
   * 
   * @param pBundle resource bundle
   * @param ccreturn error code for credit card data
   */
  protected void addCreditCardFormException(ResourceBundle pBundle, int ccreturn){
    ExtendableCreditCardTools cardTools = getCreditCardTools();
 
    String msg = "";
    if ((pBundle != null) && (pBundle.getLocale() != null)) { 
      msg = cardTools.getStatusCodeMessage(ccreturn, pBundle.getLocale());
    }
    else {
      msg = cardTools.getStatusCodeMessage(ccreturn);
    }

    // set path to invalid property according to the return code (ccreturn)
    StorePropertyManager propertyManager = getStorePropertyManager();
    StringBuilder path = new StringBuilder(getAbsoluteName() + "." + EDIT_VALUE + ".");

    switch (ccreturn) {
    case ExtendableCreditCardTools.CARD_TYPE_NOT_VALID :
      path.append(propertyManager.getCreditCardTypePropertyName());
      break;

      case ExtendableCreditCardTools.CARD_NUMBER_HAS_INVALID_CHARS :
      case ExtendableCreditCardTools.CARD_NUMBER_DOESNT_MATCH_TYPE :
      case ExtendableCreditCardTools.CARD_LENGTH_NOT_VALID :
      case ExtendableCreditCardTools.CARD_NUMBER_NOT_VALID :
        path.append(propertyManager.getCreditCardNumberPropertyName());
        break;

      case ExtendableCreditCardTools.CARD_EXPIRED :
      case ExtendableCreditCardTools.CARD_EXP_DATE_NOT_VALID :
        path.append(propertyManager.getCreditCardExpirationMonthPropertyName());
    }

    addFormException(new DropletFormException(msg, path.toString(), MSG_INVALID_CC));
  }
  
  //---------------------------------------
  // handleUpdateCard and related methods
  //---------------------------------------
  
  /**
   * Called before handleUpdateCard logic is applied. Adds properties listed in immutableCardProperties property to
   * editValue map.
   * @param pRequest current request
   * @param pResponse current response
   * @throws RepositoryException if unable to obtain user card's properties.
   */
  protected void preUpdateCard(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws RepositoryException
  {
    RepositoryItem cardToUpdate = findCurrentCreditCard();
    for (String propertyName: mImmutableCardProperties)
    {
      getEditValue().put(propertyName, cardToUpdate.getPropertyValue(propertyName));
    }
  }

  /**
   * Searches current user's credit card by nick-name from editValue properties. 
   * @return credit card if found.
   */
  protected RepositoryItem findCurrentCreditCard()
  {
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    String cardNickname = (String) getEditValue().get(getNicknameValueMapKey());
    RepositoryItem cardToUpdate = profileTools.getCreditCardByNickname(cardNickname, getProfile());
    return cardToUpdate;
  }
  
  /**
   * Updates the credit card as modified by the user.
   *
   * @param pRequest
   *            the servlet's request
   * @param pResponse
   *            the servlet's response
   * @exception ServletException
   *                if there was an error while executing the code
   * @exception IOException
   *                if there was an error with servlet io
   * @exception RepositoryException
   *                if there was an error accessing the repository
   * @return true if success, false - otherwise
   */
  public boolean handleUpdateCard(DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse) throws RepositoryException,
      ServletException, IOException {
    preUpdateCard(pRequest, pResponse);
    
    // validate credit card information
    if (!validateUpdateCreditCardInformation(pRequest, pResponse)) {
      return checkFormRedirect(null, getUpdateCardErrorURL(), pRequest, pResponse);
    }

    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      Profile profile = getProfile();
      Map edit = getEditValue();
      Map billAddrValue = getBillAddrValue();

      String newNickname = (String) edit.get(getNewNicknameValueMapKey());

      try {

        if (isLoggingDebug()) {
          logDebug("Updating credit card properties");
        }
        
        // Get credit card to update
        RepositoryItem cardToUpdate = findCurrentCreditCard();
        
        // Update credit card
        profileTools.updateProfileCreditCard(cardToUpdate, profile, edit, newNickname,
                                             billAddrValue, profileTools.getBillingAddressClassName());
        
        // Update nickname case if necessary
        String nickname = profileTools.getCreditCardNickname(profile, cardToUpdate);
        if (!StringUtils.isBlank(newNickname) && !newNickname.equals(nickname) && newNickname.equalsIgnoreCase(nickname)) {
          profileTools.changeCreditCardNickname(profile, nickname, newNickname);
        }

        //save this card as default if needed
        String newCreditCard = (String) edit.get(getStorePropertyManager().getNewCreditCard());
        if(!StringUtils.isEmpty(newCreditCard) && newCreditCard.equalsIgnoreCase("true")) {
          profileTools.setDefaultCreditCard(profile, newNickname);
        } else if ("false".equalsIgnoreCase(newCreditCard)) {
          //current card should not be default
          RepositoryItem defaultCreditCard = profileTools.getDefaultCreditCard(profile);
          if (defaultCreditCard != null && cardToUpdate.getRepositoryId().equals(defaultCreditCard.getRepositoryId())) {
            //current card is default, make it not to be
            profileTools.updateProperty(getStorePropertyManager().getDefaultCreditCardPropertyName(), null, profile);
            //otherwise we shouldn't change anything more in the profile
          }
        }
      } catch (RepositoryException repositoryExc) {
        addFormException(MSG_ERR_UPDATING_CREDIT_CARD, repositoryExc, pRequest);

        if (isLoggingError()) {
          logError(LogUtils.formatMajor(""), repositoryExc);
        }

        return checkFormRedirect(null, getUpdateCardErrorURL(), pRequest, pResponse);
      } catch (InstantiationException ex) {
        throw new ServletException(ex);

      } catch (IllegalAccessException ex) {
        throw new ServletException(ex);

      } catch (ClassNotFoundException ex) {
        throw new ServletException(ex);
      } catch (IntrospectionException ex) {
        throw new ServletException(ex);
      }
      billAddrValue.clear();
      edit.clear();
      return checkFormRedirect(getUpdateCardSuccessURL(), getUpdateCardErrorURL(), pRequest,
                               pResponse);
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
        if (isLoggingError()) {
          logError("Can't end transaction ", e);
        }
      }
    }
  }
  
  /**
   * Validates updated credit card information entered by user:
   * <ul>
   *  <li>country/state combination is valid for billing address
   *  <li>card expiry date are valid
   *  <li>not duplicate credit card nickname is used
   * </ul>
   * @param pRequest http request
   * @param pResponse http response
   * 
   * @return true is validation succeeded
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected boolean validateUpdateCreditCardInformation(DynamoHttpServletRequest pRequest,
                                                        DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {
    
    // return false if there were missing required properties
    if (getFormError()) {
      return false;
    }
    
    HashMap card = (HashMap) getEditValue();
    HashMap billingAddress = (HashMap) getBillAddrValue();
    String nickname = ((String) card.get(getNicknameValueMapKey())).trim();

    //Validate billing address fields
    validateBillingAddressFields(pRequest, pResponse);
    validateCountryStateCombination(billingAddress, pRequest, pResponse);

    if (getFormError()) {
      return false;
    }
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    Profile profile = getProfile();
    StorePropertyManager propertyManager = getStorePropertyManager();

    // Verify that card expiry date is valid
    RepositoryItem cardToUpdate = profileTools.getCreditCardByNickname(nickname, profile);
    
    // only expiry date is needed to be validated because credit card type and number
    // are not updated. But we need to set credit card type and number to map because 
    // they are needed for expiry date validation
    card.put(propertyManager.getCreditCardNumberPropertyName(),
             cardToUpdate.getPropertyValue(propertyManager.getCreditCardNumberPropertyName()));
    card.put(propertyManager.getCreditCardTypePropertyName(),
             cardToUpdate.getPropertyValue(propertyManager.getCreditCardTypePropertyName()));

    ResourceBundle bundle = LayeredResourceBundle.getBundle(RESOURCE_BUNDLE, getLocale(pRequest));
    if (!validateCreditCard(card, bundle)) {
      return false;
    }

    // Check that the new  nickname is not already used for a credit card
    String newNickname = ((String) card.get(getNewNicknameValueMapKey())).trim();
    if (!StringUtils.isBlank(newNickname) && !newNickname.equals(nickname)) {
      List ignore = new ArrayList();
      ignore.add(nickname);
      if (profileTools.isDuplicateCreditCardNickname(profile, newNickname, ignore)) {
        addFormException(MSG_DUPLICATE_CC_NICKNAME, new String[] { newNickname },
                              getAbsoluteName() + EDIT_VALUE + ".creditCardNickname", pRequest);

        return false;
      }
    }

    return true;
  }
  
  //---------------------------------------
  // handleRemoveCard
  //--------------------------------------- 
 
  /**
   * Removes specified in <code>removeCard</code> property credit card
   * from user's credit cards map.
   * 
   * @param pRequest http request
   * @param pResponse http response
   * @return true if success, false - otherwise
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleRemoveCard(DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {
    
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    
    String cardNickname = getRemoveCard();
    if (StringUtils.isBlank(cardNickname)) {
      if (isLoggingDebug()) {
        logDebug("A null or empty nickname was provided to handleRemoveAddress");
      }

      // if no nickname provided, do nothing.
      return true;
    }

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }
      Profile profile = getProfile();

      // remove credit card from user's credit cards map
      profileTools.removeProfileCreditCard(profile, cardNickname);

      // Success; redirect if required to do so following success
      return checkFormRedirect(getRemoveCardSuccessURL(), getRemoveCardErrorURL(), pRequest,
                               pResponse);
      
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } catch (RepositoryException repositoryExc) {
      if (isLoggingError()){  
        logError(LogUtils.formatMajor(""), repositoryExc);
      }
    } finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
        if (isLoggingError()) {
          logError("Can't end transaction ", e);
        }
      }
    }

    return false;
  }
  
  //---------------------------------------
  // handleDefaultCard 
  //---------------------------------------

  /**
   * Makes the credit card identified by a nickname, default in the given
   * profile.
   * @deprecated
   * @param pRequest
   *            DynamoHttpServletRequest
   * @param pResponse
   *            DynamoHttpServletResponse
   * @return boolean true/false for success
   * @throws RepositoryException
   *             if there was an error accessing the repository
   * @throws ServletException
   *             if there was an error while executing the code
   * @throws IOException
   *             if there was an error with servlet io
   */
  public boolean handleDefaultCard(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse) throws RepositoryException,
      ServletException, IOException {
    
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      // Get nickname of the credit card that should be done default
      String targetCard = getDefaultCard();

      if (StringUtils.isBlank(targetCard)) {

        if (isLoggingDebug()) {
          logDebug("A null or empty nickname was provided to handleDefaultCard");
        }

        // if no nickname provided, do nothing.
        return true;
      }

      Profile profile = getProfile();
      StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
      profileTools.setDefaultCreditCard(profile, targetCard);

      // Success; redirect if required to do so following success
      return checkFormRedirect(getDefaultCardSuccessURL(), getDefaultCardErrorURL(), pRequest,
                               pResponse);

    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
        if (isLoggingError()) {
          logError("Can't end transaction ", e);
        }
      }
    }
  }
  
  //---------------------------------------
  // handleEditCard 
  //---------------------------------------

  /**
   * Copy the named credit card into the editValue map, allowing the user
   * to edit them.
   *
   * @param pRequest
   *            the servlet's request
   * @param pResponse
   *            the servlet's response
   * @exception ServletException
   *                if there was an error while executing the code
   * @exception IOException
   *                if there was an error with servlet io
   * @return true if success, false - otherwise
   */
  public boolean handleEditCard(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {
    
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    StorePropertyManager propertyManager = getStorePropertyManager();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      // the nickname of the credit card we want to edit
      String targetCard = getEditCard();

      if ((targetCard == null) || (targetCard.trim().length() == 0)) {
        // we should only get here through a hyperlink that supplies the
        // secondary credit card nickname. Just in case, exit quietly.
        return true;
      }

      if (!getFormError()) {
        Profile profile = getProfile();
        Map creditCards = (Map) profile.getPropertyValue(propertyManager.getCreditCardPropertyName());
        MutableRepositoryItem card = (MutableRepositoryItem) creditCards.get(targetCard);
        MutableRepositoryItem cardAddress = (MutableRepositoryItem) card.getPropertyValue(propertyManager.getBillingAddressPropertyName());

        Map edit = getEditValue();
        Map billAddrMap = getBillAddrValue();

        // record the nickname 
        edit.put(getNicknameValueMapKey(), targetCard);

        edit.put(getNewNicknameValueMapKey(), targetCard);

        String[] cardProps = getCardProperties();

        // copy each property to the map
        Object property;

        for (int i = 0; i < cardProps.length; i++) {
          property = card.getPropertyValue(cardProps[i]);

          if (property != null) {
            edit.put(cardProps[i], property);
          }
        }

        // now copy billing address properties
        property = null;

        String[] addressProps = getAddressProperties();

        for (int i = 0; i < addressProps.length; i++) {
          property = cardAddress.getPropertyValue(addressProps[i]);

          if (property != null) {
            billAddrMap.put(addressProps[i], property);
          }
        }
      }

      return checkFormRedirect(getUpdateCardSuccessURL(), getUpdateCardErrorURL(), pRequest,
                               pResponse);
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
        if (isLoggingError()) {
          logError("Can't end transaction ", e);
        }
      }
    }
  }

  //---------------------------------------
  // handleNewAddress and related methods 
  //---------------------------------------

  /**
   * Creates a new shipping address using the entries entered in the editValue
   * map. The address will be indexed using the nickname provided by the user.
   *
   * @param pRequest The current HTTP request
   * @param pResponse The current HTTP response
   * @return boolean returns true/false for success
   * @throws ServletException if there was an error while executing the code
   * @throws IOException if there was an error with servlet io
   */
  public boolean handleNewAddress(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException
  {
    // Validate address data entered by user, if its invalid go to the error URL
    if (!validateAddress(pRequest, pResponse)) {
      return checkFormRedirect(null, getNewAddressErrorURL(), pRequest, pResponse);
    }
    
    // Get the current user Profile and the ProfileTools bean and the values
    // entered into the new address form by the user.
    Profile profile = getProfile();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    Map<String, Object> newAddress = getEditValue();

    // Generate unique nickname if it is not provided by the user
    String nickname = (String) newAddress.get(getNicknameValueMapKey());
    if (StringUtils.isBlank(nickname)) {
      nickname = profileTools.getUniqueShippingAddressNickname(newAddress, profile, null);
    }
        
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      try {
        // Create an Address object from the values the user entered.
        Address addressObject = AddressTools.createAddressFromMap(newAddress,
          profileTools.getShippingAddressClassName());

        // Create an entry in the secondaryAddress map on the profile, for this
        // new address. Set this new Id as the newAddressId so it can be picked
        // up on the success page (used to select it in a dropdown).
        String newAddressId = 
          profileTools.createProfileRepositorySecondaryAddress(profile, nickname, addressObject);
        
        if(newAddressId != null){
          setNewAddressId(newAddressId);
        }

        // Check to see Profile.shippingAddress is null, if it is,
        // add the new address as the default shipping address
        if(isUseShippingAddressAsDefault()){
          profileTools.setDefaultShippingAddress(profile, nickname);
        }

        // empty out the map
        newAddress.clear();
      } 
      catch (RepositoryException repositoryExc) {
        addFormException(MSG_ERR_CREATING_ADDRESS, new String[] { nickname },
          repositoryExc, pRequest);       
  
        if (isLoggingError()) {
          logError(LogUtils.formatMajor(""), repositoryExc);
        }
  
        // Failure, redirect to the error URL
        return checkFormRedirect(null, getNewAddressErrorURL(), pRequest, pResponse);
      } 
      catch (InstantiationException ex) {
        throw new ServletException(ex);
      } 
      catch (IllegalAccessException ex) {
        throw new ServletException(ex);
      } 
      catch (ClassNotFoundException ex) {
        throw new ServletException(ex);
      } 
      catch (IntrospectionException ex) {
        throw new ServletException(ex);
      }

      // Success, redirect to the success URL
      return checkFormRedirect(getNewAddressSuccessURL(), 
        getNewAddressErrorURL(), pRequest, pResponse);
    } 
    catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } 
    finally {
      try {
        if (tm != null) {
          td.end();
        }
      } 
      catch (TransactionDemarcationException e) {
        if (isLoggingError()) {
          logError("Can't end transaction ", e);
        }
      }
    }
  }
  
  /**
   * Validates new address fields entered by user:
   * <ul>
   *  <li>all required fields are specified for new address
   *  <li>country/state combination is valid for new address
   *  <li>not duplicate address nickname is used for create address or update
   *      address operation
   * </ul>
   * @param pRequest http request
   * @param pResponse http response
   * 
   * @return true is validation succeeded
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected boolean validateAddress(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {
    
    // return false if there were missing required properties
    if (getFormError()) {
      return false;
    }

    // Validate country/state combination
    HashMap newAddress = (HashMap) getEditValue();
    validateCountryStateCombination(newAddress, pRequest, pResponse);

    if (getFormError()) {
      return false;
    }

    // Validate address nickname or new nickname if it's  update address operation
    String nickname = (String) newAddress.get(getNicknameValueMapKey());
    String newNickname = (String) newAddress.get(getNewNicknameValueMapKey());
    
    if (!StringUtils.isBlank(newNickname)) {  
      // Editing an address, in this case we want to allow them to change the casing of the nickname
      // so we remove the original nickname from the duplicate check by adding it to the ignore list.
      // We still need to perform the check incase they change it to another name thats in the list. 
      if (!newNickname.equals(nickname)) {       
        List ignore = new ArrayList();
        ignore.add(nickname);
        boolean duplicateNickname =  
          ((StoreProfileTools)getProfileTools()).isDuplicateAddressNickname(getProfile(), newNickname, ignore);
        
        if(duplicateNickname){
          addFormException(MSG_DUPLICATE_ADDRESS_NICKNAME, new String[] { newNickname },
                          getAbsoluteName(), pRequest);   
        }        
      }
    }
    else {
      //It's new address so validate nickname against all nicknames
      if (!StringUtils.isBlank(nickname)) {
        boolean duplicateNickname =  
          ((StoreProfileTools)getProfileTools()).isDuplicateAddressNickName(getProfile(), nickname);
        
        if(duplicateNickname){
          addFormException(MSG_DUPLICATE_ADDRESS_NICKNAME, new String[] { nickname },
                          getAbsoluteName(), pRequest);   
        }
      }
    }
    
    if (getFormError()) {
      return false;
    }
    
    //all validation passed successfully so return true
    return true;
  }
  
  /**
   * Validate country-state combination.
   *
   * @param pAddress - address
   * @param pRequest - http address
   * @param pResponse - http response
   */
  protected void validateCountryStateCombination(Map pAddress, DynamoHttpServletRequest pRequest,
                                                 DynamoHttpServletResponse pResponse) {
    if (pAddress != null) {
      CommercePropertyManager propertyManager = getStorePropertyManager();
      validateCountryStateCombination((String)pAddress.get(propertyManager.getAddressCountryPropertyName()),
          (String)pAddress.get(propertyManager.getAddressStatePropertyName()), pRequest, pResponse);
    }
  }

  /**
   * Validate country-state combination.
   * @param pCountry country code
   * @param pState county code
   * @param pRequest dynamo request
   * @param pResponse dynamo response
   */
  protected void validateCountryStateCombination(String pCountry, String pState, DynamoHttpServletRequest pRequest,
                                                 DynamoHttpServletResponse pResponse) {
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    if (!profileTools.isValidCountryStateCombination(pCountry, pState)) {
      Object[] args = new Object[] {LocaleUtils.constructLocale("en_" + pCountry).getDisplayCountry(getRequestLocale().getLocale())};
      addFormException(MSG_STATE_IS_INCORRECT, args, getAbsoluteName(), pRequest);      
    }
  }
   
  //---------------------------------------
  // handleUpdateAddress 
  //---------------------------------------
  
  /**
   * Update the secondary address as modified by the user.
   *
   * @param pRequest
   *            the servlet's request
   * @param pResponse
   *            the servlet's response
   * @exception ServletException
   *                if there was an error while executing the code
   * @exception IOException
   *                if there was an error with servlet io
   * @exception RepositoryException
   *                if there was an error accessing the repository
   * @return true for successful address update, false - otherwise
   */
  public boolean handleUpdateAddress(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse)
      throws RepositoryException, ServletException, IOException {

    //Validate address data entered by user
    if (!validateAddress(pRequest, pResponse)) {
      return checkFormRedirect(null, getUpdateAddressErrorURL(), pRequest, pResponse);
    }
    
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      Profile profile = getProfile();
      
      Map edit = getEditValue();
      String nickname = (String) edit.get(getNicknameValueMapKey());
      String newNickname = ((String) edit.get(getNewNicknameValueMapKey()));

      try {
        
        //Populate Address object data entered by user
        Address addressObject = AddressTools.createAddressFromMap(edit,
                                                                  profileTools.getShippingAddressClassName());
        // Get address repository item to be updated
        RepositoryItem oldAddress = profileTools.getProfileAddress(profile, nickname);
        
        // Update address repository item 
        profileTools.updateProfileRepositoryAddress(oldAddress, addressObject);
        
        // Check if nickname should be changed
        if (!StringUtils.isBlank(newNickname) && !newNickname.equals(nickname)) {
          profileTools.changeSecondaryAddressName(profile, nickname, newNickname);
        }
        
        if(isUseShippingAddressAsDefault()){
          ((StoreProfileTools) getProfileTools()).setDefaultShippingAddress(profile, newNickname);
        } else if (newNickname.equalsIgnoreCase(profileTools.getDefaultShippingAddressNickname(profile))) {
          profileTools.setDefaultShippingAddress(profile, null);
        }
        
        // update secondary properties of the address in the order (e.g phone num)
        Order currentOrder = getShoppingCart().getCurrent();        
        if(currentOrder != null){
          List shippingGroupList = currentOrder.getShippingGroups();
          for(Object shippingGroup : shippingGroupList){
            if(shippingGroup instanceof HardgoodShippingGroup){
              Address orderAddress = ((HardgoodShippingGroup)shippingGroup).getShippingAddress();
              if(StoreAddressTools.compare(addressObject, orderAddress)){
                  updateSecondaryInfo((ContactInfo)orderAddress,
                                      (ContactInfo)addressObject);
              }
            }
          }
        }
      } catch (RepositoryException repositoryExc) {
        addFormException(MSG_ERR_UPDATING_ADDRESS, repositoryExc, pRequest);

        if (isLoggingError()) {
          logError(LogUtils.formatMajor(""), repositoryExc);
        }

        return checkFormRedirect(null, getUpdateAddressErrorURL(), pRequest, pResponse);
      } catch (InstantiationException ex) {
        throw new ServletException(ex);
      } catch (IllegalAccessException ex) {
        throw new ServletException(ex);
      } catch (ClassNotFoundException ex) {
        throw new ServletException(ex);
      } catch (IntrospectionException ex) {
        throw new ServletException(ex);
      }

      edit.clear();
      return checkFormRedirect(getUpdateAddressSuccessURL(), getUpdateAddressErrorURL(), pRequest,
                               pResponse);
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
        if (isLoggingError()) {
          logError("Can't end transaction ", e);
        }
      }
    }
  }
  
  /**
   * Updates all the properties of an address that don't effect where
   * the item is shipped to - e.g the phone number.
   * @param pTargetAddress The target Address
   * @param pSourceAddress The source Address
   */
  protected void updateSecondaryInfo(Address pTargetAddress, Address pSourceAddress){
    
    // Update secondary ContactInfo
    if((pTargetAddress instanceof ContactInfo) &&
       (pSourceAddress instanceof ContactInfo))
    {
      // Secondary Property Phone Number
      if(((ContactInfo)pTargetAddress).getPhoneNumber() != ((ContactInfo)pSourceAddress).getPhoneNumber())
      {
        ((ContactInfo)pTargetAddress).setPhoneNumber(((ContactInfo)pSourceAddress).getPhoneNumber());  
      }
    }
  }
  
  //---------------------------------------
  // handleRemoveAddress 
  //---------------------------------------
  
  /**
   * This handler deletes a secondary address named in the removeAddress
   * property. 
   *
   * @param pRequest
   *            the servlet's request
   * @param pResponse
   *            the servlet's response
   * @return boolean true/false for success
   * @exception ServletException
   *                if there was an error while executing the code
   * @exception IOException
   *                if there was an error with servlet io
   */
  public boolean handleRemoveAddress(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {
    
    preRemoveAddress(pRequest, pResponse);

    // Stop execution if we have form errors
    if (getFormError()) {
      return true;
    }
    
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      // Get nickaname of the address to be removed
      String nickname = getRemoveAddress();

      if ((nickname == null) || (nickname.trim().length() == 0)) {
        if (isLoggingDebug()) {
          logDebug("A null or empty nickname was provided to handleRemoveAddress");
        }

        // if no nickname provided, do nothing.
        return true;
      }

      Profile profile = getProfile();
      
      // Remove the Address from the Profile
      RepositoryItem purgeAddress = profileTools.getProfileAddress(profile, nickname);
      if(purgeAddress != null){
        profileTools.removeProfileRepositoryAddress(profile, nickname, true);
      }    

      // Get the shipping group id that contains the Address
      String shippingGroupId = null;
      Map shippingGroupMap = getShippingGroupMapContainer().getShippingGroupMap();
      
      if(shippingGroupMap != null){
        if(shippingGroupMap.containsKey(nickname)){
          
          if (getProfile().isTransient()) {            
            vlogDebug("Removing transient user's shipping group: {0}", nickname);

            shippingGroupMap.remove(nickname);
            return true;
          }
          
          shippingGroupId = ((ShippingGroup)(shippingGroupMap.get(nickname))).getId();
        }
      }
      
      // Remove the Address from the Order
      if(shippingGroupId != null){
        Order currentOrder = getOrder();
        if(currentOrder instanceof StoreOrderImpl){
          boolean purged = ((StoreOrderImpl)currentOrder).removeAddress(shippingGroupId);
          if(!purged){
            if(isLoggingDebug()){
              logDebug("The Address could not be removed from order " + currentOrder.getId());
            }
          }
        }
      }
      return true;
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } catch (RepositoryException repositoryExc) {
      if (isLoggingError()){
        logError(LogUtils.formatMajor(""), repositoryExc);
      }
    } finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
        if (isLoggingError()) {
          logError("Can't end transaction ", e);
        }
      }
    }

    return false;
  }
  
  //---------------------------------------
  // handleDefaultShippingAddress 
  //---------------------------------------

  /**
   * This sets the default shipping address.
   * @deprecated
   * @param pRequest
   *            DynamoHttpServletRequest
   * @param pResponse
   *            DynamoHttpServletResponse
   * @return true for success, false - otherwise
   * @throws RepositoryException indicates that a severe error occured while performing a Repository task
   * @throws ServletException if there was an error while executing the code
   */
  public boolean handleDefaultShippingAddress(DynamoHttpServletRequest pRequest,
                                              DynamoHttpServletResponse pResponse)
      throws RepositoryException, ServletException {
    
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      Profile profile = getProfile();
      StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
      String addressNickname = getDefaultShippingAddress();

      if (StringUtils.isBlank(addressNickname)) {

        if (isLoggingDebug()) {
          logDebug("A null or empty nickname was provided to handleDefaultShippingAddress");
        }
        // if no nickname provided, do nothing.
        return true;
      }

      // Set requested shipping addres as default
      profileTools.setDefaultShippingAddress(profile, addressNickname);

      return true;
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
        if (isLoggingError()) {
          logError("Can't end transaction ", e);
        }
      }
    }
  }

  //---------------------------------------
  // handleEditAddress 
  //---------------------------------------
 
  /**
   * Copy the named address into the editValue map, allowing the user to edit it.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @throws PropertyNotFoundException If a property is not found
   */
  public boolean handleEditAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException, PropertyNotFoundException 
  {
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    StorePropertyManager propertyManager = getStorePropertyManager();

    try {
      
      if (tm != null){
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      String nickname = getEditAddress();
      if (nickname == null || nickname.trim().length() == 0) {
        return true;
      }

      // If we dont find the address on the profiles secondaryAddresses property check in the
      // shipping group map. This will occur when a user is unregistered (as we dont store addresses
      // on the profile for security reasons) or when a registered user tries to edit an address
      // that is only saved in the order (e.g anon user with order logging in).
      Profile profile = getProfile();
      Map secondaryAddress = (Map) profile.getPropertyValue(propertyManager.getSecondaryAddressPropertyName());
      Object theAddress = secondaryAddress.get(nickname);
      
      if(theAddress == null){
        Map shippingGroupMap = getShippingGroupMapContainer().getShippingGroupMap();
        if(shippingGroupMap != null){
          if(shippingGroupMap.containsKey(nickname)){
            HardgoodShippingGroup hgsg = (HardgoodShippingGroup) getShippingGroupMapContainer().getShippingGroup(nickname);
            theAddress = hgsg.getShippingAddress();
          }
        }
      }
      
      // We should never get here, but just incase
      if(theAddress == null){
        if(isLoggingError()){
          logError("Could not find the address " + nickname +
              " on the profile or in the shippingGroupMap");
        }
        return false;
      }
      
      String[] addressProps = getAddressProperties();
      Object property = null;
      Map edit = getEditValue();
      
      // Add Address properties to the edit Map
      edit.put(getNicknameValueMapKey(), nickname);
      edit.put(getNewNicknameValueMapKey(), nickname);
      
      // RepositoryItem Object
      if(theAddress instanceof RepositoryItem){
        edit.put(getAddressIdValueMapKey(), ((MutableRepositoryItem)theAddress).getRepositoryId());
        for (int i = 0; i < addressProps.length; i++) {
          property = ((RepositoryItem)theAddress).getPropertyValue(addressProps[i]);
          if (property != null){
            edit.put(addressProps[i], property);
          }
        }
      }
      // Address Object
      else{
        for (int i = 0; i < addressProps.length; i++) {
          property = DynamicBeans.getPropertyValue(theAddress, addressProps[i]);
          if (property != null){
            edit.put(addressProps[i], property);
          }
        }
      }

      return true;
      
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } finally {
      try {
        if (tm != null){
          td.end();
        }
      } catch (TransactionDemarcationException e) {
        if (isLoggingDebug()){
          logDebug("Ignoring exception", e);
        }
      }
    }
  }
  
  //---------------------------------------
  // handleClear 
  //---------------------------------------
  
  /**
   * Override to prevent clear if there are form errors.
   * {@inheritDoc}
   * <p>
   * This is here because postLogin will do a clear of the value dictionary even if the
   * login fails
   */
  public boolean handleClear(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    if (getFormError()) {
      return true;
    } else {
      return super.handleClear(pRequest, pResponse);
    }
  }  
  
  /**
   * Override to prevent clear if there are form errors.
   * {@inheritDoc}
   * <p>
   * This is here because postLogin will do a clear of the value dictionary even if the
   * login fails
   */
  public boolean handleClearForm(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {
      if(mEditValue != null) {
        mEditValue.clear();
      }
      if(mBillAddrValue != null) {
        mBillAddrValue.clear();
      }
      
      resetFormExceptions();
      return super.handleClear(pRequest, pResponse);
  } 
  
  /**
   * Perform pre-registration actions before user actually registered. 
   * Check email address. If it is passed validation, redirect to 
   * the registration page with filled in email value. Otherwise,
   * add form error and stays on the same page.
   * 
   * @param pRequest Dynamo HTTP request
   * @param pResponse Dynamo HTTP response
   * @return If redirect to the registration page occurred,
   *         return false. If no redirect occurred, return true.
   * @throws ServletException if there was an error while executing the code
   * @throws IOException      if there was an error with servlet io
   */
  public boolean handlePreRegister(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    
    //validate the email address
    String email = getStringValueProperty(getStorePropertyManager().getEmailAddressPropertyName());

    if (StringUtils.isBlank(email) || !((StoreProfileTools) getProfileTools()).validateEmailAddress(email)) {
      addFormException(MSG_INVALID_EMAIL, getAbsoluteName(), pRequest);
    }
    else if (((StoreProfileTools) getProfileTools()).isDuplicateEmailAddress(email)) {
      addFormException(MSG_DUPLICATE_USER, getAbsoluteName(), pRequest);
    }
    
    return checkFormRedirect(getPreRegisterSuccessURL(), getPreRegisterErrorURL(), pRequest,
        pResponse);
  }
  
  //---------------------------------------
  // findUser 
  //---------------------------------------
  
  /**
   * Returns the user profile or null if the user could not be found with the given login and password.
   * @param pLogin the login name for the person
   * @param pPassword the password for the person, optionally null if no password checking should be performed.
   * @param pProfileRepository the repository which should contain the desired profile template
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return the user profile or null if the user could not be found with the given login and password
   * @throws RepositoryException - if there was an error while accessing the Profile Repository
   * @throws ServletException - if there was an error while executing the code
   * @throws IOException - if there was an error with servlet io
   */
  protected RepositoryItem findUser(String pLogin, String pPassword,
      Repository pProfileRepository, DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws RepositoryException,
      ServletException, IOException {
    // convert login to lower case as case-insensitive logins are used
    // and they are stored in repository in lower case
    return super.findUser(pLogin.toLowerCase(), pPassword, pProfileRepository, pRequest,
        pResponse);
  }
  
  //---------------------------------------
  // Utility methods 
  //---------------------------------------

  /**
   * Utility method to retrieve the StorePropertyManager.
   * @return property manager 
   */
  protected StorePropertyManager getStorePropertyManager() {
    return (StorePropertyManager) getProfileTools().getPropertyManager();
  } 

  /**
   * Create a form exception, by looking up the exception code in a resource
   * file identified by the RESOURCE_BUNDLE constant (defined above).
   *
   * @param pWhatException
   *            String description of exception
   * @param pRepositoryExc
   *            RepositoryException
   * @param pRequest
   *            DynamoHttpServletRequest
   */
  protected void addFormException(String pWhatException, RepositoryException pRepositoryExc,
                                       DynamoHttpServletRequest pRequest) {
    addFormException(pWhatException,null,pRepositoryExc,pRequest);
  }
  
  /**
   * Create a form exception, by looking up the exception code in a resource
   * file identified by the RESOURCE_BUNDLE constant (defined above).
   *
   * @param pWhatException
   *            String description of exception
   * @param pArgs
   *            String array with arguments used message formatting
   * @param pRepositoryExc
   *            RepositoryException
   * @param pRequest
   *            DynamoHttpServletRequest
   */
  protected void addFormException(String pWhatException, Object[] pArgs, RepositoryException pRepositoryExc,
                                       DynamoHttpServletRequest pRequest) {   
    ResourceBundle bundle = LayeredResourceBundle.getBundle(RESOURCE_BUNDLE, getLocale(pRequest));
    String errorStr = bundle.getString(pWhatException);
    if (pArgs != null && pArgs.length > 0){
      errorStr = (new MessageFormat(errorStr)).format(pArgs);
    }
    addFormException(new DropletFormException(errorStr, pRepositoryExc, pWhatException));
  }

  /**
   * Creates a form exception, by looking up the exception code in a resource
   * file identified by the RESOURCE_BUNDLE constant (defined above).
   *
   * @param pWhatException
   *            String description of exception
   * @param pArgs
   *            String array with arguments used message formatting
   * @param pPath
   *            Full path to form handler property associated with the exception
   * @param pRequest
   *            DynamoHttpServletRequest
   */
  protected void addFormException(String pWhatException, Object[] pArgs, 
                                       String pPath, DynamoHttpServletRequest pRequest) {
    ResourceBundle bundle = LayeredResourceBundle.getBundle(RESOURCE_BUNDLE, getLocale(pRequest));
    String errorStr = bundle.getString(pWhatException);

    if (pArgs != null && pArgs.length > 0){
      errorStr = (new MessageFormat(errorStr)).format(pArgs);
    }
    
    addFormException(new DropletFormException(errorStr, pPath, pWhatException));
  }
  
  /**
   * Creates a form exception, by looking up the exception code in a resource
   * file identified by the RESOURCE_BUNDLE constant (defined above).
   *
   * @param pWhatException
   *            String description of exception 
   * @param pPath
   *            Full path to form handler property associated with the exception
   * @param pRequest
   *            DynamoHttpServletRequest
   */
  protected void addFormException(String pWhatException, 
                                       String pPath, DynamoHttpServletRequest pRequest) {
    addFormException(pWhatException, null, pPath, pRequest);
  }
 

  /**
   * Determine the user's current locale, if available.
   *
   * @param pRequest
   *            DynamoHttpServletRequest
   * @return Locale Request's Locale
   */
  protected Locale getLocale(DynamoHttpServletRequest pRequest) {
    RequestLocale reqLocale = pRequest.getRequestLocale();

    if (reqLocale == null) {
      reqLocale = getRequestLocale();
    }
    
    if(reqLocale == null){
      return null;
    }
    else {
      return reqLocale.getLocale();
    }
  }
  
  /**
   * Operation called to get the Date on the basis of locale format.
   *
   * @param pDate
   *            getting from database
   * @param pFormat
   *            Date Format get by database
   * @return date in specified format
   */
  protected String getDateByFormat(Object pDate, String pFormat) {
    DateFormat df;
    if(pFormat == null)
      df = new SimpleDateFormat(DATE_FORMAT);
    else
      df = new SimpleDateFormat(pFormat);
    
    return df.format(((java.util.Calendar)pDate).getTime());
  }
    
  /**
   * Return a String message specific for the given locale.
   *
   * @param pKey
   *            the identifier for the message to retrieve out of the
   *            ResourceBundle
   * @param pLocale
   *            the locale of the user
   * @return the localized message
   */
  public static String getString(String pKey, Locale pLocale) {
    return (getResourceBundle(pLocale).getString(pKey));
  }  
  
  /**
   * Returns a ResourceBundle specific for the given locale.
   *
   * @param pLocale
   *            the locale of the user
   * @return ResourcerBundle
   * @throws MissingResourceException
   *             ResourceBundle could not be located
   */
  public static ResourceBundle getResourceBundle(Locale pLocale)
      throws MissingResourceException {
    return (LayeredResourceBundle.getBundle(RESOURCE_BUNDLE, pLocale));
  }

  /**
   * Returns the Locale for the user given the request.
   *
   * @param pRequest
   *            the request object which can be used to extract the user's
   *            locale
   * @return Locale
   */
  protected Locale getUserLocale(DynamoHttpServletRequest pRequest) {
    if (pRequest != null) {
      RequestLocale reqLocale = pRequest.getRequestLocale();

      if (reqLocale != null) {
        return reqLocale.getLocale();
      }
    }

    return null;
  }
  
  /**
   * Handles changes on the 'Checkout Defaults' page - default credit card, shipping address and shipping method.
   * @param pRequest - current HTTP request.
   * @param pResponse - current HTTP response.
   * @return true if request hasn't been redirected and false otherwise.
   * @throws ServletException if something goes wrong.
   * @throws IOException if unable to redirect current request.
   */
  public boolean handleCheckoutDefaults(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException
  {
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    try
    {
      td.begin(tm, TransactionDemarcation.REQUIRED);
      
      StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
      StorePropertyManager propertyManager = getStorePropertyManager();
      
      String shippingAddressProperty = propertyManager.getShippingAddressPropertyName();
      String shippingAddressName = (String) getValue().get(shippingAddressProperty);
      if (!StringUtils.isEmpty(shippingAddressName))
      {
        RepositoryItem shippingAddress = profileTools.getProfileAddress(getProfile(), shippingAddressName);
        profileTools.updateProperty(shippingAddressProperty, shippingAddress, getProfile());
      }
      
      String creditCardProperty = propertyManager.getDefaultCreditCardPropertyName();
      String creditCardName = (String) getValue().get(creditCardProperty);
      if (!StringUtils.isEmpty(creditCardName))
      {
        RepositoryItem creditCard = profileTools.getCreditCardByNickname(creditCardName, getProfile());
        profileTools.updateProperty(creditCardProperty, creditCard, getProfile());
      }
      
      String defaultCarrierProperty = propertyManager.getDefaultShippingMethodPropertyName();
      profileTools.updateProperty(defaultCarrierProperty, getValue().get(defaultCarrierProperty), getProfile());
      
      return checkFormRedirect(getUpdateSuccessURL(), getUpdateErrorURL(), pRequest, pResponse);
    } catch (TransactionDemarcationException e)
    {
      throw new ServletException(e);
    } catch (RepositoryException e)
    {
      throw new ServletException(e);
    } finally
    {
      try
      {
        td.end();
      } catch (TransactionDemarcationException e)
      {
        throw new ServletException(e);
      }
    }
  }
  
  /**
   * Address that we'd like to remove could be associated
   * with a gift list. In this case, we'd like to prevent
   * address removal from the profile.
   * 
   * Also on billing page shouldn't be deleted address that 
   * was chosen as shipping address.
   * 
   * @param pRequest Dynamo http request
   * @param pResponse Dynamo http response
   */
  @SuppressWarnings("unchecked") // Ok, we cast 'secondaryAddresses' Map property
  public void preRemoveAddress(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) {
    
    // Get collection of the gift lists that belongs to the profile,
    // iterate over this collection and check addresses
    // If giftlist address is equal to the address we want to remove,
    // add form exceprion
    String addressNickname = getRemoveAddress();
    
    Map<String, RepositoryItem> profileAddresses =
          (Map<String, RepositoryItem>) getProfile().getPropertyValue(getStorePropertyManager().getSecondaryAddressPropertyName());
    RepositoryItem profileAddress = (RepositoryItem) profileAddresses.get(addressNickname);
    if (profileAddress == null && !getProfile().isTransient()) { //Already removed? Do nothing then.
      return;
    }
    
    GiftlistTools glTools = getGiftlistManager().getGiftlistTools();
    try {
      Collection giftlists = glTools.getGiftlists(getProfile());

      if(giftlists != null) {
        
        for(Object giftlist : giftlists) {
          RepositoryItem giftlistAddress = 
            (RepositoryItem) ((RepositoryItem)giftlist).getPropertyValue(glTools.getShippingAddressProperty());
          
          if(giftlistAddress != null) {
            
            if(profileAddress.getRepositoryId().equals(giftlistAddress.getRepositoryId())) {
              
              String giftlistName = (String) ((RepositoryItem)giftlist).getPropertyValue(glTools.getEventNameProperty());
              addFormException(MSG_ERR_DELETE_GIFT_ADDRESS, 
                  new String[] { addressNickname, giftlistName}, getAbsoluteName(), pRequest);
            }
          }
        }
      }
        
    } catch (CommerceException e) {
        if (isLoggingError()){
          logError("Can't get Gift Lists ", e);
        }    
    }
    
    // If checkout progress state is BILLING or the current profile is transient and the address 
    // has been chosen as the current shipping address, the address shouldn't be deleted.
    if (CheckoutProgressStates.DEFAULT_STATES.BILLING.toString().equals(getCheckoutProgressStates().getCurrentLevel()) || 
        getProfile().isTransient()) {
      ShippingGroup shippingGroup = getShippingGroupMapContainer().getShippingGroup(addressNickname);
      if (shippingGroup != null){
        int itemRelationshipCount =  shippingGroup.getCommerceItemRelationshipCount();
        if (itemRelationshipCount > 0) {
          addFormException(MSG_ERR_DELETE_SHIPPING_ADDRESS, new String[] { addressNickname}, getAbsoluteName(), pRequest);
        }
      }
    }
  } 

  /**
   * Operation called just after the user's password is changed. If the were no 
   * errors during password changing, Updated Password email should be send.
   * 
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  @Override
  protected void postChangePassword(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException 
  {
    sendEmail(pRequest, pResponse);    
  }
  
  /**
   * Send email to current user using configured TemplateEmailInfo and TemplateEmailSender.
   * 
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void sendEmail(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException{
    //Chech that there were no errors during password changing.
    if ((checkFormError(getChangePasswordErrorURL(), pRequest, pResponse)) == STATUS_SUCCESS) {
      
      getTemplateEmailInfo().setSiteId(SiteContextManager.getCurrentSiteId());
      
      Profile profile = getProfile();
      ProfileTools ptools = getProfileTools();
      try {
        ptools.sendEmailToUser(profile,isSendEmailInSeparateThread(),isPersistEmails(),getTemplateEmailSender(),
            getTemplateEmailInfo(), null);
      } catch (TemplateEmailException exc) {
        String msg = formatUserMessage(MSG_ERR_SENDING_EMAIL, pRequest);
        addFormException(new DropletException(msg, exc, MSG_ERR_SENDING_EMAIL));      
        if (isLoggingError()){
          logError(exc);
        }
        exc.printStackTrace();
      }
    }
  }
  
  /**
   * Validates the values of the mBirthDate property. If all of them are set
   * birthDate will be updated.
   */
  protected void validateBirthDate() {
    Integer day = null;
    Integer month = null;
    Integer year = null;
    
    try {
      day = Integer.parseInt(mDate);
      month = Integer.parseInt(mMonth);
      year = Integer.parseInt(mYear);
    }
    /*
     *  Not all birth day properies are set. As they are
     *  optional no error will be displayed
     */
    catch (NumberFormatException nfe){
      vlogDebug("Birth day, month or year is not set. As this parameters are optinal no error message will be displayed.");
      return;
    }
    
    if (mBirthDate != null) {
      mBirthDate.set(year, month, day); 
    }
    else {
      mBirthDate = Calendar.getInstance();
      mBirthDate.set(year, month, day); 
    }
  }
}
