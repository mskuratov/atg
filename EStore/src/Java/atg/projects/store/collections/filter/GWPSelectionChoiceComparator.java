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

package atg.projects.store.collections.filter;

import java.util.Comparator;

import atg.commerce.promotion.GiftWithPurchaseSelectionChoice;
import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;


/**
 * This is the comparator implementation for GWP selection choices. Sorts choices alphabetically by
 * the product name, for one-sku GWP choices that have the same product specified it sorts alphabetically 
 * by sku's color.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/collections/filter/GWPSelectionChoiceComparator.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 * 
 */
public class GWPSelectionChoiceComparator implements Comparator<GiftWithPurchaseSelectionChoice>{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/collections/filter/GWPSelectionChoiceComparator.java#2 $$Change: 768606 $";
  
  
  public static final String DISPLAY_NAME_PROPERTY_NAME = "displayName";
  public static final String COLOR_PROPERTY_NAME = "color";
  
  /**
   * Compares two GWP selection choices by their product display name alphabetically.
   * If both choices are for the same product and we are dealing with one-sku choice
   * case the choices are compared by SKU's color if it's specified.
   */
  @Override
  public int compare(GiftWithPurchaseSelectionChoice pGWPChoice1, GiftWithPurchaseSelectionChoice pGWPChoice2) {
    if (pGWPChoice1 == null || pGWPChoice2 == null){
      return 0;
    }
    
    RepositoryItem product1 = pGWPChoice1.getProduct();
    RepositoryItem product2 = pGWPChoice2.getProduct();
    
    if (!product1.getRepositoryId().equals(product2.getRepositoryId())){
      return ((String)product1.getPropertyValue(DISPLAY_NAME_PROPERTY_NAME)).compareTo(
                                               (String)product2.getPropertyValue(DISPLAY_NAME_PROPERTY_NAME));
    }else{
      
      // Check whether we are dealing with one-sku choices
      if (pGWPChoice1.getSkus().size() == 1 && pGWPChoice2.getSkus().size() == 1){
        
        // The one-sku per choice case, sort choices alphabetically by SKU color.
        RepositoryItem sku1 = pGWPChoice1.getSkus().iterator().next();
        RepositoryItem sku2 = pGWPChoice2.getSkus().iterator().next();
        
        // Get SKUs colors
        String color1 = (String)sku1.getPropertyValue(COLOR_PROPERTY_NAME);
        String color2 = (String)sku2.getPropertyValue(COLOR_PROPERTY_NAME);
        
        // If color property is specified for the SKUs compare them alphabetically
        if (!StringUtils.isEmpty(color1) && !StringUtils.isEmpty(color2)){
          return color1.compareTo(color2);
        }
      }
      
      // It's not the one-sku per choice case or the SKU has no color specified,
      // so compare just by product's display name that are in this case the same.
      return 0;
    }
  }

}
