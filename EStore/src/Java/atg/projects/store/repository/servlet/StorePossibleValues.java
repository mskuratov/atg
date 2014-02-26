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


package atg.projects.store.repository.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import javax.servlet.ServletException;

import atg.nucleus.naming.ParameterName;
import atg.repository.Repository;
import atg.repository.SortDirectives;
import atg.repository.servlet.PossibleValues;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

/**
 * Extends PossibleValues droplet to add possibility to sort possible values using
 * the custom comparator. The comparator that should be used for sorting can be specified
 * through droplet's <code>comparator</code> parameter.
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/repository/servlet/StorePossibleValues.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */

public class StorePossibleValues extends PossibleValues {
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/repository/servlet/StorePossibleValues.java#2 $$Change: 768606 $";
  
  public final static ParameterName COMPARATOR = ParameterName.getParameterName( "comparator" );
 
  /**
   * Gets the comparator to sort result list of possible values.
   * @return the comparator to sort result list of possible values.
   */
  public Comparator<PossibleValue> getComparator() {
    DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
    return (Comparator<PossibleValue>) request.getObjectParameter(COMPARATOR);
  }

  /**
   * Overrides base method to sort the result list of possible values using the specified comparator.
   *  
   * @param pRepository the repository
   * @param pItemDescriptorName item descriptor name
   * @param pPropertyName the property name
   * @param pUseCodeForValue the code for value
   * @param pSortDirectives the sort directives
   * @return wrapped results with localized labels for enumerated property values.
   */
  public Object getRepositoryValues(Repository pRepository, String pItemDescriptorName,
                                    String pPropertyName, boolean pUseCodeForValue,
                                    SortDirectives pSortDirectives) {
        
    Object values =  super.getRepositoryValues(pRepository, pItemDescriptorName,
                                               pPropertyName, pUseCodeForValue, pSortDirectives);
       
    if (values instanceof PossibleValues.PossibleValue[]){
      
      // sort values using the passed in comparator
      sort ((PossibleValues.PossibleValue[])values);
    }
    
    return values;
  }
  
  /**
   * Sorts the array of possible values using the specified comparator. If no
   * comparator is specified no changes are applied to the order of possible values
   * in the list.
   * @param pValues The array of possible values to sort.
   */
  public void sort( PossibleValue[] pValues ) {
    Comparator<PossibleValue> comparator = getComparator();
    if ( comparator != null ) {
      Arrays.sort(pValues, comparator);
    }
  }
  
}