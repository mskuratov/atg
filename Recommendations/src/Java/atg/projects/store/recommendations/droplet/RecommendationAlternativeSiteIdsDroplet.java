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




package atg.projects.store.recommendations.droplet;

import atg.core.util.StringUtils;
import atg.multisite.Site;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteGroupManager;
import atg.nucleus.naming.ParameterName;
import atg.projects.store.multisite.StoreSitePropertiesManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;

/**
 * This droplet returns the list of alternative store IDs in the order of preference
 * that will be used to lookup product link, price, etc.
 * 
 * The first sites in the list will be sites from the same site sharing group as the current
 * site, then the rest sites will go.
 * 
 * The input parameters:
 * <dl>
 *   <dt>siteId (optional)</dt>
 *   <dd>The site ID that should be used to look for the sites in the
 *   same sharing group or to compare site channels. If <code>siteId</code> is not provided the current site ID 
 *   will be used.</dd>
 *   <dt>shareableTypeId (optional)</dt>
 *   <dd>The shareable type ID to use to look up for the sites
 *   in the same site sharing group. If not provided the shareable type ID configured in 
 *   the corresponding component's property is used</dd>
 *   <dt>excludeInputSite</dt>
 *   <dd>If true input site will be excluded form the list of
 *   alternative site IDs.</dd>
 *   <dt>includeActiveSites</dt>
 *   <dd>If true only active sites will be returned.</dd>
 *   <dt>inputSiteChannelOnly</dt>
 *   <dd>If true only sites that have the same channel as the input site will be included into the result.
 *   Default is <code>true</code></dd>
 * </dl>
 * 
 * The output parameters for this droplet are:
 * <dl>
 * <dt>output
 * <dd>This parameter is rendered once if a collection of sites is found.
 * <dt>sites
 * <dd>This parameter is set to the list of Site objects in the order of preference.
 * </dl>
 * 
 * @author sshulman
 * @version $Change: 788278 $$DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */

@atg.nucleus.annotation.Service(requiredProperties={"siteGroupManager","storeSitePropertiesManager"})
public class RecommendationAlternativeSiteIdsDroplet extends DynamoServlet {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Recommendations/src/atg/projects/store/recommendations/droplet/RecommendationAlternativeSiteIdsDroplet.java#3 $$Change: 788278 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  
  /** ParameterName for siteId **/
  public final static ParameterName SITE_ID = ParameterName.getParameterName("siteId");
  /** ParameterName for shareableTypeId **/
  public final static ParameterName SHAREABLE_TYPE_ID = ParameterName.getParameterName("shareableTypeId");
  /** ParameterName for excludeInputSite **/
  public final static ParameterName EXCLUDE_INPUT_SITE = ParameterName.getParameterName("excludeInputSite");
  /** ParameterName for includeActiveSites **/
  public final static ParameterName INCLUDE_ACTIVE_SITES = ParameterName.getParameterName("includeActiveSites");
  /** ParameterName for inputSiteChannelOnly **/
  public final static ParameterName INPUT_SITE_CHANNEL_ONLY = ParameterName.getParameterName("inputSiteChannelOnly");
  
  /** ParameterName for output **/
  public final static ParameterName OUTPUT = ParameterName.getParameterName("output");
  /** ParameterName for empty **/
  
  /** Parameter name for site **/
  public final static String SITES_IDS_PROP_NAME = "siteIds";

  
  //-------------------------------------
  // Properties
  //-------------------------------------

   
  /** SiteGroupManager **/
  protected SiteGroupManager mSiteGroupManager = null;

  /**
   * Returns the SiteGroupManager. 
   * 
   * @return the siteGroupManager
   */
  public SiteGroupManager getSiteGroupManager() {
    return mSiteGroupManager;
  }

  /**
   * Sets the SiteGroupManager
   * 
   * @param pSiteGroupManager the siteGroupManager to set
   */
  public void setSiteGroupManager(SiteGroupManager pSiteGroupManager) {
    mSiteGroupManager = pSiteGroupManager;
  }
  
  /** StoreSitePropertiesManager */
  private StoreSitePropertiesManager mStoreSitePropertiesManager;
  /**
   * Gets the StoreSitePropertiesManager bean which is used to manage store properties.
   * @return The StoreSitePropertiesManager bean which is used to manage store properties.
   */
  public StoreSitePropertiesManager getStoreSitePropertiesManager() {
    return mStoreSitePropertiesManager;
  }
  /**
   * Sets the StoreSitePropertiesManager bean which is used to manage store properties.
   * @param StoreSitePropertiesManager Set a new storeSitePropertyManager.
   */
  public void setStoreSitePropertiesManager(StoreSitePropertiesManager pStoreSitePropertiesManager) {
    mStoreSitePropertiesManager = pStoreSitePropertiesManager;
  }
  
  /** ShareableTypeId **/
  protected String mShareableTypeId = null;
  
  /**
   * @return the mShareableTypeId
   */
  public String getShareableTypeId() {
    return mShareableTypeId;
  }

