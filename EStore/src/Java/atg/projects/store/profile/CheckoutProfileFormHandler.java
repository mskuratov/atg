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

import atg.core.util.StringUtils;
import atg.projects.store.order.StoreOrderHolder;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import java.io.IOException;
import javax.servlet.ServletException;

/**
 * This is an EStore's form handler to be used when logging in a user during the checkout process.
 * This form handler supports three operations:
 * <dl>
 *   <dt>returningCustomer</dt>
 *   <dd>This process tries to log in an existing customer.</dd>
 *   <dt>newCustomer</dt>
 *   <dd>This process pre-validates an e-mail to be used as login for the new user. It also logs user out, if he's auto-logged in.</dd>
 *   <dt>anonymousCustomer</dt>
 *   <dd>This process logs the user out, if he's auto-logged in.</dd>
 * </dl>
 * Note that only <code>loginSuccessURL</code> and <code>loginErrorURL</code> properties are being used by this form handler
 * (though it can log out a user).
 * <br/>
 * User's login must be specified through the <code>emailAddress</code> property. User's password must be specified with the
 * <code>value.password</code> property.
 * @author ATG
 */
public class CheckoutProfileFormHandler extends StoreProfileFormHandler {
  
  //-----------------------------------
  // STATIC
  //-----------------------------------

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/profile/CheckoutProfileFormHandler.java#3 $$Change: 789818 $";
  
  // New email address constant
  protected static final String NEW_EMAIL_ADDRESS = "newEmailAddress";
  
  /**
   * The name of request parameter indicating whether user is logging in during checkout.
   */
  public static final String LOGIN_DURING_CHECKOUT_PARAMETER="userCheckingOut";

  //-----------------------------------
  // METHODS
  //-----------------------------------

  /**
   * It's a getter method for the <code>storeProfileTools</code> read-only property.
   * This method converts the value of the <code>profileTools</code> property to the {@link StoreProfileTools} type.
   * @return <code>StoreProfileTools</code> instance.
   */
  public StoreProfileTools getStoreProfileTools() {
    return (StoreProfileTools) super.getProfileTools();
  }

