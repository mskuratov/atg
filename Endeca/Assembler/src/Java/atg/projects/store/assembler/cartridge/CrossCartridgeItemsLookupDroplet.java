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
package atg.projects.store.assembler.cartridge;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;

import atg.endeca.assembler.AssemblerTools;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * <p>
 *   This servlet looks for the items displayed in the configured cartridges. The obtained list of
 *   items is returned as a request parameter. To get the list of items, the droplet invokes the 
 *   ContentItemTreeIterator component configured in the <code>contentItemTreeIterator</code> property.
 *   The ContentItemTreeIterator component performs the whole process of traversing content item tree
 *   and obtaining displayed items from the configured cartridges.
 * </p>
 * 
 * <p>
 *   There are no required input parameters.
 * </p>
 * <p>
 *   The following parameters are rendered:
 * </p>
 * 
 * <dl>
 *   <dt>output</dt>
 *   <dd>Rendered if the list of items is NOT empty.</dd>
 *
 *   <dt>empty</dt>
 *   <dd>Rendered if the list of items is empty.</dd>
 * </dl>
 * 
 * <p>
 *   The output parameters for this servlet are:
 * </p>
 * 
 * <dl>
 *   <dt>items</dt>
 *   <dd>The list of items displayed in configured cartridges.</dd>
 * </dl>
 * 
 * @author Natallia Paulouskaya
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/CrossCartridgeItemsLookupDroplet.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class CrossCartridgeItemsLookupDroplet extends DynamoServlet {

  //----------------------------------
  /** Class version string */
  public static String CLASS_VERSION = 
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/CrossCartridgeItemsLookupDroplet.java#3 $$Change: 788278 $";
    
  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------
  
  private static final String ROOT_CONTENT_ITEM_ATTRIBUTE = "rootContentItem";

  static final String ITEMS = "items";
  static final String OUTPUT_OPARAM = "output";
  static final String EMPTY_OPARAM = "empty";
  
  //----------------------------------------------------------------------------
  // Properties
  //----------------------------------------------------------------------------
    
  //--------------------------------------------------------
  // property: contentItemTreeIterator
  //--------------------------------------------------------
  CrossCartridgeItemsLookup mContentItemTreeIterator = null;  
  
  /**
   * @return the ContentItemTreeIterator component responsible for walking through 
   *         content item tree and looking for displayed items.
   */
  public CrossCartridgeItemsLookup getContentItemTreeIterator() {
    return mContentItemTreeIterator;
  }

  /**
   * @param pContentItemTreeIterator - The ContentItemTreeIterator component responsible 
   *                                   for walking through content item tree and looking 
   *                                   for displayed items.
   */
  public void setContentItemTreeIterator(CrossCartridgeItemsLookup pContentItemTreeIterator) {
    this.mContentItemTreeIterator = pContentItemTreeIterator;
  }

  //----------------------------------------------------------------------------
  //  METHODS
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  /**
   * <p>
   *   This method invokes the configured <code>ContentItemTreeIterator</code>, passing the root 
   *   content item as a parameter.
   * </p>
   * <p>
   *   The <code>ContentItemTreeIterator</code> traverses the content item tree looking for the 
   *   specified content items and retrieves the displayed items from them. The found displayed 
   *   items are returned as an <code>items</code> request parameter.
   * </p>
   * 
   * @param pRequest - The HTTP request object.
   * @param pResponse - The HTTP response object. 
   * 
   * @throws ServletException
   * @throws IOException
   */
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    // Get root content item.
    ContentItem rootContentItem = (ContentItem) pRequest.getAttribute(ROOT_CONTENT_ITEM_ATTRIBUTE);    
    
    // Traverse the content item tree looking for the specified content items.    
    try {
      getContentItemTreeIterator().traverse(rootContentItem);
    } 
    catch (CartridgeHandlerException che) {
      AssemblerTools.getApplicationLogging().vlogDebug(
        che, "There was a problem tranversing the content item tree for item {0} ", rootContentItem);
    }
    
    List<String> displayedItems = getContentItemTreeIterator().getDisplayedItems();
    
    if (displayedItems != null && !displayedItems.isEmpty()) {
      pRequest.setParameter(ITEMS, displayedItems);
      pRequest.serviceLocalParameter(OUTPUT_OPARAM, pRequest, pResponse);      
    }
    else {    
      pRequest.serviceLocalParameter(EMPTY_OPARAM, pRequest, pResponse);
    }
  }

}
