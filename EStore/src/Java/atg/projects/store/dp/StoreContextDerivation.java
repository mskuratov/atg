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

import java.util.List;
import java.util.StringTokenizer;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
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
import atg.repository.dp.RepositoryKeyService;
import atg.repository.query.PropertyQueryExpression;

/**
 * This derived property method will derive a property based on the current site and 
 * profile's current locale.
 * 
 * For example:<br>
 * <pre>
 *   &lt;property name="imagePath"&gt;
 *     &lt;derivation user-method="atg.projects.store.dp.StoreContextDerivation"&gt;
 *       &lt;expression&gt;image&lt;/expression&gt;
 *     &lt;/derivation&gt;
 *   &lt;/property&gt;
 * </pre>;
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/dp/StoreContextDerivation.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 **/
public class StoreContextDerivation extends DerivationMethodImpl {

  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION =
  "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/dp/StoreContextDerivation.java#2 $$Change: 768606 $";

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  
  protected static final String DERIVATION_NAME = "storeContextDerivation";
  protected static final String DISPLAY_NAME = "Derive by the current site and locale";
  protected static final String SHORT_DESCRIPTION = "Get value mapped to by current site and locale";
  
  protected static final String KEY_SERVICE_PATH = "/atg/userprofiling/LocaleService";
  
  protected static final String SITE_TAG = "{site}";
  protected static final String LANGUAGE_TAG = "{language}";
  
  protected static final String DEFAULT_LANGUAGE_ATTR = "defaultLanguage";
  protected static final String DEFAULT_SITE_ATTR = "defaultSite";

  //static initializer
  static {
    Derivation.registerDerivationMethod(DERIVATION_NAME,
        StoreContextDerivation.class);
  }
  
  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------
  
  /**
   * Set the name, display name and short description properties.
   */
  public StoreContextDerivation() {
    setName(DERIVATION_NAME);
    setDisplayName(DISPLAY_NAME);
    setShortDescription(SHORT_DESCRIPTION);
  }
  
  //--------------------------------------------------
  // Attributes
  //--------------------------------------------------

  //--------------------------------------------------
  // attribute: defaultSite
  //--------------------------------------------------

  private String mDefaultSiteAttribute = null;
  
  /**
   * Gets the mDefaultSiteAttribute
   * @return return mDefaultSiteAttribute 
   */
  public String getDefaultSiteAttribute() {
    if(mDefaultSiteAttribute == null) {
      RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
      mDefaultSiteAttribute = (String) pd.getValue(DEFAULT_SITE_ATTR);
    }
    return mDefaultSiteAttribute;
  }

  //--------------------------------------------------
  // attribute: defaultLanguage
  //--------------------------------------------------

  private String mDefaultLanguageAttribute = null;
  
  /**
   * Gets the mDefaultLanguageAttribute
   * @return mDefaultLanguageAttribute
   */
  public String getDefaultLanguageAttribute() {
    if(mDefaultLanguageAttribute == null) {
      RepositoryPropertyDescriptor pd = getDerivation().getPropertyDescriptor();
      mDefaultLanguageAttribute = (String) pd.getValue(DEFAULT_LANGUAGE_ATTR);
    }
    return mDefaultLanguageAttribute;
  }

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //-------------------------------
  // property: Logger
  private static ApplicationLogging mLogger =
    ClassLoggingFactory.getFactory().getLoggerForClass(StoreContextDerivation.class);

  /**
   * @return ApplicationLogging object for logger.
   */
  private ApplicationLogging getLogger()  {
    return mLogger;
  }
  
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------
  
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

  /**
   * Determine the derived property value.
   *
   * @param pItem the item whose property value is desired
   * @return the derived value
   * @exception RepositoryException if there is a problem deriving the value
   **/
  public Object derivePropertyValue(RepositoryItemImpl pItem)
      throws RepositoryException {

    List exps = getDerivation().getExpressionList();
    
    // get the original property value
    String value = getValue(exps, pItem);
    
    if(!StringUtils.isEmpty(value)) {
      
      // process site 
      if(value.contains(SITE_TAG)) {
        String currentSiteId = SiteContextManager.getCurrentSiteId();
        if(!StringUtils.isEmpty(currentSiteId)) {
          value = value.replace(SITE_TAG, currentSiteId);
        } else {
          // for some reason siteId is not found, use default site          
          value = value.replace(SITE_TAG, getDefaultSiteAttribute());
        }
      }
      
      // process language
      if(value.contains(LANGUAGE_TAG)) {
        
        RepositoryKeyService keyService = getKeyService();
        
        if(keyService != null) {
          String reqLocaleStr = keyService.getRepositoryKey().toString();
          
          if (!StringUtils.isEmpty(reqLocaleStr)) {
            StringTokenizer st = new StringTokenizer(reqLocaleStr, "_");
            String language = null;
            
            if (st.hasMoreTokens()) {
              language = st.nextToken();
            }

            if (!StringUtils.isEmpty(language)) {
              value = value.replace(LANGUAGE_TAG, language);
            }
          }
        } else {
          // we still want to replace LANG_TAG with default language
          value = value.replace(LANGUAGE_TAG, getDefaultLanguageAttribute());
        }
      }
    }
    
    return value;
  }

  /**
   * Determine the derived property value using the specified bean.
   *
   * @param pBean the bean representing a repository item whose property
   * value is desired
   * @return the derived value
   * @exception RepositoryException if there is a problem deriving the value
   **/
  public Object derivePropertyValue(Object pBean) throws RepositoryException {
    return null;
  }

  /**
   * Determine whether the specified property can be used in a query.
   *
   * @return false
   **/
  public boolean isQueryable() {
    return false;
  }
  
  /**
   * Returns value for the given property expression 
   * 
   * @param pExps list of property expressions
   * @param pItem repository item
   * @return repository value
   * @throws RepositoryException indicates that a severe error occured while performing a Repository task
   */
  private String getValue(List pExps, RepositoryItemImpl pItem) throws RepositoryException {
    
    PropertyExpression pe;
    Object value = null;

    try {
      if (pExps == null || pExps.size() == 0) {
        return null;
      }
      else {
        pe = (PropertyExpression)pExps.get(0);
        value = pe.evaluate(pItem);
        return String.valueOf(value);
      }
    }
    catch (IndexOutOfBoundsException ioobe) {
      return null;
    }  
  }
  
  /**
   * Returns LocaleKeyService service.
   * 
   * @return RepositoryKeyService The service for which we will get the locale 
   * to use proper language.
   */
  public RepositoryKeyService getKeyService() {

    RepositoryKeyService repositoryKeyService = null;
    Nucleus nucleus = Nucleus.getGlobalNucleus();

    if (nucleus != null) {
      Object keyService = nucleus.resolveName(KEY_SERVICE_PATH);

      if (!(keyService instanceof RepositoryKeyService)) {
        if (getLogger().isLoggingDebug()){
          getLogger().logDebug("The RepositoryKeyService (" + KEY_SERVICE_PATH +
                             ") does not implement atg.repository.dp.RepositoryKeyService");
        }
      }
      else {
        repositoryKeyService = (RepositoryKeyService)keyService;
      }
    }
    return repositoryKeyService;
  }
}
