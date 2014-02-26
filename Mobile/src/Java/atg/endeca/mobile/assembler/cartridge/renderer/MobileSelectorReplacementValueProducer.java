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
package atg.endeca.mobile.assembler.cartridge.renderer;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.endeca.infront.assembler.ContentItem;

import atg.endeca.assembler.cartridge.renderer.SelectorReplacementValueProducer;
import atg.projects.store.mobile.MobileStoreConfiguration;
import atg.projects.store.mobile.link.MobileDetectionInterceptor;
import atg.projects.store.profile.SessionBean;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

/**
 * "B2CStore mobile" extension over the "SelectorReplacementValueProducer" class.
 *
 * This class introduces "selectorFolder" property, which value is "/{selector}"
 * ("{selector}" is specified in the "browserTypeToSelectorName" property).
 *
 * @author Andrei Urbanovich
 */
public class MobileSelectorReplacementValueProducer extends SelectorReplacementValueProducer {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Mobile/src/atg/endeca/mobile/assembler/cartridge/renderer/MobileSelectorReplacementValueProducer.java#10 $$Change: 794302 $";

  // -------------------------------------
  // Constants
  // -------------------------------------
  /** What we use to represent a <code>null</code> selector. */
  static final String NULL_SELECTOR = "*null*";
  /** Selector for mobile browsers. */
  static final String MOBILE_SELECTOR = "mobile";
  /** Request attribute to cache the mobile selector in request. */
  static final String ATTR_MOBILE_SELECTOR = "atg.endeca.mobile.assembler.cartridge.renderer.MobileSelectorReplacementValueProducer.selector";


  // -------------------------------------
  // Properties
  // -------------------------------------

  //-------------------------------------
  // property: selectorFolderKeyName
  //
  private String mSelectorFolderKeyName = "selectorFolder";
  /**
   * Gets the "selectorFolderKeyName" property.
   * The key to associate with the selector "folder", which is the selector prefixed with "/".
   */
  public String getSelectorFolderKeyName() {
    return mSelectorFolderKeyName;
  }
  /**
   * Sets the "selectorFolderKeyName" property.
   * The key to associate with the selector "folder", which is the selector prefixed with "/".
   */
  public void setSelectorFolderKeyName(String pSelectorFolderKeyName) {
    mSelectorFolderKeyName = pSelectorFolderKeyName;
  }

  //-------------------------------------
  // property: enableFullSiteParam
  //
  // The name of the URL parameter which forces switching to the full site.
  private String mEnableFullSiteParam;
  /**
   * Gets the "enable full site param" property. Defaults to {@link MobileDetectionInterceptor#FULL_SITE_PARAM}.
   * @return The "enable full site param" property. Defaults to {@link MobileDetectionInterceptor#FULL_SITE_PARAM}.
   */
  public String getEnableFullSiteParam() {
    if (mEnableFullSiteParam != null) {
      return mEnableFullSiteParam;
    }
    return MobileDetectionInterceptor.FULL_SITE_PARAM;
  }
  /**
   * Sets the "enable full site param" property.
   * @param pEnableFullSiteParam The "enable full site param" property.
   */
  public void setEnableFullSiteParam(String pEnableFullSiteParam) {
    mEnableFullSiteParam = pEnableFullSiteParam;
  }

  //-------------------------------------
  // property: previewEnabled
  //
  // Is Endeca Preview enabled?
  private boolean mPreviewEnabled = true;
  /**
   * Gets the "Is Endeca Preview enabled?" property.
   * @return The "Is Endeca Preview enabled?" property.
   */
  public boolean isPreviewEnabled() {
    return mPreviewEnabled;
  }
  /**
   * Sets the "Is Endeca Preview enabled?" property.
   * @param pPreviewEnabled The "Is Endeca Preview enabled?" property.
   */
  public void setPreviewEnabled(boolean pPreviewEnabled) {
    mPreviewEnabled = pPreviewEnabled;
  }

