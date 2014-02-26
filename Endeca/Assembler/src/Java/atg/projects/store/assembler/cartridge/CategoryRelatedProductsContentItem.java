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

import java.util.List;

import atg.repository.RepositoryItem;

import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.ContentItem;

/**
 * CategoryRelatedProductsContentItem cartridge which will be returned as part of a larger ContentItem
 * on category page and used to renderer category related products.
 *
 * @author Natallia Paulouskaya
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/CategoryRelatedProductsContentItem.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class CategoryRelatedProductsContentItem extends BasicContentItem {

  //-------------------------------------------
  /** Class version string. */
  protected static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/CategoryRelatedProductsContentItem.java#3 $$Change: 788278 $";

  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------
  
  /** The cartridge numberOfRecords property name */
  public static final String NUMBER_OF_RECORDS = "numberOfRecords";
  
  /** The cartridge categoryId property name */
  public static final String CATEGORY_ID = "categoryId";
  
  /** The cartridge relatedProducts property name */
  public static final String RELATED_PRODUCTS = "relatedProducts";
  
  /** The default value for the numberOfRecords property. It set to unlimited number. */
  public static final int DEFAULT_NUMBER_OF_RECORDS = -1;

  //----------------------------------------------------------------------------
  // CONSTRUCTOR
  //----------------------------------------------------------------------------

  //----------------------------------------------------------------------------
  /**
   * Construct a CategoryRelatedProductsContentItem from the pConfig passed in.
   * 
   * @param pConfig ContentItem content item configuration.
   */
  public CategoryRelatedProductsContentItem(ContentItem pConfig) {
    super(pConfig);
  }

  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------

  //------------------------------
  // property: numberOfRecords
  //------------------------------

  /**
   * @return the number of records to display in the cartridge.
   */
  public int getNumberOfRecords(){
    return getIntProperty(NUMBER_OF_RECORDS, DEFAULT_NUMBER_OF_RECORDS);
  }

  /**
   * @param pNumberOfRecords - The number of records to display in the cartridge.
   */
  public void setNumberOfRecords(int pNumberOfRecords){
    put(NUMBER_OF_RECORDS, pNumberOfRecords);
  }
  
  //------------------------------
  // property: categoryId
  //------------------------------

  /**
   * @return the category ID for which the related products should be displayed.
   */
  public String getCategoryId(){
    return getTypedProperty(CATEGORY_ID);
  }

  /**
   * @param pCategoryId - The category ID for which the related products should be displayed.
   */
  public void setCategoryId(String pCategoryId){
    put(CATEGORY_ID, pCategoryId);
  }
  
  //------------------------------
  // property: relatedProducts
  //------------------------------

  /**
   * @return The list of category's related products.
   */
  public List<RepositoryItem> getRelatedProducts(){
    return getTypedProperty(RELATED_PRODUCTS);
  }

  /**
   * @param pRelatedProducts - The list of category's related products.
   */
  public void setRelatedProducts(List<RepositoryItem> pRelatedProducts){
    put(RELATED_PRODUCTS, pRelatedProducts);
  }

}

