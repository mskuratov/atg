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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;

import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.util.PlaceList;
import atg.core.i18n.CountryList;
import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.projects.store.gifts.StoreGiftlistManager;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.util.CountryRestrictionsService;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * <p>
 * The droplet sorts shipping addresses so that the default address 
 * is first and returns only permitted shipping addresses.
 * </p>
 * 
 * <p>
 * This droplet takes the following parameters
 * <dl>
 * <dt>defaultKey</dt>
 * <dd>The parameter that defines the map key of the default item that should be
 * placed in the beginning of the array.</dd>
 * </dl>
 * <dt>sortByKeys</dt>
 * <dd>Boolean that specifies whether to sort map entries by keys or not.</dd>
 * </dl>
 * <dl>
 * <dt>map</dt>
 * <dd>The parameter that defines the map of items to convert to the sorted array.</dd>
 * </dl>
 * <dl>
 * </p>
 * 
 * <p>
 * Output parameters
 * <dt>output</dt><dd>Rendered for permitted shipping address list, permittedAddresses
 * parameter contains the permitted shipping addresses.
 * </dd>
 * <dt>empty</dt><dd>Rendered if there are no shipping addresses or there are no
 * permitted shipping addresses</dd>
 * </dl>
 * </p>
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/AvailableShippingGroups.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class AvailableShippingGroups extends DynamoServlet{

  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/AvailableShippingGroups.java#3 $$Change: 788278 $";

   /**
   * Oparam: true.
   */
  private static final String TRUE = "true";
  
  /**
   * Oparam: false.
   */
  private static final String FALSE = "false";
  
  /**
   * Permitted addresses parameter name
   */
  private static final String  PERMITTED_ADDRESSES = "permittedAddresses";
  
  /**
   * Oparam: output
   */
  private static final ParameterName OUTPUT  = ParameterName.getParameterName( "output" );
  
  /**
   * Empty parameter name.
   */
  public final static ParameterName EMPTY = ParameterName.getParameterName("empty");
  
  /**
   * Map parameter name.
   */
  public final static ParameterName MAP = ParameterName.getParameterName("map");

  /**
   * Default id parameter name.
   */
  public final static ParameterName DEFAULT_ID = ParameterName.getParameterName("defaultId");
  
  /**
   * Default key parameter name.
   */
  public final static ParameterName DEFAULT_KEY = ParameterName.getParameterName("defaultKey");
  
  /**
   * Sort by keys parameter name
   */
  public final static ParameterName SORT_BY_KEYS = ParameterName.getParameterName("sortByKeys");

  
  /**
   * Available shipping groups service
   */
  CountryRestrictionsService mCountryRestrictionsService;
  
  /**
   * @return the countryRestrictionsService
   */
  public CountryRestrictionsService getCountryRestrictionsService() {
    return mCountryRestrictionsService;
  }

  /**
   * @param pCountryRestrictionsService the countryRestrictionsService to set
   */
  public void setCountryRestrictionsService(
      CountryRestrictionsService pCountryRestrictionsService) {
    mCountryRestrictionsService = pCountryRestrictionsService;
  }

  /**
   * Country list.
   */
  private CountryList mCountryList;
  
  /**
   * @return country list.
   */
  public CountryList getCountryList() {
    return mCountryList;
  }

  /**
   * @param pCountryList
   *          - country list.
   */
  public void setCountryList(CountryList pCountryList) {
    mCountryList = pCountryList;
  }

  /**
   * Permitted country codes.
   */
  private List mPermittedCountryCodes;
  
  /**
   * @return permitted country list.
   */
  public List getPermittedCountryCodes() {
    return mPermittedCountryCodes;
  }

  /**
   * @param pPermittedCountryCodes
   *          - permitted country list.
   */
  public void setPermittedCountryCodes(List pPermittedCountryCodes) {
    mPermittedCountryCodes = pPermittedCountryCodes;

    if (isLoggingDebug()) {
      logDebug(LogUtils
          .formatMajor("++++++inside setPermittedCountryCodes+++++++"));

      if (pPermittedCountryCodes != null) {
        Iterator it = pPermittedCountryCodes.iterator();

        while (it.hasNext()) {
          String code = (String) it.next();
          logDebug(LogUtils.formatMajor("code = " + code));
        }
      }

      logDebug(LogUtils
          .formatMajor("++++++exiting setPermittedCountryCodes+++++++"));
    }
  }


  /**
   * Restricted country codes.
   */
  private List mRestrictedCountryCodes;
  
  /**
   * @return restricted country codes.
   */
  public List getRestrictedCountryCodes() {
    return mRestrictedCountryCodes;
  }

  /**
   * @param pRestrictedCountryCodes
   *          - restricted country codes.
   */
  public void setRestrictedCountryCodes(List pRestrictedCountryCodes) {
    mRestrictedCountryCodes = pRestrictedCountryCodes;

    if (isLoggingDebug()) {
      logDebug(LogUtils
          .formatMajor("++++++inside setRestrictedCountryCodes+++++++"));

      if (pRestrictedCountryCodes != null) {
        Iterator it = pRestrictedCountryCodes.iterator();

        while (it.hasNext()) {
          String code = (String) it.next();
          logDebug(LogUtils.formatMajor("code = " + code));
        }
      }

      logDebug(LogUtils
          .formatMajor("++++++exiting setRestrictedCountryCodes+++++++"));
    }
  }
  
  //-----------------------------------
  // property: removeGiftShippingGroups
  private boolean mRemoveGiftShippingGroups;
  
  /** @return Boolean indicating whether or not shipping groups in the input map should be returned in the output list*/
  public boolean isRemoveGiftShippingGroups() {
    return mRemoveGiftShippingGroups;
  }
  /** @param pRemoveGiftShippingGroups Set the removeShippingGroups flag */
  public void setRemoveGiftShippingGroups(boolean pRemoveGiftShippingGroups) {
    mRemoveGiftShippingGroups = pRemoveGiftShippingGroups;
  }
  
  //-----------------------------------
  // property: giftlistManager
  private StoreGiftlistManager mGiftlistManager;
  
  /** @return The GiftlistManager component */
  public StoreGiftlistManager getGiftlistManager() {
    return mGiftlistManager;
  }
  /** @param Set a new GiftlistManager */
  public void setGiftlistManager(StoreGiftlistManager pGiftlistManager) {
    mGiftlistManager = pGiftlistManager;
  }

  //-----------------------------------
  // METHODS
  //-----------------------------------
  /**
   * See the class documentation above.
   *
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @exception ServletException an application specific error occurred
   * processing this request
   * @exception IOException an error occurred reading data from the request
   * or writing data to the response.
   */
  public void service(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {
   
    String defaultKey = pRequest.getParameter(DEFAULT_KEY);
    String sortByKeysParameter = pRequest.getParameter(SORT_BY_KEYS);
    Map map = (Map) pRequest.getObjectParameter(MAP);
    
    Object[] sortedArray = getSortedAddressList(defaultKey, 
        sortByKeysParameter, map);
       
    // if the were shipping groups returned
    if (sortedArray != null && sortedArray.length > 0) {
      
      List permittedCountryList = getCountryRestrictionsService().getPermittedCountryList(pRequest, 
          getPermittedCountryCodes(), getRestrictedCountryCodes(), getCountryList());
      List resultAddressList = new ArrayList(); 
      
      /* 
       * Loop through available shipping groups (this means addresses too) and
       * check if shipping address is allowed for shipping.
       */
      for (Object sortedItem : sortedArray) {
        ShippingGroup shippingGroup = (ShippingGroup) ((Map.Entry)sortedItem).getValue();
        String countryCode = getCountryCode(shippingGroup);
        if (getCountryRestrictionsService().checkCountryInPermittedList(countryCode, permittedCountryList)){
          
          // Remove giftlist shipping groups
          if(isRemoveGiftShippingGroups()){ 
            if(getGiftlistManager().hasGiftAddress(shippingGroup)){
              continue;
            }
          }
          /*
           * If noPermittedAddress is true, this is the first permitted
           * shipping address and OUTPUT_START param should be service
           */
          resultAddressList.add(sortedItem);
        }
      }
      /* 
       * If list of addresses is empty, render EMPTY open parameter 
       */
      if (resultAddressList.isEmpty()){
        pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse );
      }
      // list is not empty, OUTPUT param should be rendered
      else{
        pRequest.setParameter(PERMITTED_ADDRESSES, resultAddressList);
        pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse );
      }
    }
    //if no shipping groups, render EMPTY parameter
    else {
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse );
    }

  }

  /**
   * Returns country code for the given item. For the AvailableShippingGroups 
   * pItem.value is of type HardgoodShippingGroup
   *  
   * @param map entry item that contains country
   * @return country code associated with the pItem
   */
  protected String getCountryCode(ShippingGroup pShippingGroup) {
    String countryCode = null;
    
    if(pShippingGroup instanceof HardgoodShippingGroup) {
      HardgoodShippingGroup hardgoodShippingGroup = (HardgoodShippingGroup) pShippingGroup;
      countryCode = hardgoodShippingGroup.getShippingAddress().getCountry();
    }
    
    return countryCode;
  }
  
  /**
   * Converts the map to the array of map entries with the default one in the beginning of the array.
   * If <code>pSortByKeys</code> boolean is true then map entries are sorted by keys.
   *
   * @param pMap - the map to convert to the sorted array
   * @param pDefaultKey - the map key that corresponds to the default item
   * @param pSortByKeys - boolean that specifies whether to sort items by keys or not
   * @return Object[] sorted array of items
   */
  public Object[] getSortedArray(Map pMap, String pDefaultKey, boolean pSortByKeys) {
    
    Map resultMap = pMap;
    if (pSortByKeys) {
      // convert to the sorted by keys map
      resultMap = new TreeMap(pMap); 
    }    
    
    if (pDefaultKey == null) {
      // default key is not specified, so just convert the map to array
      return resultMap.entrySet().toArray();
    }
    
    //default map key is specified, put it in the beginning of the array
    LinkedList sortedItems = new LinkedList();

    // iterate over the set of items and look for default one
    for (Object item : resultMap.entrySet()) {
      String key = (String)((Map.Entry)item).getKey();
      
      // insert default item in the beginning of the page
      if (key.equals(pDefaultKey)) {
        sortedItems.addFirst(item);
      } else {
        sortedItems.addLast(item);
      }
    }

    return sortedItems.toArray();
  }
  
  /**
   * This method converts a map of items to a sorted array of map entries
   * with the default item in the beginning of the array. The default item is specified
   * through defaultKey parameter that is the map key that corresponds to the default item.
   * 
   * The method is used to sort shipping addresses  
   * so that the default address is displayed as the first item on the JSP
   * 
   * @param pDefaultKey defines the map key of the default item that should be
   * placed in the beginning of the array.
   * @param pSortByKeysParameter is set to true then the returning array will be sorted by keys
   * otherwise an unsorted array will be returned.
   * In the case of a null map, the returning array would be null.
   * @param pMap defines the map of items to convert to the sorted array.
   * @return Object[] sorted array of addresses
   */
  public Object[] getSortedAddressList(String pDefaultKey,
      String pSortByKeysParameter, Map pMap){   
    
    // if sortByKeys parameter is not specified do not perform sorting of the array
    boolean sortByKeys = false;
    if (!StringUtils.isEmpty(pSortByKeysParameter)){
      sortByKeys = Boolean.parseBoolean(pSortByKeysParameter);
    }

    if (isLoggingDebug()) {
      logDebug(" defaultKey = " + pDefaultKey);
      logDebug(" sortByKeys = " + sortByKeys);
      logDebug(" map = " + pMap);
    }

    if (pMap == null || pMap.size() == 0 ) {
      if (isLoggingDebug()) {
        logDebug("map parameters is null or empty");
      }
     
      return null;
    }

    // get the key that corresponds the specified default ID
    String defaultMapKey = null;
    // if default ID is not specified use defaultKey parameter
    if (pDefaultKey != null && pMap.containsKey(pDefaultKey)){
      defaultMapKey = pDefaultKey;
    }

    if (isLoggingDebug()) {
      logDebug("Map size: " + pMap.size());

      if (defaultMapKey != null) {
        logDebug("Default entry was found in the map");
      } else {
        logDebug("Default entry was not found in the map");
      }
    }

    Object[] sortedArray = getSortedArray(pMap, defaultMapKey, sortByKeys);

    return sortedArray;
  }
  

}
