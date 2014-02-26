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
import java.util.List;

import atg.adc.pipeline.ADCPipelineArgs;
import atg.commerce.catalog.CatalogNavHistory;
import atg.commerce.catalog.custom.CatalogProperties;
import atg.projects.store.recommendations.adc.StoreADCRequestData;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;

/**
 * This processor is responsible for generating clickstream tracking code 
 * markup for category landing pages. It extends <code>TrackingCodeProcessor</code> 
 * and overrides its <code>buildTrackingCodeViewContent()</code> method in order to add 
 * 'category' entry to the 'view' configuration parameter. The category repository 
 * item should be stored into the <code>ADCRequestData</code> by the preceding
 * <code>SetCategoryEventProcessor</code>.
 * 
 * @author Yekaterina Kostenevich
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Recommendations/src/atg/projects/store/recommendations/processor/CategoryTrackingCodeProcessor.java#2 $Change: 630322 $
 * @updated $DateTime: 2012/12/26 06:47:02 $Author: ykostene $
 *
 */
public class CategoryTrackingCodeProcessor extends TrackingCodeProcessor{  

  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Recommendations/src/atg/projects/store/recommendations/processor/CategoryTrackingCodeProcessor.java#2 $Change: 630322 $";
  
  /** Entries format constants*/
  public static final String CATEGORY_ENTRY = "<dt>category</dt><dd>{0}</dd>";     
  
  public static final String CATEGORY_SEPARATOR = ">";
    
  /**
   * Category display name property
   */
  private String mCategoryDisplayNameProperty;  
  
  /**
   * Returns the category display name property.
   * 
   * @return the category display name property
   */
  public String getCategoryDisplayNameProperty() {
    return mCategoryDisplayNameProperty;
  }

  /**
   * Sets the category display name property.
   * 
   * @param pCategoryDisplayNameProperty the category display name property
   */
  public void setCategoryDisplayNameProperty(String pCategoryDisplayNameProperty) {
    mCategoryDisplayNameProperty = pCategoryDisplayNameProperty;
  }

  /**
   * The catalog navigation history component path
   */
  private String mCatalogNavHistoryPath;
  
  /**
   * Returns the catalog navigation history component path
   * 
   * @return the catalog navigation history component path
   */
  public String getCatalogNavHistoryPath() {
    return mCatalogNavHistoryPath;
  }

  /**
   * Sets the catalog navigation history component path
   * 
   * @param pCatalogNavHistoryPath the catalog navigation history component path
   */
  public void setCatalogNavHistoryPath(String pCatalogNavHistoryPath) {
    mCatalogNavHistoryPath = pCatalogNavHistoryPath;
  }
  
  /**
   * The boolean indicating whether to include root category into category
   * navigation path
   */
  private boolean mIncludeRootCategory;

  /**
   * @return the boolean indicating if root category should be included
   *        into the category path
   */
  public boolean isIncludeRootCategory() {
    return mIncludeRootCategory;
  }

  /**
   * @param IncludeRootCategory the boolean indicating if root category 
   * should be included into the category path
   */
  public void setIncludeRootCategory(boolean pIncludeRootCategory) {
    mIncludeRootCategory = pIncludeRootCategory;
  }
  
  /**
   * CatalogProperties component that holds the names of catalog properties
   */
  private CatalogProperties mCatalogProperties;

  /**
   * @param the class that holds the names of all catalog properties
   */
  public void setCatalogProperties(CatalogProperties pCatalogProperties) {
    mCatalogProperties = pCatalogProperties;
  }

  /**
   * @return the class that holds the names of all catalog properties
   */
  public CatalogProperties getCatalogProperties() {
    return mCatalogProperties;
  }

