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

import atg.projects.store.profile.StorePropertyManager;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;

import javax.servlet.ServletException;


/**
 * Checks to see if this user can use ExpressCheckout or not.
 *
 * @author ATG
 * @version $Revision: #2 $
 */
public class ExpressCheckoutOkDroplet extends DynamoServlet {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/ExpressCheckoutOkDroplet.java#2 $$Change: 768606 $";

  /** Input parameter name profile. */
  public static final ParameterName PROFILE = ParameterName.getParameterName("profile");

  /** Oparam true. */
  public static final ParameterName TRUE = ParameterName.getParameterName("true");

  /** Oparam false. */
  public static final ParameterName FALSE = ParameterName.getParameterName("false");

  /**
   * Store property manager.
   */
  private StorePropertyManager mStorePropertyManager;

  /**
   * @return the mStore property manager.
   */
  public StorePropertyManager getStorePropertyManager() {
    return mStorePropertyManager;
  }

  /**
   * @param pStorePropertyManager - the mStorePropertyManager to set.
   */
  public void setStorePropertyManager(StorePropertyManager pStorePropertyManager) {
    mStorePropertyManager = pStorePropertyManager;
  }

  /**
   * Services true oparam if user can use express checkout, false if not.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Object profileItem = pRequest.getObjectParameter(PROFILE);
    StorePropertyManager pmgr = getStorePropertyManager();

    if ((profileItem == null) || !(profileItem instanceof RepositoryItem)) {
      if (isLoggingDebug()) {
        logDebug("Bad profile parameter passed: null=" + (profileItem == null) +
          ". If null=false, then wrong object type.");
      }

      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);

      return;
    }

    RepositoryItem profile = (RepositoryItem) profileItem;
    
    // Transient (guest) users can't use express checkout option
    if (profile.isTransient())
    {
      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
      return;
    }

    if (isLoggingDebug()) {
      logDebug("Default shipping address name: " + pmgr.getShippingAddressPropertyName());
    }

    // Validate default shipping address for Express Checkout
    if (!validateShippingAddressForExpressCheckout(profile)) {
      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
      return;
    }

    // Validate default credit card for Express Checkout
    if (!validateCreditCardForExpressCheckout(profile)) {
      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
      return;
    }

    // Return false if default shipping method not set
    String defaultShippingMethod = (String) profile.getPropertyValue(pmgr.getDefaultShippingMethodPropertyName());

    if ((defaultShippingMethod == null) || (defaultShippingMethod.trim().length() == 0)) {
      if (isLoggingDebug()) {
        logDebug("User's " + pmgr.getDefaultShippingMethodPropertyName() + " is empty");
      }

      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);

      return;
    }

    // If all the above checks were successful, then user can use
    //  Express checkout.
    pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);
  }
  
  /**
   * Validates profile's default shipping address for Express Checkout.
   * 
   * @param pProfile profile repository item
   * @return true if profile's default shipping address is valid for Express Checkout
   */
  public boolean validateShippingAddressForExpressCheckout(RepositoryItem pProfile) {
    // Return false if default shipping not set
    String shippingAddressPropertyName = getStorePropertyManager().getShippingAddressPropertyName();
    if (pProfile.getPropertyValue(shippingAddressPropertyName) == null) {
      if (isLoggingDebug()) {
        logDebug("User's " + shippingAddressPropertyName + " is null");
      }

      return false;
    }
    return true;
  }

  /**
   * Validates profile's default credit card for Express Checkout.
   * 
   * @param pProfile profile repository item
   * @return true if profile's default credit card is valid for Express Checkout
   */
  public boolean validateCreditCardForExpressCheckout(RepositoryItem pProfile) {
    // Return false if default cc not set
    String defaultCreditCardPropertyName = getStorePropertyManager().getDefaultCreditCardPropertyName();
    if (pProfile.getPropertyValue(defaultCreditCardPropertyName) == null) {
      if (isLoggingDebug()) {
        logDebug("User's " + defaultCreditCardPropertyName + " is null");
      }
      return false;
    }
    return true;
  }
  
  
}
