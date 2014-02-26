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


package atg.projects.store.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import atg.commerce.util.PlaceList;
import atg.core.i18n.CountryList;
import atg.droplet.DropletException;
import atg.nucleus.GenericService;
import atg.nucleus.naming.ParameterName;
import atg.projects.store.logging.LogUtils;
import atg.servlet.DynamoHttpServletRequest;

/**
 * This class contains different methods that are user in
 * AvailableShippingGroups droplet
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/util/CountryRestrictionsService.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */          
public class CountryRestrictionsService extends GenericService {

  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/util/CountryRestrictionsService.java#2 $$Change: 768606 $";

  /**
   * User locale parameter name.
   */
  private static final ParameterName USER_LOCALE = ParameterName.getParameterName("userLocale");

  /**
   * Resource bundle name.
   */
  private static final String RESOURCE_BUNDLE_NAME = "atg.commerce.util.CountryStateResources";

  /**
   * Check if country code is permitted.
   * 
   * @param pResultCountryList the results country list
   * @param pCountryCode the country code
   * 
   * @return true if pCountryCode is permitted, otherwise - false
   */
  public boolean isPermittedCountry(List pResultCountryList, String pCountryCode) {
    if ((pResultCountryList == null) || (pCountryCode == null)) {
      if (isLoggingDebug()) {
        logDebug("Either CountryList or CountryCode is null");
      }

      return false;
    }

    Iterator iterResultCountryLists = pResultCountryList.iterator();

    while (iterResultCountryLists.hasNext()) {
      PlaceList.Place places = (PlaceList.Place) iterResultCountryLists.next();

      if (pCountryCode.equals(places.getCode())) {
        return true;
      }
    }

    return false;
  }

  /**
   * This method removes the country codes (<i>codesToDelete</i>) from the
   * country list (<i>countryList</i>) and returns the resultant list having
   * localized country names.
   * 
   * @param pResourceBundle Resource Bundle used for country names localization.
   * @param pCodesToDelete ArrayList Country codes to delete.
   * @param pCountryList ArrayList List of countries.
   * @return ArrayList List of countries with desired country codes and their
   * localized country names.
   */
  public List removeCountryWithCode(ResourceBundle pResourceBundle,
      List pCodesToDelete, List pCountryList) {
    ArrayList returnList = new ArrayList(pCountryList.size());
    Iterator it = pCountryList.iterator();

    while (it.hasNext()) {
      PlaceList.Place country = (PlaceList.Place) it.next();

      if (!(pCodesToDelete.contains(country.getCode()))) {
        String localeDisplayName = getDisplayNameForCode(pResourceBundle,
            country.getCode(), country.getDisplayName());
        returnList.add(new PlaceList.Place(country.getCode(), localeDisplayName));

        if (isLoggingDebug()) {
          logDebug(LogUtils.formatMajor("Country removed from list:"
              + country.getCode()));
        }
      }
    }

    returnList.trimToSize();

    return returnList;
  }

  /**
   * This method returns the localized display name for the country code
   * provided as input.
   * 
   * @param pResourceBundle Resource Bundle used for country name localization.
   * @param pCode String Country code.
   * @param pDefault String Default value if localized country name is not found.
   * @return String localized country name or the default valie if localized
   * country name is not found.
   */
  public String getDisplayNameForCode(ResourceBundle pResourceBundle,
      String pCode, String pDefault) {
    String returnString = pDefault;

    if ((pResourceBundle != null) && (pCode != null)) {
      try {
        returnString = pResourceBundle.getString("CountryCode." + pCode);
      } catch (MissingResourceException ex) {
        if (isLoggingError()) {
          logError("Can't find Country Code in Resource Bundle ", ex);
        }
      }
    }

    return returnString;
  }