  /**
   * @param mShareableTypeId the mShareableTypeId to set
   */
  public void setShareableTypeId(String pShareableTypeId) {
    mShareableTypeId = pShareableTypeId;
  }

  /** ExcludeInputSite **/
  protected boolean mExcludeInputSite = false;

  /**
   * @return the excludeInputSite
   */
  public boolean isExcludeInputSite() {
    return mExcludeInputSite;
  }

  /**
   * @param pExcludeInputSite the excludeInputSite to set
   */
  public void setExcludeInputSite(boolean pExcludeInputSite) {
    mExcludeInputSite = pExcludeInputSite;
  }

  /** IncludeActiveSites **/
  protected boolean mIncludeActiveSites = false;
  
  /**
   * Returns the includeActiveSites parameter, determining whether only active 
   * sites should be retrieved
   * <p>
   * The default value for this property is <code>false</code>.
   * <p>
   * @return the includeActiveSites
   */
  public boolean isIncludeActiveSites() {
    return mIncludeActiveSites;
  }

  /**
   * Sets the includeActiveSites property, determining whether only active 
   * site should be retrieved
   * <p>
   * The default value for this property is <code>false</code>.
   * <p>
   * @param pIncludeActiveSites the includeActiveSites to set
   */
  public void setIncludeActiveSites(boolean pIncludeActiveSites) {
    mIncludeActiveSites = pIncludeActiveSites;
  }
  
  /** InputSiteChannelOnly **/
  protected boolean mInputSiteChannelOnly = true;
  
  /**
   * Returns the InputSiteChannelOnly property, determining whether only sites with the same
   * channel as the input site should be included. E.g., if the input site is the mobile one
   * then only sites with 'mobile' channel will be include.
   * <p>
   * The default value for this property is <code>true</code>.
   * <p>
   * @return the InputSiteChannelOnly
   */
  public boolean isInputSiteChannelOnly() {
    return mInputSiteChannelOnly;
  }

  /**
   * Sets the InputSiteChannelOnly property, determining whether only sites with the same
   * channel as the input site should be included. E.g., if the input site is the mobile one
   * then only sites with 'mobile' channel will be include.
   * <p>
   * The default value for this property is <code>false</code>.
   * <p>
   * @param pInputSiteChannelOnly the InputSiteChannelOnly property to set
   */
  public void setInputSiteChannelOnly(boolean pInputSiteChannelOnly) {
    mInputSiteChannelOnly = pInputSiteChannelOnly;
  }

  /**
   * Services the output oparam if a the list of sites can be determined.
   * 
   * @param pRequest The request
   * @param pResponse The response
   * @throws ServletException
   * @throws IOException
   * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest,
   *      atg.servlet.DynamoHttpServletResponse)
   *      
   * @see SiteGroupManager#findSharingSites(String, String, boolean, boolean)
   */
  @Override
  public void service(DynamoHttpServletRequest pRequest,
                      DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    // If the siteId is not passed in as an input parameter, retrieve it from
    // the SiteContextManager
    String inputSiteId = pRequest.getParameter(SITE_ID);
    
    if (StringUtils.isBlank(inputSiteId)) {
      inputSiteId = SiteContextManager.getCurrentSiteId(); 
    }
    
    if (isLoggingDebug()) {
      logDebug("service: siteId=" + inputSiteId);
    }
    
    // Determine the site's channel
    String inputSiteChannel = getSiteChannel(inputSiteId);
    
    // If the shareableTypeId is not passed in as an input parameter, retrieve it from
    // the configuration properties
    String shareableTypeId = pRequest.getParameter(SHAREABLE_TYPE_ID); 
    
    if (StringUtils.isBlank(shareableTypeId)) { 
      shareableTypeId = getShareableTypeId();
    } 
    
    if (isLoggingDebug()) {
      logDebug("service: shareableTypeId=" + shareableTypeId);
    }
    
    // If excludeInputSite is not passed in as an input parameter, retrieve it from
    // configuration
    String excludeInputSiteString = pRequest.getParameter(EXCLUDE_INPUT_SITE);
    boolean excludeInputSite = isExcludeInputSite();
    
    if (!StringUtils.isBlank(excludeInputSiteString)) {
      excludeInputSite = Boolean.parseBoolean(excludeInputSiteString);
    }
    
    if (isLoggingDebug()) {
      logDebug("service: isExcludeInputSite=" + excludeInputSite);
    }

    // If includeActiveSites is not passed in as an input parameter, retrieve it from
    // configuration
    String includeActiveSitesString = pRequest.getParameter(INCLUDE_ACTIVE_SITES);
    boolean includeActiveSites = isIncludeActiveSites();
    
    if (!StringUtils.isBlank(includeActiveSitesString)) { 
      includeActiveSites = Boolean.parseBoolean(includeActiveSitesString);
    }
    
    if (isLoggingDebug()) {
      logDebug("includeActiveSites=" + includeActiveSites);
    }
    
    // If inputSiteChannelOnly is not passed in as an input parameter, retrieve it from
    // configuration
    String inputSiteChannelOnlyString = pRequest.getParameter(INPUT_SITE_CHANNEL_ONLY);
    boolean inputSiteChannelOnly = isInputSiteChannelOnly();
    
    if (!StringUtils.isBlank(inputSiteChannelOnlyString)) { 
      inputSiteChannelOnly = Boolean.parseBoolean(inputSiteChannelOnlyString);
    }
    
    if (isLoggingDebug()) {
      logDebug("InputSiteChannelOnly=" + inputSiteChannelOnly);
    }
    
    // If site ID and shareable type ID are defined
    if (inputSiteId != null) {
      try {
        
        List<String> alternativeSitesList = new ArrayList<String>();
        
        if (shareableTypeId != null){
        
          Collection<Site> sitesFromSharingGroup = getSiteGroupManager().findSharingSites(inputSiteId, shareableTypeId, excludeInputSite, includeActiveSites);

          // Add sites from the same sharing group to the sites list
          for( Site site : sitesFromSharingGroup){
            addSiteToList(site, alternativeSitesList, inputSiteId, excludeInputSite, inputSiteChannel, inputSiteChannelOnly);
          }
        }
        
        // Now include the rest sites that are not in the same site group
        // with the current site.
        // First get all sites
        RepositoryItem[] allSites = null; 
        if (includeActiveSites){
          allSites = getSiteGroupManager().getSiteManager().getActiveSites();
        }else{
          allSites = getSiteGroupManager().getSiteManager().getAllSites();
        }
        
        // Go through the all sites and add those that are not still in the
        // sites list and not current site (if configure not to include current site)
        for (RepositoryItem site : allSites){
          addSiteToList(site, alternativeSitesList, inputSiteId, excludeInputSite, inputSiteChannel, inputSiteChannelOnly);
        }
        
        pRequest.setParameter(SITES_IDS_PROP_NAME, alternativeSitesList);
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
      }
      catch (SiteContextException e) {
        if (isLoggingDebug()) {
          logDebug("service: Error getting site " + inputSiteId, e);
        }
      } catch (RepositoryException e) {
        if (isLoggingError())
            logError("service: Error getting sites", e);
      }
    }
    else {
      if (isLoggingDebug()) {
        logDebug("service: siteId or shareableTypeId could not be found");
      }
    }
  }
  