  //-------------------------------------
  // property: sessionBean
  //
  private String mSessionBean;
  /**
   * Gets the "session bean" property.
   * @return The "session bean" property.
   */
  public String getSessionBean() {
    return mSessionBean;	
  }
  /**
   * Sets the "session bean" property.
   * @param pSessionBean The "session bean" property.
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

  // -------------------------------------
  // Public Methods
  // -------------------------------------
  /**
   * @inheritDoc
   *
   * In addition to the super class method, it optionally checks for the browser type
   * using the "Endeca Preview device" user agent query parameter value.
   */
  @Override
  protected String calculateSelector(ContentItem pContentItem, HttpServletRequest pRequest) {
    String strSelector = trimToNull(pRequest.getParameter(getSelectorOverrideParameterName()));
    if (strSelector != null) {
      return strSelector;
    }

    if (getBrowserTypeToSelectorName() != null) {
      DynamoHttpServletRequest dynamoRequest = ServletUtil.getDynamoRequest(pRequest);
      if (dynamoRequest == null) {
        vlogWarning("Unable to determine browser type, because Dynamo request was not available");
        return null;
      }

      Map<String, String> btypeToSelector = getBrowserTypeToSelectorName();
      for (Map.Entry<String, String> entryCur : btypeToSelector.entrySet()) {
        // Check browser type from the request "User-Agent" header
        if (dynamoRequest.isBrowserType(entryCur.getKey())) {
          return entryCur.getValue();
        }
      }

      String endecaBrowserType = MobileDetectionInterceptor.getEndecaUserAgent(dynamoRequest);
      if (endecaBrowserType != null && isPreviewEnabled()) {
        // Under Endeca preview, we are not able to detect device "User-Agent" type if the device is not selected
        String endecaRequestURI = pRequest.getRequestURI();
        endecaRequestURI = endecaRequestURI.replaceFirst(pRequest.getContextPath(), "");
        if (endecaRequestURI.startsWith(getStoreConfiguration().getMobileStorePrefix())) {
          // Mobile pages preview
          return MOBILE_SELECTOR;
        }
      }

      // BCC preview
      SessionBean sessionBean = (SessionBean)dynamoRequest.resolveName(getSessionBean());
      Boolean isBccPreviewForMobile = (Boolean)sessionBean.getValues().get(MobileDetectionInterceptor.BCC_PREVIEW_IS_SITE_MOBILE);
      if (isBccPreviewForMobile != null && isBccPreviewForMobile.equals(Boolean.TRUE)) {
        return MOBILE_SELECTOR;
      }
    }

    return null;
  }

  @Override
  public void addReplacementValues(Map<String, String> pMap, ContentItem pContentItem, HttpServletRequest pRequest) {
    String strSelector = (String)pRequest.getAttribute(ATTR_MOBILE_SELECTOR);
    if (strSelector == null) {
      strSelector = calculateSelector(pContentItem, pRequest);

      if (isCacheSelectorOnRequest()) {
        // Cache it. Caching NULL_SELECTOR if the value is null
        pRequest.setAttribute(ATTR_MOBILE_SELECTOR, (strSelector != null) ? strSelector : NULL_SELECTOR);
      }
    }

    if (NULL_SELECTOR.equals(strSelector)) {
      strSelector = null;
    }

    // Do NOT add "selector folder" if the "full site" parameter from the "MobileDetectionInterceptor" is enabled:
    // in this case, we need to refer a full CRS cartridge
    if (strSelector != null && !isFullSiteEnabled(pRequest)) {
      pMap.put(getSelectorFolderKeyName(), "/" + strSelector);
    }
  }

  // -------------------------------------
  // Private Methods
  // -------------------------------------
  /**
   * Returns <code>true</code> if the full site has been enabled for mobile device. <code>false</code> otherwise.
   * That means, when entering a full site using mobile browser with the "full site" parameter set to true,
   * a mobile user enters the full site without any redirects.
   *
   * @param pRequest the current request.
   * @return true if the full site has been enabled. Returns false otherwise.
   */
  private boolean isFullSiteEnabled(HttpServletRequest pRequest) {
    // Check the URL for the parameter that toggles full site
    String enableFullSite = pRequest.getParameter(getEnableFullSiteParam());
    if (enableFullSite != null) {
      return Boolean.valueOf(enableFullSite);
    }

    return false;
  }
}
