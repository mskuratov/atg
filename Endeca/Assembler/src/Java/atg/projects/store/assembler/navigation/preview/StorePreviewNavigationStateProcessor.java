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

package atg.projects.store.assembler.navigation.preview;

import java.util.ArrayList;
import java.util.List;

import com.endeca.infront.navigation.NavigationState;

import atg.commerce.endeca.cache.DimensionValueCacheObject;
import atg.endeca.assembler.AssemblerTools;
import atg.projects.store.assembler.navigation.StoreNavigationStateProcessor;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

/**
 * Preview extension of the StoreNavigationStateProcessor.
 *
 * @author ckearney
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/navigation/preview/StorePreviewNavigationStateProcessor.java#5 $$Change: 791340 $
 * @updated $DateTime: 2013/02/19 09:03:40 $$Author: abakinou $
 */
public class StorePreviewNavigationStateProcessor extends StoreNavigationStateProcessor {

  //----------------------------------------
  /** Class version string. */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/navigation/preview/StorePreviewNavigationStateProcessor.java#5 $$Change: 791340 $";
  
  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------
  
  //-----------------------------------------------------
  // property: categoryIdParameterName
  //-----------------------------------------------------
  private String mCategoryIdParameterName = "categoryId";

  /**
   * @return the categoryIdParameterName which is an explicit category 
   *         parameter passed in on the request. Defaults to categoryId.
   */
  public String getCategoryIdParameterName() {
    return mCategoryIdParameterName;
  }

  /**
   * @param pCategoryIdParameterName - The categoryIdParameterName to set.
   */
  public void setCategoryIdParameterName(String pCategoryIdParameterName) {
    mCategoryIdParameterName = pCategoryIdParameterName;
  }

  //-----------------------------------------------------
  // property: productIdParameterName
  //-----------------------------------------------------
  private String mProductIdParameterName = "productId";
  
  /**
   * @return the productIdParameterName which is an explicit product 
   *         parameter passed in on the request. Defaults to productId.
   */
  public String getProductIdParameterName() {
    return mProductIdParameterName;
  }

  /**
   * @param pProductIdParameterName - The productIdParameterName to set.
   */
  public void setProductIdParameterName(String pProductIdParameterName) {
    mProductIdParameterName = pProductIdParameterName;
  }

  //----------------------------------------------
  // property: unindexedDimensionValueId
  //----------------------------------------------
  private String mUnindexedDimensionValueId = "1";
  
  /**
   * @return the ID to use when previewing an unindexed category.
   */
  public String getUnindexedDimensionValueId() {
    return mUnindexedDimensionValueId;
  }

  /**
   * @param pUnindexedDimensionValueId - The ID to use when previewing an unindexed category.
   */
  public void setUnindexedDimensionValueId(String pUnindexedDimensionValueId) {
    mUnindexedDimensionValueId = pUnindexedDimensionValueId;
  }

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  //----------------------------------------------------------------------------
  /**
   * An override of the process method to look for the explicit category ID 
   * parameter in the request. If this exists we know its a preview environment
   * and we should try to render as much of the page as possible.
   * 
   * @inheritDoc
   */
  @Override
  public void process(NavigationState pNavigationState) {
    // Look for our explicit category ID parameter specified in preview URLs.
    // If it exists we will try to retrieve the dimension value from the cache.
    DynamoHttpServletRequest requestCur = ServletUtil.getCurrentRequest();
    
    if(requestCur != null) {
      
      String productId = requestCur.getParameter(getProductIdParameterName());
      
      // When navigating to a product detail page, the request can contain both the 
      // productId and it's parent categoryId parameters. We must clear the current 
      // navigation settings when a productId is present as the CatalogNavigation's 
      // current category shouldn't be set when not on a category page.
      if (productId != null) {
        getCatalogNavigation().clear();
        return;
      }

      String categoryId = requestCur.getParameter(getCategoryIdParameterName());
      
      if(categoryId != null && !categoryId.isEmpty()) {
        updateStateWithSpecifiedCategory(categoryId, pNavigationState);
      }
    }
  }
  
  //----------------------------------------------------------------------------
  /**
   * Looks for a category ID parameter in the current request (specified by
   * getCategoryIdParameterName). If this exists look in the cache.
   *
   * @param pCategoryId - A category ID.
   * @param pNavigationState - The NavigationState object.
   */
  protected void updateStateWithSpecifiedCategory(String pCategoryId, NavigationState pNavigationState) {
    
    AssemblerTools.getApplicationLogging().vlogDebug(
      "Explicit Category ID parameter {0} has been specified, updating the NavigationState with its corresponding dimval", 
      pCategoryId);
   
    String dimvalId = null;
    List<String> ancestorIds = new ArrayList<String>();

    // We have an explicit category ID parameter check if it's in the cache.
    DimensionValueCacheObject dimValFromCache = getDimensionValueCacheTools().get(pCategoryId, null);
    
    if (dimValFromCache == null) {
      AssemblerTools.getApplicationLogging().vlogDebug(
        "Dimval for {0} was not found in the cache. The unindexed category dimension will be added to the NavigationState.", 
        pCategoryId);
      
      dimvalId = getUnindexedDimensionValueId();
      
      // Some cartridges depend on the ancestors. The ancestors have not been 
      // generated, so we will retrieve the default ancestor tree and use this.
      try {
        RepositoryItem catCurr = getCatalogTools().findCategory(pCategoryId);
        
        if (catCurr != null) {
          ancestorIds = 
            getDimensionValueCacheTools().getAncestorIds(catCurr.getRepositoryId());
        }
      }
      catch(RepositoryException e){
        AssemblerTools.getApplicationLogging().vlogError(
          e, "An error occured retrieving item {0} from the repository", pCategoryId);
      }
    }
    else {
      dimvalId = dimValFromCache.getDimvalId();
      ancestorIds = dimValFromCache.getAncestorRepositoryIds();
    }

    AssemblerTools.getApplicationLogging().vlogDebug(
      "Dimension {0} corresponds to category {1}", dimValFromCache, pCategoryId);

    // Update NavigationState.
    List<String> navigationFilters = new ArrayList<String>();
    navigationFilters.add(dimvalId);
    pNavigationState.getFilterState().setNavigationFilters(navigationFilters);
        
    // Update CatalogNavigation (last category browsed).
    updateCatalogNavigation(pCategoryId, ancestorIds);
      
    // Add user state.
    addCatalogUserState(pNavigationState);
  }

}
