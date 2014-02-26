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

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupRelationship;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.purchase.CommitOrderFormHandler;
import atg.commerce.order.purchase.ShippingGroupContainerService;
import atg.commerce.pricing.PricingConstants;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.profile.StoreProfileTools;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


/**
 * Extends the default CommitOrderFormHandler as to implement custom preCommitOrder
 * and postCommitOrder functionality.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreCommitOrderHandler.java#5 $$Change: 796495 $ 
 * @updated $DateTime: 2013/03/13 06:59:28 $$Author: dstewart $
 */
public class StoreCommitOrderHandler extends CommitOrderFormHandler {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreCommitOrderHandler.java#5 $$Change: 796495 $";

  /**
   * Verification number invalid message key.
   */
  protected static final String VERIFICATION_NUMBER_INVALID = "invalidCreditCardVerificationNumber";

  /**
   * Shipping method invalid message key.
   */
  protected static final String SHIPPING_METHOD_INVALID = "invalidShippingMethod";
  
  /**
   * Confirm Email Address invalid message key.
   */
  protected static final String CONFIRM_EMAIL_INVALID = "invalidConfirmEmailAddress";
  
  /**
   * Confirm Email Address already exists message key.
   */
  protected static final String CONFIRM_EMAIL_ALREADY_EXISTS = "confirmEmailAddressAlreadyExists";
  
  /**
   * property: checkoutProgressStates
   */
  private CheckoutProgressStates mCheckoutProgressStates;
  
