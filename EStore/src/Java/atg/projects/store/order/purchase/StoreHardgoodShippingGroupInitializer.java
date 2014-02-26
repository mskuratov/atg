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

import java.util.Map;
import java.util.Set;

import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.HardgoodShippingGroupInitializer;
import atg.commerce.order.purchase.ShippingGroupMapContainer;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.Address;
import atg.projects.store.gifts.StoreGiftlistManager;
import atg.projects.store.profile.StoreAddressTools;
import atg.servlet.ServletUtil;

/**
 * This class performs specific to CRS actions with {@link HardgoodShippingGroup} 
 * in addition to those which are provided in parent class. 
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreHardgoodShippingGroupInitializer.java#4 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreHardgoodShippingGroupInitializer extends HardgoodShippingGroupInitializer {

  //-------------------------------------
  // Class version string
  //-------------------------------------
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreHardgoodShippingGroupInitializer.java#4 $$Change: 788278 $";

  private static final String MY_RESOURCE_NAME = "atg.commerce.gifts.GiftlistResources";

  // ----------------------------
  /**
   * <p>
   *   This method contains some specific logic related to gift list ShippingGroups.
   * </p>
   * <br />
   * <p>
   *   In case if the shipping group description contains 
   *   {@link StoreGiftlistManager#getGiftShippingGroupDescriptionPrefix()} we extract the gift list 
   *   event name, which is located after the prefix, and use it in creation of shippingGroupName.
   * </p>
   * 
   * @param pShippingGroup ShippingGroup for which a name will be created.
   * 
   * @return name of the ShippingGroup
   * */
  public String getNewShippingGroupName(ShippingGroup pShippingGroup) {
    String newGiftShippingGroupName = null;

    if (!(pShippingGroup instanceof HardgoodShippingGroup)) {
      return null;
    } 
    else {
      String description = pShippingGroup.getDescription();
      StoreGiftlistManager giftlistManager = (StoreGiftlistManager) getConfiguration().getGiftlistManager();

      if (description != null && 
          description.startsWith(giftlistManager.getGiftShippingGroupDescriptionPrefix())) {
        
        // Create shippingGroupName in the following way:
        
        // 1. Use the localized value for "Gift".
        String giftShippingGroupNamePrefix = 
          LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, 
                                          ServletUtil.getUserLocale()).getString("giftShippingGroupNamePrefix");
        // 2. Use gift list event Name.
        String eventName = description.replace(giftlistManager.getGiftShippingGroupDescriptionPrefix(), "");

        newGiftShippingGroupName = giftShippingGroupNamePrefix + " " + eventName;
      }
    }

    return getShippingGroupManager().getOrderTools().getProfileTools().getUniqueAddressNickname(
      ((HardgoodShippingGroup) pShippingGroup).getShippingAddress(),
      getShippingGroupMapContainer().getShippingGroupNames(), 
      newGiftShippingGroupName);
  }

  /**
   * Check address in the additional map of addresses that haven't stored in profile.
   * 
   * @param pShippingGroup a ShippingGroup value.
   * @param pShippingGroupMapContainer a ShippingGroupMapContainer value.
   * 
   * @return String the name of the entry in the ShippingGroupMapContainer that matches the shipping group.
   */
  public String matchShippingGroup(ShippingGroup pShippingGroup,
      ShippingGroupMapContainer pShippingGroupMapContainer) {
    
    String shippingGroupName = super.matchShippingGroup(pShippingGroup, pShippingGroupMapContainer);
    
    // If address for shipping group not found in ShippingGroupMapContainer,
    // look for map of addresses not stored in profile.
    if(shippingGroupName == null) {
      if (!(pShippingGroup instanceof HardgoodShippingGroup)){
        return null;
      }
      
      try {
        StoreShippingGroupContainerService storeShippingGroupContainerService = 
          (StoreShippingGroupContainerService) pShippingGroupMapContainer;
        
        Map<String, Address> addressesMap = 
          storeShippingGroupContainerService.getNonProfileShippingAddressesMap();

        Address address = ((HardgoodShippingGroup) pShippingGroup).getShippingAddress();
        
        for(Map.Entry<String, Address> entry : addressesMap.entrySet()){
          if(StoreAddressTools.compare(address, entry.getValue())) {
            shippingGroupName = entry.getKey();
            break;
          }
        }
      }
      catch (ClassCastException cce){
        if (isLoggingError()) {
          logError("pShippingGroupMapContainer can't be cast to StoreShippingGroupContainerService", cce);
        }        
      }     
    }    
    return shippingGroupName;
  }
  
  //-------------------------------------
  /** 
   * Compare Addresses.
   * 
   * @param pAddressA the first address to compare.
   * @param pAddressB the second address to compare.
   * 
   * @return true if each field in the addresses are the same or if both addresses are null.
   */
  protected boolean compareAddresses(Address pAddressA, Address pAddressB) {    
    return StoreAddressTools.compare(pAddressA, pAddressB);
  }
}
