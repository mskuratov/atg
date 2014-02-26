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



package atg.projects.store.fluoroscope.listener;

import java.io.IOException;
import java.text.MessageFormat;

import atg.core.util.StringUtils;
import atg.service.fluoroscope.SensorEvent;
import atg.service.fluoroscope.listener.CachingListener;
import atg.service.fluoroscope.listener.DhtmlSensorEventHtmlInserterFactory;
import atg.service.fluoroscope.listener.EventContext;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

/**
 * CRS implementation of Fluoroscope HtmlInserterFactory. This implementation uses its own {@link SensorEventHtmlInserter}
 * to render events on the page. This implementation is based on {@link DhtmlSensorEventHtmlInserter} class.
 * 
 * @see DhtmlSensorEventHtmlInserterFactory
 * @see SensorEventHtmlInserter
 * @see DhtmlSensorEventHtmlInserter
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Fluoroscope/src/atg/projects/store/fluoroscope/listener/StoreHtmlInserterFactory.java#3 $$Change: 768606 $ 
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class StoreHtmlInserterFactory extends DhtmlSensorEventHtmlInserterFactory {
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Fluoroscope/src/atg/projects/store/fluoroscope/listener/StoreHtmlInserterFactory.java#3 $$Change: 768606 $";

  // Factory name to be displayed within /dyn/admin application.
  private static final String STORE_FACTORY_NAME = "Store HTML";
  private static final String EVENT_CONTEXT_DIV = "<div id=\"eventContextId\" style=\"display:none;\">{0}</div>";
  
  private String mEventHtmlTemplate;
  
  @Override
  protected SensorEventHtmlInserter createSensorHtmlInserter(DynamoHttpServletRequest pRequest) {
    // We will use our own SensorEventHtmlInserter implementation.
    return new StoreHtmlInserter(pRequest);
  }
  
  @Override
  public String getFactoryName() {
    return STORE_FACTORY_NAME;
  }

  public String getEventHtmlTemplate() {
    return mEventHtmlTemplate;
  }

  public void setEventHtmlTemplate(String pEventHtmlTemplate) {
    mEventHtmlTemplate = pEventHtmlTemplate;
  }
  
  //----------------------------
  // property: CachingListener
  
  /** Caching listener component. Used to access cached SensorEvents to update
   * their position. */
  StoreCachingListener mCachingListener;
  
  /** Set the caching listener. The caching listener is used to
   * obtain SensorEvents so we can update their positions (once determined).
   */
  public void setCachingListener(StoreCachingListener pCachingListener) {
    mCachingListener = pCachingListener;
  }

  /** Get the caching listener. The caching listener is used to
   * obtain SensorEvents so we can update their positions (once determined).
   */
  public StoreCachingListener getCachingListener() {
    return mCachingListener;
  }

  @Override
  public String getExternalJavascriptBoilerplateUri() {
    /*
     * Add current request's contextPath to the external javascript URI defined within the *.properties file.
     * This method should always be called within some request scope, cause it calculates HTML code to be displayed on the page.
     * So, correct URI consits of current contextPath (i.e. /crs/storeus) and relative URI defined with properties file. 
     */
    String result = super.getExternalJavascriptBoilerplateUri();
    DynamoHttpServletRequest currentRequest = ServletUtil.getCurrentRequest();
    if (currentRequest == null) {
      return result;
    } else {
      return currentRequest.getContextPath() + result;
    }
  }

  /*
   * This implemenation updates HTML code generated by its superclass. It adds the 'class' attribute to <span> generated.
   * It enables styling with CSS.
   */
  private class StoreHtmlInserter extends DhtmlSensorEventHtmlInserter {
    private StoreHtmlInserter(DynamoHttpServletRequest pRequest) {
      super(pRequest);
    }
    
    
    @Override
    public String getHtmlBeforeEndBody() throws IOException {
      return null;
    }
    
    /**
     * {@inheritDoc}
     * </br>
     * Current implementation renders a hidden element after the begin of body element. The generated div
     * tracks current event context ID in order it can be used outside of current request.
     * @see EventContext
     */
    @Override
    public String getHtmlAfterBeginBody() throws IOException {
    
      // Retrieve current sensor events context, we will put it into hidden DIV element.
      EventContext currentContext = (EventContext) getRequest().getAttribute(CachingListener.ATTR_GLOBAL_EVENT_CONTEXT);
      String eventContextId = null;
      
      if (currentContext == null){
        
        // No event context is created for the current request yet. Just get the context ID
        // using the CachingListener's getEventContextIdForRequest() method
        
        eventContextId = getCachingListener().getEventContextIdForRequest(ServletUtil.getCurrentRequest());
        
      }else{
        eventContextId = currentContext.getContextId();
      } 
      
      if (StringUtils.isEmpty(eventContextId)){
        return null;
      }
      
      return MessageFormat.format(EVENT_CONTEXT_DIV, new Object[]{eventContextId});
    }

    /**
     * {@inheritDoc}
     * <br/>
     * Current implementation creates event's HTML code on base of MessageFormat. Format pattern is defined with
     * <code>eventHtmlTemplate</code> property. Only two parameters will be inserted into this pattern.
     * First parameter is event's HTML ID, second parameter is event's class simple name.
     */
    @Override
    public String getHtmlForSensorEvent(SensorEvent pEvent) throws IOException {
      return MessageFormat.format(getEventHtmlTemplate(), pEvent.getHtmlSensorId(), pEvent.getClass().getSimpleName());
    }
  }
}