  /**
   * @return the checkout progress states.
   */
  public CheckoutProgressStates getCheckoutProgressStates() {
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
   * property: commitOrderHelper
   */
  protected StoreCommitOrderProcessHelper mCommitOrderHelper;
  
  /**
   * @return the Order Helper component.
   */
  public StoreCommitOrderProcessHelper getCommitOrderHelper() {
    return mCommitOrderHelper;
  }

  /**
   * @param pCommitOrderHelper - the order helper component to set.
   */
  public void setCommitOrderHelper(StoreCommitOrderProcessHelper pCommitOrderHelper) {
    mCommitOrderHelper = pCommitOrderHelper;
  }
  
  /**
   * property: confirmEmailAddress
   */
  protected String mConfirmEmailAddress;
  
  /**
   * @return the confirm e-mail address.
   */
  public String getConfirmEmailAddress() {
    return mConfirmEmailAddress;
  }

  /**
   * @param pConfirmEmailAddress - the confirm e-mail address to set.
   */
  public void setConfirmEmailAddress(String pConfirmEmailAddress) {
    mConfirmEmailAddress = pConfirmEmailAddress;
  }

  /**
   * property: creditCardVerificationNumber
   */
  protected String mCreditCardVerificationNumber;
  
  /**
   * @return the credit card verification number.
   */
  public String getCreditCardVerificationNumber() {
    return mCreditCardVerificationNumber;
  }

  /**
   * @param pCreditCardVerificationNumber - the credit card verification number to set.
   */
  public void setCreditCardVerificationNumber(String pCreditCardVerificationNumber) {
    mCreditCardVerificationNumber = pCreditCardVerificationNumber;
  }
  
  /**
   * property: couponCode.
   */
  private String mCouponCode;

  /**
   * @return a coupon code to be claimed.
   */
  public String getCouponCode()
  {
    return mCouponCode;
  }
  
  /**
   * @param pCouponCode - the coupon code to set.
   */
  public void setCouponCode(String pCouponCode)
  {
    mCouponCode = pCouponCode;
  }

  /**
   * property: shippingGroupContainerService
   */
  private ShippingGroupContainerService mShippingGroupContainerService;
  
  /** 
   * @param pShippingGroupContainerService- a new ShippingGroupContainerService. 
   */
  public void setShippingGroupContainerService(ShippingGroupContainerService pShippingGroupContainerService){
    mShippingGroupContainerService = pShippingGroupContainerService;
  }
  
  /** 
   * @return Returns the ShippingGroupContainerService 
   */
  public ShippingGroupContainerService getShippingGroupContainerService(){
    return mShippingGroupContainerService;
  }
  
  /**
   * property: expiredPromotionErrorURL
   */
  private String mExpiredPromotionErrorURL = null;
  
  /**
   * @param pExpiredPromotionErrorURL - The expired promotion error redirect URL.
   */
  public void setExpiredPromotionErrorURL(String pExpiredPromotionErrorURL) {
    mExpiredPromotionErrorURL = pExpiredPromotionErrorURL;
  }
  
  /**
   * @return the expired promotion error redirect URL.
   */
  public String getExpiredPromotionErrorURL() {
    return mExpiredPromotionErrorURL;
  }
  
  /**
   * Claim the specified coupon, register a form exception if the coupon is invalid or an error occurs.
   *
   * @param pRequest - current HTTP servlet request.
   * @param pResponse - current HTTP servlet response.
   *
   * @throws ServletException if an error occurred during claiming the coupon.
   * @throws IOException if an error occurred during claiming the coupon.
   */
  private void tenderCoupon(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    try {
      boolean couponTendered = 
        ((StorePurchaseProcessHelper) getPurchaseProcessHelper()).tenderCoupon(getCouponCode(),
                                                                               (StoreOrderImpl) getOrder(),
                                                                               getProfile(),
                                                                               getUserPricingModels(),
                                                                               getUserLocale());
      if (!couponTendered) {
        String errorMessage = formatUserMessage(StorePurchaseProcessHelper.MSG_UNCLAIMABLE_COUPON,
                                                pRequest, pResponse);

        addFormException(new DropletFormException(errorMessage,
                                                  "",
                                                  StorePurchaseProcessHelper.MSG_UNCLAIMABLE_COUPON));
      }
    }
    catch (Exception exception) {
      processException(exception,
                       StoreCartProcessHelper.MSG_UNCLAIMABLE_COUPON,
                       pRequest, pResponse);
    }
  }

  /**
   * Ensures that an email address is set in the billing address, as required
   * by CyberSource.  Also assures that the profile ID associated with the order
   * is correct.
   *
   * @param pRequest - HTTP request.
   * @param pResponse - HTTP response.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if IO error occurs.
   * 
   * @see atg.commerce.order.purchase.CommitOrderFormHandler#handleCommitOrder.
   */
  @Override
  public void preCommitOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    tenderCoupon(pRequest, pResponse);

    if (getFormError()) {
      return;
    }
    
    RepositoryItem profile = getProfile();

    // Set the email provided by the anonymous shopper.
    if (profile.isTransient()) {
      String confirmEmailAddress = getConfirmEmailAddress();
      boolean isValidConfirmEmailAddress = validateEmailAddress();

      if(!isValidConfirmEmailAddress) {
        String msg = formatUserMessage(CONFIRM_EMAIL_INVALID, pRequest, pResponse);
        addFormException(new DropletFormException(msg, (String) null, CONFIRM_EMAIL_INVALID));
        return;
      }
      
      if (!StringUtils.isEmpty(confirmEmailAddress)){
        if(isEmailAlreadyRegistered(confirmEmailAddress)) {
          String msg = formatUserMessage(CONFIRM_EMAIL_ALREADY_EXISTS, pRequest, pResponse);
          addFormException(new DropletFormException(msg, (String) null, CONFIRM_EMAIL_ALREADY_EXISTS));
          return;
        }
        // only add the email address if it's not empty
        getCommitOrderHelper().addEmailToAnonymousUser(getConfirmEmailAddress(), profile);
      }      
    }

    // Check if order uses credit card for payment.
    if (isCreditCardRequired()){
      
      // Credit card is used for payment in the order, verify credit card verification number.
      if(!getCommitOrderHelper().validateCreditCardVerificationNumber(getOrder(), 
                                                                      getCreditCardVerificationNumber())) {
        String msg = formatUserMessage(VERIFICATION_NUMBER_INVALID, pRequest, pResponse);
        addFormException(new DropletFormException(msg, (String) null, VERIFICATION_NUMBER_INVALID));
        return;
      }
    }
    
    // Check if the order uses valid shipping methods.
    boolean validShippingMethods = getCommitOrderHelper().validateShippingMethods(getOrder(), 
                                                                                  profile, 
                                                                                  getUserLocale(),
                                                                                  getUserPricingModels());
    if (!validShippingMethods) {
      String msg = formatUserMessage(SHIPPING_METHOD_INVALID, pRequest, pResponse);
      addFormException(new DropletFormException(msg, (String) null, SHIPPING_METHOD_INVALID));
      return;
    }
    
    getCommitOrderHelper().addEmailToCreditCard(getOrder(), profile);
    getCommitOrderHelper().updateProfileIdOnOrder(getOrder(), profile);

    super.preCommitOrder(pRequest, pResponse);
  }

