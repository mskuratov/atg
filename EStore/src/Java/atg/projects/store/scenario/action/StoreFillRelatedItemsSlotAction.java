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



package atg.projects.store.scenario.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import atg.commerce.scenario.FillRelatedItemsSlotAction;
import atg.multisite.SiteGroupManager;
import atg.process.ProcessException;
import atg.repository.RepositoryItem;

/**
 * EStore implementation of a fillRelatedItemsSlotAction scenario action.
 * This implementation filters related items based on order specified and current site (only products from sharing sites are added to slot). 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/scenario/action/StoreFillRelatedItemsSlotAction.java#2 $$Change: 768606 $ 
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class StoreFillRelatedItemsSlotAction extends FillRelatedItemsSlotAction {
  /**
   * Class version
   */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/scenario/action/StoreFillRelatedItemsSlotAction.java#2 $$Change: 768606 $";
  
  private String mShareableTypeId;
  private String mSitesPropertyName;
  private SiteGroupManager mSiteGroupManager;

  /**
   * Configures the action using the given configuration object. 
   * The configuration object is typically a global Nucleus component which is 
   * configured with the information necessary for the action's operation.
   * 
   * @param pConfiguration the configuration to use
   * @throws ProcessException - if the action could not be configured - for example,
   * because some of the required properties are missing from the configuration
   */
  @Override
  public void configure(Object pConfiguration) throws ProcessException {
    super.configure(pConfiguration);
    // EStore specific configuration
    StoreFillRelatedItemsSlotActionConfiguration configuration = (StoreFillRelatedItemsSlotActionConfiguration) pConfiguration;
    mShareableTypeId = configuration.getShareableTypeId();
    mSitesPropertyName = configuration.getSitesPropertyName();
    mSiteGroupManager = configuration.getSiteGroupManager();
  }

  /**
   * Checks to see that relatedItems don't exist in order, 
   * if any of the items exists then it omits them from the result set.  
   * 
   * @param pOrderProducts the products in the order.
   * @param pRelatedItems related items for the products in the order.
   * @param pRelatedItemSet related items which don't exist in order.
   */
  @SuppressWarnings("unchecked")
  @Override
  protected void checkRelatedItems(List pOrderProducts, List pRelatedItems, Set pRelatedItemSet) {
    for (RepositoryItem relatedProduct: (List<RepositoryItem>) pRelatedItems) {
      // Add into resulting collection only products that satisfy some specific rules
      if (checkRelatedItem(pOrderProducts, relatedProduct)) {
        pRelatedItemSet.add(relatedProduct);
      }
    }
  }
  
  /**
   * This method checks the product specified to see if it should be added into related items slot.
   * Only products from shared sites will be added to this slot.
   * @param pOrderProducts - products in the current order.
   * @param pRelatedProduct - product to be tested.
   * @return <code>true</code> if the product specified should be added into slot and <code>false</code> otherwise.
   */
  @SuppressWarnings("unchecked")
  protected boolean checkRelatedItem(List<RepositoryItem> pOrderProducts, RepositoryItem pRelatedProduct) {
    // Do not display products that already in the shopping cart
    if (pOrderProducts.contains(pRelatedProduct)) {
      return false;
    }
    Collection<String> sharingSiteIds = mSiteGroupManager.getSharingSiteIds(mShareableTypeId);
    // No sharing sites, i.e. no multisite, so all products are good
    if (sharingSiteIds == null) {
      return true;
    }
    // Clone sites collection to leave the product intact
    Collection<String> productSites = new HashSet<String>((Collection<String>) pRelatedProduct.getPropertyValue(mSitesPropertyName));
    // If there is something to remove then these collections intercept, i.e. we should display the product specified
    // If there is nothing to remove then these collections do not intercept, i.e. the product is from unshared site, do not display it
    return productSites.removeAll(sharingSiteIds);
  }
}
