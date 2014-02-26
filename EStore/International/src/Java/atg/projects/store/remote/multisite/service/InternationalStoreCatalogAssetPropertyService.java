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



package atg.projects.store.remote.multisite.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import atg.beans.DynamicPropertyDescriptor;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.multisite.SiteManager;
import atg.projects.store.multisite.InternationalStoreSitePropertiesManager;
import atg.remote.assetmanager.editor.model.PropertyUpdate;
import atg.remote.assetmanager.editor.service.AssetEditorInfo;
import atg.remote.assetmanager.editor.service.CollectionPropertyServiceInfo;
import atg.remote.assetmanager.editor.service.RepositoryAssetPropertyServiceImpl;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.dynamo.LangLicense;
import atg.servlet.ServletUtil;

/**
 * An implementation of an AssetPropertyService for international store catalog repository properties.
 *
 * @author David Stewart
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/src/atg/projects/store/remote/multisite/service/InternationalStoreCatalogAssetPropertyService.java#2 $$Change: 791340 $
 * @updated $DateTime: 2013/02/19 09:03:40 $$Author: abakinou $
 */
public class InternationalStoreCatalogAssetPropertyService extends RepositoryAssetPropertyServiceImpl{

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = 
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/src/atg/projects/store/remote/multisite/service/InternationalStoreCatalogAssetPropertyService.java#2 $$Change: 791340 $";

  
  //--------------------------------------------------------------------------
  // CONSTANTS
  //--------------------------------------------------------------------------
  
  /** Resource name */
  private static final String RESOURCE_NAME = "atg.remote.multisite.Resources";

  /** siteIds property name */
  public static final String SITE_IDS = "siteIds";
  
  
  //--------------------------------------------------------------------------
  // PROPERTIES
  //--------------------------------------------------------------------------
  
  //---------------------------------------------------------------
  // property: storeSitePropertiesManager
  //---------------------------------------------------------------
  protected InternationalStoreSitePropertiesManager mStoreSitePropertiesManager;

  /**
   * @return the StoreSitePropertiesManager.
   */
  public InternationalStoreSitePropertiesManager getStoreSitePropertiesManager() {
    return mStoreSitePropertiesManager;
  }

  /**
   * @param StoreSitePropertiesManager the StoreSitePropertiesManager to set.
   */
  public void setStoreSitePropertiesManager(InternationalStoreSitePropertiesManager pStoreSitePropertiesManager) {
    mStoreSitePropertiesManager = pStoreSitePropertiesManager;
  }
  
  
  //--------------------------------------------------------------------------
  // METHODS
  //--------------------------------------------------------------------------
  
