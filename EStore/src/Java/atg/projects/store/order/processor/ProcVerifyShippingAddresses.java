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


package atg.projects.store.order.processor;

import atg.commerce.CommerceException;

import atg.commerce.order.*;
import atg.commerce.order.processor.ProcVerifyOrderAddresses;

import atg.core.util.Address;
import atg.core.util.ResourceUtils;

import atg.service.dynamo.LangLicense;
import atg.service.pipeline.PipelineResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * This class extends ProcVerifyOrderAddresses to verify only shipping-group addresses.  This
 * is so shipping address verification can take place immediately after the user specifies the
 * shipping addresses, eliminating the UI awkwardness that takes place when a user is informed
 * of an invalid shipping address after specifying billing information.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/processor/ProcVerifyShippingAddresses.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class ProcVerifyShippingAddresses extends ProcVerifyOrderAddresses {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/processor/ProcVerifyShippingAddresses.java#3 $$Change: 788278 $";

  /**
   * Resource bundle name.
   */
  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  //-------------------------------------
  // Constants
  //-------------------------------------
  // Resource message keys
  public static final String MSG_INVALID_ORDER_PARAMETER = "InvalidOrderParameter";
  public static final String MSG_INVALID_ORDER_MANAGER_PARAMETER = "InvalidOrderManagerParameter";
  public static final String MSG_INVALID_ADDRESS = "InvalidAddress";

  /** Resource Bundle. **/
  private static java.util.ResourceBundle sResourceBundle = 
      atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, LangLicense.getLicensedDefault());

  /**
   * Success constant.
   */
  private final static int SUCCESS = 1; 

  /**
   * <p>
   *   This method executes the address verification. It searches through the
   *   ShippingGroup and PaymentGroup lists in the Order for HardgoodShippingGroup
   *   and CreditCard objects. It then gets the address from them and calls
   *   verifyAddress().
   * </p>
   * <p>
   *   This method requires that an Order and an OrderManager object be supplied
   *   in pParam in a HashMap. Use the PipelineConstants class' static members to key
   *   the objects in the HashMap.
   * </p>
   * 
   * @param pParam a HashMap which must contain an Order and OrderManager object.
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invocation.
   *                
   * @return an integer specifying the processor's return code.
   * 
   * @throws CommerceException indicates that a severe error occurred while performing a commerce operation.
   * 
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult).
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws CommerceException {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    OrderManager orderManager = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);

    // Check for null parameters.
    if (order == null) {
      throw new InvalidParameterException(ResourceUtils.getMsgResource(MSG_INVALID_ORDER_PARAMETER, 
                                                                       MY_RESOURCE_NAME,
                                                                       sResourceBundle));
    }

    if (orderManager == null) {
      throw new InvalidParameterException(ResourceUtils.getMsgResource(MSG_INVALID_ORDER_MANAGER_PARAMETER,
                                                                       MY_RESOURCE_NAME, 
                                                                       sResourceBundle));
    }

    List shippingGroups = order.getShippingGroups();
    int sgcount = shippingGroups.size();
    List hardgoodSGList = new ArrayList(sgcount);
    HardgoodShippingGroup hgsg;
    Address addr1;
    Address addr2;
    String addrId;
    Object o;

    // Find all the HardgoodShippingGroup objects.
    sgcount = 0;

    Iterator iter = shippingGroups.iterator();

    while (iter.hasNext()) {
      o = iter.next();

      if (o instanceof HardgoodShippingGroup) {
        // Skip empty shipping groups.
        if (((HardgoodShippingGroup)o).getCommerceItemRelationshipCount() == 0){
          continue;
        }
        
        hardgoodSGList.add(o);
        sgcount++;
      }
    }

    for (int i = 0; i < sgcount; i++) {
      hgsg = (HardgoodShippingGroup) hardgoodSGList.get(i);

      if (hgsg != null) {
        addr1 = hgsg.getShippingAddress();
        addr2 = null;
        addrId = hgsg.getId();
      } 
      else { // Should never happen.
        throw new CommerceException(ResourceUtils.getMsgResource(MSG_INVALID_ADDRESS, 
                                                                 MY_RESOURCE_NAME, 
                                                                 sResourceBundle));
      }

      verifyAddress(addrId, addr1, addr2, pResult, orderManager);
    }

    if (pResult.hasErrors()) {
      return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
    }

    return SUCCESS;
  }
}
