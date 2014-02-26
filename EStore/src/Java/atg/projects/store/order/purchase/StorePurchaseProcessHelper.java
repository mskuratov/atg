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


package atg.projects.store.order.purchase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.commerce.CommerceException;
import atg.commerce.claimable.ClaimableException;
import atg.commerce.claimable.ClaimableManager;
import atg.commerce.order.Order;
import atg.commerce.order.purchase.PurchaseProcessHelper;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.PricingTools;
import atg.commerce.promotion.DuplicatePromotionException;
import atg.commerce.util.PlaceUtils;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.projects.store.StoreConfiguration;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.StoreOrderManager;
import atg.projects.store.order.StoreOrderTools;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;

/**
 * Store implementation of the purchase process helper.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StorePurchaseProcessHelper.java#4 $$Change: 791597 $
 * @updated $DateTime: 2013/02/20 03:19:59 $$Author: npaulous $
  */
public class StorePurchaseProcessHelper extends PurchaseProcessHelper {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StorePurchaseProcessHelper.java#4 $$Change: 791597 $";

  private static final String PHONE_NUMBER_PROP_NAME = "phoneNumber";
  public static final String POSTAL_CODE_PROP_NAME = "postalCode";
  public static final String STATE_PROP_NAME = "state";
  public static final String COUNTRY_PROP_NAME = "country";
  public static final String CITY_PROP_NAME = "city";
  public static final String ADDRESS_PROP_NAME = "address1";
  public static final String LAST_NAME_PROP_NAME = "lastName";
  public static final String FIRST_NAME_PROP_NAME = "firstName";

  /**
   * Missing required address property.
   */
  public static final String MSG_MISSING_REQUIRED_ADDRESS_PROPERTY = "missingRequiredAddressProperty";

  /**
   * Pricing error invalid address message key.
   */
  public static final String PRICING_ERROR_ADDRESS = "pricingErrorInvalidAddress";

  /**
   * Pricing error message key.
   */
  public static final String PRICING_ERROR = "pricingError";

  /**
   * Multiple coupons per order error message key.
   */
  public static final String MSG_MULTIPLE_COUPONS_PER_ORDER = "multipleCouponsPerOrder";

  /**
   * Unclaimable coupon error message key.
   */
  public static final String MSG_UNCLAIMABLE_COUPON = "couponNotClaimable";

  /**
   * property: addressProperyNameMap
   */
  Map mAddressPropertyNameMap;

  /**
   * @return the address property name map.
   */
  public Map getAddressPropertyNameMap() {
    return mAddressPropertyNameMap;
  }

  /**
   * @param pAddressPropertyNameMap - the address property name map to set.
   */
  public void setAddressPropertyNameMap(Map pAddressPropertyNameMap) {
    mAddressPropertyNameMap = pAddressPropertyNameMap;
  }

  /**
   * property: requiredAddressProperyNames
   */
  private String[] mRequiredAddressPropertyNames = new String[0];

  /**
   * @return the address property names.
   */
  public String[] getRequiredAddressPropertyNames() {
    return mRequiredAddressPropertyNames;
  }

  /**
   * @param pRequiredAddressPropertyNames - the address property name map to set.
   */
  public void setRequiredAddressPropertyNames(String[] pRequiredAddressPropertyNames) {
    if (pRequiredAddressPropertyNames == null) {
      mRequiredAddressPropertyNames = new String[0];
    }
    else {
      mRequiredAddressPropertyNames = pRequiredAddressPropertyNames;
    }
  }

  /**
   * property: storeOrderTools
   */
  private StoreOrderTools mStoreOrderTools;

  /**
   * @return the Store order tools property.
   */
  public StoreOrderTools getStoreOrderTools() {
    return mStoreOrderTools;
  }

  /**
   * @param pStoreOrderTools - the Store order tools property.
   */
  public void setStoreOrderTools(StoreOrderTools pStoreOrderTools) {
    mStoreOrderTools = pStoreOrderTools;
  }

  /**
   * property: storeConfiguration
   */
  private StoreConfiguration mStoreConfiguration;

  /**
   * @return the store configuration.
   */
  public StoreConfiguration getStoreConfiguration() {
    return mStoreConfiguration;
  }

  /**
   * @param pStoreConfiguration - the store configuration to set.
   */
  public void setStoreConfiguration(StoreConfiguration pStoreConfiguration) {
    mStoreConfiguration = pStoreConfiguration;
  }

