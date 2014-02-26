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



package atg.projects.store.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.commerce.catalog.CatalogNavHistory;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;
import atg.service.collections.validator.CollectionObjectValidator;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.RequestLocale;

/**
 * This class contains different methods that are user in DocumentLinksDroplet
 *  
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/util/DocumentLinksService.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */ 
public class DocumentLinksService extends GenericService {  
  
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/util/DocumentLinksService.java#3 $$Change: 788278 $";

  private static final String INTERNATIONAL_STORE_MODULE_PATH = "/atg/modules/InternationalStore";

  // Links rel attributes values
  public final static String HOME = "home";
  public final static String UP = "up";
  public final static String CHAPTER = "chapter";
  public final static String SECTION = "section";
  public final static String PREV = "prev";
  public final static String NEXT = "next";
  public final static String ALTERNATE = "alternate";
  public final static String CANONICAL = "canonical"; 
  
  private final static String RELATED_PRODUCTS = "relatedProducts";
  private final static String CHILD_PRODUCTS = "childProducts";  
  private final static String LANGUAGES = "languages"; 
  private final static String DEFAULT_COUNTRY = "defaultCountry";
 
  /**
   * Return join of childProducts property and relatedProducts property for pCategory
   * @param pCategory the category
   * @return join of childProducts property and relatedProducts property
   */  
  public List getProductsList(RepositoryItem pCategory) {
    List products = null;    
    if (pCategory != null) {
      List relatedProducts = (List) pCategory.getPropertyValue(RELATED_PRODUCTS);
      List childProducts = (List) pCategory.getPropertyValue(CHILD_PRODUCTS);
      
      if (relatedProducts!= null || childProducts != null) {
        if (childProducts != null && childProducts.size() > 0) {
          products = childProducts;          
        };            
        if (relatedProducts != null && relatedProducts.size() > 0) {
          if (products != null) {
            Collections.addAll(products, relatedProducts);
          }
          else {
            products = relatedProducts;     
          }          
        }
      }
    }    
    return products;
  }
  
