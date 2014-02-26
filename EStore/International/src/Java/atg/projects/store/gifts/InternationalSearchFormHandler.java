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


package atg.projects.store.gifts;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.projects.store.multisite.InternationalStoreSitePropertiesManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * Store extension of the SearchFormHandler from DCS. Filters gift lists with non-shippable addresses.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/src/atg/projects/store/gifts/InternationalSearchFormHandler.java#3 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class InternationalSearchFormHandler extends StoreSearchFormHandler {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/src/atg/projects/store/gifts/InternationalSearchFormHandler.java#3 $$Change: 788278 $";

  //-------------------------------
  // Constants
  //-------------------------------
  private static final String SHIPPING_ADDRESS = "shippingAddress";  
  private static final String COUNTRY = "country";
  
  //-------------------------------
  // Properties
  //-------------------------------
    
  /**
   * property: storeSitePropertiesManager
   */

  protected InternationalStoreSitePropertiesManager mStoreSitePropertiesManager;

  /**
   * @return the InternationalStoreSitePropertiesManager.
   */
  public InternationalStoreSitePropertiesManager getStoreSitePropertiesManager() {
    return mStoreSitePropertiesManager;
  }

  /**
   * @param pStoreSitePropertiesManager - the InternationalStoreSitePropertiesManager.
   */
  public void setStoreSitePropertiesManager(InternationalStoreSitePropertiesManager pStoreSitePropertiesManager) {
    mStoreSitePropertiesManager = pStoreSitePropertiesManager;
  }
  
  /**
   * Remove any gift list whose shipping addresses are non-shippable from the current country store..
   *
   * @param pDynamoHttpServletRequest - HTTP request.
   * @param pDynamoHttpServletResponse - HTTP response.
   * 
   * @throws ServletException if there was an error while executing the code.
   * @throws IOException if there was an error with servlet io.
   */
  @Override
  public void postSearch(DynamoHttpServletRequest pDynamoHttpServletRequest, 
                         DynamoHttpServletResponse pDynamoHttpServletResponse) 
    throws javax.servlet.ServletException, IOException {
    
    super.postSearch(pDynamoHttpServletRequest, pDynamoHttpServletResponse);

    Site currentSite = SiteContextManager.getCurrentSiteContext().getSite();
    InternationalStoreSitePropertiesManager propertiesManager = getStoreSitePropertiesManager();
    
    // Get list of shippable and non-shippable countries.
    List shippableCountries = 
        (List) currentSite.getPropertyValue(propertiesManager.getShippableCountriesPropertyName());
    List nonShippableCountries = 
        (List) currentSite.getPropertyValue(propertiesManager.getNonShippableCountriesPropertyName());

    Collection giftlists = getSearchResults();

    if(giftlists != null && giftlists.size() > 0) {
      
      for (Iterator iterator = giftlists.iterator(); iterator.hasNext();) {
        // Check giftlist's shipping addresses
        RepositoryItem shippingAddress = (RepositoryItem) 
          ((RepositoryItem) iterator.next()).getPropertyValue(SHIPPING_ADDRESS);

        if (shippingAddress == null) {
          // Current gift list has no shipping address, just do nothing (as requested by CSC team - 
          // they do not filter gift lists by shippable countries and we do not have gift lists without 
          // addresses). This does not impact our functionality.
          continue;
        }
        
        String country = (String) shippingAddress.getPropertyValue(COUNTRY);

        // Check in non-shippable list.
        if (nonShippableCountries != null && nonShippableCountries.size() > 0 && 
           nonShippableCountries.contains(country)) {
          
          if (isLoggingDebug()) {
            logDebug("Country [" + country + "] is non-shippable for this giftlist");
          }
          
          iterator.remove();
          
          // Go to next iteration step to prevent double removal of current gift list.
          continue;         
        }

        // Check in shippable list.
        if (shippableCountries != null && shippableCountries.size() > 0 && 
            !shippableCountries.contains(country)) {
          
          if (isLoggingDebug()) {
            logDebug("Country [" + country + "] is non-shippable for this giftlist");
          }
          
          iterator.remove();         
        }
      }
    }
  }
  
}
