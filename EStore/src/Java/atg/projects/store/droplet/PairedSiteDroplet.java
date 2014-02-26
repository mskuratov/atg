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

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteGroupManager;
import atg.multisite.SiteManager;
import atg.nucleus.naming.ParameterName;
import atg.projects.store.multisite.StoreSitePropertiesManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * This droplet returns a paired site ID for a site identified by "site ID".
 *
 * The "paired sites" concept was first introduced in CRS mobile for the "MobileDetectionInterceptor"
 * component to be able to find corresponding mobile site by known non-mobile (full CRS) site and vice versa.
 * A site is identified as "full CRS" when it has the "channel" property equal to "desktop". All other values
 * are considered as not a "full CRS" identifiers: for example, the "channel" value of "mobile" identifies a mobile site.
 * The "channel" property is defined in "sites.xml" files.
 * The "MobileDetectionInterceptor" uses the "paired sites" info and the site "channel" property value for redirection between mobile and
 * non-mobile sites, when a mobile user agent performs a request with non-mobile site URL or non-mobile (desktop)
 * user agent requests a resource from a mobile site.
 * Site pairs are defined in "sites.xml" files of "CRS mobile" submodule, by special site groups
 * with "crs.MobileSitePairs" shareable type. Each site group with the "crs.MobileSitePairs" shareable type
 * joins together a "full CRS" and corresponding mobile site (in other words, this way we get
 * a "non-mobile - mobile" site pairs).
 * For example, the CRS mobile "sites.xml" file defines the following "US stores" site group:
 * <code>
 *   <add-item item-descriptor="siteGroup" id="sitesUS">
 *     <set-property name="displayName"><![CDATA[Sites (US)]]></set-property>
 *     <set-property name="sites"><![CDATA[storeSiteUS,mobileStoreSiteUS]]></set-property>
 *     <set-property name="shareableTypes"><![CDATA[crs.MobileSitePairs]]></set-property>
 *   </add-item>
 * </code>
 * Using this info, the "MobileDetectionInterceptor" component redirects a mobile user agent when the last one
 * requests the "storeSiteUS" site URL - to the "mobileStoreSiteUS" site.
 *
 * The output parameters for this droplet are:
 * <dl>
 *   <dt>output</dt><dd>This parameter is rendered once if corresponding site ID was detected.</dd>
 *   <dt>error</dt><dd>This optional parameter is rendered if there was an error.
 *                     "errorMessage" request parameter will also be set in this case.</dd>
 * </dl>
 *
 * @author Andrei Urbanovich
 */
public class PairedSiteDroplet extends DynamoServlet {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/PairedSiteDroplet.java#2 $$Change: 768606 $";

  // -------------------------------------
  // Constants
  // -------------------------------------
  /** "siteId" parameter name. */
  public static final ParameterName PARAM_SITE_ID = ParameterName.getParameterName("siteId");
  /** "pairedSiteId" parameter name. */
  public static final String PARAM_PAIRED_SITE_ID = "pairedSiteId";
  /** "output" parameter name. */
  public final static ParameterName OPARAM_OUTPUT = ParameterName.getParameterName("output");
  /** "error" parameter name. */
  public final static ParameterName OPARAM_ERROR = ParameterName.getParameterName("error");
  /** "errorMessage" parameter name. */
  public final static String PARAM_ERROR_MSG = "errorMessage";

  /** "channel" property value: desktop. */
  public static final String PROP_VALUE_CHANNEL_DESKTOP = "desktop";


  //--------------------------------------
  // Properties
  //--------------------------------------

  //--------------------------------------
  // property: storeSitePropertiesManager
  //
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

  //--------------------------------------
  // property: shareableTypeId
  //
  // ID of ShareableType to use to lookup for a site pair.
  private String mShareableTypeId;
  /**
   * Gets the "ShareableType ID for site pairs" property.
   * @return The "ShareableType ID for site pairs" property.
   */
  public String getShareableTypeId() {
    return mShareableTypeId;
  }
  /**
   * Sets the "ShareableType ID for site pairs" property.
   * @param pShareableTypeId The "ShareableType ID for site pairs" property.
   */
  public void setShareableTypeId(String pShareableTypeId) {
    mShareableTypeId = pShareableTypeId;
  }

