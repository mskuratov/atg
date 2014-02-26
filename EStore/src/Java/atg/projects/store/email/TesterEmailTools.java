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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import atg.commerce.CommerceException;
import atg.commerce.catalog.custom.CatalogProperties;
import atg.commerce.csr.returns.ReturnRequest;
import atg.commerce.csr.returns.ReturnTools;
import atg.core.util.StringUtils;
import atg.projects.store.catalog.StoreCatalogTools;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.util.CurrentDate;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.email.TemplateEmailInfoImpl;

/**
 * Utility methods for e-mail templates testing.
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/email/TesterEmailTools.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class TesterEmailTools extends StoreEmailTools {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/email/TesterEmailTools.java#3 $$Change: 788278 $";

  //-------------------------------------
  // Constants
  // -------------------------------------

  public static final String EMAIL_PARAMETER = "email";
  public static final String ORDER_PARAMETER = "order";  
  public static final String SHIPPING_GROUP_PARAMETER = "shippingGroup";
  public static final String RETURN_REQUEST_PARAMETER = "returnRequest";
  public static final String RETURN_ITEM_TO_QUANTITY_PARAMETER = "returnItemToQuantityReceived";
  public static final String NEWPASSWORD_PARAMETER = "newpassword";
  public static final String SKU_ID_PARAMETER = "skuId";
  public static final String EMAIL_A_FRIEND_MESSAGE = "message";
  public static final String PRODUCT_ID_PARAMETER = "productId";
  public static final String LOCALE_PARAMETER = "locale";
  public static final String PROFILE_PARAMETER = "profile";
  public static final String SITE_PARAMETER = "siteId";
  public static final String USER_TYPE = "user";
  public static final String RECIPIENT_NAME_PARAMETER = "recipientName";
  public static final String RECIPIENT_EMAIL_PARAMETER = "recipientEmail";
  public static final String FIRST_NAME_PROP = "firstName";  
  public static final String SITE_ID_PARAMETER = "siteId";
  public static final String SITE_IDS_PARAMETER = "siteIds";
  public static final String SKU_PARAMETER = "sku";

  
  private RecentlySentList mSentList;
  
  /**
   * @return the mSentList
   */
  public RecentlySentList getSentList() {
    return mSentList;
  }

  /**
   * @param pSentList the sentList to set
   */
  public void setSentList(RecentlySentList pSentList) {
    mSentList = pSentList;
  }
  
  //---------------------------------------------------------------------------
  // property: catalogTools
  
  protected StoreCatalogTools mCatalogTools;

  /**
   * Set the CatalogTools object to use when looking up products, categories
   * and SKUs.
   * 
   * @param pCatalogTools the catalogTools to set
   **/  
  public void setCatalogTools(StoreCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  /**
   * Get the CatalogTools object to use when looking up products, categories
   * and SKUs.
   * 
   * @return the mCatalogTools
   **/
  public StoreCatalogTools getCatalogTools() {
    return mCatalogTools;
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
  
  /**
   * currentDate
   */
  private CurrentDate mCurrentDate;
  /**
   * Sets the CurrentDate component.
   */
  public void setCurrentDate(CurrentDate pCurrentDate) { 
    mCurrentDate = pCurrentDate; 
  }
  /**
   * Gets the CurrentDate component.
   */
  public CurrentDate getCurrentDate() { 
    return mCurrentDate; 
  }
  
  
  /* (non-Javadoc)
   * @see atg.projects.store.catalog.StoreEmailTools#prepareEmailInfoParams(atg.userprofiling.Profile, atg.userprofiling.email.TemplateEmailInfoImpl, java.util.Map)
   */
  /**
   * Prepare email parameters before sending
   * @param pProfile the profile
   * @param pEmailInfo email template
   * @param pEmailParams map of parameters
   * @return the prepared email parameters 
   */
  public Map prepareEmailInfoParams(Profile pProfile, TemplateEmailInfoImpl pEmailInfo, Map pEmailParams) {
    Map preparedParams = super.prepareEmailInfoParams(pProfile, pEmailInfo, pEmailParams);
    
    // add "new password"
    preparedParams.put(NEWPASSWORD_PARAMETER, pEmailParams.get(NEWPASSWORD_PARAMETER));
    
    // add "shipping group"
    preparedParams.put(SHIPPING_GROUP_PARAMETER, pEmailParams.get(SHIPPING_GROUP_PARAMETER));
    
    // add "order"
    preparedParams.put(ORDER_PARAMETER, pEmailParams.get(ORDER_PARAMETER));
    
    // add returnRequest parameter
    preparedParams.put(RETURN_REQUEST_PARAMETER, pEmailParams.get(RETURN_REQUEST_PARAMETER));
    
    // add return items quantity map
    preparedParams.put(RETURN_ITEM_TO_QUANTITY_PARAMETER, pEmailParams.get(RETURN_ITEM_TO_QUANTITY_PARAMETER));
    
    // add "sku Id"
    preparedParams.put(SKU_ID_PARAMETER, pEmailParams.get(SKU_ID_PARAMETER));
    
    // add "Email A Friend message"
    preparedParams.put(EMAIL_A_FRIEND_MESSAGE, pEmailParams.get(EMAIL_A_FRIEND_MESSAGE));

    // add "locale" 
    preparedParams.put(LOCALE_PARAMETER, pEmailParams.get(LOCALE_PARAMETER));
    
    RepositoryItem profileDataSource = (RepositoryItem) preparedParams.get(PROFILE_PARAMETER);

    // try to set Profile's first name
    if(profileDataSource!=null) {
      try {
        MutableRepositoryItem profile = getProfileTools().getProfileItem(profileDataSource.getRepositoryId());
        profile.setPropertyValue(FIRST_NAME_PROP, pEmailParams.get(RECIPIENT_NAME_PARAMETER));
      } catch (RepositoryException e) {
        if(isLoggingError()) {
          logError("Cannot retrieve profile", e);
        }
      }
    } else {
      getProfileTools().createNewUser(USER_TYPE, pProfile);
      pProfile.setPropertyValue(FIRST_NAME_PROP, pEmailParams.get(RECIPIENT_NAME_PARAMETER));
      preparedParams.put(PROFILE_PARAMETER, pProfile.getDataSource());
    }
    
    // store email meta information into list
    // replace changed parameters
    Map savedEmailParams = preparedParams;
    savedEmailParams.put(RECIPIENT_NAME_PARAMETER, pEmailParams.get(RECIPIENT_NAME_PARAMETER));
    savedEmailParams.put(RECIPIENT_EMAIL_PARAMETER, pEmailParams.get(RECIPIENT_EMAIL_PARAMETER));
    
    saveEmailInfo(savedEmailParams, (String)pEmailParams.get(getTemplateUrlName()));

    return preparedParams;
  }

  /**
   * Save email information into the list of recently sent 
   * 
   * @param pPreparedParams email parameters
   * @param pTemplateUrl email template URL 
   */
  protected void saveEmailInfo(Map pPreparedParams, String pTemplateUrl) {
    
    // Get the current system time.
    CurrentDate cd = getCurrentDate();
    Date currentDate = cd.getTimeAsDate();
    
    // get current timestamp
    SimpleDateFormat sdf = new SimpleDateFormat(RecentlySentList.SENT_DATE_FORMAT);
    String timestamp = sdf.format(currentDate.getTime());
    
    getSentList().addItem(getSentList().getTemplateName(pTemplateUrl),
        pPreparedParams, timestamp);
  }

  /* (non-Javadoc)
   * @see atg.projects.store.catalog.StoreEmailTools#addRecipients(atg.userprofiling.Profile, atg.userprofiling.email.TemplateEmailInfoImpl)
   */
  /**
   * Returns list of recipients
   * @param pProfile the profile
   * @param pEmailInfo the email info 
   * @return list of recipients
   */
  @Override
  protected List addRecipients(Profile pProfile, TemplateEmailInfoImpl pEmailInfo) {
    List recipients = new ArrayList();
    
    if(pProfile != null && pProfile.getDataSource() != null) {
      // we don't want to use profile's email here
      String originalEmail = (String)pProfile.getPropertyValue(EMAIL_PARAMETER);
      pProfile.setPropertyValue(EMAIL_PARAMETER, pEmailInfo.getMessageTo());
      recipients.add(pProfile);
      
      // add null check to prevent NPE since
      // profile.email is required property and 
      // we can't set it to null
      if(!StringUtils.isEmpty(originalEmail)) {
        pProfile.setPropertyValue(EMAIL_PARAMETER, originalEmail);
      }
      
    } else {
      recipients = super.addRecipients(pProfile, pEmailInfo);
    }
      
    return recipients;
  }
  
  /**
   * Finds product in repository.
   * Return true if product has been found for specified store.
   * Return false if product hasn't been found.  
   * 
   * @param pProductId product ID parameter
   * @param pSiteId site ID parameter 
   * @return true - if product has been found for specified store, false - if product hasn't been found
   * @throws RepositoryException indicates that a severe error occured while performing a Repository task
   */
  public boolean isValidProductId(String pProductId, String pSiteId) throws RepositoryException{
    MutableRepository rep = getCatalogRepository();
    RepositoryItem it = rep.getItem(pProductId, getProductItemName());    
    if (it != null) {      
      String siteIds = it.getPropertyValue(SITE_IDS_PARAMETER).toString(); 
      if (siteIds.contains(pSiteId)) {
         return true;
      } 
    }
    return false;
  }
  
  /**
   * Finds order in repository.
   * Return true if order has been found for specified store.
   * Return false if order hasn't been found.  
   * 
   * @param pOrderId order ID parameter
   * @param pSiteId site ID parameter 
   * @return true - if order has been found for specified store, false - if order hasn't been found
   * @throws RepositoryException indicates that a severe error occurred while performing a Repository task
   */
  public boolean isValidOrderId(String pOrderId, String pSiteId) throws RepositoryException{
    MutableRepository rep = getOrderRepository();
    RepositoryItem it = rep.getItem(pOrderId, ORDER_PARAMETER);
    if (it != null) {
      String siteId = it.getPropertyValue(SITE_ID_PARAMETER).toString();
      if ((siteId != null) && (siteId.contains(pSiteId))) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Finds return request in repository.
   * Return true if return request has been found for specified store.
   * Return false if return request hasn't been found.  
   * 
   * @param pReturnRequestId return request ID parameter
   * @return true - if return request has been found for specified store, false - if return request hasn't been found
   * @throws RepositoryException indicates that a severe error occurred while performing a Repository task
   * @throws CommerceException if some error occurred during return request retrieving.
   */
  public boolean isValidReturnRequestId(String pReturnRequestId) 
      throws RepositoryException, CommerceException{
    ReturnRequest item = getReturnTools().getReturnRequest(pReturnRequestId);
    return item != null;
  }
  
  /**
   * Finds shipping group in repository.
   * Return true if shipping group has been found for specified store.
   * Return false if shipping group hasn't been found.  
   * 
   * @param pShippingGroupId shipping group ID parameter
   * @param pSiteId site ID parameter 
   * @return true - if shipping group has been found for specified store, false - if shipping group hasn't been found
   * @throws RepositoryException indicates that a severe error occured while performing a Repository task
   */
  public boolean isValidShippingGroupId(String pShippingGroupId, String pSiteId) throws RepositoryException{
    MutableRepository rep = getOrderRepository();
    RepositoryItem shippingGroup = rep.getItem(pShippingGroupId, SHIPPING_GROUP_PARAMETER);
    if (shippingGroup != null) {
      RepositoryItem it = (RepositoryItem) shippingGroup.getPropertyValue(ORDER_PARAMETER);       
      String siteId = it.getPropertyValue(SITE_ID_PARAMETER).toString();      
      if ((siteId != null) && (siteId.contains(pSiteId))) {
        return true;
      }
    }
    return false;  
  }
  
  /**
   * Finds SKU in repository.
   * Return true if SKU has been found for specified store.
   * Return false if SKU hasn't been found.  
   * 
   * @param pSkuId SKU ID parameter
   * @param pSiteId site ID parameter 
   * @return true - if SKU has been found for specified store, false - if SKU hasn't been found
   * @throws RepositoryException indicates that a severe error occured while performing a Repository task
   */
  public boolean isValidSkuId(String pSkuId, String pSiteId) throws RepositoryException {
    MutableRepository rep = getCatalogRepository();
    RepositoryItem it = rep.getItem(pSkuId, SKU_PARAMETER);
    if (it != null) {
      String siteIds = it.getPropertyValue(SITE_IDS_PARAMETER).toString();
      if (siteIds.contains(pSiteId)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Gets parent product ID for a specified SKU. If SKU has more than
   * one parent products, the first one is returned.
   * 
   * @param pSkuId SKU ID to find product for
   * @return product ID for the SKU
   */
  public String getProductIdForSKU(String pSkuId){
    String productId = null;
    RepositoryItem sku;
    try {
      sku = getCatalogTools().findSKU(pSkuId);
  
      CatalogProperties catalogProperties = getCatalogTools().getCatalogProperties();
      if (sku != null){
        Collection parentProducts = (Collection) sku.getPropertyValue(catalogProperties.getParentProductsPropertyName());
        if (parentProducts != null && parentProducts.size() > 0){
          // there are parent products, get the first one
          productId = ((RepositoryItem)parentProducts.iterator().next()).getRepositoryId();
        }
      }
    } catch (RepositoryException e) {
      if(isLoggingError()) {
        logError("Cannot find SKU ", e);
      }
    }
    return productId;
  }
}
