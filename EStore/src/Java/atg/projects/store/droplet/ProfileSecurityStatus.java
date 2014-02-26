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

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.userprofiling.Profile;
import atg.userprofiling.PropertyManager;

/**
 * This droplet checks security status of the <code>Profile</code> and 
 * displays appropriate open parameter.
 * 
 * <p>
 * Input parameters:
 * </br>
 * None.
 * </p>
 * 
 * <p>
 * Output parameters:
 * <ul>
 * <li><code>anonymous</code> - rendered when user is not recognized
 * 
 * <li><code>loggedFromCookie</code> - rendered when user is logged from cookie
 * 
 * <li><code>loggedIn</code> - rendered when user is logged in with login/password
 * 
 * <li><code>default</code> - this oparam is rendered if no other oparam has been handled
 * </ul>
 * </p>
 * <p>
 * Output parameters:
 * </br>
 * None
 * </p>
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/ProfileSecurityStatus.java#2 $
 */
public class ProfileSecurityStatus extends DynamoServlet {
  
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/ProfileSecurityStatus.java#2 $$Change: 768606 $";

  //-------------------------------------
  // Constants
  
  /**
   * Anonymous parameter name.
   */
  public static final String ANONYMOUS_OPARAM = "anonymous";
  
  /**
   * Anonymous parameter name.
   */
  public static final String AUTO_LOGGED_OPARAM = "autoLoggedIn";
 
  /**
   * Anonymous parameter name.
   */
  public static final String LOGGED_OPARAM = "loggedIn";

  /**
   * Default open parameter name
   */
  public static final String DEFAULT_OPARAM = "default";
  
  //-------------------------------------
  // Properties
  
  //-------------------------------------
  // Profile
  private Profile mProfile;

  /**
   * @return Profile object
   */
  public Profile getProfile() {
    return mProfile;
  }

  /**
   * @param pProfile Profile object
   */
  public void setProfile(Profile pProfile) {
    mProfile = pProfile;
  }
  
  //-------------------------------------
  // PropertyManager
  private PropertyManager mPropertyManager;

  /**
   * @return Profile property manager  
   */
  public PropertyManager getPropertyManager() {
    return mPropertyManager;
  }

  /**
   * @param pPropertyManager Profile property manager
   */
  public void setPropertyManager(PropertyManager pPropertyManager) {
    mPropertyManager = pPropertyManager;
  }
  
  //-------------------------------------
  // Methods
  
  /**
   * Render appropriate open parameter based on Profile's security status:
   * </br>
   * if Profile.securityStatus == 0 (ANONYMOUS) then set 'anonymous' open parameter;
   * </br>
   * if Profile.securityStatus == 2 (AUTO-SIGNIN) then set 'autoLoggedIn' open parameter;
   * </br>
   * if Profile.securityStatus == 4 (EXPLICIT-SIGNIN) then set 'loggedIn' open parameter;
   * </br>
   * set 'default' if no other oparam has been handled.
   * 
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @throws ServletException an application specific error occurred processing this request
   * @throws IOException an error occurred reading data from the request or writing data to the response.
   */
  public void service(DynamoHttpServletRequest pRequest, 
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    
    // retrieve security status
    Integer profileSecurityStatus = 
      (Integer) getProfile().getPropertyValue(getPropertyManager().getSecurityStatusPropertyName());
    int profileStatusValue = profileSecurityStatus.intValue();

    boolean handled = false;

    // security status is enumerated property check possible values
    if (profileStatusValue == 0) {
      handled = pRequest.serviceLocalParameter(ANONYMOUS_OPARAM, pRequest,
          pResponse);
    } else if (profileStatusValue == 2) {
      handled = pRequest.serviceLocalParameter(AUTO_LOGGED_OPARAM, pRequest,
          pResponse);
    } else if (profileStatusValue == 4) {
      handled = pRequest.serviceLocalParameter(LOGGED_OPARAM, pRequest,
          pResponse);
    }

    // if no other oparams has been handled, render 'default'
    if (!handled) {
      pRequest.serviceLocalParameter(DEFAULT_OPARAM, pRequest, pResponse);
    }
  }
}
