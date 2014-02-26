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
import atg.commerce.adc.CommerceADCRequestData;
import atg.repository.RepositoryItem;

/**
 * This processor is responsible for generating clickstream tracking code markup
 * for product detail pages. It extends <code>TrackingCodeProcessor</code> and 
 * overrides its <code>buildTrackingCodeViewContent()</code> method in order to add 
 * 'productId' entry to the 'view' configuration parameter. 
 * The product repository item should be stored into the 
 * <code>ADCRequestData</code> by the preceding <code>ProcSetProduct</code> processor.
 * 
 * @author Yekaterina Kostenevich
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Recommendations/src/atg/projects/store/recommendations/processor/ProductTrackingCodeProcessor.java#2 $Change: 630322 $
 * @updated $DateTime: 2012/12/26 06:47:02 $Author: ykostene $
 *
 */
public class ProductTrackingCodeProcessor  extends TrackingCodeProcessor{
  
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Recommendations/src/atg/projects/store/recommendations/processor/ProductTrackingCodeProcessor.java#2 $Change: 630322 $";

  /** Entries format constants*/
  public static final String PRODUCT_ID_ENTRY = "<dt>productId</dt><dd>{0}</dd>";  
    
  /**
   * Overrides base </code>TrackingCodeProcessor's</code> method in order to check
   * whether product repository item is specified in <code>ADCRequestData</code> 
   * object stored in the ADC pipeline arguments. If not the further 
   * processing will be stopped.
   * 
   * @param pArgs The pipeline arguments
   * @return true if product repository item is specified in <code>ADCRequestData</code> 
   *         object stored in the ADC pipeline arguments. Otherwise false.
   */  
  @Override
  protected boolean validateRequiredData(ADCPipelineArgs pArgs){
    boolean valid = false;
    
    try {
      RepositoryItem product = ((CommerceADCRequestData) pArgs.getADCRequestData()).getProduct();
      if (product == null) {
        if (isLoggingDebug()) {
          logDebug("Product is not set. Skip processor execution");
        }
      }
      else {
        valid = true;
      }
    } 
    catch (ClassCastException cce){
      if (isLoggingDebug()) {
        logDebug("Can't cast ADCRequestData to CommerceADCRequestData. Skip processor execution");
      }
    }
      
    return valid;
  }
  
  
  /**
   * Overrides base <code>TrackingCodeProcessor's</code> method in order to append 'productId' 
   * entry to the 'view' configuration parameter content.
   * 
   * @param pArgs ADC pipeline arguments
   * @return 'view' entry content for the recommendations clickstream tracking code
   */
  @Override
  protected String buildTrackingCodeViewContent (ADCPipelineArgs pArgs){
    StringBuilder trackingCodeContent = new StringBuilder();
    
    // Append tracking content from super.buildTrackingCodeViewContent if it is not null
    appendEntry(trackingCodeContent, super.buildTrackingCodeViewContent(pArgs));
    
    // Append productId entry
    appendEntry(trackingCodeContent, buildProductIdEntry(pArgs));
    
    return trackingCodeContent.toString();
  }
  
  /**
   * Builds 'productId' entry for the recommendations clickstream tracking code. 
   * The product ID is taken from the data stored into <code>ADCRequestData</code> by the 
   * preceding <code>ProcSetProduct</code> pipeline processor.
   * 
   * @param pArgs ADC pipeline arguments
   * @return productId entry
   */
  protected String buildProductIdEntry (ADCPipelineArgs pArgs){
    String productIdEntry = "";
    RepositoryItem product = ((CommerceADCRequestData) pArgs.getADCRequestData()).getProduct();
    productIdEntry = MessageFormat.format(PRODUCT_ID_ENTRY, new Object[] { product.getRepositoryId() });
    return productIdEntry;
  }
 
}
