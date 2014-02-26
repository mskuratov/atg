/*<ORACLECOPYRIGHT>
 * Copyright (C) 1994-2012 Oracle and/or its affiliates. All rights reserved.
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
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.endeca.cache.DimensionValueCacheObject;
import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.projects.store.cache.StoreDimensionValueCacheTools;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * <p>
 *   This droplet's main purpose is to determine an Endeca driven target link URL.
 * </p>
 * <p>  
 *   The URL passed into this droplet can be in any format but a substitution will only 
 *   be attempted with what's between particular opening and closing tokens (if they exist). For 
 *   the substitution to be attempted, the <code>url</code> value, should be in the form of:  
 *   
 *   <ul>
 *     <li>/crs/storeus{categoryId=catMen}</li>
 *   </ul>
 *   
 *   The item ID key and repository ID will then be stripped from between the opening and closing tokens, 
 *   and the corresponding Endeca dimension value URL will be determined. So the above URL will be 
 *   modified to the following form:
 *   
 *   <ul>
 *     <li>/crs/storeus/browse?N=10115</li>
 *   </ul>
 *   
 *   or for an SEO formatted URL:
 *   
 *   <ul> 
 *     <li>/crs/storeus/browse/_/Category_Men/N_7tq</li>
 *   </ul>
 * </p>
 * <p>
 *   Input Paramaters:
 *   <ul>
 *     <li>
 *       url - A <code>url</code> that will be processed to try to generate a corresponding Endeca driven targetLinkURL.
 *     </li>
 *   </ul
 * </p>
 * <p>          
 *   Open Parameters:
 *   <ul>
 *     <li>output - Always services a targetLinkUrl.</li>
 *   </ul>  
 * </p>
 * <p> 
 *   Output Parameters:  
 *    <ul>
 *      <li>
 *        targetLinkURL - The value of this parameter will be one of three values:
 *        <ul>
 *          <li>
 *            A modified URL that has substituted whats between the opening and 
 *            closing tokens with a corresponding Endeca dimension value ID.
 *          </li>
 *          <li>
 *            The original passed in URL when no opening and closing tokens have been found.
 *          </li>
 *          <li>
 *            An empty string when the original passed in URL is empty or when 
 *            a corresponding dimension value ID can't be found.
 *          </li>
 *        </ul>
 *      </li>
 *    </ul>
 * </p>
 * <p>
 *   Example:
 *   <pre>
 *     &lt;dsp:droplet name="/atg/endeca/store/droplet/TargetLinkURLDroplet"&gt;
 *       &lt;dsp:param name="url" param="promotion.media.targetLink.url"/&gt;
 *     
 *       &lt;dsp:oparam name="output"&gt;
 *         &lt;dsp:getvalueof var="targetLinkURL" param="targetLinkURL"/&gt;
 *       &lt;/dsp:oparam&gt;
 *     &lt;/dsp:droplet&gt;
 *   </pre>
 * </p>
 * 
 * @author David Stewart
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/droplet/TargetLinkURLDroplet.java#3 $$Change: 793981 $
 * @updated $DateTime: 2013/03/01 04:54:26 $$Author: dstewart $
 */
public class TargetLinkURLDroplet extends DynamoServlet {

  //----------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/droplet/TargetLinkURLDroplet.java#3 $$Change: 793981 $";

  //--------------------------------------------------------------------------
  //  CONSTANTS
  //--------------------------------------------------------------------------

  /** url parameter name. */
  public static final ParameterName URL = ParameterName.getParameterName("url");

  /** Target link URL output parameter name. */
  public static final String TARGET_LINK_URL = "targetLinkURL";

  /** The key that is used to reference a category ID */
  public static final String CATEGORY_ID = "categoryId";
  
  /** The key/ID separator character */
  public static final char ID_SEPARATOR = '=';
  
  /** Output parameter name. */
  public static final String OUTPUT = "output";

  //--------------------------------------------------------------------------
  //  PROPERTIES
  //--------------------------------------------------------------------------
  
  //---------------------------------------------------------------------
  //  property: dimensionValueCacheTools
  //---------------------------------------------------------------------
  private StoreDimensionValueCacheTools mDimensionValueCacheTools = null;
                                         
