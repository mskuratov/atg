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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.DetailedItemPriceInfo;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.UnitPriceBean;
import atg.core.util.Range;
import atg.projects.store.pricing.StorePricingTools;
import atg.projects.store.pricing.StoreUnitPriceBean;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * This droplet generates UnitPriceBeans for the whole order, or for the shipping-group-commerce-item relationship.
 * If an order is specified, there would be generated price beans with quantity equal to 1 (always) for each item in the order specified.
 * If a relationship is specified, price beans for this relationship would be generated, there also would be calculated total amount
 * for the relationthip specified.
 * <br/>
 * This droplet always serves 'output' oparam.
 * If there is 'order' input parameter specified, it would set 'priceBeansMap' output parameter
 * calculated with {@link #generatePriceBeansForOrder(Order)} method.
 * If there is 'relationship' input parameter set, it would set 'priceBeans', 'priceBeansQuantity' and 'priceBeansAmount' output parameters,
 * see {@link #generatePriceBeansForRelationship(ShippingGroupCommerceItemRelationship)} for details.
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/StorePriceBeansDroplet.java#3 $$Change: 788278 $ 
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StorePriceBeansDroplet extends DynamoServlet {

  private static final String OPARAM_OUTPUT = "output";
    
  private static final String PARAM_PRICE_BEANS_MAP = "priceBeansMap";
  private static final String PARAM_QUANTITY = "priceBeansQuantity";
  private static final String PARAM_GWP_QUANTITY = "gwpPriceBeansQuantity";
  private static final String PARAM_AMOUNT = "priceBeansAmount";
  private static final String PARAM_PRICE_BEANS = "priceBeans";
  private static final String PARAM_RELATIONSHIP = "relationship";
  private static final String PARAM_ITEM = "item";
  private static final String PARAM_ORDER = "order";
  
  /**
   * Class version
   */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/StorePriceBeansDroplet.java#3 $$Change: 788278 $";
  
  /**
   * Pricing tools.
   */
  protected StorePricingTools mPricingTools;

  /**
   * @return the pricing tools.
   */
  public StorePricingTools getPricingTools() {
    return mPricingTools;
  }

  /**
   * @param pPricingTools - the pricing tools to set.
   */
  public void setPricingTools(StorePricingTools pPricingTools) {
    mPricingTools = pPricingTools;
  }
  
  /**
   * Provides the implementation of service method.
   * @param pRequest the request to be processed
   * @param pResponse the response object for this request
   * @throws ServletException an application specific error occurred processing this request
   * @throws IOException an error occurred reading data from the request or writing data to the response.
   */
@Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
  Order order = (Order) pRequest.getObjectParameter(PARAM_ORDER);
    CommerceItem item = (CommerceItem) pRequest.getObjectParameter(PARAM_ITEM);
    ShippingGroupCommerceItemRelationship sgciRelationship =
        (ShippingGroupCommerceItemRelationship) pRequest.getObjectParameter(PARAM_RELATIONSHIP);
    
       
    // If there is a relationship parameter, build price beans for it    
    List<StoreUnitPriceBean> priceBeans = null;
    if (sgciRelationship != null || item != null) {
      if (sgciRelationship != null){
        priceBeans = generatePriceBeansForRelationship(order, sgciRelationship);
      }else{
        priceBeans = getPricingTools().generatePriceBeans(order, item);
      }
        
      pRequest.setParameter(PARAM_PRICE_BEANS, priceBeans);
        
      double totalAmount = 0;
      long totalQuantity = 0;
        
      if (priceBeans != null) {
        for (UnitPriceBean priceBean: priceBeans) {
          totalAmount += priceBean.getUnitPrice() * priceBean.getQuantity();
          totalQuantity += priceBean.getQuantity();
        }
      }
        
      pRequest.setParameter(PARAM_AMOUNT, totalAmount);
      pRequest.setParameter(PARAM_QUANTITY, totalQuantity);
      pRequest.setParameter(PARAM_GWP_QUANTITY, getGwpPriceBeansQuantity(priceBeans));
      
    }else{
      
      // If there is an order specified, build price beans for it
      if (order != null) {
        pRequest.setParameter(PARAM_PRICE_BEANS_MAP, generatePriceBeansForOrder(order));
      }
      
    }
    
    pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
  }
  
  /**
   * Generates price beans for a relationship specified.
   * This method takes all commerce item's price infos with range located within relationship's range.
   * For each price info it creates a price bean.
   * @param pRelationship - specifies a shipping-group-commerce-item relationsip to build price beans from.
   * @return list of price beans for the relationship specified.
   */
  @SuppressWarnings("unchecked") // Ok, we know collections we're working with
  protected List<StoreUnitPriceBean> generatePriceBeansForRelationship(Order pOrder, ShippingGroupCommerceItemRelationship pRelationship) {
    Range relationshipRange = pRelationship.getRange();
    
    CommerceItem ci = pRelationship.getCommerceItem();
    ItemPriceInfo priceInfo = null;
    
    if (ci != null) {
      priceInfo = ci.getPriceInfo();
    }
    
    if (priceInfo != null) {
      List<DetailedItemPriceInfo> itemPriceInfos = 
        (List<DetailedItemPriceInfo>) priceInfo.getCurrentPriceDetailsForRange(relationshipRange);
      
      // Only price infos with proper ranges should be used
      return getPricingTools().generatePriceBeans(pOrder, pRelationship.getCommerceItem(), itemPriceInfos);
    }
    
    return null;
  }
  
  
  /**
   * Returns total quantity of GWP items in the list of price beans.
   * @param pPriceBeans The list of price beans to calculate GWP quantity for.
   * @return The total quantity of GWP items in the list of price beans.
   */
  protected long getGwpPriceBeansQuantity(Collection<StoreUnitPriceBean> pPriceBeans){
	  
    long totalQuantity = 0;
        
    if (pPriceBeans != null) {
      for (StoreUnitPriceBean priceBean: pPriceBeans) {
        if (priceBean.isGiftWithPurchase()){
          totalQuantity += priceBean.getQuantity();
        }
      }
    }
      
    return totalQuantity;
  }
  
  /**
   * Generates price beans for an order specified.
   * This method iterates over all order's commerce items and generates price beans for each item.
   * Each price bean will have a quantity of 1, that is for commerce item with quantity 2 will be created 2 price beans.
   * @param pOrder - order to build price beans from.
   * @return list of price beans mapped by apropriate commerce item ID.
   */
  @SuppressWarnings("unchecked")
  protected Map<String, StoreUnitPriceBean> generatePriceBeansForOrder(Order pOrder) {
    FifoMultiMap<StoreUnitPriceBean> result = new FifoMultiMap<StoreUnitPriceBean>();
    for (CommerceItem commerceItem: (List<CommerceItem>) pOrder.getCommerceItems()) {
      List<StoreUnitPriceBean> priceBeans = getPricingTools().generatePriceBeans(pOrder, commerceItem);
      for (StoreUnitPriceBean priceBean : priceBeans) {
        // split price beans by quantity 1
        long initialQuantity = priceBean.getQuantity();
        priceBean.setQuantity(1);
        for (long i = 0; i < initialQuantity; i++) {
          result.put(commerceItem.getId(), priceBean);
        }
      }
    }
    return result;
  }
  
  /**
   * This HashMap implementation can contain several values per single key.
   * When a new value is passed into {@link #put(String, UnitPriceBean)} method, it saves this value into an ArrayList mapped by key specified.
   * When some object is obtained from {@link #get(Object)} method, it retrieves an ArrayList mapped by the key specified,
   * returns first object from it and then removes it from the list.
   * @see ArrayList
   */
  @SuppressWarnings("serial")
  private static class FifoMultiMap<E extends UnitPriceBean> extends HashMap<String, E> {

    //-------------------------------------
    /** Class version string */

    public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/StorePriceBeansDroplet.java#3 $$Change: 788278 $";

    private Map<String, ArrayList<E>> mInnerMap = new HashMap<String, ArrayList<E>>();
    
    /**
     * @param pKey the key whose associated value is to be returned 
     * @return the value to which the specified key is mapped, 
     * or null if this map contains no mapping for the key
     */
    @Override
    public E get(Object pKey) {
      return getOrCreateList((String) pKey).remove(0);
    }

    /**
     * Associates the specified value with the specified key in this map (optional operation). 
     * If the map previously contained a mapping for the key, the old value is replaced by the specified value. 
     * (A map m is said to contain a mapping for a key k if and only if m.containsKey(k) would return true.) 
     * 
     * @param pKey key with which the specified value is to be associated
     * @param pValue value to be associated with the specified key 
     * @return the previous value associated with key, or null if there was no mapping for key. 
     * (A null return can also indicate that the map previously associated null with key, if the implementation supports null values.) 
     */
    @Override
    public E put(String pKey, E pValue) {
      getOrCreateList(pKey).add(pValue);
      return null;
    }
    
    /**
     * Tries to find an ArrayList inside the inner HashMap, if nothing found, it creates a new instance and puts it into inner Map.
     * @param pKey - key to be used.
     * @return stored ArrayList (if any) or new instance.
     */
    private List<E> getOrCreateList(String pKey)
    {
      ArrayList<E> collection = mInnerMap.get(pKey);
      if (collection == null) {
        collection = new ArrayList<E>();
        mInnerMap.put(pKey, collection);
      }
      return collection;
    }
  }
}
