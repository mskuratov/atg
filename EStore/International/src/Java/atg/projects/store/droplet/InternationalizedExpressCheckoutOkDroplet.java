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

import java.util.List;

import atg.core.util.StringUtils;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.projects.store.multisite.InternationalStoreSitePropertiesManager;
import atg.repository.RepositoryItem;

/**
 * Internationalized version of base ExpressCheckoutOkDroplet.
 * When validating user's shipping address and credit card takes also into account
 * whether default shipping and billing addresses are allowed for shipping and billing 
 * correspondingly in the international store.
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/src/atg/projects/store/droplet/InternationalizedExpressCheckoutOkDroplet.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
*/

public class InternationalizedExpressCheckoutOkDroplet extends ExpressCheckoutOkDroplet {
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/src/atg/projects/store/droplet/InternationalizedExpressCheckoutOkDroplet.java#2 $$Change: 768606 $";

  //-------------------------------
  // Constants
  //-------------------------------

  //-------------------------------
  // Properties
  //-------------------------------
  
  //-------------------------------------
  // property: StoreSitePropertiesManager
  //-------------------------------------
  private InternationalStoreSitePropertiesManager mStoreSitePropertiesManager;

  /**
   * @return the InternationalStoreSitePropertiesManager
   */
  public InternationalStoreSitePropertiesManager getStoreSitePropertiesManager() {
    return mStoreSitePropertiesManager;
  }

  /**
   * @param pStoreSitePropertiesManager the InternationalStoreSitePropertiesManager to set
   */
  public void setStoreSitePropertiesManager(InternationalStoreSitePropertiesManager pStoreSitePropertiesManager) {
    mStoreSitePropertiesManager = pStoreSitePropertiesManager;
  }

  /**
   * Checks if profile's default credit card is valid for Express Checkout.
   * It is considered valid if billing address's country is in billable countries list
   * specified in store configuration.
   * 
   * @param pProfile profile repository item
   * @return true if profile's default credit card is valid for Express Checkout
   */
  public boolean validateCreditCardForExpressCheckout(RepositoryItem pProfile) {

    String creditCardPropertyName = getStorePropertyManager().getDefaultCreditCardPropertyName();
    RepositoryItem card = (RepositoryItem) pProfile.getPropertyValue(creditCardPropertyName);
    RepositoryItem billingAddress = null;
    String countryCode = null;

    if (card != null) {
      billingAddress = (RepositoryItem) card.getPropertyValue(getStorePropertyManager().getCreditCardBillingAddressPropertyName());
      if (billingAddress != null) {
        countryCode = (String) billingAddress.getPropertyValue(getStorePropertyManager().getAddressCountryPropertyName());
      }
      
      Site currentSite = SiteContextManager.getCurrentSiteContext().getSite(); 
      InternationalStoreSitePropertiesManager propManager = getStoreSitePropertiesManager();
      
      List billableCountries = 
        (List) currentSite.getPropertyValue(propManager.getBillableCountriesPropertyName());
      List nonBillableCountries = 
        (List) currentSite.getPropertyValue(propManager.getNonBillableCountriesPropertyName());
      return isCountryValid(countryCode, billableCountries,
                            nonBillableCountries);
    }

    return false;
  }

  /**
   * Checks if profile's default shipping address is valid for Express Checkout.
   * It is considered valid if shipping address's country is in shippable countries list
   * specified in store configuration.
   * 
   * @param pProfile profile repository item
   * @return true if profile's default shipping address is valid for Express Checkout
   */
  public boolean validateShippingAddressForExpressCheckout(RepositoryItem pProfile) {
    String shippingAddressPropertyName = getStorePropertyManager().getShippingAddressPropertyName();
    RepositoryItem shippingAddress = (RepositoryItem) pProfile.getPropertyValue(shippingAddressPropertyName);
    if (shippingAddress != null) {
      String countryCode = (String) shippingAddress.getPropertyValue(getStorePropertyManager().getAddressCountryPropertyName());
      Site currentSite = SiteContextManager.getCurrentSiteContext().getSite();
      InternationalStoreSitePropertiesManager propManager = getStoreSitePropertiesManager();
      
      List shippableCountries = 
        (List) currentSite.getPropertyValue(propManager.getShippableCountriesPropertyName());
      List nonShippableCountries = 
        (List)currentSite.getPropertyValue(propManager.getNonShippableCountriesPropertyName());
      return isCountryValid(countryCode, shippableCountries,
                            nonShippableCountries);

    }
    return false;
  }

  /**
   * Helper method that checks that given country code is in list of allowed countries,
   * if list of allowed countries is not specified then checks that given country code
   * is not in the list of restricted countries.
   * 
   * @param pCountryCode country code to check
   * @param pCountryList allowed countries list
   * @param pRestrictedCountryList restricted countries list
   * @return true if country code is in list of allowed countries,if list of allowed 
   *         countries is not specified then returns true if given country code
   *         is not in the list of restricted countries.
   */
  public boolean isCountryValid(String pCountryCode, List pCountryList, List pRestrictedCountryList) {
    boolean valid = false;

    if (!StringUtils.isEmpty(pCountryCode)) {

      if (pCountryList != null && !(pCountryList.isEmpty())) {
        //Allowed country codes are specified
        if (pCountryList.contains(pCountryCode)) {
          valid = true;
        }
      } else {
        if (pRestrictedCountryList != null && !(pRestrictedCountryList.isEmpty())) {
          //Restricted country codes are specified
          if (!pRestrictedCountryList.contains(pCountryCode)) {
            valid = true;
          }
        } else {
          // both allowed and restricted country lists are not specified
          // so assume that all countries are allowed
          valid = true;
        }
      }
    }
    return valid;

  }

}
