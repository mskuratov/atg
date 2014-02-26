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
package atg.projects.store.mobile.userprofiling;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import atg.commerce.profile.CommercePropertyManager;
import atg.core.util.Base64;
import atg.crypto.Cipher;
import atg.projects.store.profile.SessionBean;
import atg.projects.store.profile.StoreProfileTools;
import atg.projects.store.profile.StorePropertyManager;

/**
 * Extensions to StoreProfileTools.
 *
 * @author dhomenok
 */
public class MobileStoreProfileTools extends StoreProfileTools {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Mobile/src/atg/projects/store/mobile/userprofiling/MobileStoreProfileTools.java#3 $$Change: 768606 $";

  
  private static final String CREDIT_CARD = "creditCard";
  private static final String BILLING_ADDRESS = "billingAddress";
  private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
  private static final Charset CHARSET_ASCII = Charset.forName("US-ASCII");


  //--------------------------------------------------
  private Cipher mCipher;

  public Cipher getCipher() {
    return mCipher;
  }

  public void setCipher(Cipher pCipher) {
    this.mCipher = pCipher;
  }
  
  /**
   * Restores Credit Card data from session bean to map
   *
   * @param remove a flag to remove the card map from session. 
   *    When true card map removed from session. 
   *    When false - card map left in the session and copy returned.
   */
  public Map restoreCreditCardFromSession(boolean remove, SessionBean pSessionBean) {
    CommercePropertyManager propertyManager = (CommercePropertyManager)getPropertyManager();
    String creditCardNumberPropertyName = propertyManager.getCreditCardNumberPropertyName();

    Map savedCard = (Map) pSessionBean.getValues().get(CREDIT_CARD);

    /**
     * Note, that method 'restoreCreditCardFromSession' may be called when user session has been expired.
     * So session bean may hasn't card value. 
     */
    if (savedCard != null) {
      String encryptedCardNumber = (String) savedCard.get(creditCardNumberPropertyName);

      if (remove) {
        pSessionBean.getValues().remove(CREDIT_CARD);
      } else {
        //Make it to be a copy of the session map to prevent changes the session one. See below decryption of the one of the fields. 
        savedCard = new HashMap(savedCard);
      }

      try {
          savedCard.put(creditCardNumberPropertyName, new String(mCipher.decrypt(Base64.decodeToByteArray(encryptedCardNumber.getBytes(CHARSET_ASCII))), CHARSET_UTF8));
      } catch (GeneralSecurityException e) {
          if (isLoggingError()) {
            logError("Some errors occurred while saving credit card data to session", e);
          }
      }
    }

    return savedCard;
  }

  public void storeCreditCardToSession(Map newCard, SessionBean pSessionBean) {
    StorePropertyManager propertyManager = (StorePropertyManager)getPropertyManager();

    // Get editValue map, containing the credit card properties

    //encrypting credit card number to prevent saving it in open state
    String creditCardNumber = (String) newCard.get(propertyManager.getCreditCardNumberPropertyName());

    try {
      newCard.put(propertyManager.getCreditCardNumberPropertyName(), new String(Base64.encodeToByteArray(mCipher.encrypt(creditCardNumber.toString().getBytes(CHARSET_UTF8))), CHARSET_ASCII));
    } catch (GeneralSecurityException e) {
        if (isLoggingError()) {
          logError("Some errors occurred while restoring credit card data from session", e);
        }
    }
    
    // put credit card info to session bean
    pSessionBean.getValues().put(CREDIT_CARD, newCard);
  }
  
  public void storeBillingAddressToSession(Map billAddr, SessionBean pSessionBean) {
	  pSessionBean.getValues().put(BILLING_ADDRESS, billAddr);
  }
  
  public Map restoreBillingAddressFromSession(SessionBean pSessionBean) {
	  return (Map) pSessionBean.getValues().get(BILLING_ADDRESS);
  }
}
