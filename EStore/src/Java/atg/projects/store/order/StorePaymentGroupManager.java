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

import atg.commerce.CommerceException;

import atg.commerce.claimable.ClaimableManager;

import atg.commerce.order.*;

import atg.projects.store.logging.LogUtils;
import atg.projects.store.payment.StoreStoreCredit;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds utility methods for manipulating PaymentGroups.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/StorePaymentGroupManager.java#3 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 * 
 */
public class StorePaymentGroupManager extends PaymentGroupManager {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/StorePaymentGroupManager.java#3 $$Change: 788278 $";

  /**
   * property: claimableManager.
   */
  private ClaimableManager mClaimableManager;
  
  /**
   * @return the claimable manager.
   */
  public ClaimableManager getClaimableManager() {
    return mClaimableManager;
  }

  /**
   * @param pClaimableManager - claimable manager.
   */
  public void setClaimableManager(ClaimableManager pClaimableManager) {
    mClaimableManager = pClaimableManager;
  }

  /**
   * property: orderManager.
   */
  private OrderManager mOrderManager;
  
  /**
   * @return the order manager.
   */
  public OrderManager getOrderManager() {
    return mOrderManager;
  }

  /**
   * @param pOrderManager - order manager
   */
  public void setOrderManager(OrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  /**
   * property: storeOrderTools.
   */
  private StoreOrderTools mStoreOrderTools;
  
  /**
   * @return the Store order tools.
   */
  public StoreOrderTools getStoreOrderTools() {
    return mStoreOrderTools;
  }

  /**
   * @param pStoreOrderTools - the Store order tools.
   */
  public void setStoreOrderTools(StoreOrderTools pStoreOrderTools) {
    mStoreOrderTools = pStoreOrderTools;
  }

  /**
   * property: storeCreditTypePayment
   */
  private String mStoreCreditPaymentType;
  
  /**
   * @return string representing the store credit payment type.
   */
  public String getStoreCreditPaymentType() {
    return mStoreCreditPaymentType;
  }

  /**
   * @param pStoreCreditPaymentType - store credit payment type.
   */
  public void setStoreCreditPaymentType(String pStoreCreditPaymentType) {
    mStoreCreditPaymentType = pStoreCreditPaymentType;
  }

  /**
   * Given an order, return all the store credit payment group ids.
   * 
   * @param pOrder - the order.
   */
  public void removeStoreCreditPaymentGroups(Order pOrder) {
    List allPaymentGroups = pOrder.getPaymentGroups();
    ArrayList storeCreditIds = new ArrayList();
    int numPayGroups = allPaymentGroups.size();
    PaymentGroup currentPaymentGroup = null;
    String storeCreditType = getStoreCreditPaymentType();

    for (int i = 0; i < numPayGroups; i++) {
      currentPaymentGroup = (PaymentGroup) allPaymentGroups.get(i);

      if (currentPaymentGroup.getPaymentGroupClassType().equals(storeCreditType)) {
        storeCreditIds.add(currentPaymentGroup.getId());
      }
    }

    if (storeCreditIds.size() == 0) {
      return;
    }

    int numStoreCreditPaymentGroups = storeCreditIds.size();

    for (int j = 0; j < numStoreCreditPaymentGroups; j++) {
      try {
        removePaymentGroupFromOrder(pOrder, (String) storeCreditIds.get(j));
      } 
      catch (CommerceException ce) {
        if (isLoggingError()) {
          logError(LogUtils.formatMajor("Could not remove payment group: "), ce);
        }
      }
    }
  }

  /**
   * This method will take an array of store credit ids, and create payment
   * groups and add them to the order. This will only create enough store
   * credit payment groups to cover the order costs. If more store credit ids
   * are passed in than are needed to cover the order, they are not used. On
   * the other hand, if not enough store credit ids are here to cover the
   * order costs, then this will search for a credit card and add the
   * remaining order amount to the credit card.
   *
   * @param pProfile - users profile.
   * @param pOrder - the order.
   * @param pOnlineCreditIds - online credit ids.
   * 
   * @return true.
   * 
   * @throws atg.commerce.CommerceException if an error occurs.
   */
  public boolean initializePaymentMethods(RepositoryItem pProfile, Order pOrder, String[] pOnlineCreditIds)
    throws CommerceException {
    
    List onlineCredits = getOnlineCredits(pOnlineCreditIds);
    OrderManager om = getOrderManager();
    StoreOrderTools orderTools = getStoreOrderTools();

    // First get rid of the current store credit payment groups.
    removeStoreCreditPaymentGroups(pOrder);

    // Also, remove the "remainingOrderAmount" from the credit card if it exists.
    CreditCard cc = orderTools.getCreditCard(pOrder);

    if (cc != null) {
      try {
        List relationships = getAllPaymentGroupRelationships(pOrder);

        // The initial credit card on the order does not have a relationship with the order. 
        // If this is the case, don't try to remove the amount from payment group.
        if ((relationships != null) && (relationships.size() != 0)) {
          om.removeRemainingOrderAmountFromPaymentGroup(pOrder, cc.getId());
        }
      } 
      catch (CommerceException ce) {
        // Assume this is not an issue.
        if (isLoggingError()) {
          logError(LogUtils.formatMajor("Failed attempt to remove amount from credit card: " + ce));
        }
      }
    }

    if (onlineCredits == null) {
      if (isLoggingDebug()) {
        logDebug("Online credits null.");
      }

      // Should never happen, this is only called when user is paying with online credit.
      return true;
    }

    StoreStoreCredit storeCreditPaymentGroup = null;
    RepositoryItem creditItem = null;
    String storeCreditPaymentType = getStoreCreditPaymentType();
   
    double orderTotal = pOrder.getPriceInfo().getTotal();
    double remainingAmount = orderTotal;
    double onlineCreditAmount = 0.0;

    // Create and add store credit payment group(s) to order.
    try {
      for (int i = 0; i < onlineCredits.size(); i++) {
        if (remainingAmount == 0.0) {
          return true;
        }

        creditItem = (RepositoryItem) onlineCredits.get(i);
        storeCreditPaymentGroup = (StoreStoreCredit) createPaymentGroup(storeCreditPaymentType);
        storeCreditPaymentGroup.setStoreCreditNumber(creditItem.getRepositoryId());
        storeCreditPaymentGroup.setProfileId(pProfile.getRepositoryId());
        addPaymentGroupToOrder(pOrder, storeCreditPaymentGroup);
        String amountAvailableProperty = 
          getClaimableManager().getClaimableTools().getStoreCreditAmountAvailablePropertyName();
        onlineCreditAmount = ((Double)creditItem.getPropertyValue(amountAvailableProperty)).doubleValue();

        if (remainingAmount > onlineCreditAmount) {
          om.addOrderAmountToPaymentGroup(pOrder, storeCreditPaymentGroup.getId(), onlineCreditAmount);

          storeCreditPaymentGroup.setAmountAppliedToOrder(onlineCreditAmount);

          remainingAmount -= onlineCreditAmount;
        } 
        else {
          // This onlineCredit will cover all the remaining costs of the order.
          if (isLoggingDebug()) {
            logDebug("Online credit will pay for rest of order: " + storeCreditPaymentGroup.getId());
          }

          om.addRemainingOrderAmountToPaymentGroup(pOrder, storeCreditPaymentGroup.getId());
          storeCreditPaymentGroup.setAmountAppliedToOrder(remainingAmount);
          remainingAmount = 0.0;
        }
      }

      // Payment sufficient already validated, so add remainder to credit card.
      if (remainingAmount > 0.0 && cc != null) {
        om.addRemainingOrderAmountToPaymentGroup(pOrder, cc.getId());       
      }

      om.updateOrder(pOrder);
    } 
    catch (CommerceException ce) {
      // Make sure we capture in the logs where this happened, then re-throw the exception.
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Exception creating online credit payment group: "), ce);
      }

      throw ce;
    }

    return true;
  }

