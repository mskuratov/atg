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

import atg.projects.store.profile.StoreProfileTools;

import atg.repository.MutableRepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import atg.userprofiling.ProfileTools;

import java.io.IOException;

import javax.servlet.ServletException;


/**
* <p>
* Given an emailAddress, this droplet will call into
* StoreProfileTools.retrieveEmailRecipient(). If a valid item is
* returned, the droplet renders a "true" oparam, otherwise, "false".
* <p>
* This droplet takes the following input parameters:
* <ul>
* <li>email - The email address of the current Profile
* </ul>
* <p>
* This droplet renders the following oparams:
* <ul>
* <li>true - if current Profile has already subscribed to receive emails
* <li>false - if current Profile has not subscribed to receive emails
* </ul>
* <p>
* Example:
*
* <PRE>
*
* &lt;dsp:droplet bean="/atg/store/droplet/IsEmailRecipient"&gt; &lt;dsp:param
* name="email" bean="RegistrationFormHandler.value.email"&gt; &lt;dsp:oparam name="true"&gt;
* * &lt;/dsp:oparam&gt;  &lt;dsp:oparam name="false"&gt; &lt;/dsp:oparam&gt;&lt;/dsp:droplet&gt;
*
* </PRE>
*
* @author ATG
* @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/IsEmailRecipient.java#2 $$Change: 768606 $
*
*/
public class IsEmailRecipient extends DynamoServlet {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/IsEmailRecipient.java#2 $$Change: 768606 $";



  /** The input parameter name for the email address to check. */
  public static final ParameterName PARAM_EMAIL = ParameterName.getParameterName("email");

  /** The oparam name rendered once if current Profile has already subscribed to receive emails.*/
  public static final ParameterName OPARAM_OUTPUT_TRUE = ParameterName.getParameterName("true");

  /** The oparam name rendered once if current Profile has not subscribed to receive emails.*/
  public static final ParameterName OPARAM_OUTPUT_FALSE = ParameterName.getParameterName("false");

  /**
   * Profile tools.
   */
  protected ProfileTools mProfileTools;

  /**
   * @return the profileTools.
   */
  public ProfileTools getProfileTools() {
    return mProfileTools;
  }

  /**
   * @param pProfileTools - The profileTools to set.
   */
  public void setProfileTools(ProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }

  /**
   * Given an email address, this will call into
   * StoreProfileTools.retrieveEmailRecipient() to determine
   * if the address is already subscribed to receive emails or not.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Object email = pRequest.getObjectParameter(PARAM_EMAIL);

    if ((email == null) || !(email instanceof String)) {
      if (isLoggingDebug()) {
        logDebug("INVALID PARAM: invalid or no email address supplied");
      }

      return;
    }

    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    MutableRepositoryItem emailRecipient = (MutableRepositoryItem) profileTools.retrieveEmailRecipient((String) (email));

    if (emailRecipient != null) {
      pRequest.serviceLocalParameter(OPARAM_OUTPUT_TRUE, pRequest, pResponse);
    } else {
      pRequest.serviceLocalParameter(OPARAM_OUTPUT_FALSE, pRequest, pResponse);
    }
  } 
}
