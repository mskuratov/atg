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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.droplet.CurrencyFormatter;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * CRS extension of CurrencyFormatter droplet with possibility to specify custom currency
 * formats for some locales. The droplet defines a map <code>customCurrencyPatterns</code>
 * property where keys are locales and values are custom format patterns.
 *  
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/StoreCurrencyFormatter.java#2 $$Change: 791340 $
 * @updated $DateTime: 2013/02/19 09:03:40 $$Author: abakinou $
 */

public class StoreCurrencyFormatter extends CurrencyFormatter{
    
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/StoreCurrencyFormatter.java#2 $$Change: 791340 $";

    
  //-------------------------------
  // Property: customCurrencyPatterns
  private Map<String, String> mCustomCurrencyPatterns;
    
    
  /**
   * Returns the map with custom currency patterns where keys
   * are locales and values are custom patterns for the locale.
   * @return The map with custom currency patterns where keys
   * are locales and values are custom patterns for the locale.
   */
  public Map<String, String> getCustomCurrencyPatterns() {
    return mCustomCurrencyPatterns;
  }


  /**
   * Sets the map with custom currency patterns where keys
   * are locales and values are custom patterns for the locale.
   * @param pCustomCurrencyPatterns The map with custom currency patterns where keys
   * are locales and values are custom patterns for the locale.
   */
  public void setCustomCurrencyPatterns(Map<String, String> pCustomCurrencyPatterns) {
    mCustomCurrencyPatterns = pCustomCurrencyPatterns;
  }

  /**
   * The method checks whether custom currency format is specified for the
   * current locale and if so formats the currency with the specified format.
   * If not the super method is called.
   */
  @Override
  protected String formatCurrency(DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse)
    throws ServletException,IOException {
  
    // Get a locale that should be used for currency formatting
    Locale locale = getCurrencyLocale(pRequest, pResponse);
    
    // Is there custom format specified for the current locale?
    String customPattern = getCustomCurrencyPattern(locale.toString());
    
    if (!StringUtils.isEmpty(customPattern)){
      Number currency = getCurrency(pRequest, pResponse);
      DecimalFormat formatter = (DecimalFormat)NumberFormat.getCurrencyInstance(locale);
      formatter.applyPattern(customPattern);
      synchronized (formatter) {
        return formatter.format(currency);
      }
    }

    // No custom format for the locae, call the super method.
    return super.formatCurrency(pRequest, pResponse);
  }
  
  /**
   * Returns custom currency pattern for the specified locale.
   * @param pLocale The locale to get custom pattern for
   * @return The custom currency pattern for the specified locale.
   */
  private String getCustomCurrencyPattern(String pLocale){
    if (getCustomCurrencyPatterns()!= null){
      return getCustomCurrencyPatterns().get(pLocale);
    }
    return null;
  }

}
