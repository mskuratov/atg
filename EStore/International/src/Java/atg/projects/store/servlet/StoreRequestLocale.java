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


package atg.projects.store.servlet;

import java.util.Locale;

import atg.core.util.StringUtils;
import atg.multisite.Site;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.projects.store.servlet.pipeline.StoreProfilePropertySetter;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.RequestLocale;
import atg.userprofiling.ProfileRequestLocale;
import java.util.List;

/**
 * The extensions to ootb RequestLocale.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/src/atg/projects/store/servlet/StoreRequestLocale.java#2 $
*/
public class StoreRequestLocale extends ProfileRequestLocale {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/src/atg/projects/store/servlet/StoreRequestLocale.java#2 $$Change: 768606 $";



  //-------------------------------
  // Constants
  //-------------------------------
  public static final String LANG_SELECTION_PARAMETER = "locale";
  public static final String PROFILE_LOCALE_UNSET_VALUE = "unset";
  
  /**
   * Site's default country attribute name .
   */
  public static final String DEFAULT_COUNTRY_ATTRIBUTE_NAME = "defaultCountry";
  
  /**
   * Site's default language attribute name .
   */
  public static final String DEFAULT_LANGUAGE_ATTRIBUTE_NAME = "defaultLanguage";

  /**
   *
   */
  public static final String LANGUAGES_ATTRIBUTE_NAME = "languages";

  //-------------------------------
  // Properties
  //-------------------------------

  //-------------------------------
  // property: Logger
  private static ApplicationLogging mLogger =
    ClassLoggingFactory.getFactory().getLoggerForClass(StoreRequestLocale.class);

  /**
   * @return ApplicationLogging object for logger.
   */
  private ApplicationLogging getLogger()  {
    return mLogger;
  }

  /**
   * Obtains locale from http request.
   *
   * @param pRequest DynamoHttpServletRequest
   * @param pReqLocal Request locale
   * @return Locale object
   */
  public Locale discernRequestLocale(DynamoHttpServletRequest pRequest, RequestLocale pReqLocal) {

    if (getLogger().isLoggingDebug()){
      getLogger().logDebug("StoreRequestLocale:discernRequestLocale:Entry");
    }

    Locale locale;

    if(getLogger().isLoggingDebug()){
      getLogger().logDebug("StoreRequestLocale:discernRequestLocale:initLocale from LangSelection");
    }

    locale = fillLocaleFromLangSelection(pRequest);
    
    if(locale==null) {
      if(getLogger().isLoggingDebug()){
        getLogger().logDebug("StoreRequestLocale:discernRequestLocale:initLocale from storeSelection");
      } 
      locale = fillLocaleFromStoreSelection(pRequest,pReqLocal);
    }
    
    if(locale==null) {
      if(getLogger().isLoggingDebug()){
         getLogger().logDebug("StoreRequestLocale:discernRequestLocale:initLocale from Profile");
      }  
       locale = fillLocaleFromProfile(pRequest,pReqLocal);
    }

    if(locale==null) {
      if(getLogger().isLoggingDebug()){
        getLogger().logDebug("StoreRequestLocale:discernRequestLocale:initLocale from UserPrefLang");
      }  
      locale = fillLocaleFromUserPrefLang(pRequest);
    }

    if(locale==null) {
      if(getLogger().isLoggingDebug()){
        getLogger().logDebug("StoreRequestLocale:discernRequestLocale:initLocale from super.discernRequestLocale(pRequest,pReqLocal");
      }  
      locale = super.discernRequestLocale(pRequest,pReqLocal);
    }

    if(getLogger().isLoggingDebug()) {
      getLogger().logDebug("StoreRequestLocale:discernRequestLocale:locale="+locale);
      getLogger().logDebug("StoreRequestLocale:discernRequestLocale:Exit");
    }

    return locale;
  }

