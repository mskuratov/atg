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


package atg.projects.store.catalog;

import java.util.LinkedList;

import atg.commerce.catalog.CatalogNavHistory;

/**
 * A sub class of CatalogNavHistory that allows the current top level category to be
 * obtained by a method call, instead of querying the navHistory list in the JSP.
 * 
 * @author ATG
 */
public class StoreCatalogNavHistory extends CatalogNavHistory{
  //-----------------------------------
  // STATIC
  //-----------------------------------
  /**
   * Class Version
   */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/catalog/StoreCatalogNavHistory.java#2 $$Change: 768606 $";
  
  //-----------------------------------
  // PUBLIC
  //-----------------------------------
  /**
   * The position of the top level category in the navigation history
   */
  public static final int TOP_LEVEL_CATEGORY_NAV_POSITON = 1;
  
  //-----------------------------------
  // METHODS
  //-----------------------------------
  /**
   * Returns the top level category that the user is currently viewing or viewing one
   * of its children.
   * 
   * @return The top level category repository item or null if it dosnt exist
   */
  public Object getTopLevelCategory(){
    LinkedList navHistory = getNavHistory();
    // The Catalog is a hierarchy, so position 1 in the hierarchy will
    // always be the current top level category if there is one. The first
    // entry is always the catalog root node.
    if(navHistory != null && navHistory.size() > 1){     
      return navHistory.get(TOP_LEVEL_CATEGORY_NAV_POSITON);     
    }
    
    return null;
  }

}
