/*
 * Register custom renderer
 */
ATGSvcs.renderer("crs", function (slot_element){
  var recommendationsDIV = null;
  var ulElement = null;
  // check whether we are dealing with category page
  if (ATGSvcs.cfg('-category_page', slot_element.id)){
    recommendationsDIV = ATGSvcs.dom.DIV({id: 'atg_store_prodList'});
    ulElement = ATGSvcs.dom.UL({id: 'atg_store_product', Class: 'atg_store_product'});
  }else{
    recommendationsDIV = ATGSvcs.dom.DIV({id: 'atg_store_recommendedProducts'});
    ulElement = ATGSvcs.dom.UL({}); 
  }
  
  // Check if there is headerText to display, if so display it
  if (this.headerText) {
    var header = ATGSvcs.dom.H3({},this.headerText);
    recommendationsDIV.appendChild(header);
  }    
    
  // add to eahc recommendations data its index and the number of recommendations returned
  var length = this.recs.length;
  for(var i = 0; i < length; i++) {
    var rec = this.recs[i];
    rec.index = i;
    rec.length = length;
    //call the builder for current recommendation data and appedn the result to the
    // parent DOM element.
    ulElement.appendChild(ATGSvcs.build_rec(slot_element.id, rec));
  }
  
  recommendationsDIV.appendChild (ulElement);
  
  // append built recommendations DOM to the slot
  slot_element.appendChild(recommendationsDIV);

});


/*
 * Register category page Recommendation Builder
 */
ATGSvcs.rec_builder("Category_Recommendation_Builder", 
                    function (slot_name, rec_data) {
  //get store ID
  var storeId = ATGSvcs.cfg('view/storeId');
    
  // get localized display name for the recommendation
  var displayName = getLocalizedDisplayName(slot_name, rec_data);
  
  // get product link
  var link = getProductLink(slot_name, rec_data, storeId);
  
  //build price element
  var listPriceElement = getPriceElement (slot_name, rec_data, storeId);
   
  // create a new LI element for the recommendation
  var recommendation = 
    ATGSvcs.dom.LI({id: ATGSvcs.rec_id(slot_name, rec_data.productId), Class: "cs-rec"+getAdditionalClasses(rec_data)}, 
      ATGSvcs.dom.A({href: link},
        ATGSvcs.dom.SPAN({Class: 'atg_store_productImage'}, 
          ATGSvcs.dom.IMG({src: rec_data.image, alt: displayName})
        ),
        ATGSvcs.dom.SPAN({Class: 'atg_store_productTitle'}, displayName),
        listPriceElement
      )
    );

  // return the recommendation DOM
  return recommendation;
});

/*
 * Register product detail page Recommendation Builder
 */
ATGSvcs.rec_builder("Product_Recommendation_Builder", 
                    function (slot_name, rec_data) {
  //get store ID
  var storeId = ATGSvcs.cfg('view/storeId');
      
  // get localized display name for the recommendation
  var displayName = getLocalizedDisplayName(slot_name, rec_data);
  
  // get product link
  var link = getProductLink(slot_name, rec_data, storeId);
  
  //build price element  
  var siteIndicatorElement = getSiteIndicatorElement (slot_name, rec_data, storeId);  
     
  // create a new LI element for the recommendation
  var recommendation = 
    ATGSvcs.dom.LI({id: ATGSvcs.rec_id(slot_name, rec_data.productId), Class: "cs-rec"+getAdditionalClasses(rec_data)}, 
      ATGSvcs.dom.A({href: link, title: displayName},
        ATGSvcs.dom.IMG({src: rec_data.thumb_image_link, title: displayName, alt: displayName}),
        siteIndicatorElement
      )
    );

  // return the recommendation DOM
  return recommendation;
});

/**
 * Determines additional CSS classes that should be added to the recommendation
 * top level element based on the current recommendation index.
 */
getAdditionalClasses = function(rec_data){
  var additionalClasses = ''
  
  if (rec_data.index % 2 == 0){
    additionalClasses = additionalClasses+' odd';
  }else{
    additionalClasses = additionalClasses+' even';
  }
  if (rec_data.index == 0){
    additionalClasses = additionalClasses+' first';
  }
  if (rec_data.index == rec_data.length-1){
    additionalClasses = additionalClasses+' last';
  }
  return additionalClasses;
}

/**
 * Returns localized display name for the recommendation.
 */
getLocalizedDisplayName = function(slot_name, rec_data){
  // first get default display name
  var displayName = rec_data.name;
  var language = ATGSvcs.cfg('-language', slot_name);
  
  // check whether there is localized display name for the specified language
  if (rec_data['title_'+language] != null){
    displayName = rec_data['title_'+language];
  }
  return displayName;
};

/**
 * Get site-specific product link.
 */