  /**
   * Adds site's ID to the list of sites. Before adding the site the method
   * checks whether the site conforms to all conditions. If <code>pExcludeInputSite</code>
   * is <code>true</code> then input site will not be added.
   * If <code>pInputSiteChannel</code> is <code>true</code> then only sites with the same
   * channel will be added to the list.
   * @param pSite The site repository item to add to the list
   * @param pSitesList The list of sites to add to.
   * @param pInputSiteId The droplet's input site.
   * @param pExcludeInputSite The boolean indicating whether input site should be excluded.
   * @param pInputSiteChannel The channel of the input site.
   * @param pInputSiteChannelOnly The boolean indicating whether only site's with the same
   *                              channel as the input site can be added to the list.
   */
  protected void addSiteToList(RepositoryItem pSite, List<String> pSitesList,
                           String pInputSiteId, boolean pExcludeInputSite,
                           String pInputSiteChannel, boolean pInputSiteChannelOnly){
  if (pSite == null || pSitesList.contains(pSite.getRepositoryId())){
    return;
  }
    
    // If the site is the same as the input site and excludeInputSite boolean is
  // set to true then skip adding site to the list.
  if (pExcludeInputSite && pInputSiteId.equals(pSite.getRepositoryId())){
      return;
    }
   
  // Check whether only sites with the same channel as the input site can be added to the list
    if(pInputSiteChannelOnly){
      String siteChannel = (String) getSiteChannel(pSite);
      if (!siteChannel.equals(pInputSiteChannel)){
        return;
      }
    }
    
    // All validation is passed so we can add site to the list. 
    pSitesList.add(pSite.getRepositoryId());
  }
  
  /**
   * Gets value of the "channel" property of the site identified by given site ID.
   *
   * @param pSiteId Site ID to get a "channel" property of.
   * @return Value of the "channel" property of the site identified by given site ID.
   */
  private String getSiteChannel(String pSiteId) {
    String channel = null;
    try {
      channel = (String)getSiteGroupManager().getSiteManager().getSite(pSiteId)
                  .getPropertyValue(getStoreSitePropertiesManager().getChannelPropertyName());
    } catch (Exception ex) {
      vlogError("There was a problem retrieving site \"" + pSiteId + "\".\n", ex);
    }
    return channel;
  }
  
  /**
   * Gets value of the "channel" property of the site identified by given site repository item.
   *
   * @param pSite Site repository item to get a "channel" property of.
   * @return Value of the "channel" property of the site identified by given site repository item.
   */
  private String getSiteChannel(RepositoryItem pSite) {
    if (pSite == null){
      return null;
    }
  
    return (String) pSite.getPropertyValue(getStoreSitePropertiesManager().getChannelPropertyName());
  }
  
}
