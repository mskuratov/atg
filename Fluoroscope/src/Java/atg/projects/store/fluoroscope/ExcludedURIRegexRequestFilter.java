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



package atg.projects.store.fluoroscope;

import java.util.regex.Pattern;

import atg.service.fluoroscope.RequestFilter;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

/**
 * This implementation of the <code>RequestFilter</code> excludes requests with URI matched by the regex specified with the
 * <code>excludedUriRegex</code> property.
 * 
 * @see RequestFilter
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Fluoroscope/src/atg/projects/store/fluoroscope/ExcludedURIRegexRequestFilter.java#2 $$Change: 768606 $ 
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class ExcludedURIRegexRequestFilter implements RequestFilter {
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Fluoroscope/src/atg/projects/store/fluoroscope/ExcludedURIRegexRequestFilter.java#2 $$Change: 768606 $";
  
  private String mExcludedUriRegex;
  private Pattern mExcludedUriRegexPattern;

  /**
   * This property defines a regex to be used to define, if the request should be excluded.
   * It defines all request URIs to be excluded from the Fluoroscope procession.
   * @return URI regex.
   */
  public String getExcludedUriRegex() {
    return mExcludedUriRegex;
  }

  public void setExcludedUriRegex(String pExcludedUriRegex) {
    mExcludedUriRegex = pExcludedUriRegex;
  }
  
  /**
   * This method compiles a regex {@link Pattern} to be used later.
   * @return {@code Pattern} instance to be used.
   */
  protected Pattern getExcludedUriRegexPattern() {
    return mExcludedUriRegexPattern == null ? mExcludedUriRegexPattern = Pattern.compile(getExcludedUriRegex()) : mExcludedUriRegexPattern;
  }

  /**
   * {@inheritDoc}
   * <br/>
   * Current implementation obtains actual request URI (i.e. URI before any <code>&lt;dsp:include&gt;</code> tag met) and
   * matches it against a regex defined with the <code>excludedUriRegex</code> property. If matched, request will be ignored.
   */
  @Override
  public boolean isRequestExcluded(DynamoHttpServletRequest pRequest) {
    // Get actual request URI. If <dsp:include> tag used, getRequestURI method will return wrong value.
    // Hence we have to call a special method to define initial request URI.
    String actuialRequestUri = ServletUtil.getCurrentRequestURI(pRequest);
    return getExcludedUriRegexPattern().matcher(actuialRequestUri).matches();
  }

}
