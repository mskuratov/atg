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
import java.util.Set;

import javax.servlet.ServletException;

import java.util.Iterator;

import atg.nucleus.naming.ParameterName;
import atg.projects.store.catalog.StoreCatalogProperties;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * <p>
 *    This droplet retrieves the parent categories from a passed in product item. It then iterates
 *    through each category checking if the siteIds property contains the passed in siteId. If the
 *    siteId is matched, a 'categoryId' output parameter is set with the category id of the matched
 *    category.
 * </p>
 * 
 * <dl>
 *   <dt>Input Parameters:</dt>
 *   <dt>product<dt>
 *     <dd>The product RepositoryItem that we want to retrieve the parentCategories of.</dd>
 *   <dt>siteId</dt>
 *     <dd>The siteId of the site we want to get the corresponding category of.</dd>
 * </dl>
 * <br/>
 * <dl>
 *   <dt>Open Parameters:</dt>
 *     <dt>output<dt>
 *       <dd>Always rendered.</dd> 
 * </dl>
 * <br/>
 * <dl>     
 *  <dt>Output Parameters:<dt>  
 *    <dt>categoryId</dt>
 *      <dd>The category id of the category that is relevant to the passed in site id. Will 
 *          return an empty string in the categoryId parameter in no match is found.</dd> 
 * </dl>
 * 
 * @author David Stewart
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/CategoryBySiteDroplet.java#2 $$Change: 791340 $
 * @updated $DateTime: 2013/02/19 09:03:40 $$Author: abakinou $
 */
public class CategoryBySiteDroplet extends DynamoServlet {

  //------------------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/CategoryBySiteDroplet.java#2 $$Change: 791340 $";

  //--------------------------------------------------------------
  //  PROPERTIES
  //--------------------------------------------------------------

  //-------------------------------------------------------
  // property: catalogProperties
  //-------------------------------------------------------
  private StoreCatalogProperties mCatalogProperties = null;
  
  /**
   * @return the StoreCatalogProperties component.
   */
  public StoreCatalogProperties getCatalogProperties() {
    return mCatalogProperties;
  }
  
  /**
   * @param pCatalogProperties - The StoreCatalogProperties component.
   */
  public void setCatalogProperties(StoreCatalogProperties pCatalogProperties) {
    mCatalogProperties = pCatalogProperties;
  }
  
  //--------------------------------------------------------------
  //  CONSTANTS
  //--------------------------------------------------------------

  /** Site id parameter name  */
  public static final ParameterName SITE_ID = ParameterName.getParameterName("siteId");

  /** Product parameter name. */
  public static final ParameterName PRODUCT = ParameterName.getParameterName("product");

  /** Category id output parameter name. */
  public static final String CATEGORY_ID = "categoryId";
  
  /** Output parameter name. */
  public static final String OUTPUT = "output";

  //--------------------------------------------------------------
  // METHODS
  //--------------------------------------------------------------

  //--------------------------------------------------------------
  /**
   * Service method, see API definition.
   *
   * @param pRequest DynamoHttpSevletRequest
   * @param pResponse DynamoHttpServletResponse
   * 
   * @throws ServletException if an error occurs
   * @throws IOException if an error occurs
   */
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    // Get input parameters.
    Object siteIdParam = pRequest.getObjectParameter(SITE_ID);
    Object productParam = pRequest.getObjectParameter(PRODUCT);
    
    // Product parameter checks.
    if (productParam == null) {
      if (isLoggingDebug()) {
        logDebug("MISSING PARAM: no product repository item supplied");
      }
      return;
    } 
    else if (!(productParam instanceof RepositoryItem)) {
      if (isLoggingDebug()) {
        logDebug("INCORRECT PARAM: product argument not a repository item");
      }
      return;
    }

    // Site id parameter checks.
    if (siteIdParam == null) {
      if (isLoggingDebug()) {
        logDebug("MISSING PARAM: no site id supplied");
      }
      return;
    } 
    else if (!(siteIdParam instanceof String)) {
      if (isLoggingDebug()) {
        logDebug("INCORRECT PARAM: site id argument is not a string");
      }
      return;
    }
    
    // The category id that will be returned to the client.
    String categoryId = "";
    
    // Retrieve the parent categories of the product parameter.
    Set parentCategories = 
      (Set) ((RepositoryItem)productParam).getPropertyValue(getCatalogProperties().getParentCategoriesPropertyName());
    
    if (parentCategories != null) {
      Iterator it = parentCategories.iterator();
      
      while (it.hasNext()) {
        Object obj = it.next();
        
        if (obj instanceof RepositoryItem) {
          RepositoryItem category = (RepositoryItem) it.next();
          
          // Retrieve the site ids for the category being iterated over.
          Set<String> categorySiteIds = 
            (Set<String>) category.getPropertyValue(getCatalogProperties().getSitesPropertyName());
          
          if (categorySiteIds.contains(siteIdParam)) {
            // The category being iterated over specifies the siteId parameter in its 
            // list of site ids. Get the category id and return it to client.
            categoryId = category.getRepositoryId();
            break;
          }
        }
      }
    }

    pRequest.setParameter(CATEGORY_ID, categoryId);
    pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
  }
  
}
