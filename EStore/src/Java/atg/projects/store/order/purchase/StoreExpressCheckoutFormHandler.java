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

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.order.purchase.ExpressCheckoutFormHandler;

import atg.core.util.ResourceUtils;

import atg.droplet.DropletFormException;

import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreShippingGroupManager;
import atg.projects.store.profile.StorePropertyManager;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;


/**
 * This class is needed to get the billing address from the credit card
 * rather than the user's default billing address.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreExpressCheckoutFormHandler.java#3 $$Change: 788278 $ 
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 * @see ExpressCheckoutFormHandler
 */
public class StoreExpressCheckoutFormHandler extends ExpressCheckoutFormHandler {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreExpressCheckoutFormHandler.java#3 $$Change: 788278 $";


  /**
   * property: checkoutProgessStates
   */
  private CheckoutProgressStates mCheckoutProgressStates;

  /**
   * @return the checkout progress states.
   */
  public CheckoutProgressStates getCheckoutProgressStates()
  {
    return mCheckoutProgressStates;
  }

  /**
   * @param pCheckoutProgressStates - the checkout progress states to set.
   */
  public void setCheckoutProgressStates(CheckoutProgressStates pCheckoutProgressStates)
  {
    mCheckoutProgressStates = pCheckoutProgressStates;
  }

  /**
   * property: storePropertyManager
   */
  private StorePropertyManager mStorePropertyManager;
  
  /**
   * @return the Store property manager property.
   */
  public StorePropertyManager getStorePropertyManager() {
    return mStorePropertyManager;
  }

  /**
   * @param pStorePropertyManager - the store property manager.
   */
  public void setStorePropertyManager(StorePropertyManager pStorePropertyManager) {
    mStorePropertyManager = pStorePropertyManager;
  }

  /**
   * property: expressCheckoutHelper
   */
  private StoreExpressCheckoutProcessHelper mExpressCheckoutHelper;
  
  /**
   * @return the Store express checkout property.
   */
  public StoreExpressCheckoutProcessHelper getExpressCheckoutHelper() {
    return mExpressCheckoutHelper;
  }

  /**
   * @param pExpressCheckoutHelper - Store express checkout helper.
   */
  public void setExpressCheckoutHelper(StoreExpressCheckoutProcessHelper pExpressCheckoutHelper) {
    mExpressCheckoutHelper = pExpressCheckoutHelper;
  }

  /**
   * Property BillingHelper
   */
  private StoreBillingProcessHelper mBillingHelper;

  /**
   * @return the billing helper.
   */
  public StoreBillingProcessHelper getBillingHelper() {
    return mBillingHelper;
  }

  /**
   * @param pBillingHelper - the billing helper to set.
   */
  public void setBillingHelper(StoreBillingProcessHelper pBillingHelper) {
    mBillingHelper = pBillingHelper;
  }

  /**
   * property: autoApplyStoreCredits
   */
  boolean mAutoApplyStoreCredits = false;

  /**
   * Set the AutoApplyStoreCredits property.
   * 
   * @param pAutoApplyStoreCredits a <code>boolean</code> value.
   * 
   * @beaninfo description: Should profile's store credits be auto-applied to the order?
   *           Set to true for auto-applying.
   */
  public void setAutoApplyStoreCredits(boolean pAutoApplyStoreCredits) {
    mAutoApplyStoreCredits = pAutoApplyStoreCredits;
  }

  /**
   * Return the AutoApplyStoreCredits property.
   * 
   * @return a <code>boolean</code> value to determine whether to auto-apply store credits or not.
   */
  public boolean isAutoApplyStoreCredits() {
    return mAutoApplyStoreCredits;
  }

