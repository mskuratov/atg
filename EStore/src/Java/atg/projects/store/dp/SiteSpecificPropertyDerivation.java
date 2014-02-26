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

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import atg.commerce.dp.Constants;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
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

/**
 * This derived property method will derive a property based on the current site.
 * 
 * For example:<br>
 * <pre>
 *   &lt;property name="feature"&gt;
 *     &lt;derivation user-method="atg.projects.store.dp.SiteSpecificPropertyDerivation"&gt;
 *       &lt;expression&gt;myFeature&lt;/expression&gt;
 *       &lt;expression&gt;siteSpecificFeature&lt;/expression&gt;
 *     &lt;/derivation&gt;
 *   &lt;/property&gt;
 *
 * where 'myFeature' is a property holding a single 'promotionalContent' 
 * repository item and 'siteSpecificFeature' is a Map property holding site specific 
 * 'promotionalContent' items, i.e. <code>site1=promo1,site2=promo2</code> etc.
 * 
 * Note that the site specific expression should always be defined after the single value expression.
 * 
 * </pre>
 * 
 * @author David Stewart
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/dp/SiteSpecificPropertyDerivation.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 **/
public class SiteSpecificPropertyDerivation extends DerivationMethodImpl {

  //----------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION =
  "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/dp/SiteSpecificPropertyDerivation.java#2 $$Change: 768606 $";

  //-----------------------------------------------------------------
  // Constants
  //-----------------------------------------------------------------
  
  protected static final String DERIVATION_NAME = "siteSpecificPropertyDerivation";
  protected static final String DISPLAY_NAME = "Derive the property value by site";
  protected static final String SHORT_DESCRIPTION = "Get the value of the property, derived by site";
  protected static final String KEY_SERVICE_PATH = "/atg/userprofiling/LocaleService";

  //static initializer
  static {
    Derivation.registerDerivationMethod(DERIVATION_NAME, SiteSpecificPropertyDerivation.class);
  }
  
  //-----------------------------------------------------------------
  // Constructors
  //-----------------------------------------------------------------
  
  //-----------------------------------------------------------------
  /**
   * Set the name, display name and short description properties.
   */
  public SiteSpecificPropertyDerivation() {
    setName(DERIVATION_NAME);
    setDisplayName(DISPLAY_NAME);
    setShortDescription(SHORT_DESCRIPTION);
  }

  //-----------------------------------------------------------------
  // Properties
  //-----------------------------------------------------------------

  //-----------------------------------------
  // property: Logger
  private static ApplicationLogging mLogger =
    ClassLoggingFactory.getFactory().getLoggerForClass(SiteSpecificPropertyDerivation.class);

  //-----------------------------------------------------------------
  /**
   * @return ApplicationLogging object for logger.
   */
  private ApplicationLogging getLogger()  {
    return mLogger;
  }
  
  //-----------------------------------------------------------------
  // Methods 
  //-----------------------------------------------------------------
  
  //-----------------------------------------------------------------  
  /**
   * We do not support query for this derivation implementation
   */
  protected Query createQuery(int pQueryType, boolean pDerivedPropertyOnLeft,
      boolean pCountDerivedProperty, QueryExpression pOther, int pOperator,
      boolean pIgnoreCase, QueryExpression pMinScore,
      QueryExpression pSearchStringFormat, Query pItemQuery,
      QueryBuilder pBuilder, PropertyQueryExpression pParentProperty,
      List pChildPropertyList) throws RepositoryException {

    throw new UnsupportedFeatureException();
  }
  
  //-----------------------------------------------------------------  
  /**
   * Determine the derived property value.
   *
   * @param pItem The item whose property value is desired.
   * @return The derived value or null.
   * @exception RepositoryException if there is a problem deriving the value
   **/
  public Object derivePropertyValue(RepositoryItemImpl pItem) throws RepositoryException {
      return derivePropertyValue((Object)pItem);
  }

  //-----------------------------------------------------------------
  /**
   * Determine the derived property value using the specified bean.
   *
   * @param pBean the bean representing a repository item whose property
   * value is desired
   * @return the derived value
   * @exception RepositoryException if there is a problem deriving the value
   **/
  public Object derivePropertyValue(Object pBean) throws RepositoryException {
    Object defaultValue = null;
    
    Iterator i = getDerivation().getDepthFirstExpressionList().iterator();
    while (i.hasNext()) {
      PropertyExpression pe = (PropertyExpression)i.next();
      Object value = pe.evaluate(pBean);
      if (value instanceof Map) {
        Map siteSpecificValues = (Map)value;
        if (siteSpecificValues.isEmpty()) {
          continue;
        }
        // The current site id will be used to determine a site specific property value.
        String currentSiteId = SiteContextManager.getCurrentSiteId();
        if (currentSiteId != null) {
          Object obj = siteSpecificValues.get(currentSiteId);         
          if (obj != null) {
            return obj;
          }
        }
      } else {
        defaultValue = value;
      }
    }
    
    return defaultValue;
  }
  
  

  /**
   * Validate the derivation. <p>
   *
   * @return a non-null, possibly empty List of RepositoryExceptions if the
   * derivation is not valid. The size of the list is the number of errors
   * found.
   **/
  public List<RepositoryException> validate() {
    // The validation is similar to the base type's validation. The only
    // difference is that the expression is not the same type as the property we
    // are deriving. So we cannot use the validation procedure of base class.
    LinkedList<RepositoryException> errors = new LinkedList<RepositoryException>();

    // we must have a derived property
    RepositoryPropertyDescriptor prop = getPropertyDescriptor();
    if (prop == null) {
      errors.add(new RepositoryException(Constants.ERR_NO_DP));

      // don't bother continuing with error checking as we can't check very
      // much without knowing the property
      return errors;
    }

    // validate depth first
    List expressions = null;
    try  {
      expressions = getDerivation().getDepthFirstExpressionList();
    } catch (RepositoryException re) {
      errors.add(re);
      return errors;
    }

    if(expressions.size() == 0) {
      errors.add(new RepositoryException(Constants.EMPTY_EXPRESSION_LIST));
    }
    
    boolean readable = prop.isReadable();
    boolean queryable = prop.isQueryable();

    // check all the expressions
    for (Iterator i=expressions.iterator(); i.hasNext();) {
      // resolve the property referenced in the expression
      // so that we can get at the corresponding property descriptor
      PropertyExpression pe = (PropertyExpression)i.next();

      try {
        pe.resolve();
      } catch (RepositoryException re) {
        errors.add(re);
        continue;
      }

      RepositoryPropertyDescriptor epd = pe.getPropertyDescriptor();

      // it can't refer to the derived property
      if (pe.references(prop)) {
        errors.add(new RepositoryException(MessageFormat.format(Constants.ERR_DP_EXP_REFERENCES_DP, pe.toString())));
      }

      // if we're readable then so must the expression be
      if (readable && !epd.isReadable()) {
        errors.add(new RepositoryException(MessageFormat.format(Constants.ERR_DP_READABLE_OP_NOT, pe.toString())));
      }

      // if we're queryable then so must the expression be
      if (queryable && !epd.isQueryable()) {
        errors.add(new RepositoryException(MessageFormat.format(Constants.ERR_DP_QUERYABLE_OP_NOT, pe.toString())));
      }
    }

    // return the errors we found
    return errors;
  }

  //-----------------------------------------------------------------
  /**
   * Determine whether the specified property can be used in a query.
   *
   **/
  public boolean isQueryable() {
    return false;
  }
  
}

