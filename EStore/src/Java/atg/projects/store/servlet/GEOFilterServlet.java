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

import atg.core.util.StringUtils;

import atg.projects.store.logging.LogUtils;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import atg.servlet.pipeline.InsertableServletImpl;

import java.io.IOException;

import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletException;


/**
 * This servlet performs rudimentary GEOFiltering functionality by checking the
 * request's locale and rerouting the user's destination based upon that.
 *
 * @author ATG
 * @version $Revision: #2 $
 */
public class GEOFilterServlet extends InsertableServletImpl {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/servlet/GEOFilterServlet.java#2 $$Change: 768606 $";

  /**
   * If true, this servlet will perform GEOFiltering If false, it will simply
   * pass all requests through untouched.
   */
  private boolean mEnabled = true;

  /**
   * Stores a map of country codes to redirect URLs.
   */
  private Properties mCountryRedirectURLs;

  /**
   * Default URL to redirect to.
   */
  private String mDefaultRedirectURL;

  /**
   * Returns the current "enabled" state of the servlet. If true, GEOFiltering
   * is enabledd.  If false, no GEOFiltering is done.
   *
   * @beaninfo description: If true, specified IP addresses are being blocked.
   * @return true if enabled, false - otherwise
   */
  public boolean isEnabled() {
    return mEnabled;
  }

  /**
   * Changes the current "enabled" state of the servlet. If true, GEOFiltering
   * is enabledd. If false, no GEOFiltering is done.
   * @param pEnabled - true to enable, false - otherwise
   */
  public void setEnabled(boolean pEnabled) {
    mEnabled = pEnabled;
  }

  /**
   * Stores a map of country codes to redirect URLs.
   *
   * @param pCountryRedirectURLs - country redirect URLs
   */
  public void setCountryRedirectURLs(Properties pCountryRedirectURLs) {
    mCountryRedirectURLs = pCountryRedirectURLs;
  }

  /**
   * Stores a map of country codes to redirect URLs.
   *
   * @return country redirect URLs
   */
  public Properties getCountryRedirectURLs() {
    return mCountryRedirectURLs;
  }

  /**
   * Default URL to redirect to if the country code is not the US and if the
   * country code does not have an explicit redirect location set in
   * <code>countryRedirectURLs</code>.
   *
   * @param pDefaultRedirectURL - default redirect URL
   */
  public void setDefaultRedirectURL(String pDefaultRedirectURL) {
    mDefaultRedirectURL = pDefaultRedirectURL;
  }

  /**
   * Default URL to redirect to if the country code is not the US and if the
   * country code does not have an explicit redirect location set in
   * <code>countryRedirectURLs</code>.
   *
   * @return default redirect URL
   */
  public String getDefaultRedirectURL() {
    return mDefaultRedirectURL;
  }

  /**
   * Look at the locale header in the request and determine if we need to
   * redirect based upon that. This servlet makes the locale determination
   * based <b>purely </b> on the Accept-Language header. This is the preferred
   * locale as set by the client.
   * <p>
   * If the locale country is not US then redirect according to the url
   * fetched from the <code>countryRedirectURLs</code>. If the country does
   * not have a URL mapped to it, redirect to the
   * <code>defaultRedirectURL</code>.
   *
   * @param pRequest - http request
   * @param pResponse - http response
   * @throws IOException if IO error occurs
   * @throws ServletException if servlet error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws IOException, ServletException {
    // easy disable switch
    if (!mEnabled) {
      super.service(pRequest, pResponse);

      return;
    }

    // find out where they are coming from
    // This locale is based purely upon the Accept-Language header
    Locale clientLocale = pRequest.getLocale();

    // debug the locale
    if (isLoggingDebug()) {
      superDebug(clientLocale, true);
    }

    // get the country code
    String countryCode = clientLocale.getCountry();

    // if no country code just let them through
    if (StringUtils.isEmpty(countryCode)) {
      if (isLoggingDebug()) {
        logDebug("Country Code is empty, just letting them through");
      }

      super.service(pRequest, pResponse);

      return;
    } else if ("US".equals(countryCode)) {
      // US customer. Let them in
      super.service(pRequest, pResponse);

      return;
    } else {
      String redirectURL = null;

      // see if the country code is in the map
      Properties redirects = getCountryRedirectURLs();

      if (redirects != null) {
        redirectURL = (String) redirects.get(countryCode);
      }

      // if we don't have one yet, use the default
      if (StringUtils.isEmpty(redirectURL)) {
        redirectURL = getDefaultRedirectURL();
      }

      if (!StringUtils.isEmpty(redirectURL)) {
        if (isLoggingDebug()) {
          logDebug("Redirecting to " + redirectURL);
        }

        pResponse.sendRedirect(redirectURL);

        return;
      } else {
        if (isLoggingDebug()) {
          logDebug(LogUtils.formatMajor("No redirect url found for country " + countryCode + " - allowing site access"));
        }
      }
    }

    super.service(pRequest, pResponse);
  }

  /**
   * Massive debugging information on the locale.
   *
   * @param pLocale - the locale to debug
   * @param pOmitDisplayOfAll - omits the display of all available locales
   */
  private void superDebug(Locale pLocale, boolean pOmitDisplayOfAll) {
    if (isLoggingDebug()) {
      StringBuilder bul = new StringBuilder();

      bul.append("\n");
      bul.append("country = " + pLocale.getCountry()).append("\n");
      bul.append("displayCountry = " + pLocale.getDisplayCountry()).append("\n");
      bul.append("displayLanguage = " + pLocale.getDisplayLanguage()).append("\n");
      bul.append("displayName = " + pLocale.getDisplayName()).append("\n");
      bul.append("displayVariant = " + pLocale.getDisplayVariant()).append("\n");
      bul.append("ISO3Country = " + pLocale.getISO3Country()).append("\n");
      bul.append("ISO3Language = " + pLocale.getISO3Language()).append("\n");
      bul.append("language = " + pLocale.getLanguage()).append("\n");
      bul.append("variant = " + pLocale.getVariant()).append("\n");
      bul.append("toString = " + pLocale.toString()).append("\n");

      if (!pOmitDisplayOfAll) {
        bul.append("Interesting constants = ").append("\n");
        bul.append("US = " + Locale.US).append("\n");
        bul.append("UK = " + Locale.UK).append("\n");
        bul.append("CAN = " + Locale.CANADA).append("\n");
        bul.append("FR CAN = " + Locale.CANADA_FRENCH).append("\n");
        bul.append("\n");

        bul.append("\n").append("All Countries:").append("\n");

        String[] allLocales = Locale.getISOCountries();

        for (int m = 0; (allLocales != null) && (m < allLocales.length); m++) {
          bul.append(allLocales[m]).append("\n");
        } // for all avaliable locales
      }

      logDebug(bul.toString());
    }
  }
}