  //--------------------------------------
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


  // -------------------------------------
  // Member variables
  // -------------------------------------

  // Paired sites cache
  private Map<String, String> mCachePairedSites = Collections.synchronizedMap(new HashMap<String, String>(10));


  //-------------------------------------
  // Public Methods
  //-------------------------------------

  /**
   * @param pRequest The request.
   * @param pResponse The response.
   * @throws ServletException The ServletException
   * @throws IOException The IOException
   * @see atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest,
   *      atg.servlet.DynamoHttpServletResponse)
   */
  @Override
  public void service(DynamoHttpServletRequest pRequest,
                      DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    Object site = pRequest.getObjectParameter(PARAM_SITE_ID);
    String siteID = null;
    if (site != null) {
      siteID = site.toString();
    }
    if (siteID == null || StringUtils.isBlank(siteID)) {
      siteID = SiteContextManager.getCurrentSiteId();
    }
    if (StringUtils.isBlank(siteID)) {
      pRequest.setParameter(PARAM_ERROR_MSG, "'siteId' is null");
      pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
      vlogError("'siteId' is null");
      return;
    }

    String siteChannel = getSiteChannel(siteID);

    // Get a paired site ID:
    //  - Firstly, from the cache
    //  - Secondly, from the "SiteGroupManager"
    String pairedSiteID = mCachePairedSites.get(siteID);
    if (pairedSiteID == null) {
      Collection<String> sharingSites = getSiteGroupManager().getOtherSharingSiteIds(siteID, getShareableTypeId());
      if (sharingSites == null || sharingSites.size() == 0) {
        pRequest.setParameter(PARAM_ERROR_MSG, "No site groups with \"shareableTypes=" + getShareableTypeId() +
          "\" defined or a site with ID=" + siteID + " does not belong to any of such groups");
        pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
        vlogError("No site groups with \"shareableTypes={0}"
          + "\" defined or a site with ID={1} does not belong to any of such groups", getShareableTypeId(), siteID);
        return;
      }
      if (sharingSites.size() > 1) {
        vlogWarning("There are more than one site sharing \"{0}\" with the current site. The first site from the collection will be used",
                    getShareableTypeId());
      }
      Iterator<String> iter = sharingSites.iterator();
      while (iter.hasNext() && pairedSiteID == null) {
        pairedSiteID = iter.next();
        vlogDebug("Got a paired site: {0}", pairedSiteID);
      }
      String pairedSiteChannel = getSiteChannel(pairedSiteID);

      if (!((PROP_VALUE_CHANNEL_DESKTOP.equals(siteChannel) && !PROP_VALUE_CHANNEL_DESKTOP.equals(pairedSiteChannel)) ||
            (!PROP_VALUE_CHANNEL_DESKTOP.equals(siteChannel) && PROP_VALUE_CHANNEL_DESKTOP.equals(pairedSiteChannel)))) {
        pRequest.setParameter(PARAM_ERROR_MSG,
          "Bad configuration for a sites pair \"" + siteID + "\" and \"" + pairedSiteID + "\":\n" +
          "they should have different \"channel\" property value");
        pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
        vlogError("Bad configuration for a sites pair \"{0}\" and \"{1}\":\n" +
          "they should have different \"channel\" property value", siteID, pairedSiteID);
        return;
      }
      mCachePairedSites.put(siteID, pairedSiteID);
    }

    pRequest.setParameter(PARAM_PAIRED_SITE_ID, pairedSiteID);
    pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
  }

  // -------------------------------------
  // Private Methods
  // -------------------------------------

  /**
   * Gets value of the "channel" property of the site identified by given site ID.
   *
   * @param pSiteID Site ID to get a "channel" property of.
   * @return Value of the "channel" property of the site identified by given site ID.
   */
  private String getSiteChannel(String pSiteID) {
    String channel = null;
    try {
      channel = (String)SiteManager.getSiteManager().getSite(pSiteID)
                  .getPropertyValue(getStoreSitePropertiesManager().getChannelPropertyName());
    } catch (Exception ex) {
      vlogError("There was a problem retrieving site \"" + pSiteID + "\".\n", ex);
    }
    return channel;
  }
}
