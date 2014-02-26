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



package atg.projects.store.catalog;

import java.sql.Timestamp;
import java.util.*;

import atg.commerce.catalog.custom.CatalogSearchFormHandler;
import atg.commerce.catalog.custom.CustomCatalogTools;
import atg.core.net.URLParamUtils;
import atg.core.util.StringUtils;
import atg.droplet.DropletFormException;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.SortDirective;
import atg.repository.SortDirectives;
import atg.service.util.CurrentDate;

/**
 * This class extends the ATG CatalogSearchFormHandler 
 * so as to handle multi worded keywords.
 *
 * @author ATG
 * @version $Revision: #2 $
 */
public class StoreSearchFormHandler extends CatalogSearchFormHandler {

  /**
   * Class version string.
   */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/catalog/StoreSearchFormHandler.java#2 $$Change: 768606 $";

  /**
   * Dummy search text.
   */
  String mDummySearchText;

  /**
   * Should process dummy search text.
   */
  boolean mProcessDummySearchText;

  /**
   * Used to sort results. If null sort by id.
   */
  String mDefaultSortField;

  /**
   * If true - ascending sort. False by default.
   */
  boolean mDescending = false;


  /**
   * Root navigation category property name.
   */
  String mRootNavigationCategoryPropertyName = "rootNavigationCategory";

  /**
   * Count of found items
   */
  Integer mSearchResultsSize = 0;

  /**
   * Results for page - pagination setting
   */
  Integer mResultsPerPage = -1;

  /**
   * Current page - pagination setting
   */
  Integer mCurrentPage = -1;

  /**
   * Success URL dynamic parameters - map of parameter names to formhandler properties
   */
  Map<String, String> mSuccessURLDynamicParams = null;

  /**
   * Size of results.
   * @return size of results found by handler
   */
  public Integer getSearchResultsSize() {
    return mSearchResultsSize;
  }

  /**
   * Sets size of results
   * @param pSearchResultsSize size of results found by handler
   */
  public void setSearchResultsSize(Integer pSearchResultsSize) {
    mSearchResultsSize = pSearchResultsSize;
  }

  /**
   * Returns results per page - pagination setting
   * @return results per page
   */
  public Integer getResultsPerPage() {
    return mResultsPerPage;
  }

  /**
   * Sets results per page - pagination setting
   * @param pResultsPerPage results per page
   */
  public void setResultsPerPage(Integer pResultsPerPage) {
    if (pResultsPerPage == null){
      mResultsPerPage = -1;
    }else{
       mResultsPerPage = pResultsPerPage; 
    }
  }

  /**
   * Returns current page - pagination setting
   * @return  current page
   */
  public Integer getCurrentPage() {
    return mCurrentPage;
  }

  /**
   * Sets current page - pagination setting
   * @param pCurrentPage current page
   */
  public void setCurrentPage(Integer pCurrentPage) {
    if (pCurrentPage == null){
      mCurrentPage = -1;
    }else{
      mCurrentPage = pCurrentPage;
    }
  }

  /**
   * Sets the search text shown in the search input textbox upon loading of the
   * page (i.e., "Enter Keyword").
   *
   * @param pDummySearchText the search text shown in the search input textbox
   *                         upon loading of the page (i.e., "Enter Keyword")
   */
  public void setDummySearchText(String pDummySearchText) {
    mDummySearchText = pDummySearchText;
  }

  /**
   * @return the search text shown in the search input textbox
   *         upon loading of the page (i.e., "Enter Keyword").
   */
  public String getDummySearchText() {
    return mDummySearchText;
  }

  /**
   * Boolean indicating if a search request that contains only the dummy search
   * text should be processed (as opposed to giving an error).
   *
   * @param pProcessDummySearchText boolean indicating if a search request that contains
   *                                &        only the dummy search text should be processed (as opposed to giving an error)
   */
  public void setProcessDummySearchText(boolean pProcessDummySearchText) {
    mProcessDummySearchText = pProcessDummySearchText;
  }

  /**
   * @return boolean indicating if a search equest that contains only the dummy search
   *         text should be processed (as opposed to giving an error).
   */
  public boolean isProcessDummySearchText() {
    return mProcessDummySearchText;
  }

  /**
   * The name of the "rootNavigationCategory" property of the catalog
   * repository item.
   *
   * @param pRootNavigationCategoryPropertyName
   *         the name of the "rootNavigationCategory"
   *         property of the catalog repository item
   */
  public void setRootNavigationCategoryPropertyName(String pRootNavigationCategoryPropertyName) {
    mRootNavigationCategoryPropertyName = pRootNavigationCategoryPropertyName;
  }