  /**
   * @param pRequest DynamoHttpServletRequest object.
   * @return Locale based on language selection.
   */
  protected Locale fillLocaleFromLangSelection(DynamoHttpServletRequest pRequest) {

    if(getLogger().isLoggingDebug()){
      getLogger().logDebug("StoreRequestLocale:discernRequestLocale:fillLocaleFromLangSelection:Entry");
    }
    Locale locale = null;

    //langSelection parameter set by Affiliates and laguage dropdown
    String langSelection = pRequest.getParameter(LANG_SELECTION_PARAMETER);
    if(langSelection!=null && !langSelection.trim().equals("")){
      //init locale using langSelection
      //update/create persistent cookie "userPrefLang"
      //update profile.locale if different

      locale = RequestLocale.getCachedLocale(langSelection);
      /*
       *  Check if locale in request is applied for current site. 
       *  If not applied the method shouldn't return locale from
       *  request.
       */
      if (!langAppliedForSite(pRequest, locale)){
        locale =  null;
      }
    }
    if(getLogger().isLoggingDebug()) {
      getLogger().logDebug("StoreRequestLocale:fillLocaleFromLangSelection:locale="+locale);
      getLogger().logDebug("StoreRequestLocale:fillLocaleFromLangSelection:Exit");
    }    
    return locale;
  }

  /**
   * Check if language can be applied to current site
   * @param pRequest DynamoHttpServletRequest object
   * @param pLocal the request locale to  check
   * @return true if language can be applied to current site,
   *   false otherwise.
   */
  private boolean langAppliedForSite(DynamoHttpServletRequest pRequest,
      Locale pLocal) {    
    List<String> storeLanguages = null;
    Site site = null;
    SiteContext currentSiteContext = null;
    currentSiteContext = SiteContextManager.getCurrentSiteContext();
    boolean langAppliedForCurrentSite = false;
    
    if(currentSiteContext != null) {
      site = currentSiteContext.getSite();

      if(site != null) {
        // Get site languages
        storeLanguages = (List<String>)site.getPropertyValue(LANGUAGES_ATTRIBUTE_NAME);
        // Get language from locale
        String localeLang = pLocal.getLanguage();
        
        if(storeLanguages.contains(localeLang)){
          langAppliedForCurrentSite = true;
        }
      }      
    }
    return langAppliedForCurrentSite;
  }

  /**
   * @param pRequest DynamoHttpServletRequest object.
   * @param pReqLocal Request locale
   * @return Locale based on store selection.
   */
  protected Locale fillLocaleFromStoreSelection(DynamoHttpServletRequest pRequest,RequestLocale pReqLocal) {

    if(getLogger().isLoggingDebug()){
      getLogger().logDebug("StoreRequestLocale:discernRequestLocale:fillLocaleFromStoreSelection:Entry");
    }
    Locale locale = null;
    
    String  storeLocaleCode = getStoreLocaleCode(pRequest,pReqLocal);

    if(!StringUtils.isEmpty(storeLocaleCode)) {
      if(getLogger().isLoggingDebug()){
        getLogger().logDebug("StoreRequestLocale:discernRequestLocale:fillLocaleFromStore:storeDefaultLanguage is not null="+storeLocaleCode);
      }
      //init locale using storeDefaultLanguage
      //create persistent cookie "userPrefLang" to storeDefaultLanguage
      //update profile.local if different
  
      locale = RequestLocale.getCachedLocale(storeLocaleCode);
    }
    else {
      if(getLogger().isLoggingDebug()){
        getLogger().logDebug("StoreRequestLocale:discernRequestLocale:fillLocaleFromStore:storeDefaultLanguage is null.");
      }
    }
    
    if(getLogger().isLoggingDebug()) {
      getLogger().logDebug("StoreRequestLocale:fillLocaleFromStoreSelection:locale="+locale);
      getLogger().logDebug("StoreRequestLocale:fillLocaleFromStoreSelection:Exit");
    }
    return locale;
  }

