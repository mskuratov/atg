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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.endeca.infront.navigation.url.UrlNavigationStateBuilder;

import atg.commerce.endeca.cache.DimensionValueCacheTools;
import atg.core.net.URLUtils;
import atg.core.util.StringUtils;
import atg.core.i18n.LocaleUtils;
import atg.nucleus.naming.ParameterName;
import atg.projects.store.catalog.CatalogNavigationService;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.servlet.RequestLocale;

/**
 * This droplet takes a list of language keys, and returns a list of objects 
 * associating those keys with their proper display languages.
 * 
 * This is useful to list the available languages at the top of a page.
 * 
 * Input Parameters:
 *   languages - Available language codes for a particular site, e.g [en,es]
 *           
 *   countryCode - The country code e.g 'US'
 *           
 * Open Parameters:
 *   output - Serviced when there are no errors
 *           
 * Output Parameters:
 *   currentSelection - The index of the currently selected locale in 
 *   displayLanguages according to the request
 *           
 *   displayLanguages - A list of objects associating the language codes with
 *   display languages  
 *       
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/DisplayLanguagesDroplet.java#6 $$Change: 795788 $
 * @updated $DateTime: 2013/03/08 14:08:18 $$Author: ckearney $
 *
 */
public class DisplayLanguagesDroplet extends DynamoServlet {

  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/DisplayLanguagesDroplet.java#6 $$Change: 795788 $";

  //-----------------------------------
  // STATIC
  //-----------------------------------
  
  /**
   * Default page
   */
  private static final String DEFAULT_PAGE = "index.jsp";
  
  /**
   * Country code parameter name.
   */
  public static final ParameterName COUNTRY_CODE = ParameterName.getParameterName("countryCode");

  /**
   * Language parameter name.
   */
  public static final ParameterName LANGUAGES = ParameterName.getParameterName("languages");

  /**
   * Display languages parameter name.
   */
  public final static String DISPLAY_LANGUAGES = "displayLanguages";

  /**
   * Current selection parameter name.
   */
  public final static String CURRENT_SELECTION = "currentSelection";

  /**
   * Locale parameter name.
   */
  public final static String LANG_SELECTION = "locale";

  /**
   * Output parameter name.
   */
  public final static String OUTPUT = "output";
  
  //-----------------------------------
  // PROPERTIES
  //-----------------------------------

  //-----------------------------------
  // property: requestLocale
  private RequestLocale mRequestLocale;

  /**
   * @param pRequestLocale - request locale.
   */
  public void setRequestLocale(RequestLocale pRequestLocale) {
    mRequestLocale = pRequestLocale;
  }

  /**
   * @return the request locale.
   **/
  public RequestLocale getRequestLocale() {
    return mRequestLocale;
  }
  
  //-----------------------------------
  // property: catalogNavigationService
  private CatalogNavigationService mCatalogNavigation;  

  /**
   * @param pCatalogNavigationService - The component used to track users catalog navigation.
   */
  public void setCatalogNavigation(CatalogNavigationService pCatalogNavigationService) {
    mCatalogNavigation = pCatalogNavigationService;
  }

  /**
   * @return the component used to track users catalog navigation.
   */
  public CatalogNavigationService getCatalogNavigation() {
    return mCatalogNavigation;
  }
  
  //-----------------------------------
  // property: navigationStateBuilder
  private UrlNavigationStateBuilder mNavigationStateBuilder;
  
  /**
   * @return Component responsible for building the NavigationState
   */
  public UrlNavigationStateBuilder getNavigationStateBuilder() {
    return mNavigationStateBuilder;
  }

  /**
   * @param pNavigationStateBuilder Set a new NavigationStateBuilder component
   */
  public void setNavigationStateBuilder(
      UrlNavigationStateBuilder pNavigationStateBuilder) {
    mNavigationStateBuilder = pNavigationStateBuilder;
  }
  
  //-----------------------------------
  //property: dimensionValueCacheTools
  private DimensionValueCacheTools mDimensionValueCacheTools;

