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


package atg.projects.store.recommendations.processor;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.jms.Message;

import atg.adc.pipeline.ADCEventPipelineProcessor;
import atg.adc.pipeline.ADCPipelineArgs;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.core.util.StringUtils;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.projects.store.recommendations.adc.StoreADCSessionData;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.servlet.DynamoHttpServletRequest;
import atg.userprofiling.Profile;

/**
 * This processor is responsible for generating base clickstream tracking 
 * code markup that is included into all pages.
 * The tracking code generated by this processor includes the following parameter:
 *  
 * <ul>
 *   <li>retailer ID</li>
 *   <li>customer ID (for non-transient profiles)</li>
 *   <li>store ID (for multisite context)</li>
 *   <li>excludeDefaultStore (for multisite context and the value is true)</li>
 *   <li>-inc-price (optional)</li>
 *   <li>locale (optional)</li>
 *   <li>cart (included only if one of the configured <code>JMSTypes</code> events present)</li>
 *   <li>-failover (included only if non default configuration is used)</li>
 * </ul>  
 *   
 * Upon completion of tracking code generating the processor will append it to the current
 * auto-tagging data stored in ADC pipeline arguments.
 *
 * The tracking code generated by this processor may also include cart content
 * entry depending on whether the ADCRequestData object contains one of the 
 * following events:
 * 
 * <ul>
 *   <li>atg.commerce.order.ItemAddedToOrder</li>
 *   <li>atg.commerce.order.ItemQuantityChanged</li>
 *   <li>atg.commerce.order.ItemRemovedFromOrder</li>
 *   <li>atg.commerce.promotion.ScenarioAddedItemToOrder</li>
 *   <li>atg.dps.Login</li>
 *   <li>atg.commerce.fulfillment.SubmitOrder</li>
 *   <li>atg.commerce.pricing.PriceChanged</li>
 * 
 * These events notify TrackingCodeProcessor that cart content or order's price
 * may have changed so new cart content tracking information should be sent to 
 * Recommendations On Demand service.
 * 
 * The TrackingCodeProcessor processor extends ADC's <code>ADCEventPipelineProcessor</code>
 * and serves as a base class for the all other clickstream tracking code 
 * related processors (for product, category pages, search result pages, etc.)
 * 
 * @author Yekaterina Kostenevich
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Recommendations/src/atg/projects/store/recommendations/processor/TrackingCodeProcessor.java#2 $Change: 630322 $
 * @updated $DateTime: 2012/12/26 06:47:02 $Author: ykostene $
 *
 */
public class TrackingCodeProcessor extends ADCEventPipelineProcessor {  

  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Recommendations/src/atg/projects/store/recommendations/processor/TrackingCodeProcessor.java#2 $Change: 630322 $";
  
  /** Entries format constants*/
  public static final String TRACKING_DIV = "<div id=\"cs-cfg\" style=\"display:none !important;\"><dl class=\"cs-cfg\">{0}</dl></div>";
  public static final String CUSTOMER_ID_ENTRY = "<dt>customerId</dt><dd>{0}</dd>";
  public static final String RETAILER_ID_ENTRY = "<dt>retailerId</dt><dd>{0}</dd>";
  public static final String STORE_ID_ENTRY = "<dt>storeId</dt><dd>{0}</dd>";
  public static final String VIEW_ENTRY = "<dt>view</dt><dd><dl>{0}</dl></dd>";
  public static final String EXCLUDE_DEFAULT_STORE_ENTRY = "<dt>excludeDefaultStore</dt><dd>{0}</dd>";
  public static final String PRODUCT_ENTRY = "<dt>{0}</dt>";
  public static final String CART_ENTRY = "<dt>cart</dt><dd><dl><dt>productIds</dt><dd><dl>{0}</dl></dd><dt>totalPrice</dt><dd>{1}</dd></dl></dd>";
  public static final String INCLUDE_PRICE_ENTRY = "<dt>-inc-price</dt><dd>{0}</dd>";
  public static final String LOCALE_ENTRY = "<dt>locale</dt><dd>{0}</dd>";
  public static final String FAILOVER_ENTRY = "<dt>-failover</dt><dd><dl>{0}</dl></dd>";
  public static final String DISABLED_FAILOVER = "<dt>-failover</dt><dd>false</dd>";
  public static final String FAILOVER_TIMEOUT_ENTRY = "<dt>timeout</dt><dd>{0}</dd>";
  public static final String FAILOVER_SKIP_RECOMMENDATIONS = "<dt>skip-recommendations</dt><dd>{0}</dd>";
  public static final String FAILOVER_CONTENT = "<dt>content</dt><dd>{0}</dd>";
  
