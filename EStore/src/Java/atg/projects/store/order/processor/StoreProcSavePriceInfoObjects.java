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
import atg.beans.PropertyNotFoundException;

import atg.commerce.CommerceException;

import atg.commerce.order.CommerceIdentifier;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.processor.ProcSavePriceInfoObjects;

import atg.commerce.pricing.AmountInfo;
import atg.commerce.pricing.TaxPriceInfo;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;

import java.beans.IntrospectionException;


/**
 * Extended ATG base class in order to save TaxPriceInfo of commerce items and shipping groups.
 * Commerce item's TaxPriceInfo objects are saved to the CommerceItem's ItemPriceInfo. Shipping 
 * group's TaxPriceInfo objects are saved to the ShippingGroup's ShippingPriceInfo.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/processor/StoreProcSavePriceInfoObjects.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreProcSavePriceInfoObjects extends ProcSavePriceInfoObjects {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/processor/StoreProcSavePriceInfoObjects.java#3 $$Change: 788278 $";  
  
  /**
   * Overrides the base savePriceInfo method to save taxPriceInfo into parent price info repository item. 
   * 
   * @param pOrder The order being saved.
   * @param pCi The commerce object whose price info is being saved.
   * @param pRepItem The repository item corresponding to the commerce object.
   * @param pRepItemPropName The property of piRepItem that stores the price info.
   * @param pPriceInfo The price info object that will be saved to piRepItem.repItemPropName.
   * @param pMutRep The repository where the order is being saved.
   * @param pOrderManager The OrderManager from the pipeline params.
   * 
   * @return The repository item.
   * 
   * @throws RepositoryException indicates that a severe error occurred while performing a Repository task.
   * @throws IntrospectionException an exception happens during Introspection.
   * @throws PropertyNotFoundException if property mapper does not support the requested property.
   * @throws CommerceException indicates that a severe error occurred while performing a commerce operation.
   */
  protected MutableRepositoryItem savePriceInfo(Order pOrder, 
                                                CommerceIdentifier pCi, 
                                                MutableRepositoryItem pRepItem, 
                                                String pRepItemPropName, 
                                                AmountInfo pPriceInfo, 
                                                MutableRepository pMutRep,
                                                OrderManager pOrderManager) 
    throws RepositoryException, IntrospectionException, PropertyNotFoundException, CommerceException {

    MutableRepositoryItem piRepItem = 
      super.savePriceInfo(pOrder, pCi, pRepItem, pRepItemPropName, pPriceInfo, pMutRep, pOrderManager);
    
    if (piRepItem != null && piRepItem.getItemDescriptor().hasProperty(getTaxPriceInfoProperty())) {
      saveTaxPriceInfo(pOrder, pCi, pPriceInfo, piRepItem, pMutRep, pOrderManager);
    }
    
    return piRepItem;
  }
  
  /**
   * This method is used to save the price info's tax price info. Can be used to save commerce item's or
   * shipping group's priceInfo's taxPriceInfo.
   *
   * @param pOrder - order.
   * @param pCi - commerce identifier.
   * @param pPriceInfo - price info object from where the tax price info should be saved.
   * @param pPriceInfoRepItem - price info repository item to where tax price info should be saved.
   * @param pRepository - repository.
   * @param pOrderManager - order manager.
   * 
   * @throws RepositoryException if repository error occurs.
   * @throws IntrospectionException if introspection error occurs.
   * @throws PropertyNotFoundException if property was not found.
   * @throws CommerceException if commerce error occurs.
   */
  protected void saveTaxPriceInfo(Order pOrder, CommerceIdentifier pCi, AmountInfo pPriceInfo, 
                                  MutableRepositoryItem pPriceInfoRepItem, MutableRepository pRepository, 
                                  OrderManager pOrderManager)
    throws RepositoryException, IntrospectionException, PropertyNotFoundException, CommerceException {
    
    if (pPriceInfoRepItem != null) {
      
      MutableRepositoryItem tpiRepItem = 
        (MutableRepositoryItem) pPriceInfoRepItem.getPropertyValue(getTaxPriceInfoProperty());
      
      TaxPriceInfo taxPriceInfo = 
        (TaxPriceInfo)DynamicBeans.getPropertyValue(pPriceInfo, getTaxPriceInfoProperty());
  
      tpiRepItem = savePriceInfo(pOrder, pCi, tpiRepItem, null, taxPriceInfo, pRepository, pOrderManager);
  
      pPriceInfoRepItem.setPropertyValue(getTaxPriceInfoProperty(), tpiRepItem);
    }
  }
}
