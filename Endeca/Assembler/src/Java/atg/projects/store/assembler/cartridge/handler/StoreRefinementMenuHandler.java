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
package atg.projects.store.assembler.cartridge.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import atg.commerce.catalog.CatalogTools;
import atg.commerce.endeca.assembler.cartridge.handler.CategoryMenuHandler;
import atg.core.util.StringUtils;
import atg.endeca.assembler.navigation.filter.RangeFilterBuilder;
import atg.projects.store.assembler.cartridge.StoreCartridgeTools;
import atg.projects.store.collections.validator.StartEndDateValidator;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.search.record.alias.AttributeAliasManager;
import atg.servlet.ServletUtil;

import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.RefinementMenu;
import com.endeca.infront.cartridge.RefinementMenuConfig;
import com.endeca.infront.cartridge.model.Refinement;
import com.endeca.infront.navigation.model.FilterState;
import com.endeca.infront.navigation.model.RangeFilter;

/**
 * Extends RefinementMenuHandler to override preprocess method 
 * to use CategoryToDimensionAttributeAliasManager to get 
 * the source name for attribute alias. Process method is overridden
 * to update unlocalized category names to localized ones.
 * 
 * @author Yekaterina Kostenevich
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/handler/StoreRefinementMenuHandler.java#6 $$Change: 798089 $
 * @updated $DateTime: 2013/03/20 11:02:21 $$Author: dstewart $
 */
public class StoreRefinementMenuHandler extends CategoryMenuHandler {
  
  //----------------------------------------
  /** Class version string. */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/handler/StoreRefinementMenuHandler.java#6 $$Change: 798089 $";

  //----------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------
  
  protected static final String DISPLAY_NAME_PROPERTY = "displayNameProperty";
  protected static final String DISPLAY_NAME_PROPERTY_ALIAS = "displayNamePropertyAlias";
 
  //----------------------------------------------------------------------
  // MEMBERS
  //----------------------------------------------------------------------
  
  /** This will hold the main navigation filter state when a temporary filter state is being used. */
  protected FilterState mNavigationFilterState = null;
  
  //----------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------
  
  //----------------------------------------------------------------
  // property: localeAttributeAliasManager
  //----------------------------------------------------------------
  private AttributeAliasManager mLocaleAttributeAliasManager = null;
  
  /**
   * @return the Manager component used to determine the source property for the display name aliased property.
   */
  public AttributeAliasManager getLocaleAttributeAliasManager() {
    return mLocaleAttributeAliasManager;
  }

  /**
   * @param pLocaledAttributeAliasManager - The localeAttributeAliasResolver to set.
   */
  public void setLocaleAttributeAliasManager(AttributeAliasManager pLocaleAttributeAliasManager) {
    this.mLocaleAttributeAliasManager = pLocaleAttributeAliasManager;
  }

  //---------------------------------------------------------------
  //  property: categoryIdPropertyName
  //---------------------------------------------------------------
  private String mCategoryIdPropertyName = "category.repositoryId";
  
  /**
   * @returns the category ID property name.
   */
  public String getCategoryIdPropertyName() {
    return mCategoryIdPropertyName;
  }

  /**
   * Sets mCategoryDimensionName
   */
  public void setCategoryIdPropertyName(String pCategoryIdPropertyName) {
    mCategoryIdPropertyName = pCategoryIdPropertyName;
  }
  
  //--------------------------------------------
  //  property: skuPropertyNames
  //--------------------------------------------
  private List<String> mSkuPropertyNames = null;
  
  /**
   * @param pSkuPropertyNames - List of SKU dimension property names that should use SKU range filters. 
   *                            This ensures that when all of a product's SKUs contain invalid date(s), 
   *                            only the SKU refinements will be affected. 
   */
  public void setSkuPropertyNames(List<String> pSkuPropertyNames) {
    mSkuPropertyNames = pSkuPropertyNames;
  }
  
  /**
   * @return the list of SKU dimension property names that should use SKU range filters. 
   *         This ensures that when all of a product's SKUs contain invalid date(s), 
   *         only the SKU refinements will be affected.
   */
  public List<String> getSkuPropertyNames() {
    return mSkuPropertyNames;
  }
  
  //----------------------------------------------------------
  //  property: startEndDateValidator
  //----------------------------------------------------------
  private StartEndDateValidator mStartEndDateValidator = null;
  
  /**
   * @param pStartEndDateValidator - The start/end date validator.
   */
  public void setStartEndDateValidator(StartEndDateValidator pStartEndDateValidator) {
    mStartEndDateValidator = pStartEndDateValidator;
  }
  
  /**
   * @return the start/end date validator.
   */
  public StartEndDateValidator getStartEndDateValidator() {
    return mStartEndDateValidator;
  }
  
  //----------------------------------------
  //  property: catalogTools
  //----------------------------------------
  private CatalogTools mCatalogTools = null;
  
