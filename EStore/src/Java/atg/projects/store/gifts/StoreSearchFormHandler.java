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



package atg.projects.store.gifts;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletException;

import atg.commerce.gifts.SearchFormHandler;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;

/**
 * EStore version of DCS' GiftlistSearch form handler. Resets form errors when necessary. 
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/gifts/StoreSearchFormHandler.java#3 $$Change: 788278 $ 
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreSearchFormHandler extends SearchFormHandler
{
  /**
   * Class version
   */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/gifts/StoreSearchFormHandler.java#3 $$Change: 788278 $";
  
  private static final String FIRST_NAME_KEY = "firstName";
  private static final String LAST_NAME_KEY = "lastName";
  
  /**
   * Resource bundle name.
   */
  private static final String MY_RESOURCE_NAME = "atg.commerce.gifts.UserMessages";

  /**
   * Invalid e-mail format message key.
   */
  protected static final String MSG_EMPTY_FIRST_LAST_NAME = "emptyFirstLastName";
  
  /**
   * This handler clears form inputs for advanced search along with form exceptions.
   * 
   * @param pRequest - the HTTP request.
   * @param pResponse - the HTTP response.
   * 
   * @return always <code>true</code>.
   * 
   * @throws ServletException if something goes wrong.
   * @throws IOException if something goes wrong.
   */
  public boolean handleClearForm(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    // Reset form exceptions
    resetFormExceptions();
    
    // Clear form inputs
    setPropertyValues(null);
    
    return true;
  }
  
  /**
   * Validates entered first and last name.
   * 
   * @param pRequest - the HTTP request.
   * @param pResponse - the HTTP response. 
   * 
   * @throws ServletException if something goes wrong.
   * @throws IOException if something goes wrong.
   */
  public void preSearch(DynamoHttpServletRequest pRequest,
                        DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    
    if (!validateSearchInput()){
      addFormException(new DropletException(
        formatUserMessage(MSG_EMPTY_FIRST_LAST_NAME, pRequest, pResponse), MSG_EMPTY_FIRST_LAST_NAME));
      
      return;
    }
    
    super.preSearch(pRequest, pResponse);
  }
  
  /**
   * Ensures that at least one of the first name or last name fields is not empty.
   * 
   * @return true if at least one of the name's fields is not empty, otherwise false.
   */
  public boolean validateSearchInput() {
    
    String firstName = (String) getPropertyValues().get(FIRST_NAME_KEY);
    String lastName = (String) getPropertyValues().get(LAST_NAME_KEY);
    
    // If both first name and last name are empty return false
    if (StringUtils.isEmpty(firstName) && StringUtils.isEmpty(lastName)){
      return false;
    }
    
    return true;
  }
  
  /**
   * Retrieves a message from default resource bundle. Resource bundle is defined 
   * with {@link #MY_RESOURCE_NAME} field.
   * 
   * @param pResourceKey - key to be searched within a resource bundle.
   * @param pRequest - the HTTP request.
   * @param pResponse - the HTTP response
   * 
   * @return string obtained from the resource bundle.
   */
  protected String formatUserMessage(String pResourceKey, 
                                     DynamoHttpServletRequest pRequest, 
                                     DynamoHttpServletResponse pResponse) {
    
    RequestLocale requestLocale = pRequest.getRequestLocale();
    Locale currentLocale = requestLocale == null ? Locale.getDefault() : requestLocale.getLocale();
    ResourceBundle bundle = LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, currentLocale);
    
    return bundle.getString(pResourceKey);
  }
}