  /**
   * property: pricingTools
   */
  private PricingTools mPricingTools;

  /**
   * @param pPricingTools - pricing tools.
   */
  public void setPricingTools(PricingTools pPricingTools) {
    mPricingTools = pPricingTools;
  }

  /**
   * @return mPricingTools - pricing tools.
   */
  public PricingTools getPricingTools() {
    return mPricingTools;
  }
  
  /**
   * property: placeUtils
   */
  PlaceUtils mPlaceUtils;

  /**
   * @return Utility methods class for PlaceList handling.
   */
  public PlaceUtils getPlaceUtils() {
    return mPlaceUtils;
  }

  /**
   * @param pPlaceUtils - Utility methods class for PlaceList handling.
   */
  public void setPlaceUtils(PlaceUtils pPlaceUtils) {
    mPlaceUtils = pPlaceUtils;
  }
  
  /**
   * property: claimableManager
   */
  private ClaimableManager mClaimableManager;

  /**
   * @return the claimable manager.
   */
  public ClaimableManager getClaimableManager()
  {
    return mClaimableManager;
  }

  /**
   * @param pClaimableManager - the claimable manager to set.
   */
  public void setClaimableManager(ClaimableManager pClaimableManager)
  {
    mClaimableManager = pClaimableManager;
  }

  /**
   * Get mandatory state country list.
   *
   * @param pRequest - HTTP request.
   *
   * @return mandatory state country list.
   */
  protected List getMandatoryStateCountryList(DynamoHttpServletRequest pRequest) {
    return getStoreConfiguration().getMandatoryStateCountryList();
  }

  /**
   * Validates the required billing properties.
   *
   * @param pContactInfo - Contact information.
   * @param pRequest - HTTP request.
   * 
   * @return a list of required properties that are missing from the ContactInfo.
   */
  public List checkForRequiredAddressProperties(ContactInfo pContactInfo) {
    // You won't always have a repository item here, so the usual
    // getPropertyValue(<property_name>) won't work. To overcome this,
    // we will use constant property names.
    List missingRequiredAddressProperties = new ArrayList();
    List requiredAddressPropertyNames = Arrays.asList(getRequiredAddressPropertyNames());

    try {
      if (requiredAddressPropertyNames.contains(FIRST_NAME_PROP_NAME) && 
          StringUtils.isEmpty(pContactInfo.getFirstName())) {
        missingRequiredAddressProperties.add(FIRST_NAME_PROP_NAME);
      }

      if (requiredAddressPropertyNames.contains(LAST_NAME_PROP_NAME) && 
          StringUtils.isEmpty(pContactInfo.getLastName())) {
        missingRequiredAddressProperties.add(LAST_NAME_PROP_NAME);
      }

      if (requiredAddressPropertyNames.contains(ADDRESS_PROP_NAME) && 
          StringUtils.isEmpty(pContactInfo.getAddress1())) {
        missingRequiredAddressProperties.add(ADDRESS_PROP_NAME);
      }

      if (requiredAddressPropertyNames.contains(CITY_PROP_NAME) && 
          StringUtils.isEmpty(pContactInfo.getCity())) {
        missingRequiredAddressProperties.add(CITY_PROP_NAME);
      }

      if (requiredAddressPropertyNames.contains(COUNTRY_PROP_NAME) && 
          StringUtils.isEmpty(pContactInfo.getCountry())) {
        missingRequiredAddressProperties.add(COUNTRY_PROP_NAME);
      }

      if (requiredAddressPropertyNames.contains(STATE_PROP_NAME) && 
          StringUtils.isEmpty(pContactInfo.getState())) {
        if( pContactInfo.getCountry()== null || getPlaceUtils().getPlaces(pContactInfo.getCountry())!=null) {
          missingRequiredAddressProperties.add(STATE_PROP_NAME);  
        }        
      }

      if (requiredAddressPropertyNames.contains(POSTAL_CODE_PROP_NAME) && 
          StringUtils.isEmpty(pContactInfo.getPostalCode())) {
        missingRequiredAddressProperties.add(POSTAL_CODE_PROP_NAME);
      }

      if (requiredAddressPropertyNames.contains(PHONE_NUMBER_PROP_NAME) && 
          StringUtils.isEmpty(pContactInfo.getPhoneNumber())) {
        missingRequiredAddressProperties.add(PHONE_NUMBER_PROP_NAME);
      }
    } 
    catch (Exception e) {
      if (isLoggingError()) {
        logError(LogUtils.formatMinor("Error getting message: "), e);
      }
    }

    return missingRequiredAddressProperties;
  }

