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



package atg.projects.store.remote.multisite.service.validation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.projects.store.multisite.StoreSitePropertiesManager;
import atg.remote.assetmanager.editor.model.PropertyUpdate;
import atg.remote.assetmanager.editor.service.AssetEditorInfo;
import atg.remote.multisite.service.SiteAssetService;
import atg.remote.multisite.service.validation.SiteAssetServiceValidator;
import atg.service.dynamo.LangLicense;
import atg.servlet.ServletUtil;

/**
 * This validator ensures a site's country codes are valid. 
 * 
 * @author dstewart
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/Versioned/src/atg/projects/store/remote/multisite/service/validation/CountryCodeValidator.java#2 $$Change: 791340 $
 * @updated $DateTime: 2013/02/19 09:03:40 $$Author: abakinou $
 */
public class CountryCodeValidator extends GenericService implements SiteAssetServiceValidator {

  /** Class version string */
  public static String CLASS_VERSION = 
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/Versioned/src/atg/projects/store/remote/multisite/service/validation/CountryCodeValidator.java#2 $$Change: 791340 $";

  // Resource name.
  protected static final String RESOURCE_NAME = "atg.remote.multisite.Resources";
  

  //--------------------------------------------------------------------------
  // PROPERTIES
  //--------------------------------------------------------------------------
  
  //---------------------------------------------------------------
  // property: storeSitePropertiesManager
  //---------------------------------------------------------------
  protected StoreSitePropertiesManager mStoreSitePropertiesManager;

  /**
   * @return the StoreSitePropertiesManager
   */
  public StoreSitePropertiesManager getStoreSitePropertiesManager() {
    return mStoreSitePropertiesManager;
  }

  /**
   * @param StoreSitePropertiesManager the StoreSitePropertiesManager to set
   */
  public void setStoreSitePropertiesManager(StoreSitePropertiesManager pStoreSitePropertiesManager) {
    mStoreSitePropertiesManager = pStoreSitePropertiesManager;
  }

  
  //--------------------------------------------------------------------
  //  METHODS
  //--------------------------------------------------------------------
  
  //--------------------------------------------------------------------
  /**
   * @return The resource bundle based on the user's locale.
   */
  public ResourceBundle getResourceBundle() {
    // Get the locale from the logged in user. If the user's locale can't be found,
    // get the browser locale. If either of these can't be found, use the server locale.
    Locale locale = ServletUtil.getUserLocale();
    
    if (locale != null) {
      // Get the resource bundle associated with the current locale.
      return LayeredResourceBundle.getBundle(RESOURCE_NAME, locale);
    }
    
    // Couldn't retrieve the locale from user's profile, browser or server so just get the default locale.
    return LayeredResourceBundle.getBundle(RESOURCE_NAME, LangLicense.getLicensedDefault());
  }
  
  
  //--------------------------------------------------------------------
  /**
   * This method will run validation on site property updates. If validation
   * fails, an error or warning will be created to be displayed to the user.
   * 
   * @param pEditorInfo Information object for the current editor.
   * @param pUpdates Collection of PropertyEditorAssetViewUpdate objects.
   */
  @Override
  public void validate(AssetEditorInfo pEditorInfo, Collection pUpdates) {

    SiteAssetService sas = new SiteAssetService();
    
    // Get all current property updates.
    List<PropertyUpdate> updates = sas.getPropertyUpdates(pUpdates);
    
    if (updates != null) {
      
      // Iterate through all URL property updates.
      for (PropertyUpdate propUpdate : updates) {
        // Get updated property name
        String name = propUpdate.getPropertyName();
        
        if (name.equals(getStoreSitePropertiesManager().getDefaultCountryPropertyName())) {
 
          String countryCodeValue = (String) propUpdate.getPropertyValue();
          
          if (!StringUtils.isEmpty(countryCodeValue)) {
            List<String> countryCodes = Arrays.asList(Locale.getISOCountries());
  
            if (countryCodeValue != null && !countryCodes.contains(countryCodeValue)) {
              
              pEditorInfo.getAssetService().addError(
                ResourceUtils.getUserMsgResource("siteLocale.error.defaultCountryCodeInvalid",
                                                 RESOURCE_NAME,
                                                 getResourceBundle()));
            }
          }
        }
      }
    } 
  }

}
