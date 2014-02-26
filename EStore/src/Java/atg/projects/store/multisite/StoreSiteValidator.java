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



package atg.projects.store.multisite;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;

import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;
import atg.service.collections.validator.CollectionObjectValidator;

import java.util.Iterator;
import java.util.Set;

/**
 * This validator validates that a store item exists in the current site.
 *
 * @author ATG
 * @version $Revision:
 */
public class StoreSiteValidator extends GenericService implements CollectionObjectValidator {

  /**
   * Class version string.
   */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/multisite/StoreSiteValidator.java#3 $$Change: 788278 $";


  /**
   * property: sitesPropertyName
   */
  private String mSitesPropertyName = "sites";

  /**
   * @return the sitesPropertyName
   */
  public String getSitesPropertyName() {
    return mSitesPropertyName;
  }

  /**
   * @param pSitesPropertyName the sitesPropertyName to set
   */
  public void setSitesPropertyName(String pSitesPropertyName) {
    mSitesPropertyName = pSitesPropertyName;
  }

  /**
   * This method validates that a store item exists in the current site.
   *
   * @param pObject - Object to validate
   * @return true if the store item exists in the current site, false - otherwise
   */
  public boolean validateObject(Object pObject) {
    if (!(pObject instanceof RepositoryItem) ) {
      return false;
    }

    String siteId = SiteContextManager.getCurrentSiteId();

    RepositoryItem store = (RepositoryItem) pObject;

    boolean valid = false;

    // Retrieve the set of sites belonging to the store.
    Set<RepositoryItem> sites = (Set<RepositoryItem>) store.getPropertyValue(getSitesPropertyName());
    
    if (sites != null) {
      Iterator<RepositoryItem> it = sites.iterator();
      
      while (it.hasNext()) {
        RepositoryItem site = it.next();
  
        // When one of the store site values is equal to the current site id value, the store exists in the current site. 
        if (!StringUtils.isEmpty(siteId) &&
            site.getRepositoryId().equals(siteId)) {
        
          valid = true;
          break;
        }
      }
    }
    
    if (isLoggingDebug()) {
      logDebug("Store " + store + " in site = " + valid);
    }

    return valid;
  }
}
