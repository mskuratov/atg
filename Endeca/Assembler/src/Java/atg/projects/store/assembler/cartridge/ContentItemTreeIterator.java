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

import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import java.util.*;

/**
 * <p>
 *   An abstract base class which provides the functionality for traversing content item tree.
 * </p>
 * <p>
 *   For each detected content item during tree traversing the <code>process</code> method is called.
 *   The actual implementation of the <code>process</code> method should be provided by the subclasses.
 * </p>
 * <p>
 *   If a circular reference is detected during tree traversing the process is terminated with the
 *   exception.
 * </p>
 * 
 * @author Natallia Paulouskaya
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/ContentItemTreeIterator.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public abstract class ContentItemTreeIterator {

  //-------------------------------------------
  /** Class version string. */
  protected static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Endeca/Assembler/src/atg/projects/store/assembler/cartridge/ContentItemTreeIterator.java#3 $$Change: 788278 $";
    
  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------
  
  private static final String PATH_SEPARATOR = " -> ";
  
  //----------------------------------------------------------------------------
  // MEMBERS
  //----------------------------------------------------------------------------
  
  /**
   * The stack of content items that are already visited by the Iterator. It is used
   * to detect circular references in content item tree.
   */
  private Stack<ContentItem> mVisitedItems = new Stack<ContentItem>();

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  /**
   * The entry point for traversing content item tree. The method first processes the content
   * item passed in and then moves to processing its children. 
   * 
   * @param pContentItem - The content item to process.
   * 
   * @throws CartridgeHandlerException if the content item has already been visited.
   */
  public void traverse(ContentItem pContentItem) throws CartridgeHandlerException {

    if(pContentItem != null) {
          
      // First process current content item.
      process(pContentItem);
            
      // Determine whether we already visited this content item and if so report the
      // circular reference by throwing an exception.
      if(mVisitedItems.contains(pContentItem)) {
        String errorMessage = getCircularReferenceErrorMessage(pContentItem);
        throw new CartridgeHandlerException(errorMessage);
      }
            
      // Put content item into the visited items stack.
      mVisitedItems.push(pContentItem);
            
      // Traverse content item children.
      traverseChildren(pContentItem);
            
      // After the children are processed remove content item from the stack.
      if(!mVisitedItems.empty()) {
        mVisitedItems.pop();
      }
    }
  }

  //----------------------------------------------------------------------------
  /**
   * Returns the error message for the circular reference. Builds the full path to the content item
   * where the circular reference were detected.
   * 
   * @param pContentItem The content item where the circular reference were detected.
   * 
   * @return The error message for the circular reference.
   */
  protected String getCircularReferenceErrorMessage(ContentItem pContentItem) {
    
    // Build the path to the passed in content item.
    StringBuilder circularPath = new StringBuilder();
    
    for(ContentItem item : mVisitedItems) {
      if (circularPath.length() > 0) {
        circularPath.append(PATH_SEPARATOR);
      }
      circularPath.append(item.getType());
    }
  
    String errorMessage = "Circular reference is detected : The content item of type: " +
                          pContentItem.getType() + " already exists on the stack. \n\r" +
                          "The path is: " + circularPath.toString();
      
    return errorMessage;
  }
  
  //----------------------------------------------------------------------------
  /**
   * Traverses the content item's children. When a child content item is detected
   * the method calls the traverse method for it to traverse it further.
   * 
   * @param pContentItem The content item which children to traverse.
   * 
   * @throws CartridgeHandlerException if the content item has already been visited.
   */
  protected void traverseChildren(ContentItem pContentItem) throws CartridgeHandlerException {
    
    for (Object childObject : pContentItem.values()) {
      if (childObject  instanceof ContentItem) {
        traverse((ContentItem) childObject);
        continue;
      }
      if (childObject instanceof List) {
        traverseList((List<?>) childObject);
      }
    }   
  }
  
  //----------------------------------------------------------------------------
  /**
   * Traverses the list of objects. If list members are content items they are
   * passed to traverse method for further processing.
   * 
   * @param pList - The list of objects to traverse.
   * 
   * @throws CartridgeHandlerException if the content item has already been visited.
   */
  protected void traverseList(List<?> pList) throws CartridgeHandlerException {
    for (Object nextObject : pList) {
      if (nextObject instanceof ContentItem) {
        traverse((ContentItem) nextObject);
      }
    }
  }
  
  //----------------------------------------------------------------------------
  /**
   * The method contains processing logic that need to be executed for each
   * detected content item during content item tree traversing.
   * 
   * @param pContentItem The content item to process.
   */
  public abstract void process(ContentItem pContentItem);
    
}
