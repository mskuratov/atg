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



package atg.projects.store.catalog.comparison;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.commerce.catalog.comparison.ProductComparisonList;
import atg.commerce.catalog.comparison.ProductListHandler;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


/**
 * Extension to the atg.commerce.catalog.comparison.ProductListHandler to obtain property
 * values from the request parameter if not supplied via a form submission.
 *
 * @see atg.commerce.catalog.comparison.ProductListHandler
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/catalog/comparison/StoreProductListHandler.java#2 $
 * @updated $ $$ $
 */
public class StoreProductListHandler extends ProductListHandler
{

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/catalog/comparison/StoreProductListHandler.java#2 $$Change: 768606 $";

  /**
   * Request parameter ID constant values
   */
  public static final String CATEGORY_ID = "categoryID";
  public static final String PRODUCT_ID = "productID";
  public static final String SKU_ID = "skuID";

  /**
   * Overrides ProductListHandler.preAddProduct()
   * This method is called just before adding a product to the list.
   * Attempts to retrieve category id/sku id/product id from the request parameter if
   * these properties are currently unset.
   *
   * @param pRequest the dynamo request object
   * @param pResponse the dynamo response object
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   **/
  public void preAddProduct(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
  throws ServletException, IOException {
    super.preAddProduct( pRequest, pResponse );
    
    vlogDebug("Passed productID is {0}.",  getProductID() );
    vlogDebug("Passed categoryID is {0}.",  getCategoryID() );
    vlogDebug("Passed skuID is {0}.",  getSkuID() );
    
    //If the product id is blank then put the category id or then the sku id in the product id
    if (StringUtils.isBlank(getProductID())) {
      if (!StringUtils.isBlank(getCategoryID())) {
        setProductID(getCategoryID());
      }

      if (!StringUtils.isBlank(getSkuID())) {
        setProductID(getSkuID());
      }
    }
  }


  /**
   * Add the product specified by <code>productID</code> to the product
   * comparison list, applying the optional category and sku information
   * in <code>categoryID</code> and <code>skuID</code>.
   *
   * @param pRequest the dynamo request object
   * @param pResponse the dynamo response object
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   */
  protected void addProduct(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    ProductComparisonList list = getProductList();
    String productID = getProductID();

    if (list == null) {
      String errorCode = "noProductList";
      addFormException(new DropletException(getUserMessage(errorCode, pRequest), errorCode));
    }
    if (StringUtils.isBlank(productID)) {
      String errorCode = "noProductId";
      addFormException(new DropletException(getUserMessage(errorCode, pRequest), errorCode));
    }

    if (! checkFormRedirect(null, getAddProductErrorURL(), pRequest, pResponse)){
      return;
    }

    try {
      list.add(productID, getCategoryID(), getSkuID(), getRepositoryKey(), getSiteID());
    }
    catch (RepositoryException re) {
      String errorCode = "errorAddingProduct";
      addFormException(new DropletException(getUserMessage(errorCode, pRequest), re, errorCode));
    }
  }

  /**
   * Overrides ProductListHandler.handleAddProduct()
   * Adds the product to the product comparison comparison list.
   *
   * @see ProductComparisonList#add(String,String,String,String) ProductComparisonList.add()
   *
   * @param pRequest the dynamo request object
   * @param pResponse the dynamo response object
   * @return If redirect (for whatever reason) to a new page occurred, return false. 
   * If NO redirect occurred, return true.
   * @exception ServletException if something went wrong
   * @exception IOException if something went wrong
   **/
  public boolean handleAddProduct(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
      try{
      //If any form errors found, redirect to error URL:
      if (! checkFormRedirect(null, getAddProductErrorURL(), pRequest, pResponse)){
        return false;
      }

      preAddProduct(pRequest, pResponse);

      addProduct(pRequest, pResponse);

      postAddProduct(pRequest, pResponse);
    }
    catch (Exception exc) {
      String errorCode = "errorAddingProduct";
      addFormException(new DropletException(getUserMessage(errorCode, pRequest), exc, errorCode));
    }

    return checkFormRedirect (getAddProductSuccessURL(), getAddProductErrorURL(), pRequest, pResponse);
  }
}
