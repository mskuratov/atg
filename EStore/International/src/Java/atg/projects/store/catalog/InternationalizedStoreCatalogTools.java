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
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import atg.core.util.StringUtils;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryItem;

/**
 * StoreCatalogTools implementation with Internationalization support. 
 * @see StoreCatalogTools
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/src/atg/projects/store/catalog/InternationalizedStoreCatalogTools.java#2 $$Change: 768606 $ 
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class InternationalizedStoreCatalogTools extends StoreCatalogTools
{
  /**
   * Class version
   */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/src/atg/projects/store/catalog/InternationalizedStoreCatalogTools.java#2 $$Change: 768606 $";

  private SizesComparator mSizesComparator;
  
  /**
   * Obtains a list of sizes used by SKUs specified. This list will be sorted in correspondence to the getSizeSortOrder() property.
   * @param pSkus collection of SKUs sizes should be taken from.
   * @return a List of sizes.
   */
  @Override
  public List<String> getSortedSizes(Collection<RepositoryItem> pSkus)
  {
    if (pSkus == null)
    {
      return Collections.emptyList();
    }
    
    InternationalizedStoreCatalogProperties catalogProperties = (InternationalizedStoreCatalogProperties) getCatalogProperties();
    String sizePropertyName = catalogProperties.getSizePropertyName();
    String defaultSizePropertyName = catalogProperties.getDefaultSizePropertyName();

    Map<String, String> sizesByDefaultSize = new HashMap<String, String>(); //Contains 'Default Size -> Size' mapping
    for (RepositoryItem sku: pSkus)
    {
      String size = (String) sku.getPropertyValue(sizePropertyName);
      String defaultSize = (String) sku.getPropertyValue(defaultSizePropertyName);
      if (!StringUtils.isEmpty(defaultSize) && !sizesByDefaultSize.containsKey(defaultSize))
      {
        sizesByDefaultSize.put(defaultSize, size);
      }
    }
    
    List<String> sortedDefaultSizes = new ArrayList<String>(sizesByDefaultSize.keySet()); 
    Collections.sort(sortedDefaultSizes, mSizesComparator);
    
    List<String> result = new LinkedList<String>();
    for (String defaultSize: sortedDefaultSizes)
    {
      result.add(sizesByDefaultSize.get(defaultSize));
    }
    
    return result;
  }
  
  /**
   * This method tries to set the defaultCatalog property. 
   * If defaultCatalogId is configured in the properties file, that ID is used. 
   * If defaultCatalogId is null and the repository contains exactly one catalog item, 
   * that item becomes the default. Otherwise, defaultCatalog remains null.
   * @throws ServiceException - if error occurs 
   */
  @Override
  public void doStartService() throws ServiceException
  {
    super.doStartService();
    mSizesComparator = new SizesComparator();
  }

  /**
   * A {@link Comparator} implementation for sorting clothing-sku sizes.
   * @author ATG
   * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/src/atg/projects/store/catalog/InternationalizedStoreCatalogTools.java#2 $$Change: 768606 $ 
   * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
   */
  private class SizesComparator implements Comparator<String>
  {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/src/atg/projects/store/catalog/InternationalizedStoreCatalogTools.java#2 $$Change: 768606 $";

    private Map<String, Integer> mSortOrder;
    
    /**
     * Creates a new instance of SizesComparator.
     * This instance reads the {@link StoreCatalogTools#getSizeSortOrder()} property and compares all passed objects
     * accordingly to this defined sort order.
     * Note that this property lists all possible default size names in ascending order.
     */
    private SizesComparator()
    {
      mSortOrder = new HashMap<String, Integer>();
      String[] sizeSortOrder = getSizeSortOrder();
      if (sizeSortOrder != null)
      {
        for (int i = 0; i < sizeSortOrder.length; i++)
        {
          mSortOrder.put(sizeSortOrder[i], i);
        }
      }
    }
    
    /**
     * This implementation of {@link Comparator#compare(Object, Object)} method is relied on the size sort order specified.
     * If any of the parameters specified is not listed in the sort order, there will be thrown an {@link IllegalArgumentException}.
     * @param pO1 - first size to be compared.
     * @param pO2 - second size to be compared.
     * @see SizesComparator#SizesComparator(String[])
     * @return 0 if the size sort order are equal
     */
    public int compare(String pO1, String pO2)
    {
      Integer order1 = mSortOrder.get(pO1);
      Integer order2 = mSortOrder.get(pO2);
      if (order1 == null || order2 == null)
      {
        throw new IllegalArgumentException("Only default size values should be compared. Unknown size: " + (order1 == null ? order1 : order2));
      }
      return order1 - order2;
    }
  }
}
