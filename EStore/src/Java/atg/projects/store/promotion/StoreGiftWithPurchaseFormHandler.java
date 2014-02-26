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



package atg.projects.store.promotion;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.inventory.InventoryException;
import atg.commerce.promotion.GiftWithPurchaseFormHandler;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.projects.store.inventory.StoreInventoryManager;
import atg.projects.store.profile.StoreProfileTools;
import atg.projects.store.ui.AjaxUtils;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * CRS extension of core commerce GiftWithPurchaseFormHandler.
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/promotion/StoreGiftWithPurchaseFormHandler.java#4 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreGiftWithPurchaseFormHandler extends GiftWithPurchaseFormHandler {

  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/promotion/StoreGiftWithPurchaseFormHandler.java#4 $$Change: 788278 $";

  //-------------------------------------
  // Constants
  //-------------------------------------  
    
  public static final String MSG_ERROR_GIFT_OUT_OF_STOCK = "errorGiftOutOfStock";
  
  public static final String MSG_ERROR_SELECTED_SKU_IS_INVALID = "errorSelectedSkuIsInvalid";
  
  public static final String MSG_ERROR_SELECT_PRODUCT = "errorGiftProductNotSelected";
  
  public static final String MSG_ERROR_SELECT_SKU = "errorGiftSkuNotSelected";
  
  public static final String MSG_ERROR_SELECT_GIFT = "errorGiftNotSelected";
  
  public static final String MSG_ERROR_SELECT_PRODUCT_AND_SKU = "errorGiftProductSkuNotSelected";
  
  public static final String MSG_ERROR_SELECT_COLOR = "errorGiftColorNotSelected";
  
  public static final String MSG_ERROR_SELECT_SIZE= "errorGiftSizeNotSelected"; 
  
  public static final String MSG_ERROR_SELECT_WOOD_FINISH = "errorGiftWoodFinishNotSelected";
  
  private static final String CLOTHING_SKU_TYPE = "clothing";

  private static final String FURNITURE_SKU_TYPE = "furniture";
  
  private static final String COLOR_ATTRIBUTE = "color";
  
  private static final String SIZE_ATTRIBUTE = "size";
  
  private static final String WOOD_FINISH_ATTRIBUTE = "woodFinish";
  
  private static final String EMPTY_SELECTION = "EMPTY";
  
  public static final String SKU_PARAMETER = "sku";


  //-------------------------------------
  // Properties
  //-------------------------------------
  
  /**
   * property: requiredSkuAttributes
   */ 
  private Map mRequiredSkuAttributes = new HashMap();

  /**
   * @return a map of attributes user need to specify to define SKU, for example 
   *         for clothing SKU it is color and size.
   */
  public Map getRequiredSkuAttributes() {
    return mRequiredSkuAttributes;
  }

  /**
   * @param pRequiredSkuAttributes - a map of attributes user need to specify to define SKU, for example 
   *                                 for clothing SKU it is color and size. 
   */
  public void setRequiredSkuAttributes(Map pRequiredSkuAttributes) {
    mRequiredSkuAttributes = pRequiredSkuAttributes;
  }

  /**
   * property: SkuType  
   */
  private String mSkuType;

  /**
   * @return the SKU type.
   */
  public String getSkuType() {
    return mSkuType;
  }

  /**
   * @param pSkuType - the SKU type.
   */
  public void setSkuType(String pSkuType) {
    mSkuType = pSkuType;
  }

  /**
   * property: ajaxMakeGiftSelectionSuccessURL
   */
  protected String mAjaxMakeGiftSelectionSuccessURL;
  
  /**
   * @return the AJAX gift selection success URL.
   */
  public String getAjaxMakeGiftSelectionSuccessURL() {
    return mAjaxMakeGiftSelectionSuccessURL;
  }

  /**
   * @param pAjaxMakeGiftSelectionSuccessURL - the AJAX gift selection success URL.
   */
  public void setAjaxMakeGiftSelectionSuccessURL(
      String pAjaxMakeGiftSelectionSuccessURL) {
    mAjaxMakeGiftSelectionSuccessURL = pAjaxMakeGiftSelectionSuccessURL;
  }

  /**
   * property: ajaxMakeGiftSelectionErrorURL
   */
  protected String mAjaxMakeGiftSelectionErrorURL;

  /**
   * @return the AJAX gift selection error URL.
   */
  public String getAjaxMakeGiftSelectionErrorURL() {
    return mAjaxMakeGiftSelectionErrorURL;
  }

  /**
   * @param pAjaxMakeGiftSelectionErrorURL - the AJAX gift selection error URL.
   */
  public void setAjaxMakeGiftSelectionErrorURL(
      String pAjaxMakeGiftSelectionErrorURL) {
    mAjaxMakeGiftSelectionErrorURL = pAjaxMakeGiftSelectionErrorURL;
  }
  
  /** 
   * property: ajaxMakeGiftSelectionTimeoutURL
   */
  protected String mAjaxMakeGiftSelectionTimeoutURL;

  /**
   * @return The URL that holds the JSON session timeout object data. 
   */
  public String getAjaxMakeGiftSelectionTimeoutURL() {
    return mAjaxMakeGiftSelectionTimeoutURL;
  }

  /**
   * @param pAjaxMakeGiftSelectionTimeoutURL - The URL that holds the JSON session timeout object data.
   */
  public void setAjaxMakeGiftSelectionTimeoutURL(String pAjaxMakeGiftSelectionTimeoutURL) {
    mAjaxMakeGiftSelectionTimeoutURL = pAjaxMakeGiftSelectionTimeoutURL;
  }
  
  /** 
   * property: inventoryManager
   */
  protected StoreInventoryManager mInventoryManager;

  /**
   * @return the inventoryManager.
   */
  public StoreInventoryManager getInventoryManager() {
    return mInventoryManager;
  }

  /**
   * @param pInventoryManager - the inventoryManager.
   */
  public void setInventoryManager(StoreInventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }
  
  /** 
   * property: profileTools    
   */
  protected StoreProfileTools mProfileTools = null;
  
  /**
   * @return The profile tools utility class.
   */
  public StoreProfileTools getProfileTools() {
    return mProfileTools;
  }
  
  /**
   * @param pProfileTools - The profile tools utility class.
   */
  public void setProfileTools(StoreProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
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
  
  //-------------------------------------
  // Public Methods
  //-------------------------------------  
  
  /**
   * Set valid SKU ID based on selected Product
   */
  public void preMakeGiftSelection(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {

    super.preMakeGiftSelection(pRequest, pResponse);
    
    // This request parameter value will retrieve a product ID selected by the user. 
    Object productId = pRequest.getParameter(getPromotionId() + "_" + getSkuId());

    // Retrieve selected product ID which is linked to the selected SKU.
    if(productId != null) {
      setProductId((String)productId);
    }
        
    // Different validation messages for different types of request. If request has been sent 
    // as JSON, assume that it was rich UI pop-over dialog with color/size/wood finish picker. 
    // In that case, notify user only about unselected items.
    if (AjaxUtils.isAjaxRequest(pRequest)) {
      validateAjaxSelection(pRequest, pResponse);
      
    } 
    else {
      if(StringUtils.isEmpty(getProductId()) || StringUtils.isEmpty(getSkuId())) {
        addFormException(new DropletException(formatUserMessage(MSG_ERROR_SELECT_GIFT,
                                                                pRequest, pResponse)));
      }
    }
    
    if(!getFormError()) {
      try {
        MutableRepository rep = getCatalogRepository();
        RepositoryItem sku = rep.getItem(getSkuId(), SKU_PARAMETER);
        
        if(!((StoreGWPManager)getGwpManager()).validateGiftItem(sku)) {
          addFormException(new DropletException(formatUserMessage(MSG_ERROR_SELECTED_SKU_IS_INVALID, 
                                                                  pRequest, pResponse)));
        }
      } catch (RepositoryException e) {
        vlogError(e, "Cannot get sku  for id", getSkuId());
      }
    }
    
    
    if(!getFormError()) {
      // Check inventory status
      try {

        // Get product repository item.
        RepositoryItem productItem = (RepositoryItem)getInventoryManager().getCatalogRefRepository().getItem(getProductId(), getInventoryManager().getCatalogProperties().getProductItemName());
        int status = getInventoryManager().queryAvailabilityStatus(productItem, getSkuId());
      
        if(status != getInventoryManager().getAvailabilityStatusInStockValue()
             && status != getInventoryManager().getAvailabilityStatusBackorderableValue()
             && status != getInventoryManager().getAvailabilityStatusPreorderableValue()){

          addFormException(new DropletException(formatUserMessage(MSG_ERROR_GIFT_OUT_OF_STOCK, 
                                                                  pRequest, pResponse)));
        }
      } 
      catch (InventoryException e) {
        vlogError(e, "Cannot get inventory status for {0}", getSkuId());
      } catch (RepositoryException e) {
      vlogError(e, "Cannot get product repository item for ID = {0}", getProductId());
      }
    }
    
    super.preMakeGiftSelection(pRequest, pResponse);
  }

  /**
   * Check if this is an AJAX request first and update success/error URLs.
   * 
   * @param pRequest - the HTTP request parameter.
   * @param pResponse - the HTTP response parameter.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if there was an error with servlet io.
   */
  public boolean handleMakeGiftSelection(DynamoHttpServletRequest pRequest,
                                         DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    //  If this is an AJAX RichCart request, then handle it as such, sending a JSON response.          
    if (AjaxUtils.isAjaxRequest(pRequest)) {
      if (isLoggingDebug()) {
        logDebug("Handling AJAX MakeGiftSelection request");
      }

      // This request has been sent from a JavaScript component in the browser that is expecting
      // a JSON response, so we have to make sure that's what we send it. Use the AJAX success/error 
      // URLs from this point onwards.       
      setMakeGiftSelectionSuccessURL(getAjaxMakeGiftSelectionSuccessURL());
      setMakeGiftSelectionErrorURL(getAjaxMakeGiftSelectionErrorURL());
    }
    
    return super.handleMakeGiftSelection(pRequest, pResponse);
  }

  /**
   * Validate selection has been made via AJAX and choose appropriate selection based 
   * on missed  SKU attributes.
   * 
   * @param pRequest - the HTTP request parameter.
   * @param pResponse - the HTTP response parameter.
   * 
   * @throws ServletException if servlet error occurs.
   * @throws IOException if there was an error with servlet io.
   */
  protected void validateAjaxSelection(DynamoHttpServletRequest pRequest,
                                       DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    if(StringUtils.isEmpty(getProductId())) {
      addFormException(new DropletException(formatUserMessage(MSG_ERROR_SELECT_PRODUCT_AND_SKU, 
                                                              pRequest, pResponse)));
    }
    
    if(!getFormError()) {
      
      if(StringUtils.isEmpty(getSkuId())) {
        
        // determine required SKU
        if(CLOTHING_SKU_TYPE.equals(getSkuType())) {
          
          String color = (String) getRequiredSkuAttributes().get(COLOR_ATTRIBUTE);
          String size = (String) getRequiredSkuAttributes().get(SIZE_ATTRIBUTE);
          
          if(EMPTY_SELECTION.equals(color) && EMPTY_SELECTION.equals(size)) {
            addFormException(new DropletException(formatUserMessage(MSG_ERROR_SELECT_SKU, 
                                                                    pRequest, pResponse)));
          } 
          else if((EMPTY_SELECTION.equals(color) && !EMPTY_SELECTION.equals(size)) ||
                  (EMPTY_SELECTION.equals(size) && color == null)) {
            addFormException(new DropletException(formatUserMessage(MSG_ERROR_SELECT_COLOR, 
                                                                    pRequest, pResponse)));
          } 
          else if((!EMPTY_SELECTION.equals(color) && EMPTY_SELECTION.equals(size)) ||
                  (EMPTY_SELECTION.equals(color) && size == null)) {
            addFormException(new DropletException(formatUserMessage(MSG_ERROR_SELECT_SIZE, 
                                                                    pRequest, pResponse)));
          }
        } 
        else if(FURNITURE_SKU_TYPE.equals(getSkuType())) {
          String woodFinish = (String) getRequiredSkuAttributes().get(WOOD_FINISH_ATTRIBUTE);
          
          if(StringUtils.isEmpty(woodFinish) || EMPTY_SELECTION.equals(woodFinish)) {
            addFormException(new DropletException(formatUserMessage(MSG_ERROR_SELECT_WOOD_FINISH, 
                                                                    pRequest, pResponse)));
          }
        }
      }
    }
  }
  
  /**
   * <p>
   *   If the request is a form submission from:
   *   <ul>
   *     <li> A non-transient user who is not currently logged in.</li> 
   *     <li> A transient user who is using a brand new session or if their shopping cart is empty.</li>
   *   </ul>
   *   Redirect to a session expiration URL. Otherwise allow the super-class to determine redirect URL.
   * </p>
   * 
   * @param pSuccessURL a <code>String</code> value.
   * @param pFailureURL a <code>String</code> value.
   * @param pRequest the servlet's request.
   * @param pResponse the servlet's response.
   *
   * @exception ServletException if there was an error while executing the code.
   * @exception IOException if there was an error with servlet io.
   *
   * @return If redirect (for whatever reason) to a new page occurred,
   *         return false.  If NO redirect occurred, return true.
   */
  @Override
  public boolean checkFormRedirect (String pSuccessURL,
                                    String pFailureURL,
                                    DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {

    RepositoryItem profile = getProfile();

    if (profile != null) {
    
      boolean isProfileCookieValid = 
        getProfileTools().getCookieManager().cookieProfileIdValid(profile.getRepositoryId(), 
                                                                  pRequest, 
                                                                  pResponse);
      if (((profile.isTransient() && 
          (pRequest.getSession().isNew() || getShoppingCart().isCurrentEmpty())) || !profile.isTransient()) && 
          isFormSubmission(pRequest) &&
          (!getProfileTools().isUserLoggedIn(profile) && !isProfileCookieValid) &&
          getSessionExpirationURL() != null) {
  
        if (AjaxUtils.isAjaxRequest(pRequest)) {
          // When the browser is expecting a JSON response.
          setMakeGiftSelectionErrorURL(getAjaxMakeGiftSelectionTimeoutURL());
        }
        else {
          setMakeGiftSelectionErrorURL(getSessionExpirationURL());
        }
        
        // The session has expired, redirect to error page.
        if (isLoggingDebug()) {
          logDebug("Session expired: redirecting to " +  getSessionExpirationURL());
        }
        
        redirectOrForward(pRequest, pResponse, getMakeGiftSelectionErrorURL()); 
        return false;
      }
    }
    return super.checkFormRedirect(pSuccessURL, pFailureURL, pRequest, pResponse);
  }
}
