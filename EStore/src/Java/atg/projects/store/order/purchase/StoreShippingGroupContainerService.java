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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.purchase.ShippingGroupContainerService;
import atg.core.util.Address;
import atg.projects.store.gifts.StoreGiftlistManager;

/**
 * An override of the DCS class ShippingGroupContainerService to add customizations such as 
 * using the shippingGroupMap property to display addresses on the UI.
 *
 * @author ckearney
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreShippingGroupContainerService.java#4 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreShippingGroupContainerService extends ShippingGroupContainerService{

  //-----------------------------------
  // STATIC
  //-----------------------------------
  private static final long serialVersionUID = 1L;
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreShippingGroupContainerService.java#4 $$Change: 788278 $";

  //-----------------------------------
  // PROPERTIES
  //-----------------------------------
  
  /**
   * property: NonProfileShippingAddressesMap
   */
  Map<String, Address> mNonProfileShippingAddressesMap = new HashMap<String, Address>();
  
  /**
   * @return the nonProfileShippingAddressesMap.
   */
  public Map<String, Address> getNonProfileShippingAddressesMap() {
    return mNonProfileShippingAddressesMap;
  }

  /**
   * @param pNonProfileShippingAddressesMap - the nonProfileShippingAddressesMap to set.
   */
  public void setNonProfileShippingAddressesMap(
      Map<String, Address> pNonProfileShippingAddressesMap) {
    mNonProfileShippingAddressesMap = pNonProfileShippingAddressesMap;
  }
  
  
  /**
   * property: firstNonGiftShippingGroupName
   */
  private String mFirstNonGiftShippingGroupName;
  
  /** 
   * @return The first non gift list shipping group nickname.
   */
  public String getFirstNonGiftShippingGroupName() {
    initalizeFirstNonGiftShippingGroupName();
    return mFirstNonGiftShippingGroupName;
  }

  /** 
   * @param pFirstNonGiftShippingGroupName - set a new first non gift-list shipping group name.
   */
  public void setFirstNonGiftShippingGroupName(String pFirstNonGiftShippingGroupName) {
    mFirstNonGiftShippingGroupName = pFirstNonGiftShippingGroupName;
  }
  
  /**
   * property: giftlistManager
   */
  private StoreGiftlistManager mGiftlistManager;
  
  /** 
   * @return The giftlistManager component.
   */
  public StoreGiftlistManager getGiftlistManager() {
    return mGiftlistManager;
  }

  /** 
   * @param pGiftlistManager - set a new giftlistManager component.
   */
  public void setGiftlistManager(StoreGiftlistManager pGiftlistManager) {
    mGiftlistManager = pGiftlistManager;
  }
  
  /**
   * property: nonDisplayableShippingGroups
   */
  private List<String> mNonDisplayableShippingGroups;
  
  /** 
   * @return A List of shipping group names whose shipping groups shouldn't be displayed. 
   */
  public List<String> getNonDisplayableShippingGroups() {
    return mNonDisplayableShippingGroups;
  }

  /** 
   * @param pNonDisplayableShippingGroups - set a new list of non-displayable shipping groups.
   */
  public void setNonDisplayableShippingGroups(List<String> pNonDisplayableShippingGroups) {
    mNonDisplayableShippingGroups = pNonDisplayableShippingGroups;
  }
  
  /**
   * property: shippingGroupMapForDisplay
   */
  private Map mShippingGroupMapForDisplay;

  /** 
   * @return A shipping group map with non-displayable addresses removed.
   */
  public Map getShippingGroupMapForDisplay() {
    initalizeShippingGroupMapForDisplay();
    return mShippingGroupMapForDisplay;
  }

  /** 
   * @param pShippingGroupMapForDisplay - set a new shippingGroupMapForDisplay 
   */
  public void setShippingGroupMapForDisplay(Map pShippingGroupMapForDisplay) {
    mShippingGroupMapForDisplay = pShippingGroupMapForDisplay;
  }

  //-----------------------------------
  // PROTECTED
  //-----------------------------------
  
  /**
   * Initalizes the shippingGroupMapForDisplay property from contents of the shippingGroupMap.
   */
  protected void initalizeShippingGroupMapForDisplay() {
    
    if (mShippingGroupMapForDisplay != null) {
      mShippingGroupMapForDisplay.clear();
    }
    
    Map sgMap = getShippingGroupMap();
    List<String> nonDisplayableShippingGroups = getNonDisplayableShippingGroups();
    
    mShippingGroupMapForDisplay = new HashMap(sgMap);
    
    if(nonDisplayableShippingGroups != null && nonDisplayableShippingGroups.size() > 0) {
      
      // Remove the entries we don't wish to display from the return map.
      for(String nonDisplayableSg : nonDisplayableShippingGroups) {
        
        if(mShippingGroupMapForDisplay.containsKey(nonDisplayableSg)) {
          mShippingGroupMapForDisplay.remove(nonDisplayableSg);
        }
      }
    }
    
  }

  /**
   * Sets the shipping group name belonging to the first non-gift hardgood shipping group 
   * in the shipping group map.
   */
  protected void initalizeFirstNonGiftShippingGroupName(){
    Map sgMap = getShippingGroupMap();
    
    // Check the map entries until we find one that isn't a gift shipping group, then return its name.
    for(Object mapEntry : sgMap.entrySet()){
      
      Object sgName = ((Map.Entry)mapEntry).getKey();
      Object hgSg = ((Map.Entry)mapEntry).getValue();
      
      if(hgSg instanceof HardgoodShippingGroup){
        if(!getGiftlistManager().hasGiftAddress((HardgoodShippingGroup) hgSg)){
          mFirstNonGiftShippingGroupName = (String) sgName;
          return;
        }
      }
    }
    mFirstNonGiftShippingGroupName = null;
  }
}