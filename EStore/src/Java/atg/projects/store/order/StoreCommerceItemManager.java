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

package atg.projects.store.order;

import java.util.List;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;

/**
 * CRS extension to the DCS {@link CommerceItemManager} component. This implementation adds new useful methods.
 * 
 * @see CommerceItemManager
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/StoreCommerceItemManager.java#3 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreCommerceItemManager extends CommerceItemManager {
  
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/StoreCommerceItemManager.java#3 $$Change: 788278 $";
  
  /**
   * This method searches for a {@code CommerceItem} within the {@code Order} specified.
   * It searches for the item with the same <code>skuId</code>, <code>productId</code> and <code>siteId</code>.
   * 
   * @param pOrder - order to be examined.
   * @param pSkuId - <code>catalogRefId</code> parameter value.
   * @param pProductId - <code>auxiliaryData.productId</code> parameter value.
   * @param pSiteId - <code>auxiliaryData.siteId</code> parameter value.
   * 
   * @return {@code CommerceItem}, or {@code null} if nothing found.
   */
  @SuppressWarnings("unchecked")
  public CommerceItem getCommerceItem(Order pOrder, String pSkuId, String pProductId, String pSiteId) {
    try {
      
      List<CommerceItem> filteredItems = pOrder.getCommerceItemsByCatalogRefId(pSkuId);
      
      for (CommerceItem item: filteredItems) {
        if (item.getAuxiliaryData().getProductId().equals(pProductId) && 
            item.getAuxiliaryData().getSiteId().equals(pSiteId)) {
          return item;
        }
      }
    } 
    catch (CommerceItemNotFoundException e) {
      // Can't find items with catalogRefId specified, just return nothing at the end of the method.
    } 
    catch (InvalidParameterException e) {
      // Can't be caught, because this means that pSkuId is null.
    }
    
    return null;
  }
}
