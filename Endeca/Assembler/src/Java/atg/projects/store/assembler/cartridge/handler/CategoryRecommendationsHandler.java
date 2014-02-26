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
import atg.projects.store.assembler.cartridge.CategoryRecommendationsContentItem;
import atg.projects.store.assembler.cartridge.StoreCartridgeTools;

import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.assembler.ContentItemInitializer;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;

/**
 * Handler for the CategoryRecommendations cartridge. This class is responsible for creating
 * and initializing the CategoryRecommendationsContentItem content item. It extends NavigationCartridgeHandler.
 * 
 * The handler creates and executes MDEX request. The results of the MDEX request execution is used
 * to determine the currently selected category ID.
 *
 * @author Natallia Paulouskaya
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/handler/CategoryRecommendationsHandler.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class CategoryRecommendationsHandler 
  extends NavigationCartridgeHandler<ContentItem, CategoryRecommendationsContentItem> {
  
  //---------------------------------------------------------------------------
  // STATIC
  //---------------------------------------------------------------------------

  /** Class version string. */
  protected static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/handler/CategoryRecommendationsHandler.java#3 $$Change: 788278 $";
  
  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------
  
  //--------------------------------------------------------
  // property: StoreCartridgeTools
  //--------------------------------------------------------
  protected StoreCartridgeTools mStoreCartridgeTools = null;

  /**
   * @param pStoreCartridgeTools - The StoreCartridgeTools helper component.
   */
  public void setStoreCartridgeTools(StoreCartridgeTools pStoreCartridgeTools) {
    mStoreCartridgeTools = pStoreCartridgeTools;
  }

  /**
   * @return the StoreCartridgeTools helper component.
   */
  public StoreCartridgeTools getStoreCartridgeTools() {
    return mStoreCartridgeTools;
  }
  
  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------
  
  //---------------------------------------------------------------------------
  /**
   * Creates a new CategoryRecommendationsContentItem and populates it with 
   * the currently viewed category ID.
   * 
   * @param pCartridgeConfig - This cartridge's configuration content item.
   * 
   * @return a CategoryRecommendationsContentItem referencing the currently viewed category ID.
   * 
   * @throws CartridgeHandlerException
   */
  @Override
  public CategoryRecommendationsContentItem process(ContentItem pCartridgeConfig) 
    throws CartridgeHandlerException {
    
    // Create a CategoryRecommendationsContentItem content item.
    CategoryRecommendationsContentItem recommendations = new CategoryRecommendationsContentItem(pCartridgeConfig);
    
    // Retrieve currently viewed category ID using StoreCartridgeTools component.
    String categoryId = getStoreCartridgeTools().getCurrentCategoryId();
    
    // If category ID is found, store category ID and category featured items in content item.
    if (!StringUtils.isEmpty(categoryId)){
      recommendations.setCategoryId(categoryId);
    }
    
    return recommendations;
  }
 
  //---------------------------------------------------------------------------
  /**
   * Create a new CategoryRecommendationsConfig using the passed in ContentItem.
   * 
   * @param pContentItem - The configuration content item for this cartridge handler. This will either be 
   *                       the fully initialized configuration object, if a {@link ContentItemInitializer} 
   *                       has been set, or it will simply be the instance configuration.
   * 
   * @return an instance of <code>ConfigType</code> which wraps the input {@link ContentItem}.
   */
  @Override
  protected ContentItem wrapConfig(ContentItem pContentItem) {
    return new BasicContentItem(pContentItem);
  }
  
}