dojo.provide("atg.store.rightNow");

atg.store.rightNow = {

	isFirstLoad: true,

	isWidgetReady: function() {
		return (typeof skw_0 != 'undefined') && (skw_0.status == 'SUCCESS');
	},

	isSearchComplete: function() {
		// If data.Query IS NOT "undefined" then a search has
		// been performed. If a search has been performed and
		// the widget's query string and the data.Query object's
		// searchTerms parameter are the same then the widget has
		// finished updating, so ok to decorate links. If query
		// and searchTerms don't match then the widget hasn't
		// finished updating so wait before checking again.
		return (typeof skw_0.data.Query != 'undefined') &&
			(skw_0.query == skw_0.data.Query[0].searchTerms);
	},

	resetSearchTerm: function(){
		// the query search term is used to determine
		// if the search is complete, therefore need
		// to reset the value to handle the scenario where
		// the user searches for the same term twice in
		// succession - the search terms would match
		// and decoratelinks would be called before the
		// rightnow widget has completed its work
		if (typeof skw_0.data.Query != 'undefined'){
			delete skw_0.data.Query;
		}

		atg.store.rightNow.isFirstLoad = false;
	},

	decorateLinks: function() {
		console.debug(">>> decorateLinks");

		var allLinks = dojo.query(".rn_Navigation rn_Show a, .rn_Item  a, .rn_List  a, .rn_Navigation  a");
		
		allLinks.forEach(function(link){
			console.debug(link);
			dojo.connect(link, "onclick", null, function(e){
				var node    = e.target;
				var url     = dojo.attr(node, "href");
				var urlText = dojo.attr(node, "innerHTML");
				var iframe  = dojo.byId("knowledgebaseItemIframe");
				
				// set the src of the iframe, ensuring to add
				// the in page anchor name for the anchor that
				// appears at the top of the rightnow page...
				// ensures that focus is inside the iframe
				dojo.attr(iframe, "src", url + "#rn_MainContent");
				dojo.attr(iframe, "title", urlText);
				dojo.stopEvent(e);
				
				var vs = dojo.window.getBox();
				var dialog  = dijit.byId("knowledgebaseItem");
				dialog.set("dimensions", [vs.w * 0.75, vs.h * 0.95])
				dialog.show();
					
				// When the dialog is closed remove the contents of the 
				// iframe (prevents the iframe from showing any previous
				// knowledge item before switching to the current item)
				dojo.connect(dialog, "onHide", function(evt){
		      		dojo.attr(iframe, "src", "");
				});

			// prevent the close icon from being hidden when
			// the user moves the mouse out of the dialog
			dialog._navOut= dialog._navIn;
				
			});
		});

		// set focus to the first link, this
		// will ensure that the browser moves
		// the now visible knowledgebase div
		// into the viewport, should the div
		// be below the fold
		dojo.byId("rn_Queryskw_0").focus();

		console.debug("<<< decorateLinks");
	},

	decorateLinksWhenReady: function() {
		console.debug(">>> decorateLinksWhenReady");

		if (atg.store.rightNow.isWidgetReady() && ((atg.store.rightNow.isSearchComplete()) || (atg.store.rightNow.isFirstLoad))) {
			atg.store.rightNow.decorateLinks();
			atg.store.rightNow.resetSearchTerm();
			atg.store.rightNow.isFirstLoad = false;
		} else {
			console.debug("setTimeout decorateLinksWhenReady");
			setTimeout(atg.store.rightNow.decorateLinksWhenReady, 50);
		}

		console.debug("<<< decorateLinksWhenReady");
	},

	toggleKnowledgebaseDisplay: function() {
		console.debug(">>> toggleKnowledgebaseDisplay");

		if(dojo.hasClass("knowledgebase", "hideKnowledgebase")){
			dojo.removeClass("knowledgebase", "hideKnowledgebase");
			atg.store.rightNow.decorateLinksWhenReady();
		}
		else {
			dojo.addClass("knowledgebase", "hideKnowledgebase");
		}

		console.debug("<<< toggleKnowledgebaseDisplay");
	},


	closeDialog:  function(){
		console.debug(">>> closeDialog");

		var dialog  = dijit.byId("knowledgebaseItem");
		dialog.hide();

		console.debug("<<< closeDialog");
	},

	postMessageListener: function(event){
		console.log(">>> postMessageListener");

		/* "event.origin should be updated as necessary based on the url */
		if ( event.origin !== "http://oracleatg.rightnowdemo.com" ) {
			console.debug("postMessage did not originate from a trusted domain");
			return;
		}
		else{
			if (event.data === "close") {
				console.debug("postMessage to close dialog received, closing dialog");
				atg.store.rightNow.closeDialog();
			}
		}

		console.log("<<< postMessageListener");
	}
};