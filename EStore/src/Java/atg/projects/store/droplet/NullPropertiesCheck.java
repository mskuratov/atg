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

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.projects.store.logging.LogUtils;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;


/**
 * <p>
 * This droplet is a convenience to help prevent JSP compilation failure due to
 * the passing of nulls into a dsp tag. This droplet takes a pipe-delimited
 * list of property names and a repository item. In the case of strings,
 * strings are considered null if they are equivalent to the empty string or
 * are null.
 * </p>
 *
 * <p>
 * This droplet takes the following input parameters:
 *
 * <ul>
 * <li>
 * item - repository item whose properties need checking
 * </li>
 * <li>
 * properties - pipe delimited list of property names to check
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * This droplet renders the following oparams:
 *
 * <ul>
 * <li>
 * all - rendered if all of the properties are null
 * </li>
 * <li>
 * true - rendered if some of the properties are null
 * </li>
 * <li>
 * false - rendered if none of the properties are null
 * </li>
 * </ul>
 * </p>
 *
 * <p>
 * This droplet sets the following parameters on output oparam of true
 *
 * <ul>
 * <li>nullProperties - list of properties that were null
 * <li>definedProperties - list of properties that were not null
 * </ul>
 * </p>
 *
 * <p>
 * Example:
 * <PRE>
 *
 * &lt;dsp:droplet bean="/atg/store/droplet/NullPropertiesCheck"&gt;
 * &lt;dsp:param name="item" param="category"&gt;
 * &lt;dsp:param name="properties" value="template.url|auxiliaryMedia.navon.url"&gt;
 * &lt;dsp:oparam name="true"&gt;
 * These properties were null
 * &lt;dsp:valueof param="nullProperties"&gt;
 * &lt;/dsp:valueof&gt;
 * &lt;/dsp:oparam&gt;
 * &lt;dsp:oparam name="false"&gt;
 * &lt;/dsp:oparam&gt;
 * &lt;/dsp:droplet&gt;
 *
 * </PRE>
 * </p>
 *
 * @author ATG
 * @version $Revision: #3 $
 */
public class NullPropertiesCheck extends DynamoServlet {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/NullPropertiesCheck.java#3 $$Change: 788278 $";

  /**
   * Item parameter name.
   */
  public static final ParameterName ITEM = ParameterName.getParameterName("item");

  /**
   * Properties parameter name.
   */
  public static final ParameterName PROPERTIES = ParameterName.getParameterName("properties");

  /**
   * All parameter name.
   */
  public static final ParameterName ALL = ParameterName.getParameterName("all");

  /**
   * True parameter name.
   */
  public static final ParameterName TRUE = ParameterName.getParameterName("true");

  /**
   * False parameter name.
   */
  public static final ParameterName FALSE = ParameterName.getParameterName("false");

  /**
   * Null properties parameter name.
   */
  public static final String NULL_PROPERTIES = "nullProperties";

  /**
   * Defined properties parameter name.
   */
  public static final String DEFINED_PROPERTIES = "definedProperties";

  /**
   * <p>
   * Performs the tasks outlined in the class description.
   * </p>
   *
   * @param pRequest DynamoHttpServletRequest
   * @param pResponse DynamoHttpServletResponse
   *
   * @throws ServletException if an error occurs
   * @throws IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    String properties = (String)pRequest.getLocalParameter(PROPERTIES);
    RepositoryItem item = (RepositoryItem) pRequest.getObjectParameter(ITEM);

    List nullProperties = new ArrayList();
    List definedProperties = new ArrayList();

    if (StringUtils.isEmpty(properties)) {
      if (isLoggingDebug()) {
        logDebug(LogUtils.formatMajor("Null value for property 'properties' passed"));
      }

      return;
    }

    if (item == null) {
      if (isLoggingDebug()) {
        logDebug(LogUtils.formatMajor("Null value for property 'item' passed"));
      }

      return;
    }

    try {
      String[] props = properties.split("\\|");

      if (props != null) {
        int len = props.length;
        String propertyName = null;
        Object propertyValu = null;

        for (int i = 0; i < len; i++) {
          propertyName = props[i];

          if (isLoggingDebug()) {
            logDebug("Checking item " + item.getRepositoryId() + " for property " + propertyName);
          }

          if (StringUtils.isEmpty(propertyName)) {
            continue;
          }

          propertyValu = DynamicBeans.getSubPropertyValue(item, propertyName);

          if (isValueEmpty(propertyValu)) {
            nullProperties.add(propertyName);
          } else {
            definedProperties.add(propertyName);
          }
        }

        // for each property to check
      }
    } catch (PropertyNotFoundException pnfe) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor
                ("Property passed in 'properties' param not found for the given page."), pnfe);
      }
    }

    if (definedProperties.isEmpty()) {
      // if there were no defined properties then all of them 
      // are null
      if (isLoggingDebug()) {
        logDebug("None of the properties were defined, servicing all param");
      }

      pRequest.setParameter(NULL_PROPERTIES, nullProperties);
      pRequest.setParameter(DEFINED_PROPERTIES, definedProperties);
      pRequest.serviceLocalParameter(ALL, pRequest, pResponse);
    } else if (!nullProperties.isEmpty()) {
      if (isLoggingDebug()) {
        logDebug("Some of the properties were empty, servicing true");
      }

      // if there were some empty properties then return the 
      // empty ones and defined ones.      
      pRequest.setParameter(NULL_PROPERTIES, nullProperties);
      pRequest.setParameter(DEFINED_PROPERTIES, definedProperties);
      pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
    } else {
      // other wise 
      pRequest.setParameter(NULL_PROPERTIES, nullProperties);
      pRequest.setParameter(DEFINED_PROPERTIES, definedProperties);
      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
    }
  }

  /**
   * <p>Returns true if the value represented by the object is empty.
   * <p>For strings they are empty if they equal the empty string '' or are
   * null.
   * <p>Collections are empty if they say they are empty
   * <p>null is always empty
   * @param pValue - value to check
   * @return true if the value represented by the object is empty, false - otherwise
   */
  protected boolean isValueEmpty(Object pValue) {
    // check to see if its an instance of a string
    if (pValue == null) {
      return true;
    } else if (pValue instanceof String) {
      return (StringUtils.isEmpty((String) pValue));
    } else if (pValue instanceof Collection) {
      return ((Collection) pValue).isEmpty();
    } else if (pValue instanceof Object[]) {
      int len = ((Object[]) pValue).length;

      return len == 0;
    }

    return false;
  }
}