  /**
   * @return String the name of the "rootNavigationCategory" property of the catalog
   *         repository item.
   */
  public String getRootNavigationCategoryPropertyName() {
    return mRootNavigationCategoryPropertyName;
  }

  /**
   * Returns field to be sorted by
   *
   * @return field to be sorted by
   */
  public String getDefaultSortField() {
    return mDefaultSortField;
  }

  /**
   * Sets field to be sorted by
   *
   * @param pDefaultSortField field to be sorted by
   */
  public void setDefaultSortField(String pDefaultSortField) {
    mDefaultSortField = pDefaultSortField;
  }

  /**
   * Returns type of sort: descending or ascending. Used in conjunction with mSortingField
   *
   * @return descending or ascending sort
   */
  public boolean isDescending() {
    return mDescending;
  }

  /**
   * Sets the successURLDynamicParams Map.
   * @param pSuccessURLDynamicParams the parameter map
   */
  public void setSuccessURLDynamicParams(Map<String, String> pSuccessURLDynamicParams) {
    mSuccessURLDynamicParams = pSuccessURLDynamicParams;
  }

  /**
   * Returns the Map of success URL parameters
   * @return the parameter Map
   */
  public Map<String,String> getSuccessURLDynamicParams() {
    return mSuccessURLDynamicParams;
  }

  /**
   * Sets type of sort: descending or ascending. Used in conjunction with mSortingField
   *
   * @param pDescending type of sort
   */
  public void setDescending(boolean pDescending) {
    mDescending = pDescending;
  }

  /**
   * currentDate
   */
  private CurrentDate mCurrentDate;
  /**
   * Sets the CurrentDate component.
   */
  public void setCurrentDate(CurrentDate pCurrentDate) { 
    mCurrentDate = pCurrentDate; 
  }
  /**
   * Gets the CurrentDate component.
   */
  public CurrentDate getCurrentDate() { 
    return mCurrentDate; 
  }
  
  //-----------------------------------
  // searchPerformedThisRequest
  private boolean mSearchPerformedThisRequest = false;
  
  /**
   * This property should be set when performing an initial search. Its useful if the search results
   * page also performs a search (for example to allow loading a search from a bookmark). The flag 
   * can be tested to avoid performing the same search twice.
   * 
   * @return A boolean indicating if a search has been performed in this request.
   */
  public boolean isSearchPerformedThisRequest(){
    return mSearchPerformedThisRequest;
  }
  
  /**
   * @param pSearchPerformedThisRequest Set a new searchPerformedThisRequest flag
   */
  public void setSearchPerformedThisRequest(boolean pSearchPerformedThisRequest){
    mSearchPerformedThisRequest = pSearchPerformedThisRequest;
  }
  
  /**
   * @return true if:
   *         <p/>
   *         - The property processDummySearchText is false AND;
   *         - The search input is not blank AND;
   *         - The search input is identical to the dummySearchText.
   */
  protected boolean areSearchValuesEmpty() {
    String searchInput = getSearchInput();

    // The search input may have an odd leading character to buffer
    // the dummy text in the textbox.  We don't want to consider
    // that character when comparing against the dummy text, so
    // we strip it out.
    for (int i = 0; i < searchInput.length(); i++) {
      if (Character.isLetter(searchInput.charAt(i))) {
        searchInput = searchInput.substring(i);

        break;
      }
    }

    if (!isProcessDummySearchText() && !StringUtils.isBlank(searchInput) &&
        searchInput.trim().equals(getDummySearchText().trim())) {
      if (isLoggingDebug()) {
        logDebug("Search input identical to dummy text: returning areSearchValuesEmpty=true");
      }

      return true;
    }

    return super.areSearchValuesEmpty();
  }

