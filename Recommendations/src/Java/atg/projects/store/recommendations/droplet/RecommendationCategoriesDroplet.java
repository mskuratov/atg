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


package atg.projects.store.recommendations.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.catalog.custom.CatalogProperties;
import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.projects.store.catalog.StoreCatalogTools;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * <p>
 * This droplet will determine recommendation categories for the specified product or
 * recommendation categories for the specified category.
 * The categories will be returned as full category paths using their display names,
 * e.g. "Home Accents>Decor". Recommendation categories will be taken from product's
 * parent category's related categories or from related categories of category parameter.
 * </p>
 * 
 * <p>
 * To pass product to the droplet either 'product' or 'productId' parameters can be used
 * for the product repository item or product ID correspondingly.
 * </p>
 * 
 * <p>
 * To pass category to the droplet 'category' or 'categoryId' parameters can be used for 
 * the category repository item or category ID correspondingly.
 * </p>
 * 
 * <p>
 * This droplet takes the following parameters:
 * <dl>
 * <dt>product</dt>
 * <dd>The parameter that contains product repository item for which recommendation categories
 * should be determined.</dd>
 * <dt>productId</dt>
 * <dd>Product ID for which recommendation categories will be determined.</dd>
 * <dt>category</dt>
 * <dd>Category for which recommendation categories will be determined.</dd>
 * <dt>categoryId</dt>
 * <dd>Category ID for which recommendation categories will be determined.</dd>
 * </dl>
 * </p>
 * 
 * <p>
 * Output parameters:
 * <dl>
 * <dt>recommendationCategories</dt><dd>Contains the list of recommendations categories for the product.
 * Each category is represented as full category path using the categories display names.</dd>
 * <dt>output</dt><dd>Rendered when product's parent category has related categories.
 * </dd>
 * <dt>empty</dt><dd>Rendered when product's parent category has no related categories.</dd>
 * </dl>
 * </p>
 * 
 * Here the example of usage:
 * 
 *  &lt;dsp:droplet name="/atg/store/recommendations/droplet/RecommendationCategoriesForProduct"&gt;
 *    &lt;dsp:param name="product" param="product"/&gt;
 *    &lt;dsp:oparam name="output"&gt;
 *      &lt;dsp:getvalueof var="recommendationCategories" param="recommendationCategories"/&gt;
 *      &lt;c:if test="${not empty recommendationCategories}"&gt;
 *        &lt;dt&gt;recommendationCategories&lt;/dt&gt;
 *        &lt;dd&gt;
 *          &lt;dl&gt;
 *            &lt;c:forEach var="category" items="${recommendationCategories}"&gt;
 *              &lt;dt&gt;${category}&lt;/dt&gt;
 *             &lt;/c:forEach&gt;
 *          &lt;/dl&gt;
 *        &lt;/dd&gt;   
 *      &lt;/c:if&gt;
 *    &lt;/dsp:oparam&gt;
 *  &lt;/dsp:droplet&gt;
 * 
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Recommendations/src/atg/projects/store/recommendations/droplet/RecommendationCategoriesDroplet.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class RecommendationCategoriesDroplet extends DynamoServlet{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Recommendations/src/atg/projects/store/recommendations/droplet/RecommendationCategoriesDroplet.java#3 $$Change: 788278 $";
  
  /** Category path separator */
  public static final String CATEGORY_SEPARATOR = ">";
  
  
  /**
   * Product ID parameter name.
   */
  private static final ParameterName PRODUCT_ID  = ParameterName.getParameterName( "productId" );
  
  /**
   * Product repository item parameter name
   */
  private static final ParameterName PRODUCT  = ParameterName.getParameterName( "product" );
  
  /**
   * Category repository item parameter name
   */
  private static final ParameterName CATEGORY  = ParameterName.getParameterName( "category" );
  
  /**
  * Category ID parameter name.
  */
 private static final ParameterName CATEGORY_ID  = ParameterName.getParameterName( "categoryId" );
  
  /**
   * Recommendation categories parameter name.
   */
  private static final String RECOMMENDATION_CATEGORIES  = "recommendationCategories" ;
  
  /**
   * Oparam: output
   */
  private static final ParameterName OUTPUT  = ParameterName.getParameterName( "output" );
  
  /**
   * Empty parameter name.
   */
  public final static ParameterName EMPTY = ParameterName.getParameterName("empty");
  
  /**
   * CatalogTools component
   */
  private StoreCatalogTools mCatalogTools;
  
  /**
   * Gets the catalog tools object
   * 
   * @return mCatalogTools the catalog tools object
   */
  public StoreCatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  /**
   * Sets the catalog tools object
   * 
   * @param pCatalogTools the catalog tools object
   */
  public void setCatalogTools(StoreCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }
  
  /**
   * The boolean indicating whether to include root category into category
   * navigation path
   */
  private boolean mIncludeRootCategory;

  /**
   * Returns the boolean indicating whether to include root category
   * into the category path.
   * 
   * @return the boolean indicating whether root category should be included
   *        into the category path
   */
  public boolean isIncludeRootCategory() {
    return mIncludeRootCategory;
  }

  /**
   * Sets the boolean indicating whether to include root category
   * into the category path.
   * 
   * @param pIncludeRootCategory the boolean indicating whether root category 
   * should be included into the category path
   */
  public void setIncludeRootCategory(boolean pIncludeRootCategory) {
    mIncludeRootCategory = pIncludeRootCategory;
  }
  
  /**
   * Category's display name property name
   */
  private String mCategoryDisplayNameProperty;  
  
  /**
   * Returns the category's display name property name.
   * 
   * @return the category's display name property name
   */
  public String getCategoryDisplayNameProperty() {
    return mCategoryDisplayNameProperty;
  }

  /**
   * Sets the category's display name property name.
   * 
   * @param pCategoryDisplayNameProperty the category's display name property name
   */
  public void setCategoryDisplayNameProperty(String pCategoryDisplayNameProperty) {
    mCategoryDisplayNameProperty = pCategoryDisplayNameProperty;
  }
  
  /**
   * The boolean indicating whether to include product category into recommendation
   * categories
   */
  private boolean  includeProductCategory = true;
  
  /**
   * Gets the boolean indicating whether to include product category into recommendation
   * categories
   * 
   * @return the includeProductCategory
   */
  public boolean isIncludeProductCategory() {
    return includeProductCategory;
  }

  /**
   * Sets the boolean indicating whether to include product category into recommendation
   * categories
   * 
   * @param pIncludeProductCategory the includeProductCategory to set
   */
  public void setIncludeProductCategory(boolean pIncludeProductCategory) {
    includeProductCategory = pIncludeProductCategory;
  }
  
  
  /**
   * Determines product's recommendations categories list and puts them into
   * <code>recommendationCategories</code> output parameter. Each category is represented
   * as full category path using categories display names.
   */
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {
  
    String categoryId = pRequest.getParameter(CATEGORY_ID);
    RepositoryItem category = (RepositoryItem) pRequest.getObjectParameter(CATEGORY);    
    String productId = pRequest.getParameter(PRODUCT_ID);
    RepositoryItem product = (RepositoryItem) pRequest.getObjectParameter(PRODUCT);
    
    if (category == null && StringUtils.isEmpty(productId) && StringUtils.isEmpty(categoryId) && product == null){
      if (isLoggingError()){
        logError("No catgeory, category ID, product or product ID is specifiedy for RecommendationCategoriesDroplet.");
      }
      return;
    }
      
    if (category == null){  
      // If categoryId is not set, check product and productId
      if (StringUtils.isEmpty(categoryId)){
        if (product == null){
          // Get product repository item using product ID
          try {
            product = getCatalogTools().findProduct(productId);
          } catch (RepositoryException e) {
            if (isLoggingError())
                logError("Error occurred while trying to retrieve product from repository.", e);
            return;
          }
        }
        category = getProductParentCategory(product);
      }
      // If categoryId is set, look for corresponding category
      else {
        try {
          category = getCatalogTools().findCategory(categoryId);
        } catch (RepositoryException e) {
          if (isLoggingError())
            logError("Error occurred while trying to retrieve category from repository.", e);
          return;
        }
      }
    }
        
    List<String> recommendationCategories =  getRecommendationCategories(category);
    
    if (recommendationCategories!= null && recommendationCategories.size()>0){
      pRequest.setParameter(RECOMMENDATION_CATEGORIES, recommendationCategories);
      pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }else{
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    }
  
  }
  
  /**
   * Gets parent category for given product.
   * 
   * @param pProductItem the product repository item
   * @return parent category for given product.
   */
  public RepositoryItem getProductParentCategory(RepositoryItem pProductItem){
    CatalogProperties catalogProperties = getCatalogTools().getCatalogProperties();
    
    RepositoryItem parentCategory = (RepositoryItem)pProductItem.getPropertyValue(catalogProperties.getParentCategoryPropertyName());
         
    return parentCategory;
  }
  
  /**
   * Returns list of recommendation categories for the given category.
   * Recommendation categories are taken from category's related
   * categories. Each category is returned as full category path using 
   * categories display names.
   * 
   * @param pCategoryItem the category repository item
   * @return list of recommendation categories paths.
   */
  public List<String>  getRecommendationCategories(RepositoryItem pCategoryItem){
    CatalogProperties catalogProperties = getCatalogTools().getCatalogProperties();
    List<String> result = new ArrayList<String>();
      
    if (pCategoryItem != null){
      /*
       *  Check if product category should be included in
       *  related categories.
       */      
      if (isIncludeProductCategory()) {
        result.add(getDefaultCategoryPath(pCategoryItem));
      }
      
      List relatedCategories = (List) pCategoryItem.getPropertyValue(catalogProperties.getRelatedCategoriesPropertyName());
      
      for (Object category : relatedCategories){
        // Get full category path for the category
        result.add(getDefaultCategoryPath((RepositoryItem)category));
      }
    }
    
    return result;
  }
    
  /**
   * Returns category default path using categories display names, e.g.,
   * "Home Accents>Decor".
   * 
   * @param pCategoryItem category repository item.
   * @return category default path.
   */
  public String getDefaultCategoryPath(RepositoryItem pCategoryItem){
    if (pCategoryItem == null){
      return null;
    }
    
    StringBuilder categoryPath = new StringBuilder("");
    RepositoryItem category = pCategoryItem;
    
    while (category != null) {  
      // Check if category is root and should it be included in category structure or not
      if (isIncludeRootCategory() || !isRootCategory(category)) {
        if (!StringUtils.isEmpty(categoryPath.toString())) {
          categoryPath.insert(0, CATEGORY_SEPARATOR);
        }
        
        categoryPath.insert(0, category.getPropertyValue(getCategoryDisplayNameProperty()));
      }
      category = (RepositoryItem) category.getPropertyValue(getCatalogTools().getCatalogProperties().getParentCategoryPropertyName());
    }
    
    return categoryPath.toString();
    
  }

  /**
   * Checks if category is root category. Category is assumed to
   * be root if it has no parent category
   * 
   * @param pCategory category item to check
   * @return boolean indicating if the category is root category
   */
  public boolean isRootCategory(RepositoryItem pCategory) {
    RepositoryItem parentCategory = (RepositoryItem) pCategory.getPropertyValue(getCatalogTools().getCatalogProperties().getParentCategoryPropertyName());
    return (parentCategory == null) ? true : false;
  }
  

}