  /**
   * @param pDimensionValueCacheTools - The utility class for access to the ATG<->Endeca catalog cache.
   */
  public void setDimensionValueCacheTools(DimensionValueCacheTools pDimensionValueCacheTools) {
    mDimensionValueCacheTools = pDimensionValueCacheTools;
  }

  /**
   * @return the utility class for access to the ATG<->Endeca catalog cache.
   */
  public DimensionValueCacheTools getDimensionValueCacheTools() {
    return mDimensionValueCacheTools;
  }

  //-----------------------------------
  // METHODS
  //-----------------------------------

  /**
   * Renders "displayLanguages" output parameter which is a list of objects associating the
   * language codes with display languages.
   *
   * @param pRequest DynamoHttpSevletRequest
   * @param pResponse DynamoHttpServletResponse
   * @throws ServletException if an error occurs
   * @throws IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    List languages = (List) pRequest.getObjectParameter(LANGUAGES);
    String countryCode = (String)pRequest.getLocalParameter(COUNTRY_CODE);
    List displayLanguages = new ArrayList();

    RequestLocale requestLocale = getRequestLocale();
    Locale currentLocale = requestLocale.discernRequestLocale(pRequest, requestLocale);

    int currentSelection = 0;

    if (languages != null) {
      Iterator languageIter = languages.iterator();
      int index = 0;

      while (languageIter.hasNext()) {
        String language = (String) languageIter.next();
        Locale locale = new Locale(language, countryCode);
        String languageDisplayName = LocaleUtils.getCapitalizedDisplayLanguage(locale, locale);
        DisplayLanguage displayLanguage = new DisplayLanguage(languageDisplayName,
            createLinkURL(locale, pRequest), locale.toString());
        displayLanguages.add(displayLanguage);

        if ((currentLocale != null) && (currentLocale.getLanguage() != null) &&
            currentLocale.getLanguage().equalsIgnoreCase(locale.getLanguage())) {
          currentSelection = index;
        } else {
          index++;
        }
      }
    }

    pRequest.setParameter(CURRENT_SELECTION, Integer.valueOf(currentSelection));
    pRequest.setParameter(DISPLAY_LANGUAGES, displayLanguages);
    pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
  }

  /**
   * Generate a URL to the current page with the locale parameter set to pLocale. 
   * On clicking the generated link the page should reload in the language 
   * indicated by pLocale. 
   *
   * @param pLocale - A locale
   * @param pRequest - HTTP request
   *
   * @return URL to the current page with the locale parameter set to the locale
   * represented by pLocale.
   */
  public String createLinkURL(Locale pLocale, DynamoHttpServletRequest pRequest) {
    // Use original HTTP request because Dynamo HTTP request/ does not contain
    // proper query parameters after JumpServlet forwards request.
    HttpServletRequest originalHttpRequest = pRequest.getRequest();
    String requestURI = originalHttpRequest.getRequestURI();
    String contextPath = originalHttpRequest.getContextPath();
    boolean linkHome = false;
    int paramCount = 0;
    
    StringBuilder linkBuilder = new StringBuilder(requestURI);
    
    // Link to index.jsp when requesting to the context root
    if(requestURI.equals(contextPath) || requestURI.equals(contextPath + "/")) {
      linkHome = true;
      if(!requestURI.endsWith("/")) {
        linkBuilder.append("/");
      }
      linkBuilder.append(DEFAULT_PAGE);
    }

    // We dont need any params on the home page so skip this bit
    if(!linkHome){
      Enumeration queryParameterNames = originalHttpRequest.getParameterNames();
      if (queryParameterNames != null) {
        /*
         *  Dont keep checking if were refining by category if we have already
         *  processed the category param. This is used because we want to
         *  maintain the category refinement (even from the search page) on a 
         *  language switch.
         */
        boolean foundCategory = false;
        
        while (queryParameterNames.hasMoreElements()) {
          String queryParamName = (String) queryParameterNames.nextElement();

          /*
           * Dont add:
           * - The locale param
           * - params beginning with _ (to prevent multiple form submissions)
           * - Endeca search parameter if the user switches language after 
           *   refining they will be brought back to the default search.
           */
          if(isIgnoredParameter(queryParamName, pRequest)){
            continue;
          }
          
          String value = originalHttpRequest.getParameter(queryParamName);

          if(!foundCategory 
          && getNavigationStateBuilder().getNavigationFiltersParam().equals(queryParamName))
          {
            /*
             * Multiple refinements are currently applied. Find the category
             * refinement if there is one. The parameter may look similar to
             * N=1234+4567+7899
             */
            String[] refinements = value.split(" ");
            for(int i = 0; i < refinements.length; i++){
              if(getDimensionValueCacheTools().getCachedObjectForDimval(refinements[i]) != null){
                value = refinements[i];
                foundCategory = true;
                break;
              }
            }

            if(!foundCategory){
              continue;
            }
          }
          
          if (paramCount == 0) {
            linkBuilder.append("?");
          }
          else {
            linkBuilder.append("&");
          }
            
          linkBuilder.append(queryParamName);
          linkBuilder.append("=");
          linkBuilder.append(URLUtils.escapeUrlString(value));
          
          paramCount++;
        }
      }
    }

    if (paramCount == 0) {
      linkBuilder.append("?");
    } else {
      linkBuilder.append("&");
    }

    linkBuilder.append(LANG_SELECTION);
    linkBuilder.append("=");
    linkBuilder.append(pLocale.getLanguage());
    linkBuilder.append("_");
    linkBuilder.append(pLocale.getCountry());

    return linkBuilder.toString();
  }
  