  /**
   * This overridden method checks for an 'ItemPromotionExpired' formException and sets
   * the 'commitOrderErrorURL' with the 'expiredPromotionErrorURL' to go to when this type 
   * of exception is detected.
   * 
   * @param pOrder - The order that is being committed.
   * @param pRequest - The HTTP request object.
   * @param pResponse - The HTTP response object.
   * 
   * @throws ServletException
   * @throws IOException
   */
  @Override
  public void commitOrder(Order pOrder, 
                          DynamoHttpServletRequest pRequest,
                          DynamoHttpServletResponse pResponse) throws ServletException, IOException {
  
    super.commitOrder(pOrder, pRequest, pResponse);
    
    if (getFormExceptions().size() > 0) {
      
      for (int i = 0; i < getFormExceptions().size(); i++) {
        Object obj = getFormExceptions().get(i);
        
        if (obj instanceof DropletException) {
          DropletException de = (DropletException) getFormExceptions().get(i);
          
          if (de.getErrorCode().equals("ItemPromotionExpired")) {  
            vlogDebug("An ItemPromotionExpired form exception has been detected, setting commitOrderErrorURL to {0}",
                      getExpiredPromotionErrorURL());
            
            super.setCommitOrderErrorURL(getExpiredPromotionErrorURL());
          }
        }
      }
    }
  }
  
  /**
   * Checks whether an order uses a credit card for a payment. In case when there are
   * payment group relationships with a credit card - return true,
   * otherwise when other payment methods are used to pay for the order - return false.
   * 
   * @return true if there are payment group relationships with credit card.
   */
  public boolean isCreditCardRequired(){

    // If there is nothing to pay (order total is 0), there is no need in credit card - return false.
     if (getOrder().getPriceInfo().getTotal() == 0) {
      return false;
    }
     
    List rels = getOrder().getPaymentGroupRelationships();
    
    // Return true if there are no still payment group relationships. This can happen if 
    // express checkout is used and credit card payment group relationship will be created later.
    if (rels == null || rels.size()== 0) {
      return true;
    }
    
    for (Iterator iter = rels.iterator(); iter.hasNext();) {
      
      PaymentGroupRelationship rel = (PaymentGroupRelationship) iter.next();
      PaymentGroup paymentGroup = rel.getPaymentGroup();
      
      if (paymentGroup instanceof CreditCard) {
        return true;
      }
    }
    return false;
  }

  /**
   * Called after all processing is done by the handleCommitOrder method. This
   * method is responsible for populating the profile with the attributes on
   * the profile, such as itemsBought, lastPurchaseDate, numberOfOrders, etc.
   *
   * @param pRequest - the request object.
   * @param pResponse - the response object.
   * 
   * @exception ServletException - if an error occurs.
   * @exception IOException - if an error occurs.
   * 
   * @see atg.commerce.order.purchase.CommitOrderFormHandler#handleCommitOrder
   */
  @Override
  public void postCommitOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    if (getFormError()) {
      return;
    }
    
    StoreOrderImpl commitedOrder = (StoreOrderImpl) getShoppingCart().getLast();
    
    try {
      getCommitOrderHelper().doPostCommitOrderProcessing(commitedOrder, getProfile());
    }
    catch(RepositoryException re) {
      throw new ServletException(re);
    }
    
    super.postCommitOrder(pRequest, pResponse);
    
    // Wipe out the shipping addresses stored in the shipping group map for security reasons.
    if(getProfile().isTransient()) {
      getShippingGroupContainerService().removeAllShippingGroups();
    }
    
    if (mCheckoutProgressStates != null && !getFormError()) {
      mCheckoutProgressStates.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString());
    }
  }

  /**
   * Method to allow page access to CreditCard object.
   *
   * @return credit card with this order.
   */
  public CreditCard getCreditCard() {
    return getCommitOrderHelper().getStoreOrderTools().getCreditCard(getOrder());
  }
  
  /**
   * Validates an email address for correctness. An email is valid if it is null/blank or it passes the
   * call to validateEmailAddress in StoreProfileTools.
   *
   * @return boolean true if email address is valid.
   * 
   * @see atg.projects.store.profile.StoreProfileTools#validateEmailAddress.
   */
  private boolean validateEmailAddress() {
    boolean validEmail = false;
    String email = getConfirmEmailAddress();
    
    StoreProfileTools profileTools = 
      (StoreProfileTools)getCommitOrderHelper().getStoreOrderTools().getProfileTools();
    
    if(StringUtils.isBlank(email)) {
      validEmail = true;
    }
    else {
      validEmail = profileTools.validateEmailAddress(email);  
    }      
      
    return validEmail;
  }
  
  /**
   * Returns true if a user already exists with the given email.
   * 
   * @param pEmail - the email which a user wants to use.
   * 
   * @return true if a user already exists with the given email.
   */
  protected boolean isEmailAlreadyRegistered(String pEmail)
  { 
    // Check if user with such email already exists.
    StoreProfileTools profileTools = 
      (StoreProfileTools)getCommitOrderHelper().getStoreOrderTools().getProfileTools();
    
    RepositoryItem user = 
      profileTools.getItem(pEmail.toLowerCase(), null, profileTools.getDefaultProfileType());
    
    return user != null;
  }
}
