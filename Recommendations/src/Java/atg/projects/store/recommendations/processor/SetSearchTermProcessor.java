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


package atg.projects.store.recommendations.processor;

import atg.adc.pipeline.ADCPipelineArgs;
import atg.adc.pipeline.PatternMappingProcessor;
import atg.core.util.PagePattern;
import atg.core.util.StringUtils;
import atg.nucleus.ServiceException;
import atg.projects.store.recommendations.adc.StoreADCRequestData;
import atg.service.cache.Cache;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.servlet.DynamoHttpServletRequest;

/**
 * This processor knows where to look up for the search term in the search 
 * results page request and stores corresponding search keyword into 
 * <code>ADCRequestData</code> object for the use in the remaining pipeline processors. 
 * The processor extends the <code>atg.adc.pipeline.PatternMappingProcessor</code> 
 * that gives the possibility to lookup URL parameter value for the configured 
 * set of pages. If the URL of the requested page matches an entry in the  
 * configured <code>searchResultsPages</code> property, the product ID is taken from a query
 * parameter named in the <code>categoryIdQueryArgs</code> property.
 * 
 * @author Yekaterina Kostenevich
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Recommendations/src/atg/projects/store/recommendations/processor/SetSearchTermProcessor.java#2 $Change: 630322 $
 * @updated $DateTime: 2012/12/26 06:47:02 $Author: ykostene $
 *
 */
public class SetSearchTermProcessor extends PatternMappingProcessor{
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Recommendations/src/atg/projects/store/recommendations/processor/SetSearchTermProcessor.java#2 $Change: 630322 $";

  /** Performance monitor constants. */
  private static final String PERFORM_MONITOR_NAME = "SetSearchTermProcessor";
  public static final String PERFORM_OPERATION_NAME = "updateADCData";
  
  /**
   * Search results pages
   */
  private String[] mSearchResultsPages;
  
  /**
   * Gets search results pages
   * 
   * @return the array of search results pages
   */
  public String[] getSearchResultsPages() {
    return mSearchResultsPages;
  }
  
  /**
   * Sets search results pages
   * 
   * @param pSearchResultsPages the array of search results pages
   */
  public void setSearchResultsPages(String[] pSearchResultsPages) {
    mSearchResultsPages = pSearchResultsPages;
  }


  /**
   * Search results pages cache
   */
  private Cache mSearchResultsPagesCache;    

  /**
   * Gets the search results pages cache
   * 
   * @return the search results pages cache
   */
  public Cache getSearchResultsPagesCache() {
    return mSearchResultsPagesCache;
  }

  /**
   * Sets the search results pages cache
   * 
   * @param pSearchResultsPagesCache the search results pages cache
   */
  public void setSearchResultsPagesCache(Cache pSearchResultsPagesCache) {
    mSearchResultsPagesCache = pSearchResultsPagesCache;
  }
  
  /**
   * Compiled search results page patterns
   */
  private PagePattern[] mSearchResultsPagePatterns;
      
  /**
   * Gets compiled search results page patterns
   * 
   * @return the compiled search results page patterns
   */
  public PagePattern[] getSearchResultsPagePatterns() {
    return mSearchResultsPagePatterns;
  }

  /**
   * Sets compiled search results page patterns
   * 
   * @param pSearchResultsPagePatterns the compiled search results page patterns
   */
  public void setSearchResultsPagePatterns(
      PagePattern[] pSearchResultsPagePatterns) {
    mSearchResultsPagePatterns = pSearchResultsPagePatterns;
  }

  /**
   * Search term query arguments
   */
  private String[] mSearchTermQueryArgs;  

  /**
   * Gets search term query arguments
   * 
   * @return the search term query arguments
   */
  public String[] getSearchTermQueryArgs() {
    return mSearchTermQueryArgs;
  }

  /**
   * Sets search term query arguments
   * 
   * @param pSearchTermQueryArgs the search term query arguments
   */
  public void setSearchTermQueryArgs(String[] pSearchTermQueryArgs) {
    mSearchTermQueryArgs = pSearchTermQueryArgs;
  }
  
