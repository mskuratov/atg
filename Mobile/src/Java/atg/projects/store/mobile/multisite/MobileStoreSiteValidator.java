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



package atg.projects.store.mobile.multisite;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import atg.multisite.*;
import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;
import atg.service.collections.validator.CollectionObjectValidator;

/**
 * This validator validates that a store item exists in the current site, or
 * the desktop version of the current mobile site
 *
 * @author ATG
 * @version $Revision: #3 $
 */
public class MobileStoreSiteValidator extends GenericService implements CollectionObjectValidator {

  /**
   * Class version string.
   */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Mobile/src/atg/projects/store/mobile/multisite/MobileStoreSiteValidator.java#3 $$Change: 788278 $";

  /** "channel" property name from Site configuration. */
  public static final String PROP_CHANNEL = "channel";
  /** "channel" property value: mobile. */
  public static final String PROP_VALUE_CHANNEL_MOBILE = "mobile";
  /** SiteIdPropertyName. */
  private String mSitesPropertyName = "sites";

  /**
   * @return the SiteIdPropertyName
   */
  public String getSitesPropertyName() {
    return mSitesPropertyName;
  }

  /**
   * @param pSitesPropertyName the SiteIdPropertyName to set
   */
  public void setSitesPropertyName(String pSitesPropertyName) {
    mSitesPropertyName = pSitesPropertyName;
  }

  //-------------------------------------
  // property: siteGroupManager
  //
  private SiteGroupManager mSiteGroupManager;
  /**
   * This property contains a reference to {@link SiteGroupManager} to be used when determining sharing sites.
   * @return SiteGroupManager instance.
   */
  public SiteGroupManager getSiteGroupManager() {
    return mSiteGroupManager;
  }
  /**
   * Sets the new reference to {@link SiteGroupManager} to be used when determining sharing sites.
   * @param pSiteGroupManager Value to set.
   */
  public void setSiteGroupManager(SiteGroupManager pSiteGroupManager) {
    mSiteGroupManager = pSiteGroupManager;
  }

  //-------------------------------------
  // property: shareableTypeIdForMobileSitePairs
  //
  // ID of ShareableType to use to lookup for a site pair.
  private String mShareableTypeIdForMobileSitePairs;
  /**
   * Gets the "ShareableType ID for mobile site pairs" property.
   * @return The "ShareableType ID for mobile site pairs" property.
   */
  public String getShareableTypeIdForMobileSitePairs() {
    return mShareableTypeIdForMobileSitePairs;
  }
  /**
   * Sets the "ShareableType ID for mobile site pairs" property.
   * @param pShareableTypeIdForMobileSitePairs The "ShareableType ID for mobile site pairs" property.
   */
  public void setShareableTypeIdForMobileSitePairs(String pShareableTypeIdForMobileSitePairs) {
    mShareableTypeIdForMobileSitePairs = pShareableTypeIdForMobileSitePairs;
  }


  /**
   * This method validates that a store item exists in the current site, or
   * the desktop version of the current mobile site
   *
   * @param pObject Object to validate
   * @return true if the store item exists in the current site, false - otherwise
   */
  public boolean validateObject(Object pObject) {
    if (!(pObject instanceof RepositoryItem) ) {
      return false;
    }

    Site currentSite = SiteContextManager.getCurrentSite();

    RepositoryItem store = (RepositoryItem) pObject;

    boolean valid = false;

    Set<RepositoryItem> storeSites = (Set<RepositoryItem>)store.getPropertyValue(getSitesPropertyName());
    if (collectionContainsObject(storeSites, currentSite)) {
      valid = true;
    } else {
      Site currentNonMobileSite = null;
      ShareableType shareableType = getSiteGroupManager().getShareableTypeById(getShareableTypeIdForMobileSitePairs());
      Collection<Site> sharingSites = getSiteGroupManager().getOtherSharingSites(currentSite, shareableType);
      if (sharingSites.size() > 1) {
        vlogWarning("There are more than one site sharing {0} with the current site. The first site from the collection will be used",
                    getShareableTypeIdForMobileSitePairs());
      }
      Iterator<Site> iter = sharingSites.iterator();
      while (iter.hasNext() && currentNonMobileSite == null) {
        currentNonMobileSite = iter.next();
        vlogDebug("Got a paired site \"{0}\" (is supposed to be a non-mobile one)", currentNonMobileSite);
      }
      try {
        String channelProperty = (String)currentNonMobileSite.getPropertyValue(PROP_CHANNEL);
        if (PROP_VALUE_CHANNEL_MOBILE.equals(channelProperty)) {
          vlogError("Bad configuration for a paired site \"{0}\": it's a mobile one", currentNonMobileSite);
        } else {
          if (collectionContainsObject(storeSites, currentNonMobileSite)) {
            valid = true;
          }
        }
      } catch (Exception ex) {
        vlogError("There was a problem retrieving site " + currentNonMobileSite + ".\n", ex);
      }
    }

    if (isLoggingDebug()) {
      vlogDebug("Store " + store + " in site = " + valid);
    }

    return valid;
  }

  private boolean collectionContainsObject(Collection<RepositoryItem> pCollection, RepositoryItem pObject) {
    for (RepositoryItem item: pCollection) {
      if (item.getRepositoryId().equals(pObject.getRepositoryId())) {
        return true;
      }
    }
    return false;
  }
}
