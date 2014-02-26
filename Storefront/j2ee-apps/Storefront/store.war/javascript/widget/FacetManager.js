/**
 * Facet Manager Widget

 * Created by Jon Sykes, Jan 2011 
 *
 */
dojo.provide("atg.store.widget.facetManager");


dojo.declare(
  "atg.store.widget.facetManager", 
  [dijit._Widget, dijit._Container],
  {
    debugOn: false,
    id:"",
    facetData: {},
    facetsNode: [],
    facetValuePaging: 5,
    
    // init the widget
    postCreate: function(){
      this.containerNode = this.facetsNode;
      // check data contains facet groups
      this._debug("Initial Facet Data: ", this.facetData);
      
      // i18n variable setups      
      this.expandFacetLabel = this.facetData.expandFacetLabel;
      this.collapseFacetLabel = this.facetData.collapseFacetLabel;
      
      // Do we have facets, if so, create the UI
      if(this.facetData.facetOptions.length > 0){
        this.createFacets(this.facetData);
      }
      // subscribe to the facetManager update topic for other parts of the UI
      // to publish to if needs be update, it is expecting facetData level JSON
      this.topics = [
        dojo.subscribe("/atg/facetManager/update", this, "createFacets")
      ];
      
    },
    
    createFacets: function(facetObject){
      this._debug("Create Facets Object: ", facetObject);
      var facetData = facetObject.facetObject || facetObject;
      
      // Update the results count
      if(facetData.resultsCount){
        if(dojo.byId("resultsCount")){
          dojo.byId("resultsCount").innerHTML = facetData.resultsCount;
        }
      }
      
      // loop through the facet groups create facetGroup widgets for each
      this.clearFacets();
      
      var facetGroups = facetData.facetOptions;
      dojo.forEach((facetGroups), function(facet, index, facetArray){
        this._debug("Adding Facet Group: ", facet.name);
        this.addFacetGroup(facet);
      }, this);
      
    },
    
    /**
     * Add a Facet Group
     */
    addFacetGroup: function(data){
      var newFacetGroup = new atg.store.widget.facetGroup({
        expandFacetLabel: this.expandFacetLabel,
        collapseFacetLabel:this.collapseFacetLabel,
        facetValuePaging: this.facetValuePaging,
        displayAllOptions: data.displayAllOptions,
        parentWidget: this,
        facetGroupId: data.id,
        facetGroupTitle: data.name,
        facetGroupSelected: data.selected,
        data: data
      });
      // add product widget to this parent;
      this.addChild(newFacetGroup);
      // add facet to the DOM
      this.containerNode.appendChild(newFacetGroup.domNode);
      this._debug("addFacetGroup Dom Node: ", newFacetGroup.domNode);
    },
    
    
    clearFacets: function(){
      // get all child widgets
      var children = this.getChildren();
      // loop thru them all and destroy them
      for (var i=0; i<children.length; i++){
        this.removeChild(children[i]);
        children[i].destroy();
      }
      // delete all child dom nodes in facetOpions parent div
      dojo.empty(this.containerNode);
    },
    
    
    _debug: function(debugTitle, toBeDebugged){
      if(this.debugOn){ 
        var title = debugTitle || "";
        console.debug("----- "+ this.id +" -----");     
        console.debug(title, toBeDebugged);     
      }
    },
    
    destroyRecursive: function(){
      // defining destroyRecursive for back-compat.
      // this.destroy();
    },
    
    // don't edit below here
    noCommaNeeded:""
})