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

import atg.commerce.CommerceException;
import atg.commerce.order.purchase.PurchaseProcessFormHandler;
import atg.droplet.DropletFormException;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.StoreOrderManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * Extension of {@link PurchaseProcessFormHandler} for working with coupons.
 * This form handler processes add/edit/remove operations on order coupons.
 * 
 * @author ATG
 * @version $$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreCouponFormHandler.java#3 $$$$Change: 788278 $$ 
 * @updated $$DateTime: 2013/02/05 00:41:33 $$$$Author: jsiddaga $$
 */
public class StoreCouponFormHandler extends PurchaseProcessFormHandler {
  /**
   * Class version
   */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreCouponFormHandler.java#3 $$Change: 788278 $";
  
  /**
   * property: couponCode
   */
  private String mCouponCode;
  
  /**
   * This property contains a coupon code to be claimed for current order.
   * 
   * @return coupon code to be claimed.
   */
  public String getCouponCode() {
    return mCouponCode;
  }
  
  /**
   * @param pCouponCode - the coupon code to set.
   */
  public void setCouponCode(String pCouponCode) {
    mCouponCode = pCouponCode;
  }
  
  /**
   * property: applyCouponSuccessURL
   */
  private String mApplyCouponSuccessURL;
  
  /**
   * This property contains an URL the request should be redirected when coupon successfully added.
   * 
   * @return URL the request should be redirected when coupon successfully added.
   */
  public String getApplyCouponSuccessURL() {
    return mApplyCouponSuccessURL;
  }

  /**
   * @param pApplyCouponSuccessURL - the applyCouponSuccessURL to set.
   */
  public void setApplyCouponSuccessURL(String pApplyCouponSuccessURL) {
    mApplyCouponSuccessURL = pApplyCouponSuccessURL;
  }

  /**
   * property: applyCouponErrorURL
   */
  private String mApplyCouponErrorURL;
  
  /**
   * This property contains an URL the request should be redirected when there is an error adding coupon.
   * 
   * @return URL the request should be redirected when there is an error adding coupon.
   */
  public String getApplyCouponErrorURL() {
    return mApplyCouponErrorURL;
  }

  /**
   * @param pApplyCouponErrorURL - the applyCouponErrorURL to set.
   */
  public void setApplyCouponErrorURL(String pApplyCouponErrorURL) {
    mApplyCouponErrorURL = pApplyCouponErrorURL;
  }

  /**
   * property: editCouponSuccessURL
   */
  private String mEditCouponSuccessURL;
  
  /**
   * This property contains an URL the request should be redirected when coupon successfully edited.
   * 
   * @return URL the request should be redirected when coupon successfully edited.
   */
  public String getEditCouponSuccessURL() {
    return mEditCouponSuccessURL;
  }

  /**
   * @param pEditCouponSuccessURL - the editCouponSuccessURL to set.
   */
  public void setEditCouponSuccessURL(String pEditCouponSuccessURL) {
    mEditCouponSuccessURL = pEditCouponSuccessURL;
  }

  /**
   * property: editCouponErrorURL
   */
  
  private String mEditCouponErrorURL;
  /**
   * This property contains an URL the request should be redirected when there is an error editing coupon.
   * 
   * @return URL the request should be redirected when there is an error editing coupon.
   */
  public String getEditCouponErrorURL() {
    return mEditCouponErrorURL;
  }

  /**
   * @param pEditCouponErrorURL - the editCouponErrorURL to set.
   */
  public void setEditCouponErrorURL(String pEditCouponErrorURL) {
    mEditCouponErrorURL = pEditCouponErrorURL;
  }

  /**
   * property: removeCouponSuccessURL
   */
  private String mRemoveCouponSuccessURL;
  
  /**
   * This property contains an URL the request should be redirected when coupon successfully removed.
   * 
   * @return URL the request should be redirected when coupon successfully removed.
   */
  public String getRemoveCouponSuccessURL() {
    return mRemoveCouponSuccessURL;
  }

  /**
   * @param pRemoveCouponSuccessURL - the removeCouponSuccessURL to set.
   */
  public void setRemoveCouponSuccessURL(String pRemoveCouponSuccessURL) {
    mRemoveCouponSuccessURL = pRemoveCouponSuccessURL;
  }

  /**
   * property: removeCouponErrorURL
   */
  private String mRemoveCouponErrorURL;
  
  /**
   * This property contains an URL the request should be redirected when there is an error removing coupon
   * @return URL the request should be redirected when there is an error removing coupon
   */
  public String getRemoveCouponErrorURL() {
    return mRemoveCouponErrorURL;
  }

  /**
   * @param pRemoveCouponErrorURL the removeCouponErrorURL to set
   */
  public void setRemoveCouponErrorURL(String pRemoveCouponErrorURL) {
    mRemoveCouponErrorURL = pRemoveCouponErrorURL;
  }


  /**
   * Returns the Order property as StoreOrderImpl.
   *
   * @return an <code>Order</code> value
   */
  @Override
  public StoreOrderImpl getOrder() {
    return (StoreOrderImpl)super.getOrder();
  }
  
  /**
   * This getter implements a read-only <code>currentCouponCode</code> property. 
   * This property calculates a coupon code used by the current user's shopping cart.
   * 
   * @return currently used coupon code, or {@code null} if none of coupons used.
   */
  public String getCurrentCouponCode() {
    try {
      StoreOrderManager orderManager = (StoreOrderManager) getOrderManager();
      return orderManager.getCouponCode(getOrder());
    } 
    catch (CommerceException e) {
      return null;
    }
  }

  /**
   * Claim the specified coupon, register a form exception if the coupon is invalid or an error occurs.
   * 
   * @param pRequest - current HTTP servlet request.
   * @param pResponse - current HTTP servlet response.
   *
   * @return true if coupon has been claimed; otherwise false.
   *
   * @throws ServletException if an error occurred during claiming the coupon.
   * @throws IOException if an error occurred during claiming the coupon.
   */
  public boolean handleClaimCoupon(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {

    try {
      /*
       * Attempt to claim the specified coupon
       */
      boolean couponTendered = 
        ((StorePurchaseProcessHelper) getPurchaseProcessHelper()).tenderCoupon(getCouponCode(),
                                                                               getOrder(),
                                                                               getProfile(),
                                                                               getUserPricingModels(),
                                                                               getUserLocale());
      // If the coupon could not be claimed add an error.
      if (!couponTendered) {
        String errorMessage = formatUserMessage(StorePurchaseProcessHelper.MSG_UNCLAIMABLE_COUPON,
                                                pRequest, pResponse);

        addFormException(new DropletFormException(errorMessage,
                                                  "",
                                                  StorePurchaseProcessHelper.MSG_UNCLAIMABLE_COUPON));
      }
    }
    catch (CommerceException commerceException) {
      processException(commerceException,
                       StoreCartProcessHelper.MSG_UNCLAIMABLE_COUPON,
                       pRequest,
                       pResponse);
    }

    return checkFormRedirect(getApplyCouponSuccessURL(),
                             getApplyCouponErrorURL(),
                             pRequest, pResponse);
  }
}
