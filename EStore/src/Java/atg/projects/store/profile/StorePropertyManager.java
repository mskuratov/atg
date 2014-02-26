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


package atg.projects.store.profile;

import atg.commerce.profile.CommercePropertyManager;


/**
 * Store-specific extenstions with names of extended profile properties.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/profile/StorePropertyManager.java#2 $
 */
public class StorePropertyManager extends CommercePropertyManager {
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/profile/StorePropertyManager.java#2 $$Change: 768606 $";

  /**
   * Back in stock notify item descriptor name.
   */
  String mBackInStockNotifyItemDescriptorName = "backInStockNotifyItem";

  /**
   * Bisn sku id property name.
   */
  String mBisnSkuIdPropertyName = "catalogRefId";

  /**
   * Bisn product id property name.
   */
  String mBisnProductIdPropertyName = "productId";

  /**
   * Bisn e-mail property name.
   */
  String mBisnEmailPropertyName = "emailAddress";

  /**
   * Date of birth property name.
   */
  String mDateOfBirthPropertyName = "dateOfBirth";

  /**
   * E-mail recipient item descriptor name.
   */
  protected String mEmailRecipientItemDescriptorName;

  /**
   * E-mail recipient property name.
   */
  protected String mEmailRecipientPropertyName;

  /**
   * Source code property name.
   */
  protected String mSourceCodePropertyName;
  
  /**
   * Source code property name.
   */
  protected String mUserIdPropertyName;

  /**
   * Date of the last purchase.
   */
  protected String mLastPurchaseDate = "lastPurchaseDate";

  /**
   * Bought items constant.
   */
  protected String mItemsBought = "itemsBought";

  /**
   * Number of orders constant. 
   */
  protected String mNumberOfOrders = "numberOfOrders";

  /** 'receivePromoEmail' property name */
  private String mReceivePromoEmailPropertyName = "receivePromoEmail";

  private String mGiftlistsPropertyName = "giftlists";

  /**
   * @return the mGiftlistsPropertyName
   */
  public String getGiftlistsPropertyName() {
    return mGiftlistsPropertyName;
  }

  /**
   * @param pGiftlistsPropertyName the giftlistsPropertyName to set
   */
  public void setGiftlistsPropertyName(String pGiftlistsPropertyName) {
    mGiftlistsPropertyName = pGiftlistsPropertyName;
  }
  
  /**
   * @return the mReceivePromoEmailPropertyName
   */
  public String getReceivePromoEmailPropertyName() {
    return mReceivePromoEmailPropertyName;
  }

  /**
   *  @param pReceivePromoEmailPropertyName the receivePromoEmailPropertyName to set
   */
  public void setReceivePromoEmailPropertyName(
      String pReceivePromoEmailPropertyName) {
    mReceivePromoEmailPropertyName = pReceivePromoEmailPropertyName;
  }

  private String mWishlistPropertyName = "wishlist";
  
  /**
   * @return the mWishlistPropertyName
   */
  public String getWishlistPropertyName()
  {
    return mWishlistPropertyName;
  }
  
  /**
   * @param pWishlistPropertyName the wishlistPropertyName to set
   */
  public void setWishlistPropertyName(String pWishlistPropertyName)
  {
    mWishlistPropertyName = pWishlistPropertyName;
  }

  /**
  * @param pDateOfBirthPropertyName - date of birth property name.
  */
  public void setDateOfBirthPropertyName(String pDateOfBirthPropertyName) {
    mDateOfBirthPropertyName = pDateOfBirthPropertyName;
  }

  /**
   * @return date of birth property name.
   */
  public String getDateOfBirthPropertyName() {
    return mDateOfBirthPropertyName;
  }

  /**
   * @param pEmailRecipientItemDescriptorName - e-mail recipient item
   * descriptor name.
   */
  public void setEmailRecipientItemDescriptorName(String pEmailRecipientItemDescriptorName) {
    mEmailRecipientItemDescriptorName = pEmailRecipientItemDescriptorName;
  }

