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

import atg.core.util.StringUtils;
import atg.endeca.assembler.AssemblerTools;
import atg.projects.store.actor.ActorExecutor;
import atg.projects.store.assembler.cartridge.ProductListCategoryChildrenContentItem;
import atg.projects.store.assembler.cartridge.StoreCartridgeTools;
import atg.projects.store.catalog.StoreCatalogProperties;
import atg.projects.store.catalog.StoreCatalogTools;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.actor.Actor;

import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;
import com.endeca.infront.cartridge.model.NavigationAction;
import com.endeca.infront.cartridge.model.SortOptionLabel;
import com.endeca.infront.navigation.NavigationState;
import com.endeca.infront.navigation.model.FilterState;

import java.util.ArrayList;
import java.util.List;

/**
 * Category header banner cartridge handler.
 * 
 * @author Paul Watson
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/handler/ProductListCategoryChildrenHandler.java#4 $$Change: 794592 $
 * @updated $DateTime: 2013/03/04 17:50:17 $$Author: cbarthle $
 */

public class ProductListCategoryChildrenHandler 
  extends NavigationCartridgeHandler<ContentItem, ProductListCategoryChildrenContentItem> {

  //----------------------------------------
  /** Class version string. */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/handler/ProductListCategoryChildrenHandler.java#4 $$Change: 794592 $";

  //----------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------
  
  // Dimension properties
  /** The dimension property key for the chosen category */
  public static String CATEGORY_ID = "category.repositoryId";
  /** The dimension property key for the chosen category hierarchy */
  public static String CATEGORY_HIERARCHY_DIM_PROP_NAME  = "SourceId";
  /** The chosen category's root catalog ID */
  public static String CATEGORY_ROOT_CATALOG_ID = "category.rootCatalogId";
  
  /** The Constant RECORDS_PER_PAGE. */
  private static final String RECORDS_PER_PAGE = "recordsPerPage";
  
  /** Default number of records per page*/
  public static final int DEFAULT_NUMBER_OF_RECORDS = 12;
  
  //----------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------
  
  //---------------------------------------------
  // property: catalogTools
  //---------------------------------------------
  private StoreCatalogTools mCatalogTools = null;

  /**
   * @return the CatalogTools component.
   */
  public StoreCatalogTools getCatalogTools() {
    return mCatalogTools;
  }
  
  /**
   * @param pCatalogTools - The CatalogTools component.
   */
  public void setCatalogTools(StoreCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
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

  private ActorExecutor mActorExecutor = null;

  /**
   * @return the actorExecutor
   */
  public ActorExecutor getActorExecutor() {
    return mActorExecutor;
  }

  /**
   * @param pActorExecutor the actorExecutor to set
   */
  public void setActorExecutor(ActorExecutor pActorExecutor) {
    mActorExecutor = pActorExecutor;
  }

  private Actor mPostProcessActor = null;

  /**
   * @return the postProcessActor
   */
  public Actor getPostProcessActor() {
    return mPostProcessActor;
  }

  /**
   * @param pPostProcessActor the postProcessActor to set
   */
  public void setPostProcessActor(Actor pPostProcessActor) {
    mPostProcessActor = pPostProcessActor;
  }

  private String mSortParamName = "sort";

  /**
   * @return the sortParamName
   */
  public String getSortParamName() {
    return mSortParamName;
  }

  /**
   * @param pSortParamName the sortParamName to set
   */
  public void setSortParamName(String pSortParamName) {
    mSortParamName = pSortParamName;
  }

  private String mSortOptionsContentItemPropertyName = "sortOptions";

  /**
   * @return the sortOptionsContentItemPropertyName
   */
  public String getSortOptionsContentItemPropertyName() {
    return mSortOptionsContentItemPropertyName;
  }

  /**
   * @param pSortOptionsContentItemPropertyName the
   *          sortOptionsContentItemPropertyName to set
   */
  public void setSortOptionsContentItemPropertyName(
      String pSortOptionsContentItemPropertyName) {
    mSortOptionsContentItemPropertyName = pSortOptionsContentItemPropertyName;
  }

  private String mPageParamName = "p";

  /**
   * @return the pageParamName
   */
  public String getPageParamName() {
    return mPageParamName;
  }

  /**
   * @param pPageParamName the pageParamName to set
   */
  public void setPageParamName(String pPageParamName) {
    mPageParamName = pPageParamName;
  }

  private String mPageParamTemplate = "{pageNum}";

  /**
   * @return the pageParamTemplate
   */
  public String getPageParamTemplate() {
    return mPageParamTemplate;
  }

  /**
   * @param pPageParamTemplate the pageParamTemplate to set
   */
  public void setPageParamTemplate(String pPageParamTemplate) {
    mPageParamTemplate = pPageParamTemplate;
  }

  private String mPagingActionTemplateContentItemPropertyName = "pagingActionTemplate";

  /**
   * @return the pagingActionTemplateContentItemPropertyName
   */
  public String getPagingActionTemplateContentItemPropertyName() {
    return mPagingActionTemplateContentItemPropertyName;
  }

  /**
   * @param pPagingActionTemplateContentItemPropertyName the
   *          pagingActionTemplateContentItemPropertyName to set
   */
  public void setPagingActionTemplateContentItemPropertyName(
      String pPagingActionTemplateContentItemPropertyName) {
    mPagingActionTemplateContentItemPropertyName = pPagingActionTemplateContentItemPropertyName;
  }

  private boolean mEnablePostProcessActor = false;

  /**
   * @return the enablePostProcessActor
   */
  public boolean isEnablePostProcessActor() {
    return mEnablePostProcessActor;
  }

  /**
   * @param pEnablePostProcessActor the enablePostProcessActor to set
   */
  public void setEnablePostProcessActor(boolean pEnablePostProcessActor) {
    mEnablePostProcessActor = pEnablePostProcessActor;
  }

  // ----------------------------------------------------------------------
  // METHODS
  // ----------------------------------------------------------------------

  //-----------------------------------------------------------------------
  /**
   * Wrap config method.
   * 
   * @param pContentItem - The cartridge content item to be wrapped.
   * 
   * @return a new Category Child Product List configuration.
   */
  @Override
  protected ContentItem wrapConfig(ContentItem pContentItem) {
    return new BasicContentItem(pContentItem);
  }

  //-----------------------------------------------------------------------
  /**
   * <p>
   *   Create a new ProductListCategoryChildrenContentItem.
   * </p>
   * <p>
   *   This process method will populate the cartridge with details such as category title, hero image and
   *   promotional content.
   * </p>
   * 
   * @param pCartridgeConfig - The cartridge configuration for the CategoryChildProductList.
   * 
   * @throws CartridgeHandlerException
   */
  @Override
  public ProductListCategoryChildrenContentItem process(ContentItem pCartridgeConfig)
    throws CartridgeHandlerException {

    NavigationState navigationState = getNavigationState();
    
    ProductListCategoryChildrenContentItem categoryChildProductList = 
      new ProductListCategoryChildrenContentItem(pCartridgeConfig);
    
    if (getStoreCartridgeTools().getCurrentCategoryId() != null) {
      // Populate the content item with details from the current chosen category.
      loadCategoryDetails(pCartridgeConfig, categoryChildProductList);
      categoryChildProductList.setRecsPerPage(
        ((BasicContentItem)pCartridgeConfig).getIntProperty(RECORDS_PER_PAGE, DEFAULT_NUMBER_OF_RECORDS));
      categoryChildProductList.setCategoryAction(navigationState.toString());
    } 
    else {
      categoryChildProductList.setCategoryId("");
      categoryChildProductList.setTotalNumRecs(0L);
      categoryChildProductList.setCategoryAction("");
    }
    if (shouldExecutePostProcessActor()) {
      executePostProcessActor(categoryChildProductList);

      addSortOptions(categoryChildProductList);
      addPagingTemplate(categoryChildProductList);
    }

    return categoryChildProductList;
  }

  /**
   * Determines if the
   * executePostProcessActor(ProductListCategoryChildrenContentItem),
   * addSortOpstions(ProductListCategoryChildrenContentItem), and
   * addPagingTemplate(ProductListCategoryChildrenContentItem) methods should be
   * executed.
   * 
   * @return true if both enablePostProcessActor is true and the "format" param
   *         is set.
   */
  protected boolean shouldExecutePostProcessActor() {
    return isEnablePostProcessActor()
        && !StringUtils.isBlank(getNavigationState().getParameter("format"));
  }

  /**
   * Adds sort actions to the content item.
   * 
   * @param pContentItem The content item to add the sort options to.
   */
  protected void addSortOptions(
      ProductListCategoryChildrenContentItem pContentItem) {
    List<SortOptionLabel> sorts = new ArrayList<SortOptionLabel>();

    sorts.add(createSortNavAction(null, "common.topPicks"));
    sorts.add(createSortNavAction("displayName:ascending", "sort.nameAZ"));
    sorts.add(createSortNavAction("displayName:descending", "sort.nameZA"));
    sorts.add(createSortNavAction("price:ascending", "sort.priceLH"));
    sorts.add(createSortNavAction("price:descending", "sort.priceHL"));

    pContentItem.put(getSortOptionsContentItemPropertyName(), sorts);
  }

  /**
   * Adds a paging template action to the content item
   * 
   * @param pContentItem The content item to add the action to.
   */
  protected void addPagingTemplate(
      ProductListCategoryChildrenContentItem pContentItem) {
    pContentItem.put(getPagingActionTemplateContentItemPropertyName(),
        createPagingActionTemplate());
  }
  
  /**
   * Invokes the postProcessActor.
   * 
   * @param pContentItem The content item to make available to the actor.
   */
  protected void executePostProcessActor(
      ProductListCategoryChildrenContentItem pContentItem) {
    if(getActorExecutor()!=null){
      getActorExecutor().invokeActor(getPostProcessActor(), pContentItem);
    }
  }

  /**
   * Creates a sort action for a given sort and label.
   * 
   * @param pSortSelection The sort order
   * @param pSortLabel The label
   * @return The SortOptionLabel action
   */
  protected SortOptionLabel createSortNavAction(String pSortSelection,
      String pSortLabel) {
    NavigationState navigationState = getNavigationState();
    String currentSort = navigationState.getParameter(getSortParamName());
    NavigationState sortNavAction;
    if (StringUtils.isBlank(pSortSelection)) {
      sortNavAction = navigationState.removeParameter(getSortParamName());
    }
    else {
      sortNavAction = navigationState.putParameter(getSortParamName(),
        pSortSelection);
    }

    SortOptionLabel sortOpt = new SortOptionLabel();
    sortOpt.setNavigationState(sortNavAction.toString());
    sortOpt.setLabel(pSortLabel);

    populateNavigationPathDefaults(sortOpt);

    if (StringUtils.isBlank(currentSort) && StringUtils.isBlank(pSortSelection)) {
      // If there is no sort selection set the default sort as selected
      sortOpt.setSelected(true);
    }
    else if (pSortSelection != null && pSortSelection.equals(currentSort)) {
      sortOpt.setSelected(true);
    }
    else {
      sortOpt.setSelected(false);
    }

    return sortOpt;
  }

  /**
   * Creates the paging action template.
   * 
   * @return The navigation action for paging.
   */
  protected NavigationAction createPagingActionTemplate() {
    NavigationState navState = getNavigationState();

    NavigationState pageState = navState.putParameter(getPageParamName(),
        getPageParamTemplate());

    NavigationAction pagingAction = new NavigationAction(pageState.toString());
    populateNavigationPathDefaults(pagingAction);

    return pagingAction;
  }

  //----------------------------------------------------------------------
  /**
   * This method sets the current chosen category/dimension IDs and the total number 
   * of records (child products) in the cartridge.
   * 
   * @param pCartridgeConfig - The cartridge configuration for the CategoryChildProductList.
   * 
   * @param pCargridge - The actual cartridge used in this handler.
   * 
   */
  protected void loadCategoryDetails(ContentItem pCartridgeConfig, ProductListCategoryChildrenContentItem pCartridge) {

    Repository catalog = getCatalogTools().getCatalog();
    StoreCatalogProperties catalogProperties = (StoreCatalogProperties) getCatalogTools().getCatalogProperties();
    
    // The currently chosen category ID. This will be used to retrieve the current category item.
    String chosenCategoryId = getStoreCartridgeTools().getCurrentCategoryId();

    try {  
      if (!StringUtils.isEmpty(chosenCategoryId)) {
        
        RepositoryItem currentCat = null;
          
        // The category that has triggered this handler.
        currentCat = catalog.getItem(chosenCategoryId, catalogProperties.getCategoryItemName());
        pCartridge.setCategoryId(chosenCategoryId);
        pCartridge.setCategory(currentCat);
        
        FilterState filterState = getNavigationState().getFilterState();
        List<String> navigationFilters= filterState.getNavigationFilters();
        
        if (navigationFilters != null && navigationFilters.size() > 0) {
          // Set the current category dimension ID in the cartridge. 
          pCartridge.setCategoryDimensionId(Integer.parseInt(navigationFilters.get(0)));
        }
        else {
          AssemblerTools.getApplicationLogging().vlogDebug(
            "The Dimension ID for category {0} was not found in the current navigation filter", chosenCategoryId);
        }

        if (currentCat != null) {
          
          List childProducts = (List) currentCat.getPropertyValue("childProducts");
          
          if (childProducts != null) {
            pCartridge.setTotalNumRecs(childProducts.size());
          }
          else {
            pCartridge.setTotalNumRecs(0);
          }
        }
      }
    } 
    catch (RepositoryException re) {
      AssemblerTools.getApplicationLogging().logError(
        "There was a problem retrieving the category or top level category from the catalog", re);
    }
  }

}
