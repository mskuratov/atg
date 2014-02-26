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


package atg.projects.store.recommendations;

import atg.commerce.pricing.priceLists.PriceListManager;
import atg.nucleus.GenericService;
import atg.service.cache.Cache;

/**
 * The configuration of the store recommendations auto-tagging
 * feature.
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Recommendations/src/atg/projects/store/recommendations/StoreRecommendationsConfiguration.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class StoreRecommendationsConfiguration extends GenericService{

  //--------------------------------------------------
  // class version string
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Recommendations/src/atg/projects/store/recommendations/StoreRecommendationsConfiguration.java#2 $$Change: 768606 $";
  
  //--------------------------------------------------
  // property: RetailerId
  // The Recommendations account retailer ID
  private String mRetailerId;

  /**
   * Returns recommendations account retailer ID.
   * 
   * @return the Recommendations Retailer ID
   */
  public String getRetailerId() {
    return mRetailerId;
  }

  /**
   * Sets recommendations account retailer ID.
   * 
   * @param RetailerId the Recommendations Retailer ID to set
   */
  public void setRetailerId(String pRetailerId) {
    mRetailerId = pRetailerId;
  }
  
  //--------------------------------------------------
  // property: RecommendationsScriptUrl
  // The URL to Recommendations JavaScript library
  private String mRecommendationsScriptUrl;

  /**
   * Returns the URL to recommendations JavaScript library.
   * 
   * @return the Recommendation JavaScript library URL
   */
  public String getRecommendationsScriptUrl() {
    return mRecommendationsScriptUrl;
  }

  /**
   * Sets the URL to recommendations JavaScript library.
   * 
   * @param pRecommendationsScriptUrl - the Recommendation JavaScript library URL
   */
  public void setRecommendationsScriptUrl(String pRecommendationsScriptUrl) {
    mRecommendationsScriptUrl = pRecommendationsScriptUrl;
  }
  
  //---------------------------------------------------------------------------
  // property: SearchTermQueryArgs
  // The array of query arguments that contain search term
  private String[] mSearchTermQueryArgs;

  /**
   * An array of query arguments that specify what search term is searched for.
   * 
   * @param pSearchTermQueryArgs An array of query arguments that specify what 
   * search term is searched for.
   **/
  public void setSearchTermQueryArgs(String[] pSearchTermQueryArgs) {
    mSearchTermQueryArgs = pSearchTermQueryArgs;
  }

  /**
   * Returns an array of query arguments that specify what search term is searched for.
   * 
   * @return An array of query arguments that specify what search term is searched for.
   **/
  public String[] getSearchTermQueryArgs() {
    return mSearchTermQueryArgs;
  }
  
  //--------------------------------------------------
  // property: SearchResultsPages
  // The array of search results pages that should be used to look for
  // search term query arguments.
  private String[] mSearchResultsPages;

  /**
   * Returns the list of search results pages on which 
   * the searchTermQueryArgs should be looked for.
   * Wildcard characters can be used.  If this property is null, the 
   * searchTermQueryArgs are looked for on all pages.
   * 
   * @return the String[] - the list of search results pages on which 
   * the searchTermQueryArgs should be looked for.  
   * 
   */
  public String[] getSearchResultsPages() {
    return mSearchResultsPages;
  }

  /**
   * Sets the list of search results pages on which 
   * the searchTermQueryArgs should be looked for.
   * Wildcard characters can be used.  If this property is null, the 
   * searchTermQueryArgs are looked for on all pages.
   * 
   * @param SearchResultsPages the String[] - the list of search results pages
   * on which the searchTermQueryArgs should be looked for.
   */
  public void setSearchResultsPages(String[] pSearchResultsPages) {
    mSearchResultsPages = pSearchResultsPages;
  }
  
  //--------------------------------------------------
  // property: SearchResultsPagesCache
  // The cache storing whether URLs match SearchResaultsPages patterns
  private Cache mSearchResultsPagesCache;

  /**
   * Returns the cache storing whether URLs match SearchResultsPages patterns
   * 
   * @return The cache storing whether URLs match SearchResultsPages patterns
   */
  public Cache getSearchResultsPagesCache() {
    return mSearchResultsPagesCache;
  }

  /**
   * Sets the cache storing whether URLs match SearchResultsPages patterns.
   * 
   * @param SearchResultsPagesCache The cache storing whether URLs match 
   * SearchResultsPages patterns
   */
  public void setSearchResultsPagesCache(Cache pSearchResultsPagesCache) {
    mSearchResultsPagesCache = pSearchResultsPagesCache;
  }
  
  //---------------------------------------------------------------------------
  // property:CartChangeJMSTypes
  // The list of JMS types that indicate that cart content has been changed
  private String[] mCartChangeJMSTypes;

  /**
   * The JMSTypes of events that indicate that cart content has been changed.
   * 
   * @param pCartChangeJMSTypes The JMSTypes of events that indicate that cart 
   * content has been changed.
   **/
  public void setCartChangeJMSTypes(String[] pCartChangeJMSTypes) {
    mCartChangeJMSTypes = pCartChangeJMSTypes;
  }

  /**
   * Returns JMSTypes of events that indicate that cart content has been changed.
   * 
   * @return The JMSTypes of events that indicate that cart content has been changed.
   **/
  public String[] getCartChangeJMSTypes() {
    return mCartChangeJMSTypes;
  }

  //---------------------------------------------------------------------------
  // property:CatalogNavHistoryPath
  // The catalog navigation history path property
  private String mCatalogNavHistoryPath;
  
  /**
   * Returns the path to catalog navigation history component.
   * 
   * @return the catalogNavHistoryPath returns the path to catalog navigation history component.
   */
  public String getCatalogNavHistoryPath() {
    return mCatalogNavHistoryPath;
  }

  /**
   * Sets the path to catalog navigation history component.
   * 
   * @param pCatalogNavHistoryPath the path to catalog navigation history component.
   */
  public void setCatalogNavHistoryPath(String pCatalogNavHistoryPath) {
    mCatalogNavHistoryPath = pCatalogNavHistoryPath;
  }
  
  //--------------------------------------------------------------------------
  // property:PriceListManager
  // Price list manager property
  private PriceListManager mPriceListManager;
  
  /**
   * Returns the price list manager.
   * 
   * @return the priceListManager the price list manager.
   */
  public PriceListManager getPriceListManager() {
    return mPriceListManager;
  }

  /**
   * Sets the price list manager.
   * 
   * @param pPriceListManager the price list manager.
   */
  public void setPriceListManager(PriceListManager pPriceListManager) {
    mPriceListManager = pPriceListManager;
  }
  
  //--------------------------------------------------------------------------
  // property:IncludeRootCategory
  // The boolean indicating whether to include root category into  the category
  // path
  private boolean mIncludeRootCategory;

  /**
   * Returns the boolean indicating whether to include root category
   * into the category path.
   * 
   * @return the boolean indicating whether root category should be included
   *        into the category path
   */
  public boolean isIncludeRootCategory() {
    return mIncludeRootCategory;
  }

  /**
   * Sets the boolean indicating whether to include root category
   * into the category path.
   * 
   * @param pIncludeRootCategory the boolean indicating whether root category 
   * should be included into the category path
   */
  public void setIncludeRootCategory(boolean pIncludeRootCategory) {
    mIncludeRootCategory = pIncludeRootCategory;
  }
 
}
