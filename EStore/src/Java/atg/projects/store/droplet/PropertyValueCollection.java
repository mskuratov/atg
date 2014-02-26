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

import atg.nucleus.naming.ParameterName;

import atg.projects.store.catalog.StoreCatalogTools;

import atg.service.collections.filter.CachedCollectionFilter;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;

import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;


/**
 * Creates a collection of property values for a collection of repository items.
 * An optional CollectionFilter can be applied to the values to filter and/or sort them.
 * 
 * <p>
 * Input Parameters: <br/>
 * <code>items</code> - The collection of items that will be used to create the 
 * collection of property values.
 *  
 * <code>propertyName</code> - The property whose values will populate the resultant
 * collection.
 *  
 * <code>filter</code> - An optional CollectionFilter to filter the resultant collection
 * </p>
 * 
 * <p>
 * Open Parameters: <br/>
 * <code>output</code> - Rendered when there is atleast 1 entry in the resultant
 * property values collection.
 * 
 * <code>empty</code> - Rendered if there is no entrys in the resultant property
 * values collection.
 * 
 * <code>error</code> - Rendered if an exception occurs
 * </p>
 * 
 * <p>
 * Output Parameters:<br/>
 * None
 * </p>
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/PropertyValueCollection.java#2 $
 */
public class PropertyValueCollection extends DynamoServlet {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/PropertyValueCollection.java#2 $$Change: 768606 $";

  /**
   * Items parameter name.
   */
  public static final ParameterName ITEMS_PARAM = ParameterName.getParameterName("items");

  /**
   * Proeprty name parameter name.
   */
  public static final ParameterName PROPERTY_NAME_PARAM = ParameterName.getParameterName("propertyName");

  /**
   * Filter parameter name.
   */
  public static final ParameterName FILTER_PARAM = ParameterName.getParameterName("filter");

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
   * Catalog tools.
   */
  protected StoreCatalogTools mCatalogTools = null;

  /**
   * Filter.
   */
  protected CachedCollectionFilter mFilter;

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

  /**
  * Sets the the default filter to execute.
   * @param pFilter - filter
  */
  public void setFilter(CachedCollectionFilter pFilter) {
    mFilter = pFilter;
  }

  /**
  * @return the default filter to execute.
  */
  public CachedCollectionFilter getFilter() {
    return mFilter;
  }

  //-------------------------------------
  // Methods
  //-------------------------------------

  /**
   * Renders the <code>output</code> oparam with a collection of optionally filtered/sorted values.
   * Renders the <code>empty</code> oparam if the collection of items or values is empty.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    try {
      String propertyName = (String)pRequest.getLocalParameter(PROPERTY_NAME_PARAM);
      List items = (List) pRequest.getObjectParameter(ITEMS_PARAM);

      Collection values = getCatalogTools().getPossibleValuesForSkus(items, propertyName);

      String renderParam;

      if (values.isEmpty()) {
        renderParam = EMPTY_OPARAM;
      } else {
        CachedCollectionFilter filter = getFilter(pRequest);

        if (filter != null) {
          // Apply the filter without caching
          values = filter.filterCollection(values, null, null, false, false);
        }

        renderParam = OUTPUT_OPARAM;
        pRequest.setParameter(VALUES_PARAM, values);
      }

      pRequest.serviceLocalParameter(renderParam, pRequest, pResponse);
    } catch (Exception exc) {
      // TODO error message
      pRequest.serviceLocalParameter(ERROR_OPARAM, pRequest, pResponse);
    }
  }

  /**
  *
  * @param pRequest DynamoHttpServletRequest value
  * @return the <code>filter</code> param from the request. if the param was not provided,
  * the configured <code>filter</code> is returned.
  */
  protected CachedCollectionFilter getFilter(DynamoHttpServletRequest pRequest) {
    CachedCollectionFilter filter = (CachedCollectionFilter) pRequest.getObjectParameter(FILTER_PARAM);

    if (filter == null) {
      filter = getFilter();
    }

    return filter;
  }
}
