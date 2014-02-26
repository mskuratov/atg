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


package atg.projects.store.inventory;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletException;

import atg.core.util.ResourceUtils;
import atg.droplet.DropletFormException;
import atg.droplet.GenericFormHandler;
import atg.multisite.SiteContextManager;
import atg.projects.store.email.StoreEmailTools;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.service.localeservice.LocaleService;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;


/**
 * This form handler will take requests from users to be notified when an item is back in stock.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/inventory/BackInStockFormHandler.java#3 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class BackInStockFormHandler extends GenericFormHandler {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/inventory/BackInStockFormHandler.java#3 $$Change: 788278 $";

  /**
   * Resource bundle name.
   */
  private static final String MY_RESOURCE_NAME = "atg.projects.store.inventory.UserMessages";

  /**
   * Resource bundle.
   */
  private static ResourceBundle sResourceBundle = 
    atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME,
                                                  atg.service.dynamo.LangLicense.getLicensedDefault());

  /**
   * Invalid e-mail format message key.
   */
  protected static final String MSG_INVALIDATE_EMAIL_FORMAT = "invalidateEmailFormat";

  /**
   * property: localeService
   */
  private LocaleService mLocaleService;

  /**
   * @return the mLocaleService
   */
  public LocaleService getLocaleService()
  {
    return mLocaleService;
  }

  /**
   * @param pLocaleService the localeService to set
   */
  public void setLocaleService(LocaleService pLocaleService)
  {
    mLocaleService = pLocaleService;
  }

  /**
   * property: catalogRefId
   */
  private String mCatalogRefId;
  
  /**
   * @return the catalog reference id.
   */
  public String getCatalogRefId() {
    return mCatalogRefId;
  }

  /**
   * @param pCatalogRefId - the catalog reference id.
   */
  public void setCatalogRefId(String pCatalogRefId) {
    mCatalogRefId = pCatalogRefId;
  }

  /**
   * property: emailAddress.
   */
  private String mEmailAddress;
  
  /**
   * @return the e-mail address.
   */
  public String getEmailAddress() {
    return mEmailAddress;
  }

  /**
   * @param pEmailAddress - the e-mail address to set.
   */
  public void setEmailAddress(String pEmailAddress) {
    mEmailAddress = pEmailAddress;
  }

  /**
   * property: productId
   */
  private String mProductId;
  
  /**
   * @return the product id.
   */
  public String getProductId() {
    return mProductId;
  }

  /**
   * @param pProductId - the product id to set.
   */
  public void setProductId(String pProductId) {
    mProductId = pProductId;
  }

  /**
   * property: profileRepository.
   */
  private MutableRepository mProfileRepository;
  
  /**
   * @return the profile repository.
   */
  public MutableRepository getProfileRepository() {
    return mProfileRepository;
  }

  /**
   * @param pProfileRepository - the profile repository to set.
   */
  public void setProfileRepository(MutableRepository pProfileRepository) {
    mProfileRepository = pProfileRepository;
  }

  /**
   * property: successURL.
   */
  private String mSuccessURL;
  
  /**
   * @return the success redirect URL.
   */
  public String getSuccessURL() {
    return mSuccessURL;
  }

  /**
   * @param pSuccessURL - the success redirect URL to set.
   */
  public void setSuccessURL(String pSuccessURL) {
    mSuccessURL = pSuccessURL;
  }

  /**
   * property: errorURL.
   */
  private String mErrorURL;
  
  /**
   * @return the error redirect URL.
   */
  public String getErrorURL() {
    return mErrorURL;
  }

  /**
   * @param pErrorURL - the error redirect URL to set.
   */
  public void setErrorURL(String pErrorURL) {
    mErrorURL = pErrorURL;
  }

  /** 
   * property: emailTools
   */
  private StoreEmailTools mEmailTools;

  /**
   * @return the StoreEmailTools.
   */
  public StoreEmailTools getEmailTools() {
    return mEmailTools;
  }

  /**
   * @param pEmailTools - the StoreEmailTools to set.
   */
  public void setEmailTools(StoreEmailTools pEmailTools) {
    mEmailTools = pEmailTools;
  }

  /** 
   * property: inventoryManager
   */
  private StoreInventoryManager mInventoryManager;

  /**
   * @return the StoreInventoryManager.
   */
  public StoreInventoryManager getInventoryManager() {
    return mInventoryManager;
  }

  /**
   * @param pInventoryManager - the StoreInventoryManager to set
   */
  public void setInventoryManager(StoreInventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }

  /** 
   * property: noJavascriptSuccessURL
   */
  public String mNoJavascriptSuccessURL;

  /**
   * @return the No JavaScript Success URL.
   */
  public String getNoJavascriptSuccessURL(){
    return mNoJavascriptSuccessURL;
  }

  /**
   * @param pNoJavascriptSuccessURL -the No JavaScript Success URL.
   */
  public void setNoJavascriptSuccessURL(String pNoJavascriptSuccessURL){
    mNoJavascriptSuccessURL = pNoJavascriptSuccessURL;
  }

  /** 
   * property: noJavascriptErrorURL
   */
  public String mNoJavascriptErrorURL;

  /**
   * @return the No JavaScript Error URL.
   */
  public String getNoJavascriptErrorURL(){
    return mNoJavascriptErrorURL;
  }

  /**
   * @param pNoJavascriptErrorURL - the No JavaScript Error URL.
   */
  public void setNoJavascriptErrorURL(String pNoJavascriptErrorURL){
    mNoJavascriptErrorURL = pNoJavascriptErrorURL;
  }

  /**
   * This method returns ResourceBundle object for specified locale.
   *
   * @param pLocale - The locale used to retrieve the resource bundle. If <code>null</code>
   *                  then the default resource bundle is returned.
   *
   * @return the resource bundle.
   */
  public ResourceBundle getResourceBundle(Locale pLocale) {
    if (pLocale == null) {
      return sResourceBundle;
    }

    ResourceBundle rb = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, pLocale);

    return rb;
  }

  /**
   * This method will handle "notify when back in stock" requests.
   *
   * @param pRequest the servlet's request.
   * @param pResponse the servlet's response.
   * 
   * @throws ServletException if there was an error while executing the code.
   * @throws IOException if there was an error with servlet io.
   * 
   * @return true if success, otherwise false.
   */
  public boolean handleNotifyMe(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    if ((getCatalogRefId() == null) || (getCatalogRefId().length() < 1)) {
      
      if (isLoggingDebug()) {
        logDebug("catalogRefId is null. backInStockNotifyItem was not created.");
      }

      // When JavaScript is off - if the skuId is not set, don't display the success message.
      if(getNoJavascriptErrorURL() != null) {
        return checkFormRedirect(null, getNoJavascriptErrorURL(), pRequest, pResponse);
      }
      
      // Set in JSPs when JavaScript is enabled.
      return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
    }

    if ((getProductId() == null) || (getProductId().length() < 1)) {
      if (isLoggingDebug()) {
        logDebug("productId is null. backInStockNotifyItem was not created.");
      }

      // When JavaScript is off - if the productId is not set, don't display the success message.
      if(getNoJavascriptErrorURL() != null) {
        return checkFormRedirect(null, getNoJavascriptErrorURL(), pRequest, pResponse);
      }
      
      return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
    }

    if (!getEmailTools().validateEmailAddress(getEmailAddress())) {
      
      String msg = 
          ResourceUtils.getMsgResource(MSG_INVALIDATE_EMAIL_FORMAT, 
                                       MY_RESOURCE_NAME, 
                                       getResourceBundle(ServletUtil.getUserLocale()));
      
      addFormException(new DropletFormException(msg, null));

      // When JavaScript is off - if the email address is not valid, display
      // the same page with a warning message.
      if(getNoJavascriptErrorURL() != null) {
        return checkFormRedirect(null, getNoJavascriptErrorURL(), pRequest, pResponse);
      }
      
      return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
    }

    try {
      boolean alreadyExists =
        getInventoryManager().isBackInStockItemExists(getProfileRepository(), 
                                                      getCatalogRefId(),
                                                      getEmailAddress(), 
                                                      getProductId());
      if (alreadyExists) {
        if (isLoggingDebug()) {
          logDebug("backInStockNotifyItem already exists for this combination of catalogRefId, " +
            "email and productId.");
        }

        // When JavaScript is off - if the notification already exists, display the success message.
        if(getNoJavascriptSuccessURL() != null){
          return checkFormRedirect(getNoJavascriptSuccessURL(), null, pRequest, pResponse);
        }
        
        return checkFormRedirect(null, getErrorURL(), pRequest, pResponse);
      }

      getInventoryManager().createBackInStockNotifyItem(getProfileRepository(), 
                                                        getCatalogRefId(),
                                                        getEmailAddress(), 
                                                        getProductId(), 
                                                        getLocaleService().getLocale().toString(), 
                                                        SiteContextManager.getCurrentSiteId());
    } 
    catch (RepositoryException ex) {
      throw new ServletException(ex);
    }

    // Notification created successfully.
    if((getNoJavascriptSuccessURL() != null) || (getNoJavascriptErrorURL() != null)) {
      
      return checkFormRedirect(getNoJavascriptSuccessURL(), 
                               getNoJavascriptErrorURL(),
                               pRequest, pResponse);
    }
    
    return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
  }
  
}
