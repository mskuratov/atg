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

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;

import atg.droplet.Range;
import atg.nucleus.ServiceMap;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;

/**
 * A Droplet used to sort a list of product repository items.
 * 
 * <p>
 * Input Parameters:
 * 
 * <code>sortProperty</code> - This propery should indicate which 
 * atg.projects.store.sort.SimpleSorter to use to sort the repository item list. It should match 
 * a key in the mSorters map, for example "price" or "name".
 * 
 * <code>sortOrder</code> (Optional) - Indicates the direction the sorted list should take, either
 * "ascending" or "descending". If this is not specified it will default to "ascending".
 * 
 * <code>itemList</code> - The unsorted list of RepositoryItem's.
 * </p>
 *
 * <p>
 * Open Parameters: 
 * <code>output</code> - Serviced when there are no errors.
 * </p>
 * 
 * <p>
 * Output Parameters:
 * <code>sortedList</code> - A list sorted using the sortProperty sorter in sortOrder direction. If
 * no sortProperty is specified or no sorter is found to sort by the specified sortProperty then the
 * unsorted itemList is returned.
 * </p>
 * 
 * @author ckearney
 */
public class RangeSortDroplet extends Range{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/sort/RangeSortDroplet.java#2 $$Change: 768606 $";



  //-----------------------------------
  // STATIC
  //----------------------------------- 
  public static final String SORT_PARAM_DELIMITER = ":";
  
  /* Input Parameters */
  public static final String SORT_SELECTION_PARAM = "sortSelection";
  
  //-----------------------------------
  // PROPERTIES
  //-----------------------------------

  //-----------------------------------
  // property: mSorters
  private ServiceMap mSorters;
  /** @return A ServiceMap of sorter names e.g "price" and the components used to sort */
  public ServiceMap getSorters() {
    return mSorters;
  }
  /** @param pSorters A ServiceMap of sorter names e.g "price" and the components used to sort */
  public void setSorters(ServiceMap pSorters) {
    mSorters = pSorters;
  }
  
  //-----------------------------------
  // METHODS
  //-----------------------------------
  /**
   * Sorts the input parameter <code>itemList</code> using the <code>sortProperty</code> 
   * SimpleSorter by <code>sortOrder</code>
   * 
   * @param pRequest The current DynamoHttpServletRequest
   * @param pResponse The DynamoHttpServletResponse
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {   
    /*
     * Input validation
     */
    
    // Input Parameter: array
    Object items = pRequest.getObjectParameter(Range.ARRAY);
    if(!(items instanceof List)){
      super.service(pRequest, pResponse);
      return;
    }
    
    // Input Parameter: sortSelection
    String sortSelection = pRequest.getParameter(SORT_SELECTION_PARAM);
    if(!(sortSelection instanceof String)){
      //defaultSortOrder(pRequest, pResponse);
      super.service(pRequest, pResponse);
      return;
    }
        
    /*
     * Parse input
     */
    String sortProperty = null;
    String sortDirection = PropertySorter.DESCENDING; 
    
    String[] sortParams = sortSelection.split(SORT_PARAM_DELIMITER);
    if(sortParams.length == 1){
      sortProperty = sortParams[0];
    }
    else if(sortParams.length == 2){
      sortProperty = sortParams[0];
      sortDirection = sortParams[1];
    }
    else{
      super.service(pRequest, pResponse);
      return;
    }
        
    /*
     * Perform Sorting
     */
    Object sortedItems = items;
    if(getSorters().containsKey(sortProperty)){
      Object sorter = getSorters().get(sortProperty);
      if(sorter instanceof PropertySorter){
        
        Locale locale = null;
        if(pRequest != null){
          RequestLocale requestLocale = pRequest.getRequestLocale();
          
          if(requestLocale != null){
            locale = requestLocale.getLocale();
          }
          else{
            locale = pRequest.getLocale();
          }
        }
        
        sortedItems = ((PropertySorter) sorter).sort((List)items, sortDirection, locale);
      }
    }

    pRequest.setParameter(Range.ARRAY, sortedItems);
    
    // Perform pagination
    super.service(pRequest, pResponse);
  }
}