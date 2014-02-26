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
package atg.projects.store.returns;

import java.util.Calendar;
import java.util.Date;

import atg.commerce.csr.returns.ReturnTools;
import atg.commerce.csr.returns.ReturnableStates;
import atg.repository.RepositoryItem;
import atg.service.util.CurrentDate;

/**
 * Extends ReturnTools to add one more returnable state: ORDER_BEYOND_RETURN_PERIOD.
 * This state will be set if orders submit date is more than mDaysAllowedToReturn
 * days ago. 
 * 
 * @author Yekaterina Kostenevich
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/returns/StoreReturnTools.java#3 $Change: 630322 $
 * @updated $DateTime: 2013/02/19 09:03:40 $Author: ykostene $
 *
 */
public class StoreReturnTools extends ReturnTools{
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/returns/StoreReturnTools.java#3 $Change: 630322 $";
  
  public static final int UNLIMITED_DAYS_ALLOWED_TO_RETURN = -1;
  
  /**
   * Contains number of days from submition day when
   * the order can be returned
   */
  private int mDaysAllowedToReturn = UNLIMITED_DAYS_ALLOWED_TO_RETURN;

  /**
   * @return the mDaysAllowedToReturn
   */
  public int getDaysAllowedToReturn() {
    return mDaysAllowedToReturn;
  }

  /**
   * @param pDaysAllowedToReturn the mDaysAllowedToReturn to set
   */
  public void setDaysAllowedToReturn(int pDaysAllowedToReturn) {
    this.mDaysAllowedToReturn = pDaysAllowedToReturn;
  }
  
  /**
   * Contains order's submitted date property name
   */
  private String mSubmittedDatePropertyName;

  /**
   * @return the mSubmittedDatePropertyName
   */
  public String getSubmittedDatePropertyName() {
    return mSubmittedDatePropertyName;
  }

  /**
   * @param pSubmittedDatePropertyName the pSubmittedDatePropertyName to set
   */
  public void setSubmittedDatePropertyName(String pSubmittedDatePropertyName) {
    this.mSubmittedDatePropertyName = pSubmittedDatePropertyName;
  }
  
  
  /**
   * property: currentDate
   */
  private CurrentDate mCurrentDate;
 
  /**
   * @param pCurrentDate the current date.
   */
  public void setCurrentDate(CurrentDate pCurrentDate) {
    mCurrentDate = pCurrentDate;
  }

  /**
   * @return the mCurrentDate property
   */
  public CurrentDate getCurrentDate() {
    return mCurrentDate;
  }
  
  /**
   * Adds ORDER_BEYOND_RETURN_PERIOD return state. This state
   * is set to order if order submission date is more then 
   * mDaysAllowedToReturn in the past.
   * 
   * @param pOrderItem
   * @return true if a return can be done on the order.
   * @see ReturnableStates
   */
  @Override
  public String getOrderReturnableState(RepositoryItem pOrderItem) {
    
    String returnableState = super.getOrderReturnableState(pOrderItem);
    
    // If the state returned from super class is not returnable or
    // period allow to return is unlimited return the same state
    // as for super class.
    if(!StoreReturnableStates.ORDER_RETURNABLE.equals(returnableState) ||
       getDaysAllowedToReturn() == UNLIMITED_DAYS_ALLOWED_TO_RETURN) {
      return returnableState;
    }
    
    // Check whether order's submission date is not past allowed return period
    Date submittedDate = (Date) pOrderItem.getPropertyValue(getSubmittedDatePropertyName());    
    
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(submittedDate);
    // Add daysAllowedToReturn to submittionDate
    calendar.add(Calendar.DATE, getDaysAllowedToReturn());
    
    Date dateToCompare = calendar.getTime();
    Date currentDate = getCurrentDate().getDateAsDate();
    
    /* If current date is after submission date plus days allowed to
     * return, return ORDER_BEYOND_RETURN_PERIOD state. Otherwise 
     * return the result of super method
     */
    
    if (currentDate.before(dateToCompare)) {
      return returnableState;
      
    }
    else {
      return StoreReturnableStates.ORDER_BEYOND_RETURN_PERIOD;
    }  
    
  }
  

}
