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



package atg.projects.store.order.purchase;

import atg.commerce.CommerceException;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderTools;
import atg.commerce.order.ShippingGroupManager;
import atg.commerce.profile.CommerceProfileTools;
import atg.core.util.Address;
import atg.repository.RepositoryItem;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

/**
 * Extends the StorePurchaseProcessHelper to implement express checkout processing methods.
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreExpressCheckoutProcessHelper.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreExpressCheckoutProcessHelper extends StorePurchaseProcessHelper {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreExpressCheckoutProcessHelper.java#3 $$Change: 788278 $";


  /**
   * Ensure that any gift shipping groups have their shipping method set to the default shipping method.
   * 
   * @param pOrder - The current order, an <code>Order</code> value.
   * @param pProfile - A profile, a <code>RepositoryItem</code> value.
   * @param pShippingMethodPropertyName - The name of the shipping method property, a <code>String</code> value.
   * 
   * @throws IOException - if an error occurs.
   * @throws ServletException - if an error occurs.
   */
  protected void ensureShippingMethodOfGiftShippingGroups(Order pOrder, 
                                                          RepositoryItem pProfile, 
                                                          String pShippingMethodPropertyName)
    throws IOException, ServletException {

    List giftShippingGroups = ((ShippingGroupManager) getShippingGroupManager()).getGiftShippingGroups(pOrder);

    if ((giftShippingGroups != null) && (giftShippingGroups.size() > 0)) {
      
      Iterator shippingGrouperator = giftShippingGroups.iterator();
      HardgoodShippingGroup shippingGroup;
      String defaultShippingMethod = (String) pProfile.getPropertyValue(pShippingMethodPropertyName);
      CommerceProfileTools profileTools = getStoreOrderTools().getProfileTools();
    
      while (shippingGrouperator.hasNext()) {
        shippingGroup = (HardgoodShippingGroup) shippingGrouperator.next();
        
        // Set the default shipping method.
        shippingGroup.setShippingMethod(defaultShippingMethod);
        Address shippingAddress = shippingGroup.getShippingAddress();
        
        // For wish lists we need to set shipping address to profile's default address
        // as no address is specified in wish list itself.
        if (profileTools.isAddressEmpty(shippingGroup.getShippingAddress())) {
          RepositoryItem defaultAddress = profileTools.getDefaultShippingAddress(pProfile);
          
          if (shippingAddress == null) {
            shippingAddress = new Address();
            shippingGroup.setShippingAddress(shippingAddress);
          }
          try {
            OrderTools.copyAddress(defaultAddress, shippingAddress);
          } 
          catch( CommerceException ce ) {
            logError(ce);
          } 
        } 
      }
    }
  }
  
}
