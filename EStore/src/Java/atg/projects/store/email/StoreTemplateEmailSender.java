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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import atg.projects.store.profile.StorePropertyManager;
import atg.repository.RepositoryItem;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailSender;

/**
 * This is a Store extention of DPS' TemplateEmailSender component.
 * This extention passes current locale (if set) into the e-mail template to be sent.
 * @see TemplateEmailSender
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/email/StoreTemplateEmailSender.java#2 $$Change: 768606 $ 
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class StoreTemplateEmailSender extends TemplateEmailSender
{
  /**
   * Class version
   */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/email/StoreTemplateEmailSender.java#2 $$Change: 768606 $";
  
  /**
   * If this parameter is set to 'true' on HTTP request, then request is issued to render an email body.
   */
  public static final String PARAMETER_NAME_EMAIL_REQUEST = "isEmailRequest";
  
  private StorePropertyManager mPropertyManager;

  /**
   * Link to a user properties manager to be used by this component.
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
   * This implementation does the following:
   * <ul>
   *  <li>obtain current locale from recipient specified by pRecipients parameter (if there is only one recipient)
   *  <li>if emailInfo's template parameters doesn't contain locale parameter and locale is defined, put locale into template parameters
   *  <li>add template parameter to mark request as email 
   *  <li>send email with super-method
   * </ul>
   * 
   * @param pEmailInfo the email info
   * @param pRecipients the recipient
   * @param pRunInSeparateThread if true, it shoulld be run in separate thread
   * @param pPersist if true, the mailing will be persisted in the database before
   *  the email is actually sent. Thus, if the server goes down unexpectedly, the mailing 
   *  will resume when the server is restarted.
   * @throws TemplateEmailException  if any problem was encountered while assembling or 
   *  sending the message (but see above for an explanation of when an exception is not thrown)
   */
  @SuppressWarnings("unchecked") //ok, cause we use <Object, Object> generic map
  @Override
  public void sendEmailMessage(TemplateEmailInfo pEmailInfo, Collection pRecipients, boolean pRunInSeparateThread, boolean pPersist)
      throws TemplateEmailException
  {
    Map<Object, Object> templateParameters = pEmailInfo.getTemplateParameters();
    if (templateParameters == null)
    {
      templateParameters = new HashMap<Object, Object>();
    }
    // We should set locale parameter for individual email senders only, otherwise we can't determine proper locale for all recipients.
    // Do not set locale parameter if it's specified by emailInfo already.
    if (pRecipients.size() == 1 && !templateParameters.containsKey(getLocalePropertyName()))
    {
      String locale = (String) ((RepositoryItem) pRecipients.iterator().next()).getPropertyValue(getPropertyManager().getLocalePropertyName());
      templateParameters.put(getLocalePropertyName(), locale);
    }
    // Mark future request as email, this will be used by SiteDefaultLocaleProcessor.
    templateParameters.put(PARAMETER_NAME_EMAIL_REQUEST, Boolean.TRUE);
    pEmailInfo.setTemplateParameters(templateParameters);
    
    super.sendEmailMessage(pEmailInfo, pRecipients, pRunInSeparateThread, pPersist);
  }
}
