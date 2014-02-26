/**
 * Color/Size picker implementation.
 * Dojo version: 1.0
 */

dojo.provide("atg.store.picker");
dojo.require("dojo.parser");

atg.store.picker={
      
/**
 * Adds the item to Cart 
 */
  addtoCart : function (){
    
    if (!this.checkAddtoCartAvailable()){
      // if product's color/size is not selected do nothing
      // just show corresponding message
      dojo.byId('promptSelectDIV').style.display='block';
      dojo.byId('promptSelectDIV2').style.display='none';
      dojo.byId('promptSelectDIV3').style.display='none';
      return;
    }
    //post addToCart form to richCart widget
    dijit.byId("atg_store_richCart").postForm("addToCart");
    
  },

/**
 * Check the status of addToCart form
 *
 * @return  true: if item can be added to the cart
 *     false: if not
 */
  checkAddtoCartAvailable: function(){ 
    var addtocartform = dojo.byId("addToCart");
    var selectedSku =  addtocartform.elements["/atg/commerce/order/purchase/CartModifierFormHandler.items[0].catalogRefId"].value;    
   
    if( !selectedSku){  
      return false;
      console.debug("checkAddtoCartAvailable: SKU not available");
    }

    return true;
  },
  
/**
 * Called when a user clicks on a color, it changes selected color
 * to the passed one
 * @param color: which color is selected
 */
  clickColor: function(element,color){
      
    console.debug('selected color is ' + color);
    var formId = "colorsizerefreshform";
    var form = dojo.byId(formId);
    var currentColor = form.elements.selectedColor.value;

    //if the color is not changing, don't do anything
    if(currentColor == color){
      return;
    }

    //set the new selected color in the refresh form and submit it
    form.elements.selectedColor.value = color;
    var picker=atg.store.picker;
    picker.setQuantity(formId);
    picker.setGiftlistId(formId);
    picker.submitRefreshForm(element,formId);  
  },  


/**
 * Called when a user clicks on a size, it changes selected size 
 * in the refresh form to the passed one
 * @param size: which size is selected
 */
  clickSize: function(element,size){
                
    console.debug('selected size is ' + size);
    var formId = "colorsizerefreshform";
    var form = dojo.byId(formId);

    //if the user clicks the size that's already selected, don't do anything
    var currentSize = form.elements.selectedSize.value;
    if(currentSize === size){
      return;
    }

    //set the new selected size in the refresh form and submit it
    form.elements.selectedSize.value = size;
    var picker=atg.store.picker;
    picker.setQuantity(formId);
    picker.setGiftlistId(formId);
    picker.submitRefreshForm(element,formId);       
  },
  
  /**
   * Called when a user clicks on a color, it changes selected color
   * to the passed one
   * @param color: which color is selected
   */
  clickGiftColor: function(color, productId, gwpRadioId){
    // If we click the colour select the products radio button.
    if(gwpRadioId){
      var gwpRadioButton = dojo.byId(gwpRadioId);
      if(gwpRadioButton){
        atg.store.picker.clickGiftProduct(productId, null, gwpRadioButton);
      }
    }
	  
    console.debug('selected color is ' + color);
    var formId = "colorsizerefreshform_" + productId;
    var form = dojo.byId(formId);
    var currentColor = form.elements.selectedColor.value;

    //if the color is not changing, don't do anything
    if(currentColor == color){
      return;
    }

    //set the new selected color in the refresh form and submit it
    form.elements.selectedColor.value = color;
    var picker=atg.store.picker;
    picker.submitRefreshGiftForm(formId, productId);  
  },  
  
  /**
   * Called when a user clicks on a size, it changes selected size 
   * in the refresh form to the passed one
   * @param size: which size is selected
   */
  clickGiftSize: function(size, productId, gwpRadioId){
    // If we click the size select the products radio button.
    if(gwpRadioId){
      var gwpRadioButton = dojo.byId(gwpRadioId);
      if(gwpRadioButton){
        atg.store.picker.clickGiftProduct(productId, null, gwpRadioButton);
      }
    }
	    
    console.debug('selected size is ' + size);
    var formId = "colorsizerefreshform_" + productId;
    var form = dojo.byId(formId);

    //if the user clicks the size that's already selected, don't do anything
    var currentSize = form.elements.selectedSize.value;
    if(currentSize === size){
      return;
    }

    //set the new selected size in the refresh form and submit it
    form.elements.selectedSize.value = size;
    var picker=atg.store.picker;
    picker.submitRefreshGiftForm(formId, productId);       
  },    
  
  /**
   * Called when a user clicks on a selector radio button, 
   * 
   * @param productId: which product is selected
   * @param skuId:
   * @param gwpRadioButton:
   */
  clickGiftProduct: function(productId, skuId, gwpRadioButton){
	
    if(gwpRadioButton){
      gwpRadioButton.checked = true;
    }
	
    console.debug('selected product is ' + productId);
    var formId = "colorsizerefreshform_" + productId;
    var form = dojo.byId(formId);

    var gwpForm = dojo.byId("gwpform");
    if(!gwpForm){
      console.debug('gwpform is not found');
      return;  
    }
          
    gwpForm.elements["/atg/commerce/promotion/GiftWithPurchaseFormHandler.productId"].value = productId;
    console.debug("Selected product ID is " + gwpForm.elements["/atg/commerce/promotion/GiftWithPurchaseFormHandler.productId"].value);


    if(skuId != null) {
      //set the new selected sku ID in the form
      form.elements.skuId.value = skuId;    
    } else {
      //set the new selected product ID in the refresh form and submit it
      form.elements.productId.value = productId;
      
      // When we are passed the gwpRadioId this indicates the colour/size swatch was clicked.
      // We dont need to submit the form as this will be taken care of by the calling method.
      if(!gwpRadioButton){
        var picker = atg.store.picker;
        picker.submitRefreshGiftForm(formId, productId);
      }
    }
  },
  
  /**
   * Called when a user selects gift SKU to add to cart. Changes the GiftWithPurchaseFormHandler
   * product ID, sku ID and sku type to the selected ones.
   * @param productId: which product is selected
   * @param skuId: which sku is selected
   * @param skuType: the type of the selected sku
   */
  clickGiftSku: function(productId, skuId, skuType, gwpRadioId){
	
    // When we are displaying a skus color/size picker we just
    // invoke this method directly and there will only be one size/color.
    if(gwpRadioId){
      var gwpRadioButton = dojo.byId(gwpRadioId);
      if(gwpRadioButton){
        gwpRadioButton.checked = true;
      }
    }
    var gwpForm = dojo.byId("gwpform");
    if(!gwpForm){
      console.debug('gwpform is not found');
      return;  
    }
          
    gwpForm.elements["/atg/commerce/promotion/GiftWithPurchaseFormHandler.productId"].value = productId;
    console.debug("Selected product ID is " + gwpForm.elements["/atg/commerce/promotion/GiftWithPurchaseFormHandler.productId"].value);
    
    gwpForm.elements["/atg/commerce/promotion/GiftWithPurchaseFormHandler.skuId"].value = skuId;
    console.debug("Selected sku ID is " + gwpForm.elements["/atg/commerce/promotion/GiftWithPurchaseFormHandler.skuId"].value);
    
    gwpForm.elements["/atg/commerce/promotion/GiftWithPurchaseFormHandler.skuType"].value = skuType;
    console.debug("The type of the selected sku is " + gwpForm.elements["/atg/commerce/promotion/GiftWithPurchaseFormHandler.skuType"].value);
   
  }, 
  
  /**
   * Called when a user clicks on 'Add to Cart' button
   */
  clickAddGWithPurchaseToCart: function(buttonNode, addingToCartText) {
    
    console.debug("Adding selected gift to cart");
    
    var gwpForm = dojo.byId("gwpform");
    
    if(!gwpForm){
      console.debug('gwpform is not found');
      return;  
    }
    
    // Disable Add to Cart button.    
    // Set values for width and height so element doesn't change size
    buttonNode.style.width=dojo._getBorderBox(buttonNode).w+"px";
    buttonNode.originalValue = buttonNode.value;    
    buttonNode.value = addingToCartText;
    buttonNode.disabled = true;
    
    var productId = gwpForm.elements["/atg/commerce/promotion/GiftWithPurchaseFormHandler.productId"].value; 
    if(!productId) {
      console.debug('product id is not specified');
      this.ajaxSubmit(dojo.byId("gwpform"), buttonNode);
      return false;  
    }
    
    /*
     * If there is colorsizerefreshform form for this product ID take
     * selected SKU ID and type values from the form 
     */    
    var giftFormId = "colorsizerefreshform_" + productId;
    var giftForm = dojo.byId(giftFormId);
    if (giftForm){
      var skuId = giftForm.elements.skuId.value;
      var skuType = giftForm.elements.skuType.value;
      gwpForm.elements["/atg/commerce/promotion/GiftWithPurchaseFormHandler.skuId"].value = skuId;
      gwpForm.elements["/atg/commerce/promotion/GiftWithPurchaseFormHandler.skuType"].value = skuType;
      
      /*
       * Initialize color/size attributes so we may choose
       * appropriate error message in case something is unselected
       */
      if(giftForm.elements.selectedColor) { 
        if (giftForm.elements.selectedColor.value != "") {
          gwpForm.elements["/atg/commerce/promotion/GiftWithPurchaseFormHandler.requiredSkuAttributes.color"].value = giftForm.elements.selectedColor.value;
        } 
        else {
          gwpForm.elements["/atg/commerce/promotion/GiftWithPurchaseFormHandler.requiredSkuAttributes.color"].value = "EMPTY";
          // Don't update buttonNode value as color hasn't been specified.
          console.debug('color is not specified'); 
        }
      }
      else {
        console.debug('color is not a selectable option');       
      }
      
      if(giftForm.elements.selectedSize) {
        if (giftForm.elements.selectedSize.value != "") {
          gwpForm.elements["/atg/commerce/promotion/GiftWithPurchaseFormHandler.requiredSkuAttributes.size"].value = giftForm.elements.selectedSize.value;
        } 
        else {
          gwpForm.elements["/atg/commerce/promotion/GiftWithPurchaseFormHandler.requiredSkuAttributes.size"].value = "EMPTY"; 
          // Don't update buttonNode value as size hasn't been specified.
          console.debug('size is not specified');
        }
      }
      else {
        console.debug('size is not a selectable option');
      }
    }
    
    console.debug("Adding [ " + skuId + " : " + productId + " ] as a gift to the shopping cart");
    
    // ajax submit the form
    this.ajaxSubmit(dojo.byId("gwpform"), buttonNode);
    return false;
    
  },
  
  
  ajaxSubmit: function(formNode, buttonNode){
    
    var bindParams={
      headers: { "Accept" : "application/json" },
      handleAs: "json",
      
      load: function(node, ioArgs) {
        atg.store.picker.handleJSON(node, buttonNode);
      },
      error: dojo.hitch(this, "handleError"),
      timeout: dojo.hitch(this, "handleError")
      
    };
    
    var content = {};
    // Create content object with the name/value pair of the submit button that's been clicked
    // dojo.xhrPost doesn't send the value of submit buttons when serializing the form as it 
    // doesn't know which one has been clicked. Server side FormHandlers need this data to
    // invoke the correct formHandler method.
    content[buttonNode.name] = buttonNode.value;
    
    // Setup the ajax params
    dojo._mixin(bindParams,{
      form: formNode,
      content: content
    });
    
    dojo.xhrPost(bindParams);
    
  },
  
  handleJSON: function(obj, buttonNode){
    console.debug("HANDLEJSON",obj);

    var isError = obj["error"];
    var isTimeout = obj["timeout"];
    
    if(isError != null && isError == 'true') {
  
      // clean previous error messages
      var errorContainer = dojo.byId("atg_store_pickerValidationError");
  
      var oldErrors = dojo.query("#atg_store_pickerValidationError div");
      if(oldErrors.length > 0) {
        for (var i = 0; i<oldErrors.length; i++){
          errorContainer.removeChild(oldErrors[i]);
         }                       
      }

      var errors = obj["errors"];
      for(var i=0; i<errors.length; i++) {
        var e = dojo.create("div", { innerHTML: "<p>" + errors[i] + "</p>" });
        dojo.addClass(e, "errorMessage");
        errorContainer.appendChild(e);
      }
      
      // enable "Add to Cart" button
      buttonNode.disabled = false;
      buttonNode.value = buttonNode.originalValue;
      
    } 
    else if(isTimeout != null && isTimeout == 'true') {
      // Load session timeout page.
      window.location.href = obj["timeoutUrl"];
    }
    else {
      window.location.reload(); 
    }
  },
  
  handleError: function(obj){
    console.debug("HANDLERROR",obj);
    // FIXME:
    // assuming HTML return is a success, so reload the page
    // probably needs to be fixed to work better
    window.location.reload();
  },

/**
 * Called when a user clicks on a wood finish, it changes selected wood finish
 * to the passed one
 * @param woodFinish: which wood finish is selected
 */
  clickWoodFinish: function(woodFinish){
    console.debug('selected wood finish is ' + woodFinish);
    var formId = "woodfinishrefreshform";
    var form = dojo.byId(formId);
    var currentWoodFinish = form.elements.selectedWoodFinish.value;

    //if the wood finish is not changing, don't do anything
    if(currentWoodFinish == woodFinish){
      return;
    }

    //set the new selected wood finish in the refresh form and submit it
    form.elements.selectedWoodFinish.value = woodFinish;
    var picker=atg.store.picker;
    picker.setQuantity(formId);
    picker.setGiftlistId(formId);
    picker.submitRefreshForm(formId);  
  },
/**
 * Gets the quantity from the addToCart form and sets the refresh form quantity.
 * We do this so we can preserve the quantity between refreshes.
 */
  setQuantity: function(formId)
  {
    var currentQuantity = dojo.query(".atg_store_numericInput")[0].value;
    var refreshform = dojo.byId(formId);
    refreshform.elements.savedquantity.value = currentQuantity;
  },
  
 
/**
 * Gets the gift list id from the addToGiftList form and sets the refreshform savedgiftlist
 * parameter.
 * We do this so we can preserve the gift list selection between refreshes
 */
  setGiftlistId: function(formId) {
    var addToGiftListForm = dojo.byId("addToGiftList");
    if(!addToGiftListForm){
      return;  
    }
    
    var currentGiftList = addToGiftListForm.elements["/atg/commerce/gifts/GiftlistFormHandler.giftlistId"].value;
    var refreshform = dojo.byId(formId);
    refreshform.elements.savedgiftlist.value = currentGiftList; 
  },
  
   
/**
 * Resets the color and size selected and submits the refresh form
 */
  resetPicker: function(formId){
    var form = dojo.byId(formId);
    //reset the new selected size and color in the refresh form and submit it
    form.elements.selectedSize.value = "";
    form.elements.selectedColor.value = "";

    var picker=atg.store.picker;
    picker.setQuantity(formId);
    picker.setGiftlistId(formId);
    picker.submitRefreshForm(formId);  
  },

/**
 * Submits the refresh form. 
 */  
  submitRefreshForm: function(selectedElement, formId){

    dojo.xhrGet({
    
      //url: "http://localhost:8080/store/browse/gadgets/pickerContents.jsp",
      load: function(data){
        
        var divColorPicker = dojo.byId("picker_contents");
        //data = data.replace(/<form\s*[^>]*>|<\/form>/g,"");
        divColorPicker.innerHTML = data;
        
        // temp fix
        // parent node implementation is buggy in IE7 & IE8
        // epecially if the DOM contains an empty <span />
        // for now check if parentNode is not null  before
        // attempting to access it
        if (null != selectedElement.parentNode)
        {
          container = selectedElement.parentNode.parentNode.className;
          container = dojo.query('.'+container)[0];
          element = dojo.query('.atg_store_pickerAttribute', container);
          element[0].focus();
        }
        
        
        var richCartWidget = dijit.byId('atg_store_richCart'); 
        
        if (richCartWidget.doHijack) {
          richCartWidget.hijackAllAddToCartNodes();
          var targetNode = dojo.query(".atg_store_numericInput")[0];
          atg.store.util.addNumericValidation(targetNode);
        }
        // just check that we don't need any popuplaunchers on the new code
        atg.store.util.setUpPopupEnhance();
        dojo.parser.parse(divColorPicker);
        
        dojo.query("*", divColorPicker).forEach(
          function(formElement){
            dojo.connect(formElement, "onkeypress", atg.store.util, "killEnter");  
        });
        
        },
        form:  formId
    });
       
  },

  /**
   * Submits the gift refresh form. 
   */  
    submitRefreshGiftForm: function(formId, productId){

      dojo.xhrGet({
      
        //url: "http://localhost:8080/store/browse/gadgets/pickerContents.jsp",
        load: function(data){
          var divColorPicker = dojo.byId("gift_contents_" + productId);
          //data = data.replace(/<form\s*[^>]*>|<\/form>/g,"");
          divColorPicker.innerHTML = data;
          
          // just check that we don't need any popuplaunchers on the new code
          atg.store.util.setUpPopupEnhance();
          dojo.parser.parse(divColorPicker);
          dojo.query("*", divColorPicker).forEach(
            function(formElement){
              dojo.connect(formElement, "onkeypress", atg.store.util, "killEnter");  
              });
        },
        form:  formId
      });
      
    },  
  
/**
 * Submits the addToFavorites form. 
 */  
  submitAddToFavoritesForm : function(){


    if(!this.checkGiftListSubmitAvailable("addToFavorites", "/atg/commerce/gifts/GiftlistFormHandler.catalogRefIds")){
     // display message that user should select product's color 
     // and size before adding it to favorites list
     dojo.byId('promptSelectDIV2').style.display='block';
     dojo.byId('promptSelectDIV').style.display='none';
     dojo.byId('promptSelectDIV3').style.display='none';
     
     // close the popup
     dojo.byId("atg_picker_moreActionsButton").className = "more";
     
     return;
    }
    //console.debug(dojo.byId("atg_store_addToFavorites"));
    //alert(dojo.byId("addToFavorites").nodeName);
    dojo.byId("atg_store_addToFavorites").click();
    // Nasty work around for an IE6 form submission bug that sometimes it just won't go the first time round.
    if(dojo.isIE && dojo.isIE < 7){
        setTimeout("atg.store.picker.submitAddToFavoritesForm()", 500);
    }
    
  },

/**
 * Submits the addToGiftList form. 
 */   
  submitGiftListForm : function(giftList){

    if(!this.checkGiftListSubmitAvailable("addToGiftList", 
                                           "/atg/commerce/gifts/GiftlistFormHandler.catalogRefIds")){
     // display message that user should select product's color 
     // and size before adding it to gift list
     dojo.byId('promptSelectDIV3').style.display='block';
     dojo.byId('promptSelectDIV').style.display='none';
     dojo.byId('promptSelectDIV2').style.display='none';
     
     
     return;
    }
    
    // set form's giftListId to the one that is clicked by user
    this.setGiftlistIdOnGiftListForm(giftList);
    // set quantity on gift list form
    this.setQuantityOnGiftlistForm();
    // submit form
    dojo.byId("atg_store_addToGiftSubmit").click();
  }, 
  
 
/**
 * Checks if  addToGiftList or addToFavorites form is ready for submit
 * @param giftListForm: form id
 * @param giftListRefIdElement: id of form's element that contains  SKU id 
 *                              that is going to be added    
 * @return true: if can be submitted
 *          false: if not 
 */
  checkGiftListSubmitAvailable : function(giftListForm, giftListRefIdElement ){
    var selectedSku = dojo.byId(giftListForm).elements[giftListRefIdElement].value;
    if(!selectedSku){
      return false;
    }    
    return true;
  },
  
 /**
  * Used to set quantity of items to add to gift list. Quantity value is taken
  * from addToCart form.
  */
  setQuantityOnGiftlistForm: function() {
    // get quantity from addToCart form
    var currentQuantity = dojo.byId("atg_store_quantityField").value;
    var addtogiftlistform = dojo.byId("addToGiftList");
    //set the quantity in the add to gift list form
    addtogiftlistform.elements.giftListAddQuantity.value = currentQuantity;
  },
  
/**
 * Used to set giftListId selected by user in addToGiftList from.
 */
  setGiftlistIdOnGiftListForm: function(giftListId) {
    var addToGiftListForm = dojo.byId("addToGiftList");
    if(!addToGiftListForm){
      return;  
    }
    
    addToGiftListForm.elements["/atg/commerce/gifts/GiftlistFormHandler.giftlistId"].value = giftListId;   
  }    
};
