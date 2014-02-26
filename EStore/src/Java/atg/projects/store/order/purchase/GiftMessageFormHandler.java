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

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.purchase.PurchaseProcessFormHandler;
import atg.core.util.StringUtils;
import atg.droplet.DropletFormException;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.StoreOrderManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


/**
 * This class is used to handle form submissions from the Gift Message page.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/GiftMessageFormHandler.java#3 $$Change: 788278 $ 
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class GiftMessageFormHandler extends PurchaseProcessFormHandler {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/GiftMessageFormHandler.java#3 $$Change: 788278 $";

  /**
   * Gift message too long message key.
   */
  protected static final String GIFT_MESSAGE_TOO_LONG = "giftMessageTooLong";
  
  /**
   * Gift message from field or gift message to field empty.
   */
  protected static final String MSG_NO_FROM_TO_VALUES = "additionalInfoRequired";
  
  /**
   * No gift message message key.
   */
  protected static final String MSG_NO_GIFT_MSG = "noGiftMessage";

  /**
   * Error adding gift message message key.
   */
  protected static final String ERROR_ADDING_GIFT_MESSAGE = "errorAddingGiftMessage";
   
  /**
   * Add gify message success redirect URL.
   */
  protected String mAddGiftMessageSuccessURL;

  /**
   * Add gift message redirect URL.
   */
  protected String mAddGiftMessageErrorURL;

  /**
   * Gift message sender.
   */
  protected String mGiftMessageTo;

  /**
   * Gift message recipient.
   */
  protected String mGiftMessageFrom;

  /**
   * Gift message text.
   */
  protected String mGiftMessage;

  /**
   * Express checkout.
   */
  protected String mExpressCheckout;
  
  private String mCouponCode = null;

  /**
   * @return the coupon code
   */
  public String getCouponCode() {
    return mCouponCode;
  }

  /**
   * @param pCouponCode the coupon code to set
   */
  public void setCouponCode(String pCouponCode) {
    mCouponCode = pCouponCode;
  }

  /**
   * This method will add the gift message to the order's specialInstructions.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   *
   * @return boolean success or failure
   *
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleAddGiftMessage(DynamoHttpServletRequest pRequest, 
                                      DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Transaction tr = null;
    

    try {
      tr = ensureTransaction();
      
      //Check if session has expired, redirect to sessionExpired URL:
      if (!checkFormRedirect(null, getAddGiftMessageErrorURL(), pRequest, pResponse)) {
        return false;
      }
      
      // Claim coupon before adding gift message
      try {
        boolean couponTendered = ((StorePurchaseProcessHelper) 
          getPurchaseProcessHelper()).tenderCoupon(getCouponCode(), 
                                                   (StoreOrderImpl) getOrder(), 
                                                   getProfile(), 
                                                   getUserPricingModels(), 
                                                   getUserLocale());
        if (!couponTendered) {
          String errorMessage = 
            formatUserMessage(StorePurchaseProcessHelper.MSG_UNCLAIMABLE_COUPON, pRequest, pResponse);
          
          addFormException(new DropletFormException(errorMessage, "", 
                                                    StorePurchaseProcessHelper.MSG_UNCLAIMABLE_COUPON));
        }
      }
      catch (Exception exception) {
        processException(exception, StoreCartProcessHelper.MSG_UNCLAIMABLE_COUPON, pRequest, pResponse);
      }
      
      addGiftMessage(pRequest, pResponse);
      
    } 
    catch (CommerceException ce) {
      // invalid number given for quantity of item to add
      String msg = formatUserMessage(ERROR_ADDING_GIFT_MESSAGE, pRequest, pResponse);
      addFormException(new DropletFormException(msg, ce, ERROR_ADDING_GIFT_MESSAGE));
      
      if (isLoggingError()) {
        logError("Failed adding gift message to order", ce);
      }
    } 
    finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }

    return checkFormRedirect(getAddGiftMessageSuccessURL(), 
                             getAddGiftMessageErrorURL(), 
                             pRequest, pResponse);
  }

  /**
   * <p>Adds the gift message to the order.  It gets the gift message from the
   * <code>giftMessageTo</code>
   * <code>giftMessageFrom</code>
   * <code>giftMessage</code> parameters set on the form.
   *
   * @throws CommerceException should anything go wrong
   * 
   * @see StoreOrderManager.addGiftMessage
   * 
   * @param pRequest - HTTP request
   * @param pResponse - HTTP response
   * 
   * @throws java.io.IOException if IO error occurs
   * @throws javax.servlet.ServletException if servlet error occurs
   */
  protected void addGiftMessage(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws IOException, ServletException, CommerceException {
    
    if (!StringUtils.isBlank(getGiftMessageFrom()) && !StringUtils.isBlank(getGiftMessageTo())) {
      
      StoreOrderImpl order = (StoreOrderImpl) getOrder();
      StoreOrderManager om = (StoreOrderManager) getOrderManager();

      String message = getGiftMessage();
      
      if (message.length() > 140) {
        // Default message in case getting from bundle fails:
        String errorMsg = formatUserMessage(GIFT_MESSAGE_TOO_LONG, pRequest, pResponse);
        addFormException(new DropletFormException(errorMsg, "giftMessage", GIFT_MESSAGE_TOO_LONG));
      }

      if (!getFormError()) {
        // Let the message get set even on form error so the
        // user can see the input, and modify it. The limitation
        // is with SAP, and the message will be truncated in
        // fulfillment anyway.
        om.addGiftMessage(order, getGiftMessageTo(), getGiftMessage(), getGiftMessageFrom());
      }
    }
    else{
      // Show message that required fields are empty
      String errorMsg = formatUserMessage(MSG_NO_FROM_TO_VALUES, pRequest, pResponse);
      addFormException(new DropletFormException(errorMsg, "giftMessage", MSG_NO_FROM_TO_VALUES));
    }
  }

  /**
   * @return add gift message success redirect URL.
   */
  public String getAddGiftMessageSuccessURL() {
    return mAddGiftMessageSuccessURL;
  }

  /**
   * @param pAddGiftMessageSuccessURL add gift message success redirect URL.
   */
  public void setAddGiftMessageSuccessURL(String pAddGiftMessageSuccessURL) {
    mAddGiftMessageSuccessURL = pAddGiftMessageSuccessURL;
  }

  /**
   * @return add gift message error redirect URL.
   */
  public String getAddGiftMessageErrorURL() {
    return mAddGiftMessageErrorURL;
  }

  /**
   * @param pAddGiftMessageErrorURL add gift message error redirect URL.
   */
  public void setAddGiftMessageErrorURL(String pAddGiftMessageErrorURL) {
    mAddGiftMessageErrorURL = pAddGiftMessageErrorURL;
  }

  /**
   * @param pGiftMessageTo gift message sender.
   */
  public void setGiftMessageTo(String pGiftMessageTo) {
    mGiftMessageTo = pGiftMessageTo;
  }

  /**
   * @return mGiftMessageTo gift message sender.
   */
  public String getGiftMessageTo() {
    return mGiftMessageTo;
  }

  /**
   * @param pGiftMessageFrom gift message recipient.
   */
  public void setGiftMessageFrom(String pGiftMessageFrom) {
    mGiftMessageFrom = pGiftMessageFrom;
  }

  /**
   * @return gift message recipient.
   */
  public String getGiftMessageFrom() {
    return mGiftMessageFrom;
  }

  /**
   * @param pGiftMessage gift message text.
   */
  public void setGiftMessage(String pGiftMessage) {
    mGiftMessage = pGiftMessage;
  }

  /**
   * @return gift message text.
   */
  public String getGiftMessage() {
    return mGiftMessage;
  }

  /**
   * @param pExpressCheckout express checkout.
   */
  public void setExpressCheckout(String pExpressCheckout) {
    mExpressCheckout = pExpressCheckout;
  }

  /**
   * @return express checkout.
   */
  public String getExpressCheckout() {
    return mExpressCheckout;
  }
}
