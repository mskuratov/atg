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


package atg.projects.store.repository;

import atg.adapter.gsa.GSAPropertyDescriptor;
import atg.core.util.Base64;
import atg.crypto.Cipher;
import atg.nucleus.Nucleus;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

/**
 * A property descriptor to encrypt and decrypt string values.
 * <p>
 * You must pass in a {@link Cipher} implementation class by adding an <code>&lt;attribute&gt;</code> tag to
 * your property definition as follows:
 * <br/>
 * <pre>
 * &lt;table name="table_name"&gt;
 *   &lt;property name="propertyName" property-type="atg.projects.store.repository.EncryptionPropertyDescriptor"&gt;
 *     &lt;attribute name="cipher" value="/path/to/CipherComponent"/&gt;
 *   &lt;/property&gt;
 * &lt;/table&gt;
 * </pre>
 *
 * @author ATG
 * @version $1.1$
 */
public class EncryptionPropertyDescriptor extends GSAPropertyDescriptor {
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/repository/EncryptionPropertyDescriptor.java#2 $$Change: 768606 $";

  private static final long serialVersionUID = 1L;
  private static final String TYPE_NAME = "encrypted";
  private static final String CIPHER_COMPONENT = "cipher";
  private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
  private static final Charset CHARSET_ASCII = Charset.forName("US-ASCII");

  static {
    RepositoryPropertyDescriptor.registerPropertyDescriptorClass(TYPE_NAME, EncryptionPropertyDescriptor.class);
  }

  /*
   * Do not serialize this field directly, because Cipher is not Serializable.
   * During the deserialization process SerializableFeatureDescriptor calls setValue method.
   * This method will restore cipher instance (it will resolve Nucleus component by name stored with 'cipher' attribute value).
   */
  private transient Cipher mCipher;

  /**
   * @return property Queryable
   */
  @Override
  public boolean isQueryable() {
    /*
     * Encrypted properties can't be included into any repository searches.
     * That's why <code>isQueryable</code> method must return <code>false</code>.
     */
    return false;
  }

  /**
   * @param pItem the item
   * @param pValue the value to set   
   */
  @Override
  public void setPropertyValue(RepositoryItemImpl pItem, Object pValue) {
    if (pValue == null) {
      return;
    }
    
    try {
      /*
       * Raw bytes to be encrypted are obtained in the UTF-8 charset.
       * This provides necessary internationalization support; this charset must be available on all JVM implementations.
       * Base64-encoded encrypted bytes are stored into database in form of ASCII string.
       * This is ok, because Base64 encodes bytes into ASCII-compatible symbols.
       */
      super.setPropertyValue(pItem, new String(Base64.encodeToByteArray(mCipher.encrypt(pValue.toString().getBytes(CHARSET_UTF8))), CHARSET_ASCII));
    } catch (GeneralSecurityException e) {
      throw new IllegalArgumentException("Failed to encrypt property " + getItemDescriptor().getItemDescriptorName(), e);
    }
  }

  /**
  * @param pItem the item
  * @param pValue the value to get
  * @return the property value   
  */
  @Override
  public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue) {
    if ((pValue == null) || (pValue.equals(RepositoryItemImpl.NULL_OBJECT))) {
      return null;
    }

    try {
      /*
       * This is an inverse process to the setPropertyValue, hence database data should be treated as an ASCII-compatible string.
       * Decrypted data should be treated as a UTF-8 string.
       */
      return super.getPropertyValue(pItem, new String(mCipher.decrypt(Base64.decodeToByteArray(pValue.toString().getBytes(CHARSET_ASCII))), CHARSET_UTF8));
    } catch (GeneralSecurityException e) {
      throw new IllegalArgumentException("Failed to decrypt property " + getItemDescriptor().getItemDescriptorName(), e);
    }
  }

  /**
   * Associate a named attribute with this feature.
   * @param pAttributeName the named attribute
   * @param pValue the value
   */
  @Override
  public void setValue(String pAttributeName, Object pValue) {
    super.setValue(pAttributeName, pValue);

    if ((pValue == null) || (pAttributeName == null)) {
      return;
    }

    // Save Cipher component specified for future use.
    if (pAttributeName.equalsIgnoreCase(CIPHER_COMPONENT)) {
      resolveCipherComponent(pValue.toString());
    }
  }

  /**
   * @param pComponentName the component name to resolve
   */
  private void resolveCipherComponent(String pComponentName) {
    try {
      Nucleus nucleus = Nucleus.getGlobalNucleus();
      if (nucleus != null) {
        mCipher = (Cipher) nucleus.resolveName(pComponentName);
      }
    } catch (ClassCastException e) {
      throw new IllegalArgumentException("Invalid component specified as Cipher. It must implement atg.crypto.Cipher interface.", e);
    }
  }

  /**
   * @return the TYPE_NAME 
   */
  @Override
  public String getTypeName() {
    return TYPE_NAME;
  }

  /**
   * @return the type property
   */
  @Override
  public Class getPropertyType() {
    return java.lang.String.class;
  }

  /**
   * @param pClass the property type to set
   */
  @Override
  public void setPropertyType(Class pClass) {
    if (pClass != java.lang.String.class) {
      throw new IllegalArgumentException("Enncrypted properties must be of java.lang.String type.");
    }
    super.setPropertyType(pClass);
  }

  /**
   * @param pClass the component property type to set
   */
  @Override
  public void setComponentPropertyType(Class pClass) {
    if (pClass != null) {
      throw new IllegalArgumentException("Encrypted properties must be scalars.");
    }
  }

  /**
   * @param pDesc the propertyItemDescriptor to set
   */
  @Override
  public void setPropertyItemDescriptor(RepositoryItemDescriptor pDesc) {
    if (pDesc != null) {
      throw new IllegalArgumentException("Encrypted properties must be of java.lang.String type.");
    }
  }

  /**
   * @param pDesc the repositoryItemDescriptor 
   */
  @Override
  public void setComponentItemDescriptor(RepositoryItemDescriptor pDesc) {
    if (pDesc != null) {
      throw new IllegalArgumentException("Encrypted properties must be scalars.");
    }
  }
}
