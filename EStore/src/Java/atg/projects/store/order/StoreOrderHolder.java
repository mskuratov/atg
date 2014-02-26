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

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

/**
 * This is an EStore's extension to the <code>ShoppingCart</code> component.
 * This extension can set a copy of some order as current, instead of creating a new empty order.
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/StoreOrderHolder.java#3 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreOrderHolder extends OrderHolder {
  
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/StoreOrderHolder.java#3 $$Change: 788278 $";
  
  /**
   * This constant specifies a request parameter name, parameter with this name 
   * contains an order ID to be cloned.
   */
  public static final String LOGOUT_ORDER_ID = "checkoutOrderId";

  /**
   * This method is a shortcut to the {@link ServletUtil#getCurrentRequest()} method.
   * 
   * @return current HTTP request.
   */
  public DynamoHttpServletRequest getCurrentRequest() {
    return ServletUtil.getCurrentRequest();
  }

  /**
   * This method returns a current order (or shopping cart).
   * <br/>
   * If there is a shopping cart loaded already, it will be returned. If no shopping cart yet 
   * loaded and there is an order ID specified with the {@link #LOGOUT_ORDER_ID} request property,
   * there will be created a copy of the order specified and set as shopping cart.
   * <br/>
   * If no order ID specified or unable to create a copy, some saved order will become a shopping cart. 
   * If the system can't find appropriate order, it will create a new instance.
   * 
   * @see OrderHolder#getCurrent()
   * 
   * @return current shopping cart's <code>Order</code> instance.
   */
  @Override
  public synchronized Order getCurrent() {
    // No shopping cart loaded yet? Is there a proper request parameter?
    if (getCurrent(false) == null && getCurrentRequest().getQueryParameter(LOGOUT_ORDER_ID) != null) {
      try {
        
        // If true, clone the order specified and set is as current.
        Order order = ((StoreOrderManager) getOrderManager()).cloneOrder(
          getCurrentRequest().getQueryParameter(LOGOUT_ORDER_ID), getProfile());
        
        setCurrent(order);
      } 
      catch (CommerceException e) {
        if (isLoggingError()) {
          logError("Unable to create the order: ", e);
        }
      }
    }
    
    // Can't clone order or there is nothing to clone, call Commerce's logic.
    return super.getCurrent();
  }
}
