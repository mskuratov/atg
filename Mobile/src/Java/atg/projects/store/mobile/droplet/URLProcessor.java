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

package atg.projects.store.mobile.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class URLProcessor extends DynamoServlet {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Mobile/src/atg/projects/store/mobile/droplet/URLProcessor.java#2 $$Change: 768606 $";


  /** Url for processing. */
  private String mUrl;
  
  /** Type of processing, f.e. 'addOrReplaceParameter' */
  private String mOperation;
  
  /** Parameter name */
  private String mParameter;
  
  /** Parameter value */
  private String mParameterValue;
  
  static final String PARAM_URL       = "url";
  static final String PARAM_OPERATION = "operation";
  static final String PARAM_NAME      = "parameter";
  static final String PARAM_VALUE     = "parameterValue";
  
  static final ParameterName OUTPUT_OPARAM = ParameterName.getParameterName("output");
  static final String ELEMENT = "element";
  static final String OPERATION_ADD_OR_REPLACE_PARAMETER = "addOrReplaceParameter";
  static final String OPERATION_DEFAULT = OPERATION_ADD_OR_REPLACE_PARAMETER;
  
  /**
   * This droplet transforms specified url according to the selected type of processing, 
   * that is declared through "operation"-parameter.<br>
   * If operation isn't defined, droplet by default will add or replace request parameter value.<br>
   */
  @Override
  public void service(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    String operation = (String) pRequest.getLocalParameter(PARAM_OPERATION);
    if (operation == null){
      operation = OPERATION_DEFAULT; 
    }

    String url = (String) pRequest.getLocalParameter(PARAM_URL);
    String result = url;

    if (url != null && !url.isEmpty()) {
      if ( OPERATION_ADD_OR_REPLACE_PARAMETER.equals(operation) ){
        String param = (String) pRequest.getLocalParameter(PARAM_NAME);

        if (param != null) {
          Object newValueObj = pRequest.getLocalParameter(PARAM_VALUE); 
          String newValue = newValueObj != null ? newValueObj.toString() : "";
          
          String[] parts = url.split(param + "=");
          
          // if parameter is presented in url,
          // replace it's value to the new one
          if (parts != null && parts.length > 1){
            String oldValue;
            int index = parts[1].indexOf("&");
            
            // parameter isn't last
            if (index != -1) {
              oldValue = parts[1].substring(0, index);
            }
            else { /* parameter is last */
              oldValue = parts[1];
            }
            
            result = url.replaceAll(param + "=" + oldValue, param + "=" + newValue);
          }
          else { 
            // parameter is absent in url
            // add new parameter with a specified value
            String sep = (parts[0].indexOf("=") == -1 ? "?"  : "&");
            result = url + sep + param + "=" + newValue;
          }
        }
      }
    }

    pRequest.setParameter(ELEMENT, result);
    pRequest.serviceLocalParameter(OUTPUT_OPARAM, pRequest, pResponse);
  }

  /**
   * Returns url of processing 
   * @return url of processing
   */
  public String getUrl() {
    return mUrl;
  }

  /**
   * Sets url for processing.
   * @param pUrl new url value for processing
   */
  public void setUrl(String pUrl) {
    this.mUrl = pUrl;
  }

  /**
   * Returns type of processing
   * @return type of processing
   */
  public String getOperation() {
    return mOperation;
  }

  /**
   * Sets type of processing
   * @param pOperation new value for the type of processing
   */
  public void setOperation(String pOperation) {
    this.mOperation = pOperation;
  }

  /**
   * Returns parameter name
   * @return parameter name
   */
  public String getParameter() {
    return mParameter;
  }

  /**
   * Sets new value for the parameter name
   * @param pParameter new value of parameter name
   */
  public void setParameter(String pParameter) {
    this.mParameter = pParameter;
  }

  /**
   * Returns new value for the parameter 
   * @return parameter value
   */
  public String getParameterValue() {
    return mParameterValue;
  }

  /**
   * Sets new value for parameter
   * @param pParameterValue parameter value
   */
  public void setParameterValue(String pParameterValue) {
    this.mParameterValue = pParameterValue;
  }
}
