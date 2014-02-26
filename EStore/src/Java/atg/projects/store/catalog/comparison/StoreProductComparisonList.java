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



package atg.projects.store.catalog.comparison;

import atg.commerce.catalog.CatalogTools;
import atg.commerce.catalog.comparison.ProductComparisonList;
import atg.commerce.catalog.custom.CustomCatalogTools;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * 
 * Extends atg.commerce.catalog.comparison.ProductComparisonList and gets the 
 * default category for the current site.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/catalog/comparison/StoreProductComparisonList.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreProductComparisonList extends ProductComparisonList {
  
  //-----------------------------------
  // STATIC
  //-----------------------------------
  
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/catalog/comparison/StoreProductComparisonList.java#3 $$Change: 788278 $";
  
  //-----------------------------------
  // MEMBERS
  //-----------------------------------
  
  //-----------------------------------
  // mSiteManager
  private SiteManager mSiteManager;
  
  /**
   * Returns the site manager
   * @return mSiteManager
   */
  public SiteManager getSiteManager(){
    return mSiteManager;
  }
  
  /**
   * Sets the SiteManager
   * @param pSiteManager The SiteManager
   */
  public void setSiteManager(SiteManager pSiteManager){
    mSiteManager = pSiteManager;
  }
  
  //-----------------------------------
  // PUBLIC METHODS
  //-----------------------------------
  
  /**
   * @see ProductComparisonList.add(String pProductId, String pCategoryId,
   *  String pSkuId, String pCatalogKey, String pSiteId)
   *  
   *  Adds a product to the comparison list, if the pCategoryId is null try
   *  to determine the default categoryId for product pProductId on site
   *  pSiteId.
   *  
   * @param pProductId
   *    The repository id of the product being added.
   * @param pCategoryId
   *    The repository id of the parent category to use for this Entry.
   *    If null, the product's default parent category will be used instead.
   *    If the product has no default parent category, the category in the
   *    Entry will be null.
   * @param pSkuId
   *    The repository id of the sku to use for this Entry.  If null, the
   *    first sku in the product's sku list will be used instead.  If the product
   *    has no skus, the sku in the Entry will be null.
   * @param pCatalogKey
   *    An optional key that is passed to the CatalogTools component, which
   *    uses it to locate an alternate product catalog from which to add
   *    the product.  If null, the default catalog is used.
   * @param pSiteId
   *    The site id.  
   * @return True if the item was added to the list, false if it was not, 
   *    either because no such product with the given id was found, or because 
   *    the category/product/sku triplet already existed in the list.
   * @throws RepositoryException 
   *   If there is any repository error while looking up
   *   categories, products, or skus.
   */
  public boolean add(String pProductId, String pCategoryId, String pSkuId, String pCatalogKey, String pSiteId)
    throws RepositoryException
  {
    String categoryId = pCategoryId;
    if(categoryId == null){
      categoryId = defaultCategory(pProductId, pSiteId);
    }
    
    return super.add(pProductId, categoryId, pSkuId, pCatalogKey, pSiteId);
  }
  
  /**
   * @see ProductComparisonList.remove(String pProductId, String pCategoryId,
   * String pSkuId, String pCatalogKey, String pSiteId) 
   * 
   * Remove the Entry containing the given product id, category id, and
   * sku id. A null category id matches the product's default parent category,
   * if any.  A null sku id matches the product's default sku, if any.
   * <p>
   * The behavior of <code>remove</code> therefore parallels the behavior of
   * <code>add</code>, so that calling <code>add(productId, categoryId, skuId)</code>
   * and then calling <code>remove(productId, categoryId, skuId)></code> with the
   * same values will remove the item just added.
   * <p>
   * The <code>pCatalogKey</code> parameter is used to select a
   * product catalog in cases where it is necessary to locate a
   * product's default parent category or default sku.  A null
   * value means use the default product catalog.
   * The <code>pSiteId</code> parameter is used to pass site id
   **/
  
  public void remove(String pProductId, String pCategoryId, String pSkuId, String pCatalogKey, String pSiteId)
    throws RepositoryException {
   
    // If siteId is empty set it to null
    if (StringUtils.isEmpty(pSiteId)) {
      pSiteId = null;
    }
    
    super.remove(pProductId, pCategoryId, pSkuId, pCatalogKey, pSiteId);
  }
  
  
  /**
   * @see ProductComparisonList.contains(String pProductId, String pCategoryId,
   *  String pSkuId, String pCatalogKey, String pSiteId, boolean pMatchSku) 
   *  
   *  Determines if a product exists in the comparison list, if the pCategoryId
   *  is null try to determine the default categoryId for product pProductId
   *  on site pSiteId.
   *  
   * @param pProductId
   *    The repository id of the product being checked.
   * @param pCategoryId
   *    The repository id of the parent category to use for this Entry.
   *    If null, the product's default parent category will be used instead.
   *    If the product has no default parent category, the category in the
   *    Entry will be null.
   * @param pSkuId
   *    The repository id of the sku to use for this Entry.  If null, the
   *    first sku in the product's sku list will be used instead.  If the product
   *    has no skus, the sku in the Entry will be null.
   * @param pCatalogKey
   *    An optional key that is passed to the CatalogTools component. Used to select a
   *    product catalog in cases where it is necessary to locate a
   *    product's default parent category or default sku.  A null
   *    value means use the default product catalog.
   * @param pSiteId
   *    The site id.  
   * @param pMatchSku   
   *    Indicates if pSkuId parameter is used. If pMatchSku is false, then no check on sku. 
   * @return 
   *   True if a product exists in the comparison list, false if it is not.
   * @throws RepositoryException 
   *   If there is any repository error while looking up
   *   categories, products, or skus. 
   */
  public boolean contains(String pProductId, String pCategoryId, String pSkuId, String pCatalogKey, String pSiteId, boolean pMatchSku) 
    throws RepositoryException  
  {
    String categoryId = pCategoryId;
    if(categoryId == null){
      categoryId = defaultCategory(pProductId, pSiteId);
    }
    
    return super.contains(pProductId, categoryId, pSkuId, pCatalogKey, pSiteId, pMatchSku);
  }
  
  //-----------------------------------
  // PROTECTED METHODS
  //-----------------------------------
  
  /**
   * Returns the default categorys for pProductId on pSiteId
   * @param pProductId A productId
   * @param pSiteId A siteId
   * @return The default categoryId for product pProductId on site pSiteId
   */
  protected String defaultCategory(String pProductId, String pSiteId) {
    String siteId = pSiteId;
    
    if(pProductId == null){
      return null;
    }
    
    // If siteId is null use the site we are currently on
    if(siteId == null){
      siteId = !(siteId == null) ? siteId : SiteContextManager.getCurrentSiteId();
    }
    
    CatalogTools catalogTools = getCatalogTools();
    
    try{
      if(catalogTools instanceof CustomCatalogTools)
      {        
        // site repository item
        RepositoryItem site = getSiteManager().getSite(siteId);
        if(site == null){
          if(isLoggingDebug()){
            logDebug("Cannot get default category for product " + pProductId + 
              " on site " + siteId + " the site may not exist");
          }
          return null;
        }
        
        // get the catalog repository item from the site id
        RepositoryItem catalog = ((CustomCatalogTools) catalogTools).getCatalogForSite(site);
        if(catalog == null){
          if(isLoggingDebug()){
            logDebug("Cannot get default category for product " + pProductId + 
              " on site " + siteId + " the catalog may not exist");
          }
          return null;
        }
        
        // product repository item
        RepositoryItem product = ((CustomCatalogTools) catalogTools).findProduct(pProductId);
        if(product == null){
          if(isLoggingDebug()){
            logDebug("Cannot get default category for product " + pProductId + 
              " on site " + siteId + " the product may not exist");
          }
          return null;
        }
                
        //get the category from the product and catalog
        RepositoryItem category = ((CustomCatalogTools)catalogTools).getParentCategory(product, catalog);
        if(category == null){
          if(isLoggingDebug()){
            logDebug("Cannot get default category for product " + pProductId + 
              " on site " + siteId + " the category may not exist");
          }
          return null;
        }
        
        // if we have a category repository item gets its id
        if(category != null){
          return category.getRepositoryId(); 
        }
      }
    }
    catch(RepositoryException e){
      if(isLoggingError()){
        logError("Repository Exception occur:", e);
      }
    }
    
    return null;
  }

}