  /**
   * @param pDimensionValueCacheTools - The tools class for the DimensionValueCache.
   */
  public void setDimensionValueCacheTools(StoreDimensionValueCacheTools pDimensionValueCacheTools) {
    mDimensionValueCacheTools = pDimensionValueCacheTools;
  }
  
  /**
   * @return the tools class for the DimensionValueCache.
   */
  public StoreDimensionValueCacheTools getDimensionValueCacheTools() {
    return mDimensionValueCacheTools;
  }
  
  //----------------------------------------
  //  property: enclosingStartToken
  //----------------------------------------
  private String mEnclosingStartToken = "{";
  
  /**
   * @param pEnclosingStartToken - The start token that encloses the category ID.
   */
  public void setEnclosingStartToken(String pEnclosingStartToken) {
    mEnclosingStartToken = pEnclosingStartToken;
  }
  
  /**
   * @return the start token that encloses the category ID.
   */
  public String getEnclosingStartToken() {
    return mEnclosingStartToken;
  }
  
  //--------------------------------------
  //  property: enclosingEndToken
  //--------------------------------------
  private String mEnclosingEndToken = "}";
  
  /**
   * @param pEnclosingEndToken - The end token that encloses the category ID. 
   */
  public void setEnclosingEndToken(String pEnclosingEndToken) {
    mEnclosingEndToken = pEnclosingEndToken;
  }
  
  /**
   * @return the end token that encloses the category ID.
   */
  public String getEnclosingEndToken() {
    return mEnclosingEndToken;
  }
  
  //--------------------------------------------------------------------------
  // METHODS
  //--------------------------------------------------------------------------

  //--------------------------------------------------------------------------
  /**
   * See class description - {@link TargetLinkURLDroplet}.
   *
   * @param pRequest - DynamoHttpSevletRequest.
   * @param pResponse - DynamoHttpServletResponse.
   * 
   * @throws ServletException - if an error occurs.
   * @throws IOException - if an error occurs.
   */
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    // The URL to be modified.
    String url = (String) pRequest.getObjectParameter(URL);
    
    if (StringUtils.isEmpty(url)) {
      if (isLoggingDebug()) {
        logDebug("The 'url' parameter is empty. No target link URL will be created.");
      }
      
      pRequest.setParameter(TARGET_LINK_URL, "");
    }
    else {
    
      String modifiedURL = "";
      
      // Get the indexes of the tokens that enclose the key/value.
      int openingTokenIndex = url.indexOf(getEnclosingStartToken());
      int closingTokenIndex = url.lastIndexOf(getEnclosingEndToken());
      
      // Ensure that the key/value is enclosed between tokens.
      if (openingTokenIndex != -1 && closingTokenIndex != -1) {
  
        // Get the index of the key/ID separator character e.g. categoryId=cat12345.
        int idSeparator = url.indexOf(ID_SEPARATOR);
  
        // Get the key that defines the item type, e.g. categoryId.
        String key = url.substring((openingTokenIndex + 1), idSeparator);
        
        if (key.equals(CATEGORY_ID)) {
          // The category ID that follows the ID separator character.
          String categoryId = url.substring((idSeparator + 1), closingTokenIndex);
          
          if (!StringUtils.isEmpty(categoryId)) {
            // Strip the category ID from between the curly braces and retrieve it's 
            // corresponding DimensionValueCacheObject.
            List<DimensionValueCacheObject> dimValId = getDimensionValueCacheTools().get(categoryId);
            
            if (dimValId != null) {
              // Build the updated URL.
              modifiedURL = url.substring(0, openingTokenIndex) + dimValId.get(0).getUrl();
              pRequest.setParameter(TARGET_LINK_URL, modifiedURL);
              
              vlogDebug("The targetLinkURL created from {0} is: {1}", url, modifiedURL);
            }
            else {
              vlogDebug("No dimension value ID can be found for category ID: {0}", categoryId);
              pRequest.setParameter(TARGET_LINK_URL, "");
            }
          }
          else {
            vlogDebug("No category ID value has been defined in URL: {0}", url);
            pRequest.setParameter(TARGET_LINK_URL, "");
          }
        }
      }
      else {
        vlogDebug("No opening and closing tokens have been found in the URL. The targetLinkURL will be set to {0}: ", url);
        pRequest.setParameter(TARGET_LINK_URL, url);
      }
    }
    
    pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
  }
  
}