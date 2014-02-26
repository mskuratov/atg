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


package atg.projects.store.order;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import atg.commerce.CommerceException;
import atg.commerce.claimable.ClaimableManager;
import atg.commerce.gifts.GiftlistHandlingInstruction;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.InventoryManager;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.HandlingInstruction;
import atg.commerce.order.HandlingInstructionManager;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderTools;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupRelationship;
import atg.commerce.order.SimpleOrderManager;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.commerce.pricing.TaxPriceInfo;
import atg.commerce.profile.CommerceProfileTools;
import atg.commerce.profile.CommercePropertyManager;
import atg.commerce.promotion.PromotionTools;
import atg.commerce.states.CommerceItemStates;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.droplet.DropletFormException;
import atg.projects.store.logging.LogUtils;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.idgen.IdGenerator;
import atg.service.idgen.IdGeneratorException;


/**
 * <p>
 *   The class extends the ATG SimpleOrderManager. The main functionality
 *   added to this class is related to gift services. The business logic for
 *   addition and removal of gift message and gift wrap is here.
 * </p>
 * <br />
 * <p>
 *   Also included in this class is the logic for building a set of
 *   AddCommerceItemInfo objects based on an Order.
 * </p>
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/StoreOrderManager.java#3 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreOrderManager extends SimpleOrderManager {
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/StoreOrderManager.java#3 $$Change: 788278 $";

  /**
   * Resource bundle name.
   */
  private static final String MY_RESOURCE_NAME = "atg.commerce.inventory.Resources";

  /**
   * Resource bundle.
   */
  static ResourceBundle sResources = 
    LayeredResourceBundle.getBundle(MY_RESOURCE_NAME,
                                    atg.service.dynamo.LangLicense.getLicensedDefault());

  /**
   * Item not found property name.
   */
  private static final String ITEM_NOT_FOUND = "noSuchItem";

  /**
   * property: adjustInventoryOnCheckout
   */
  private boolean mAdjustInventoryOnCheckout;
  
  /**
   * @return true if inventory should be decremented on checkout, otherwise false.
   */
  public boolean isAdjustInventoryOnCheckout() {
    return mAdjustInventoryOnCheckout;
  }

  /**
   * @param pAdjustInventoryOnCheckout - boolean indicating if inventory should be decremented on checkout.
   */
  public void setAdjustInventoryOnCheckout(boolean pAdjustInventoryOnCheckout) {
    mAdjustInventoryOnCheckout = pAdjustInventoryOnCheckout;
  }

  /**
   * property: commerceItemStates
   */
  private CommerceItemStates mCommerceItemStates;
  
  /**
   * @return the commerce item states component.
   */
  public CommerceItemStates getCommerceItemStates() {
    return mCommerceItemStates;
  }

  /**
   * @param pCommerceItemStates - the commerce item states component.
   */
  public void setCommerceItemStates(CommerceItemStates pCommerceItemStates) {
    mCommerceItemStates = pCommerceItemStates;
  }

  /**
   * property: giftListManager
   */
  private GiftlistManager mGiftlistManager;
  
  /**
   * Specifies the GiftlistManager.
   * 
   * @param pGiftlistManager - a <code>GiftlistManager</code> value.
   */
  public void setGiftlistManager(GiftlistManager pGiftlistManager) {
    mGiftlistManager = pGiftlistManager;
  }

  /**
   * The gift list manager.
   * 
   * @return a <code>GiftlistManager</code> value.
   */
  public GiftlistManager getGiftlistManager() {
    return mGiftlistManager;
  }

  /**
   * property: OMSTransactionIdGenerator
   */
  private IdGenerator mOMSTransactionIdGenerator;
  
  /**
   * Specifies the OMSTransactionIdGenerator.
   * 
   * @param pOMSTransactionIdGenerator - a <code>OMSTransactionIdGenerator</code> value.
   */
  public void setOMSTransactionIdGenerator(IdGenerator pOMSTransactionIdGenerator) {
    mOMSTransactionIdGenerator = pOMSTransactionIdGenerator;
  }

  /**
   * The OMSTransactionIdGenerator.
   * 
   * @return a <code>OMSTransactionIdGenerator</code> value.
   */
  public IdGenerator getOMSTransactionIdGenerator() {
    return mOMSTransactionIdGenerator;
  }

  /**
   * property: OMSTransactionIdSpace
   */
  private String mOMSTransactionIdSpace = "storeOMSTransaction";
  
  /**
   * Specifies the OMSTransactionIdSpace.
   * 
   * @param pOMSTransactionIdSpace - a <code>OMSTransactionIdSpace</code> value.
   */
  public void setOMSTransactionIdSpace(String pOMSTransactionIdSpace) {
    mOMSTransactionIdSpace = pOMSTransactionIdSpace;
  }

  /**
   * The OMSTransactionIdSpace.
   * 
   * @return a <code>OMSTransactionIdSpace</code> value.
   */
  public String getOMSTransactionIdSpace() {
    return mOMSTransactionIdSpace;
  }

  /**
   * Adds the gift message to the order's special instructions.
   *
   * @param pOrder - the order to remove gift wrap from.
   * @param pMessageTo - the "message to:" field.
   * @param pMessage - the message body.
   * @param pMessageFrom - the "message from:" field.
   * 
   * @exception CommerceException if an error occurs removing item from order.
   */
  public void addGiftMessage(StoreOrderImpl pOrder, String pMessageTo, String pMessage, String pMessageFrom)
    throws CommerceException {
    
    // Issues with using "put" when key/value pair is already in the Map.
    // As a result, remove keys and values explicitly before put.
    removeGiftMessage(pOrder);
    
    // Get the special instructions from the order and add empty values for gift messages.
    Map specialInstructions = pOrder.getSpecialInstructions();

    if (isLoggingDebug()) {
      logDebug("Adding gift message. Current gm to: " +
        specialInstructions.get(StoreOrderImpl.GIFT_MESSAGE_TO_KEY) + ", gm: " +
        specialInstructions.get(StoreOrderImpl.GIFT_MESSAGE_KEY) + ", gm from: " +
        specialInstructions.get(StoreOrderImpl.GIFT_MESSAGE_FROM_KEY));
    }

    specialInstructions.put(StoreOrderImpl.GIFT_MESSAGE_TO_KEY, pMessageTo);
    
    if (!StringUtils.isEmpty(pMessage)) {
      specialInstructions.put(StoreOrderImpl.GIFT_MESSAGE_KEY, pMessage);
    }
    
    specialInstructions.put(StoreOrderImpl.GIFT_MESSAGE_FROM_KEY, pMessageFrom);

    updateOrder(pOrder);
  }

  /**
   * Removes the gift message from the order.
   *
   * @param pOrder - the order to remove gift wrap from.
   * 
   * @exception CommerceException if an error occurs removing item from order.
   */
  public void removeGiftMessage(StoreOrderImpl pOrder)
    throws CommerceException {
    
    Map specialInstructions = pOrder.getSpecialInstructions();

    if (isLoggingDebug()) {
      logDebug("Removing gift message. Current gm to: " +
        specialInstructions.get(StoreOrderImpl.GIFT_MESSAGE_TO_KEY) + ", gm: " +
        specialInstructions.get(StoreOrderImpl.GIFT_MESSAGE_KEY) + ", gm from: " +
        specialInstructions.get(StoreOrderImpl.GIFT_MESSAGE_FROM_KEY));
    }

    // Remove the gift message special instructions from the order.
    specialInstructions = pOrder.getSpecialInstructions();

    Set instructionKeys = specialInstructions.keySet();

    // Remove gift message to
    if (instructionKeys.contains(StoreOrderImpl.GIFT_MESSAGE_TO_KEY)) {
      if (isLoggingDebug()) {
        logDebug("Removing specialInstructions entry for gift message to.");
      }

      specialInstructions.remove(StoreOrderImpl.GIFT_MESSAGE_TO_KEY);

      if (isLoggingDebug()) {
        logDebug("Removing instructions key for gift message to.");
      }

      instructionKeys.remove(StoreOrderImpl.GIFT_MESSAGE_TO_KEY);
    }

    // Remove gift message.
    if (instructionKeys.contains(StoreOrderImpl.GIFT_MESSAGE_KEY)) {
      if (isLoggingDebug()) {
        logDebug("Removing specialInstructions entry for gift message.");
      }

      specialInstructions.remove(StoreOrderImpl.GIFT_MESSAGE_KEY);

      if (isLoggingDebug()) {
        logDebug("Removing instructions key for gift message.");
      }

      instructionKeys.remove(StoreOrderImpl.GIFT_MESSAGE_KEY);
    }

    // Remove gift message from.
    if (instructionKeys.contains(StoreOrderImpl.GIFT_MESSAGE_FROM_KEY)) {
      if (isLoggingDebug()) {
        logDebug("Removing specialInstructions entry for gift message from.");
      }

      specialInstructions.remove(StoreOrderImpl.GIFT_MESSAGE_FROM_KEY);

      if (isLoggingDebug()) {
        logDebug("Removing instructions key for gift message from.");
      }

      instructionKeys.remove(StoreOrderImpl.GIFT_MESSAGE_FROM_KEY);
    }
  }
  
  /**
   * This method can be used by form handlers to add / remove gift message or
   * gift wrap from the order.
   *
   * @param pOrder - The order.
   * @param pAddGiftWrap - boolean value indicating whether or not to add gift wrap.
   * @param pAddGiftMessage - boolean value indicating whether or not to add gift message.
   * @param pGiftWrapSkuId - String value indicating Sku Id of the gift wrapped.
   * @param pGiftWrapProductId - String value indicating Product Id of the gift wrapped.
   */
  public void addRemoveGiftServices(StoreOrderImpl pOrder, 
                                    boolean pAddGiftWrap, 
                                    boolean pAddGiftMessage,
                                    String pGiftWrapSkuId, 
                                    String pGiftWrapProductId) throws CommerceException {
    try {
      if (pAddGiftWrap) {
        // User wants gift wrap.
        if (!pOrder.getContainsGiftWrap()) {
          // Gift wrap item not in order, add now.
          addGiftWrap(pOrder, pGiftWrapSkuId, pGiftWrapProductId);
        }
      } 
      else {
        // user does not want gift wrap
        if (pOrder.getContainsGiftWrap()) {
          removeGiftWrap(pOrder);
        }
      }

      if (pAddGiftMessage) {
        // User wants gift message.
        if (!pOrder.getContainsGiftMessage() && !pOrder.isShouldAddGiftNote()) {
          pOrder.setShouldAddGiftNote(true);
        }
      } 
      else {
        // User does not want gift message.
        if (pOrder.getContainsGiftMessage() || pOrder.isShouldAddGiftNote()) {
          // Remove it from the order.
          removeGiftMessage(pOrder);
          pOrder.setShouldAddGiftNote(false);
        }
      }

      updateOrder(pOrder);
    } 
    catch (CommerceException ce) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error adding/removing gift services."), ce);
      }
      
      throw ce;
    }
  }

  /**
   * Creates and adds the gift wrap commerce item to the order.
   *
   * @param pOrder - the order to add gift wrap to.
   * @param pSkuId - SKU id.
   * @param pProductId - product id.
   * 
   * @exception CommerceException if error creating item or adding item to order.
   */
  @SuppressWarnings("unchecked") //Ok to suppress because we know which Collections we use here.
  public void addGiftWrap(StoreOrderImpl pOrder, String pSkuId, String pProductId)
    throws CommerceException {
    
    if (isLoggingDebug()) {
      logDebug("Gift wrap item being added. Sku id: " + pSkuId + ", prod id: " + pProductId);
    }

    if (pOrder.getContainsGiftWrap()) {
      if (isLoggingWarning()) {
        logWarning("This order already has gift wrap.");
      }

      return;
    }

    ShippingGroup sg = null;
    List<ShippingGroup> shippingGroups = pOrder.getShippingGroups();
    
    for (ShippingGroup shippingGroup: shippingGroups) {
      //Search for gift shipping groups first.
      List handlingInstructions = shippingGroup.getHandlingInstructions();
      
      //If found, use it
      if (handlingInstructions != null && handlingInstructions.size() > 0) {
        sg = shippingGroup;
        break;
      }
    }
    
    //If gift shipping group is not found, get the first comer
    sg = sg == null ? (ShippingGroup) pOrder.getShippingGroups().get(0) : sg;

    CommerceItemManager ciManager = getCommerceItemManager();
    long giftWrapQuantity = 1;

    StoreOrderTools orderTools = (StoreOrderTools) getOrderTools();

    CommerceItem giftWrapItem = 
      ciManager.createCommerceItem(orderTools.getGiftWrapCommerceItemType(), 
                                   pSkuId, pProductId, 
                                   giftWrapQuantity);

    ciManager.addItemToOrder(pOrder, giftWrapItem);

    ciManager.addItemQuantityToShippingGroup(pOrder, giftWrapItem.getId(), sg.getId(), giftWrapQuantity);

    List list = sg.getHandlingInstructions();

    if ((list != null) && (list.size() > 0)) {
      // If this is a gift shipping group and we are adding gift wrap to it, we need to add
      // a handling instruction so that ShippingGroupDroplet/FormHandler won't attempt to
      // add the gift wrap to a different shipping group.
      HandlingInstructionManager himgr = getHandlingInstructionManager();
      HandlingInstruction handlingInstruction = (HandlingInstruction) list.get(0);
      HandlingInstruction newHandlingInstruction = himgr.copyHandlingInstruction(handlingInstruction);
      newHandlingInstruction.setCommerceItemId(giftWrapItem.getId());
      sg.addHandlingInstruction(newHandlingInstruction);
    }

    updateOrder(pOrder);
  }

  /**
   * Removes the gift wrap commerce item from the order.
   *
   * @param pOrder - the order to remove gift wrap from.
   * 
   * @exception CommerceException if an error occurs removing item from order.
   */
  public void removeGiftWrap(StoreOrderImpl pOrder) throws CommerceException {
    
    int itemCount = pOrder.getCommerceItemCount();
    List items = pOrder.getCommerceItems();
    CommerceItem item;
    CommerceItemManager ciManager = getCommerceItemManager();
    String giftWrapItemId = null;

    for (int i = 0; i < itemCount; i++) {
      item = (CommerceItem) items.get(i);

      if (item instanceof GiftWrapCommerceItem) {
        giftWrapItemId = item.getId();
      }
    }

    // Don't remove the item while looping around commerce items because you'll
    // see an index out of bounds exception if the gift wrap item isn't the last
    // item. Instead, do it here, then update order.
    if (giftWrapItemId != null) {
      ciManager.removeItemFromOrder(pOrder, giftWrapItemId);
    }

    updateOrder(pOrder);
  }

  /**
   * This method creates an order's duplicate with a new ID.
   * <br/>
   * The new order will possess copies of original order's commerce items. Items created from 
   * a gift-list will be marked as created from gift-list. There will be created shipping groups 
   * to represent all gift-lists used and proper links will be set.
   * <br/>
   * The new order will also be re-priced in order to user proper promotions and prices.
   * 
   * @param pOrderId base order, specified by its ID.
   * @param pProfile new order will be created on behalf of this user.
   * 
   * @return a fully ready to work copy of input order.
   * 
   * @throws CommerceException if unable to create the order.
   */
  public Order cloneOrder(String pOrderId, RepositoryItem pProfile) throws CommerceException {
    Order result = null;
   
    // First, load an input order. This will load all necessary data from database.
    Order order = loadOrder(pOrderId);
    
    // Create new order with the same type on behalf of the user specified.
    result = createOrder(pProfile.getRepositoryId(), order.getOrderClassType());
    
    // New property created order must contain at least one shipping group. Get it.
    ShippingGroup firstShippingGroup = (ShippingGroup) result.getShippingGroups().get(0);
    
    // Main loop, copy all commerce items from the input order.
    for (CommerceItem item : (Collection<CommerceItem>) order.getCommerceItems()) {
      // Create commerce item's clone with new ID and the same properties.
      CommerceItem clonedItem = 
        getCommerceItemManager().createCommerceItem(item.getCommerceItemClassType(),
                                                    item.getCatalogRefId(), 
                                                    item.getAuxiliaryData().getCatalogRef(), 
                                                    item.getAuxiliaryData().getProductId(),
                                                    item.getAuxiliaryData().getProductRef(), 
                                                    item.getQuantity(), 
                                                    item.getCatalogKey(), 
                                                    item.getCatalogId(),
                                                    item.getAuxiliaryData().getSiteId(), 
                                                    null);
      // Add this item to the newly created order.
      getCommerceItemManager().addItemToOrder(result, clonedItem);
      
      // And add it to the first order's shipping group.
      getCommerceItemManager().addItemQuantityToShippingGroup(result, 
                                                              clonedItem.getId(), 
                                                              firstShippingGroup.getId(), 
                                                              clonedItem.getQuantity());
      
      // Now iterate over all available shipping group relationships, we need them in order to 
      // find linked shipping groups.
      for (ShippingGroupRelationship shippingRelationship : 
           (Collection<ShippingGroupRelationship>) item.getShippingGroupRelationships()) {
        
        // Get all gift list handling instructions for the current commerce item and shipping group, 
        // iterate over them.
        for (GiftlistHandlingInstruction giftInstruction :
          (Collection<GiftlistHandlingInstruction>) getGiftlistManager().
            getGiftHandlingForShippingGroup(shippingRelationship.getShippingGroup(), item)) {
          
          // Mark the new commerce item as created from a gift list. This will move item's quantity 
          // from general quantity to gift quantity. It also will make all necessary links to gift list
          // and create a shipping group (if needed) with giftlist's address.
          getGiftlistManager().addGiftToOrder(pProfile, 
                                              result, 
                                              clonedItem.getId(), 
                                              firstShippingGroup,
                                              giftInstruction.getQuantity(), 
                                              giftInstruction.getGiftlistId(), 
                                              giftInstruction.getGiftlistItemId());
        }
      }
    }
    // Run first price process on the newly created order.
    priceOrderTotal(result);
    
    // Order is ready, return it to caller.
    return result;
  }

  /**
   * This method is a shortcut to the {@link PricingTools#priceOrderTotal(atg.commerce.order.Order)} method.
   * 
   * @param pOrder order to be re-priced.
   * 
   * @throws PricingException if unable to re-price the order specified.
   */
  protected final void priceOrderTotal(Order pOrder) throws PricingException {
    getOrderTools().getProfileTools().getPricingTools().priceOrderTotal(pOrder);
  }

  /**
   * This method sets the state of each commerce item based on the SKU's current
   * inventory availability. If adjustInventoryOnCheckout = true, it also decrements 
   * the inventory levels of the items in the order.
   * <br />
   *              
   * @param pOrder Order whose commerce items statuses are to be updated in inventory.
   *
   * @throws atg.commerce.inventory.InventoryException if an error occurs
   * 
   * @beaninfo description: This method takes the order and iterates through commerce items
   *              to update the inventory status.
   */
  public void manageInventoryOnCheckout(Order pOrder) throws InventoryException {
    InventoryManager inventoryManager = getOrderTools().getInventoryManager();
    List commerceItems = pOrder.getCommerceItems();
    Iterator iter = commerceItems.iterator();

    while (iter.hasNext()) {
      CommerceItem commItem = (CommerceItem) iter.next();
      long quantity = commItem.getQuantity();
      String id = commItem.getCatalogRefId();

      if (isLoggingDebug()) {
        logDebug("inside adjustInventory . Sku id: " + id);
        logDebug("inside adjustInventory . quantity: " + quantity);
      }

      // Check for the status and call the appropriate method.
      try {
        int status = checkStatus(commItem);

        if (status == InventoryManager.AVAILABILITY_STATUS_PREORDERABLE) {
          if (isLoggingDebug()) {
            logDebug("item is preorderable");
          }

          commItem.setState(getCommerceItemStates().getStateValue(CommerceItemStates.PRE_ORDERED));

          if (isAdjustInventoryOnCheckout()) {
            int inventoryStatus = inventoryManager.preorder(id, quantity);

            if (inventoryStatus == InventoryManager.INVENTORY_STATUS_INSUFFICIENT_SUPPLY) {
              inventoryManager.setPreorderLevel(id, 0);
            }
          }
        } 
        else if (status == InventoryManager.AVAILABILITY_STATUS_BACKORDERABLE) {
          if (isLoggingDebug()) {
            logDebug("item is backorderable");
          }

          commItem.setState(getCommerceItemStates().getStateValue(CommerceItemStates.BACK_ORDERED));

          if (isAdjustInventoryOnCheckout()) {
            int inventoryStatus = inventoryManager.backorder(id, quantity);

            if (inventoryStatus == InventoryManager.INVENTORY_STATUS_INSUFFICIENT_SUPPLY) {
              inventoryManager.setBackorderLevel(id, 0);
            }
          }
        } 
        else if (status == InventoryManager.AVAILABILITY_STATUS_IN_STOCK) {
          if (isLoggingDebug()) {
            logDebug("item is in stock");
          }

          if (isAdjustInventoryOnCheckout()) {
            int inventoryStatus = inventoryManager.purchase(id, quantity);

            if (inventoryStatus == InventoryManager.INVENTORY_STATUS_INSUFFICIENT_SUPPLY) {
              inventoryManager.setStockLevel(id, 0);
            }
          }
        } 
        else {
          if (isLoggingDebug()) {
            logDebug("Availability Status: " + inventoryManager.queryAvailabilityStatus(id));
          }
        }
      } 
      catch (InventoryException e) {
        Object[] args = { id };

        if (isLoggingWarning()) {
          logWarning(ResourceUtils.getMsgResource(ITEM_NOT_FOUND, MY_RESOURCE_NAME, sResources, args));
        }
      }
    }
  }

  /**
   * This method uses the inventory manager to query the availability status of the
   * given commerce item.
   *
   * @param pCommItem commerce item whose status is to be checked in inventory.
   * 
   * @return return status either pre-orderable or back-orderable or in stock.
   *
   * @throws atg.commerce.inventory.InventoryException if an error occurs.
   */
  public int checkStatus(CommerceItem pCommItem) throws InventoryException {
    InventoryManager inventoryManager = getOrderTools().getInventoryManager();
    String skuId = pCommItem.getCatalogRefId();

    return inventoryManager.queryAvailabilityStatus(skuId);
  }

  /**
   * Use the OMSTransactionIdGenerator to get the next Transaction Id.
   *
   * @return New transactionId generated by OMSTransactionIdGenerator.
   */
  public String getOMSTransactionId() {
    IdGenerator IdGenerator = getOMSTransactionIdGenerator();
    String idSpace = getOMSTransactionIdSpace();

    if (IdGenerator == null) {
      if (isLoggingDebug()) {
        logDebug(LogUtils.formatMajor("Cannot generate OMSTransactionId. IdGenerator is null."));
      }

      return null;
    }

    // Generate an Id in the OMSTransaction IdSpace and return it.
    try {
      String newId = IdGenerator.generateStringId(idSpace);

      if (isLoggingDebug()) {
        logDebug("OrderManager:getOMSTransactionId: Generated OMSTransactionId = " + newId);
      }

      return newId;
    } 
    catch (IdGeneratorException ie) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error in generating new OMSTransactionId."), ie);
      }

      return null;
    }
  }

  /**
   * <p>
   *   Calls OrderManager.createOrder to create a new Order object using the class mapped to the 
   *   given name in pOrderType and whose id will be that which is supplied in pOrderId. Populates 
   *   the Order with the supplied data.
   * </p>
   * <br />
   * <p>
   *   Sets shipping method to the shipping groups the same as profile's default shipping method.
   * </p>
   * 
   * @param pProfileId - the id of the Profile object which this Order belongs to.
   * @param pOrderId - the id which will be assigned to the Order.
   * @param pOrderPriceInfo - the OrderPriceInfo object for this Order.
   * @param pTaxPriceInfo - the TaxPriceInfo object for this Order.
   * @param pShippingPriceInfo - the ShippingPriceInfo object for the default ShippingGroup.
   * @param pOrderType - the name that is mapped in the OrderTools.properties file to the class of
   *                     the desired type to create.
   *                   
   * @return the Order object which was created.
   * 
   * @throws CommerceException indicates that a severe error occurred while performing a commerce operation.
   */
  @Override
  public Order createOrder(String pProfileId, 
                           String pOrderId, 
                           OrderPriceInfo pOrderPriceInfo,
                           TaxPriceInfo pTaxPriceInfo, 
                           ShippingPriceInfo pShippingPriceInfo,
                           String pOrderType) throws CommerceException {

    Order order = 
      super.createOrder(pProfileId, pOrderId, pOrderPriceInfo, pTaxPriceInfo, pShippingPriceInfo, pOrderType);
    
    try {
      RepositoryItem profile = getOrderTools().getProfileTools().getProfileForOrder(order);
      
      if(profile != null) {
        
        CommercePropertyManager propManager = 
          (CommercePropertyManager) getOrderTools().getProfileTools().getPropertyManager();
        
        String defaultShippingMethod = 
          (String) profile.getPropertyValue(propManager.getDefaultShippingMethodPropertyName());
        
        List shipGroups = order.getShippingGroups();
        
        if(shipGroups != null) {
          for(Object shipGroup : shipGroups) {
            if(shipGroup instanceof HardgoodShippingGroup) {
              ((HardgoodShippingGroup)shipGroup).setShippingMethod(defaultShippingMethod);
            }
          }
        }
      }
      
    } 
    catch (RepositoryException e) {
      if (isLoggingError()){
        logError(LogUtils.formatMajor("Cannot retrieve user for order " + order.getId()), e);
      }
    }

    return order;
  }
  
  /**
   * This method calculates a coupon code used by the order specified. It looks up an owner of the 
   * order (i.e. profile) and iterates over its active promotion statuses. It calculates all coupons 
   * linked by these statuses. First coupon with proper site ID (i.e. from the shared cart site group)
   * will be returned.
   * 
   * @param pOrder - order to be inspected.
   * 
   * @return coupon code used by the order or {@code null} if none of coupons used.
   * 
   * @throws CommerceException if something goes wrong.
   */
  @SuppressWarnings("unchecked") // OK to suppress, we just cast collections.
  public String getCouponCode(StoreOrderImpl pOrder) throws CommerceException {
    try {
      // Get useful managers and tools.
      OrderTools orderTools = getOrderTools();
      Repository profileRepository = orderTools.getProfileRepository();
      CommerceProfileTools profileTools = orderTools.getProfileTools();
      ClaimableManager claimableManager = profileTools.getClaimableManager();
      PromotionTools promotionTools = claimableManager.getPromotionTools();
      
      // In order to calculate active coupon, we will have to iterate over profile's 
      // active promotions. Find a user.
      RepositoryItem profile = 
        profileRepository.getItem(pOrder.getProfileId(), profileTools.getDefaultProfileType());
      
      // Take its active promotions and iterate over them.
      Collection<RepositoryItem> activePromotions = (Collection<RepositoryItem>) 
        profile.getPropertyValue(promotionTools.getActivePromotionsProperty());
      
      for (RepositoryItem promotionStatus: activePromotions) {
        
        // Each promotionStatus contains a list of coupons that activated a promotion. Inspect these coupons.
        Collection<RepositoryItem> coupons = (Collection<RepositoryItem>) 
          promotionStatus.getPropertyValue(promotionTools.getPromoStatusCouponsPropertyName());
        
        for (RepositoryItem coupon: coupons) {
          // Proper coupon? I.e. does it came from a shared cart site group?
          if (claimableManager.checkCouponSite(coupon)) {
            // True, return this coupon code.
            String couponId = (String) 
              coupon.getPropertyValue(claimableManager.getClaimableTools().getIdPropertyName());
            
            return couponId;
          }
        }
      }
    } 
    catch (RepositoryException e) {
      throw new CommerceException(e);
    }
    // Can't find proper coupon, return nothing.
    return null;
  }
}
