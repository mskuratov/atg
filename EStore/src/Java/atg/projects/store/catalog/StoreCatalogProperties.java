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



package atg.projects.store.catalog;

import atg.commerce.catalog.custom.CatalogProperties;


/**
 * <p>This class provides a mechanism to access the property names for
 * catalog item descriptors modified for ATG.
 * For example, if a calling class needed the property name of the
 * sku that provides the start date, the
 * getStartDatePropertyName() method will return the string value for the
 * property name as used in the repository definition.
 * </p>
 *
 * @author ATG
 * @version $Revision: #3 $
 */
public class StoreCatalogProperties extends CatalogProperties {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/catalog/StoreCatalogProperties.java#3 $$Change: 768606 $";

  /**
   * Sku type property name.
   */
  private String mSkuTypePropertyName = "type";

  /**
   * Start date property name.
   */
  private String mStartDatePropertyName;

  /**
   * End date property name.
   */
  private String mEndDatePropertyName;

  /**
   * Color property name.
   */
  private String mColorPropertyName = "color";

  /**
   * Size property name.
   */
  private String mSizePropertyName = "size";

  /**
   * Color swatch name.
   */
  private String mColorSwatchName = "colorSwatch";

  /**
   * Wood finish property name.
   */
  private String mWoodFinishPropertyName = "woodFinish";
  /**
   * Gift wrapt eligiple property name.
   */
  private String mGiftWrapEligiblePropertyName = "giftWrapEligible";

  /**
   * Preordarable property name.
   */
  private String mPreorderablePropertyName;

  /**
   * Preorder end date property name.
   */
  private String mPreorderEndDatePropertyName;

  /**
   * Use inventory for preorder property name.
   */
  private String mUseInventoryForPreorderPropertyName;
  
  /**
   * PromotionalContent item name
   */
  private String mPromotionalContentItemName="promotionalContent";
  
  /**
   * PromotionalContent item's display name property
   */
  private String mPromotionalContentDisplayNamePropertyName="displayName";
  
  /**
   * PromotionalContent item's image property
   */
  private String mPromotionalContentImagePropertyName="image";

  /**
   * <p>The start date property name.
   * @param pStartDatePropertyName - start date property name
   */
  public void setStartDatePropertyName(String pStartDatePropertyName) {
    mStartDatePropertyName = pStartDatePropertyName;
  }

  /**
   * <p>The start date property name.
   * @return start date property name
   */
  public String getStartDatePropertyName() {
    return mStartDatePropertyName;
  }

  /**
   * <p>The end date property name.
   * @param pEndDatePropertyName - end date property name
   */
  public void setEndDatePropertyName(String pEndDatePropertyName) {
    mEndDatePropertyName = pEndDatePropertyName;
  }

  /**
   * <p>The end date property name.
   * @return end date property name
   */
  public String getEndDatePropertyName() {
    return mEndDatePropertyName;
  }

  /**
   * <p>The sku type property name.
   * @return sku type property name
   */
  public String getSkuTypePropertyName() {
    return mSkuTypePropertyName;
  }

  /**
   * <p>The sku type property name.
   * @param pSkuTypePropertyName - sku type property name
   */
  public void setSkuTypePropertyName(String pSkuTypePropertyName) {
    mSkuTypePropertyName = pSkuTypePropertyName;
  }

  /**
   * <p>The name of the property used to indicate an item as 'giftwrappable'.
   * @param pGiftWrapEligiblePropertyName - name of the property used to indicate an item as 'giftwrappable'
   */
  public void setGiftWrapEligiblePropertyName(String pGiftWrapEligiblePropertyName) {
    mGiftWrapEligiblePropertyName = pGiftWrapEligiblePropertyName;
  }

  /**
   * <p>The name of the property used to indicate an item as 'giftwrappable'.
   * @return name of the property used to indicate an item as 'giftwrappable'
   */
  public String getGiftWrapEligiblePropertyName() {
    return mGiftWrapEligiblePropertyName;
  }

  /**
   * @return Returns the colorPropertyName.
   */
  public String getColorPropertyName() {
    return mColorPropertyName;
  }

  /**
   * @param pColorPropertyName The colorPropertyName to set.
   */
  public void setColorPropertyName(String pColorPropertyName) {
    mColorPropertyName = pColorPropertyName;
  }

  /**
   * @return Returns the sizePropertyName.
   */
  public String getSizePropertyName() {
    return mSizePropertyName;
  }

  /**
   * @param pSizePropertyName - The sizePropertyName to set.
   */
  public void setSizePropertyName(String pSizePropertyName) {
    mSizePropertyName = pSizePropertyName;
  }

