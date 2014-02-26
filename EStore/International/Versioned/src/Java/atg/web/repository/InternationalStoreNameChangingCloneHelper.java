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



package atg.web.repository;

import atg.beans.DynamicPropertyDescriptor;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryUtils;

/**
 * <p>
 * This NameChangingCloneHelper extension changes the displayNameDefault property of a 
 * cloned item to something like "Copy of Foo" or "Foo (copy)".
 * </p>
 *
 * @author David Stewart
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/Versioned/src/atg/web/repository/InternationalStoreNameChangingCloneHelper.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 **/

public class InternationalStoreNameChangingCloneHelper extends NameChangingCloneHelper {

  //-------------------------------------
  // CONSTANTS
  //-------------------------------------
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/International/Versioned/src/atg/web/repository/InternationalStoreNameChangingCloneHelper.java#3 $$Change: 788278 $";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------
  
  //--------------------------------------------------
  //  property: derivedDisplayNamePropertyName
  //--------------------------------------------------
  private String mDerivedDisplayNamePropertyName = "displayNameDefault";
  
  //----------------------------------------------------------------------------
  /**
   * @param pDerivedDisplayNamePropertyName The derived display name property name.
   */
  public void setDerivedDisplayNamePropertyName(String pDerivedDisplayNamePropertyName) {
    mDerivedDisplayNamePropertyName = pDerivedDisplayNamePropertyName;
  }
  
  //----------------------------------------------------------------------------
  /**
   * @return The derived display name property name.
   */
  public String getDerivedDisplayNamePropertyName() {
    return mDerivedDisplayNamePropertyName;
  }

  
  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  //----------------------------------------------------------------------------
  /**
   * Add the prefix and suffix to any unique properties and to displayName/displayNameDefault.
   * 
   * @param pSourceItem The original item that is being cloned.
   * @param pClone The cloned item.
   * 
   * @throws RepositoryException
   */
  @Override
  public void postClone(RepositoryItem pSourceItem, RepositoryItem pClone) throws RepositoryException {

    super.postClone(pSourceItem, pClone);

    DynamicPropertyDescriptor pd = 
      pClone.getItemDescriptor().getPropertyDescriptor(getDerivedDisplayNamePropertyName());
    
    // Ensure that the displayNameDefault property is writable.
    if (pd != null && pd.isWritable()) {
      if (isLoggingDebug()) {
        logDebug("The display name property has been derived to " + getDerivedDisplayNamePropertyName() +
          " so the cloneNamePrefix and/or cloneNameSuffix will be added to this property.");
      }
      
      MutableRepositoryItem mutClone = RepositoryUtils.getMutableRepositoryItem(pClone);
      Object propertyName = pClone.getPropertyValue(getDerivedDisplayNamePropertyName());

      // Is the property descriptor unique? 
      Object nameIsUniqueObj = pd.getValue("unique");
      boolean nameIsUnique = nameIsUniqueObj != null && ((String)nameIsUniqueObj).equals("true");

      // Check for the property type and if not String, log a debug message.
      if (propertyName instanceof String) {
        
        // Add the prefix and/or suffix to the derived display name and see if we already have 
        // an item with this name. If so, then we need to add an incrementing int to the display name. 
        String derivedDisplayName = (String) pClone.getPropertyValue(getDerivedDisplayNamePropertyName());
        
        // Starting with one because first copy should not get a number and second copy should get number 2. 
        int ii=1; 
        String newDerivedDisplayName;
        
        while (true) {
          newDerivedDisplayName = addPrefixAndSuffix(derivedDisplayName, ii);

          // If the derivedDisplayName does not need to be unique, and we aren't always making 
          // unique names then we can break out of this loop and keep the name 
          if (!nameIsUnique && !getAlwaysMakeUniqueNames()) {
            break;
          }

          // The derived display name we create must be unique. See if this one exists...
          if (!itemExists(pSourceItem.getItemDescriptor(), getDerivedDisplayNamePropertyName(), newDerivedDisplayName)) {
            break;
          }
          
          ii++;
        }
        
        // Update the displayNameDefault property with the new name..
        mutClone.setPropertyValue(getDerivedDisplayNamePropertyName(), newDerivedDisplayName);
        
        // Save the changes
        ((MutableRepository)mutClone.getRepository()).updateItem(mutClone);
      }
      else {
        if (isLoggingDebug()) {
          logDebug("Property " + getDerivedDisplayNamePropertyName() + " is not of type String for " +
            "the cloned item of type: " + pClone.getItemDescriptor().getItemDescriptorName());
        }
      } 
    }
    else {
      if (isLoggingDebug()) {
        logDebug("Properties cloneNamePrefix and/or cloneNameSuffix are set, but the " + 
          getDerivedDisplayNamePropertyName() + " property is unwritable for the cloned item of type: " + 
        	pClone.getItemDescriptor().getItemDescriptorName());
      }
    }
  }

}
