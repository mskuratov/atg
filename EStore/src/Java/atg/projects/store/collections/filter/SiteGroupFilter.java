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



package atg.projects.store.collections.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import atg.core.util.StringUtils;
import atg.multisite.SiteGroupManager;
import atg.multisite.SiteManager;
import atg.repository.RepositoryItem;
import atg.service.collections.filter.CachedCollectionFilter;
import atg.service.collections.filter.FilterException;

/**
 * <p>
 * Filters a collection of (repository) items on their sites/siteGroup
 * properties.
 * </p>
 * 
 * <p>
 * A Set of the (repository) items site ids and siteGroups site ids is 
 * constructed and compared to the site ids of the current sites 
 * mShareableTypeId siteGroup site ids. If any sites from the items Set match
 * then the item will be returned in the filtered collection.
 * </p>
 * 
 * @author ATG
 */
public class SiteGroupFilter extends CachedCollectionFilter{

  /** Class version */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/collections/filter/SiteGroupFilter.java#2 $$Change: 768606 $";

  //-----------------------------------
  // PROPERTIES
  //-----------------------------------
  
  //-------------------------------------
  // property: shareableTypeId
  protected String mShareableTypeId;

  /**
   * This property contains a shareable ID to be used when determining sharing sites.
   * @return current shareable type ID
   */
  public String getShareableTypeId() {
    return mShareableTypeId;
  }

  /**
   * Sets the mSharableTypeId
   * @param pShareableTypeId SharableTypeId to set
   */
  public void setShareableTypeId(String pShareableTypeId) {
    mShareableTypeId = pShareableTypeId;
  }
  
  //-------------------------------------
  // property: sitesPropertyName
  protected String mSitesPropertyName;

  /**
   * This property contains the name of the property that holds references to an item's sites.
   * @return 'mSitesPropertyName' property name
   */
  public String getSitesPropertyName() {
    return mSitesPropertyName;
  }

  /**
   * Sets the mSitesPropertyName
   * @param pSitesPropertyName Value to set
   */
  public void setSitesPropertyName(String pSitesPropertyName) {
    mSitesPropertyName = pSitesPropertyName;
  }
  
  //-------------------------------------
  // property: siteGroupPropertyName
  protected String mSiteGroupPropertyName;

  /**
   * This property contains the name of the property that holds references to an item's siteGroups.
   * @return 'mSiteGroupPropertyName' property name
   */
  public String getSiteGroupPropertyName() {
    return mSiteGroupPropertyName;
  }

  /**
   * Sets the mSiteGroupPropertyName
   * @param pSiteGroupPropertyName Value to set
   */
  public void setSiteGroupPropertyName(String pSiteGroupPropertyName) {
    mSiteGroupPropertyName = pSiteGroupPropertyName;
  }
  
  //-------------------------------------
  // property: idPropertyName
  protected String mIdPropertyName;

  /**
   * This property contains the name of the property that holds references to an item's id.
   * @return 'mIdPropertyName' property name
   */
  public String getIdPropertyName() {
    return mIdPropertyName;
  }
 
  /**
   * Sets the mIdPropertyName
   * @param pIdPropertyName Value to set
   */
  public void setIdPropertyName(String pIdPropertyName) {
    mIdPropertyName = pIdPropertyName;
  }
  
  
  //-------------------------------------
  // property: siteGroupManager
  protected SiteGroupManager mSiteGroupManager;

  /**
   * This property contains a reference to {@link SiteGroupManager} to be used when determining sharing sites.
   * @return SiteGroupManager instance
   */
  public SiteGroupManager getSiteGroupManager() {
    return mSiteGroupManager;
  }

  
  /**
   * Sets the mSiteGroupManager
   * @param pSiteGroupManager Value to set
   */
  public void setSiteGroupManager(SiteGroupManager pSiteGroupManager) {
    mSiteGroupManager = pSiteGroupManager;
  }

  //-------------------------------------
  // property: siteManager
  protected SiteManager mSiteManager;

  /**
   * This property contains a reference to {@link SiteManager} to be used when determining active sites.
   * @return SiteManager instance
   */
  public SiteManager getSiteManager() {
    return mSiteManager;
  }

