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

import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;

import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;

import atg.nucleus.logging.ApplicationLoggingImpl;

import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.GiftWrapCommerceItem;

import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;


/**
 * This processor validates the order has more than just gift cards.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/processor/ProcValidateGiftCardForCheckout.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class ProcValidateGiftCardForCheckout extends ApplicationLoggingImpl implements PipelineProcessor {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/processor/ProcValidateGiftCardForCheckout.java#3 $$Change: 788278 $";

  /**
   * Resource bundle name.
   */
  protected static final String RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /**
   * Resource bundle name for user messages.
   */
  protected static final String USER_MSGS_RES_NAME = "atg.commerce.order.UserMessages";

  //-------------------------------------
  // Constants
  //-------------------------------------
 
  // Resource message keys
  public static final String MSG_INVALID_ORDER_PARAMETER = "InvalidOrderParameter";

  /**
   * Resource bundle.
   */
  protected static ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle(RESOURCE_NAME,
      atg.service.dynamo.LangLicense.getLicensedDefault());

  /**
   * User resource bundle.
   */
  protected static ResourceBundle sUserResourceBundle = 
    LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, java.util.Locale.getDefault());

  /**
   * Gift cards only error resource key.
   */
  protected static final String GIFT_CARDS_ONLY_ERROR = "giftCardsOnlyError";

  /**
   * Success constant.
   */
  protected final static int SUCCESS = 1;

  //-------------------------------------
  // Methods
  //-------------------------------------
  
  /**
   * This method does the work of this processor Make sure the order has more
   * than just gift cards in it.
   *
   * @param pParam - Pipeline params.
   * @param pResult - Pipeline result.
   * 
   * @return int (SUCCESS or STOP_CHAIN_EXECUTION_AND_ROLLBACK).
   * 
   * @throws InvalidParameterException if an invalid argument is passed into a method call.
   */
  @Override
  public int runProcess(Object pParam, PipelineResult pResult) throws InvalidParameterException {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);    

    // Check for null parameters.
    if (order == null) {
      throw new InvalidParameterException(ResourceUtils.getMsgResource(MSG_INVALID_ORDER_PARAMETER, 
                                                                       RESOURCE_NAME,
                                                                       sResourceBundle));
    }

    if (!(order instanceof StoreOrderImpl)) {
      return SUCCESS;
    }

    List items = order.getCommerceItems();
   
    if (validateItemsInOrder(items)) {
      return SUCCESS;
    }

    // After looping through all commerce items, we didn't find any that weren't samples. Add error.
    String msg = sUserResourceBundle.getString(GIFT_CARDS_ONLY_ERROR);
    pResult.addError(GIFT_CARDS_ONLY_ERROR, msg);

    return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
  }

  /**
   * This is a utility method that takes a list of CommerceItems and checks to see if 
   * there is a non-GiftWrapCommerceItem in the list.
   *
   * @param pItems is a list of CommerceItems.
   * 
   * @return true if list contains a non-GiftWrapCommerceItem.
   */
  public boolean validateItemsInOrder(List pItems) {
    CommerceItem item = null;

    for (int i = 0; i < pItems.size(); i++) {
      item = (CommerceItem) pItems.get(i);

      if (!(item instanceof GiftWrapCommerceItem)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns the valid return codes 1 - The processor completed.
   *
   * @return an integer array of the valid return codes.
   */
  @Override
  public int[] getRetCodes() {
    int[] ret = { SUCCESS };

    return ret;
  }
}