  /** Default failover timeout */
  public static final int DEFAULT_TIMEOUT = 5000;
   
  // PerfomanceMonitor constants
  public static final String PERFORM_MONITOR_NAME = "TrackingCodeProcessor";
  public static final String PERFORM_OPERATION_NAME = "updateADCData";
       
  /**
   * Price list manager
   */
  private PriceListManager mPriceListManager;
  
  /**
   * Gets the price list manager
   * 
   * @return the price list manager
   */
  public PriceListManager getPriceListManager() {
    return mPriceListManager;
  }

  /**
   * Sets the price list manager
   * 
   * @param pPriceListManager the price list manager
   */
  public void setPriceListManager(PriceListManager pPriceListManager) {
    mPriceListManager = pPriceListManager;
  }

  /**
   * Indicates whether 'locale' entry should be included into 
   * tracking code.
   */
  private boolean mIncludeLocale;
  
  
  /**
   * Returns the boolean indicating whether 'locale'
   * entry should be included into tracking code.
   * 
   * @return the includeLocale the boolean indicating whether 'locale'
   *         entry should be included into tracking code.
   */
  public boolean isIncludeLocale() {
    return mIncludeLocale;
  }

  /**
   * Sets includeLocale, that indicates whether 'locale' entry should be 
   * included into tracking code.
   * 
   * @param pIncludeLocale the includeLocale to set
   */
  public void setIncludeLocale(boolean pIncludeLocale) {
    mIncludeLocale = pIncludeLocale;
  }

  /**
   * Indicates whether 'include price' entry
   * should be included into tracking code.
   */
  private boolean mIncludePrice;  
  
  /**
   * Returns the boolean indicating whether 'include price'
   * entry should be included into tracking code.
   * 
   * @return the boolean indicating whether 'include price' entry
   * should be included into tracking code.
   */
  public boolean isIncludePrice() {
    return mIncludePrice;
  }

  /**
   * Sets includePrice, that indicates whether 'include price'
   * entry should be included into tracking code.
   * 
   * @param pIncludePrice the includePrice to set
   */
  public void setIncludePrice(boolean pIncludePrice) {
    mIncludePrice = pIncludePrice;
  }

  /**
   * Path to shopping cart Nucleus component
   */
  private String mShoppingCartPath;

  /**
   * Gets path to shopping cart Nucleus component
   * 
   * @return the path to shopping cart Nucleus component
   */
  public String getShoppingCartPath() {
    return mShoppingCartPath;
  }

  /**
   * Sets Get path to shopping cart Nucleus component
   * 
   * @param pShoppingCartPath the path to shopping cart Nucleus component
   */
  public void setShoppingCartPath(String pShoppingCartPath) {
    mShoppingCartPath = pShoppingCartPath;
  }
  
  /**
   * The boolean representing the value of 
   * excludeDefaultStore parameter for clickstream tracking code.
   */
  private boolean mExcludeDefaultStore;

  /**
   * Returns ecludeDefaultStore, that represents the value of 
   * excludeDefaultStore parameter for clickstream tracking code.
   * 
   * @return the boolean representing the value of 
   * excludeDefaultStore parameter for clickstream tracking code.
   */
  public boolean isExcludeDefaultStore() {
    return mExcludeDefaultStore;
  }

  /**
   * Sets ecludeDefaultStore, that represents the value of 
   * excludeDefaultStore parameter for clickstream tracking code.
   * 
   * @param pExcludeDefaultStore the boolean representing the value of 
   * excludeDefaultStore parameter for clickstream tracking code.
   */
  public void setExcludeDefaultStore(boolean pExcludeDefaultStore) {
    mExcludeDefaultStore = pExcludeDefaultStore;
  }

  /**
   * Retailer Id property
   */
  private String mRetailerId;

