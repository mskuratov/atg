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
import java.util.HashSet;
import java.util.List;

import atg.commerce.CommerceException;
import atg.commerce.inventory.InventoryException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingModelHolder;

import atg.core.util.ContactInfo;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.StoreOrderManager;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.order.StoreShippingGroupManager;
import atg.projects.store.profile.StorePropertyManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryUtils;
import atg.userprofiling.PropertyManager;


import java.util.Iterator;
import java.util.Locale;


/**
 * This class performs logic necessary for the commit-order process.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreCommitOrderProcessHelper.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreCommitOrderProcessHelper extends StorePurchaseProcessHelper{

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/purchase/StoreCommitOrderProcessHelper.java#3 $$Change: 788278 $";


  /**
   * Validates the shipping methods on an order against the list of available shipping methods.
   * 
   * @param pOrder - The order on which to check the shipping methods.
   * @param pProfile - The user's profile.
   * @param pLocale - The locale to use for determining available shipping methods.
   * @param pPricingModels - A Collection of RepositoryItems representing PricingModels.
   * 
   * @return true if the order contains valid shipping methods; otherwise false.
   */
  public boolean validateShippingMethods(Order pOrder, 
                                         RepositoryItem pProfile, 
                                         Locale pLocale, 
                                         PricingModelHolder pPricingModels) {
    try {
      List shippingGroups = null;
      List availableMethods = null;
             
      shippingGroups = getShippingGroupManager().getHardgoodShippingGroups(pOrder);    
      
      if (shippingGroups == null || shippingGroups.isEmpty()) {
        // We're only validating the shipping methods for hardgood shipping groups.
        return true;
      }
        
      availableMethods = 
        pPricingModels.getShippingPricingEngine().getAvailableMethods((ShippingGroup)shippingGroups.get(0), 
                                                                      pPricingModels.getShippingPricingModels(), 
                                                                      pLocale, pProfile, null);
      if ( availableMethods == null ) { 
        // Hardgood shipping groups must have at least one shipping method.
        return false;
      }
      
      for (Iterator i = shippingGroups.iterator(); i.hasNext();) {
        ShippingGroup sg = (HardgoodShippingGroup)i.next();    
        
        if (!availableMethods.contains(sg.getShippingMethod())) {
          return false;
        }
      }
    }
    catch (PricingException pe) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor( "Could not validate shipping methods: " + pe), pe);
      }
      
      return false;
    }

    return true;
  }
  
  
  /**
   * Validates the credit card verification number on the order, if required.
   * 
   * @param pOrder - An <code>Order</code> that the credit card is linked to.
   * @param pCcv - The credit card verification number
   * 
   * @return True if the credit card verification number is valid
   */
  public boolean validateCreditCardVerificationNumber(Order pOrder, String pCcv) {
     StoreOrderTools orderTools = getStoreOrderTools();
     
     if ( orderTools.getCreditCard(pOrder) != null) {
       // If there's a credit card payment group, make sure the verification number has already been provided. 
       // This value might be missing if express checkout was used to get here.
       if (getStoreConfiguration().isRequireCreditCardVerification()) {
         
         String currentNumber = orderTools.getCreditCardVerificationNumberFromCard(pOrder);

         if (currentNumber == null) {
           if (!orderTools.validateCreditCardAuthorizationNumber(pCcv)) {
             return false;
           }

           orderTools.setCreditCardVerificationNumber(pOrder, pCcv);
         }
       }
     }
     
     return true;
  }
  
  /**
   * Adds the profile's e-mail address to the credit cart.
   * 
   * @param pOrder - the <code>Order</code> that the profile is linked to.
   * @param pProfile - the profile to retrieve the email address from. 
   */
  public void addEmailToCreditCard(Order pOrder, RepositoryItem pProfile) {
    PropertyManager pm = getStoreOrderTools().getProfileTools().getPropertyManager(); 
    CreditCard card;
    
    if ((card = getStoreOrderTools().getCreditCard(pOrder)) != null) {
      // CyberSource requires that the billingAddress has an email address.
      // Set it here if paying with credit card.
      ContactInfo billingAddress = (ContactInfo) card.getBillingAddress();
      String email = (String) pProfile.getPropertyValue(pm.getEmailAddressPropertyName());

      if ((email != null) && (email.trim().length() > 0)) {
        billingAddress.setEmail(email);
      } 
      else {
        billingAddress.setEmail("user@atg.com");
      } 
    }
  }
  
  /**
   * Makes sure that the profile id is set correctly on the order.
   * 
   * @param pOrder - the <code>Order</code> that the profile is linked to.
   * @param pProfile - the profile to retrieve the profileId from.
   */
  public void updateProfileIdOnOrder(Order pOrder, RepositoryItem pProfile) {
    String profileId = pProfile.getRepositoryId();
    
    if (!pOrder.getProfileId().equals(profileId)) {
      if (isLoggingDebug()) {
        logDebug("Setting profile_id on order.");
      }

      pOrder.setProfileId(pProfile.getRepositoryId());

      try {
        getOrderManager().updateOrder(pOrder);
      } 
      catch (CommerceException ce) {
        if (isLoggingError()) {
          logError(LogUtils.formatMajor("Could not update order: " + ce), ce);
        }
      }
    }
  }
  
  /**
   * Add the e-mail provided by an anonymous shopper to the profile.
   * 
   * @param pEmailAddress - E-mail address as a <code>String</code> value.
   * @param pProfile - Profile to add the e-mail address too, a <code>RepositoryItem</code> value.
   */
  public void addEmailToAnonymousUser(String pEmailAddress, RepositoryItem pProfile) {
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();

    PropertyManager pm = orderTools.getProfileTools().getPropertyManager();

    // Set the email provided by the anonymous shopper.
    if (pProfile.isTransient()) {
      MutableRepositoryItem mutitem = (MutableRepositoryItem) pProfile;
      mutitem.setPropertyValue(pm.getEmailAddressPropertyName(), pEmailAddress);
    }
  }
  
  /**
   * Makes post commit order processing: setting profile's last purchase date,
   * number of orders and bought items, order's OMS order id, inventory managing.
   * 
   * @param pStoreOrder - The last order, a <code>StoreOrderImpl</code> value.
   * @param pProfile - A <code>RepositoryItem</code> value.
   * 
   * @throws RepositoryException indicates that a severe error occurred while performing a Repository task.
   */
  public void doPostCommitOrderProcessing(StoreOrderImpl pStoreOrder, RepositoryItem pProfile)
    throws RepositoryException {

    // Assign the OMS order id.
    getStoreOrderTools().assignOmsOrderId(pStoreOrder);

    // If user submits another order in this session, then make sure the
    // "showSamples" flag isn't set to false by setting to true here.
    StorePropertyManager pm = (StorePropertyManager) 
      getStoreOrderTools().getProfileTools().getPropertyManager();
    
    MutableRepositoryItem profile = RepositoryUtils.getMutableRepositoryItem(pProfile);

    StoreOrderManager orderManager = (StoreOrderManager) getOrderManager();

    try {
      orderManager.manageInventoryOnCheckout(pStoreOrder);
    } 
    catch (InventoryException e) {
      if (isLoggingError()) {
        logError("Inventory Exception occur: ", e);
      }
    }

    profile.setPropertyValue(pm.getLastPurchaseDate(), pStoreOrder.getSubmittedDate());

    Integer numOrders = (Integer) profile.getPropertyValue(pm.getNumberOfOrders());
    numOrders = (numOrders != null) ? numOrders : Integer.valueOf(0);
    profile.setPropertyValue(pm.getNumberOfOrders(), Integer.valueOf(numOrders.intValue() + 1));

    List commerceItems = pStoreOrder.getCommerceItems();
    List previousItems = (List) profile.getPropertyValue(pm.getItemsBought());
    HashSet merge = new HashSet(previousItems);

    for (int i = 0; i < commerceItems.size(); i++) {
      merge.add(((CommerceItem) commerceItems.get(i)).getAuxiliaryData().getCatalogRef());
    }

    profile.setPropertyValue(pm.getItemsBought(), new ArrayList(merge));
  }
}