  /**
   * @param pCatalogTools - The catalog tools.
   */
  public void setCatalogTools(CatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }
  
  /**
   *  @return the catalog tools.
   */
  public CatalogTools getCatalogTools() {
    return mCatalogTools;
  }
  
  //-------------------------------------------------------
  // property: rangeFilterBuilders
  //-------------------------------------------------------
  private RangeFilterBuilder[] mRangeFilterBuilders = null;

  /**
   * @return an array of RangeFilterBuilders.
   */
  public RangeFilterBuilder[] getRangeFilterBuilders() {
    return mRangeFilterBuilders;
  }

  /**
   * @param pRangeFilterBuilders - An array of RangeFilterBuilders.
   *          -
   */
  public void setRangeFilterBuilders(RangeFilterBuilder[] pRangeFilterBuilders) {
    mRangeFilterBuilders = pRangeFilterBuilders;
  }

  //--------------------------------------------------------
  // property: storeCartridgeTools
  //--------------------------------------------------------
  protected StoreCartridgeTools mStoreCartridgeTools = null;

  /**
   * @return the StoreCartridgeTools helper component.
   */
  public StoreCartridgeTools getStoreCartridgeTools() {
    return mStoreCartridgeTools;
  }
  
  /**
   * @param pStoreCartridgeTools - the StoreCartridgeTools helper component.
   */
  public void setStoreCartridgeTools(StoreCartridgeTools pStoreCartridgeTools) {
    mStoreCartridgeTools = pStoreCartridgeTools;
  }

  //----------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------

  //----------------------------------------------------------------------
  /**
   * Create a new StoreRefinementMenuConfig using the passed in ContentItem.
   * 
   * @param pContentItem - The cartridge content item to be wrapped.
   * 
   * @return a new RefinementMenu configuration.
   */
  @Override
  protected RefinementMenuConfig wrapConfig(ContentItem pContentItem) {
    return new RefinementMenuConfig(pContentItem);
  }

  //----------------------------------------------------------------------
  /**
   * Determine the source property name for aliased display name property 
   * before calling super.preprocess method.
   * 
   * @param pCartridgeConfig - The RefinementMenu cartridge configuration.
   * 
   * @throws CartridgeHandlerException if an error occurs that is scoped to an individual cartridge instance. 
   *                                   This exception will not halt the entire assembly process, which occurs 
   *                                   across multiple cartridges; instead, this exception will be packaged in 
   *                                   the overall response model. If an unchecked exception is thrown, then the 
   *                                   entire assembly process will be halted.
   */
  @Override
  public void preprocess(RefinementMenuConfig pCartridgeConfig) throws CartridgeHandlerException {
    
    // If we have an alias try to find the source property.
    String displayNameAlias = (String)pCartridgeConfig.get(DISPLAY_NAME_PROPERTY_ALIAS);
    
    if (displayNameAlias != null) {
      
      String sourceName = getLocaleAttributeAliasManager().getSourceNameForAttributeAlias(
        ServletUtil.getCurrentRequest(), displayNameAlias);
    
      if(sourceName != null) {
        pCartridgeConfig.put(DISPLAY_NAME_PROPERTY, sourceName);
      }
      else if ((String)pCartridgeConfig.get(DISPLAY_NAME_PROPERTY_ALIAS) != null) {
        pCartridgeConfig.put(DISPLAY_NAME_PROPERTY, displayNameAlias);
      }
    }

    String dimensionName = (String) pCartridgeConfig.get("dimensionName");

    // If the current dimension is of type SKU, add this component's specific range filters,
    // which should contain filters for sku.startDate and sku.endDate.
    if (getSkuPropertyNames() != null && getSkuPropertyNames().contains(dimensionName)) {
      
      mNavigationFilterState = getNavigationState().getFilterState().clone();
      FilterState newFilterState = getNavigationState().getFilterState().clone();

      if (newFilterState != null) {
        // Update the range filter list with RangeFilters generated by the RangeFilterBuilders.
        List<RangeFilter> rangeFilters = 
          getStoreCartridgeTools().updateRangeFilters(newFilterState.getRangeFilters(), getRangeFilterBuilders());
        
        // Add the updated range filters to the navigation state's filter state.
        newFilterState.setRangeFilters(rangeFilters);
        setNavigationState(getNavigationState().updateFilterState(newFilterState));
        
        super.preprocess(pCartridgeConfig);
        
        return;
      }
    }
      
    super.preprocess(pCartridgeConfig);
  }
  
