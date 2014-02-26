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


package atg.projects.store.scenario;

import atg.beans.DynamicBeans;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;

import atg.commerce.pricing.Constants;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PMDLExpressionFilterConfiguration;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.definition.CompoundPricingModelExpression;

import atg.nucleus.logging.ApplicationLogging;

import atg.process.ProcessException;
import atg.process.ProcessExecutionContext;

import atg.process.expression.Expression;

import atg.process.filter.ExpressionFilter;
import atg.process.filter.Filter;

import atg.repository.RepositoryItem;

import atg.service.cache.Cache;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.RequestLocale;

import java.text.MessageFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * This expression filter (Scenario Condition) allows us to write
 * scenarios that say "Order contains [less than] [$24.00] merchandise
 * where items [in category ] [x].  This is specifically to handle the
 * case where a merchant wishes to give a free gift when a user purchases at
 * least $24.50 of items from a specific category.
 *
 * @author ATG
 * @version $Version$
 */
public class MerchandiseTotalCondition extends ExpressionFilter {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/scenario/MerchandiseTotalCondition.java#2 $$Change: 768606 $";

  /**
   * Less than constant.
   */
  public static final int LESS_THAN = 0;

  /**
   * Equal constant.
   */
  public static final int EQUAL_TO = 1;

  /**
   * Greater than constant.
   */
  public static final int GREATER_THAN = 2;

  /**
   * The property of the message that points to the order.
   */
  public static final String MESSAGE_ORDER_PROPERTY = "order";

  //-------------------------------------
  // Member variables
  //-------------------------------------

  /** The comparison type being performed on the item totals
   *  as per above constants.
   **/
  int mComparisonType;

  /**
   * The amount to compare the item totals against.
   */
  double mCompareAmount;

  /** PMDL string associated with this filter. **/
  String mPMDLString;

  /** Logger component. **/
  ApplicationLogging mLogger;

  /** Scenario PMDL cache. **/
  Cache mScenarioPMDLCache;

  //-------------------------------------
  // ExpressionFilter overrides
  //-------------------------------------

  //-------------------------------------
  /**
   * Configures this ExpressionFilter using the given configuration
   * object.  The configuration object is typically a global Nucleus
   * component which is configured with the information necessary for
   * the filter's operation.
   *
   * @param pOperator - operator
   * @param pConfiguration - configuration
   * @throws ProcessException if the filter could not be configured
   * - for example, because some of the required properties are
   * missing from the configuration
   **/
  public void configure(String pOperator, Object pConfiguration)
    throws ProcessException {
    PMDLExpressionFilterConfiguration config = (PMDLExpressionFilterConfiguration) pConfiguration;

    mLogger = config.getLogger();

    if (mLogger == null) {
      Object[] args = { pOperator };
      throw new ProcessException(MessageFormat.format(Constants.FILTER_NO_LOGGER, args));
    }

    mScenarioPMDLCache = config.getScenarioPMDLCache();

    if (mScenarioPMDLCache == null) {
      Object[] args = { pOperator };
      throw new ProcessException(MessageFormat.format(Constants.FILTER_NO_PMDL_CACHE, args));
    }
  }

