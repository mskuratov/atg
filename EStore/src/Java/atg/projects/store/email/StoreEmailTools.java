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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.TransactionManager;

import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.projects.store.multisite.StoreSitePropertiesManager;
import atg.projects.store.profile.StoreProfileTools;
import atg.projects.store.profile.StorePropertyManager;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.dynamo.LangLicense;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;

/**
 * This utility class defines various utility methods that are used by
 * Store for processing email.
 * <p>  
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/email/StoreEmailTools.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreEmailTools extends ApplicationLoggingImpl {
  
  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/email/StoreEmailTools.java#3 $$Change: 788278 $";

  //-------------------------------------
  // Constants
  // -------------------------------------
  private static final String YES = "yes";
  
  /** Constant used to indicate that the form has no errors. * */
  public final static int STATUS_SUCCESS = 0;

  /**
   * Constant used to indicate that the form has errors, and that the form
   * should not perform a redirect.
   */
  public final static int STATUS_ERROR_STAY = 1;

  /**
   * Constant used to indicate that the form has errors, and that the form
   * should perform a redirect.
   */
  public final static int STATUS_ERROR_REDIRECT = 2;

  /**
   * Resource key for invalid recipient e-mail format.
   */
  public final static String MSG_INVALID_RECIPIENT_EMAIL_FORMAT = "invalidRecipientEmailFormat";

  /**
   * Resource key for invalid sender e-mail format.
   */
  public final static String MSG_INVALID_SENDER_EMAIL_FORMAT = "invalidSenderEmailFormat";

  /**
   * Resource key for e-mail subject.
   */
  public final static String EMAIL_SUBJECT = "emailSubject";
  
  /**
   * Invalid e-mail address constant.
   */
  public static final String MSG_ERR_BAD_EMAIL = "invalidEmailAddress";

  /**
   * Error creating e-mail recipient message key.
   */
  public static final String MSG_ERR_CREATING_EMAIL_RECIPIENT = "errorCreatingEmailRecipient";
  
  /**
   * No sender address message key.
   */
  public static final String MSG_NO_SENDER_EMAIL = "NoSenderEmail";

  /**
   * Invalid sender message key.
   */
  public static final String MSG_INVALID_SENDER_EMAIL = "InvalidSenderEmail";

  /**
   * Missing recipient address message key.
   */
  public static final String MSG_NO_RECIPIENT_EMAIL = "NoRecipientEmail";

  /**
   * Invalid recipient address message key.
   */
  public static final String MSG_INVALID_RECIPIENT_EMAIL = "InvalidRecipientEmail";

  /**
   * Empty message body message key.
   */
  public static final String MSG_EMPTY_MESSAGE = "EmptyMessage";

  /**
   * Host name is missing message key.
   */
  public static final String MSG_NO_EMAIL_HOST_NAME = "NoEmailHostName";

  /**
   * Sender first name is missing message key.
   */
  public static final String MSG_NO_SENDER_FIRST_NAME = "NoSenderFirstName";

  /**
   * Sender last name is missing message key.
   */
  public static final String MSG_NO_SENDER_LAST_NAME = "NoSenderLastName";

  /**
   * Subject is empty message key.
   */
  public static final String MSG_EMPTY_SUBJECT = "EmptySubject";
  
  //-------------------------------------
  // Properties
  // -------------------------------------
  
  //-------------------------------------
  // property: StoreProfileTools
  private StoreProfileTools mProfileTools;
  
  /**
   * @return profile tools.
   */
  public StoreProfileTools getProfileTools() {
    return mProfileTools;
  }

  /**
   * @param pProfileTools - profile tools.
   */
  public void setProfileTools(StoreProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }
  
  //--------------------------------------------------
  // property: DefaultLocale
  private String mDefaultLocale = "en_US";

  /**
   * @return the String
   */
  public String getDefaultLocaleString() {
    return mDefaultLocale;
  }

  /**
   * @param pDefaultLocale the String to set
   */
  public void setDefaultLocale(String pDefaultLocale) {
    mDefaultLocale = pDefaultLocale;
  }
  
  //--------------------------------------------------
  // property: TransactionManager
  private TransactionManager mTransactionManager ;
  
  /**
  * Sets the Transaction Manager.
  * @param pTransactionManager transactionManager to set
  */
  public void setTransactionManager(TransactionManager pTransactionManager)
  {
    mTransactionManager = pTransactionManager;
  }
  /**
  * Transaction Manager
  * @beaninfo description: The Transaction Manager.
  * @return the mTransactionManager
  */
  public TransactionManager getTransactionManager()
  {
    return mTransactionManager;
  }
  
  /**
   * Recipient name parameter name.
   */
  private String mRecipientNameParamName = null;

  /**
   * E-mail recipient parameter name.
   */
  private String mRecipientEmailParamName = null;

  /**
   * Sender parameter name.
   */
  private String mSenderNameParamName = null;

  /**
   * E-mail sender parameter name.
   */
  private String mSenderEmailParamName = null;

  /**
   * Message parameter name.
   */
  private String mMessageParamName = null;

  /**
   * Product id parameter name.
   */
  private String mProductIdParamName = null;

  /**
   * Subject parameter name.
   */
  private String mSubjectParamName = null;

  /**
   * Profile parameter name.
   */
  private String mProfileParamName = null;

  /**
   * Locale parameter name - it represents the name of locale parameter to be
   * used in Email template.
   */
  private String mLocaleParamName = "locale";

  /**
   * Gets the name of the Name value used for the To: field. This is configured
   * in the component property file.
   * 
   * @return the name of the Name value used for the To: field.
   */
  public String getRecipientNameParamName() {
    return mRecipientNameParamName;
  }

  /**
   * Sets the name of the Name value used for the To: field. This is configured
   * in the component property file.
   * 
   * @param pRecipientNameParamName -
   *          the name of the Name value used for the To: field.
   */
  public void setRecipientNameParamName(String pRecipientNameParamName) {
    mRecipientNameParamName = pRecipientNameParamName;
  }

  /**
   * Gets the name of the Email value used for the To: field. This is configured
   * in the component property file.
   * 
   * @return the name of the Email value used for the To: field.
   */
  public String getRecipientEmailParamName() {
    return mRecipientEmailParamName;
  }

  /**
   * Sets the name of the Email value used for the To: field. This is configured
   * in the component property file.
   * 
   * @param pRecipientEmailParamName -
   *          the name of the Email value used for the To: field.
   */
  public void setRecipientEmailParamName(String pRecipientEmailParamName) {
    mRecipientEmailParamName = pRecipientEmailParamName;
  }

  /**
   * Gets the name of the Name value used for the From: field. This is
   * configured in the component property file.
   * 
   * @return the name of the Name value used for the from field.
   */
  public String getSenderNameParamName() {
    return mSenderNameParamName;
  }

  /**
   * Sets the name of the Name value used for the From: field. This is
   * configured in the component property file.
   * 
   * @param pSenderNameParamName -
   *          the name of the Name value used for the From: field.
   */
  public void setSenderNameParamName(String pSenderNameParamName) {
    mSenderNameParamName = pSenderNameParamName;
  }

  /**
   * Gets the name of the Email value used for the From: field. This is
   * configured in the component property file.
   * 
   * @return the name of the Email value used for the From: field.
   */
  public String getSenderEmailParamName() {
    return mSenderEmailParamName;
  }

  /**
   * Sets the name of the Email value used for the From: field. This is
   * configured in the component property file.
   * 
   * @param pSenderEmailParamName -
   *          the name of the Email value used for the From: field.
   */
  public void setSenderEmailParamName(String pSenderEmailParamName) {
    mSenderEmailParamName = pSenderEmailParamName;
  }

  /**
   * Gets the name of the parameter used for the Message: field. This is
   * configured in the component property file.
   * 
   * @return the name of the parameter used for the Message: field.
   */
  public String getMessageParamName() {
    return mMessageParamName;
  }

  /**
   * Sets the name of the parameter used for the Message: field. This is
   * configured in the component property file.
   * 
   * @param pMessageParamName -
   *          the name of the parameter used for the Comment: field.
   */
  public void setMessageParamName(String pMessageParamName) {
    mMessageParamName = pMessageParamName;
  }

  /**
   * Gets the name of the parameter used for the ProductId: field. This is
   * configured in the component property file.
   * 
   * @return the name of the parameter used for the ProductId: field.
   */
  public String getProductIdParamName() {
    return mProductIdParamName;
  }

  /**
   * Sets the name of the parameter used for the ProductId: field. This is
   * configured in the component property file.
   * 
   * @param pProductIdParamName -
   *          the name of the parameter used for the ProductId: field.
   */
  public void setProductIdParamName(String pProductIdParamName) {
    mProductIdParamName = pProductIdParamName;
  }

  /**
   * Gets the name of the parameter used for the Subject: field. This is
   * configured in the component property file.
   * 
   * @return the name of the parameter used for the Subject: field.
   */
  public String getSubjectParamName() {
    return mSubjectParamName;
  }

  /**
   * Sets the name of the parameter used for the Subject: field. This is
   * configured in the component property file.
   * 
   * @param pSubjectParamName -
   *          the name of the parameter used for the Subject: field.
   */
  public void setSubjectParamName(String pSubjectParamName) {
    mSubjectParamName = pSubjectParamName;
  }

  /**
   * Gets the name of the parameter used to send the current user profile to the
   * template. This is configured in the component property file.
   * 
   * @return The name of the parameter used to send the current user profile to
   *         the template.
   */
  public String getProfileParamName() {
    return mProfileParamName;
  }

  /**
   * Sets the name of the parameter used to send the current user profile to the
   * template. This is configured in the component property file.
   * 
   * @param pProfileParamName
   *          The name of the parameter used to send the current user profile to
   *          the template.
   */
  public void setProfileParamName(String pProfileParamName) {
    mProfileParamName = pProfileParamName;
  }


  /**
   * @param pLocaleParamName -
   *          locale parameter name.
   */
  public void setLocaleParamName(String pLocaleParamName) {
    mLocaleParamName = pLocaleParamName;
  }

  /**
   * @return the value of property getEmailParamName.
   */
  public String getLocaleParamName() {
    return mLocaleParamName;
  }

  private TemplateEmailInfo mDefaultEmailInfo;
  /**
   * Sets the default email information. This is configured in the component
   * property file.
   * 
   * @param pDefaultEmailInfo -
   *          the default email information
   */
  public void setDefaultEmailInfo(TemplateEmailInfo pDefaultEmailInfo) {
    mDefaultEmailInfo = pDefaultEmailInfo;
  }

  /**
   * Gets the default email information. This is configured in the component
   * property file.
   * 
   * @return the default email information
   */
  public TemplateEmailInfo getDefaultEmailInfo() {
    return mDefaultEmailInfo;
  }
  
  /**
   * Template URL.
   */
  String mTemplateUrl = null;
  
  /**
   * Sets the URL for the email template used to send the email. This is
   * configured in the component property file.
   * 
   * @param pTemplateUrl -
   *          the URL
   */
  public void setTemplateUrl(String pTemplateUrl) {
    mTemplateUrl = pTemplateUrl;
  }

  /**
   * Gets the URL for the email template used to send the email. This is
   * configured in the component property file.
   * 
   * @return the URL
   */
  public String getTemplateUrl() {
    return mTemplateUrl;
  }
  
  private boolean mSendEmailInSeparateThread;
  /**
   * Sets boolean indicating whether the email is sent in a separate thread.
   * 
   * @param pSendEmailInSeparateThread
   *          boolean indicating whether the email is sent in a separate thread.
   * @beaninfo description: boolean indicating whether the email is sent in a
   *           separate thread.
   */
  public void setSendEmailInSeparateThread(boolean pSendEmailInSeparateThread) {
    mSendEmailInSeparateThread = pSendEmailInSeparateThread;
  }

  /**
   * @return boolean indicating whether the email is sent in a separate thread.
   */
  public boolean isSendEmailInSeparateThread() {
    return mSendEmailInSeparateThread;
  }
  
  private boolean mPersistEmails;
  
  /**
   * Sets boolean indicating whether the email is persisted before it is sent.
   * 
   * @param pPersistEmails
   *          boolean indicating whether the email is persisted before it is
   *          sent.
   * @beaninfo description: boolean indicating whether the email is persisted
   *           before it is sent.
   */
  public void setPersistEmails(boolean pPersistEmails) {
    mPersistEmails = pPersistEmails;
  }

  /**
   * @return boolean indicating whether the email is persisted before it is
   *         sent.
   */
  public boolean isPersistEmails() {
    return mPersistEmails;
  }
  
  private TemplateEmailSender mEmailSender;
  
  /**
   * Sets the email send component. This is configured in the component property
   * file.
   * 
   * @param pEmailSender -
   *          the email send component
   */
  public void setEmailSender(TemplateEmailSender pEmailSender) {
    mEmailSender = pEmailSender;
  }

  /**
   * Gets the email send component. This is configured in the component property
   * file.
   * 
   * @return the email send component
   */
  public TemplateEmailSender getEmailSender() {
    return mEmailSender;
  }
    
  private String mProductItemName;
  
  /**
   * Gets the name of the product item.
   * 
   * @return the name of the product item.
   */
  public String getProductItemName() {
    return mProductItemName;
  }

  /**
   * Sets the name of the product item.
   * 
   * @param pProductItemName -
   *          the name of the product item.
   */
  public void setProductItemName(String pProductItemName) {
    mProductItemName = pProductItemName;
  }
  
  /**
   * Catalog repository.
   */
  private MutableRepository mCatalogRepository = null;
  
  /**
   * Gets the value of the catalogRepository field.
   * 
   * @return the value of the catalogRepository field.
   */
  public MutableRepository getCatalogRepository() {
    return mCatalogRepository;
  }

  /**
   * Sets the value of the catalogRepository: field.
   * 
   * @param pCatalogRepository -
   *          the value of the catalogRepository: field.
   */
  public void setCatalogRepository(MutableRepository pCatalogRepository) {
    mCatalogRepository = pCatalogRepository;
  }
  
  /**
   * Order repository.
   */
  private MutableRepository mOrderRepository = null;
  
  /**
   * Gets the value of the orderRepository field.
   * 
   * @return the value of the orderRepository field.
   */
  public MutableRepository getOrderRepository() {
    return mOrderRepository;
  }

  /**
   * Sets the value of the orderRepository: field.
   * 
   * @param pOrderRepository -
   *          the value of the orderRepository: field.
   */
  public void setOrderRepository(MutableRepository pOrderRepository) {
    mOrderRepository = pOrderRepository;
  }

  /**
   * Store name.
   */
  private String mStoreNameResource = null;
  
  /**
   * @return the storeNameResource.
   */
  public String getStoreNameResource() {
    return mStoreNameResource;
  }

  /**
   * @param pStoreNameResource
   *          The storeNameResource to set.
   */
  public void setStoreNameResource(String pStoreNameResource) {
    mStoreNameResource = pStoreNameResource;
  }
  
  private String mDisplayNamePropertyName = null;
  
  /**
   * Gets the name of the Display Name property.
   * 
   * @return the name of the Display Name property.
   */
  public String getDisplayNamePropertyName() {
    return mDisplayNamePropertyName;
  }

  /**
   * Sets the name of the Display Name property.
   * 
   * @param pDisplayNamePropertyName -
   *          the name of the Display Name property.
   */
  public void setDisplayNamePropertyName(String pDisplayNamePropertyName) {
    mDisplayNamePropertyName = pDisplayNamePropertyName;
  }

  /**
   * Resource bundle name.
   */
  private String mEmailAFriendResourceBundleName;

  /**
   * Resource bundle.
   */
  private ResourceBundle mEmailAFriendResourceBundle;
  
  /**
   * @return the location of the Email a Friend resources bundle.
   */
  public String getEmailAFriendResourceBundleName() {
    return mEmailAFriendResourceBundleName;
  }

  /**
   * @param pEmailAFriendResourceBundleName -
   *          the location of the Email a Friend resource bundle.
   */
  public void setEmailAFriendResourceBundleName(
      String pEmailAFriendResourceBundleName) {
    mEmailAFriendResourceBundleName = pEmailAFriendResourceBundleName;
  }

  /**
   * @return the Email a Friend resources bundle.
   */
  public ResourceBundle getEmailAFriendResourceBundle() {
    if (mEmailAFriendResourceBundle == null) {
      mEmailAFriendResourceBundle = LayeredResourceBundle.getBundle(getEmailAFriendResourceBundleName(), 
                                                                    LangLicense.getLicensedDefault());
    }

    return mEmailAFriendResourceBundle;
  }
  
  //--------------------------------------------------
  // property: SiteIdParamName
  private String mSiteIdParamName="siteId";

  /**
  * The siteId parameter name - it represents the Site ID from which the email is dispatched
  * parameter to be used in Email template
  * @param pSiteIdParamName siteId parameter name
  */
  public void setSiteIdParamName(String pSiteIdParamName)
  {
    mSiteIdParamName = pSiteIdParamName;
  }

  /**
  * @return the value of property SiteIdParamName
  */
  public String getSiteIdParamName()
  {
    return mSiteIdParamName;
  }
  
  //--------------------------------------------------
  // property: mTemplateUrlName
  private String mTemplateUrlName;

  /**
   * @return the String
   */
  public String getTemplateUrlName() {
    return mTemplateUrlName;
  }

  /**
   * @param pTemplateUrlName the String to set
   */
  public void setTemplateUrlName(String pTemplateUrlName) {
    mTemplateUrlName = pTemplateUrlName;
  }
  
  //-------------------------------------
  // property: StoreSitePropertiesManager
  //-------------------------------------
  protected StoreSitePropertiesManager mStoreSitePropertiesManager;

  /**
   * @return the StoreSitePropertiesManager
   */
  public StoreSitePropertiesManager getStoreSitePropertiesManager() {
    return mStoreSitePropertiesManager;
  }

  /**
   * @param pStoreSitePropertiesManager the StoreSitePropertiesManager to set
   */
  public void setStoreSitePropertiesManager(StoreSitePropertiesManager pStoreSitePropertiesManager) {
    mStoreSitePropertiesManager = pStoreSitePropertiesManager;
  }
  
  //-------------------------------------
  // property: PromoTemplateUrls
  //-------------------------------------
  private List<String> mPromoTemplateUrls;
  
  /**
   * @return the mPromoTemplateUrls
   */
  public List<String> getPromoTemplateUrls() {
    return mPromoTemplateUrls;
  }

  /**
   * @param pPromoTemplateUrls the promoTemplateUrls to set
   */
  public void setPromoTemplateUrls(List<String> pPromoTemplateUrls) {
    mPromoTemplateUrls = pPromoTemplateUrls;
  }

  //-------------------------------------
  // property: PropertyManager
  //-------------------------------------
  StorePropertyManager mPropertyManager;
  
  /**
   * @return the mPropertyManager
   */
  public StorePropertyManager getPropertyManager() {
    return mPropertyManager;
  }

  /**
   * @param pPropertyManager the propertyManager to set
   */
  public void setPropertyManager(StorePropertyManager pPropertyManager) {
    mPropertyManager = pPropertyManager;
  }

  /**
   * Check to see if Email format is correct.
   *
   * @param pEmailAddress - e-mail address
   * @return true if format is correct, false - otherwise
   */
  public boolean validateEmailAddress(String pEmailAddress) {
    if (pEmailAddress == null) {
      return false;
    }
    
    String emailFormat = getProfileTools().getEmailFormat();
    //Set the email pattern string
    Pattern p = Pattern.compile(emailFormat);
    //Match the given string with the pattern
    Matcher m = p.matcher(pEmailAddress);
    //check whether match is found
    return m.matches();
  }

 
  
  /**
   * Compose the sender tag as: pFirstName pLastName <pEmail>
   * 
   * @param pFirstName sender's first name
   * @param pLastName sender's last name
   * @param pEmail sender's email
   * @return String as: pFirstName pLastName <pEmail>
   */
  public String createSender(String pFirstName, String pLastName, String pEmail) {
    StringBuilder sender = new StringBuilder();
    
    if ((pFirstName != null) && (pLastName != null) && (pEmail != null)) {
      sender.append(pFirstName).append(" ").append(pLastName).append(" <").append(pEmail).append(">");
    }
    
    return sender.toString();
  }
  
  /**
   * Create string of view: <code>pName</code>&lt;<code>pEmail</code>&gt;
   * 
   * @param pName the name
   * @param pEmail the email
   * @return  the string of view: <code>pName</code>&lt;<code>pEmail</code>&gt;
   */
  public String createQualifiedString(String pName, String pEmail) {
    StringBuilder qualified = new StringBuilder();
    return qualified.append(pName.trim()).append("<").append(pEmail).append(">").toString();
  }
  
  /**
   * Prepare email info template.
   * Initialize basic parameters: template URL, subject, from:, to: 
   * 
   * @param pEmailParams the email parameters
   * @return TemplateEmailInfoImpl object with preset fields 
   * @throws RepositoryException indicates that a severe error occured while performing a Repository task.
   */
  public TemplateEmailInfoImpl prepareEmailInfo(Map pEmailParams) throws RepositoryException {
    TemplateEmailInfoImpl emailInfo = (TemplateEmailInfoImpl) getDefaultEmailInfo().copy();
    
    //set the template
    emailInfo.setTemplateURL((String)pEmailParams.get(getTemplateUrlName()));
    
    // set the email header properties.
    // if the from: was specified set it otherwise use the default.
    String senderEmail = (String)pEmailParams.get(getSenderEmailParamName());
    
    if (!StringUtils.isEmpty(senderEmail)) {
      String qualifiedFrom = createQualifiedString((String)pEmailParams.get(getSenderNameParamName()), senderEmail);
      emailInfo.setMessageFrom(qualifiedFrom);
    }
    
    // if the Subject: was specified set it otherwise use the default.
    String subject = (String)pEmailParams.get(getSubjectParamName());

    if (!StringUtils.isEmpty(subject)) {
      emailInfo.setMessageSubject(subject);
    }

    // if the To: was specified set it
    String recipientEmail = (String)pEmailParams.get(getRecipientEmailParamName());
    
    if (!StringUtils.isEmpty(recipientEmail)) {
      String qualifiedRecipient = createQualifiedString((String)pEmailParams.get(getRecipientNameParamName()), recipientEmail);
      emailInfo.setMessageTo(qualifiedRecipient);
    }
    
    // site site ID of email template
    // if the Site ID: was specified set it otherwise use the current one.
    String siteId = (String)pEmailParams.get(getSiteIdParamName());

    if (!StringUtils.isEmpty(siteId)) {
      emailInfo.setSiteId(siteId);
    }else{
      emailInfo.setSiteId(SiteContextManager.getCurrentSiteId());
    }

    return emailInfo;
  }
  
  /**
   * Prepare email parameters before sending
   * 
   * @param pProfile the profile
   * @param pEmailInfo email template
   * @param pEmailParams map of parameters
   * @return the prepared email parameters 
   */
  public Map prepareEmailInfoParams(Profile pProfile, TemplateEmailInfoImpl pEmailInfo, Map pEmailParams) {
    // set the email template properties
    HashMap params = new HashMap();
    params.put(getSenderEmailParamName(), pEmailInfo.getMessageFrom());
    params.put(getRecipientEmailParamName(), pEmailInfo.getMessageTo());
    params.put(getSubjectParamName(), pEmailInfo.getMessageSubject());

    params.put(getSenderNameParamName(), pEmailParams.get(getSenderNameParamName()));
    params.put(getRecipientNameParamName(), pEmailParams.get(getRecipientNameParamName()));
    params.put(getMessageParamName(), pEmailParams.get(getMessageParamName()));
    params.put(getProductIdParamName(), pEmailParams.get(getProductIdParamName()));
    params.put(getProfileParamName(), pProfile.getDataSource());
    
    // Determine current user's locale
    Locale userLocale = ServletUtil.getUserLocale();
    String locale = null;

    if (userLocale != null) {
      locale = userLocale.getLanguage() + "_" + userLocale.getCountry();
    }

    if (StringUtils.isEmpty(locale)) {
      locale = getDefaultLocaleString();
    }

    params.put(getLocaleParamName(), locale);
    
    String siteId = (String)pEmailParams.get(getSiteIdParamName());
    if(!StringUtils.isEmpty(siteId)) {
      params.put(getSiteIdParamName(), siteId);
    }
    
    return params;
  }
  
  /**
   * Prepare email and send it.
   * 
   * @param pProfile the profile 
   * @param pEmailParams parameters
   * @throws TemplateEmailException occurs while composing or sending a piece of template-based email
   * @throws RepositoryException indicates that a severe error occured while performing a Repository task
   */
  public void sendEmail(Profile pProfile, Map pEmailParams) throws TemplateEmailException, RepositoryException {
    TemplateEmailInfoImpl emailInfo = prepareEmailInfo(pEmailParams);
    emailInfo.setTemplateParameters(prepareEmailInfoParams(pProfile, emailInfo, pEmailParams));
    
    List recipents = addRecipients(pProfile, emailInfo);
    
    // check if template in the list of promo templates 
    String currentTemplateURL = emailInfo.getTemplateURL();

    List<String> templateUrls = getPromoTemplateUrls();
    if(templateUrls != null) {
      for(String templateUrl : templateUrls) {
        if(currentTemplateURL.endsWith(templateUrl)) {
          // check if profile.receivePromoEmails property is true
          // if yes, then allow to send this type of email
          String receivePromoEmails = (String)pProfile.getPropertyValue(getPropertyManager().getReceivePromoEmailPropertyName());
          if(receivePromoEmails.equals(YES)) {
            getEmailSender().sendEmailMessage(emailInfo, 
                recipents,
                isSendEmailInSeparateThread(),
                isPersistEmails());
            
            return;
          }
        }
      }
    }
    
    // send the email
    getEmailSender().sendEmailMessage(emailInfo, 
                                      recipents,
                                      isSendEmailInSeparateThread(),
                                      isPersistEmails());
  }

  /**
   * Returns list of recipients
   * 
   * @param pProfile the profile
   * @param pEmailInfo the email info
   * @return list of recipients
   */
  protected List addRecipients(Profile pProfile, TemplateEmailInfoImpl pEmailInfo) {
    List recipents = new ArrayList();
    recipents.add(pEmailInfo.getMessageTo());
    return recipents;
  }
  
  
  
  /**
   * Compute the subject for the email message with Product Name as part of
   * subject.
   * 
   * @param pProductId - product id
   * @return subject
   * @throws atg.repository.RepositoryException if error occurs
   */
  protected String getSubject(String pProductId) throws RepositoryException {
    MutableRepository catalogRepository = getCatalogRepository();
    RepositoryItem productItem = catalogRepository.getItem(pProductId,
        getProductItemName());
    String productName = (String) productItem.getPropertyValue(getDisplayNamePropertyName());
    
    String storeName = ResourceUtils.getMsgResource(getStoreNameResource(),
          (String)SiteContextManager.getCurrentSiteContext().getSite().getPropertyValue(
              getStoreSitePropertiesManager().getResourceBundlePropertyName()), null);
    String[] fmtSubjectKey = { productName, storeName };
    String emailSubjectVal = ResourceUtils.getMsgResource(EMAIL_SUBJECT,
                                                          getEmailAFriendResourceBundleName(),
                                                          getEmailAFriendResourceBundle(),
                                                          fmtSubjectKey);
    // return computed subject
    return emailSubjectVal;
  }
}
