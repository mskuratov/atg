/**
 *  Product View Widget
 *
 *  Created by Jon Sykes, Jan 2011 
 *
 *  Widget that individual rendering of a product item within a list
 *  includes:
 *    - HTML template
 *    - destruction via poductListing widget
 *    - Hooks for further rich interaction - quick view, add to cart, etc
 *
 */
dojo.provide("atg.store.widget.productView");


dojo.declare(
  "atg.store.widget.productView", 
  [dijit._Widget, dijit._Templated],
  {
    
    debugOn: false,
    //templateString: contextPath + "/javascript/widget/template/productView.html",

    templateString: '<li class="${previewClassString}"><a href="${productURL}"><span class="atg_store_productImage"><img src="${productImage}" alt="${productImageTitle}" /></span><span class="atg_store_productTitle">${productTitle}</span><span class="atg_store_productPrice">${productPrice}</span><span class="siteIndicator" dojoAttachPoint="productSiteIcon"></span></a></li>',
    
    data: [],
    pricePredicate: "was", // this string gets update via the JSON with the i18n equivilent
    /* Example of Data Structure Expected
    "name":"Cargo Pants",
    "url":"/crs/storeus/browse/productDetailColorSizePicker.jsp?productId=xprod2517",
    "imageUrl":"/crsdocroot/content/images/products/APP_CargoPants_listing.jpg",
    "price":{
        
        // Single Price:
        "price":{
          "singlePrice":"39.0"
         }

        // Sale Price
        "price":{
          "singlePrice":"39.0", 
          "oldSinglePrice":"44.0"
        }

        // Price Range:
        "price":{
          "priceRange":{
            "low":"39.0", 
            "high:"50.0"
          }
        }

        // Sale Price Range:
        "price":{
          "priceRange":{
            "low":"39.0", 
            "high:"50.0"
          }, 
          "oldPriceRange":{
            "low":"60.0", 
            "high":"70.0"
          }
        }
    }
    */
    
    postMixInProperties: function(){
      this._debug("Creating a Product View with this JSON: ", this.data);
      // Set the text in the template to the data passed to this widget on startup
      this.productURL = this.data.url;
      this.productImage = this.data.imageUrl;
      this.productImageTitle = this.data.name;
      this.productTitle = this.data.name;
      this.previewClassString = this.data.previewClassString;

      // does the product havae a discount applied
      if(this.data.price.oldSinglePrice || this.data.price.oldPriceRange){
        // What price should we be showing 2 possibilities
        var currentPrice = this.data.price.singlePrice || this.data.price.priceRange.high;
        var oldPrice = (this.data.price.range)? (this.data.price.oldPriceRange.low +" - "+ this.data.price.oldPriceRange.high) : this.data.price.oldSinglePrice;
        
        var price = currentPrice+"<span class='atg_store_oldPrice'> "+this.pricePredicate+" <del>"+oldPrice+"</del></span>";
        // Set template value for widget
        this.productPrice = price;
      }else{
        // Set template value for widget, 2 possibilities
        this.productPrice = this.data.price.singlePrice || (this.data.price.priceRange.low +" - "+ this.data.price.priceRange.high) || "Pricing Error";
      }
      
      // This is where you could add in animations or transitions
    },
    
    postCreate: function(){
      if(!this.data.site){
        dojo.destroy(this.productSiteIcon);
      }else{
        this.productSiteIcon.innerHTML = "From <span>"+this.data.site+"</span";
      }
    },
    
    destroy: function(){
      dojo.destroy(this.domNode);
    },
    
    
    _debug: function(debugTitle, toBeDebugged){
      if(this.debugOn){ 
        var title = debugTitle || "";
        var toBeDebugged = toBeDebugged || "";
        console.debug("----- "+this.id+" -----");     
        console.debug(title, toBeDebugged);     
      }
    },
    
    
    // don't edit below here
    noCommaNeeded:""
})