  /**
   * Sets the mSiteManager
   * @param pSiteManager Value to set
   */
  public void setSiteManager(SiteManager pSiteManager) {
    mSiteManager = pSiteManager;
  }
  
  //-------------------------------------
  // property: includeDisabledSites
  protected boolean mIncludeDisabledSites;

  /**
   * Returns the includeDisabledSites property.
   * 
   * @return the includeDisabledSites property.
   */
  public boolean isIncludeDisabledSites() {
    return mIncludeDisabledSites;
  }

  /**
   * Sets the includeDisabledSites property.
   * 
   * @param pIncludeDisabledSites whether or not to filter out items that exist only
   * on disabled sites
   */
  public void setIncludeDisabledSites(boolean pIncludeDisabledSites) {
    mIncludeDisabledSites = pIncludeDisabledSites;
  }
  
  //-------------------------------------
  // property: includeInactiveSites
  /**
   * mIncludeInactiveSites; a value of <code>true</code> indicates that items should be
   * retained in the collection even if all their sites are inactive
   */
  protected boolean mIncludeInactiveSites;

  /**
   * Returns the includeInactiveSites property.
   * 
   * @return the includeInactiveSites property.
   */
  public boolean isIncludeInactiveSites() {
    return mIncludeInactiveSites;
  }

  /**
   * Sets the includeInactiveSites property.
   * 
   * @param pIncludeInactiveSites whether or not to filter out items that exist only
   * on inactive sites 
   */
  public void setIncludeInactiveSites(boolean pIncludeInactiveSites) {
    mIncludeInactiveSites = pIncludeInactiveSites;
  }
  
  //-----------------------------------
  // property: mAllowAllSite
  private boolean mAllowAllSite;
  
  /**
   * @return allowAllSite boolean
   */
  public boolean isAllowAllSite() {
    return mAllowAllSite;
  }

  /**
   * If set to true and there are no sites/siteGroups configured
   * for a particular item in the collection to be filtered, the
   * item is treated as all-site and will be returned in the filtered
   * collection.
   * 
   * @param pAllowAllSite Indicates whether or not to return an item
   * if it has no sites/siteGroups configured
   */
  public void setAllowAllSite(boolean pAllowAllSite) {
    mAllowAllSite = pAllowAllSite;
  }

  //-----------------------------------
  // METHODS
  //-----------------------------------
  
  /**
   * This method filters the passed in collection (of repository items) based
   * on whether or not any of its items sites/siteGroups.sites exist in the
   * current mShareableTypeId siteGroup
   * 
   * @param pUnfilteredCollection the collection to be filtered
   * @param pCollectionIdentifierKey unused
   * @param pProfile unused
   * 
   * @return a collection items that have the current site or siteGroup
   * 
   * @throws FilterException if the item does not have a sites property
   */
  @Override
  public Collection generateFilteredCollection(Collection pUnfilteredCollection, String pCollectionIdentifierKey, RepositoryItem pProfile)
      throws FilterException {
    
    //If the sites property name is not entered then just return the unfiltered collection
    if (getSitesPropertyName() == null){
      return pUnfilteredCollection;
    }

    //The result
    Collection<RepositoryItem> resultCollection = new HashSet<RepositoryItem>();

    // Current sharing sites, there should remain items from these sites only
    Collection<String> sharingSiteIds = getSiteGroupManager().getSharingSiteIds(getShareableTypeId());

    // sharingSiteIds includes the current site. It's null if there is no current site or if
    // the configured shareable type isn't registered (e.g., the customer has unregistered the
    // ShoppingCart shareable type to say that all sites can share the cart). In either case,
    // we return all items in the unfiltered collection.
    if (sharingSiteIds == null) {
      return pUnfilteredCollection;
    }

    // If we're supposed to filter out items that exist only on disabled sites or only on inactive sites,
    // then remove the sites from consideration before we start looking at the items.
    if (!isIncludeDisabledSites() || !isIncludeInactiveSites()) {
      String[] siteArray = sharingSiteIds.toArray(new String[0]);
      if (!isIncludeDisabledSites()) {
        siteArray = getSiteManager().filterDisabledSites(siteArray);
      }
      if (!isIncludeInactiveSites()) {
        siteArray = getSiteManager().filterInactiveSites(siteArray);
      }
      // If all the sites in the group are gone, filter out all the items.
      if (siteArray.length == 0) {
        return new HashSet();
      }
      sharingSiteIds = Arrays.asList(siteArray);
    }
    
    // Perform the filtering
    for (Iterator<RepositoryItem> iterator = pUnfilteredCollection.iterator(); iterator.hasNext();) {
      RepositoryItem item = iterator.next();
      
      // Merged siteIds set
      Set combinedSiteIds = new HashSet();
      
      // The items siteIds
      Set itemSiteIds = getSiteIds(item);
      if(itemSiteIds != null && itemSiteIds.size() > 0){
        combinedSiteIds.addAll(itemSiteIds);
      }
      
      // The items siteGroup siteIds
      Set itemSiteGroupSiteIds = getSiteGroupSiteIds(item);
      if(itemSiteGroupSiteIds != null && itemSiteGroupSiteIds.size() > 0){
        combinedSiteIds.addAll(itemSiteGroupSiteIds);
      }

      // All-site
      if(isAllowAllSite() && combinedSiteIds.size() == 0){
        resultCollection.add(item);
        continue;
      }
           
      //Now see if this items sites exists in the sharingSiteIds
      if (!Collections.disjoint(combinedSiteIds, sharingSiteIds)) {
        resultCollection.add(item);
      }
    }
    return resultCollection;
  }
  
