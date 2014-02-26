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
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;

import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.droplet.GenericFormHandler;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.email.TemplateEmailException;

/**
 * Generic form handler for email sending.
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/email/GenericEmailSenderFormHandler.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class GenericEmailSenderFormHandler extends GenericFormHandler {
  
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/email/GenericEmailSenderFormHandler.java#2 $$Change: 768606 $";

  public static final String MSG_ACTION_SUCCESS = "E-mail has been sent successfully. Check your inbox.";
  
  /**
   * Success URL.
   */
  private String mSuccessURL;
  /**
   * Error URL.
   */
  private String mErrorURL;
  /**
   * Template URL.
   */
  String mTemplateUrl = null;
  /**
   * Recipient name.
   */
  private String mRecipientName = null;
  /**
   * Recipient e-mail.
   */
  private String mRecipientEmail = null;
  /**
   * Sender name.
   */
  private String mSenderName = null;
  /**
   * Sender e-mail.
   */
  private String mSenderEmail = null;
  /**
   * Message.
   */
  private String mMessage = null;
  
  /**
   * Site id.
   */
  private String mSiteId = null;

  //--------------------------------------------------
  // property: Subject
  private String mSubject;

  /**
   * @return the String
   */
  public String getSubject() {
    return mSubject;
  }

  /**
   * @param pSubject the String to set
   */
  public void setSubject(String pSubject) {
    mSubject = pSubject;
  }

  /**
   * @return the URL of the success page.
   */
  public String getSuccessURL() {
    return mSuccessURL;
  }

  /**
   * Sets the URL of the success page.
   * 
   * @param pSuccessURL -
   *          the URL of the success page
   */
  public void setSuccessURL(String pSuccessURL) {
    if (StringUtils.isBlank(pSuccessURL)) {
      mSuccessURL = null;
    } else {
      mSuccessURL = pSuccessURL;
    }
  }

  /**
   * @return the URL of the error page.
   */
  public String getErrorURL() {
    return mErrorURL;
  }

  /**
   * Sets the URL of the error page.
   * 
   * @param pErrorURL -
   *          the URL of the error page
   */
  public void setErrorURL(String pErrorURL) {
    if (StringUtils.isBlank(pErrorURL)) {
      mErrorURL = null;
    } else {
      mErrorURL = pErrorURL;
    }
  }

  /**
   * Gets the value of the RecipientName: field.
   * 
   * @return the value of the RecipientName: field.
   */
  public String getRecipientName() {
    return mRecipientName;
  }

  /**
   * Sets the value of the RecipientName: field.
   * 
   * @param pRecipientName -
   *          the value of the RecipientName: field.
   */
  public void setRecipientName(String pRecipientName) {
    mRecipientName = pRecipientName;
  }

  /**
   * Gets the value of the RecipientEmail: field.
   * 
   * @return the value of the RecipientEmail: field.
   */
  public String getRecipientEmail() {
    return mRecipientEmail;
  }

  /**
   * Sets the value of the RecipientEmail: field.
   * 
   * @param pRecipientEmail -
   *          the value of the RecipientEmail: field.
   */
  public void setRecipientEmail(String pRecipientEmail) {
    if (pRecipientEmail != null){
      mRecipientEmail = pRecipientEmail.trim();  
    }
  }

  /**
   * Gets the value of the SenderName: field.
   * 
   * @return the value of the SenderName: field.
   */
  public String getSenderName() {
    return mSenderName;
  }

  /**
   * Sets the value of the SenderName: field.
   * 
   * @param pSenderName -
   *          the value of the SenderName: field.
   */
  public void setSenderName(String pSenderName) {
    mSenderName = pSenderName;
  }

  /**
   * Gets the value of the SenderEmail: field.
   * 
   * @return the value of the SenderEmail: field.
   */
  public String getSenderEmail() {
    return mSenderEmail;
  }

  /**
   * Sets the value of the SenderEmail: field.
   * 
   * @param pSenderEmail -
   *          the value of the SenderEmail: field.
   */
  public void setSenderEmail(String pSenderEmail) {
    mSenderEmail = pSenderEmail.trim();
  }
  
  /**
   * Gets the value of the SiteId: field.
   * 
   * @return the value of the SiteId: field.
   */
  public String getSiteId() {
    return mSiteId;
  }

  /**
   * Sets the value of the SiteId: field.
   * 
   * @param pSiteId -
   *          the value of the SiteId: field.
   */
  public void setSiteId(String pSiteId) {
    mSiteId = pSiteId;
  }

  /**
   * Gets the value of the Message: field.
   * 
   * @return the value of the Message: field.
   */
  public String getMessage() {
    return mMessage;
  }

  /**
   * Sets the value of the Message: field.
   * 
   * @param pMessage -
   *          the value of the Message: field.
   */
  public void setMessage(String pMessage) {
    mMessage = pMessage;
  }

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
   * Message parameter name.
   */
  private String mMessageParamName = null;
  
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
   * SiteId parameter name.
   */
  private String mSiteIdParamName = null;
  
  /**
   * Gets the name of the parameter used for the SiteId: field. This is
   * configured in the component property file.
   * 
   * @return the name of the parameter used for the SiteId: field.
   */
  public String getSiteIdParamName() {
    return mSiteIdParamName;
  }

  /**
   * Sets the name of the parameter used for the SiteId: field. This is
   * configured in the component property file.
   * 
   * @param pSiteIdParamName -
   *          the name of the parameter used for the Comment: field.
   */
  public void setSiteIdParamName(String pSiteIdParamName) {
    mSiteIdParamName = pSiteIdParamName;
  }


  
  /**
   * @return the ProfileTools.
   */
  public ProfileTools getProfileTools() {
    return getProfile().getProfileTools();
  }

  // --------------------------------------------------
  // property: EmailTools
  private StoreEmailTools mEmailTools;

  /**
   * @return the StoreEmailTools
   */
  public StoreEmailTools getEmailTools() {
    return mEmailTools;
  }

  /**
   * @param pEmailTools
   *          the StoreEmailTools to set
   */
  public void setEmailTools(StoreEmailTools pEmailTools) {
    mEmailTools = pEmailTools;
  }
  
  /**
   * Profile.
   */
  private Profile mProfile = null;

  /**
   * Sets The user profile associated with the email. The default profile is
   * used here. This is configured in the component property file.
   * 
   * @param pProfile -
   *          the user profile of the logged in user.
   */
  public void setProfile(Profile pProfile) {
    mProfile = pProfile;
  }

  /**
   * Gets the user profile associated with the email. The default profile is
   * used here. This is configured in the component property file.
   * 
   * @return the user profile of the logged in user.
   */
  public Profile getProfile() {
    return mProfile;
  }
  
  /**
   * Resource bundle name.
   */
  private String mResourceBundleName;

  
  /**
   * Resource bundle.
   */
  private ResourceBundle mResourceBundle;
  
  /**
   * @return the location of the resources bundle.
   */
  public String getResourceBundleName() {
    return mResourceBundleName;
  }

  /**
   * @param pResourceBundleName -
   *          the location of the resource bundle.
   */
  public void setResourceBundleName(String pResourceBundleName) {
    mResourceBundleName = pResourceBundleName;
  }
  
  /**
   * @param pRequest the request
   * @return the request specific resources bundle.
   */
  public ResourceBundle getResourceBundle(DynamoHttpServletRequest pRequest) {
    if (mResourceBundle == null) {
      mResourceBundle = LayeredResourceBundle.getBundle(getResourceBundleName(), 
                                                        pRequest.getRequestLocale().getLocale());
    }

    return mResourceBundle;
  }
  
  //--------------------------------------------------
  // property: SubjectParamName
  private String mSubjectParamName;

  /**
   * @return the String
   */
  public String getSubjectParamName() {
    return mSubjectParamName;
  }

  /**
   * @param pSubjectParamName the String to set
   */
  public void setSubjectParamName(String pSubjectParamName) {
    mSubjectParamName = pSubjectParamName;
  }
  
  //--------------------------------------------------
  // property: ActionResult
  private String mActionResult;

  /**
   * @return the String
   */
  public String getActionResult() {
    return mActionResult;
  }

  /**
   * @param pActionResult the String to set
   */
  public void setActionResult(String pActionResult) {
    mActionResult = pActionResult;
  }

  /**
   * Handles the form submit and sends the email.
   * 
   * @param pRequest -
   *          http request
   * @param pResponse -
   *          http response
   * @return true on success, false - otherwise
   * @throws IOException an error occurred reading data from the request
   * or writing data to the response.
   * @throws ServletException an application specific error occurred
   * processing this request  
   */
  public boolean handleSend(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException  {
    if (isLoggingDebug()) {
      logDebug("GenericEmailSenderFormHandler - [handleSend] = Entered in method \n");
    }

    if (!checkFormRedirect(null, getErrorURL(), pRequest, pResponse)){
      return false;
    }

    boolean isValidRecipientEmailFormat = getEmailTools().validateEmailAddress(getRecipientEmail());

    if (!isValidRecipientEmailFormat) {
      String msg = ResourceUtils.getMsgResource(StoreEmailTools.MSG_INVALID_RECIPIENT_EMAIL_FORMAT,
                                                getResourceBundleName(),
                                                getResourceBundle(pRequest));

      addFormException(new DropletFormException(msg, null));

    }

    boolean isValidSenderEmailFormat = getEmailTools().validateEmailAddress(
        getSenderEmail());

    if (!isValidSenderEmailFormat) {
      String msg = ResourceUtils.getMsgResource(StoreEmailTools.MSG_INVALID_SENDER_EMAIL_FORMAT,
                                                getResourceBundleName(),
                                                getResourceBundle(pRequest));

      addFormException(new DropletFormException(msg, null));
    }
    
    if (!checkFormRedirect(null, getErrorURL(), pRequest, pResponse)){
      return false;
    }

    try {
      // send email
      getEmailTools().sendEmail(getProfile(), collectParams());
      
      //set action result message
      setActionResult(MSG_ACTION_SUCCESS);
      
    } catch (TemplateEmailException e) {
      processException(e, "emailFormHandler.send.TemplateEmailException",
                       pRequest, pResponse);
    } catch (RepositoryException re) {
      processException(re, "emailFormHandler.send.RepositoryException",
                       pRequest, pResponse);
    }

    if (isLoggingDebug()) {
      logDebug("GenericEmailSenderFormHandler - [handleSend] = After sendEmail()");
    }

    return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
  }
  
  /**
   * Collect parameters for e-mail templates
   * @return map of parameters
   */
  protected Map collectParams() {
    // collect params from form handler to map and pass them into tools class
    Map emailParams = new HashMap();
    emailParams.put(getTemplateUrlName(), getTemplateUrl());
    emailParams.put(getSenderNameParamName(), getSenderName());
    emailParams.put(getSenderEmailParamName(), getSenderEmail());
    emailParams.put(getRecipientNameParamName(), getRecipientName());
    emailParams.put(getRecipientEmailParamName(), getRecipientEmail());
    emailParams.put(getMessageParamName(), getMessage());
    emailParams.put(getSubjectParamName(), getSubject());
    emailParams.put(getSiteIdParamName(), getSiteId());
    
    return emailParams;
  }

  /**
   * Add a user error message to the form exceptions.
   * 
   * @param pException - exception to process
   * @param pMsgId - message id
   * @param pRequest - http request
   * @param pResponse - http response
   */
  public void processException(Throwable pException, String pMsgId,
      DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
    String msg = ResourceUtils.getMsgResource(pMsgId,
                                              getResourceBundleName(),
                                              getResourceBundle(pRequest));

    // If there is a message in the exception then add that
    if (pException != null) {
      String msg2 = pException.getLocalizedMessage();

      if (!StringUtils.isBlank(msg2)) {
        msg += (" " + msg2);
      }
    }

    DropletException de;

    if (pException == null) {
      de = new DropletException(msg);
    } else {
      de = new DropletException(msg, pException, pMsgId);
    }

    addFormException(de);

    if (isLoggingDebug()) {
      logDebug(pException);
    }
  }
}
