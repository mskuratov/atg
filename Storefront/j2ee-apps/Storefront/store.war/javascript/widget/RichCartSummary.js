/**
 * Rich Cart Widget
 * This widget provides the main rich cart functionality.
 * Created by Jim Barraud, 10/11/06 
 * Reworked by James Wiltshire, 01/17/2007
 *
 * Updated to Dojo 1.0
 */
dojo.provide("atg.store.widget.RichCartSummary");

// dojo.require("dojo.fx");
// dojo.require("dijit._Widget");
// dojo.require("dijit._Templated");
// dojo.require("dijit._Container");

//dojo.require("atg.store.widget.RichCartSummaryItem");

dojo.declare(
  "atg.store.widget.RichCartSummary", 
  [dijit._Widget, dijit._Templated, dijit._Container],
  {
    // Define all global variables for the widget.
    // templateString: dojo.cache("dijit", contextPath + "/javascript/widget/template/richCartSummary.html"),
    
    // dojo.uri.dojoUri(contextPath+"/javascript/widget/template/richCartSummary.html"),
    templateString: '<div id="${id}" class="${id}" dojoAttachPoint="csContainer" style="overflow:hidden;z-index:1;" tabindex="0">  <div id="atg_store_richCartHeader">    <a href="javascript:void(0);" class="atg_store_csClose" dojoAttachPoint="csClose" dojoAttachEvent="onclick:hide">      <img width="12" height="11" src="/crsdocroot/content/images/storefront/btn_close.png" alt="${i18n.close}"/>    </a>    <h3>${i18n.myCartSummary}</h3><span class="atg_store_richCartCount" dojoAttachPoint="csQuantity">??(10)??</span>  </div>        <ul id="atg_store_csContent" dojoAttachPoint="csContent,containerNode">    <li dojoAttachPoint="csEmptyMessage" class="atg_store_emptyRichCart"><span>${i18n.cartIsEmpty}</span></li>  </ul>    <div id="atg_store_csFooter" dojoAttachPoint="csFooter">      <div class="atg_store_richCartSubtotal"><span class="summary"  dojoAttachPoint="csSubtotalContainer">        ${i18n.your} ${i18n.itemsSubtotal}${i18n.labelSeparator}      </span>      <span class="atg_store_csSubtotal atg_store_viewCartPrice" dojoAttachPoint="csSubtotal">      </span>  </div></div>    <a class="atg_store_richCartCartLink" dojoAttachPoint="csFullCheckout" href="${url.checkout}">    ${i18n.viewCart}  </a>      <a href="${url.checkout}" class="atg_store_basicButton atg_store_chevron" dojoAttachPoint="csCheckout">    <span>${i18n.checkout}</span>  </a>    </div>',
    // Widget properties
    triggerWidget: null,  // Reference to trigger widget
    data: null,           // Cart data - should be set with initial widget initialisation properties
    quantityNodeId: null, // DOM ID of the node to contain the cart quantity - i.e. "Show Cart (3)"
    hijackClassName: null,  // CSS class used to signify forms/anchors to hijack 
    highlightColor: null, // Color used to highlight newly added items
    firstPlacementDone: false,
    cartAnimationInProgress: false,
    ESCAPE_KEY_CODE: 27,
    SHIFT_KEY_CODE: 16,
    TAB_KEY_CODE: 9,
    isShiftKeyDown: false, 
    triggerElement: null, // holds a reference to the last item to have focus before richcart opened
    connectHandles: [], // holds a handle to each dojo.connect that is setup
    duration:{
      // Durations in ms of animation elements
      highlight: 3000,
      scroll:500,
      wipe: 280,
      autoHide:5000
    },
    

    /**
     * Initialize the widget
     */
    afterStartup: function(){
      console.debug("Initializing RichCartSummary widget");
      // Load any initial data into the widget
      if (this.data!==null){
        this.setAllCartData(this.data);
      }     
      
      this.triggerWidget=dijit.byId("richCartTrigger");
      
      if (this.doHijack===true){
        this.hijackAllAddToCartNodes();
      }    
    },
    
    /**
     * Sets up all required event handlers
     */
    connectEventHandling: function(){
    
    var _this=this;
    
    // reposition cart when window resized
    this.connectHandles.push(dojo.connect(window, "onresize", this, "placeCart"));
    
    // reposition richcart when scrollbar position changes
    this.connectHandles.push(dojo.connect(window, "onscroll", this, "placeCart"));
    
    // close richcart when users clicks anywhere outside richcart  
    this.connectHandles.push(dojo.connect(document.body, "onclick", function(evt){
          _this.hide();
      }));
    
    // when user clicks on/in the richcart prevent clicks bubbling up to docuemnt body
    this.connectHandles.push(dojo.connect(this.domNode, "onclick", function(evt){
        evt.stopPropagation(); 
      }));
    
    // check which key has been pressed on/in richcart and carry out appropriate action
    this.connectHandles.push(dojo.connect(this.domNode, "onkeydown", function(evt){
        var e = evt || window.event;

        // if escape key pressed close the richcart
        if(e.keyCode === _this.ESCAPE_KEY_CODE) {
            _this.hide();
            return;
        }

        // set isShiftKeyDown=true when shift key pressed, this value is used by other functions
        // note, we don't use and else to set isShiftKeyDown=false as we need to check for key stroke combinations
        // (use onkeyup event to do this)
        if(e.keyCode === _this.SHIFT_KEY_CODE){
          _this.isShiftKeyDown = true;
        }
        
      }));
    
    // check which key has been released on/in richcart and carry out appropriate action
    this.connectHandles.push(dojo.connect(this.domNode, "onkeyup", function(evt){
        var e = evt || window.event;
        
        // set isShiftKeyDown=false when shift key pressed 
        if(e.keyCode === _this.SHIFT_KEY_CODE){
          _this.isShiftKeyDown = false;
          return;
        }
        
      }));
    
    // check which key has been pressed on richcart close button and carry out appropriate action
    this.connectHandles.push(dojo.connect(this.csClose, "onkeydown", function (evt){
        var e = evt || window.event;
            
            // shift-tab combination means users is tabbing backwards, so close the richcart
            if (e.keyCode === _this.TAB_KEY_CODE && _this.isShiftKeyDown) {
              _this.hide();
              return;
            }        
      }));
    
    // get a handle to the last visible link  
    var lastLinkNode;
    if (dojo.style(this.csCheckout, "display") != "none") {
        lastLinkNode = this.csCheckout;
    }
    else {
      lastLinkNode = this.csFullCheckout;
    }
    
    // check which key has been pressed on richcart last link and carry out appropriate action  
    this.connectHandles.push(dojo.connect(lastLinkNode, "onkeydown", function (evt){
          var e = evt || window.event;
            
            // user has pressed tab only means users is tabbing forward, so close the richcart
            if (e.keyCode === _this.TAB_KEY_CODE && !_this.isShiftKeyDown) {
              _this.hide();
              return;
            }
            
        }));
    },
    
    /**
     * Removes all event handlers that have been setup by the richcart
     * removing the event handlers prevents them from firing when
     * the richcart is closed
     */
    disconnectEventHandling: function(){
      dojo.forEach(this.connectHandles, dojo.disconnect);
    },

    /*
     * Replace postCreate with startup. postCreate removed from dojo 0.9
     */
    startup: function(){
      console.debug("Startup RichCartSummary widget");
      var _this=this;
      dojo.addOnLoad(function(){
        // Prepare for first show animation - hide the element's domNode and call the
        // hide animation. Set a callback to change the visibility when hide is complete.
        _this.domNode.style.visibility="hidden";
        _this.attachToContainer();
        _this.hide(function(){
        _this.domNode.style.visibility="visible";
        });
      });
      
      this.afterStartup();
    },
    
    /**
     * Set the rich cart to display all of the passed in data. This function will be
     * called whenever the rich cart widget is initialised (i.e. on page load) and
     * also whenever an item has been added to the cart and a JSON XHR response
     * is received containing the new cart contents.
     */
    setAllCartData: function(pData){
      console.debug("Setting all cart data");
//      console.debug(pData);
      this.data=pData;
      this.clearCartItems();
      this.setCartSummaryData();
      if (pData.itemsContainer.items){
        // Create CartSummaryItem widget for each line item and add to this parent widget
        for (var i=0; i<pData.itemsContainer.items.length; i++){
          this.addCartItem(pData.itemsContainer.items[i]);
        }
      }
      
      // Add messages if any
      //console.debug("pData.messagesContainer: ", pData.messagesContainer);
      if(pData.messagesContainer) {
        if(pData.messagesContainer.messages) {
          for (var i=0; i<pData.messagesContainer.messages.length; i++){
            this.addMessageItem(pData.messagesContainer.messages[i]);
          }
        }
      }
      if(this.onSetAllCartData)
        this.onSetAllCartData();
    },
    
    /**
     * Set the summary data for the cart - this includes the subtotal and item quantity
     */
    setCartSummaryData: function(){      
      // Set the cart quantity total - this is the qty in the 'View Cart (3)' link
      var el;
      if (this.quantityNodeId!==null){
        el=dojo.byId(this.quantityNodeId);
        if (el){
          console.debug("Setting Quantity Value in Rich Cart to: ",this.data.itemsContainer.itemsQuantity);
          el.innerHTML = dojo.string.substitute(this.i18n.itemCount, [this.data.itemsContainer.itemsQuantity]);
        }
        el = this.csQuantity;
        if (el) {
          el.innerHTML = dojo.string.substitute(this.i18n.itemCount, [this.data.itemsContainer.itemsQuantity]);
        }
      }
      
      if (this.data.itemsContainer.itemCount===0){
        this.showEmptyCart(true);
      }
      else {
        this.showEmptyCart(false);
        // Set the subtotal amount in the cart
        this.csSubtotal.innerHTML = this.data.itemsContainer.subtotal;
      }          
    },
    
    /**
     * Turn on/off certain elements of the cart if it is empty. Display a
     * 'cart is empty' message if it is
     */
    showEmptyCart: function(pEmpty){
      if (pEmpty===true){
        // Cart is empty
        dojo.style(this.csSubtotalContainer,'display','none');
        dojo.style(this.csCheckout,'display','none');
        dojo.style(this.csEmptyMessage,'display','');

      }
      else {
        // Cart is not empty
        dojo.style(this.csSubtotalContainer,'display','');
        dojo.style(this.csCheckout,'display','');
        dojo.style(this.csEmptyMessage,'display','none');
      }
    },
    
    /**
     * Add a line item to the rich cart
     */
    addCartItem: function(data){
      console.debug("Adding a Line Item");
      var lineItem = new atg.store.widget.RichCartSummaryItem( {
        data: data,
        highlightColor: this.highlightColor,
        highlightDuration: this.duration.highlight,
        scrollDuration: this.duration.scroll,
        i18n: this.i18n
      });
      lineItem.startup();
      this.csContent.appendChild(lineItem.domNode);
      this.addChild(lineItem);
    },
    
    /*
     * Clear all items from the cart
     */
    clearCartItems: function(){
      var children = this.getChildren();
      for (var i=0; i<children.length; i++){
        this.removeChild(children[i]);
        children[i].destroy();
      }
      
      // clear previously added messages
      dojo.query(".atg_store_richCartMessage").forEach(function(node) {
        dojo.destroy(node);
      });

    },
    
    /**
     * Add a message to the rich cart
     */
    addMessageItem: function(data) {
      console.debug("Adding a message");
      
      var messageItem = new atg.store.widget.RichCartMessage( {
        data: data
      });
      
      messageItem.startup();
      dojo.place(messageItem.domNode, this.csFooter, "first");

    },
        
    /**
     * Toggle display of the cart. This will be called by the CartTrigger widget whenever the
     * show/hide rich cart link is clicked.
     */
    toggleCart: function(){
      if (this.isShowing===true){
        this.hide();
      }
      else {
        // set a callback function to set focus to the first
        // link in the richcart (the close link)
        var closeElem = this.csClose;
        this.show(function(){
          closeElem.focus();
        });
        
        //richcart may have been previously opened to reset vars global to this widget
        this.isShiftKeyDown = false; 
        this.triggerElement = this.triggerWidget.domNode;
        
        // connect event handlers
        this.connectEventHandling();
      }
    },
    
    /**
     * Position the cart at the correct location on screen
     */
    placeCart: function(){
      if (!this.isShowing && this.firstPlacementDone && !this.cartAnimationInProgress){
        return;
      }
      console.debug('placeCart call');
      this.firstPlacementDone=true;
      var node = this.triggerWidget.triggerLink;
      var pos = dojo._abs(node);
      var cartLeft,cartTop,triggerHeight,scrollOffsetHeight;

      // Left position is 228 pixels left of the trigger link
      cartLeft = pos.x - 138;
      cartLeft = (cartLeft > 0) ? cartLeft : 0;
      // Top position is directly under the trigger link
      triggerHeight = dojo._getMarginBox(node.parentNode).h;
      scrollOffsetHeight=dijit.getViewport().t;
      console.debug('scrollOffsetHeight' + scrollOffsetHeight);
      cartTop = pos.y;
      cartTop = (cartTop-scrollOffsetHeight > 0) ? cartTop : scrollOffsetHeight; 

      console.debug("Placing cart @ "+cartLeft+", "+cartTop);
          
      this.domNode.style.left=cartLeft+"px";
      this.domNode.style.top=cartTop+"px";
      this.domNode.style.zIndex="9999";

      
      // If we have a bgIframe for IE6, resize it so it's positioned directly under the cart
      if (this.bgIframe){
        this.bgIframe.size(this.domNode);
      }
    },

    /**
     * Show the Rich Cart
     */
    show: function(callback) {
      console.debug('show cart');
      if (this.isShowing){
        // If we've been passed a callback function, then call it even if we're showing. It's
        // most likely that a new item has been added, and the callback is the highlight
        if (callback){
          callback();
        }
        return;
      }
      
      if (this.cartAnimationInProgress===true){
        return;
      }
      this.cartAnimationInProgress=true;
      this.placeCart();
      var _this=this;

      console.debug('show:node: ' + this.domNode);
      console.debug('show:duration: ' + this.duration.wipe);

      var wipeAnimation=dojo.fx.wipeIn({
              node: this.domNode,
              duration: this.duration.wipe,
              onEnd: function() {
                _this.isShowing = true;
                _this.cartAnimationInProgress=false;

                // IE6 - prevent form elements from shining through cart with hidden bg iframe
                if(dojo.isIE){
                if(dojo.isIE < 6){
                  if(!_this.bgIframe){
                    _this.bgIframe = new dijit.BackgroundIframe();
                    _this.bgIframe.setZIndex(_this.domNode);
                  }
                  _this.bgIframe.size(_this.domNode);
                  _this.bgIframe.show();
                }
                }

                if (callback && dojo.isFunction(callback)){
                  callback();
                }
              }
      });

      console.debug("after wipe animation");

      var fadeAnimation=dojo.fadeIn({
        node: this.domNode,
        properties: {
        opacity: {
            start:0.3,
            end:0.1
        }},
        duration: this.duration.wipe}
        );
      dojo.fx.combine([wipeAnimation,fadeAnimation]).play();
      this.triggerWidget.updateTriggerDisplay();
    },

    /**
     * Hide the Rich Cart
     */
    hide: function(callback){
      if (this.cartAnimationInProgress===true){
        return;
      }

      this.cartAnimationInProgress=true;
      var _this=this;
      console.debug('hide:node: ' + this.domNode);
      console.debug('hide:duration: ' + this.duration.wipe);
      var wipeAnimation=dojo.fx.wipeOut({
              node: this.domNode,
              duration: this.duration.wipe,
              onEnd: function(){
                _this.isShowing = false;
                _this.cartAnimationInProgress=false;
                _this.triggerWidget.updateTriggerDisplay();
                

                // IE6 - Prevent form element shine through - hide hidden iframe
                if(_this.bgIframe){
                  _this.bgIframe.hide();
                  _this.bgIframe.size({left:0, top:0, width:0, height:0});
                }

                if (callback && dojo.isFunction(callback)){
                  callback();
                }
      }});

//      var fadeAnimation=dojo.fadeIn(this.domNode, { start:1, end: 0.8 }, this.duration.wipe);

      var fadeAnimation=dojo.fadeOut({node: this.domNode,
        properties: { 
        opacity: {
            start:1,
            end:0.8
        }},
        duration: this.duration.wipe}
      );
      dojo.fx.combine([wipeAnimation,fadeAnimation]).play();
      this.clearAutoHide();
      
      // return focus to the element that had focus
      // before the richart was opened
      if (this.triggerElement !== null) {
        this.triggerElement.focus();
      }
      
      // remove all event handlers
      this.disconnectEventHandling();
    },
    
    /*
     * Get an array of all items that have been flagged as 'modified. This will
     * usually be just a single item - the item that has just been added to the cart.
     */
    getChangedItemWidgets: function(){
      var changedItems=[];
      var item;
      var children = this.getChildren();
      for (var i=0; i<this.data.itemsContainer.items.length; i++){
        item=this.data.itemsContainer.items[i];
        if (item.modified===true){
          // Get referene to child widget
          changedItems[changedItems.length] = children[i];
        }
      }
      return changedItems;
    },

    /**
     * Start the auto-hide timer. This will close the rich cart after a short period of
     * time, unless the use mouses over the cart display, in which case the timer
     * will be cancelled so the user can continue to view the contents
     */
    startAutoHide: function(){      
      // Clear any existing auto-hide timer
      if (this.autoHideTimer!==null){
        this.clearAutoHide();
      }
      
      console.debug("Starting auto-hide (in "+this.duration.autoHide+" ms)");
      var _this = this;
      // Auto-hide the rich cart after n ms...
      this.autoHideTimer = setTimeout(dojo.hitch(_this, "hide"), this.duration.autoHide);
      
      // ... unless the user mouses over the cart
      dojo.connect(this.domNode, "onmouseover", this, "clearAutoHide");
    },
      
    /**
     * Clear the auto-hide timer. This will stop the widget from auto-hiding. The user
     * must now click the 'hide rich cart' link/icon to hide the widget 
     */
    clearAutoHide: function(){
      console.debug("Clearing auto-hide");
      clearTimeout(this.autoHideTimer);
      dojo.disconnect(this.domNode, "onmouseover", this, "clearAutoHide");
      this.autoHideTimer=null;        
    },
    
    /**
     * Handle a JSON response following an 'add to cart' form submission
     */
    handleResponse: function(data,node){
      console.debug("RichCart:handleResponse");
      console.debug('RichCart:handleResponse:node ' + node);
      console.debug('RichCart:handleResponse:data ' + data);
      // If we got no data whatsoever, then treat this as a serious error
      if (!data){
        this.handleError();
        return;
      }
      
      // If we got an error back in the data then we need to update the UI to display the errors
      if (data.error){
        console.debug("Received error from server - resubmitting form");
        this.resubmitForm(node);
        return;
      }
      
      // All good, update the UI with new cart data
      this.setAllCartData(data);
      var alreadyShowing=this.isShowing;
      var changedItems=this.getChangedItemWidgets();

      // Show the cart and scroll the first newly added item into view
      var _this=this;
      this.show(function(){
        // Scroll the first newly added item into view
        if (changedItems.length>0){
          changedItems[0].scrollIntoView();
        }
        for (var i=0; i<changedItems.length; i++){
          changedItems[i].highlight();
        }
        _this.enableNode(node);
        
        // connect event handlers
        _this.connectEventHandling();
      });
           
      // Start the auto-hide timer if the cart wasn't already showing. This will also reset the timer
      // if it's already running
      if (!alreadyShowing || this.autoHideTimer!==null){
        this.startAutoHide();
      }
    },
    
    // reset the color size picker
    resetPicker: function(){

      dojo.query(".atg_store_quantity input[type='text']").forEach(
          function(inputElement) {
              console.debug("Resetting quantity field: ", inputElement);
              inputElement.value = "0";
         }
      );
      
    },
    
    /*
     * Connect the cart to all forms and links that have the specified className.
     * The class will be set on any <input type="submit"> and <a> tags that are submitted or clicked
     * to add items to the cart. All of these nodes must be 'hijacked' so that the
     * http request uses XHR so that the rich cart can operate.
     */
    hijackAllAddToCartNodes: function(){
      console.debug("Connecting RichCart to all elements with class ["+this.hijackClassName+"]");
      var _this = this;
      console.debug('class name: ' + this.hijackClassName);
      dojo.query("*."+this.hijackClassName).forEach(function(node) {
        _this.hijackNode(node);
      });
    },
    
    /*
     * Hijack a node. The node should be either a <form> or an <a> node.
     * Hookup the submit to use XHR instead of standard browser request, and 
     * process the returned JSON data with the handleRespones() function
     * 
     */
    hijackNode: function(node){
      console.debug("Hijacking node");
      console.debug(node);
      if (node.isHijacked){
        console.debug("Node is already hijacked - ignoring");
        return;
      }
      node.isHijacked=true;
           
      // Create object with common params for io.bind call
      var _this = this;
//      var _node = node;
      var bindParams={
        headers: { "Accept" : "application/json" },
//        mimetype: "application/json",
        handleAs: "json",
        load: function(data, ioArgs) {
          _this.handleResponse(data,node);
        },
        error: function(data, ioArgs) {
          _this.handleError(data, ioArgs);
        },
        timeout: function(data, ioArgs) {
          _this.handleError(data, ioArgs);
        }
      };
      
      
      if (node.nodeName=="INPUT"){
        dojo.connect(node, "onclick", function(evt){
          evt.cancelBubble=true;
          evt.preventDefault();
          // Get parent form node for this input node
          var formNode=_this.getParentNode(node, "form");
          console.debug('formNode: ' + formNode);
          // Create content object with the name/value pair of the submit button that's been clicked
          // dojo.io.bind doesn't send the value of submit buttons when serializing the form as it 
          // doesn't know which one has been clicked. Server side FormHandlers need this data to
          // invoke the correct formHandler method.
          var content={};
          content[node.name]=node.value;
          
          console.debug("Add to Cart form clicked - submitting form");
//          console.debug(formNode);

          // bug fixed: 154139
          // hide div with error messages
          dojo.query(".atg_store_formValidationError").forEach( function(errorDiv){
            dojo.empty(errorDiv);
          }, this);



          // Add the form node and the submit button name/value to the io.bind params
          dojo._mixin(bindParams,{
            form: formNode,
            content: content
          });
          
          _this.disableNode(node);
          dojo.xhrPost(bindParams);
          
          // hold a reference to node as we will return focus to it when richcart is closed
          _this.triggerElement = node;
          return false;
        });
      }
      else if (node.nodeName=="A"){
        dojo.connect(node, "onclick", function(evt){
          console.debug("Add to Cart link clicked");
          evt.preventDefault();
          
          // Ensure it's not a double click
          if (node.currentlyAdding && node.currentlyAdding===true){
            console.debug("This link has already been clicked - ignoring");
            return;
          }
          
          // Add the URL of the clicked link to the io.bind params

          dojo._mixin(bindParams,{
            url: node.href
          });
         
          _this.disableNode(node);
          dojo.xhrGet(bindParams);
          
        });               
      }
      else{
        console.debug("Node is not a form submit or an anchor - ignoring");
      }     
    },
    
    /**
     * Attach this widget's domNode to its containing node
     */
    attachToContainer: function(){
      console.debug("Appending cart domNode to " + this.domNode);
      document.body.appendChild(this.domNode);
    },
    
    /**
     * Function that will be called whenever an error or timeout occurs with an io.bind call
     * Changes the page location to 'url.error'. This is usually set to the standard cart
     * page, so if anything goes wrong with the Rich Cart, we get to fall back to the standard cart.
     */
    handleError: function(data, ioArgs){
      console.debug("RichCartSummary:handleError");
      document.location=this.url.error;
    },
    
    /**
     * Disable a node whilst it is being added to the cart. Used to prevent double clicks resulting
     * in duplicate additions to the cart.
     */
     disableNode: function(node){

       if (node.nodeName=="INPUT"){

         // Store original properties we're about to mess with
         node.originalProps={};
         node.originalProps.width=node.style.width;
         node.originalProps.height=node.style.height;
         // Set values for width and height so element doesn't change size
         node.style.width=dojo._getBorderBox(node).w+"px";

         node.originalProps.value=node.value;
         node.disabled=true;
         node.value=this.i18n.addingToCart;

       }

       else if (node.nodeName=="A"){

         //Edited In Order To Preserve Width During Dynamic InnerHTML Change
         
         // Acquire child span of A
    	 // Check for IE 8 and lower  
         if (navigator.userAgent.indexOf('MSIE') !=-1 && !(document.getElementsByClassName)){
         var childspan = node.childNodes[0];
         }else{
         var childspan = node.childNodes[1];
         }

         // Store original properties we're about to mess with   
         node.originalProps={};
         node.originalProps.width=childspan.style.width;
         node.originalProps.height=childspan.style.height;
         
         // Get The "span" padding-left
         basepadding = dojo.style(childspan, "paddingLeft");

         // Get the "span" offsetWidth , and subtract the left padding from it.
         basewidth = childspan.offsetWidth - basepadding;

         // Set a fixed width on the "span" element
         childspan.style.width=basewidth+"px";

         // Set values for width and height so element doesn't change size
         node.originalProps.innerHTML=childspan.innerHTML;
         node.currentlyAdding=true;
         childspan.innerHTML=this.i18n.addingToCart;

       }
     },

     /**
      * Re-enable a node that has been disabled by disableNode();
      */
     enableNode: function(node){     
        
       if (node.nodeName=="INPUT"){

         node.disabled=false;
         node.value=node.originalProps.value;

         // Reset size attributes
         node.style.width=node.originalProps.width;
         node.style.height=node.originalProps.height;
         node.originalProps=null;

       }
       else if (node.nodeName=="A"){

         //Acquire child span of A
    	 // Check for IE 8 and lower 
         if (navigator.userAgent.indexOf('MSIE') !=-1 && !(document.getElementsByClassName))
          {
          var childspan = node.childNodes[0];
          }
          else {
          var childspan = node.childNodes[1];
          }

         node.currentlyAdding=false;
         childspan.innerHTML=node.originalProps.innerHTML;   

         // Reset size attributes
         childspan.style.width=node.originalProps.width;
         childspan.style.height=node.originalProps.height;
         node.originalProps=null;

       }


     },

    
    /**
     * Resubmit the AddToCart form using a normal HTTP request (non XHR).
     * The Submit button's node should be passed in to signify which button was clicked.
     */
    resubmitForm: function(node){
      // Create hidden form element to copy submit button's value into. Need to do this as disabled elements
      // are not submitted from a form by the browser.
      console.debug("RichCartSummary:resubmit from");
      var replacementNode=document.createElement("input");
      replacementNode.type="hidden";
      replacementNode.name=node.name;
      replacementNode.value=node.value;
      
      // Append this to the parent form
      var formNode=this.getParentNode(node, "FORM");
      formNode.appendChild(replacementNode);
      
      formNode.submit();
    },

    getParentNode: function(/* HTMLElement */node, /* string */type) {
      //  summary
      //  Returns the first ancestor of node with tagName type.
      var _document = dojo.doc;
      var parent = dojo.byId(node);
      type = type.toLowerCase();

      while((parent)&&(parent.nodeName.toLowerCase()!=type)){
        if(parent==(_document["body"]||_document["documentElement"])){
          return null;
        }
        parent = parent.parentNode;
      }
      return parent;  //  HTMLElement
    },

    onSetAllCartData: null
  }
);