  /**
   * Add a clause to the search that requires all results to be contained in the
   * "rootNavigationCategory" of the current catalog or catalogs if we specify
   * list of catalog IDs. This is to prevent items like "gift wrap" 
   * from appearing as a search result.
   * <p>
   * Also, add a clause for start and end dates. Only items with start date
   * before then current and end date after the current will be 
   * added to the search result.   
   *
   * @param pRepository   The repository being search
   * @param pItemType     the type of item to search for. Typically corresponds to an
   *                      item descriptor name in the catalog repository
   * @param pQueryBuilder the builder that should be used to construct the query
   * @return The query generated
   * @throws RepositoryException  If an error occurred while forming the query
   * @throws DropletFormException If an error occurred while forming the query
   */
  protected Query generateSearchQuery(Repository pRepository, String pItemType, QueryBuilder pQueryBuilder)
      throws DropletFormException, RepositoryException {
    
    Query q = super.generateSearchQuery(pRepository, pItemType, pQueryBuilder);

    if (q == null) {
      return q;
    }

    /*
     * If catalog IDs are specified and site IDs are passed in, use these IDs to retrieve 
     * root navigation categories for these catalogs. Otherwise,
     * use the user's current catalog to determine the root navigation category. 
     */
    String[] catalogIds = getCatalogs();
    Query ancestorCategoryQuery = null;
    String[] siteIds = getSiteIds();
    
    if(catalogIds != null && catalogIds.length > 0 && siteIds != null && siteIds.length > 0) {
      ancestorCategoryQuery = generateAncestorCategoriesQuery(pQueryBuilder, catalogIds);
    } else {
      Object currentCatalog = getCurrentCatalog();
      ancestorCategoryQuery = generateAncestorCategoryQuery(pQueryBuilder, currentCatalog);
    }

    // generate start date and end date checks
    Query startDateCheck = generateStartDateCheckClause(pQueryBuilder);
    Query endDateCheck = generateEndDateCheckClause(pQueryBuilder);
    
    // combine new query clauses with the existing query 
    Query[] elements = {q, ancestorCategoryQuery, startDateCheck, endDateCheck};
    Query newQuery = pQueryBuilder.createAndQuery(elements);

    if (isLoggingDebug()) {
      logDebug("Store specific query: " + newQuery);
    }

    return newQuery;
  }
  
  /**
   * Generates start date clause of view
   * '[current date] >= startDate OR startDate IS "null"'
   * 
   * @param pQueryBuilder query builder
   * @return query clause
   * @throws RepositoryException indicates that a severe error occurred while performing a Repository task
   */
  protected Query generateStartDateCheckClause(QueryBuilder pQueryBuilder) throws RepositoryException {
    StoreCatalogProperties catProperties = 
      (StoreCatalogProperties) ((CustomCatalogTools)getCatalogTools()).getCatalogProperties();
    
    // Get the current system time.
    CurrentDate cd = getCurrentDate();
    Date currentDate = cd.getTimeAsDate();
    
    QueryExpression dateToday = 
      pQueryBuilder.createConstantQueryExpression(new Timestamp(currentDate.getTime()));
    
    String startDateProp = catProperties.getStartDatePropertyName();
    QueryExpression productStartDate = pQueryBuilder.createPropertyQueryExpression(startDateProp);
    Query todayGreaterThenStartDate = pQueryBuilder.createComparisonQuery(dateToday,
        productStartDate,
        QueryBuilder.GREATER_THAN_OR_EQUALS);

    Query startDateIsNull = pQueryBuilder.createIsNullQuery(productStartDate);
    Query[] startDateQueries = {todayGreaterThenStartDate, startDateIsNull};
    
    return pQueryBuilder.createOrQuery(startDateQueries);
  }
  
  /**
   * Generate end date clause of view 
   * '[current date] <= endDate OR endDate IS "null"' 
   * 
   * @param pQueryBuilder query builder
   * @return query clause
   * @throws RepositoryException indicates that a severe error occured while performing a Repository task
   */
  protected Query generateEndDateCheckClause(QueryBuilder pQueryBuilder) throws RepositoryException {
    StoreCatalogProperties catProperties = 
      (StoreCatalogProperties) ((CustomCatalogTools)getCatalogTools()).getCatalogProperties();
    
    // Get the current system time.
    CurrentDate cd = getCurrentDate();
    Date currentDate = cd.getTimeAsDate();
    
    QueryExpression dateToday = 
      pQueryBuilder.createConstantQueryExpression(new Timestamp(currentDate.getTime()));
    
    String endDate = catProperties.getEndDatePropertyName();
    QueryExpression productEndDate = pQueryBuilder.createPropertyQueryExpression(endDate);
    Query todayLessThenEndDate = pQueryBuilder.createComparisonQuery(dateToday,
        productEndDate,
        QueryBuilder.LESS_THAN_OR_EQUALS);

    Query endDateIsNull = pQueryBuilder.createIsNullQuery(productEndDate);
    Query[] endDateQueries = {todayLessThenEndDate, endDateIsNull};
    
    return pQueryBuilder.createOrQuery(endDateQueries);
  }
  
