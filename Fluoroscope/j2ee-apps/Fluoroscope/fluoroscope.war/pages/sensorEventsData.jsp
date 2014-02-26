<%--
  This gadget renders all sensor events properties in form of JSON object.

  Required parameters:
    eventContextId
      Sensor event context ID. All sensor events will be withdrawn from this context.

  Optional parameters:
    None.
--%>
<dsp:page>
  <%-- Render json itself. --%>

    <json:object>
      <%--
        This droplet retrieves the EventContext instance from the CachingListener by its ID.

        Input parameters:
          contextId
            ID of context to be retrieved.

        Output parameters:
          eventContext
            Event context, if found.

        Open parameters:
          empty
            Rendered, if can't find context.
          output
            Rendered otherwise.
      --%>
      <dsp:droplet name="/atg/store/fluoroscope/GetSensorEventContextDroplet">
        <dsp:param name="contextId" param="eventContextId"/>
        <dsp:oparam name="output">
          <%-- We will render all available sensor events data. Each event will be presented as a separate object. --%>
          <dsp:getvalueof var="sensorEvents" vartype="java.lang.Object" param="eventContext.sensorEvents"/>
          <c:forEach var="sensorEvent" items="${sensorEvents}">
            <json:object name="${sensorEvent.id}">
              <%-- Each event has ID. --%>
              <json:property name="beginEventId" value="${sensorEvent.beginEventId}"/>
              <%--
                This droplet finds a sensor event specified by its ID within the specified event context.
                It also calculates page stack for all 'page begin' events.
              
                Input parameters:
                  context
                    Event context to be examined.
                  eventId
                    ID of event to be found.
              
                Output parameters:
                  event
                    Sensor event, if found.
                  breadcrumbs
                    Page stack collection. Each element in this collection contains an ID of the appropriate page start event
                    and URI of the page.
              
                Open parameters:
                  empty
                    Rendered if unable to find sensor event.
                  page
                    Rendered if event type is 'page begin'.
                  droplet
                    Rendered if event type is 'droplet begin'.
                  form
                    Rendered if event type is 'form begin'.
                  event
                    Rendered if event type is 'scenario event received'.
                  action
                    Rendered if event type is 'scenario action performed'.
              --%>
              <dsp:droplet name="/atg/store/fluoroscope/GetSensorEventDroplet">
                <dsp:param name="eventId" value="${sensorEvent.id}"/>
                <dsp:param name="context" param="context"/>

                <%-- It's a 'page begin event', display its properties. --%>
                <dsp:oparam name="page">
                  <json:property name="Context Root" value="${sensorEvent.contextPath}"/>
                  <json:property name="Servlet Path" value="${sensorEvent.servletPath}"/>
                  <json:property name="Real Path" value="${sensorEvent.fileReference.absolutePath}"/>
                  <json:property name="Request URI" value="${sensorEvent.requestURI}"/>
                  <json:object name="Page Params">
                    <c:forEach var="paramEntry" items="${sensorEvent.passedParameters}">
                      <json:property name="${paramEntry.key}" value="${paramEntry.value}"/>
                    </c:forEach>
                  </json:object>
                  <%-- Each 'page stack' element contains page URI and appropriate 'page begin' event ID. --%>
                  <json:object name="Page Stack">
                    <dsp:getvalueof var="breadcrumbs" vartype="java.util.Collection" param="breadcrumbs"/>
                    <c:forEach var="stackElement" items="${breadcrumbs}">
                      <json:property name="${stackElement.id}" value="${stackElement.uri}"/>
                    </c:forEach>
                  </json:object>
                </dsp:oparam>

                <%-- It's a 'droplet begin' event, display its properties. --%>
                <dsp:oparam name="droplet">
                  <json:property name="Droplet Name" value="${sensorEvent.dropletName}"/>
                  <json:property name="Droplet Path" value="${sensorEvent.configurationPath}"/>
                  <json:object name="Stack Frame">
                    <c:forEach var="paramEntry" items="${sensorEvent.passedParameters}">
                      <json:property name="${paramEntry.key}" value="${paramEntry.value}"/>
                    </c:forEach>
                  </json:object>
                </dsp:oparam>

                <%-- It's a 'scenario event' event. --%>
                <dsp:oparam name="event">
                  <json:property name="Event Name" value="${sensorEvent.eventType}"/>
                  <json:property name="Scenario Manager Name" value="${sensorEvent.managerName}"/>
                </dsp:oparam>

                <%-- It's a 'scenario action' event. --%>
                <dsp:oparam name="action">
                  <json:property name="Action Name" value="${sensorEvent.actionName}"/>
                  <json:property name="Scenario Manager Name" value="${sensorEvent.managerName}"/>
                  <json:object name="Action Params">
                    <c:forEach var="paramEntry" items="${sensorEvent.actionProperties}">
                      <json:property name="${paramEntry.key}" value="${paramEntry.value}"/>
                    </c:forEach>
                  </json:object>
                  <c:if test="${not empty sensorEvent.contextInfo}">
                    <json:object name="Action Context">
                      <json:property name="individual" value="${sensorEvent.contextInfo.individual}"/>
                      <json:property name="processInstance" value="${sensorEvent.contextInfo.processInstance}"/>
                      <json:property name="subject" value="${sensorEvent.contextInfo.subject}"/>
                      <json:property name="messageType" value="${sensorEvent.contextInfo.messageType}"/>
                      <json:property name="message" value="${sensorEvent.contextInfo.message}"/>
                      <json:property name="sessionId" value="${sensorEvent.contextInfo.sessionId}"/>
                      <json:property name="parentSessionId" value="${sensorEvent.contextInfo.parentSessionId}"/>
                      <json:property name="site" value="${sensorEvent.contextInfo.site}"/>
                    </json:object>
                  </c:if>
                </dsp:oparam>

                <%-- It's a 'form begin' event. --%>
                <dsp:oparam name="form">
                  <json:property name="Form Name" value="${sensorEvent.formId}"/>
                  <json:property name="Context" value="${sensorEvent.contextId}"/>
                  <json:array name="Form Field Names">
                    <c:forEach var="name" items="${sensorEvent.eventNames}">
                      <json:property name="name" value="${name}"/>
                    </c:forEach>
                  </json:array>
                  <json:object name="Form Elements">
                    <c:forEach var="propertyEntry" items="${sensorEvent.propertyNameByFieldName}">
                      <json:property name="${propertyEntry.key}" value="${propertyEntry.value}"/>
                    </c:forEach>
                  </json:object>
                </dsp:oparam>
              </dsp:droplet>
            </json:object>
          </c:forEach>
        </dsp:oparam>
      </dsp:droplet>
    </json:object>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Fluoroscope/j2ee/fluoroscope.war/pages/sensorEventsData.jsp#2 $$Change: 742374 $--%>
