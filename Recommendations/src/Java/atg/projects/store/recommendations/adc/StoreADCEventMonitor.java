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


package atg.projects.store.recommendations.adc;

import javax.jms.JMSException;
import javax.jms.Message;

import atg.adc.ADCEventMonitor;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

/**
 * This class extends <code>ADCEventMonitor</code> in order to store events into session data.
 * This is needed for the case when event is happen in the scope of AJAX request and can't be
 * processed in the same request. So we need to store events into the session to so that the next
 * request will be able to process them.
 *   
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Recommendations/src/atg/projects/store/recommendations/adc/StoreADCEventMonitor.java#2 $Change: 630322 $
 * @updated $DateTime: 2012/12/26 06:47:02 $Author: ykostene $
 *
 */
public class StoreADCEventMonitor extends ADCEventMonitor {
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Recommendations/src/atg/projects/store/recommendations/adc/StoreADCEventMonitor.java#2 $Change: 630322 $";
  
  //---------------------------------------------------------------------------
  // property:SessionDataPath
  // The path to session data component 
  //---------------------------------------------------------------------------
  private String mSessionDataPath;

  /**
   * The path to the session-scoped Nucleus component that holds the events for
   * the current session.
   * 
   * @param pSessionDataPath the path to the session-scoped Nucleus component
   *        that holds the events for the current session.
   **/
  public void setSessionDataPath(String pSessionDataPath) {
    mSessionDataPath = pSessionDataPath;
  }

  /**
   * Returns the path to the session scoped Nucleus component that holds the 
   * events for the current session
   * 
   * @return The path to the session scoped Nucleus component that holds the 
   *         events for the current session
   **/
  public String getSessionDataPath() {
    return mSessionDataPath;
  }
  
  /**
   * Return the EventHolder associated with the current session.
   * 
   * @return the EventHolder associated with the current session.
   */
  public StoreADCSessionData getCurrentSessionData() {
    DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();

    if(request == null)
      return null;
    
    StoreADCSessionData sessionData = (StoreADCSessionData) request.resolveName(getSessionDataPath());
    return sessionData;
  }

  /**
   * Overrides parent class method in order to store events not only into the request data holder
   * object but into the session data holder too. This allows not to loose events that happened in the
   * scope of AJAX requests.  
   */
  @Override
  public void receiveMessage(String pPortName, Message pMessage)
      throws JMSException {

    super.receiveMessage(pPortName, pMessage);
    
    // Store event into session too.
    StoreADCSessionData sessionData = getCurrentSessionData();
    
    if(sessionData != null) {
      if (isLoggingDebug())
        logDebug("Adding message " + pMessage + " to StoreADCSessionData");
      sessionData.addEvent(pMessage);
    }
    else if (isLoggingDebug())
      logDebug("StoreADCSessionData is null, message not stored");
  }  
  
}
