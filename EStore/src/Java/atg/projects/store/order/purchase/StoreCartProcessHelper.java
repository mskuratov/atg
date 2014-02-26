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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.catalog.custom.CustomCatalogTools;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.projects.store.order.GiftWrapCommerceItem;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.StoreOrderManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.PropertyManager;

/**
 * <p>
 *   Extends the StorePurchaseProcessHelper to implement cart processing helper methods.
 * </p>
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreCartProcessHelper.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreCartProcessHelper extends StorePurchaseProcessHelper {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreCartProcessHelper.java#3 $$Change: 788278 $";

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------  

  /**
   * Error modifying order message resource key.
   */
  public static final String MSG_ERROR_MODIFYING_ORDER = "errorUpdateOrder";

  /**
   * Error adding to giftlist message resource key.
   */
  public static final String MSG_ERROR_ADDING_TO_GIFTLIST = "errorAddingToGiftlist";

  /**
   * No items selected message resource key.
   */
  public static final String MSG_NO_ITEMS_SELECT = "errorNoItemsSelected";

  /**
   * Invalid items message resource key.
   */
  public static final String MSG_INVALID_ITEMS = "invalidItems";

  /**
   * Invalid recipient e-mail address resource key.
   */
  public static final String MSG_INVALID_RECIPIENT_EMAIL = "invalidRecipientEmailAddress";

  /**
   * Invalid sender e-mail address.
   */
  public static final String MSG_INVALID_SENDER_EMAIL = "invalidSenderEmailAddress";
    
  /**
   * property: profileTools
   */
  protected ProfileTools mProfileTools;

  /**
   * @return the profile tools.
   */
  public ProfileTools getProfileTools() {
    return mProfileTools;
  }
  
  /**
   * @param pProfileTools - the profile tools to set.
   */
  public void setProfileTools(ProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }

  /**
   * property: profilePropertyManager
   */
  PropertyManager mProfilePropertyManager;
  
  /**
   * @param pProfilePropertyManager - the property manager for profiles, used to see if the user is logged in.
   * 
   * @beaninfo description:  The PropertyManager for profiles, used to see if the user is logged in.
   **/
  public void setProfilePropertyManager(PropertyManager pProfilePropertyManager) {
    mProfilePropertyManager = pProfilePropertyManager;
  }

  /**
   * @return profile property manager.
   **/
  public PropertyManager getProfilePropertyManager() {
    return mProfilePropertyManager;
  }
  
  /**
   *<p> 
  *   Adds the given item(s) to the selected gift list.
  * </p>
  * <br />
  * <p>
  *   This method uses the AddCommerceItemInfo items to determine which SKUs and quantities to add to the
  *   gift list.
  * </p>
  * 
  * @param pGiftlistId - A <code>String</code> value.
  * @param pItems - The items to add to the gift list, a <code>AddCommerceItemInfo[]</code> value.
  * @param pGiftlistManager - The gift list manager, a <code>GiftlistManager</code> value.
  * 
  * @return null if item was selected. Otherwise - no items selected message resource key.
  * 
  * @throws ServletException if there was an error while executing the code.
  * @throws IOException if there was an error with servlet io.
  * @throws CommerceException indicates that a severe error occurred while performing a commerce operation.
  */
  public String addItemsToGiftlist(String pGiftlistId, 
                                   AddCommerceItemInfo[] pItems, 
                                   GiftlistManager pGiftlistManager)
    throws ServletException, IOException, CommerceException {

    boolean itemSelected = false;
    
    try {
      
      // Get all SKUs selected.
      if (pItems.length == 0) {
        return null;
      }

      String skuId;
      String productId;
      AddCommerceItemInfo info;
      RepositoryItem product;
      RepositoryItem sku;
      String giftId;
      String displayName = null;
      String description = null;

      CatalogTools cattools = pGiftlistManager.getGiftlistTools().getCatalogTools();

      for (int i = 0; i < pItems.length; i++) {
        info = pItems[i];

        if (info.getQuantity() == 0) {
          continue;
        }

        itemSelected = true;
        skuId = info.getCatalogRefId();
        productId = info.getProductId();
        product = cattools.findProduct(productId);
        sku = cattools.findSKU(skuId);

        if (product != null) {
          displayName = (String) 
            product.getPropertyValue(pGiftlistManager.getGiftlistTools().getDisplayNameProperty());
          
          description = (String) 
            product.getPropertyValue(pGiftlistManager.getGiftlistTools().getDescriptionProperty());
        } 
        else {
          displayName = null;
          description = null;
        }

        // Increment quantity if item is in gift list, otherwise add.
        giftId = pGiftlistManager.getGiftlistItemId(pGiftlistId, skuId);

        if (giftId != null) {
          pGiftlistManager.increaseGiftlistItemQuantityDesired(pGiftlistId, giftId, info.getQuantity());
        } else {
          String itemId = pGiftlistManager.createGiftlistItem(skuId, 
                                                              sku, 
                                                              productId, 
                                                              product, 
                                                              info.getQuantity(), 
                                                              displayName,
                                                              description);
          pGiftlistManager.addItemToGiftlist(pGiftlistId, itemId);
        }
      }
    } 
    catch (RepositoryException exc) {
      throw new CommerceException(exc);
    }
    
    if(itemSelected) {
      return null;
    } 
    else {
      return MSG_NO_ITEMS_SELECT;
    }
  }
  
  /**
   * Check if all items on the order are in the catalog.
   * 
   * @param pOrder - the <code>Order</code> to process.
   * @param pOrderManager - the order manager object. 
   * 
   * @return true - id items are in catalog, otherwise false.
   */
  public boolean areItemsInCatalog(Order pOrder) {
    
    CustomCatalogTools catalogTools = (CustomCatalogTools) getOrderManager().getOrderTools().getCatalogTools();
    List commerceItems = pOrder.getCommerceItems();
    
    if (commerceItems != null) {
      Iterator itemIter = commerceItems.iterator();

      while (itemIter.hasNext()) {
        CommerceItem item = (CommerceItem) itemIter.next();
        RepositoryItem product = (RepositoryItem) item.getAuxiliaryData().getProductRef();

        if (!catalogTools.verifyCatalog(product)) {
          return false;
        }
      }
    }
    
    return true;
  }
  
  /**
   * Get the current security status from the provided profile.
   * 
   * @param pProfile - the profile to get the security status of.
   * 
   * @return Profile's security status as a <code>int</code> value.
   */
  public int getSecurityStatus(RepositoryItem pProfile) {
    // Proceed to login or shipping depending on securityStatus.
    int status = -1;
    
    String securityStatusProperty = 
      getStoreOrderTools().getProfileTools().getPropertyManager().getSecurityStatusPropertyName();
    
    Object securityStatus = pProfile.getPropertyValue(securityStatusProperty);

    if (securityStatus != null) {
      status = ((Integer) securityStatus).intValue();
    }
    return status;
  }
  
  /**
   * Check to see if gift-wrap has been added or removed from the order.
   * 
   * @param pOrder - the <code>Order</code> to process.
   * @param pGiftWrapSelected - value for whether or not gift-wrap is selected, a <code>boolean</code> value.
   * 
   * @return true if gift-wrap has been added or removed from the order, a <code>boolean</code> value.
   */
  public boolean isGiftWrapAddedOrRemoved(StoreOrderImpl pOrder, boolean pGiftWrapSelected) {
    
    // If gift wrap is either being added or removed, we need to re-price.
    boolean orderContainsGiftWrap = pOrder.getContainsGiftWrap();

    if ((pGiftWrapSelected && !orderContainsGiftWrap) || (!pGiftWrapSelected && orderContainsGiftWrap)) {
      return true;
    }
    return false;
  }
  
  /**
   * Check to see if the order contains only gift wrap items.
   * 
   * @param pOrder - An <code>Order</code> value.
   * 
   * @return true if all items on the order are gift-wrapped, a <code>boolean</code> value.
   */
  public boolean isAllGiftWrap(StoreOrderImpl pOrder) {
    Collection items = pOrder.getCommerceItems();
    Iterator itemerator = items.iterator();

    while (itemerator.hasNext()) {
      if (!(itemerator.next() instanceof GiftWrapCommerceItem)) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * Check the security status of the profile to see if the user is authorized.
   * 
   * @param pProfile - the profile to be checked.
   * 
   * @return true if the profile is logged in, a <code>boolean</code> value.
   */
  public boolean isAuthorizedUser(RepositoryItem pProfile) {
    if (getSecurityStatus(pProfile) <= getProfilePropertyManager().getSecurityStatusCookie()) {
      return false;
    }
    
    return true;
  }
  
  /**
   * Check the security status of the profile to see if the user is logged in.
   * 
   * @param pProfile - the profile to be checked.
   * @return true if the profile is logged in, a <code>boolean</code> value.
   */
  public boolean isLoginUser(RepositoryItem pProfile) {
    if (getSecurityStatus(pProfile) < getProfilePropertyManager().getSecurityStatusLogin()) {
      return false;
    }
    
    return true;
  }
  
  /**
   * Rollback the provided transaction.
   * 
   * @param pTransactionManager - Transaction Manager containing the transaction to roll-back, 
   */
  public void rollbackTransaction(TransactionManager pTransactionManager) {
    if (pTransactionManager != null) {
      try {
        Transaction t = pTransactionManager.getTransaction();

        if (t != null) {
          t.setRollbackOnly();
        }
      } 
      catch (SystemException exc) {
        if (isLoggingError()) {
          logError("System Exception occur: ", exc);
        }
      }
    }
  }
  
  /**
   * This method can be used by form handlers to add / remove gift message or
   * gift-wrap from the order.
   *
   * @param pStoreOrder - the order.
   * @param pAddGiftWrap - boolean value indicating whether or not to add gift wrap.
   * @param pAddGiftMessage - boolean value indicating whether or not to add gift message.
   * @param pGiftWrapSkuId - String value indicating Sku Id of the gift wrapped.
   * @param pGiftWrapProductId - String value indicating Product Id of the gift wrapped.
   */
  public void addRemoveGiftServices(StoreOrderImpl pStoreOrder, 
                                    boolean pAddGiftWrap, 
                                    boolean pAddGiftMessage,
                                    String pGiftWrapSkuId, 
                                    String pGiftWrapProductId) throws CommerceException {
    
    StoreOrderManager om = (StoreOrderManager) getOrderManager();
    om.addRemoveGiftServices(pStoreOrder, pAddGiftWrap, pAddGiftMessage, pGiftWrapSkuId, pGiftWrapProductId);
  }
  
}
