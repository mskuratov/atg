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


package atg.projects.store.order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderTools;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroupManager;
import atg.commerce.order.StoreCredit;
import atg.commerce.profile.CommerceProfileTools;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.Address;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.projects.store.catalog.StoreCatalogProperties;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.payment.creditcard.StoreCreditCardTools;
import atg.projects.store.profile.StoreProfileTools;
import atg.projects.store.profile.StorePropertyManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.idgen.IdGenerator;
import atg.servlet.ServletUtil;
import atg.userprofiling.PasswordChangeException;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.PropertyManager;


/**
 * This is the OrderTools class for the Store e-commerce site.
 * Order manipulations happen in this class. This is also the
 * holder for information about commerce item and order types.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/StoreOrderTools.java#7 $
 * @updated $DateTime: 2013/02/19 04:09:20 $$Author: npaulous $
 */
public class StoreOrderTools extends OrderTools {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/order/StoreOrderTools.java#7 $$Change: 791285 $";

  /**
   * Error key for duplicate nickname.
   */
  public static final String MSG_DUPLICATE_NICKNAME = "duplicateNickname";
  
  /**
   * Error key for shipping address creation.
   */
  public static final String MSG_ERROR_SHIPPING_ADDRESS = "errorCreatingShippingAddress";

  /**
   * Resource bundle name.
   */
  public static final String RESOURCE_NAME = "atg.commerce.order.UserMessages";
  
  /**
   * property: shippingGroupManager
   */
  ShippingGroupManager mShippingGroupManager;
  
  /**
   * @param pShippingGroupManager - a <code>shipping group manager</code> value.
   */
  public void setShippingGroupManager(ShippingGroupManager pShippingGroupManager) {
    mShippingGroupManager = pShippingGroupManager;
  }

  /**
   * @return a <code>shipping group manager</code> value.
   */
  public ShippingGroupManager getShippingGroupManager() {
    return mShippingGroupManager;
  }

  /**
   * property: giftWrapCommerceItemType
   */
  private String mGiftWrapCommerceItemType;
  
  /**
   * @return gift wrap commerce item type.
   */
  public String getGiftWrapCommerceItemType() {
    return mGiftWrapCommerceItemType;
  }

  /**
   * @param pGiftWrapCommerceItemType - gift wrap commerce item type.
   */
  public void setGiftWrapCommerceItemType(String pGiftWrapCommerceItemType) {
    mGiftWrapCommerceItemType = pGiftWrapCommerceItemType;
  }

  /**
   * property: commerceItemManager
   */
  private CommerceItemManager mCommerceItemManager;
  
  /**
   * @return commerce item manager.
   */
  public CommerceItemManager getCommerceItemManager() {
    return mCommerceItemManager;
  }

  /**
   * @param pCommerceItemManager - commerce item manager.
   */
  public void setCommerceItemManager(CommerceItemManager pCommerceItemManager) {
    mCommerceItemManager = pCommerceItemManager;
  }

  /**
   * property: idGenerator.
   */
  private IdGenerator mIdGenerator;
  
  /**
   * @return id generator.
   */
  public IdGenerator getIdGenerator() {
    return mIdGenerator;
  }

  /**
   * @param pIdGenerator - id generator.
   */
  public void setIdGenerator(IdGenerator pIdGenerator) {
    mIdGenerator = pIdGenerator;
  }

  /**
   * property: propertiesToCopyOnRegistration
   */
  String[] mPropertiesToCopyOnRegistration;
  
  /**
   * @param pPropertiesToCopyOnRegistration - properties to copy on registration.
   * 
   * @beaninfo description: properties to copy from anonymous profiles to
   *                        persistent profiles on Registration.
   */
  public void setPropertiesToCopyOnRegistration(String[] pPropertiesToCopyOnRegistration) {
    mPropertiesToCopyOnRegistration = pPropertiesToCopyOnRegistration;
  }

  /**
   * @return properties to copy on registration.
   */
  public String[] getPropertiesToCopyOnRegistration() {
    return mPropertiesToCopyOnRegistration;
  }

