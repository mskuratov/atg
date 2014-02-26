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



package atg.projects.store.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import atg.commerce.catalog.custom.CustomCatalogTools;
import atg.core.util.StringUtils;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;


/**
 * The extensions to ootb CatalogTools.
 * At the time of its writing, all it contained were the
 * ids of the Store catalog and the corresponding
 * repository items.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/catalog/StoreCatalogTools.java#2 $
 */
public class StoreCatalogTools extends CustomCatalogTools {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/catalog/StoreCatalogTools.java#2 $$Change: 768606 $";

  /**
   * Root navigation category id.
   */
  private String mRootNavigationCategoryId = "rootCategory";

  /**
   * Size sort order.
   */
  String[] mSizeSortOrder;

  // ------------------------------------------------------------------------------
  /**
   * This field contains pairs of objects, where a key is the name of the property,
   * and the value is a key for the localized string. E.g.:
   * <pre>
   *  color=browse_productComparisons.colors,\
   *  size=browse_productComparisons.sizes,\
   * </pre>
   * Note, that the field is declared as {@link LinkedHashMap} in order to keep pairs
   * in the same order as they are specified in <code>.properties</code> file.
   */
  private Map mPropertyToLabelMap;

  /**
   * Setter-method for {@link #mPropertyToLabelMap} field.
   * @param pPropertyToLabelMap Map containing Property-to-Label pairs
   */
  public void setPropertyToLabelMap(Map pPropertyToLabelMap) {
    mPropertyToLabelMap = pPropertyToLabelMap;
  }

  /**
   * Getter-method for {@link #mPropertyToLabelMap} field.
   * @return pPropertyToLabelMap Map containing Property-to-Label pairs
   */
  public Map getPropertyToLabelMap(){
    return mPropertyToLabelMap;
  }
  // ------------------------------------------------------------------------------

  /**
   * This field contains pairs of objects, where a key is the name of the property,
   * and the value is a name of filter bean. E.g.:
   * <pre>
   * color=/atg/store/collections/filter/ColorSorter,\
   * size=/atg/store/collections/filter/SizeSorter,\
   * </pre>
   */
  private Map mPropertyToFilterMap;

  /**
   * Setter-method for {@link #mPropertyToFilterMap} field.
   * @param pPropertyToFilterMap Map containing Property-to-Filter pairs
   */
  public void setPropertyToFilterMap(Map pPropertyToFilterMap) {
    mPropertyToFilterMap = pPropertyToFilterMap;
  }

  /**
   * Getter-method for {@link #mPropertyToFilterMap} field.
   * @return pPropertyToFilterMap Map containing Property-to-Filter pairs
   */
  public Map getPropertyToFilterMap(){
    return mPropertyToFilterMap;
  }
  // ------------------------------------------------------------------------------
  /**
   * @return RootNavigationCategoryId.
   */
  public String getRootNavigationCategoryId() {
    return mRootNavigationCategoryId;
  }

  /**
   * @param pRootNavigationCategoryId - new RootNavigationCategoryId.
   */
  public void setRootNavigationCategoryId(String pRootNavigationCategoryId) {
    mRootNavigationCategoryId = pRootNavigationCategoryId;
  }

  /**
   * @return Returns the SizeSortOrder used for displaying sizes.
   */
  public String[] getSizeSortOrder() {
    return mSizeSortOrder;
  }

  /**
   * @param pSizeSortOrder The SizeSortOrder used for displaying sizes.
   */
  public void setSizeSortOrder(String[] pSizeSortOrder) {
    mSizeSortOrder = pSizeSortOrder;
  }

  /**
   * Get the catalog with the given id.
   * @return the catalog with the given id
   * @param pCatalogId - catalog id
   * @throws atg.repository.RepositoryException if error occurs
   */
  public RepositoryItem getCatalog(String pCatalogId) throws RepositoryException {
    Repository catalog = getCatalog();
    RepositoryItem catalogItem = catalog.getItem(pCatalogId, getBaseCatalogItemType());

    if (null == catalogItem && isLoggingDebug()) {
      logDebug("The Store catalog id " + pCatalogId + " is not valid.");      
    }

    return catalogItem;
  }