  /**
   * This is a <tt>Log In</tt> process for the returning customer.
   * <br/>
   * There must be set two properties, <code>emailAddress</code> (will be used as user login) and <code>value.password</code>
   * (user password). If <code>loginSuccessURL</code> and <code>loginErrorURL</code> properties are set, the user will be redirected
   * to the specified URLs on login success or error correspondingly.
   * <br/>
   * If the user is already explicitly logged, there will be added a form exception. If wrong e-mail address is specified, there will be
   * added a form exception.
   * <br/>
   * Successful process will log the user in.
   * @param pRequest current HTTP request.
   * @param pResponse current HTTP response.
   * @return true, if request is not redirected; false otherwise.
   * @throws ServletException if something goes wrong.
   * @throws IOException if something goes wrong.
   */
  public boolean handleReturningCustomer(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
          throws ServletException, IOException {
    // First, check if the user is already logged in. If true, we should not do anything, just display him an error message.
    if (isUserExplicitlyLoggedIn()) {
      addFormException(MSG_ALREADY_LOGGEDIN, getAbsoluteName(), pRequest);
    }
    
    if (getFormError()){
      // Redirect if user is already explicitly logged in or required fields are missing
      return checkFormRedirect(getLoginSuccessURL(), getLoginErrorURL(), pRequest, pResponse);
    }
   
    // Set input properties to be used by super-implementation of this form handler.
    getValue().put(getStorePropertyManager().getLoginPropertyName(), getEmailAddress().toLowerCase());
    
    // add parameter indicating that user is logging in from checkout flow
    pRequest.setParameter(LOGIN_DURING_CHECKOUT_PARAMETER, true);
    
    // Login the user with e-mail and password specified.
    return handleLogin(pRequest, pResponse);
  }

  /**
   * This is a <tt>Log In</tt> process for the new customer.
   * <br/>
   * There must be set <code>emailAddress</code> property (will be used as user login).
   * If <code>loginSuccessURL</code> and <code>loginErrorURL</code> properties are set, the user will be redirected
   * to the specified URLs on login success or error correspondingly.
   * <br/>
   * If the user is already explicitly logged, there will be added a form exception. If wrong e-mail address is specified, there will be
   * added a form exception. If the user with e-mail specified already exists, there will be added a form exception.
   * <br/>
   * Actually, this is a <tt>Log Out</tt> process, that is successfully finished process will make current user anonymous.
   * There also will be added current shopping cart's order ID to the request parameters.
   * @param pRequest current HTTP request.
   * @param pResponse current HTTP response.
   * @return true, if request is not redirected; false otherwise.
   * @throws ServletException if something goes wrong.
   * @throws IOException if something goes wrong.
   */
  public boolean handleNewCustomer(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
          throws ServletException, IOException {
    // First, check if the user is already logged in. If true, do not do anything, just display him an error message.
    if (isUserExplicitlyLoggedIn()) {
      addFormException(MSG_ALREADY_LOGGEDIN, getAbsoluteName(), pRequest);
    }
    
    if (getFormError()){
      // Redirect if user is already explicitly logged in or required fields are missing
      return checkFormRedirect(getLoginSuccessURL(), getLoginErrorURL(), pRequest, pResponse);
    }
    
    // Then, validate input e-mail address.
    if (!getStoreProfileTools().validateEmailAddress(getEmailAddress())) {
      addFormException(MSG_INVALID_EMAIL, getAbsoluteName(), pRequest);
    }
    // Then, check if input e-mail is already used by some other user.
    if (getStoreProfileTools().isDuplicateEmailAddress(getEmailAddress())) {
      addFormException(MSG_USER_ALREADY_EXISTS, getAbsoluteName(), pRequest);
    }
    // If the user is auto-logged in, we should log him out.
    if (!isUserAnonymous()) {
      return logOutUser(pRequest, pResponse);
    }
    // User is not logged-in, we will not do anything, just pass the e-mail he entered.
    pRequest.addQueryParameter(NEW_EMAIL_ADDRESS, getEmailAddress());
    // Redirect the user to the proper place.
    return checkFormRedirect(getLoginSuccessURL(), getLoginErrorURL(), pRequest, pResponse);
  }

  /**
   * This is a <tt>Log In</tt> process for the anonymous customer.
   * <br/>
   * If <code>loginSuccessURL</code> and <code>loginErrorURL</code> properties are set, the user will be redirected
   * to the specified URLs on login success or error correspondingly.
   * <br/>
   * If the user is already explicitly logged, there will be added a form exception.
   * <br/>
   * Actually, this is a <tt>Log Out</tt> process, that is successfully finished process will make current user anonymous.
   * There also will be added current shopping cart's order ID to the request parameters.
   * @param pRequest current HTTP request.
   * @param pResponse current HTTP response.
   * @return true, if request is not redirected; false otherwise.
   * @throws ServletException if something goes wrong.
   * @throws IOException if something goes wrong.
   */
  public boolean handleAnonymousCustomer(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
          throws ServletException, IOException {
    // First, check if the user is already logged in. If true, do not do anything, just display him an error message.
    if (isUserExplicitlyLoggedIn()) {
      addFormException(MSG_ALREADY_LOGGEDIN, getAbsoluteName(), pRequest);
    }
    
    if (getFormError()){
      // Redirect if user is already explicitly logged in or required fields are missing
      return checkFormRedirect(getLoginSuccessURL(), getLoginErrorURL(), pRequest, pResponse);
    }
    
    // If the user is auto-logged in, we should log him out.
    if (!isUserAnonymous()) {
      return logOutUser(pRequest, pResponse);
    }
    // Redirect the user to the proper place.
    return checkFormRedirect(getLoginSuccessURL(), getLoginErrorURL(), pRequest, pResponse);
  }

  /**
   * This implementation adds two checkout-specific request parameters, if no errors occurred during the logout process.
   * The following parameters are added:
   * <dl>
   * <dt>{@link StoreOrderHolder#LOGOUT_ORDER_ID}</dt>
   * <dd>This parameter will contain current shopping cart's order ID.</dd>
   * <dt>{@link #NEW_EMAIL_ADDRESS}</dt>
   * <dd>This parameter will contain a desired user login (if <tt>New User</tt> process selected).</dd>
   * </dl>
   * @param pRequest current HTTP request.
   * @param pResponse current HTTP response.
   * @throws ServletException if something goes wrong.
   * @throws IOException if something goes wrong.
   */
  @Override
  public void postLogoutUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    // Do all session-related work, etc.
    super.postLogoutUser(pRequest, pResponse);
    if (!getFormError()) {
      // Successfully logged-out, save current shopping cart ID for future use.
      pRequest.addQueryParameter(StoreOrderHolder.LOGOUT_ORDER_ID, getShoppingCart().getCurrent().getId());
      if (!StringUtils.isEmpty(getEmailAddress())) {
        // Some e-mail has been entered, pass it for future use.
        pRequest.addQueryParameter(NEW_EMAIL_ADDRESS, getEmailAddress());
      }
    }
  }

  /**
   * This method determines, if the user is already explicitly logged in.
   * @return true, if the user is logged in. False otherwise.
   */
  private boolean isUserExplicitlyLoggedIn() {
    // User is explicitly logged in, if his securityStatus is more than or equal to security status login.
    String securityStatusPropertyName = getStorePropertyManager().getSecurityStatusPropertyName();
    int securityStatus = ((Integer) getProfile().getPropertyValue(securityStatusPropertyName)).intValue();
    return securityStatus >= getStorePropertyManager().getSecurityStatusLogin();
  }

  /**
   * This method determines, if the user is anonym.
   * @return true, if the user is an anonym. False otherwise.
   */
  private boolean isUserAnonymous() {
    // User is anonymous, if his profile is transient.
    return getProfile().isTransient();
  }

  /**
   * This method logs the user out. The request will be redirected to the <code>loginSuccessURL</code> or <code>loginErrorURL</code>,
   * if the logout succeeds or not correspondingly. This method also adds current shopping cart's order ID to request's query parameters.
   * @param pRequest current HTTP request.
   * @param pResponse current HTTP response.
   * @return true, if request is not redirected; false otherwise.
   * @throws ServletException if there was an error while executing the code
   * @throws IOException if there was an error with servlet io
   */
  private boolean logOutUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    // Set proper success/error URLs to redirect during logout process.
    setLogoutSuccessURL(getLoginSuccessURL());
    setLogoutErrorURL(getLoginErrorURL());
    // Logout the user.
    return handleLogout(pRequest, pResponse);
  }
}
