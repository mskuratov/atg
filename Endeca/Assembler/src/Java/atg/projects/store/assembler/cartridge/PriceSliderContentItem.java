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

package atg.projects.store.assembler.cartridge;

import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.model.RangeFilterBreadcrumb;

/**
 * PriceSliderContentItem cartridge which will be returned as part of a larger ContentItem
 * and is used by the PriceSlider.jsp to render a price slider control on the storefront.
 *
 * @author ckearney
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/PriceSliderContentItem.java#4 $$Change: 788566 $
 * @updated $DateTime: 2013/02/06 08:08:38 $$Author: ykachube $
 */
public class PriceSliderContentItem extends BasicContentItem {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/PriceSliderContentItem.java#4 $$Change: 788566 $";

     
  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------

  // Properties of the slider.
  public static final String FILTER_CRUMB = "filterCrumb";
  public static final String PRICE_PROPERTY = "priceProperty";
  public static final String SLIDER_MIN = "sliderMin";
  public static final String SLIDER_MAX = "sliderMax";
  public static final String ENABLED = "enabled";
  
  //----------------------------------------------------------------------------
  // CONSTRUCTOR
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  /**
   * Construct a PriceSlider using the properties defined in the pConfig argument.
   *  
   * @param pConfig - The configuration item used to build the PriceSlider.
   */
  public PriceSliderContentItem(ContentItem pConfig) {
    super(pConfig);
    
    // If enabled property has not been set as default to true.
    if(getEnabled() == null) {
      setEnabled(true);
    }
  }
  
  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------
  
  //-----------------------------------
  // property: filterCrumb
  //-----------------------------------
  /**
   * @return the filterCrumb property. The filterCrumb is a breadcrumb for the
   *         price slider. It tells us if the price slider has been applied.
   */
  public RangeFilterBreadcrumb getFilterCrumb() {
    return getTypedProperty(FILTER_CRUMB);
  }

  /**
   * @param pFilterCrumb - Set a new filterCrumb.
   */
  public void setFilterCrumb(RangeFilterBreadcrumb pFilterCrumb) {
    put(FILTER_CRUMB, pFilterCrumb);
  }
  
  //-----------------------------------
  // property: priceProperty
  //-----------------------------------
  
  /**
   * @return Get the priceProperty that this slider uses. This is the property
   *         in the MDEX that we perform filtering on.
   */
  public String getPriceProperty() {
    return getTypedProperty(PRICE_PROPERTY);
  }

  /**
   * @param pPriceProperty - Set a new priceProperty.
   */
  public void setPriceProperty(String pPriceProperty) {
    put(PRICE_PROPERTY, pPriceProperty);
  }
  
  //-----------------------------------
  // property: sliderMin
  //-----------------------------------
  
  /**
   * @return the price slider minimum value.
   */
  public String getSliderMin() {
    return getTypedProperty(SLIDER_MIN);
  }
  
  /**
   * @param pSliderMin - The new price slider minimum value.
   */
  public void setSliderMin(String pSliderMin) {
    put(SLIDER_MIN, pSliderMin);
  }
  
  //-----------------------------------
  // property: sliderMax
  //-----------------------------------
  
  /**
   * @return the price slider maximum value.
   */
  public String getSliderMax() {
    return getTypedProperty(SLIDER_MAX);
  }
  
  /**
   * @param pSliderMax - The new price slider maximum value.
   */
  public void setSliderMax(String pSliderMax) {
    put(SLIDER_MAX, pSliderMax);
  }
  
  //-----------------------------------
  // enabled property
  /**
   * @return whether or not the slider is enabled.
   */
  public Boolean getEnabled() {
    return getTypedProperty(ENABLED);
  }
  
  /**
   * @param pEnabled - the new enabled value.
   */
  public void setEnabled(Boolean pEnabled) {
    put(ENABLED, pEnabled);
  }
  
}