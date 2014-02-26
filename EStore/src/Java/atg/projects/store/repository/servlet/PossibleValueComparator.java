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

import java.util.Comparator;

import atg.core.util.StringUtils;
import atg.repository.servlet.PossibleValues.EnumeratedOptionPossibleValue;
import atg.repository.servlet.PossibleValues.PossibleValue;

/**
 * Comparator class for PossibleValue objects. Sorts PossibleValue objects alphabetically 
 * by their labels (or localized labels for EnumeratedOptionPossibleValue type) with the
 * exception that the possible value which <code>settableValue</code> 
 * is the same as <code>firstElementValue</code> will be always at the beginning of the list
 * and possible value with <code>settableValue</code> the same as in <code>lastElementValue</code>
 * property will be always at the end of the list.
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/repository/servlet/PossibleValueComparator.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */

public class PossibleValueComparator implements Comparator<PossibleValue>{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/repository/servlet/PossibleValueComparator.java#2 $$Change: 768606 $";
  
  //-------------------------------------
  // The FirstElementValue property
  String mFirstElementValue;  

  /**
   * Gets the element value that always should be put at the beginning of the list
   * @return the element value that always should be put at the beginning of the list
   */
  public String getFirstElementValue() {
    return mFirstElementValue;
  }

  /**
   * Sets the element value that always should be put at the beginning of the list.
   * @param pFirstElementValue - the element value that always should be put at the beginning of the list.
   */
  public void setFirstElementValue(String pFirstElementValue) {
    this.mFirstElementValue = pFirstElementValue;
  }
  
  //-------------------------------------
  // The LastElementValue property
  String mLastElementValue;  

  /**
   * Gets the element value that always should be put at the end of the list
   * @return the element value that always should be put at the end of the list
   */
  public String getLastElementValue() {
    return mLastElementValue;
  }

  /**
   * Sets the element value that always should be put at the end of the list.
   * @param pLastElementValue - the element value that always should be put at the end of the list.
   */
  public void setLastElementValue(String pLastElementValue) {
    this.mLastElementValue = pLastElementValue;
  }

  /**
   * Compares two PossibleValue objects alphabetically 
   * by their labels (or localized labels for EnumeratedOptionPossibleValue type) with the
   * exception that the possible value which <code>settableValue</code> 
   * is the same as <code>firstElementValue</code> will be always at the beginning of the list
   * and possible value with <code>settableValue</code> the same as in <code>lastElementValue</code>
   * property will be always at the end of the list. 
   */
  public int compare( PossibleValue pObj1, PossibleValue pObj2 ) {
    if (!StringUtils.isEmpty(getLastElementValue())){
      if(getLastElementValue().equalsIgnoreCase((String)pObj1.getSettableValue())){
        return Integer.MAX_VALUE;
      }
      if(getLastElementValue().equalsIgnoreCase((String)pObj2.getSettableValue())){
        return Integer.MIN_VALUE;
      }
    }
    if (!StringUtils.isEmpty(getFirstElementValue())){
      if(getFirstElementValue().equalsIgnoreCase((String)pObj1.getSettableValue())){
        return Integer.MIN_VALUE;
      }
      if(getFirstElementValue().equalsIgnoreCase((String)pObj2.getSettableValue())){
        return Integer.MAX_VALUE;
      }
    }
    // if it enumerated option's possible value sort by localized labels
    if (pObj1 instanceof EnumeratedOptionPossibleValue){
      return ((EnumeratedOptionPossibleValue) pObj1).getLocalizedLabel().compareTo(((EnumeratedOptionPossibleValue)pObj2).getLocalizedLabel());
    }
    return pObj1.getLabel().compareTo(pObj2.getLabel());
  }
}
