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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.ServletOutputStream;

import atg.service.configuration.ResponseWrappingConfiguration.ResponseOutputWrapper;
import atg.service.fluoroscope.SensorEvent;
import atg.service.fluoroscope.listener.CachingListener;
import atg.service.fluoroscope.listener.EventContext;
import atg.service.fluoroscope.listener.MarkTagFilterParser;
import atg.service.fluoroscope.listener.PositionMarkingSensorListener;
import atg.service.fluoroscope.listener.PositionMarkingSensorListener.PositionMarkingResponseOutputWrapper;
import atg.servlet.DynamoHttpServletRequest;

/**
 * CRS implementation of the <code>PositionMarkingSensorListener</code>.
 * <br/>
 * Super-implementation uses <code>&lt;&lt;&lt;atg:sensorEvent:[event ID]:&gt;&gt;&gt;</code> tags.
 * There are problems, if you use them inside of tags that encode XML special characters. So we had to redefine
 * tag structure to be used.
 * <br/>
 * This implementation redefines marker tag to be written to the output HTML code.
 * Current implementation uses the following form of the marker tag:
 * <code>###atg:sensorEvent:[event ID]:###</code>. It also redefines output filter parser for the new tag to be supported.
 * 
 * @see PositionMarkingSensorListener
 * @see PositionMarkingResponseOutputWrapper
 * @see MarkTagFilterParser
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Fluoroscope/src/atg/projects/store/fluoroscope/listener/StorePositionMarkingSensorListener.java#2 $$Change: 768606 $ 
 * @updated $DateTime: 2012/12/26 06:47:02 $$Author: abakinou $
 */
public class StorePositionMarkingSensorListener extends PositionMarkingSensorListener {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/Fluoroscope/src/atg/projects/store/fluoroscope/listener/StorePositionMarkingSensorListener.java#2 $$Change: 768606 $";

  /**
   * Start marker tag.
   */
  protected static final String MARK_TAG_PREFIX = "###atg:";
  /**
   * End marker tag.
   */
  protected static final String MARK_TAG_SUFFIX = ":###";
  
  /**
   * {@inheritDoc}
   * <br/>
   * Current implementation returns marker tag in the following format: <code>###atg:sensorEvent:[event ID]:###</code>.
   */
  @Override
  protected String getMarkTagForSensorEvent(SensorEvent pSensorEvent) {
    // Construct marker tag value in new form.
    StringBuffer strbuf = new StringBuffer();
    strbuf.append(MARK_TAG_PREFIX);
    strbuf.append(SENSOR_EVENT_PREFIX);
    strbuf.append(System.identityHashCode(pSensorEvent));
    strbuf.append(MARK_TAG_SUFFIX);
    return strbuf.toString();
  }

  /**
   * Creates a new instance of the <code>ResponseOutputWrapper</code> to be used by this sensor listener.
   * @return <code>ResponseOutputWrapper</code> to be used.
   * @see ResponseOutputWrapper
   */
  public ResponseOutputWrapper getOutputWrapper() {
    return new StorePositionMarkingResponseOutputWrapper();
  }

  @Override
  protected void enable() {
    // Super-implementation will enable Caching listener essential for the position marking.
    super.enable();
    // However, it also adds broken PositionMarkingResponseOutputWrapper to output wrappers collection. We do not need it.
    getResponseWrappingConfiguration().removeResponseOutputWrapper(getResponseOutputWrapper());
    // Add our correct and lovely wrapper. 
    getResponseWrappingConfiguration().addResponseOutputWrapper(getOutputWrapper());
  }

  protected void disable() {
    super.disable();
    // Remove our wrapper, we should not use it, if position marking listener is off.
    getResponseWrappingConfiguration().removeResponseOutputWrapper(getOutputWrapper());
  }