  /**
   * Gets the Retailer Id provided by Recommendations On Demand service
   * 
   * @return the Retailer Id provided by Recommendations On Demand service
   */
  public String getRetailerId() {
    return mRetailerId;
  }

  /**
   * Sets Retailer Id provided by Recommendations On Demand service
   * 
   * @param pRetailerId
   *          the Retailer Id to set
   */
  public void setRetailerId(String pRetailerId) {
    mRetailerId = pRetailerId;
  }
  
  /**
   * Boolean indicating whether Recommendations failover should be disabled.
   */
  private boolean mEnableFailover = true;

  /**
   * Returns the boolean indicating whether Recommendations failover should be disabled.
   * 
   * @return the boolean indicating whether Recommendations failover should be disabled.
   */
  public boolean isEnableFailover() {
    return mEnableFailover;
  }

  /**
   * Sets the boolean indicating whether Recommendations failover should be disabled.
   * 
   * @param pEnableFailover
   *          the boolean indicating whether Recommendations failover should be disabled.
   */
  public void setEnableFailover(boolean pEnableFailover) {
    mEnableFailover = pEnableFailover;
  }
  
  /**
   * The boolean indicating whether generated recommendations should be skipped and
   * Recommendations failover content should be used instead.
   */
  private boolean mSkipRecommendations;

  /**
   * Returns the boolean indicating whether generated recommendations should be skipped and
   * Recommendations failover content should be used instead.
   * 
   * @return The boolean indicating whether generated recommendations should be skipped and
   * Recommendations failover content should be used instead.
   */
  public boolean isSkipRecommendations() {
    return mSkipRecommendations;
  }

  /**
   * Sets the boolean indicating whether generated recommendations should be skipped and
   * Recommendations failover content should be used instead.
   * 
   * @param pSkipRecommendations
   *          The boolean indicating whether generated recommendations should be skipped and
   * Recommendations failover content should be used instead.
   */
  public void setSkipRecommendations(boolean pSkipRecommendations) {
    mSkipRecommendations = pSkipRecommendations;
  }
  
  /**
   * Boolean indicating whether the content included into the recommendations container should
   * be displayed as failover content.
   */
  private boolean mUseContentFailover;

  /**
   * Returns the boolean indicating whether the content included into the recommendations container should
   * be displayed as failover content.
   * 
   * @return the boolean indicating whether the content included into the recommendations container should
   * be displayed as failover content.
   */
  public boolean isUseContentFailover() {
    return mUseContentFailover;
  }

  /**
   * Sets the boolean indicating whether the content included into the recommendations container should
   * be displayed as failover content.
   * 
   * @param pUseContentFailover
   *          the boolean indicating whether the content included into the recommendations container should
   *          be displayed as failover content.
   */
  public void setUseContentFailover(boolean pUseContentFailover) {
    mUseContentFailover = pUseContentFailover;
  }
  
  /**
   * The failover timeout in milliseconds indicating how long to wait for generated 
   * recommendations before using the failover.
   */
  private int mFailoverTimeout = DEFAULT_TIMEOUT;

  /**
   * Gets the failover timeout in milliseconds indicating how long to wait for generated 
   * recommendations before using the failover.
   * 
   * @return The failover timeout in milliseconds indicating how long to wait for generated 
   * recommendations before using the failover.
   */
  public int getFailoverTimeout() {
    return mFailoverTimeout;
  }

  /**
   * Sets the failover timeout in milliseconds indicating how long to wait for generated 
   * recommendations before using the failover.
   * 
   * @param pFailoverTimeout the failover timeout in milliseconds indicating how long to wait 
   * for generated recommendations before using the failover.
   */
  public void setFailoverTimeout(int pFailoverTimeout) {
    mFailoverTimeout = pFailoverTimeout;
  }
  
  /**
   * If the ADC request data stored in pipeline arguments contains all 
   * required data the method builds recommendations clickstream 
   * tracking code and appends it to the current auto-tagging data in stored
   * in the ADC request data that later will be inserted into response output stream.
   * 
   * @param pArgs The pipeline arguments
   * @return status code NO_CHANGE
   */
  @Override
  public int updateADCData(ADCPipelineArgs pArgs) {
    PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME,
        PERFORM_OPERATION_NAME);
    boolean perfCancelled = false;
    int status = NO_CHANGE;

