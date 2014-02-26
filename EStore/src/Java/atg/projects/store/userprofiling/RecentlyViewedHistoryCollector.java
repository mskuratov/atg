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


package atg.projects.store.userprofiling;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import atg.repository.ItemDescriptorImpl;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.ServletUtil;
import atg.dms.patchbay.MessageSink;
import atg.nucleus.GenericService;
import atg.userprofiling.dms.ViewItemMessage;
import atg.projects.store.profile.StoreProfileTools;
import atg.projects.store.profile.StorePropertyManager;
import atg.projects.store.profile.recentlyviewed.RecentlyViewedTools;

/**
 * This MessageSink component listens for JMS 'product' ViewItem messages. It will
 * receive all product information from the ProductBrowsed droplet which fires the 
 * JMS messages.
 * 
 * When the product information is extracted from a JMS message, processing is
 * delegated to the RecentlyViewedTools component which adds the product to the
 * recently viewed list.
 * 
 * @author David Stewart
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/userprofiling/RecentlyViewedHistoryCollector.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class RecentlyViewedHistoryCollector extends GenericService implements MessageSink {
  
  //----------------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------------
  
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/userprofiling/RecentlyViewedHistoryCollector.java#2 $$Change: 768606 $";  


  //----------------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------------

  /** 
   * We store the item descriptor here so we can do an 'instanceof' check of the
   * items viewed to see if they are 'products'. 
   */ 
  private ItemDescriptorImpl mProductItemDescriptor = null;
  
  //--------------------------------------------------------
  // property: recentlyViewedTools
  //--------------------------------------------------------
  protected RecentlyViewedTools mRecentlyViewedTools = null;

  /**
   * @return the ProfileTools
   */
  public RecentlyViewedTools getRecentlyViewedTools() {
    return mRecentlyViewedTools;
  }

  /**
   * @param pProfileTools the ProfileTools to set
   */
  public void setRecentlyViewedTools(RecentlyViewedTools pRecentlyViewedTools) {
    mRecentlyViewedTools = pRecentlyViewedTools;
  }
  
  //----------------------------
  // property: MessageType
  //----------------------------
  protected String mMessageType = "atg.dps.ViewItem";

  /**
   * @return The JMS message type
   */
  public String getMessageType() {
    return mMessageType;
  }

  /**
   * @param pMessageType The new JMS message type
   */
  public void setMessageType(String pMessageType) {
    mMessageType = pMessageType;
  }

  //-------------------------------------
  // property: catalogRepository
  //-------------------------------------
  protected Repository mCatalogRepository = null;

  /**
   * @return The catalogRepository.
   */
  public Repository getCatalogRepository() {
    return mCatalogRepository;
  }

  /**
   * @param The catalogRepository.
   */
  public void setCatalogRepository(Repository pCatalogRepository) {
    mCatalogRepository = pCatalogRepository;
  }
  
  //-----------------------------------------------
  // property: profileTools
  //-----------------------------------------------
  protected StoreProfileTools mProfileTools = null;

  /**
   * @return the ProfileTools
   */
  public StoreProfileTools getProfileTools() {
    return mProfileTools;
  }
  /**
   * @param pProfileTools the ProfileTools to set
   */
  public void setProfileTools(StoreProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }
  
  
  //----------------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------------
  /**
   * Receives JMS message and processes it if the message type in the 
   * message is equal to <code>mMessageType</code> and the itemType in 
   * the message is equal to the 'product' item name.
   */
  public void receiveMessage(String pPortName, Message pMessage)
    throws JMSException {
    
    String productPropertyName = 
      ((StorePropertyManager) getProfileTools().getPropertyManager()).getProductPropertyName();
    
    try {
      mProductItemDescriptor = 
        (ItemDescriptorImpl) getCatalogRepository().getItemDescriptor(productPropertyName);
    } 
    catch (RepositoryException re) {
      if(isLoggingError()){
        logDebug("There was a problem getting the item descriptor for " + 
            productPropertyName + "\n" +re);
      }
    }
    catch (ClassCastException cce) {
      // Its alright if the product item descriptor is not an ItemDescriptorImpl,
      // we just won't do the 'instanceof' check.
    }
    
    String messageType = pMessage.getJMSType();
    
    if(messageType.equals(getMessageType())) {
      ObjectMessage oMessage = (ObjectMessage) pMessage;
      ViewItemMessage itemMessage = (ViewItemMessage) oMessage.getObject();
      
      if(isLoggingDebug()){
        logDebug("ViewItemMessageSink receives object = " + itemMessage);
      }
      
      if(itemMessage.getItemType().equals(productPropertyName) ||
        (mProductItemDescriptor != null && 
         mProductItemDescriptor.isInstance(itemMessage.getItem()))) {
        processMessage(itemMessage);
      }
    }
  }
  
  
  //----------------------------------------------------------------------------------
  /**
   * Process received product data item. 
   * 
   * The product repository item and siteId are extracted from the message and the current 
   * user profile is retrieved. Using these references, the recentlyViewed repository item
   * can be updated.
   * 
   * @param pItemMessage The JMS message to be processed.
   */
  public void processMessage(ViewItemMessage pItemMessage) {

    // Retrieve the product from the message.
    RepositoryItem itemToAdd = pItemMessage.getItem();
    // Retrieve the current user profile item.
    RepositoryItem currentProfile = ServletUtil.getCurrentUserProfile();
    // Retrieve the site id that the current request is coming from (from the message).
    String currentSiteId = pItemMessage.getSiteId();
    
    try {
      // Add the product to the recentlyViewed item list.
      getRecentlyViewedTools().addProduct(itemToAdd, currentProfile, currentSiteId);
    } catch (RepositoryException re) {
      if (isLoggingError()){
        logError("There was a problem adding product '" + itemToAdd.getRepositoryId() + 
            "' to profile - '" + currentProfile.getRepositoryId() + "'\n" + re);
      }
    }
  }
  
}
