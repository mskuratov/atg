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

package atg.projects.store.catalog;


import java.util.ArrayList;
import java.util.List;

import atg.endeca.assembler.AssemblerTools;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.collections.validator.CollectionObjectValidator;


/**
 * Helper bean to store user catalog navigation information providing convenience methods for:
 *   the current category that user is currently viewing
 *   the ancestor categories from the current category to the top level category
 *   the full category path from and including the current category to the top level category
 *   the top level category
 * The bean is intended to be used anywhere requiring access to the current shopper catalog navigation such as 
 * breadcrumbs, continue shopping, and with targeters to specify targeting rules based on the currently viewed 
 * category. The store uses this when targeting promotional content to the shopper.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/catalog/CatalogNavigationService.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class CatalogNavigationService 
{
  public final static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/catalog/CatalogNavigationService.java#3 $$Change: 788278 $";


  /**
   * The top level category for the category that shopper is currently viewing.
   */
  protected String mTopLevelCategoryId = "";

  /**
   * Sets the top level category for the category that user is currently viewing.
   *
   * @param pTopLevelCategory the top level category for the category that user is currently viewing.
   */
  public void setTopLevelCategory( String pTopLevelCategoryId )
  {
    mTopLevelCategoryId = pTopLevelCategoryId;
  }

  /**
   * Returns the top level category for the category that user is currently viewing.
   *
   * @return the top level category for the category that user is currently viewing.
   */
  public String getTopLevelCategory()
  {
    return mTopLevelCategoryId;
  }


  /**
   * The current category that shopper is currently viewing.
   */
  protected String mCurrentCategoryId = "";

  /**
   * Sets the current category that shopper is currently viewing.
   *
   * @param pCurrentCategoryId the current category that shopper is currently viewing.
   */
  public void setCurrentCategory( String pCurrentCategoryId )
  {
    mCurrentCategoryId = pCurrentCategoryId;
  }

  /**
   * Returns the current category that user is currently viewing.
   *
   * @return the current category that user is currently viewing.
   */
  public String getCurrentCategory()
  {
    return mCurrentCategoryId;
  }


  /**
   * The ancestors of the category that shopper is currently viewing.
   */
  protected List<String> mAncestorCategoryIds = new ArrayList<String>();

  /**
   * Sets ancestors of the category that user is currently viewing.
   *
   * @param pAncestorCategoryIds the ancestors of the category that user is currently viewing.
   */
  public void setAncestorCategories( List<String> pAncestorCategoryIds )
  {
    mAncestorCategoryIds = pAncestorCategoryIds;
  }

  /**
   * Returns the ancestors of the category that user is currently viewing.
   * The first category in the list is the top level category and the last category in the
   * list is the parent category of the category that user is currently viewing.
   *
   * @return the ancestors of the category that user is currently viewing.
   */
  public List<String> getAncestorCategories()
  {
    return mAncestorCategoryIds;
  }


  /**
   * The full category path from and including the current category to the top level category.
   */
  protected List<String> mCategoryNavigationPath = new ArrayList<String>();

  /**
   * Sets the full category path from and including the current category to the top level category.
   *
   * @param pCategoryNavigationPath the full category path from and including the current category to the top level category.
   */
  public void setCategoryNavigationPath( List<String> pCategoryNavigationPath )
  {
    mCategoryNavigationPath = pCategoryNavigationPath;
  }

  /**
   * Returns full category path from and including the current category to the top level category.
   * The first category in the list is the top level category and the last category in the list
   * is the category that user is currently viewing.
   *
   * @return the full category path from and including the current category to the top level category.
   */
  public List<String> getCategoryNavigationPath()
  {
    return mCategoryNavigationPath;
  }
  
  
  /**
   * The boolean flag that indicates if current category is valid
   */
  private boolean mCurrentCategoryValid = true;
  
  /**
   * @return the isCurrentCategoryValid
   */
  public boolean isCurrentCategoryValid() {
    return mCurrentCategoryValid;
  }

  /**
   * @param puCrrentCategoryValid the currentCategoryValid to set
   */
  public void setCurrentCategoryValid(boolean pCurrentCategoryValid) {
    mCurrentCategoryValid = pCurrentCategoryValid;
  }
  
  /**
   * property: Catalog Tools
   */
  private StoreCatalogTools mCatalogTools;
 
  /**
   * @return the CatalogTools component.
   */
  public StoreCatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  /**
   * @param pCatalogTools - The CatalogTools component.
   */
  public void setCatalogTools(StoreCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }
  
  /**
   * Validators that will be applied to the category
   */
  private CollectionObjectValidator[] mValidators;

  /**
   * @return the validators
   */
  public CollectionObjectValidator[] getValidators() {
    return mValidators;
  }

  /**
   * @param pValidators the validators to set
   */
  public void setValidators(CollectionObjectValidator[] pValidators) {
    mValidators = pValidators;
  }

  /**
   * Tracks the shopper's catalog navigation setting the current category being viewed, the ancestor categories
   * of the current category, the full category path from the top level category to the current category, and the
   * top level category.
   *
   * @param pCategoryId the current category.
   * @param pAncestorCategoryIds the ancestors of the current category that user is currently viewing.
   */
  public void navigate( String pCategoryId,
                        List<String> pAncestorCategoryIds )
  {
    List<String> categoryNavigationPath = new ArrayList<String>( (pAncestorCategoryIds == null) ? new ArrayList<String>() 
                                                                                                : new ArrayList<String>(pAncestorCategoryIds) );
    setCurrentCategoryValid(validateCategory(pCategoryId));
    categoryNavigationPath.add( pCategoryId );
    setCategoryNavigationPath( categoryNavigationPath );
                                                  
    setCurrentCategory( pCategoryId );
    
    if ( pAncestorCategoryIds != null 
         && !pAncestorCategoryIds.isEmpty() ) 
    {
      setTopLevelCategory( pAncestorCategoryIds.get(0) );
      setAncestorCategories( pAncestorCategoryIds );
    }
    else
    {
      setTopLevelCategory( pCategoryId );
      setAncestorCategories( new ArrayList<String>() );
    }
  }
  
  /** 
   * This method retrieves category and applies configured validators to 
   * it. 
   * @param pCategoryId the id of category to validate
   * @return true if category is valid, false otherwise.
   */
  private boolean validateCategory(String pCategoryId) {
    // There is  no validators set, so no filtering is needed.
    if (getValidators() == null || getValidators().length == 0) {
      return true;
    }
    
    Repository catalog = getCatalogTools().getCatalog();
    StoreCatalogProperties catalogProperties = (StoreCatalogProperties) getCatalogTools().getCatalogProperties();
    // Retrive currentCategoryItem from Catalog 
    RepositoryItem currentCategoryItem = null;
    try {
      currentCategoryItem = catalog.getItem(pCategoryId, catalogProperties.getCategoryItemName());
    } catch (RepositoryException re) {
      AssemblerTools.getApplicationLogging().logError(
          "There was a problem retrieving the category from the catalog", re);
    } 
    
    boolean isValid = true;  
    if (currentCategoryItem != null) {
      
      for (CollectionObjectValidator validator: getValidators()) {
        if (!validator.validateObject(currentCategoryItem)) {
          
          // Item doesn't pass validation. Set isValid to false
          // and leave the loop as there is no need to check all
          // others validators.                
          isValid = false;
          break;
        }
      }      
    }
    
    return isValid;
  }

  /**
   * Clears the current navigation settings.
   */
  public void clear(){
    setCategoryNavigationPath(new ArrayList());
    setCurrentCategory("");
    setTopLevelCategory("");
    setAncestorCategories(new ArrayList());
  }
}
