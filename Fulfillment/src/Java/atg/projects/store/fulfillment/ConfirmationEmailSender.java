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


package atg.projects.store.fulfillment;

import atg.commerce.order.Order;

import atg.nucleus.GenericService;

import atg.projects.store.logging.LogUtils;

import atg.repository.RepositoryItem;

import atg.userprofiling.email.*;

import java.util.HashMap;
import java.util.Map;


/**
 * This class is responsible for sending order confirmation emails.
 * @author ATG
 */
public class ConfirmationEmailSender extends GenericService {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Fulfillment/src/atg/projects/store/fulfillment/ConfirmationEmailSender.java#2 $$Change: 768606 $";

  /**
   * Order constant.
   */
  protected static final String ORDER = "order";

  /**
   * Message content processor.
   */
  protected MessageContentProcessor mContentProcessor;

  /**
   * Template e-mail sender.
   */
  protected TemplateEmailSender mTemplateEmailSender;

  /**
   * E-mail message sender address.
   */
  protected String mEmailMessageFrom;

  /**
   * E-mail message subject.
   */
  protected String mEmailMessageSubject;

  /**
   * Should fill from template.
   */
  protected boolean mFillFromTemplate = true;

  /**
   * @return the content processor.
   */
  public MessageContentProcessor getContentProcessor() {
    return mContentProcessor;
  }

  /**
   * @param pContentProcessor - content processor.
   */
  public void setContentProcessor(MessageContentProcessor pContentProcessor) {
    mContentProcessor = pContentProcessor;
  }

  /**
   * The template email sender to use to send the order confirm email.
   * @param pTemplateEmailSender - template e-mail sender
   */
  public void setTemplateEmailSender(TemplateEmailSender pTemplateEmailSender) {
    mTemplateEmailSender = pTemplateEmailSender;
  }

  /**
   * The template email sender used to send the order confirm email.
   * @return template e-mail sender
   */
  public TemplateEmailSender getTemplateEmailSender() {
    return mTemplateEmailSender;
  }

  /**
   * The email address to set the message from for the order confirm email.
   * @param pEmailMessageFrom - e-mail address sender
   */
  public void setEmailMessageFrom(String pEmailMessageFrom) {
    mEmailMessageFrom = pEmailMessageFrom;
  }

  /**
   * The email address to set the message from for the order confirm email.
   * @return e-mail message sender address
   */
  public String getEmailMessageFrom() {
    return mEmailMessageFrom;
  }

  /**
   * The subject to use for the order confirmation emails.
   * @param pEmailMessageSubject - e-mail message subject
   */
  public void setEmailMessageSubject(String pEmailMessageSubject) {
    mEmailMessageSubject = pEmailMessageSubject;
  }

  /**
   * The subject to use for the order confirmation emails.
   * @return e-mail message subject
   */
  public String getEmailMessageSubject() {
    return mEmailMessageSubject;
  }

  /**
   * @return true if we should try to extract email information from
   * the &lt;meta&gt; tags in the template.
   **/
  public boolean getFillFromTemplate() {
    return mFillFromTemplate;
  }

  /**
   * Sets the flag indicating whether we should try to extract email
   * information from the &lt;meta&gt; tags in the template.
   * @param pFillFromTemplate - true if should be filled, false - otherwise
   **/
  public void setFillFromTemplate(boolean pFillFromTemplate) {
    mFillFromTemplate = pFillFromTemplate;
  }

  /**
   * This method sends the order confirmation emails.
   * @param pOrder - order
   * @param pProfile - user profile
   * @param pTemplateUrl - template URL
   */
  public void sendConfirmationEmail(Order pOrder, RepositoryItem pProfile, String pTemplateUrl) {
    TemplateEmailSender tes = getTemplateEmailSender();
    String emailAddress = (String) pProfile.getPropertyValue(tes.getEmailAddressPropertyName());

    Object[] recipient = { pProfile };
    TemplateEmailInfo emailInfo = createTemplateEmailInfo(emailAddress, pTemplateUrl);

    Map params = new HashMap();
    params.put(ORDER, pOrder);
    emailInfo.setTemplateParameters(params);

    try {
      // set true to run in separate thread
      // set true to persist
      tes.sendEmailMessage(emailInfo, recipient, true, true);

      if (isLoggingDebug()) {
        logDebug("Sending order confirmation for order: " + pOrder.getId());
      }
    } catch (TemplateEmailException tee) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error sending order confirmation email."), tee);
      }
    }
  }

  /**
   * Creates the template email info for the template we need to use
   * for order confirmation.
   *
   * @param pEmailTo - e-mail recipient address
   * @param pEmailTemplate - e-mail template
   * @return template e-mail information
   */
  protected TemplateEmailInfo createTemplateEmailInfo(String pEmailTo, String pEmailTemplate) {
    // make a copy of the default email info
    TemplateEmailInfoImpl emailInfo = new TemplateEmailInfoImpl();

    // set the template url
    emailInfo.setTemplateURL(pEmailTemplate);
    emailInfo.setMessageFrom(getEmailMessageFrom());
    emailInfo.setMessageTo(pEmailTo);
    emailInfo.setMessageSubject(getEmailMessageSubject());
    emailInfo.setContentProcessor(getContentProcessor());
    emailInfo.setFillFromTemplate(getFillFromTemplate());

    return emailInfo;
  }
}