  /**
   * property: OmsOrderIdSpace
   */
  private String mOmsOrderIdSpace;
  
  /**
   * @return the name of the id space for OMS order ids.
   */
  public String getOmsOrderIdSpace() {
    return mOmsOrderIdSpace;
  }

  /**
   * @param pOmsOrderIdSpace - the name of the id space for OMS order ids.
   */
  public void setOmsOrderIdSpace(String pOmsOrderIdSpace) {
    mOmsOrderIdSpace = pOmsOrderIdSpace;
  }

  /**
   * property: testSkuId
   */
  private String mTestSkuId;
  
  /**
   * @return the id of the test SKU.
   */
  public String getTestSkuId() {
    return mTestSkuId;
  }

  /**
   * @param pTestSkuId - the id of the test SKU.
   */
  public void setTestSkuId(String pTestSkuId) {
    mTestSkuId = pTestSkuId;
  }

  /**
   * property: catalogProperties
   */
  private StoreCatalogProperties mCatalogProperties;
  
  /**
   *
   * @return catalog properties.
   */
  public StoreCatalogProperties getCatalogProperties() {
    return mCatalogProperties;
  }

  /**
   * @param pProperties - catalog properties.
   */
  public void setCatalogProperties(StoreCatalogProperties pProperties) {
    mCatalogProperties = pProperties;
  }
 
  /**
   * property: creditCardTools
   */
  private StoreCreditCardTools mCreditCardTools = null;

  /**
   * Returns property creditCardTools
   * @return returns property creditCardTools
   */
  public StoreCreditCardTools getCreditCardTools() {
    return mCreditCardTools;
  }

  /**
   * Sets property creditCardTools
   * @param pCreditCardTools the value to set for property creditCardTools
   */
  public void setCreditCardTools(StoreCreditCardTools pCreditCardTools) {
    mCreditCardTools = pCreditCardTools;
  }
  
  /**
   * @return resource bundle.
   */
  public ResourceBundle getResourceBundle() {
    return LayeredResourceBundle.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  }

  /**
   * @param pLocale the local of resource bundle.
   * 
   * @return resource bundle.
   */
  public ResourceBundle getResourceBundle(Locale pLocale) {
    if (pLocale == null) {
      return getResourceBundle();
    }
    return LayeredResourceBundle.getBundle(RESOURCE_NAME, pLocale);
  }

  /**
   * Returns the CreditCard payment group for the order. This is a utility method so form 
   * handlers can easily get the credit card payment group. This method returns the first 
   * payment group of type CreditCard. If multiple credit cards are supported, this method 
   * needs to be updated.
   *
   * @param pOrder - order to get credit card from.
   * 
   * @return credit card for this order.
   */
  public CreditCard getCreditCard(Order pOrder) {
    if (pOrder == null) {
      if (isLoggingDebug()) {
        logDebug("Null order passed to getCreditCard method.");
      }

      return null;
    }

    List paymentGroups = pOrder.getPaymentGroups();

    if (paymentGroups == null) {
      if (isLoggingDebug()) {
        logDebug("Order has null list of payment groups.");
      }

      return null;
    }

    int numPayGroups = paymentGroups.size();

    if (numPayGroups == 0) {
      if (isLoggingWarning()) {
        logWarning("No payment group on this order!");
      }

      return null;
    }

    PaymentGroup pg = null;

    // We are only supporting a single credit card payment group. Return the first one we get.
    for (int i = 0; i < numPayGroups; i++) {
      pg = (PaymentGroup) paymentGroups.get(i);

      if (pg instanceof CreditCard) {
        return (CreditCard) pg;
      }
    }

    return null;
  }

