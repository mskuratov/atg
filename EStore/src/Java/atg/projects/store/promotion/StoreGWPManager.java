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


package atg.projects.store.promotion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.commerce.pricing.PricingContext;
import atg.commerce.pricing.PricingException;
import atg.commerce.promotion.GWPManager;
import atg.commerce.promotion.GWPMarkerManager;
import atg.commerce.promotion.GiftWithPurchaseSelection;
import atg.commerce.promotion.GiftWithPurchaseSelectionChoice;
import atg.commerce.promotion.PromotionConstants;
import atg.core.util.ResourceUtils;
import atg.markers.MarkerException;
import atg.projects.store.inventory.StoreInventoryManager;
import atg.repository.RepositoryItem;
import atg.service.collections.validator.CollectionObjectValidator;

/**
 * CRS extension of core commerce GiftWithPurchaseFormHandler.
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/promotion/StoreGWPManager.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreGWPManager extends GWPManager {

  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/promotion/StoreGWPManager.java#3 $$Change: 788278 $";

  //-------------------------------------
  // Properties
  //-------------------------------------
  
  /**
   * property: inventoryManager.
   */
  protected StoreInventoryManager mInventoryManager;

  /**
   * @return the inventoryManager.
   */
  public StoreInventoryManager getInventoryManager() {
    return mInventoryManager;
  }

  /**
   * @param pInventoryManager - the inventoryManager.
   */
  public void setInventoryManager(StoreInventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }
  
  /**
   * property: validators   
   */
  private CollectionObjectValidator[] mValidators;
  
  /**
   * @return array of validators that will be applied to gifts
   */
  public CollectionObjectValidator[] getValidators() {
    return mValidators;
  }

  /**
   * @param validators the validators to set
   */
  public void setValidators(CollectionObjectValidator[] pValidators) {
    this.mValidators = pValidators;
  }

  //-------------------------------------
  // Public Methods
  //-------------------------------------  
  /**
   * Returns total quantity of items to be selected for the given order.
   * 
   * @param pOrder - Order instance.
   * 
   * @return quantity of total items to be selected for the given order.
   * 
   * @throws MarkerException if error occurs during selection retrieval.
   */
  public long getQuantityToBeSelected(Order pOrder) throws CommerceException {
    
    long quantity = 0L;
    
    Collection<GiftWithPurchaseSelection> selections =  getSelections(pOrder); 
    Iterator<GiftWithPurchaseSelection> it = selections.iterator();
    GiftWithPurchaseSelection selection = null;
    
    while (it.hasNext()){
      
      selection = it.next();
      
      if (selection.getQuantityAvailableForSelection() > 0){
        quantity += selection.getQuantityAvailableForSelection();
      }
    }
    
    return quantity;
  }
  
  /**
   * Get the gift quantity for the given commerce item.
   * 
   * @param pOrder - the order to be processed.
   * @param pItem - the item that will determine the gift quantity.
   * 
   * @return gift quantity for the commerce item.
   */
  public long getItemGiftQuantity(Order pOrder, CommerceItem pItem){
    long giftQuantity = 0l;
    try {
      Collection<GiftWithPurchaseSelection> selections = getSelections(pOrder, pItem);
      for (GiftWithPurchaseSelection selection : selections){
        giftQuantity += 
          selection.getAutomaticQuantity() + selection.getTargetedQuantity() + selection.getSelectedQuantity();
      }
    }
    catch (CommerceException e) {
      if (isLoggingError())
          logError(e);
    }
    
    return giftQuantity;
  }

  /**
   * <p>
   *   Override commerce implementation to check inventory level
   *   of gift item marked as auto add.
   * </p>   * 
   *  <p>
   *    Mark item as failed if out of stock.
   *  </p>
   *  
   *  @param pOrderMarker - the order marker.
   *  @param pQuantityToAdd - the quantity to add.
   *  @param pNewItemInfos - List of AddCommerceItemInfo objects.
   *  @param pNewItems - List of CommerceItem objects.
   *  @param pPricingContext - the PricingContext to be used.
   *  @param pExtraParameters - Optional map of extra parameters.
   *  
   *  @return the newly added commerce item.
   *  
   *  @throws PricingException when there is an error auto-adding the item.
   */
  public CommerceItem processAutoAdd(RepositoryItem pOrderMarker,
                                     long pQuantityToAdd, 
                                     List<AddCommerceItemInfo> pNewItemInfos,
                                     List<CommerceItem> pNewItems, 
                                     PricingContext pPricingContext,
                                     Map pExtraParameters) throws PricingException {
    if (isLoggingDebug()) {
      logDebug("Entered processAutoAdd()");
    }
    
    CommerceItem newItem = null;
    GWPMarkerManager manager = getGwpMarkerManager();
    
    // See if this is a single SKU selection choice so we can auto add.
    GiftWithPurchaseSelectionChoice selectionChoice = null;
    
    try {
      selectionChoice =
        getAutoAddGiftSelectionChoice(manager.getGiftType(pOrderMarker), 
                                      manager.getGiftDetail(pOrderMarker), 
                                      pExtraParameters);
      // Can we auto add?
      if (selectionChoice != null) {

        boolean isValid = validateGiftItem(selectionChoice.getSkus().iterator().next()) 
            && validateGiftItem(selectionChoice.getProduct());
        
        RepositoryItem sku = selectionChoice.getSkus().iterator().next();
        String skuId = sku.getRepositoryId();
        int availabilityStatus = getInventoryManager().queryAvailabilityStatus(selectionChoice.getProduct(),skuId);
        
        // Add item if not out of stock.
        if(isValid && (availabilityStatus == getInventoryManager().getAvailabilityStatusInStockValue() ||
          availabilityStatus == getInventoryManager().getAvailabilityStatusBackorderableValue() ||
          availabilityStatus == getInventoryManager().getAvailabilityStatusPreorderableValue())) {
        
          // We have a single valid SKU id so we can auto add.
          newItem = addGiftQuantity(pOrderMarker, 
                                    selectionChoice.getProduct().getRepositoryId(), 
                                    skuId,
                                    pQuantityToAdd, 
                                    null, 
                                    pPricingContext.getShippingGroup(), 
                                    null, 
                                    null, 
                                    pNewItemInfos, 
                                    pNewItems, 
                                    -1,
                                    pPricingContext, 
                                    pExtraParameters);
          
          // Add item marker if needed and update quantities.
          RepositoryItem itemMarker = 
            manager.getOrAddItemMarker(newItem, 
                                       manager.getPromotionId(pOrderMarker), 
                                       manager.getGiftHashCode(pOrderMarker));
          
          manager.updateItemMarkerQuantities(newItem, 
                                             itemMarker, 
                                             pPricingContext.getOrder(), 
                                             pQuantityToAdd, 
                                             0, 
                                             0);
          
          RepositoryItem newMarker = manager.getItemMarker(newItem, 
                                                           manager.getPromotionId(pOrderMarker), 
                                                           manager.getGiftHashCode(pOrderMarker));
          
          if (isLoggingDebug()) {
            logDebug("newMarker=" + newMarker);  
          }
        } 
        else {
          // Gift item is out of stock, mark as failed.

          if(isLoggingDebug()) {
            logDebug("Auto add item is out of stock or have invalid dates, set quantity " + pQuantityToAdd + " as failed");
          }
          
          try {
            manager.setFailedQuantity(pOrderMarker, manager.getFailedQuantity(pOrderMarker) + pQuantityToAdd);
          }
          catch (MarkerException me) {
            String msg = ResourceUtils.getMsgResource(PromotionConstants.GWP_ERROR_AUTO_ADD,
                                                      MY_RESOURCE_NAME, sResourceBundle);
            if (isLoggingError()) {
              logError(msg, me);
              if (pOrderMarker != null) { 
                logError(pOrderMarker.toString());
              }
            }
            
            throw new PricingException(msg);
          }
        }
      }
    } 
    catch (Exception ce) {
      
      if (isLoggingDebug()) {
        logDebug("Failed to auto add quantity.", ce);
      }
      
      // Error during auto add, so store failed quantity.
      try {
        manager.setFailedQuantity(pOrderMarker, manager.getFailedQuantity(pOrderMarker) + pQuantityToAdd);
      }
      catch (MarkerException me) {
        String msg = ResourceUtils.getMsgResource(PromotionConstants.GWP_ERROR_AUTO_ADD,
                                                  MY_RESOURCE_NAME, sResourceBundle);
        if (isLoggingError()) {
          logError(msg, me);
          if (pOrderMarker != null) { 
            logError(pOrderMarker.toString());
          }
        }
        
        throw new PricingException(msg);
      }
    }    
    
    if (isLoggingDebug()){
      logDebug("Leaving processAutoAdd() with " + newItem);
    }
    
    return newItem;
  }
  
  /**
   * Validate repository item (SKU or product) using configured set
   * of validators.
   * @param item the item to validate
   * @return false - if item fails validation, true if there should 
   * be no validation or if item is valid 
   */
  public boolean validateGiftItem(RepositoryItem item) {
    
    // There is  no validators set, so no filtering is needed.
    if (getValidators() == null || getValidators().length == 0) {
      return true;
    }
    
    boolean isValid = true;
    for (CollectionObjectValidator validator: getValidators()) {
      if (!validator.validateObject(item)) {
        
        if (isLoggingDebug()) {
          logDebug("Item: " + item.getRepositoryId() + " doesn't pass validator:" + validator);
        }
        
        // Item doesn't pass validation. Set isValid to false
        // and leave the loop as there is no need to check all
        // others validators.                
        isValid = false;
        break;
        
      }
    }
    return isValid;
  }

  /**
   * Get gift selection choices filtered by products and SKUs start\end date.
   * 
   * @param pGiftType String gift type, e.g. sku
   * @param pGiftDetail String gift detail, e.g. sku1234
   * @param pReturnSkus boolean, true to return the SKUs for all choices
   * @param pExtraParameters Map of extra parameters (optional)
   * @return Array of GiftWithPurchaseSelectionChoice
   */
  @Override
  @SuppressWarnings("unchecked")
  public GiftWithPurchaseSelectionChoice[] getGiftSelectionChoices(
    String pGiftType, String pGiftDetail, boolean pReturnSkus, Map pExtraParameters)
    throws CommerceException {
    
    GiftWithPurchaseSelectionChoice[] choiceArray = super.getGiftSelectionChoices(pGiftType, pGiftDetail, pReturnSkus, pExtraParameters);
  
    if (choiceArray != null) {
      List<GiftWithPurchaseSelectionChoice> choices = Arrays.asList(choiceArray);
      List<GiftWithPurchaseSelectionChoice> filtersChoices = new ArrayList<GiftWithPurchaseSelectionChoice>();;
      Iterator<GiftWithPurchaseSelectionChoice> choicesIterator = choices.listIterator();
      while(choicesIterator.hasNext()) {
        GiftWithPurchaseSelectionChoice choice = choicesIterator.next();
        
        // If product doesn't pass validation (currently checks only dates) - remove it from array of choices 
        if (validateGiftItem(choice.getProduct())) {
          
          // If SKUs are returned for each choices - check the dates of SKUs
          if (pReturnSkus) {
            List<RepositoryItem> skus = (List<RepositoryItem>) choice.getSkus();
            Iterator<RepositoryItem> skusIter = skus.listIterator();
            
            while(skusIter.hasNext()) {
              if (validateGiftItem(skusIter.next())){
                filtersChoices.add(choice); 
                // There is at least one SKU that pass validation(currently checks only dates). The choice should be displayed.
                break;
              }
            }
          }
          else {
            filtersChoices.add(choice); 
          }
        }       
      }
      choiceArray = filtersChoices.toArray(new GiftWithPurchaseSelectionChoice[0]);
      return choiceArray;
    }    
    return null; 
  }
  
}