  /**
   * @return the woodTypePropertyName
   */
  public String getWoodFinishPropertyName()
  {
    return mWoodFinishPropertyName;
  }

  /**
   * @param pWoodFinishPropertyName the woodFinishPropertyName to set
   */
  public void setWoodFinishPropertyName(String pWoodFinishPropertyName)
  {
    mWoodFinishPropertyName = pWoodFinishPropertyName;
  }

  /**
   * @return Returns the colorSwatchName.
   */
  public String getColorSwatchName() {
    return mColorSwatchName;
  }

  /**
   * @param pColorSwatchName - The colorSwatchName to set.
   */
  public void setColorSwatchName(String pColorSwatchName) {
    mColorSwatchName = pColorSwatchName;
  }

  /**
   * <p>The name of the property used to indicate an item as 'preorderable'.
   * @param pPreorderablePropertyName The preorderablePropertyName to set.
   */
  public void setPreorderablePropertyName(String pPreorderablePropertyName) {
    mPreorderablePropertyName = pPreorderablePropertyName;
  }

  /**
   * <p>The name of the property used to indicate an item as 'preorderable'.
   * @return Returns the preorderablePropertyName.
   */
  public String getPreorderablePropertyName() {
    return mPreorderablePropertyName;
  }

  /**
   * <p>The name of the property used to indicate the 'preorderEndDate' of
   * an item that is preorderable.
   * @param pPreorderEndDatePropertyName The preorderEndDatePropertyName to set.
   */
  public void setPreorderEndDatePropertyName(String pPreorderEndDatePropertyName) {
    mPreorderEndDatePropertyName = pPreorderEndDatePropertyName;
  }

  /**
   * <p>The name of the property used to indicate the 'preorderEndDate' of
   * an item that is preorderable.
   * @return Returns the preorderEndDatePropertyName.
   */
  public String getPreorderEndDatePropertyName() {
    return mPreorderEndDatePropertyName;
  }

  /**
   * <p>The name of the property used to indicate the 'useInventoryForPreorder' of
   * an item that is preorderable.
   * @param pUseInventoryForPreorderPropertyName The useInventoryForPreorderPropertyName to set.
   */
  public void setUseInventoryForPreorderPropertyName(String pUseInventoryForPreorderPropertyName) {
    mUseInventoryForPreorderPropertyName = pUseInventoryForPreorderPropertyName;
  }

  /**
   * <p>The name of the property used to indicate the 'useInventoryForPreorder' of
   * an item that is preorderable.
   * @return Returns the useInventoryForPreorderPropertyName.
   */
  public String getUseInventoryForPreorderPropertyName() {
    return mUseInventoryForPreorderPropertyName;
  }
  
  /**
   * <p>Sets the name of PromotionalContent item.
   * @param pPromotionalContentItemName the name of PromotionalContent item.
   */
  public void setPromotionalContentItemName(String pPromotionalContentItemName) {
    mPromotionalContentItemName = pPromotionalContentItemName;
  }

  /**
   * <p>Returns the name of PromotionalContent item..
   * @return Returns the name of PromotionalContent item.
   */
  public String getPromotionalContentItemName() {
    return mPromotionalContentItemName;
  }
  
  /**
   * <p>Sets the name of PromotionalContent item's display name property.
   * @param pPromotionalContentDisplayNamePropertyName the name of PromotionalContent item's display name property.
   */
  public void setPromotionalContentDisplayNamePropertyName(String pPromotionalContentDisplayNamePropertyName) {
    mPromotionalContentDisplayNamePropertyName = pPromotionalContentDisplayNamePropertyName;
  }

  /**
   * <p>Returns the name of PromotionalContent item's display name property.
   * @return Returns the name of PromotionalContent item's display name property.
   */
  public String getPromotionalContentDisplayNamePropertyName() {
    return mPromotionalContentDisplayNamePropertyName;
  }
  
  /**
   * <p>Sets the name of PromotionalContent item's image property.
   * @param pPromotionalContentImagePropertyName the name of PromotionalContent item's image property.
   */
  public void setPromotionalContentImagePropertyName(String pPromotionalContentImagePropertyName) {
    mPromotionalContentImagePropertyName = pPromotionalContentImagePropertyName;
  }

  /**
   * <p>Returns the name of PromotionalContent item's image property.
   * @return Returns the name of PromotionalContent item's image property.
   */
  public String getPromotionalContentImagePropertyName() {
    return mPromotionalContentImagePropertyName;
  }
}
