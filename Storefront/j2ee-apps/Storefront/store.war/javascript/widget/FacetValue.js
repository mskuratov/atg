/**
 *  Facet Value Widget
 *
 *  Created by Jon Sykes, Jan 2011 
 *
 *  Widget for the display of a single facet value and it's state.
 *
 */
dojo.provide("atg.store.widget.facetValue");


dojo.declare(
  "atg.store.widget.facetValue", 
  [dijit._Widget, dijit._Templated],
  {
    debugOn: false,
    //templateString: dojo.cache("dijit", contextPath + "/javascript/widget/template/facetValue.html"),
    templateString: '<li><a title="${facetValue}" href="javascript:void(0);" dojoAttachEvent="onclick:handleOnClick">${facetValue} (${facetQty})</a></li>',
    parentWidget: "",
    facetValueUrl: "",
    facetValue: "",
    facetQty: "",
    
    /**
     * Setup, handle instance when the facet value is currently selected
     */
    postCreate: function(){
      // loop through the facet groups create facetGroup widgets for each
      this._debug = this.parentWidget._debug;
      // remove the quantity field as this is a child value of a selected facet group.
      if(this.parentWidget.facetGroupSelected == "true"){
        dojo.addClass(this.domNode, "remove");
        dojo.query("a", this.domNode)[0].innerHTML = this.facetValue;
      }
    },
    
    /**
     * Handle Interaction, publish product listing update topic
     */
    handleOnClick: function(evt){
      evt.preventDefault();
      dojo.publish("/atg/productListing/update", [{ "q_facetTrail": this.facetValueUrl, "token": "", "p": ""}]);
    },
    
    destroy: function(){
      // stub
      
    },
    
    // don't edit below here
    noCommaNeeded:""
})