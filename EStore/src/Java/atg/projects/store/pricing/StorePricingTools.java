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


package atg.projects.store.pricing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.UnitPriceBean;
import atg.commerce.promotion.GWPManager;
import atg.commerce.promotion.GiftWithPurchaseSelection;
import atg.commerce.promotion.PromotionConstants;
import atg.repository.RepositoryItem;


/**
 * Store extension for the PricingTools.
 *
 * @see atg.commerce.pricing.PricingTools
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/pricing/StorePricingTools.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StorePricingTools extends PricingTools {
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/pricing/StorePricingTools.java#3 $$Change: 788278 $";
  
  public static String SKIP_STACKING_RULES_MESSAGES_SENDING = "skipStackingRulesMessagesSending";
  
  
  //---------------------------------------------------------------------------
  // property: GwpManager

  /**
   * Manager component for gift with purchase promotions
   */
  protected GWPManager mGwpManager;

  /**
   * Setter for the gift with purchase manager property.
   * @param pGwpManager GWPManager
   */
  public void setGwpManager(GWPManager pGwpManager) {
    mGwpManager = pGwpManager;
  }

  /**
   * Getter for the gift with purchase manager property.
   * @return GWPManager
   */
  public GWPManager getGwpManager() {
    return mGwpManager;
  }
  
  /**
   * Generates a Map of unit price beans for all items in the order. the key to the map
   * is the commerce item id. Each value in the map is a list of UnitPriceBeans.
   * 
   * @param pOrder - the order to process.
   * @return map of unit price beans.
   */
  public Map<String, List<StoreUnitPriceBean>> generatePriceBeans(Order pOrder) {
    
    Map<String, List<StoreUnitPriceBean>> unitPriceBeans = new HashMap<String, List<StoreUnitPriceBean>>(pOrder.getCommerceItemCount());

    // First generate all the UnitPriceBeans for all items in the order.
    @SuppressWarnings("unchecked")
    List<CommerceItem> commerceItems = (List<CommerceItem>) pOrder.getCommerceItems();
    List<StoreUnitPriceBean> unitbeans = null;

    for (CommerceItem item : commerceItems ) {
      
      unitbeans = generatePriceBeans(pOrder, item);

      if ((unitbeans != null) && (unitbeans.size() > 0)) {
        unitPriceBeans.put(item.getId(), unitbeans);
      }
    }

    return unitPriceBeans;
  }
  
  /**
   * Generate GWP aware price beans for the given commerce item.
   * @param pOrder The order commerce item belongs to.
   * @param pItem The commerce item to generate price beans for.
   * @return The list of GWP aware price beans for the given commerce item and price infos.
   */
  @SuppressWarnings("unchecked")
  public List<StoreUnitPriceBean> generatePriceBeans(Order pOrder, CommerceItem pItem) {
    return generatePriceBeans(pOrder, pItem, pItem.getPriceInfo().getCurrentPriceDetailsSorted());
  }
 
  
  /**
   * Generate GWP aware price beans for the given commerce item and list of price infos.
   * @param pOrder The order commerce item belongs to.
   * @param pItem The commerce item to generate price beans for.
   * @param pInfos The list of detailed price infos to generate prcie beans for.
   * @return The list of GWP aware price beans for the given commerce item and price infos.
   */
  public List<StoreUnitPriceBean> generatePriceBeans(Order pOrder, CommerceItem pItem,
                                                               List<DetailedItemPriceInfo> pInfos) {
    // Get initial list of not GWP aware price beans.
    @SuppressWarnings("unchecked")
    List<UnitPriceBean> initialPriceBeans = (List<UnitPriceBean>) generatePriceBeans(pInfos);
  
    if (initialPriceBeans == null){
      return null;
    }
  
    // Get GWP selections for commerce item      
    Collection<GiftWithPurchaseSelection> selections = null;
    try {
      selections = getGwpManager().getSelections(pOrder, pItem);
    } catch (CommerceException e) {
      if (isLoggingError()){
        logError(e);
      }
      String msg = PromotionConstants.getStringResource(PromotionConstants.GWP_ERROR_GETTING_SELECTIONS);
      logError(msg, e);
    }
    
    // Wrap initial list of not GWP aware price beans into GWP aware price beans and populate
    // price beans GwpSelections list.
    List<StoreUnitPriceBean> storePriceBeans = new ArrayList<StoreUnitPriceBean>(initialPriceBeans.size());
    
    for (UnitPriceBean unitPriceBean : initialPriceBeans){
      StoreUnitPriceBean storePriceBean = new StoreUnitPriceBean(unitPriceBean);
      populatePriceBeanSelections(storePriceBean, selections);
      storePriceBeans.add(storePriceBean);
    }
  
    return storePriceBeans;
  }
  
  /**
   * Populates price bean's GwpSelections list. The method compares passed in commerce item's
   * GWP selections within price bean's pricing models. If matching selections are found
   * they are added to the price bean's GwpSelections list.
   * @param pPriceBean The price bean to populate list of GWP selections for.
   * @param pGwpSelections The list of GWP selections for the price bean's commerce item.
   */
  public void populatePriceBeanSelections(StoreUnitPriceBean pPriceBean, Collection<GiftWithPurchaseSelection> pGwpSelections){
    
    // Check whether there are any selections or pricing models to compare with,
    // If not then the price bean is not GWP so nothing to populate.
    if (pPriceBean == null || pGwpSelections == null || pGwpSelections.size() == 0 ||
        pPriceBean.getPricingModels() == null || pPriceBean.getPricingModels().size() == 0){      
      return;
    }
    
    List<GiftWithPurchaseSelection> priceBeanSelections = new ArrayList<GiftWithPurchaseSelection>();
    
    @SuppressWarnings("unchecked")
    List<RepositoryItem> pricingModels = (List<RepositoryItem>) pPriceBean.getPricingModels();
    
    // Iterate through all commerce item's selections and compare their promotion IDs
    // with IDs of pricing Models. If match is found add it to the list of price bean's selections.
    for (GiftWithPurchaseSelection selection : pGwpSelections){
      String gwpPromotionId = selection.getPromotionId();
    
      for (RepositoryItem pricingModel : pricingModels){
        if (gwpPromotionId.equals(pricingModel.getRepositoryId())){
          priceBeanSelections.add(selection);
        }
        break;
      }
    }
    
    pPriceBean.setGwpSelections(priceBeanSelections);
    
  }
 
}