  /**
   * Initializes this ExpressionFilter, given its operator and
   * operands.  The default implementation of this method simply sets
   * the operator and operands properties.
   *
   * <p>This implementation, in addition, verifies that there is
   * exactly one operand, and that its value is a constant.  The
   * constant value of the operand is the PMDL string that defines
   * this filter.
   *
   * @param pOperator - operator
   * @param pOperands - operands
   * @throws ProcessException if the operands argument is invalid
   **/
  public void initialize(String pOperator, Expression[] pOperands)
    throws ProcessException {
    super.initialize(pOperator, pOperands);

    // verify we have the expected number of operands
    if ((pOperands == null) || (pOperands.length != 3)) {
      throw new ProcessException(MessageFormat.format(Constants.FILTER_NO_OPERANDS, new Object[] { pOperator }));
    }

    if (mLogger.isLoggingDebug()) {
      mLogger.logDebug("__MerchandiseTotalCondition: Number of operands = " + pOperands.length);
    }

    // check the first operand    
    if (!(pOperands[0].canGetValue(null))) {
      throw new ProcessException(MessageFormat.format(Constants.FILTER_OPERAND_NOT_CONSTANT,
          new Object[] { pOperands[0], pOperator }));
    }

    //  Now look up the values, for the first operand,
    // cast them to the correct types, and cache values in data members.
    //
    // The rigorous type checking isn't strictly necessary since the
    // scenario editor grammar should constrain the types of our operands,
    // but a little extra sanity checking never hurts.
    Expression exComparisonType = pOperands[0];

    Object value = null;

    try {
      value = exComparisonType.getValue(null);

      Integer testType = (Integer) value;

      if (testType == null) {
        throw new ProcessException("Test type is null");
      }

      mComparisonType = testType.intValue();
    } catch (ClassCastException cce) {
      throw new ProcessException("Test type had unexpected type: " + value.getClass().getName(), cce);
    }

    // check the second operand
    if (!(pOperands[1].canGetValue(null))) {
      throw new ProcessException(MessageFormat.format(Constants.FILTER_OPERAND_NOT_CONSTANT,
          new Object[] { pOperands[1], pOperator }));
    }

    //  Now look up the value, for the second operand,
    // cast them to the correct types, and cache values in data members.
    Expression exCompareAmount = pOperands[1];

    value = null;

    try {
      value = exCompareAmount.getValue(null);

      Double testAmount = (Double) value;

      if (testAmount == null) {
        throw new ProcessException("Amount is null");
      }

      mCompareAmount = testAmount.doubleValue();
    } catch (ClassCastException cce) {
      throw new ProcessException("Amount had unexpected type: " + value.getClass().getName(), cce);
    }

    // check the last operand  - our PMDL rule
    if (!(pOperands[2].canGetValue(null))) {
      throw new ProcessException(MessageFormat.format(Constants.FILTER_OPERAND_NOT_CONSTANT,
          new Object[] { pOperands[2], pOperator }));
    }

    Object pmdl = pOperands[2].getValue(null);

    if (!(pmdl instanceof String)) {
      throw new ProcessException(MessageFormat.format(Constants.FILTER_OPERAND_NOT_CONSTANT,
          new Object[] { pOperands[2], pOperator }));
    }

    mPMDLString = (String) pmdl;
  }

  /**
   * <p>This filter takes the amount entered by the business user an
   * the pmdl rule.  It then finds all the commerce items in the order
   * that match the given pmdl rule.  Of those that match this filter
   * totals up their ItemPriceInfo.amount fields and applies the logic
   * supplied by the business user to determine if the total amount of
   * all the items matches the price criteria.
   *
   *
   * <p>Evaluates this filter in the given scenario execution context.
   * The context may not yet contain all of the information necessary
   * to evaluate the filter - specifically, it may be missing the
   * particular scenario instance and/or profile that the scenario is
   * being executed on.  If that is the case, the filter is evaluated
   * as much as possible, and the simplified filter is returned.
   *
   * <p>The possible return values of this method are as follows:
   * <ul>
   * <li><code>Filter.TRUE</code> - if the filter can be fully
   * evaluated, and is satisfied in the given context
   * <li><code>Filter.FALSE</code> - if the filter can be fully
   * evaluated, and is not satisfied in the given context
   * <li><code>this</code> or another, simplified, <code>Filter</code>
   * - if the filter cannot be fully evaluated because of the missing
   * information in the context
   * <li><code>null</code> - if the filter cannot be evaluated because
   * of a null expression encountered during evaluation (e.g., filter
   * refers to a profile property which evaluates to null)
   * </ul>
   *
   * @param pContext - process execution context
   * @throws ProcessException if there is a problem evaluating the
   * filter (other than information missing from the context)
   * @return The Filter object or null after evaluation of the context
   **/
  protected Filter evaluate(ProcessExecutionContext pContext)
    throws ProcessException {
    if (mLogger.isLoggingDebug()) {
      mLogger.logDebug("__MerchandiseTotalCondition: evaluating PMDLExpressionFilter");
    }

    // extract the message from the name resolver
    Object message = pContext.getMessage();

    // if no message this is an exception
    if (message == null) {
      throw new ProcessException(Constants.NULL_MESSAGE);
    }

    // the message contains the order in this context.
    Object orderObject = null;

    try {
      orderObject = DynamicBeans.getSubPropertyValue(message, MESSAGE_ORDER_PROPERTY);
    } catch (atg.beans.PropertyNotFoundException e) {
      // if there's no order property, then we cannot 
      // evaluate so fail immediately
      return Filter.FALSE;
    }

    // make sure the order is what we want it to be
    Order order = null;

    if (orderObject instanceof Order) {
      order = (Order) orderObject;
    } else {
      // order object is not an order - fail immediately
      return Filter.FALSE;
    }

    boolean result = false;

    if (mLogger.isLoggingDebug()) {
      mLogger.logDebug("__MerchandiseTotalCondition: Looping commerce items ");
    }

    // get the commerce items from the order
    List commerceItems = order.getCommerceItems();

    // loop through the commerce items and try each for the pmdl rule
    if ((commerceItems != null) && (commerceItems.size() > 0)) {
      // the value we are collecting
      double itemTotal = 0.00;

      // loop through the commerce items and try each for the pmdl rule
      int size = commerceItems.size();
      CommerceItem item = null;
      ItemPriceInfo price = null;

      for (int i = 0; i < size; i++) {
        item = (CommerceItem) commerceItems.get(i);

        // bind the commerce item to a pmdl rule evaluation
        // and see if it evaluates for our rule
        if (evaluatePMDLRule(pContext, order, item)) {
          if (mLogger.isLoggingDebug()) {
            mLogger.logDebug("__MerchandiseTotalCondition: item passes eval ");
          }

          // it meets the condition of the rule
          // get the price and keep a running total
          price = item.getPriceInfo();

          if (price != null) {
            itemTotal += price.getAmount();

            if (mLogger.isLoggingDebug()) {
              mLogger.logDebug("__MerchandiseTotalCondition: itemTotal = " + itemTotal);
            }
          }
        }
      } // for each commerce item  

      if (mLogger.isLoggingDebug()) {
        mLogger.logDebug("__MerchandiseTotalCondition: comparison Type is " + mComparisonType);
        mLogger.logDebug("__MerchandiseTotalCondition: compare amount is " + mComparisonType);
      }

      // now evaluate the filter
      switch (mComparisonType) {
      case LESS_THAN:
        result = itemTotal < mCompareAmount;

        break;

      case EQUAL_TO:
        result = itemTotal == mCompareAmount;

        break;

      case GREATER_THAN:
        result = itemTotal > mCompareAmount;

        break;

      default:
        throw new ProcessException("Unknown comparison value: " + mComparisonType);
      }
    } // if we have commerce items

    if (mLogger.isLoggingDebug()) {
      mLogger.logDebug("__MerchandiseTotalCondition: evaluation result = " + result);
      mLogger.logDebug("\n");
    }

    // return either Filter.TRUE or Filter.FALSE
    return result ? Filter.TRUE : Filter.FALSE;
  }