  /*
   * CRS extension of PositionMarkingResponceOutputWrapper.
   * This implementation creates CRS version of MarkTagFilterParser and binds it to request.
   * All other behaviour will be the same.
   */
  private class StorePositionMarkingResponseOutputWrapper extends PositionMarkingResponseOutputWrapper {
    @Override
    public ServletOutputStream wrapOutputStream(DynamoHttpServletRequest pRequest, ServletOutputStream pOutputStream)
        throws IOException {
      pRequest.setAttribute(ATTR_WRAPPED, true);
      // Wrap current request with CRS version of wrapper.
      StorePositionRememberingFilterParser wrappingParser = new StorePositionRememberingFilterParser(pOutputStream, pRequest);
      ServletOutputStream output = wrappingParser.getFilteringServletOutputStream();
      pRequest.setAttribute(ATTR_OSTRM, output);
      outputQueuedSensorEvents(pRequest, output, null);
      return output;
    }

    @Override
    public PrintWriter wrapPrintWriter(DynamoHttpServletRequest pRequest, PrintWriter pPrintWriter) throws IOException {
      pRequest.setAttribute(ATTR_WRAPPED, true);
      // Wrap current request with CRS version of wrapper.
      StorePositionRememberingFilterParser wrappingParser = new StorePositionRememberingFilterParser(pPrintWriter, pRequest);
      PrintWriter output = wrappingParser.getFilteringPrintWriter();
      pRequest.setAttribute(ATTR_OSTRM, output);
      outputQueuedSensorEvents(pRequest, null, output);
      return output;
    }
  }

  /*
   * This version of MarkTagFilterParser supports marker tag in form of ###atg:sensorEvent:[event ID]:###
   * All other behaviour is the same.
   */
  private class StorePositionRememberingFilterParser extends MarkTagFilterParser {
    public StorePositionRememberingFilterParser(OutputStream pOutput, DynamoHttpServletRequest pRequest) {
      super(pOutput, pRequest);
      // Do not write marker tags when they're parsed.
      setEmittingTags(false);
    }

    public StorePositionRememberingFilterParser(Writer pWriter, DynamoHttpServletRequest pRequest) {
      super(pWriter, pRequest);
      // Do not write marker tags when they're parsed.
      setEmittingTags(false);
    }
    
    @Override
    protected int[][] getTransitionTable() {
      /*
       * Create transition table used by parser here.
       * This transition table is almost exactly the same as super.getTransitionTable() creates; it has only one difference,
       * marker tags are expected to be of the ###atg:sensorEvent:[event ID]:### form.
       */
      int[][] result = new int[MAX_STATES][256];
      result[0]['#'] = 1;
      result[1]['#'] = 2;
      result[2]['#'] = 3;
      result[3]['#'] = 3;
      result[3]['a'] = 4;
      result[4]['t'] = 5;
      result[5]['g'] = 6;
      result[6][':'] = 7;
      assignAllTransitions(result[7], 7);
      result[7][':'] = 8;
      assignAllTransitions(result[8], 7);
      result[8]['#'] = 9;
      assignAllTransitions(result[9], 7);
      result[9]['#'] = 10;
      assignAllTransitions(result[10], 7);
      result[10]['#'] = 0;
      return result;
    }

    @Override
    protected void handleEndTag(MarkTag pTag) {
      /*
       * It's a copy and pase version of the PositionMarkingSensorListener#PositionRememberingFilterParser.handleEndTag method.
       * PositionRememberingFilterParser is package-private, that's why it can't be extended.
       * Hence, we have to create separate implmenetation with the same behaviour.
       */
      super.handleEndTag(pTag);

      String tagContent = pTag.getContent().trim();
      if (!tagContent.startsWith(SENSOR_EVENT_PREFIX)) {
        return;
      }
      EventContext context = (EventContext) mRequest.getAttribute(CachingListener.ATTR_SESSION_EVENT_CONTEXT);
      if (context == null) {
        return;
      }

      int eventId = Integer.parseInt(tagContent.substring(SENSOR_EVENT_PREFIX.length()));
      SensorEvent event = context.getSensorEvent(eventId);
      if (event == null) {
        return;
      }

      event.setOffset(pTag.getNoTagPos());
    }
  }
}