  /**
   * @return mEmailRecipientItemDescriptorName - e-mail recipient
   * item descriptor name.
   */
  public String getEmailRecipientItemDescriptorName() {
    return mEmailRecipientItemDescriptorName;
  }

  /**
   * @param pEmailRecipientPropertyName - e-mail recipient
   * property name.
   */
  public void setEmailRecipientPropertyName(String pEmailRecipientPropertyName) {
    mEmailRecipientPropertyName = pEmailRecipientPropertyName;
  }

  /**
   * @return mEmailRecipientPropertyName - e-mail recipient property name.
   */
  public String getEmailRecipientPropertyName() {
    return mEmailRecipientPropertyName;
  }

  /**
   * @return source code property name.
   */
  public String getSourceCodePropertyName() {
    return mSourceCodePropertyName;
  }

  /**
   * @param pSourceCodePropertyName - source code
   * property name.
   */
  public void setSourceCodePropertyName(String pSourceCodePropertyName) {
    mSourceCodePropertyName = pSourceCodePropertyName;
  }
  
  /**
   * @return user id property name.
   */
  public String getUserIdPropertyName() {
    return mUserIdPropertyName;
  }

  /**
   * @param pUserIdPropertyName - user id
   * property name.
   */
  public void setUserIdPropertyName(String pUserIdPropertyName) {
    mUserIdPropertyName = pUserIdPropertyName;
  }

  /**
   * @return backInStockNotifyItem - back in stock
   * notify item.
   */
  public String getBackInStockNotifyItemDescriptorName() {
    return mBackInStockNotifyItemDescriptorName;
  }

  /**
   * @param pBackInStockNotifyItemDescriptorName -
   * back in stock notify item descriptor name.
   */
  public void setBackInStockNotifyItemDescriptorName(String pBackInStockNotifyItemDescriptorName) {
    mBackInStockNotifyItemDescriptorName = pBackInStockNotifyItemDescriptorName;
  }

  /**
   * @return bisn e-mail property name.
   */
  public String getBisnEmailPropertyName() {
    return mBisnEmailPropertyName;
  }

  /**
   * @param pBisnEmailPropertyName - bisn e-mail
   * property name.
   */
  public void setBisnEmailPropertyName(String pBisnEmailPropertyName) {
    mBisnEmailPropertyName = pBisnEmailPropertyName;
  }

  /**
   * @return bisn sku id property name.
   */
  public String getBisnSkuIdPropertyName() {
    return mBisnSkuIdPropertyName;
  }

  /**
   * @param pBisnSkuIdPropertyName - bisn
   * sku id property name.
   */
  public void setBisnSkuIdPropertyName(String pBisnSkuIdPropertyName) {
    mBisnSkuIdPropertyName = pBisnSkuIdPropertyName;
  }

  /**
   * @return bisn product id property name.
   */
  public String getBisnProductIdPropertyName() {
    return mBisnProductIdPropertyName;
  }

  /**
   * @param pBisnProductIdPropertyName - bisn
   * property product id property name.
   */
  public void setBisnProductIdPropertyName(String pBisnProductIdPropertyName) {
    mBisnProductIdPropertyName = pBisnProductIdPropertyName;
  }

  /**
   * @return items bought.
   */
  public String getItemsBought() {
    return mItemsBought;
  }

  /**
   * @param pItemsBought - items bought.
   */
  public void setItemsBought(String pItemsBought) {
    mItemsBought = pItemsBought;
  }

  /**
   * @return last purchase date.
   */
  public String getLastPurchaseDate() {
    return mLastPurchaseDate;
  }

  /**
   * @param pLastPurchaseDate - the date of last purchase.
   */
  public void setLastPurchaseDate(String pLastPurchaseDate) {
    mLastPurchaseDate = pLastPurchaseDate;
  }

  /**
   * @return number of orders.
   */
  public String getNumberOfOrders() {
    return mNumberOfOrders;
  }

  /**
   * @param pNumberOfOrders - number of orders.
   */
  public void setNumberOfOrders(String pNumberOfOrders) {
    mNumberOfOrders = pNumberOfOrders;
  }
  
