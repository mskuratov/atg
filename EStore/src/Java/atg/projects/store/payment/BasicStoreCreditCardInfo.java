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


package atg.projects.store.payment;

import atg.commerce.order.Order;

import atg.core.util.Address;

import atg.payment.creditcard.GenericCreditCardInfo;

import atg.projects.store.payment.creditcard.StoreCreditCardInfo;


/**
 * This is a non-repository item backed implementation of the StoreCreditCardInfo
 * interface. This class is used to validate credit card information that is
 * entered in profile maintenance.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/payment/BasicStoreCreditCardInfo.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class BasicStoreCreditCardInfo extends GenericCreditCardInfo implements StoreCreditCardInfo {
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/payment/BasicStoreCreditCardInfo.java#2 $$Change: 768606 $";

  /**
   * Credit card number.
   */
  protected String mCreditCardNumber = null;

  /**
   * Expiration month.
   */
  protected String mExpirationMonth = null;

  /**
   * Expiration day of month.
   */
  protected String mExpirationDayOfMonth = null;

  /**
   * Expiration year.
   */
  protected String mExpirationYear = null;

  /**
   * Credit card type.
   */
  protected String mCreditCardType = null;

  /**
   * Amount.
   */
  protected double mAmount = 0;

  /**
   * Paymant id.
   */
  protected String mPaymentId = null;

  /**
   * Currency code.
   */
  protected String mCurrencyCode = null;

  /**
   * Billing address.
   */
  protected Address mBillingAddress = null;

  /**
   * Card verification number.
   */
  protected String mCardVerificationNumber = null;

  /**
   * @return the credit card number.
   */
  public String getCreditCardNumber() {
    return mCreditCardNumber;
  }

  /**
   * @param pCreditCardNumber - the credit card number to set.
   */
  public void setCreditCardNumber(String pCreditCardNumber) {
    mCreditCardNumber = pCreditCardNumber;
  }

  /**
   * @return the credit card type.
   */
  public String getCreditCardType() {
    return mCreditCardType;
  }

  /**
   * @param pCreditCardType - the credit card type to set.
   */
  public void setCreditCardType(String pCreditCardType) {
    mCreditCardType = pCreditCardType;
  }

  /**
   * @return the expiration month.
   */
  public String getExpirationMonth() {
    return mExpirationMonth;
  }

  /**
   * @param pExpirationMonth - the expiration month to set.
   */
  public void setExpirationMonth(String pExpirationMonth) {
    mExpirationMonth = pExpirationMonth;
  }

  /**
   * @return the expiration year.
   */
  public String getExpirationYear() {
    return mExpirationYear;
  }

  /**
   * @param pExpirationYear - the expiration year to set.
   */
  public void setExpirationYear(String pExpirationYear) {
    mExpirationYear = pExpirationYear;
  }

  /**
   * @return order.
   */
  public Order getOrder() {
    return null;
  }

  /**
   * @return the card verification number.
   */
  public String getCardVerificationNumber() {
    return mCardVerificationNumber;
  }

  /**
   * @param pCardVerificationNumber - the card verification number to set.
   */
  public void setCardVerificationNumber(String pCardVerificationNumber) {
    mCardVerificationNumber = pCardVerificationNumber;
  }

  /**
   * @return the amount.
   */
  public double getAmount() {
    return mAmount;
  }

  /**
   * @param pAmount - the amount to set.
   */
  public void setAmount(double pAmount) {
    mAmount = pAmount;
  }

  /**
   * @return the expiration day of month.
   */
  public String getExpirationDayOfMonth() {
    return mExpirationDayOfMonth;
  }

  /**
   * @param pExpirationDayOfMonth - the expiration day of month to set.
   */
  public void setExpirationDayOfMonth(String pExpirationDayOfMonth) {
    mExpirationDayOfMonth = pExpirationDayOfMonth;
  }

  /**
   * @return the payment id.
   */
  public String getPaymentId() {
    return mPaymentId;
  }

  /**
   * @param pPaymentId - the payment id to set.
   */
  public void setPaymentId(String pPaymentId) {
    mPaymentId = pPaymentId;
  }

  /**
   * @return the currency code.
   */
  public String getCurrencyCode() {
    return mCurrencyCode;
  }

  /**
   * @param pCurrencyCode - the currency code to set.
   */
  public void setCurrencyCode(String pCurrencyCode) {
    mCurrencyCode = pCurrencyCode;
  }

  /**
   * @return the billing address.
   */
  public Address getBillingAddress() {
    return mBillingAddress;
  }

  /**
   * @param pBillingAddress - the billing address to set.
   */
  public void setBillingAddress(Address pBillingAddress) {
    mBillingAddress = pBillingAddress;
  }
}