  //----------------------------------------------------------------------
  /**
   * Creates a new RefinementMenu with localized category labels. Also ensures that
   * menu only includes category refinements with valid start/end dates.
   * 
   * @param pCartridgeConfig - The cartridge configuration for the RefinementMenu.
   * 
   * @throws CartridgeHandlerException if an error occurs that is scoped to an individual cartridge 
   *                                   instance. This exception will not halt the entire assembly process, 
   *                                   which occurs across multiple cartridges; instead, this exception will 
   *                                   be packaged in the overall response model. If an unchecked  exception 
   *                                   is thrown, then the entire assembly process will be halted.
   */
  @Override
  public RefinementMenu process(RefinementMenuConfig pCartridgeConfig) throws CartridgeHandlerException {
    
    RefinementMenu refinementMenu = super.process(pCartridgeConfig);
    
    if (refinementMenu != null && refinementMenu.getRefinements() != null) {
      
      List<Refinement> refinements = refinementMenu.getRefinements();
      List<Refinement> localizedRefinements = new ArrayList<Refinement>(refinements.size());

      for(Refinement refinement: refinements) {
        // For each category refinement look for localized label.
        String currentLabel = refinement.getLabel();
        String localizedLabel = findLocalizedLabel(refinement.getProperties(), 
                                                   currentLabel, 
                                                   (String)pCartridgeConfig.get(DISPLAY_NAME_PROPERTY));
        refinement.setLabel(localizedLabel);
        localizedRefinements.add(refinement);
      }
      
      refinementMenu.setRefinements(localizedRefinements);

      String dimensionName = (String) refinementMenu.get("dimensionName");
      
      // Only filter if refinement is a category
      if (getCategoryDimensionName().equals(dimensionName)) {
        if ( sLogger != null ) {
          sLogger.vlogDebug("Filtering category facets by start/end date");
        }
        // Filter category refinements by start/end dates.
        refinementMenu = filterCategoryFacetsByDate(refinementMenu);
      }
    }
    
    if (mNavigationFilterState != null) {
      // A temporary filter state has been applied to the navigation state for the current
      // dimension. This must reset it back to the original filter state before proceeding.
      setNavigationState(getNavigationState().updateFilterState(mNavigationFilterState));
      mNavigationFilterState = null;
    }
    
    return refinementMenu;
  }

  //----------------------------------------------------------------------
  /**
   * Filter the category refinements by validating their start/end dates.
   * If a category refinement's start date is in the future or it's end
   * date is in the past, it won't be included in the refinement menu.
   * 
   * @param pCategoryRefinementMenu - The refinement menu consisting of category refinements.
   * 
   * @return a RefinementMenu containing all of the category refinements that have valid start/end dates.
   *         If this handler's StartEndDateValidator is null, no start/end date validation will occur and
   *         the pCategoryRefinementMenu will just be returned.
   * 
   * @throws CartridgeHandlerException
   */
  protected RefinementMenu filterCategoryFacetsByDate(RefinementMenu pCategoryRefinementMenu) 
    throws CartridgeHandlerException { 

    if(getStartEndDateValidator() != null) {
    
      List<Refinement> currentRefinements = pCategoryRefinementMenu.getRefinements();
      List<Refinement> validRefinements = new ArrayList<Refinement>();
      
      for (Refinement refinement : currentRefinements) {
        
        String categoryId = refinement.getProperties().get(getCategoryIdPropertyName());
        
        if (categoryId != null) {
          RepositoryItem categoryItem = null;
          
          try {
            categoryItem = getCatalogTools().findCategory(categoryId, getCatalogsPropertyName());
          } 
          catch (RepositoryException e) {
            if (sLogger != null) {
              sLogger.vlogError("There was a problem finding category with ID: {0}",
                                categoryId);
            }
          }
          
          if (categoryItem != null) {
            // Only add the category refinement if it passes start/end date validation.
            if (getStartEndDateValidator().validateObject(categoryItem)) {
              validRefinements.add(refinement);
            }
            else {
              if (sLogger != null) {
                sLogger.vlogDebug("Invalid category {0}", refinement.getLabel());
              }
            }
          }
        }
        else {
          if (sLogger != null) {
            sLogger.vlogDebug("No category ID could be found for refinement: {0}",
                              refinement.getLabel());
          }
        }
      }
      
      pCategoryRefinementMenu.setRefinements(validRefinements);
    }
    
    return pCategoryRefinementMenu;
  }

  //----------------------------------------------------------------------
  /**
   * Looks for localized label for refinements.
   * 
   * @param pProperties - The properties of current refinement.
   * @param pLabel - The non-localized label of refinement.
   * @param pDisplayNameProperty - The localized display name property.
   * 
   * @return a localized label.
   */
  public String findLocalizedLabel(Map<String, String> pProperties,
                                   String pLabel, 
                                   String pDisplayNameProperty) {
    
    String localizedLabel = pLabel;
    String localizedName = pProperties.get(pDisplayNameProperty);
    
    if (! StringUtils.isEmpty(localizedName)) {
      localizedLabel = localizedName;
    }  
    
    return localizedLabel;
  }

}
