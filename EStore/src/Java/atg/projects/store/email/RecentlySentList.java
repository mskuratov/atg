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


package atg.projects.store.email;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.core.i18n.LocaleUtils;
import atg.nucleus.GenericService;

/**
 * This service holds the list of recently sent emails. Every entry contains 
 * type of email template, list of recipients, timestamp, and template meta parameters.  
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/email/RecentlySentList.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class RecentlySentList extends GenericService {
  
  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/email/RecentlySentList.java#2 $$Change: 768606 $";

  //-------------------------------------
  // Constants
  
  public static final String SENT_DATE_FORMAT = "MM/dd/yy HH:mm:ss";
  public static final String LOCALE = "locale";
  
  //-------------------------------------
  // Properties
  
  //-------------------------------------
  // listSize
  private int mListSize = 5;
  
  /**
   * Returns list size
   * 
   * @return return size of the list
   */
  public int getListSize() {
    return mListSize;
  }

  /**
   * Sets new list size
   * 
   * @param pListSize new list size
   */
  public void setListSize(int pListSize) {
    mListSize = pListSize;
  }
  
  //-------------------------------------
  // templateToTypeMap
  private Map<String,String> mTemplateToTypeMap;
  
  /**
   * @return the mTemplateToTypeMap
   */
  public Map<String, String> getTemplateToTypeMap() {
    return mTemplateToTypeMap;
  }
  
  /**
   * @param pTemplateToTypeMap the templateToTypeMap to set
   */
  public void setTemplateToTypeMap(Map<String, String> pTemplateToTypeMap) {
    mTemplateToTypeMap = pTemplateToTypeMap;
  }
  
  //-------------------------------------
  // typeToMenuItemMap
  private Map<String,String> mTypeToMenuItemMap;
  
  /**
   * @return the mTypeToMenuItemMap
   */
  public Map<String, String> getTypeToMenuItemMap() {
    return mTypeToMenuItemMap;
  }

  /**
   * @param pTypeToMenuItemMap the typeToMenuItemMap to set 
   */
  public void setTypeToMenuItemMap(Map<String, String> pTypeToMenuItemMap) {
    mTypeToMenuItemMap = pTypeToMenuItemMap;
  }

  //-------------------------------------
  // sentList  
  private LinkedList<SentItem> mSentList = new LinkedList<SentItem>();
  
  /**
   * Returns list of sent items
   * 
   * @return list of sent items
   */
  public List<SentItem> getSentList() {
    return Collections.unmodifiableList(mSentList);
  }

  /**
   * Sets new list for recently sent items
   * 
   * @param pSentList new list
   */
  public void setSentList(LinkedList<SentItem> pSentList) {
    if(pSentList == null) {
      mSentList = new LinkedList<SentItem>();
    } else {
      mSentList = pSentList;
    }
  }
  
  /**
   * Adds new item to the list. If size of the SentList is exceed 
   * ListSize then remove first item from the list and 
   * add new item at the end of the list.  
   * 
   * @param pItem new SentItem element
   * @return true if element has been added
   */
  public boolean addItem(SentItem pItem) {
    synchronized (pItem) {
      if(!mSentList.contains(pItem)) {
        
        if(mSentList.size() >= getListSize()) {
          mSentList.removeLast();
        }

        mSentList.addFirst(pItem);
        return true;
        
      } else {
        return false;
      }
    }
  }
  
  /**
   * Return SentItem at the given index 
   * 
   * @param pIndex the index
   * @return SentItem bean
   */
  public SentItem getItem(int pIndex) {
    return getSentList().get(pIndex);
  }
  
  /**
   * Create and add new item based on given input parameters
   * 
   * @param pType email type
   * @param pEmailParams email parameters
   * @param pTimestamp timestamp
   */
  public void addItem(String pType, Map pEmailParams,
      String pTimestamp) {
    
    addItem(new SentItem(pType, pEmailParams, pTimestamp));
  }
  
  /**
   * Removes item from the list
   *  
   * @param pItem item to remove
   * @return true if list contains the pItem 
   */
  public boolean removeItem(SentItem pItem) {
    synchronized(mSentList) {
      return mSentList.remove(pItem);
    }
  }
  
  /**
   * Resolve template name for the given URL
   * 
   * @param pTemplateUrl template url
   * @return template name
   */
  public String getTemplateName(String pTemplateUrl) {
    if(getTemplateToTypeMap() != null && getTemplateToTypeMap().containsKey(pTemplateUrl)) {
      return getTemplateToTypeMap().get(pTemplateUrl);
    } 
    
    return null;
  }
  /**
   * SentItem object, which contains email type, map of email parameters and time of email creation     
   */
  public class SentItem {
    
    /**
     * Create new SentItem object
     * 
     * @param pType email type
     * @param pParameters map of email parameters
     * @param pTimestamp time of email creation
     */
    public SentItem(String pType, Map pParameters, String pTimestamp) {
      mType = pType;
      mParameters = pParameters;
      mTimestamp = pTimestamp;
    }
    
    //-------------------------------------
    // type 
    private String mType;
    
    /**
     * @return the mType
     */
    public String getType() {
      return mType;
    }

    /**
     * @param pType the type to set
     */
    public void setType(String pType) {
      mType = pType;
    }

    //-------------------------------------
    // parameters 
    private Map mParameters;
    
    /**
     * @return the mParameters
     */
    public Map getParameters() {
      return mParameters;
    }

    /**
     * @param pParameters the parameters to set
     */
    public void setParameters(Map pParameters) {
      mParameters = pParameters;
    }

    //-------------------------------------
    // timestamp 
    private String mTimestamp;
    
    /**
     * @return the mTimestamp
     */
    public String getTimestamp() {
      return mTimestamp;
    }
    
    /**
     * @param pTimestamp the timestamp to set
     */
    public void setTimestamp(String pTimestamp) {
      mTimestamp = pTimestamp;
    }
    
    /**
     * Returns language for email's locale defined in email parameters
     * 
     * @return email's language
     */
    public String getLanguage() {
      if(mParameters != null) {
        Locale loc = LocaleUtils.constructLocale((String)mParameters.get(LOCALE));
        return loc.getDisplayLanguage();
      }
      
      return null;
    }
    
    /**
     * Returns menu item name
     * 
     * @return menu item name
     */
    public String getMenuItem() {
      return getTypeToMenuItemMap().get(getType());
    }
  }
}
