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

package atg.projects.store.dp;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;
import atg.repository.UnsupportedFeatureException;
import atg.repository.dp.Derivation;
import atg.repository.dp.DerivationMethodImpl;
import atg.repository.dp.PropertyExpression;
import atg.repository.query.PropertyQueryExpression;
import atg.servlet.ServletUtil;

/**
 * This derived property method will derive a property based on device/browser type,
 * as determined by the 'user-agent' HTTP header
 * 
 * The list of expressions contains Map properties, where the key is the name of the BrowserType.
 * Each string from the property attribute 'deviceTypes' (a comma-delimited list) is compared to the
 * BrowserType if it exists in the map--the value is returned if it matches.
 * A string property in the list of expressions will be treated as a default property and returned.
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/MobileCommerce/version/10.2/server/MobileCommerce/src/atg/projects/store/dp/DeviceTypeDerivation.java#2 $$Change: 796710 $
 * @updated $DateTime: 2013/03/14 04:12:49 $$Author: abakinou $
 */
public class DeviceTypeDerivation extends DerivationMethodImpl {
  
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/MobileCommerce/version/10.2/server/MobileCommerce/src/atg/projects/store/dp/DeviceTypeDerivation.java#2 $$Change: 796710 $";
  
  protected static final String DERIVATION_NAME = "deviceTypeDerivation";
  protected static final String DISPLAY_NAME = "Derive device type (user-agent)";
  protected static final String SHORT_DESCRIPTION = "Derive a property based on the HTTP user-agent";
  
  // name of the repository property attribute containing list of device types
  private static final String DEVICE_TYPES_ATTRIBUTE = "deviceTypes";
  
  //static initializer
  static {
    Derivation.registerDerivationMethod(DERIVATION_NAME,
        DeviceTypeDerivation.class);
  }
  
  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------
  
  /**
   * Set the name, display name and short description properties.
   */
  public DeviceTypeDerivation() {
    setName(DERIVATION_NAME);
    setDisplayName(DISPLAY_NAME);
    setShortDescription(SHORT_DESCRIPTION);
  }
  
  private String[] mDeviceTypes = null;
  
  /**
   * Retrieve the device types from the repository property attribute 'deviceTypes'
   * @return
   */
  public String[] getDeviceTypes() {
    if(mDeviceTypes == null) {
      RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
      mDeviceTypes = ((String) pd.getValue(DEVICE_TYPES_ATTRIBUTE)).split(",");
    }
    return mDeviceTypes;
  }
  
  /**
   * Return the derived property value based on browser type
   */
  @Override
  public Object derivePropertyValue(RepositoryItemImpl pItem)
      throws RepositoryException {
    List exps = getDerivation().getExpressionList();
    if (exps == null || exps.size() == 0)
      return null;
        
    for (Object exp : exps) {
      PropertyExpression pe = (PropertyExpression) exp;
      Object value = pe.evaluate(pItem);
      if (value instanceof Map && ((Map) value).size() > 0) {
        //For the case where we don't have a current request
        if(ServletUtil.getCurrentRequest() != null){
          Map imageMap = (Map) value;
          for (String deviceType : getDeviceTypes()) {
            try{
              if (imageMap.containsKey(deviceType) && ServletUtil.getCurrentRequest().isBrowserType(deviceType))
                return imageMap.get(deviceType);
            } catch(NoSuchElementException exception){
              //This is the case where we can't find the device typer
              //and break to the outer loop
              break;
            }
          }
        }
      }
      else if (value instanceof String) {
        // return the 'default' value
        return value;
      }
    }
    // no match found, no default value specified
    return null;
  }
  
  /**
   * We need to override the validate method so that it accepts Maps
   */
  @Override
  public List validate() {
    return new LinkedList();
  }

  /**
   * No support for deriving the property value from a bean.
   * Always returns null (throwing an exception breaks the ACC)
   */
  @Override
  public Object derivePropertyValue(Object pBean) throws RepositoryException {
    return null;
  }

  /**
   * Don't need to support queries, always returns false
   */
  @Override
  public boolean isQueryable() {
    return false;
  }

  /**
   * Queries not supported, throws UnsupportedFeatureException
   */
  @Override
  protected Query createQuery(int pQueryType, boolean pDerivedPropertyOnLeft,
      boolean pCountDerivedProperty, QueryExpression pOther, int pOperator,
      boolean pIgnoreCase, QueryExpression pMinScore,
      QueryExpression pSearchStringFormat, Query pItemQuery,
      QueryBuilder pBuilder, PropertyQueryExpression pParentProperty,
      List pChildPropertyList) throws RepositoryException {
    throw new UnsupportedFeatureException();
  }

}
