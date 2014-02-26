/**
 *  Facet Group Widget
 *
 *  Created by Jon Sykes, Jan 2011 
 *
 *  Manages a group of facet values:
 *  This widget UI includes:
 *    - title 
 *    - series of child widgets for each value
 *    - show more/less state and display
 *
 */
dojo.provide("atg.store.widget.facetGroup");


dojo.declare(
  "atg.store.widget.facetGroup", 
  [dijit._Widget, dijit._Templated, dijit._Container],
  {
    debugOn: false,
    //templatePath: contextPath + "/javascript/widget/template/facetGroup.html",
    //templateString: dojo.cache("dijit", contextPath + "/javascript/widget/template/facetGroup.html"),
    templateString: '<div class="atg_store_facetsGroup_options_catsub" id="facet_${facetGroupId}"><h5>${facetGroupTitle}</h5><div id="${facetGroupId}">     <ul dojoAttachPoint="containerNode" class=""><li class="atg_store_facetMore" dojoAttachPoint="showMoreNode"><a title="${expandFacetLabel}" href="" dojoAttachEvent="onclick:handleClick">${expandFacetLabel}</a></li><li class="atg_store_facetLess" dojoAttachPoint="showLessNode"><a title="${collapseFacetLabel}" href="" dojoAttachEvent="onclick:handleClick">${collapseFacetLabel}</a></li></ul></div></div>',
    parentWidget: "",
    facetGroupId: "",
    facetGroupTitle: "",
    facetValuePaging: 5,
    facetGroupSelected: false,
    displayAllOptions: false,
    data: [],
    
    /**
     * Setup and Loop through child facet values
     */
    postCreate: function(){
      // loop through the facet groups create facetGroup widgets for each
      this._debug = this.parentWidget._debug;
      this._debug("Post Create Facet: ", this.facetGroupTitle);
      var facetOptions = this.data.options;
      _thisGroup = this;
      dojo.forEach((facetOptions), function(option, index, optionArray){
        this._debug("Adding Facet Option: ", option.facetName);
        this.addFacetValue(option, index);
      }, this);
      if(this.facetGroupSelected == "true"){
        dojo.addClass(this.domNode, "selected");
      }
    },
    /**
     * Add Each Facet Value as a widget
     */
    addFacetValue: function(data, index){
      
      var facetValue = new atg.store.widget.facetValue({
        parentWidget: this,
        facetValueId: data.id,
        facetValueTitle: data.facetName,
        facetValueUrl: data.urlFacet,
        facetValue: data.facetName,
        facetQty: data.qty || "",
        data: data
      });
      this._debug("this.displayAllOptions", this.displayAllOptions);
      
      if( typeof(this.displayAllOptions) == "undefined" && index >= this.facetValuePaging){
        dojo.addClass(facetValue.domNode, "atg_store_hiddenFacet");
        dojo.addClass(this.containerNode, "atg_store_facetOptions");
      }
      // add facet to the DOM
      dojo.place(facetValue.domNode, this.showMoreNode, "before")
      //this.containerNode.appendChild(facetValue.domNode);
      this._debug("Creating Facet Value Wudget: ", facetValue.domNode);
      // add product widget to this parent;
      this.addChild(this.parentWidget);
    },
    /**
     * Toggle the more less classname on the parent container
     */
    handleClick: function(evt){
      this._debug("More/Less Click Handle: ", evt);
      evt.preventDefault();  
      dojo.toggleClass(this.containerNode, "atg_store_facetOptionsShowMore");
    },
    
    destroyRecursive: function(){
      // defining destroyRecursive for back-compat.
      this.destroy();
    },
    
    // don't edit below here
    noCommaNeeded:""
})