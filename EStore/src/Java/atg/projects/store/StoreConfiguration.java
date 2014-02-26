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

package atg.projects.store;

import java.util.List;

import atg.nucleus.GenericService;


import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * Stores configuration information that is specific to Commerce Reference Store.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/StoreConfiguration.java#4 $$Change: 788842 $
 * @updated $DateTime: 2013/02/07 07:18:28 $$Author: npaulous $
 */
public class StoreConfiguration extends GenericService {
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/StoreConfiguration.java#4 $$Change: 788842 $";


  /**
   * Indicates whether orders paid by credit card must include the credit card 
   * verification code. Verification codes are required by default.
   */
  protected boolean mRequireCreditCardVerification = true;

  /**
   * Is Dojo debug turned on. Debugging is disabled by default.
   */
  protected boolean mDojoDebug = false;

  /**
   * The list of countries for which <b>state</b> is a required address field.
   */
  protected java.util.List<String> mMandatoryStateCountryList;

  
  /**
   * This property determines if credit card verification numbers are required by the application.
   * <p/>
   * Many credit cards have a card verification number printed, not embossed, on the card.   
   * This number is never transferred during card swipes and should be known only by the cardholder.  
   * Each card association has its own name for this number. Visa calls it the Card Verification 
   * Value (CVV2), and MasterCard calls it the Card Validation Code (CVC2). Visa and MasterCard print 
   * the number on the back of the card. American Express and Discover call it the Card
   * Identification Digits (CID).
   * 
   * @return <code>true</code> if credit card verification is required, otherwise <code>false</code>.
   */
  public boolean isRequireCreditCardVerification() {
    return mRequireCreditCardVerification;
  }

  /**
   * Set a property indicating whther that the credit card verification is required. This is 
   * used in the store during checkout. 
   * 
   * @param pRequireCreditCardVerification - <code>true</code> if credit card verification is required
   */
  public void setRequireCreditCardVerification(boolean pRequireCreditCardVerification) {
    mRequireCreditCardVerification = pRequireCreditCardVerification;
  }

  /**
   * Determines whether debug logging has been configured to be enabled.
   * @return Flag indicating whether dojo debugging should be enabled. 
   */
  public boolean isDojoDebug() {
    return mDojoDebug;
  }

  /**
   * Sets a property stating that debug logging should be enable in the 
   * Dojo Toolkit.
   *
   * @param pDojoDebug - <code>true</code> if Dojo debug is to be enabled.
   */
  public void setDojoDebug(boolean pDojoDebug) {
    mDojoDebug = pDojoDebug;
  }

  /**
   * Returns the list of configured country codes that are mandatory. Used when shopper
   * creates billing and shipping addresses. These indicate when the state property of the 
   * address is a mandatory field.
   * 
   * @return the list of country codes of mandatory countries.
   */
  public List<String> getMandatoryStateCountryList() {
    return mMandatoryStateCountryList;
  }

  /**
   * Sets the list of configured country codes that are mandatory. Used when shopper
   * creates billing and shipping addresses. These indicate when the state property of the 
   * address is a mandatory field.
   * 
   * @param pMandatoryStateCountryList - the List of code of mandatory countries.
   */
  public void setMandatoryStateCountryList(List<String> pMandatoryStateCountryList) {
    mMandatoryStateCountryList = pMandatoryStateCountryList;
  }

  //--------- Property: SiteHttpServerName -----------
  /**
   * The name of the HTTP server.When running of an internal facing
   * instance this should be configured to the production instance. It's used for 
   * the generation of external links e.g. those that are sent out in email.
   */
  String mSiteHttpServerName;

  /**
   * Sets the name of the HTTP server.When running of an internal facing
   * instance this should be configured to the production instance. It's used for 
   * the generation of external links e.g. those that are sent out in email.
   * 
   * @param pSiteHttpServerName The server name.
   */
  public void setSiteHttpServerName(String pSiteHttpServerName) {
    mSiteHttpServerName = pSiteHttpServerName;
  }
  
  /**
   * Returns the name of the HTTP server. When running of an internal facing
   * instance this should be configured to the production instance. It's used for 
   * the generation of external links e.g. those that are sent out in email.
   * 
   * @return The name of the HTTP server. 
   */
  public String getSiteHttpServerName() {
    return mSiteHttpServerName;
  }

  //--------- Property: SiteHttpServerPort -----------
  /**
   * The port number of the HTTP server.When running of an internal facing
   * instance this should be configured to the production instance. It's used for 
   * the generation of external links e.g. those that are sent out in email.
   */
  int mSiteHttpServerPort;
  
  /**
   * Sets the port number of the HTTP server. When running of an internal facing
   * instance this should be configured to the production instance. It's used for 
   * the generation of external links e.g. those that are sent out in email.
   * 
   * Sets the port of the HTTP server.
   * @param pSiteHttpServerPort The port number to set
   */
  public void setSiteHttpServerPort(int pSiteHttpServerPort) {
    mSiteHttpServerPort = pSiteHttpServerPort;
  }
  