  /**
   * Determines the locale code to use for the store.
   * If the current user profile language is in the list of site languages then the locale code
   * is constructed from the profile language & site default country code; otherwise if the user
   * profile language is not in the list of site languages then the locale code is constructed
   * from the site default language & site default country code.
   *
   * @param pRequest DynamoHttpServletRequest object.
   * @param pReqLocal Request locale
   * @return Locale Code
   */
  private String getStoreLocaleCode(DynamoHttpServletRequest pRequest,RequestLocale pReqLocal) {
    String storeLocaleCode = "";

    List storeLanguages = null;
    String storeDefaultLanguage = "";
    String storeDefaultCountry = "";

    Site site = null;
    SiteContext currentSiteContext = null;

    String profileLanguage = "";
    Locale profileLocale = null;


    currentSiteContext = SiteContextManager.getCurrentSiteContext();
    if(currentSiteContext!=null) {
      site = currentSiteContext.getSite();

      if(site!=null) {
        storeLanguages = (List)site.getPropertyValue(LANGUAGES_ATTRIBUTE_NAME);
        storeDefaultLanguage = (String)site.getPropertyValue(DEFAULT_LANGUAGE_ATTRIBUTE_NAME);
        storeDefaultCountry = (String)site.getPropertyValue(DEFAULT_COUNTRY_ATTRIBUTE_NAME);

        profileLocale = localeFromProfileAttribute(pRequest,pReqLocal);
        if(profileLocale!=null) {
          profileLanguage = profileLocale.getLanguage();

          // Create locale code from the current profile language and site default country code
          if (storeLanguages!=null && storeLanguages.contains(profileLanguage)) {
            storeLocaleCode = profileLanguage+"_"+storeDefaultCountry;
          }
        }

        if(StringUtils.isEmpty(storeLocaleCode) && !StringUtils.isEmpty(storeDefaultLanguage) 
            && !StringUtils.isEmpty(storeDefaultCountry)) {
          // Create locale code from the site default language and country code
         storeLocaleCode = storeDefaultLanguage+"_"+storeDefaultCountry;         
        }
      }
    }

    return storeLocaleCode;
  }

  /**
   * @param pRequest DynamoHttpServletRequest object.
   * @return Locale based on user preffered language.
   */
  protected Locale fillLocaleFromUserPrefLang(DynamoHttpServletRequest pRequest) {

    if(getLogger().isLoggingDebug()) {
      getLogger().logDebug("StoreRequestLocale:discernRequestLocale:fillLocaleFromUserPrefLang:Entry");
    }
    Locale locale = null;

    String userPrefLang = pRequest.getCookieParameter(StoreProfilePropertySetter.USER_PREF_LANGUAGE_COOKIE_NAME);
    if(userPrefLang!=null && !userPrefLang.trim().equals("")) {
      //init locale using userPrefLang
      //update profile.locale if different

      locale = RequestLocale.getCachedLocale(userPrefLang);
    }
    if(getLogger().isLoggingDebug()) {
      getLogger().logDebug("StoreRequestLocale:fillLocaleFromUserPrefLang:locale="+locale);
      getLogger().logDebug("StoreRequestLocale:fillLocaleFromUserPrefLang:Exit");
    }
    return locale;
  }

  /**
   * @param pRequest DynamoHttpServletRequest object.
   * @param pReqLocal RequestLocale object.
   * @return Locale based on profile.
   */
  protected Locale fillLocaleFromProfile(DynamoHttpServletRequest pRequest,RequestLocale pReqLocal) {

    if(getLogger().isLoggingDebug()){
      getLogger().logDebug("StoreRequestLocale:discernRequestLocale:fillLocaleFromProfile:Entry");
    }
    Locale locale = null;

    //Retrieve from Profile
    Locale profileLocale = localeFromProfileAttribute(pRequest,pReqLocal);
    if(profileLocale!=null && !PROFILE_LOCALE_UNSET_VALUE.equals(profileLocale.toString())) {
      locale = RequestLocale.getCachedLocale(profileLocale.toString());
    }

    if(getLogger().isLoggingDebug()) {
      getLogger().logDebug("StoreRequestLocale:fillLocaleFromProfile:locale="+locale);
      getLogger().logDebug("StoreRequestLocale:fillLocaleFromProfile:Exit");
    }
    return locale;
  }
}