  /**
   * This will return the online credits selected by the user.
   *
   * @param pOnlineCreditIds - online credit ids.
   * 
   * @return List of online credits
   */
  public List getOnlineCredits(String[] pOnlineCreditIds) {
    ArrayList onlineCredits = new ArrayList();

    if (pOnlineCreditIds == null) {
      return onlineCredits;
    }

    try {
      RepositoryItem onlineCredit = null;

      ClaimableManager cm = getClaimableManager();
      Repository cr = cm.getClaimableTools().getClaimableRepository();
      String storeCreditItemDescName = cm.getClaimableTools().getStoreCreditItemDescriptorName();

      for (int j = 0; j < pOnlineCreditIds.length; j++) {
        onlineCredit = cr.getItem(pOnlineCreditIds[j], storeCreditItemDescName);
        onlineCredits.add(onlineCredit);
      }
    } 
    catch (RepositoryException re) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error getting store credits."), re);
      }

      // UPDATE: handle exception!
    }

    return onlineCredits;
  }

  /**
   * This returns true if this is the only payment group in the order, 
   * or if this payment group is not empty. Otherwise it returns false.
   * 
   * @param pOrder The order containing the payment group.
   * @param pPaymentGroup The payment group being checked.
   * 
   * @return true if this payment group will be used to pay for the order.
   */
  @Override
  public boolean isPaymentGroupUsed(Order pOrder, PaymentGroup pPaymentGroup) {

    // If there is nothing to pay, we do not use any payment groups (if any).
    // Otherwise we should apply common logic defined by DCS.
    if (pOrder.getPriceInfo().getTotal() > 0) {
      return super.isPaymentGroupUsed(pOrder, pPaymentGroup);
    } 
    else {
      return false;
    }
  }
  
}
