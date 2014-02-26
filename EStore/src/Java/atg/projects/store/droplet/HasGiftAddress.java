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

import javax.servlet.ServletException;

import atg.commerce.order.ShippingGroup;
import atg.nucleus.naming.ParameterName;
import atg.projects.store.gifts.StoreGiftlistManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * This droplet checks if passed in shipping group has gift address.
 * 
 * This droplet takes the following parameters:
 *   shippingGroup
 *     The shipping group to check
 *     
 * This droplet renders the following oparams:
 *   true
 *     Is rendered if shipping group contains
 *     gift address
 *   false
 *     Is rendered if shipping group contains not
 *     a gift address
 * 
 * @author Yekaterina Kostenevich
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/HasGiftAddress.java#2 $Change: 630322 $
 * @updated $DateTime: 2013/02/19 09:03:40 $Author: ykostene $
 *
 */
public class HasGiftAddress extends DynamoServlet{
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/HasGiftAddress.java#2 $Change: 630322 $";
  
  /** The input parameter name for the shipping group to check. */
  public static final ParameterName PARAM_SHIPPING_GROUP = ParameterName.getParameterName("shippingGroup");

  /** The oparam name rendered once if shipping group contains gift address*/
  public static final ParameterName OPARAM_OUTPUT_TRUE = ParameterName.getParameterName("true");

  /** The oparam name rendered once if shipping group contains not a gift address*/
  public static final ParameterName OPARAM_OUTPUT_FALSE = ParameterName.getParameterName("false");
  
  /**
   * Gift list manager
   */
  private StoreGiftlistManager mGiftlistManager;
  
  /** @return The GiftlistManager component */
  public StoreGiftlistManager getGiftlistManager() {
    return mGiftlistManager;
  }
  /** @param Set a new GiftlistManager */
  public void setGiftlistManager(StoreGiftlistManager pGiftlistManager) {
    mGiftlistManager = pGiftlistManager;
  } 
  
  
  /**
   * Checks if given shipping group has gift address and 
   * renders appropriate oparam (true or false).
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Object sg = pRequest.getObjectParameter(PARAM_SHIPPING_GROUP);

    if ((sg == null) || !(sg instanceof ShippingGroup)) {
      if (isLoggingDebug()) {
        logDebug("INVALID PARAM: invalid or no shipping group supplied");
      }

      return;
    }

    if (getGiftlistManager().hasGiftAddress((ShippingGroup)sg)) {
      pRequest.serviceLocalParameter(OPARAM_OUTPUT_TRUE, pRequest, pResponse);
    } else {
      pRequest.serviceLocalParameter(OPARAM_OUTPUT_FALSE, pRequest, pResponse);
    }
  } 
  

}
