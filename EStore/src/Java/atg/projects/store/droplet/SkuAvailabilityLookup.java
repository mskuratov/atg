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

import atg.commerce.inventory.InventoryException;

import atg.nucleus.naming.ParameterName;

import atg.projects.store.inventory.StoreInventoryManager;

import atg.repository.RepositoryItem;

import atg.service.util.CurrentDate;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;

import java.util.Date;

import javax.servlet.ServletException;


/**
 * <P>
 * This droplet looks at a single sku and the product referencing it and determines if it is
 * available, preorderable, backorderable or unavailable.
 *
 * <P>
 * The <code>inventoryManager</code> property must be configured for this droplet.
 *
 * <P>
 * This droplet takes the following input parameters:
 *
 * <UL>
 * <LI>
 * product - The product repository item that is to be checked
 * </LI>
 * <LI>
 * skuId - The sku repository item id that is to be checked
 * </LI>
 * </UL>
 *
 * <P>
 * This droplet renders the following open parameters:
 *
 * <UL>
 * <LI>
 * available - rendered if the sku is available
 * </LI>
 * <LI>
 * preorderable - rendered if the sku is preorderable
 * </LI>
 * <LI>
 * backorderable - rendered if the sku is backorderable
 * </LI>
 * <LI>
 * unavailable - rendered if the sku is unavailable
 * </LI>
 * <LI>
 * error - rendered if there is an error looking up the availability data for the sku
 * </LI>
 * <LI>
 * default - rendered if there is no open parameter coorespoding to the result of the lookup
 * </LI>
 * </UL>
 *
 * <P>
 * This droplet sets the following output parameters when rendering the available and
 * backorderable open parameters.
 *
 * <UL>
 * <LI>
 * availabilityDate - a Date object representing the date the item is supposed to become
 * available (may be null)
 * </LI>
 * </UL>
 *
 * <P>
 * Example:
 * <PRE>
 *
 * &lt;dsp:droplet name="/atg/store/droplet/SkuAvailabilityLookup"&gt;
 *   &lt;dsp:param name="product" param="product"/&gt;
 *   &lt;dsp:param name="skuId" param="product.childSKUs[0].repositoryId"/&gt;
 *   &lt;dsp:oparam name="available"&gt;
 *     Is available
 *   &lt;/dsp:oparam&gt;
 *   &lt;dsp:oparam name="preorderable"&gt;
 *     Can be preordered
 *     &lt;dsp:droplet name="/atg/dynamo/droplet/IsEmpty"&gt;
 *       &lt;dsp:param name="value" param="availabilityDate"/&gt;
 *       &lt;dsp:oparam name="false"&gt;
 *         &lt;br&gt;
 *         Will be available
 *         &lt;dsp:valueof param="availabilityDate" converter="date" format="M/d/yy"/&gt;
 *       &lt;/dsp:oparam&gt;
 *     &lt;/dsp:droplet&gt;
 *   &lt;/dsp:oparam&gt;
 *   &lt;dsp:oparam name="backorderable"&gt;
 *     Can be backordered
 *   &lt;/dsp:oparam&gt;
 *   &lt;dsp:oparam name="unavailable"&gt;
 *     Is unavailable
 *   &lt;/dsp:oparam&gt;
 * &lt;/dsp:droplet&gt;
 *
 * </PRE>
 *
 * <P>
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/SkuAvailabilityLookup.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class SkuAvailabilityLookup extends DynamoServlet {
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/SkuAvailabilityLookup.java#2 $$Change: 768606 $";

  /** The input parameter name for the product and skuId to check. */
  public static final ParameterName PRODUCT = ParameterName.getParameterName("product");

  /**
   * Sku id parameter name.
   */
  public static final ParameterName SKU_ID = ParameterName.getParameterName("skuId");

  /** The output parameter name for the availabilityDate to set. */
  public static final String AVAILABILITY_DATE = "availabilityDate";

  /** The oparam name rendered once if the item is a preorderable item. */
  public static final String OPARAM_OUTPUT_PREORDERABLE = "preorderable";

  /** The oparam name rendered once if the item is not preorderable and is in stock. */
  public static final String OPARAM_OUTPUT_AVAILABLE = "available";

  /** The oparam name rendered once if the item is not preorderable, is not in stock  and is backorderable. */
  public static final String OPARAM_OUTPUT_BACKORDERABLE = "backorderable";

  /** The oparam name rendered once if the item is none of the above. */
  public static final String OPARAM_OUTPUT_UNAVAILABLE = "unavailable";

  /** The oparam name rendered once if the provided skuId can not be looked up in the inventory repository. */
  public static final String OPARAM_OUTPUT_ERROR = "error";

  /** The oparam name rendered once if none of the above open parameters exists. */
  public static final String OPARAM_OUTPUT_DEFAULT = "default";

  /**
   * Inventory manager.
   */
  protected StoreInventoryManager mInventoryManager;

  /**
   * @return the inventoryManager.
   */
  public StoreInventoryManager getInventoryManager() {
    return mInventoryManager;
  }

  /**
   * @param pInventoryManager - the inventoryManager to set.
   */
  public void setInventoryManager(StoreInventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }
  
  /**
   * currentDate
   */
  private CurrentDate mCurrentDate;
  /**
   * Sets the CurrentDate component.
   */
  public void setCurrentDate(CurrentDate pCurrentDate) { 
    mCurrentDate = pCurrentDate; 
  }
  /**
   * Gets the CurrentDate component.
   */
  public CurrentDate getCurrentDate() { 
    return mCurrentDate; 
  }
  
  
  /**
   * Determines if the item is preorderable.
   * @param pRequest - http request
   * @param pResponse - http response
   * @throws ServletException if an error occurs
   * @throws IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    boolean handled = false;
    Object productParam = pRequest.getObjectParameter(PRODUCT);
    Object skuIdParam = pRequest.getObjectParameter(SKU_ID);

    // Check for valid input
    if (productParam == null) {
      if (isLoggingDebug()) {
        logDebug("MISSING PARAM: no product repository item supplied");
      }

      return;
    } else if (!(productParam instanceof RepositoryItem)) {
      if (isLoggingDebug()) {
        logDebug("INCORRECT PARAM: product argument not a repository item");
      }

      return;
    }

    if (skuIdParam == null) {
      if (isLoggingDebug()) {
        logDebug("MISSING PARAM: no sku id supplied");
      }

      return;
    } else if (!(skuIdParam instanceof String)) {
      if (isLoggingDebug()) {
        logDebug("INCORRECT PARAM: sku id argument is not a string");
      }

      return;
    }

    StoreInventoryManager invManager = getInventoryManager();

    // Call InventoryManager to do all the work and convert results into correctly rendered output
    try {
      int availability = invManager.queryAvailabilityStatus((RepositoryItem) productParam, (String) skuIdParam);

      if (availability == invManager.getAvailabilityStatusInStockValue()) {
        handled = pRequest.serviceLocalParameter(OPARAM_OUTPUT_AVAILABLE, pRequest, pResponse);
      } else if (availability == invManager.getAvailabilityStatusBackorderableValue()) {
        // For store, do not report a backorder availability date that has already passed
        Date backorderAvailabilityDate = invManager.getBackorderAvailabilityDate((String) skuIdParam);

        // Get the current system time.
        CurrentDate cd = getCurrentDate();
        Date currentDate = cd.getTimeAsDate();
        
        if (backorderAvailabilityDate != null && backorderAvailabilityDate.before(currentDate)) {
          backorderAvailabilityDate = null;          
        }

        pRequest.setParameter(AVAILABILITY_DATE, backorderAvailabilityDate);
        handled = pRequest.serviceLocalParameter(OPARAM_OUTPUT_BACKORDERABLE, pRequest, pResponse);
      } else if (availability == invManager.getAvailabilityStatusPreorderableValue()) {
        pRequest.setParameter(AVAILABILITY_DATE, invManager.getPreorderAvailabilityDate((RepositoryItem) productParam));
        handled = pRequest.serviceLocalParameter(OPARAM_OUTPUT_PREORDERABLE, pRequest, pResponse);
      } else {
        // For store, default everything else to unavailable
        handled = pRequest.serviceLocalParameter(OPARAM_OUTPUT_UNAVAILABLE, pRequest, pResponse);
      }
    } catch (InventoryException ie) {
      handled = pRequest.serviceLocalParameter(OPARAM_OUTPUT_ERROR, pRequest, pResponse);
    }

    if (!handled) {
      handled = pRequest.serviceLocalParameter(OPARAM_OUTPUT_DEFAULT, pRequest, pResponse);
    }
  }
}
