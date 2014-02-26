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



package atg.projects.store.collections.filter;

import java.util.HashMap;
import java.util.Map;

import atg.commerce.promotion.PromotionConstants;
import atg.naming.NameResolver;
import atg.service.filter.ItemFilter;
import atg.web.messaging.UserMessage;

/**
 * This filter removes duplicate messages from the collection of messages.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/collections/filter/RemoveDuplicateMessagesFilter.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class RemoveDuplicateMessagesFilter implements ItemFilter {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/collections/filter/RemoveDuplicateMessagesFilter.java#2 $$Change: 768606 $";

  /**
   * Remove duplicated messages, i.e. messages with equal identifiers
   * Process only GWP messages
   * 
   * @param pItems an array of UserMessage items
   * @return filtered array 
   */
  public Object[] filterItems(Object[] pItems, NameResolver pResolver) {
    Map<String, UserMessage> filteredMap = new HashMap<String, UserMessage>(); 
    
    if(pItems == null || pItems.length == 0){
      return null;
    }
    
    // Filter out duplicates
    for(Object item : pItems) {
      UserMessage message = (UserMessage) item;
      boolean messageIsAdded = false;
      if(!filteredMap.containsKey(message.getIdentifier())) {
        
        /*
         *  If promotion message is failure, remove all previous 'success' messages for this promotion.
         *  If current failure promotion message is linked to another promotion then 'success' messages, 
         *  it should be added to result map.
         */
        if(message.getIdentifier().equals(PromotionConstants.GWP_MESSAGE_IDENTIFIER_PROMOTION_PARTIAL_FAILURE) ||
            message.getIdentifier().equals(PromotionConstants.GWP_MESSAGE_IDENTIFIER_PROMOTION_FULL_FAILURE) ||
            message.getIdentifier().equals(PromotionConstants.GWP_MESSAGE_IDENTIFIER_PROMOTION_INVALIDATED) ) {
          
          // Check if we added qualify message before linked to the same promotion as the current message 
          UserMessage qualifiedMessage = filteredMap.get(PromotionConstants.GWP_MESSAGE_IDENTIFIER_PROMOTION_QUALIFIED);
          if(qualifiedMessage != null) {

            // Parameters array contains promotion ID
            if(qualifiedMessage.getParams()[0].equals(message.getParams()[0])) {
              // Remove previous qualified message
              filteredMap.remove(PromotionConstants.GWP_MESSAGE_IDENTIFIER_PROMOTION_QUALIFIED);
            }
            // If we added qualify message for another promotion, the current message should be also added
            else {
              filteredMap.put(message.getIdentifier(), message);
              messageIsAdded = true;
            }
          }
        }
        
        // If message still isn't  added, check unqualifying message flag
        if (!messageIsAdded) {
          // Do not put into map messages when unqualifying message flag is 'false'
          if(message.getParams().length > 1) {
            Object unqualifyingMessageFlag = message.getParams()[1];
            if(unqualifyingMessageFlag instanceof Boolean && ((Boolean) unqualifyingMessageFlag).booleanValue()) {
              filteredMap.put(message.getIdentifier(), message);
            }
          } else {
            filteredMap.put(message.getIdentifier(), message);
          }
        }
        
      }
    }
    
    return filteredMap.values().toArray();
  }
}
