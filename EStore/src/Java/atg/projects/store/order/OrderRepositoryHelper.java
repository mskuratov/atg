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

import atg.commerce.order.OrderManager;

import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import atg.repository.rql.RqlStatement;


/**
 * This class is only used during development. It contains no-arg void methods to clear orders 
 * from the order repository. These methods can be called from the component browser of the
 * component. Unless you want to wipe all the orders from the database, do not use this in any 
 * environment other than development.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/OrderRepositoryHelper.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class OrderRepositoryHelper extends ApplicationLoggingImpl {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/OrderRepositoryHelper.java#3 $$Change: 788278 $";

  /**
   * property: orderManager
   */
  private OrderManager mOrderManager;

  /**
   * @return order manager.
   */
  public OrderManager getOrderManager() {
    return mOrderManager;
  }

  /**
   * @param pManager - order manager.
   */
  public void setOrderManager(OrderManager pManager) {
    mOrderManager = pManager;
  }
  
  /**
   * Method to remove orders from the repository.
   * 
   * @param pOrderType - order type
   */
  public void deleteOrders(String pOrderType) {
    String orderType = pOrderType;
    
    if (orderType == null) {
      orderType = "order";
    }

    try {
      Repository repository = getOrderManager().getOrderTools().getOrderRepository();
      String itemDescriptor = orderType;
      RepositoryView view = repository.getView(itemDescriptor);

      Object[] params = {  };
      RqlStatement statement = RqlStatement.parseRqlStatement("ALL");

      RepositoryItem[] items = statement.executeQuery(view, params);

      for (int i = 0; i < items.length; i++) {
        getOrderManager().removeOrder(items[i].getRepositoryId());
      }
    } 
    catch (Exception ex) {
      if (isLoggingError()){
        logError("Can't delete oders ", ex);
      }
    }
  }

  /**
   * Delete all orders.
   */
  public void deleteAllOrders() {
    deleteOrders("order");
  }

}
