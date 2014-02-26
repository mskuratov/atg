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

package atg.knowledgebase.adc;

import java.util.ResourceBundle;

import atg.adc.ADCRequestData;
import atg.adc.pipeline.ADCPipelineArgs;
import atg.adc.pipeline.PatternMappingProcessor;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.PagePattern;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.nucleus.ServiceException;
import atg.service.cache.Cache;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

/**
 * This processor performs a look up for the page urls configured 
 * and persists the page details into <code>ADCRequestData</code>
 * object for the use in the remaining pipeline processors. 
 * The processor extends <code>atg.adc.pipeline.PatternMappingProcessor</code> 
 * to perform a lookup for the URL parameter value for the configured 
 * set of pages. If the URL of the requested page matches an entry in the  
 * configured <code>widgetDisplayPages</code> property, it is set to the ADC request
 * data.
 * 
 * @author Gayathri Sasidharan
 *
 */
public class KnowledgeBaseFindPageUrlProcessor extends PatternMappingProcessor{
  
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/KnowledgeBase/src/atg/knowledgebase/adc/KnowledgeBaseFindPageUrlProcessor.java#2 $$Change: 791340 $";
  
  /** Performance monitor constants. */
  private static final String PERFORM_MONITOR_NAME = "KnowledgeBaseFindPageUrlProcessor";
  public static final String PERFORM_OPERATION_NAME = "updateADCData";
  
  /**
   * Resource bundle name.
   */
  private static final String RESOURCE_BUNDLE_NAME = "atg.knowledgebase.adc.Resources";
  
  /**
   * Performance Monitor Error.
   */
  protected static final String ERROR_PERF_MONITOR = "knowledge_base.error.perfMonitor";
  
  //----------------------------------------------
  // property: WidgetDisplayPages
  //----------------------------------------------
  private String[] mWidgetDisplayPages;
  
  /**
   * Get search results pages
   * 
   * @return the array of search results pages
   */
  public String[] getWidgetDisplayPages() { 
    return mWidgetDisplayPages;
  }
  
  /**
   * Set widget display pages
   * 
   * @param pWidgetDisplayPages the array of search results pages
   */
  public void setWidgetDisplayPages(String[] pWidgetDisplayPages) {
    mWidgetDisplayPages = pWidgetDisplayPages;
  }

  //----------------------------------------------
  // property: WidgetDisplayPagesCache
  //----------------------------------------------
  private Cache mWidgetDisplayPagesCache;    

  /**
   * Gets the widget display pages cache
   * 
   * @return the widget display pages cache
   */
  public Cache getWidgetDisplayPagesCache() {
    return mWidgetDisplayPagesCache;
  }

  /**
   * Sets the widget display pages cache
   * 
   * @param pWidgetDisplayPagesCache the widget display pages cache
   */
  public void setWidgetDisplayPagesCache(Cache pWidgetDisplayPagesCache) {
    mWidgetDisplayPagesCache = pWidgetDisplayPagesCache;
  }
  
  //----------------------------------------------
  // property: WidgetDisplayPagePatterns
  //----------------------------------------------
  private PagePattern[] mWidgetDisplayPagePatterns;
      
  /**
   * Gets widget display page patterns
   * 
   * @return the widget display page patterns
   */
  public PagePattern[] getWidgetDisplayPagePatterns() { 
    return mWidgetDisplayPagePatterns;
  }

  /**
   * Sets widget display page patterns
   * 
   * @param pWidgetDisplayPagePatterns the widget display page patterns
   */
  public void setWidgetDisplayPagePatterns(
      PagePattern[] pWidgetDisplayPagePatterns) {
    mWidgetDisplayPagePatterns = pWidgetDisplayPagePatterns;
  }

  /**
   * Initializes <code>searchResultsPagePatterns</code> property with 
   * compiled page patterns corresponding to <code>widgetDisplayPages</code>
   * list.
   * 
   * @exception ServiceException if the Service had a problem starting up
   */
  public void doStartService() throws ServiceException {
    super.doStartService();
    
    String[] widgetDisplayPages = getWidgetDisplayPages();
    
    if (widgetDisplayPages != null) {
      PagePattern[] widgetDisplayResultsPatterns = new PagePattern[widgetDisplayPages.length];
      
      for (int i = 0; i < widgetDisplayPages.length; i++) {
        widgetDisplayResultsPatterns[i] = PagePattern.compile(widgetDisplayPages[i]);
      }

      if (widgetDisplayResultsPatterns.length != 0) {
        setWidgetDisplayPagePatterns(widgetDisplayResultsPatterns);
      }
    }
  }

