<%--
  This page renders the main Fluoroscope web-application page.
  It renders the information pane and an <iframe> that will contain inspected web application page.

  Required parameters:
    None.

  Optional parameters:
    None.
--%>
<dsp:page>
  <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd">
  <html>
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
      <title>Inspect Page</title>
      <%-- Get styles and javascripts to be used on the page. --%>
      <link rel="stylesheet" href="css/fluoroscope.css" type="text/css" media="screen" />
      <script type="text/javascript" src="/dojo-1-6-1/dojo/dojo.js"></script>
      <script type="text/javascript" src="/dojo-1-6-1/dojo/dnd/autoscroll.js"></script>
      <script type="text/javascript" src="/dojo-1-6-1/dojo/dnd/common.js"></script>
      <script type="text/javascript" src="/dojo-1-6-1/dojo/dnd/Mover.js"></script>
      <script type="text/javascript" src="/dojo-1-6-1/dojo/dnd/Moveable.js"></script>
      <script type="text/javascript" src="/dojo-1-6-1/dojo/dnd/move.js"></script>
      <script type="text/javascript" src="/dojo-1-6-1/dojo/dnd/TimedMoveable.js"></script>
      <script type="text/javascript" src="/dojo-1-6-1/dojo/i18n.js"></script>
      <script type="text/javascript" src="/dojo-1-6-1/dijit/nls/dijit-all_en-us.js"></script>
      <script type="text/javascript" src="/dojo-1-6-1/dijit/dijit-all.js"></script>
      <script type="text/javascript" src="/dojo-1-6-1/dojo/fx/Toggler.js"></script>
      <script type="text/javascript" src="/dojo-1-6-1/dojo/fx.js"></script>
      <script type="text/javascript" src="/dojo-1-6-1/dojo/cookie.js"></script>
      <script type="text/javascript">
        <%-- Create global var with webapp context path - this can be used to build absolute URLs. --%>
          var contextPath="${pageContext.request.contextPath}";
        <%-- Define Store javascript modules path --%>
          dojo.registerModulePath('atg.store', contextPath + '/javascript');
      </script>
      <script type="text/javascript" src="/dojo-1-6-1/dojo/i18n.js"></script>
      <script type="text/javascript"src="/dojo-1-6-1/dojo/html.js"></script>
      <script type="text/javascript" src="/dojo-1-6-1/dijit/layout/ContentPane.js"></script>
      <script type="text/javascript" src="/dojo-1-6-1/dijit/form/_FormMixin.js"></script>
      <script type="text/javascript" src="/dojo-1-6-1/dijit/_DialogMixin.js"></script>
      <script type="text/javascript" src="/dojo-1-6-1/dijit/DialogUnderlay.js"></script>
      <script type="text/javascript"src="/dojo-1-6-1/dijit/TooltipDialog.js"></script>
      <script type="text/javascript" src="/dojo-1-6-1/dijit/Dialog.js"></script>
      <script type="text/javascript"src="/dojo-1-6-1/dojox/fx/_base.js"></script>

      <%-- Get custom dojo widgets, they will represent Fluoroscope markers, etc. --%>
      <c:set var="javascriptRoot" value="${pageContext.request.contextPath}/javascript"/>
      <script type="text/javascript" src="${javascriptRoot}/widget/SensorManager.js"></script>
      <script type="text/javascript" src="${javascriptRoot}/widget/SensorMarker.js"></script>
      <script type="text/javascript" src="${javascriptRoot}/widget/SensorData.js"></script>
      <script type="text/javascript" charset="utf-8">
        dojo.addOnLoad(function(){
          atg_sensorManager = new atg.store.widget.SensorManager({
            id:"atg_sensorManager"
          });
        });
      </script>
    </head>
    <body>
      <%-- Render Fluoroscope events details pane. --%>
      <div id="fluoroscopeContainer">
        <dsp:include page="/pages/fluoroscope.jsp"/>
      </div>
      <%-- Web-application welcome-page should be defined within the special Configuration component. --%>
      <dsp:getvalueof var="startPage" vartype="java.lang.String" bean="/atg/store/fluoroscope/Configuration.startPage"/>
      <%-- Render this page inside the <iframe>. --%>
      <iframe src="${startPage}" width="100%" height="100%" id="crsContainer" frameBorder="0" name="crsFrame">
        <p>Your browser does not support iframes.</p>
      </iframe>
    </body>
  </html>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Fluoroscope/j2ee/fluoroscope.war/pages/index.jsp#1 $$Change: 735822 $--%>