  /**
   * Get the store credit payment groups from the order. This will return the actual 
   * payment groups, not the Claimable items.
   *
   * @param pOrder - the order.
   * 
   * @return list of store credit payment groups.
   */
  public List getStoreCreditPaymentGroups(Order pOrder) {
    ArrayList storeCreditGroups = new ArrayList();

    if (pOrder == null) {
      if (isLoggingDebug()) {
        logDebug("Null order passed to getStoreCreditPaymentGroups method.");
      }

      return storeCreditGroups;
    }

    List paymentGroups = pOrder.getPaymentGroups();

    if (paymentGroups == null) {
      if (isLoggingDebug()) {
        logDebug("Order has null list of payment groups.");
      }

      return storeCreditGroups;
    }

    int numPayGroups = paymentGroups.size();

    if (numPayGroups == 0) {
      if (isLoggingWarning()) {
        logWarning("No payment group on this order!");
      }

      return storeCreditGroups;
    }

    PaymentGroup pg = null;

    // Create the list of StoreCredit payment groups.
    for (int i = 0; i < numPayGroups; i++) {
      pg = (PaymentGroup) paymentGroups.get(i);

      if (pg instanceof StoreCredit) {
        storeCreditGroups.add(pg);
      }
    }

    return storeCreditGroups;
  }

  /**
   * Copies an address to the credit card billing address. This method uses the superclass' 
   * copyAddress method, but first pulls the billing address from the credit card.
   * 
   * @param pAddress - the address that is copied the order's credit card payment group address.
   * @param pOrder - order to copy address info.
   */
  public void copyShippingToBilling(Address pAddress, Order pOrder) {
    CreditCard cc = getCreditCard(pOrder);

    if (!(cc == null)) {
      try {
        if (isLoggingDebug()) {
          logDebug("Copying shipping info to billing address.");
        }

        copyAddress(pAddress, cc.getBillingAddress());
      } 
      catch (CommerceException ce) {
        if (isLoggingError()) {
          logError(LogUtils.formatMajor("Error copying shipping address to billing."), ce);
        }
      }
    } 
    else {
      if (isLoggingWarning()) {
        logWarning("Can not copy billing address. No credit card payment " + "group is on this order." +
          pOrder.getId());
      }
    }
  }

  /**
   * Get the profile from the order, and update the address book with the shipping group address.
   *
   * @param pProfile- profile to copy address to.
   * @param pAddress -the address to copy.
   * @param pNickname -key for address book map.
   *
   * @return true on success, otherwise false.
   * 
   * @throws CommerceException if error with copy.
   */
  public boolean copyAddressToAddressBook(RepositoryItem pProfile, Address pAddress, String pNickname)
    throws CommerceException {
    
    CommerceProfileTools ptools = getProfileTools();

    if (ptools.isDuplicateAddressNickName(pProfile, pNickname)) {
      if (isLoggingDebug()) {
        logDebug("User tried to create address book entry with nickname" + " that already exists");
      }

      String msg = 
        ResourceUtils.getMsgResource(MSG_DUPLICATE_NICKNAME, 
                                     RESOURCE_NAME, 
                                     getResourceBundle(ServletUtil.getUserLocale()));

      throw new CommerceException(msg);
    }

    try {
      ptools.createProfileRepositorySecondaryAddress(pProfile, pNickname, pAddress);
    } 
    catch (RepositoryException re) {
      if (isLoggingError()) {
        logError(LogUtils.formatMinor("Error saving address to profile "), re);
      }
      return false;
    }
    
    return true;
  }

