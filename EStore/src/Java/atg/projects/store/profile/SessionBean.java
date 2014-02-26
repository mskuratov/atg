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



package atg.projects.store.profile;

import java.util.HashMap;
import java.util.Map;

import atg.nucleus.GenericService;

/**
 * A session scoped bean containing only a map property.
 *
 * @author ATG
 * @version $revision 1.1$
 */
public class SessionBean extends GenericService {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/profile/SessionBean.java#3 $$Change: 788278 $";

  public static final String SKU_ID_TO_GIFTLIST_PROPERTY_NAME = "skuIdToAdd";
  
  public static final String COMMERCE_ITEM_ID_PROPERTY_NAME = "commerceItemId";
  
  public static final String PRODUCT_ID_TO_GIFTLIST_PROPERTY_NAME = "productIdToAdd";
  
  public static final String QUANTITY_TO_ADD_TO_GIFTLIST_PROPERTY_NAME = "quantityToAdd";
  
  public static final String GIFTLIST_ID_PROPERTY_NAME = "giftListIdToAdd";
  
  public static final String SITE_ID_PROPERTY_NAME = "siteId";
  
  public static final String REDIRECT_AFTER_LOGIN_URL_PROPERTY_NAME = "redirectAfterSuccessLoginURL";
  
  public static final String LAST_RETURN_REQUEST = "lastReturnRequest";
  
  /**
   * Values map. 
   */
  private Map mValues;
    
  /**
   * A map containing miscellaneous session values.
   * @return the values.
   */
  public Map getValues() {
    if (mValues == null) {
      mValues = new HashMap();
    }
    return mValues;
  }

  private String mSkuIdToAdd;

  /**
   * <i>skuIdToAdd</i> property contains SKU id to be added into shopping cart.
   * This property added in order to set two form handlers' property from a single HTML element.
   * 
   * @return the skuIdToAdd
   */
  public String getSkuIdToAdd()
  {
    return mSkuIdToAdd;
  }
  
  /**
   * <i>skuIdToAdd</i> property in form of String array.
   * @return string array with mSkuIdToAdd  - if mSkuIdToAdd not null; null otherwise
   */
  public String[] getSkuIdToAddArray()
  {
    return mSkuIdToAdd == null ? null : new String[] {mSkuIdToAdd};
  }

  /**
   * @param pSkuIdToAdd the skuIdToAdd to set
   */
  public void setSkuIdToAdd(String pSkuIdToAdd)
  {
    mSkuIdToAdd = pSkuIdToAdd;
  }
  
  private String[] mSearchSiteIds;

  /**
   * This property contains site IDs using by the user for search. This property should be updated each time the user runs new ATG search.
   * @return site IDs of current search process.
   */
  public String[] getSearchSiteIds()
  {
    if (mSearchSiteIds == null)
    {
      mSearchSiteIds = new String[0];
    }
    return mSearchSiteIds;
  }

  /**
   *  @param pSearchSiteIds the searchSiteIds to set
   */
  public void setSearchSiteIds(String[] pSearchSiteIds)
  {
    mSearchSiteIds = pSearchSiteIds;
  }
}
