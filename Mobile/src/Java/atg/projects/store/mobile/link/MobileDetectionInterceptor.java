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
package atg.projects.store.mobile.link;

import atg.core.net.URLUtils;
import atg.core.util.LRUMap;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteGroup;
import atg.multisite.SiteGroupManager;
import atg.multisite.SiteManager;
import atg.multisite.SiteURLManager;
import atg.projects.store.mobile.MobileStoreConfiguration;
import atg.projects.store.multisite.StoreSitePropertiesManager;
import atg.projects.store.profile.SessionBean;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.pipeline.InsertableServletImpl;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * Pipeline servlet that performs redirection from desktop URL to mobile one and vice versa,
 * depending on used browser type (browser user agent).
 * This servlet also detects Endeca preview mode.
 * This servlet performs the following actions in order to verify if redirection is required:
 *   - Checks if a request belongs to excluded file types ("excludedFileTypes" property).
 *   - Checks if a request is the preview one:
 *     in that case, it redirects a mobile content preview requests to a mobile site.
 *     For example, a preview request to "/crs/mobile/browse/" would be redircted to "/crs/mobile/storeus/mobile/browse/".
 *   - Checks if a request from mobile user agent is performed to desktop URI.
 *     The "desktopToMobilePaths" property value (map) could also be used on this step
 *     to resolve "desktop -> mobile" URI mapping.
 *   - Checks if a request from desktop user agent is performed to mobile URI.
 *     The "mobileToDesktopPaths", "desktopToMobilePaths" properties values (map)
 *     could also be also used on this step to resolve "mobile -> desktop" URI mapping.
 *
 * @author Michael Moscardini
 * @version $Change: 795448 $$DateTime: 2013/03/07 07:34:17 $$Author: vbreida $
 * @updated $DateTime: 2013/03/07 07:34:17 $$Author: vbreida $
 */
public class MobileDetectionInterceptor extends InsertableServletImpl {
  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Mobile/src/atg/projects/store/mobile/link/MobileDetectionInterceptor.java#18 $$Change: 795448 $";

  // -------------------------------------
  // Constants
  // -------------------------------------
  /** HTTP request header: "User-Agent". */
  public static final String HTTP_HEADER_USER_AGENT = "User-Agent";
  /** Default value of "enableFullSiteParam" property of this component. */
  public static final String FULL_SITE_PARAM = "enableFullSite";
  /** "channel" property value: mobile. */
  public static final String PROP_VALUE_CHANNEL_MOBILE = "mobile";
  /** "channel" property value: desktop. */
  public static final String PROP_VALUE_CHANNEL_DESKTOP = "desktop";
  /** Forward slash character. */
  public static final String SLASH = "/";
  /** "Endeca preview user agent" request parameter name. */
  public static final String ENDECA_USER_AGENT_PARAM = "Endeca_user_agent";
  /** "Endeca preview user agent" request attribute name. */
  public static final String ENDECA_USER_AGENT_ATTR = "endecaUserAgent";
  /** "Endeca preview user segments" request parameter name. */
  public static final String ENDECA_USER_SEGMENTS_PARAM = "Endeca_user_segments";
  /** BCC Preview session attribute: represents site name, selected in BCC Preview Launcher */ 
  public static final String BCC_PREVIEW_SITE = "BCC_PREVIEW_SITE";
  /** BCC Preview session attribute: used to indicate if site belongs to a mobile channel. */
  public static final String BCC_PREVIEW_IS_SITE_MOBILE = "BCC_PREVIEW_IS_SITE_MOBILE";

  // -------------------------------------
  // Properties
  // -------------------------------------

