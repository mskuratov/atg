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


package atg.projects.store.multisite;

/**
 * Properties manager for SiteRepository.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/multisite/StoreSitePropertiesManager.java#4 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreSitePropertiesManager {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/multisite/StoreSitePropertiesManager.java#4 $$Change: 788278 $";


  
  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // Property: catalogIdPropertyName
  //-------------------------------------
  protected String mCatalogIdPropertyName = "catalogId";

  /**
   * @return the mCatalogIdPropertyName
   */
  public String getCatalogIdPropertyName() {
    return mCatalogIdPropertyName;
  }

  /**
   * @param pCatalogIdPropertyName the catalogIdPropertyName to set
   */
  public void setCatalogIdPropertyName(String pCatalogIdPropertyName) {
    mCatalogIdPropertyName = pCatalogIdPropertyName;
  }

  //-------------------------------------
  // Property: listPriceListIdPropertyName
  //-------------------------------------
  protected String mListPriceListIdPropertyName = "listPriceListId";

  /**
   * @return the listPriceListIdPropertyName
   */
  public String getListPriceListIdPropertyName() {
    return mListPriceListIdPropertyName;
  }

  /**
   * @param pListPriceListIdPropertyName the listPriceListIdPropertyName to set
   */
  public void setListPriceListIdPropertyName(String pListPriceListIdPropertyName) {
    mListPriceListIdPropertyName = pListPriceListIdPropertyName;
  }

  //-------------------------------------
  // Property: salePriceListIdPropertyName
  //-------------------------------------
  protected String mSalePriceListIdPropertyName ="salePriceListId";

  /**
   * @return the salePriceListIdPropertyName
   */
  public String getSalePriceListIdPropertyName() {
    return mSalePriceListIdPropertyName;
  }

  /**
   * @param pSalePriceListIdPropertyName the salePriceListIdPropertyName to set
   */
  public void setSalePriceListIdPropertyName(String pSalePriceListIdPropertyName) {
    mSalePriceListIdPropertyName = pSalePriceListIdPropertyName;
  }
  
  //-------------------------------------
  // Property: resourceBundlePropertyName
  //-------------------------------------  
  protected String mResourceBundlePropertyName = "resourceBundle";
  
  /**
   * @return the resourceBundlePropertyName
   */
  public String getResourceBundlePropertyName() {
    return mResourceBundlePropertyName;
  }

  /**
   * @param pResourceBundlePropertyName the resourceBundlePropertyName to set
   */
  public void setResourceBundlePropertyName(String pResourceBundlePropertyName) {
    mResourceBundlePropertyName = pResourceBundlePropertyName;
  }

  //-------------------------------------
  // Property: newProductThresholdDaysPropertyName
  //-------------------------------------
  protected String mNewProductThresholdDaysPropertyName ="newProductThresholdDays";

  /**
   * @return the newProductThresholdDaysPropertyName
   */
  public String getNewProductThresholdDaysPropertyName() {
    return mNewProductThresholdDaysPropertyName;
  }

  /**
   * @param pNewProductThresholdDaysPropertyName the newProductThresholdDaysPropertyName to set
   */
  public void setNewProductThresholdDaysPropertyName(
      String pNewProductThresholdDaysPropertyName) {
    mNewProductThresholdDaysPropertyName = pNewProductThresholdDaysPropertyName;
  }
  
  //-------------------------------------
  // Property: defaultPageSizePropertyName
  //-------------------------------------  
  protected String mDefaultPageSizePropertyName = "defaultPageSize";

  /**
   * @return the defaultPageSizePropertyName
   */
  public String getDefaultPageSizePropertyName() {
    return mDefaultPageSizePropertyName;
  }

  /**
   * @param pDefaultPageSizePropertyName the defaultPageSizePropertyName to set
   */
  public void setDefaultPageSizePropertyName(String pDefaultPageSizePropertyName) {
    mDefaultPageSizePropertyName = pDefaultPageSizePropertyName;
  }
  
  //-------------------------------------
  // property: CssFilePropertyName
  //-------------------------------------
  protected String mCssFilePropertyName = "cssFile";

  /**
   * @return the mCssFilePropertyName
   */
  public String getCssFilePropertyName() {
    return mCssFilePropertyName;
  }

  /**
   * @param pCssFilePropertyName the String to set
   */
  public void setCssFilePropertyName(String pCssFilePropertyName) {
    mCssFilePropertyName = pCssFilePropertyName;
  }
  
  //-------------------------------------
  // property: DefaultCountryPropertyName
  //-------------------------------------
  protected String mDefaultCountryPropertyName = "defaultCountry";

  /**
   * @return the String
   */
  public String getDefaultCountryPropertyName() {
    return mDefaultCountryPropertyName;
  }

  /**
   * @param pDefaultCountryPropertyName the String to set
   */
  public void setDefaultCountryPropertyName(String pDefaultCountryPropertyName) {
    mDefaultCountryPropertyName = pDefaultCountryPropertyName;
  }
  
  //-------------------------------------
  // property: EmailAFriendEnabledPropertyName
  //-------------------------------------
  protected String mEmailAFriendEnabledPropertyName = "emailAFriendEnabled";

  /**
   * @return the String
   */
  public String getEmailAFriendEnabledPropertyName() {
    return mEmailAFriendEnabledPropertyName;
  }

  /**
   * @param pEmailAFriendEnabledPropertyName the String to set
   */
  public void setEmailAFriendEnabledPropertyName(String pEmailAFriendEnabledPropertyName) {
    mEmailAFriendEnabledPropertyName = pEmailAFriendEnabledPropertyName;
  }
  
  //-------------------------------------
  // property: BackInStockFromAddressPropertyName
  //-------------------------------------
  protected String mBackInStockFromAddressPropertyName = "backInStockFromAddress";

  /**
   * @return the String
   */
  public String getBackInStockFromAddressPropertyName() {
    return mBackInStockFromAddressPropertyName;
  }

  /**
   * @param pBackInStockFromAddressPropertyName the String to set
   */
  public void setBackInStockFromAddressPropertyName(String pBackInStockFromAddressPropertyName) {
    mBackInStockFromAddressPropertyName = pBackInStockFromAddressPropertyName;
  }
  
  //-------------------------------------
  // property: NewPasswordFromAddressPropertyName
  //-------------------------------------
  protected String mNewPasswordFromAddressPropertyName = "newPasswordFromAddress";

  /**
   * @return the String
   */
  public String getNewPasswordFromAddressPropertyName() {
    return mNewPasswordFromAddressPropertyName;
  }

  /**
   * @param pNewPasswordFromAddressPropertyName the String to set
   */
  public void setNewPasswordFromAddressPropertyName(String pNewPasswordFromAddressPropertyName) {
    mNewPasswordFromAddressPropertyName = pNewPasswordFromAddressPropertyName;
  }
  
  //-------------------------------------
  // property: changePasswordFromAddressPropertyName
  //-------------------------------------
  protected String mChangePasswordFromAddressPropertyName = "changePasswordFromAddress";

  /**
   * @return the String
   */
  public String getChangePasswordFromAddressPropertyName() {
    return mChangePasswordFromAddressPropertyName;
  }

  /**
   * @param pChangePasswordFromAddressPropertyName the String to set
   */
  public void setChangePasswordFromAddressPropertyName(String pChangePasswordFromAddressPropertyName) {
    mChangePasswordFromAddressPropertyName = pChangePasswordFromAddressPropertyName;
  }
  
  //-------------------------------------
  // property: registeredUserFromAddressPropertyName
  //-------------------------------------
  protected String mRegisteredUserFromAddressPropertyName = "registeredUserFromAddress";

  /**
   * @return the String
   */
  public String getRegisteredUserFromAddressPropertyName() {
    return mRegisteredUserFromAddressPropertyName;
  }

  /**
   * @param pRegisteredUserFromAddressPropertyName the String to set
   */
  public void setRegisteredUserFromAddressPropertyName(String pRegisteredUserFromAddressPropertyName) {
    mRegisteredUserFromAddressPropertyName = pRegisteredUserFromAddressPropertyName;
  }
  
  //-------------------------------------
  // property: OrderConfirmationFromAddressPropertyName
  //-------------------------------------
  protected String mOrderConfirmationFromAddressPropertyName = "orderConfirmationFromAddress";

  /**
   * @return the String
   */
  public String getOrderConfirmationFromAddressPropertyName() {
    return mOrderConfirmationFromAddressPropertyName;
  }

  /**
   * @param pOrderConfirmationFromAddressPropertyName the String to set
   */
  public void setOrderConfirmationFromAddressPropertyName(String pOrderConfirmationFromAddressPropertyName) {
    mOrderConfirmationFromAddressPropertyName = pOrderConfirmationFromAddressPropertyName;
  }
  
  //-------------------------------------
  // property: OrderShippedFromAddressPropertyName
  //-------------------------------------
  protected String mOrderShippedFromAddressPropertyName = "orderShippedFromAddress";

  /**
   * @return the String
   */
  public String getOrderShippedFromAddressPropertyName() {
    return mOrderShippedFromAddressPropertyName;
  }

  /**
   * @param pOrderShippedFromAddressPropertyName the String to set
   */
  public void setOrderShippedFromAddressPropertyName(String pOrderShippedFromAddressPropertyName) {
    mOrderShippedFromAddressPropertyName = pOrderShippedFromAddressPropertyName;
  }
  
  //-------------------------------------
  // property: PromotionEmailAddressPropertyName
  //-------------------------------------
  protected String mPromotionEmailAddressPropertyName = "promotionEmailAddress";

  /**
   * @return the String
   */
  public String getPromotionEmailAddressPropertyName() {
    return mPromotionEmailAddressPropertyName;
  }

  /**
   * @param pPromotionEmailAddressPropertyName the String to set
   */
  public void setPromotionEmailAddressPropertyName(String pPromotionEmailAddressPropertyName) {
    mPromotionEmailAddressPropertyName = pPromotionEmailAddressPropertyName;
  }
  
  //-------------------------------------
  // property: BillableCountriesPropertyName
  //-------------------------------------
  protected String mBillableCountriesPropertyName = "billableCountries";

  /**
   * @return the String
   */
  public String getBillableCountriesPropertyName() {
    return mBillableCountriesPropertyName;
  }

  /**
   * @param pBillableCountriesPropertyName the String to set
   */
  public void setBillableCountriesPropertyName(String pBillableCountriesPropertyName) {
    mBillableCountriesPropertyName = pBillableCountriesPropertyName;
  }
  
  //-------------------------------------
  // property: ShippableCountriesPropertyName
  //-------------------------------------
  protected String mShippableCountriesPropertyName = "shippableCountries";

  /**
   * @return the String
   */
  public String getShippableCountriesPropertyName() {
    return mShippableCountriesPropertyName;
  }

  /**
   * @param pShippableCountriesPropertyName the String to set
   */
  public void setShippableCountriesPropertyName(String pShippableCountriesPropertyName) {
    mShippableCountriesPropertyName = pShippableCountriesPropertyName;
  }
  
  //-------------------------------------
  // property: NonShippableCountriesPropertyName
  //-------------------------------------
  protected String mNonShippableCountriesPropertyName = "nonShippableCountries";

  /**
   * @return the String
   */
  public String getNonShippableCountriesPropertyName() {
    return mNonShippableCountriesPropertyName;
  }

  /**
   * @param pNonShippableCountriesPropertyName the String to set
   */
  public void setNonShippableCountriesPropertyName(String pNonShippableCountriesPropertyName) {
    mNonShippableCountriesPropertyName = pNonShippableCountriesPropertyName;
  }
  
  //-------------------------------------
  // property: NonBillableCountriesPropertyName
  //-------------------------------------
  protected String mNonBillableCountriesPropertyName = "nonBillableCountries";

  /**
   * @return the String
   */
  public String getNonBillableCountriesPropertyName() {
    return mNonBillableCountriesPropertyName;
  }

  /**
   * @param pNonBillableCountriesPropertyName the String to set
   */
  public void setNonBillableCountriesPropertyName(String pNonBillableCountriesPropertyName) {
    mNonBillableCountriesPropertyName = pNonBillableCountriesPropertyName;
  }
  
  //----------------------------------
  // property: priceSliderMinimumValue
  //----------------------------------
  protected String mPriceSliderMinimumValuePropertyName = "priceSliderMinimumValue";

  /**
   * @return the priceSliderMinimumValue property name.
   */
  public String getPriceSliderMinimumValuePropertyName() {
    return mPriceSliderMinimumValuePropertyName;
  }

  /**
   * @param pPriceSliderMinimumValuePropertyName the priceSliderMinimumValue property name.
   */
  public void setPriceSliderMinimumValuePropertyName(String pPriceSliderMinimumValuePropertyName) {
    mPriceSliderMinimumValuePropertyName = pPriceSliderMinimumValuePropertyName;
  }
  
  //----------------------------------
  // property: priceSliderMaximumValue
  //----------------------------------
  protected String mPriceSliderMaximumValuePropertyName = "priceSliderMaximumValue";

  /**
   * @return the priceSliderMaximumValue property name.
   */
  public String getPriceSliderMaximumValuePropertyName() {
    return mPriceSliderMaximumValuePropertyName;
  }

  /**
   * @param pPriceSliderMaximumValuePropertyName the priceSliderMaximumValue property name.
   */
  public void setPriceSliderMaximumValuePropertyName(String pPriceSliderMaximumValuePropertyName) {
    mPriceSliderMaximumValuePropertyName = pPriceSliderMaximumValuePropertyName;
  }

  //----------------------------------
  // property: channel
  //----------------------------------
  protected String mChannelPropertyName = "channel";

  /**
   * @return the channel property name.
   */
  public String getChannelPropertyName() {
    return mChannelPropertyName;
  }

  /**
   * @param pChannelPropertyName the channel property name.
   */
  public void setChannelPropertyName(String pChannelPropertyName) {
    mChannelPropertyName = pChannelPropertyName;
  }

  //-------------------------------------
  // property: RightNowUrlsPropertyName
  //-------------------------------------
  protected String mRightNowUrlsPropertyName = "rightNowUrl";

  /**
   * @return the locale specific url of the RightNow site 
   */
  public String getRightNowUrlsPropertyName() {
    return mRightNowUrlsPropertyName;
  }

  /**
   * @param pRightNowUrlsPropertyName the locale specific url of the RightNow site to set
   */
  public void setRightNowUrlsPropertyName(String pRightNowUrlsPropertyName) {
    mRightNowUrlsPropertyName = pRightNowUrlsPropertyName;
  }

}
