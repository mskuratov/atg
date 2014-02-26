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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.PaymentGroupRelationship;
import atg.commerce.order.ShippingGroup;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.projects.store.payment.StoreStoreCredit;
import atg.repository.RemovedItemException;


/**
 * Extension to order class.
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/StoreOrderImpl.java#3 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreOrderImpl extends OrderImpl {
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/StoreOrderImpl.java#3 $$Change: 788278 $";

  // WARNING: These are also in the /atg/commerce/order/OrderTools.properties
  /** Gift message to key name. */
  public static final String GIFT_MESSAGE_TO_KEY = "giftMessageTo";

  /** Gift message key name. */
  public static final String GIFT_MESSAGE_KEY = "giftMessage";

  /** Gift message from key name. */
  public static final String GIFT_MESSAGE_FROM_KEY = "giftMessageFrom";

  /**
   * Sample commerce item type.
   */
  public static final String SAMPLE_COMMERCE_ITEM_TYPE = "sampleCommerceItem";

  /**
   * property: shouldAddGiftNote
   */
  private transient volatile boolean mShouldAddGiftNote;

  /**
   * @return the mShouldAddGiftNote
   */
  public boolean isShouldAddGiftNote() {
    return mShouldAddGiftNote;
  }

  /**
   * @param pShouldAddGiftNote the shouldAddGiftNote to set
   */
  public void setShouldAddGiftNote(boolean pShouldAddGiftNote) {
    mShouldAddGiftNote = pShouldAddGiftNote;
  }
  
  /**
   * @return the SAP order id.
   */
  public String getOmsOrderId() {
    return (String) getPropertyValue(StorePropertyNameConstants.OMSORDERID);
  }

  /**
   * Sets the SAP order id.
   * @param pOmsId - the SAP order id
   */
  public void setOmsOrderId(String pOmsId) {
    setPropertyValue(StorePropertyNameConstants.OMSORDERID, pOmsId);
  }

  /**
   * @return list of OMS segments.
   */
  public List getOmsSegments() {
    return (List) getPropertyValue(StorePropertyNameConstants.OMSSEGMENTS);
  }

  /**
   * Sets the OMS segments.
   * 
   * @param pOmsSegment - list of OMS segments.
   */
  public void addOmsSegment(String pOmsSegment) {
    List segments = getOmsSegments();

    if (segments == null) {
      segments = new ArrayList();
    }

    segments.add(pOmsSegment);
    setChanged(true);
  }

  /**
   * Removes all OMS segments from order.
   */
  public void removeAllOmsSegments() {
    List omsSegments = getOmsSegments();

    if ((omsSegments != null) && (omsSegments.size() > 0)) {
      setPropertyValue(StorePropertyNameConstants.OMSSEGMENTS, new LinkedList());
      setChanged(true);
    }
  }
  
  /**
   * Checks to see if this order is gift wrapped or not.
   * 
   * @return true if gift wrapped, otherwise false.
   */
  public boolean getContainsGiftWrap() {
    List items = getCommerceItems();
    int itemCount = getCommerceItemCount();

    for (int i = 0; i < itemCount; i++) {
      if (items.get(i) instanceof GiftWrapCommerceItem) {
        return true;
      }
    }

    return false;
  }
  
  /**
   * Returns gift wrap commerce item.
   * 
   * @return gift wrap item if found, otherwise null.
   */
  public GiftWrapCommerceItem getGiftWrapItem() {
    List items = getCommerceItems();
    
    int itemCount = getCommerceItemCount();

    for (int i = 0; i < itemCount; i++) {
      if (items.get(i) instanceof GiftWrapCommerceItem) {
        return (GiftWrapCommerceItem) items.get(i);
      }
    }

    return null;
  }

  /**
   * Checks to see if this order has a gift message special instruction.
   * 
   * @return true if order contains gift message, otherwise false.
   */
  public boolean getContainsGiftMessage() {
    Map specialInstructions = getSpecialInstructions();

    return (specialInstructions != null) && specialInstructions.containsKey(GIFT_MESSAGE_TO_KEY);
  }

  /**
   * Sets the gift message.
   * 
   * @param pMessageTo - the message to field.
   * @param pMessage - the message field.
   * @param pMessageFrom - the message from field.
   */
  public void setGiftMessage(String pMessageTo, String pMessage, String pMessageFrom) {
    Map specialInstructions = getSpecialInstructions();

    specialInstructions.put(GIFT_MESSAGE_TO_KEY, pMessageTo);
    specialInstructions.put(GIFT_MESSAGE_KEY, pMessage);
    specialInstructions.put(GIFT_MESSAGE_FROM_KEY, pMessageFrom);
  }

  /**
   * Determines if user has entered gift message.
   * 
   * @return true if gift message was populated, otherwise false.
   */
  public boolean getGiftMessagePopulated() {
    if (!getContainsGiftMessage()) {
      return false;
    }

    String giftMessageTo = (String) getSpecialInstructions().get(GIFT_MESSAGE_TO_KEY);
    String giftMessageFrom = (String) getSpecialInstructions().get(GIFT_MESSAGE_FROM_KEY);
    String giftMessage = (String) getSpecialInstructions().get(GIFT_MESSAGE_KEY);

    if (StringUtils.isBlank(giftMessageTo) || 
        StringUtils.isBlank(giftMessageFrom) || 
        StringUtils.isBlank(giftMessage)) {
      return false;
    }

    return true;
  }

  /**
   * This is a read-only property getter.
   * <br/>
   * <code>storeCreditsAppliedTotal</code> property contains total amount of store credits 
   * applied to the current order.
   * 
   * @return order's store credits amount.
   */
  public double getStoreCreditsAppliedTotal() {
    
    double result = 0;
    
    for (PaymentGroupRelationship relationship : 
         (Collection<PaymentGroupRelationship>) getPaymentGroupRelationships()) {
      
      // Take into account all store credit relationships, because we have sometimes CreditCard 
      // relationship with ORDERAMOUNT type and StoreCredit relationship with ORDERAMOUNTREMAINING type.
      if (relationship.getPaymentGroup() instanceof StoreStoreCredit) {
        
        // If there is amount set on the relationship, add it. Otherwise current store credit has been 
        // just added to the order and there should be set amountAppliedToOrder property.
        result += relationship.getAmount() > 0 ? 
          relationship.getAmount() :
          ((StoreStoreCredit) relationship.getPaymentGroup()).getAmountAppliedToOrder();
      }
    }
    return result;
  }
  
  /**
   * Returns total count of returned items in the order.
   * @return total count of returned items in the order.
   */
  public long getTotalReturnedItemsCount(){
    
    List<CommerceItem> items = getCommerceItems();
  
    long count = 0;
  
    for (CommerceItem item : items){
      count += item.getReturnedQuantity();
    }
  
    return count;
  
  }
  
  /**
   * Returns original total count of commerce items in the order at the point
   * when the order has been submitted. That count includes already returned items
   * count.
   *   
   * @return original total count of commerce items including already returned items.
   */
  public long getOriginalTotalItemsCount(){
    return getTotalCommerceItemCount() + getTotalReturnedItemsCount();
  }
  
  /**
   * Removes an Address associated with this orders shipping group.
   * 
   * @param pShippingGroupId - The shipping groups id from which to remove the address.
   * 
   * @return A boolean indicating success or failure.
   */
  public boolean removeAddress(String pShippingGroupId){
    
    ShippingGroup orderShippingGroup =  null;
    
    try{
      orderShippingGroup = getShippingGroup(pShippingGroupId);
    }
    catch(Exception e) {
      return false;
    }
    
    // If the shipping group is found, set its Address to an empty address.   
    if(orderShippingGroup instanceof HardgoodShippingGroup) {
      Address emptyAddress = new Address();
      ((HardgoodShippingGroup)orderShippingGroup).setShippingAddress(emptyAddress);
      
      return true;
    }  
    
    return false;
  }

  /**
   * Method that renders the general order information in a readable string format.
   * 
   * @return string representation of the class.
   */
  public String toString() {
    StringBuilder sb = new StringBuilder("Order[");

    try {
      sb.append("type:").append(getOrderClassType()).append("; ");
      sb.append("id:").append(getId()).append("; ");
      sb.append("state:").append(getStateAsString()).append("; ");
      sb.append("transient:").append(isTransient()).append("; ");
      sb.append("profileId:").append(getProfileId()).append("; ");
    } 
    catch (RemovedItemException exc) {
      sb.append("removed");
    }

    sb.append("]");

    return sb.toString();
  }
}