  /**
   * Checks if the current URL matches one of the <code>widgetDisplayPages</code>
   * configured and if a matching url is found then it is set to the ADCRequestData.
   * 
   * @param pArgs The pipeline arguments
   * 
   * @return NO_CHANGE pipeline result code
   */
  @Override
  public int updateADCData(ADCPipelineArgs pArgs) {
  
    ResourceBundle resourceBundle = LayeredResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, ServletUtil.getUserLocale());
    ADCRequestData requestData =  pArgs.getADCRequestData();
        
    // If page name is already set in request data then do nothing
    if (requestData.getPageName()!= null)
      return NO_CHANGE;
    
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, PERFORM_OPERATION_NAME);
    boolean perfCancelled = false;
    
    try {
      if (isLoggingDebug())
        logDebug("Looking for the page name url in the request parameters");
      
      String pageUrl = findPageUrl(pArgs);
            
      if (StringUtils.isEmpty(pageUrl))
        return STOP_CHAIN_EXECUTION;
      
      //Get the page url
      String pageName = pageUrl.trim().substring(pageUrl.lastIndexOf("/"), pageUrl.length());
            
      // If the page url is found,  set it to ADCRequestData
      if (!StringUtils.isEmpty(pageName))
           requestData.setPageName(pageName);
      
      if (isLoggingDebug())
        logDebug("Found Page Url: " + pageUrl); 
      
    } finally {
      
      try {
        if (!perfCancelled) {
          PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, PERFORM_OPERATION_NAME);
          perfCancelled = true;
        }
      } catch (PerfStackMismatchException e) {
        if (isLoggingWarning()) {
          logWarning(ResourceUtils.getMsgResource(ERROR_PERF_MONITOR, RESOURCE_BUNDLE_NAME, resourceBundle)+e);
        }
      }
      
    }
    return NO_CHANGE;
  }

  /**
   * Finds the Page URL in the query arguments in the request. If the URL
   * for the current request matches an entry in <code>widgetDisplayPages</code>,
   * each argument name in <code>pArgs</code> is checked for
   * a matching page url.
   * 
   * @param pArgs The pipeline arguments
   * 
   * @return The url of the page , or null if nothing is found
   */
  protected String findPageUrl(ADCPipelineArgs pArgs) {
    
    ResourceBundle resourceBundle = LayeredResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, ServletUtil.getUserLocale());
    DynamoHttpServletRequest request = pArgs.getADCRequestData().getRequest();
    
    String url = request.getServletPath();
    PagePattern[] widgetDisplayPagePatterns = getWidgetDisplayPagePatterns();
        
    if (widgetDisplayPagePatterns != null) {
      
      try {
        // If servlet path doesn't match any page pattern return null
        if (!pathMatchesPattern(url, widgetDisplayPagePatterns,
            getWidgetDisplayPagesCache())) {
          return null;
        } 
      } catch (Exception e) {
          if (isLoggingError())
            logError(ResourceUtils.getMsgResource(ERROR_PERF_MONITOR, RESOURCE_BUNDLE_NAME, resourceBundle)+e);
            return null;
        }
    }

    return url;
  }


  /**
   * This method determines if the given path matches any of the
   * page patterns defined.
   * 
   * @param pPath The url to search for
   * @param pPatterns The url patterns
   * @param pCache Cached results of previous paths
   * 
   * @return boolean indicating if the path fits one of the patterns
   */
  public boolean pathMatchesPattern(String pPath, PagePattern[] pPatterns,
      Cache pCache) throws Exception {
    
    if (pPatterns == null)
      return false;

    // First check whether cache already contains specified path
    if (pCache != null && pCache.contains(pPath))
      return ((Boolean) pCache.get(pPath)).booleanValue();

    boolean found = false;

    // Check whether specified path matches one of the page patterns
    int i = 0;
    while (!found && i < pPatterns.length) {
      found = pPatterns[i].matches(pPath);
      if (!found)
        i++;
    }

    // Put the boolean result into cache
    if (pCache != null)
      pCache.put(pPath, new Boolean(found));
    
    return found;

  }
}

