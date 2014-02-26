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

package atg.projects.store.assembler.cartridge.handler;

import atg.endeca.assembler.AssemblerTools;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteManager;
import atg.projects.store.assembler.SearchedSites;
import atg.projects.store.assembler.cartridge.PriceSliderContentItem;
import atg.projects.store.multisite.StoreSitePropertiesManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.assembler.ContentItemInitializer;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;
import com.endeca.infront.cartridge.model.RangeFilterBreadcrumb;
import com.endeca.infront.navigation.NavigationState;
import com.endeca.infront.navigation.model.RangeFilter;
import com.endeca.infront.navigation.request.BreadcrumbsMdexQuery;
import com.endeca.infront.navigation.request.MdexRequest;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.Navigation;

/**
 * Handler for the PriceSlider cartridge. This class is responsible for creating
 * and initializing the PriceSliderContentItem. It extends the NavigationCartridgeHandler.
 *
 * @author ckearney
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/handler/PriceSliderHandler.java#5 $$Change: 791669 $
 * @updated $DateTime: 2013/02/20 08:51:49 $$Author: ykostene $
 */
public class PriceSliderHandler 
  extends NavigationCartridgeHandler<ContentItem, PriceSliderContentItem> { 
  //----------------------------------------
  /** Class version string. */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/handler/PriceSliderHandler.java#5 $$Change: 791669 $";
  
  //--------------------------------------------------------------------------
  // MEMBERS
  //--------------------------------------------------------------------------
  
  private MdexRequest mMdexRequest = null;
      
  //--------------------------------------------------------------------------
  // PROPERTIES
  //--------------------------------------------------------------------------
  
  //--------------------------------------------------------------------
  // property: storeSitePropertiesManager
  private StoreSitePropertiesManager mStoreSitePropertiesManager = null;

  /**
   * @return The StoreSitePropertiesManager bean which is used to manage store properties.
   */
  public StoreSitePropertiesManager getStoreSitePropertiesManager() {
    return mStoreSitePropertiesManager;
  }

  /**
   * @param StoreSitePropertiesManager - Set a new storeSitePropertyManager.
   */
  public void setStoreSitePropertiesManager(StoreSitePropertiesManager pStoreSitePropertiesManager) {
    mStoreSitePropertiesManager = pStoreSitePropertiesManager;
  }
  
  //------------------------------------------
  // property: searchedSites
  private SearchedSites mSearchedSites = null;
  
  /**
   * @return A bean thats used to indicate the scope of the current search.
   */
  public SearchedSites getSearchedSites() {
    return mSearchedSites;
  }

  /**
   * @param Set a new SearchedSites bean.
   */
  public void setSearchedSites(SearchedSites pSearchedSites) {
    mSearchedSites = pSearchedSites;
  }
  
  //--------------------------------------
  // property: siteManager
  private SiteManager mSiteManager = null;
  
  /**
   * @return A bean used to manage sites.
   */
  public SiteManager getSiteManager() {
    return mSiteManager;
  }

  /**
   * @param Set a new SiteManager bean.
   */
  public void setSiteManager(SiteManager pSiteManager) {
    mSiteManager = pSiteManager;
  }
  
  //----------------------------------------
  // property: defaultMinimumValue
  private String mDefaultMinimumValue = "0";

  /**
   * @return the default minimum price slider value.
   */
  public String getDefaultMinimumValue() {
    return mDefaultMinimumValue;
  }
  
  /**
   * @param pDefaultMinimumValue - The default minimum price slider value. 
   */
  public void setDefaultMinimumValue(String pDefaultMinimumValue) {
    mDefaultMinimumValue = pDefaultMinimumValue;
  }
  
  //-------------------------------------------
  // property: defaultMaximumValue
  private String mDefaultMaximumValue = "1500";
  
  /**
   * @return the default maximum price slider value.
   */
  public String getDefaultMaximumValue() {
    return mDefaultMaximumValue;
  }
  
  /**
   * @param pDefaultMaximumValue - The default maximum price slider value. 
   */
  public void setDefaultMaximumValue(String pDefaultMaximumValue) {
    mDefaultMaximumValue = pDefaultMaximumValue;
  }
  
  //--------------------------------------------------------------------------
  // METHODS
  //--------------------------------------------------------------------------

  //--------------------------------------------------------------------------
  /**
   * Create a new BasicContentItem using the passed in ContentItem.
   * 
   * @param pContentItem - The configuration content item for this cartridge handler. This will either be 
   *                       the fully initialized configuration object, if a {@link ContentItemInitializer} 
   *                       has been set, or it will simply be the instance configuration.
   * 
   * @return an instance of <code>ConfigType</code> which wraps the input {@link ContentItem}.
   */
  @Override
  protected ContentItem wrapConfig(ContentItem pContentItem) {
    return new BasicContentItem(pContentItem);
  }

  //--------------------------------------------------------------------------
  /**
   * Currently only used to create an MdexRequest. Does not execute it.
   * 
   * @param pCartridgeConfig - The PriceSlider cartridge configuration.
   * 
   * @throws CartridgeHandlerException if the operation fails.
   */
  @Override
  public void preprocess(ContentItem pCartridgeConfig) throws CartridgeHandlerException {
    // Create the request.  Do not execute it yet.
    mMdexRequest = createMdexRequest(getNavigationState().getFilterState(), new BreadcrumbsMdexQuery());
  }

  //--------------------------------------------------------------------------
  /**
   * <p>
   *   Create a new PriceSliderContentItem. If the PriceSliderContentItem has not previously been
   *   applied, the current minimum and maximum pointers will be set to the values 
   *   obtained from the current sites searched. 
   * </p>
   * <p>
   *   If the PriceSliderContentItem has been applied then the minimum and maximum pointers will 
   *   be set to the previously selected user values. The slider range is static and is obtained 
   *   from the current sites being searched.
   * </p>
   * 
   * @param pCartridgeConfig - The PriceSlider cartridge configuration.
   * 
   * @return a PriceSliderContentItem with minimum and maximum values.
   * 
   * @throws CartridgeHandlerException if a NavigationException is caught.
   */
  @Override
  public PriceSliderContentItem process(ContentItem pCartridgeConfig) 
    throws CartridgeHandlerException  {
    
    ENEQueryResults results = executeMdexRequest(mMdexRequest);
    NavigationState navigationState = getNavigationState();
    navigationState.inform(results);

    // Create a default Price slider then configure it.
    PriceSliderContentItem slider = new PriceSliderContentItem(pCartridgeConfig);
    
    try {
      configureSlider(slider);
    }
    catch (RepositoryException e) {
      AssemblerTools.getApplicationLogging().vlogError(e,  
        "An error occurred when attempting to configure the slider");
    }
 
    RangeFilter rangeFilter = getSliderRangeFilter(slider.getPriceProperty());

    // Price slider not currently applied just return a default slider. Don't 
    // render the slider in the case we have < 2 results, we still render it
    // when all results have the same price or when a user adjusts the slider
    // and there are < 2 results.
    if(rangeFilter == null) {
      Navigation navigation = results.getNavigation();
      
      if(navigation != null) {
        long numResults = 0;
        
        // Total number of aggregated records.
        if(navigationState.getFilterState().getRollupKey() != null) {
          numResults = navigation.getTotalNumAggrERecs();
        }
        // Non rolled up records.
        else {
          numResults = navigation.getTotalNumERecs();
        }

        if(numResults < 2) {
          slider.setEnabled(false);
          return slider;
        }
      }
      
      // Default slider.
      return slider;
    }

    // Range filter bread crumb.
    RangeFilterBreadcrumb filterCrumb = buildFilterCrumb(rangeFilter);
    slider.setFilterCrumb(filterCrumb);
    return slider;
  }
  
  //--------------------------------------------------------------------------
  /**
   * Set the sliders range (min/max values).
   * 
   * @param pSlider - The PriceSliderContentItem to configure.
   * 
   * @throws RepositoryException
   */
  protected void configureSlider(PriceSliderContentItem pSlider) throws RepositoryException {
    
    Integer max = -1;
    Integer min = -1;
    
    // If site context is specified, try to get min and max
    // values for price slider from it
    if (SiteContextManager.getCurrentSiteContext() != null) {
      
      // The site ids of the currently searched sites.
      String[] siteIds = getSearchedSites().getSiteIds();
      
      // If it has no contents add the current site.
      if(siteIds == null || siteIds.length == 0) {
        
        siteIds = new String[1];
        Site currentSite = SiteContextManager.getCurrentSiteContext().getSite();
        
        if(currentSite != null) {
          siteIds[0] = currentSite.getId();
        }
      }
      
      // Iterate over the site ids and find the maximum and minimum.
      for(String siteId : siteIds) {
        RepositoryItem site = getSiteManager().getSite(siteId);
        
        // Get the minimum value.
        Integer siteCurMin = (Integer) 
          site.getPropertyValue(getStoreSitePropertiesManager().getPriceSliderMinimumValuePropertyName());
        
        if(siteCurMin != null) {
          if(min == -1) {
            min = siteCurMin;
          }
          else {
            if(siteCurMin < min) {
              min = siteCurMin;
            }
          }
        }
        
        // Get the maximum value.
        Integer siteCurMax = (Integer)
          site.getPropertyValue(getStoreSitePropertiesManager().getPriceSliderMaximumValuePropertyName());
        
        if(siteCurMax != null) {
          if(max == -1){
            max = siteCurMax;
          }
          else {
            if(siteCurMax > max) {
              max = siteCurMax;
            }
          }
        }
      }
      
    }    

    if(min == -1) {
      pSlider.setSliderMin(getDefaultMinimumValue());
    }
    else {
      pSlider.setSliderMin(Integer.toString(min));
    }
    
    if(max == -1) {
      pSlider.setSliderMax(getDefaultMaximumValue());
    }
    else {
      pSlider.setSliderMax(Integer.toString(max));
    }
  }

  //--------------------------------------------------------------------------
  /**
   * Construct a RangeFilterBreadcrumb which can be used to render the current
   * position of the slider markers.
   * 
   * @param pRangeFilder - RangeFilter used to populate the RangeFilterBreadcrumb.
   * 
   * @return the populated RangeFilterBreadcrumb.
   */
  protected RangeFilterBreadcrumb buildFilterCrumb(RangeFilter pRangeFilter) {
    
    RangeFilterBreadcrumb filterCrumb = new RangeFilterBreadcrumb();
    
    filterCrumb.setPropertyName(pRangeFilter.getPropertyName());
    filterCrumb.setOperation(pRangeFilter.getOperation().name());
    filterCrumb.setLowerBound(String.valueOf(pRangeFilter.getBound1()));
    filterCrumb.setUpperBound(String.valueOf(pRangeFilter.getBound2()));
    
    return filterCrumb;
  }

  //--------------------------------------------------------------------------  
  /**
   * Determines the RangeFilter that corresponds to our price slider.
   * 
   * @param pFilterProperty - The propertyName of the RangeFilter to be retrieved.
   * 
   * @return the RangeFilter that matches the passed in propertyName.
   */
  protected RangeFilter getSliderRangeFilter(String pFilterProperty) {
    
    // Retrieve all the currently applied RangeFilters that are held on the navigationState.filterState.
    for(RangeFilter rangeFilter : getNavigationState().getFilterState().getRangeFilters()) {
      
      // If the RangeFilter is filtering on our price property.
      if (rangeFilter.getPropertyName().equals(pFilterProperty)) {
        return rangeFilter;
      }
    }
    
    return null;
  }
}