  /**
   * Gets objects for canonical, alternate, prev and next objects
   * @param pCurrentCategory the current category
   * @param pCurrentProduct the current product
   * @param pRequest the request
   * @param pRequestLocale 
   * @param validators validator to valid product and categories start\end dates
   * @return the map of link rel attribute as key and object to generate canonical
   * link as value
   */
  public Map getOtherLinks(RepositoryItem pCurrentCategory,
    RepositoryItem pCurrentProduct, DynamoHttpServletRequest pRequest,
    RequestLocale pRequestLocale, CollectionObjectValidator[] validators) {
       
    Map links = new HashMap();   

    // Check if International module is Used. Otherwise it will be no other languages 
    // and no alternate links.    
    Object obj = pRequest.resolveName(INTERNATIONAL_STORE_MODULE_PATH, false);
    if (obj != null) {
      Site currentSite = SiteContextManager.getCurrentSiteContext().getSite();
      List languages = (List) currentSite.getPropertyValue(LANGUAGES);
      String countryCode = (String) currentSite.getPropertyValue(DEFAULT_COUNTRY);
      RequestLocale requestLocale = pRequestLocale;
      Locale currentLocale = requestLocale.discernRequestLocale(pRequest, requestLocale);      
      
      if (languages != null && (pCurrentCategory != null || pCurrentProduct != null)) {  
        
        //  Iterating through available locales and if it isn't equals to
        //  current locale put object for alternate link in the map
        for (Object language: languages) {        
          if ((currentLocale != null) && (currentLocale.getLanguage() != null) &&
              !currentLocale.getLanguage().equalsIgnoreCase((String)language)) {          
            Locale locale = new Locale((String)language, countryCode);
            links.put(ALTERNATE, locale);
          }      
        }    
      }    
       
      //  If pCurrentCategory is set and pCurrentProduct isn't, put 
      //  pCurrentCategory in the map for canonical link
      if (pCurrentCategory != null && pCurrentProduct == null) {
        links.put(CANONICAL, pCurrentCategory);      
      }
       
      // If pCurrentProduct is set, put 
      // pCurrentCategory in the map for canonical link 
      else if (pCurrentProduct != null) {
        links.put(CANONICAL, pCurrentProduct);
      }
      
      List products = getProductsList(pCurrentCategory);
      
      RepositoryItem prevProduct = null;
      RepositoryItem nextProduct = null;        
      if (products != null && pCurrentProduct != null) {
                  
        String currentProductId = pCurrentProduct.getRepositoryId();
        int amount = products.size();      

        // Iterating through list of products to find the current one and
        // set next and previous products 
        for (int i = 0; i < amount; i++) {
          if (((RepositoryItem)products.get(i)).getRepositoryId().equals(currentProductId)) {
          
            // Set previous product if exist
            if (i != 0) {                        
              // If validator is set, start\end date of the product will be checked
              if (validators != null) {
                RepositoryItem possiblePrevProduct = null; 
                for (int j = i-1; j >= 0; j--) {
                  possiblePrevProduct = (RepositoryItem)products.get(j);   
                
                  if (validateProduct(possiblePrevProduct, validators)) {
                    prevProduct = possiblePrevProduct;
                    break;
                  }                
                }
              }              
              else {
                prevProduct = (RepositoryItem)products.get(i - 1);
              }
            }
            
            // Set next product if exist
            if (i != amount - 1) {
              nextProduct = (RepositoryItem)products.get(i + 1);              
               // If validator is set, start\end date of the product will be checked
              if (validators != null) {
                RepositoryItem possibleNextProduct = null; 
                for (int j = i + 1; j < amount; j++) {
                  possibleNextProduct = (RepositoryItem)products.get(j);   
                
                  if (validateProduct(possibleNextProduct, validators)) {
                    nextProduct = possibleNextProduct;
                    break;
                  }                
                }
              }
              else{
                nextProduct = (RepositoryItem)products.get(i + 1);
              }
            }
            // We find product we need. Don't need to iterate any more.  
            break;
          }       
        }
      }    
      // If prevProduct is set, put prevProduct in the map for prev link   
      if (prevProduct != null) {
        links.put(PREV, prevProduct);
      }
      
      // If nextProduct is set, put nextProduct in the map for next link
      if (nextProduct != null) {
        links.put(NEXT, nextProduct);
      }
    }
    return links;
  }
  
  
  /**
   * Validate product using passed in array of validators.
   * @param item the item to validate
   * @param validators the validators that should be applied to product
   * @return false - if products fails validation, true if there should 
   * be no validation or if the product is valid 
   */
  private boolean validateProduct(RepositoryItem product, CollectionObjectValidator[] validators) {
    
    // There is  no validators set, so no filtering is needed.
    if (validators == null || validators.length == 0) {
      return true;
    }
    
    boolean isValid = true;
    for (CollectionObjectValidator validator: validators) {
      if (!validator.validateObject(product)) {
        
        if (isLoggingDebug()) {
          logDebug("Product: " + product.getRepositoryId() + " doesn't pass validator:" + validator);
        }

        //  Product doesn't pass validation. Set isValid to false
        //  and leave the loop as there is no need to check all
        //  others validators.   
        isValid = false;
        break;
        
      }
    }
    return isValid;
  }
    
  /**
   * Get objects for home, up, chapter and section links from navigation history
   * @param pCurrentCategory the current category
   * @param pCurrentProduct the current product
   * @param pCatalogNavHistory 
   * @return the map of link rel attribute as key and object to generate canonical
   * link as value
   */
  public Map getBreadcrumbsLinks(RepositoryItem pCurrentCategory, RepositoryItem pCurrentProduct,
      CatalogNavHistory pCatalogNavHistory) {
    Map links = new HashMap();    
    List navHistory = pCatalogNavHistory.getNavHistory();
    if (navHistory != null) {
      int size = navHistory.size();
      
      if (size > 0) {
        // The first item is the catalog root category. Will be used to render home link
        Object home = navHistory.get(0);
        links.put(HOME, home);
        
        
        if (size > 1 && (pCurrentCategory != null || pCurrentProduct != null)) {
          // The second item will be used to render chapter link 
          Object chapter = navHistory.get(1);
          links.put(CHAPTER, chapter);

          // The last item will be used to render section link. 
          //  It also will be used to render up link if productId is set
          Object section = navHistory.get(size - 1);
          links.put(SECTION, section);
          if (pCurrentProduct != null) {
            links.put(UP, section);
          }
        }
                  
        // The last but one item will be used to render up link if productId isn't set
        if (size - 2 >= 0 && pCurrentProduct == null && pCurrentCategory != null) {
          Object section = navHistory.get(size - 2);
          links.put(UP, section);
        }
      }
    }    
    return links;
  }  

}
