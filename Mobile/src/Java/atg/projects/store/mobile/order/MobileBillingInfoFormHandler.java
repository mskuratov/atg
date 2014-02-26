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
package atg.projects.store.mobile.order;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.core.util.ContactInfo;
import atg.droplet.DropletException;
import atg.projects.store.mobile.userprofiling.MobileStoreProfileTools;
import atg.projects.store.order.purchase.BillingInfoFormHandler;
import atg.projects.store.profile.SessionBean;
import atg.projects.store.profile.StoreProfileFormHandler;
import atg.projects.store.profile.StorePropertyManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * Mobile Billing handler
 * 
 * @see BillingInfoFormHandler
 */
public class MobileBillingInfoFormHandler extends BillingInfoFormHandler {

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Mobile/src/atg/projects/store/mobile/order/MobileBillingInfoFormHandler.java#5 $$Change: 794871 $";

  private SessionBean mSessionBean;
  protected MobileStoreProfileTools mProfileTools;
  protected String mDispatchCSV;

  /** Possible values of dispatch parameter, used for delegating work to the {@link StoreProfileFormHandler CRS-billing handler} on the page, where CSV code is provided*/
  private static final String DISPATCH_BILLING_WITH_SAVED_ADDRESS_AND_NEW_CARD  = "newCardSelectedAddress";
  private static final String DISPATCH_BILLING_WITH_NEW_ADDRESS_AND_NEW_CARD    = "newCardNewAddress";
  private static final String DISPATCH_BILLING_WITH_SAVED_CARD                  = "selectCard";
  
  /** Key-name of property in map, defining if created card should be saved or not */
  private static final String KEY_NAME_IS_CARD_SAVED = "saveCreditCard";
  
  /** Key-name of property in map, defining if created billable address should be saved or not */
  private static final String KEY_NAME_IS_ADRESS_SAVED = "saveBillingAddress";
  
  private StorePropertyManager mProfilePropertyManager;
  
  /**
   * Returns session bean
   * 
   * @return the session bean
   */
  public SessionBean getSessionBean() {
    return mSessionBean;
  }

  /**
   * Sets value for the session bean
   * 
   * @param pSessionBean the session bean to set
   */
  public void setSessionBean(SessionBean pSessionBean) {
    mSessionBean = pSessionBean;
  }

  /**
   * Returns dispatch parameter value 
   * 
   * @return dispatch parameter value
   */
  public String getDispatchCSV() {
	return mDispatchCSV;
  }

  /**
   * Sets new value for the dispatch parameter
   * 
   * @param pDispatchCSV new value of dispatch parameter to set
   */
  public void setDispatchCSV(String pDispatchCSV) {
    mDispatchCSV = pDispatchCSV;
  }
  
