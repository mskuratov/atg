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

package atg.projects.store.assembler.cartridge.handler;

import java.util.ArrayList;
import java.util.List;

import atg.core.util.StringUtils;
import atg.endeca.assembler.AssemblerTools;
import atg.naming.NameResolver;
import atg.projects.store.assembler.cartridge.TargetedItemsContentItem;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.service.collections.validator.CollectionObjectValidator;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.targeting.Targeter;
import atg.targeting.TargetingSourceMap;

import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;

/**
 * <p>
 *   TargetedItemsHandler adds a configurable prefix to the cartridge targeter component path if needed,
 *   resolves the targeter component, targets for a set number of items and adds them into TargeterItems.
 * </p>
 * <p>
 *   Properties:
 *   <ul>
 *     <li>
 *       sourceMap - Targeting source map to be used during targeting. Represents a mapping 
 *                   between source names used in a targeting operation and their Nucleus paths. 
 *                   sourceMap is used to obtain a NameResolver object and passes it to the Targeter, 
 *                   which uses the NameResolver to resolve the names of source objects associated with 
 *                   a targeting operation. 
 *     </li>
 *     <li>
 *       itemDescriptor - Descriptor of item that should be returned be this targeter.
 *     </li>
 *     <li>   
 *       pathPrefix - Prefix for targeter components.
 *     </li>
 *   </ul>
 * </p>
 * 
 * @author Yekaterina Kostenevich
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/handler/TargetedItemsHandler.java#4 $$Change: 791340 $
 * @updated $DateTime: 2013/02/19 09:03:40 $$Author: abakinou $
 */
public class TargetedItemsHandler extends NavigationCartridgeHandler<ContentItem, TargetedItemsContentItem> {
  
  //----------------------------------------
  /** Class version string. */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/handler/TargetedItemsHandler.java#4 $$Change: 791340 $";

  //----------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------
  
  protected final static String COMPONENT_PATH_SEPARATOR = "/";
  protected final static String COMPONENT_PATH = "componentPath";
  protected final static String ITEM_COUNT = "itemCount";  
  protected final static int DEFAULT_ITEMS_AMOUNT = 0; 
    
  //----------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------
  
  //--------------------------------
  // property: pathPrefix
  //--------------------------------
  private String mPathPrefix = null;

  /**
   * @return the pathPrefix.
   */
  public String getPathPrefix() {
    return mPathPrefix;
  }

  /**
   * @param pPathPrefix - The pathPrefix to set.
   */
  public void setPathPrefix(String pPathPrefix) {
    mPathPrefix = pPathPrefix;
  }
  
  //------------------------------------
  // property: itemDescriptor
  //------------------------------------
  private String mItemDescriptor = null;

  /**
   * @return the item descriptor of item that should be used in this targeter.
   */
  public String getItemDescriptor() {
    return mItemDescriptor;
  }

  /**
   * @param pItemDescriptor - The item descriptor of item that should be used in this targeter.
   */
  public void setItemDescriptor(String pItemDescriptor) {
    mItemDescriptor = pItemDescriptor;
  }
  
  //------------------------------------
  // property: repository
  //------------------------------------
  private Repository mRepository = null;

  /**
   * @return the repository of item that should be used by this targeter.
   */
  public Repository getRepository() {
    return mRepository;
  }

  /**
   * @param pRepository the repository of item that should be returned by this targeter.
   */
  public void setRepository(Repository pRepository) {
    mRepository = pRepository;
  }
  
  //-----------------------------------------------------
  // property: validators
  //-----------------------------------------------------
  private CollectionObjectValidator[] mValidators = null;
  
  /**
   * @return array of validators that will be applied to items.
   */
  public CollectionObjectValidator[] getValidators() {
    return mValidators;
  }

  /**
   * @param pValidators - The validators to set.
   */
  public void setValidators(CollectionObjectValidator[] pValidators) {
    this.mValidators = pValidators;
  }

  //-----------------------------------
  // property sourceMap
  //-----------------------------------
  TargetingSourceMap mSourceMap = null;
  
  /**
   * @return the mSourceMap.
   */
  public TargetingSourceMap getSourceMap() {
    return mSourceMap;
  }

  /**
   * @param pSourceMap - The sourceMap to set.
   */
  public void setSourceMap(TargetingSourceMap pSourceMap) {
    mSourceMap = pSourceMap;
  }

  //----------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------
  
  //----------------------------------------------------------------------
  /**
   * Create a new BasicContentItem using the passed in ContentItem.
   * 
   * @param pContentItem - The cartridge content item to be wrapped.
   * 
   * @return a new TargetedItems configuration.
   */
  @Override
  protected ContentItem wrapConfig(ContentItem pContentitem) {
    return new BasicContentItem(pContentitem);
  }