    if (validateRequiredData(pArgs)){
      try {
        
        status = processADCData(pArgs);
  
      } finally {
        try {
          if (!perfCancelled) {
            PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME,
                PERFORM_OPERATION_NAME);
            perfCancelled = true;
          }
        } catch (PerfStackMismatchException e) {
          if (isLoggingWarning()) {
            logWarning(e);
          }
        }
      }
    }
    return status;
  }
  
  /**
   * The method checks whether the ADC data stored in pipeline 
   * arguments contains all required information for the processor. 
   * In this case there is no any specific data that should be 
   * added to ADC data before processor invocation.
   * 
   * @param pArgs The pipeline arguments
   * @return true
   */
  protected boolean validateRequiredData(ADCPipelineArgs pArgs){
     return true;
  }
  
  /**
   * Builds and appends tracking code to the response stream.
   * 
   * @param pArgs The pipeline arguments
   * @return status code MADE_CHANGE
   */
  protected int processADCData(ADCPipelineArgs pArgs){
  
    // Get current auto-tagging data
    StringBuffer data = pArgs.getCurrentADCData().getData();
    
    // Build recommendations clickstream tracking code and append it
    // to auto-tagging data
    String trackingCode = buildTrackingCode(pArgs);
    data.append(trackingCode);
    
    if (isLoggingDebug()) {
      logDebug("Auto-tagging data is appended to ADCRequestData: " + trackingCode);
    }
    return MADE_CHANGE;
  }
  
  /**
   * Builds recommendations tracking code markup to be appended to the ADC data.
   * 
   * @param pArgs ADC pipeline arguments
   * @return recommendations tracking code markup.
   */
  protected String buildTrackingCode(ADCPipelineArgs pArgs) {
    if (isLoggingDebug()) {
      logDebug("Start generating auto-tagging data");
    }
    String trackingCode = MessageFormat.format(TRACKING_DIV, new Object[] {buildTrackingCodeContent(pArgs)}); 
    return trackingCode;
  }
  
  /**
   * Builds the content of tracking code DIV element.
   * 
   * @param pArgs ADC pipeline arguments
   * @return the content for the tracking code DIV element.
   */
  protected String buildTrackingCodeContent(ADCPipelineArgs pArgs) {
    StringBuilder trackingCodeContent = new StringBuilder();
    
    // Append retailer ID entry
    appendEntry(trackingCodeContent, buildRetailerIdEntry(pArgs));
    
    // Append customer ID entry
    appendEntry(trackingCodeContent, buildCustomerIdEntry(pArgs));

    // Append view configuration parameter entry if present
    appendEntry(trackingCodeContent, buildTrackingCodeViewEntry(pArgs)); 
    
    // Append cart entry
    if (isIncludeCartContent(pArgs)) {
      String cartEntry = buildCartEntry(pArgs, getCurrentOrder(pArgs));
      appendEntry(trackingCodeContent, cartEntry); 
    }
    
    // Append include price entry. The '-inc-price' entry is included only 
    // if it's value is true.
    if (isIncludePrice()){
      trackingCodeContent.append(buildIncludePriceEntry(pArgs));
    }
    
    // Append locale entry
    if (isIncludeLocale()){
      appendEntry(trackingCodeContent, buildLocaleEntry(pArgs));
    }
    
    // Append failover configuration
    appendEntry(trackingCodeContent, buildFailoverEntry(pArgs));
    
    return trackingCodeContent.toString();
  }

  /**
   * The utility method that appends entry to the StringBuilder in the case if it's not null
   * or empty.
   * 
   * @param pTrackingCodeContent trackingCodeContent StringBuilder object
   * @param pEntry the entry to append to trackingCodeContent
   */
  protected void appendEntry(StringBuilder pTrackingCodeContent, String pEntry){
    if (!StringUtils.isEmpty(pEntry)){
      pTrackingCodeContent.append(pEntry);
    }  
  }
 
  /**
   * Builds view entry for the recommendations clickstream tracking code.
   * The entry is built by calling the <code>buildTrackingCodeViewContent()</code> 
   * and inserting the result into 'view' entry container markup.
   * 
   * @param pArgs ADC pipeline arguments
   * @return 'view' entry for the recommendations clickstream tracking code
   */
  protected String buildTrackingCodeViewEntry(ADCPipelineArgs pArgs) {
    String trackingCode = "";
    String trackingCodeContent = buildTrackingCodeViewContent(pArgs);    
    if (!StringUtils.isEmpty(trackingCodeContent)){
      trackingCode = MessageFormat.format(VIEW_ENTRY, new Object[] {trackingCodeContent}); 
    }
    return trackingCode;
  }
  
  
  /**
   * Builds the content of the recommendations 'view' configuration parameter
   * that will be put inside of the tracking code view entry. The content 
   * can consist of different set of parameters so the method 
   * will call the sequence of build functions for each particular entry that
   * should be included into recommendations view configuration parameter. 
   * For this processor the <code>buildStoreIdEntry()</code> and 
   * <code>buildExcludeDefaultStoreEntry()</code> will be called.
   * 
   * @param pArgs ADC pipeline arguments
   * @return content of the recommendations 'view' configuration parameter
   */
  protected String buildTrackingCodeViewContent (ADCPipelineArgs pArgs){
    StringBuilder trackingCodeContent = new StringBuilder();
    
    // Append storeId entry
    appendEntry(trackingCodeContent, buildStoreIdEntry(pArgs));
    
    // Append exludeDefaultStore entry if the value is true
    if (isExcludeDefaultStore()){
      appendEntry(trackingCodeContent, buildExcludeDefaultStoreEntry(pArgs));
    }
    
    return trackingCodeContent.toString();
  }
  
  
  /**
   * Builds retailer ID entry for recommendations tracking code.
   * 
   * @param pArgs ADC pipeline arguments
   * @return retailer ID entry.
   */
  public String buildRetailerIdEntry(ADCPipelineArgs pArgs) {
    String retailerIdEntry = "";
    String retailerId = getRetailerId();    
    if (retailerId != null) {
      retailerIdEntry = MessageFormat.format(RETAILER_ID_ENTRY, new Object[] {retailerId}); 
    }
    return retailerIdEntry;
  }
   
  
  /**
   * Builds customerId entry for the recommendations clickstream tracking code.
   * Retrieves customer ID from Profile component. 
   * If profile is transient no entry is built.
   * 
   * @param pArgs ADC pipeline arguments
   * @return customer ID entry.
   */
  public String buildCustomerIdEntry(ADCPipelineArgs pArgs) {
    String customerIdEntry = "";
    Profile profile = pArgs.getADCRequestData().getProfile();
    if (profile != null && !profile.isTransient()){
      String customerId = profile.getRepositoryId();
      customerIdEntry = MessageFormat.format(CUSTOMER_ID_ENTRY, new Object[] {customerId});       
    }
    return customerIdEntry;
  }

  /**
   * Builds storeId entry for the recommendations clickstream tracking code.
   * The current site ID is obtained by calling 
   * SiteContextManager.getCurrentSiteId() method. If the application is run 
   * in no multisite context no entry will be created.
   * 
   * @param pArgs ADC pipeline arguments
   * @return store ID entry.
   */
  protected String buildStoreIdEntry (ADCPipelineArgs pArgs) {  
    String storeIdEntry = "";
    String storeId = SiteContextManager.getCurrentSiteId() ;
    if (storeId != null){
      storeIdEntry = MessageFormat.format(STORE_ID_ENTRY, new Object[] {storeId});            
    }
    return storeIdEntry; 
  }
  

  /**
   * Builds 'locale' entry for the recommendations clickstream tracking code.
   * The locale is taken from configured in the current site configuration's
   * or profile sale price list or, if sale price list is null, from list price list.
   * 
   * @param pArgs ADC pipeline arguments
   * @return the 'locale' entry
   */
  protected String buildLocaleEntry(ADCPipelineArgs pArgs) {
    String localeEntry = "";
    
    // Get current site
    Site site = SiteContextManager.getCurrentSite();
        
    try {
      RepositoryItem priceList = null;
      
      // Profile's sale price list property name
      String salePriceListPropertyName = getPriceListManager().getSalePriceListPropertyName();   
      Profile profile = pArgs.getADCRequestData().getProfile();
      
      // Get sale price list, it will be taken either from profile or from site configuration
      priceList = getPriceListManager().determinePriceList(profile, site, salePriceListPropertyName);
      
      // If price list is null, get list price list
      if (priceList == null){ 
        
        // Profile's list price list property name
        String priceListPropertyName = getPriceListManager().getPriceListPropertyName();                 
        priceList = getPriceListManager().determinePriceList(profile, site, priceListPropertyName);           
      }
      
      // If price list was found, get its locale
      if (priceList != null) {        
        Locale locale = getPriceListManager().getPriceListLocale(priceList);
        String countryCode = locale.getCountry();
        
        // Build locale entry
        localeEntry = MessageFormat.format(LOCALE_ENTRY, new Object[] {countryCode});    
      }
    } catch (RepositoryException e) {
      if (isLoggingError()){
        logError("Can't get price list for site " + site.getId(), e);
      }
    }      
    
    return localeEntry;
  }

  /**
   * Builds '-inc-price' entry for the recommendations clickstream tracking code.
   * 
   * @param pArgs ADC pipeline arguments
   * @return the include price entry
   */
  protected String buildIncludePriceEntry(ADCPipelineArgs pArgs) {
    String includePrice = MessageFormat.format(INCLUDE_PRICE_ENTRY, new Object[] { isIncludePrice() });      
    return includePrice;
  }
  
  /**
   * Builds 'excludeDefaultStore' parameter entry for the recommendations clickstream 
   * tracking code. The value of 'excludeDefaultStore' parameter is configurable 
   * through processor's configuration.
   * 
   * @param pArgs ADC pipeline arguments
   * @return excludeDefaultStore parameter entry
   */
  protected String buildExcludeDefaultStoreEntry (ADCPipelineArgs pArgs){
    String exludeDefaultStoreEntry = "";
    
    // Do not build the entry if it's not a multisite context
    if (!StringUtils.isEmpty(SiteContextManager.getCurrentSiteId())){
      exludeDefaultStoreEntry = MessageFormat.format(EXCLUDE_DEFAULT_STORE_ENTRY, new Object[] { isExcludeDefaultStore() });
    }
    
    return exludeDefaultStoreEntry;
  }
  
  /**
   * Builds failover configuration entry. If configuration doesn't differ from 
   * Recommendations On Demand default failover configuration no content is returned
   * for failover entry.
   * 
   * @param pArgs ADC pipeline arguments
   * @return failover configuration entry
   */
  protected String buildFailoverEntry (ADCPipelineArgs pArgs){
    String failoverEntry = "";
      
    if (!isEnableFailover()){
      // Disable failover
      failoverEntry = DISABLED_FAILOVER;
    }
    else{
      // Failover entry content
      StringBuilder failoverEntryContent = new StringBuilder();
      
      if (isSkipRecommendations()){
        // Include skip-recommendations entry
        failoverEntryContent.append(MessageFormat.format(FAILOVER_SKIP_RECOMMENDATIONS,
                                                         new Object[]{true}));
      }
      
      // Check whether recommendations container content should be used as failover content
      if (isUseContentFailover()){
        // Include skip-recommendations entry
        failoverEntryContent.append(MessageFormat.format(FAILOVER_CONTENT,
                                                         new Object[]{true}));
      }
      
      if (getFailoverTimeout() != DEFAULT_TIMEOUT){
        // Include failover timeout configuration
        failoverEntryContent.append(MessageFormat.format(FAILOVER_TIMEOUT_ENTRY, 
                                                         new Object[]{getFailoverTimeout()}));
      }
      
      if (failoverEntryContent.length()>0){
        failoverEntry = MessageFormat.format(FAILOVER_ENTRY,
            new Object[]{failoverEntryContent.toString()});  
      }
    }
   
    
    return failoverEntry;
  }
 
  
  /**
   * Builds 'cart' entry for the recommendations clickstream tracking code. 
   * The method iterates through the order's commerce items and adds 
   * product ID for each commerce item to the 'cart' element of the tracking 
   * code. The empty cart content is also included to let ATG 
   * Recommendations service know that the cart is empty.
   * 
   * @param pArgs ADC pipeline arguments
   * @param pOrder current shopping cart
   * @return cart entry
   */
  protected String buildCartEntry (ADCPipelineArgs pArgs, Order pOrder){
    String cartEntry = "";
    
    if (pOrder != null) {
      List commerceItems = pOrder.getCommerceItems();
      if (commerceItems == null) {
        if (isLoggingDebug()) {
          logDebug("List of commerce items is null, nothing to process. Skip processor execution");
        }
      }
      else {
        StringBuilder products = new StringBuilder();
        
        for (Iterator iter = commerceItems.iterator(); iter.hasNext();) {
          CommerceItem ci = (CommerceItem) iter.next();

          if (isLoggingDebug()) {
            logDebug("Process commerce item for product: "
                + ci.getAuxiliaryData().getProductId());
          }

          /*
           * If the order contains more than one unit of any product the 
           * product ID will be included once per each unit of product.
           */
          for (int i = 0; i < ci.getQuantity(); i++) {
            // Escape product ID to prevent XSS attacks using it
            String productId = ci.getAuxiliaryData().getProductId();
            if (productId != null){
              productId = StringUtils.escapeHtmlString(productId);
            }
            String productEntry = MessageFormat.format(PRODUCT_ENTRY,
                new Object[] { productId });
            products.append(productEntry);
          }                   
        }   

        double priceInfoSubTotal = (pOrder.getPriceInfo() == null) ? 
                                    0.0 : pOrder.getPriceInfo().getRawSubtotal();
        
        cartEntry = MessageFormat.format(CART_ENTRY,
            new Object[] { products , priceInfoSubTotal});
      }      
    }
    else {
      if (isLoggingDebug()) {
        logDebug("Order is null, nothing to process.");
      }
    }
    return cartEntry;
  }
  
  /**
   * The method gets Order object corresponding to the current user's 
   * shopping cart. The path to the ShoppingCart component is 
   * configurable using the processor's configuration. The order 
   * returned by this method will be passed to the <code>buildCartEntry()</code> 
   * method for further processing.
   * 
   * @param pArgs ADC pipeline arguments
   * @return order object corresponding to the current user's 
   * shopping cart
   */
  protected Order getCurrentOrder(ADCPipelineArgs pArgs){
    DynamoHttpServletRequest request = pArgs.getADCRequestData().getRequest();
    OrderHolder shoppingCart = (OrderHolder) request.resolveName(getShoppingCartPath());
    Order order = shoppingCart.getCurrent(false);
    
    return order;
  }
  
  /**
   * This method checks whether ADCRequestData contains one of the events listed 
   * in the JMSTypes property.
   * 
   * @param pArgs ADC pipeline arguments
   * @return true  - if ADCRequestData contains one of the events listed 
   * in the JMSTypes property. False otherwise.
   */
  protected boolean isIncludeCartContent(ADCPipelineArgs pArgs){
    boolean isIncludeCartContent = false;
    StoreADCSessionData sessionData = (StoreADCSessionData) pArgs.getADCSessionData();
    
    String[] types = getJMSTypes();   
    if(types != null) {
      for(int i = 0; i < types.length; i++) {
        String type = types[i];
        
        if(isLoggingDebug()){
          logDebug("Getting events of type " + type);
        }
        /* 
         * Look for the events of the specific type in the session event holder. We are using
         * session event holder here not request event holder as some cart change event can happen
         * in the scope of AJAX request and can't be processed in the same request.
         */ 
        List events = sessionData.getEvents(type);
          
        if(events != null && events.size() > 0) {
          isIncludeCartContent = true;
          break;
        } 
        else {
          if(isLoggingDebug()){
            logDebug("No events found in the session data holder");
          }
        }
        
      }
      // clear session event holder
      sessionData.clearAllEvents();
      
    } 
    else {
      if(isLoggingDebug()){
        logDebug("No configured jms types.");
      }
    }
    
    return isIncludeCartContent;
  }

  /**
   * Do nothing. Always return NO_CHANGE.
   * 
   * @param pArgs The ADCPipelineArgs for this pipeline execution
   * @param pMessage A JMS message that matches one of the types in the JMSTypes
   * property.
   * @return NO_CHANGE
   */
  @Override
  public int processEvent(ADCPipelineArgs pArgs, Message pMessage) {   
    return NO_CHANGE;
  }
  
  
}
