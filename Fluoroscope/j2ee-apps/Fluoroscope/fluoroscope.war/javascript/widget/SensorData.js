/**
 * Fluoroscope Widget
 * This widget facilitates fluoroscope functionality
 * Created by Ken Wheeler, 2/22/11 
 *
 */
 
dojo.provide("atg.store.widget.SensorData");

dojo.declare(
  "atg.store.widget.SensorData", 
  [dijit._Widget, dijit._Templated],
  {
    templateString: dojo.cache("dijit", contextPath + "/javascript/widget/template/sensorData.html"),
    sensorDetails: "",
    
     loadStackData: function(evt){
       
        dojo.query('.pageStackLink').forEach(function(node,index,array){
          if(node!=evt.target){
            node.removeAttribute('isactive',true);
          }
           else{
            node.setAttribute('isactive',true);
           }
        });
       
        dojo.query('.sensorOverlay', window.frames[0].document.body).forEach(function(node,index,array){
          node.removeAttribute('isactive',true);
          dojo.destroy(node);
        });

        targetUri = evt.target.innerHTML;
        targetSensor = evt.target.getAttribute('parentId');
        
        dojo.query('span[id$="'+targetSensor+'"]', window.frames[0].document).forEach(function(node,index,array){
          targetNode=node;
        });
        
        if(targetNode){
          targetDiv = targetNode.parentNode;

          overlayDiv = dojo.place("<span class='sensorOverlay'></span>", targetDiv,'first');
          overlayDiv.setAttribute('isactive',true);
          
        }
        
        this.createStackData(targetSensor, targetUri);

      },
      
      createStackData: function(sensorID, requestUri){
        
        __this = this;

        dojo.empty(dojo.byId('atg_store_sensorLinksList'));

        dojo.style(dojo.byId('atg_store_sensorLinksList'),'opacity','0')

        if (!atg_sensorManager.JSONdata){
          console.debug("JSONdata is empty");
          return;
        }
        
        objct = atg_sensorManager.JSONdata[sensorID];
        if (!objct){
          console.debug("Sensor ID is not found in sensor data object.");
          return;	
        }
        
        beginId = objct.beginEventId;
        
        if(beginId != "") {
          objct = atg_sensorManager.JSONdata[beginId]; 
        }
        
        data = "<strong class='stackTitle'>" + requestUri + "</strong>";
        __this.renderStackData(data);

        for(var prm in objct){          
        if(typeof(objct[prm])=="object") {
          if(prm!='Page Stack'){
          data = "<strong>" + prm + "</strong>: ";
          __this.renderStackData(data);
          subobjct = objct[prm];           
        for(var subprm in subobjct){            
             data = "<strong>" + subprm + "</strong>: " + subobjct[subprm];
             __this.renderStackData(data);
        } 
        } 
        }       
        else {             
        if((prm=='Scenario Manager Name') || (prm=='Droplet Name') || (prm=='Droplet Path')) {     
        data = "<strong>" + prm + "</strong>: <a href='/dyn/admin/nucleus" + objct[prm] + "'>" + objct[prm] + "</a> (HTML ADMIN)";
        __this.renderStackData(data);
        }
        else {
        if(prm!='beginEventId'){
        data = "<strong>" + prm + "</strong>: " + objct[prm];
        __this.renderStackData(data);
        }
        }           
        }           
        }

        _this.dataAnimatedScroll(dojo.byId('atg_store_sensorLinksListContainer'));

        dojo.animateProperty({
          node: dojo.byId('atg_store_sensorLinksList'),
          properties: {
            opacity: 1
          }
        }).play();

      },
      
      renderStackData: function(sensorData) {
        var newSensorData = new atg.store.widget.SensorData({ sensorDetails: sensorData });
        dojo.byId('atg_store_sensorLinksList').appendChild(newSensorData.domNode);
      },
      
      showSensorOverlay: function(evt) {

        targetSensor = evt.target.getAttribute('parentId');

        dojo.query('span[id$="'+targetSensor+'"]', window.frames[0].document).forEach(function(node,index,array){
          targetNode=node;
        });
        
        if(targetNode){
          
        dojo.query('.sensorOverlay', window.frames[0].document.body).forEach(function(node,index,array){
          if(!node.getAttribute('isactive')){
          dojo.destroy(node);
          }
        });
               
        targetDiv = targetNode.parentNode;
                
        if(dojo.query('> .sensorOverlay', targetDiv).length < 1){
        overlayDiv = dojo.place("<span class='sensorOverlay'></span>", targetDiv,'first');
        }
        
        }

      },
      
      hideSensorOverlay: function(evt){
        if(!evt.target.getAttribute('isactive')){
        dojo.query('.sensorOverlay', window.frames[0].document.body).forEach(function(node,index,array){
          if(!node.getAttribute('isactive')){
          dojo.destroy(node);
          }
        });
        }
      },
  
    noCommaNeeded:""

})