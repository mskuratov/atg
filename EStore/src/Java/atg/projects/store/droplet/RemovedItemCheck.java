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

package atg.projects.store.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.adapter.gsa.GSAItem;
import atg.commerce.order.CommerceItemImpl;
import atg.commerce.order.OrderImpl;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * <p>
 *   This droplet takes an order, retrieves it's List of commerce items and 
 *   checks if any 'removed' items exist. If a removed item is found and the 
 *   <code>invalidateOrder</code> is set to <code>true</code>, the order will 
 *   be invalidated.
 * </p>
 * <p>
 *   Input Paramaters:
 *   <ul>
 *     <li>
 *       order - The order that will be checked for removed items.
 *     </li>
 *     <li>
 *       invalidateOrder (optional) - Flag (true/false) that indicates that the order 
 *                                    should be invalidated if a removed item is found.
 *     </li>
 *   </ul
 * </p>
 * <p>          
 *   Open Parameters:
 *   <ul>
 *     <li>
 *       true - Serviced when a removed item is found in the order.
 *     </li>
 *     <li>
 *       false - Serviced when a removed item is NOT found in the order.
 *     </li>
 *   </ul>  
 * </p>
 * <p>
 *   Example:
 *   <pre>
 *   &lt;dsp:droplet bean="/atg/store/droplet/RemovedItemsCheck"&gt; 
 *       &lt;dsp:param name="order" bean="ShoppingCart.current"&gt; 
 *       &lt;dsp:param name="invalidateOrder" value="true"&gt;
 *       
 *       &lt;dsp:oparam name="true"&gt;
 *           &lt;dsp:droplet name="RepriceOrderDroplet"&gt;
 *               &lt;dsp:param name="pricingOp" value="ORDER_SUBTOTAL"/&gt;
 *           &lt;/dsp:droplet&gt;
 *       &lt;/dsp:oparam&gt;  
 *   &lt;/dsp:droplet&gt;
 *   </pre>
 * </p>
 *       
 * @author David Stewart
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/RemovedItemCheck.java#2 $$Change: 791340 $
 * @updated $DateTime: 2013/02/19 09:03:40 $$Author: abakinou $
 *
 */
public class RemovedItemCheck extends DynamoServlet {

  //----------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/RemovedItemCheck.java#2 $$Change: 791340 $";

  //--------------------------------------------------------------------------
  //  CONSTANTS
  //--------------------------------------------------------------------------

  /** order parameter name. */
  public static final ParameterName ORDER = ParameterName.getParameterName("order");

  /** invalidateOrder parameter name. */
  public static final ParameterName INVALIDATE_ORDER = ParameterName.getParameterName("invalidateOrder");
  
  /** The oparam name rendered once if an item in the order is found to be removed.*/
  public static final ParameterName OPARAM_OUTPUT_TRUE = ParameterName.getParameterName("true");

  /** The oparam name rendered once if no items in the order are found to be removed.*/
  public static final ParameterName OPARAM_OUTPUT_FALSE = ParameterName.getParameterName("false");

  //--------------------------------------------------------------------------
  // METHODS
  //--------------------------------------------------------------------------

  //--------------------------------------------------------------------------
  /**
   * This method takes an order, retrieves it's List of commerce items and 
   * checks if any 'removed' items exist. If a removed item is found and the 
   * <code>invalidateOrder</code> is set to <code>true</code>, the order will 
   * be invalidated.
   *
   * @param pRequest - DynamoHttpSevletRequest.
   * @param pResponse - DynamoHttpServletResponse.
   * @throws ServletException - if an error occurs.
   * @throws IOException - if an error occurs.
   */
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    OrderImpl order = (OrderImpl) pRequest.getObjectParameter(ORDER);
    
    @SuppressWarnings("unchecked")
    List<CommerceItemImpl> items = order.getCommerceItems();
    
    boolean removedItemFound = false;
    
    if (items != null) {  
      for (CommerceItemImpl item : items) {
        GSAItem gsaItem = (GSAItem) item.getRepositoryItem();
  
        if (gsaItem.isRemoved()) {
          vlogDebug("Found {0} to be a removed item.", item.getId());
          
          if (order != null && order instanceof OrderImpl) {
            
            String invalidateOrder = (String) pRequest.getObjectParameter(INVALIDATE_ORDER);
            
            if (invalidateOrder.equals("true")) {
              if (isLoggingDebug()) {
                vlogDebug("Invalidating order {0} as it contains removed item(s).", order.getId());
              }
              ((OrderImpl)order).invalidateOrder();
            }
            
            removedItemFound = true;
            break;
          }
        }
      }
    }
    
    if (removedItemFound) { 
      pRequest.serviceLocalParameter(OPARAM_OUTPUT_TRUE, pRequest, pResponse);
    }
    else {
      pRequest.serviceLocalParameter(OPARAM_OUTPUT_FALSE, pRequest, pResponse);
    }
  }
}
