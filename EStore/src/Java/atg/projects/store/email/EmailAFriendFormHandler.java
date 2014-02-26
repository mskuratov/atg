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
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.ResourceUtils;
import atg.droplet.DropletFormException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * Form handler for sending email from the ATG Store website. <br/> The JSP form
 * that accepts the email can directly set the From, Subject and To fields or
 * use the defaults as named in the configuration of the DefaultEmailInfo. <br/>
 * When the form submits the parameters the template is used to format the email
 * and then the EmailSender sends the email. The names for the From, Subject, To
 * and Profile parameters that are sent to the Email Template are set in the
 * configuration and must match the email template. <br/>
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/email/EmailAFriendFormHandler.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class EmailAFriendFormHandler extends GenericEmailSenderFormHandler {
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/email/EmailAFriendFormHandler.java#2 $$Change: 768606 $";

  
  private static final String MESSAGE_TOO_LONG="emailToFriendTooLong";
  /**
   * URL parameters for success page
   */
  private static final String RECIPIENT_NAME = "recipientName";
  private static final String RECIPIENT_EMAIL = "recipientEmail";
  private static final String AMP_SYMBOL = "&";
  private static final String EQUAL_SYMBOL = "=";
    
  
  /**
   * Locale name.
   */
  private String mLocale = null;

  /**
   * Product Id.
   */
  private String mProductId = null;

  /**
   * Product id parameter name.
   */
  private String mProductIdParamName = null;

  /**
   * Subject parameter name.
   */
  private String mSubjectParamName = null;

  /**
   * Locale parameter name - it represents the name of locale parameter to be
   * used in Email template.
   */
  private String mLocaleParamName = "locale";
  
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

  /**
   * Gets the value of the Locale: field.
   * 
   * @return the value of the locale: field.
   */
  public String getLocale() {
    return mLocale;
  }

  /**
   * Sets the value of the locale: field.
   * 
   * @param pLocale -
   *          the value of the locale: field.
   */
  public void setLocale(String pLocale) {
    mLocale = pLocale;
  }

   
  /**
   * Gets the value of the ProductId: field.
   * 
   * @return the value of the ProductId: field.
   */
  public String getProductId() {
    return mProductId;
  }

  /**
   * Sets the value of the ProductId: field.
   * 
   * @param pProductId -
   *          the value of the ProductId: field.
   */
  public void setProductId(String pProductId) {
    mProductId = pProductId;
  }

  /**
   * Collect parameters for e-mail templates 
   * @return the email parameters
   */
  protected Map collectParams() {
    // collect params from form handler to map and pass them into tools class
    Map emailParams = super.collectParams();
    // replace with new subject
    emailParams.put(getProductIdParamName(), getProductId());
    return emailParams;
  }

  /**
   * Forward or redirect, as required.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @param pURL the url to redirect or forward to.
   * @throws IOException an error occurred reading data from the request
   * or writing data to the response.
   * @throws ServletException an application specific error occurred
   * processing this request
   */
  protected void redirectOrForward(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse, String pURL) throws IOException,
      ServletException {
    pResponse.sendLocalRedirect(pURL, pRequest);
  }
  
  /**
   * @return the URL of the success page.
   */
  public String getSuccessURL() {
    StringBuilder successUrl = new StringBuilder(super.getSuccessURL());
    successUrl.append(AMP_SYMBOL).append(RECIPIENT_NAME)
    .append(EQUAL_SYMBOL).append(getRecipientName().trim())
    .append(AMP_SYMBOL).append(RECIPIENT_EMAIL)
    .append(EQUAL_SYMBOL).append(getRecipientEmail().trim());
    
    return successUrl.toString();
  }
  
  /**
   * Handles the form submit and sends the email. Override existing 
   * method to check if message has valid length.
   *  
   * @param pRequest http request
   * @param pResponse http response
   * @return true on success, false - otherwise
   * @throws IOException an error occurred reading data from the request
   * or writing data to the response.
   * @throws ServletException an application specific error occurred
   * processing this request  
   */
  @Override
  public boolean handleSend(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException  {
    if (isLoggingDebug()) {
      logDebug("EmailAFriendFormHandler - [handleSend] = Entered in method \n");
    }
    
    String message = getMessage();
    validateMassageLength(message, pRequest, pResponse);
    
    if (!checkFormRedirect(null, getErrorURL(), pRequest, pResponse)){
      return false;
    }    
    
    return super.handleSend(pRequest, pResponse);
  }

  /**
   * Check if message length is valid. If message exceeds the valid amount
   * of symbols form exception will be added.
   * 
   * @param pMessage the message to check
   * @param pRequest http request
   * @param pResponse http response
   */
  private void validateMassageLength(String pMessage, DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) {
    if (pMessage.length() > 200) {
      String errorMsg = ResourceUtils.getMsgResource(MESSAGE_TOO_LONG,
          getResourceBundleName(), getResourceBundle(pRequest));
      addFormException(new DropletFormException(errorMsg, "emailToFriendTooLong", MESSAGE_TOO_LONG));
    }
    
  }

}
