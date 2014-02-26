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



package atg.projects.store.profile;

import java.util.Collection;
import java.util.ResourceBundle;

import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.servlet.ServletUtil;

/**
 * This class implements internationalization support for ProfileTools component.
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/src/atg/projects/store/profile/InternationalStoreProfileTools.java#2 $$Change: 768606 $ 
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class InternationalStoreProfileTools extends StoreProfileTools {
  /**
   * Class version
   */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/src/atg/projects/store/profile/InternationalStoreProfileTools.java#2 $$Change: 768606 $";
  
  /**
   * Resource bundle name with profile-related resources.
   */
  protected static final String PROFILE_RESOURCE_BUNDLE_NAME = "atg.commerce.profile.Resources";
  /**
   * Resource name for default address name.
   */
  protected static final String ADDRESS_NICKNAME_PREFIX = "addressNicknamePrefix";

  /**
   * Overrides base method to use layered resource bundle for address default prefix.
   */
  @Override
  public String getUniqueAddressNickname (Object pAddress, Collection pNicknames, String pNewNickname) {
    String newNickname = pNewNickname;
    if (StringUtils.isBlank(pNewNickname)) { // Do we actually need to do anything?
      // We will construct address name from this draft. 
      ResourceBundle profileResources = LayeredResourceBundle.getBundle(PROFILE_RESOURCE_BUNDLE_NAME, ServletUtil.getUserLocale());
      newNickname = ResourceUtils.getMsgResource(ADDRESS_NICKNAME_PREFIX, PROFILE_RESOURCE_BUNDLE_NAME, profileResources);
    }
    return super.getUniqueAddressNickname(pAddress, pNicknames, newNickname);
  }
}
