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

package atg.projects.store.collections.validator;

import java.text.SimpleDateFormat;
import java.util.Date;

import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;
import atg.service.collections.validator.CollectionObjectValidator;
import atg.service.util.CurrentDate;

/**
 * <p>
 * This validator validates all products by start and end date.
 * </p>
 * <p>If today's date falls on or between the start and end dates
 * then the product is valid.
 * <p>If the start date is null then the product is valid
 * as long as the endDate has not passed.
 * <p>If the end date is null then the product is valid if the startDate
 * has passed.
 * <p>If both the start and end dates are null then the product is valid
 *
 * @author ATG
 * @version $Revision: #2 $
 */
public class StartEndDateValidator extends GenericService implements CollectionObjectValidator {

  public static final String DEBUG_DATE_FORMAT_PATTERN = "yyyy.MM.dd G 'at' HH:mm:ss z";

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/collections/validator/StartEndDateValidator.java#2 $$Change: 791340 $";

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
   * property: startDatePropertyName
   */
  private String mStartDatePropertyName;
   
  /**
   * @return the mStartDatePropertyName property
   */
  public String getStartDatePropertyName() {
    return mStartDatePropertyName;
  }

  /**
   * @param pStartDatePropertyName the start date property name to set
   */
  public void setStartDatePropertyName(String pStartDatePropertyName) {
    this.mStartDatePropertyName = pStartDatePropertyName;
  }

  /**
   * property: endDatePropertyName
   */
  private String mEndDatePropertyName;
   
  /**
   * @return the mEndDatePropertyName property
   */
  public String getEndDatePropertyName() {
    return mEndDatePropertyName;
  }

  /**
   * @param pEndDatePropertyName the end date property name to set
   */
  public void setEndDatePropertyName(String pEndDatePropertyName) {
    this.mEndDatePropertyName = pEndDatePropertyName;
  }

  /**
   * These method validates the passed in object (repository items) based on
   * it's startDate and endDate properties. 
   * 
   * If today's date falls on or between the start and end dates
   * then the product is valid.
   * If the start date is null then the product is valid
   * as long as the endDate has not passed.
   * If the end date is null then the product is valid if the startDate
   * has passed.
   * If both the start and end dates are null then the product is valid
   * 
   * @param pObject an object to validate
   * @return true if the object passes validation or if no validation was performed.
   */
  public boolean validateObject(Object pObject) {
    if (!(pObject instanceof RepositoryItem) ) {
      return false;
    }
    
    RepositoryItem item = (RepositoryItem) pObject;
    Date current = getCurrentDate().getTimeAsTimestamp();

    // if it starts after today then its not visible
    Date start = (Date) item.getPropertyValue(getStartDatePropertyName());
    if (start != null && start.after(current)) {
      vlogDebug("Item {0} has an start date after today and has failed validation",
        item.getRepositoryId());
      return false;     
    }
    
    // if it ends before today then its not visible
    Date end = (Date) item.getPropertyValue(getEndDatePropertyName());
    if (end != null && end.before(current)) {
      vlogDebug("Item {0} has an end date before today and has failed validation",
        item.getRepositoryId());
      return false;   
    }

    return true;
  }

}
