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


package atg.projects.store.sort;

import java.util.List;
import java.util.Locale;

/**
 * A ProductSorter interface. Classes which sort a list of RepositoryItems pItems
 * by a particular property of a RepositoryItem in a particular direction pDirection
 * should implement this interface and add the path to their sorter component to
 * the SimpleSort droplets sorters property.
 * 
 * @author ckearney
 */
public interface PropertySorter {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/sort/PropertySorter.java#2 $$Change: 768606 $";

  
  //-----------------------------------
  // STATIC
  //-----------------------------------
  
  // Sort Directions
  public static final String ASCENDING = "ascending";
  public static final String DESCENDING = "descending";
  
  // Symbols the SortArray understands
  public static final String ASCENDING_SYMBOL = "+";
  public static final String DESCENDING_SYMBOL = "-";
    
  /**
   * Classes which sort a list of RepositoryItems pItems by a particular property
   * of a RepositoryItem in a particular direction pDirectionshould implement this
   * method to perform the actual sorting.
   * 
   * @param pItems An unsorted list of RepositoryItem's
   * @param pDirection The direction of the sorted list "ascending" or "descending"
   * @param pLocale The locale
   * @return A sorted list
   */
  public Object[] sort(List pItems, String pDirection, Locale pLocale);
}