  /**
   * Logic to re-price order, and parse any errors.
   *
   * @param pOrder - the order to price.
   * @param pUserLocale - the locale of the user, may be null.
   * @param pProfile - the user, may be null.
   * @param pUserPricingModels - the PricingModelHolder is an object which contains all the
   *                             pricing models associated with a user (i.e. item, shipping, 
   *                             order and tax).
   * 
   * @exception PricingException if there was an error while computing the pricing information.
   */
  public void repriceOrder(Order pOrder,
                           PricingModelHolder pUserPricingModels,
                           Locale pUserLocale,
                           RepositoryItem pProfile)
    throws PricingException {

    try {

      if (isLoggingDebug()) {
        logDebug("Repricing w/ pricing tools (priceOrderTotal.");
      }

      // Need to do priceOrderTotal here to catch errors in the shipping address.
      // CyberSource is the only means of doing city/state/zip validation, so use them here and
      // do tax calculation to make sure city/state/zip is valid.
      getPricingTools().performPricingOperation(PricingConstants.OP_REPRICE_ORDER_TOTAL,
                                                pOrder, pUserPricingModels,
                                                pUserLocale, pProfile, null);
    }
    catch (PricingException pe) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error w/ PricingTools.priceOrderTotal: "), pe);
      }
      throw pe;
    }
    catch (Exception e) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor(""), e);
      }
    }
  }

  /**
   * <p>
   *   Attempt to claim the specified coupon code for a specific user and order.
   * </p>
   * <br />
   * <p>
   *   If the tendered coupon code is empty then remove any existing coupon on the order. If the
   *   tendered coupon is not empty then check this is a valid claimable item before removing any
   *   existing coupon and claiming the tendered coupon. This avoids issues such as revoke promotion
   *   events if we simply remove the coupon and then roll-back when claiming the coupon fails.
   * </p>
   * 
   * @param pCouponCode - coupon code to be claimed.
   * @param pOrder - order to be re-priced when the coupon has been claimed.
   * @param pProfile - user who claims a coupon.
   * @param pUserPricingModels - user's pricing models to be used for order re-price process.
   * @param pUserLocale - user's locale to be used when re-pricing order.
   *
   * @return true if the coupon has been successfully removed or tendered/claimed; otherwise false.
   *
   * @throws CommerceException if an error occurred during claiming the coupon.
   * @throws IllegalArgumentException indicate that a method has been passed an illegal or inappropriate argument.
   */
  public boolean tenderCoupon(String pCouponCode, 
                              StoreOrderImpl pOrder, 
                              RepositoryItem pProfile,
                              PricingModelHolder pUserPricingModels, 
                              Locale pUserLocale)
    throws CommerceException, IllegalArgumentException {
    
    TransactionDemarcation td = new TransactionDemarcation();
    StoreOrderManager orderManager = (StoreOrderManager) getOrderManager();
    String currentCouponCode = orderManager.getCouponCode(pOrder);
    String tenderedCouponCode = pCouponCode;

    boolean removeCouponOnly = false;
    boolean rollback = true;
    boolean canClaimTenderedCoupon = false;

    // If this coupon has already been claimed just return.
    if (!StringUtils.isEmpty(tenderedCouponCode) && tenderedCouponCode.equals(currentCouponCode)) {
      return true;
    }
    
    // An empty coupon indicates we want to remove the currently applied coupon.
    if (StringUtils.isEmpty(tenderedCouponCode)) {
      removeCouponOnly = true;
    }
    
    // If there is no coupon applied and no coupon entered just return.
    if(removeCouponOnly && StringUtils.isEmpty(currentCouponCode)){
      return true;
    }

    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      // Remove Existing Coupon.
      if (!StringUtils.isEmpty(currentCouponCode)) {
        // No coupon to claim, only removing the existing one, so re-price the order now.
        if (removeCouponOnly) {
          removeCoupon(pOrder, pProfile, true, pUserPricingModels, pUserLocale);
        }
        // If there is a coupon to claim wait until claiming this before re-pricing the order.
        else {
          removeCoupon(pOrder, pProfile, false, pUserPricingModels, pUserLocale);
        }
      }

      // Claim Tendered Coupon.
      if (!removeCouponOnly) {
        canClaimTenderedCoupon = getClaimableManager().canClaimCoupon(pProfile.getRepositoryId(),
                                                                      tenderedCouponCode);
        // Claim the new coupon code if it is valid.
        if (canClaimTenderedCoupon) {
          claimCoupon(tenderedCouponCode, pOrder, pProfile, true, pUserPricingModels, pUserLocale);
        }
        // The new coupon is not valid, reclaim the old one.
        else {
          if (!StringUtils.isEmpty(currentCouponCode)) {
            claimCoupon(currentCouponCode, pOrder, pProfile, false, pUserPricingModels, pUserLocale);
          }
        }
      } 

      getOrderManager().updateOrder(pOrder);

      rollback = false;

      return (removeCouponOnly || canClaimTenderedCoupon);
    }
    catch (Exception exception) {
      throw new CommerceException(exception);
    }
    finally {
      try {
        td.end(rollback);
      }
      catch (TransactionDemarcationException transactionDemarcationException) {
        throw new CommerceException(transactionDemarcationException);
      }
    }
  }

  /**
   * This method claims a coupon specified by its code for a specific user and order and reprices the order.
   *
   * @param pCouponCode - coupon code to be claimed.
   * @param pOrder - order to be re-priced when the coupon has been claimed.
   * @param pProfile - user who claims a coupon.
   * @param pUserPricingModels - user's pricing models to be used for order reprice process.
   * @param pUserLocale - user's locale to be used when re-pricing order.
   * 
   * @throws CommerceException - if something goes wrong.
   * @throws IllegalArgumentException - if order has a claimed coupon already.
   */
  public void claimCoupon(String pCouponCode,
                          StoreOrderImpl pOrder,
                          RepositoryItem pProfile,
                          PricingModelHolder pUserPricingModels,
                          Locale pUserLocale)
    throws CommerceException, IllegalArgumentException {
    
    claimCoupon(pCouponCode, pOrder, pProfile, true, pUserPricingModels, pUserLocale);
  }

  /**
   * This method claims a coupon specified by its code for a specific user and order.
   * The order is re-priced if the 'pRepriceOrder' parameter is true.
   *
   * @param pCouponCode - coupon code to be claimed.
   * @param pOrder - order to be re-priced when the coupon has been claimed.
   * @param pProfile - user who claims a coupon.
   * @param pRepriceOrder - boolean flag to indicate if order should be re-priced.
   * @param pUserPricingModels - user's pricing models to be used for order re-price process.
   * @param pUserLocale - user's locale to be used when re-pricing order.
   * 
   * @throws CommerceException - if something goes wrong.
   * @throws IllegalArgumentException - if order has a claimed coupon already.
   */
  public void claimCoupon(String pCouponCode,
                          StoreOrderImpl pOrder,
                          RepositoryItem pProfile,
                          boolean pRepriceOrder,
                          PricingModelHolder pUserPricingModels,
                          Locale pUserLocale)
    throws CommerceException, IllegalArgumentException {
   
    // First, check the coupon code to be used.
    if (StringUtils.isBlank(pCouponCode)) {
      return;
    }
    
    StoreOrderManager orderManager = (StoreOrderManager) getOrderManager();

    // Then check if the order specified has a claimed coupon already; if true, throw an exception.
    if (!StringUtils.isEmpty(orderManager.getCouponCode(pOrder))) {
      throw new IllegalArgumentException("There is a coupon claimed for order specified!");
    }

    // And after that, claim a coupon.
    TransactionDemarcation td = new TransactionDemarcation();
    // We should rollback transaction, if any exception occurs.
    boolean shouldRollback = true; 

    try {
      td.begin(getTransactionManager());
      
      getClaimableManager().claimCoupon(pProfile.getRepositoryId(), pCouponCode);

      if (pRepriceOrder) {
        
        // Initialize pricing models in order to use recently claimed coupon's promotions.
        pUserPricingModels.initializePricingModels();

        // Re-price order in order to display most recent prices for items etc.
        repriceOrder(pOrder, pUserPricingModels, pUserLocale, pProfile);
      }

      shouldRollback = false;
    }
    catch (ClaimableException e) {
      // Propagate only exceptions that are not 'Duplicate promotion for current user'; 
      // this exact exception should be suppressed!
      if (e.getCause() instanceof DuplicatePromotionException) {
        return;
      }
      throw e;
    }
    catch (TransactionDemarcationException e) {
      throw new CommerceException(e);
    } 
    finally {
      // Only commit or roll-back transaction if we have created it. If someone has called 
      // this method, then they should handle it.
      try {
        td.end(shouldRollback);
      } 
      catch (TransactionDemarcationException e)
      {
        throw new CommerceException(e);
      }
    }
  }

  /**
   * This method removes a coupon from the order specified and re-prices the order.
   *
   * @param pOrder - order with coupon claimed.
   * @param pProfile - user who removes a coupon.
   * @param pUserPricingModels - user's pricing models to be used in order re-pricing process.
   * @param pUserLocale - user's locale to be used when re-pricing order.
   * 
   * @throws CommerceException - if something goes wrong.
   */
  public void removeCoupon(StoreOrderImpl pOrder,
                           RepositoryItem pProfile,
                           PricingModelHolder pUserPricingModels,
                           Locale pUserLocale)
    throws CommerceException {
    
    removeCoupon(pOrder, pProfile, true, pUserPricingModels, pUserLocale);
  }


  /**
   * This method removes a coupon from the order specified.
   * The order is re-priced if the 'pRepriceOrder' parameter is true.

   * @param pOrder - order with coupon claimed.
   * @param pProfile - user who removes a coupon.
   * @param pRepriceOrder - boolean flag to indicate if order should be re-priced.
   * @param pUserPricingModels - user's pricing models to be used in order re-pricing process.
   * @param pUserLocale - user's locale to be used when re-pricing order.
   * 
   * @throws CommerceException - if something goes wrong.
   */
  public void removeCoupon(StoreOrderImpl pOrder,
                           RepositoryItem pProfile,
                           boolean pRepriceOrder,
                           PricingModelHolder pUserPricingModels,
                           Locale pUserLocale)
    throws CommerceException {
    
    RepositoryItem profile = pProfile;

    StoreOrderManager orderManager = (StoreOrderManager) getOrderManager();
    String couponCode = orderManager.getCouponCode(pOrder);
    
    // If there's no couponCode, there's nothing to remove.
    if (couponCode == null) {
      return;
    }
    
    TransactionDemarcation td = new TransactionDemarcation();
    boolean shouldRollback = true; // We should roll-back transaction, if any exception occurs.

    try {
      td.begin(getTransactionManager());
      
      // Get and remove all coupon's promotions from order.
      RepositoryItem coupon = getClaimableManager().claimItem(couponCode);
      String promotionsPropertyName = getClaimableManager().getClaimableTools().getPromotionsPropertyName();
      
      //Ok to suppress because coupon.promotions contains a set of promotions (in form of repository items).
      @SuppressWarnings("unchecked") 
      Collection<RepositoryItem> promotions = 
        (Collection<RepositoryItem>) coupon.getPropertyValue(promotionsPropertyName);

      // Ensure profile to be a MutableRepositoryItem.
      if (!(profile instanceof MutableRepositoryItem)) {
        
        // Profile uses a MutableRepository for sure.
        profile = 
          ((MutableRepository) profile.getRepository()).getItemForUpdate(profile.getRepositoryId(),
          profile.getItemDescriptor().getItemDescriptorName());
      }

      for (RepositoryItem promotion: promotions) {
        // Now we can cast profile to the type we need
        getClaimableManager().getPromotionTools().
          removePromotion((MutableRepositoryItem) profile, promotion, false);
      }

      if (pRepriceOrder) {
        
        // Initialize pricing models to use current promotions, that is exclude coupon's 
        // promotions from pricing models.
        pUserPricingModels.initializePricingModels();

        // Re-price order to display most recent prices.
        repriceOrder(pOrder, pUserPricingModels, pUserLocale, profile);
      }
 
      shouldRollback = false;
    }
    catch (TransactionDemarcationException e) {
      throw new CommerceException(e);
    }
    catch (RepositoryException e) {
      throw new CommerceException(e);
    }
    finally {
      // Only commit or roll-back transaction if we have created it.
      // If someone's called this method, then they should handle it.
      try {
        td.end(shouldRollback);
      } 
      catch (TransactionDemarcationException e) {
        throw new CommerceException(e);
      }
    }
  }
}
