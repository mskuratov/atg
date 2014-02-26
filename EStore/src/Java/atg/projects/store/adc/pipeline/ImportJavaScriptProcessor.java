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


package atg.projects.store.adc.pipeline;

import atg.adc.pipeline.ADCPipelineArgs;
import atg.adc.pipeline.ADCPipelineProcessor;
import atg.core.util.StringUtils;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.servlet.ServletUtil;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

/**
 * This processor is responsible for creating JavaScript library include.
 * 
 * @author ATG
 * @version $Id:
 *          //hosting-blueprint/B2CBlueprint/main/integrations/test/src/atg/projects/b2cblueprint/dusttests/BlueprintMismatchException.java#2
 *          $$Change: 791340 $
 * @updated $DateTime: 2013/02/19 09:03:40 $$Author: abakinou $
 */
public class ImportJavaScriptProcessor extends ADCPipelineProcessor {

  // -------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/adc/pipeline/ImportJavaScriptProcessor.java#2 $$Change: 791340 $";

  // -------------------------------------
  // Constants
  // -------------------------------------
  public static final String PERFORM_MONITOR_NAME = "ImportJavaScriptProcessor";

  public static final String PERFORM_OPERATION_NAME = "updateADCData";

  public static final String JAVASCRIPT_IMPORT = "<script type=\"text/javascript\" charset=\"utf-8\" src=\"{0}\"></script>";
  
  public static final String ABSOLUTE_URL_PREFIX="//";
  
  public static final String URL_SEPARATOR="/";

  //--------------------------------------------------
  // property: ScriptUrl
  // The URL to JavaScript library for which the JavaScipt import statement
  // should be generated
  private String mScriptUrl;

  /**
   * Returns the JavaScript library URL that will be used to build JavaSript import statement.
   * 
   * @return the JavaScript library URL
   */
  public String getScriptUrl() {
    return mScriptUrl;
  }

  /**
   * Sets the JavaScript library URL that will be used to build JavaSript import statement.
   * 
   * @param pScriptUrl - the JavaScript library URL
   */
  public void setScriptUrl(String pScriptUrl) {
    mScriptUrl = pScriptUrl;
  }
  
  //--------------------------------------------------
  // property: IncludeContextPath
  private boolean mIncludeContextPath = true;
  
  /** 
   * Sets <code>includeContextPath</code> boolean property indicating whether
   * current context path should be added to the script URL.
   * @param pIncludeContextPath The boolean property indicating whether
   * current context path should be added to the script URL.
   */
  public void setIncludeContextPath(boolean pIncludeContextPath){
    mIncludeContextPath = pIncludeContextPath;
  }
  
  /** 
   * Returns <code>includeContextPath</code> boolean property indicating whether
   * current context path should be added to the script URL.
   * @return The  boolean property indicating whether
   * current context path should be added to the script URL.
   */
  public boolean isIncludeContextPath(){
    return mIncludeContextPath;
  }

  /**
   * This processor will append to the response stream  the configured JavaScript library import.
   * 
   * @param pArgs the current ADC pipeline arguments
   * @return an ADC processor status code
   */
  @Override
  public int updateADCData(ADCPipelineArgs pArgs) {

    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME,
        PERFORM_OPERATION_NAME);
    
    String scriptURL = getScriptUrl();
    
    if (StringUtils.isEmpty(scriptURL)){
      vlogError("No script URL is configured for the ImportJavaScriptProcessor");      
      return STOP_CHAIN_EXECUTION;
    }
    
    if (!scriptURL.startsWith(URL_SEPARATOR)){
      scriptURL = URL_SEPARATOR + scriptURL;
    }
    
    // If includeContextPath is configured to true, retrieve current context path and prepend the
    // configured script URL in the case it's not absolute already.
    if(isIncludeContextPath() && !scriptURL.startsWith(ABSOLUTE_URL_PREFIX)){
      
      HttpServletRequest currentRequest = ServletUtil.getCurrentRequest();
      
      if (currentRequest != null){
        String contextPath = currentRequest.getContextPath();
        if (!scriptURL.startsWith(contextPath)){
          scriptURL = contextPath + scriptURL; 
        }
      }
    }
    
    StringBuffer currentData = pArgs.getCurrentADCData().getData();
    try {
      currentData.append(MessageFormat.format(JAVASCRIPT_IMPORT,
          new Object[] { scriptURL }));
    } finally {
      try {
        PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME,
            PERFORM_OPERATION_NAME);
      } catch (PerfStackMismatchException e) {
        if (isLoggingWarning()) {
          logWarning(e);
        }
      }
    }
    return MADE_CHANGE;
  }

}
