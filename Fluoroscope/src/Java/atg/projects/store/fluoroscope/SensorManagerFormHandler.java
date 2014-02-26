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



package atg.projects.store.fluoroscope;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;

import atg.droplet.GenericFormHandler;
import atg.service.fluoroscope.Sensor;
import atg.service.fluoroscope.SensorListener;
import atg.service.fluoroscope.SensorManager;
import atg.service.fluoroscope.SensorManagerService;
import atg.service.fluoroscope.SensorSessionData;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This form handler enables/disables sensor events in the CRS application. It also calculates collection of sensors
 * to be displayed on the page.
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Fluoroscope/src/atg/projects/store/fluoroscope/SensorManagerFormHandler.java#2 $$Change: 768606 $ 
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class SensorManagerFormHandler extends GenericFormHandler {
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Fluoroscope/src/atg/projects/store/fluoroscope/SensorManagerFormHandler.java#2 $$Change: 768606 $";
  
  private SensorManagerService mSensorManager;
  private String[] mDisplayedSensorNames = new String[] {};
  private String mUpdateSuccessURL;
  private String mUpdateErrorURL;
  private String[] mSensors = new String[] {};

  /**
   * This property contains a reference to a {@link SensorManager} Nucleus component.
   * It's a central component for all Fluoroscope functionality.
   * @return <code>SensorManager</code> instance.
   */
  public SensorManagerService getSensorManager() {
    return mSensorManager;
  }

  public void setSensorManager(SensorManagerService pSensorManager) {
    mSensorManager = pSensorManager;
  }
  
  /**
   * This property contains names of sensors to be displayed on the UI.
   * @return array of displayed sensors names.
   */
  public String[] getDisplayedSensorNames() {
    return mDisplayedSensorNames;
  }

  public void setDisplayedSensorNames(String[] pSensorNames) {
    mDisplayedSensorNames = pSensorNames;
  }

  /**
   * The user will be redirected to this URL when update process successfully finished.
   * @return update success URL.
   */
  public String getUpdateSuccessURL() {
    return mUpdateSuccessURL;
  }

  public void setUpdateSuccessURL(String pUpdateSuccessURL) {
    mUpdateSuccessURL = pUpdateSuccessURL;
  }

  /**
   * The user will be redirected to this URL when update process failed.
   * @return update failure URL.
   */
  public String getUpdateErrorURL() {
    return mUpdateErrorURL;
  }

  public void setUpdateErrorURL(String pUpdateErrorURL) {
    mUpdateErrorURL = pUpdateErrorURL;
  }

  /**
   * This is an input property for the update process. Only sensors with names enlisted in this property will be enabled
   * during update process execution.
   * @return array of selected sensors names.
   */
  public String[] getSensors() {
    return mSensors;
  }

  public void setSensors(String[] pSensors) {
    mSensors = pSensors;
  }

  /**
   * This method calculates a collection of sensors to be dislpayed on the UI.
   * @return <code>Collection</code> of sensors to be displayed on the UI.
   */
  public Collection<Sensor> getAvailableSensors() {
    Collection<Sensor> sensors = new ArrayList<Sensor>();
    for (String sensorName: getDisplayedSensorNames()) {
      Sensor sensor = getSensorManager().getSensorByName(sensorName);
      if (sensor != null) {
        sensors.add(sensor);
      }
    }
    return sensors;
  }

  /**
   * Implementation of the update process. Current implementation iterates over all declared within <code>SensorManager</code> sensors
   * and enables only sensors enlisted within the <code>sensors</code> property. Other sensors will be disabled.
   * <br/>
   * If one or more sensors should be enabled, this process turns on all defined sensor listeners.
   * 
   * @param pRequest current HTTP request.
   * @param pResponse current HTTP response.
   * @return <code>false</code> if redirect occurred, <code>true</code> otherwise.
   * @throws ServletException if something goes wrong.
   * @throws IOException if something goes wrong.
   */
  public boolean handleUpdate(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    List<String> selectedSensors = Arrays.asList(getSensors());
    SensorSessionData ssd = getSensorManager().getCurrentSensorSessionData(pRequest);
    boolean enableListeners = false;
    for (Sensor sensor: getSensorManager().getSensors()) {
      boolean enableSensor = selectedSensors.contains(sensor.getName());
      sensor.setEnabled(enableSensor);
      ssd.setSensorEnabledForSession(sensor.getName(), enableSensor);
      enableListeners = enableListeners || enableSensor;
    }
    for (SensorListener listener: getSensorManager().getSensorListeners()) {
      listener.setEnabled(enableListeners);
      ssd.setListenerEnabledForSession(listener.getName(), enableListeners);
    }
    return checkFormRedirect(getUpdateSuccessURL(), getUpdateErrorURL(), pRequest, pResponse);
  }
}
