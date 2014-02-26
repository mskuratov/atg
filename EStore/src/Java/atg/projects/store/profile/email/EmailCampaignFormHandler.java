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


package atg.projects.store.profile.email;

import atg.core.util.UserMessage;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.projects.store.email.StoreEmailTools;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.profile.StoreProfileTools;
import atg.repository.RepositoryException;
import atg.repository.servlet.RepositoryFormHandler;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import java.io.IOException;

import javax.servlet.ServletException;


/**
 * This formhandler is used to create an EmailRecipient repository item
 * when a user signs up to receive marketing email campaigns.
 *
 * @author ATG
 * @version $Id: EmailCampaignFormHandler.java,v 1.7 2004/09/10 12:28:30
 *          sdere Exp $
 */
public class EmailCampaignFormHandler extends RepositoryFormHandler {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/profile/email/EmailCampaignFormHandler.java#2 $$Change: 768606 $";

  /**
   * Resource bundle name.
   */
  static final String RESOURCE_BUNDLE = "atg.commerce.profile.UserMessages";


  /**
   * Registration e-mail.
   */
  private String mRegistrationEmail;

  /**
   * Email Campaign Source Code.
   */
  private String mSourceCode;

  /**
  * The email to use to register.
  * @return registration e-mail
  */
  public String getRegistrationEmail() {
    return mRegistrationEmail;
  }

  /**
   * The email to use to register.
   * @param pRegistrationEmail - registration e-mail
   */
  public void setRegistrationEmail(String pRegistrationEmail) {
    mRegistrationEmail = pRegistrationEmail;
  }

  /**
   * The code that indicates the source of the form input.
   * @return source code
   */
  public String getSourceCode() {
    return mSourceCode;
  }

  /**
   * The code that indicates the source of the form input.
   * @param pSourceCode - source code
   */
  public void setSourceCode(String pSourceCode) {
    mSourceCode = pSourceCode;
  }

   
  //--------------------------------------------------
  // property: EmailTools
  private StoreEmailTools mEmailTools;

  /**
   * @return the StoreEmailTools
   */
  public StoreEmailTools getEmailTools() {
    return mEmailTools;
  }

  /**
   * @param pEmailTools the StoreEmailTools to set
   */
  public void setEmailTools(StoreEmailTools pEmailTools) {
    mEmailTools = pEmailTools;
  }

  /**
   * Creates an EmailRecipient with the user's email.
   *
   * @param pRequest - HTTP Request
   * @param pResponse - HTTP Response
   *
   * @return True if success False if failed
   *
   * @throws ServletException - if servlet exception occurs
   * @throws IOException - if I/O exception occurs
   */
  public boolean handleCreateEmailRecipient(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    StoreProfileTools pTools = (StoreProfileTools) getEmailTools().getProfileTools();
    String email = getRegistrationEmail();
    

    if (!getEmailTools().validateEmailAddress(email)) {
      addFormException(StoreEmailTools.MSG_ERR_BAD_EMAIL, pRequest);

      return checkFormRedirect(getCreateSuccessURL(), getCreateErrorURL(), pRequest, pResponse);
    }

    // check to see if email is already signed up, if so return quietly
    if (!(pTools.retrieveEmailRecipient(email) == null)) {
      if (isLoggingDebug()) {
        logDebug("Email is already registered so no need to do anything");
        logDebug("Exiting quietly");
      }

      return checkFormRedirect(getCreateSuccessURL(), getCreateErrorURL(), pRequest, pResponse);
    }

    if (!getFormError()) {
      try {
        pTools.createEmailRecipient(email, getSourceCode());

      } catch (RepositoryException repositoryExc) {
        addFormException(
            StoreEmailTools.MSG_ERR_CREATING_EMAIL_RECIPIENT,
            repositoryExc, pRequest);

        if (isLoggingError()) {
          logError(LogUtils.formatMinor(repositoryExc.toString()),
              repositoryExc);
        }
      }
    }

    return checkFormRedirect(getCreateSuccessURL(), getCreateErrorURL(), pRequest, pResponse);
  }
  
  /**
   * Create a form exception, by looking up the exception code in a resource
   * file identified by the RESOURCE_BUNDLE constant (defined above).
   *
   * @param pWhatException - String description of exception
   * @param pRepositoryExc - RepositoryException
   * @param pRequest - HTTP Request Object
   */
  protected void addFormException(String pWhatException, RepositoryException pRepositoryExc,
    DynamoHttpServletRequest pRequest) {
    //fetch the correct error message
    //based on user's locale.
    String errorStr = UserMessage.getString(RESOURCE_BUNDLE, pWhatException, ServletUtil.getUserLocale(pRequest));
    //create the DropletException both with the translated user message, 
    //and with the whatException
    addFormException(new DropletFormException(errorStr, pRepositoryExc, pWhatException));
  }

  /**
   * Create a form exception, by looking up the exception code in a resource
   * file identified by the RESOURCE_BUNDLE constant (defined above).
   *
   * @param pMsgKey - key to the message in the bundl
   * @param pRequest - HTTP Request Object
   */
  protected void addFormException(String pMsgKey, DynamoHttpServletRequest pRequest) {
    //fetch the correct error message
    //based on user's locale.
    String errorStr = UserMessage.getString(RESOURCE_BUNDLE, pMsgKey, ServletUtil.getUserLocale(pRequest));
    //create the DropletException with the translated user message
    addFormException(new DropletException(errorStr));
  }
}
