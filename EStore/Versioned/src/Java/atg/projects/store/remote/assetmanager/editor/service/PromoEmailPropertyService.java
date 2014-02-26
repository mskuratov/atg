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
package atg.projects.store.remote.assetmanager.editor.service;

import atg.beans.DynamicPropertyDescriptor;
import atg.core.util.StringUtils;
import atg.projects.store.profile.StoreProfileTools;
import atg.projects.store.profile.StorePropertyManager;
import atg.remote.assetmanager.editor.model.PropertyDescriptorState;
import atg.remote.assetmanager.editor.model.PropertyUpdate;
import atg.remote.assetmanager.editor.service.AssetEditorInfo;
import atg.remote.assetmanager.editor.service.RepositoryAssetPropertyServiceImpl;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * Add logic to create email recipient if Receive promo email property is changed in BCC.
 * @author Yekaterina Kostenevich
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/Versioned/src/atg/projects/store/remote/assetmanager/editor/service/PromoEmailPropertyService.java#3 $Change: 630322 $
 * @updated $DateTime: 2013/03/20 03:30:31 $Author: ykostene $
 *
 */
public class PromoEmailPropertyService extends RepositoryAssetPropertyServiceImpl{
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/Versioned/src/atg/projects/store/remote/assetmanager/editor/service/PromoEmailPropertyService.java#3 $Change: 630322 $";
  
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
    

  /**
   * Profile Tools
   */
  StoreProfileTools mProfileTools;

  //----------------------------------
  /**
   * Sets Profile Tools
   **/
  public void setProfileTools(StoreProfileTools pProfileTools) {
    mProfileTools = pProfileTools;}

  //----------------------------------
  /**
   * Returns Profile Tools
   **/
  public StoreProfileTools getProfileTools() {
    return mProfileTools;
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

  
  
  /**
   * Saves current users email and then execute property update logic.
   * Call updateEmailRecipient method then to update\create\remove 
   * email recipient.
   *
   * @param pItem an Item to update
   * @param pPropertyName property name to update
   * @param pNewValue new value
   */
  protected void performActualItemPropertyUpdate(MutableRepositoryItem pItem, String pPropertyName, Object pNewValue) {    
    //  Save current users email so that if email will be changed during item update
    //  the correct email recipient could be retrieved.
    StoreProfileTools profileTools = getProfileTools();
    StorePropertyManager propertyManager =  (StorePropertyManager) profileTools.getPropertyManager();
    String previousEmailAddress = (String) pItem.getPropertyValue(propertyManager.getEmailAddressPropertyName());
    setPreviousEmailAddress(previousEmailAddress);
    
    // Update item
    super.performActualItemPropertyUpdate(pItem, pPropertyName, pNewValue);
    
    // Update email recipient
    updateEmailRecipient(pItem);    
  }

    
  
  /**
   * Creates \ updates \ removes email recipient item according to the updates
   * to profile.
   * 
   * @param pProfile profile to update
   */
  protected void updateEmailRecipient(MutableRepositoryItem pProfile){
  
    StoreProfileTools profileTools = getProfileTools();
    StorePropertyManager propertyManager =  (StorePropertyManager) profileTools.getPropertyManager();

    
    // Get profile's email
    String newEmail = (String) pProfile.getPropertyValue(propertyManager.getEmailAddressPropertyName());
    RepositoryItem emailRecipient = null;
    if (!StringUtils.isEmpty(getPreviousEmailAddress())){
      emailRecipient = profileTools.retrieveEmailRecipient(getPreviousEmailAddress());
    }else{
      emailRecipient = profileTools.retrieveEmailRecipient(newEmail);
    }
    
    String receivePromoEmail = (String) pProfile.getPropertyValue(propertyManager.getReceivePromoEmailPropertyName());
    
    if (YES.equals(receivePromoEmail)){
      // If receivePromoEmail is true but there is no corresponding
      // emailRecipient repository item create it.
      if (emailRecipient == null){
        try {
          profileTools.createEmailRecipient(pProfile, newEmail, getEmailOptInSourceCode());
        } catch (RepositoryException e) {
          if (isLoggingError())
              logError(e);
        }
      }else{
        try {
          profileTools.updateEmailRecipient(pProfile, getPreviousEmailAddress(), newEmail, getEmailOptInSourceCode());
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
   * Override retrievePropertyValue for receivePromoEmail property.
   * receivePromoEmail should be "NO" value if:
   * 1) receivePromoEmail="no" OR
   * 2) receivePromoEmail="yes", but no email recipient with such address exist. 
   */
  @Override
  protected Object retrievePropertyValue(
      RepositoryItem pItem,
      String pPropertyName,
      DynamicPropertyDescriptor pDesc,
      PropertyDescriptorState pDescriptorState) throws RepositoryException{
    
    Object propertyValue = super.retrievePropertyValue(pItem, pPropertyName, pDesc, pDescriptorState);
   
    StoreProfileTools profileTools = getProfileTools();
    StorePropertyManager propertyManager =  (StorePropertyManager) profileTools.getPropertyManager();    
    
    // If we are retrieving value for ReceivePromoEmailProperty
    if (propertyManager.getReceivePromoEmailPropertyName().equals(pPropertyName)) {
          
      // Get receivePromoEmail property 
      String receivePromoEmail = (String) propertyValue;
      if (YES.equals(receivePromoEmail)) {
        /*
         *  Get profile's email and look for recipient for this email. If there is no
         *  recipient, value should be false
         */
        RepositoryItem emailRecipient = null;
        String email = (String) pItem.getPropertyValue(propertyManager.getEmailAddressPropertyName());
        if (email != null) {
          emailRecipient = profileTools.retrieveEmailRecipient(email);
        }
        if (emailRecipient == null) {
          propertyValue = NO;
        } 
      }
    
    }
    
    return propertyValue;

  }
  
  /**
   * Override isModifiedPropertyUpdate for receivePromoEmail property.
   * receivePromoEmail old value considered to have "NO" value if:
   * 1) receivePromoEmail="no" OR
   * 2) receivePromoEmail="yes", but no email recipient with such address exist.   * 
   */
  @Override
  protected boolean isModifiedPropertyUpdate(AssetEditorInfo pEditorInfo, RepositoryItem pItem, PropertyUpdate pUpdate) {
    
    boolean isModified = super.isModifiedPropertyUpdate(pEditorInfo, pItem, pUpdate);
    
    StoreProfileTools profileTools = getProfileTools();
    StorePropertyManager propertyManager =  (StorePropertyManager) profileTools.getPropertyManager();  
    String receivePromoEmailProerty = propertyManager.getReceivePromoEmailPropertyName();
    
    // If we are checking value for ReceivePromoEmailProperty
    if (receivePromoEmailProerty.equals(pUpdate.getPropertyName())) {
      Object newvalue = pUpdate.getPropertyValue();
      Object oldvalue = pItem.getPropertyValue(receivePromoEmailProerty);
      if (YES.equals(oldvalue)) {
        /*
         *  Get profile's email and look for recipient for this email. If there is no
         *  recipient, value should be false
         */
        RepositoryItem emailRecipient = null;
        String email = (String) pItem.getPropertyValue(propertyManager.getEmailAddressPropertyName());
        if (email != null) {
          emailRecipient = profileTools.retrieveEmailRecipient(email);
        }        
        if (emailRecipient == null) {
          oldvalue = NO;
        } 
      }
      
      if ((oldvalue == null && newvalue != null) ||
          (oldvalue != null && !oldvalue.equals(newvalue))) {
        isModified = true;
      }
      else {
        isModified = false;
      }
    }
  
    return isModified;
  }
  
  
}