  /**
   * Gets the siteIds property from the passed in RepositoryItem
   * and returns them in a Set.
   * 
   * @param pItem A RepositoryItem
   * @return A Set of pItems siteIds
   */
  protected Set<String> getSiteIds(RepositoryItem pItem){
    if(pItem == null || StringUtils.isEmpty(getSitesPropertyName())){
      return null;
    }
    
    // A collection of Site RepositoryItems
    Collection sites = null;
    
    try{
      sites = (Collection)pItem.getPropertyValue(getSitesPropertyName());
    }
    catch(Exception e){
      if(isLoggingError()){
        logError("Could not get property " + getSitesPropertyName()
            + " from repository item " + pItem, e);
      }
    }
    
    if(sites == null){
      return null;
    }
    
    // Create our result set from the collection of sites
    Set<String> siteIds = new HashSet<String>(sites.size());
    for(Object site : sites){
      if(site instanceof RepositoryItem){
        String siteId = (String) ((RepositoryItem)site).getPropertyValue(getIdPropertyName());
        
        if(siteId == null){
          continue;
        }
        siteIds.add(siteId);
      }else{
        // check whether sites collection consists of site IDs 
        if (site instanceof String){
          siteIds.add((String)site);
        }
      }
    }
    return siteIds;
  }
  
  /**
   * Gets the siteIds from all pItems siteGroups and returns them in 
   * a Set.
   * 
   * @param pItem A RepositoryItem
   * @return A Set of siteIds constructed from the siteGroups of pItem
   */
  protected Set<String> getSiteGroupSiteIds(RepositoryItem pItem){
    if(pItem == null || StringUtils.isEmpty(getSiteGroupPropertyName())){
      return null;
    }
    
    // A collection of SiteGroup repository Items
    Collection siteGroups = null;
    
    try{
      siteGroups = (Collection)pItem.getPropertyValue(getSiteGroupPropertyName());
    }
    catch(Exception e){
      if(isLoggingError()){
        logError("Could not get property " + getSiteGroupPropertyName()
            + " from repository item " + pItem, e);
      }
    }
    
    if(siteGroups == null){
      return null;
    }
    
    // Create our result set from the collection of siteGroups
    Set<String> siteIds = new HashSet();    
    for(Object site : siteGroups){
      if(site instanceof RepositoryItem){
        Set<String> currentSiteId = getSiteIds((RepositoryItem)site);
        if(currentSiteId != null){
          siteIds.addAll(currentSiteId);
        }
      }
    }
    return siteIds;
  }
}