  /**
   * Gets the resource bundle for user local, specified in request
   * 
   * @param pRequest the DynamoHttpServletRequest
   * @return the resource bundle
   */
  public ResourceBundle getResourceBundle(DynamoHttpServletRequest pRequest) {
    ResourceBundle resourceBundle = null;

    Locale locale = (Locale) pRequest.getObjectParameter(USER_LOCALE);

    try {
      if (locale == null) {
        resourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);
      } else {
        resourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(
            RESOURCE_BUNDLE_NAME, locale);
      }
    } catch (MissingResourceException ex) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Missing resource bundle -> "
            + RESOURCE_BUNDLE_NAME + ex), ex);
      }

      resourceBundle = null;
    }

    return resourceBundle;
  }

  /**
   * Checks if country is in permitted country list
   * 
   * @param countryCode
   *          code of country to look in permitted country list
   * @param pPermittedCountryList
   *          list of permitted country codes
   * @return true, if countryCode is in permitted country list, false othwerwise
   */
  public boolean checkCountryInPermittedList(String countryCode,
      List pPermittedCountryList) {

    if (countryCode != null) { // checks if mCountryCode is in the list of
      // permitted countries

      if ((pPermittedCountryList != null)
          && isPermittedCountry(pPermittedCountryList, countryCode)) {
        return true;
      } else {
        return false;
      }
    }

    return false;
  }

  /**
   * Gets the list of permitted country codes
   * 
   * @param pRequest
   *          the DynamoHttpServletRequest
   * @param pPermittedCountryCodes
   *          the permitted country codes
   * @param pRestrictedCountryCodes
   *          the restricted country codes
   * @param pCountryList
   *          the country list
   * @return the list of permitted country codes
   */
  public List getPermittedCountryList(DynamoHttpServletRequest pRequest,
      List pPermittedCountryCodes, List pRestrictedCountryCodes,
      CountryList pCountryList) {
    List listCountryCds = null;
    List resultCountryList = null; // Place[]
    ResourceBundle resourceBundle = getResourceBundle(pRequest);

    listCountryCds = pPermittedCountryCodes;

    if ((listCountryCds != null) && !(listCountryCds.isEmpty())) { // PermittedCountryCodes
      // are
      // specified
      resultCountryList = new ArrayList(listCountryCds.size());

      Iterator iterator = listCountryCds.iterator();

      while (iterator.hasNext()) {
        String code = (String) iterator.next();
        PlaceList.Place place = pCountryList.getPlaceForCode(code);
        resultCountryList.add(new PlaceList.Place(code, getDisplayNameForCode(
            resourceBundle, code, place.getDisplayName())));
      }
    } else { // RestrictedCountryCodes are specified
      listCountryCds = pRestrictedCountryCodes;

      if ((listCountryCds != null) && !(listCountryCds.isEmpty())) {
        PlaceList.Place[] places = pCountryList.getPlaces();

        if (places != null) {
          resultCountryList = new ArrayList(Arrays.asList(places));

          Iterator iterator = listCountryCds.iterator();
          ArrayList codesToDelete = new ArrayList(listCountryCds.size());

          while (iterator.hasNext()) {
            String code = (String) iterator.next();
            codesToDelete.add(code);
          }

          resultCountryList = removeCountryWithCode(resourceBundle,
              codesToDelete, resultCountryList);
        }
      } else { // DEFAULT - ALL

        PlaceList.Place[] places = pCountryList.getPlaces();

        if (places != null) {
          PlaceList.Place[] localizedPlaces = new PlaceList.Place[places.length];

          for (int i = 0; i < places.length; i++) {
            String code = places[i].getCode();
            String localizedDisplayName = getDisplayNameForCode(resourceBundle,
                code, places[i].getDisplayName());
            localizedPlaces[i] = new PlaceList.Place(code, localizedDisplayName);
          }

          resultCountryList = new ArrayList(Arrays.asList(localizedPlaces));
          places = null;
          localizedPlaces = null;
        }
      }
    } // END RestrictedCountryCodes are specified

    return resultCountryList;
  }
  
  /**
   * This static method gets localized country name from the Resource Bundle based on the input locale.
   * (Provided for ShippingGroupFormHandler)
   * @param pCountryCode String Country Code.
   * @param pUserLocale Locale user locale.
   * @return String localized country name.
   * @throws DropletException an exception within Droplet
   */
  public static String getCountryName(String pCountryCode, Locale pUserLocale) throws DropletException {
    ResourceBundle sResourceBundle = null;
    String returnString = pCountryCode;

    try {
      if (pUserLocale == null) {
        sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);
      } else {
        sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, pUserLocale);
      }

      if ((sResourceBundle != null) && (pCountryCode != null)) {
        returnString = sResourceBundle.getString("CountryCode." + pCountryCode);
      }
    } catch (MissingResourceException ex) {
        throw new DropletException("Can't find Country Code in Resource Bundle ", ex);
    }

    return returnString;
  }
}