  /**
   * Evaluate the rule put before us for the commerce item in question.
   * @param pContext - process execution context
   * @param pOrder - order
   * @param pItem - the commerce item to evaluate the rule for
   * @return rule
   * @throws ProcessException if process error occurs
   */
  protected boolean evaluatePMDLRule(ProcessExecutionContext pContext, Order pOrder, CommerceItem pItem)
    throws ProcessException {
    if (mLogger.isLoggingDebug()) {
      mLogger.logDebug("__MerchandiseTotalCondition: evaluating item " + pItem);
    }

    // turn the string into a PricingModelElem
    CompoundPricingModelExpression pmdl = null;

    try {
      pmdl = (CompoundPricingModelExpression) mScenarioPMDLCache.get(mPMDLString);
    } catch (Exception e) {
      throw new ProcessException("Exception searching the cache for the object with the specified key ", e);
    }

    // set up everything for the PMDL evaluation environment
    // by binding all the elements to the rule
    Map bindings = new HashMap(5);

    // profile
    // get the profile from the context
    RepositoryItem profile = pContext.getSubject();

    if (profile != null) {
      bindings.put(Constants.PROFILE_BINDING_NAME.trim(), profile);
    }

    bindings.put(Constants.ORDER_BINDING_NAME.trim(), pOrder);

    // items
    Object items = pOrder.getCommerceItems();

    if (items != null) {
      bindings.put(Constants.ITEMS_BINDING_NAME.trim(), items);
    }

    // item
    bindings.put(Constants.ITEM_BINDING_NAME, pItem);

    // locale
    DynamoHttpServletRequest request = pContext.getRequest();

    if (request != null) {
      RequestLocale requestLocale = request.getRequestLocale();

      if (requestLocale != null) {
        Locale locale = requestLocale.getLocale();

        if (locale != null) {
          bindings.put(Constants.LOCALE_BINDING_NAME.trim(), locale);
        }
      }
    }

    // bindings are done lets evaluate the rule
    Object ret = null;

    try {
      ret = pmdl.evaluate(pContext, mLogger, bindings);
    } catch (PricingException e) {
      throw new ProcessException("Can't evaluate the PMDL expression ", e);
    }

    if (ret == null) {
      return false;
    }

    if (!(ret instanceof Boolean)) {
      throw new ProcessException(MessageFormat.format(Constants.FILTER_BAD_EVAL,
          new Object[] { mPMDLString, (ret == null) ? "null" : ret.getClass().getName() }));
    }

    return ((Boolean) ret).booleanValue();
  }
}
