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



package atg.projects.store.assetmanager.editor.profile;

import java.io.IOException;
import java.util.Dictionary;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.projects.store.profile.StoreProfileTools;
import atg.projects.store.profile.StorePropertyManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.web.assetmanager.editor.profile.UserFormHandler;

/**
*
* Store extension of form handler for user assets. 
*
* @author ATG
* @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/Versioned/src/atg/projects/store/assetmanager/editor/profile/StoreUserFormHandler.java#2 $$Change: 768606 $
* @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
*/
public class StoreUserFormHandler extends UserFormHandler {

  //-------------------------------------
  // CONSTANTS
  //-------------------------------------

  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/Versioned/src/atg/projects/store/assetmanager/editor/profile/StoreUserFormHandler.java#2 $$Change: 768606 $";
  
  /** ReceivePromoEmails property values */
  protected static final String NO = "no";
  protected static final String YES = "yes";
  
  /**
   * E-mail option source code.
   */
  protected String mEmailOptInSourceCode;

  /**
   * @return E-mail opt in source code.
   */
  public String getEmailOptInSourceCode() {
    return mEmailOptInSourceCode;
  }

  /**
   * @param pEmailOptInSourceCode -
   * e-mail opt in source code.
   */
  public void setEmailOptInSourceCode(String pEmailOptInSourceCode) {
    mEmailOptInSourceCode = pEmailOptInSourceCode;
  }
  
  //-------------------------------------
  // property: ReceivePromoEmail   
  /**
   * ReceivePromoEmail setting
   */
  private String mReceivePromoEmail;

  /**
   * @return ReceivePromoEmail setting
   */
  public String getReceivePromoEmail() {
    return mReceivePromoEmail;
  }

  /**
   * @param pReceivePromoEmail - ReceivePromoEmail setting
   */
  public void setReceivePromoEmail(String pReceivePromoEmail) {
    mReceivePromoEmail = pReceivePromoEmail;
  }
  
  //-------------------------------------
  // property: previousEmailAddress   
  /**
   * Previous email address
   */
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

  
  //-------------------------------------
  // METHODS
  //-------------------------------------
  
  /**
   * Utility method to retrieve the StorePropertyManager.
   * @return property manager 
   */
  protected StorePropertyManager getStorePropertyManager() {
    return (StorePropertyManager) getProfileTools().getPropertyManager();
  }
  
  //--------------------------------------------------------------------
  /**
   * Converts the users login to lower case, if not already.
   */
  public void convertLoginToLowerCase() {
    // Get dictionary of profile values. 
    Dictionary valuesDict = getValue();
    // Get the user login.
    String login = (String) valuesDict.get("LOGIN");
    
    // Ensure user login exists.
    if (!StringUtils.isEmpty(login)) {
      if (isLoggingDebug()) {
        logDebug("Converting user login " + 
            login + " to lower case, if not already.");
      }
      // Change the user login value to lower case.
      valuesDict.put("LOGIN", login.toLowerCase());
    }
  }
  
  /**
   * Creates \ updates \ removes email recipient item according to the updates
   * to profile.
   */
  protected void updateEmailRecipient(DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse){
  
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    StorePropertyManager propertyManager =  getStorePropertyManager();
        
    // Get profile repository item
    RepositoryItem profile = getRepositoryItem();
    
    // Get profile's email
    String newEmail = (String) getItemProperty(propertyManager.getEmailAddressPropertyName());
    RepositoryItem emailRecipient = null;
    if (!StringUtils.isEmpty(getPreviousEmailAddress())){
      emailRecipient = profileTools.retrieveEmailRecipient(getPreviousEmailAddress());
    }else{
      emailRecipient = profileTools.retrieveEmailRecipient(newEmail);
    }
    
    
    if (YES.equals(getReceivePromoEmail())){
      // If receivePromoEmail is true but there is no corresponding
      // emailRecipient repository item create it.
      if (emailRecipient == null){
        try {
          profileTools.createEmailRecipient(profile, newEmail, getEmailOptInSourceCode());
        } catch (RepositoryException e) {
          if (isLoggingError())
              logError(e);
        }
      }else{
        try {
          profileTools.updateEmailRecipient(profile, getPreviousEmailAddress(), newEmail, getEmailOptInSourceCode());
        } catch (RepositoryException e) {
          if (isLoggingError())
              logError(e);
        }
      }
      
    }else{
      // If receivePromoEmail is false but there is corresponding emailRecipient item
      // remove it.
      if (emailRecipient != null){
        try {
          profileTools.removeEmailRecipient(getPreviousEmailAddress());
        } catch (RepositoryException e) {
          if (isLoggingError())
              logError(e);
        }
      }
    }
  }
  
  /**
   * Stores email related data for later use in post create / update methods.
   * @param pRequest the servlet's http request
   * @param pResponse the servlet's http response
   * @param boolean if true indicates that new user item is being created
   */
  protected void initializeData(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse,
                                boolean pNewItem){
    
    //Get current profile's email
    StorePropertyManager propertyManager =  getStorePropertyManager();
    
    // If not new user item is being created store previous email for later use
    if (!pNewItem){
      String email = (String) getItemProperty(propertyManager.getEmailAddressPropertyName());
      setPreviousEmailAddress(email);  
    }
    
    // Store receivePromoEmail setting for later use
    // Retrieve the value of receivePromoEmail property
    String receivePromoEmails = (String) getValue().get(propertyManager.getReceivePromoEmailPropertyName().toUpperCase());
    setReceivePromoEmail(receivePromoEmails);
  }
  
   /**
   * This method is called just before update item. If the user login has 
   * uppercase characters, they will be converted to lowercase.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing
   * the code
   * @exception IOException if there was an error with servlet io
   **/
  @Override
  protected void preUpdateItem(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    convertLoginToLowerCase();
    initializeData(pRequest, pResponse, false);
    
    super.preUpdateItem(pRequest, pResponse);
  }
  
  //---------------------------------------------------------------------
  /**
   * This method is called just before the item adding process is
   * started. If the user login has uppercase characters, they 
   * will be converted to lowercase.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing
   * the code
   * @exception IOException if there was an error with servlet io
   **/
  @Override
  protected void preAddItem(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    convertLoginToLowerCase();
    initializeData(pRequest, pResponse, true);
    super.preAddItem(pRequest, pResponse);
  }
  
  /**
   * Overrides base method to create emailRecipient item if needed.
   */
  @Override
  protected void postAddItem(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    super.postAddItem(pRequest, pResponse);
    
    // Create emailRecipient item if needed
    updateEmailRecipient(pRequest, pResponse);
  }

  /**
   * Overrides base method to update emailRecipient item.
   */
  @Override
  protected void postUpdateItem(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {
 
    super.postUpdateItem(pRequest, pResponse);
    
    // Update emailRecipient item
    updateEmailRecipient(pRequest, pResponse);
   
  }
  
  
}
