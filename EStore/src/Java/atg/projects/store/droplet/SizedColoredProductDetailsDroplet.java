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
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.ServletException;

/**
 * This droplet should be used on the PDP with wood finish picker.
 *
 * <p>This droplet takes currently viewed product and collection of its child SKUs from the current request. It saves all necessary data
 * to be displayed into the current request.</p>
 *
 * <p>Please, see a list of inherited input/output/open parameters are inherited from the {@link ColoredProductDetailsDroplet} droplet.</p>
 *
 * <p>Please note that color parameter name should be specified through droplet configuration. Parameter with this name will be taken
 * from SKUs specified when searching them by color or calculating available colors.</p>
 *
 * <p><b>Additional input Parameters:</b>
 *   <dl>
 *     <dt>selectedSize</dt>
 *     <dd>Currently selected size.</dd>
 *   </dl>
 * </p>
 *
 * <p><b>Additional output parameter:</b>
 *   <dl>
 *     <dt>availableSizes<dt>
 *     <dd>All available sizes for the collection of SKUs specified. These sizes are stored in form of {@link Size} instances.</dd>
 *   </dl>
 * </p>
 *
 * @see DefaultProductDetailsDroplet
 * @see ColoredProductDetailsDroplet
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/SizedColoredProductDetailsDroplet.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class SizedColoredProductDetailsDroplet extends ColoredProductDetailsDroplet {
  
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/SizedColoredProductDetailsDroplet.java#2 $$Change: 768606 $";

  protected static final String PARAMETER_AVAILABLE_SIZES = "availableSizes";
  protected static final String PARAMETER_SELECTED_SIZE = "selectedSize";

  /**
   * Provides the implementation of service method
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @throws ServletException an application specific error occurred processing this request
   * @throws IOException an error occurred reading data from the request or writing data to the response.
   */
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    pRequest.setParameter(PARAMETER_SELECTED_SIZE, getSelectedSize(pRequest));
    pRequest.setParameter(PARAMETER_AVAILABLE_SIZES, getAllSizes(pRequest));
    super.service(pRequest, pResponse);
  }

  /**
   * This method calculates currently selected SKU on base of selected color and size.
   * If color or size is not selected yet, nothing will be returned.
   * @param pRequest current request.
   * @return currently selected SKU.
   */
  @Override
  protected RepositoryItem getSelectedSku(DynamoHttpServletRequest pRequest) {
    String selectedColor = getSelectedColor(pRequest);
    String selectedSize = getSelectedSize(pRequest);
    if (selectedColor != null && selectedSize != null) {
      // A color and size have been selected so get the sku by color and size
      return findSkuByColorSize(getAllSkus(pRequest), selectedColor, selectedSize);
    }
    else if (selectedColor != null && getAllSizes(pRequest).size() == 0) {
      // The item does not have size options so get the sku by the selected color
      return findSkuByColor(getAllSkus(pRequest), selectedColor);
    }
    return null;
  }

  /**
   * @param pRequest current request.
   * @param pColor color to be queried.
   * @return one of the possible states.
   */
  @Override
  protected String getStateForColor(DynamoHttpServletRequest pRequest, String pColor) {
    if (pColor.equals(getSelectedColor(pRequest))) {
      return AVAILABILITY_STATUS_SELECTED;
    }
    String selectedSize = getSelectedSize(pRequest);
    if (selectedSize == null) {
      return AVAILABILITY_STATUS_AVAILABLE;
    }
    RepositoryItem specifiedSku = findSkuByColorSize(getAllSkus(pRequest), pColor, selectedSize);
    if (specifiedSku == null) {
      return AVAILABILITY_STATUS_NOT_OFFERED;
    }
    return getAvailabilityType(specifiedSku, getCurrentProduct(pRequest));
  }

  /**
   * This method calculates currently selected size. If something is specified throug the <code>selectedSize</code> request parameter,
   * it will be used as a selected size. Otherwise all possible sizes will be taken from SKUs specified; and if only one size is available
   * this size will be used as selected.
   * @param pRequest current request.
   * @return currently selected size.
   */
  protected String getSelectedSize(DynamoHttpServletRequest pRequest) {
    String selectedSize = (String) pRequest.getObjectParameter(PARAMETER_SELECTED_SIZE);
    Collection<String> possibleSizes = getCatalogTools().getPossibleValuesForSkus(getAllSkus(pRequest), getCatalogProperties().getSizePropertyName());
    if (!StringUtils.isBlank(selectedSize) && possibleSizes.size() > 0) {
      return selectedSize;
    }
    if (possibleSizes.size() == 1) {
      return possibleSizes.iterator().next();
    }
    return null;
  }

  /**
   * This method calculates all possible sizes for the SKUs specified. These sizes are returned in form of {@link Size} instances.
   * @param pRequest current request.
   * @return <code>Collection</code> of sizes.
   */
  protected Collection<Size> getAllSizes(DynamoHttpServletRequest pRequest) {
    ArrayList<Size> allSizes = new ArrayList<Size>();
    Collection<String> possibleSizes = getCatalogTools().getSortedSizes(getAllSkus(pRequest));
    for (String size: possibleSizes) {
      allSizes.add(new Size(size, getStateForSize(pRequest, size)));
    }
    return allSizes;
  }

  /**
   * This method calculates a state for the size specified. The following states are possible:
   * <ol>
   *   <li>{@link #AVAILABILITY_STATUS_SELECTED} - if SKU of this size is currently selected.</li>
   *   <li>{@link #AVAILABILITY_STATUS_AVAILABLE} - if SKU of this size is available for ordering.</li>
   *   <li>{@link #AVAILABILITY_STATUS_BACKORDERABLE} - if SKU of this size is available for backordering.</li>
   *   <li>{@link #AVAILABILITY_STATUS_PREORDERABLE} - if SKU of this size is available for preordering.</li>
   *   <li>{@link #AVAILABILITY_STATUS_UNAVAILABLE} - if SKU of this size is not available for ordering.</li>
   *   <li>{@link #AVAILABILITY_STATUS_NOT_OFFERED} - if SKU of this size is not presented in catalog.</li>
   * </ol>
   * @param pRequest current request.
   * @param pSize size to be queried.
   * @return one of the possible states.
   */
  protected String getStateForSize(DynamoHttpServletRequest pRequest, String pSize) {
    if (pSize.equals(getSelectedSize(pRequest))) {
      return AVAILABILITY_STATUS_SELECTED;
    }
    String selectedColor = getSelectedColor(pRequest);
    if (selectedColor == null) {
      return AVAILABILITY_STATUS_AVAILABLE;
    }
    RepositoryItem specifiedSku = findSkuByColorSize(getAllSkus(pRequest), selectedColor, pSize);
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
   * This method searches for SKU by color and size specified.
   * @param pSkus Collection of skus to look in
   * @param pColor color to look for
   * @param pSize size to look for
   * @return first SKU from the collection specified with both color and size equal
   * to those specified through method parameters.
   */
  private RepositoryItem findSkuByColorSize(Collection<RepositoryItem> pSkus, String pColor, String pSize) {
    for (RepositoryItem sku: pSkus) {
      String skuColor = (String) sku.getPropertyValue(getColorPropertyName());
      String skuSize = (String) sku.getPropertyValue(getCatalogProperties().getSizePropertyName());
      if (pColor.equals(skuColor) && pSize.equals(skuSize)) {
        return sku;
      }
    }
    return null;
  }

  /**
   * This is an information bean to be used on the JSP pages. It contains size's name and its status. To see the list of possible
   * statuses, refer to the {@link SizedColoredProductDetailsDroplet#getStateForSize} method.
   */
  public static class Size { // Hide this class and display it to the droplet's extentions only.
    private String mName;
    private String mStatus;

    /**
     * @param pName - name
     * @param pStatus - status
     */
    public Size(String pName, String pStatus) {
      mName = pName;
      mStatus = pStatus;
    }

    /**
     * @return the name
     */
    public String getName() {
      return mName;
    }

    /**
     * @return the status
     */
    public String getStatus() {
      return mStatus;
    }
  }
}