  /**
   * Overrides base <code>TrackingCodeProcessor's</code> method in order to check 
   * whether category repository item is specified in <code>ADCRequestData</code> 
   * object stored in the ADC pipeline arguments. If not the further 
   * processing will be stopped.
   * 
   * @param pArgs The pipeline arguments
   * @return true if category repository item is specified in <code>ADCRequestData</code> 
   *              object stored in the ADC pipeline arguments. Otherwise false.
   */  
  @Override
  protected boolean validateRequiredData(ADCPipelineArgs pArgs){
    boolean valid = false;
    
    // Retrieve category item from ADCDataRequest object 
    RepositoryItem category = ((StoreADCRequestData) pArgs.getADCRequestData()).getCategoryItem();
    
    if (category == null) {
      
      // Category is not store in ADCDataRequest, stop processor execution. 
      if (isLoggingDebug()) {
        logDebug("Category is not set. Skip processor execution");
      }
    }
    else {
      valid = true;
    }      
    return valid;
  }
    
  /**
   * Overrides base <code>TrackingCodeProcessor's</code> method in order to append 
   * 'category' entry to the 'view' configuration parameter content.
   * 
   * @param pArgs ADC pipeline arguments
   * @return 'view' entry for the recommendations clickstream tracking code
   */
  @Override
  protected String buildTrackingCodeViewContent (ADCPipelineArgs pArgs){
    StringBuilder trackingCodeContent = new StringBuilder();
    
    // Append tracking code's 'view' content from the parent class
    appendEntry(trackingCodeContent, super.buildTrackingCodeViewContent(pArgs));    
    
    // Append category entry
    appendEntry(trackingCodeContent, buildCategoryEntry(pArgs));
    
    return trackingCodeContent.toString();
  }
  
  /**
   * Builds category entry for the recommendations clickstream tracking code. 
   * The category value contains fully qualified category path using the categories
   * display names (e.g., 'Home Accents>Decor'). The currently viewed category is stored
   * into ADCRequestData by the preceding SetProductProcessor pipeline processor.
   * 
   * @param pArgs ADC pipeline arguments
   * @return category entry
   */
  protected String buildCategoryEntry (ADCPipelineArgs pArgs){
    String categoryEntry = "";
    
    // Get fully qualified category path
    String fullCategoryPath = getFullCategoryPath(pArgs);
    
    // Build category entry
    categoryEntry = MessageFormat.format(CATEGORY_ENTRY, new Object[] { fullCategoryPath });
    return categoryEntry;
  }

  /**
   * Gets full category path to the currently viewed category using the the 
   * CatalogNavigationHistory component that tracks user's navigation.
   * 
   * @param pArgs the pipeline arguments
   * @return full category path to the currently viewed category
   */
  protected String getFullCategoryPath(ADCPipelineArgs pArgs) {
  
    StringBuilder fullCategoryPath = new StringBuilder();
    
    // Resolve request-scoped CatalogNavigationHistory component using the configure path 
    DynamoHttpServletRequest request = pArgs.getADCRequestData().getRequest();
    CatalogNavHistory catalogNavHistory = (CatalogNavHistory) request.resolveName(getCatalogNavHistoryPath());
    
    // Get navigation history list
    List navHistory = catalogNavHistory.getNavHistory();
    
    
    
    if (navHistory != null) {
      
      // First append root category to category navigation path
      if (isIncludeRootCategory() && navHistory.size()>0){
        // Get first category from navigation path
        RepositoryItem category = (RepositoryItem)navHistory.get(1);
        // Get parent category
        RepositoryItem parentCategory = (RepositoryItem)category.getPropertyValue(getCatalogProperties().getParentCategoryPropertyName());
        
        if (parentCategory != null){
          // Get root category display name and append it to the category path
          String categoryName = (String) parentCategory.getPropertyValue(getCategoryDisplayNameProperty());
          fullCategoryPath.append(categoryName);
          fullCategoryPath.append(CATEGORY_SEPARATOR);
        }
      }
      
      // Add remaining categories to the category path
      for (int i = 1; i < navHistory.size(); i++){
        
        RepositoryItem category = (RepositoryItem)navHistory.get(i);
        
        // Get category display name and append it to the category path
        String categoryName = (String) category.getPropertyValue(getCategoryDisplayNameProperty());
        fullCategoryPath.append(categoryName);
        
        // Append path separator if needed
        if (i + 1 < navHistory.size()) {
          fullCategoryPath.append(CATEGORY_SEPARATOR);
        }
      }
    }
    
    return fullCategoryPath.toString();
  }
  
}
