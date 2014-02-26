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
package atg.projects.store.mobile.userprofiling;

import java.io.IOException;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.servlet.ServletException;

import atg.core.i18n.LayeredResourceBundle;
import atg.projects.store.profile.StoreProfileFormHandler;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * Mobile Profile form handler.
 *
 * @see atg.projects.store.profile.StoreProfileFormHandler
 */
public class MobileStoreProfileFormHandler extends StoreProfileFormHandler {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Mobile/src/atg/projects/store/mobile/userprofiling/MobileStoreProfileFormHandler.java#3 $$Change: 768606 $";


  protected static ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  private String mSelectedBillingAddress;
  private String mCreateBillingAddressSuccessURL;
  private String mCreateBillingAddressErrorURL;
  
  private MobileStoreProfileTools mMobileProfileTools;

  public String getSelectedBillingAddress() {
    return mSelectedBillingAddress;
  }

  public void setSelectedBillingAddress(String pSelectedBillingAddress) {
    mSelectedBillingAddress = pSelectedBillingAddress;
  }

  public String getCreateBillingAddressSuccessURL() {
    return mCreateBillingAddressSuccessURL;
  }

  public void setCreateBillingAddressSuccessURL(String pCreateBillingAddressSuccessURL) {
    mCreateBillingAddressSuccessURL = pCreateBillingAddressSuccessURL;
  }

  public String getCreateBillingAddressErrorURL() {
    return mCreateBillingAddressErrorURL;
  }
  
  public void setCreateBillingAddressErrorURL(String pCreateBillingAddressErrorURL) {
	  mCreateBillingAddressErrorURL = pCreateBillingAddressErrorURL;
  }

  /**
   * Returns instance of MobileProfileTools 
   * 
   * @return MobileProfileTools
   */
  public MobileStoreProfileTools getMobileProfileTools() {
    return mMobileProfileTools;
  }

  /**
   * Sets value for MobileStoreProfileTools
   * 
   * @return The value of the property MobileProfileTools.
   */
  public void setMobileProfileTools(MobileStoreProfileTools pMobileStoreProfileTools){
    mMobileProfileTools = pMobileStoreProfileTools;
  }

  /**
   * Method prepares credit card information and nickname of the selected address in it's internal map structures: {@link #getEditValue() editValue} and {@link #getBillAddrValue() billAddrValue}. <br><br>
   * 
   * Note, that creating of new credit card with the selected address in CRS-M consists of 2 steps:
   * <ol> 
   * <li>Card info providing (card type like VISA or MasterCard, card number etc.)
   * <li>Billing address (firstname, lastname, street etc.) selection from the list.
   * </ol> 
    * Non mobile CRS application makes in one step in {@link #handleCreateNewCreditCard(DynamoHttpServletRequest, DynamoHttpServletResponse) handleCreateNewCreditCard}.<br><br>
   * So, all {@link #preCreateNewCreditCard(DynamoHttpServletRequest, DynamoHttpServletResponse) preCreateNewCreditCard} method does, 
   * it prepares context for calling of {@link #handleCreateNewCreditCard(DynamoHttpServletRequest, DynamoHttpServletResponse) handleCreateNewCreditCard}.<br><br>
   * 
   * @see #handleCreateNewCreditCard(DynamoHttpServletRequest, DynamoHttpServletResponse)  handleCreateNewCreditCard
   * @see #getEditValue() editValue - map, containing credit card info
   * @see #getBillAddrValue() billAddrValue - map, containing billable address info
   */
  @Override
  protected void preCreateNewCreditCard(DynamoHttpServletRequest pRequest,
                                        DynamoHttpServletResponse pResponse)
    throws ServletException, IOException  {
    super.preCreateNewCreditCard(pRequest, pResponse);
    
    getEditValue().putAll(getMobileProfileTools().restoreCreditCardFromSession(false, getSessionBean()));
    getBillAddrValue().put(getNewNicknameValueMapKey(), getSelectedBillingAddress());
  }

  /**
   * Method prepares credit card information in it's internal map structure: {@link #getEditValue() editValue}. <br>
   * New billable address information should be in {@link #getBillAddrValue() billAddrValue} while calling of this method.<br><br>
   * 
   * Note, that creating of new credit card with the new billable address in CRS-M consists of 2 steps:
   * <ol> 
   * <li>Card info providing (card type like VISA or MasterCard, card number etc.)
   * <li>New billable address providing (firstname, lastname, street etc.).
   * </ol> 
   * Non mobile CRS application makes in one step in {@link #handleCreateNewCreditCardAndAddress(DynamoHttpServletRequest, DynamoHttpServletResponse) handleCreateNewCreditCardAndAddress}.<br><br>
   * So, all {@link #preCreateNewCreditCardAndAddress(DynamoHttpServletRequest, DynamoHttpServletResponse) preCreateNewCreditCardAndAddress} method does, 
   * it prepares context for calling of {@link #handleCreateNewCreditCardAndAddress(DynamoHttpServletRequest, DynamoHttpServletResponse) handleCreateNewCreditCardAndAddress}.<br><br>
   * 
   * @see #handleCreateNewCreditCardAndAddress(DynamoHttpServletRequest, DynamoHttpServletResponse) handleCreateNewCreditCardAndAddress
   * @see #getEditValue() editValue - map, containing credit card info
   * @see #getBillAddrValue() billAddrValue - map, containing billable address info
   */
  @Override
  protected void preCreateNewCreditCardAndAddress(DynamoHttpServletRequest pRequest, 
                                                  DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    super.preCreateNewCreditCardAndAddress(pRequest, pResponse);
    getEditValue().putAll(getMobileProfileTools().restoreCreditCardFromSession(false, getSessionBean()));	
  }

