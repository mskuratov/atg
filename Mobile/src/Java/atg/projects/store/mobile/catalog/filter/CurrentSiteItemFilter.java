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

package atg.projects.store.mobile.catalog.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import atg.multisite.SiteContextManager;
import atg.multisite.SiteManager;
import atg.naming.NameResolver;
import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.service.filter.ItemFilter;

/**
 * Filter the incoming array of objects, deleting one which belong to the current site
 * 
 * @author Uladzimir_Sokalau
 * 
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Mobile/src/atg/projects/store/mobile/catalog/filter/CurrentSiteItemFilter.java#2 $Change: 630322 $
 * @updated $DateTime: 2012/12/26 06:47:02 $Author: ykostene $
 */
public class CurrentSiteItemFilter extends GenericService implements ItemFilter {

  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Mobile/src/atg/projects/store/mobile/catalog/filter/CurrentSiteItemFilter.java#2 $Change: 630322 $";

  /**
   * Site ids property
   */
  public static final String SITE_IDS = "siteIds";
  
  /**
   * Enabled property for sites
   */
  public static final String ENABLED = "enabled";

  /**
   * Product item descriptor
   */
  private String mProductItemDescriptor;
  
   
  /**
   * @return the productItemDescriptor
   */
  public String getProductItemDescriptor() {
    return mProductItemDescriptor;
  }

  /**
   * @param pProductItemDescriptor the productItemDescriptor to set
   */
  public void setProductItemDescriptor(String pProductItemDescriptor) {
    mProductItemDescriptor = pProductItemDescriptor;
  }
  
  
  /** 
   * Filter pItems: leave only those items which belong to current site.
   * @param pItems array of items to filter
   * @param pResolver the resolver
   * @return filtered array of items
   */
  @Override
  public Object[] filterItems(Object[] pItems, NameResolver pResolver) {
    List<Object> resultCollection = new ArrayList<Object>();
    try {
           
      for (Object item: pItems) {
        
        RepositoryItem repItem = (RepositoryItem)item;
        RepositoryItemDescriptor itemDescriptor =  repItem.getItemDescriptor();
      
        // Check that repItem is product and we have multisite configuration
        if (getProductItemDescriptor() != null && getProductItemDescriptor().equalsIgnoreCase(itemDescriptor.getItemDescriptorName())
            && (SiteContextManager.getCurrentSiteId() != null)){       
        
          Set<String> siteIds = (Set<String>) repItem.getPropertyValue(SITE_IDS);
         
          for (String siteId: siteIds){
            RepositoryItem site = null;
            
            try{
              site = SiteManager.getSiteManager().getSite(siteId);
              // Check if site is enabled and it is current site 
              if ((Boolean)site.getPropertyValue(ENABLED) && SiteContextManager.getCurrentSiteId().equals(siteId)){
                resultCollection.add(repItem);
                /*
                 *  Already found site which is enabled and add item to result collection.
                 *  No need to iterate over other sites of this item.
                 */
                break;
              }
            }catch (Exception e) {
              if (isLoggingError()) {
                logError("There was a problem retrieving site. " + siteId, e);
              }
            }  
          }
        }
        // If  repItem is not a product and we have not multisite configuration return unchanged pItems
        else {
          return pItems;
        }
      }
    // Probably items don't contain itemDescriptor or siteIds properties. Log error and return unchanged array.  
    }catch (Exception e) {
      if (isLoggingError()) {
        logError("There was a problem during item filtering.", e);
      }
      return pItems;
    }
    
    return resultCollection.toArray();
  }

}
