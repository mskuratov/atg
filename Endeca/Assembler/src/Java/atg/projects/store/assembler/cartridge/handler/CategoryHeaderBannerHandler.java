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
import atg.projects.store.assembler.cartridge.CategoryHeaderBannerContentItem;
import atg.projects.store.assembler.cartridge.StoreCartridgeTools;
import atg.projects.store.catalog.StoreCatalogProperties;
import atg.projects.store.catalog.StoreCatalogTools;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.collections.validator.CollectionObjectValidator;

import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;

/**
 * Category header banner cartridge handler.
 *
 * @author David Stewart
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/handler/CategoryHeaderBannerHandler.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class CategoryHeaderBannerHandler 
  extends NavigationCartridgeHandler<ContentItem, CategoryHeaderBannerContentItem> {
  
  //----------------------------------------------------------------------
  // STATIC
  //----------------------------------------------------------------------
  
  /** Class version string. */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/handler/CategoryHeaderBannerHandler.java#3 $$Change: 788278 $";
  
  // Repository property names.
  /** The category's 'feature' property name */
  public static final String FEATURE_PROPERTY_NAME = "feature";
  /** The category's hero image property name */
  public static final String HERO_IMAGE_PROPERTY_NAME = "heroImage";
  /** The path for the actual hero image */
  public static final String URL_PROPERTY_NAME = "url";
  
  //----------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------
  
  //---------------------------------------
  // property: catalogTools
  //---------------------------------------
  private StoreCatalogTools mCatalogTools;
 
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

  //--------------------------------------------------
  // property: storeCartridgeTools
  //--------------------------------------------------
  protected StoreCartridgeTools mStoreCartridgeTools;

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
  
  //-----------------------------------
  // property: validators
  private CollectionObjectValidator[] mValidators;
  
  /**
   * @return array of validators that will be applied to items
   */
  public CollectionObjectValidator[] getValidators() {
    return mValidators;
  }

  /**
   * @param validators the validators to set
   */
  public void setValidators(CollectionObjectValidator[] pValidators) {
    this.mValidators = pValidators;
  }

  //----------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------
 
  //----------------------------------------------------------------------
  /**
   * Wrap config method.
   * 
   * @param pContentItem - The cartridge content item to be wrapped.
   * 
   * @return new BasicContentItem created based on pContentItem
   */
  @Override
  protected ContentItem wrapConfig(ContentItem pContentItem) {
    return new BasicContentItem(pContentItem);
  }

  //----------------------------------------------------------------------
  /**
   * <p>
   *   Create a new CategoryHeaderBannerContentItem.
   * </p>
   * <p>
   *   This process method will populate the cartridge with details such as category title, 
   *   hero image and promotional content.
   * </p>
   * 
   * @param pCartridgeConfig - The cartridge configuration for the CategoryHeaderBannerContentItem.
   * 
   * @throws CartridgeHandlerException
   */
  @Override
  public CategoryHeaderBannerContentItem process(ContentItem pCartridgeConfig) throws CartridgeHandlerException {
    
    CategoryHeaderBannerContentItem categoryHeaderBanner = new CategoryHeaderBannerContentItem(pCartridgeConfig);
    
    // Get the category with the appropriate promotionalContent and populate the cartridge with it's details.
    loadBannerItemDetails(pCartridgeConfig, categoryHeaderBanner);
    
    return categoryHeaderBanner;
  }

  //----------------------------------------------------------------------
  /**
   * <p>
   *   This method retrieves the currently chosen category and top level category from the 
   *   CatalogNavigationService cache. The current category displayName property value is used to
   *   set the cartridge's headerTitle property and the top level category's hero image path 
   *   and promotionalContent id are used to set the cartridge's backgroundBannerURL and
   *   promotionalContentId properties.
   * </p> 
   * 
   * @param pCartridgeConfig - The cartridge configuration for the CategoryHeaderBannerContentItem.
   * @param pCartridge - The actual cartridge used in this handler.
   */
  protected void loadBannerItemDetails(ContentItem pCartridgeConfig, 
                                         CategoryHeaderBannerContentItem pCartridge) {

    Repository catalog = getCatalogTools().getCatalog();
    StoreCatalogProperties catalogProperties = (StoreCatalogProperties) getCatalogTools().getCatalogProperties();
    
    // The currently chosen category id. This will be used to retrieve the current category item.
    String chosenCategoryId = getStoreCartridgeTools().getCurrentCategoryId();
    // The currently chosen top level category id. This will be used to retrieve the current category 
    // top level category item.
    String chosenCategoryTopLevelCategory = getStoreCartridgeTools().getCatalogNavigation().getTopLevelCategory();
    
    RepositoryItem chosenCategoryItem = null;
    RepositoryItem chosenCategoryTopLevelCategoryItem = null;
    
    try {  
      // The category item that has triggered this handler.
      chosenCategoryItem = 
        !StringUtils.isEmpty(chosenCategoryId) ? 
          catalog.getItem(chosenCategoryId, catalogProperties.getCategoryItemName()) : 
          null;
          
      // The chosen category's top level category item.
      chosenCategoryTopLevelCategoryItem = 
        !StringUtils.isEmpty(chosenCategoryTopLevelCategory) ? 
          catalog.getItem(chosenCategoryTopLevelCategory, catalogProperties.getCategoryItemName()) :
          null;
    } 
    catch (RepositoryException re) {
      AssemblerTools.getApplicationLogging().logError(
        "There was a problem retrieving the category or top level category from the catalog", re);
    }
    
    if (chosenCategoryItem != null && chosenCategoryTopLevelCategoryItem != null) {
      
      // Retrieve the top level category's hero image item.
      RepositoryItem heroImage = (RepositoryItem) 
        chosenCategoryTopLevelCategoryItem.getPropertyValue(HERO_IMAGE_PROPERTY_NAME);
      
      if (heroImage == null) {  
        AssemblerTools.getApplicationLogging().vlogDebug(
          "{0} has no hero image property value. No banner details will be returned", 
          chosenCategoryTopLevelCategoryItem.getItemDisplayName());
        
        return;
      }
      
      if (!validateItem(heroImage)){
        AssemblerTools.getApplicationLogging().vlogDebug(
            "Hero image with id {0} doen't pass validation. No banner details will be returned", 
            heroImage.getRepositoryId());
          
          return;
      }
      
      // Add the chosen category's display name to the cartridge.
      pCartridge.put(CategoryHeaderBannerContentItem.HEADER_TITLE, (String) chosenCategoryItem.getItemDisplayName());
      
      // Add the top level category's hero image URL to the cartridge.
      String heroImageURL = (String) heroImage.getPropertyValue(URL_PROPERTY_NAME);
      pCartridge.put(CategoryHeaderBannerContentItem.BACKGROUND_BANNER_URL, heroImageURL);
      
      RepositoryItem feature = (RepositoryItem) 
        chosenCategoryTopLevelCategoryItem.getPropertyValue(FEATURE_PROPERTY_NAME);
      
      if (feature != null && validateItem(feature)) {
        // Add the top level category's promotional content to the cartridge.
        pCartridge.put(CategoryHeaderBannerContentItem.PROMOTIONAL_CONTENT_ID, feature.getRepositoryId());
      }
      else {
        AssemblerTools.getApplicationLogging().vlogDebug(
          "{0} has no promotional content (feature) property value defined or it's feature doen't pass validation",
          chosenCategoryTopLevelCategoryItem.getItemDisplayName());
      }
      
    }
  }
  
  //----------------------------------------------------------------------
  /**
   * Validate repository item using configured set of validators.
   * 
   * @param pItem - The item to validate.
   * 
   * @return false if item fails validation, true if there should be no validation or item is valid.
   */
  public boolean validateItem(RepositoryItem pItem) {
    
    // There are no validators set, so no filtering is needed.
    if (getValidators() == null || getValidators().length == 0) {
      return true;
    }
    
    boolean isValid = true;
    
    for (CollectionObjectValidator validator : getValidators()) {
      if (!validator.validateObject(pItem)) {
        AssemblerTools.getApplicationLogging().vlogDebug(
          "Item {0} doesn't pass validator: {1}", pItem.getRepositoryId(), validator);
        
        // Item doesn't pass validation. Set isValid to false
        // and leave the loop as there is no need to check all
        // others validators.                
        isValid = false;
        break;        
      }
    }
    
    return isValid;
  }

}