  /**
   * This method will get an address from the profile's address book, and set
   * the credit card's billing address.
   *
   * @param pProfile -to get address from.
   * @param pOrder -the order to set the billing address on.
   * @param pNickname -the nickname of the Address book address.
   * 
   * @return true if success, otherwise false.
   * 
   * @throws CommerceException if error storing address.
   */
  public boolean copyProfileAddressToCreditCard(RepositoryItem pProfile, Order pOrder, String pNickname)
    throws CommerceException {
    
    StorePropertyManager pm = (StorePropertyManager) getProfileTools().getPropertyManager();

    Map addresses = (Map) pProfile.getPropertyValue(pm.getSecondaryAddressPropertyName());

    // Check that the address exists.
    if (addresses.get(pNickname) == null) {
      if (isLoggingDebug()) {
        logDebug(LogUtils.formatMajor("No address exists with this nickname: " + pNickname));
      }

      return false;
    }

    MutableRepositoryItem storedAddress = (MutableRepositoryItem) addresses.get(pNickname);

    // Copy address to credit card.
    try {
      CreditCard creditCard = getCreditCard(pOrder);
      copyAddress(storedAddress, creditCard.getBillingAddress());
    } 
    catch (CommerceException ce) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error copying address to address book: "), ce);
      }
      return false;
    }

    return true;
  }

  /**
   * This method gets the credit card from the order, and adds it to the list
   * of credit cards for this user. The nickname passed as a param is used to
   * identify the card. The address to be used for this credit card passed in
   * the pStoredAddressKey parameter. The OOB copyCreditCardToProfile method
   * does not take a parameter for the nickname.
   *
   * @param pOrder -  order to find shipping address.
   * @param pCardNickname -key for address book map.
   * @param pProfile - profile to copy address to.
   * 
   * @return true if success, otherwise false.
   */
  public boolean copyCreditCardToProfile(RepositoryItem pProfile, Order pOrder, String pCardNickname) {
    CommerceProfileTools ptools = (CommerceProfileTools) getProfileTools();
    MutableRepositoryItem newCreditCard;
    CreditCard orderCreditCard = getCreditCard(pOrder);

    try {
      Map ccMap = getProfileTools().getUsersCreditCardMap(pProfile);
      MutableRepositoryItem profileCreditCard = (MutableRepositoryItem) ccMap.get(pCardNickname);

      if (profileCreditCard == null) {
        // Create a new card and add it to the profile.
        newCreditCard = ptools.createCreditCardItem(pProfile);
        ptools.copyCreditCard(orderCreditCard, newCreditCard);
        ((MutableRepository) pProfile.getRepository()).updateItem(newCreditCard);
        ccMap.put(pCardNickname, newCreditCard);
      } 
      else {
        // Update the credit card w/ same nickname on profile
        ptools.copyCreditCard(orderCreditCard, profileCreditCard);
        ((MutableRepository) pProfile.getRepository()).updateItem(profileCreditCard);
      }
    } catch (RepositoryException re) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error creating new credit card: "), re);
      }
      return false;
    }

    return true;
  }

  /**
   * Creates a new credit card repository item for the given profile, but does
   * not assign a billing address.
   *
   * @param pProfile - the customer profile.
   * 
   * @return The new credit card repository item.
   * 
   * @exception RepositoryException if there was an error when creating the new repository item.
   */
  public MutableRepositoryItem createNewCreditCard(RepositoryItem pProfile)
    throws RepositoryException {
    
    ProfileTools ptools = getProfileTools();
    StorePropertyManager pm = (StorePropertyManager) ptools.getPropertyManager();

    MutableRepositoryItem creditCardRepositoryItem;

    if (pProfile != null) {
      MutableRepository repository = (MutableRepository) pProfile.getRepository();
      
      // create new credit card object
      creditCardRepositoryItem = repository.createItem(pm.getCreditCardItemDescriptorName());
      repository.addItem(creditCardRepositoryItem);

      return creditCardRepositoryItem;
    }

    return null;
  }

  /**
   * Saves the address in the pAddress parameter to the default billing address of the user in 
   * the pProfile parameter. A new address item is created and stored in the default billing address, 
   * not just a reference to the incoming address.
   *
   * @param pAddress - address.
   * @param pProfile - profile.
   * 
   * @return true if successful, otherwise false.
   */
  public boolean saveAddressToDefaultBilling(Address pAddress, RepositoryItem pProfile) {
    MutableRepositoryItem address = null;
    MutableRepository profileRepository = (MutableRepository) pProfile.getRepository();
    ProfileTools profileTools = getProfileTools();
    StorePropertyManager pm = (StorePropertyManager) profileTools.getPropertyManager();

    // Create a new address and store it in address book.
    try {
      // Create an Address item.
      address = profileRepository.createItem(pm.getContactInfoItemDescriptorName());
      
      copyAddress(pAddress, address);
      profileRepository.addItem(address);
      
      // Update the profile with the billing address.
      profileTools.updateProperty(pm.getBillingAddressPropertyName(), address, pProfile);
    } 
    catch (RepositoryException re) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error creating new address object: "), re);
      }
      return false;
    } 
    catch (CommerceException ce) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error copying address to address book: "), ce);
      }
      return false;
    }

    return true;
  }

  /**
   * This utility method creates an address nickname for the credit card
   * address. All credit card nicknames are auto-generated in this
   * manner. The type of card and the last 4 digits become the nickname.
   * <p>
   * As an example, Visa 4111111111111111 will be converted to Visa - 1111
   * for a nickname.
   *
   * @param pCreditCard  - credit card to base nickname on.
   * 
   * @return card nickname based on type and last 4 digits of card.
   */
  public String createCreditCardNickname(Object pCreditCard) {
    StringBuffer nickname = new StringBuffer();
    String cardNumber = null;

    if (pCreditCard instanceof CreditCard) {
      CreditCard card= (CreditCard)pCreditCard;
      nickname.append(getCreditCardTools().getLocalizedCreditCardType(card.getCreditCardType()));
      cardNumber = card.getCreditCardNumber();
    }
    else if (pCreditCard instanceof HashMap) {
      HashMap card = (HashMap)pCreditCard;
      StorePropertyManager pm = (StorePropertyManager) getProfileTools().getPropertyManager();
      nickname.append(getCreditCardTools().getLocalizedCreditCardType((String)card.get(pm.getCreditCardTypePropertyName())));
      cardNumber = (String)card.get(pm.getCreditCardNumberPropertyName());
    }

    if (cardNumber == null) {
      return nickname.toString();
    }

    int cardNumberLength = cardNumber.length();
    String lastFour = cardNumber.substring(cardNumberLength - 4, cardNumberLength);
    nickname.append(" - " + lastFour);

    if (isLoggingDebug()) {
      logDebug("New credit card nickname created");
    }

    return nickname.toString();
  }

  /**
   * <p>
   *   Returns the ShippingGroup. We only have one shipping group, and this method relies on this 
   *   fact. It will throw a warning if an extra shipping group ever sneaks onto the order.
   * </p>
   * <br />
   * <p>
   *   If multiple ship-to is supported, then this method should not be used.
   * </p>
   * 
   * @param pOrder - the order object.
   * @return the hardgood shipping group for this order.
   */
  public HardgoodShippingGroup getShippingGroup(Order pOrder) {
    
    HardgoodShippingGroup sg = 
      ((StoreShippingGroupManager) getShippingGroupManager()).getFirstNonGiftHardgoodShippingGroup(pOrder);

    if (sg == null) {
      try {
        sg = (HardgoodShippingGroup) getShippingGroupManager().createShippingGroup();
        pOrder.addShippingGroup(sg);
      } 
      catch (CommerceException ce) {
        if (isLoggingError()) {
          logError("Commerce Exception occur: ", ce);
        }
        return null;
      }
    }

    return sg;
  }

  /**
   * This method will register a new user during the checkout process.
   * There are a number of steps to register the user:
   *
   * <ul>
   *   <li>Copy profile properties before registration.</li>
   *   <li>Create the hashed password</li>
   *   <li>Register the user</li>
   *   <li>Copy the previously transient profiles to the new user</li>
   * </ul>
   * </p>
   * <br />
   * <p>
   *   This method copies the billing address first and last name to the profile. This is because 
   *   we don't have any other information about the user at this point.
   * </p>
   *
   * @param pProfile - profile to register.
   * @param pOrder - order to pull registration info from.
   * @param pProfileValues - profile values.
   * 
   * @return true on success, false on failure.
   * 
   * @throws CommerceException if errors occurs.
   */
  public boolean registerNewUserDuringCheckout(Profile pProfile, Order pOrder, Map pProfileValues)
    throws CommerceException {
    
    StorePropertyManager pm = (StorePropertyManager) getProfileTools().getPropertyManager();

    String userEmailAddress = (String) pProfileValues.get(pm.getEmailAddressPropertyName());

    String profileType = getProfileTools().getDefaultProfileType();

    // Validations passed, create user.
    if (isLoggingDebug()) {
      logDebug("Validated registration info, creating new user.");
    }

    Map propertiesToCopy = generatePropertiesToCopy(getPropertiesToCopyOnRegistration(), pProfile);

    // Add personal information.
    propertiesToCopy.put(pm.getFirstNamePropertyName(), 
                         pProfileValues.get(pm.getFirstNamePropertyName()));
    propertiesToCopy.put(pm.getLastNamePropertyName(), 
                         pProfileValues.get(pm.getLastNamePropertyName()));
    propertiesToCopy.put(pm.getGenderPropertyName(), 
                         pProfileValues.get(pm.getGenderPropertyName()));
    propertiesToCopy.put(pm.getRefferalSourcePropertyName(), 
                         pProfileValues.get(pm.getRefferalSourcePropertyName()));
    propertiesToCopy.put(pm.getDateOfBirthPropertyName(), 
        pProfileValues.get(pm.getDateOfBirthPropertyName()));

    // Store login in lower case to support case-insensitive logins.
    propertiesToCopy.put(pm.getLoginPropertyName(), userEmailAddress.toLowerCase());

    ProfileTools ptools = getProfileTools();
    PropertyManager pmgr = ptools.getPropertyManager();
    
    try {
      // Create an encrypted password and salt from the plain text password supplied by the user.
      ptools.changePassword(pProfile, 
                            (String) pProfileValues.get(pm.getPasswordPropertyName()), 
                            "", 
                            "", 
                            false, 
                            false);
      
      // Put the newly generated password and salt into the properties to copy map.
      propertiesToCopy.put(pm.getPasswordSaltPropertyName(), 
                           pProfile.getPropertyValue(pm.getPasswordSaltPropertyName()));
      propertiesToCopy.put(pm.getPasswordPropertyName(),
                           pProfile.getPropertyValue(pm.getPasswordPropertyName())); 
    }
    catch (PasswordChangeException pce) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error creating encrypted password. The new password may have " +
                                      "violated a rule of the password rule checker: "), pce);
      }
      
      throw new CommerceException(pce.getMessage(), pce);
    } 
    catch (RepositoryException re) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error creating encrypted password. The profile property value(s) " +
                                      "can't be read or written for some reason: "), re);
      }
      
      throw new CommerceException(re.getMessage(), re);
    } 
    catch (PropertyNotFoundException pnfe) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error creating encrypted password. Login or password properties " +
                                      "couldn't be found in the profile item"), pnfe);
      }
      
      throw new CommerceException(pnfe.getMessage(), pnfe);
    }
    
    boolean newUserSuccess = false;

    try {
      if (isLoggingDebug()) {
        logDebug("Pre-createUser user id: " + pProfile.getRepositoryId());
      }

      newUserSuccess = getProfileTools().createNewUser(profileType, pProfile);

      if (newUserSuccess) {
        assignPostRegistrationProperties(propertiesToCopy, pProfile);
        updateProfileInRepository(pProfile);

        // Copy ZIP to homeAddress.
        MutableRepositoryItem homeAddress = 
          (MutableRepositoryItem) pProfile.getPropertyValue(pm.getContactInfoPropertyName());
        
        // Copy postal code to home address.
        homeAddress.setPropertyValue(pm.getAddressPostalCodePropertyName(), 
                                     pProfileValues.get(pm.getAddressPostalCodePropertyName()));

        // Update homeAddress
        ((MutableRepository) getProfileRepository()).updateItem(homeAddress);

        // The new user is now registered and logged in. Set security status.
        pProfile.setPropertyValue(pm.getSecurityStatusPropertyName(), 
                                  Integer.valueOf(pm.getSecurityStatusLogin()));

        if (isLoggingDebug()) {
          logDebug("New user id: " + pProfile.getRepositoryId());
        }

        // Register new user profile for this order.
        pOrder.setProfileId(pProfile.getRepositoryId());

      } 
      else {
        if (isLoggingDebug()) {
          logDebug(LogUtils.formatMajor("New user not created successfully."));
        }
      }
    } 
    catch (RepositoryException re) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error creating / updating user: "), re);
      }

      throw new CommerceException(re.getMessage(), re);
    }

    return true;
  }

  /**
   * This method is used when registering a new user. It will create a Map of property names to 
   * property values which will be set on the profile after registration.
   *
   * @param pProperties - the properties to copy.
   * @param pProfile - the profile to get the properties from.
   * 
   * @return map of property names to property values.
   */
  protected Map generatePropertiesToCopy(String[] pProperties, Profile pProfile) {
    HashMap propertyMap = new HashMap();
    int numProperties = pProperties.length;

    for (int i = 0; i < numProperties; i++) {
      propertyMap.put(pProperties[i], pProfile.getPropertyValue(pProperties[i]));
    }

    return propertyMap;
  }

  /**
   * This method copies the properties in the incoming Map to the profile item..
   *
   * @param pPropertiesToCopy - the map of properties to copy.
   * @param pProfile - profile to copy properties to.
   */
  protected void assignPostRegistrationProperties(Map pPropertiesToCopy, Profile pProfile) {
    Object[] keys = pPropertiesToCopy.keySet().toArray();
    int numberOfKeys = keys.length;
    String key = null;

    for (int j = 0; j < numberOfKeys; j++) {
      key = (String) keys[j];

      if (isLoggingDebug()) {
        logDebug("Copying property: " + key);
      }

      pProfile.setPropertyValue(key, pPropertiesToCopy.get(key));
    }
  }

  /**
   * Convenience method to update the profile item in the repository.
   *
   * @param pProfile - the profile to update.
   * 
   * @throws RepositoryException if update fails.
   */
  protected void updateProfileInRepository(Profile pProfile)
    throws RepositoryException {
    
    MutableRepository profileRepository = (MutableRepository) pProfile.getRepository();
    MutableRepositoryItem profileItem = (MutableRepositoryItem) pProfile.getDataSource();

    // Add and update item in Profile repository
    profileRepository.addItem(profileItem);
    profileRepository.updateItem(profileItem);
  }

  /**
   * This method assigns an OMS order id, and sets it on the order.
   * <br />
   * By default, the omsOrderId is set same as the order's id.
   * 
   * @param pOrder - the order parameter.
   */
  public void assignOmsOrderId(StoreOrderImpl pOrder) {
    pOrder.setOmsOrderId(pOrder.getId());
  }

  /**
   * Gets the gift wrap price from the order. This is used by Fulfillment. Gift wrap is unusual
   * because it is a SKU in the ATG system, but it is sent to SAP as an order header level price. 
   * Therefore, this method was added to retrieve the cost of gift wrap.
   *
   * @param pOrder - order to get GW price from.
   * 
   * @return price of gift wrap.
   */
  public double getGiftWrapPrice(StoreOrderImpl pOrder) {
    double giftWrapAmount = 0.0;
    int itemCount = pOrder.getCommerceItemCount();
    List items = pOrder.getCommerceItems();
    CommerceItem item;

    for (int i = 0; i < itemCount; i++) {
      item = (CommerceItem) items.get(i);

      if (item instanceof GiftWrapCommerceItem) {
        giftWrapAmount += item.getPriceInfo().getAmount();
      }
    }

    return giftWrapAmount;
  }

  /**
   * Checks to see if order is a Test order. If this order has other items in it, then a 
   * critical error is thrown, and the result is that it is not a test order. If the order has
   * a single "test SKU", then it should have no other items.
   *
   * @param pOrder - order.
   * @return true if success, otherwise false.
   */
  public boolean isTestOrder(StoreOrderImpl pOrder) {
    List items = pOrder.getCommerceItems();
    int itemCount = items.size();
    CommerceItem item = null;
    String skuId = null;

    for (int i = 0; i < itemCount; i++) {
      item = (CommerceItem) items.get(i);
      skuId = item.getCatalogRefId();

      if ((skuId != null) && skuId.equals(getTestSkuId())) {
        if (itemCount > 1) {
          if (isLoggingDebug()) {
            logDebug(LogUtils.formatCritical("Order found with test" +
                " product and other items. Order is being sent to fulfillment." +
                " A customer may have been able to add the test product to cart."));
          }
          return false;
        }
        return true;
      }
    }

    return false;
  }

  /**
   * Saves the address to address book and makes it the default shipping address if it hasn't already
   * been set.
   *
   * @param pProfile - profile.
   * @param pAddress - address.
   * @param pNickName - nickname.
   * @throws atg.commerce.CommerceException if commerce error occurs.
   */
  public void saveAddressToAddressBook(RepositoryItem pProfile, Address pAddress, String pNickName)
    throws CommerceException {
    
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    StorePropertyManager pm = (StorePropertyManager) profileTools.getPropertyManager();

    boolean result = copyAddressToAddressBook(pProfile, pAddress, pNickName);
    
    if(!result){
      String msg = 
        ResourceUtils.getMsgResource(MSG_ERROR_SHIPPING_ADDRESS, RESOURCE_NAME, 
                                     getResourceBundle(ServletUtil.getUserLocale()));
      
      throw new CommerceException(msg);
    }

    RepositoryItem currentDefault = (RepositoryItem) 
      pProfile.getPropertyValue(pm.getShippingAddressPropertyName());

    if (currentDefault == null) {
      if (isLoggingDebug()) {
        logDebug("Copying address to default address. " + pNickName);
      }

      try {
        profileTools.setDefaultShippingAddress(pProfile, pNickName);
      } 
      catch (RepositoryException ex) {
        throw new CommerceException(ex);
      }
    }
  }

  /**
   * Sets the default shipping method for a profile, but only if it hasn't been set already.
   * 
   * @param pProfile - profile.
   * @param pShippingMethod - shipping method.
   */
  public void saveDefaultShippingMethod(RepositoryItem pProfile, String pShippingMethod) {
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    StorePropertyManager pm = (StorePropertyManager) profileTools.getPropertyManager();
    String defaultMethodName = pm.getDefaultShippingMethodPropertyName();
    String defaultMethod = (String) pProfile.getPropertyValue(defaultMethodName);

    if ((defaultMethod == null) || (defaultMethod.trim().length() == 0)) {
      if (isLoggingDebug()) {
        logDebug("Setting default shipping method to: " + pShippingMethod);
      }

      try {
        profileTools.updateProperty(defaultMethodName, pShippingMethod, pProfile);
      } 
      catch (RepositoryException re) {
        if (isLoggingError()) {
          logError(LogUtils.formatMajor("Error setting default shipping method: "), re);
        }
      }
    }
  }

  /**
   * Verifies that the authentication number is valued and a number.
   * 
   * @param pAuthNumber - authentication number.
   * 
   * @return true if authentication number is a valid, otherwise false.
   */
  public boolean validateCreditCardAuthorizationNumber(String pAuthNumber) {
    
    if (StringUtils.isBlank(pAuthNumber)) {
      return false;
    } 
    else {
      // Make sure it's a number.
      try {
        Integer.parseInt(pAuthNumber);
      } 
      catch (NumberFormatException e) {
        return false;
      }
    }

    return true;
  }

  /**
   * Sets the verification number on the order's credit card payment group.
   *
   * @param pOrder - order.
   * @param pVerificationNumber - verification number.
   */
  public void setCreditCardVerificationNumber(Order pOrder, String pVerificationNumber) {
    CreditCard creditCard = getCreditCard(pOrder);
    creditCard.setCardVerificationNumber(pVerificationNumber);
  }

  /**
   * Gets the verification number from the order's credit card payment group.
   *
   * @param pOrder - order.
   * 
   * @return the verification number from the order's credit card payment group.
   */
  public String getCreditCardVerificationNumberFromCard(Order pOrder) {
    CreditCard creditCard = getCreditCard(pOrder);

    if (creditCard != null) {
      return creditCard.getCardVerificationNumber();
    } 
    else {
      return null;
    }
  }
}
