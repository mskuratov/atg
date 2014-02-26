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


package atg.projects.store.inventory;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import atg.commerce.fulfillment.UpdateInventory;
import atg.dms.patchbay.MessageSink;
import atg.nucleus.GenericService;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.profile.StorePropertyManager;
import atg.repository.MutableRepository;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.service.collections.validator.CollectionObjectValidator;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailSender;


/**
 * This class will be used to notify users when an item is back in stock. It
 * will subscribe to a queue to listen for "UpdateInventory" messages which are
 * only fired when an item goes from Out of Stock to In Stock.
 *
 * @author ATG
 * @version $Id: BackInStockNotificationService.java,v 1.6 2004/07/24 20:39:40
 *          twoodward Exp $
 */
public class BackInStockNotificationService extends GenericService implements MessageSink {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Fulfillment/src/atg/projects/store/inventory/BackInStockNotificationService.java#3 $$Change: 788278 $";

  /** 
   * Sku item descriptor
   */
  public final static String SKU_ITEM_DESCRIPTOR = "sku";
  
  /** 
   * Product item descriptor
   */
  public final static String PRODUCT_ITEM_DESCRIPTOR = "product";
  
  /** 
   * This is the type of the profile that is created by the create operation.
   */
  public final static String DEFAULT_PROFILE_TYPE = "user";
  
  /**
   * Sku id parameter name.
   */
  public static final String PARAM_SKU_ID = "skuId";

  /**
   * Product id parameter name.
   */
  public static final String PARAM_PRODUCT_ID = "productId";

  /**
   * RQL query to find items.
   */
  protected static final String RQL_QUERY_FIND_BISN_ITEMS = "catalogRefId = ?0";

  /**
   * Template e-mail sender.
   */
  protected TemplateEmailSender mTemplateEmailSender;

  /**
   * Repository.
   */
  protected Repository mProfileRepository;

  /**
   * Property manager.
   */
  protected StorePropertyManager mPropertyManager;

  /**
   * Template e-mail information.
   */
  protected TemplateEmailInfo mTemplateEmailInfo;
  
  /**
   * Profile Tools
   */
  protected ProfileTools mProfileTools;
  /**
   * Sets the property ProfileTools.
   * @beaninfo expert: true
   * description: the ProfileTools used to manipulate the profile
   */
  public void setProfileTools(ProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }
  /**
   * @return The value of the property ProfileTools.
   */
  public ProfileTools getProfileTools() {
    return mProfileTools;
  }
  
  /**
   * Profile Type
   */
  protected String mProfileType = DEFAULT_PROFILE_TYPE;

  /**
   * Sets the property ProfileType.  
   **/
  public void setProfileType(String pProfileType) {
    mProfileType = pProfileType;
  }

  /**
   * @return The value of the property ProfileType
   **/
  public String getProfileType() {
    return mProfileType;
  }
  
  /**
   * property: catalogRepository
   */
  private MutableRepository mCatalogRepository = null;
  
  /**
   * @return the value of the catalogRepository field.
   */
  public MutableRepository getCatalogRepository() {
    return mCatalogRepository;
  }

  /**
   * @param pCatalogRepository -
   *          the value of the catalogRepository: field.
   */
  public void setCatalogRepository(MutableRepository pCatalogRepository) {
    mCatalogRepository = pCatalogRepository;
  }

  /**
   * property: validators   
   */
  private CollectionObjectValidator[] mValidators;
  
  /**
   * @return array of validators that will be applied to gifts
   */
  public CollectionObjectValidator[] getValidators() {
    return mValidators;
  }

  /**
   * @param validators the validators to set
   */
  public void setValidators(CollectionObjectValidator[] pValidators) {
    this.mValidators = pValidators;
  }
  
  /**
   * Initialize service in this method.
   */
  public void doStartService() {
  }

