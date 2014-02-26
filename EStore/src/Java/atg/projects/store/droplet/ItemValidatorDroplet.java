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

import javax.servlet.ServletException;

import atg.service.collections.validator.CollectionObjectValidator;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * This droplet will validate single item according to array of validators set.
 * 
 * Input parameters:
 *   item
 *     an item to be validated
 *     
 *  Open parameters:
 *    output
 *     Always rendered
 *     
 *  Output parameters:
 *   itemIsValid
 *     It is set to 'true' if item has valid start\end dates,
 *     and to 'false' if item has invalid start and end dates.
 * 
 * 
 * @author Yekaterina Kostenevich
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/ItemValidatorDroplet.java#2 $Change: 630322 $
 * @updated $DateTime: 2013/02/19 09:03:40 $Author: ykostene $
 *
 */
public class ItemValidatorDroplet extends DynamoServlet{
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/ItemValidatorDroplet.java#2 $Change: 630322 $";
  
  private static final String OPARAM_TRUE = "true";
  private static final String OPARAM_FALSE = "false";
  private static final String ITEM = "item";
  
  /**
   * property: validators
   * Array of validators that will be applied to items
   */
  private CollectionObjectValidator[] mValidators;
  
  /**
   * @return the validators
   */
  public CollectionObjectValidator[] getValidators() {
    return mValidators;
  }

  /**
   * @param validators the validators to set
   */
  public void setValidators(CollectionObjectValidator[] pValidators) {
    this.mValidators = pValidators;
  }
  
  /**
   * Validates item in request using configured validators.
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Object item = pRequest.getObjectParameter(ITEM);
     
    boolean isValid = validateItem(item);
      
    if (isValid) {
      pRequest.serviceLocalParameter(OPARAM_TRUE, pRequest, pResponse);  
    }
    else {
      pRequest.serviceLocalParameter(OPARAM_FALSE, pRequest, pResponse);  
    }   
  }
  
  /**
   * Validate object using configured array of validators.
   * @param item the item to validate
   * @return false - if object fails validation, true if there should 
   * be no validation or if the object is valid 
   */
  private boolean validateItem(Object item) {
    
    // There is  no validators set, so no filtering is needed.
    if (getValidators() == null || getValidators().length == 0) {
      return true;
    }
    
    boolean isValid = true;
    for (CollectionObjectValidator validator: getValidators()) {
      if (!validator.validateObject(item)) {
        
        if (isLoggingDebug()) {
          logDebug("Object: " + item + " doesn't pass validator:" + validator);
        }
        
        
        // Item doesn't pass validation. Set isValid to false
        // and leave the loop as there is no need to check all
        // others validators.              
        isValid = false;
        break;
        
      }
    }
    return isValid;
  }
}
