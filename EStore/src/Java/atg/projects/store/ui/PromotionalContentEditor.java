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


package atg.projects.store.ui;

import atg.beans.DynamicBeanState;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.search.refinement.CommerceFacetSourceItem;

import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.ui.repository.RepositoryItemTable;
import atg.ui.repository.RepositoryResources;
import atg.ui.repository.model.RepositoryFolderNode;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * An ACC editor for RepositoryItems of type "promotionalContent". The path
 * property is auto calculated from the name and parent folder.
 *
 * @author ATG
 * @version $Revision: #2 $
 */
public class PromotionalContentEditor extends RepositoryItemTable implements PropertyChangeListener {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/ui/PromotionalContentEditor.java#2 $$Change: 768606 $";

  /**
   * File name property name.
   */
  public static final String NAME_PROPERTY = RepositoryResources.getString("folderFileNameProperty");

  /**
   * Folder path property name.
   */
  public static final String PATH_PROPERTY = RepositoryResources.getString("folderPathProperty");

  /**
   * Parent folder property name.
   */
  public static final String PARENT_FOLDER_PROPERTY = RepositoryResources.getString("folderParentFolderProperty");

  /**
   * Loger for the class
   */
  public static ApplicationLoggingImpl sLogger =
    (ApplicationLoggingImpl)ClassLoggingFactory.getFactory().getLoggerForClass(CommerceFacetSourceItem.class);

  /**
   * Bean.
   */
  protected Object mBean;

  /**
   * Create a card panel for each type of folder and editing components for
   * each type's properties.
   */
  public PromotionalContentEditor() {
    super();

    String[] readOnly = { PATH_PROPERTY };
    setReadOnlyProperties(readOnly);
  }

  /**
   * Override to add/remove propertyChangeListeners.
   *
   * @param pBean - bean object
   */
  public void setBean(Object pBean) {
    if (mBean instanceof DynamicBeanState) {
      ((DynamicBeanState) mBean).removePropertyChangeListener(this);
    }

    mBean = pBean;

    if (mBean instanceof DynamicBeanState) {
      ((DynamicBeanState) mBean).addPropertyChangeListener(this);
    }

    super.setBean(mBean);
  }

  //---------------------------------
  // PropertyChangeLister interface.
  //-------------------------------

  /**
   * If the file name is updated, update the ImageEditor file name field.
   *
   * @param pEvent "PropertyChange" event
   */
  public void propertyChange(PropertyChangeEvent pEvent) {
    String name = pEvent.getPropertyName();
    Object newValue = pEvent.getNewValue();

    try {
      // when the "parentFolder" is updated, update the "path"
      if (name.equals(PARENT_FOLDER_PROPERTY)) {
        // the "path" is the folder path plus the file name 
        String fileName = (String) DynamicBeans.getPropertyValue(mBean, NAME_PROPERTY);
        String folderPath = null;

        if (newValue instanceof RepositoryFolderNode) {
          folderPath = ((RepositoryFolderNode) newValue).getItemPath();
        }

        String newPath = constructPath(fileName, folderPath);

        DynamicBeans.setPropertyValue(mBean, PATH_PROPERTY, newPath);
      }

      // when the file "name" changes, update the "path"
      if (name.equals(NAME_PROPERTY)) {
        // the "path" is the folder path plus the file name
        Object obj = DynamicBeans.getPropertyValue(mBean, PARENT_FOLDER_PROPERTY);

        String folderPath = null;

        if (obj instanceof RepositoryFolderNode) {
          folderPath = ((RepositoryFolderNode) obj).getItemPath();
        }

        String newPath = constructPath((String) newValue, folderPath);

        DynamicBeans.setPropertyValue(mBean, PATH_PROPERTY, newPath);
      }
    } catch (PropertyNotFoundException e) {
      if (sLogger.isLoggingError()){
        sLogger.logError("Can't change property value ", e);
      }
    }
  }

  /**
   * The path is constructed from a file name and a folder path.
   *
   * @param pFileName file name
   * @param pFolderPath foder path
   *
   * @return string that contains path which constructed from a file name and folder path
   */
  protected String constructPath(String pFileName, String pFolderPath) {
    if ((pFileName == null) && (pFolderPath == null)) {
      return "/";
    }

    if (pFileName == null) {
      return pFolderPath;
    }

    if (pFolderPath == null) {
      return pFileName;
    }

    if ((pFolderPath.length() > 0) && (pFolderPath.charAt(pFolderPath.length() - 1) == '/')) {
      return pFolderPath + pFileName;
    } else {
      return pFolderPath + "/" + pFileName;
    }
  }
}
