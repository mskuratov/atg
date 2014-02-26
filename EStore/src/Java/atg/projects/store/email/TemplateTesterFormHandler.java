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


package atg.projects.store.email;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.csr.returns.ReturnRequest;
import atg.commerce.csr.returns.ReturnTools;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.ShippingGroup;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.droplet.DropletFormException;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.profile.StoreRepositoryProfileItemFinder;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

/**
 * Email templates form handler.
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/email/TemplateTesterFormHandler.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class TemplateTesterFormHandler extends GenericEmailSenderFormHandler {

  /** Class version string **/
  public static final String CLASS_VERSION = 
  "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/email/TemplateTesterFormHandler.java#3 $$Change: 788278 $";
  
  //--------------------------------------------------
  // Constants
  
  // error codes
  public static final String INVALID_SKU_ID_MSG = "invalidSkuIdMessage";
  public static final String INVALID_SHIPPING_GROUP_ID_MSG = "invalidShippingGroupIdMessage";
  public static final String INVALID_ORDER_ID_MSG = "invalidOrderIdMessage";
  public static final String INVALID_PRODUCT_ID_MSG = "invalidProductIdMessage";
  public static final String INVALID_RETURNREQUEST_ID_MSG = "invalidReturnRequestIdMessage";
  public static final String NO_RETURN_ITEMS_QUANTITY_MSG = "noReturnItemsQuantityMessage";
  

  // resource bundle
  private static final String MY_RESOURCE_NAME = "atg.projects.store.email.TemplateTesterResources";
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  public static final String EMAIL_ID_PARAM = "emailId";
  public static final String USER_PROFILE_TYPE = "user";
  public static final String ORDER_ID_PROPERTY_NAME = "orderId";
  public static final String MISSING_REQUIRED_FIELD = "missingRequiredValue";
  
  // success message
  public static final String MSG_ACTION_SENT_EMAIL = "E-mail has been sent successfully. <span>Check your inbox.</span>";

