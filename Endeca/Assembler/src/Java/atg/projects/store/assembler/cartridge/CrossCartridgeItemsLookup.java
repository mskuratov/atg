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
package atg.projects.store.assembler.cartridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;

import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.model.Record;

/**
 * <p>
 *   This is the subclass of <code>ContentItemTreeIterator</code> that iterates through the content item tree looking
 *   for the items displayed in different kind of cartridges. The main purpose of this class is to put
 *   all displayed items into one bucket so that other component can check it to avoid duplicates in the cartridges.
 * </p>
 * <p>
 *   The class looks for displayed items only in the cartridges with types configured in the
 *   <code>typeToPropertyNameMap</code>. The <code>typeToPropertyNameMap</code> contains mapping between cartridge
 *   type and property name where the displayed items are stored. Items are expected to be RepositoryItems or 
 *   Endeca Record objects. If detected items are RepositoryItems the repository ID is stored into the storage. 
 *   If items are Endeca Records, then the ID of item is retrieved from the property configured in the 
 *   <code>itemIdKey</code> property.
 * </p>
 * <p>
 *   The <code>ContentItemTreeIterator</code> also performs caching of content items with IDs configured in the
 *   <code>contentItemsToCache</code> list. The cached content items can be used in cases where displayed items are
 *   not part of content item but retrieved in the renderer code.
 * </p>
 * 
 * @author Natallia Paulouskaya
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/CrossCartridgeItemsLookup.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class CrossCartridgeItemsLookup extends ContentItemTreeIterator {
      
  //-------------------------------------------
  /** Class version string. */
  protected static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/CrossCartridgeItemsLookup.java#3 $$Change: 788278 $";
  
  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------
  
  //-----------------------------------------------------
  // property: displayedItems
  //-----------------------------------------------------
  List<String> mDisplayedItems = new ArrayList<String>();

  /**
   * @return The list of displayed items.
   */
   public List<String> getDisplayedItems() {
     return mDisplayedItems;
   }

  /**
   * @param pDisplayedItems - The list of displayed items to set.
   */
  public void setDisplayedItems(List<String> pDisplayedItems) {
    mDisplayedItems = pDisplayedItems;
  }
  
  //-----------------------------------------------
  // property: typeToPropertyNameMap
  //-----------------------------------------------
  Map<String,String> mTypeToPropertyNameMap = null;
  
  /**
   * @return the mapping between cartridge types and property names where the items we are looking for stored.
   */
  public Map<String, String> getTypeToPropertyNameMap() {
    return mTypeToPropertyNameMap;
  }

  /**
   * @param pTypeToPropertyNameMap - The mapping between cartridge types and property names where the items we 
   *                                 are looking for stored.
   */
  public void setTypeToPropertyNameMap(Map<String, String> pTypeToPropertyNameMap) {
    mTypeToPropertyNameMap = pTypeToPropertyNameMap;
  }
  
  //---------------------------------------
  // property: contentItemsToCache
  //---------------------------------------
  List<String> mContentItemsToCache = null;
  
  /**
   * @return the array of content item types which should be cached during content item tree iteration.
   */
  public List<String> getContentItemsToCache() {
    return mContentItemsToCache;
  }

  /**
   * @param pContentItemsToCache - The array of content item types which should be cached during content 
   *                               item tree iteration.
   */
  public void setContentItemsToCache(List<String> pContentItemsToCache) {
    mContentItemsToCache = pContentItemsToCache;
  }
  
  //------------------------------------------------
  // property: contentItemsCache
  //------------------------------------------------
  Map<String,List<ContentItem>> mContentItemsCache = new HashMap<String, List<ContentItem>>();
  
  /**
   * @return the mapping between cartridge types and list of content items assembled for this request.
   */
  public  Map<String,List<ContentItem>> getContentItemsCache() {
    return mContentItemsCache;
  }

  /**
   * @param pContentItemsCache - The mapping between cartridge types and list of content items 
   *                             assembled for this request.
   */
  public void setContentItemsCache( Map<String,List<ContentItem>> pContentItemsCache) {
    mContentItemsCache = pContentItemsCache;
  }
  
  //-----------------------
  // property: itemIdKey
  //-----------------------
  String mItemIdKey = null;  

  /**
   * @return the content item key where the item's ID stored.
   */
  public String getItemIdKey() {
    return mItemIdKey;
  }

  /**
   * @param pItemIdKey - The content item key where the item's ID stored.
   */
  public void setItemIdKey(String pItemIdKey) {
    mItemIdKey = pItemIdKey;
  }

  //----------------------------------------------------------------------------
  //  METHODS
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  /**
   * <p>
   *   The implementation of content item process method. This method checks 
   *   whether the passed in content item has one of the types configured in 
   *   <code>typeToPropertyNameMap</code> and if so, retrieves items from the 
   *   property mapped to the corresponding cartridge type. 
   * </p>
   * <p>  
   *   Item IDs are stored in the <code>displayedItems</code> list. If the retrieved 
   *   items are RepositoryItems, the ID is taken from the repositoryId property. In 
   *   the case when the retrieved items are Endeca records, the IDs are taken from 
   *   the property configured in the <code>itemIdKey</code>.
   * </p>
   * 
   * @param pContentItem The configuration item.
   */
  @Override
  public void process(ContentItem pContentItem) {
    
    // The type of current content item.
    String type = pContentItem.getType();
    
    // Check whether we need to cache this content item.
    if (getContentItemsToCache() != null && getContentItemsToCache().contains(type)) {
      
      // Cache content item.        
      List<ContentItem> contentItemList = getContentItemsCache().get(type);
        
      if (contentItemList == null){
        contentItemList = new ArrayList<ContentItem>();
        getContentItemsCache().put(type, contentItemList);
      }
        
      contentItemList.add(pContentItem);
    }
    
    // Check whether the content item is of configured type. If not just skip processing.
    if (getTypeToPropertyNameMap() != null && getTypeToPropertyNameMap().containsKey(type)){
      
      // The content item type is in the configure typeToPropertyNameMap mapping.
      // Retrieve items displayed by the cartridge from the configured property.
      String propertyName = getTypeToPropertyNameMap().get(type);
      
      if (!StringUtils.isEmpty(propertyName)) {
        Object items = pContentItem.get(propertyName);

        if (items != null) {
          if (items instanceof String) {
            getDisplayedItems().add((String)items);
            return;
          }
          if (items instanceof RepositoryItem) {
            getDisplayedItems().add(((RepositoryItem)items).getRepositoryId());
            return;
          }
          if (items instanceof Record) {
            getDisplayedItems().add(((Record)items).getAttributes().get(getItemIdKey()).toString());
            return;
          }
          if (items instanceof List) {
            for(Object item : (List<?>)items) {
              if (item instanceof String) {
                getDisplayedItems().add((String)item);
                continue;
              }
              if (item instanceof RepositoryItem) {
                getDisplayedItems().add(((RepositoryItem)item).getRepositoryId());
                continue;
              }
              if (item instanceof Record) {
                getDisplayedItems().add(((Record)item).getAttributes().get(getItemIdKey()).toString());
                continue;
              }
            }
          }
        }
      }
    }
  }

}
