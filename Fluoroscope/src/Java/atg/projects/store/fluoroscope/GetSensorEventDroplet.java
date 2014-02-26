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

import javax.servlet.ServletException;

import atg.service.fluoroscope.SensorEvent;
import atg.service.fluoroscope.listener.EventContext;
import atg.service.fluoroscope.sensor.DropletTagSensor.DropletBeginSensorEvent;
import atg.service.fluoroscope.sensor.PageTagSensor.PageBeginSensorEvent;
import atg.service.fluoroscope.sensor.PageTagSensor.PageSensorEvent;
import atg.service.fluoroscope.sensor.ProcessManagerSensor.ExecuteActionProcessSensorEvent;
import atg.service.fluoroscope.sensor.ProcessManagerSensor.ProcessManagerReceivedEventSensorEvent;
import atg.service.fluoroscope.sensor.FormTagSensor.FormBeginSensorEvent;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import java.util.Stack;

/**
 * This droplet searches for a previously thrown sensor event.
 * Current implementation takes event by ID from the event context specified. 
 * 
 * <h5>Input parameters:</h5>
 * <dl>
 *    <dt>eventId</dt>
 *    <dd><i>Required.</i> ID of event to be found.</dd>
 *    <dt>contextId</dt>
 *    <dd><i>Required.</i> ID of context containing the event.</dd>
 *    <dt>getBreadcrumbs</dt>
 *    <dd><i>Optional.</i> Flags, if droplet should calculate event's breadcrumbs.</dd>
 * </dl>
 * 
 * <h5>Output parameters:</h5>
 * <dl>
 *    <dt>event</dt>
 *    <dd>Event instance, if found.</dd>
 *    <dt>breadcrumbs</dt>
 *    <dd>Event's breadcrumbs. I.e. stack of pages from initial page to the page actually thrown an event.</dd> 
 * </dl>
 * 
 * <h5>Oper paremters:</h5>
 * <dl>
 *    <dt>output</dt>
 *    <dd>Rendered when event is found.</dd>
 *    <dt>empty</dt>
 *    <dd>Rendered when can't find an event specified.</dd>
 * </dl>
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Fluoroscope/src/atg/projects/store/fluoroscope/GetSensorEventDroplet.java#3 $$Change: 768606 $ 
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class GetSensorEventDroplet extends DynamoServlet {
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Fluoroscope/src/atg/projects/store/fluoroscope/GetSensorEventDroplet.java#3 $$Change: 768606 $";
  // Input parameter names.
  private static final String EVENT_ID = "eventId";
  private static final String EVENT_CONTEXT = "eventContext";
  // Output parameter names.
  private static final String EVENT_NAME = "event";
  private static final String BREADCRUMB_NAME = "breadcrumbs";
  // Open parameter names.
  private static final String EMPTY = "empty";
  private static final String PAGE = "page";
  private static final String DROPLET = "droplet";
  private static final String FORM = "form";
  private static final String EVENT = "event";
  private static final String ACTION = "action";

  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    // First check, if we have input parameters.
    String eventId = (String) pRequest.getObjectParameter(EVENT_ID);
    EventContext context = (EventContext) pRequest.getObjectParameter(EVENT_CONTEXT);
    // Get event context and event from CachingListener's caches.
    if (context == null) {
      // No context found, render 'empty' oparam.
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
      return;
    }
    
    SensorEvent currentEvent = context.getSensorEvent(Integer.parseInt(eventId));
    if (currentEvent == null) {
      // No event found, render 'empty' oparam.
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
      return;
    }
    
    pRequest.setParameter(EVENT_NAME, currentEvent);

    // We will calculate breadcrumbs for 'start page' events only.
    if (currentEvent instanceof PageBeginSensorEvent) {
      // We will save events' URIs only, that's why generic type is String.
      Stack<PageContextStackElement> breadcrumbs = new Stack<PageContextStackElement>();
      
      /*
       * All events are sorted by creation time, when you get them through the getSensorEvents() method.
       * This guarantees that we will get start event before its end pair, while iterating over context's events.
       * It's also essential to turn on PageSensor, because breadcrumbs are calculated on base of page events 
       * stored into current context.
       */
      for (SensorEvent event: context.getSensorEvents()) {
        // Only PageEvents can change breadcrumbs, so we can ignore others.
        if (event instanceof PageSensorEvent) {
          String eventRequestURI = ((PageSensorEvent) event).getRequestURI();
          if (event.isEndEvent()) {
 
            if (!breadcrumbs.isEmpty()){
              
              // Check whether end event matches last begin event in the stack
              PageContextStackElement lastStackElement = breadcrumbs.lastElement();
              if (eventRequestURI.equals(lastStackElement.getUri())){
                
                // The end event matches the last event in the stack so pop it out.
                breadcrumbs.pop();
              }else{
                
                // It's quite rare case when begin page event came without corresponding end event
                // or vice versa.
                // Check the next to last element in the stack
                // If the current end event doesn't match two last elements in stack
                // just ignore it.
                if (breadcrumbs.size()>1 && breadcrumbs.get(breadcrumbs.size()-2).getUri().equals(eventRequestURI)){
                  
                  // Current end event matches the next to last element in stack
                  // So remove the last two elements from stack
                  breadcrumbs.pop();
                  breadcrumbs.pop();
                }
              }
                
            }
          } else {
            
            // New page has began. Save its URI into breadcrumbs collection.
            breadcrumbs.push(new PageContextStackElement(event.getId(), ((PageSensorEvent) event).getRequestURI()));
            
          }
        }
        if (event.equals(currentEvent)) {
          // We've found current event, hence all breadcrumbs are calculated. Break the loop.
          break;
        }
      }
      pRequest.setParameter(BREADCRUMB_NAME, breadcrumbs);
    }
    
    // Render output oparams, it's time now.
    if (currentEvent instanceof DropletBeginSensorEvent) {
      pRequest.serviceLocalParameter(DROPLET, pRequest, pResponse);
    } else if (currentEvent instanceof PageBeginSensorEvent) {
      pRequest.serviceLocalParameter(PAGE, pRequest, pResponse);
    } else if (currentEvent instanceof ProcessManagerReceivedEventSensorEvent) {
      pRequest.serviceLocalParameter(EVENT, pRequest, pResponse);
    } else if (currentEvent instanceof FormBeginSensorEvent) {
      pRequest.serviceLocalParameter(FORM, pRequest, pResponse);
    } else if (currentEvent instanceof ExecuteActionProcessSensorEvent) {
      pRequest.serviceLocalParameter(ACTION, pRequest, pResponse);
    }
  }
  
  /**
   * It's a DTO object used to present a single item in the 'Page Stack' property list.
   * This object contains an URI of stack element and corresponding sensor event ID.
   * @author ATG
   */
  public static class PageContextStackElement {
    private String mId;
    private String mUri;
    
    // Do not allow to instantiate this class from outside droplet.
    private PageContextStackElement(String pId, String pUri) {
      mId = pId;
      mUri = pUri;
    }
    
    /*
     * Getter for the ID property.
     */
    public String getId() {
      return mId;
    }
    
    /*
     * Getter for the URI property.
     */
    public String getUri() {
      return mUri;
    }
  }
}
