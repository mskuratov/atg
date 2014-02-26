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


package atg.projects.store.profile;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.profile.CommerceProfileTools;
import atg.commerce.profile.CommercePropertyManager;
import atg.commerce.util.PlaceUtils;
import atg.core.i18n.PlaceList.Place;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.projects.store.StoreConfiguration;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.profile.recentlyviewed.RecentlyViewedTools;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;
import atg.userprofiling.address.AddressTools;


/**
 * Extensions to CommerceProfileTools.
 *
 * @author ATG
 * @version $Id: StoreProfileTools.java,v 1.13 2004/08/01 20:09:11 vjayaraman
 *          Exp $
 */
public class StoreProfileTools extends CommerceProfileTools {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/profile/StoreProfileTools.java#4 $$Change: 788278 $";
  public static final String ID_PROPERTY_NAME = "ID";
  public static final String UNKNOWN_STATE_CODE = "unknown";
  
  /** resource bundle name */
  private static final String RESOURCE_BUNDLE_NAME = "atg.userprofiling.ProfileResources";
  
  /** Resource bundle */
  private static ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  /** Error codes */
  private static final String MSG_SECURITY_STATUS_PROPERTY_NOT_FOUND = "securityStatusPropertyNotFound";

  /**
   * Minimum password length.
   */
  private int mMinPasswordLength = -1;

  /**
   * Maximum password length.
   */
  private int mMaxPasswordLength = -1;

  /**
   * Email Format Regular Expression.
   */
  private String mEmailformat;

  /**
   * These are the j2ee app servlet contexts for the main commerce
   * site and an alternate site. These values are used in the shopping
   * cart to determine what type of order we need for the requested url.
   */
  private String mUrlContextStoreCommerce;

  /**
   * Invalid context root.
   */
  private String mInvalidContextRoot;

  /**
   * @return maximum password length.
   */
  public int getMaxPasswordLength() {
    return mMaxPasswordLength;
  }

  /**
   * @param pMaxPasswordLength - maximum password length.
   */
  public void setMaxPasswordLength(int pMaxPasswordLength) {
    mMaxPasswordLength = pMaxPasswordLength;
  }

  /**
   * @return minimum password length.
   */
  public int getMinPasswordLength() {
    return mMinPasswordLength;
  }

  /**
   * @param pMinPasswordLength - minimum password length.
   */
  public void setMinPasswordLength(int pMinPasswordLength) {
    mMinPasswordLength = pMinPasswordLength;
  }

  /**
   * @return the Email Format Regular Expression.
   */
  public String getEmailFormat() {
    return mEmailformat;
  }

  /**
   * @param pEmailformat
   *            The Email Format Regular Expression.
   */
  public void setEmailFormat(String pEmailformat) {
    mEmailformat = pEmailformat;
  }

   /**
   * Place utils helper
   */
  PlaceUtils mPlaceUtils;

  /**
   *
   * @return the place utils
   */
  public PlaceUtils getPlaceUtils() {
    return mPlaceUtils;
  }

  /**
   * Sets place utils
   * @param pPlaceUtils the place utils to set
   */
  public void setPlaceUtils(PlaceUtils pPlaceUtils) {
    mPlaceUtils = pPlaceUtils;
  }
  
  /**
   * Store configuration.
   */
  private StoreConfiguration mStoreConfiguration;
  
  /**
   * @return store configuration.
   */
  public StoreConfiguration getStoreConfiguration() {
    return mStoreConfiguration;
  }

  /**
   * @param pStoreConfiguration - store configuration.
   */
  public void setStoreConfiguration(StoreConfiguration pStoreConfiguration) {
    mStoreConfiguration = pStoreConfiguration;
  }
  
  //--------------------------------------------------
  // property: ShippingAddressClassName
  private String mShippingAddressClassName = "atg.core.util.ContactInfo";

  /**
   * @return the String
   */
  public String getShippingAddressClassName() {
    return mShippingAddressClassName;
  }

  /**
   * @param pShippingAddressClassName the String to set
   */
  public void setShippingAddressClassName(String pShippingAddressClassName) {
    mShippingAddressClassName = pShippingAddressClassName;
  }
 
  //--------------------------------------------------
  // property: BillingAddressClassName
  private String mBillingAddressClassName = "atg.core.util.ContactInfo";

  /**
   * @return the String
   */
  public String getBillingAddressClassName() {
    return mBillingAddressClassName;
  }

  /**
   * @param pBillingAddressClassName the String to set
   */
  public void setBillingAddressClassName(String pBillingAddressClassName) {
    mBillingAddressClassName = pBillingAddressClassName;
  }
  
  //------------------------------------------------------
  // property: recentlyViewedTools
  //------------------------------------------------------
  /** Provides helper methods for working with recently viewed products */
  private RecentlyViewedTools mRecentlyViewedTools = null;