  /**
   * Method is called when user provides csv code and clicks Continue. <br>
   * Note, that providing of card info on ui in CRS-M consists of several steps:
   * <ol>
   * <li>Credit card information providing (card type like VISA or MasterCard, card number etc.)
   * <li>Billing address providing (firstname, lastname, address)
   * <li>CSV code providing
   * </ol>
   *  
   * Method delegates all handling to one of the parent methods, that are used in non mobile CRS-application and make all work in one step. These are:
   * <ul>
   * <li>{@link #handleBillingWithSavedAddressAndNewCard handleBillingWithSavedAddressAndNewCard},
   * <li>{@link #handleBillingWithNewAddressAndNewCard handleBillingWithNewAddressAndNewCard} or
   * <li>{@link #handleBillingWithSavedCard handleBillingWithSavedCard}
   * </ul>
   * They are called according to the {@link #mDispatchCSV dispatch} paremeter, specified in request.<br><br>
   * 
   * On success method makes redirect to {@link #getMoveToConfirmSuccessURL() moveToConfirmSuccessURL} <br>
   * On failure methods makes redirect to {@link #getMoveToConfirmErrorURL() moveToConfirmErrorURL} <br>
   * <br>
   * 
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @throws ServletException if there was an error while executing the code
   * @throws IOException if there was an error with servlet io
   * @throws CommerceException if an error occurs
   * @return true if success, false - otherwise
   * @see #handleBillingWithNewAddressAndNewCard handleBillingWithNewAddressAndNewCard
   * @see #handleBillingWithSavedCard handleBillingWithSavedCard
   * @see #handleBillingWithSavedAddressAndNewCard handleBillingWithSavedAddressAndNewCard
   * @see #getMoveToConfirmSuccessURL() moveToConfirmSuccessURL
   * @see #getMoveToConfirmErrorURL() moveToConfirmErrorURL
   */
  public boolean handleMoveToConfirm(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws IOException, ServletException, CommerceException {
    
    // Check if session has expired, redirect to sessionExpired URL.
    // Note, that handler may be called from INA-client through REST-platform and 'checkFormRedirect'-method doesn't 
    // work in this case and as a result it doesn't see expired session.
    // As a workaround, we add additional checks for session expirations (for INA only) in the different places below. 
    if (!checkFormRedirect(null, getMoveToConfirmErrorURL(), pRequest, pResponse)) {
      if (isLoggingDebug()) {
        logDebug("Form error at beginning of handleMoveToConfirm, redirecting.");
      }
      return false;
    } 
    
    String dispatch = getDispatchCSV();

    if (DISPATCH_BILLING_WITH_SAVED_ADDRESS_AND_NEW_CARD.equals(dispatch)) {
      Map cardInfo = getProfileTools().restoreCreditCardFromSession(false, getSessionBean());
      
      // Additional check for expired session (for INA)
      if (cardInfo == null) {
        addFormExceptionSessionExpired();
        return false;
      }
	        
      StorePropertyManager propertyManager = getProfilePropertyManager();
      setCreditCardNickname       ((String) cardInfo.get(propertyManager.getCreditCardNicknamePropertyName()));
      setCreditCardNumber         ((String) cardInfo.get(propertyManager.getCreditCardNumberPropertyName()));
      setCreditCardType           ((String) cardInfo.get(propertyManager.getCreditCardTypePropertyName()));
      setCreditCardExpirationMonth((String) cardInfo.get(propertyManager.getCreditCardExpirationMonthPropertyName()));
      setCreditCardExpirationYear ((String) cardInfo.get(propertyManager.getCreditCardExpirationYearPropertyName()));
      setSaveCreditCard (getProfile().isTransient() ? false : Boolean.parseBoolean( (String) cardInfo.get(KEY_NAME_IS_CARD_SAVED)));
  
      initializeBillingAddressSelection(); // contains initialize inside
 
      return handleBillingWithSavedAddressAndNewCard(pRequest, pResponse);
  }
    else if (DISPATCH_BILLING_WITH_NEW_ADDRESS_AND_NEW_CARD.equals(dispatch)) {
      Map cardInfo = getProfileTools().restoreCreditCardFromSession(false, getSessionBean());
      
      // Additional check for expired session (for INA)
      if (cardInfo == null) {
        addFormExceptionSessionExpired();
        return false;
      }

      StorePropertyManager propertyManager = getProfilePropertyManager();
      setCreditCardNickname       ((String) cardInfo.get(propertyManager.getCreditCardNicknamePropertyName()));
      setCreditCardNumber         ((String) cardInfo.get(propertyManager.getCreditCardNumberPropertyName()));
      setCreditCardType           ((String) cardInfo.get(propertyManager.getCreditCardTypePropertyName()));
      setCreditCardExpirationMonth((String) cardInfo.get(propertyManager.getCreditCardExpirationMonthPropertyName()));
      setCreditCardExpirationYear ((String) cardInfo.get(propertyManager.getCreditCardExpirationYearPropertyName()));
      setSaveCreditCard (Boolean.parseBoolean( (String) cardInfo.get(KEY_NAME_IS_CARD_SAVED)));
 
      Map addrInfo = getProfileTools().restoreBillingAddressFromSession(getSessionBean());
      setSaveBillingAddress(getProfile().isTransient() ? false : Boolean.parseBoolean( (String) addrInfo.get(KEY_NAME_IS_ADRESS_SAVED)));
      ContactInfo cinfo = new ContactInfo();
      cinfo.setFirstName  ( (String) addrInfo.get(propertyManager.getAddressFirstNamePropertyName()));
      cinfo.setLastName   ( (String) addrInfo.get(propertyManager.getAddressLastNamePropertyName()));
      cinfo.setAddress1   ( (String) addrInfo.get(propertyManager.getAddressLineOnePropertyName()));
      cinfo.setAddress2   ( (String) addrInfo.get(propertyManager.getAddressLineTwoPropertyName()));
      cinfo.setCity       ( (String) addrInfo.get(propertyManager.getAddressCityPropertyName()));
      cinfo.setState      ( (String) addrInfo.get(propertyManager.getAddressStatePropertyName()));
      cinfo.setCountry    ( (String) addrInfo.get(propertyManager.getAddressCountryPropertyName()));
      cinfo.setPhoneNumber( (String) addrInfo.get(propertyManager.getAddressPhoneNumberPropertyName()));
      cinfo.setPostalCode ( (String) addrInfo.get(propertyManager.getAddressPostalCodePropertyName()));
      setCreditCardBillingAddress(cinfo);
      
      return handleBillingWithNewAddressAndNewCard(pRequest, pResponse);
    }
    else if (DISPATCH_BILLING_WITH_SAVED_CARD.equals(dispatch)){
      // Additional check for expired session (for INA)
      if (getOrder().getPriceInfo() == null){
        addFormExceptionSessionExpired();
        return false;
      }
      
      setCreditCardVerificationNumber( getNewCreditCardVerificationNumber() );
      return handleBillingWithSavedCard(pRequest, pResponse);
    }
    else {
      return handleBillingWithStoreCredit(pRequest, pResponse);
    }
  }
  
  /**
   * Adds form exception with code 'sessionExpired' and 'Session expiration' text.<br>
   * Note, that it copies behaviour of {@link atg.droplet.GenericFormHandler#checkFormRedirect}<br><br>
   * 
   * Method was separated for additional checks of session-expiration cases in INA-client, that isn't able to detect 'session expiration' nor in {@link atg.droplet.GenericFormHandler#checkFormRedirect} 
   * either in {@link atg.commerce.order.purchase.PurchaseProcessFormHandler#checkFormRedirect(String, String, DynamoHttpServletRequest, DynamoHttpServletResponse)}.  
   */
  private void addFormExceptionSessionExpired(){
    addFormException(new DropletException("Your session expired since this form was displayed - please try again.", "sessionExpired"));
  }
  
  /**
   * Defines credit card number and type (VISA, MasterCard etc.) for viewing on the CSV page.<br><br>
   * 
   * Note, that for a new credit card these values are taken from the session, <br>
   * for existent card these values are taken from the repository by credit card nick name. 
   * 
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return always true
   * @throws ServletException if there was an error while executing the code
   * @throws IOException if there was an error with servlet io
   */
  public boolean handlePrepareCreditCardNumberAndType(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
     throws IOException, ServletException {

    MobileStoreProfileTools profileTools = getProfileTools();
    StorePropertyManager propertyManager = getProfilePropertyManager();
    String creditCardNumberPropertyName = propertyManager.getCreditCardNumberPropertyName();
    String creditCardTypePropertyName = propertyManager.getCreditCardTypePropertyName();
    
    String dispatch = pRequest.getParameter("dispatchCSV");
    
    if (DISPATCH_BILLING_WITH_SAVED_ADDRESS_AND_NEW_CARD.equals(dispatch) 
        || DISPATCH_BILLING_WITH_NEW_ADDRESS_AND_NEW_CARD.equals(dispatch) ) {
      Map cardInfo = profileTools.restoreCreditCardFromSession(false, getSessionBean());

      setCreditCardNumber((String) cardInfo.get(creditCardNumberPropertyName));
      setCreditCardType  ((String) cardInfo.get(creditCardTypePropertyName));
    }
    else if (DISPATCH_BILLING_WITH_SAVED_CARD.equals(dispatch)){
      String nickname = getStoredCreditCardName();
      
      RepositoryItem cardInfo = profileTools.getCreditCardByNickname(nickname, getProfile());
      if (cardInfo != null) {
        setCreditCardNumber((String) cardInfo.getPropertyValue(creditCardNumberPropertyName));
        setCreditCardType((String) cardInfo.getPropertyValue(creditCardTypePropertyName));
      }        
    }
    
    return true;
  }

  /**
   * Returns MobileProfileTools
   * 
   * @return The MobileProfileTools instance.
   */
  public MobileStoreProfileTools getProfileTools() {
    return mProfileTools;
  }
  
  /**
   * Sets the new value for the profile tools 
   *
   * @param pProfileTools value for profile tools
   */
  public void setProfileTools(MobileStoreProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }
  
  /**
   * @return the mProfilePropertyManager
   */
  public StorePropertyManager getProfilePropertyManager() {
    return mProfilePropertyManager;
  }

  /**
   * @param pProfilePropertyManager the profilePropertyManager to set
   */
  public void setProfilePropertyManager(
      StorePropertyManager pProfilePropertyManager) {
    mProfilePropertyManager = pProfilePropertyManager;
  }
  
}