  /**
   * Returns the list of possible sizes for a given collection of skus.
   *
   * @param pSkus collection of skus
   * @return list of colors
   */
  public List getPossibleSizes(Collection pSkus) {
    StoreCatalogProperties catalogProps = (StoreCatalogProperties) getCatalogProperties();

    return getPossibleValuesForSkus(pSkus, catalogProps.getSizePropertyName());
  }

  /**
   * Obtains a list of sizes used by SKUs specified. This list will be sorted in correspondence to the {@link #getSizeSortOrder()} property.
   * @see #getSizeSortOrder()
   * @param pSkus collection of SKUs sizes should be taken from.
   * @return a List<String> of sizes.
   */
  @SuppressWarnings("unchecked")
  public List<String> getSortedSizes(Collection<RepositoryItem> pSkus)
  {
    return sortSizes(getPossibleSizes(pSkus));
  }

  /**
   * Returns the list of possible values for a given collection of skus.
   *
   * @param pSkus collection of skus
   * @param pPropertyName the property name of the sku to use.
   * @return Collection of values
   */
  public List getPossibleValuesForSkus(Collection pSkus, String pPropertyName) {
    List values = new ArrayList();

    if (pSkus != null && pSkus.size() > 0) {
      Iterator skuerator = pSkus.iterator();
      RepositoryItem sku;

      while (skuerator.hasNext()) {
        sku = (RepositoryItem) skuerator.next();

        Object value = sku.getPropertyValue(pPropertyName);

        if ((value == null) || (value instanceof String && StringUtils.isBlank((String) value))) {
          if (isLoggingDebug()) {
            logDebug("sku" + sku.getRepositoryId() + " has a missing " + pPropertyName + " specification");
          }
        } else if (!values.contains(value)) {
          values.add(value);
        }
      }
    }

    return values;
  }

  /**
   * Returns a product's child skus.
   * @param pProduct - product
   * @return Collection of skus
   */
  public Collection getProductChildSkus(RepositoryItem pProduct) {
    return (Collection) pProduct.getPropertyValue(getCatalogProperties().getChildSkusPropertyName());
  }

  /**
   * Sorts a list of colors.
   * @param pColors - list of pColors
   * @return the sorted list of colors.
   */
  public List sortColors(List pColors) {
    ArrayList sorted = new ArrayList(pColors.size());
    sorted.addAll(pColors);
    Collections.sort(sorted);

    return sorted;
  }
  
  /**
   * Sorts sizes accoring to the configured template.
   * @see #getSizeSortOrder()
   * @param pSizes - list of sizes
   * @return List of sorted sizes
   */
  public List sortSizes(List pSizes) {
    String[] sortOrder = getSizeSortOrder();

    if ((sortOrder != null) && (sortOrder.length > 0)) {
      return sortStrings(pSizes, getSizeSortOrder());
    } else {
      return pSizes;
    }
  }

  /**
   * Sorts the collection of strings relative to their ordinal position in the sort order array.
   * <p>
   * @param pStrings a collection of strings to sort
   * @param pSortOrder the string array of possible values and their relative order.
   * @return List of sorted Strings
   */
  public List sortStrings(List pStrings, String[] pSortOrder) {
    int size = pStrings.size();
    List sortedStrings = new ArrayList(size);
    int sortStringIndex = 0;

    for (int i = 0; (i < pSortOrder.length) && (sortStringIndex < size); i++){
      if (pStrings.contains(pSortOrder[i])) {
        sortedStrings.add(sortStringIndex++, pSortOrder[i]);
        pStrings.remove(pSortOrder[i]);
      }
    }
    // Add any sizes that weren't in the sort order at the end
    sortedStrings.addAll(pStrings);

    return sortedStrings;
  }

}
