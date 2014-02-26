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

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderTools;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupManager;
import atg.commerce.order.purchase.CommerceItemShippingInfo;
import atg.commerce.order.purchase.CommerceItemShippingInfoContainer;
import atg.commerce.order.purchase.CommerceItemShippingInfoTools;
import atg.commerce.order.purchase.ShippingGroupMapContainer;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.profile.CommerceProfileTools;
import atg.commerce.profile.CommercePropertyManager;
import atg.core.i18n.PlaceList.Place;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.projects.store.catalog.StoreCatalogProperties;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.GiftWrapCommerceItem;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.profile.StoreProfileTools;
import atg.projects.store.profile.StorePropertyManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryUtils;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;

/**
 * Helper methods for handling shipping information
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreShippingProcessHelper.java#4 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreShippingProcessHelper extends StorePurchaseProcessHelper {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreShippingProcessHelper.java#4 $$Change: 788278 $";

  //---------------------------------------------------------------------------
  // Constants
  //---------------------------------------------------------------------------
  
  private static final String MY_RESOURCE_NAME = "atg.commerce.order.purchase.PurchaseProcessResources";
  private static java.util.ResourceBundle sResourceBundle = 
    atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, 
                                                  atg.service.dynamo.LangLicense.getLicensedDefault());
  
  public static final String NON_SHIPPABLE_COUNTRIES_PROPERTY_NAME = "nonShippableCountries";

  public static final String SHIPPABLE_COUNTRIES_PROPERTY_NAME = "shippableCountries";

  public static final String DISPLAY_NAME_PROPERTY_NAME = "displayName";
  
  protected static final String NICKNAME_SEPARATOR = ";;";  
  
  /**
   * New address constant.
   */
  public static final String NEW_ADDRESS = "NEW";
  
  /**
   * Country delimiter.
   */
  public static final String COUNTRY_DELIM = "|";

  /**
   * Invalid state for method message key.
   */
  public static final String MSG_INVALID_STATE_FOR_METHOD = "invalidStateForMethod";
  
  /**
   * Error message for incorrect state
   */
   protected static final String MSG_ERROR_INCORRECT_STATE = "stateIsIncorrect";

  /**
   * Restricted shipping message key.
   */
  public static final String MSG_RESTRICTED_SHIPPING = "restrictedShipping";

  /**
   * Duplicate nickname message key.
   */
  public static final String MSG_DUPLICATE_NICKNAME = "duplicateNickname";

  /**
   * Invalid city address message key.
   */
  public static final String MSG_INVALID_CITY_ADDRESS = "invalidCityAddress";

  /**
   * Invalid Street Address message key
   */
  public static final String MSG_INVALID_STREET_ADDRESS = "invalidStreetAddress";

  /**
   * Error updating shipping groups message key.
   */
  public static final String MSG_ERROR_UPDATE_SHIPPINGGROUP = "errorUpdateShippingGroup";
  
  /**
   * Error messages for logging
   */
  public static final String MSG_ERROR_CREATING_SHIPPINGGROUP_FOR_DEFAULT_ADDRESS = 
    "errorCreatingShippingGroupForDefaultAddress";
  public static final String MSG_ERROR_CHANGING_SECONDARY_ADDRESS_NAME = 
    "errorChangingSecondaryAddressName";
  public static final String MSG_ERROR_REMOVING_PROFILE_REPOSITORY_ADDRESS = 
    "errorRemovingProfileRepositoryAddress";
  public static final String MSG_ERROR_COPING_ADDRESS = "errorCopingAddress";
  
  //---------------------------------------------------------------------------
  // Properties
  //---------------------------------------------------------------------------

  //---------------------------------------------------------------------------
  // property: commerceItemShippingInfoTools
  //---------------------------------------------------------------------------
  private CommerceItemShippingInfoTools mCommerceItemShippingInfoTools;

  /**
   * Returns the tools component containing the API for modifying CommerceItemShippingInfos.
   * 
   * @return CommerceItemShippingInfoTools.
   */
  public CommerceItemShippingInfoTools getCommerceItemShippingInfoTools() {
    return mCommerceItemShippingInfoTools;
  }

  /**
   * @param pCommerceItemShippingInfoTools the Commerce Item Shipping Info Tools to set.
   */
  public void setCommerceItemShippingInfoTools(CommerceItemShippingInfoTools pCommerceItemShippingInfoTools) {
    mCommerceItemShippingInfoTools = pCommerceItemShippingInfoTools;
  }

  /**
   * property: invalidCityPatterns
   */
  private Pattern[] mInvalidCityPatterns;
 
  /**
   * @param pInvalidCityPatterns - Invalid city patterns.
   */
  protected void setInvalidCityPatterns(Pattern[] pInvalidCityPatterns) {
    mInvalidCityPatterns = pInvalidCityPatterns;
  }

  /**
   * @return mInvalidCityPatterns - Invalid city patterns.
   */
  protected Pattern[] getInvalidCityPatterns() {
    return mInvalidCityPatterns;
  }

  /**
   * @param pInvalidCityStrings - invalid city strings.
   */
  public void setInvalidCityStrings(String[] pInvalidCityStrings) {
    ArrayList patterns = new ArrayList();

    for (int i = 0; i < pInvalidCityStrings.length; i++) {
      String regexString = pInvalidCityStrings[i];
      Pattern pattern = Pattern.compile(regexString);
      patterns.add(pattern);
    }

    mInvalidCityPatterns = (Pattern[]) patterns.toArray(new Pattern[patterns.size()]);
  }

  /**
   * property: invalidStreetStrings
   */
  private String[] mInvalidStreetStrings;
  
  /**
   * @return invalid streets strings.
   */
  public String[] getInvalidStreetStrings() {
    return mInvalidStreetStrings;
  }

  /**
   * @param pInvalidStreetStrings - invalid streets strings.
   */
  public void setInvalidStreetStrings(String[] pInvalidStreetStrings) {
    mInvalidStreetStrings = pInvalidStreetStrings;
  }

  /**
   * property: invalidStatesForShipMethod.
   */
  private Properties mInvalidStatesForShipMethod;

  /**
   * Expects a pipe-delimited string containing 2-digit state abbreviations
   * that are invalid for a particular shipping method. The key is the name of
   * the shipping method the value is the pipe-delimited list of states.
   *
   * @param pInvalidStatesForShipMethod - invalid states for shipping methods.
   */
  public void setInvalidStatesForShipMethod(Properties pInvalidStatesForShipMethod) {
    mInvalidStatesForShipMethod = pInvalidStatesForShipMethod;
  }

  /**
   * Expects a pipe-delimited string containing 2-digit state abbreviations
   * that are invalid for a particular shipping method. The key is the name of
   * the shipping method the value is the pipe-delimited list of states.
   *
   * @return invalid states for shipping method.
   */
  public Properties getInvalidStatesForShipMethod() {
    return mInvalidStatesForShipMethod;
  }

  /**
   * property: validateShippingRestriction
   */
  private boolean mValidateShippingRestriction;

  /**
   * @return true if shipping restrictions should be validated, otherwise false.
   */
  public boolean isValidateShippingRestriction() {
    return mValidateShippingRestriction;
  }

  /**
   * @param pValidateShippingRestriction - true if shipping restrictions should be validated.
   */
  public void setValidateShippingRestriction(boolean pValidateShippingRestriction) {
    mValidateShippingRestriction = pValidateShippingRestriction;
  }

  /**
   * property: validateShippGroups
   */
  protected boolean mValidateShippingGroups = true;

  /**
   * @param pValidateShippingGroups - flag indicating if the shipping groups should be validated 
   *                                  after being applied to the order. The default setting is true.
   */
  public void setValidateShippingGroups(boolean pValidateShippingGroups) {
    mValidateShippingGroups = pValidateShippingGroups;
  }

  /**
   * @return flag indicating if the shipping groups should be validated after being applied to the order. 
   *         The default setting is true.
   */
  public boolean isValidateShippingGroups() {
    return mValidateShippingGroups;
  }

  /**
   * property: ValidateShippingGroupsChainId
   */
  String mValidateShippingGroupsChainId;

  /**
   * Set the ValidateShippingGroupsChainId property.
   * 
   * @param pValidateShippingGroupsChainId a <code>String</code> value.
   * 
   * @beaninfo description: ChainId to execute for validating ShippingGroups.
   */
  public void setValidateShippingGroupsChainId(String pValidateShippingGroupsChainId) {
    mValidateShippingGroupsChainId = pValidateShippingGroupsChainId;
  }

  /**
   * Return the ValidateShippingGroupsChainId property.
   * 
   * @return a <code>String</code> value.
   */
  public String getValidateShippingGroupsChainId() {
    return mValidateShippingGroupsChainId;
  }

  /**
   * property: consolidateShippingInfos
   */
  protected boolean mConsolidateShippingInfosBeforeApply = false;
  
  /**
   * <p>
   *   This property is used to control is the CommerceItemShippingInfo objects are
   *   consolidated prior to being applied to the order.
   * <p>
   * 
   * @return the consolidateShippingInfosBeforeApply flag.
   */
  public boolean isConsolidateShippingInfosBeforeApply() {
    return mConsolidateShippingInfosBeforeApply;
  }

  /**
   * <p>
   *   Sets the consolidateShippingInfosBeforeApply property.
   * </p>
   * <br /> 
   * <p>
   *   This property is used to control is the CommerceItemShippingInfo objects are
   *   consolidated prior to being applied to the order.
   * <p>
   * 
   * @see #consolidateShippingInfos(atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
   * @param pConsolidateShippingInfosBeforeApply The consolidateShippingInfosBeforeApply to set.
   */
  public void setConsolidateShippingInfosBeforeApply(boolean pConsolidateShippingInfosBeforeApply) {
    mConsolidateShippingInfosBeforeApply = pConsolidateShippingInfosBeforeApply;
  }

  /**
   * property: ApplyDefaultShippingGroup
  */
  boolean mApplyDefaultShippingGroup;

  /**
   * Set the ApplyDefaultShippingGroup property.
   * 
   * @param pApplyDefaultShippingGroup a <code>boolean</code> value.
   * 
   * @beaninfo description: Should the default ShippingGroup apply?
   */
  public void setApplyDefaultShippingGroup(boolean pApplyDefaultShippingGroup) {
    mApplyDefaultShippingGroup = pApplyDefaultShippingGroup;
  }

  /**
   * Return the ApplyDefaultShippingGroup property.
   * 
   * @return a <code>boolean</code> value.
   */
  public boolean isApplyDefaultShippingGroup() {
    return mApplyDefaultShippingGroup;
  }

  /** 
   * property: catalogProperties.
   */
  protected StoreCatalogProperties mCatalogProperties;
  
  /** 
   * @param pCatalogProperties - the catalog properties.
   */
  public void setCatalogProperties(StoreCatalogProperties pCatalogProperties) {
    mCatalogProperties = pCatalogProperties;
  }

  /**
   * @return the catalog properties.
   */
  public StoreCatalogProperties getCatalogProperties() {
    return mCatalogProperties;
  }

  //---------------------------------------------------------------------------
  // Utility Methods
  //---------------------------------------------------------------------------
 
  /**
   * Set the profile's default shipping method if it's not already set.
   * 
   * @param pProfile - profile to update.
   * @param pShippingMethod - shipping method.
   */
  public void saveDefaultShippingMethod(RepositoryItem pProfile, String pShippingMethod) {
    getStoreOrderTools().saveDefaultShippingMethod(pProfile, pShippingMethod);
  }

  /**
   * Saves address to address book.
   * 
   * @param pAddress - address Address to save.
   * @param pNickName - nickname - Nickname for the address being saved.
   * @param pProfile - the profile.
   * 
   * @throws CommerceException indicates that a severe error occurred while performing a commerce operation.
   */
  public void saveAddressToAddressBook(Address pAddress, String pNickName, RepositoryItem pProfile)
    throws CommerceException {
    
    getStoreOrderTools().saveAddressToAddressBook(pProfile, pAddress, pNickName);
  }

  //---------------------------------------------------------------------------
  // Helper Methods
  //---------------------------------------------------------------------------

  /**
   * <code>runProcessValidateShippingGroups</code> runs a configurable Pipeline chain
   * to validate ShippingGroups or prepare for the next checkout phase.
   *
   * @param pOrder - an <code>Order</code> value.
   * @param pPricingModels - a <code>PricingModelHolder</code> value.
   * @param pLocale - a <code>Locale</code> value.
   * @param pProfile - a <code>RepositoryItem</code> value.
   * @param pExtraParameters - a <code>Map</code> value.
   * 
   * @return a PipelineResult value.
   * 
   * @exception atg.service.pipeline.RunProcessException if an error occurs.
   */
  protected PipelineResult runProcessValidateShippingGroups(Order pOrder, 
                                                            PricingModelHolder pPricingModels,
                                                            Locale pLocale, 
                                                            RepositoryItem pProfile, 
                                                            Map pExtraParameters)
    throws RunProcessException {
    
    return runProcess(getValidateShippingGroupsChainId(), 
                      pOrder, 
                      pPricingModels, 
                      pLocale,
                      pProfile, pExtraParameters);
  }

  /**
  * Determines the shipping method in the following order of precedence: shipping group's
  * shipping method, the profile's default shipping method, and the default configured shipping method.
  *
  * @param pProfile - Shopper profile.
  * @param pShippingGroup - shipping group for the order.
  * @param pDefaultShippingMethod - the default configured shipping method.
  * 
  * @return shipping method being used for the shipping group.
  */
  public String initializeShippingMethod(RepositoryItem pProfile,
                                         ShippingGroup pShippingGroup,
                                         String pDefaultShippingMethod) {

    // If the shipping group has a configured shipping method use it.
    if (!StringUtils.isEmpty(pShippingGroup.getShippingMethod()) &&
        !pShippingGroup.getShippingMethod().equals(pShippingGroup.getShippingGroupClassType())) {

      return pShippingGroup.getShippingMethod();
    }

    // If the profile has a default shipping method use it.
    StoreOrderTools orderTools = getStoreOrderTools();
    StorePropertyManager pm = (StorePropertyManager) orderTools.getProfileTools().getPropertyManager();
    String defaultMethodName = pm.getDefaultShippingMethodPropertyName();
    String defaultMethod = (String) pProfile.getPropertyValue(defaultMethodName);

    if (!StringUtils.isEmpty(defaultMethod)) {
      return defaultMethod;
    }

    // Use the configured default shipping method.
    return pDefaultShippingMethod;
  }

  /**
   * Retrieve the collection of all hardgood shipping groups referenced by commerce item infos.
   *
   * @param pShippingGroupMapContainer - map of all shipping groups for the profile.
   * @param pCommerceItemShippingInfoContainer - map of all commerce item infos for the order.
   * 
   * @return collection of all hardgood shipping groups referenced by commerce item infos.
   */
  public Collection getUniqueHardgoodShippingGroups(ShippingGroupMapContainer pShippingGroupMapContainer,
                    CommerceItemShippingInfoContainer pCommerceItemShippingInfoContainer) {

    // Get the commerce item infos and compile a collection of all hardgood shipping groups they reference
    Collection hardgoodShippingGroups = new ArrayList();
    Set<String> addedShippingGroupIds = new HashSet<String>();
    Collection infos = pCommerceItemShippingInfoContainer.getAllCommerceItemShippingInfos();

    if (infos != null) {
      CommerceItemShippingInfo info;
      ShippingGroup sg;
      Iterator infoerator = infos.iterator();

      while (infoerator.hasNext()) {
        info = (CommerceItemShippingInfo) infoerator.next();
        sg = pShippingGroupMapContainer.getShippingGroup(info.getShippingGroupName());

        if (sg instanceof HardgoodShippingGroup && !addedShippingGroupIds.contains(sg.getId())) {
          hardgoodShippingGroups.add(sg);
          addedShippingGroupIds.add(sg.getId());
        }
      }
    }
    return hardgoodShippingGroups;
  }

  /**
   * Get the List of all the CommerceItemShippingInfos for hardgoods
   * from the CommerceItemShippingInfoMap. If a CommerceItemShippingInfo
   * has no shipping group, assume the item represents hardgoods.
   *
   * @param pShippingGroupMapContainer - map of all shipping groups for the profile.
   * @param pCommerceItemShippingInfoContainer - map of all commerce item infos for the order.
   * 
   * @return a <code>List</code> value.
   */
  public List getAllHardgoodCommerceItemShippingInfos(ShippingGroupMapContainer pShippingGroupMapContainer,
    CommerceItemShippingInfoContainer pCommerceItemShippingInfoContainer) {
    
    List hardgoodCommerceItemShippingInfos =
        pCommerceItemShippingInfoContainer.getAllCommerceItemShippingInfos();

    Iterator iter = hardgoodCommerceItemShippingInfos.iterator();
    
    while (iter.hasNext()) {
      CommerceItemShippingInfo info = (CommerceItemShippingInfo) iter.next();
      String nickname = info.getShippingGroupName();

      if ((nickname != null) && 
          !(pShippingGroupMapContainer.getShippingGroup(nickname) instanceof HardgoodShippingGroup)) {
        
        iter.remove();
      }
    }
    return hardgoodCommerceItemShippingInfos;
  }

  /**
   * This method is called when using multiple shipping groups. This method sets the
   * shipping group name for all the gift wrap commerce item infos in the map.
   * 
   * @param pCommerceItemShippingInfos - all the commerce item infos that will be applied to the order.
   */
  public void setGiftWrapItemShippingGroupInfos(List pCommerceItemShippingInfos) {
   
    //First we must find a non-gift wrap commerce item info and get the shipping 
    // group name to which it is pointing.
    Iterator cinfoiter = pCommerceItemShippingInfos.iterator();
    CommerceItemShippingInfo cInfo;
    String shippingGroupName = null;
    String shippingMethod = null;

    while (cinfoiter.hasNext() && (shippingGroupName == null)) {
      cInfo = (CommerceItemShippingInfo) cinfoiter.next();

      if (!(cInfo.getCommerceItem() instanceof GiftWrapCommerceItem)) {
        
        // Retrieve the item's catalog reference so we can determine whether it is eligible for
        // gift wrapping  or not. If not, we will continue to the next iteration item until we find
        // one that is eligible. We will then will use this item's shipping group to add the gift wrap to.
        RepositoryItem sku = (RepositoryItem) cInfo.getCommerceItem().getAuxiliaryData().getCatalogRef();

        if (sku != null) {
          boolean isGiftWrapEligible = 
            (Boolean) sku.getPropertyValue(this.getCatalogProperties().getGiftWrapEligiblePropertyName());
          
          if (!isGiftWrapEligible) {
            if (isLoggingDebug()) {
              logDebug("SKU: " + sku.getRepositoryId() + " is not eligible for gift wrapping.");
            }
            
            continue;
          }
        }
        else {
          // This should never happen.
          if (isLoggingWarning()) {
            logWarning("There is a commerce item without a SKU, " + " Commerce item id: " + 
              cInfo.getCommerceItem().getId() + " with catalog ref id: " + cInfo.getCommerceItem().getCatalogRefId());
          }   

          continue;
        }

        if (isLoggingDebug()) {
          logDebug("SKU: " + sku.getRepositoryId() + " is eligible for gift wrapping. Using this item's " +
            " shipping group to add the gift wrap item to (if a gift wrap item exists).");
        }
        
        shippingGroupName = cInfo.getShippingGroupName();
        shippingMethod = cInfo.getShippingMethod();
      }
    }

    // If we find a shipping group name, look through again and set any gift wrap items to that shipping group.
    if (shippingGroupName != null) {
      cinfoiter = pCommerceItemShippingInfos.iterator();

      while (cinfoiter.hasNext()) {
        cInfo = (CommerceItemShippingInfo) cinfoiter.next();
        
        if (cInfo.getCommerceItem() instanceof GiftWrapCommerceItem) {
          cInfo.setShippingGroupName(shippingGroupName);
          cInfo.setShippingMethod(shippingMethod);
        }
      }
    }
    else {
      if (isLoggingDebug()) {
        logDebug("There are no shipping groups containing an item that is eligible for gift wrapping.");
      }
    }
  }

  /**
   * Creates a HardgoodShippingGroup from the profile's default shipping address item.
   *
   * @param pProfile - shopper profile.
   * 
   * @return  hardgood shipping group.
   */
  public HardgoodShippingGroup createShippingGroupFromDefaultAddress(RepositoryItem pProfile) {

    StoreProfileTools profileTools = (StoreProfileTools) getStoreOrderTools().getProfileTools();
    RepositoryItem item = profileTools.getDefaultShippingAddress(pProfile);

    if (item != null) {
      try {
        HardgoodShippingGroup hgsg = (HardgoodShippingGroup) getShippingGroupManager().createShippingGroup();

        if (hgsg != null) {
          Address address = profileTools.getAddressFromRepositoryItem(item);
          hgsg.setShippingAddress(address);

          return hgsg;
        }
      } 
      catch (CommerceException e) {
        if (isLoggingError()) {          
          logError(ResourceUtils.getMsgResource(MSG_ERROR_CREATING_SHIPPINGGROUP_FOR_DEFAULT_ADDRESS,
                                                MY_RESOURCE_NAME, sResourceBundle), 
                                                e);
        }
      } 
      catch (RepositoryException e) {
        if (isLoggingError()) {
          logError(ResourceUtils.getMsgResource(MSG_ERROR_CREATING_SHIPPINGGROUP_FOR_DEFAULT_ADDRESS,
                                                MY_RESOURCE_NAME, sResourceBundle), 
                                                e);
        }
      }
    }
    return null;
  }

  /**
   * Changes the shipping group name and shipping method for a collection
   * of items in the CommerceItemShippingInfoContainer.
   * 
   * @param pCommerceItemShippingInfos - the collection of items to change.
   * @param pShippingGroupName - if this is not null, each info item's shipping group name is set to this value.
   * @param pShippingMethod - if this is not null, each info item's shipping method is set to this value.
   */
  public void changeShippingGroupForCommerceItemShippingInfos(Collection pCommerceItemShippingInfos,
                                                              String pShippingGroupName,
                                                              String pShippingMethod) {
    
    Iterator commerceItemShippingInfoerator = pCommerceItemShippingInfos.iterator();

    while (commerceItemShippingInfoerator.hasNext()) {
      CommerceItemShippingInfo cisi = (CommerceItemShippingInfo) commerceItemShippingInfoerator.next();

      if (pShippingGroupName != null) {
        cisi.setShippingGroupName(pShippingGroupName);
      }

      if (pShippingMethod != null) {
        cisi.setShippingMethod(pShippingMethod);
      }
    }
  }

  /**
   * Tries to find an appropriate hardgood shipping group within the shipping group map container specified.
   * Shipping group is good enough if it has the same shipping address as the address specified or if
   * it has empty address and the same name as shipping group that should be added.
   * If such shipping group can't be found, this method will create a new one.
   * 
   * @param pProfile - current user's profile.
   * @param pNewShipToAddressName - nickname for the new shipping group.
   * @param pAddress - address to be used.
   * @param pShippingGroupMapContainer - an instance of ShippingGroupContainerService.
   * @param pSaveShippingAddress - true if this address should be saved into profile specified.
   * 
   * @return shipping group name if found and null if new shipping group has been created.
   * 
   * @throws CommerceException if something goes wrong.
   * @throws IntrospectionException if something goes wrong.
   */
  @SuppressWarnings("unchecked") //OK to suppress, we know which Map we are using
  public void findOrAddShippingGroupByNickname(RepositoryItem pProfile, 
                                               String pNewShipToAddressName, 
                                               Address pAddress,
                                               ShippingGroupMapContainer pShippingGroupMapContainer, 
                                               boolean pSaveShippingAddress) 
    throws CommerceException, IntrospectionException {
    
    // Check if shipping group with such name already exists.
    ShippingGroup shippingGroup = 
      ((Map<String, ShippingGroup>) pShippingGroupMapContainer.getShippingGroupMap()).get(pNewShipToAddressName);
    
    if (shippingGroup instanceof HardgoodShippingGroup) {
      
      Address shippingGroupAddress = ((HardgoodShippingGroup) shippingGroup).getShippingAddress();
      
      if (shippingGroupAddress != null) {
        if (shippingGroupAddress.getCountry() == null){ 
          ((HardgoodShippingGroup) shippingGroup).setShippingAddress(pAddress);
          return;
        }
      }
    }
    
    addShippingAddress(pProfile, 
                       pNewShipToAddressName, 
                       pAddress, 
                       pShippingGroupMapContainer, 
                       pSaveShippingAddress);
  }
  
  /**
   * <p>
   *   Creates a new shipping group and adds it to the shipping group map container.
   * </p>
   * <p>
   *   Optionally saves the shipping group address to the profile based on the saveShippingAddress option.
   * </p>  
   *
   * @param pAddress - Address to add.
   * @param pNewShipToAddressName - Address nickname to use for the address being added.
   * @param pProfile - shopper profile.
   * @param pShippingGroupMapContainer - map of all shipping groups for the profile.
   * @param pSaveShippingAddress - true if this address should be saved into profile specified.
   * 
   * @throws CommerceException  indicates that a severe error occurred while performing a commerce operation.
   */
  public void addShippingAddress(RepositoryItem pProfile,
                                 String pNewShipToAddressName,
                                 Address pAddress,
                                 ShippingGroupMapContainer pShippingGroupMapContainer,
                                 boolean pSaveShippingAddress)
    throws CommerceException {

    ShippingGroupManager sgm = getShippingGroupManager();
    Map shippingGroupMap = pShippingGroupMapContainer.getShippingGroupMap();

    // Create a new shipping group with the new address.
    HardgoodShippingGroup hgsg = (HardgoodShippingGroup) sgm.createShippingGroup();
    hgsg.setShippingAddress(pAddress);

    String newShipToAddressName = 
      StringUtils.isBlank(pNewShipToAddressName) ? 
        getUniqueShippingAddressNickname(pAddress, pProfile, pShippingGroupMapContainer, null) : 
        pNewShipToAddressName;

    if (shippingGroupMap.containsKey(newShipToAddressName)) {
      throw new StorePurchaseProcessException(MSG_DUPLICATE_NICKNAME);
    }

    // Put the new shipping group in the container.
    pShippingGroupMapContainer.addShippingGroup(newShipToAddressName, hgsg);

    if (pSaveShippingAddress) {
      saveAddressToAddressBook(pAddress, newShipToAddressName, pProfile);
    } 
    else {
      // if we do not save shipping address into profile, we need to save address in container 
      // to avoid issues with missed nicknames. The problem is that in ShippingGroupDroplet we 
      // perform cleanup and re-initialize ShippingGroupMapContainer based on profile and order 
      // information. If the address isn't in the profile, then it takes it from the order where 
      // the nickname for the address doesn't exist.
      //
      // See CRS-164187 for details.
      try {
        StoreShippingGroupContainerService storeShippingGroupContainerService = 
          (StoreShippingGroupContainerService) pShippingGroupMapContainer;
        
        storeShippingGroupContainerService.
          getNonProfileShippingAddressesMap().put(newShipToAddressName, pAddress);
      }
      catch (ClassCastException cce) {
        if (isLoggingError()) {
          logError(cce);
        }
      }
    }
  }
  
  /**
   * This method returns a unique address nickname. The method checks for existing nicknames 
   * in the profile and shipping group map container. If the specified nickname already exists,
   * the "##{number}" suffix is added to the nickname to qualify the unique string where {number} 
   * is some number.
   * 
   * If no new nickname is passed to the method, it uses default address prefix.
   * 
   * @param pAddress - shipping address.
   * @param pProfile - shopper profile.
   * @param pShippingGroupMapContainer - map of all shipping groups for the profile.
   * @param pNewShipToAddressName - Address nickname to use for the address being added.
   * 
   * @return unique nickname for shipping address.
   */
  public String getUniqueShippingAddressNickname(Address pAddress,
                                                 RepositoryItem pProfile,
                                                 ShippingGroupMapContainer pShippingGroupMapContainer,
                                                 String pNewShipToAddressName){
  
    CommerceProfileTools profileTools = getStoreOrderTools().getProfileTools();
  
    // Get shipping addresses nicknames from profile.
    Collection nicknames = profileTools.getProfileAddressNames(pProfile);
   
    // Also add nicknames from shipping group map container as not all addresses
    // can be stored in the profile.
    nicknames.addAll(pShippingGroupMapContainer.getShippingGroupNames());

    return profileTools.getUniqueAddressNickname (pAddress, nicknames, pNewShipToAddressName);
  }

  /**
   * Edits a shipping group address in the container. 
   *
   * @param pEditShippingAddressNickName - Nickname for the address to be modified.
   * @param pShippingGroupMapContainer - map of all shipping groups for the profile.
   * @param pEditAddress - Address to be modified.
   */
   public void modifyShippingAddress(String pEditShippingAddressNickName,
                                     Address pEditAddress,
                                     ShippingGroupMapContainer pShippingGroupMapContainer)  {

    HardgoodShippingGroup hgsg = (HardgoodShippingGroup) 
      pShippingGroupMapContainer.getShippingGroup(pEditShippingAddressNickName);
    
    if (hgsg != null) {
      hgsg.setShippingAddress(pEditAddress);
    }
  }
  
  /**
   * Modifies shipping address nickname.
   * 
   * @param pProfile - shopper profile
   * @param pEditShippingAddressNickName - Address Nickname to be modified
   * @param pShippingAddressNewNickName - New Address Nickname
   * @param pShippingGroupMapContainer - map of all shipping groups for the profile
   */
  public void modifyShippingAddressNickname(RepositoryItem pProfile,
                                            String pEditShippingAddressNickName,
                                            String pShippingAddressNewNickName,
                                            ShippingGroupMapContainer pShippingGroupMapContainer) {
    
    HardgoodShippingGroup hgsg = (HardgoodShippingGroup) 
      pShippingGroupMapContainer.getShippingGroup(pEditShippingAddressNickName);
    
    // Remove the shipping group from the container.
    pShippingGroupMapContainer.removeShippingGroup(pEditShippingAddressNickName);
    
    // Put the shipping group in the container under new nickname.
    pShippingGroupMapContainer.addShippingGroup(pShippingAddressNewNickName, hgsg);
    
    // Change address nickname in the profile's addresses map if it contains this address.
    modifyAddressBookNickname(pProfile, pEditShippingAddressNickName, pShippingAddressNewNickName);
  }
  
  /**
   * Modifies the address nick name is in the profile's map.
   * 
   * @param pProfile - shopper profile.
   * @param pEditShippingAddressNickName - Address Nickname to be modified.
   * @param pShippingAddressNewNickName - Address New Nickname.
   */
  public void modifyAddressBookNickname(RepositoryItem pProfile,
                                        String pEditShippingAddressNickName,
                                        String pShippingAddressNewNickName) {
    
    StoreProfileTools profileTools = (StoreProfileTools) getOrderManager().getOrderTools().getProfileTools();
    
    try {
      profileTools.changeSecondaryAddressName(pProfile, 
                                              pEditShippingAddressNickName, 
                                              pShippingAddressNewNickName);
    } 
    catch (RepositoryException ex) {
      if (isLoggingError()){
        logError(ResourceUtils.getMsgResource(MSG_ERROR_CHANGING_SECONDARY_ADDRESS_NAME,
                                              MY_RESOURCE_NAME, sResourceBundle), 
                                              ex);
      }
    }
  }

  /**
  * saveModifiedShippingAddress shipping address processing. If the address nick name is in the profile's map,
  * the updates are applied to that address too.
  *
  * @param pEditShippingAddressNickName - Nickname for the address to be modified.
  * @param pProfile - shopper profile.
  * @param pEditAddress - Address to be modified.
  */
  public void saveModifiedShippingAddressToProfile(RepositoryItem pProfile,
                                                   String pEditShippingAddressNickName,
                                                   Address pEditAddress) {

    StoreProfileTools profileTools = 
      (StoreProfileTools) getOrderManager().getOrderTools().getProfileTools();
    
    String secondaryAdressProperty = 
      ((CommercePropertyManager)profileTools.getPropertyManager()).getSecondaryAddressPropertyName();
    
    Map addresses = (Map) pProfile.getPropertyValue(secondaryAdressProperty);

    if (addresses.containsKey(pEditShippingAddressNickName)) {
      RepositoryItem address = profileTools.getProfileAddress(pProfile, pEditShippingAddressNickName);

      if (address != null) {
        try {
          MutableRepositoryItem mutAddress = RepositoryUtils.getMutableRepositoryItem(address);
          MutableRepository mutrep = (MutableRepository) mutAddress.getRepository();
          OrderTools.copyAddress(pEditAddress, mutAddress);
          mutrep.updateItem(mutAddress);
        }
        catch (Exception e) {
          if (isLoggingError()) {
            logError(ResourceUtils.getMsgResource(MSG_ERROR_COPING_ADDRESS,
                                                  MY_RESOURCE_NAME, sResourceBundle),
                                                  e);
          }
        }
      }
    }
  }
  
  /**
   * Removes a shipping group from the container.
   *
   * @param pRemoveShippingAddressNickName - Nickname for the address to be removed.
   * @param pShippingGroupMapContainer - map of all shipping groups for the profile.
   * @param pProfile - the profile.
   */
   public void removeShippingAddress(RepositoryItem pProfile,
                                     String pRemoveShippingAddressNickName,
                                     ShippingGroupMapContainer pShippingGroupMapContainer)  {

    HardgoodShippingGroup hgsg = (HardgoodShippingGroup) 
      pShippingGroupMapContainer.getShippingGroup(pRemoveShippingAddressNickName);
    
    if (hgsg != null) {
      pShippingGroupMapContainer.removeShippingGroup(pRemoveShippingAddressNickName);
    }  
  }
   
   /**
    * If the shipping address nickname is in the profile's addresses map, remove it from the profile.
    *
    * @param pProfile - shopper profile.
    * @param pRemoveShippingAddressNickName - Nickname for the address to be removed.
    */
    public void removeShippingAddressFromProfile(RepositoryItem pProfile,
                                                 String pRemoveShippingAddressNickName) {

      StoreProfileTools profileTools = (StoreProfileTools) 
        getOrderManager().getOrderTools().getProfileTools();
      
      String secondaryAdressProperty = 
        ((CommercePropertyManager)profileTools.getPropertyManager()).getSecondaryAddressPropertyName();
      
      Map addresses = (Map) pProfile.getPropertyValue(secondaryAdressProperty);

      if (addresses.containsKey(pRemoveShippingAddressNickName)) {
        try {
          profileTools.removeProfileRepositoryAddress(pProfile, pRemoveShippingAddressNickName, true);
        } 
        catch (RepositoryException ex) {
          if (isLoggingError()){
              logError(ResourceUtils.getMsgResource(MSG_ERROR_REMOVING_PROFILE_REPOSITORY_ADDRESS,
                                                    MY_RESOURCE_NAME, sResourceBundle), ex);
          }
        }
      }
    }
    
    /**
     * Loops through all CommerceItemShippingInfos and finds items that have
     * the same shipping address but different shipping methods then creates separate 
     * shipping groups for them. As a result all shipping groups that are referenced in
     * CommerceItemShippingInfos will have only one shipping method associated with them.
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value.
     * @param pResponse a <code>DynamoHttpServletResponse</code> value.
     * 
     * @return List of shippingGroupMap ids that were created.
     * 
     * @throws CommerceException indicates that a severe error occurred while performing a commerce operation.
     */
    public List<String> splitShippingGroupsByMethod(ShippingGroupMapContainer pShippingGroupMapContainer,
      List pAllHardgoodCommerceItemShippingInfos) throws CommerceException {
      
      // A list to store the names of the shipping groups that only differ by shipping method.
      List splitShippingGroups = new ArrayList<String>();
          
      ShippingGroupManager sgm = getShippingGroupManager();
      
      // helper map to store ship group nickname to ship method
      Map<String, String> shipGroupToMethod = new HashMap<String, String>();
      
      // helper map to store 'ship group nickname' + 'shipping method' combination to shipping group
      Map<String, ShippingGroup> sgMethodToShipGroup = new HashMap<String, ShippingGroup>();

      Iterator iter = pAllHardgoodCommerceItemShippingInfos.iterator();

      // Loop through all CISIs to find shipping groups with the same address
      // but different shipping methods.
      while (iter.hasNext()) {
        
        CommerceItemShippingInfo cisi = (CommerceItemShippingInfo) iter.next();
        String shipGroupName = cisi.getShippingGroupName();
        String shippingMethod = cisi.getShippingMethod();
        String shipGroupMethodKey = (shipGroupName + NICKNAME_SEPARATOR + shippingMethod);

        // Get shipping group from container for this CISI
        HardgoodShippingGroup shippingGroup =
          (HardgoodShippingGroup)pShippingGroupMapContainer.getShippingGroup(shipGroupName);

        // Check if it's the first appearance of this shipping group.
        if (shipGroupToMethod.containsKey(shipGroupName)){
          
          // There was already CISI with the same shipping group
          // check if the shipping method is the same as in previous CISI
          if (!sgMethodToShipGroup.containsKey(shipGroupMethodKey)){
            
            // Shipping method is not the same as in previous CISI with this shipping address
            // clone shipping group and add it to the ShippingGroupMapContainer.
            ShippingGroup clonedShipGroup = sgm.cloneShippingGroup(shippingGroup);

            // Put it to the shipping group container and update CISI with the new shipping group.
            pShippingGroupMapContainer.addShippingGroup(shipGroupMethodKey, clonedShipGroup);
            cisi.setShippingGroupName(shipGroupMethodKey);
            splitShippingGroups.add(shipGroupMethodKey);
          }
          else {
            // There was already such a combination of shipping group and shipping method. Update shipping 
            // group of this CISI with the one that contains the same shipping method if needed.
            if (!shipGroupToMethod.get(shipGroupName).equals(shippingMethod)) {
              // Update shipping group of CISI with the one that contains the same shipping method.
              cisi.setShippingGroupName(shipGroupMethodKey);
            }
          }
        }
        else {
          // It's first occurrence of this shipping group add corresponding ship group + method 
          // combination to shipGroupToMethod map.
          shipGroupToMethod.put(shipGroupName, shippingMethod);
          sgMethodToShipGroup.put(shipGroupMethodKey, shippingGroup);
        }
      }
      
      return splitShippingGroups;
    }

  /**
   * <p>
   *   <code>applyShippingGroups</code> removes all non-gift ShippingGroups from
   *   the Order and then iterates over the supplied CommerceItemShippingInfos
   *   for each of the CommerceItems. Each CommerceItemShippingInfo is used
   *   to update the Order.
   * </p>
   * <p>
   *   If property <code>consolidateShippingInfosBeforeApply</code> is true, the commerce
   *   item shipping info objects are first consolidated by calling the <code>consolidateShippingInfos</code>
   *   method.
   * </p>
   * 
   * @param pShippingGroupMapContainer - map of all shipping groups for the profile.
   * @param pCommerceItemShippingInfoContainer - map of all commerce item infos for the order.
   * @param pOrder - Order whose shipping groups are to be saved.
   * 
   * @exception CommerceException if an error occurs.
   */
  public void applyShippingGroups(CommerceItemShippingInfoContainer pCommerceItemShippingInfoContainer,
                                  ShippingGroupMapContainer pShippingGroupMapContainer,
                                  Order pOrder)
    throws CommerceException {
    
    getCommerceItemShippingInfoTools().
      applyCommerceItemShippingInfos(pCommerceItemShippingInfoContainer,
                                     pShippingGroupMapContainer,
                                     pOrder, 
                                     isConsolidateShippingInfosBeforeApply(), 
                                     isApplyDefaultShippingGroup());
  }
 
  /**
   * Validates the shipping city against a collection of regular expressions.
   *
   * @param pShippingAddress shipping address to validate.
   * 
   * @throws StorePurchaseProcessException is an error occurs.
   */
  public void validateShippingCity(Address pShippingAddress) throws StorePurchaseProcessException {

    String city = pShippingAddress.getCity();

    if (city != null) {
      Pattern[] invalidCityPatterns = getInvalidCityPatterns();

      for (int i = 0; i < invalidCityPatterns.length; i++) {
        Pattern invalidCityPattern = invalidCityPatterns[i];

        if (isLoggingDebug()) {
          logDebug("Validating city \"" + city + "\" against regex \"" + invalidCityPattern.pattern() + "\"");
        }

        Matcher matcher = invalidCityPattern.matcher(city);

        if (matcher.matches()) {
          if (isLoggingDebug()) {
            logDebug("City \"" + city + "\" matches regex \"" + invalidCityPattern.pattern() + "\"");
          }

          Object[] params = {  };

          if (matcher.groupCount() > 0) {
            params = new String[] { matcher.group(1) };
          }

          throw new StorePurchaseProcessException(MSG_INVALID_CITY_ADDRESS,params);
        }
      }
    }
  }

  /**
   * This method will ensure the user isn't trying to use a shipping method that isn't valid for the 
   * state. For example, Express shipping to Alaska is not allowed.
   *
   * @param pAddress - address to validate against the shipping method.
   * @param pShippingMethod - shipping method to validate address for.
   * 
   * @throws StorePurchaseProcessException if an error occurs
   */
  public void validateShippingMethod(Address pAddress, String pShippingMethod)
    throws StorePurchaseProcessException {

    // Check the state entered in the address against the shipping method.
    String shipState = pAddress.getState();

    if (isLoggingDebug()) {
      logDebug("Checking to see if state " + shipState + " is valid for method " + pShippingMethod);
    }

    Properties props = getInvalidStatesForShipMethod();

    if ((props != null) && !StringUtils.isEmpty(shipState) && (pShippingMethod != null)) {
      
      // A pipe-delimited list of states should be a safe place to just do an index of check.
      String invalidStates = props.getProperty(pShippingMethod);

      if (!StringUtils.isEmpty(invalidStates)) {
        if (invalidStates.indexOf(shipState) > -1) {
          if (isLoggingDebug()) {
            logDebug("Found invalid state " + shipState + " for method " + pShippingMethod);
          }
          Object[] params = { pShippingMethod, shipState };
          throw new StorePurchaseProcessException(MSG_INVALID_STATE_FOR_METHOD, params);
        }
      }
    }
  }

  /**
  * Check shipping restrictions.
  *
  * @param pCommerceItemShippingInfoContainer - map of all commerce item infos for the order.
  * @param pShippingGroupMapContainer - map of all shipping groups for the profile.
  * 
  * @return a list of pairs of products that are being shipped to restricted countries.
  * 
  * @throws CommerceException if an error occurs.
  */
  public List checkShippingRestrictions(CommerceItemShippingInfoContainer pCommerceItemShippingInfoContainer,
                                        ShippingGroupMapContainer pShippingGroupMapContainer)
    throws CommerceException {
    
    List commerceItemsHavingNonShippableCountry = new ArrayList();
    List commerceItemShippingInfos = pCommerceItemShippingInfoContainer.getAllCommerceItemShippingInfos();

    String countryCode = null;
    String productDisplayName = null;

    for (Iterator iterCisis = commerceItemShippingInfos.iterator(); iterCisis.hasNext();) {
      
      CommerceItemShippingInfo cisi = (CommerceItemShippingInfo) iterCisis.next();
      CommerceItem commerceItem = cisi.getCommerceItem();
      RepositoryItem product = (RepositoryItem) commerceItem.getAuxiliaryData().getProductRef();
      productDisplayName = (String) product.getPropertyValue(DISPLAY_NAME_PROPERTY_NAME);

      Set shippableCountries = (Set) product.getPropertyValue(SHIPPABLE_COUNTRIES_PROPERTY_NAME);
      Set nonShippableCountries = (Set) product.getPropertyValue(NON_SHIPPABLE_COUNTRIES_PROPERTY_NAME);

      if (isLoggingDebug()) {
        logDebug(" List of Shippable Countries : " + shippableCountries + 
          " for product: " + productDisplayName);
        
        logDebug(" List of NonShippable Countries : " + nonShippableCountries + 
          " for product: " + productDisplayName);
      }

      ShippingGroup shippingGroup = pShippingGroupMapContainer.getShippingGroup(cisi.getShippingGroupName());

      // Only considering hard goods.
      if (shippingGroup instanceof HardgoodShippingGroup) { 
        countryCode = ((HardgoodShippingGroup) shippingGroup).getShippingAddress().getCountry();

        if (isLoggingDebug()) {
          logDebug(" ShippingGroup is HardgoodShippingGroup");
          logDebug(" Country : " + countryCode);
        }

        Object[] args = { productDisplayName, countryCode};
       
        if ((shippableCountries == null) || shippableCountries.isEmpty()) {
          if ((nonShippableCountries == null) || nonShippableCountries.isEmpty()) {
            
            // Nothing is specified , assuming product is shippable.
            if (isLoggingDebug()) {
              logDebug(" Product: " + productDisplayName + " is shippable " + " to " + countryCode);
            }
          } 
          else if (nonShippableCountries.contains(countryCode)) {
            commerceItemsHavingNonShippableCountry.add(productDisplayName + COUNTRY_DELIM + countryCode);

            if (isLoggingDebug()) {
              logDebug(" Product: " + productDisplayName + " is not Shippable " + " to " + countryCode);
            }
          } 
          else {
            // CountryCode is in the list of Shippable country.
            if (isLoggingDebug()) {
              logDebug(" Product: " + productDisplayName + " is shippable " + " to " + countryCode);
            }
          }
        } 
        else {
          if (!shippableCountries.contains(countryCode)) {
            commerceItemsHavingNonShippableCountry.add(productDisplayName + COUNTRY_DELIM + countryCode);

            if (isLoggingDebug()) {
              logDebug(" Product: " + productDisplayName + " is not Shippable " + " to " + countryCode);
            }
          } 
          else {
            // CountryCode is in the list of Shippable country.
            if (isLoggingDebug()) {
              logDebug(" Product: " + productDisplayName + " is shippable " + " to " + countryCode);
            }
          }
        }
      } //end of while
    } //end of for

    return commerceItemsHavingNonShippableCountry;
  }

  /**
   * Validates against invalid street addresses.
   *
   * @param pContactInfo - contact information.
   * 
   * @return invalid street address patterns.
   */
  public List checkForInvalidStreetAddress(ContactInfo pContactInfo) {

    List invalidStreetStrings = new ArrayList();
    String[] invalidStreetAddresses = getInvalidStreetStrings();
    String address1 = pContactInfo.getAddress1();

    if (address1 != null) {
      for (int i = 0; i < invalidStreetAddresses.length; i++) {
        String invalidString = invalidStreetAddresses[i];

        if (isLoggingDebug()) {
          logDebug("Checking if street address contains: " + invalidString);
        }

        address1 = address1.toLowerCase();
        String invalidStringLower = invalidString.toLowerCase();
        
        if (address1.indexOf(invalidStringLower) != -1) {
          invalidStreetStrings.add(invalidString);
        }
      }
    }
    return invalidStreetStrings;
  }

  /**
   * Trim string.
   * 
   * @param pInStr - string to trim.
   * 
   * @return trimmed string.
   */
  public String trimmedString(String pInStr) {
    if (pInStr != null) {
      return pInStr.trim();
    } 
    else {
      return pInStr;
    }
  }
  
  /**
   * Trims spaces from address values.
   * 
   * @param pAddress - Address whose values are to trimmed.
   * 
   * @return the address.
   * 
   * @throws ServletException if there was an error while executing the code.
   * @throws IOException if there was an error with servlet io.
   */
  public Address trimSpacesFromAddressValues(Address pAddress)
    throws ServletException, IOException {

    Address address = pAddress;
    
    // Save trimmed address values to disallow space(s) as address values.
    address.setFirstName(trimmedString(address.getFirstName()));
    address.setLastName(trimmedString(address.getLastName()));
    address.setAddress1(trimmedString(address.getAddress1()));
    address.setCity(trimmedString(address.getCity()));
    address.setCountry(trimmedString(address.getCountry()));
    address.setState(trimmedString(address.getState()));
    address.setPostalCode(trimmedString(address.getPostalCode()));
    ((ContactInfo) address).setPhoneNumber(trimmedString(((ContactInfo) address).getPhoneNumber()));

    String address2 = trimmedString(address.getAddress2());
    
    if (address2 != null && address2.equals("")) {
       address.setAddress2(null);
    }

    return address;
  }
  
  /**
   * Determine if the given shipping address contains valid state for the shipping.
   * 
   * @param pShippingAddress - shipping address contact info.
   * 
   * @return true if shipping state in the list of allowed places.
   */
  public boolean isValidShippingState(ContactInfo pShippingAddress) {
    
    String country = pShippingAddress.getCountry();
    String state = pShippingAddress.getState();
    Place[] places = getPlaceUtils().getPlaces(country);
    boolean validationResult = true;

    // If place is not found for the given state or place does not belongs to the country, return false.
    if ((places == null && !StringUtils.isEmpty(state)) ||
        (places != null && !getPlaceUtils().isPlaceInCountry(country, state))) {
      
      validationResult = false;
    }
    
    return validationResult;
  }
  
  /**
   * Checks whether new address is empty.
   * 
   * @param pAddress address to check.
   * 
   * @return true if address is empty, otherwise false.
   */
  public boolean isEmptyNewAddress(ContactInfo pAddress){
    if (!StringUtils.isEmpty(pAddress.getFirstName())) {
      return false;
    }
    if (!StringUtils.isEmpty(pAddress.getLastName())) {
      return false;
    }
    if (!StringUtils.isEmpty(pAddress.getAddress1())) {
      return false;
    }
    if (!StringUtils.isEmpty(pAddress.getCity())) {
      return false;
    }
    if (!StringUtils.isEmpty(pAddress.getState())) {
      return false;
    }
    if (!StringUtils.isEmpty(pAddress.getPostalCode())) {
      return false;
    }
    if (!StringUtils.isEmpty(pAddress.getPhoneNumber())) {
      return false;
    }
    
    return true;
  }
  
}
