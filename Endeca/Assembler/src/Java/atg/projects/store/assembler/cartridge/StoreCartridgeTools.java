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

package atg.projects.store.assembler.cartridge;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import atg.core.util.StringUtils;
import atg.endeca.assembler.AssemblerTools;
import atg.endeca.assembler.navigation.filter.RangeFilterBuilder;
import atg.projects.store.catalog.CatalogNavigationService;

import com.endeca.infront.navigation.UserState;
import com.endeca.infront.navigation.model.RangeFilter;

/**
 * This is the helper class containing reusable methods for obtaining/modifying a user's navigation information.
 *
 * @author Natallia Paulouskaya
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/StoreCartridgeTools.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreCartridgeTools {
  
  //-------------------------------------------
  /** Class version string. */
  protected static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/StoreCartridgeTools.java#3 $$Change: 788278 $";

  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------
  
  //---------------------------------------------------
  //  property: categoryNavigationUserSegment
  //---------------------------------------------------
  private String mCategoryNavigationUserSegment = null;

  /**
   * @param pCategoryNavigationUserSegment - The user segment name used to determine whether the user 
   *                                         is on the category page.
   */
  public void setCategoryNavigationUserSegment(String pCategoryNavigationUserSegment) {
    mCategoryNavigationUserSegment = pCategoryNavigationUserSegment;
  }

  /**
   * @return the user segment name used to determine whether the user is on the category page.
   */
  public String getCategoryNavigationUserSegment() {
    return(mCategoryNavigationUserSegment);
  }
  
  //----------------------------------
  //  property: userState
  //----------------------------------
  private UserState mUserState = null;

  /**
   * @param pUserState - The UserState object holding user segments.
   */
  public void setUserState(UserState pUserState) {
    mUserState = pUserState;
  }

  /**
   * @return The UserState object holding user segments.
   */
  public UserState getUserState() {
    return(mUserState);
  }
  
  //---------------------------------------------------------
  //  property: catalogNavigation
  //---------------------------------------------------------
  private CatalogNavigationService mCatalogNavigation = null;

  /**
   * @param pCatalogNavigation - The component used to track users catalog navigation.
   */
  public void setCatalogNavigation(CatalogNavigationService pCatalogNavigation) {
    mCatalogNavigation = pCatalogNavigation;
  }

  /**
   * @return the component used to track users catalog navigation.
   */
  public CatalogNavigationService getCatalogNavigation() {
    return(mCatalogNavigation);
  } 
  
  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  /**
   * <p>
   *   Returns the category ID that the user is currently viewing. 
   * </p>
   * <p>
   *   This method first checks whether the user is currently on the category page and 
   *   if so, picks up the last browsed category from the CatalogNavigation component.
   * </p>
   * 
   * @return The current category ID.
   */
  public String getCurrentCategoryId() {
    
    String categoryId = null;
    
    if (isUserOnCategoryPage()) {
      categoryId = getCatalogNavigation().getCurrentCategory();
    }
    
    return categoryId;    
  }
  
  //----------------------------------------------------------------------------
  /**
   * <p>
   *   Determines whether the user is currently on the category page.
   * </p>
   * <p>
   *   This is performed by checking whether the user state contains the category
   *   navigation user segment.
   * </p>
   * 
   * @return the boolean indicating whether the user is currently on the category page.
   */
  public boolean isUserOnCategoryPage() {
    if (StringUtils.isEmpty(getCategoryNavigationUserSegment())) {
      AssemblerTools.getApplicationLogging().vlogError(
        "No category navigation user segment is configured for StoreNavigationTools component");
      return false;
    }
    
    // Check whether the user state contains the category navigation user segment.
    Set<String> userSegments = getUserState().getUserSegments();
    
    if (userSegments != null && !userSegments.isEmpty() ) {
      return userSegments.contains(getCategoryNavigationUserSegment());
    }
    
    return false;
  }

  //----------------------------------------------------------------------------
  /**
   * Return a List of unique RangeFilters by the property they filter on.
   * 
   * @param pRangeFilters - The List of RangeFilters to be updated.
   * @param pRangeFilterBuilders - The RangeFilterBuilders that will generate RangeFilters to be added to pRangeFilters.
   * 
   * @return a new RangeFilter list consisting of pRangeFilters and the RangeFilters generated by pRangeFilterBuilders. 
   */
  public List<RangeFilter> updateRangeFilters(List<RangeFilter> pRangeFilters, 
                                              RangeFilterBuilder[] pRangeFilterBuilders) {
    
    List<RangeFilter> allRangeFilters = pRangeFilters;
    
    if(pRangeFilters == null) {
      allRangeFilters = new ArrayList<RangeFilter>();
    }
    
    if(pRangeFilterBuilders == null) {
      return pRangeFilters;
    }
    
    // Generate the RangeFilters.
    List<RangeFilter> generatedRangeFilters = new ArrayList<RangeFilter>();
    
    for (RangeFilterBuilder builder : pRangeFilterBuilders) {
      generatedRangeFilters.addAll(builder.buildRangeFilters());
    }
      
    // Ensure that no generated RangeFilters are added as duplicates.
    for (RangeFilter generatedRangeFilter : generatedRangeFilters) {
      boolean addToList = true;

      for (RangeFilter rf1 : allRangeFilters) {
        if (rf1.getPropertyName().equals(generatedRangeFilter.getPropertyName())) {
          addToList = false;
          break;
        }
      }
        
      if (addToList) {
        allRangeFilters.add(generatedRangeFilter);
      }
    }
    
    return allRangeFilters;
  }

}