getProductLink = function(slot_name, rec_data, storeId){
  // get relative URL from the recommendation data
  var link = rec_data.url;
  
  // determine the product's store ID
  var productStoreId = getProductStoreId (slot_name, rec_data, storeId);
  
  // get base URL from the configuration parameters and add it to the 
  // product's relative URL
  var baseUrl = ATGSvcs.cfg('-baseURL_'+productStoreId, slot_name);
    
  if (baseUrl != null){
	  
    // Retrieve query parameters from base URL
    baseURLQueryParams = baseUrl.split('?')[1];
    baseUrl = baseUrl.split('?')[0];
    
    // remove the last slash from base URL and add the product's relative link to it
    link = baseUrl.substring(0,baseUrl.length-1)+link;
    
    // add base URL query params
    if(baseURLQueryParams != null){
      if(link.indexOf('?') == -1){
        link = link + '?';
      }else{
    	  link = link + '&';
      }
      
      link = link + baseURLQueryParams;
      
    }
  }
    
  return link
};

/**
 * Determine product's store ID
 */
getProductStoreId = function(slot_name, rec_data, currentStoreId){
  // get store IDs on which product is available
  var availableStores = rec_data.availableStores;
  
  // get the list of alternative store IDs in the order of preference
  var alternativeStoreIds = ATGSvcs.cfg('-alternativeStoreIds',slot_name);
  
  // if product is available on the current store then return the current store ID
  // otherwise look through the list of alternativeStoreIDs  
  var productStoreId = currentStoreId;
  if (availableStores != null && dojo.indexOf(availableStores, currentStoreId) == -1 )  {
    if (alternativeStoreIds != null)  {
      for (var i=0; i<alternativeStoreIds.length; i++){
        var alternativeStoreId=alternativeStoreIds[i];      
        if (dojo.indexOf(availableStores, alternativeStoreId) >= 0){
          return alternativeStoreId;
        }
      }
    }
  }
  
  return productStoreId;
}

/**
 * Build site indicator element if the product is not available on the
 * current store otherwise return empty string.
 */
getSiteIndicatorElement = function(slot_name, rec_data, storeId){
  var siteDisplayName = null;
  var siteIndicatorElement = '';
  
  // determine best matching product's store ID.
  var productStoreId = getProductStoreId (slot_name, rec_data, storeId);  
  
  // if product's store ID is not the current store build site indicator
  // element
  if (productStoreId != storeId){
    var siteDisplayName = ATGSvcs.cfg('-'+productStoreId+'_name',slot_name);
    var siteIndicatorText = ATGSvcs.cfg('-siteIndicatorText', slot_name);
    
    siteIndicatorElement = ATGSvcs.dom.SPAN({Class: 'siteIndicator'}, siteIndicatorText,
                             ATGSvcs.dom.SPAN({}, ' '+siteDisplayName)
                           );
  }
  return siteIndicatorElement;
};

/**
 * Build price element for the recommendation. If product is on sale
 * both sale and list prices are included.
 */
getPriceElement = function(slot_name, rec_data, storeId){
  var lowestPrice = rec_data.price;
  var highestPrice = rec_data['highestPrice_'+storeId]
  var lowestListPrice = rec_data['listPrice_'+storeId];
  var highestListPrice = rec_data['highestListPrice_'+storeId];
  
  // Build sale and list price ranges
  var priceRange = ATGSvcs.price(slot_name, lowestPrice);
  if (highestPrice != null){
    priceRange =  priceRange + ' - ' + ATGSvcs.price(slot_name, highestPrice);
  }
  
  // List price range
  var listPriceRange = null;
  if (lowestListPrice != null){
    listPriceRange = ATGSvcs.price(slot_name, lowestListPrice);
    if (highestListPrice != null){
      listPriceRange =  listPriceRange + ' - ' + ATGSvcs.price(slot_name, highestListPrice);
    }
  }
  
  var oldPriceText = ATGSvcs.cfg('-oldPriceText', slot_name);
  
  //Build price element
  var priceElement = null;
  if (listPriceRange != null){
    priceElement = 
        ATGSvcs.dom.SPAN({Class: 'atg_store_productPrice'}, 
          priceRange,      
          ATGSvcs.dom.SPAN({Class: 'atg_store_oldPrice'}, 
            " ", oldPriceText, " ",
            ATGSvcs.dom.DEL({}, listPriceRange)
          )          
        );  
  }else{
    priceElement = 
        ATGSvcs.dom.SPAN({Class: 'atg_store_productPrice'}, priceRange); 
  }
  return priceElement;
};

/**
 * Register DE locale and specify its price format.
 */
ATGSvcs.l10n.register("DE", function () {
  this.CUR_SYM = "\u20ac";
  this.currency_string = function(triplet_array, decimal_string) {
    return triplet_array.join(".") + "," + decimal_string + " " + this.CUR_SYM;
  };
});

/**
 * Add H3 and DEL tag support.
 */
ATGSvcs.dom.tags("H3|DEL");