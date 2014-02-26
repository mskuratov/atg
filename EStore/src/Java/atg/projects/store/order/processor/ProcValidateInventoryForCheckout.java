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

import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.InventoryInfo;
import atg.commerce.inventory.InventoryManager;

import atg.commerce.order.*;

import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;

import atg.nucleus.logging.ApplicationLoggingImpl;

import atg.projects.store.inventory.StoreInventoryManager;
import atg.projects.store.promotion.StoreGWPManager;

import atg.repository.RepositoryItem;

import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import java.text.MessageFormat;

import java.util.*;


/**
 * <p>
 *   Determine if the item is available by ensuring that at least 1 is available for pre-order, 
 *   back-order or immediate availability.
 * </p>
 * <p>
 *   This does not check the quantity being purchased with the levels available and the threshold 
 *   levels. The OMS will do that since it has more up to the minute inventory data. The order will 
 *   be accepted as long as the inventory data shows that it can be at least partially fulfilled.
 * </p>
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/processor/ProcValidateInventoryForCheckout.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class ProcValidateInventoryForCheckout extends ApplicationLoggingImpl implements PipelineProcessor {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/processor/ProcValidateInventoryForCheckout.java#3 $$Change: 788278 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  
  public static final String DISPLAY_NAME_PROPERTY_NAME = "displayName";
  
  /**
   * Item out of stock resource key.
   */
  public static final String ITEM_OUT_OF_STOCK = "itemOutOfStock";
  
  /**
   * Gift item out of stock resource key.
   */
  public static final String GIFT_ITEM_OUT_OF_STOCK = "giftItemOutOfStock";

  /**
   * Resource bundle name.
   */
  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /**
   * User messages resource bundle name.
   */
  static final String USER_MSGS_RES_NAME = "atg.commerce.order.UserMessages";

  // Resource message keys
  public static final String MSG_INVALID_ORDER_PARAMETER = "InvalidOrderParameter";

  /** 
   * Resource Bundle. 
   */
  private static java.util.ResourceBundle sResourceBundle = 
    LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  /**
   * Success constant.
   */
  private static final int SUCCESS = 1;

  /**
   * Logging identifier.
   */
  String mLoggingIdentifier = "ProcValidateInventoryForCheckout";

  /**
   * Sets property LoggingIdentifier.
   * 
   * @param pLoggingIdentifier - logging identifier.
   */
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }

  /**
   * @return property LoggingIdentifier.
   */
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
  }
  
  /**
   * Manager component for gift with purchase promotions.
   */
  protected StoreGWPManager mGwpManager;

  /**
   * Setter for the gift with purchase manager property.
   * 
   * @param pGwpManager - GWPManager.
   */
  public void setGwpManager(StoreGWPManager pGwpManager) {
    mGwpManager = pGwpManager;
  }

  /**
   * Getter for the gift with purchase manager property.
   * 
   * @return the store GWPManager.
   */
  public StoreGWPManager getGwpManager() {
    return mGwpManager;
  }

  /**
   * Validates that there is inventory for all items in the order.
   *
   * @param pParam a HashMap which must contain an Order and optionally a Locale object.
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invocation.
   *                
   * @return an integer specifying the processor's return code.
   * 
   * @throws InvalidParameterException if an invalid argument is passed into a method call.
   * 
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   * @see atg.commerce.pricing.AmountInfo
   * @see atg.commerce.pricing.OrderPriceInfo
   * @see atg.commerce.pricing.TaxPriceInfo
   * @see atg.commerce.pricing.ItemPriceInfo
   * @see atg.commerce.pricing.ShippingPriceInfo
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws InvalidParameterException {
    
    boolean inventoryAvailable = true;

    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    OrderManager om = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);
    InventoryManager inventoryManager = om.getOrderTools().getInventoryManager();

    if (om == null) {
      throw new InvalidParameterException("OrderManager is null");
    }

    if (order == null) {
      throw new InvalidParameterException(ResourceUtils.getMsgResource(MSG_INVALID_ORDER_PARAMETER, 
                                                                       MY_RESOURCE_NAME,
                                                                       sResourceBundle));
    }

    ResourceBundle bundle = null;
    Locale resourceLocale = (Locale) map.get(PipelineConstants.LOCALE);
    bundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, resourceLocale);

    List items = order.getCommerceItems();

    for (int j = 0; j < items.size(); j++) {
      CommerceItem item = (CommerceItem) items.get(j);

      boolean isGiftItem = isGiftItem(order, item);
      
      if (!itemIsAvailable(item, inventoryManager)) {
        String catalogRefId = item.getCatalogRefId();

        if (isLoggingDebug()) {
          logDebug("None of this item in stock, preorderable or backorderable: " + catalogRefId);
        }

        RepositoryItem ciRep = (RepositoryItem) item.getAuxiliaryData().getCatalogRef();
        String name = (String) ciRep.getPropertyValue(DISPLAY_NAME_PROPERTY_NAME);

        Object[] objParams = { name };
        
        // Display different type of messages for gift item and ordinary item.
        String formattedMsg = null;
        
        if (isGiftItem) {
          formattedMsg = MessageFormat.format(bundle.getString(GIFT_ITEM_OUT_OF_STOCK), objParams);
          addHashedError(pResult, GIFT_ITEM_OUT_OF_STOCK, catalogRefId, formattedMsg);
        }
        else {
          formattedMsg = MessageFormat.format(bundle.getString(ITEM_OUT_OF_STOCK), objParams);
          addHashedError(pResult, ITEM_OUT_OF_STOCK, catalogRefId, formattedMsg);
        }

        inventoryAvailable = false;
      }
    }

    if (!inventoryAvailable) {
      return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
    }

    return SUCCESS;
  }

  /**
   * <p>
   *   Determine if the item is available by ensuring that at least 1 is available for pre-order, 
   *   back-order or immediate availability. 
   * </p>
   * <p>
   *   This does not check the quantity being purchased with the levels available and the threshold 
   *   levels. The OMS will do that since it has more up to the minute inventory data. The order will
   *   be accepted as long as the inventory data shows that it can be at least partially fulfilled.
   * </p>
   *   
   * @param pItem - commerce item.
   * @param pManager - inventory manager.
   * 
   * @return a boolean with true for available and false for unavailable.
   */
  protected boolean itemIsAvailable(CommerceItem pItem, InventoryManager pManager) {
    
    boolean available = false;

    // Get the item Id we'll use it lots of places.
    String itemId = pItem.getCatalogRefId();

    if (isLoggingDebug()) {
      logDebug("itemIsAvailable(): checking inventory statuc for commerce item " + pItem);
    }

    // We should have a StoreInventoryManager as the inventory manager, but just in
    // case, do a check and still work if it is the default inventory manager (we just will
    // not handle pre-order as expected).
    if (pManager instanceof StoreInventoryManager) {
      
      StoreInventoryManager storeInvManager = (StoreInventoryManager) pManager;
      RepositoryItem pProduct = (RepositoryItem) pItem.getAuxiliaryData().getProductRef();

      try {
        int availability = storeInvManager.queryAvailabilityStatus(pProduct, itemId);

        if (isLoggingDebug()) {
          logDebug("itemIsAvailable(): item " + itemId + " availability=" + availability);
        }
                
        // If gift item check only in stock availability, back-orderable and 
        // pre-orderable states are not accepted.
        if ((availability == storeInvManager.getAvailabilityStatusInStockValue()) ||
            (availability == storeInvManager.getAvailabilityStatusBackorderableValue()) ||
            (availability == storeInvManager.getAvailabilityStatusPreorderableValue())) {
          
          available = true;
        } 
        else {
          // For store, default everything else to unavailable.
          available = false;  
        }
      } 
      catch (InventoryException ie) {
        // If we can't look up the item, then assume no availability.
        available = false;
      }
    } 
    else {
      if (isLoggingWarning()) {
        logWarning(
          "ProcValidateInventoryForCheckout: the inventory manager is not a StoreInventoryManager. " +
          "Preorder may not be handled as expected.");
      }

      // Create the inventory info for the item.
      InventoryInfo info = new InventoryInfo(itemId, pManager, this);

      if (isLoggingDebug()) {
        logDebug("itemIsAvailable(): item " + itemId + " availability=" + info.getAvailabilityStatusMsg());
      }

      if (info.getAvailabilityStatus() != null) {
        // Get the availability status from the info.
        int status = info.getAvailabilityStatus().intValue();

        if ((status == InventoryManager.AVAILABILITY_STATUS_IN_STOCK) ||
            (status == InventoryManager.AVAILABILITY_STATUS_BACKORDERABLE)) {
          available = true;
        } 
        else {
          available = false;
        }
      } 
      else {
        available = false;
      }
    }
    
    return available;
  }
  
  /**
   * Checks whether the given commerce item is a gift item.
   * 
   * @param pOrder order the commerce item belongs to.
   * @param pItem the commerce item to check.
   * 
   * @return boolean indicating whether commerce item is a gift item.
   */
  protected boolean isGiftItem(Order pOrder, CommerceItem pItem){
    // Check whether we are dealing with gift item.
    long giftQuantity = getGwpManager().getItemGiftQuantity(pOrder, pItem);
    return giftQuantity>0l;
  }

  /**
   * Returns the valid return codes:
   * 1 - The processor completed.
   * 
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes() {
    int[] ret = { SUCCESS };

    return ret;
  }

  /**
   * This method adds an error to the PipelineResult object. This method, rather than
   * just storing a single error object in pResult, stores a Map of errors. This allows more
   * than one error to be stored using the same key in the pResult object. pKey is
   * used to reference a HashMap of errors in pResult. So, calling
   * pResult.getError(pKey) will return an object which should be cast to a Map.
   * Each entry within the map is keyed by pId and its value is pError.
   *
   * @param pResult the PipelineResult object supplied in runProcess().
   * @param pKey the key to use to store the HashMap in the PipelineResult object.
   * @param pId the key to use to store the error message within the HashMap in the PipelineResult object.
   * @param pError the error object to store in the HashMap.
   * 
   * @see atg.service.pipeline.PipelineResult
   * @see #runProcess(Object, PipelineResult)
   */
  protected void addHashedError(PipelineResult pResult, String pKey, String pId, Object pError) {
    Object error = pResult.getError(pKey);

    if (error == null) {
      HashMap map = new HashMap(5);
      pResult.addError(pKey, map);
      map.put(pId, pError);
    } 
    else if (error instanceof Map) {
      Map map = (Map) error;
      map.put(pId, pError);
    }
  }
}
