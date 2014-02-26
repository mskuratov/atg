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

import atg.core.util.Address;

/**
 * Contains useful functions for Address manipulation
 * 
 * @author ATG
 */
public class StoreAddressTools {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/profile/StoreAddressTools.java#2 $$Change: 768606 $";


  /**
   * Compares the properties of two addresses equality. If all properties are
   * equal then the addresses are equal.
   * 
   * @param pAddressA An Address
   * @param pAddressB An Address
   * @return A boolean indicating whether or not pAddressA and pAddressB 
   * represent the same address.
   */
  public static boolean compare(Address pAddressA, Address pAddressB){
    
    if((pAddressA == null) && (pAddressB != null)){
      return false;
    }
    if((pAddressA != null) && (pAddressB == null)){
      return false;
    }
    if((pAddressA == null) || (pAddressB == null)){
      return true;
    }

    if(!(pAddressA instanceof Address)){
      return false;
    }
    
    if(!(pAddressB instanceof Address)){
      return false;
    }
        
    String aFirstName = pAddressA.getFirstName();
    String bFirstName = pAddressB.getFirstName();

    String aLastName = pAddressA.getLastName();
    String bLastName = pAddressB.getLastName();

    String aAddress1 = pAddressA.getAddress1();
    String bAddress1 = pAddressB.getAddress1();

    String aAddress2 = pAddressA.getAddress2();
    String bAddress2 = pAddressB.getAddress2();

    String aCity = pAddressA.getCity();
    String bCity = pAddressB.getCity();

    String aState = pAddressA.getState();
    String bState = pAddressB.getState();

    String aPostalCode = pAddressA.getPostalCode();
    String bPostalCode = pAddressB.getPostalCode();

    String aCountry = pAddressA.getCountry();
    String bCountry = pAddressB.getCountry();

    if((aFirstName == null) && ((bFirstName != null))){
      return false;
    }
    if((aLastName == null) && ((bLastName != null))){
      return false;
    }
    if((aAddress1 == null) && ((bAddress1 != null))){
      return false;
    }
    if((aAddress2 == null) && ((bAddress2 != null))){
      return false;
    }
    if((aCity == null) && ((bCity != null))){
      return false;
    }
    if((aState == null) && ((bState != null))){
      return false;
    }
    if((aPostalCode == null) && ((bPostalCode != null))){
      return false;
    }
    if((aCountry == null) && ((bCountry != null))){
      return false;
    }
    
    if(
       (((aFirstName == null) && (bFirstName == null)) ||
         (aFirstName.equals(bFirstName)))
       &&
       (((aLastName == null) && (bLastName == null)) ||
         (aLastName.equals(bLastName)))
       &&
       (((aAddress1 == null) && (bAddress1 == null)) ||
         (aAddress1.equals(bAddress1)))
       &&
       (((aAddress2 == null) && (bAddress2 == null)) ||
         (aAddress2.equals(bAddress2)))
       &&
       (((aCity == null) && (bCity == null)) ||
         (aCity.equals(bCity)))
       &&
       (((aState == null) && (bState == null)) ||
         (aState.equals(bState)))
       &&
       (((aPostalCode == null) && (bPostalCode == null)) ||
         (aPostalCode.equals(bPostalCode)))
       &&
       (((aCountry == null) && (bCountry == null)) ||
         (aCountry.equals(bCountry)))
       )
    {
      return true;
    }
    else {
      return false;
    }
  }
}
