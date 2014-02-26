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


package atg.projects.store.servlet.pipeline;


import atg.projects.store.profile.StorePropertyManager;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfilePropertySetter;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;


/**
 * <p>Update the store profile properties based on values for the current site.
 *
 * @author ATG
 *
 */
public class StoreProfilePropertySetter extends ProfilePropertySetter {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/servlet/pipeline/StoreProfilePropertySetter.java#2 $$Change: 768606 $";
  
  public static final String USER_PREF_LANGUAGE_COOKIE_NAME = "userPrefLanguage";
  
  private StorePropertyManager mPropertyManager;


  /**
   * Getter method for the <code>propertyManager</code> property. This manager will be used
   * to retrieve profile-related property names.
   * @return {@code PropertyManager} to be used.
   */
  public StorePropertyManager getPropertyManager() {
    return mPropertyManager;
  }

  public void setPropertyManager(StorePropertyManager pPropertyManager) {
    mPropertyManager = pPropertyManager;
  }

  /**
   * Update the store profile properties based on values for the current site 
   * @param pProfile - Profile to set properties for
   * @param pRequest - Current request
   * @param pResponse - Current response
   * @return true
   * @throws IOException an error occurred reading data from the request
   * or writing data to the response.
   * @throws ServletException an application specific error occurred
   * processing this request
   * @throws RepositoryException indicates that a severe error occured while performing a Repository task
   */
  public boolean setProperties(Profile pProfile,
                               DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse)
      throws IOException, ServletException, RepositoryException {
    // Resolve StoreRequestLocale created by DynamoHandler. userPrefLang cookie is written to browser.
    RequestLocale requestLocale = pRequest.getRequestLocale();
    if (requestLocale != null) {
      createUserPrefLangCookie(pRequest, pResponse, requestLocale.getLocale());
      updateProfileLocale(pProfile, requestLocale.getLocale());
    }

    return true;
  }
  
  /**
   * This operation creates cookie for user preffered language.
   * @param pRequest DynamoHttpServletRequest object.
   * @param pLocale User preferred language.
   * 
   */
  protected void createUserPrefLangCookie(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, Locale pLocale) {
    Cookie userPrefLanguageCookie = ServletUtil.createCookie(USER_PREF_LANGUAGE_COOKIE_NAME, pLocale.toString());
    userPrefLanguageCookie.setMaxAge(1567800000); // This cookie will be valid for 50*365*24*60*60 = 50 years
    userPrefLanguageCookie.setPath(pRequest.getContextPath() + "/");
    pResponse.addCookie(userPrefLanguageCookie);
  }

  /**
   * Updates locale in profile.
   * @param pRequest DynamoHttpServletRequest object.
   * @param pLocale Profile Locale.
   */
  protected void updateProfileLocale(Profile pProfile, Locale pLocale) {
    String localePropertyName = getPropertyManager().getLocalePropertyName();
    if (pLocale == null) {
      setProfileProperty(pProfile, localePropertyName, null);
    } else {
      try {
        setProfileProperty(pProfile, localePropertyName, pLocale.toString());
      } catch (IllegalArgumentException e) {
        // Wrong locale value, just erase it.
        pProfile.setPropertyValue(localePropertyName, null);
      }
    }
  }
}

