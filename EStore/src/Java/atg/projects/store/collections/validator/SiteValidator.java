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
package atg.projects.store.collections.validator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import atg.core.util.StringUtils;
import atg.multisite.SiteGroupManager;
import atg.multisite.SiteManager;
import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;
import atg.service.collections.validator.CollectionObjectValidator;

/**
 * SiteValidator validates an item based on its sites/siteGroup/siteIds properties.
 * 
 * siteGroup and sites properties could be configured for promotional items, and
 * siteIds property could be configured for products.
 *
 * <p>
 * A Set of the (repository) items site ids and siteGroups site ids is 
 * constructed and compared to the site ids of the current sites 
 * mShareableTypeId siteGroup site ids. If any sites from the items Set match
 * then the item will be returned.
 * </p>
 * * 
 * @author Yekaterina Kostenevich
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/collections/validator/SiteValidator.java#2 $Change: 630322 $
 * @updated $DateTime: 2013/02/19 09:03:40 $Author: ykostene $
 *
 */
public class SiteValidator extends GenericService implements CollectionObjectValidator{
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/collections/validator/SiteValidator.java#2 $Change: 630322 $";

  /**
   * property: shareableTypeId
   */
  protected String mShareableTypeId;

  /**
   * @return shareable ID to be used when determining sharing sites.
   */
  public String getShareableTypeId() {
    return mShareableTypeId;
  }

  /**
   * @param pShareableTypeId SharableTypeId to set
   */
  public void setShareableTypeId(String pShareableTypeId) {
    mShareableTypeId = pShareableTypeId;
  }
  
  /**
   * property: SiteIdsPropertyName
   */
  protected String mSiteIdsPropertyName;

  /**
   * @return 'mSitesPropertyName' that contains the name of the property 
   * that holds references to an item's sites. 
   */
  public String getSiteIdsPropertyName() {
    return mSiteIdsPropertyName;
  }

  /**
   * @param pSitesPropertyName value to set
   */
  public void setSiteIdsPropertyName(String pSiteIdsPropertyName) {
    mSiteIdsPropertyName = pSiteIdsPropertyName;
  }
  
  /**
   * property: sitesPropertyName
   */
  protected String mSitesPropertyName;

  /**
   * @return mSitesPropertyName, that contains the name of the property 
   * that holds references to an item's sites.
   */
  public String getSitesPropertyName() {
    return mSitesPropertyName;
  }

  /**
   * @param pSitesPropertyName value to set
   */
  public void setSitesPropertyName(String pSitesPropertyName) {
   mSitesPropertyName = pSitesPropertyName;
  }
  
  /**
   * property: siteGroupPropertyName
   */
  protected String mSiteGroupPropertyName;

  /**
   * @return mSiteGroupPropertyName, that  contains the name of the property 
   * that holds references to an item's siteGroups.
   */
  public String getSiteGroupPropertyName() {
    return mSiteGroupPropertyName;
  }

  /**
   * @param pSiteGroupPropertyName value to set
   */
  public void setSiteGroupPropertyName(String pSiteGroupPropertyName) {
    mSiteGroupPropertyName = pSiteGroupPropertyName;
  }
   
  /**
   * property: siteGroupManager
   */
  protected SiteGroupManager mSiteGroupManager;

  /**
   * @return SiteGroupManager instance, that contains a reference 
   * to {@link SiteGroupManager} to be used when determining sharing sites.
   */
  public SiteGroupManager getSiteGroupManager() {
    return mSiteGroupManager;
  }
  
  /**
   * @param pSiteGroupManager value to set
   */
  public void setSiteGroupManager(SiteGroupManager pSiteGroupManager) {
    mSiteGroupManager = pSiteGroupManager;
  }

  /**
   * property: siteManager
   */
  protected SiteManager mSiteManager;