  //--------------------------------------------------------------------
  /**
   * @return The resource bundle to be used in this class.
   */
  public ResourceBundle getResourceBundle() {
    
    // Get the locale from the logged in user. If the user's locale can't be found,
    // get the browser locale. If either of these can't be found, use the server locale.
    Locale locale = ServletUtil.getUserLocale();
    
    if (locale != null) {
      // Get the resource bundle associated with the current locale.
      return LayeredResourceBundle.getBundle(RESOURCE_NAME, locale);
    }
    
    if (locale == null) {
      // Couldn't retrieve the locale from user's profile, browser or server so just get the default locale.
      return LayeredResourceBundle.getBundle(RESOURCE_NAME, LangLicense.getLicensedDefault());
    }
    
    return null;
  }
  
  
  //--------------------------------------------------------------------
  /**
   * Validate the repository item with the given property update.
   * 
   * @param pEditorInfo pEditorInfo Information object for the current editor.
   * @param pItem The current updated asset.
   * @param pUpdates URL property update.
   */
  @Override
  protected void validateItemPropertyUpdate(AssetEditorInfo pEditorInfo, 
                                            RepositoryItem pItem, 
                                            PropertyUpdate pUpdate) {
    super.validateItemPropertyUpdate(pEditorInfo, pItem, pUpdate);
    
    // Run 'translations' property key validation.
    if (pUpdate.getPropertyName().equals("translations")) {
      validateTranslationsLanguageKeyValue(pEditorInfo, pItem, pUpdate);
    } 
  }
  
  
  //--------------------------------------------------------------------
  /**
   * <p>
   * Validate the translations property keys to ensure they are valid language codes. An
   * invalid language code returns an error to the user.
   * </p>
   * <p>
   * This method also checks each site in the category/product/sku site list to see if 
   * an updated translation property key is supported by those sites. If the language
   * is not found in any of these sites 'languages' property lists, a warning is returned
   * to the user.   
   * </p>
   * 
   * @param pEditorInfo pEditorInfo Information object for the current editor.
   * @param pItem The current updated asset.
   * @param pUpdates URL property update.
   */
  public void validateTranslationsLanguageKeyValue(AssetEditorInfo pEditorInfo, 
                                                   RepositoryItem pItem, 
                                                   PropertyUpdate pUpdate) {
    
    // Get the translations property updates.
    Map<String,RepositoryItem> translations =
      (Map<String,RepositoryItem>) super.getCollectionPropertyUpdateValue(pEditorInfo, pItem, pUpdate);

    if (translations != null) {

      // List of languages to detail in invalid language warning message.
      List<String> languagesNotInSiteList = new ArrayList<String>();
      
      DynamicPropertyDescriptor desc = null;
      CollectionPropertyServiceInfo info = null;
      
      // Sites that contain the current category/product/sku.
      List<Map> sites = null;
      
      try {
        desc = pItem.getItemDescriptor().getPropertyDescriptor(SITE_IDS); 
      }
      catch (RepositoryException e) {
        if (isLoggingError()) {
          logError("There was a problem retrieving the itemDescriptor for " + pItem, e); 
        }
      }
  
      if (desc != null) {
        info = 
          (CollectionPropertyServiceInfo) pEditorInfo.getAssetPropertyServiceData().get(desc.getName());
    
        if (info == null || info.propertyValue == null || info.collectionDescriptorInfo == null) {
          if (isLoggingDebug()) {
            logDebug("Collection property " +  SITE_IDS + " does not have a cached value."); 
          }
        }
        else {
          sites = super.getCollectionPropertyValues(pEditorInfo, SITE_IDS, info, 0, 999);
        }
      }  
      
      // Get all language codes supported by the latest ISO language list.
      List<String> ISOlanguageCodes = Arrays.asList(Locale.getISOLanguages());
      // Retrieve the updated language code values. 
      Set<String> languageCodes = translations.keySet();

      for (String languageCode : languageCodes) {

        // Add an error if an updated language code doesn't exist in the ISO language list.
        if (!ISOlanguageCodes.contains(languageCode)) {

          if (isLoggingDebug()) {
            logDebug("The translations language code key (" + languageCode + 
              ") does not conform to the Java ISO locale standard.");
          }
          
          Object[] errorParams = { languageCode };

          pEditorInfo.getAssetService().addError(
            ResourceUtils.getUserMsgResource("catalog.error.translationLanguageKeyInvalid",
                                             RESOURCE_NAME,
                                             getResourceBundle(),
                                             errorParams
                                             ));
        }
        else if (sites != null) {
          // Check that the sites in the category/product/sku site list supports the updated translation key.
          
          List<String> siteList = new ArrayList<String>();
          
          for (int i = 0; i < sites.size(); i++) {
            Map<String,String> siteNames = sites.get(i);
            Collection<String> siteColValue = siteNames.values();
            Iterator it = siteColValue.iterator();
            
            while (it.hasNext()) {
              siteList.add((String) (it.next()));
            }
          }

          RepositoryItem[] items = null;
          
          try {
            items = SiteManager.getSiteManager().getAllSites();
          } 
          catch (RepositoryException e) {
            if (isLoggingError()) {
              logError("There was a problem retrieving all sites in system", e); 
            }
          }

          if (items != null) {

            siteLoop:
            for (String name : siteList) {

              // Get the RepositoryItem objects for sites in siteIds property.
              for (RepositoryItem item : items) {
                
                if (item.getItemDisplayName().equals(name)) {      
            
                  // Check if a site's languages list contains the updated language code. 
                  List<String> siteLanguages = (List<String>) 
                    item.getPropertyValue(getStoreSitePropertiesManager().getLanguagesPropertyName());
                  
                  if (siteLanguages.contains(languageCode)) {
                    if (languagesNotInSiteList.contains(languageCode)) {
                      languagesNotInSiteList.remove(languageCode);
                    }
                    
                    if (isLoggingDebug()) {
                      logDebug("The language code (" + languageCode + 
                        ") is valid as it has been set as a language code in site " + name);
                    }
                    
                    break siteLoop;
                  } 
                  else { 
                    if (!languagesNotInSiteList.contains(languageCode)) {
                      // Language code not set in current site's 'languages' list.
                      languagesNotInSiteList.add(languageCode);
                    }
                  }
                  
                  break;
                }
              }
            }
          }
        }
      }

      if (languagesNotInSiteList.size() > 0) {
        
        if (isLoggingDebug()) {
          logDebug("The language codes " + languagesNotInSiteList + 
            " are not available in any sites referenced by " + pItem.getItemDisplayName() + ", adding warning.");
        }
        
        // The updated language code(s) are not in the site's language list, add warning.
        Object[] errorParams = { languagesNotInSiteList, pItem.getItemDisplayName() };
        String errorResourceKey = "";
        
        if (languagesNotInSiteList.size() == 1) {
          errorResourceKey = "catalog.error.translationLanguageKeyNotAvailable";
        }
        else {
          errorResourceKey = "catalog.error.translationLanguageKeysNotAvailable";
        }
        
        pEditorInfo.getAssetService().addWarning(
          ResourceUtils.getUserMsgResource(errorResourceKey,
                                           RESOURCE_NAME,
                                           getResourceBundle(),
                                           errorParams
                                           ));
      }
    }
  }
  
}
