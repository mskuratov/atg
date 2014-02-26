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

import atg.repository.RepositoryItem;

import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.ContentItem;

/**
 * Category Header Banner class.
 * 
 * @author Paul Watson
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/ProductListCategoryChildrenContentItem.java#5 $$Change: 792760 $
 * @updated $DateTime: 2013/02/25 10:34:51 $$Author: dstewart $
 */
public class ProductListCategoryChildrenContentItem extends BasicContentItem {

  //----------------------------------------
  /** Class version string. */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/ProductListCategoryChildrenContentItem.java#5 $$Change: 792760 $";
  
  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------
  
  public static final String RECORDS_PER_PAGE_KEY = "recsPerPage";
  public static final String CATEGORY_ID_KEY = "categoryId";
  public static final String CATEGORY_DIMENSION_ID_KEY = "categoryDimensionId";
  public static final String TOTAL_NUM_RECS_KEY = "totalNumRecs";
  public static final String CATEGORY_ACTION_KEY = "categoryAction";
  public static final String CATEGORY = "category";

  //----------------------------------------------------------------------------
  // CONSTRUCTOR
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  /**
   * Instantiates a new product list category children.
   *
   * @param pConfig - The configuration item.
   */
  public ProductListCategoryChildrenContentItem(ContentItem pConfig) {
    super(pConfig);
  }

  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------

  //------------------------------
  // property: getRecsPerPage
  //------------------------------

  /**
   * @return the records per page.
   */
  public long getRecsPerPage() {
    return getLongProperty(RECORDS_PER_PAGE_KEY, 0L);
  }

  /**
   * @param pRecsPerPage - The records per page.
   */
  public void setRecsPerPage(long pRecsPerPage) {
    put(RECORDS_PER_PAGE_KEY, Long.valueOf(pRecsPerPage));
  }
  
  //------------------------------
  // property: CategoryId
  //------------------------------
  
  /**
   * @return the category ID.
   */
  public String getCategoryId() {
    return (String) get(CATEGORY_ID_KEY);
  }

  /**
   * @param pCategoryId - The new category ID.
   */
  public void setCategoryId(String pCategoryId) {
    put(CATEGORY_ID_KEY, pCategoryId);
  }
  
  //------------------------------
  // property: categoryAction
  //------------------------------

  /**
   * @return the category action.
   */
  public String getCategoryAction() {
    return (String) get(CATEGORY_ACTION_KEY);
  }

  /**

   * @param pCategoryAction - The new category action.
   */
  public void setCategoryAction(String pCategoryAction) {
    put(CATEGORY_ACTION_KEY, pCategoryAction);
  }
  
  //--------------------------------
  // property: categoryDimensionId
  //--------------------------------

  /**
   * @return the category dimension ID
   */
  public String getCategoryDimensionId() {
    if (get(CATEGORY_DIMENSION_ID_KEY) != null) {
      return Long.toString((Long) get(CATEGORY_DIMENSION_ID_KEY));
    }
    
    return null;
  }

  /**
   * @param pCategoryDimensionId - The new category dimension ID.
   */
  public void setCategoryDimensionId(long pCategoryDimensionId) {
    put(CATEGORY_DIMENSION_ID_KEY, Long.valueOf(pCategoryDimensionId));
  }

  //------------------------------------
  // property: totalNumRecs
  //------------------------------------
  /**
   * Gets the total num recs.
   *
   * @return the total num recs
   */
  public long getTotalNumRecs() {
    return getLongProperty(TOTAL_NUM_RECS_KEY, 0L);
  }

  /**
   * Sets the total num recs.
   *
   * @param pTotalNumRecs the new total num recs
   */
  public void setTotalNumRecs(long pTotalNumRecs) {
    put(TOTAL_NUM_RECS_KEY, Long.valueOf(pTotalNumRecs));
  }
  
  //----------------------------------
  // property: category
  //----------------------------------
  /**
   * @return the category item.
   */
  public RepositoryItem getCategory() {
    return (RepositoryItem)get(CATEGORY);
  }

  /**
   * @param pCategory - The category item.
   */
  public void setCategory(RepositoryItem pCategory) {
    put(CATEGORY, pCategory);
  }

}