  /**
   * Logic to determine if a parameter should be added to the language switching
   * URL.
   * 
   * @param pName Parameter name
   * @param pRequest The current HTTPServletRequest
   * @return
   */
  protected boolean isIgnoredParameter(String pName, HttpServletRequest pRequest){
    // Dont add empty params
    if(StringUtils.isEmpty(pRequest.getParameter(pName))){
      return true;
    }
    
    // Dont add locale param
    if(pName.equals(LANG_SELECTION)){
      return true;
    }
    // Dont add params added as a result of a form submission (prevents multiple
    // submissions).
    if(pName.startsWith("_")){
      return true;
    }
    
    // Dont add Endeca record filters as they contain locale filters which are 
    // already automatically applied
    String recFilter = getNavigationStateBuilder().getRecordFilterParam();
    if(pName.equals(recFilter)){
      return true;
    }
    
    // Dont add the search term as this more than likely wont make sense cross
    // language
    String searchTerm = getNavigationStateBuilder().getSearchFiltersParam();
    if(pName.equals(searchTerm)){
      return true;
    }

    return false;
  }
  
  //-----------------------------------
  // INNER CLASS DisplayLanguage
  //-----------------------------------

  /**
   * Class for display language. Stores the information we use to render the 
   * language switching link.
   */
  public static class DisplayLanguage {

    //-------------------------------------
    /** Class version string */

    public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/DisplayLanguagesDroplet.java#6 $$Change: 795788 $";

    /**
     * Display language.
     */
    private String mDisplayLanguage;

    /**
     * URL.
     */
    private String mLinkURL;
    
    /**
     * Locale
     */
    private String mLocale;

    /**
     * Constructor.
     * @param pDisplayLanguage - display language
     * @param pLinkURL - URL
     * @param pLocale - locale
     */
    public DisplayLanguage(String pDisplayLanguage, String pLinkURL, String pLocale) {
      mDisplayLanguage = pDisplayLanguage;
      mLinkURL = pLinkURL;
      mLocale = pLocale;
    }

    /**
     * @return The name of the language in the given language.
     **/
    public String getDisplayLanguage() {
      return mDisplayLanguage;
    }

    /**
     * @return The URL we want the link for this language to go to.
     **/
    public String getLinkURL() {
      return mLinkURL;
    }
    
    /**
     * @return The locale.
     **/
    public String getLocale() {
      return mLocale;
    }
  }
}
