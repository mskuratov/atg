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


package atg.projects.store.order.processor;

import atg.beans.DynamicBeans;

import atg.commerce.order.*;
import atg.commerce.order.processor.ProcLoadPriceInfoObjects;

import atg.repository.ItemDescriptorImpl;
import atg.repository.MutableRepositoryItem;
import atg.repository.RemovedItemException;
import atg.repository.RepositoryItemDescriptor;


/**
 * <p>
 *   This class is overridden to include loading of TaxPriceInfo for commerce items
 *   and shipping groups.
 * </p>
 * <p>
 *   Commerce item's TaxPriceInfo objects are stored with the CommerceItems'
 *   ItemPriceInfo. Shipping group's TaxPriceInfo is stored with the ShippingGroup
 *   ShippingPriceInfo.
 * </p>
 *
 * @see atg.commerce.order.processor.ProcLoadPriceInfoObjects
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/processor/StoreProcLoadPriceInfoObjects.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreProcLoadPriceInfoObjects extends ProcLoadPriceInfoObjects {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/processor/StoreProcLoadPriceInfoObjects.java#3 $$Change: 788278 $";

  /**
   * property: priceInfoRepositoryItemName
   */
  String mPriceInfoRepositoryItemName;
  
  /**
   * @return price information repository item name.
   */
  public String getPriceInfoRepositoryItemName() {
    return mPriceInfoRepositoryItemName;
  }

  /**
   * @param pPriceInfoRepositoryItemName - price information repository item name.
   */
  public void setPriceInfoRepositoryItemName(String pPriceInfoRepositoryItemName) {
    mPriceInfoRepositoryItemName = pPriceInfoRepositoryItemName;
  }

  /**
   * property: taxPriceInfoRepositoryItemName
   */
  String mTaxPriceInfoRepositoryItemName;

  /**
   * @return tax price repository item name.
   */
  public String getTaxPriceInfoRepositoryItemName() {
    return mTaxPriceInfoRepositoryItemName;
  }

  /**
   * @param pTaxPriceInfoRepositoryItemName - tax price repository price name.
   */
  public void setTaxPriceInfoRepositoryItemName(String pTaxPriceInfoRepositoryItemName) {
    mTaxPriceInfoRepositoryItemName = pTaxPriceInfoRepositoryItemName;
  }

  /**
   * <p>
   *   Overriding to load tax price info for items. The only code added to the OOB implementation is a call
   *   to the loadItemTaxPriceInfo method.
   * </p>
   * <p>
   *   The complete OOB code is available in the $DYNAMO_ROOT/DCS/src/Java directory of the ATG install.
   * </p>
   * 
   * @see atg.commerce.order.processor.ProcLoadPriceInfoObjects#readProperties
   * @see atg.commerce.order.processor.ProcLoadPriceInfoObjects#loadPricingAdjustments
   * @see atg.commerce.order.processor.ProcLoadPriceInfoObjects#loadDetailedItemPriceInfos
   *
   * @param pOrder - The order.
   * @param pCi - The commerce identifier object.
   * @param pMutItem - The repository item.
   * @param pOrderManager - The order manager.
   * @param pInvalidateCache - Boolean to invalidate cache.
   * 
   * @throws Exception if an error occurs.
   * 
   * @see atg.commerce.order.processor.
   *        ProcLoadPriceInfoObjects#loadItemPriceInfo(atg.commerce.order.Order, 
   *                                                   atg.commerce.order.CommerceIdentifier, 
   *                                                   atg.repository.MutableRepositoryItem, 
   *                                                   atg.commerce.order.OrderManager, 
   *                                                   java.lang.Boolean).
   */
  protected void loadItemPriceInfo(Order pOrder, 
                                   CommerceIdentifier pCi, 
                                   MutableRepositoryItem pMutItem,
                                   OrderManager pOrderManager, 
                                   Boolean pInvalidateCache) throws Exception {
    
    MutableRepositoryItem piRepItem = 
      (MutableRepositoryItem) pMutItem.getPropertyValue(getItemPriceInfoProperty());

    Object amtInfo = null;

    piRepItem = (MutableRepositoryItem) pMutItem.getPropertyValue(getItemPriceInfoProperty());

    if (piRepItem == null) {
      if (DynamicBeans.getBeanInfo(pCi).hasProperty(getPriceInfoRepositoryItemName())) {
        DynamicBeans.setPropertyValue(pCi, getPriceInfoRepositoryItemName(), piRepItem);
      }

      DynamicBeans.setPropertyValue(pCi, getItemPriceInfoProperty(), amtInfo);
    } 
    else {
      RepositoryItemDescriptor desc = piRepItem.getItemDescriptor();

      if (pInvalidateCache.booleanValue()) {
        invalidateCache((ItemDescriptorImpl) desc, piRepItem);
      }

      String className = pOrderManager.getOrderTools().getMappedBeanName(desc.getItemDescriptorName());
      amtInfo = Class.forName(className).newInstance();

      if (DynamicBeans.getBeanInfo(pCi).hasProperty(getPriceInfoRepositoryItemName())) {
        DynamicBeans.setPropertyValue(pCi, getPriceInfoRepositoryItemName(), piRepItem);
      }

      DynamicBeans.setPropertyValue(pCi, getItemPriceInfoProperty(), amtInfo);

      readProperties(pOrder, amtInfo, getLoadProperties(), piRepItem, desc, pOrderManager);

      loadPricingAdjustments(pOrder, amtInfo, piRepItem, pOrderManager, pInvalidateCache);

      // Added to load TaxPriceInfo items. Only extension to this method.
      loadTaxPriceInfo(pOrder, amtInfo, piRepItem, pOrderManager, pInvalidateCache);

      loadDetailedItemPriceInfos(pOrder, amtInfo, piRepItem, pOrderManager, pInvalidateCache);
    }

    if (pCi instanceof ChangedProperties) {
      ((ChangedProperties) pCi).clearChangedProperties();
    }

    /* If the item is a configurable SKU, then load the priceInfo for the subSKUs */
    if (pCi instanceof CommerceItemContainer) {
      if (isLoggingDebug()) {
        logDebug("item is configurable SKU - iterate thru its subSKUs to load them " + pMutItem);
      }

      loadSubSkuPriceInfo(pOrder, pCi, pMutItem, pOrderManager, pInvalidateCache);
    }
  }

  /**
   * <p>
   *   Load the ItemPriceInfo's or ShippingPriceInfo's TaxPriceInfo. This method is nearly identical
   *   to the OOB loadTaxPriceInfo method in the superclass, but the TaxPriceInfo is loaded from the 
   *   Commerce item's / Shipping group's PriceInfo rather than the Order.
   * </p>
   * <p>
   *   The complete OOB code is available in the $DYNAMO_ROOT/DCS/src/Java directory of the ATG install.
   * </p>
   * 
   * @see atg.commerce.order.processor.ProcLoadPriceInfoObjects#loadTaxPriceInfo.
   * 
   * @param pOrder - the order.
   * @param pAmountInfo - the ItemPriceInfo object.
   * @param pPriceInfoRepItem  - the ItemPriceInfo repository item.
   * @param pOrderManager - order manager.
   * @param pInvalidateCache - invalidateCache boolean for cache invalidation.
   * 
   * @throws Exception if an error occurs.
   */
  protected void loadTaxPriceInfo(Order pOrder, 
                                  Object pAmountInfo, 
                                  MutableRepositoryItem pPriceInfoRepItem,
                                  OrderManager pOrderManager, 
                                  Boolean pInvalidateCache) throws Exception {
    if (isLoggingDebug()) {
      logDebug("Inside loadItemTaxPriceInfo");
    }

    // Get the TaxPriceInfo repository item from the incoming ItemPriceInfo.
    MutableRepositoryItem tpiRepItem = 
      (MutableRepositoryItem) pPriceInfoRepItem.getPropertyValue(getTaxPriceInfoProperty());
    
    Object amtInfo = null;

    // If tpiRepItem not present, then: 
    if (tpiRepItem == null) {
      if (isLoggingDebug()) {
        logDebug("piRepItem is null");
      }

      if (DynamicBeans.getBeanInfo(pAmountInfo).hasProperty(getTaxPriceInfoRepositoryItemName())) {
        DynamicBeans.setPropertyValue(pAmountInfo, getTaxPriceInfoRepositoryItemName(), tpiRepItem);
      }

      DynamicBeans.setPropertyValue(pAmountInfo, getTaxPriceInfoProperty(), amtInfo);
    } 
    else {
      if (isLoggingDebug()) {
        logDebug("piRepItem is not null");
      }

      RepositoryItemDescriptor tpiDesc = tpiRepItem.getItemDescriptor();

      if (pInvalidateCache.booleanValue()) {
        invalidateCache((ItemDescriptorImpl) tpiDesc, tpiRepItem);
      }

      String className = pOrderManager.getOrderTools().getMappedBeanName(tpiDesc.getItemDescriptorName());

      amtInfo = Class.forName(className).newInstance();

      if (DynamicBeans.getBeanInfo(pAmountInfo).hasProperty(getTaxPriceInfoRepositoryItemName())) {
        DynamicBeans.setPropertyValue(pAmountInfo, getTaxPriceInfoRepositoryItemName(), tpiRepItem);
      }

      if (isLoggingDebug()) {
        logDebug("tpiRepitem: " + tpiRepItem.getRepositoryId());
        logDebug("tpiDesc: " + tpiDesc.getItemDescriptorName());
        logDebug("className: " + className);
        logDebug("pAmountInfo: " + pAmountInfo.toString());
      }

      // DynamicBeans.setPropertyValue(pAmountInfo, getItemPriceInfoProperty(), amtInfo);
      try {
        DynamicBeans.setPropertyValue(pAmountInfo, getTaxPriceInfoProperty(), amtInfo);

        readProperties(pOrder, amtInfo, getLoadProperties(), tpiRepItem, tpiDesc, pOrderManager);
      } 
      catch (RemovedItemException rie) {
        if (isLoggingWarning()) {
          logWarning("Problem loading taxPriceInfo.", rie);
        }
      }
    }

    if (pAmountInfo instanceof ChangedProperties) {
      ((ChangedProperties) pAmountInfo).clearChangedProperties();
    }
  }

  /**
   * Overrides the base method to load ShippingPriceInfo's TaxPriceInfo.
   * 
   * @param pOrder - The order whose shipping group's price info is being loaded.
   * @param pCi - The shipping group whose price is being loaded.
   * @param pMutItem - The repository item for the shipping group.
   * @param pOrderManager - The OrderManager that was in the pipeline params.
   * @param pInvalidateCache - If true, then the shipping group's price info repository cache entry is invalidated.
   * 
   * @throws Exception if any exception occurred.
   */
  protected void loadShippingPriceInfo(Order pOrder, 
                                       CommerceIdentifier pCi,
                                       MutableRepositoryItem pMutItem, 
                                       OrderManager pOrderManager,
                                       Boolean pInvalidateCache) throws Exception {
    
    super.loadShippingPriceInfo(pOrder, pCi, pMutItem, pOrderManager, pInvalidateCache);
    
    Object amtInfo = DynamicBeans.getPropertyValue(pCi, getShippingPriceInfoProperty());
    
    MutableRepositoryItem piRepItem = 
      (MutableRepositoryItem) pMutItem.getPropertyValue(getShippingPriceInfoProperty());
    
    if (piRepItem != null) {
      loadTaxPriceInfo(pOrder, amtInfo, piRepItem, pOrderManager, pInvalidateCache);
    }
    
    if (pCi instanceof ChangedProperties) {
      ((ChangedProperties) pCi).clearChangedProperties();
    }
  }
  
}
