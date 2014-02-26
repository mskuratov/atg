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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.claimable.ClaimableManager;
import atg.commerce.inventory.InventoryManager;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderTools;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupManager;
import atg.commerce.order.PaymentGroupOrderRelationship;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.Relationship;
import atg.commerce.order.RelationshipNotFoundException;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.order.purchase.ShippingGroupContainerService;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.PricingTools;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.order.StorePaymentGroupManager;
import atg.projects.store.payment.StoreStoreCredit;
import atg.projects.store.profile.StoreProfileTools;
import atg.projects.store.profile.StorePropertyManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.userprofiling.Profile;

/**
 * Store implementation of the purchase process helper for billing sub-process.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreBillingProcessHelper.java#4 $$Change: 788278 $ 
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreBillingProcessHelper extends StorePurchaseProcessHelper {
  
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreBillingProcessHelper.java#4 $$Change: 788278 $";

  //-------------------------------------
  // Constants
  private static final String MY_RESOURCE_NAME = "atg.commerce.order.purchase.PurchaseProcessResources";
  private static ResourceBundle sResourceBundle = 
    LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  /**
   * Missing required address property.
   */
  protected static final String MSG_MISSING_REQUIRED_ADDRESS_PROPERTY = "missingRequiredAddressProperty";

  /**
   * Address property name map.
   */
  Map mAddressPropertyNameMap;

  /**
   * Wrong nickname length message key.
   */
  protected static final String MSG_NICKNAME_WRONG_LENGTH = "nickNameWrongLength";

  /**
   * Duplicate CC nickname message key.
   */
  protected static final String MSG_DUPLICATE_CC_NICKNAME = "duplicateCCNickname";

  /**
   * Store Credit payment group type name.
   */
  protected static final String SC_PAYMENT_GROUP_TYPE_NAME = "storeCredit";

  /**
   * Online credit insufficient.
   */
  protected static final String ONLINE_CREDIT_INSUFFICIENT = "onlineCreditInsufficient";

  /**
   * Store credit payment group error message key.
   */
  protected static final String STORE_CREDIT_ERROR = "storeCreditPaymentGroupError";

  /**
   * Invalid verification number message key.
   */
  protected static final String VERIFICATION_NUMBER_INVALID = "invalidCreditCardVerificationNumber";
  
  /**
   * New address constant.
   */
  protected static final String NEW_ADDRESS = "NEW";

  /**
   * New credit card constant.
   */
  protected static final String NEW_CREDIT_CARD = "NEW";
  
  public static final String AMOUNT_REMAINING_PROP_NAME = "amountRemaining";

  public static final String AMOUNT_AUTHORIZED_PROP_NAME = "amountAuthorized";

  //---------------------------------------------------------------------------
  // property: paymentGroupManager
  //---------------------------------------------------------------------------
  PaymentGroupManager mPaymentGroupManager;

  /**
   * Set the PaymentGroupManager property.
   * 
   * @param pPaymentGroupManager a <code>PaymentGroupManager</code> value.
   */
  public void setPaymentGroupManager(PaymentGroupManager pPaymentGroupManager) {
    mPaymentGroupManager = pPaymentGroupManager;
  }

  /**
   * Return the PaymentGroupManager property.
   * 
   * @return a <code>PaymentGroupManager</code> value.
   */
  public PaymentGroupManager getPaymentGroupManager() {
    return mPaymentGroupManager;
  }
  
  //---------------------------------------------------------------------------
  // property: shippingGroupContainerService
  //---------------------------------------------------------------------------
  private ShippingGroupContainerService mShippingGroupContainerService;
  
  /** 
   * @param pShippingGroupContainerService A new ShippingGroupContainerService.
   */
  public void setShippingGroupContainerService(ShippingGroupContainerService pShippingGroupContainerService){
    mShippingGroupContainerService = pShippingGroupContainerService;
  }
  
  /** 
   * @return Returns the ShippingGroupContainerService.
   */
  public ShippingGroupContainerService getShippingGroupContainerService(){
    return mShippingGroupContainerService;
  }

  //---------------------------------------------------------------------------
  // ResourceBundle support
  //---------------------------------------------------------------------------

  /**
   * @return the error message ResourceBundle.
   */
  protected ResourceBundle getResourceBundle() {
    return sResourceBundle;
  }

  /**
   * @return the name of the error message ResourceBundle.
   */
  protected String getResourceBundleName() {
    return MY_RESOURCE_NAME;
  }

 /**
   * Checks to see if user is paying with a stored credit card.
   * 
   * @param pOnlineCreditIds the online credit cards ids.
   * 
   * @return true if success, otherwise false.
   */
  protected boolean isPayingWithOnlineCredit(String[] pOnlineCreditIds) {
    if ((pOnlineCreditIds != null) && (pOnlineCreditIds.length > 0)) {
      return true;
    }
    return false;
  }

  /**
   * This method saves the credit card from the payment group to the profile.
   * And Save credit card as profile default if it is.
   * 
   * @param pOrder the order.
   * @param pProfile the profile.
   * @param pCreditCardNickName the credit card nick name.
   */
  public void saveCreditCardToProfile(Order pOrder, RepositoryItem pProfile, String pCreditCardNickName) {

    String creditCardNickName = pCreditCardNickName;
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
    StoreProfileTools profileTools = (StoreProfileTools) orderTools.getProfileTools();

    // Copy the credit card to the profile
    CreditCard card = getCreditCard(pOrder);
    
    // false - user must be paying whole order amount with online credit.
    // true - some remaining amount is paid by credit card
    if(isPaymentGroupOrderRelationShipExist(card)) {

      if (StringUtils.isBlank(creditCardNickName)) {
        creditCardNickName = orderTools.createCreditCardNickname(card);
      }

      orderTools.copyCreditCardToProfile(pProfile, pOrder, creditCardNickName);

      // If user does not have a default credit card, then save this one as default.
      // Note: this will always be true for anonymous users.
      if (profileTools.getDefaultCreditCard(pProfile) == null) {
        profileTools.setDefaultCreditCard(pProfile, creditCardNickName);
      }
    }
  }

  /**
   * This method saves the Billing Address to the profile.
   * 
   * @param pOrder the order.
   * @param pProfile the profile.
   * @param pBillingAddressNickname the billing address nickname.
   */
  public void saveBillingAddressToProfile(Order pOrder, 
                                          RepositoryItem pProfile, 
                                          String pBillingAddressNickname) {
    // Save the billing info as a possible shipping address
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
    StoreProfileTools profileTools = (StoreProfileTools) orderTools.getProfileTools();
    CreditCard card = getCreditCard(pOrder);

    try {
      String billingAddressNickname = pBillingAddressNickname;

      if (StringUtils.isBlank(billingAddressNickname)) {
        billingAddressNickname = 
          profileTools.getUniqueShippingAddressNickname(card.getBillingAddress(), pProfile, null);
      }

      orderTools.saveAddressToAddressBook(pProfile, card.getBillingAddress(), billingAddressNickname);
    } 
    catch (CommerceException ce) {
      if (isLoggingError()) {
        logError(LogUtils.formatMinor(""), ce);
      }
    }
  }

  /**
   * Saves the address on the credit card to the user's default billing address.
   * 
   * @param pProfile the profile.
   * @param pCreditCard the credit card.
   */
  public void saveDefaultBillingAddress(RepositoryItem pProfile, CreditCard pCreditCard) {
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
    StoreProfileTools profileTools = (StoreProfileTools) orderTools.getProfileTools();
    StorePropertyManager pm = (StorePropertyManager) profileTools.getPropertyManager();

    if (pProfile.getPropertyValue(pm.getBillingAddressPropertyName()) == null) {
      if (pCreditCard != null) {
        if (isLoggingDebug()) {
          logDebug("Saving default billing address.");
        }

        orderTools.saveAddressToDefaultBilling(pCreditCard.getBillingAddress(), pProfile);
      }
    }
  }


  /**
   * This method checks to see if the user chose a profile address. If so, the
   * address is copied from the address book to the credit card.
   * 
   * @param pCard the card.
   * @param pStoredAddressSelection the stored address selection.
   * @param pProfile the profile.
   * @param pOrder the order.
   * 
   * @throws CommerceException indicates that a severe error occured while performing a commerce operation.
   */
  protected void addBillingAddressToCard(CreditCard pCard, 
                                         boolean pUsingStoredAddress, 
                                         String pStoredAddressSelection,
                                         RepositoryItem pProfile, 
                                         Order pOrder)
    throws CommerceException {

    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();

    // If user is using a stored address with the new credit card, copy the address book 
    // address to this credit card.

    if (pUsingStoredAddress) {
      if (isLoggingDebug()) {
        logDebug("Copying address: " + pStoredAddressSelection + " to credit card.");
      }

      // User chose a stored address, copy it from the address book to the current CC payment group.
      try {
        
        // User entered the card info, but is copying the profile address to card. We need to
        // preserve the name on card setting. The "copyAddress" method will overwrite the name on
        // card. Don't want to override, b/c not applicable to other cases of copyAddress.
        StorePropertyManager pm = (StorePropertyManager) orderTools.getProfileTools().getPropertyManager();
        String firstname = null, middlename = null, lastname = null;
        
        // Look in the  the ShippingGroupContainerService.shippingGroupMap for addresses.
        Map addresses = getShippingGroupContainerService().getShippingGroupMap();
        Object shippingGroup = addresses.get(pStoredAddressSelection);
          
        if(shippingGroup instanceof HardgoodShippingGroup) {
          Address storedAddress = ((HardgoodShippingGroup) shippingGroup).getShippingAddress();

          // Save the name
          firstname = storedAddress.getFirstName();
          middlename = storedAddress.getMiddleName();
          lastname = storedAddress.getLastName();
            
          // Copy address
          orderTools.copyAddress(storedAddress, pCard.getBillingAddress());
        }
           
        // Copy preserved details
        pCard.getBillingAddress().setFirstName(firstname);
        pCard.getBillingAddress().setMiddleName(middlename);
        pCard.getBillingAddress().setLastName(lastname);
        
      } 
      catch (CommerceException ce) {
        if (isLoggingError()) {
          logError(LogUtils.formatMajor("Error copying address: "), ce);
        }
        throw ce;
      }
    }
    else {
      if (isLoggingDebug()) {
        logDebug("No profile address selected, user adding a new address.");
      }
    }
 }

  /**
   * Utility method to copy credit card from profile to order.
   *
   * @param pCreditCard - Instance of Credit Card Payment Group.
   * @param pNickname - nickname profile stored credit card nick name.
   * @param pProfile -  Instance of Profile Repository.
   * @param pUserLocale - Locale.
   */
  protected void copyCreditCardFromProfile(CreditCard pCreditCard, 
                                           String pNickname, 
                                           RepositoryItem pProfile, 
                                           Locale pUserLocale) {
    if (isLoggingDebug()) {
      logDebug("Copying credit card from profile");
    }

    try {
      StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
      StoreProfileTools profileTools = (StoreProfileTools) orderTools.getProfileTools();

      profileTools.copyCreditCardToPaymentGroup(pNickname, pCreditCard, pProfile, pUserLocale);
    } 
    catch (Exception e) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error copying credit card from profile: "), e);
      }
    }
  }

  /**
   * This method verifies if the online credit(s) total is sufficient to cover
   * the order total. Note the need to calculate the true amount remaining
   * by subtracting the amount authorized.
   *
   * The uncovered amount of the order total is returned.
   *
   * @param pOnlineCreditIds the online credits ids.
   * @param pOrder the order.
   * 
   * @return true if success, otherwise false.
   */
  protected double validateSufficientOnlineCredit(String[] pOnlineCreditIds, Order pOrder) {

    if (isLoggingDebug()) {
      logDebug("Checking to see online credit amount will cover order total");
    }

    if (!isPayingWithOnlineCredit(pOnlineCreditIds)) {
      return pOrder.getPriceInfo().getTotal();
    }

    StorePaymentGroupManager paymtGrpMgr = (StorePaymentGroupManager) getPaymentGroupManager();
    List onlineCredits = paymtGrpMgr.getOnlineCredits(pOnlineCreditIds);
    Iterator itr = onlineCredits.iterator();
    
    Double amountRemaining = null;
    Double amountAuthorized = null;
    
    double totalCredit = 0.00;
    double orderTotal = pOrder.getPriceInfo().getTotal();

    while (itr.hasNext()) {
      RepositoryItem onlineCredit = (RepositoryItem) itr.next();
      amountRemaining = (Double) onlineCredit.getPropertyValue(AMOUNT_REMAINING_PROP_NAME);
      amountAuthorized = (Double) onlineCredit.getPropertyValue(AMOUNT_AUTHORIZED_PROP_NAME);
      totalCredit += (amountRemaining.doubleValue() - amountAuthorized.doubleValue());
    }

    if (isLoggingDebug()) {
      logDebug("Order total: " + orderTotal);
      logDebug("Online credit total: " + totalCredit);
    }

    if (orderTotal > totalCredit) {
      return orderTotal - totalCredit;
    }

    return 0;
  }

  /**
   * Get the remaining Amount from the order.
   * 
   * @throws atg.commerce.CommerceException if commerce error occurs.
   */
  public double getOrderRemaningAmount(Order pOrder) throws CommerceException {

    Order order = pOrder;
    PricingTools pricingTools = getOrderManager().getOrderTools().getProfileTools().getPricingTools();
    
    double usedStoreCreditAmount = 0;
    double orderTotal = order.getPriceInfo().getTotal();
    double amountAppliedToOrder;
    double orderReminingAmount;

    if(order.getPaymentGroupRelationshipCount() > 0) {

      List groups = order.getPaymentGroupRelationships();
      
      if (groups != null) {
        Iterator pgOrderRelsIterator = groups.iterator();
        
        while(pgOrderRelsIterator.hasNext()) {

          PaymentGroupOrderRelationship pgOrderRel = 
            (PaymentGroupOrderRelationship) pgOrderRelsIterator.next();
          
          PaymentGroup paymentGroup =  pgOrderRel.getPaymentGroup();
          
          if(paymentGroup.getPaymentGroupClassType().equals(SC_PAYMENT_GROUP_TYPE_NAME)) {

            if(pgOrderRel.getRelationshipType() == RelationshipTypes.ORDERAMOUNT) {

              amountAppliedToOrder = (pgOrderRel.getAmount() > 0) ? 
                                       pgOrderRel.getAmount() : 
                                       ((StoreStoreCredit)paymentGroup).getAmountAppliedToOrder();
                  
              usedStoreCreditAmount += amountAppliedToOrder;
              
              if (isLoggingDebug()) {
                logDebug("Current Store Credit PaymentGroup  Amount: "+ amountAppliedToOrder);
              }

            } 
            else {
              if (isLoggingDebug()) {
                logDebug("All Order Amount paid by store credits. Order Remaining Amount is 0");
              }
              return 0;
            }
          }
        }
      }
    }
    orderReminingAmount = pricingTools.round(orderTotal - usedStoreCreditAmount);

    if (isLoggingDebug()) {
      logDebug("Total Amount of StoreCredits PaymentGroup = " + usedStoreCreditAmount);
      logDebug("Order Remaning Amount = " + orderReminingAmount);
    }

    return orderReminingAmount;
  }

  /**
   * @return true if credit cards are new, otherwise false.
   */
  protected boolean isNewCreditCards(Order pOrder) {
    int size;

    if ((pOrder.getPaymentGroups() != null) && (pOrder.getPaymentGroups().size() > 0)) {
      size = pOrder.getPaymentGroups().size();

      for (int i = 0; i < size; i++) {
        if ((pOrder.getPaymentGroups().get(i)) instanceof CreditCard) {
          if (!isCreditCardEmpty((CreditCard) pOrder.getPaymentGroups().get(i))) {
            return true;
          }
        }
      }
    }

    return false;
  }

  /**
   * Checks to see if a CreditCard object is empty.  Empty means that certain necessary 
   * fields are missing.  The properties that it checks for are those specified by the 
   * creditCardProperties String array.
   *
   * <p>
   *   This behavior can be overridden by making additions to the String array
   *   creditCardProperties, or if necessary extending this method.
   * </p>
   * @param pCreditCard a value of type 'CreditCard'.
   * 
   * @return true if the payment group is empty.
   * 
   * @deprecated this method has been moved to CommerceProfileTools.
   */
  protected boolean isCreditCardEmpty(CreditCard pCreditCard) {
    StoreProfileTools profileTools = (StoreProfileTools)getOrderManager().getOrderTools().getProfileTools();
    return profileTools.isCreditCardEmpty(pCreditCard);
  }

  /**
   * Remove any store credit payment groups from order.
   * 
   * @param pOrder.
   *
   **/
  protected void removeStoreCreditPaymentGroups(Order pOrder) {
    ((StorePaymentGroupManager)getPaymentGroupManager()).removeStoreCreditPaymentGroups(pOrder);
  }

  /**
   * Initialize Store Credit Payment Group, add amount to the payment group and add remainng amount
   * to other payment groups CreditCard.
   * 
   * @param pOrder - the order to set-up.
   * @param pProfile - the profile to which the order belongs.
   * @param pOnlineCreditIds - the online credit cards ids.
   * 
   * @throws CommerceException if an error occurs
   * @throws RepositoryException if an error occurs
   * @throws IOException if an error occurs
   * @throws ServletException if an error occurs
   */
  public void setupStoreCreditPaymentGroupsForOrder(Order pOrder, RepositoryItem pProfile, String[] pOnlineCreditIds)
    throws CommerceException, RepositoryException, IOException, ServletException {

    // In case there are lingering store credit payment groups we need to remove them
    removeStoreCreditPaymentGroups(pOrder);

    // First preference given to any store credits, then credit cards
    // If user is paying with online credit initialize payment groups

    if (isPayingWithOnlineCredit(pOnlineCreditIds)) {
      initializePaymentGroups(pOrder, pProfile, pOnlineCreditIds);

    }

    addOrderAmountRemainingToCreditPaymentGroup(pOrder);
  }

 /**
   * Initializes the store credit payment method(s) based on the store credits the user chose.
   *
   * @throws ServletException If servlet exception occurs.
   * @throws IOException If IO exception occurs.
   * 
   * @return true on success, otherwise false.
   */
  protected boolean initializePaymentGroups(Order pOrder, RepositoryItem pProfile,String[] pOnlineCreditIds )
    throws CommerceException, ServletException, IOException {

    if (isLoggingDebug()) {
      logDebug("Store credit being used, initializing store credit payment groups.");
    }

    StorePaymentGroupManager paymtGrpMgr = (StorePaymentGroupManager) getPaymentGroupManager();
    return paymtGrpMgr.initializePaymentMethods(pProfile, pOrder, pOnlineCreditIds);

  }

  /**
   * Add Credit Card Verification Number to the CreditCard Payment Group.
   * 
   * @param pOrder - the order that the credit card information is related to.
   * @param pCreditCardVerificationNumber - the credit card's verification number. 
   */
   public void addCreditCardAuthorizationNumber(Order pOrder,String pCreditCardVerificationNumber) {

     StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
     if (getStoreConfiguration().isRequireCreditCardVerification()) {

      if(orderTools.validateCreditCardAuthorizationNumber(pCreditCardVerificationNumber)) {
        orderTools.setCreditCardVerificationNumber(pOrder, pCreditCardVerificationNumber);
      }
    }
  }

  /**
   * Verify the Credit Card verification number.
   * 
   * @param pCreditCardVerificationNumber - the credit card's verification number.
   * 
   * @return true if StoreConfiguration.isRequireCreditCardVerification is false and
   *         given authorization number is valid, otherwise return false.
   */
  public boolean validateCreditCardAuthorizationNumber(String pCreditCardVerificationNumber) {
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();

    if (getStoreConfiguration().isRequireCreditCardVerification()) {
      return orderTools.validateCreditCardAuthorizationNumber(pCreditCardVerificationNumber);
    }
    return true;
  }

  /**
   * Copy credit card from profile to order if paying with profile credit card.
   * 
   * @param pCreditCard - Instance of Credit Card Payment Group.
   * @param pStoredCreditCardName - Nickname profile stored credit card nick name.
   * @param pProfile - Instance of Profile Repository.
   * @param pUserLocale - The user's locale.
   */
  public void addCreditCardDetails(CreditCard pCreditCard, 
                                   boolean pUsingStoredCreditCard, 
                                   String pStoredCreditCardName,
                                   RepositoryItem pProfile, 
                                   Locale pUserLocale) {
    if (pUsingStoredCreditCard) {
      // If user is paying with a stored credit card, copy from profile.
      copyCreditCardFromProfile(pCreditCard, pStoredCreditCardName, pProfile, pUserLocale);
    } 
  }

  /**
   * Utility method to fetch credit card and set properties from page.
   *
   * @return credit card for this order.
   */
  public CreditCard getCreditCard(Order pOrder) {
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
    return orderTools.getCreditCard(pOrder);
  }

  /**
   * Added Remaining Order Amount relationship To Credit Card PaymentGroup.
   * Remove remaining Order Amount relationship if all payment payed by other 
   * payment groups like Online Credit.
   * 
   * @param pOrder - the order to process.
   * 
   * @throws CommerceException
   * @throws InvalidParameterException
   */
  public void addOrderAmountRemainingToCreditPaymentGroup(Order pOrder)
    throws CommerceException,InvalidParameterException {

    CreditCard creditCard = getCreditCard(pOrder);
    double orderRemainingAmount = getOrderRemaningAmount(pOrder);
    
    if( orderRemainingAmount <= 0 ) {
      try {
       getOrderManager().removeRemainingOrderAmountFromPaymentGroup(pOrder, creditCard.getId());
      } 
      catch (RelationshipNotFoundException exc) {
        if (isLoggingDebug()) {
          logDebug("Credit Card RelationShip not found:");
        }
       return;
      }
    } 
    else {
      try {
        creditCard.getOrderRelationship();
      } 
      catch (RelationshipNotFoundException exc) {
        getOrderManager().addRemainingOrderAmountToPaymentGroup(pOrder, creditCard.getId());
      } 
      catch (InvalidParameterException exc) {
        throw exc;
      }
    }
  }

  /**
   *  Added remaining order amount to the payment group for given
   *  <code>pStoredCreditCardName</code>. If <code>pStoredCreditCardName</code>
   *  is not empty, then copy credit card details from <code>pProfile</code> 
   *  to <code>pOrder</code>.
   *
   * @param pOrder the order to process.
   * @param pProfile user profile.
   * @param pStoredCreditCardName credit card nickname.
   * @param pUserLocale the user locale.
   * 
   * @return true if credit card details added to the order.
   * 
   * @throws CommerceException
   * @throws InvalidParameterException
   */
  public boolean setupCreditCardPaymentGroupsForOrder(Order pOrder, 
                                                      RepositoryItem pProfile, 
                                                      boolean pUsingStoredCreditCard,
                                                      String pStoredCreditCardName, 
                                                      Locale pUserLocale )
    throws CommerceException, InvalidParameterException {

    // to fix issue CRS-167694 we should
    // check if another credit card already exists with this
    // relationship type. 
    boolean exists = false;
    List paymentRelationships = pOrder.getPaymentGroupRelationships();
    
    for(Object paymentRelationship : paymentRelationships) {
      if( ((Relationship)paymentRelationship).getRelationshipType() == RelationshipTypes.ORDERAMOUNTREMAINING ) {
        exists = true;
        break;
      }
    }

    if(!exists) {
      addOrderAmountRemainingToCreditPaymentGroup(pOrder);
    } 
    
    if (pStoredCreditCardName != null) {
      addCreditCardDetails(
        getCreditCard(pOrder), pUsingStoredCreditCard, pStoredCreditCardName, pProfile, pUserLocale);
      
      return true;
    } 
    else {
      return false;
    }
  }

  /**
   * Run the pipeline which should be executed at the last of the billing process.
   *
   * @param pOrder - the order to re-price.
   * @param pPricingModels - the set of all pricing models for the user (item, order, shipping, tax).
   * @param pLocale - the locale that the order should be priced within.
   * @param pProfile - the user who owns the order.
   * @param pExtraParameters -a Map of extra parameters to be used in the pricing.
   * 
   * @throws atg.service.pipeline.RunProcessException if error running pipeline process.
   */
  protected PipelineResult runProcessMoveToConfirmation(Order pOrder, 
                                                        PricingModelHolder pPricingModels,
                                                        String pMoveToConfirmationChainId,
                                                        Locale pLocale, 
                                                        RepositoryItem pProfile, 
                                                        Map pExtraParameters)
    throws RunProcessException {

    OrderManager orderManager = getOrderManager();
    OrderTools orderTools =  orderManager.getOrderTools();
    CatalogTools catalogTools = getOrderManager().getOrderTools().getCatalogTools();
    InventoryManager inventoryManager = orderTools.getInventoryManager();

    HashMap params = new HashMap(11);

    params.put(PipelineConstants.CATALOGTOOLS, catalogTools);
    params.put(PipelineConstants.INVENTORYMANAGER, inventoryManager);

    PipelineResult result = runProcess(pMoveToConfirmationChainId,
                                       pOrder, 
                                       pPricingModels, 
                                       pLocale, 
                                       pProfile,
                                       params, pExtraParameters);
    return result;
  }

  /**
   * Utility method to check if user's NickName meets the minimum and maximum length.
   *
   * @param pNickName - the nickname to validate.
   * 
   * @return true if nickname passes minimum/maximum validation checks, otherwise false.
   */
  public boolean isValidNickNameLength(String pNickName, int pMinNickNameLength, int pMaxNickNameLength) {
    int nickNameLength = pNickName.length();

    // Check to see if NickName.length is between minimum and maximum values.
    if ((nickNameLength >= pMinNickNameLength) && (nickNameLength <= pMaxNickNameLength)) {
      return true;
    }

    return false;
  }

  /**
   * This method validates the credit card nickname if one is required.
   * 
   * @param pOrder - the corresponding order.
   * @param pProfile - the user's profile.
   * @param pCreditCardNickname - the credit card's nickname.
   * @param pMinNickNameLength - the minimum length allowed for a nickname.
   * @param pMaxNickNameLength - the maximum length allowed for a nickname.
   * 
   * @return true if success else return false.
   */
  public boolean validateCreditCardNicknameInput(Order pOrder, 
                                                 RepositoryItem pProfile, 
                                                 String pCreditCardNickname,
                                                 int pMinNickNameLength,
                                                 int pMaxNickNameLength)
    throws StorePurchaseProcessException {

    // Check for a valid nickname only if the save credit card option is checked and the
    // registration is taking place or the user is already logged in.
    Profile profile = (Profile) pProfile;
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
    StoreProfileTools profileTools = (StoreProfileTools) orderTools.getProfileTools();
    StorePropertyManager pmgr = (StorePropertyManager) profileTools.getPropertyManager();

    String nickname = pCreditCardNickname;

    // If nickname is not supplied by the user, create a new one
    if (StringUtils.isBlank(nickname)) {
      nickname = orderTools.createCreditCardNickname(getCreditCard(pOrder));
    }
    boolean isValidNickNameLength = 
      isValidNickNameLength(nickname.trim(), pMinNickNameLength, pMaxNickNameLength);
    
    if (!isValidNickNameLength) {
      // BUGS-FIXED: 136133 :: case where nick name length exceeds max 42 chars
      if (isLoggingDebug()) {
        logDebug("Credit card nickname Length is invalid, must be 1-42 characters, adding form exception.");
      }
      
      throw new StorePurchaseProcessException(MSG_NICKNAME_WRONG_LENGTH);
    } 
    else {
      if (profileTools.isDuplicateCreditCardNickname(profile, nickname)) {
        if (isLoggingDebug()) {
          logDebug("Credit card nickname is already used, adding form exception.");
        }

        throw new StorePurchaseProcessException(MSG_DUPLICATE_CC_NICKNAME);
      }
    }

    return true;
  }

  /**
   * Verify, whether Order Relationship exists in the given Payment Group or not.
   * 
   * @param pPaymentGroup - the payment group to process.
   * 
   * @return true if relation ship found, otherwise return false.
   */
  public boolean isPaymentGroupOrderRelationShipExist(PaymentGroup pPaymentGroup) {
    try {
      pPaymentGroup.getOrderRelationship();
    } 
    catch (RelationshipNotFoundException rnfexc) {
      if (isLoggingDebug()) {
        logDebug("BillingProcessHelper: No Relationship Found for credit card" +
                " is paying with a new Credit card.");
        logDebug(rnfexc);
      }

      return false;
    }  
    catch (InvalidParameterException exc) {
      if (isLoggingDebug()) {
        logDebug("BillingProcessHelper: "+ exc);
      }
      return false;
    }
    return true;
  }
 
  /**
   * This method returns store credits that for the given profile that can be used for the order.
   * 
   * @param pProfile profile repository item.
   * @return array of store credit IDs.
   */
  public String[] getStoreCreditIds(RepositoryItem pProfile) {
    String[] availableCreditIds = null;
    ClaimableManager cm = ((StorePaymentGroupManager)getPaymentGroupManager()).getClaimableManager();
    List storeCredits = null;

    if (isLoggingDebug()) {
      logDebug("Checking for store credits for " + pProfile.getRepositoryId());
    }
    
    try {
    storeCredits = cm.getStoreCreditsForProfile(pProfile.getRepositoryId(), false);

    if ((storeCredits != null) && (storeCredits.size() > 0)) {
      
      availableCreditIds = new String[storeCredits.size()];
      int i = 0;
      
      for(Object storeCredit : storeCredits) {
        availableCreditIds[i++] = ((RepositoryItem)storeCredit).getRepositoryId();
      }
    }
    } 
    catch(CommerceException ce) {
      if (isLoggingError()){
        logError("Cannot retrieve store credits for the given profile", ce);  
      }      
    }
    
    return availableCreditIds;
  }

  /**
   * Check required fields for the new credit card.
   * 
   * @param card CreditCard object.
   * @param pVerificationNumber - new verification number for credit card.
   * @param pNickname - credit card's nickname.
   * 
   * @return false if one of the required fields is null or empty.
   */
  protected boolean validateCreditCardRequiredFields(CreditCard card, String pVerificationNumber, String pNickname) {
    return !(StringUtils.isEmpty(card.getCreditCardNumber()) || 
            StringUtils.isEmpty(card.getCreditCardType()) ||
            StringUtils.isEmpty(card.getExpirationMonth()) || 
            StringUtils.isEmpty(card.getExpirationYear()) ||
            StringUtils.isEmpty(pVerificationNumber) || 
            StringUtils.isEmpty(pNickname));
  }
}
