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

import atg.commerce.CommerceException;

import atg.commerce.order.CreditCard;
import atg.commerce.order.Order;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;

import atg.nucleus.naming.ParameterName;

import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.order.StorePaymentGroupManager;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;

import javax.servlet.ServletException;

import javax.transaction.TransactionManager;


/**
 * <p>
 * This droplet will create a credit card payment group and add it to the
 * order if it doesn't already exist. The need for this arose because we
 * are removing the credit card payment group if the online credits are
 * sufficient to pay for the order. Otherwise, CyberSource will throw
 * errors because the credit card payment group will exist, and will
 * be validated automatically. However, if the user decides not to
 * place the order, and later comes back to the billing page, we will
 * need a credit card so the form doesn't blow up trying to set properties
 * on a credit card that doesn't exist.
 *
 * @author ATG
 * @version $Revision: #2 $
 */
public class EnsureCreditCardPaymentGroup extends DynamoServlet {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/EnsureCreditCardPaymentGroup.java#2 $$Change: 768606 $";

  /** Input parameter name profile. */
  public static final ParameterName ORDER = ParameterName.getParameterName("order");

  /**
   * Store order tools.
   */
  private StoreOrderTools mStoreOrderTools;

  /**
   * Store payment group manager.
   */
  protected StorePaymentGroupManager mStorePaymentGroupManager;

  /**
   * Transaction manager.
   */
  TransactionManager mTransactionManager;

  /**
   * @return the mStoreOrderTools.
   */
  public StoreOrderTools getStoreOrderTools() {
    return mStoreOrderTools;
  }

  /**
   * @param pStoreOrderTools - the Store order tools to set.
   */
  public void setStoreOrderTools(StoreOrderTools pStoreOrderTools) {
    mStoreOrderTools = pStoreOrderTools;
  }

  /**
   * @return the Store payment group manager.
   */
  public StorePaymentGroupManager getStorePaymentGroupManager() {
    return mStorePaymentGroupManager;
  }

  /**
   * @param pStorePaymentGroupManager - the Store payment group manager.
   */
  public void setStorePaymentGroupManager(StorePaymentGroupManager pStorePaymentGroupManager) {
    mStorePaymentGroupManager = pStorePaymentGroupManager;
  }

  /**
   * @return the transaction manager.
   */
  public TransactionManager getTransactionManager() {
    return mTransactionManager;
  }

  /**
   * @param pTransactionManager -  the transactional manager.
   */
  public void setTransactionManager(TransactionManager pTransactionManager) {
    mTransactionManager = pTransactionManager;
  }

  /**
   * <p>
   * If the credit card payment group is empty, this droplet will
   * create one and add it to the order. Wrapped in a transaction
   * since we're making order modifications.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    if (isLoggingDebug()) {
      logDebug("Ensuring order contains credit card pg");
    }

    Object orderObject = pRequest.getObjectParameter(ORDER);

    if ((orderObject == null) || !(orderObject instanceof Order)) {
      if (isLoggingDebug()) {
        logDebug("Bad order parameter passed: null=" + (orderObject == null) +
          ". If null=false, then wrong object type.");
      }

      return;
    }

    Order order = (Order) orderObject;

    try {
      TransactionDemarcation td = new TransactionDemarcation();

      try {
        td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

        StoreOrderTools orderTools = getStoreOrderTools();

        CreditCard cc = orderTools.getCreditCard(order);

        if (cc == null) {
          if (isLoggingDebug()) {
            logDebug("Creating credit card pg, current one is null.");
          }

          try {
            CreditCard newCard = (CreditCard) orderTools.createPaymentGroup("creditCard");
            order.addPaymentGroup(newCard);
            getStorePaymentGroupManager().getOrderManager().updateOrder(order);
          } catch (CommerceException ce) {
            if (isLoggingError()) {
              logError(LogUtils.formatMajor("Couldn't create new cc group"), ce);
            }
          }
        } else {
          if (isLoggingDebug()) {
            logDebug("Credit card pg is not null, no need to create.");
            logDebug("Billing address is null: " + (cc.getBillingAddress() == null));
          }
        }
      } finally {
        td.end();
      }
    } catch (TransactionDemarcationException e) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Transaction error"), e);
      }
    }

    if (isLoggingDebug()) {
      logDebug("cc from orderTools is null: " + (getStoreOrderTools().getCreditCard(order) == null));
    }
  }
}