  protected String mNewCreditCard = "newCreditCard";

  /**
   * @return the mNewCreditCard
   */
  public String getNewCreditCard() {
    return mNewCreditCard;
  }

  /**
   * @param pNewCreditCard the newCreditCard to set
   */
  public void setNewCreditCard(String pNewCreditCard) {
    mNewCreditCard = pNewCreditCard;
  }
  
  protected String mGenderPropertyName = "gender";
  
  /**
   * @return the mGenderPropertyName
   */
  public String getGenderPropertyName() {
    return mGenderPropertyName;
  }

  /**
   * @param pGenderPropertyName the genderPropertyName to set
   */
  public void setGenderPropertyName(String pGenderPropertyName) {
    mGenderPropertyName = pGenderPropertyName;
  }

  protected String mRefferalSourcePropertyName = "referralSource";
  
  /**
   * @return the mRefferalSourcePropertyName
   */
  public String getRefferalSourcePropertyName() {
    return mRefferalSourcePropertyName;
  }

  /**
   * @param pRefferalSourcePropertyName the refferalSourcePropertyName to set
   */
  public void setRefferalSourcePropertyName(String pRefferalSourcePropertyName) {
    mRefferalSourcePropertyName = pRefferalSourcePropertyName;
  }

  protected String mRecentlyViewedProductsPropertyName = "recentlyViewedProducts";

  /**
   * @return The recentlyViewedProducts property name.
   */
  public String getRecentlyViewedProductsPropertyName() {
    return mRecentlyViewedProductsPropertyName;
  }

  /**
   * @param pRecentlyViewedProductsPropertyName The recentlyViewedProducts property name.
   */
  public void setRecentlyViewedProductsPropertyName(String pRecentlyViewedProductsPropertyName) {
    mRecentlyViewedProductsPropertyName = pRecentlyViewedProductsPropertyName;
  }
  
  protected String mRecentlyViewedProductItemDescriptorName = "recentlyViewedProduct";

  /**
   * @return The recentlyViewedProduct item descriptor name.
   */
  public String getRecentlyViewedProductItemDescriptorName() {
    return mRecentlyViewedProductItemDescriptorName;
  }

  /**
   * @param pRecentlyViewedProductItemDescriptorName The recentlyViewedProduct item descriptor name.
   */
  public void setRecentlyViewedProductItemDescriptorName(String pRecentlyViewedProductItemDescriptorName) {
    mRecentlyViewedProductItemDescriptorName = pRecentlyViewedProductItemDescriptorName;
  }
  
  protected String mProductPropertyName = "product";

  /**
   * @return The name of property name of recentlyViewedProduct 'product'.
   */
  public String getProductPropertyName() {
    return mProductPropertyName;
  }
  /**
   * @param pProductPropertyName The name of property name of recentlyViewedProduct 'product'.
   */
  public void setProductPropertyName(String pProductPropertyName) {
    mProductPropertyName = pProductPropertyName;
  }

  protected String mSiteIdPropertyName = "siteId";
  
  /**
   * @return The property name of recentlyViewedProduct 'siteId'.
   */
  public String getSiteIdPropertyName() {
    return mSiteIdPropertyName;
  }

  /**
   * @param pSiteIdPropertyName The property name of recentlyViewedProduct 'siteId'.
   */
  public void setSiteIdPropertyName(String pSiteIdPropertyName) {
    mSiteIdPropertyName = pSiteIdPropertyName;
  }

  protected String mTimeStampPropertyName = "timestamp";
  
  /**
   * @return The descriptor name of recentlyViewedProduct 'timeStamp'.
   */
  public String getTimeStampPropertyName() {
    return mTimeStampPropertyName;
  }
  /**
   * @param pTimeStampPropertyName The descriptor name of recentlyViewedProduct 'timeStamp'.
   */
  public void setTimeStampPropertyName(String pTimeStampPropertyName) {
    mTimeStampPropertyName = pTimeStampPropertyName;
  }

}