  //-------------------------------------
  // property: storeSitePropertiesManager
  //
  // NOTE: It's only used to get the "channel" site property value.
  private StoreSitePropertiesManager mStoreSitePropertiesManager;
  /**
   * Gets the "StoreSitePropertiesManager" bean which is used to manage store properties.
   * @return The "StoreSitePropertiesManager" bean which is used to manage store properties.
   */
  public StoreSitePropertiesManager getStoreSitePropertiesManager() {
    return mStoreSitePropertiesManager;
  }
  /**
   * Sets the "StoreSitePropertiesManager" bean which is used to manage store properties.
   * @param "pStoreSitePropertiesManager" The new "StoreSitePropertiesManager" value.
   */
  public void setStoreSitePropertiesManager(StoreSitePropertiesManager pStoreSitePropertiesManager) {
    mStoreSitePropertiesManager = pStoreSitePropertiesManager;
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
   * @param pShareableTypeIdForMobileSitePairs The "ShareableType ID for mobile site pairs" property value.
   */
  public void setShareableTypeIdForMobileSitePairs(String pShareableTypeIdForMobileSitePairs) {
    mShareableTypeIdForMobileSitePairs = pShareableTypeIdForMobileSitePairs;
  }

  //-------------------------------------
  // property: shareableTypeIdForNonMobileSite
  //
  // ID of ShareableType to use to lookup for a desktop sites.
  private String mShareableTypeIdForNonMobileSite;
  /**
   * Gets the "ShareableType ID for desktop sites" property.
   * @return The "ShareableType ID for desktop sites" property.
   */
  public String getShareableTypeIdForNonMobileSite() {
    return mShareableTypeIdForNonMobileSite;
  }
  /**
   * Sets the "ShareableType ID for desktop sites" property.
   * @param pShareableTypeIdForNonMobileSite The "ShareableType ID for desktop sites" property value.
   */
  public void setShareableTypeIdForNonMobileSite(String pShareableTypeIdForNonMobileSite) {
    mShareableTypeIdForNonMobileSite = pShareableTypeIdForNonMobileSite;
  }

  //-------------------------------------
  // property: sessionBean
  //
  private String mSessionBean;
  /**
   * Gets the "Session bean" property.
   * @return The "Session bean" property.
   */
  public String getSessionBean() {
    return mSessionBean;
  }
  /**
   * Sets the "Session bean" property.
   * @param pSessionBean The "Session bean" property.
   */
  public void setSessionBean(String pSessionBean) {
    mSessionBean = pSessionBean;
  }

  //-------------------------------------
  // property: storeConfiguration
  //
  // Mobile store configuration. It for example stores the mobile context root prefix ("mobileStorePrefix" property).
  private MobileStoreConfiguration mStoreConfiguration;
  /**
   * Gets the "Store configuration" property.
   * @return The "Store configuration" property.
   */
  public MobileStoreConfiguration getStoreConfiguration() {
    return mStoreConfiguration;
  }
  /**
   * Sets the "Store configuration" property.
   * @param pStoreConfiguration The "Store configuration" property.
   */
  public void setStoreConfiguration(MobileStoreConfiguration pStoreConfiguration) {
    mStoreConfiguration = pStoreConfiguration;
  }

  //-------------------------------------
  // property: siteGroupManager
  //
  private SiteGroupManager mSiteGroupManager;
  /**
   * Gets the reference to {@link SiteGroupManager} to be used when determining sharing sites.
   * @return The reference to {@link SiteGroupManager} to be used when determining sharing sites.
   */
  public SiteGroupManager getSiteGroupManager() {
    return mSiteGroupManager;
  }
  /**
   * Sets the new reference to {@link SiteGroupManager} to be used when determining sharing sites.
   * @param pSiteGroupManager The new reference to {@link SiteGroupManager} to be used when determining sharing sites.
   */
  public void setSiteGroupManager(SiteGroupManager pSiteGroupManager) {
    mSiteGroupManager = pSiteGroupManager;
  }

  //-------------------------------------
  // property: siteURLManager
  //
  // Site URL manager. This is used to generate the new URLs for redirect and to determine the current site.
  private SiteURLManager mSiteURLManager;
  /**
   * Gets the reference to {@link SiteURLManager}.
   * @return The reference to {@link SiteURLManager}.
   */
  public SiteURLManager getSiteURLManager() {
    return mSiteURLManager;
  }
  /**
   * Sets the new reference to {@link SiteURLManager}.
   * @param pSiteURLManager The new reference to {@link SiteURLManager}.
   */
  public void setSiteURLManager(SiteURLManager pSiteURLManager) {
    mSiteURLManager = pSiteURLManager;
  }

  //-------------------------------------
  // property: excludedFileTypes
  //
  // Excluded file types.
  private String[] mExcludedFileTypes;
  /**
   * Gets the "Excluded file types" property.
   * @return The "Excluded file types" property.
   */
  public String[] getExcludedFileTypes() {
    return mExcludedFileTypes;
  }
  /**
   * Sets the "Excluded file types" property.
   * @param pExcludedFileTypes The "Excluded file types" property value.
   */
  public void setExcludedFileTypes(String[] pExcludedFileTypes) {
    mExcludedFileTypes = pExcludedFileTypes;
  }

  //-------------------------------------
  // property: mobileBrowserTypes
  //
  // List of mobile browser types.
  // This list is a subset of types defined in "/atg/dynamo/servlet/pipeline/BrowserTyper" bean, in "browserTypes" property.
  private String[] mMobileBrowserTypes;
  /**
   * Gets the "Mobile browser types" property.
   * @return The "Mobile browser types" property.
   */
  public String[] getMobileBrowserTypes() {
    return mMobileBrowserTypes;
  }
  /**
   * Sets the "Mobile browser types" property.
   * @param pMobileBrowserTypes The "Mobile browser types" property value.
   */
  public void setMobileBrowserTypes(String[] pMobileBrowserTypes) {
    mMobileBrowserTypes = pMobileBrowserTypes;
  }

  //-------------------------------------
  // property: enableFullSiteParam
  //
  // The name of the URL parameter which forces switching to the full site.
  private String mEnableFullSiteParam;
  /**
   * Gets the "Enable full site param" property. Defaults to {@link #FULL_SITE_PARAM}.
   * @return The "Enable full site param" property. Defaults to {@link #FULL_SITE_PARAM}.
   */
  public String getEnableFullSiteParam() {
    if (mEnableFullSiteParam != null) {
      return mEnableFullSiteParam;
    }
    return FULL_SITE_PARAM;
  }
  /**
   * Sets the "Enable full site param" property.
   * @param pEnableFullSiteParam The "Enable full site param" property value.
   */
  public void setEnableFullSiteParam(String pEnableFullSiteParam) {
    mEnableFullSiteParam = pEnableFullSiteParam;
  }

  //-------------------------------------
  // property: desktopToMobilePaths
  //
  // Map of "Desktop (full CRS) = mobile (CRS-M)" paths.
  private Map<String, String> mDesktopToMobilePaths;
  /**
   * Gets the "Desktop to mobile paths" property.
   * @return The "Desktop to mobile paths" property.
   */
  public Map<String, String> getDesktopToMobilePaths() {
    return mDesktopToMobilePaths;
  }
  /**
   * Sets the "Desktop to mobile paths" property.
   * @param pDesktopToMobilePaths The "Desktop to mobile paths" property value.
   */
  public void setDesktopToMobilePaths(Map<String, String> pDesktopToMobilePaths) {
    mDesktopToMobilePaths = pDesktopToMobilePaths;
  }

  //-------------------------------------
  // property: mobileToDesktopPaths
  //
  // Map of "Mobile (CRS-M) = desktop (full CRS)" paths.
  private Map<String, String> mMobileToDesktopPaths;
  /**
   * Gets the "Mobile to desktop paths" property.
   * @return The "Mobile to desktop paths" property.
   */
  public Map<String, String> getMobileToDesktopPaths() {
    return mMobileToDesktopPaths;
  }
  /**
   * Sets the "Mobile to desktop paths" property.
   * @param pMobileToDesktopPaths The "Mobile to desktop paths" property value.
   */
  public void setMobileToDesktopPaths(Map<String, String> pMobileToDesktopPaths) {
    mMobileToDesktopPaths = pMobileToDesktopPaths;
  }

  // Preview properties ///////////////////////////////////////////////////////
  //-------------------------------------
  // property: previewEnabled
  //
  // Is preview enabled?
  private boolean mPreviewEnabled = true;
  /**
   * Gets the "Is preview enabled?" property.
   * @return The "Is preview enabled?" property.
   */
  public boolean isPreviewEnabled() {
    return mPreviewEnabled;
  }
  /**
   * Sets the "Is preview enabled?" property.
   * @param pPreviewEnabled The "Is preview enabled?" property value.
   */
  public void setPreviewEnabled(boolean pPreviewEnabled) {
    mPreviewEnabled = pPreviewEnabled;
  }

  //-------------------------------------
  // property: prefixDelimiter
  //
  // Endeca preview by "User Segment": delimiter that separates segment types from names.
  private String mPrefixDelimiter = null;
  /**
   * Gets the "Prefix delimiter that separates segment types from names" property.
   * @return The "Prefix delimiter that separates segment types from names" property.
   */
  public String getPrefixDelimiter() {
    return mPrefixDelimiter;
  }
  /**
   * Sets the "Prefix delimiter that separates segment types from names" property.
   * @param pPrefixDelimiter The "Prefix delimiter that separates segment types from names" property value.
   */
  public void setPrefixDelimiter(String pPrefixDelimiter) {
    mPrefixDelimiter = pPrefixDelimiter;
  }

  //-------------------------------------
  // property: sitePrefix
  //
  // Endeca preview by "User Segment": prefix to use with the ID of the current site.
  private String mSitePrefix = null;
  /**
   * Gets the "Prefix to use with the ID of the current site" property.
   * @return The "Prefix to use with the ID of the current site" property.
   */
  public String getSitePrefix() {
    return mSitePrefix;
  }
  /**
   * Sets the "Prefix to use with the ID of the current site" property.
   * @param pSitePrefix The "Prefix to use with the ID of the current site" property value.
   */
  public void setSitePrefix(String pSitePrefix) {
    mSitePrefix = pSitePrefix;
  }


  // -------------------------------------
  // Member variables
  // -------------------------------------
  
  // Cache of desktop to mobile sites.
  // Key is a desktop site ID, corresponding value is a mobile site ID.
  private Map<String, String> mCacheDesktopToMobileSites = Collections.synchronizedMap(new HashMap<String, String>(10));

  // Cache of mobile to desktop sites.
  // Key is a mobile site ID, corresponding value is a desktop site ID.
  private Map<String, String> mCacheMobileToDesktopSites = Collections.synchronizedMap(new HashMap<String, String>(10));

  // Cache of "desktop (full CRS) = mobile (CRS-M)" paths.
  private LRUMap<String, PathDetermination> mCacheDesktopToMobilePaths = new LRUMap<String, PathDetermination>(100);

  // Cache of "mobile (CRS-M) = desktop (full CRS)" paths.
  private LRUMap<String, PathDetermination> mCacheMobileToDesktopPaths = new LRUMap<String, PathDetermination>(100);

  // Lock object.
  private ReentrantReadWriteLock mLock = new ReentrantReadWriteLock();


  // -------------------------------------
  // Public Methods
  // -------------------------------------
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws IOException, ServletException {
    String redirectToURL = null; // null means no redirection

    if (isExcludedFile(pRequest)) {
      if (isLoggingDebug()) {
        vlogDebug("- No redirect (excluded file type request: {0})", pRequest.getRequestURIWithQueryString());
      }
    } else {
      if (isLoggingDebug()) {
        vlogDebug("~ Check redirect for: {0}", pRequest.getRequestURIWithQueryString());
      }

      // Check if request is made within the scope of BCC preview
      String pushSite = pRequest.getParameter("pushSite");
      String previewSessionId = pRequest.getParameter("previewsessionid");
      if (previewSessionId != null && pushSite != null) {
        SessionBean sessionBean = (SessionBean)pRequest.resolveName(getSessionBean());
        String bccPreviewSiteInSession = (String) sessionBean.getValues().get(BCC_PREVIEW_SITE);

        // Set special session attributes, indicating BCC-preview context
        if (!pushSite.equals(bccPreviewSiteInSession)) {
          sessionBean.getValues().put(BCC_PREVIEW_SITE, pushSite);
          sessionBean.getValues().put(BCC_PREVIEW_IS_SITE_MOBILE, getSiteChannel(pushSite).equals(PROP_VALUE_CHANNEL_MOBILE));
        }
      }

      String siteId; // ID of the site the current request belongs to

      if (isEndecaPreviewRequest(pRequest)) {
        // Endeca preview request (and the platform preview server-side is enabled)
        ///////////////////////////////////////////////////////////////////////////
        redirectToURL = getEndecaPreviewURL4Redirect(pRequest);
      } else if (isBCCPreviewForMobileSite(pRequest)) {
        // BCC Preview request
        //////////////////////
        if (getMobileRequestSiteId(pRequest) == null && (getDesktopRequestSiteId(pRequest) != null || isContextRootOfDesktopSite(pRequest))) {
          SessionBean sessionBean = (SessionBean)pRequest.resolveName(getSessionBean());
          redirectToURL = mapPath4Redirect(pRequest, (String)sessionBean.getValues().get(BCC_PREVIEW_SITE));
        }
      } else if ((siteId = getMobileRequestSiteId(pRequest)) != null) {
        // Mobile site request
        //////////////////////
        if (!isMobileUserAgent(pRequest) || isFullSiteEnabled(pRequest)) {
          redirectToURL = mapMobile2DesktopURL4Redirect(pRequest, siteId);
        } else {
          redirectToURL = mapPath4Redirect(pRequest, siteId);
          if (redirectToURL == null) {
            vlogDebug("- No redirect (mobile User-Agent, mobile URL. No 'full site' parameter)");
          }
        }
      } else if ((siteId = getDesktopRequestSiteId(pRequest)) != null || isContextRootOfDesktopSite(pRequest)) {
        // Desktop site request
        ///////////////////////
        if (isMobileUserAgent(pRequest)) {
          if (isFullSiteEnabled(pRequest)) {
            vlogDebug("- No redirect ('Full site' parameter)");
          } else {
            redirectToURL = mapDesktop2MobileURL4Redirect(pRequest, siteId);
          }
        } else {
          vlogDebug("- No redirect (desktop User-Agent, desktop URL)");
        }
      } else {
        // All OTHER requests (such as REST ones)
        /////////////////////////////////////////
        vlogDebug("- No redirect (the request is not a mobile, not a desktop)");
      }
    }

    if (redirectToURL != null) {
      redirectToURL = pResponse.encodeRedirectURL(redirectToURL);
      // Remove possible "jsessionid"
      redirectToURL = removeJsessionIdFromUrl(redirectToURL);
      vlogDebug("> Redirect to: {0}", redirectToURL);
      pResponse.sendRedirect(redirectToURL);
    } else {
      passRequest(pRequest, pResponse);
    }
  }

  /**
   * Returns boolean, indicating if request is processed within the BCC iframe for mobile site. <br/>
   * Check is based on special session attribute, that is set in {@link MobileDetectionInterceptor}}.
   *
   * @param pRequest The request to examine.
   * @return boolean
   */
  private boolean isBCCPreviewForMobileSite(DynamoHttpServletRequest pRequest) {
    SessionBean sessionBean = (SessionBean)pRequest.resolveName(getSessionBean());
    Boolean isBccPreviewForMobile = (Boolean)sessionBean.getValues().get(BCC_PREVIEW_IS_SITE_MOBILE);
    return (isBccPreviewForMobile != null && isBccPreviewForMobile.equals(Boolean.TRUE));
  }

  /**
   * Returns the Endeca preview module User-Agent parameter value:
   *  - Returns the {@link #ENDECA_USER_AGENT_PARAM} request parameter.
   *  - If the request parameter does not exist, tries to get similar parameter from browser cookie.
   * Unescapes the result because the Endeca preview initially escapes the {@link #ENDECA_USER_AGENT_PARAM} value.
   *
   * @param pRequest The request to examine.
   * @return The Endeca preview module User-Agent parameter value.
   */
  public static String getEndecaUserAgent(DynamoHttpServletRequest pRequest) {
    // From request
    String endecaUserAgent = pRequest.getParameter(ENDECA_USER_AGENT_PARAM);
    if (endecaUserAgent == null) {
      // From cookie
      endecaUserAgent = pRequest.getCookieParameter(ENDECA_USER_AGENT_PARAM);
    }
    if (endecaUserAgent != null) {
      endecaUserAgent = URLUtils.unescapeUrlString(endecaUserAgent);
      int i = endecaUserAgent.indexOf('|');
      if (i > 0) {
        endecaUserAgent = endecaUserAgent.substring(i + 1);
      }
      return endecaUserAgent;
    }
    return null;
  }

  // -------------------------------------
  // Private Methods
  // -------------------------------------

  /**
   * Check to see if we have an excluded file type in given request URI.
   *
   * @param pRequest The request to examine.
   * @return true if the request is to excluded file type.
   */
  private boolean isExcludedFile(HttpServletRequest pRequest) {
    String[] excludedFileTypes = getExcludedFileTypes();
    if (excludedFileTypes != null) {
      String requestURI = pRequest.getRequestURI();
      for (String fileType : excludedFileTypes) {
        if (requestURI.endsWith(fileType)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Checks if the request was performed from a mobile user agent.
   *
   * @param pRequest The request used to detect browser type.
   * @return true if a request was performed from a mobile user agent.
   */
  private boolean isMobileUserAgent(DynamoHttpServletRequest pRequest) {
    boolean mobileUserAgent = false;

    String[] mobileBrowserTypes = getMobileBrowserTypes();
    if (mobileBrowserTypes != null) {
      for (String mobileBrowserType : mobileBrowserTypes) {
        // Check browser type from the request "User-Agent" header
        if (pRequest.isBrowserType(mobileBrowserType)) {
          mobileUserAgent = true;
          break;
        }
      }
    }

    if (isLoggingDebug()) {
      vlogDebug("{0} User-Agent: {1}", mobileUserAgent ? "Mobile" : "Desktop", pRequest.getHeader(HTTP_HEADER_USER_AGENT));
    }

    return mobileUserAgent;
  }

  /**
   * Checks if a request is being made in Endeca preview mode.
   * NOTE This method sets the {@link #ENDECA_USER_AGENT_ATTR} request attribute to value of the {@link #ENDECA_USER_AGENT_PARAM}
   * Endeca preview request parameter, if Endeca preview is enabled on ATG server.
   * Otherwise, the {@link #ENDECA_USER_AGENT_ATTR} is removed from the request.
   *
   * @param pRequest The request to examine.
   * @return true if a request is being made in Endeca preview mode. Returns <code>false</code> otherwise.
   */
  private boolean isEndecaPreviewRequest(DynamoHttpServletRequest pRequest) {
    String endecaUserAgent = getEndecaUserAgent(pRequest);

    // The "AbstractPreviewLinkServlet" request parameter.
    // In case if this parameter is present, it's too early to consider this as "Endeca preview" request
    // to be processed by the interceptor
    String previewServletContentUri = pRequest.getParameter("__contentUri");

    if (endecaUserAgent != null && isPreviewEnabled() && previewServletContentUri == null) {
      // Preview is enabled. The Endeca Preview request
      pRequest.setAttribute(ENDECA_USER_AGENT_ATTR, endecaUserAgent);
      vlogDebug("Endeca Preview is enabled. Endeca Preview device \"User-Agent\": [{0}]", endecaUserAgent);
      return true;
    }
    pRequest.removeAttribute(ENDECA_USER_AGENT_ATTR);

    return false;
  }

  /**
   * Checks if given request is a request to mobile site: using the <code>SiteURLManager.getSiteIdForURL(requestURI)</code>.
   * Returns ID of the site, if given request is a request to mobile site. Returns <code>null</code> otherwise.
   *
   * NOTE This method should be called 1-st (i.e. prior to the <code>isDesktopRequest()</code>)
   * because the "contextRoot" site property value always corresponds to a desktop site contexts.
   *
   * @param pRequest The request to examine.
   * @return ID of the site, if given request is a request to mobile site. Returns <code>null</code> otherwise.
   */
  private String getMobileRequestSiteId(DynamoHttpServletRequest pRequest) {
    String siteId = getSiteURLManager().getSiteIdForURL(pRequest.getRequestURI());
    if (siteId != null) {
      if (PROP_VALUE_CHANNEL_MOBILE.equals(getSiteChannel(siteId))) {
        vlogDebug("The request is to a mobile site \"{0}\" (SiteURLManager.getSiteIdForURL(requestURI) used)", siteId);
        return siteId;
      }
    }

    return null;
  }

  /**
   * Checks if given request is a request to desktop site: using the <code>SiteURLManager.getSiteIdForURL(requestURI)</code>.
   *  2) If the 1-st check returns null, iterates over desktop site groups
   *    (i.e. the sites which have a "desktop" channel property value) and selects the first site the request URI
   *    starts with it's "contextRoot" property value.
   * Returns ID of the site, if given request is a request to desktop site. Returns <code>null</code> otherwise.
   *
   * @param pRequest The request to examine.
   * @return ID of the site, if given request is a request to desktop site. Returns <code>null</code> otherwise.
   */
  private String getDesktopRequestSiteId(DynamoHttpServletRequest pRequest) {
    String siteId = getSiteURLManager().getSiteIdForURL(pRequest.getRequestURI());
    if (siteId != null) {
      if (PROP_VALUE_CHANNEL_DESKTOP.equals(getSiteChannel(siteId))) {
        vlogDebug("The request is to a desktop site \"{0}\" (SiteURLManager.getSiteIdForURL() used)", siteId);
        return siteId;
      }
    }

    return null;
  }

  /**
   * Returns <code>true</code> if the context root of given request belongs to any desktop site
   * (the "contextRoot" site property is checked). Returns <code>false</code> otherwise.
   *
   * @param pRequest The request to examine.
   * @return <code>true</code> if the context root of given request belongs to any desktop site
   *         (the "contextRoot" site property is checked). Returns <code>false</code> otherwise.
   */
  private boolean isContextRootOfDesktopSite(DynamoHttpServletRequest pRequest) {
    String requestURI = pRequest.getRequestURI();

    // Iterate over the site groups which include desktop sites (i.e. the sites which have a "desktop" channel property value
    // and the request URI starts with the site "contextRoot" property value)
    Collection<SiteGroup> mobileSiteGroups = getSiteGroupManager().getSiteGroups(getShareableTypeIdForNonMobileSite());
    if (mobileSiteGroups != null) {
      for (SiteGroup siteGroup : mobileSiteGroups) {
        Collection<Site> sites = siteGroup.getSites();
        if (sites != null) {
          for (Site site : sites) {
            String siteContextRoot = (String)site.getPropertyValue("contextRoot");
            if (siteContextRoot != null && requestURI.startsWith(siteContextRoot)) {
              String channel = (String)site.getPropertyValue(getStoreSitePropertiesManager().getChannelPropertyName());
              if (PROP_VALUE_CHANNEL_DESKTOP.equals(channel)) {
                // The request context root corresponds to "contextRoot" of desktop site: we then may use
                // "SiteContextManager.getCurrentSiteId()" to get the current site. For example, when entering
                // CRS/CRS-M using "/crs" URI, the platform would always detect a current site as desktop site
                vlogDebug("The request is to a desktop site \"{0}\" (SiteContextManager.getCurrentSiteId() used)",
                          SiteContextManager.getCurrentSiteId());
                return true;
              }
            }
          }
        }
      }
    }

    return false;
  }

  /**
   * Gets value of the "channel" property of the site identified by given site ID.
   *
   * @param pSiteId Site ID to get a "channel" property of.
   * @return Value of the "channel" property of the site identified by given site ID.
   */
  private String getSiteChannel(String pSiteId) {
    String channelPropertyValue = null;
    try {
      channelPropertyValue = (String)SiteManager.getSiteManager().getSite(pSiteId)
                  .getPropertyValue(getStoreSitePropertiesManager().getChannelPropertyName());
    } catch (Exception ex) {
      vlogError("There was a problem retrieving site \"" + pSiteId + "\".\n", ex);
    }
    return channelPropertyValue;
  }

  /**
   * Returns the Production Site Base URL that corresponds to the passed in <code>pSiteId</code> with additional path
   * and query params included, or <code>null</code> if there is no site that corresponds to the <code>pSiteId</code>.
   * NOTE This method also adds query string to the result, if it's present in the original request.
   *
   * @param pRequest The current request.
   * @param pSiteId the Id of the Site for which the production site base URL should be found.
   * @param pPath optional path string that will be included in the returned URL.
   * @param pConsiderQueryString Whether to consider the request query string (whether to add it to the result).
   * @return The Production Site Base URL that corresponds to pSiteId with additional path and query params included,
   *         or <code>null</code> if there is no site that corresponds to the passed in <code>pSiteId</code>.
   */
  private String getProductionSiteBaseURL(DynamoHttpServletRequest pRequest, String pSiteId, String pPath,
                                          boolean pConsiderQueryString) {
    String productionURL = getSiteURLManager().getProductionSiteBaseURL(pRequest, pSiteId, pPath,
                           (pConsiderQueryString ? pRequest.getQueryString() : null), null, false);
    // Remove possible "jsessionid"
    productionURL = removeJsessionIdFromUrl(productionURL);
    // Remove possible ending slash, if original path does not end with the slash symbol
    if (!pPath.endsWith(SLASH) && productionURL.endsWith(SLASH)) {
      productionURL = productionURL.substring(0, productionURL.length() - 1);
    }
    return productionURL;
  }

  /**
   * Returns the path for given request URL i.e. a part after context root or site production URL.
   * If we succeeded with site detection from URL, we then cut off the site production URL
   * from the request URI and return the result.
   * Otherwise, we cut off the request context path.
   *
   * @param pRequest The request that we want to get path from.
   * @return The path for given request URL i.e. a part after context root or site production URL.
   */
  private String getPath(DynamoHttpServletRequest pRequest) {
    String contextPath;
    String requestURI = pRequest.getRequestURI();
    SiteURLManager siteURLManager = getSiteURLManager();

    String siteId = siteURLManager.getSiteIdForURL(requestURI);
    if (siteId == null) {
      // No site found for the current request URI -> use current site

      // The URI doesn't match to any site production URL, so we assume we are coming in as the default site.
      // So will remove the default context
      contextPath = pRequest.getContextPath();
    } else {
      String productionURL = getProductionSiteBaseURL(pRequest, siteId, SLASH, false);
      if (productionURL.endsWith(SLASH)) {
        productionURL = productionURL.substring(0, productionURL.length() - 1);
      }
      // Will remove production base URL
      contextPath = productionURL;
    }

    // Remove production base URL / context path
    return requestURI.replaceFirst(contextPath, "");
  }

  /**
   * Returns an Endeca preview URL to redirect, if redirection is needed.
   * In the Endeca preview, CRS redirection is needed in the following cases:
   *  1) A site selected from User Segment, using "site." user segments.
   *  2) Mobile page preview.
   * NOTE this method should only be called in Endeca Preview mode.
   *
   * @param pRequest The Endeca preview request.
   * @return URL to redirect to. This URL would be a mobile or desktop.
   *         Value <code>null</code> means NO redirection is needed.
   */
  private String getEndecaPreviewURL4Redirect(DynamoHttpServletRequest pRequest) {
    String requestURI = pRequest.getRequestURI();
    String siteId = getSiteURLManager().getSiteIdForURL(requestURI);

    // Check filtering by the Endeca "User Segment" using "site." user segments (site selection in Endeca preview)
    String endecaUserSegments = pRequest.getParameter(ENDECA_USER_SEGMENTS_PARAM);
    if (endecaUserSegments == null) {
      // From cookie
      endecaUserSegments = pRequest.getCookieParameter(ENDECA_USER_SEGMENTS_PARAM);
    }

    if (endecaUserSegments != null && endecaUserSegments.length() > 0) {
      // Get the '|' delimited preview user segments string
      String[] userSegments = endecaUserSegments.split("\\|");
      for (String userSegment : userSegments) {
        // When the segment starts with "site.", return the site id which is defined after the prefix delimiter
        String userSegmentSitePrefix = getSitePrefix() + getPrefixDelimiter();
        if (userSegment.startsWith(userSegmentSitePrefix)) {
          String siteIdFromUserSegment = userSegment.replaceFirst(userSegmentSitePrefix, "");
          vlogDebug("Endeca preview \"User Segment\" selected: {0}", siteIdFromUserSegment);

          // Only redirect to the site from the "User Segment", if it's NOT a current site
          // (the site is going to be changed)
          if (siteId == null || !siteId.equals(siteIdFromUserSegment)) {
            String channel = getSiteChannel(siteIdFromUserSegment);
            String mobileStorePrefix = getStoreConfiguration().getMobileStorePrefix();

            // Get path to open inside the site specified by the "User Segment"
            String path = getPath(pRequest);
            if (PROP_VALUE_CHANNEL_MOBILE.equals(channel) && path.indexOf(mobileStorePrefix) != 0) {
              // Prepend the path with the "mobile store prefix", if any
              path = mobileStorePrefix + path;
            } else if (PROP_VALUE_CHANNEL_DESKTOP.equals(channel) && path.indexOf(mobileStorePrefix) == 0) {
              // Make sure to remove the "mobile store prefix", if any
              path = path.replaceFirst(mobileStorePrefix, "");
            }
            if (path.length() == 0) {
              path = SLASH;
            }

            return getProductionSiteBaseURL(pRequest, siteIdFromUserSegment, path, true);
          }
        }
      }
    }

    if (siteId != null) {
      // Redirect had already been done previously under preview,
      // when it was impossible to detect a real site using "SiteURLManager.getSiteIdForURL(requestURI)"
      if (isFullSiteEnabled(pRequest)) {
        String channel = getSiteChannel(siteId);
        if (PROP_VALUE_CHANNEL_MOBILE.equals(channel)) {
          // "Mobile > desktop" redirect under preview: with the "Go to full site" in request
          return mapMobile2DesktopURL4Redirect(pRequest, siteId);
        } else if (PROP_VALUE_CHANNEL_DESKTOP.equals(channel)) {
          vlogDebug("- No redirect (desktop site preview, 'Full site' parameter)");
        }
      } else {
        vlogDebug("- No redirect (Endeca preview. {0} site production URL detected)", siteId);
      }
    } else {
      // Check for the "desktop > mobile" redirect under preview
      requestURI = requestURI.replaceFirst(pRequest.getContextPath(), "");
      if (requestURI.startsWith(getStoreConfiguration().getMobileStorePrefix())) {
        // Mobile pages preview: redirect to corresponding mobile site
        return mapDesktop2MobileURL4Redirect(pRequest, null);
      } else {
        vlogDebug("- No redirect (Endeca preview for desktop page)");
      }
    }

    return null;
  }

  /**
   * Given a request, this method returns a mobile URL to redirect to:
   *  1) It gets a path inside the original URL (in order to know a path in destination (mobile) URL).
   *  2) It gets corresponding mobile site ID, using mobile site pairs mechanism.
   *  3) It uses "desktopToMobilePaths" property to get destination path by the original path
   *     (obtained on the step 1).
   *
   * Examples:
   *   - "/crs/storeus" becomes "/crs/mobile/storeus"
   *   - "/crs/storeus/browse/product.jsp" becomes "/crs/mobile/storeus/mobile/browse/product.jsp"
   *
   * @param pRequest The desktop request that we want to create the mobile URL from.
   * @param pDesktopSiteId ID of the desktop site given request belongs to.
   *                       Value <code>null</means> using of the current site ID.
   * @return Mobile URL to redirect to.
   */
  private String mapDesktop2MobileURL4Redirect(DynamoHttpServletRequest pRequest, String pDesktopSiteId) {
    String path = getPath(pRequest);

    if (pDesktopSiteId == null) {
      pDesktopSiteId = SiteContextManager.getCurrentSiteId();
    }

    // Get corresponding mobile site ID:
    //  - From the cache
    //  - From the SiteGroupManager (paired sites)
    String mobileSiteId = mCacheDesktopToMobileSites.get(pDesktopSiteId);
    if (mobileSiteId == null) {
      Collection<String> sharingSites = getSiteGroupManager().getOtherSharingSiteIds(pDesktopSiteId, getShareableTypeIdForMobileSitePairs());
      if (sharingSites == null || sharingSites.size() == 0) {
        vlogError("- No redirect (no paired site found for \"{0}\")", pDesktopSiteId);
        return null;
      } else if (sharingSites.size() > 1) {
        vlogWarning("There are more than one site sharing \"{0}\" with the current site. The first site from the collection will be used",
                    getShareableTypeIdForMobileSitePairs());
      }
      Iterator<String> iter = sharingSites.iterator();
      while (iter.hasNext() && mobileSiteId == null) {
        mobileSiteId = iter.next();
      }
      if (!PROP_VALUE_CHANNEL_MOBILE.equals(getSiteChannel(mobileSiteId))) {
        vlogError("- No redirect (paired site \"{0}\" is not mobile)", mobileSiteId);
        return null;
      } else {
        vlogDebug("Got a paired mobile site \"{0}\"", mobileSiteId);
      }
      mCacheDesktopToMobileSites.put(pDesktopSiteId, mobileSiteId);
    }

    // Check if path is in the "desktopToMobilePaths" map
    String mapToPath = getPathForPath(path, getDesktopToMobilePaths(), mCacheDesktopToMobilePaths);
    if (mapToPath != null) {
      vlogDebug("Got a mobile path \"{0}\" using \"desktopToMobilePaths\" mapping", mapToPath);
    } else {
      // Make sure to get the mobile directory, but don't append if it's already there
      String mobileStorePrefix = getStoreConfiguration().getMobileStorePrefix();
      if (path.indexOf(mobileStorePrefix) != 0) {
        mapToPath = mobileStorePrefix + path;
      } else {
        mapToPath = path;
      }
    }

    return getProductionSiteBaseURL(pRequest, mobileSiteId, mapToPath, true);
  }

  /**
   * This function is used to check, if a path mapping with the subsequent redirect is needed.
   * It may be used in the following cases:
   *  1) When we request a mobile site with invalid path: in order to correct the path.
   *     For example, "/browse" is invalid path in mobile site, and this method would map this path to the "/mobile/browse".
   *  2) When we request a desktop site and know the destination mobile site (the <code>pMobileSiteId</code>).
   * Destination URI is constructed as the destination mobile site production URL plus the result of the path mapping.
   *
   * Examples:
   *   - "/crs/mobile/storeus/browse" becomes "/crs/mobile/storeus/mobile/browse"
   *
   * @param pRequest The current request.
   * @param pMobileSiteId ID of the mobile site that we want to create the redirection URL for.
   * @return Mobile URL to redirect to. Value <code>null</code> means NO redirection is needed.
   */
  private String mapPath4Redirect(DynamoHttpServletRequest pRequest, String pMobileSiteId) {
    if (pMobileSiteId != null) {
      String path = getPath(pRequest);
      String mapToPath = getPathForPath(path, getDesktopToMobilePaths(), mCacheDesktopToMobilePaths);
      if (mapToPath != null) {
        vlogDebug("Got a mobile path \"{0}\" using \"desktopToMobilePaths\" mapping", mapToPath);
      } else {
        // Make sure to get the mobile directory, but don't append if it's already there
        String mobileStorePrefix = getStoreConfiguration().getMobileStorePrefix();
        if (path.indexOf(mobileStorePrefix) != 0) {
          mapToPath = mobileStorePrefix + path;
        }
      }

      if (mapToPath != null && !path.equals(mapToPath)) {
        vlogDebug("Redirect from \"{0}\" to \"{1}\" path inside the same site \"{2}\"", path, mapToPath, pMobileSiteId);
        return getProductionSiteBaseURL(pRequest, pMobileSiteId, mapToPath, true);
      }
    }

    return null;
  }

  /**
   * Given a request, this method returns a desktop URL to redirect to:
   *  1) It gets a path inside the original URL (in order to know a path in destination (desktop) URL).
   *  2) It gets corresponding desktop site ID, using mobile site pairs mechanism.
   *  3) It uses "mobileToDesktopPaths" property to get destination path by the original path (obtained on the step 1).
   *  4) It additionally uses "mobileToDesktopPaths" property to get destination path by the original path (obtained on the step 1).
   *
   * Examples:
   *   - "/crs/mobile/storeus" becomes "/crs/storeus"
   *   - "/crs/mobile/storeus/mobile/browse/product.jsp" becomes "/crs/storeus/browse/product.jsp"
   *
   * @param pRequest The mobile request that we want to create the desktop URL from.
   * @param pMobileSiteId ID of the mobile site given request belongs to.
   *                      Value <code>null</means> using of the current site ID.
   * @return Desktop URL to redirect to.
   */
  private String mapMobile2DesktopURL4Redirect(DynamoHttpServletRequest pRequest, String pMobileSiteId) {
    String path = getPath(pRequest);

    if (pMobileSiteId == null) {
      pMobileSiteId = SiteContextManager.getCurrentSiteId();
    }

    // Get corresponding desktop site ID:
    //  - From the cache
    //  - From the SiteGroupManager (paired sites)
    String siteId = mCacheMobileToDesktopSites.get(pMobileSiteId);
    if (siteId == null) {
      Collection<String> sharingSites = getSiteGroupManager().getOtherSharingSiteIds(pMobileSiteId, getShareableTypeIdForMobileSitePairs());
      if (sharingSites == null || sharingSites.size() == 0) {
        vlogError("- No redirect (no paired site found for \"{0}\")", pMobileSiteId);
        return null;
      } else if (sharingSites.size() > 1) {
        vlogWarning("There are more than one site sharing {0} with the current site. The first site from the collection will be used",
                    getShareableTypeIdForMobileSitePairs());
      }
      Iterator<String> iter = sharingSites.iterator();
      while (iter.hasNext() && siteId == null) {
        siteId = iter.next();
      }
      if (!PROP_VALUE_CHANNEL_DESKTOP.equals(getSiteChannel(siteId))) {
        vlogError("- No redirect (paired site \"{0}\" is not desktop)", siteId);
        return null;
      } else {
        vlogDebug("Got a paired desktop site \"{0}\"", siteId);
      }
      mCacheMobileToDesktopSites.put(pMobileSiteId, siteId);
    }

    // First, check if path is in the "mobileToDesktopPaths" map
    boolean pathFoundInMap = false;
    String mapToPath = getPathForPath(path, getMobileToDesktopPaths(), mCacheMobileToDesktopPaths);
    if (mapToPath != null) {
      path = mapToPath;
      vlogDebug("Got a desktop path \"{0}\" using \"mobileToDesktopPaths\" mapping", path);
      pathFoundInMap = true;
    } else {
      // Second, check if path is in the "desktopToMobilePaths" map
      Map<String, String> desktopToMobilePaths = getDesktopToMobilePaths();
      for (Map.Entry<String, String> mapEntry : desktopToMobilePaths.entrySet()) {
        if (path.startsWith(mapEntry.getValue())) {
          path = mapEntry.getKey();
          vlogDebug("Got a desktop path \"{0}\" using \"desktopToMobilePaths\" mapping", path);
          pathFoundInMap = true;
          break;
        }
      }
    }

    if (!pathFoundInMap) {
      // Make sure to remove the mobile directory, if it's included in the path
      String mobileStorePrefix = getStoreConfiguration().getMobileStorePrefix();
      if (path.indexOf(mobileStorePrefix) == 0) {
        path = path.replaceFirst(mobileStorePrefix, "");
      }
    }

    if (path.length() == 0) {
      path = SLASH;
    }

    return getProductionSiteBaseURL(pRequest, siteId, path, true);
  }

  /**
   * Remove "jsessionId" from the specified URL, if present.
   *
   * @param pURL The URL from which to remove "jsessionid", if any.
   * @return pURL without "jsessionId", or the original URL if no "jsessionId" was present.
   */
  private String removeJsessionIdFromUrl(String pURL) {
    int idxStart = pURL.indexOf(";jsessionid=");
    if (idxStart != -1) {
      int idxEnd = pURL.indexOf("?", idxStart);
      if (idxEnd == -1) {
        idxEnd = pURL.indexOf("#", idxStart);
      }

      return (idxEnd == -1) ? pURL.substring(0, idxStart) : (pURL.substring(0, idxStart) + pURL.substring(idxEnd));
    }
    return pURL;
  }

  /**
   * Returns <code>true</code> if the full site has been enabled for mobile device. Returns <code>false</code> otherwise.
   * That means, when entering a full site using mobile browser with the "full site" parameter set to true,
   * a mobile user enters the full site without any redirects.
   *
   * @param pRequest the current request.
   * @return true if the full site has been enabled. Returns false otherwise.
   */
  private boolean isFullSiteEnabled(HttpServletRequest pRequest) {
    String enableFullSite = pRequest.getParameter(getEnableFullSiteParam());
    if (enableFullSite != null) {
      return Boolean.valueOf(enableFullSite);
    }

    return false;
  }

  /**
   * Returns mapping path for given request path, using given "path to path" map and given cache.
   *
   * @param pPath Request path (request URI minus context path).
   * @param pMap "path to path" (for example, "mobile to desktop path") map.
   * @param pCache The cache. Should NOT be null.
   * @return <code>PathDetermination</code> structure for given request path, using given "path to path" map.
   *        null if no mappings found.
   */
  private String getPathForPath(String pPath, Map<String, String> pMap, LRUMap<String, PathDetermination> pCache) {
    PathDetermination pathDesc = getPathDescForPath(pPath, pMap, pCache);
    if (pathDesc != null) {
      return pathDesc.getPath();
    }
    return null;
  }

  /**
   * Returns <code>PathDetermination</code> structure for given request path, using given "path to path" map and given cache.
   *
   * @param pPath Request path (request URI minus context path).
   * @param pMap "path to path" (for example, "mobile to desktop path") map.
   * @param pCache The cache. Should NOT be null.
   * @return <code>PathDetermination</code> structure for given request path, using given "path to path" map.
   *        null if no mappings found.
   */
  private PathDetermination getPathDescForPath(String pPath, Map<String, String> pMap, LRUMap<String, PathDetermination> pCache) {
    PathDetermination pathDesc = null;
    boolean cacheHit = false;

    mLock.readLock().lock();
    try {
      // Check the cache to see if we have an exact match for the path
      pathDesc = pCache.get(pPath);
      if (pathDesc != null) {
        cacheHit = true;
      } else {
        // If no match in the cache, then check the path map for a match against the full path
        pathDesc = getPathDescForPath(pPath, pMap);
      }
    } finally {
      mLock.readLock().unlock();
    }
    // end synchronized

    // If the path isn't null, cache it with the passed in path
    if (pathDesc != null && !cacheHit) {
      mLock.writeLock().lock();
      try {
        pCache.put(pPath, pathDesc);
      } finally {
        mLock.writeLock().unlock();
      }
    }

    return pathDesc;
  }

  /**
   * Returns <code>PathDetermination</code> structure for given request path, using given "path to path" map.
   *
   * @param pPath Request path (request URI minus context path).
   * @param pMap "path to path" (for example, "mobile to desktop path") map.
   * @return <code>PathDetermination</code> structure for given request path, using given "path to path" map.
   *        null if no mappings found.
   */
  private PathDetermination getPathDescForPath(String pPath, Map<String, String> pMap) {
    if (pMap == null || pPath == null) {
      return null;
    }

    String path = pMap.get(pPath);
    if (path != null) {
      return new PathDetermination(path, pPath);
    }

    PathDetermination pathDesc = null;
    // Check to see if there's a match if we hack off portions of the path
    String trimmedPath = trimLevel(pPath);
    boolean noMoreLevels = false;
    while (!noMoreLevels && path == null) {
      path = pMap.get(trimmedPath);
      if (path == null) {
        trimmedPath = trimLevel(trimmedPath);
        if (trimmedPath == null) {
          noMoreLevels = true;
        }
      } else {
        pathDesc = new PathDetermination(path, trimmedPath);
      }
    }

    return pathDesc;
  }

  /**
   * Trim one directory level off the given URI.
   *
   * @param pURI URI to trim one directory level off, if any.
   * @return URI with one directory level trimmed off.
   */
  private static String trimLevel(String pURI) {
    if (pURI == null || SLASH.equals(pURI)) {
      return null;
    }

    int lastIndexOfSlash = pURI.lastIndexOf(SLASH);
    if (lastIndexOfSlash < 0) {
      return null;
    }
    if (lastIndexOfSlash == 0) {
      return SLASH;
    }
    return pURI.substring(0, lastIndexOfSlash);
  }


  /**
   * An object that contains the mapping path and the part of the path that was used to determine the path.
   */
  class PathDetermination {
    /** The mapping path. */
    private String mPath;
    /** The path matching string: the part of the path that was used to determine the path. */
    private String mPathMatch;

    /**
     * Constructor.
     * Creates a <code>PathDetermination</code> object with the given path and path matching string.
     *
     * @param pPath The path.
     * @param pPathMatch The path string that was used for site determination.
     */
    public PathDetermination(String pPath, String pPathMatch) {
      mPath = pPath;
      mPathMatch = pPathMatch;
    }

    /**
     * Gets the path.
     * @return The path.
     */
    public String getPath() {
      return mPath;
    }

    /**
     * Gets the matching path string.
     * @return the matching path string.
     */
    public String getPathMatch() {
      return mPathMatch;
    }
  }
}