  /**
   * Generate clause for the ancestor categories for catalogs.
   * Retrieve catalogs repository items for the given <code>pCatalogIds</code>, 
   * then determines root navigation category for every catalog
   * and build 'include any' query for these categories. 
   * 
   * @param pQueryBuilder query builder 
   * @param pCatalogIds array of catalog IDs 
   * @return query clause 
   * @throws RepositoryException indicates that a severe error occured while performing a Repository task
   */
  protected Query generateAncestorCategoriesQuery(QueryBuilder pQueryBuilder, String[] pCatalogIds) throws RepositoryException {
    Query ancestorCategoriesQuery = null;
    
    // determines root navigation categories for every catalog in array 
    RepositoryItem[] catalogs = ((CustomCatalogTools)getCatalogTools()).getCatalogs(pCatalogIds);

    if(catalogs != null && catalogs.length > 0) {
      RepositoryItem[] rootNavigationCategories = new RepositoryItem[catalogs.length];  

      for(int i=0; i < catalogs.length; i++) {
        rootNavigationCategories[i] = 
          (RepositoryItem) ((RepositoryItem) catalogs[i]).getPropertyValue(getRootNavigationCategoryPropertyName());
      }

      // build 'includes any' query for categories 
      QueryExpression ancestorCategoriesPropertyExpression = 
        pQueryBuilder.createPropertyQueryExpression(getAncestorCategoriesPropertyName());
      QueryExpression ancestorCategories = pQueryBuilder.createConstantQueryExpression(rootNavigationCategories);
      ancestorCategoriesQuery = pQueryBuilder.createIncludesAnyQuery(ancestorCategoriesPropertyExpression, ancestorCategories);
    }

    return ancestorCategoriesQuery;
  }

  /**
   * Generates clause for the ancestor categories for <code>pCurrentCatalog</code>.
   * Retrieves root navigation category for the current catalog and
   * generate 'includes' query for this category.
   * 
   * @param pQueryBuilder query builder
   * @param pCurrentCatalog catalog repository item 
   * @return query clause 
   * @throws RepositoryException indicates that a severe error occured while performing a Repository task
   */
  protected Query generateAncestorCategoryQuery(QueryBuilder pQueryBuilder, Object pCurrentCatalog)
      throws RepositoryException {
    RepositoryItem rootNavigationCategory = 
      (RepositoryItem) ((RepositoryItem) pCurrentCatalog).getPropertyValue(getRootNavigationCategoryPropertyName());
    QueryExpression ancestorCategoriesPropertyExpression = 
      pQueryBuilder.createPropertyQueryExpression(getAncestorCategoriesPropertyName());
    QueryExpression ancestorCategory = pQueryBuilder.createConstantQueryExpression(rootNavigationCategory);

    return pQueryBuilder.createIncludesQuery(ancestorCategoriesPropertyExpression, ancestorCategory); 
  }

  /**
   * <p>This method overrides the default method so as to handle multi words as
   * one single keyword as well as separate individual keywords. For example,
   * a search input of "body butter" will be parsed into a keyword search of
   * "body", "butter", and "body butter".
   *
   * @return keyword array
   */
  public String[] getKeywords() {
    if (!StringUtils.isBlank(getSearchInput())) {
      String[] searchInput = StringUtils.splitStringAtCharacter(getSearchInput(), getKeywordInputSeparator());
      String[] keywords;

      int length = searchInput.length;

      if (length > 1) {
        keywords = new String[searchInput.length + 1];
        // add the entire searchInput as a keyword in addition to parsing
        // the string into individual words
        keywords[0] = getSearchInput().trim();

        if (isLoggingDebug()) {
          logDebug("keywords[0]: " + keywords[0]);
        }

        int i;
        int ii = 1;

        for (i = 0; i < searchInput.length; i++) {
          if (isLoggingDebug()) {
            logDebug("searchInput[" + i + "]: " + searchInput[i]);
          }

          if (!(keywords[0].equalsIgnoreCase(searchInput[i].trim()))) {
            keywords[ii] = searchInput[i];
          }

          ii++;
        }
      } else {
        keywords = new String[1];
        keywords[0] = getSearchInput().trim();
      }

      return keywords;
    } else {
      return null;
    }
  }

