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


package atg.projects.store.collections.filter;

import atg.projects.store.catalog.StoreCatalogTools;

import atg.repository.RepositoryItem;

import atg.service.collections.filter.CachedCollectionFilter;
import atg.service.collections.filter.FilterException;

import java.util.Collection;
import java.util.List;


/**
 * This filter sorts a collection of size names.
 * <p>
 * Caching should not be enabled for this filter.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/collections/filter/SizeSorter.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class SizeSorter extends CachedCollectionFilter {
  /**
   * Class version string.
   */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/collections/filter/SizeSorter.java#2 $$Change: 768606 $";

  /**
   * Catalog tools.
   */
  protected StoreCatalogTools mCatalogTools;

  /**
   * @return catalog tools.
   */
  public StoreCatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  /**
   * @param pCatalogTools - catalog tools.
   */
  public void setCatalogTools(StoreCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  /**
  * Generates a context key for this filter. This method always returns <code>null</code>
  * with the assumption that caching is turned off.
  *
  * @param pUnfilteredCollection the collection to be filtered
  * @param pCollectionIdentifierKey the key that uniquely identifies the unfiltered collection
  * @param pProfile the user profile
  * @return the context key object
  */
  public Object generateContextKey(Collection pUnfilteredCollection, String pCollectionIdentifierKey,
    RepositoryItem pProfile) {
    return null;
  }

  /**
  * {@inheritDoc}
  * @return true
  */
  public boolean shouldApplyFilter(Collection pUnfilteredCollection, String pKey, RepositoryItem pProfile) {
    return true;
  }

  /**
  * Generates a sorted collection.
  *
  * @param pUnfilteredCollection the unfiltered collection
  * @param pCollectionIdentifierKey the key the uniquely identifies the unfiltered collection (ignored)
  * @param pProfile the user profile
  * @return the sorted collection
  * @throws FilterException This exception indicates that a severe error occurs while
  * performing sortSizes method
  */
  protected Collection generateFilteredCollection(Collection pUnfilteredCollection, String pCollectionIdentifierKey,
    RepositoryItem pProfile) throws FilterException {
    if (pUnfilteredCollection instanceof List) {
      return getCatalogTools().sortSizes((List) pUnfilteredCollection);
    }

    return pUnfilteredCollection;
  }
}
