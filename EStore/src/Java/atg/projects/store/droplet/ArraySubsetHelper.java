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

import atg.projects.store.logging.LogUtils;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;

import java.lang.reflect.Array;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;


/**
 * <p>
 * This droplet determines the indices of a corresponding array if the array was
 * subdivided into separate parts. It was designed with the display of product
 * ranges in mind. It allows the user to display a numerical navigation through
 * the subsets of a given array.
 * <p>
 * This droplet takes the following input parameters:
 * <ul>
 * <li>array - Collection or array to calculate against.
 * <li>subsetsize - The size of the array subsets.
 * </ul>
 * <p>
 * This droplet renders the following oparams:
 * <ul>
 * <li>output - always rendered
 * </ul>
 * <p>
 * This droplet sets the following output parameters:
 * <ul>
 * <li>subsetIndices - list of indices for the given subdivisions
 * </ul>
 * <p>
 * This droplet assumes the base index starts at 1. Those who wish this to start
 * at zero should set the <code>startIndexAtZero</code> value to true.
 * <p>
 * Example:
 *
 * <pre>
 *
 * &lt;dsp:droplet bean="/atg/store/droplet/ArraySubsetHelper"&gt; &lt;dsp:param
 * name="array" param="category.childProducts"&gt; &lt;dsp:param
 * name="elementName" value="subsetIndex"&gt; &lt;dsp:param name="subsetSize"
 * value="10"&gt; &lt;dsp:oparam name="output"&gt; &lt;dsp:droplet
 * name="ForEach"&gt; &lt;dsp:param name="array" param="subsetIndices"&gt;
 * &lt;dsp:oparam name="output"&gt; &lt;dsp:valueof param="count"/&gt;
 * &lt;dsp:valueof param="subsetIndex"/&gt; &lt;/dsp:oparam&gt;
 * &lt;/dsp:oparam&gt; &lt;/dsp:droplet&gt;
 *
 * </pre>
 *
 * @author ATG
 * @version $Revision: #3 $
 */
public class ArraySubsetHelper extends DynamoServlet {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/ArraySubsetHelper.java#3 $$Change: 788278 $";

  /**
   * Array parameter name.
   */
  public static final ParameterName ARRAY = ParameterName.getParameterName("array");

  /**
   * Subset size parameter name.
   */
  public static final ParameterName SUBSETSIZE = ParameterName.getParameterName("subsetSize");

  /**
   * Output parameter name.
   */
  public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

  /**
   * Subset indices parameter name.
   */
  public static final String SUBSETINDEXES = "subsetIndices";

  /**
   * Should start index at zero.
   */
  private boolean mStartIndexAtZero = false;

  /**
   * @param pStartIndexAtZero - this assumes the indices returned started at zero.
   */
  public void setStartIndexAtZero(boolean pStartIndexAtZero) {
    mStartIndexAtZero = pStartIndexAtZero;
  }

  /**
   * @return If set this assumes the indices returned started at zero. Default is
   * false.
   */
  public boolean isStartIndexAtZero() {
    return mStartIndexAtZero;
  }

  /**
   * This performs the work as described in the class definition.
   * @param pRequest - http request
   * @param pResponse - http response
   * @throws ServletException - if error occurs
   * @throws IOException - if error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Object arrayParam = pRequest.getObjectParameter(ARRAY);
    String subsetSize = (String)pRequest.getLocalParameter(SUBSETSIZE);

    List indices = new ArrayList();

    if (arrayParam == null) {
      if (isLoggingDebug()) {
        logDebug(LogUtils.formatMajor("No array param passed on page " + pRequest.getRequestURI()));
      }

      return;
    }

    int length = 0;

    if (arrayParam instanceof Object[]) {
      Object[] objectArray = (Object[]) arrayParam;
      length = objectArray.length;
    } else if (arrayParam instanceof Collection) {
      Collection col = (Collection) arrayParam;
      length = col.size();
    } else if (arrayParam.getClass().isArray()) {
      length = Array.getLength(arrayParam);
    } else {
      if (isLoggingDebug()) {
        logDebug(LogUtils.formatMajor("Unrecognized array parameter passed " + arrayParam.getClass().getName()));
      }
    }

    try {
      // take the length and divide by the subset size
      int subsetLen = Integer.parseInt(subsetSize);

      // this gives us the number of full subsets
      int numberOfSets = length / subsetLen;

      // this lets us know if we have an incomplete subset
      int remaining = length % subsetLen;

      // if we have any remaining then add another
      numberOfSets = (remaining > 0) ? (numberOfSets + 1) : numberOfSets;

      if (isLoggingDebug()) {
        logDebug("Number of sets = " + numberOfSets);
      }

      // now 'count' our way up the subsets and place the index
      // of each in our output array
      for (int i = 0; i < numberOfSets; i++) {
        // the index of each subset is i * subsetLen
        int index = i * subsetLen;

        // check to see if we started our numbering at zero
        if (!isStartIndexAtZero()) {
          index += 1;
        }

        indices.add(Integer.toString(index));
      } // for each subset
    } catch (NumberFormatException nfe) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Incorrect parameter passed for the subsetSize."), nfe);
      }
    }

    pRequest.setParameter(SUBSETINDEXES, indices);
    pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
  }
}
