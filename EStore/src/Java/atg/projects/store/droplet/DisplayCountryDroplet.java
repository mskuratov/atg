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
import java.util.Locale;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.servlet.RequestLocale;

/**
 * This droplet takes a locale language key and country code and 
 * returns the corresponding country display name in the user's locale.
 * 
 * This is useful to retrieve a country display name for the region 
 * links at the top of a page.
 * 
 * Input Paramaters:
 *   language - Language code for a particular site, e.g - en, de, es...
 *           
 *   countryCode - The country code e.g - US, DE, ES...
 *           
 * Open Parameters:
 *   output - Serviced when there are no errors
 *           
 * Output Parameters:  
 *   displayCountry - A country display name based on the language and country code
 *                    input parameters.
 *       
 * @author David Stewart
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/DisplayCountryDroplet.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 *
 */
public class DisplayCountryDroplet extends DynamoServlet {

  //------------------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/DisplayCountryDroplet.java#2 $$Change: 768606 $";


  //--------------------------------------------------------------
  //  CONSTANTS
  //--------------------------------------------------------------

  /** Country code parameter name  */
  public static final ParameterName COUNTRY_CODE = ParameterName.getParameterName("countryCode");

  /** Language parameter name. */
  public static final ParameterName LANGUAGE = ParameterName.getParameterName("language");

  /** Display country name output parameter name. */
  public static final String DISPLAY_COUNTRY_NAME = "displayCountryName";
  
  /** Output parameter name. */
  public static final String OUTPUT = "output";

  
  //------------------------------------------------
  //  PROPERTIES
  //------------------------------------------------
  
  /** Request locale */
  private RequestLocale mRequestLocale = null;

  //------------------------------------------------
  /**
   * @param pRequestLocale - request locale.
   */
  public void setRequestLocale(RequestLocale pRequestLocale) {
    mRequestLocale = pRequestLocale;
  }

  //------------------------------------------------
  /**
   * @return the request locale.
   **/
  public RequestLocale getRequestLocale() {
    return mRequestLocale;
  }
  
  
  //--------------------------------------------------------------
  // METHODS
  //--------------------------------------------------------------

  //--------------------------------------------------------------
  /**
   * Renders the "displayCountryName" output parameter with a country name
   * determined by the language and countryCode input parameters.
   *
   * @param pRequest DynamoHttpSevletRequest
   * @param pResponse DynamoHttpServletResponse
   * @throws ServletException if an error occurs
   * @throws IOException if an error occurs
   */
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    // Get input parameters.
    String language = (String) pRequest.getObjectParameter(LANGUAGE);
    String countryCode = (String)pRequest.getLocalParameter(COUNTRY_CODE);
    // The name of the country that will be returned to the client.
    String displayCountry = "";
    
    // Get the current user's locale.
    RequestLocale requestLocale = getRequestLocale();
    Locale currentLocale = requestLocale.discernRequestLocale(pRequest, requestLocale);

    // Get the country name based on the language and country code input parameters.
    if ((language != null) && (countryCode != null)) {
      Locale locale = new Locale(language, countryCode);
      displayCountry = locale.getDisplayCountry(currentLocale);
    }

    pRequest.setParameter(DISPLAY_COUNTRY_NAME, displayCountry);
    pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
  }
  
}