  /**
   * @return The RecentlyViewedTools reference. 
   */
  public RecentlyViewedTools getRecentlyViewedTools() {
    return mRecentlyViewedTools;
  }
    
  /**
   * @param pRecentlyViewedTools The RecentlyViewedTools reference.
   */
  public void setRecentlyViewedTools(RecentlyViewedTools pRecentlyViewedTools) {
    mRecentlyViewedTools = pRecentlyViewedTools;
  }
 
  //-----------------------------------
  // property: mCommercePropertyManager
  /** Provides helper methods when dealing with Profiles */
  private CommercePropertyManager mCommercePropertyManager;
  
  /** @return Gets mCommercePropertyManager   */
  public CommercePropertyManager getCommercePropertyManager() {
    return mCommercePropertyManager;
  }  

  /** @param pCommercePropertyManager Sets mCommercePropertyManager  */
  public void setCommercePropertyManager(CommercePropertyManager pCommercePropertyManager) {
    mCommercePropertyManager = pCommercePropertyManager;
  }
  
  /**
   * Checks repository to see if user has already subscribed to receive emails.
   *
   * @param pEmail -
   *            email address to be checked
   * @return True if email has already signed up for emails False is email was
   *         not found in ditribution list
   */
  public RepositoryItem retrieveEmailRecipient(String pEmail) {
    StorePropertyManager propertyManager = (StorePropertyManager) getPropertyManager();
    String emailRecipientItemDescriptorName = propertyManager.getEmailRecipientItemDescriptorName();

    try {
      RepositoryView view = getProfileRepository().getView(emailRecipientItemDescriptorName);
      QueryBuilder qb = view.getQueryBuilder();

      //Query query = generateEmailQuery(pEmail, qb);
      Query query = getProfileItemFinder().generateEmailQuery(pEmail, qb);

      if (isLoggingDebug()) {
        logDebug("findByEmail query=" + query);
      }

      RepositoryItem[] items = view.executeQuery(query);

      if (isLoggingDebug()) {
        if (items != null) {
          logDebug("findByEmail resultSet=" + java.util.Arrays.asList(items) + "; length=" + items.length + "; type=" +
            getDefaultProfileType());
        } else {
          logDebug("findByEmail resultSet=null; type=" + getDefaultProfileType());
        }
      }

      if ((items != null) && (items.length > 0)) {
        if (isLoggingDebug()) {
          logDebug("findByEmail found profile from email (" + pEmail + "): " + items[0] + "; type=" +
            getDefaultProfileType());
        }

        return items[0];
      }
    } catch (RepositoryException exc) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor(""), exc);
      }
    }

    return null;
  }

  /**
   * Create new EmailRecipient in repository.
   *
   * @param pEmail -
   *            Email Address
   * @param pSourceCode -
   *            Source Code
   * @return EmailRecipient - Repository Item
   * @throws RepositoryException indicates that a severe error occured while performing a Repository task
   */
  public MutableRepositoryItem createEmailRecipient(String pEmail, String pSourceCode)
      throws RepositoryException {
    return createEmailRecipient(null, pEmail, pSourceCode);
  }

  /**
   * Create new EmailRecipient in repository.
   *  
   * @param pProfile -
   *            user's profile
   * @param pEmail -
   *            Email Address
   * @param pSourceCode -
   *            Source Code 
   * @return EmailRecipient - Repository Item
   * @throws RepositoryException indicates that a severe error occured while performing a Repository task
   */
  public MutableRepositoryItem createEmailRecipient(RepositoryItem pProfile, String pEmail,
                                                    String pSourceCode) throws RepositoryException {
    RepositoryItem profile = pProfile;
    StorePropertyManager propertyManager = (StorePropertyManager) getPropertyManager();
    MutableRepository repository = getProfileRepository();
    MutableRepositoryItem emailRecipient = null;
    String userId = null;
    if (profile == null) {
      // Check to see if email is associated with a registered user
      profile = getItemFromEmail(pEmail);
    }

    if (isLoggingDebug()) {
      logDebug("Creating emailRecipient repository item for email " + pEmail);
    }
    emailRecipient = (MutableRepositoryItem) retrieveEmailRecipient(pEmail);
    if (emailRecipient == null) {
      emailRecipient = repository.createItem(propertyManager.getEmailRecipientItemDescriptorName());
      emailRecipient.setPropertyValue(propertyManager.getEmailAddressPropertyName(), pEmail);
      emailRecipient.setPropertyValue(propertyManager.getSourceCodePropertyName(), pSourceCode);

      if (profile != null) {
        userId = profile.getRepositoryId();
        emailRecipient.setPropertyValue(propertyManager.getUserIdPropertyName(), userId);
      }
      repository.addItem(emailRecipient);
    } else {
      updateEmailRecipient(profile, pEmail, null, pSourceCode);
    }

    return emailRecipient;
  }

  /**
   * Updates email recipient's email and userId properties if corresponding repository item is found.
   * Otherwise does nothing.
   * 
   * @param pProfile profile repository item
   * @param pEmail email of email recipient to update
   * @param pNewEmail new email
   * @param pSourceCode the source code 
   * @throws RepositoryException indicates that a severe error occured while performing a Repository task
   */
  public void updateEmailRecipient(RepositoryItem pProfile, String pEmail, String pNewEmail,
                                   String pSourceCode) throws RepositoryException {

    StorePropertyManager propertyManager = (StorePropertyManager) getPropertyManager();

    // Check if OldEmailAddress is already subscribed to receive emails
    MutableRepositoryItem emailRecipient = (MutableRepositoryItem) retrieveEmailRecipient(pEmail);

    if (emailRecipient != null) {
      // If yes, then update the emailAddress in the existing item with the new email
      MutableRepository repository = (MutableRepository) pProfile.getRepository();

      // set the user_id and email properties of the repository item to the profile id
      if (pProfile != null) {
        String userId = (String) pProfile.getRepositoryId();
        emailRecipient.setPropertyValue(propertyManager.getUserIdPropertyName(), userId);
      }
      if (pNewEmail != null) {
        emailRecipient.setPropertyValue(propertyManager.getEmailAddressPropertyName(), pNewEmail);
      }
      if (pSourceCode != null) {
        emailRecipient.setPropertyValue(propertyManager.getSourceCodePropertyName(), pSourceCode);
      }
      repository.updateItem(emailRecipient);
    } 
  }

  /**
   * Remove the EmailRecipient from repository.
   *
   * @param pEmail -
   *            Email Address
   * @throws atg.repository.RepositoryException if exception occurs while attemptiong to
   * retirve email recipient item
   */
  public void removeEmailRecipient(String pEmail) throws RepositoryException {
    StorePropertyManager propertyManager = (StorePropertyManager) getPropertyManager();
    MutableRepository repository = getProfileRepository();

    try {
      if (isLoggingDebug()) {
        logDebug("Removing emailRecipient repository item for email " + pEmail);
      }

      // Retreive EmailRecipient item corresponding to pEmail
      MutableRepositoryItem emailRecipient = (MutableRepositoryItem) retrieveEmailRecipient(pEmail);

      if (emailRecipient != null) {
        String emailRecipientItemDescriptor = propertyManager.getEmailRecipientItemDescriptorName();
        String emailRecipientId = (String) emailRecipient.getRepositoryId();
        repository.removeItem(emailRecipientId, emailRecipientItemDescriptor);
      } 
    } catch (RepositoryException exc) {
      if (isLoggingError()){
        logError(LogUtils.formatMajor(""), exc);
      }      
      throw exc;
    }
  } //End removeEmailRecipient

  /**
   * Override the OOTB method so that we can determine whether or not to merge
   * a user's cart after they login. If they login from the checkout flow.
   * the cart is not merged. Otherwise merge the cart with the persisted cart.
   * <p>
   * {@inheritDoc}
   */
  protected void postLoginUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse,
    RepositoryItem pProfile, OrderHolder pShoppingCart, PricingModelHolder pPricingModelHolder)
    throws javax.servlet.ServletException {
    // Check to see if there is a userCheckingOut param in the request
    if (isLoggingDebug()) {
      logDebug("Checking for userCheckingOut request parameter");
    }

    String requestParam = pRequest.getParameter("userCheckingOut");

    if (requestParam == null) {
      // Since there is no param, assume user is logging in as normal
      // and merge their shopping cart as normal
      if (isLoggingDebug()) {
        logDebug("Request parameter not found. Continuing with nornal login.");
      }

      super.postLoginUser(pRequest, pResponse, pProfile, pShoppingCart, pPricingModelHolder);
    } else {
      // User is logging in from the checkout flow. Do everything minus
      // the call to loadUserShoppingCartForLogin()
      if (isLoggingDebug()) {
        logDebug("userCheckingOut: " + requestParam);
      }

      if ((pShoppingCart != null) && (pPricingModelHolder != null)) {
        if (isLoggingDebug()) {
          logDebug("Initializing pricingModelHolder");
        }

        pPricingModelHolder.initializePricingModels();

        //set the new owner of the current order and reprice under the 
        //new user context.
        //The order is likely transient at this point if the user was shipping anonymously and then
        //decided to login during checkout.  So we add the order to the repository here. 
        Order order = pShoppingCart.getCurrent();
        order.setProfileId(pProfile.getRepositoryId());

        try {
          repriceOrder(order, pProfile, pPricingModelHolder, getUserLocale(pRequest, pResponse),
            getRepriceOrderPricingOp());

          if (order.isTransient()) {
            getOrderManager().addOrder(order);
          } else {
            getOrderManager().updateOrder(order);
          }
        } catch (CommerceException e) {
          if (isLoggingError()){
            logError(LogUtils.formatMajor(""), e); 
          }          
        } catch (ServletException e) {
          if (isLoggingError()){
            logError(LogUtils.formatMajor(""), e);  
          }          
        } catch (IOException e) {
          if (isLoggingError()){
            logError(LogUtils.formatMajor(""), e);  
          }        
        }
      }
    }
  }

  /**
   * Utility method to copy over the profile email to the login field.
   *
   * @param pProfile -
   *            Profile Object
   */
  public void copyEmailToLogin(Profile pProfile) {
    StorePropertyManager propertyManager = (StorePropertyManager) getPropertyManager();
    String email = (String) pProfile.getPropertyValue(propertyManager.getEmailAddressPropertyName());
    pProfile.setPropertyValue(propertyManager.getLoginPropertyName(), email.toLowerCase());
  }

  /**
   * {@inheritDoc}
   * 
   * @see atg.commerce.profile.CommerceProfileTools#postCreateUser(atg.servlet.DynamoHttpServletRequest,
   *      atg.servlet.DynamoHttpServletResponse,
   *      atg.repository.RepositoryItem, atg.commerce.order.OrderHolder)

   */
  protected void postCreateUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse,
    RepositoryItem pProfile, OrderHolder pShoppingCart)
    throws ServletException {
    Profile profile = (Profile) pProfile;
    
    super.postCreateUser(pRequest, pResponse, profile, pShoppingCart);
    
    try {
      PricingModelHolder userPricingModels = (PricingModelHolder)pRequest.resolveName(getUserPricingModelsPath());
      userPricingModels.initializePricingModels();
      repriceShoppingCarts(pProfile, pShoppingCart, userPricingModels, getUserLocale(pRequest, pResponse));
    } catch (CommerceException e) {
      // Do nothing, we'll display old order price
      if (isLoggingError()){
        logError ("Can't reprice Shopping Carts. Old price will be displayd ", e);
      }
    } catch (IOException e) {
      // Do nothing, we'll display old order price
      if (isLoggingError()){
        logError ("Can't get User Locale. Old price will be displayd ", e);
      }
    }
  }


  /**
   * Obtains all credit cards associated with user profile.
   *
   * @param pProfile
   *            the user profile

   * @return Map of CreditCard or null if no credit cards are specified on the profile
   */
  public Map getCreditCards(RepositoryItem pProfile) {
    StorePropertyManager spmgr = (StorePropertyManager) getPropertyManager();
    Map creditCards = (Map) pProfile.getPropertyValue(spmgr.getCreditCardPropertyName());

    return creditCards;
  }
 
  /**
   * This method gets the credit card with parameter (pNickname) and makes it
   * the default credit card for this profile. It just returns false if this
   * fails, exception handling is up to the caller.
   *
   * @param pProfile
   *            to get the credit card from, and set default to.
   * @param pNickname
   *            credit card to copy from Map of credit cards
   * @return boolean success or failure
   */
  public boolean setDefaultCreditCard(RepositoryItem pProfile, String pNickname) {
    if (isLoggingDebug()) {
      logDebug("Setting default credit card for profile");
    }

    try {
      StorePropertyManager pm = (StorePropertyManager) getPropertyManager();
      Map creditCards = (Map) pProfile.getPropertyValue(pm.getCreditCardPropertyName());
      RepositoryItem creditCard = (RepositoryItem) creditCards.get(pNickname);

      if (creditCard == null) {
        if (isLoggingDebug()) {
          logDebug("No credit card exists for this user profile");
        }

        return false;
      }

      //pProfile.setPropertyValue(pm.getDefaultCreditCardPropertyName(), creditCard);
      updateProperty(pm.getDefaultCreditCardPropertyName(), creditCard, pProfile);
    } catch (RepositoryException re) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error setting default credit card: " + re), re);
      }

      return false;
    }

    return true;
  }

  /**
   * Gets the default credit card for a user.
   *
   * @param pProfile
   *            the user profile
   * @return CreditCard object. Returns null if no default credit card exists
   *         for the user.
   */
  public RepositoryItem getDefaultCreditCard(RepositoryItem pProfile) {
    StorePropertyManager bpm = (StorePropertyManager) getPropertyManager();
    String defaultCreditCardPropertyName = bpm.getDefaultCreditCardPropertyName();
    RepositoryItem cc = (RepositoryItem) pProfile.getPropertyValue(defaultCreditCardPropertyName);

    return cc;
  }
  
  /**
   * Finds profile's credit card that corresponds to the given credit card
   * payment group and returns its nickname.
   * 
   * @param pProfile profile object
   * @param pCreditCard credit card payment group
   * @return the nickname
   */
  public String getCreditCardNickname(RepositoryItem pProfile, CreditCard pCreditCard) {
    //find credit card in Profile with the same credit card number
    String creditCardNumber = pCreditCard.getCreditCardNumber();
    String creditCardExpMonth = pCreditCard.getExpirationMonth();
    String creditCardExpYear = pCreditCard.getExpirationYear();
    Address creditCardAddress = pCreditCard.getBillingAddress();
    StorePropertyManager spmgr = (StorePropertyManager) getPropertyManager();
    Map creditCards = (Map) pProfile.getPropertyValue(spmgr.getCreditCardPropertyName());
    String nickname = null;

    try {
      for ( Iterator it = creditCards.entrySet().iterator(); it.hasNext(); ) {
        Map.Entry cardsEntry = (Map.Entry)it.next();
        String profileCardNumber = (String)((RepositoryItem)cardsEntry.getValue()).getPropertyValue(spmgr.getCreditCardNumberPropertyName());
        String profileCardExpMonth = (String)((RepositoryItem)cardsEntry.getValue()).getPropertyValue(spmgr.getCreditCardExpirationMonthPropertyName());
        String profileCardExpYear = (String)((RepositoryItem)cardsEntry.getValue()).getPropertyValue(spmgr.getCreditCardExpirationYearPropertyName());

        RepositoryItem profileCardAddressItem = (RepositoryItem)((RepositoryItem)cardsEntry.getValue()).getPropertyValue(spmgr.getCreditCardBillingAddressPropertyName());
        Address profileCardAddress = getAddressFromRepositoryItem(profileCardAddressItem);

        if ( profileCardNumber.equals(creditCardNumber)
             && profileCardExpMonth.equals(creditCardExpMonth)
             && profileCardExpYear.equals(creditCardExpYear)
             && StoreAddressTools.compare(profileCardAddress, creditCardAddress) ) {
          nickname = (String)cardsEntry.getKey();

          if (isLoggingDebug()) {
            logDebug("Nickname for credit card " + pCreditCard + " found: " + nickname);
          }
          break;
        }
      }
    }
    catch (RepositoryException repositoryException) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error obtaining credit card nickname: " + repositoryException), 
            repositoryException);
      }

      nickname = "";
    }

    return nickname;   
  }
  
  /**
   * Gets the default credit card nickname for a user.
   *
   * @param pProfile
   *            the user profile
   * @return default Credit cart nickname for a user.
   */
  public String getDefaultCreditCardNickname(RepositoryItem pProfile) {
    RepositoryItem defaultCard = getDefaultCreditCard(pProfile);    
    return defaultCard != null? getCreditCardNickname(pProfile, defaultCard):null;
  }
  
  /**
   * Gets the default shipping method for a user.
   *
   * @param pProfile
   *            the user profile
   * @return the default shipping method for a user.
   */
  public String getDefaultShippingMethod(RepositoryItem pProfile) {
    StorePropertyManager spm = (StorePropertyManager) getPropertyManager();
    String defaultShippingMethodPropertyName = spm.getDefaultShippingMethodPropertyName();
    String shippingMethod = (String) pProfile.getPropertyValue(defaultShippingMethodPropertyName);

    return shippingMethod;
  }

  /**
   * Gets nickname for the given profile's address. 
   * 
   * @param pProfile The profile repository item
   * @param pAddress Address object 
   * @return nickname for secondary address repository item
   */
  public String getProfileAddressName(RepositoryItem pProfile, Address pAddress) {
    CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();
    Map secondaryAddresses = (Map) pProfile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());
    String nickname = null;
    
    for (Iterator it = secondaryAddresses.entrySet().iterator(); it.hasNext();){
      Map.Entry addrEntry = (Map.Entry) it.next();
      RepositoryItem address = (RepositoryItem)addrEntry.getValue();
      if (areAddressesEqual(pAddress, address, null)){
        nickname = (String) addrEntry.getKey();

        if (isLoggingDebug()) {
          logDebug("Nickname for secondary address " + pAddress + " found: " + nickname);
        }
        break;
      }
    }
    return nickname;
  }
  
  /**
   * Gets the default shipping address nickname for a user.
   *
   * @param pProfile
   *            the user profile
   * @return the default shipping address nickname for a user.
   */
  public String getDefaultShippingAddressNickname(RepositoryItem pProfile) {
    RepositoryItem defaultAddress = getDefaultShippingAddress(pProfile);    
    return defaultAddress != null? getProfileAddressName(pProfile, defaultAddress):null;
  }

  /**
   * Utility method to check if user's password meets the min & max length.
   *
   * @param pPassword
   *            is password string
   * @return True if password meets min/max requirements False if password
   *         fails min/max requirements
   */
  public boolean isValidPasswordLength(String pPassword) {
    int passwordLength = pPassword.length();

    // Check to see if password.length is between min and max values
    if ((getMinPasswordLength() == -1 || passwordLength >= getMinPasswordLength()) && 
        (getMaxPasswordLength() == -1 || passwordLength <= getMaxPasswordLength())) {
      return true;
    }

    return false;
  }

   /**
   * @return UrlContextStoreCommerce property.
   */
  public String getUrlContextStoreCommerce() {
    return mUrlContextStoreCommerce;
  }

  /**
   * @return InvalidContextRoot property.
   */
  public String getInvalidContextRoot() {
    return mInvalidContextRoot;
  }

  /**
   * @param pString
   *            UrlContextShopCommerce property.
   */
  public void setUrlContextStoreCommerce(String pString) {
    mUrlContextStoreCommerce = pString;
  }

  /**
   * @param pString
   *            UrlContextShopWithConsultant property.
   */
  public void setInvalidContextRoot(String pString) {
    mInvalidContextRoot = pString;
  }

  /**
   * Utility method to copy one address repoitory item to another
   * repository item.
   *
   * @param pAddress - source address
   * @param pNewAddress - target address
   * @param pAddressIterator - address iterator
   */
  public void copyAddress(MutableRepositoryItem pAddress, MutableRepositoryItem pNewAddress, Iterator pAddressIterator) {
    String propertyName;
    Object property;

    while (pAddressIterator.hasNext()) {
      propertyName = (String) pAddressIterator.next();
      property = pAddress.getPropertyValue(propertyName);
      pNewAddress.setPropertyValue(propertyName, property);
    }
  }

  /**
   * Determines if a profile already exists with the email address.
   *
   * @param pEmail - e-mail address
   * @return true if a profile exists with the given email
   */
  public boolean isDuplicateEmailAddress(String pEmail) {
    // make sure a user with this email address doesn't already exist
    // UPDATE: Do we need to check all types of profiles?
    StoreRepositoryProfileItemFinder profileFinder = (StoreRepositoryProfileItemFinder) getProfileItemFinder();
    String profileType = getDefaultProfileType();

    RepositoryItem[] users = profileFinder.findByEmail(pEmail, profileType);

    if (users != null) {
      // add form exception user already exists
      if (isLoggingDebug()) {
        logDebug("User already exists with this email address: " + pEmail);
      }

      return true;
    }

    return false;
  }

  /**
   * This method creates an address object and sets the property values
   * to values in the repository item passed in.
   * @param pItem the repository item
   * @return address the address object with data from repository
   * @exception RepositoryException if there was an error when creating the new repository item.
   */
  public Address getAddressFromRepositoryItem(RepositoryItem pItem)
    throws RepositoryException {
    Address address = new ContactInfo();

    // update item with values in address
    try {
      atg.commerce.order.OrderTools.copyAddress(pItem, address);
    } catch (CommerceException ce) {
      Throwable src = ce.getSourceException();

      if (src instanceof RepositoryException) {
        throw (RepositoryException) src;
      } else {
        throw new RepositoryException(src);
      }
    }

    return address;
  }

  /**
   * Validates an email address for correctness.
   *
   * @return boolean true if email address is valid
   * @param pEmail email address
   */
  public boolean validateEmailAddress(String pEmail) {
    if (pEmail == null) {
      return false;
    }
    
    String regularExp = getEmailFormat();

    if (regularExp == null) {
      // If no regular expression is configured for email validation
      // use the following expressing that accepts international characters
      regularExp = "^[\\p{L}\\p{M}\\p{Nd}_-]+([\\.-][\\p{L}\\p{M}\\p{Nd}_-]+)*@[\\p{L}\\p{M}\\p{Nd}_]+([\\.-][\\p{L}\\p{M}\\p{Nd}_]+)*(\\.[\\p{L}\\p{M}\\p{Nd}_]{2,})+$";
    }

    //Set the email pattern string
    Pattern p = Pattern.compile(regularExp);

    //Match the given string with the pattern
    Matcher m = p.matcher(pEmail);

    //check whether match is found
    return m.matches();
  }

    
  /**
   * Validates country-state combination for the given address properties map. Checks if
   * state is required for the given country.
   *
   * @param pCountry country code
   * @param pState state code
   * @return true if country-state combination is valid
   */
  public boolean isValidCountryStateCombination(String pCountry, String pState) {
    if (StringUtils.isEmpty(pState) || UNKNOWN_STATE_CODE.equals(pState)){
      //State code is empty. Make sure that specified country has no states. 
      Place[] placesForCountry = getPlaceUtils().getPlaces(pCountry);
      return ((placesForCountry == null || placesForCountry.length == 0 ));     
    }    
    return getPlaceUtils().isPlaceInCountry(pCountry, pState);
  }
  
  /**
   * This method constructs a new credit card and the address object that is on
   * the credit card. pIsNewAddress indicates new secondary address should be created 
   * or existed one should be used. If new secondary address should be created 
   * then its properties are populated from pShippingAddress map.
   * Created credit card repository item is added to user's map of credit cards.
   * If user's default billing address is null than new credit card's billing
   * address is set as default billing address.
   * If user's default credit card is null that new credit card is set as default
   * credit card for this user.
   *
   * @param pProfile user's profile object
   * @param pNewCreditCard map of credit cards properties' values
   * @param pCreditCardNickname nickname for credit card
   * @param pBillingAddress map of shipping address properties' values
   * @param pAddressNickname shipping address nickname for shipping address to be
   *        created or for existed one
   * @param pIsNewAddress indicated if new address should be created or existed one
   *        should be used
   * @return created credit card's nickname
   *            
   * @throws RepositoryException
   *             if there was an error accessing the repository
   * @throws IntrospectionException if an exception happens during Introspection
   * @throws ClassNotFoundException an application tries to load in a class, no definition 
   * for the class with the specified name could be found 
   * @throws IllegalAccessException if an application tries to reflectively create 
   * an instance (other than an array), set or get a field, or invoke a method, but 
   * the currently executing method does not have access to the definition of the specified class,
   * field, method or constructor
   * @throws InstantiationException if an application tries to create an instance of a class using 
   * the newInstance method in class Class, but the specified class object cannot be instantiated
   * @throws PropertyNotFoundException an exception thrown by a property mapper when it does not 
   * support the requested property
   */
  public String createProfileCreditCard(Profile pProfile, Map pNewCreditCard,
                                        String pCreditCardNickname, Map pBillingAddress,
                                        String pAddressNickname, boolean pIsNewAddress)
      throws RepositoryException, InstantiationException, IllegalAccessException,
      ClassNotFoundException, IntrospectionException, PropertyNotFoundException {

    //create new secondary address item or extract existed one
    RepositoryItem secondaryAddress = null;
    String addrNickname = pAddressNickname;

    if (pIsNewAddress) {
      //generate secondary address nickname if it's not provided
      if (StringUtils.isBlank(addrNickname)) {
        addrNickname = getUniqueShippingAddressNickname(pBillingAddress, pProfile, null);
      }
      // Create Profile's secondary address repository item 
      Address addressObject = AddressTools.createAddressFromMap(pBillingAddress,
                                                                getShippingAddressClassName());

      createProfileRepositorySecondaryAddress(pProfile, addrNickname, addressObject);

      // Check to see Profile.shippingAddress is null, if it is,
      // add the new address as the default shipping address
      setDefaultShippingAddressIfNull(pProfile, addrNickname);

    }

    secondaryAddress = getProfileAddress(pProfile, addrNickname);

    return createProfileCreditCard(pProfile, pNewCreditCard, pCreditCardNickname, secondaryAddress);
  }

  /**
   * This implementation makes a reference to an existing address (should be located in a <code>secondaryAddresses</code> set)
   * with the <code>shippingAddress</code> property.
   * 
   * @param pProfile profile repository item
   * @param pAddressName nickname of the address to be set as default
   * @return true
   * @throws RepositoryException indicates that a severe error occured while performing a Repository task
   */
  @Override
  public boolean setDefaultShippingAddress(RepositoryItem pProfile, String pAddressName) throws RepositoryException {
    StorePropertyManager propertyManager = (StorePropertyManager) getPropertyManager();
    RepositoryItem addressItem = StringUtils.isEmpty(pAddressName) ? null : getProfileAddress(pProfile, pAddressName);
    updateProperty(propertyManager.getShippingAddressPropertyName(), addressItem, pProfile);
    return true;
  }
  
  /**
   * Checks whether user is explicitly logged in.
   * @return true if user is explicitly logged in.
   * @throws PropertyNotFoundException if securityStatus property is not found for the given profile 
   */
  public boolean isUserLoggedIn(RepositoryItem pProfile){
    // First, check to see if the user is logged in.
    Integer securityStatus = null;
    try {
      securityStatus = getSecurityStatus((Profile)pProfile);
    } catch (PropertyNotFoundException ex) {
      if (isLoggingError()){
        logError(ResourceUtils.getMsgResource(MSG_SECURITY_STATUS_PROPERTY_NOT_FOUND,
                                              RESOURCE_BUNDLE_NAME, sResourceBundle),ex);
      }
    }
    int securityStatusCookie = getPropertyManager().getSecurityStatusCookie();
    return (securityStatus != null && securityStatus.intValue() > securityStatusCookie);
  }
  
  /**
   * Overrides CommerceProfileTools.isDuplicateAddressNickName. A case insensitive nickname check.
   * 
   * @param pProfile The current user profile
   * @param pNewNickName An address nickname
   * @return true if the nickname is duplicated, false otherwise.
   */
  @Override
  public boolean isDuplicateAddressNickName(RepositoryItem pProfile, String pNewNickName){
    return isDuplicateAddressNickname(pProfile, pNewNickName, null);
  }
  
  /**
   * A case insensitive duplicate nickname check.
   * 
   * @param pProfile The current user profile
   * @param pNewNickName An address nickname
   * @param pExcludedNames A list of nicknames that the pNewNickName shouldn't be checked against
   * @return true if the nickname is duplicated, false otherwise.
   */
  public boolean isDuplicateAddressNickname(RepositoryItem pProfile, String pNewNickname,
                                            List<String> pExcludedNames)  
  {
    String secondaryAddressPropertyName =  getCommercePropertyManager().getSecondaryAddressPropertyName();
    Map secondaryAddressMap = (Map) pProfile.getPropertyValue(secondaryAddressPropertyName);
    return checkForDuplicates(secondaryAddressMap, pNewNickname, pExcludedNames);
  }
  
  /**
   * Overrides CommerceProfileTools.isDuplicateCreditCardNickname. A case insensitive nickname check.
   * 
   * @param pProfile The current user profile
   * @param pNewNickName An credit card nickname
   * @return true if the nickname is duplicated, false otherwise.
   */
  @Override
  public boolean isDuplicateCreditCardNickname(RepositoryItem pProfile, String pNewNickName){
    return isDuplicateCreditCardNickname(pProfile, pNewNickName, null);
  }
  
  /**
   * A case insensitive duplicate nickname check.
   *
   * @param pProfile The current user profile
   * @param pNewNickName An address nickname
   * @param pExcludedNames A list of nicknames that the pNewNickName shouldn't be checked against
   * @return true if the nickname is duplicated, false otherwise.
   */
  public boolean isDuplicateCreditCardNickname(RepositoryItem pProfile, String pNewNickname, 
                                               List<String> pExcludedNames)  
  {
    String creditCardPropertyName = getCommercePropertyManager().getCreditCardPropertyName();
    Map creditCardMap = (Map) pProfile.getPropertyValue(creditCardPropertyName);
    return checkForDuplicates(creditCardMap, pNewNickname, pExcludedNames);
  }

  /**
   * Changes secondary address nickname
   * 
   * @param pProfile profile repository item
   * @param pOldAddressName old secondary address nickname
   * @param pNewAddressName new secondary address nickname
   * @throws RepositoryException 
   */
  public void changeSecondaryAddressName(RepositoryItem pProfile, String pOldAddressName,
                                         String pNewAddressName) throws RepositoryException {
    if (StringUtils.isBlank(pNewAddressName) || pNewAddressName.equals(pOldAddressName)) {
      return;
    }

    CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();
    Map secondaryAddresses = (Map) pProfile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());
    RepositoryItem address = getProfileAddress(pProfile, pOldAddressName);
    if (address != null) {
      secondaryAddresses.remove(pOldAddressName);
      secondaryAddresses.put(pNewAddressName, address);
      updateProperty(cpmgr.getSecondaryAddressPropertyName(), secondaryAddresses, pProfile);
    }
  }
  
  /**
   * Changes credit card nickname in the map of user's credit cards
   * 
   * @param pProfile profile repository item
   * @param pOldCreditCardNickname credit card's old nickname
   * @param pNewCreditCardNickname credit card's new nickname
   * @throws RepositoryException 
   */
  public void changeCreditCardNickname(RepositoryItem pProfile, String pOldCreditCardNickname,
                                       String pNewCreditCardNickname) throws RepositoryException {
    if (StringUtils.isBlank(pNewCreditCardNickname)
        || pNewCreditCardNickname.equals(pOldCreditCardNickname)) {
      return;
    }

    CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();
    Map creditCards = (Map) pProfile.getPropertyValue(cpmgr.getCreditCardPropertyName());
    RepositoryItem card = getCreditCardByNickname(pOldCreditCardNickname, pProfile);
    if (card != null) {
      creditCards.remove(pOldCreditCardNickname);
      creditCards.put(pNewCreditCardNickname, card);
      updateProperty(cpmgr.getCreditCardPropertyName(), creditCards, pProfile);
    }
  }
  
  /**
   * Checks for duplicates between pKey and the mPropertyMap.keySet().
   * 
   * @param mPropertyMap
   * @param pKey
   * @param pExcludedKeys Keys to be excluded from the case insensitive check.
   * @return
   */
  protected boolean checkForDuplicates(Map mPropertyMap, String pKey, List<String> pExcludedKeys){
    //Fetch the keys (ie nick names)
    Collection secondaryAddressKeys = mPropertyMap.keySet();

    List<String> profileNames = new ArrayList<String>();
    Iterator<String> iterator = secondaryAddressKeys.iterator();
    while (iterator.hasNext()) {
      profileNames.add(iterator.next());
    }
    
    // Remove the names we want to ignore
    if(pExcludedKeys != null){
      profileNames.removeAll(pExcludedKeys);
    }
    
    // Check for duplicates
    for(String profileNickname : profileNames){
      if(profileNickname.equalsIgnoreCase(pKey)){
        return true;
      }
    }
    return false;
  }
}
