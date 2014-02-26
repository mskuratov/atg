<%--
  This gadget renders the Fluoroscope events details pane.
  It consists of three parts:
    1. Sensor events;
    2. Event properties;
    3. Event page stack.
  It also renders a form which allows the user to select events to be displayed on the page.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>
<dsp:page>
  <dsp:importbean bean="/atg/store/fluoroscope/SensorManagerFormHandler"/>

  <%-- Display additional control buttons. --%>
  <div id="atg_store_fluoroscopeUrlBar">
    <input type="text" name="atg_store_fluoroscopeUrlInput" id="atg_store_fluoroscopeUrlInput"/>
    <button type="button" id="atg_store_fluoroscopeUrlButton">Go</button>
    <button type="button" id="atg_store_fluoroscopeOrientationToggle">Move To Bottom</button>
    <button type="button" id="atg_store_fluoroscopeOpenCurrent">Open Current Page</button>
  </div>

  <%-- Display the 'select event types' form. --%>
  <div id="atg_store_fluoroscopeControlPanel">
    <span class="atg_store_fluoroscopeSettingsTrigger">Settings</span>
    <dsp:getvalueof var="startPage" vartype="java.lang.String" bean="/atg/store/fluoroscope/Configuration.startPage"/>
    <dsp:form method="post" formid="fluoroscopeform" action="${startPage}" target="crsFrame">
      <dsp:getvalueof var="sensors" bean="SensorManagerFormHandler.availableSensors"/>
      <ul>
        <c:forEach var="sensor" items="${sensors}">
          <li>
          <dsp:input bean="SensorManagerFormHandler.sensors" type="checkbox" value="${sensor.name}" checked="${sensor.enabled}"/>
          <label>${sensor.name}</label>
          </li>
        </c:forEach>
        
        <%--
          Success and Error URLs for the SensorManagerFormHandler's update handler will be 
          set by JavaScript to the URL that is currently rendered in iframe container for Storefront.
        --%>
        <dsp:input id="atg_store_sensorManagerUpdateSuccessUrl" bean="SensorManagerFormHandler.updateSuccessURL" type="hidden"  value=""/>
        <dsp:input id="atg_store_sensorManagerUpdateErrorUrl" bean="SensorManagerFormHandler.updateErrorURL" type="hidden"  value=""/>
        <li>
        <dsp:input bean="SensorManagerFormHandler.update" type="submit" value="Save"/>
        </li>
      </ul>
    </dsp:form>
  </div>

  <div id="atg_store_sensorInfo">
    <%-- Display 'page stack' pane. --%>
    <div id="atg_store_sensorLinksListContainer">
      <ul id ="atg_store_sensorLinksList">
        <li class='atg_store_sensorTitle'>PAGE STACK</li>
      </ul>
    </div>

    <%-- Display 'sensor properties' pane. --%>
    <div id="atg_store_sensorDetailsContainer">
      <ul id="atg_store_sensorDetails">
        <li class='atg_store_sensorTitle'>SENSOR DATA</li>
      </ul>
    </div>

    <%-- Display 'sensors' pane. --%>
    <div id="atg_store_fluoroscopeSensorsContainer">
      <ul id="atg_store_fluoroscopeSensors">
        <li class='atg_store_sensorTitle'>SENSORS</li>
      </ul>
    </div>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Fluoroscope/j2ee/fluoroscope.war/pages/fluoroscope.jsp#1 $$Change: 735822 $--%>
