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
package atg.projects.store.collections.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import atg.commerce.catalog.CatalogTools;
import atg.commerce.order.AuxiliaryData;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.nucleus.GenericService;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.collections.validator.CollectionObjectValidator;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

/**
 * This validator generates filtered results based on the products in the current
 * order. All products in the unfiltered collection that are in the current order are
 * excluded from the filtered results.
 * 
 * This validator is useful for filtering upsell products or related products etc. to
 * prevent promoting a product that is already in the cart.
 *
 * @author Yekaterina Kostenevich
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/collections/validator/ExcludeItemsInCartValidator.java#2 $Change: 630322 $
 * @updated $DateTime: 2013/02/19 09:03:40 $Author: ykostene $
 *
 */
public class ExcludeItemsInCartValidator extends GenericService implements CollectionObjectValidator {
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/collections/validator/ExcludeItemsInCartValidator.java#2 $Change: 630322 $";

  /**
   * property: shoppingCartPath
   */
  protected String mShoppingCartPath="/atg/commerce/ShoppingCart";

  /**
   * @param pShoppingCartPath the shopping cart nucleus path to set
   **/
  public void setShoppingCartPath(String pShoppingCartPath) {
    mShoppingCartPath = pShoppingCartPath;
  }

  /**
   * @return the shopping cart nucleus path 
   **/
  public String getShoppingCartPath() {
    return mShoppingCartPath;
  }

  /**
   * property: catalogTools
   */
  protected CatalogTools mCatalogTools;

  /**
   * @param pCatalogTools the CatalogTools to set
   */
  public void setCatalogTools(CatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }
  /**
  * @return the CatalogTools
  */
  public CatalogTools getCatalogTools() {
    return mCatalogTools;
  }
  
  /**
   * This method validates the passed in product based on the products in the current order. 
   * @param pObject product to validate
   * @return true if pObject passes validation or if no validation was carried out, otherwise false.
   */
  @Override
  public boolean validateObject(Object pObject) {
    if (!(pObject instanceof RepositoryItem) ) {
      return false;
    }
        
    // If null order, return true as no validation is needed.
    Order currentOrder = findOrder();
    if (currentOrder == null) {
      return true;
    }

    // If no products in the order, return true as no validation will be done.
    Collection currentOrderProductIds = createProductIdCollection(currentOrder);
    if(currentOrderProductIds.size() == 0) {
      return true;
    }

    if(isLoggingDebug()) {
      logDebug("validateObject: products in cart are: " + currentOrderProductIds);
    }

    try {
      CatalogTools catalogTools = getCatalogTools();
      // Check the price on each products sku 
      
      if(!catalogTools.isProduct(pObject)) {
          // Item is not a product no validation will be done.           
          return true;
        }
        RepositoryItem product = (RepositoryItem) pObject;

        if(isLoggingDebug()) {
         logDebug("validateObject: product is " + product);
        }

        // If product is not in the cart it pass validation
        if(!currentOrderProductIds.contains(product.getRepositoryId())) {
          return true;
        }
    }//end try
    catch(RepositoryException exc) {
      if (isLoggingError()) {
        logError("validateObject: Exception occurs while checking that pObject is a product", exc);
      }
      // No validation was performed
      return true;
    }
  
    // The product doesn't pass validation
    return false;
  }
  
  /**
   * Finds the current order using the nucleus specified by
   * the <i>shoppingCartPath</i> property.
   * <p>
   * @return the order
   */
  public Order findOrder() {
    Order order = null;
    DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
    if(request != null) {
      OrderHolder oh = (OrderHolder) request.resolveName(getShoppingCartPath());
      if(oh != null) {
        order = oh.getCurrent();
      }
    }
    return order;
  }
   
  /**
   * Check if validator should be applied. Return false if the order does not have commerce items in it.
   * @return true if validator should be applied.
   */
  public boolean shouldApplyValidator(){
    Order order = findOrder();
    if(order == null) {
      return false;
    }

    if(order.getCommerceItemCount() == 0) {
      return false;
    }

    return true;
  }
   
  /**
   * This method returns the product id associated with a commerce item.  
   * @param pCommerceItem the commerce item
   * @return the product id. Null is returned if the commerce item does not
   * reference a product id in the auxiliary data.
   */
  protected String findCommerceItemProductId(CommerceItem pCommerceItem){
    String productId = null;
    //Get the aux data and check for product info
    AuxiliaryData auxData = pCommerceItem.getAuxiliaryData();
    if(auxData != null) {
      productId = auxData.getProductId();
    }

    return productId;
  }
   
  /**
   * This method creates a collection of product ids. A product id
   * is included for each product commerce item in the order.
   * @param pOrder the order
   * @return a collection of product ids.  
   */
   protected Collection createProductIdCollection(Order pOrder) {
    if(pOrder == null) {
      return null;
    }

    List commerceItems = pOrder.getCommerceItems();
    if(commerceItems != null && commerceItems.size() > 0) {
      Collection productIds = new ArrayList(pOrder.getCommerceItemCount());
      Iterator commerceItemIterator = commerceItems.iterator();
      while(commerceItemIterator.hasNext()) {
        CommerceItem commerceItem = (CommerceItem) commerceItemIterator.next();

        String productId = findCommerceItemProductId(commerceItem);
        if(productId != null) {
          productIds.add(productId);
        }
      }
      return productIds;
    }
    else {
      return new ArrayList();
    }
  }

}