  //----------------------------------------------------------------------
  /**
   * <p>
   *   This method builds a full targeter/slot component path. It is built using the component 
   *   path configured in Experience Manager along with the pathPrefix property. The targeter/slot 
   *   component is then resolved and the itemCount amount of items are targeted. 
   * </p>
   * <p>
   *   Items returned by slot/targeter should be of type itemDescriptor. Only items of this type will 
   *   be put in the TargetedItemsContentItem. If an item of another type is found, error will be logged. 
   *   If itemCount is not set, then all items returned by slot or targeter of type itemDescriptor will
   *   be put in the TargetedItemsContentItem.
   * </p>
   * 
   * @param pCartridgeConfig - if an error occurs that is scoped to an individual cartridge 
   *                           instance. This exception will not halt the entire assembly process, 
   *                           which occurs across multiple cartridges; instead, this exception will 
   *                           be packaged in the overall response model. If an unchecked  exception 
   *                           is thrown, then the entire assembly process will be halted.
   * 
   * @return a fully configured TargetedItemsContentItem.
   * 
   * @throws CartridgeHandlerException
   */
  @Override
  public TargetedItemsContentItem process(ContentItem pCartridgeConfig) throws CartridgeHandlerException {
    
    TargetedItemsContentItem targetedItems = new TargetedItemsContentItem(pCartridgeConfig);
    DynamoHttpServletRequest  request  = ServletUtil.getCurrentRequest();
    NameResolver nameResolver = getSourceMap().getNameResolver(request);
    
    // Build full targeter component path.
    String fullcomponentPath = "";
    
    if (!StringUtils.isEmpty(getPathPrefix())) {
      fullcomponentPath = getFullPath(getPathPrefix(), (String)pCartridgeConfig.get(COMPONENT_PATH));
    }
    else {
      fullcomponentPath = (String)pCartridgeConfig.get(COMPONENT_PATH);
    }
    
    if (!StringUtils.isEmpty(fullcomponentPath)) {
      try {
        
        Targeter targeter = (Targeter) request.resolveName(fullcomponentPath);
        
        if (targeter != null){
       
          Object[] items = null;
          
          if (((BasicContentItem)pCartridgeConfig).getIntProperty(ITEM_COUNT, DEFAULT_ITEMS_AMOUNT) > 0) {
            items = 
              targeter.target(nameResolver, 
                              ((BasicContentItem)pCartridgeConfig).getIntProperty(ITEM_COUNT, DEFAULT_ITEMS_AMOUNT));
          }
          else {
            AssemblerTools.getApplicationLogging().vlogDebug("TargetedItemsHandler.process: itemCount wasn't configured.");
            items = targeter.target(nameResolver);
          }
          if (items != null && items.length > 0) {
            
            List validItems = new ArrayList();

            // Check that all items are from specified repository with specified item descriptor.
                         
            for (Object item: items) {
              if (item instanceof RepositoryItem) {
                
                Repository itemRepository = ((RepositoryItem) item).getRepository();
                
                // Check that item is from required repository.
                if (itemRepository.equals(getRepository())) {
                  
                  RepositoryItemDescriptor expectedItemDescriptor = 
                    itemRepository.getItemDescriptor(getItemDescriptor());
                  
                  // Check that item has required item descriptor and pass all configured validators.
                  if (expectedItemDescriptor.isInstance(item) && validateItem((RepositoryItem) item)) {
                    validItems.add(item);
                  }
                  else {
                    AssemblerTools.getApplicationLogging().vlogDebug(
                      "TargetedItemsHandler.process: expect item with item descriptor: {0}, but got item with item descriptor: {1} ",
                      getItemDescriptor(), ((RepositoryItem) item).getItemDisplayName());
                  }
                }
                else {
                  AssemblerTools.getApplicationLogging().vlogDebug(
                    "TargetedItemsHandler.process: expect item from repository: {0} , but got item from repository: {1} ",
                    getRepository(), itemRepository);
                }
              }
            }
            
            targetedItems.setItems(validItems);     
          }
        }
        else {
          AssemblerTools.getApplicationLogging().vlogDebug(
            "TargetedItemsHandler.process: failed to resolve component: {0}", fullcomponentPath);
        }
      }
      catch(Exception ex) {
        AssemblerTools.getApplicationLogging().vlogDebug("Error occurs in TargetedItemsHandler.process method: ", ex);
      }
    }
       
    return targetedItems;
  }

  //----------------------------------------------------------------------
  /**
   * Validate repository item using configured set of validators.
   * 
   * @param pItem - The item to validate.
   * 
   * @return false if item fails validation, true if there should be no validation or if item is valid.
   */
  public boolean validateItem(RepositoryItem pItem) {
    
    // There are no validators set, so no filtering is needed.
    if (getValidators() == null || getValidators().length == 0) {
      return true;
    }
    
    boolean isValid = true;
    
    for (CollectionObjectValidator validator: getValidators()) {
      
      if (!validator.validateObject(pItem)) {
        AssemblerTools.getApplicationLogging().vlogDebug(
          "Item: {0} doesn't pass validator: {1}", pItem.getRepositoryId(), validator);
        
        // Item doesn't pass validation. Set isValid to false and leave the loop as there is 
        // no need to check all others validators.                
        isValid = false;
        break;        
      }
    }
    
    return isValid;
  }
  
  //----------------------------------------------------------------------
  /**
   * Builds full path adding pPathPrefix to pComponentPath if needed.
   * 
   * @param pPathPrefix - The path prefix of the component.
   * @param pComponentPath - The configured component path.
   * 
   * @return the full path of the component.
   */
  private String getFullPath(String pPathPrefix, String pComponentPath) {
    
    StringBuilder sb = new StringBuilder();
    
    if (!StringUtils.isEmpty(pComponentPath)) {
      if (!pComponentPath.startsWith(pPathPrefix)) {
        
        sb.append(pPathPrefix);
        
        if (!pPathPrefix.endsWith(COMPONENT_PATH_SEPARATOR)) {
          sb.append(COMPONENT_PATH_SEPARATOR);
        }
        
        if (pComponentPath.startsWith(COMPONENT_PATH_SEPARATOR)) {
          sb.append(pComponentPath.substring(1));
        }
        else{
          sb.append(pComponentPath);
        }
      }
      else{
        return pComponentPath;
      }
    }
    
    return sb.toString();
  }

}