  /**
   * The method called when a message is delivered.
   *
   * @param pPortName - the message port
   * @param pMessage - the JMS message being received
   * @throws JMSException if message error occurs
   *
   */
  public void receiveMessage(String pPortName, Message pMessage)
    throws JMSException {
    String messageType = pMessage.getJMSType();

    if (isLoggingDebug()) {
      logDebug("Received message of type " + messageType + "  " + pMessage);
    }

    if (messageType.equals(UpdateInventory.TYPE)) {
      if (pMessage instanceof ObjectMessage) {
        UpdateInventory message = (UpdateInventory) ((ObjectMessage) pMessage).getObject();
        sendBackInStockNotifications(message);
      }
    }
  }

  /**
   * Notify users when an item is back in stock.
   *
   * @param pMessage - message to send
   */
  protected void sendBackInStockNotifications(UpdateInventory pMessage) {
    String[] skuIds = pMessage.getItemIds();

    try {
      for (int i = 0; i < skuIds.length; i++) {
        String skuId = skuIds[i];

        RepositoryItem[] items = retrieveBackInStockNotifyItems(skuId);

        if (items != null) {
          sendEmail(items);
          deleteItemsFromRepository(items);
        }
      }
    } catch (Exception ex) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("There was a problem in sending back in stock notifications."), ex);
      }
    }
  }

  /**
   * Helper method to delete repository items.
   *
   * @param pItems - items to delete
   * @throws RepositoryException if repository error occurs
   */
  protected void deleteItemsFromRepository(RepositoryItem[] pItems)
    throws RepositoryException {
    MutableRepository repository = (MutableRepository) getProfileRepository();
    String itemDescriptor = getPropertyManager().getBackInStockNotifyItemDescriptorName();

    for (int i = 0; i < pItems.length; i++) {
      repository.removeItem(pItems[i].getRepositoryId(), itemDescriptor);
    }
  }

  /**
   * Helper method to do the actual email sending.
   *
   * @param pItems - items
   * @throws TemplateEmailException If an exception occurs while fetching the 
   *                                template for the emial to be send.
   */
  protected void sendEmail(RepositoryItem[] pItems) throws TemplateEmailException {
    TemplateEmailInfo templateInfo = getTemplateEmailInfo();
    StorePropertyManager pM = getPropertyManager();

    for (int i = 0; i < pItems.length; i++) {
      String skuId = (String) pItems[i].getPropertyValue(pM.getBisnSkuIdPropertyName());
      String productId = (String) pItems[i].getPropertyValue(pM.getBisnProductIdPropertyName());
      
      // Apply vaildators to the item and send email in case the item pass validation
      if (validateItem(skuId, productId)) {
      
        Object[] email = { pItems[i].getPropertyValue(pM.getBisnEmailPropertyName()) };
        email = getProfilesToEmails(email);
      
        Map params = new HashMap();
        params.put(PARAM_SKU_ID, skuId);
        params.put(PARAM_PRODUCT_ID, productId);
        params.put(pM.getLocalePropertyName(), pItems[i].getPropertyValue(pM.getLocalePropertyName()));

        TemplateEmailInfo info = templateInfo.copy();
        info.setTemplateParameters(params);
        info.setSiteId((String) pItems[i].getPropertyValue(StoreInventoryManager.PARAM_SITE_ID));
        getTemplateEmailSender().sendEmailMessage(info, email);
        
        if (isLoggingDebug()) {
          logDebug("Done sending back in stock notification emails.");
        }
      }
      else {
        if (isLoggingDebug()) {
          logDebug("No back in stock notification emails will be send for item with sku id : " +  skuId + " and product id : " + productId + 
              "because it doen't pass validation");
        }
      }
      
    } 
  }

  /**
   * Apply mValidators to items with passed in sku id and product id. 
   * 
   * @param pSkuId the items sku id
   * @param pProductId the items product id
   * @return
   */
  protected boolean validateItem(String pSkuId, String pProductId) {
    
    // There is  no validators set, so no filtering is needed.
    if (getValidators() == null || getValidators().length == 0) {
      return true;
    }
    
    RepositoryItem sku = null;
    RepositoryItem product = null;
    try {
      MutableRepository rep = getCatalogRepository();
      sku = rep.getItem(pSkuId, SKU_ITEM_DESCRIPTOR);
      product = rep.getItem(pProductId, PRODUCT_ITEM_DESCRIPTOR);     
     
    } catch (RepositoryException e) {
      if (isLoggingDebug()) {
        logDebug("Can't get item with sku id: " + pSkuId + " and product id: " + pProductId);
      }
      // No validation could be done
      return false;
    }   
    
    boolean isValid = true;
    for (CollectionObjectValidator validator: getValidators()) {
      if (!validator.validateObject(sku) || !validator.validateObject(product)) {
        
        if (isLoggingDebug()) {
          logDebug("Item with sku id: " + pSkuId + " and product id: " + pProductId + "doesn't pass validator:" + validator);
        }
        
        // Item doesn't pass validation. Set isValid to false
        // and leave the loop as there is no need to check all
        // others validators.                
        isValid = false;
        break;
        
      }
    }
    return isValid;
    
  }
  /**
   * As back in stock email should contain My Account link for registered users, 
   * we look thought all email addresses to check if there is a user with such login.
   * 
   * @param pEmail array of emails to that the back in stock email should be sent
   * @return array of objects, that contains user profiles for emails to which the 
   * profile was found and email addresses if there is no user with such login. 
   */
  private Object[] getProfilesToEmails(Object[] pEmail) { 
    
    int recipientsAmount = pEmail.length;
    Object[] emailAndProfiles = new Object[recipientsAmount];
       
    for (int i = 0; i < recipientsAmount; i++){
      Object email = pEmail[i];
      // If email is String, we will check if there is a user with such login
      if (email instanceof String){
        String possibleLogin = (String) email;
        // Look for user with login possibleLogin
        RepositoryItem user = getProfileTools().getItem(possibleLogin, null, getProfileType());
        // If user with login possibleLogin was found, add the profile to the result array
        if (user != null) {
          emailAndProfiles[i] = user;
        }
        // If there is no user with login possibleLogin, add the email to the result array
        else {
          emailAndProfiles[i] = email;
        }
      }
      else {
        emailAndProfiles[i] = email;
      }
    }
    return emailAndProfiles;
  }

  /**
   * Perform the query to retrieve appropriate back in stock repository items.
   *
   * @param pSkuId - sku ids
   * @return repository items
   * @throws RepositoryException if repository error occurs
   */
  protected RepositoryItem[] retrieveBackInStockNotifyItems(String pSkuId)
    throws RepositoryException {
    Repository repository = getProfileRepository();
    String itemDescriptor = getPropertyManager().getBackInStockNotifyItemDescriptorName();
    RepositoryView view = repository.getView(itemDescriptor);

    Object[] params = new Object[] { pSkuId };

    RqlStatement statement = RqlStatement.parseRqlStatement(RQL_QUERY_FIND_BISN_ITEMS);

    RepositoryItem[] items = statement.executeQuery(view, params);

    return items;
  }

  /**
   * @return template e-mail semder information.
   */
  public TemplateEmailSender getTemplateEmailSender() {
    return mTemplateEmailSender;
  }

  /**
   * @param sender - template e-mail sender information.
   */
  public void setTemplateEmailSender(TemplateEmailSender sender) {
    mTemplateEmailSender = sender;
  }

  /**
   * @return profile repository.
   */
  public Repository getProfileRepository() {
    return mProfileRepository;
  }

  /**
   * @param repository - profile repository.
   */
  public void setProfileRepository(Repository repository) {
    mProfileRepository = repository;
  }

  /**
   * @return property manager.
   */
  public StorePropertyManager getPropertyManager() {
    return mPropertyManager;
  }

  /**
   * @param manager - property manager.
   */
  public void setPropertyManager(StorePropertyManager manager) {
    mPropertyManager = manager;
  }

  /**
   * @return template e-mail information.
   */
  public TemplateEmailInfo getTemplateEmailInfo() {
    return mTemplateEmailInfo;
  }

  /**
   * @param info - template e-mail information.
   */
  public void setTemplateEmailInfo(TemplateEmailInfo info) {
    mTemplateEmailInfo = info;
  }
}
