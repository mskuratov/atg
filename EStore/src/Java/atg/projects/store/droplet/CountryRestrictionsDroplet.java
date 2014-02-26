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


package atg.projects.store.droplet;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.ServletException;

import atg.commerce.util.PlaceList;
import atg.core.i18n.CountryList;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.nucleus.naming.ParameterName;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.util.CountryRestrictionsService;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;


/**
 * <p>
 * This droplet is used to obtain a list of permitted countries in various situations. By specifying an 
 * optional country code we are able to return the details of that specific country along with an oparam to 
 * indicate whether or not it is restricted or permitted. In the event of no country code being specified the 
 * droplet simply returns a list of all permitted countries.
 * </p>
 * 
 * <p>
 * The droplet takes the following input parameters:
 * 
 * <ul>
 *   <li>countryCode (optional)
 *   The code of the country we wish to check in order to gauge whether or not it is restricted or permitted.
 * </ul>  
 * </p>
 * 
 * <p>
 * <ul>
 * The droplet renders the following open parameters:
 *   <li>true - the specified country code is restricted.
 *   <li>false - the specified country code is permitted.
 *   <li>output - a country code has not been specified so a list of all 
 *   permitted countries has been returned.
 * </ul>  
 * </p>
 * 
 * <p>
 * The droplet sets the following output parameters:
 * 
 * <ul>
 *   <li>countryDetail
 *   The details of the country relating to the specified country code. This is returned when a country code 
 *   is specified.
 *   
 *   <li>countries
 *   A list of all permitted country codes. This is returned when no country code is specified.
 * </ul>
 * </p>
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/CountryRestrictionsDroplet.java#4 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 * </p>
 */
public class CountryRestrictionsDroplet extends DynamoServlet {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/CountryRestrictionsDroplet.java#4 $$Change: 788278 $";

  /**
   * Country code parameter name.
   */
  public static final ParameterName COUNTRY_CODE = ParameterName.getParameterName("countryCode");

  /**
   * Countries parameter name.
   */
  public static final String COUNTRIES_PARAM = "countries";

  /**
   * Country details parameter name.
   */
  public static final String COUNTRY_DETAIL_PARAM = "countryDetail";

  /**
   * Oparam: output.
   */
  public static final String OUTPUT_OPARAM = "output";

  /**
   * Oparam: true.
   */
  public static final String TRUE_OPARAM = "true";

  /**
   * Oparam: false.
   */
  public static final String FALSE_OPARAM = "false";

  /**
   * Permitted countries property name.
   */
  private String mPermittedCountriesPropertyName;

  /**
   * Restricted countries property name.
   */
  private String mRestrictedCountriesPropertyName;

  /**
   * Country list.
   */
  private CountryList mCountryList;

  /**
   * @return permitted countries property name.
   */
  public String getPermittedCountriesPropertyName() {
    return mPermittedCountriesPropertyName;
  }

  /**
   * @param pPermittedCountriesPropertyName - permitted countries property name.
   */
  public void setPermittedCountriesPropertyName(String pPermittedCountriesPropertyName) {
    mPermittedCountriesPropertyName = pPermittedCountriesPropertyName;
  }

  /**
   * @return restricted countries property name.
   */
  public String getRestrictedCountriesPropertyName() {
    return mRestrictedCountriesPropertyName;
  }

  /**
   * @param pRestrictedCountriesPropertyName - restricted countries property name.
   */
  public void setRestrictedCountriesPropertyName(String pRestrictedCountriesPropertyName) {
    mRestrictedCountriesPropertyName = pRestrictedCountriesPropertyName;
  }

  /**
   * @return country list.
   */
  public CountryList getCountryList() {
    return mCountryList;
  }

  /**
   * @param pCountryList - master country list.
   */
  public void setCountryList(CountryList pCountryList) {
    mCountryList = pCountryList;
  }

  /**
   * the country restrictions service
   */
  private CountryRestrictionsService mCountryRestrictionsService;
    
  /**
   * @return the countryRestrictionsService
   */
  public CountryRestrictionsService getCountryRestrictionsService() {
    return mCountryRestrictionsService;
  }

  /**
   * @param pCountryRestrictionsService the countryRestrictionsService to set
   */
  public void setCountryRestrictionsService(
      CountryRestrictionsService pCountryRestrictionsService) {
    mCountryRestrictionsService = pCountryRestrictionsService;
  }

  /**
   * Service method.
   *
   * @param pRequest - http request
   * @param pResponse - http response
   *
   * @throws ServletException if an error occurs
   * @throws IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    Site currentSite = SiteContextManager.getCurrentSiteContext().getSite();

    List permittedCountryCodes = (List) currentSite.getPropertyValue(getPermittedCountriesPropertyName());
    List restrictedCountryCodes = (List) currentSite.getPropertyValue(getRestrictedCountriesPropertyName());

    ResourceBundle resourceBundle = getCountryRestrictionsService().getResourceBundle(pRequest);
      
    List resultCountryList = getCountryRestrictionsService().getPermittedCountryList(pRequest, 
      permittedCountryCodes, restrictedCountryCodes, getCountryList());

    String countryCode = (String)pRequest.getLocalParameter(COUNTRY_CODE);

    if (countryCode != null) { // checks if mCountryCode is in the list of permitted countries

      if ((resultCountryList != null) && getCountryRestrictionsService().isPermittedCountry(resultCountryList, countryCode)) {
        pRequest.setParameter(COUNTRY_DETAIL_PARAM,
          new PlaceList.Place(countryCode, getCountryRestrictionsService().getDisplayNameForCode(resourceBundle, countryCode, "")));
        pRequest.serviceLocalParameter(FALSE_OPARAM, pRequest, pResponse);
      } else {
        pRequest.setParameter(COUNTRY_DETAIL_PARAM,
          new PlaceList.Place(countryCode, getCountryRestrictionsService().getDisplayNameForCode(resourceBundle, countryCode, "")));
        pRequest.serviceLocalParameter(TRUE_OPARAM, pRequest, pResponse);
      }
    } else {
      pRequest.setParameter(COUNTRIES_PARAM, resultCountryList);
      pRequest.serviceLocalParameter(OUTPUT_OPARAM, pRequest, pResponse);

      if (isLoggingDebug()) {
        logDebug(LogUtils.formatMajor("++++++inside CountryRestrictionDroplet.service+++++++"));

        Iterator ite = resultCountryList.iterator();

        while (ite.hasNext()) {
          PlaceList.Place pl = (PlaceList.Place) ite.next();
          logDebug(LogUtils.formatMajor("code = " + pl.getCode() + " : " + pl.getDisplayName()));
        }

        logDebug(LogUtils.formatMajor("++++++exiting CountryRestrictionDroplet.service+++++++"));
      }
    }
  }



}