  /**
   * @return SiteManager instance, that contains a reference 
   * to {@link SiteManager} to be used when determining active sites.
   */
  public SiteManager getSiteManager() {
    return mSiteManager;
  }

  /**
   * @param pSiteManager value to set
   */
  public void setSiteManager(SiteManager pSiteManager) {
    mSiteManager = pSiteManager;
  }
  
  /**
   * property: includeDisabledSites   
   */
  protected boolean mIncludeDisabledSites;

  /**
   * @return the includeDisabledSites property, that indicates 
   * if items from disabled sites should pass validation
   */
  public boolean isIncludeDisabledSites() {
    return mIncludeDisabledSites;
  }

  /**
   * @param pIncludeDisabledSites whether or not to filter out items that exist only
   * on disabled sites
   */
  public void setIncludeDisabledSites(boolean pIncludeDisabledSites) {
    mIncludeDisabledSites = pIncludeDisabledSites;
  }
  
  /**
   * property: includeInactiveSites 
   */  
  protected boolean mIncludeInactiveSites;

  /**
   * @return the includeInactiveSites property, that indicates if items 
   * from inactive sites should pass validation
   */
  public boolean isIncludeInactiveSites() {
    return mIncludeInactiveSites;
  }

  /**
   * @param pIncludeInactiveSites whether or not to filter out items that exist only
   * on inactive sites 
   */
  public void setIncludeInactiveSites(boolean pIncludeInactiveSites) {
    mIncludeInactiveSites = pIncludeInactiveSites;
  }
  

  
  /**
   * This method validates the passed in object (repository items) based on whether or 
   * not any of its items sites/siteGroups.sites exist in the current mShareableTypeId 
   * siteGroup.
   * 
   * @param object to validate
   * @return true if the object passes validation or if no validation was performed.
   */
  @Override
  public boolean validateObject(Object pObject) {
    if (!(pObject instanceof RepositoryItem) ) {
      return false;
    }

    // Get current sharing sites. Items form these sites will be included in result collection.
    Collection<String> sharingSiteIds = getSiteGroupManager().getSharingSiteIds(getShareableTypeId());

    // sharingSiteIds includes the current site. It's null if there is no current site or if
    // the configured shareable type isn't registered (e.g., the customer has unregistered the
    // ShoppingCart shareable type to say that all sites can share the cart). In either case,
    // we return true (no validation)
    if (sharingSiteIds == null) {
      return true;
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
      
      sharingSiteIds = Arrays.asList(siteArray);
    }
    
    // Perform the filtering
    RepositoryItem item = (RepositoryItem) pObject;
      
    // Merged siteIds set
    Set combinedSiteIds = new HashSet();
      
    // The items siteIds from getSitesPropertyName() property
    Set itemSiteIds = getSiteIds(item);
    if(itemSiteIds != null && itemSiteIds.size() > 0){
      combinedSiteIds.addAll(itemSiteIds);
    }
    
    // The  siteIds from getSiteIdsPropertyName() property    
    if(getSiteIdsPropertyName() != null){
      Set sites = (Set<String>) item.getPropertyValue(getSiteIdsPropertyName());
      if (sites != null && sites.size() > 0) {
        combinedSiteIds.addAll(sites);
      }     
    }
      
    // The items siteGroup siteIds
    Set itemSiteGroupSiteIds = getSiteGroupSiteIds(item);
    if(itemSiteGroupSiteIds != null && itemSiteGroupSiteIds.size() > 0){
      combinedSiteIds.addAll(itemSiteGroupSiteIds);
    }

    if (combinedSiteIds.size() > 0) {
      // Now see if this items sites exists in the sharingSiteIds
      if (!Collections.disjoint(combinedSiteIds, sharingSiteIds)) {
        return true;
      }
    }
    //This items has no sites specified. It is available on all sites.
    else {
      return true;
    }
    
    // The item doesn't pass validation
    return false;
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
        String siteId = (String) ((RepositoryItem)site).getRepositoryId();
        
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