  /**
   * Returns a collection of results to be displayed on the current page. This
   * collection is taken from mSearchResults depending on what page is currently
   * being viewed and how many results per page are to be displayed. Used by 
   * Simple repository search. 
   * 
   * @return A collection of items which are to be displayed on the current
   * search results page
   */
  public Collection getSearchResults() {
    if (getCurrentPage() != -1 || getResultsPerPage() != -1) {
      //pagination
      int startPosition = (getCurrentPage() - 1) * getResultsPerPage();
      int endPosition = getCurrentPage() * getResultsPerPage();
      if (endPosition > getSearchResultsSize()) {
        endPosition = getSearchResultsSize();
      }

      if (startPosition >= 0 && startPosition < endPosition ) {
        return mSearchResults.subList(startPosition, endPosition);
      } else {
        return Collections.EMPTY_LIST;
      }
      
    }

    // return all results
    return mSearchResults;

  }

  /** 
   * Searches through all repositories in mRepositories and generates a result
   * set of items of type pItemType. The items are sorted by displayName in an 
   * ascending order and saved in mSearchResults. Used by Simple repository search. 
   * 
   * @param pItemType The item type to search for in the repository. e.g "product"
   * @return A Collection of items which match the searched term and the item type
   */
  protected Collection generateResultSet(String pItemType) {
    ArrayList results = new ArrayList();
    int endIndex = -1;
    if (getMaxRowCount() != -1) {
      int newEnd = getMaxRowCount() - mSearchResults.size();
      endIndex = (newEnd < 0) ? 0 : newEnd;
    }

    try {
      for (int i = 0; i < getRepositories().length; i++) {
        RepositoryView view = getRepositories()[i].getView(pItemType);

        if (view == null) {
          if (isLoggingDebug()){
            logDebug(" empty view for " + pItemType + " in " + getRepositories()[i]);  
          }         
          return null;
        }

        QueryBuilder qb = view.getQueryBuilder();
        Query q = generateSearchQuery(getRepositories()[i], pItemType, qb);

        if (getAllowRefine()) {
          HashMap prevQueries = (HashMap) mPreviousQuery.get(
              getRepositories()[i].getRepositoryName());
          Query previous = (Query) prevQueries.get(pItemType);

          if (previous != null) {
            if (isLoggingDebug()){
              logDebug("using previous query as filter to new query");
            }

            if (q != null) {
              Query[] oldNew = {q, previous};
              q = qb.createAndQuery(oldNew);
            } else {
              q = previous;
            }
          }

          // set previous to new
          prevQueries.put(pItemType, q);
        }

        if (isLoggingDebug()){
          logDebug("combined search query=" + q);
        }

        if (q != null) {
          RepositoryItem[] items;
          if (!StringUtils.isEmpty(getDefaultSortField())) {
            SortDirective sDirective = new SortDirective(getDefaultSortField(), isDescending() ? SortDirective.DIR_DESCENDING : SortDirective.DIR_ASCENDING);
            SortDirectives pSortDirectives = new SortDirectives();
            pSortDirectives.addDirective(sDirective);

            items = view.executeQuery(view.getQueryBuilder().addContextMembershipFilters(q, generateSiteFilter()), pSortDirectives);
          } else {
            items = view.executeQuery(view.getQueryBuilder().addContextMembershipFilters(q, generateSiteFilter()), 0, endIndex, null);
          }
          if (items != null) {
            if (endIndex != -1){
              endIndex -= items.length;
            }
            results.addAll(Arrays.asList(items));
          }
        }
      }

      setSearchResultsSize(results.size());

      return results;
    }
    catch (RepositoryException exc) {
      if (isLoggingError()){
        logError("generateResultSet failed with exception: ", exc);  
      }     
    }
    catch (DropletFormException dfe) {
      addFormException(dfe);
    }
    return null;
  }

  /**
   * Returns the successURL this formhandler should redirect to.  This overrides the superclass to add functionality
   * to add parameters to the successURL based on the contents of the successURLDynamicParams property.
   *
   * @return the success URL.
   */
  @Override
  public String getSuccessURL() {
    List<Exception> errors = new ArrayList<Exception>();
    String url = URLParamUtils.addDynamicUrlParams(this, super.getSuccessURL(),
       getSuccessURLDynamicParams(), errors);

    if (errors.size() == 0){
      return url;
    }      
    else {
      if (isLoggingError())
      {
        for (Exception exc : errors) {        
          logError("Exception encountered creating the result URL ", exc);
        }        
      }    
      return super.getSuccessURL();
    }
 }


}
