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

package atg.projects.store.assembler.cartridge.handler;

import java.util.List;

import atg.endeca.assembler.navigation.filter.RangeFilterBuilder;
import atg.projects.store.assembler.cartridge.StoreCartridgeTools;

import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.cartridge.RecordSpotlightHandler;
import com.endeca.infront.cartridge.RecordSpotlightConfig;
import com.endeca.infront.navigation.model.FilterState;
import com.endeca.infront.navigation.model.RangeFilter;

/**
 * Extended RecordSpotlightHandler that implements some specific filtering.
 * 
 * @author David Stewart
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/handler/FilteringRecordSpotlightHandler.java#2 $$Change: 791340 $
 * @updated $DateTime: 2013/02/19 09:03:40 $$Author: abakinou $
 */
public class FilteringRecordSpotlightHandler extends RecordSpotlightHandler {

  /** Class version string. */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/handler/FilteringRecordSpotlightHandler.java#2 $$Change: 791340 $";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //-------------------------------------------------------
  // property: rangeFilterBuilders
  //-------------------------------------------------------
  private RangeFilterBuilder[] mRangeFilterBuilders = null;

  /**
   * @return an array of RangeFilterBuilders.
   */
  public RangeFilterBuilder[] getRangeFilterBuilders() {
    return mRangeFilterBuilders;
  }

  /**
   * @param pRangeFilterBuilders - An array of RangeFilterBuilders.
   *          -
   */
  public void setRangeFilterBuilders(RangeFilterBuilder[] pRangeFilterBuilders) {
    mRangeFilterBuilders = pRangeFilterBuilders;
  }

  //--------------------------------------------------------
  // property: storeCartridgeTools
  //--------------------------------------------------------
  protected StoreCartridgeTools mStoreCartridgeTools = null;

  /**
   * @return the StoreCartridgeTools helper component.
   */
  public StoreCartridgeTools getStoreCartridgeTools() {
    return mStoreCartridgeTools;
  }
  
  /**
   * @param pStoreCartridgeTools - the StoreCartridgeTools helper component.
   */
  public void setStoreCartridgeTools(StoreCartridgeTools pStoreCartridgeTools) {
    mStoreCartridgeTools = pStoreCartridgeTools;
  }
  
  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------
  
  //---------------------------------------------------------------------------
  /**
   * This overridden method ensures that the appropriate filters are added to the
   * cartridgeConfig's record selection filter state.
   * 
   * @param pCartridgeConfig - The RecordSpotlight cartridge configuration.
   * 
   * @throws CartridgeHandlerException if an error occurs that is scoped to an individual cartridge 
   *                                   instance. This exception will not halt the entire assembly process, 
   *                                   which occurs across multiple cartridges; instead, this exception will 
   *                                   be packaged in the overall response model. If an unchecked exception 
   *                                   is thrown, then the entire assembly process will be halted.
   */
  @Override
  public void preprocess(RecordSpotlightConfig pCartridgeConfig) throws CartridgeHandlerException {

    FilterState filterState = pCartridgeConfig.getRecordSelection().getFilterState();

    if (filterState != null) {
      // Update the range filter list with RangeFilters generated by the RangeFilterBuilders.
      List<RangeFilter> rangeFilters = 
        getStoreCartridgeTools().updateRangeFilters(filterState.getRangeFilters(), getRangeFilterBuilders());
      
      filterState.setRangeFilters(rangeFilters);
    }
    
    super.preprocess(pCartridgeConfig);
  }

}