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


package atg.projects.store.recommendations.processor;

import java.text.MessageFormat;

import atg.adc.pipeline.ADCPipelineArgs;
import atg.projects.store.recommendations.adc.StoreADCRequestData;

/**
 * This processor is responsible for generating clickstream 
 * tracking code markup for search results pages. It extends 
 * <code>TrackingCodeProcessor</code> and overrides its 
 * <code>buildTrackingCodeViewContent()</code> method in order 
 * to add 'searchText' entry to the 'view' configuration 
 * parameter. The search keyword associated with the current request 
 * should be stored into <code>ADCRequestData</code> object in ADC 
 * pipeline arguments by preceding <code>SetSearchTermProcessor</code>.
 * 
 * @author Yekaterina Kostenevich
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Recommendations/src/atg/projects/store/recommendations/processor/SearchResultsProcessor.java#2 $Change: 630322 $
 * @updated $DateTime: 2012/12/26 06:47:02 $Author: ykostene $
 *
 */
public class SearchResultsProcessor extends TrackingCodeProcessor{  

  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Recommendations/src/atg/projects/store/recommendations/processor/SearchResultsProcessor.java#2 $Change: 630322 $";

  /** Entries format constants*/
  public static final String SEARCH_TEXT_ENTRY = "<dt>searchText</dt><dd>{0}</dd>";
    
  /**
   * Overrides base <code>TrackingCodeProcessor's</code> method in order to check 
   * whether search keyword is specified in <code>ADCRequestData</code> object stored 
   * in the ADC pipeline arguments. If not the further processing will 
   * be stopped.
   * 
   * @param pArgs The pipeline arguments
   * @return true - if category repository item is specified in <code>ADCRequestData</code> 
   * object stored in the ADC pipeline arguments. Otherwise false.
   */  
  @Override
  protected boolean validateRequiredData(ADCPipelineArgs pArgs){
    boolean valid = false;        
    try {
      String searchText = ((StoreADCRequestData) pArgs.getADCRequestData()).getSearchKeyword();
      if (searchText == null || searchText.length() < 1) {
        if (isLoggingDebug()) {
          logDebug("SearchText is not set. Skip processor execution");
        }
      }
      else {
        valid = true;
      }      
    }
    catch (ClassCastException cce){
      if (isLoggingDebug()) {
        logDebug("Can't cast ADCRequestData to StoreADCRequestData. Skip processor execution");       
      }
    }
    
    return valid;
  }
    
  /**
   * Overrides base <code>TrackingCodeProcessor's</code> method in order to append 
   * 'searchText' entry to the 'view' configuration parameter content.
   * 
   * @param pArgs ADC pipeline arguments
   * @return 'view' entry content for the recommendations clickstream tracking code
   */
  @Override
  protected String buildTrackingCodeViewContent (ADCPipelineArgs pArgs){
    StringBuilder trackingCodeContent = new StringBuilder();
    
    // Append tracking content from super.buildTrackingCodeViewContent if it is not null
    appendEntry(trackingCodeContent, super.buildTrackingCodeViewContent(pArgs));
    
    // Append searchText entry
    appendEntry(trackingCodeContent, buildSearchTextEntry(pArgs));
        
    return trackingCodeContent.toString();
  }
  
  /**
   * Builds 'searchText' entry for the recommendations clickstream tracking 
   * code. The search keyword is taken from the corresponding parameter 
   * stored in the <code>ADCRequestData</code> by the preceding <code>SetProductProcessor</code> 
   * pipeline processor.
   * 
   * @param pArgs ADC pipeline arguments
   * @return searchText entry
   */
  protected String buildSearchTextEntry (ADCPipelineArgs pArgs){
    String searchTextEntry = "";   
    String searchText = ((StoreADCRequestData) pArgs.getADCRequestData()).getSearchKeyword();
    searchTextEntry = MessageFormat.format(SEARCH_TEXT_ENTRY, new Object[] { searchText });
    return searchTextEntry;
  }
}
