/**
 * Fluoroscope Widget
 * This widget facilitates fluoroscope functionality
 * Created by Ken Wheeler, 2/22/11 
 *
 */
 
dojo.provide("atg.store.widget.SensorMarker");

dojo.declare(
  "atg.store.widget.SensorMarker", 
  [dijit._Widget, dijit._Templated],
  {
    
    templateString: dojo.cache("dijit", contextPath + "/javascript/widget/template/sensorMarker.html"),
    
    actionMarkerClick: function(evt){
      dojo.stopEvent(evt);

      if(this.domNode.getAttribute('isactive')){
        dojo.empty(dojo.byId('atg_store_sensorDetails'));
        dojo.empty(dojo.byId('atg_store_fluoroscopeSensors'));
        dojo.empty(dojo.byId('atg_store_sensorLinksList'));
        dojo.place("<li class='atg_store_sensorTitle'>SENSORS</li>", dojo.byId('atg_store_fluoroscopeSensors'),'first');
        dojo.place("<li class='atg_store_sensorTitle'>SENSOR DATA</li>", dojo.byId('atg_store_sensorDetails'),'first');
        dojo.place("<li class='atg_store_sensorTitle'>PAGE STACK</li>", dojo.byId('atg_store_sensorLinksList'),'first');
        dojo.query('> .actionOverlay', evt.target.parentNode).forEach(function(node,index,array){
           dojo.destroy(node);
        });
        dojo.query('.sensorOverlay', window.frames[0].document.body).forEach(function(node,index,array){
          dojo.destroy(node);
        });
        dojo.removeClass(evt.target , 'actionMarkerActive');
        evt.target.removeAttribute('isactive');
      }
      else{
      dojo.empty(dojo.byId('atg_store_sensorDetails'));
      dojo.empty(dojo.byId('atg_store_fluoroscopeSensors'));
      dojo.empty(dojo.byId('atg_store_sensorLinksList'));
      dojo.place("<li class='atg_store_sensorTitle'>SENSOR DATA</li>", dojo.byId('atg_store_sensorDetails'),'first');
      dojo.place("<li class='atg_store_sensorTitle'>PAGE STACK</li>", dojo.byId('atg_store_sensorLinksList'),'first');
      this.actionMarkerActivate(evt.target);
      this.actionMarkerOutline(evt.target.parentNode);  
      dojo.publish('/actionMarker/click' ,[{ markerParent: evt.target.parentNode }]);
      }
      
    },
    
    actionMarkerOver: function(){
      dojo.addClass(this.domNode , 'actionMarkerActive');
      this.domNode.style.zIndex = 2001;
      this.actionMarkerOutline(this.domNode.parentNode);  
    },
    
    actionMarkerOut: function(){
      if(!this.domNode.getAttribute('isactive')){
       dojo.removeClass(this.domNode , 'actionMarkerActive');
       this.domNode.style.zIndex = 2000;
       dojo.query('> .actionOverlay', this.domNode.parentNode).forEach(function(node,index,array){
         dojo.destroy(node);
       });
      }
    },
    
    actionMarkerOutline: function(targetDiv) {
        if(dojo.query('> .actionOverlay', targetDiv).length < 1){
        dojo.place("<span class='actionOverlay'></span>", targetDiv,'first');
        }
    },
    
    actionMarkerActivate: function(node){
      thisNode = node;
      dojo.query('.actionMarker', window.frames[0].document.body).forEach(function(node,index,array){
        dojo.removeClass(node , 'actionMarkerActive');
        if(node != thisNode) {
        dojo.query('> .actionOverlay', node.parentNode).forEach(function(node,index,array){
           dojo.destroy(node);
         });
        }
        node.style.zIndex = 2000;
        node.removeAttribute('isactive');
      });
      dojo.addClass(node , 'actionMarkerActive');
      dojo.addClass(node.parentNode, 'activeParent');
      node.style.zIndex = 2001;
      node.setAttribute('isactive',true);
    },
    
    noCommaNeeded:""

})