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



package atg.projects.store.droplet;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;

import atg.adapter.gsa.GSAItem;
import atg.commerce.catalog.comparison.ProductComparisonList;
import atg.droplet.ForEach;
import atg.nucleus.naming.ParameterName;
import atg.projects.store.catalog.StoreCatalogTools;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * This droplet is used in Product Comparison pages in order to determine 
 * whether there is at least one object in <i>items</i> containing not-null 
 * value for the specified property or the rendering of this property should be omitted. 
 * <p>
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/ComparisonRowExistsDroplet.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class ComparisonRowExistsDroplet extends ForEach {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/ComparisonRowExistsDroplet.java#2 $$Change: 768606 $";

  /**
   * Items parameter name.
   */
  public static final ParameterName ITEMS_PARAM = ParameterName.getParameterName("items");

  /**
   * Property name parameter name.
   */
  public static final ParameterName PROPERTY_NAME_PARAM = ParameterName.getParameterName("propertyName");

  /**
   * Source type parameter name.
   * There are two main values for this field, which define where the property 
   * name specified by {@link #PROPERTY_NAME_PARAM} is supposed to exist:
   * <li><i>sku</i> -  in childSKUs
   * <li><i>product</i> - in the product itself 
   */
  public static final ParameterName SOURCE_TYPE_PARAM = ParameterName.getParameterName("sourceType");

  /**
   * Values parameter name.
   */
  public static final String VALUES_PARAM = "values";

  /**
   * Output parameter name.
   */
  public static final String OUTPUT_OPARAM = "output";

  /**
   * Empty parameter name.
   */
  public static final String EMPTY_OPARAM = "empty";

  /**
   * Error parameter name.
   */
  public static final String ERROR_OPARAM = "error";

  /**
   * Product property name.
   */
  public static final String PRODUCT_PROPERTY_NAME = "product";

  /**
   * Child SKUs property name.
   */
  public static final String CHILD_SKUS_PROPERTY_NAME = "childSKUs";
  
  /**
   * Source type "sku" 
   */
  public static final String TYPE_SKU = "sku";

  /**
   * Source type "product" 
   */
  public static final String TYPE_PRODUCT = "product";
  
  /**
   * Catalog tools.
   */
  protected StoreCatalogTools mCatalogTools = null;

  /**
   * @return the catalog tools.
   */
  public StoreCatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  /**
   * Sets the catalogTools.
   * @param pCatalogTools - catalog tools.
   */
  public void setCatalogTools(StoreCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  //-------------------------------------
  // Methods
  //-------------------------------------

  /**
   * Renders the <code>output</code> oparam if 1 or more objects have a non-null value for a certain property, 
   * which name is transferred using <code>PROPERTY_NAME_PARAM</code> paramter.<p>
   * Renders the <code>empty</code> oparam if all objects in <i>items</i> have null or empty value for that property. 
   * parameter wasn't found in any of the items.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    try {
      String propertyName = pRequest.getParameter(PROPERTY_NAME_PARAM);
      String propertyType = pRequest.getParameter(SOURCE_TYPE_PARAM);
      List<ProductComparisonList.Entry> items = (List) pRequest.getObjectParameter(ITEMS_PARAM);
  
      boolean notNullValuesExist = false;
 
      if(TYPE_SKU.equals(propertyType)){
        //the property is in childSKUs 
        for (ProductComparisonList.Entry object : items) {
    
          GSAItem gsaItem = (GSAItem) (object.get(PRODUCT_PROPERTY_NAME));
          if(gsaItem != null){
            List skus = (List) gsaItem.getPropertyValue(CHILD_SKUS_PROPERTY_NAME);
            if(skus != null && !skus.isEmpty() && ((GSAItem)skus.get(0)).getItemDescriptor().hasProperty(propertyName)){
              Collection values = getCatalogTools().getPossibleValuesForSkus(skus, propertyName);  
              if (values != null && !values.isEmpty()) {
                notNullValuesExist = true;
                break;
              }
            }
          }
        }
      }else if(TYPE_PRODUCT.equals(propertyType)){
        //the property is in the product
        for (ProductComparisonList.Entry object : items) {
          
          GSAItem gsaItem = (GSAItem) (object.get(PRODUCT_PROPERTY_NAME));
          if(gsaItem != null && gsaItem.getItemDescriptor().hasProperty(propertyName)){            
            Collection values = (Collection) gsaItem.getPropertyValue(propertyName);  
            if (values != null && !values.isEmpty()) {
              notNullValuesExist = true;
              break;
            }           
          }
        }
      }
  
      String renderParam;
  
      if (notNullValuesExist) {
        renderParam = OUTPUT_OPARAM;
      } else {
        renderParam = EMPTY_OPARAM;
      }
  
      pRequest.serviceLocalParameter(renderParam, pRequest, pResponse);
  
    } catch (Exception exc) {
      pRequest.serviceLocalParameter(ERROR_OPARAM, pRequest, pResponse);
    }
  }


}
