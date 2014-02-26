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

import atg.core.util.StringUtils;
import atg.projects.store.catalog.StoreCatalogProperties;
import atg.projects.store.catalog.StoreCatalogTools;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;

/**
 * This droplet should be used on the PDP with wood finish picker.
 *
 * <p>This droplet takes currently viewed product and collection of its child SKUs from the current request. It saves all necessary data
 * to be displayed into the current request.</p>
 *
 * <p><b>The <code>selectedSku</code> is not an input parameter! It's an output parameter for the current droplet.</b>
 * All other input/output/open parameters are inherited from the {@link DefaultProductDetailsDroplet} droplet.</p>
 *
 * <p>Please note that color parameter name should be specified through droplet configuration. Parameter with this name will be taken
 * from SKUs specified when searching them by color or calculating available colors.</p>
 *
 * <p><b>Additional input Parameters:</b>
 *   <dl>
 *     <dt>selectedColor</dt>
 *     <dd>Currently selected color (or wood finish).</dd>
 *     <dt>savedquantity</dt>
 *     <dd>Quantity submitted by the 'refresh picker form'. Used to save entered quantity when refreshing the picker.</dd>
 *   </dl>
 * </p>
 *
 * <p><b>Additional output parameter:</b>
 *   <dl>
 *     <dt>availableColors<dt>
 *     <dd>All available colors/finishes for the collection of SKUs specified. These colors are stored in form of {@link Color} instances.</dd>
 *   </dl>
 * </p>
 *
 * @see DefaultProductDetailsDroplet
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/ColoredProductDetailsDroplet.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class ColoredProductDetailsDroplet extends DefaultProductDetailsDroplet {
  
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/ColoredProductDetailsDroplet.java#2 $$Change: 768606 $";

  protected static final String AVAILABILITY_STATUS_NOT_OFFERED = "notoffered";
  protected static final String AVAILABILITY_STATUS_SELECTED = "selected";
  protected static final String PARAMETER_SAVED_QUANTITY = "savedquantity";  
  protected static final String PARAMETER_AVAILABLE_COLORS = "availableColors";
  protected static final String PARAMETER_SELECTED_COLOR = "selectedColor";

  private String mColorPropertyName;
  
  /**
   * Gets the mColorPropertyName
   * @return mColorPropertyName
   */
  public String getColorPropertyName() {
    return mColorPropertyName;
  }

  /**
   * Sets the mColorPropertyName
   * @param pColorPropertyName Value to set
   */
  public void setColorPropertyName(String pColorPropertyName) {
    mColorPropertyName = pColorPropertyName;
  }


  /**
   * Gets the catalog properties
   * @return CatalogProperties
   */
  public StoreCatalogProperties getCatalogProperties() {
    return (StoreCatalogProperties) getCatalogTools().getCatalogProperties();
  }

  /**
   * Provides the implementation of service method
   *
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException an application specific error occurred
   * processing this request
   * @exception IOException an error occurred reading data from the request
   * or writing data to the response.
   */
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    pRequest.setParameter(PARAMETER_SELECTED_COLOR, getSelectedColor(pRequest));
    pRequest.setParameter(PARAMETER_AVAILABLE_COLORS, getAllColors(pRequest));
    super.service(pRequest, pResponse);
  }

  /**
   * This method calculates currently selected SKU on base of the currently selected color.
   * @param pRequest current request.
   * @return currently selected SKU.
   */
  @Override
  protected RepositoryItem getSelectedSku(DynamoHttpServletRequest pRequest) {
    String selectedColor = getSelectedColor(pRequest);
    // No color selected? then no SKU selected.
    if (selectedColor == null) {
      return null;
    }
    return findSkuByColor(getAllSkus(pRequest), selectedColor);
  }

  /**
   * Overrides default implementation provided by the {@link DefaultProductDetailsDroplet}.
   * This implementation first looks into <code>savedquantity</code> request parameter. If its value is not empty, this value will be
   * returned. Otherwise default logic is being called.
   * <p>This is done to save quantity entered by user when updating picker contents.</p>
   * @param pRequest current request.
   * @return default quantity.
   */
  @Override
  protected long getQuantity(DynamoHttpServletRequest pRequest) {
    String savedQuantity = (String) pRequest.getObjectParameter(PARAMETER_SAVED_QUANTITY);
    if (!StringUtils.isBlank(savedQuantity)) {
      return Long.valueOf(savedQuantity);
    }
    return super.getQuantity(pRequest);
  }

  /**
   * This method obtains currently selected color. It first looks into <code>selectedColor</code> request parameter. If something
   * is specified within this parameter, this value will be returned. If nothing found, this method calculates all available SKUs
   * colors; if there is only one color available, this color is returned as selected.
   * @param pRequest current request.
   * @return currently selected color.
   */
  protected String getSelectedColor(DynamoHttpServletRequest pRequest) {
    String selectedColor = (String) pRequest.getObjectParameter(PARAMETER_SELECTED_COLOR);
    if (!StringUtils.isBlank(selectedColor)) {
      return selectedColor;
    }
    Collection<String> possibleColors = getCatalogTools().getPossibleValuesForSkus(getAllSkus(pRequest), getColorPropertyName());
    if (possibleColors.size() == 1) {
      return possibleColors.iterator().next();
    }
    return null;
  }

  /**
   * This method calculates all possible colors for the SKUs specified. These colors are returned in form of {@link Color} instances.
   * @param pRequest current request.
   * @return <code>Collection</code> of all possible colors.
   */
  protected Collection<Color> getAllColors(DynamoHttpServletRequest pRequest) {
    List<String> possibleColors = getCatalogTools().getPossibleValuesForSkus(getAllSkus(pRequest), getColorPropertyName());
    Collections.sort(possibleColors);
    Map<String, RepositoryItem> possibleSwatches = getPossibleSwatches(pRequest);
    ArrayList<Color> allColors = new ArrayList<Color>();
    for (String colorName: possibleColors) {
      allColors.add(new Color(colorName, getStateForColor(pRequest, colorName), possibleSwatches.get(colorName)));
    }
    return allColors;
  }

  /**
   * This method calculates a <code>Map</code> linking SKU color to its color swatch repository item. Only SKUs with both
   * color and swatch populated contribute to this map.
   * @param pRequest current request.
   * @return Map with swatches.
   */
  protected Map<String, RepositoryItem> getPossibleSwatches(DynamoHttpServletRequest pRequest) {
    HashMap<String, RepositoryItem> result = new HashMap<String, RepositoryItem>();
    for (RepositoryItem sku: getAllSkus(pRequest)) {
      String currentColor = (String) sku.getPropertyValue(getColorPropertyName());
      if (!StringUtils.isBlank(currentColor) && !result.containsKey(currentColor)) {
        RepositoryItem currentSwatch = (RepositoryItem) sku.getPropertyValue(getCatalogProperties().getColorSwatchName());
        if (currentSwatch != null) {
          result.put(currentColor, currentSwatch);
        }
      }
    }
    return result;
  }

  /**
   * This method calculates a state for the color specified. The following states are possible:
   * <ol>
   *   <li>{@link #AVAILABILITY_STATUS_SELECTED} - if SKU of this color is currently selected.</li>
   *   <li>{@link #AVAILABILITY_STATUS_AVAILABLE} - if SKU of this color is available for ordering.</li>
   *   <li>{@link #AVAILABILITY_STATUS_BACKORDERABLE} - if SKU of this color is available for backordering.</li>
   *   <li>{@link #AVAILABILITY_STATUS_PREORDERABLE} - if SKU of this color is available for preordering.</li>
   *   <li>{@link #AVAILABILITY_STATUS_UNAVAILABLE} - if SKU of this color is not available for ordering.</li>
   *   <li>{@link #AVAILABILITY_STATUS_NOT_OFFERED} - if SKU of this color is not presented in catalog.</li>
   * </ol>
   * @param pRequest current request.
   * @param pColor color to be queried.
   * @return one of the possible states.
   */
  protected String getStateForColor(DynamoHttpServletRequest pRequest, String pColor) {
    if (pColor.equals(getSelectedColor(pRequest))) {
      return AVAILABILITY_STATUS_SELECTED;
    }
    RepositoryItem specifiedSku = findSkuByColor(getAllSkus(pRequest), pColor);
    if (specifiedSku == null) {
      return AVAILABILITY_STATUS_NOT_OFFERED;
    }
    return getAvailabilityType(specifiedSku, getCurrentProduct(pRequest));
  }

  /**
   * This method searches for a SKU by its color. It iterates over all specified SKUs and retrieves 
   * first SKU with color specified.
   * @param pSkus Collection of skus to look in
   * @param pColor color to look for
   * @return RepositoryItem sku with color pColor
   */
  private RepositoryItem findSkuByColor(Collection<RepositoryItem> pSkus, String pColor) {
    for (RepositoryItem sku: pSkus) {
      String skuColor = (String) sku.getPropertyValue(getColorPropertyName());
      if (pColor.equals(skuColor)) {
        return sku;
      }
    }
    return null;
  }

  /**
   * This is an information bean to be used on the JSP pages. It contains color's name, its status and color swatch item.
   * List of possible states is documented on the {@link ColoredProductDetailsDroplet#getStateForColor} method.
   */
  public static class Color { // Hide this class and dislpay it to droplet's extentions only.
    private String mName;
    private String mStatus;
    private RepositoryItem mSwatch;

    /**
     * Constructor for color class
     * @param pName name to set
     * @param pStatus status to set
     * @param pSwatch swatch to set
     */
    public Color(String pName, String pStatus, RepositoryItem pSwatch) {
      mName = pName;
      mStatus = pStatus;
      mSwatch = pSwatch;
    }

    /**
     * Gets the name
     * @return mName
     */
    public String getName() {
      return mName;
    }

    /**
     * Gets the status
     * @return mStatus
     */
    public String getStatus() {
      return mStatus;
    }

    /**
     * Gets the swatch
     * @return mSwatch
     */
    public RepositoryItem getSwatch() {
      return mSwatch;
    }
  }
}