  /**
   * Validates credit card information, provided in {@link #getEditValue() editValue} and on success stores it as a map in user 
   * session with encrypted card number for security purpose, then makes redirect to {@link #getCreateCardSuccessURL() createCardSuccessURL}, specified in JSP. <br>
   * If validation failed, makes redirect to {@link #getCreateCardErrorURL() createCardErrorURL}, specified in JSP.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @return true if success, false - otherwise
   * @see StoreProfileFormHandler#validateCreateCreditCardInformation validateCreateCreditCardInformation
   * @see #getCreateCardSuccessURL() createCardSuccessURL
   * @see #getCreateCardErrorURL() createCardErrorURL
   */
  public boolean handleStoreNewCreditCardDataWithoutAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    if(!validateCreateCreditCardInformation(pRequest, pResponse, false)) {
      return checkFormRedirect(null, getCreateCardErrorURL(), pRequest, pResponse);
    }
    
    MobileStoreProfileTools profileTools = getMobileProfileTools();
    profileTools.storeCreditCardToSession(getEditValue(), getSessionBean());

    return checkFormRedirect(getCreateCardSuccessURL(), getCreateCardErrorURL(), pRequest, pResponse);
  }

  /**
   * Validates billing address info (first name, last name, address), provided in {@link #getBillAddrValue() billAddrValue} and
   * on success stores it as a map in a user session, then makes redirect to the {@link #getCreateBillingAddressSuccessURL() createBillingAddressSuccessURL}. <br>
   * If validation failed, makes redirect to the {@link #getCreateBillingAddressErrorURL() createBillingAddressErrorURL}
   * 
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @return true if success, false - otherwise
   * @see StoreProfileFormHandler#validateBillingAddressFields validateBillingAddressFields
   * @see StoreProfileFormHandler#validateCountryStateCombination validateCountryStateCombination
   * @see #getCreateBillingAddressSuccessURL() createBillingAddressSuccessURL
   * @see #getCreateBillingAddressErrorURL() createBillingAddressErrorURL
   */
  public boolean handleCreateBillingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
     throws ServletException, IOException {

    HashMap newAddress = (HashMap) getBillAddrValue();

    validateBillingAddressFields(pRequest, pResponse);
    validateCountryStateCombination(newAddress, pRequest, pResponse);

    if (getFormError()) {
      return checkFormRedirect(null, getCreateBillingAddressErrorURL(), pRequest, pResponse);
    }

    MobileStoreProfileTools profileTools = getMobileProfileTools();
    profileTools.storeBillingAddressToSession(getBillAddrValue(), getSessionBean());

    return checkFormRedirect(getCreateBillingAddressSuccessURL(), getCreateBillingAddressErrorURL(), pRequest, pResponse);
  }
  
  /**
   * Sets all redirection properties to redirect in case of card or address save success.
   * 
   * @param pSuccessURL The URL to redirect in case of success.
   * @see #getNewAddressSuccessURL() newAddressSuccessURL
   * @see #getUpdateAddressSuccessURL() updateAddressSuccessURL
   * @see #getCreateCardSuccessURL() createCardSuccessURL
   * @see #getUpdateCardSuccessURL() updateCardSuccessURL
   * @see #getRemoveCardSuccessURL() removeCardSuccessURL
   */
  public void setSuccessURL(String pSuccessURL) {
    setNewAddressSuccessURL(pSuccessURL);
    setUpdateAddressSuccessURL(pSuccessURL);
    setCreateCardSuccessURL(pSuccessURL);
    setUpdateCardSuccessURL(pSuccessURL);
    setRemoveCardSuccessURL(pSuccessURL);
  }
  
  /**
   * Sets all redirection properties to redirect in case of card or address save errors.
   * 
   * @param pErrorURL The URL to redirect in case of errors.
   * @see #getNewAddressErrorURL() newAddressErrorURL
   * @see #getUpdateAddressErrorURL() updateAddressErrorURL
   * @see #getCreateCardErrorURL() createCardErrorURL
   * @see #getUpdateCardErrorURL() updateCardErrorURL
   * @see #getRemoveCardErrorURL() removeCardErrorURL
   */
  public void setErrorURL(String pErrorURL) {
    setNewAddressErrorURL(pErrorURL);
    setUpdateAddressErrorURL(pErrorURL);
    setCreateCardErrorURL(pErrorURL);
    setUpdateCardErrorURL(pErrorURL);
    setRemoveCardErrorURL(pErrorURL);
  }
}