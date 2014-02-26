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


package atg.projects.store.returns;

import java.util.Collection;
import java.util.Iterator;

import atg.commerce.csr.returns.ReturnItem;
import atg.commerce.csr.returns.ReturnManager;
import atg.commerce.csr.returns.ReturnRequest;
import atg.commerce.csr.returns.ItemCostAdjustment;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.pricing.TaxPriceInfo;
import atg.core.util.Range;
import atg.projects.store.pricing.StoreShippingPriceInfo;

/**
 * This class extends CSC's ReturnManager to include shipping tax in total tax refund
 * for the return item.
 *  
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/DCS-CSR/src/atg/projects/store/returns/StoreReturnManager.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */

public class StoreReturnManager extends ReturnManager{
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/DCS-CSR/src/atg/projects/store/returns/StoreReturnManager.java#2 $$Change: 768606 $";

 
  /**
   * Overrides base method to add shipping tax refund to the total tax refund
   * for the given return item.
   */
  protected double calculateTaxRefundForItem(ReturnRequest pReturnRequest,
      ReturnItem pReturnItem, Range pReturnRange) {
    
    double taxRefund =  super.calculateTaxRefundForItem(pReturnRequest, pReturnItem,
        pReturnRange);
        
    taxRefund += getShippingTaxRefundForItem(pReturnRequest, pReturnItem);
    
    return taxRefund;
  }


  /**
   * Calculates shipping tax refund for the given return item.
   * 
   * @param pReturnRequest return request
   * @param pReturnItem return item
   * @return shipping tax refund amount for the return item
   */
  protected double getShippingTaxRefundForItem(ReturnRequest pReturnRequest, ReturnItem pReturnItem) {
    double totalShippingTaxRefund = 0.0;
    
    // Iterate through item cost adjustments
    Collection itemCostAdjustments = pReturnItem.getItemCostAdjustments();
    Iterator adjusterator = itemCostAdjustments.iterator();    
    while(adjusterator.hasNext()){
      ItemCostAdjustment ica = (ItemCostAdjustment)adjusterator.next();
      
      // obtain shipping group that corresponds the current item cost adjustment
      String sgId = ica.getShippingGroupId();
      ShippingGroup sg;
      try {
        sg = pReturnRequest.getOrder().getShippingGroup(sgId);
      
      // retrieve taxPriceInfo from the shipping group's price info
      TaxPriceInfo taxPriceInfo = ((StoreShippingPriceInfo)sg.getPriceInfo()).getTaxPriceInfo();
      if (taxPriceInfo == null){
        // there no shipping TaxPriceInfo, nothing to return
        continue;
      }
      
      // shipping tax is specified so calculate the amount to return
      double shippingShareAdjustment = ica.getShippingShareAdjustment();
      double totalShippingCostForSG = sg.getPriceInfo().getAmount();
      double totalShippingTax = taxPriceInfo.getAmount();
      double ratio = shippingShareAdjustment / totalShippingCostForSG;
      
      if (Double.isNaN(ratio)){
        ratio = 0.0;
      }
      double shippingTaxRefund = totalShippingTax * ratio;
      shippingTaxRefund = getPricingTools().round(shippingTaxRefund);
      
      // add the shipping tax refund for the current item cost adjustment to the total item's 
      // shipping tax refund
      totalShippingTaxRefund += shippingTaxRefund;
      } catch (ShippingGroupNotFoundException ex) {
        if(isLoggingError()){
          logError(ex);
        }
     } catch (InvalidParameterException ex) {
       if(isLoggingError()){
         logError(ex);
       }
     }
    }
    
    if(Double.isNaN(totalShippingTaxRefund) || Double.isInfinite(totalShippingTaxRefund)){
      totalShippingTaxRefund = 0.0;
    }

    //flip the sign to deal with the reverse way in which ReturnItems store refund value, + = credit, - = debit)
    if(totalShippingTaxRefund != 0){
      totalShippingTaxRefund = -totalShippingTaxRefund;
    }
    
    return totalShippingTaxRefund;
  }

}