  /**
   * Returns the port number of the HTTP server. When running of an internal facing
   * instance this should be configured to the production instance. It's used for 
   * the generation of external links e.g. those that are sent out in email.
   * 
   * @return The port number of the HTTP server.
   */
  public int getSiteHttpServerPort() {
    return mSiteHttpServerPort;
  }

  //--------- Property: defaultResourceBundle -----------
  /**
   * The location of the default resource bundle. This is used if no resource 
   * bundle is defined in the site configuration repository item.
   */
  String mDefaultResourceBundle = "atg.projects.store.web.WebAppResources";
  
  /**
   * Sets the location of the default resource bundle. This is used if no resource 
   * bundle is defined in the site configuration repository item.
   * 
   * @param pDefaultResourceBundle The default resource bundle.
   */
  public void setDefaultResourceBundle(String pDefaultResourceBundle) {
    mDefaultResourceBundle = pDefaultResourceBundle;
  }
  
  /**
   * Returns the location of the default resource bundle. This is used if no resource 
   * bundle is defined in the site configuration repository item.
   * 
   * @return The location of the default resource bundle.
   */
  public String getDefaultResourceBundle() {
    return mDefaultResourceBundle;
  }

  //--------- Property: defaultCssFile -----------

  /**
   * The location of the default css file(s). This is used if no CSS
   * location is defined in the site configuration repository item
   */
  String mDefaultCssFile = "";

  /**
   * Sets the location of the default css file(s). This is used if no CSS
   * location is defined in the site configuration repository item
   *
   * @param pDefaultCssFile The default CSS file location.
   */
  public void setDefaultCssFile( String pDefaultCssFile ) {
    mDefaultCssFile = pDefaultCssFile;
  }

  /**
   * Gets the location of the default css file(s). This is used if no CSS
   * location is defined in the site configuration repository item
   *
   * @return The location of the default css file.
   */
  public String getDefaultCssFile() {
    return mDefaultCssFile;
  }


  //--------- Property: epochDate -----------
  /**
   * The store epoch date represented as a String object in the
   * format as defined by the epochDatePattern property.
   * This can be used when displaying example dates in the UI.
   */
  private String mEpochDate = "01/31/1986";

  /**
   * Sets the store epoch date.
   * The epochDate property must follow the epochDatePattern property format.
   *
   * @param pEpochDate The date to use as the epoch date.
   */
  public void setEpochDate( String pEpochDate ) {
    mEpochDate = pEpochDate;
  }

  /**
   * Returns the store epoch date represented as a String object.
   *
   * @return The store epoch date.
   */
  public String getEpochDate() {
    return mEpochDate;
  }


  /**
   * The date pattern defining the date format for the epochDate property. 
   */
  private String mEpochDatePattern = "mm/dd/yyyy";

  /**
   * Sets the date pattern defining the date format for the epochDate property.
   * The epochDate property must follow the epochDatePattern property format.
   * 
   * @param pEpochDatePattern The epoch date pattern.
   */
  public void setEpochDatePattern( String pEpochDatePattern ) {
    mEpochDatePattern = pEpochDatePattern;
  }

  /**
   * Returns the date pattern defining the date format for the epochDate property.
   * The epochDate property must follow the epochDatePattern property format.
   *
   * @return The store epoch date pattern.
   */
  public String getEpochDatePattern() {
    return mEpochDatePattern;
  }


  /**
   * The store epoch date represented as a Date object.
   */
  private Date mEpochDateAsDate = null;

  /**
   * Returns the epochDate property represented as a Date object.
   *
   * @return The store epoch date property represented as a Date object.
   */
  public Date getEpochDateAsDate() {
    if ( mEpochDateAsDate == null ) {
      SimpleDateFormat dateFormat = new SimpleDateFormat( getEpochDatePattern() );
      mEpochDateAsDate = dateFormat.parse( getEpochDate(), 
                                           new ParsePosition(0) );
    }
    
    return mEpochDateAsDate;
  }
  
  //-----------------------------------
  // property: showUnindexedCategories
  private boolean mShowUnindexedCategories = false;

  /**
   * @return the mShowUnindexedCategories. A boolean indicating whether
   * categories that aren't in the MDEX should be rendered. It will not be
   * possible to navigate to these categories. Defaults to false.
   */
  public boolean isShowUnindexedCategories() {
    return mShowUnindexedCategories;
  }

  /**
   * @param mShowUnindexedCategories the mShowUnindexedCategories to set
   */
  public void setShowUnindexedCategories(boolean pShowUnindexedCategories) {
    mShowUnindexedCategories = pShowUnindexedCategories;
  }
  
  //-----------------------------------
  // property: SupportedCreditCardTypes
  protected List<String> mSupportedCreditCardTypes;
  
  /**
   * Returns the list of supported credit card types.
   * @return the list of supported credit card types.
   */
  public List<String> getSupportedCreditCardTypes() {
    return mSupportedCreditCardTypes;
  }

  /**
   * Sets the list of supported credit card types. 
   * @param pSupportedCreditCardTypes the list of supported credit card types.
   */
  public void setSupportedCreditCardTypes(List<String> pSupportedCreditCardTypes) {
    mSupportedCreditCardTypes = pSupportedCreditCardTypes;
  }

}
