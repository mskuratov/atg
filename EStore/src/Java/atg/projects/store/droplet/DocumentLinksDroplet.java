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



package atg.projects.store.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import atg.commerce.catalog.CatalogNavHistory;
import atg.multisite.Site;
import atg.multisite.SiteURLManager;
import atg.projects.store.util.DocumentLinksService;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.seo.CanonicalItemLink;
import atg.repository.seo.ItemLinkException;
import atg.repository.seo.UrlParameter;
import atg.repository.seo.UrlParameterLookup;
import atg.repository.seo.UrlTemplate;
import atg.repository.seo.UrlTemplateMapper;
import atg.repository.seo.UserResourceLookup;
import atg.service.collections.validator.CollectionObjectValidator;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;

/**
 * <p>
 * This droplet returns next canonical links:
 * 1) home - Homepage address
 * 2) up - If PDP, last item from breadcrumbs; if CDP, last but one item from breadcrumbs.
 *    Rendered if producId or categoryId is set.
 * 3) chapter - Link to top-level category (item with index=1). Rendered if producId or categoryId is set.
 * 4) section - Link to current category (last item in breadcrumbs). Rendered if producId or categoryId is set.
 * 5) prev - If productId is set, link to previous product (if any) returned by the ProductNeighboursDroplet
 * 6) next - If productId is set, link to next product (if any) returned by the ProductNeighboursDroplet
 * 7) canonical - The canonical link for product or category
 * 8) alternate - The canonical link for the same product or category, but with different locale
 * </p>
 * 
 * <p>
 * This droplet takes the following input parameters
 * <dl>
 * <dt>currentCategory</dt>
 * <dd>The currently viewed category</dd>
 * </dl>
 * <dt>currentProduct</dt>
 * <dd>The currently viewed product</dd>
 * </dl> 
 * <dt>currentSiteId</dt>
 * <dd>The id of current site</dd>
 * </dl> 
 * </p>
 *  
 * <p>
 * Output parameters:
 * <dl>
 * <dt>rel</dt><dd>link rel attribute. It can take flowing values:
 * home, up, chapter, section, prev, next, canonical, alternate</dd>  
 * <dt>href</dt><dd>Contains the link href attribute</dd> 
 * <dt>langhref</dt><dd>Rendered only for links with rel="alternate". Contains the link langhref attribute.</dd> 
 * <dt>lang</dt><dd>Rendered only for links with rel="alternate". Contains the link lang attribute.</dd> 
 * </dl>
 * </p> 
 * 
 * <p>
 * Open parameters
 * <dt>output</dt><dd>Rendered if some link was generated 
 * </dd>
 * <dt>empty</dt><dd>Rendered if  no links were generated
 * </dd> 
 * </dl>
 * </p>
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/DocumentLinksDroplet.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class DocumentLinksDroplet extends CanonicalItemLink {
  
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/DocumentLinksDroplet.java#3 $$Change: 788278 $";

  // Input parameters
  private final static String CURRENT_CATEGORY = "currentCategory";
  private final static String CURRENT_PRODUCT = "currentProduct";
  private final static String CURRENT_SITE_ID = "currentSiteId";
  
  // Output parameters
  private final static String REL = "rel";
  private final static String HREF = "href";
  private final static String LANG = "lang";
  private final static String HREFLANG = "hreflang";
  
  private final static String ITEM = "item";
  private final static String LOCALE = "locale";
  private final static String PRODUCT_REPOSITORY_NAME = "/atg/commerce/catalog/ProductCatalog";
  
  
  /**
   * property: catalogNavHistory
   */
  private CatalogNavHistory mCatalogNavHistory;
  
  /**
   * @return the catalogNavHistory
   */
  public CatalogNavHistory getCatalogNavHistory() {
    return mCatalogNavHistory;
  }

  /**
   * @param pCatalogNavHistory the catalogNavHistory to set
   */
  public void setCatalogNavHistory(CatalogNavHistory pCatalogNavHistory) {
    mCatalogNavHistory = pCatalogNavHistory;
  }

  /**
   * property: site
   */
  private Site mSite;  
  
  /**
   * @return the site
   */
  public Site getSite() {
    return mSite;
  }

  /**
   * @param pSite the site to set
   */
  public void setSite(Site pSite) {
    mSite = pSite;
  }

  /**
   * property: requestLocale
   */
  private RequestLocale mRequestLocale;

  /**
   * @param pRequestLocale - request locale.
   */
  public void setRequestLocale(RequestLocale pRequestLocale) {
    mRequestLocale = pRequestLocale;
  }

  /**
   * @return the request locale.
   **/
  public RequestLocale getRequestLocale() {
    return mRequestLocale;
  }
  
  /**
   * property: documentLinksService
   */
  DocumentLinksService mDocumentLinksService;
   
  /**
   * @return the documentLinksService
   */
  public DocumentLinksService getDocumentLinksService() {
    return mDocumentLinksService;
  }

  /**
   * @param pDocumentLinksService the documentLinksService to set
   */
  public void setDocumentLinksService(DocumentLinksService pDocumentLinksService) {
    mDocumentLinksService = pDocumentLinksService;
  }

  /**
   * property: siteURLManager
   */
  private SiteURLManager mSiteURLManager = null;

  /**
   * Gets the SiteURLManager
   * 
   * @return the siteURLManager
   */
  public SiteURLManager getSiteURLManager() {
    return mSiteURLManager;
  }

  /**
   * Sets the SiteURLManager
   * 
   * @param pSiteURLManager the siteURLManager to set
   */
  public void setSiteURLManager(SiteURLManager pSiteURLManager) {
    mSiteURLManager = pSiteURLManager;
  }
  
  /**
   * property: validators
   * Array of validators that will be applied to gfts
   */
  private CollectionObjectValidator[] mValidators;
  
  /**
   * @return the validators
   */
  public CollectionObjectValidator[] getValidators() {
    return mValidators;
  }

  /**
   * @param validators the validators to set
   */
  public void setValidators(CollectionObjectValidator[] pValidators) {
    this.mValidators = pValidators;
  }
  
  
  /**
   * This method provides the implementation of service
   * Parameters:
   * pRequest the request to be processed
   * pResponse the response object for this request
   * Throws:
   * ServletException - an application specific error occurred processing this request
   * IOException - an error occurred reading data from the request or writing data to the response.
   */
  public void service(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException { 
  
    RepositoryItem currentCategory = (RepositoryItem) pRequest.getObjectParameter(CURRENT_CATEGORY);
    RepositoryItem currentProduct = (RepositoryItem) pRequest.getObjectParameter(CURRENT_PRODUCT);
    String currentSiteId = (String) pRequest.getObjectParameter(CURRENT_SITE_ID);
    
    if (isLoggingDebug()) {
      logDebug("In DocumentLinksDroplet: ");
      logDebug("currentCategory =  " + currentCategory);
      logDebug("currentProduct =  " + currentProduct);
      logDebug("currentSiteId =  " + currentSiteId);
    }
    
    /*
     *  Map will contain links rel attribute as key and object to be used to
     *  generate the canonical link as value 
     */    
    Map links = new HashMap();    
    
    links = getDocumentLinksService().getBreadcrumbsLinks(currentCategory, currentProduct, getCatalogNavHistory());
   
    Map otherLinks = getDocumentLinksService().getOtherLinks(currentCategory, currentProduct, pRequest, getRequestLocale(), getValidators());
    if (otherLinks != null && otherLinks.size() > 0) {
      links.putAll(otherLinks);
    }
    
    boolean linksSet = false;
    Set<String> linksKeys = links.keySet();
    if (links != null) {      
      for (String linksKey: linksKeys) {
        
        if (!getDocumentLinksService().ALTERNATE.equals(linksKey)) {
        
          Object navItem = links.get(linksKey);
          String url = getCanonicalLink(navItem, pRequest);
          url = getSiteURLManager().getProductionSiteBaseURL(pRequest, currentSiteId,
            url, null, null, false);
          if (url != null) {
            pRequest.setParameter(REL, linksKey);
            pRequest.setParameter(HREF, url);
            pRequest.serviceLocalParameter(S_OUTPUT, pRequest, pResponse);
            linksSet = true;                  
            
            if (isLoggingDebug()) {
              logDebug("In DocumentLinksDroplet output parameter is rendered: ");
              logDebug("rel =  " + linksKey);
              logDebug("href =  " + url);
            }
          }
        }
        /*
         * If alternate link locale parameter of request should be set.
         * Also hreflang and lang outut parameters will be set.
         */
        else {
          Object navItem = links.get(getDocumentLinksService().CANONICAL);
          Locale locale = (Locale) links.get(linksKey);
                  
          pRequest.setParameter(LOCALE, locale);
          
          String url = getCanonicalLink(navItem, pRequest);
          url = getSiteURLManager().getProductionSiteBaseURL(pRequest, currentSiteId,
            url, null, null, false);
          pRequest.setParameter(LOCALE, "");
          if (url != null) {
            pRequest.setParameter(REL, linksKey);
            pRequest.setParameter(HREF, url);
            pRequest.setParameter(HREFLANG, locale.getLanguage());
            pRequest.setParameter(LANG, locale.getLanguage());
            pRequest.serviceLocalParameter(S_OUTPUT, pRequest, pResponse);      
            linksSet = true;
            
            if (isLoggingDebug()) {
              logDebug("In DocumentLinksDroplet output parameter is rendered: ");
              logDebug("rel =  " + linksKey);
              logDebug("href =  " + url);
              logDebug("langhref =  " + locale.getLanguage());
              logDebug("lang =  " + locale.getLanguage());
            }
          }          
        }        
      }
    }
    // If no link was set render empty parameter
    if (!linksSet) {
      pRequest.serviceLocalParameter(S_EMPTY, pRequest, pResponse);    
    }
  }


  /**
   * This method gets the canonical link for pItem
   * @param pItem the item for which the canonical link will be generated
   * @param pRequest the request. Is used to allow using methods of ItemLink 
   * @return the canonical link for pItem or null if the were no template for url found
   * or some errors occurred 
   */
  private String getCanonicalLink(Object pItem,
      DynamoHttpServletRequest pRequest) {

    pRequest.setParameter(ITEM, pItem);
    
    RepositoryItem item = (RepositoryItem)pItem;
    String itemDescriptorParam = null;
    try {
      itemDescriptorParam = item.getItemDescriptor().getItemDescriptorName();
    } catch (RepositoryException e) {
      if (isLoggingError()) {
        logError(e);
      }
    }
    // Set parameters, as methods in ItemLink takes values from request parameters
    pRequest.setParameter(S_ITEM_DESCRIPTOR_NAME.toString(), itemDescriptorParam);
    pRequest.setParameter(S_REPOSITORY_NAME.toString(), PRODUCT_REPOSITORY_NAME);

    UrlParameterLookup itemLookup = null;
    UrlParameterLookup siteLookup = null;
    String itemDescriptorName = null;
    UrlTemplate template = null;
    UrlParameter[] params = null;
    String url = null;
    UrlTemplateMapper templateMapper = null;

    try {
      if (isLoggingDebug()) {
        logDebug("Entered getCanonicalLink method of DocumentLinksDroplet");
        logDebug("Request canonical URL for " + pItem);
      }

      // Check whether the item or the id was passed to us and create a lookup
      // item
      itemLookup = getItemLookup(pRequest);
      siteLookup = getSiteLookup(pRequest);

      if (isLoggingDebug()) {
        logDebug("Item lookup=" + itemLookup);
        logDebug("Site lookup=" + siteLookup);
      }

      // Get item descriptor name.
      itemDescriptorName = getItemDescriptorName(pRequest, itemLookup);

      if (itemDescriptorName == null) {
        throw new ItemLinkException(UserResourceLookup
            .getResource(UserResourceLookup.S_ERROR_NO_ITEM_DESCRIPTOR));
      }

      if (isLoggingDebug()) {
        logDebug("itemDescriptorName=" + itemDescriptorName);
      }

      // Get template mapper based on item descriptor name
      templateMapper = getTemplateMapper(pRequest, itemDescriptorName,
          itemLookup);

      if (templateMapper == null) {
        throw new ItemLinkException(UserResourceLookup.getResource(
            UserResourceLookup.S_ERROR_NO_TEMPLATE_MAPPER,
            new String[] { itemDescriptorName }));
      }

      if (isLoggingDebug()) {
        logDebug("templateMapper=" + templateMapper);
      }

      // Get template based on browser type
      template = getTemplate(pRequest, templateMapper);

      if (template != null) {
        // We have a template so get the Url
        if (isLoggingDebug()) {
          logDebug("template path is <" + template.getAbsoluteName() + ">");
          logDebug("template=" + template.getUrlTemplateFormat());
        }

        params = template.cloneUrlParameters();
        populateParams(pRequest, params, new UrlParameterLookup[] { itemLookup,
            siteLookup });
        url = template.formatUrl(params, getDefaultWebApp());

        if (isLoggingDebug()) {
          logDebug("url = " + url);
        }
      }
    } catch (ItemLinkException ure) {
      if (isLoggingError()) {
        logError(ure);
      }
    } catch (Throwable t) {
      if (isLoggingError()) {
        logError(t);
      }
    }

    if (isLoggingDebug()) {
      logDebug("Leaving getCanonicalLink method of DocumentLinksDroplet");
    }
    return url;
  }



}