  /**
   * This method overrides the super class to apply the address from
   * the credit card rather than from the user's "default billing".
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception IOException if an error occurs.
   * @exception ServletException if an error occurs.
   */
  protected void ensurePaymentGroup(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws IOException, ServletException {
    
    // We want to start clean - get rid of any lingering store credits or whatever 
    // from previous checkout attempts.
    try {
      getPaymentGroupManager().removeAllPaymentGroupsFromOrder(getOrder());
    } 
    catch (CommerceException ce) {
      throw new ServletException(ce);
    }

    // This call creates the payment group.
    PaymentGroup paymentGroup = getPaymentGroup();

    if ((paymentGroup == null) || (!(paymentGroup instanceof CreditCard))) {
      String msg = formatUserMessage(MSG_EXPRESS_CHECKOUT_ERROR, pRequest, pResponse);
      addFormException(new DropletFormException(msg, MSG_EXPRESS_CHECKOUT_ERROR));
    }

    // Copy the default credit card into the order
    RepositoryItem defaultCreditCard = (RepositoryItem) 
      getProfile().getPropertyValue(getDefaultCreditCardPropertyName());

    if (defaultCreditCard == null) {
      if (isLoggingDebug()) {
        logDebug(ResourceUtils.getMsgResource(ERROR_MISSING_CREDIT_CARD,
                                              getResourceBundleName(), 
                                              getResourceBundle()));
      }
    } 
    else {
      getCommerceProfileTools().copyCreditCard(defaultCreditCard, (CreditCard) paymentGroup);
    }

    if (isAutoApplyStoreCredits()){
      try {
        getBillingHelper().
          setupStoreCreditPaymentGroupsForOrder(getOrder(), 
                                                getProfile(), 
                                                getBillingHelper().getStoreCreditIds(getProfile()));
      } 
      catch (StorePurchaseProcessException exc) {
        String msg = ResourceUtils.getMsgResource(exc.getMessage(), 
                                                  getResourceBundleName(), 
                                                  getResourceBundle(getUserLocale(pRequest, pResponse)), 
                                                  exc.getParams());
        
        addFormException(new DropletFormException(msg,null));
      } 
      catch (CommerceException ce) {
        processException(ce, StoreBillingProcessHelper.STORE_CREDIT_ERROR, pRequest, pResponse);

      } catch (Exception exc) {
        if (isLoggingError()) {
          logError(LogUtils.formatMajor(""), exc);
          logError(LogUtils.formatMajor("Error while processing Store Credit : "), exc);
        }
      }
    }
  }

  /**
   * This extends the base behavior to set the shipping method for all gift
   * shipping groups to the profile's default shipping method.
   *
   * In the case when user switches from multiple shipping to express checkout,
   * all non-gift items are moved to default shipping group.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse a <code>DynamoHttpServletResponse</code> value.
   * 
   * @exception IOException if an error occurs.
   * @exception ServletException if an error occurs.
   */
  protected void ensureShippingGroup(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws IOException, ServletException {
    
    super.ensureShippingGroup(pRequest, pResponse);

    // Move non gift items to default shipping group
    moveNonGiftItemsToDefaultShippingGroup();

    // Set shipping method for gift shipping groups
    getExpressCheckoutHelper().ensureShippingMethodOfGiftShippingGroups(getOrder(), 
                                                                        getProfile(), 
                                                                        getDefaultShippingMethodPropertyName());
    
    // Remove HardgoodShippingGroups with no item relationships from the order.
    removeShippingGroupsWithoutItemRelationships();
  }
  
  /**
   * Removes shipping groups with no commerce item relationships from the order.
   */
  protected void removeShippingGroupsWithoutItemRelationships(){

    Order order = getOrder();
    if(order == null){
      return;
    }
    
    // Find empty shipping groups.
    List<String> emptyShippingGroupIds = new ArrayList<String>();
    
    for(Object sg : getOrder().getShippingGroups()){
      
      if(sg instanceof HardgoodShippingGroup){ 
        Collection rels = ((HardgoodShippingGroup) sg).getCommerceItemRelationships();
        
        if(rels == null || rels.size() == 0){
          emptyShippingGroupIds.add(((HardgoodShippingGroup)sg).getId());
        }
      }
    }
    
    // Remove the shipping groups with no relationships.
    for(String id : emptyShippingGroupIds){
      try{
        getOrder().removeShippingGroup(id);
      }
      catch(Exception e){
        if(isLoggingError()){
          logError("There was a problem removing the shipping group with id " 
                     + id + " from the order " + getOrder(), e);
        }
      }
    }
  }

  /**
   * Overrides base behavior to return non gift hardgood shipping group. If there is
   * no such shipping group in the order the new one is created and added to the order.
   *
   * @return a <code>ShippingGroup</code> value.
   * @beaninfo description: The shipping group used during modifications to shipping groups.
   *
   */
  public ShippingGroup getShippingGroup() {
    ShippingGroup sg = getShippingGroupManager().getFirstNonGiftHardgoodShippingGroup(getOrder());
    
    if (sg == null)
    {
      try {
        sg = getShippingGroupManager().createShippingGroup();
        getOrder().addShippingGroup(sg);
      } 
      catch (CommerceException ex) {
        if (isLoggingError()) {
          logError("Commerce Exception occur: ", ex);
        }
      }
    }
    return sg;
  }

  /**
   * Checks whether order contains multiple non-gift shipping groups with relationships and
   * if so moves items from non gift shipping groups to the default shipping group.
   *
   * @throws ServletException if an error occurs
   */
  public void moveNonGiftItemsToDefaultShippingGroup() throws ServletException {
    
    // Determine the case when user switches from multiple shipping checkout to express checkout
    // with default shipping address, if so then move all commerce items from other hardgood
    // shipping groups to default one. Gift shipping groups are not modified.
    if (((StoreShippingGroupManager)getShippingGroupManager()).
          isMultipleNonGiftHardgoodShippingGroupsWithRelationships(getOrder())){
      
      // get default shipping group
      ShippingGroup defaultShippingGroup = getShippingGroup();
      String defaultShippingGroupId = defaultShippingGroup.getId();

      List shippingGroups = getShippingGroupManager().getNonGiftHardgoodShippingGroups(getOrder());
      
      for (Iterator sgIter = shippingGroups.iterator();sgIter.hasNext();) {
        ShippingGroup sg = (ShippingGroup)sgIter.next();
        
        if (!defaultShippingGroupId.equals(sg.getId())) {
          // Not default shipping group so move all items from it to default shipping group.
          try {
            List cis = getCommerceItemManager().getCommerceItemsFromShippingGroup(sg);

            for (Iterator cisIter = cis.iterator(); cisIter.hasNext();) {
              CommerceItem ci = (CommerceItem)cisIter.next();
              
              ShippingGroupCommerceItemRelationship rel =
                getShippingGroupManager().getShippingGroupCommerceItemRelationship(getOrder(),
                                                                                   ci.getId(),
                                                                                   sg.getId());

                getExpressCheckoutHelper().moveItemBetweenShippingGroups(getOrder(),
                                                                         ci.getId(),
                                                                         rel.getQuantity(), 
                                                                         sg.getId(), 
                                                                         defaultShippingGroupId);
            }
          } 
          catch (CommerceException ex) {
            throw new ServletException(ex);
          }
        }
      }
    }
  }

  /**
   * postExpressCheckout is for work that must happen after expressCheckout.
   * 
   * @param pRequest the servlet's request.
   * @param pResponse the servlet's response.
   * 
   * @throws ServletException - if there was an error while executing the code.
   * @throws IOException - if there was an error with servlet io.
   */
  @Override
  public void postExpressCheckout(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    super.postExpressCheckout(pRequest, pResponse);
    
    if (mCheckoutProgressStates != null && !getFormError()) {
      mCheckoutProgressStates.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CONFIRM.toString());
    }
  }
}
