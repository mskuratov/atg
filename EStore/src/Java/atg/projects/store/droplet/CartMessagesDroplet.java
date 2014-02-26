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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletException;

import atg.commerce.order.OrderHolder;
import atg.commerce.promotion.PromotionConstants;
import atg.commerce.promotion.PromotionTools;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.web.messaging.MessageConstants;
import atg.web.messaging.UserMessage;

/**
 * This droplet returns messages to be displayed in the rich cart. Currently,
 * it will return only the one message if the cart (current order) has
 * active promotions.
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/CartMessagesDroplet.java#2 $$Change: 791340 $
 * @updated $DateTime: 2013/02/19 09:03:40 $$Author: abakinou $
 */
public class CartMessagesDroplet extends DynamoServlet {
  
  //-----------------------------------
  // STATIC
  //-----------------------------------
  
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/CartMessagesDroplet.java#2 $$Change: 791340 $";
    
  protected static final String OPEN_PARAMETER_OUTPUT = "output";
  protected static final String EMPTY_PARAMETER_OUTPUT = "empty";
  protected static final String MESSAGE_PARAMETER_OUTPUT = "message";
  
  protected static final String ACTIVE_PROMOTION_MESSAGE = "orderHasActivePromotions";
  protected static final String ACTIVE_PROMOTION_ID = "ActivePromotion";
  
  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // property: promotionTools  
  private PromotionTools mPromotionTools;
  
  public PromotionTools getPromotionTools() {
    return mPromotionTools;
  }

  public void setPromotionTools(PromotionTools pPromotionTools) {
    mPromotionTools = pPromotionTools;
  }

  //-------------------------------------
  // property: shoppingCart  
  private OrderHolder mShoppingCart;

  /**
   * @return The session scoped shopping cart component.
   */
  public OrderHolder getShoppingCart() {
    return mShoppingCart;
  }

  /**
   * @param pShoppingCart Set a new shopping cart.
   */
  public void setShoppingCart(OrderHolder pShoppingCart) {
    mShoppingCart = pShoppingCart;
  }
  
  //-----------------------------------
  // METHODS
  //-----------------------------------

  /**
   * Determine if the order has any active promotions and if yes,
   * return message.  
   */
  public void service(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException 
  {
    // Get a collection of the promotions available for the order
    Collection orderPromotions = new ArrayList();
    getPromotionTools().getOrderPromotions(getShoppingCart().getCurrent(), orderPromotions);
    
    // Order has at least one active promotion, service it 
    if(orderPromotions.size() > 0) {
      UserMessage message = new UserMessage();
      
      message.setIdentifier(ACTIVE_PROMOTION_ID);
      message.setPriority(MessageConstants.PRIORITY_INFORMATION);
      message.setType(MessageConstants.TYPE_INFORMATION);
      message.setSummary(PromotionConstants.getStringResource(ACTIVE_PROMOTION_MESSAGE, getLocale(pRequest, pResponse)));
      
      pRequest.setParameter(MESSAGE_PARAMETER_OUTPUT, message);
      pRequest.serviceLocalParameter(OPEN_PARAMETER_OUTPUT, pRequest, pResponse);
    } else {
      pRequest.serviceLocalParameter(EMPTY_PARAMETER_OUTPUT, pRequest, pResponse);
    }
  }
  
  /**
   * Returns either the Locale from the Request object (if it isn't NULL),
   * or the Locale from the JVM.
   * 
   * @param pRequest the servlet's request
   * @return Either the Locale from the Request object, or the Locale 
   * from the JVM.
   */
  protected Locale getLocale (DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse) 
  {
    if (pRequest.getRequestLocale() != null) {
      return pRequest.getRequestLocale().getLocale();
    }
    else {
      return Locale.getDefault();
    }
  }
}
