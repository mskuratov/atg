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



package atg.projects.store.droplet;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.ShippingGroupManager;

import atg.nucleus.naming.ParameterName;

import atg.projects.store.catalog.StoreCatalogProperties;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.StoreShippingGroupManager;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import atg.targeting.TargetingResults;

import java.io.IOException;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;


/**
 * Checks to see if the gift wrap option should be shown or not.
 * If the order has any item that is not gift wrappable, then don't
 * show the option.
 * <p>
 * Also renders an output parameter to indicate if the order contains any hard good
 * items (other than the gift wrap sku.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/ShowGiftWrap.java#2 $
 */
public class ShowGiftWrap extends DynamoServlet {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/ShowGiftWrap.java#2 $$Change: 768606 $";

  /** Input parameter name order. */
  public static final ParameterName ORDER = ParameterName.getParameterName("order");

  /** Oparam true. */
  public static final ParameterName TRUE = ParameterName.getParameterName("true");

  /** Oparam false. */
  public static final ParameterName FALSE = ParameterName.getParameterName("false");

  /** Oparam isHardGoods. */
  public static final String IS_HARDGOODS = "isHardGoods";

  /**
   * Shipping group manager.
   */
  protected ShippingGroupManager mShippingGroupManager;

  /**
   * Catalog properties.
   */
  protected StoreCatalogProperties mCatalogProperties;

  /**
   * Gift wrap targeting results.
   */
  protected TargetingResults mGiftWrapTargetingResults;

  /**
   * Set the ShippingGroupManager property.
   * @param pShippingGroupManager a <code>ShippingGroupManager</code> value
   */
  public void setShippingGroupManager(ShippingGroupManager pShippingGroupManager) {
    mShippingGroupManager = pShippingGroupManager;
  }

  /**
   * Return the ShippingGroupManager property.
   * @return a <code>ShippingGroupManager</code> value
   */
  public ShippingGroupManager getShippingGroupManager() {
    return mShippingGroupManager;
  }

  /**
   * Set catalog properties.
   *
   * @param pCatalogProperties - catalog properties.
   */
  public void setCatalogProperties(StoreCatalogProperties pCatalogProperties) {
    mCatalogProperties = pCatalogProperties;
  }

  /**
   * Get catalog properties.
   *
   * @return catalog properties.
   */
  public StoreCatalogProperties getCatalogProperties() {
    return mCatalogProperties;
  }

  /**
   * Get gift wrap targeting results.
   *
   * @return gift wrap targeting results.
   */
  public TargetingResults getGiftWrapTargetingResults() {
    return mGiftWrapTargetingResults;
  }

  /**
   * Set gift wrapping targeting results.
   *
   * @param pGiftWrapTargetingResults gift wrap targeting results
   */
  public void setGiftWrapTargetingResults(TargetingResults pGiftWrapTargetingResults) {
    mGiftWrapTargetingResults = pGiftWrapTargetingResults;
  }

  /**
   * Services true oparam if the gift wrapping option should be shown, false if not.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Object orderItem = pRequest.getObjectParameter(ORDER);

    if ((orderItem == null) || !(orderItem instanceof StoreOrderImpl)) {
      if (isLoggingDebug()) {
        logDebug("Bad order parameter passed: null=" + (orderItem == null) +
          ". If null=false, then wrong object type.");
      }

      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);

      return;
    }

    StoreOrderImpl order = (StoreOrderImpl) orderItem;

    StoreShippingGroupManager sgm = (StoreShippingGroupManager) getShippingGroupManager();

    if (!sgm.isAnyHardgoodShippingGroups(order)) {
      pRequest.setParameter(IS_HARDGOODS, Boolean.FALSE);
      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);

      return;
    }

    pRequest.setParameter(IS_HARDGOODS, Boolean.TRUE);

    // If order has hardgood items that are not gift-wrappable, then render false.
    List hgShippingGroups = ((StoreShippingGroupManager) getShippingGroupManager()).getHardgoodShippingGroups(order);
    Iterator shippingGrouperator = hgShippingGroups.iterator();
    ShippingGroup shippingGroup = null;
    List itemRels = null;
    Iterator itemRelerator = null;
    ShippingGroupCommerceItemRelationship rel = null;
    CommerceItem item = null;
    RepositoryItem sku = null;
    String giftWrapEligiblePropertyName = getCatalogProperties().getGiftWrapEligiblePropertyName();
    Boolean giftWrapEligible = null;

    boolean onlyGiftWrapItem = true;
    boolean isGiftItem = false;

    while (shippingGrouperator.hasNext()) {
      shippingGroup = (ShippingGroup) shippingGrouperator.next();
      itemRels = shippingGroup.getCommerceItemRelationships();
      itemRelerator = itemRels.iterator();

      while (itemRelerator.hasNext()) {
        isGiftItem = false;
        rel = (ShippingGroupCommerceItemRelationship) itemRelerator.next();
        item = rel.getCommerceItem();

        sku = (RepositoryItem) item.getAuxiliaryData().getCatalogRef();

        if (sku == null) {
          // This should never happen
          if (isLoggingWarning()) {
            logWarning("There is a commerce item without a SKU, " + " Commerce item id: " + item.getId() +
              " with catalog ref id: " + item.getCatalogRefId() + " on order: " + order.getId());
          }

          continue;
        }

        if (!productIsGiftWrap(item)) {
          onlyGiftWrapItem = false;
        } else {
          isGiftItem = true;
        }

        giftWrapEligible = (Boolean) sku.getPropertyValue(giftWrapEligiblePropertyName);

        if (!isGiftItem && ((giftWrapEligible != null && giftWrapEligible.booleanValue()))) {
          if (isLoggingDebug()) {
            logDebug("Item not gift wrappable: " + item.getCatalogRefId());
          }

          pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);

          return;
        }
      }
    }

    //if the gift wrap item is the only hardgood item, then render false
    if (onlyGiftWrapItem) {
      pRequest.setParameter(IS_HARDGOODS, Boolean.FALSE);
      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
    } else {
      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
    }
  }

  /**
   * Test to see if the item is a gift wrap item. Use TargetingResults
   * to get the gift wrap item product id.
   * @param pItem - item
   * @return true if items is a gift wrap commerce item.
   */
  protected boolean productIsGiftWrap(CommerceItem pItem) {
    String productId = pItem.getAuxiliaryData().getProductId();
    TargetingResults giftWrapTargetingResults = getGiftWrapTargetingResults();
    Enumeration giftWrapItems = giftWrapTargetingResults.getResults();

    if ((giftWrapItems != null) && giftWrapItems.hasMoreElements()) {
      // Gift wrap item targeter produced results, we now make sure
      // the product we are testing isn't a gift wrap product.
      RepositoryItem giftWrapProduct = (RepositoryItem) giftWrapItems.nextElement();

      if (productId.equals(giftWrapProduct.getRepositoryId())) {
        if (isLoggingDebug()) {
          logDebug("Item is not gift wrappable, but it's a gift wrap item!");
        }

        return true;
      }
    }

    return false;
  }
}
