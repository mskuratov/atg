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


package atg.projects.store.pricing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.pricing.Constants;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.PricingTools;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.servlet.RequestLocale;
import atg.userprofiling.Profile;

/**
 * <p>
 *   This DynamoServlet Bean is used to determine shipping costs for the shipping
 *   group with specified shipping method. The class's service method calls into
 *   the ShippingPricingEngine <code>priceShippingGroup</code> method to get the
 *   ShippingPriceInfo for the specified shipping group and the shipping method.
 *   The determined shipping cost is put into the output parameter.
 * </p>
 * <p>
 *   The following parameters are required:
 *   <dl>
 *     <dt>shippingGroup<dt>
 *       <dd>The ShippingGroup to price</dd>
 *     <dt>shippingMethod</dt>
 *       <dd>The Shipping method to price with</dd>
 *   </dl>
 * </p>
 * <p>
 *   The following output parameter is defined when the service method is invoked:
 *   <dl>
 *     <dt>shippingPrice</dt>
 *       <dd>A double value that corresponds to the shipping cost of the specified
 *           shipping group with specified shipping method.</dd>
 *      <dt>output</dt>
 *        <dd>An oparam that is serviced if the shipping cost is determined.<dd>
 *   </dl>
 * </p>
 * <p>
 *   This is an example of using this droplet to provide a price for the shipping
 *   with the specified shipping method.
 * </p>
 * <p>
 *   <pre>
 *     &lt;dsp:droplet name="/atg/store/pricing/PriceShippingMethod""&gt;
 *       &lt;dsp:param name="shippingGroup" param="shippingGroup"/"&gt;
 *       &lt;dsp:param name="shippingMethod" param="shippingMethod"/"&gt;
 *       &lt;dsp:oparam name="output""&gt;
 *         &lt;dsp:getvalueof var="shippingPrice" param="shippingPrice" /"&gt;
 *         &lt;fmt:formatNumber value="${shippingPrice}" type="currency" /"&gt;
 *       &lt;/dsp:oparam"&gt;
 *     &lt;/dsp:droplet"&gt; 
 *   </pre>
 * </p>
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/pricing/PriceShippingMethodDroplet.java#4 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class PriceShippingMethodDroplet extends DynamoServlet {

  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/pricing/PriceShippingMethodDroplet.java#4 $$Change: 788278 $";

  // -------------------------------------
  // Constants
  // -------------------------------------
  private static final String PERFORM_MONITOR_NAME = "PriceShippingMethodDroplet";

  static final ParameterName SHIPPING_GROUP_PARAM = ParameterName.getParameterName("shippingGroup");
  static final ParameterName SHIPPING_METHOD_PARAM = ParameterName.getParameterName("shippingMethod");

  static final ParameterName OUTPUT = ParameterName.getParameterName("output");
  static final String SHIPPING_PRICE = "shippingPrice";
  static final String MY_RESOURCE_NAME = "atg.commerce.pricing.Resources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = 
    java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  // -------------------------------------
  // Properties
  // -------------------------------------

  /** 
   * property: pricingTools
   */
  PricingTools mPricingTools;

  /**
   * @return pricing tools component.
   */
  public PricingTools getPricingTools() {
    return mPricingTools;
  }

  /**
   * @param pPricingTools sets new pricing tools component.
   */
  public void setPricingTools(PricingTools pPricingTools) {
    mPricingTools = pPricingTools;
  }

  /**
   * property: defaultLocale
   */
  Locale mDefaultLocale;

  /**
   * @param pDefaultLocale - the default locale for which shipping method should be priced.
   */
  public void setDefaultLocale(Locale pDefaultLocale) {
    mDefaultLocale = pDefaultLocale;
  }

  /**
   * @return the default locale for which shipping method should be priced.
   */
  public Locale getDefaultLocale() {
    return mDefaultLocale;
  }

  /**
   * property: order
   */
  Order mOrder;

  /**
   * Set the Order property.
   * 
   * @param pOrder - an <code>Order</code> value.
   */
  public void setOrder(Order pOrder) {
    mOrder = pOrder;
  }

  /**
   * @return an <code>Order</code> value.
   */
  public Order getOrder() {
    return mOrder;
  }


  /** 
   * property: profile
   */
  Profile mProfile;

  /**
   * @param pProfile - a <code>Profile</code> value.
   */
  public void setProfile(Profile pProfile) {
    mProfile = pProfile;
  }

  /**
   * @return a <code>Profile</code> value.
   */
  public Profile getProfile() {
    return mProfile;
  }

  /** 
   * property: userPricingModels
   */
  PricingModelHolder mUserPricingModels;

  /**
   * @param pUserPricingModels - a <code>PricingModelHolder</code> value.
   */
  public void setUserPricingModels(PricingModelHolder pUserPricingModels) {
    mUserPricingModels = pUserPricingModels;
  }

  /**
   * @return a <code>PricingModelHolder</code> value.
   */
  public PricingModelHolder getUserPricingModels() {
    return mUserPricingModels;
  }

  /**
   * Performs the pricing of specified shipping method. Sets the shipping method
   * the specified shipping group to the one that is passed to the droplet. Then
   * calls shipping pricing engine to perform pricing of shipping group. And
   * finally returns back the original shipping method of the shipping group.
   * The determined price is stored into the output parameter.
   * 
   * @param pRequest - the request to be processed.
   * @param pResponse - the response object for this request.
   * 
   * @exception ServletException an application specific error occurred processing this request.
   * @exception IOException an error occurred reading data from the request or writing data to the response.
   */
  @Override
  public void service(DynamoHttpServletRequest pRequest,DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {

    String perfName = "service";
    
    if (PerformanceMonitor.isEnabled()) {
      PerformanceMonitor.startOperation(PERFORM_MONITOR_NAME, perfName);
    }
    
    boolean perfCancelled = false;

    try {
      double shippingPrice = 0.0;

      ShippingGroup shippingGroup = getShippingGroup(pRequest, pResponse);
      Locale locale = getUserLocale(pRequest, pResponse);
      String shippingMethod = (String) pRequest.getLocalParameter(SHIPPING_METHOD_PARAM);
      
      if (shippingGroup != null && !StringUtils.isEmpty(shippingMethod)) {
        
        try {
          String currentShippingMethod = shippingGroup.getShippingMethod();
          
          // Set specified shipping method to determine its price.
          shippingGroup.setShippingMethod(shippingMethod);
          
          // Create extra parameters map. We need do disable stacking rule and GWP messaging and
          // messages slot clearing operations for this pricing operation as we do not
          // perform real order re-pricing but just need to display shipping prices to user
          // taking into account shipping discounts.          
          Map parameters = new HashMap();
          parameters.put(Constants.DISABLE_GWP_MESSAGING, true);
          parameters.put(Constants.DISABLE_STACKING_RULE_MESSAGING, true);
          parameters.put(Constants.DISABLE_CLEARING_MESSAGE_TOOLS, true);
          
          // Re-price order instead of shipping group.
          getPricingTools().performPricingOperation(PricingConstants.OP_REPRICE_SHIPPING, 
                                                    getOrder(),
                                                    getUserPricingModels(), 
                                                    locale, 
                                                    getProfile(), 
                                                    parameters);

          shippingPrice = getOrder().getShippingGroup(shippingGroup.getId()).getPriceInfo().getAmount();

          // Set old shipping method back.
          shippingGroup.setShippingMethod(currentShippingMethod);
          pRequest.setParameter(SHIPPING_PRICE, shippingPrice);
          pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);

        } 
        catch (PricingException exc) {
          if (isLoggingError()) {
            logError("Unable price a shipment: ", exc);
          }
        } 
        catch (ShippingGroupNotFoundException e) {
          if (isLoggingError()) {
            logError("Unable to find shipping group with ID: " + shippingGroup.getId(), e);
          }
        } 
        catch (InvalidParameterException e) {
          if (isLoggingError()) {
            logError("Wrong shipping group ID: " + shippingGroup.getId(), e);
          }
        }
      } 
      else {
        String args[] = { SHIPPING_GROUP_PARAM.toString(), SHIPPING_METHOD_PARAM.toString() };
        if (isLoggingDebug()) {
          logDebug(ResourceUtils.getMsgResource("missingRequiredInputParam",
                                                MY_RESOURCE_NAME, 
                                                sResourceBundle, 
                                                args));
        }
      }
    } 
    finally {
      try {
        if (!perfCancelled) {
          if (PerformanceMonitor.isEnabled()) {
            PerformanceMonitor.endOperation(PERFORM_MONITOR_NAME, perfName);
          }
          perfCancelled = true;
        }
      } 
      catch (PerfStackMismatchException e) {
        if (isLoggingWarning()) {
          logWarning(e);
        }
      }
    }// end finally

  }

  /**
   * Get the shipping group from that we should price with the specified shipping method.
   * 
   * @param pRequest - the request to be processed.
   * @param pResponse - the response object for this request.
   * 
   * @return the shipping group from that we should price with the specified shipping method.
   * 
   * @exception ServletException an application specific error occurred processing this request
   * @exception IOException an error occurred reading data from the request or writing data to the response.
   */
  protected ShippingGroup getShippingGroup(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    try {
      return (ShippingGroup) pRequest.getObjectParameter(SHIPPING_GROUP_PARAM);
    } 
    catch (ClassCastException exc) {
      String args[] = { SHIPPING_GROUP_PARAM.toString() };
      
      if (isLoggingError()) {
        logError(ResourceUtils.getMsgResource("invalidInputParam",
                                              MY_RESOURCE_NAME, 
                                              sResourceBundle, 
                                              args), exc);
      }
      return null;
    }
  }

  /**
   * Returns the locale associated with the request. The method first searches
   * the locale of the request. If the locale cannot be determined, the the
   * <code>defaultLocale</code> property is used.
   * 
   * @param pRequest - the request to be processed.
   * @param pResponse - the response object for this request.
   * 
   * @return the locale to be associated with this user.
   * 
   * @exception ServletException an application specific error occurred processing this request.
   * @exception IOException an error occurred reading data from the request or writing data to the response.
   */
  protected Locale getUserLocale(DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {

    RequestLocale requestLocale = pRequest.getRequestLocale();
    
    if (requestLocale != null) {
      return requestLocale.getLocale();
    }
    
    return getDefaultLocale();
  }
}
