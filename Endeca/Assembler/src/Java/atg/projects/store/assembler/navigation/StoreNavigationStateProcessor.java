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

package atg.projects.store.assembler.navigation;

import java.util.Collection;
import java.util.List;

import atg.commerce.endeca.cache.DimensionValueCacheObject;
import atg.commerce.endeca.cache.DimensionValueCacheTools;
import atg.core.util.StringUtils;
import atg.endeca.assembler.AssemblerTools;
import atg.endeca.assembler.navigation.NavigationStateProcessor;
import atg.multisite.SiteContextManager;
import atg.projects.store.catalog.CatalogNavigationService;
import atg.projects.store.catalog.StoreCatalogProperties;
import atg.projects.store.catalog.StoreCatalogTools;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.endeca.infront.navigation.NavigationState;
import com.endeca.infront.navigation.UserState;
import com.endeca.infront.navigation.model.RangeFilter;

/**
 * The navigation state is processed to determine if it represents a
 * user catalog navigation and if so, a user segment is added to the user state
 * object. This user segment is used within Experience Manager to route the user
 * to the search results page or category page accordingly.
 *
 * @author ckearney
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/navigation/StoreNavigationStateProcessor.java#4 $$Change: 791340 $
 * @updated $DateTime: 2013/02/19 09:03:40 $$Author: abakinou $
 */
public class StoreNavigationStateProcessor implements NavigationStateProcessor {
  
  //----------------------------------------
  /** Class version string. */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/navigation/StoreNavigationStateProcessor.java#4 $$Change: 791340 $";
  
  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------
  
  //---------------------------------
  // property: userSegment
  //---------------------------------
  private String mUserSegment = null;

  /**
   * @param pUserSegment - The user segment to set on the user state and used within Experience Manager to 
   *                       control routing the user to the category page instead of the search results page.
   */
  public void setUserSegment(String pUserSegment) {
    mUserSegment = pUserSegment;
  }

  /**
   * @return the user segment to set on the user state and used within Experience Manager to 
   *         control routing the user to the category page instead of the search results page.
   */
  public String getUserSegment() {
    return(mUserSegment);
  }

  //----------------------------------
  // property: userState
  //----------------------------------
  private UserState mUserState = null;

  /**
   * @param pUserState - The userState object used to hold the user segment for use within Experience Manager to
   *                     control routing the user to the category page instead of the search results page.
   */
  public void setUserState(UserState pUserState) {
    mUserState = pUserState;
  }

  /**
   * @return the userState object used to hold the user segment for use within Experience Manager to
   *         control routing the user to the category page instead of the search results page.
   */
  public UserState getUserState() {
    return(mUserState);
  }

  //----------------------------------------------------------------
  // property: dimensionValueCacheTools
  //----------------------------------------------------------------
  private DimensionValueCacheTools mDimensionValueCacheTools = null;

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
 
  //----------------------------------------------------------------
  // property: catalogNavigationService
  //----------------------------------------------------------------
  private CatalogNavigationService mCatalogNavigationService = null;

  /**
   * @param pCatalogNavigationService - The component used to track users catalog navigation.
   */
  public void setCatalogNavigation(CatalogNavigationService pCatalogNavigationService) {
    mCatalogNavigationService = pCatalogNavigationService;
  }

  /**
   * @return the component used to track users catalog navigation.
   */
  public CatalogNavigationService getCatalogNavigation() {
    return(mCatalogNavigationService );
  }
  
  //-------------------------------------------------
  // property: ignoredRangeFilters
  //-------------------------------------------------
  protected List<String> mIgnoredRangeFilters = null;
  
  /**
   * @param pIgnoredRangeFilters - The range filters to ignore when determining whether to add the
   *                               CatalogNavigation user segment or not.
   */
  public void setIgnoredRangeFilters(List<String> pIgnoredRangeFilters) {
    mIgnoredRangeFilters = pIgnoredRangeFilters;
  }
  
  /**
   * @return the range filters to ignore when determining whether to add the
   *         CatalogNavigation user segment or not.
   */
  public List<String> getIgnoredRangeFilters() {
    return mIgnoredRangeFilters;
  }
  
  //------------------------------------------
  // property: catalogTools
  //------------------------------------------
  protected StoreCatalogTools mCatalogTools = null;