//--------------------------------------------------
  // property: NewPassword
  private String mNewPassword;

  /**
   * @return the String
   */
  public String getNewPassword() {
    return mNewPassword;
  }

  /**
   * @param pNewPassword the String to set
   */
  public void setNewPassword(String pNewPassword) {
    mNewPassword = pNewPassword;
  }
  
  //--------------------------------------------------
  // property: ProfileItemFinder
  private StoreRepositoryProfileItemFinder mProfileItemFinder;

  /**
   * @return the ProfileItemFinder
   */
  public StoreRepositoryProfileItemFinder getProfileItemFinder() {
    return mProfileItemFinder;
  }

  /**
   * @param pProfileItemFinder the ProfileItemFinder to set
   */
  public void setProfileItemFinder(StoreRepositoryProfileItemFinder pProfileItemFinder) {
    mProfileItemFinder = pProfileItemFinder;
  }
  
  //--------------------------------------------------
  // property: OrderManager
  private OrderManager mOrderManager;

  /**
   * @return the OrderManager
   */
  public OrderManager getOrderManager() {
    return mOrderManager;
  }

  /**
   * @param pOrderManager the OrderManager to set
   */
  public void setOrderManager(OrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }
  
  /**
   * ReturnTools
   */
  private ReturnTools mReturnTools;

  /**
   * The service that is used to find the desired returns
   * @param pReturnTools the ReturnTools component. 
   **/
  public void setReturnTools(ReturnTools pReturnTools) {
    mReturnTools = pReturnTools;
  }

  /**
   * The service that is used to find the desired return
   **/
  public ReturnTools getReturnTools() {
    return mReturnTools;
  }
  
  //--------------------------------------------------
  // property: Login
  private String mLogin;

  /**
   * @return the String
   */
  public String getLogin() {
    return mLogin;
  }

  /**
   * @param pLogin the String to set
   */
  public void setLogin(String pLogin) {
    mLogin = pLogin;
  }
  
  //--------------------------------------------------
  // property: Password
  private String mPassword;

  /**
   * @return the String
   */
  public String getPassword() {
    return mPassword;
  }

  /**
   * @param pPassword the String to set
   */
  public void setPassword(String pPassword) {
    mPassword = pPassword;
  }
  
  //--------------------------------------------------
  // property: OrderId
  private String mOrderId;

  /**
   * @return the String
   */
  public String getOrderId() {
    return mOrderId;
  }

  /**
   * @param pOrderId the String to set
   */
  public void setOrderId(String pOrderId) {
    mOrderId = pOrderId;
  }
  
  //--------------------------------------------------
  // property: ReturnRequestId
  private String mReturnRequestId;

  /**
   * @return the String
   */
  public String getReturnRequestId() {
    return mReturnRequestId;
  }

  /**
   * @param pReturnRequestId the String to set
   */
  public void setReturnRequestId(String pReturnRequestId) {
    mReturnRequestId = pReturnRequestId;
  }
  
  //--------------------------------------------------
  // property: ProductId
  private String mProductId;

  /**
   * @return the String
   */
  public String getProductId() {
    return mProductId;
  }

  /**
   * @param pProductId the String to set
   */
  public void setProductId(String pProductId) {
    mProductId = pProductId;
  }
  
  //--------------------------------------------------
  // property: SkuId
  private String mSkuId;

  /**
   * @return the String
   */
  public String getSkuId() {
    return mSkuId;
  }

  /**
   * @param pSkuId the String to set
   */
  public void setSkuId(String pSkuId) {
    mSkuId = pSkuId;
  }
  
  //--------------------------------------------------
  // property: ShippingGroupId
  private String mShippingGroupId;

  /**
   * @return the String
   */
  public String getShippingGroupId() {
    return mShippingGroupId;
  }

  /**
   * @param pShippingGroupId the String to set
   */
  public void setShippingGroupId(String pShippingGroupId) {
    mShippingGroupId = pShippingGroupId;
  }
  
  //--------------------------------------------------
  // property: OrderTools
  private StoreOrderTools mOrderTools;

  /**
   * @return the OrderTools
   */
  public StoreOrderTools getOrderTools() {
    return mOrderTools;
  }

  /**
   * @param pOrderTools the OrderTools to set
   */
  public void setOrderTools(StoreOrderTools pOrderTools) {
    mOrderTools = pOrderTools;
  }
  
  //--------------------------------------------------
  // property: NewPasswordParameterName
  private String mNewPasswordParameterName = "newpassword";

  /**
   * @return the String
   */
  public String getNewPasswordParameterName() {
    return mNewPasswordParameterName;
  }

  /**
   * @param pNewPasswordParameterName the String to set
   */
  public void setNewPasswordParameterName(String pNewPasswordParameterName) {
    mNewPasswordParameterName = pNewPasswordParameterName;
  }
  
  //--------------------------------------------------
  // property: ProductIdParameterName
  private String mProductIdParameterName = "productId";

  /**
   * @return the String
   */
  public String getProductIdParameterName() {
    return mProductIdParameterName;
  }

  /**
   * @param pProductIdParameterName the String to set
   */
  public void setProductIdParameterName(String pProductIdParameterName) {
    mProductIdParameterName = pProductIdParameterName;
  }
  
  //--------------------------------------------------
  // property: SkuIdParameterName
  private String mSkuIdParameterName = "skuId";

  /**
   * @return the String
   */
  public String getSkuIdParameterName() {
    return mSkuIdParameterName;
  }

  /**
   * @param pSkuIdParameterName the String to set
   */
  public void setSkuIdParameterName(String pSkuIdParameterName) {
    mSkuIdParameterName = pSkuIdParameterName;
  }
  
  //--------------------------------------------------
  // property: EmailAFriendMessageParameterName
  private String mEmailAFriendMessageParameterName = "message";

  /**
   * @return the String
   */
  public String getEmailAFriendMessageParameterName() {
    return mEmailAFriendMessageParameterName;
  }

  /**
   * @param pEmailAFriendMessageParameterName the String to set
   */
  public void setEmailAFriendMessageParameterName(String pEmailAFriendMessageParameterName) {
    mEmailAFriendMessageParameterName = pEmailAFriendMessageParameterName;
  }
  
  //--------------------------------------------------
  // property: OrderParameterName
  private String mOrderParameterName = "order";

  /**
   * @return the String
   */
  public String getOrderParameterName() {
    return mOrderParameterName;
  }

  /**
   * @param pOrderParameterName the String to set
   */
  public void setOrderParameterName(String pOrderParameterName) {
    mOrderParameterName = pOrderParameterName;
  }
  
  //--------------------------------------------------
  // property: ReturnRequestParameterName
  private String mReturnRequestParameterName = "returnRequest";

  /**
   * @return the String
   */
  public String getReturnRequestParameterName() {
    return mReturnRequestParameterName;
  }

  /**
   * @param pReturnRequestParameterName the String to set
   */
  public void setReturnRequestParameterName(String pReturnRequestParameterName) {
    mReturnRequestParameterName = pReturnRequestParameterName;
  }
  
  //--------------------------------------------------
  // property: ReturnItemToQuantityReceivedParameterName
  private String mReturnItemToQuantityReceivedParameterName = "returnItemToQuantityReceived";

  /**
   * @return the String
   */
  public String getReturnItemToQuantityReceivedParameterName() {
    return mReturnItemToQuantityReceivedParameterName;
  }

  /**
   * @param pReturnItemToQuantityReceivedParameterName the String to set
   */
  public void setReturnItemToQuantityReceivedParameterName(String pReturnItemToQuantityReceivedParameterName) {
    mReturnItemToQuantityReceivedParameterName = pReturnItemToQuantityReceivedParameterName;
  }
  
  //--------------------------------------------------
  // property: ShippingGroupParameterName
  private String mShippingGroupParameterName = "shippingGroup";

  /**
   * @return the String
   */
  public String getShippingGroupParameterName() {
    return mShippingGroupParameterName;
  }

  /**
   * @param pShippingGroupParameterName the String to set
   */
  public void setShippingGroupParameterName(String pShippingGroupParameterName) {
    mShippingGroupParameterName = pShippingGroupParameterName;
  }
  
  //--------------------------------------------------
  // property: OrderRecipientEmail
  private String mOrderRecipientEmail;

  /**
   * @return the String
   */
  public String getOrderRecipientEmail() {
    return mOrderRecipientEmail;
  }

  /**
   * @param pOrderRecipientEmail the String to set
   */
  public void setOrderRecipientEmail(String pOrderRecipientEmail) {
    mOrderRecipientEmail = pOrderRecipientEmail;
  }
  
  //--------------------------------------------------
  // property: OrderRecipientName
  private String mOrderRecipientName;

  /**
   * @return the String
   */
  public String getOrderRecipientName() {
    return mOrderRecipientName;
  }

  /**
   * @param pOrderRecipientName the String to set
   */
  public void setOrderRecipientName(String pOrderRecipientName) {
    mOrderRecipientName = pOrderRecipientName;
  }
  
  //--------------------------------------------------
  // property: OrderSenderName
  private String mOrderSenderName;

  /**
   * @return the String
   */
  public String getOrderSenderName() {
    return mOrderSenderName;
  }

  /**
   * @param pOrderSenderName the String to set
   */
  public void setOrderSenderName(String pOrderSenderName) {
    mOrderSenderName = pOrderSenderName;
  }
  
  //--------------------------------------------------
  // property: OrderSenderEmail
  private String mOrderSenderEmail;

  /**
   * @return the String
   */
  public String getOrderSenderEmail() {
    return mOrderSenderEmail;
  }
  
  /**
   * @param pOrderSenderEmail the String to set
   */
  public void setOrderSenderEmail(String pOrderSenderEmail) {
    mOrderSenderEmail = pOrderSenderEmail;
  }
  
  //--------------------------------------------------
  // property: OrderSubject
  private String mOrderSubject;

  /**
   * @return the String
   */
  public String getOrderSubject() {
    return mOrderSubject;
  }

  /**
   * @param pOrderSubject the String to set
   */
  public void setOrderSubject(String pOrderSubject) {
    mOrderSubject = pOrderSubject;
  }
  
  //--------------------------------------------------
  // property: OrderMessage
  private String mOrderMessage;

  /**
   * @return the Type
   */
  public String getOrderMessage() {
    return mOrderMessage;
  }

  /**
   * @param pOrderMessage the Type to set
   */
  public void setOrderMessage(String pOrderMessage) {
    mOrderMessage = pOrderMessage;
  }
  
  //--------------------------------------------------
  // property: EmailAFriendMessage
  private String mEmailAFriendMessage;

  /**
   * @return the String
   */
  public String getEmailAFriendMessage() {
    return mEmailAFriendMessage;
  }

  /**
   * @param pEmailAFriendMessage the String to set
   */
  public void setEmailAFriendMessage(String pEmailAFriendMessage) {
    mEmailAFriendMessage = pEmailAFriendMessage;
  }

  //--------------------------------------------------
  // property: EmailLocale
  private String mEmailLocale;
  
  /**
   * @return the mEmailLocale
   */
  public String getEmailLocale() {
    return mEmailLocale;
  }

  /**
   * @param pEmailLocale the emailLocale to set
   */
  public void setEmailLocale(String pEmailLocale) {
    mEmailLocale = pEmailLocale;
  }

  //--------------------------------------------------
  // property: EmailLocaleName
  private String mEmailLocaleName = "locale";
  
  /**
   * @return the mEmailLocaleName
   */
  public String getEmailLocaleName() {
    return mEmailLocaleName;
  }

  /**
   * @param pEmailLocaleName the emailLocaleName to set
   */
  public void setEmailLocaleName(String pEmailLocaleName) {
    mEmailLocaleName = pEmailLocaleName;
  }
  
  //--------------------------------------------------
  // property: ReturnItemToQuantityMap
  private Map<String, String> mReturnItemToQuantityMap = new HashMap<String, String>();
  
  /**
   * @return the mReturnItemToQuantityMap
   */
  public Map<String, String> getReturnItemToQuantityMap() {
    return mReturnItemToQuantityMap;
  }

  /**
   * @param pReturnItemToQuantityMap the ReturnItemToQuantityMap to set
   */
  public void setReturnItemToQuantityMap(Map<String, String> pReturnItemToQuantityMap) {
    mReturnItemToQuantityMap = pReturnItemToQuantityMap;
  }
  
  //--------------------------------------------------
  // property: ApplyOrderSuccessURL
  private String mApplyOrderSuccessURL;
  
  /**
   * @return the mApplyOrderSuccessURL
   */
  public String getApplyOrderSuccessURL() {
    return mApplyOrderSuccessURL;
  }

  /**
   * @param pApplyOrderSuccessURL the applyOrderSuccessURL
   */
  public void setApplyOrderSuccessURL(String pApplyOrderSuccessURL) {
    mApplyOrderSuccessURL = pApplyOrderSuccessURL;
  }
  
  //--------------------------------------------------
  // property: ApplyOrderSuccessURL
  private String mApplyOrderErrorURL;
  
  /**
   * @return the mApplyOrderErrorURL
   */
  public String getApplyOrderErrorURL() {
    return mApplyOrderErrorURL;
  }

  /**
   * @param pApplyOrderErrorURL the applyOrderErrorURL to set
   */
  public void setApplyOrderErrorURL(String pApplyOrderErrorURL) {
    mApplyOrderErrorURL = pApplyOrderErrorURL;
  }
  
  //--------------------------------------------------
  // property: ApplyReturnSuccessURL
  private String mApplyReturnSuccessURL;
  
  /**
   * @return the mApplyReturnSuccessURL
   */
  public String getApplyReturnSuccessURL() {
    return mApplyReturnSuccessURL;
  }

  /**
   * @param pApplyReturnSuccessURL the ApplyReturnSuccessURL
   */
  public void setApplyReturnSuccessURL(String pApplyReturnSuccessURL) {
    mApplyReturnSuccessURL = pApplyReturnSuccessURL;
  }
  
  //--------------------------------------------------
  // property: ApplyReturnSuccessURL
  private String mApplyReturnErrorURL;
  
  /**
   * @return the mApplyReturnErrorURL
   */
  public String getApplyReturnErrorURL() {
    return mApplyReturnErrorURL;
  }

  /**
   * @param pApplyReturnErrorURL the ApplyReturnErrorURL to set
   */
  public void setApplyReturnErrorURL(String pApplyReturnErrorURL) {
    mApplyReturnErrorURL = pApplyReturnErrorURL;
  }
  
  //--------------------------------------------------
  // property: SessionBean
  private TemplateTesterSessionBean mSessionBean;
  
  /**
   * @return mSessionBean 
   */
  public TemplateTesterSessionBean getSessionBean() {
    return mSessionBean;
  }

  /**
   * @param pSessionBean the sessionBean to set
   */
  public void setSessionBean(TemplateTesterSessionBean pSessionBean) {
    mSessionBean = pSessionBean;
  }
  
  /* (non-Javadoc)
   * @see atg.projects.store.catalog.GenericEmailSenderFormHandler#getProfile()
   */
  /**
   * Gets the user profile associated with the email. The default profile is used here. 
   * This is configured in the component property file.
   * @return the user profile of the logged in user.
   */
  @Override
  public Profile getProfile() {
    Profile profile = super.getProfile();
    
    //find profile using profile finder
    RepositoryItem repProfile = getProfileItemFinder().findByLogin(getLogin(), getPassword(), USER_PROFILE_TYPE);
    if(repProfile != null ) {
      profile.setDataSource(repProfile);
    }
    
    return profile;
  }
  
  //-------------------------------------
  // ResourceBundle support
  //-------------------------------------

  /**
   * Returns the error message ResourceBundle
   * @return the sResourceBundle
   */
  protected ResourceBundle getResourceBundle() {
    return sResourceBundle;
  }  

  /**
   * Returns the name of the error message ResourceBundle
   * @return the MY_RESOURCE_NAME
   */
  public String getResourceBundleName() {
    return MY_RESOURCE_NAME;
  }

  /* (non-Javadoc)
   * @see atg.projects.store.catalog.GenericEmailSenderFormHandler#collectParams()
   */
  /**
   * Collect parameters for e-mail templates
   * @return map of parameters
   */
  @Override
  protected Map collectParams() {
    Map emailParams = super.collectParams();
    
    // add params for testing purposes
    emailParams.put(getNewPasswordParameterName(), getNewPassword());
    emailParams.put(getSkuIdParameterName(), getSkuId());
    emailParams.put(getEmailAFriendMessageParameterName(), getEmailAFriendMessage());
    
    String productId = getProductId();
    
    if (!StringUtils.isEmpty(getSkuId()) && StringUtils.isEmpty(productId)){
      //SKU ID is specified but product ID is not, determine product ID from SKU.
      productId = ((TesterEmailTools)getEmailTools()).getProductIdForSKU(getSkuId());
    }
    emailParams.put(getProductIdParameterName(), productId);
    
    // external recipient name
    emailParams.put(getRecipientNameParamName(), getRecipientName());
    
    // check for external locale
    String emailLocaleStr = getEmailLocale();
    if(!StringUtils.isEmpty(emailLocaleStr)) {
      emailParams.put(getEmailLocaleName(), emailLocaleStr);
    }
    
    // load order 
    String orderId = getOrderId();
    if(!StringUtils.isEmpty(orderId)) { 
      Order order;
      try {
        order = getOrderManager().loadOrder(orderId);
        emailParams.put(getOrderParameterName(), order);
        
        String shippingGroupId = getShippingGroupId();
        if(!StringUtils.isEmpty(shippingGroupId)) {
          ShippingGroup sg = order.getShippingGroup(shippingGroupId);
          emailParams.put(getShippingGroupParameterName(), sg);
        }
        
      } catch (CommerceException e) {
        if (isLoggingError()){
          logError("Commerce Exception occur: ", e);
        }
      }
    }
    
    // load return request 
    String returnRequestId = getReturnRequestId();
    if(!StringUtils.isEmpty(returnRequestId)) { 
      ReturnRequest returnRequest;
      try {
        returnRequest = getReturnTools().getReturnRequest(returnRequestId);
        emailParams.put(getReturnRequestParameterName(), returnRequest);
        
        Map<String, Long> returnItemToQuantityReceived = new HashMap<String, Long>();
        for (Map.Entry<String, String> mapEntry : getReturnItemToQuantityMap().entrySet()){
          if (!StringUtils.isEmpty(mapEntry.getValue())){
            long quantity = Long.parseLong(mapEntry.getValue());
            if (quantity > 0){
              returnItemToQuantityReceived.put(mapEntry.getKey(), Long.valueOf(quantity));
            }
          } 
          
        }
        emailParams.put(getReturnItemToQuantityReceivedParameterName(), returnItemToQuantityReceived);
        
      } catch (CommerceException e) {
        if (isLoggingError()){
          logError("Commerce Exception occur: ", e);
        }
      } catch (RepositoryException e) {
      if (isLoggingError()){
          logError("Repository Exception occur: ", e);
        }
      }
    }
    return emailParams;
  }
  
  /**
   * Stores order ID into session bean along with other user's input.
   * 
   * @param pRequest HTTP request
   * @param pResponse HTTP response  
   * @return If redirect (for whatever reason) to a new page occurred, return false. 
   * If NO redirect occurred, return true.
   * @throws ServletException if there was an error while executing the code
   * @throws IOException      if there was an error with servlet io
   */
  public boolean handleApplyOrder(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    // ignore missing required fields exceptions except of the one for orderId property
    resetRequiredFieldsFormExceptions();
    // store form inputs to the session bean
    storeUserInputsIntoSession();
    return checkFormRedirect(getApplyOrderSuccessURL(), getApplyOrderErrorURL(), pRequest, pResponse);
  }
  
  /**
   * Removes all 'missing required field' exceptions except of the one for order ID property.
   * It is necessary when applyOrder handler is used  in forms with several submit buttons. For apply
   * order action only order ID property is required, other required fields will be ignored.
   */
  public void resetRequiredFieldsFormExceptions(){
    for (Iterator iter = getFormExceptions().iterator(); iter.hasNext();){
      DropletFormException ex = (DropletFormException) iter.next();
      if (ex.getErrorCode().equals(MISSING_REQUIRED_FIELD) && !ORDER_ID_PROPERTY_NAME.equals(ex.getPropertyName())){
        iter.remove();
      }
    }
  }
  
  /**
   * Stores return request ID into session bean along with other user's input.
   * 
   * @param pRequest HTTP request
   * @param pResponse HTTP response  
   * @return If redirect (for whatever reason) to a new page occurred, return false. 
   * If NO redirect occurred, return true.
   * @throws ServletException if there was an error while executing the code
   * @throws IOException      if there was an error with servlet io
   */
  public boolean handleApplyReturn(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    // ignore missing required fields exceptions except of the one for orderId property
    resetRequiredFieldsFormExceptions();
    // store form inputs to the session bean
    storeUserInputsIntoSession();
    return checkFormRedirect(getApplyReturnSuccessURL(), getApplyReturnErrorURL(), pRequest, pResponse);
  }
  
  /**
   * Initializes form with values stored in session.
   * @param pRequest HTTP request
   * @param pResponse HTTP response
   * @return true
   * @throws Exception
   */
  public boolean handleInitializeForm(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) {
    
    // load form initial values from session bean
    initializeForm();
    return true;
  }
  
  /**
   * Gets emailId parameter from dynamo http request
   * @param pRequest dynamo http request
   * @return the email id
   */
  private String getEmailId(DynamoHttpServletRequest pRequest){
    String emailId = pRequest.getParameter(EMAIL_ID_PARAM);
    return emailId;
  }
  
  /**
   * Initialize form handler based on information stored
   * in recently sent list
   *  
   * @param pRequest dynamo http request
   * @param pResponse dynamo http response
   * @return true
   * @throws NumberFormatException application has attempted to convert a string 
   * to one of the numeric types, but that the string does not have the appropriate format. 
   */
  public boolean handleInitFromSentList(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws NumberFormatException
{
    String itemIndex = getEmailId(pRequest);
    
    if(!StringUtils.isEmpty(itemIndex)) {
      initFromSentList(Integer.parseInt(itemIndex));
    }  
    return true;
  }

  /**
   * Send email
   * 
   * @param pRequest dynamo http request
   * @param pResponse dynamo http response
   * @return true on success, false - otherwise
   * @throws ServletException if there was an error while executing the code
   * @throws IOException      if there was an error with servlet io
   */
  public boolean handleSend(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)throws IOException, ServletException {
    boolean result = false;

    // validate input parameters
    try {
      validateInputs();
    } 
    catch (RepositoryException re) {
      if (isLoggingError()) {
        logError("Can't perform Repository task ", re);
      }
    }
    catch (CommerceException ce) {
      if (isLoggingError()) {
        logError("Error occurred during inputs validation.", ce);
      }
    }

    result = super.handleSend(pRequest, pResponse);
    // if email is sent successfully clear session values
    if (result) {
      getSessionBean().clearSessionValues();
      // update success message with spans
      setActionResult(MSG_ACTION_SENT_EMAIL);
    }
    return result;
  }

  /**
   * Check that items are acceptable
   * on the selected site 
   * 
   * @throws RepositoryException indicates that a severe error occured while performing a Repository task
 * @throws CommerceException 
   */
  protected void validateInputs() throws RepositoryException, CommerceException {
    TesterEmailTools tools = (TesterEmailTools) getEmailTools();

    // Validation if product id exists.
    if ((getProductId() != null)
        && (!tools.isValidProductId(getProductId(), getSiteId()))) {
      String msg = ResourceUtils.getMsgResource(INVALID_PRODUCT_ID_MSG,
          getResourceBundleName(), getResourceBundle(), 
          new String[] { getProductId(), getSiteId() });
      
      addFormException(new DropletFormException(msg, null));
    }

    // Validation if order id exists.
    if ((getOrderId() != null)
        && (!tools.isValidOrderId(getOrderId(), getSiteId()))) {
      String msg = ResourceUtils.getMsgResource(INVALID_ORDER_ID_MSG,
          getResourceBundleName(), getResourceBundle(), 
          new String[] { getOrderId(), getSiteId() });
      
      addFormException(new DropletFormException(msg, null));
    }
    
    // Validation if return id exists.
    if ((getReturnRequestId() != null)
        && (!tools.isValidReturnRequestId(getReturnRequestId()))) {
      String msg = ResourceUtils.getMsgResource(INVALID_RETURNREQUEST_ID_MSG,
          getResourceBundleName(), getResourceBundle(), 
          new String[] { getReturnRequestId()});
      
      addFormException(new DropletFormException(msg, null));
      
    }    
    
    if (getReturnRequestId()!= null && getReturnItemToQuantityMap().size() != 0){
      long returnCount = 0;
      for (Map.Entry<String, String> mapEntry : getReturnItemToQuantityMap().entrySet()){
        if (!StringUtils.isEmpty(mapEntry.getValue())){
          returnCount += Long.parseLong(mapEntry.getValue());
        }
      } 
      if (returnCount == 0){
        String msg = ResourceUtils.getMsgResource(NO_RETURN_ITEMS_QUANTITY_MSG,
                                                  getResourceBundleName(), getResourceBundle(), null);
        addFormException(new DropletFormException(msg, null));
      }
    }

    // Validation if shipping group id exists.
    if ((getShippingGroupId() != null)
        && (!tools.isValidShippingGroupId(getShippingGroupId(), getSiteId()))) {
      String msg = ResourceUtils.getMsgResource(
          INVALID_SHIPPING_GROUP_ID_MSG, getResourceBundleName(),
          getResourceBundle(),
          new String[] { getShippingGroupId(), getSiteId() });
      
      addFormException(new DropletFormException(msg, null));
    }

    // Validation if SKU id exists.
    if ((getSkuId() != null) && (!tools.isValidSkuId(getSkuId(), getSiteId()))) {
      String msg = ResourceUtils.getMsgResource(INVALID_SKU_ID_MSG,
          getResourceBundleName(), getResourceBundle(), 
          new String[] { getSkuId(), getSiteId() });
      
      addFormException(new DropletFormException(msg, null));
    }
  }
  
  /**
   * Stores user's input into session bean.
   */
  public void storeUserInputsIntoSession(){
    // store user's inputs into the session bean
    TemplateTesterSessionBean sessionBean = getSessionBean();
    sessionBean.addSessionValue(TemplateTesterSessionBean.EMAIL_LOCALE_PROPERTY_NAME, getEmailLocale());
    sessionBean.addSessionValue(TemplateTesterSessionBean.EMAIL_SITE_ID_PROPERTY_NAME, getSiteId());
    sessionBean.addSessionValue(TemplateTesterSessionBean.RECIPIENT_EMAIL_ADDRESS_PROPERTY_NAME, getRecipientEmail());
    sessionBean.addSessionValue(TemplateTesterSessionBean.RECIPIENT_NAME_PROPERTY_NAME, getRecipientName());
    sessionBean.addSessionValue(TemplateTesterSessionBean.ORDER_ID_PROPERTY_NAME, getOrderId());
    sessionBean.addSessionValue(TemplateTesterSessionBean.RETURN_REQUEST_ID_PROPERTY_NAME, getReturnRequestId());
  }
  
  /**
   * Initializes form with values stored in the session bean.
   */
  public void initializeForm(){
    TemplateTesterSessionBean sessionBean = getSessionBean();
    setEmailLocale((String)sessionBean.getSessionValue(TemplateTesterSessionBean.EMAIL_LOCALE_PROPERTY_NAME));
    setSiteId((String)sessionBean.getSessionValue(TemplateTesterSessionBean.EMAIL_SITE_ID_PROPERTY_NAME));
    setRecipientEmail((String)sessionBean.getSessionValue(TemplateTesterSessionBean.RECIPIENT_EMAIL_ADDRESS_PROPERTY_NAME));
    setRecipientName((String)sessionBean.getSessionValue(TemplateTesterSessionBean.RECIPIENT_NAME_PROPERTY_NAME));
    setOrderId((String)sessionBean.getSessionValue(TemplateTesterSessionBean.ORDER_ID_PROPERTY_NAME));    
    setReturnRequestId((String)sessionBean.getSessionValue(TemplateTesterSessionBean.RETURN_REQUEST_ID_PROPERTY_NAME));
  }
  
  /**
   * Initializes form with values stored in the recently sent list.
   * 
   * @param pIndex - index of SentItem object in the list. We use SentItem to initialize
   * form handler fields
   */
  public void initFromSentList(int pIndex){
    TesterEmailTools tools = (TesterEmailTools)getEmailTools();
    RecentlySentList.SentItem sentItem = tools.getSentList().getItem(pIndex);
    
    if(sentItem != null) {
      setEmailLocale((String)sentItem.getParameters().get(tools.LOCALE_PARAMETER));
      setSiteId((String)sentItem.getParameters().get(tools.SITE_PARAMETER));
      setRecipientEmail((String)sentItem.getParameters().get(tools.RECIPIENT_EMAIL_PARAMETER));
      setRecipientName((String)sentItem.getParameters().get(tools.RECIPIENT_NAME_PARAMETER));
      setSkuId((String) sentItem.getParameters().get(tools.SKU_ID_PARAMETER));
      setEmailAFriendMessage((String) sentItem.getParameters().get(tools.EMAIL_A_FRIEND_MESSAGE));
      setProductId((String) sentItem.getParameters().get(tools.PRODUCT_ID_PARAMETER));
      setNewPassword((String) sentItem.getParameters().get(tools.NEWPASSWORD_PARAMETER));
      Object order = sentItem.getParameters().get(tools.ORDER_PARAMETER);
      
      if(order instanceof Order) {
        setOrderId(((Order)order).getId());
      }
      
      Object sg = sentItem.getParameters().get(tools.SHIPPING_GROUP_PARAMETER);
      if(sg instanceof ShippingGroup) {
        setShippingGroupId(((ShippingGroup)sg).getId());
      }
      
      Object returnRequest = sentItem.getParameters().get(tools.RETURN_REQUEST_PARAMETER);
      
      if(returnRequest instanceof ReturnRequest) {
        setReturnRequestId(((ReturnRequest)returnRequest).getRequestId());
      }
    }
  }  
}