  /**
   * Initializes <code>searchResultsPagePatterns</code> property with 
   * compiled page patterns corresponding to <code>searchResultsPages</code>
   * list.
   * 
   * @exception ServiceException if the Service had a problem starting up
   */
  public void doStartService() throws ServiceException {
    super.doStartService();
    
    String[] searchResultsPages = getSearchResultsPages();

    if (searchResultsPages != null) {
      PagePattern[] searchResultsPatterns = new PagePattern[searchResultsPages.length];
      for (int i = 0; i < searchResultsPages.length; i++) {
        searchResultsPatterns[i] = PagePattern.compile(searchResultsPages[i]);
      }

      if (searchResultsPatterns.length != 0) {
        setSearchResultsPagePatterns(searchResultsPatterns);
      }
    }
  }

  /**
   * Stores the search keyword in the ADCRequestData.
   * If the current URL matches one of the <code>searchResultsPages</code>
   * patterns then the current request is searched for to see if 
   * the page has a query parameter specified in <code>searchTermQueryArgs</code>
   * If query parameter is found the corresponding search keyword is stored in 
   * the ADCRequestData.
   * 
   * @param pArgs The pipeline arguments
   * @return NO_CHANGE pipeline result code
   */
  @Override
  public int updateADCData(ADCPipelineArgs pArgs) {
  
    StoreADCRequestData requestData = (StoreADCRequestData) pArgs.getADCRequestData();
    
    // If search keyword is already set in request data then do nothing
    if (requestData.getSearchKeyword() != null)
      return NO_CHANGE;
    
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, PERFORM_OPERATION_NAME);
    boolean perfCancelled = false;
    
    try {
      if (isLoggingDebug())
        logDebug("Looking for the search keyword in the query request parameters");
      
      String searchKeyword = findSearchKeyword(pArgs);
      
      if (isLoggingDebug())
        logDebug("Found serch keyword: " + searchKeyword);
      
      // If the search keyword found put it into ADCRequestData
      if (!StringUtils.isEmpty(searchKeyword)) {
        requestData.setSearchKeyword(searchKeyword);
      }
    } finally {
      try {
        if (!perfCancelled) {
          PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, PERFORM_OPERATION_NAME);
          perfCancelled = true;
        }
      } catch (PerfStackMismatchException e) {
        if (isLoggingWarning()) {
          logWarning(e);
        }
      }
    }
    return NO_CHANGE;
  }

  /**
   * Finds the search keyword in the query arguments in the request. If the URL
   * for the current request matches an entry in <code>searchResultsPages</code>,
   * each argument name in <code>searchTermQueryArgs</code> is checked until
   * something is found.
   * 
   * @param pArgs The pipeline arguments
   * @return The search keyword, or null if nothing found
   */
  protected String findSearchKeyword(ADCPipelineArgs pArgs) {
    DynamoHttpServletRequest request = pArgs.getADCRequestData().getRequest();
    
    PagePattern[] searchResultsPagePatterns = getSearchResultsPagePatterns();
    if (searchResultsPagePatterns != null) {
      
      // Get request servlet path
      String url = request.getServletPath();
      
      try {
        
        // If servlet path doesn't match any page pattern return null
        if (!pathMatchesPattern(url, searchResultsPagePatterns,
            getSearchResultsPagesCache()))
          return null;
        
      } catch (Exception e) {
        if (isLoggingError())
          logError(e);
        return null;
      }
    }
    
    // Servlet path matches one of the configured pages so look for the specified
    // query arguments
    return findQueryArgument(request, getSearchTermQueryArgs());
  }

  //TODO It would be good to move this method to PatternMappingProcessor
  /**
   * Looks for query arguments in request.
   * 
   * @param pRequest the request in which query arguments will be looked for
   * @param pQueryArgs the array of arguments to be looked in request
   * @return the first <code>pQueryArgs</code> that is found in the request or
   * null if none of them are found.
   */
  protected String findQueryArgument(DynamoHttpServletRequest pRequest,
      String[] pQueryArgs) {
    if (pQueryArgs == null)
      return null;

    String arg = null;
    for (int i = 0; i < pQueryArgs.length; i++) {
      arg = pRequest.getQueryParameter(pQueryArgs[i]);
      
      if (!StringUtils.isEmpty(arg))
        return arg;
    }

    return null;
  }

  //TODO It would be good to move this method to PatternMappingProcessor
  /**
   * This method will determine if the given path fits one of the
   * patterns.
   * 
   * @param pPath The url to search for
   * @param pPatterns The url patterns
   * @param pCache Cached results of previous paths
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
