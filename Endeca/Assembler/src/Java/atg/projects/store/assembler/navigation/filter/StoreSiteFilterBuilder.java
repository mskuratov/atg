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
package atg.projects.store.assembler.navigation.filter;

import atg.endeca.assembler.navigation.filter.SiteFilterBuilder;
import atg.projects.store.assembler.SearchedSites;
import atg.servlet.ServletUtil;

/**
 * Extends the SiteFilterBuilder to retrieve the currently searched sites from
 * the SearchedSites component.
 *
 * @author ckearney
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/navigation/filter/StoreSiteFilterBuilder.java#4 $$Change: 791340 $
 * @updated $DateTime: 2013/02/19 09:03:40 $$Author: abakinou $
 */
public class StoreSiteFilterBuilder extends SiteFilterBuilder {
  
  //----------------------------------------
  /** Class version string. */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/navigation/filter/StoreSiteFilterBuilder.java#4 $$Change: 791340 $";
  
  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------
  
  //------------------------------------------
  // property: searchedSites
  //------------------------------------------
  private SearchedSites mSearchedSites = null;
  
  /**
   * @return A bean thats used to indicate the scope of the current search.
   */
  public SearchedSites getSearchedSites() {
    return mSearchedSites;
  }

  /**
   * @param pSearchedSites - The new SearchedSites bean.
   */
  public void setSearchedSites(SearchedSites pSearchedSites) {
    mSearchedSites = pSearchedSites;
  }
  
  //---------------------------------------------
  // property: searchResetParam
  //---------------------------------------------
  private String mSearchResetParam = "siteScope";

  /**
   * @return a parameter that controls when the search scope should be reset
   *         to the current site. If this parameter is NOT present the scope is reset.
   *         Defaults to "siteScope". Typically looks like "siteScope=ok".
   */
  public String getSearchResetParam() {
    return mSearchResetParam;
  }

  /**
   * @param pSearchResetParam - The new searchResetParam.
   */
  public void setSearchResetParam(String pSearchResetParam) {
    mSearchResetParam = pSearchResetParam;
  }
  
  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  //----------------------------------------------------------------------------
  /**
   * <p>
   *   Checks the request for the parameter indicating that the currently searched
   *   scope should be reset. If this parameter is not present then reset the 
   *   scope of the currently searched sites. When we perform an initial search,
   *   this is set to true. It will be tacked on to each subsequent modification
   *   to this search. When a new request is performed by navigating away from the
   *   search page, the scope is reset. If a new search is performed, the scope is 
   *   reset anyway.
   * </p>
   * <p>
   *   Set the site IDs to use when constructing the filter to those specifed
   *   in the searched sites component.
   * </p>
   * 
   * @inheritDoc
   */
  @Override
  public String buildRecordFilter() {
    // Reset the currently searched sites if necessary before 
    // we determine which sites to include in the site filter.
    if (ServletUtil.getCurrentRequest() != null && 
        ServletUtil.getCurrentRequest().getParameter(getSearchResetParam()) == null) {
      
      getSearchedSites().reset();
    }
    
    setSiteIds(getSearchedSites().getSiteIds());
    return super.buildRecordFilter();
  }

}
