/**
 * Fluoroscope Widget
 * This widget facilitates fluoroscope functionality
 * Created by Ken Wheeler, 2/22/11 
 *
 */
  
dojo.provide("atg.store.widget.SensorManager");

dojo.declare(
  "atg.store.widget.SensorManager", 
  [dijit._Widget, dijit._Container],
  {
    
    iframeDom: {},
    dojoFrame: {},
    sensorScroll: 0,
    dataScroll: 0,
    stackScroll: 0,
    JSONdata: null,
    
    constructor: function(){
      
      //Render sensor view
      this.loadEnhancements();
      
    },
    
    showLoader: function(){
      
      if(dojo.byId('windowShade')){
        dojo.byId('windowShade').style.display="block";
        dojo.style(dojo.byId('windowShade'), 'height' , dojo.style(dojo.byId('crsContainer'),'height'));
        dojo.style(dojo.byId('windowShade'), 'width' , dojo.style(dojo.byId('crsContainer'),'width'));
        dojo.style(dojo.byId('windowShade'), 'top' , dojo.style(dojo.byId('crsContainer'),'top'));
      }
      
      else {
        windowShade = dojo.place("<div id='windowShade' style='background: black url(/inspect/images/fluoroLoader.gif) center center no-repeat; opacity: .8; display: block; height: 100%; width: 100%; position: fixed; top: 130px; left: 0px; z-index: 9999'></div>", document.body,'first');
        dojo.style(dojo.byId('windowShade'), 'height' , dojo.style(dojo.byId('crsContainer'),'height'));
        dojo.style(dojo.byId('windowShade'), 'width' , dojo.style(dojo.byId('crsContainer'),'width'));
        dojo.style(dojo.byId('windowShade'), 'top' , dojo.style(dojo.byId('crsContainer'),'top'));
      }
      
    },
    
    hideLoader: function(){
      
      //Remove window shade
      if(dojo.byId('windowShade')){
      dojo.byId('windowShade').style.display="none";
      }
      
    },
    
    resizeLoader: function(){
      
      if(dojo.byId('windowShade')){
        dojo.style(dojo.byId('windowShade'), 'height' , dojo.style(dojo.byId('crsContainer'),'height'));
        dojo.style(dojo.byId('windowShade'), 'width' , dojo.style(dojo.byId('crsContainer'),'width'));
        dojo.style(dojo.byId('windowShade'), 'top' , dojo.style(dojo.byId('crsContainer'),'top'));
      }
      
    },
    
    addActionMarkers: function(){
      
      //Find all sensors
      dojo.query('.sensorEvent', window.frames[0].document.body).forEach(function(node,index,array){
       
      if (!_this.isVisible(node.parentNode) && !_this.isDropDownItem(node.parentNode)){
        
        // If sensor is inside invisible element and not inside category drop down menu
        // move it to nearest visible element
        parents = _this.getParents(node, new Array());
              
        if (parents.length > 1){
          // Check whether the last processed sensor event has been placed in the same 
          // element where we are going to move the current sensor event
          // and if so put this event in the right position that is after
          // after the last processed event. That is needed to preserve original order
          // of sensors.
          
          if (lastProcessedEvent && lastProcessedEvent.parentNode == parents[parents.length - 1]){
            dojo.place(node, lastProcessedEvent, "after");
          }else{
            dojo.place(node, parents[parents.length - 2], "after");  
          }
        }
      }
      
        node.style.display = 'none';
        
        // Add action marker only when the node has no action markers yet.
        if(this.dojoFrame.query('> .actionMarker', node.parentNode).length<1) {
          var newActionMarker = new atg.store.widget.SensorMarker;
          dojo.place(newActionMarker.domNode, node, "after");
        }
        
        lastProcessedEvent = node;
        
      }, this);  
      
      _this.positionActionMarkers();
        
    },
    
    getParents: function(element, parents){
      if (_this.isVisible(element)){
        return parents;
      }
      var parent = element.parentNode;
      if (parent != null){
        parents.push(parent);
        return _this.getParents(parent, parents);
      }
      return null;            
    },
  
    isVisible: function(element){
      elementPosition = dojo.position(element, true);
      return (elementPosition.w > 0 && elementPosition.h > 0);
    },
    
    isDropDownItem: function(element){
      // Check whether the passed in element is inside
      // category drop down.
      // We need to check parents up to 4 levels
      var parent = element;
      for (var i=0; i<4; i++){
        parent = parent.parentNode;
        if (parent && parent.attributes && parent.attributes.class 
                   && parent.attributes.class.value == "atg_store_catSubNv atg_store_dropDownChild"){
          return true;
        }  
      }
      return false;
    },
    
    positionActionMarkers: function(){
      
      positionArray = new Array();
      dojo.query('.actionMarker', window.frames[0].document.body).forEach(function(node,index,array){
      
      parentPosition = dojo.style(node.parentNode,'position');
      
      if((parentPosition != 'relative') && (parentPosition != 'absolute')) {
        dojo.style(node.parentNode, 'position', 'relative');
      }
      
      markerPosition = dojo.position(node.parentNode, true);
      markerXY = markerPosition.x + ', ' + markerPosition.y;

      if(dojo.indexOf(positionArray, markerXY) == -1){
        positionArray.push(markerXY);
      }
      else {
        node.style.marginLeft += 15 + 'px';
        node.style.zIndex = 2000;
        markerPosition = dojo.position(node, false);
        markerXY = markerPosition.x + ', ' + markerPosition.y;
        positionArray.push(markerXY);  
      }
      
    });
      
    },
    
    cloneSensors: function(node){
      
      dojo.query('.sensorOverlay', window.frames[0].document.body).forEach(function(node,index,array){
        if(node){
        dojo.destroy(node);
        }
      });
      
      sensorContainer = dojo.byId('atg_store_fluoroscopeSensors');
      dojo.empty(sensorContainer);
        dojo.style(sensorContainer,'opacity','0');
      this.dojoFrame.query('> .sensorEvent',node).forEach(function(node,index,array){
        sensorButton = this.dojoFrame.place(this.dojoFrame.clone(node),sensorContainer,'last');
        dojo.style(sensorButton,'opacity','0.6');
        dojo.connect(sensorButton,"onmouseover", this.sensorOver);
        dojo.connect(sensorButton,"onmouseout", this.sensorOut);
        dojo.connect(sensorButton,"onclick", this.sensorClick);
      }, this);
      
      dojo.animateProperty({
       node: 'atg_store_fluoroscopeSensors',
       properties: {
       opacity: 1
       }
      }).play(200);
         
      sensorNodes = dojo.query('> .sensorEvent:first', dojo.byId('atg_store_fluoroscopeSensors'));
      sensorId = sensorNodes[0].id.substr(11);
      dojo.publish('/sensorButton/click' ,[{ sensorId: sensorId }]);
      dojo.style(sensorNodes[0],'opacity','1')
      sensorNodes[0].setAttribute('isactive',true);
      
    },
    
    sensorOver: function(evt){
      dojo.animateProperty({
        node: evt.target,
        properties: {
          opacity: 1
        }
      }).play();
 
    },
    
    sensorOut: function(evt){
      if(!evt.target.getAttribute('isactive')){
        dojo.animateProperty({
          node: evt.target,
          properties: {
            opacity: .6
          }
        }).play();
      }
    },
    
    sensorClick: function(evt){
      
      dojo.query('> .sensorEvent',evt.target.parentNode).forEach(function(node,index,array){
        dojo.style(node,'opacity','0.6')
        node.removeAttribute('isactive');
      });
      
      dojo.query('.sensorOverlay', window.frames[0].document.body).forEach(function(node,index,array){
        dojo.destroy(node);
      });
      
      dojo.style(evt.target,'opacity','1')
      evt.target.setAttribute('isactive',true);
      
      sensorId = this.id.substr(11);
      dojo.publish('/sensorButton/click' ,[{ sensorId: sensorId }]);
      
    },
    
    loadSensorData: function(sensorID){
      
      dojo.empty(dojo.byId('atg_store_sensorDetails'));
      dojo.empty(dojo.byId('atg_store_sensorLinksList'));
      dojo.place("<li class='atg_store_sensorTitle'>PAGE STACK</li>", dojo.byId('atg_store_sensorLinksList'),'first');
      dojo.style(dojo.byId('atg_store_sensorDetails'),'opacity', '0');
      
      objct = this.JSONdata[sensorID];
      beginId = objct.beginEventId;

      if(beginId != "") {
        objct = this.JSONdata[beginId]; 
      }
                    
      for(var prm in objct){          
      if(typeof(objct[prm])=="object") {
        data = "<strong>" + prm + "</strong>: ";
        _this.renderSensorData(data);
        subobjct = objct[prm];           
      for(var subprm in subobjct){            
        if(prm=='Page Stack'){
           data = "<a href='#' class='pageStackLink' dojoAttachEvent='onclick:loadStackData, onmouseover:showSensorOverlay, onmouseout:hideSensorOverlay' parentId='"+subprm+"'>" + subobjct[subprm] + "</a>";
           _this.renderSensorData(data);
        }
        else{
           data = "<strong>" + subprm + "</strong>: " + subobjct[subprm];
           _this.renderSensorData(data);
        }
      } 
      }        
      else {             
      if((prm=='Scenario Manager Name') || (prm=='Droplet Name') || (prm=='Droplet Path')) {     
      data = "<strong>" + prm + "</strong>: <a target='_blank' href='/dyn/admin/nucleus" + objct[prm] + "'>" + objct[prm] + "</a> (HTML ADMIN)";
      _this.renderSensorData(data);
      }
      else {
      if(prm!='beginEventId'){
      data = "<strong>" + prm + "</strong>: " + objct[prm];
      _this.renderSensorData(data);
      }
      }           
      }           
      }
      
      this.dataAnimatedScroll(dojo.byId('atg_store_sensorDetailsContainer'));
      
      dojo.animateProperty({
        node: dojo.byId('atg_store_sensorDetails'),
        properties: {
          opacity: 1
        }
      }).play();
            
    },
   
    /*
     * Submits AJAX request to get sensors data.
     */
    ajaxSubmit: function(){
      
      var bindParams={
        headers: { "Accept" : "application/json" },
        handleAs: "json",
        
        load: dojo.hitch(this, "handleJSON"),
        error: dojo.hitch(this, "handleError"),
        timeout: dojo.hitch(this, "handleError")
      };
      
      var targetUrl = contextPath + "/pages/sensorEventsData.jsp";
      
      // Get the event context ID from the hidden div inside storefront frame.
      var eventContextDiv = this.dojoFrame.query('#eventContextId', this.iframeDom.document.body);
      if (eventContextDiv.length < 1){
        console.debug("Event context div is not found");
        return false;
      }
      
      var eventContextId = eventContextDiv[0].innerHTML;
      var queryString = "eventContextId="+eventContextId;
      
     // Append event context ID as parameter to target URL
      targetUrl += "?" + queryString;

      // Add URL to AJAX params
      dojo.mixin(bindParams,{
        url: targetUrl
      });
            
      dojo.xhrGet(bindParams);
      this.showLoader();
      
      return true;
    },
    
    handleJSON: function(obj){
        console.debug("HANDLEJSON",obj);
        
        // Just store JSON object with sensors data
        this.JSONdata = obj;
        this.hideLoader();
    },
      
    handleError: function(obj){
      console.debug("HANDLERROR",obj);
      this.hideLoader();
    },
    
    renderSensorData: function(sensorData) {
      var newSensorData = new atg.store.widget.SensorData({ sensorDetails: sensorData });
      dojo.byId('atg_store_sensorDetails').appendChild(newSensorData.domNode);
    },
    
    dataAnimatedScroll: function(node){
      
      scrollNode = node;
      startTop = scrollNode.scrollTop;
      
      var animLoop = setInterval(dataAnimate, 30);
      
      function dataAnimate(){
        if(startTop < 1){
          clearInterval(animLoop);
        }
        else{
        scrollNode.scrollTop = startTop;
        startTop -= startTop / 5;
        }
      }
    },
    
    scrollShow: function(){
        dojo.byId('atg_store_sensorDetailsContainer').style.overflow = 'auto';
        dojo.byId('atg_store_fluoroscopeSensorsContainer').style.overflow = 'auto';
        dojo.byId('atg_store_sensorLinksListContainer').style.overflow = 'auto';
        if(dojo.isSafari){
        dojo.byId('atg_store_sensorDetailsContainer').scrollTop = _this.dataScroll +1;
        dojo.byId('atg_store_sensorDetailsContainer').scrollTop = _this.dataScroll -1;
        dojo.byId('atg_store_fluoroscopeSensorsContainer').scrollTop = _this.sensorScroll +1;
        dojo.byId('atg_store_fluoroscopeSensorsContainer').scrollTop = _this.sensorScroll-1;
        dojo.byId('atg_store_sensorLinksListContainer').scrollTop = _this.stackScroll +1;
        dojo.byId('atg_store_sensorLinksListContainer').scrollTop = _this.stackScroll -1;
        }
        if(dojo.isChrome){
        dojo.byId('atg_store_sensorDetailsContainer').scrollTop = _this.dataScroll +1;
        dojo.byId('atg_store_sensorDetailsContainer').scrollTop = _this.dataScroll;
        dojo.byId('atg_store_fluoroscopeSensorsContainer').scrollTop = _this.sensorScroll +1;
        dojo.byId('atg_store_fluoroscopeSensorsContainer').scrollTop = _this.sensorScroll;
        dojo.byId('atg_store_sensorLinksListContainer').scrollTop = _this.stackScroll +1;
        dojo.byId('atg_store_sensorLinksListContainer').scrollTop = _this.stackScroll;
        }
    },
    
    autoSizeLayout: function(){
      
      toolbarHeight = dojo.style(dojo.byId('fluoroscopeContainer'),'height');
      
      windowHeight = (typeof window.innerHeight != 'undefined' ? window.innerHeight : document.body.offsetHeight);
      
      iframeHeight = windowHeight - toolbarHeight + 'px';
      
      dojo.style(dojo.byId('crsContainer'),'height', iframeHeight);
      
    },
    
    flipOrientation: function(evt) {
        
        if(evt){
        dojo.stopEvent(evt);
        }
        if(dojo.style(dojo.byId('fluoroscopeContainer'),'bottom')!='0px'){
        dojo.cookie("toolbarOrientationCookie", "bottom", { expires: 5});
        dojo.style(dojo.byId('fluoroscopeContainer'),'top','');  
        dojo.style(dojo.byId('fluoroscopeContainer'),'bottom','0px');
        dojo.style(dojo.byId('crsContainer'),'bottom','');
        dojo.style(dojo.byId('crsContainer'),'top','0px');
        this.autoSizeLayout;
        dojo.byId('atg_store_fluoroscopeOrientationToggle').innerHTML = "Move To Top";
        }
        else {
        dojo.cookie("toolbarOrientationCookie", "top", { expires: 5});
        dojo.style(dojo.byId('fluoroscopeContainer'),'top','0px');
        dojo.style(dojo.byId('fluoroscopeContainer'),'bottom','');
        dojo.style(dojo.byId('crsContainer'),'top','');
        dojo.style(dojo.byId('crsContainer'),'bottom','');
        this.autoSizeLayout;
        dojo.byId('atg_store_fluoroscopeOrientationToggle').innerHTML = "Move To Bottom";
        }
    },
    
    iframeContentLoad: function(){
      if(dojo.byId('atg_store_fluoroscopeUrlInput').value != ""){
      dojo.byId('crsContainer').contentWindow.location.href = dojo.byId('atg_store_fluoroscopeUrlInput').value;
      }
    },

    openCurrentPage: function(){
      window.open(dojo.byId('crsContainer').contentWindow.location.href);
    },
    
    onReturnKey: function(evt){
      if(evt.charOrCode == dojo.keys.ENTER) {
        dojo.stopEvent(evt);
        if(dojo.byId('atg_store_fluoroscopeUrlInput').value != ""){
        dojo.byId('crsContainer').contentWindow.location.href = dojo.byId('atg_store_fluoroscopeUrlInput').value;
        }
      }
    },

    loadEnhancements: function(){
      
      _this = this;
      
      if(dojo.cookie("toolbarOrientationCookie") == 'bottom') {
        _this.flipOrientation();
        _this.autoSizeLayout();
      }
      
      if((dojo.isChrome) || (dojo.isSafari)){
      var scrollHandle1 = dojo.connect(dojo.byId('atg_store_sensorDetailsContainer'), 'onscroll', function(evt){
        _this.dataScroll = evt.target.scrollTop;
      });
      var scrollHandle2 = dojo.connect(dojo.byId('atg_store_fluoroscopeSensorsContainer'), 'onscroll', function(evt){
        _this.sensorScroll = evt.target.scrollTop;
      });
      var scrollHandle3 = dojo.connect(dojo.byId('atg_store_sensorLinksListContainer'), 'onscroll', function(evt){
        _this.stackScroll = evt.target.scrollTop;
      });
      }

      dojo.byId('crsContainer').onload  = function() {
      
      // Build the redirect URLs from the current URL. We dont want to append _requestid param.
      var urlBar = dojo.byId('crsContainer').contentWindow.location.href.split('?');
      var url = urlBar[0];
      var paramObj = urlBar[1];
      
      if(paramObj){
          // Convert to an object so we can easily remove the _requestid
          paramObj = dojo.queryToObject(paramObj);
          if(paramObj._requestid){
            paramObj._requestid = null;
          }
          // Back to a query string
          paramObj = dojo.objectToQuery(paramObj);
      }
      
      if(paramObj){
        url = url + "?" + paramObj;
      }
      
        dojo.byId('atg_store_fluoroscopeUrlInput').value = dojo.byId('crsContainer').contentWindow.location.href;
        dojo.byId('atg_store_sensorManagerUpdateSuccessUrl').value = url;
        dojo.byId('atg_store_sensorManagerUpdateErrorUrl').value = url;
        
        _this.hideLoader();
        
        _this.autoSizeLayout();
        
        _this.iframeDom = dojo.byId('crsContainer').contentWindow;
        
        window.frames[0].parentDojo = dojo;
        _this.dojoFrame = window.frames[0].parentDojo;
        
        _this.dojoFrame.addClass(window.frames[0].document.getElementsByTagName("body")[0],'atg_store_fluoroscope');
      
        styleInclude = document.createElement('link');
        styleInclude.setAttribute("rel","stylesheet");
        styleInclude.setAttribute("type","text/css");
        styleInclude.setAttribute("href","/inspect/css/fluoroscopeFrame.css");
        window.frames[0].document.getElementsByTagName("head")[0].appendChild(styleInclude);
        
        dojo.byId('crsContainer').contentWindow.onunload = function(){
           
           _this.showLoader();
    
           dojo.empty(dojo.byId('atg_store_sensorDetails'));
           dojo.empty(dojo.byId('atg_store_sensorLinksList'));
           dojo.empty(dojo.byId('atg_store_fluoroscopeSensors'));
           dojo.place("<li class='atg_store_sensorTitle'>SENSORS</li>", dojo.byId('atg_store_fluoroscopeSensors'),'first');
           dojo.place("<li class='atg_store_sensorTitle'>SENSOR DATA</li>", dojo.byId('atg_store_sensorDetails'),'first');
           dojo.place("<li class='atg_store_sensorTitle'>PAGE STACK</li>", dojo.byId('atg_store_sensorLinksList'),'first');
           
        };
        
        window.onresize = function(){
          _this.resizeLoader();
          _this.autoSizeLayout();
        }
        
        // Load sensor data throug AJAX request
        _this.ajaxSubmit();
        
        _this.addActionMarkers();
        
        window.frames[0].document.onmousemove = _this.scrollHide;
        
        document.onmousemove = _this.scrollShow;

        dojo.connect(dojo.byId('atg_store_fluoroscopeUrlInput'), 'onkeypress', _this.onReturnKey);
        
        dojo.connect(dojo.byId('atg_store_fluoroscopeUrlButton'), 'onclick', _this.iframeContentLoad);
                
        dojo.connect(dojo.byId('atg_store_fluoroscopeOrientationToggle'), 'onclick', _this.flipOrientation);
               
        dojo.connect(dojo.byId('atg_store_fluoroscopeOpenCurrent'), 'onclick', _this.openCurrentPage);
        
        dojo.subscribe("/actionMarker/click", function(data){
           _this.cloneSensors(data.markerParent);
        });
        
        dojo.subscribe("/sensorButton/click", function(data){
           _this.loadSensorData(data.sensorId);
        }); 
        
      }
      
    },
    
    noCommaNeeded:""

})
