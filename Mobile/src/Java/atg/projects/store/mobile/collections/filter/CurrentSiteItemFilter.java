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

package atg.projects.store.mobile.collections.filter;

import atg.service.collections.filter.CachedCollectionFilter;
import atg.service.collections.filter.FilterException;
import atg.multisite.SiteManager;
import atg.multisite.SiteContextManager;
import atg.multisite.Site;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryException;

import java.util.*;

/**
 * Implementation of {@link CachedCollectionFilter} which filters input products collection by their site IDs.
 * This filter will return only products whose site IDs include current site
 */
public class CurrentSiteItemFilter extends CachedCollectionFilter {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Mobile/src/atg/projects/store/mobile/collections/filter/CurrentSiteItemFilter.java#3 $$Change: 768606 $";


  public static final String SHOULD_APPLY_FILTER = "applyFilter";

  //-------------------------------------
  // property: siteManager
  //-------------------------------------
  protected SiteManager mSiteManager;

  /**
   * This property contains a reference to {@link SiteManager} to be used when determining active sites.
   *
   * @return SiteManager instance
   */
  public SiteManager getSiteManager() {
    return mSiteManager;
  }

  public void setSiteManager(SiteManager pSiteManager) {
    mSiteManager = pSiteManager;
  }

  //-------------------------------------
  // property: sitesPropertyName
  //-------------------------------------
  protected String mSitesPropertyName;

  /**
   * This property contains the name of the property that holds references to an item's sites.
   *
   * @return 'siteIds' property name
   */
  public String getSitesPropertyName() {
    return mSitesPropertyName;
  }

  public void setSitesPropertyName(String pSitesPropertyName) {
    mSitesPropertyName = pSitesPropertyName;
  }

  /**
   * This method filters the passed in collection by the current site.
   *
   * @param pUnfilteredCollection    the collection to be filtered
   * @param pCollectionIdentifierKey unused
   *                                 pProfile pCollectionIdentifierKey unused
   * @return a collection items that have the current site
   * @throws atg.service.collections.filter.FilterException
   *          if the item does not have a sites property
   */
  @Override
  public Collection generateFilteredCollection(Collection pUnfilteredCollection, String pCollectionIdentifierKey, RepositoryItem pProfile)
    throws FilterException
  {
    return generateFilteredCollection(pUnfilteredCollection, pCollectionIdentifierKey, pProfile, null);
  }

  @Override
  protected Collection generateFilteredCollection(Collection pUnfilteredCollection, String pCollectionIdentifierKey, RepositoryItem pProfile,
                                                  Map pExtraParameters) throws FilterException
  {
    //If the sites property name is not entered then just return the unfiltered collection
    if (getSitesPropertyName() == null)
      return pUnfilteredCollection;

    //Return unfiltered collection if we have no current site
    Site currentSite = SiteContextManager.getCurrentSite();
    if (currentSite == null) {
      return pUnfilteredCollection;
    }

    //The result
    Collection<RepositoryItem> resultCollection = new HashSet<RepositoryItem>();

    for (Iterator<RepositoryItem> iterator = pUnfilteredCollection.iterator(); iterator.hasNext();) {
      RepositoryItem item = iterator.next();

      Collection<String> itemSites = null;

      try {
        itemSites = (Collection<String>) item.getPropertyValue(getSitesPropertyName());
      } catch (IllegalArgumentException ex) {
        //If the item does not have the site property throw a FilterException
        throw new FilterException(ex);
      }

      if (itemSites != null && itemSites.contains(currentSite.getId())) {
        resultCollection.add(item);
      }
    }

    return resultCollection;
  }

  @Override
  public boolean shouldApplyFilter(Collection pUnfilteredCollection, String pCollectionIdentifierKey, RepositoryItem pProfile)
  {
    return shouldApplyFilter(pUnfilteredCollection, pCollectionIdentifierKey, pProfile, null);
  }

  @Override
  public boolean shouldApplyFilter(Collection pUnfilteredCollection, String pCollectionIdentifierKey, RepositoryItem pProfile, Map pExtraParameters)
  {
    if (pExtraParameters == null)
    {
      return true;
    }
    String applyFilter = (String)pExtraParameters.get(SHOULD_APPLY_FILTER);
    return "true".equalsIgnoreCase(applyFilter);
  }
}