  /**
   * @param pCatalogTools - The CatalogTools object to use when looking up products, categories and skus.
   */
  public void setCatalogTools(StoreCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  /**
   * @return the CatalogTools object to use when looking up products, categories and skus.
   */
  public StoreCatalogTools getCatalogTools() {
    return mCatalogTools;
  }
  
  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  //----------------------------------------------------------------------------
  /**
   * Process the navigation state to determine if it represents a user catalog
   * navigation and update the details on the catalog navigation tracking
   * component and set the user segment on the user state for routing the user
   * to the category page.
   * 
   * @inheritDoc
   */
  @Override
  public void process(NavigationState pNavigationState) {
    
    DimensionValueCacheObject cacheObject = null;

    String navigationFilterId = "";
    List<String> navigationFilters = pNavigationState.getFilterState().getNavigationFilters();

    // Obtain the navigation dimension filter (dimension value id).
    // Catalog navigation will always contain a single dimension filter.

    if (navigationFilters.size() == 1) {
      navigationFilterId = navigationFilters.get(0);
    }

    // Access the catalog cache to find if we have a matching category entry for
    // this navigation filter (dimension value id). If we find a match and we 
    // have no search refinements, then set the user segment on the user state
    // for routing the user to the category page.
    
    if (!StringUtils.isEmpty(navigationFilterId)) {
      
      cacheObject = 
        getDimensionValueCacheTools().getCachedObjectForDimval(navigationFilterId);
      
      // If category is from another site, then don't add user segment to stay
      // on search result page. 
      if (cacheObject != null && isCategoryOnCurentSite(cacheObject.getRepositoryId())) {
        // Add the "fake category" to the UserState object. 
        // This will force Endeca to return our category page.
        addCatalogUserState(pNavigationState);
        
        // Update the last browsed category.
        updateCatalogNavigation(cacheObject.getRepositoryId(), 
                                cacheObject.getAncestorRepositoryIds());
        
      }
    }
    else {
      // Clear the current CatalogNavigation.
      updateCatalogNavigation(null, null);
    }
  }

  /**
   * Retrieves category item using its repository id and checks
   * whether the category belongs to current site. 
   * 
   * @param pRepositoryId the category repository id
   * @return true is category if on current site. False otherwise.
   */
  protected boolean isCategoryOnCurentSite(String pRepositoryId) {
    boolean siteFound = false;
    // Get current category item
    RepositoryItem category = getCategory(pRepositoryId);
    StoreCatalogProperties catalogProperties = (StoreCatalogProperties) getCatalogTools().getCatalogProperties();
    
    if (category != null) {    
      //Get the current site
      String currentSiteId = SiteContextManager.getCurrentSiteId();
      if (!StringUtils.isEmpty(currentSiteId)) {
      
        Collection<String> sites = null;
        String sitesPropertyName = catalogProperties.getSitesPropertyName();
        try {
          if (!StringUtils.isEmpty(sitesPropertyName) && category.getItemDescriptor().hasProperty(sitesPropertyName)) {
            sites = (Collection<String>) category.getPropertyValue(sitesPropertyName);
           
            for (String siteId: sites) {
              if (currentSiteId.equals(siteId)) {
                siteFound = true;
                break;
              }
            }
          
          }
        } catch (RepositoryException e) {
          AssemblerTools.getApplicationLogging().vlogError(e, 
              "StoreNavigationStateProcessor.isCategoryOnCurentSite: An error occurred when retrieving {0} property category for Id: {1}",
              sitesPropertyName, pRepositoryId);
        }      
      }
      // If current site id is empty, then we not care on what site we are
      else {
        siteFound = true;
      }      
    }
     
    return siteFound;
    
  }
  
  /**
   * Get category by its id
   * @param pCategoryId the category to retrive
   * @return category repository item
   */
  
  protected RepositoryItem getCategory(String pCategoryId) {
    
    // If no category ID is specified, return null.
    if (StringUtils.isEmpty(pCategoryId)){
      return null;
    }
    
    // Look up category repository item for the specified category ID.
    RepositoryItem categoryItem = null;
    
    try {
      categoryItem = getCatalogTools().findCategory(pCategoryId);
    } 
    catch (RepositoryException ex) {
      AssemblerTools.getApplicationLogging().vlogError(
        ex, 
        "CategoryRelatedProductsHandler.getCategoryRelatedProducts: An error occurred when retrieving category for Id: {0}",
        pCategoryId);
    }
  
    if (categoryItem == null) {
      AssemblerTools.getApplicationLogging().vlogError(
        "CategoryRelatedProductsHandler.getCategoryRelatedProducts: No category found for the id: {0}", pCategoryId);
      
      return null;
    }
  
    return categoryItem;
  }

  //----------------------------------------------------------------------------
  /**
   * Update the catalog navigation component.
   * 
   * @param pCategoryId The categoryId to set as the last browsed category.
   * @param pAncestors The ancestor category ids.
   */
  protected void updateCatalogNavigation(String pCategoryId, List<String> pAncestors) {
    if(getCatalogNavigation() != null) {
      if(pCategoryId != null) {
        getCatalogNavigation().navigate(pCategoryId, pAncestors);
      }
      else {
        getCatalogNavigation().clear();
      }
    }
  }
  
  //----------------------------------------------------------------------------
  /**
   * <p>
   *   Add the CategoryNavigation user segment. We only do this when there are
   *   no search filters or range filters. This is because we only want to land
   *   on the category page when only a category is selected. 
   * </p>
   * <p>  
   *   The 'ignoredRangeFilters' property used in this method defines a list of 
   *   range filters to ignore  when determining whether to add the CatalogNavigation 
   *   segment or not.
   * </p>
   * 
   * @param pNavigationState - The NavigationState object that holds the current search/range filters.
   */
  protected void addCatalogUserState(NavigationState pNavigationState) {
    
    // Keep a count of how many ignored range filters are defined in the NavigationState. If
    // the value of this counter is equal to the size of the NavigationState's range filter list,
    // the CatalogNavigation user segment can be added.
    int numIgnoredRangeFiltersInNavState = 0;
   
    if (getIgnoredRangeFilters() != null) {
      outer:
      for (RangeFilter navStateRangeFilter : pNavigationState.getFilterState().getRangeFilters()) {
        for (String ignoredRangeFilter : getIgnoredRangeFilters()) {
          // When an ignored range filter is found in the navigation state range filters list, 
          // increment the 'numIgnoredRangeFiltersInNavState'. 
          if (navStateRangeFilter.getPropertyName().equals(ignoredRangeFilter)) {
            numIgnoredRangeFiltersInNavState++;
            continue outer;
          }
        }
      }
    }
    
    if (pNavigationState.getFilterState().getSearchFilters().isEmpty() && 
        (pNavigationState.getFilterState().getRangeFilters().isEmpty() || 
         pNavigationState.getFilterState().getRangeFilters().size() == numIgnoredRangeFiltersInNavState)) {
      
      getUserState().addUserSegments(getUserSegment());
    }
  }

}
