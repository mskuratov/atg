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

package atg.projects.store.crypto;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import atg.core.util.Base64;
import atg.nucleus.GenericService;
import atg.nucleus.PropertyValueDecoder;
import atg.nucleus.ServiceException;

/**
 * Custom {@code PropertyValueDecode} implementation.
 * This implementation requires password to be stored as Base64 coded AES cipher text.
 * I.e. it takes input value, decodes it with Base64 algorithm and then decodes it with AES.
 * Result will be treated as a UTF-8 string.
 * 
 * @see PropertyValueDecoder
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/crypto/AESPropertyDecoder.java#2 $$Change: 768606 $
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class AESPropertyDecoder extends GenericService implements PropertyValueDecoder {
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/crypto/AESPropertyDecoder.java#2 $$Change: 768606 $";
  
  // Encryption algorithm.
  private static final String ENCRYPTION_ALGORITHM = "AES";
  // Secret key for this algorithm. Can't find proper place for it, think about moving this key to another storage.
  private static final String CIPHER_KEY = "3/X9gy7SdKaGTJPFEYKpSA==";
  // Default charset to be used.
  private static final String CHARSET_UTF8 = "UTF-8";
  
  private Cipher mCipher;
  private String mSecurityProviderClass;
  
  /**
   * Getter method for the <code>securityProviderClass</code> property. This property specifies a
   * fully qualified Security Provider implementation class to be used.
   * @return Security Provider implementation class.
   */
  public String getSecurityProviderClass() {
    return mSecurityProviderClass;
  }

  public void setSecurityProviderClass(String pSecurityProviderClass) {
    mSecurityProviderClass = pSecurityProviderClass;
  }

  @Override
  public void doStartService() throws ServiceException {
    super.doStartService();
    
    try {
      // Use the provider specified. It must provide AES cipher implementation.
      Provider provider = (Provider) Class.forName(getSecurityProviderClass()).newInstance();
      Security.addProvider(provider);
      Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM, provider);
      // Cipher key is base64 encoded too (because it's an array of bytes actually).
      Key secret = new SecretKeySpec(Base64.decodeToByteArray(CIPHER_KEY), ENCRYPTION_ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, secret);
      mCipher = cipher;
    } catch (InstantiationException e) {
      throw new ServiceException("Can't instantiate a Security Provider class.", e);
    } catch (IllegalAccessException e) {
      throw new ServiceException("Security Provider class' constructor is inaccessible.", e);
    } catch (ClassNotFoundException e) {
      throw new ServiceException("Can't find Security Provider class.", e);
    } catch (NoSuchAlgorithmException e) {
      throw new ServiceException("'" + ENCRYPTION_ALGORITHM + "' algorithm is not implemented by the Security Provider specified.", e);
    } catch (NoSuchPaddingException e) {
      throw new ServiceException("Specified padding is not implemented by the Security Provider specified.", e);
    } catch (InvalidKeyException e) {
      throw new ServiceException("Specified Secret Key is invalid.", e);
    }
  }
  
  // Make this method synchronized. Cipher implementations are not thread-safe, hence we have to restrict this method usage.
  @Override
  public synchronized String decode(String pValue) {
    try {
      // Input property is considered as base64 encoded cipher text. Decode and decrypt it.
      byte[] encodedRaw = mCipher.doFinal(Base64.decodeToByteArray(pValue));
      // Consider output bytes as UTF-8 string. Never use default charsets, they may vary from machine to machine!
      return new String(encodedRaw, CHARSET_UTF8);
    } catch (GeneralSecurityException e) {
      // Wrong input value specified.
      throw new IllegalArgumentException("Can't perform password decrypting.", e);
    } catch (UnsupportedEncodingException e) {
      // This can't happen, because all JVMs should have a UTF-8 charset implementation.
      throw new IllegalArgumentException("Should never happen.", e);
    }
  }

  @Override
  public Object decode(Object pValue) {
    // We can operate with Strings only. Discard all other input values.
    if (pValue instanceof String) {
      return decode((String) pValue);
    }
    throw new IllegalArgumentException("Only String property values supported.");
  }
}
