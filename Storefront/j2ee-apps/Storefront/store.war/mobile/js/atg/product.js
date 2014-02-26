/**
 * Product Javascript functions.
 * @ignore
 */
CRSMA = window.CRSMA || {};

/**
 * @namespace "Product" Javascript Module of "Commerce Reference Store Mobile Application"
 * @description Holds functionality related to product detail pages, like <br>
 * adding product to the cart, updating it there etc.
 * 
 */
CRSMA.product = function() {
  var $addToCartForm;
  var isUpdateCart;
  var $updateCartForm;
  var $skuIdField;
  var $actionsContainer;
  var $addToCartButton;
  var $buttonText;

  var $emailMeContainer;
  var $emailMeConfirm;
  var $emailMeForm;
  var $emailMeSkuField;
  var $emailMeAddress;
  var $emailMeAddressRow;
  var $rememberCheckbox;
  var $rememberInput;

  var $priceContainer;
  var initialPriceDisplay;

  /**
   * Map of pairs (skuid, skuFields) or something like (color:size, skufields)
   * for page with several pickers on the page.
   * @private
   */
  var productSKUs = {};

  /**
   * Array of pickers types, used on a page (f.e. color, size)
   * @private
   */
  var pickerTypes = [];

  /**
   * "true" means the "actionHandler" should display Shopping Cart page.
   * @private
   */
  var goToViewCart = false;

  /**
   * Object, containing currently selected SKU properties.
   * @private
   */
  var selectedSku = null;

  /**
   * Mobile site-specific context path, which also includes "/mobile/" suffix at the end.
   * @private
   */
  var productionURL;

  /** 
   * Initializes CRSMA.product context variables.<br/>
   * Should be used on DOM ready.
   *
   * @param {string} pProductionURL - The site production base URL ("/mobile/" path suffix is also included).
   * @public
   */
  var init = function(pProductionURL) {
    productionURL = pProductionURL;

    $addToCartForm = $("#addToCartForm");
    isUpdateCart = ($addToCartForm.length == 0);
    // See mobile/browse/productDetail.jsp — the update cart form will only be present
    // when the add to cart form is not
    $updateCartForm = $("#updateCart");
    $skuIdField = $("input#addToCart_skuId");
    $actionsContainer = $("#addToCartButton");
    $addToCartButton = $("button", $actionsContainer);
    $buttonText = $("span#buttonText", $actionsContainer);

    $emailMeContainer = $("#emailMePopup");
    $emailMeConfirm =$("#emailMeConfirm");
    $emailMeForm = $("form", $emailMeContainer);
    $emailMeSkuField = $("#emailMeSkuId", $emailMeForm);
    $emailMeAddress = $("#rememberMeEmailAddress", $emailMeForm);
    $emailMeAddressRow = $("#emailAddressRow", $emailMeForm);
    $rememberCheckbox = $("#rememberCheckbox", $emailMeForm)[0];
    $rememberInput = $("input[name='rememberEmail']", $emailMeForm);

    $priceContainer = $("#pickerPrice");
    initialPriceDisplay = $priceContainer.html();
  }

  /**
   * Updates product and sale prices.
   *
   * @param {string} salePrice sale price.
   * @param {string} oldPrice product price.
   * @private
   */
   var updatePrice = function(salePrice, oldPrice) {
    var price = "<p><strong>" + salePrice + "</strong>";
    if (oldPrice) {
      price = price + "<br>" + CRSMA.i18n.getMessage("mobile.js.price.old") + "&nbsp;<span>" + oldPrice + "</span>";
    }
    price = price + "</p>";
    $priceContainer.html(price);
  };

  /**
   * This function is called asynchronously on callback after product SKU is added to the cart.<br/>
   * On success it changes cart handler for viewing the cart and it's label, increases cart badge counter.<br/>
   * On failure logs error in console.
   *
   * @param responseData JSON response, contains new cart item count and form errors (if any).
   * @private
   */
  var addToCartCallback = function(responseData) {
    var error = responseData.addToCartError;
    if (error) {
      console.log(error);
    } else {
      goToViewCart = true;
      $addToCartButton.text(CRSMA.i18n.getMessage("mobile.js.navigation_shoppingCart.viewCart"));
      CRSMA.global.refreshCartBadge(responseData.cartItemCount);
    }
  };

  /**
   * Product action handler.<br/><br/>
   *
   * According to the parameters:
   *  - Navigates to Shopping Cart page (displays "Shopping Cart")
   *  - Adds product to the cart
   *  - Updates product in the cart
   *  - Shows pop-up with warning text about product unavailability.
   * @param event Event object
   * @public
   */
  var actionHandler = function(event) {
    event.stopPropagation();

    // Non-empty viewCart url means, that product was successfully added to the cart 
    // and user hasn't changed anything on the page since that time
    if (goToViewCart) {
      window.location.replace(productionURL + "cart/cart.jsp");
      return;
    }

    // Usually "selectedSku" is filled when pickers or quantity are changed.
    // But if user immediately clicks "Add to cart", "selectedSku" may be not initialized
    selectedSku = getSelectedSku();
      
    // If product is "out of stock", we don't add it to the cart.
    // Show popup with information for customer, that product is absent and offers
    // email providing if user wants to receive notification when product will be in stock
    if (selectedSku.status == "outofstock") {
      $emailMeAddressRow.toggleClass("errorState", false); // clear previous error state, if any
      $emailMeContainer.show();
      $emailMeConfirm.hide();
      CRSMA.global.toggleModal(true);
      return;
    }

    // We there if the product has one of the following statuses (available, backorder, preorder)
    // and it could be added to the cart or updated
    if (isUpdateCart) {
      // Update product in the cart
      $updateCartForm.submit(); // when updating the cart, just submit the form normally
    } else {
      // Add product to the cart
      $.post(
        $addToCartForm.attr("action"),
        $addToCartForm.serialize(),
        addToCartCallback
      );
    }
  }

  /**
   * Updates product price, sale price, label on a button, which adds product to cart,
   * product status for preorderable, backorderable, outofstock products and button handler.<br/>
   * May be used only when user completes selection of SKU.
   *
   * @param {string} buttonLabel label like "Add to Cart", "Update Cart" etc. 
   * @param {string} buttonText sku status, for example, "Available Soon".
   * @param {Object} skuFields SKU-Object.
   * @private
   */
  var refreshProductInfo = function(buttonLabel, buttonText, skuFields) {
    updatePrice(skuFields.productPrice, skuFields.salePrice);

    if (skuFields.status == "outofstock") {
      $emailMeSkuField.val(skuFields.skuId);
    } else {
      $skuIdField.val(skuFields.skuId);
    }

    $addToCartButton.text(buttonLabel).removeAttr("disabled");

    $buttonText.text(buttonText);
  };

  /**
   * Makes submit from popup, appearing when user tries to add "unavailable" product to the cart.<br/>
   * On success, closes popup.<br/>
   * On failure, shows errors inside.
   *
   * @param event Event object
   * @public
   */
  var emailMeSubmit = function(event) {
    event.preventDefault();
    if ($rememberCheckbox.checked) {
      $rememberInput.val($emailMeAddress.val());
    }
    $.post(
      $emailMeForm.attr("action"),
      $emailMeForm.serialize(),
      function(errors) {
        if (errors.length > 0) {
          $emailMeAddressRow.toggleClass("errorState", true);
        } else {
          $emailMeAddressRow.toggleClass("errorState", false);
          $emailMeContainer.hide();
          $emailMeConfirm.show();
        }
      },
      "json"
    );
  }

  /**
   * Returns currently selected SKU or null if some pickers aren't specified.<br/>
   * Note, that for single sku page it always returns not-null object.
   *
   * @return JSON-object with the SKU fields or null.
   * @private
   */
  var getSelectedSku = function() {
    var sku;
    if (pickerTypes.length > 0) {
      // Product with pickers
      var key = "";
      for (var i = 0; i < pickerTypes.length; i++) {
        var prop = pickerTypes[i];
        var $input = $("select[name='" + prop + "']");
        if ($input.length > 0) {
          key += $input.val() + ":";
        }
      }
      sku = productSKUs[key.slice(0, -1)]; // remove the trailing colon
    } else {
      // Single SKU page
      for (var key in productSKUs) {
        sku = productSKUs[key]; // Get first one
        break;
      }
    }
    return sku;
  }

  /**
   * Gets executed every time a picker value (or sometimes quantity) is changed to
   * determine which SKU has been selected, if any.<br/>
   * The function will search for "select" elements on the page with corresponding names.<br/>
   * NOTE, that order is important for constructing the key!
   *
   * @private
   */
  var checkForSelectedSku = function() {
    var sku = getSelectedSku();
    if (sku) { // If this is not a valid SKU selection, sku will be null
      onSkuSelect(sku);
    } else {
      // Disable the button if this is not a valid selection
      $addToCartButton.attr("disabled", "disabled");
    }
  }

  /**
   * It's called when user completes selection of SKU.<br/>
   * Refreshes SKU details info on a page according to the selected SKU status,
   * prepares cart button for future submit.
   *
   * @param sku SKU fields object
   * @private
   */
  var onSkuSelect = function(sku) {
    selectedSku = sku;

    switch (sku.status) {
    case "available":
      refreshProductInfo(
          CRSMA.i18n.getMessage(isUpdateCart ? "mobile.js.productDetails.updatecart" : "mobile.js.common.button.addToCartText"),
          "", sku);
      break;

    case "preorder":
      refreshProductInfo(
          CRSMA.i18n.getMessage(isUpdateCart ? "mobile.js.productDetails.updatecart" : "mobile.js.button.preorderLabel"),
          CRSMA.i18n.getMessage("mobile.js.button.preorderText"), sku);
      break;

    case "backorder":
      refreshProductInfo(
          CRSMA.i18n.getMessage(isUpdateCart ? "mobile.js.productDetails.updatecart" : "mobile.js.common.button.addToCartText"),
          CRSMA.i18n.getMessage("mobile.js.button.backorderText"), sku);
      break;

    case "outofstock":
      refreshProductInfo(
          CRSMA.i18n.getMessage("mobile.js.common.temporarilyOutOfStock"),
          CRSMA.i18n.getMessage("mobile.js.button.emailMeText"), sku);
      break;
    }
  }

  /**
   * Called when a picker value has changed. 
   *
   * @param event Event object.
   * @public
   */
  var pickerSelect = function(event) {
    var $this = $(event.currentTarget);
    // We need to set "title" attribute for select to make VoiceOver read it as "Name - Value"
    var defaultText = $("option:disabled", $this).attr("label");
    var noValue = ($this.val() == "");
    if (noValue) {
      // Revert to initial display if there is no valid selection
      $priceContainer.html(initialPriceDisplay);
    }
    // We need to set the appropriate title for the current selection
    $this.attr("title", noValue ? "" : defaultText);
    // If no value has been selected, toggle "selected" off. Otherwise, toggle it on
    $(event.currentTarget).toggleClass("selected", !noValue);
    checkForSelectedSku(); // check if this is a complete SKU selection
    goToViewCart = false;
  }

  /**
   * Called when the quantity picker value has been changed.
   * @public
   */
  var quantitySelect = function(event) {
    var $qtyField = $("#addToCart_qty");
    var selectedQty = $("select[name='qty']").val();
    $qtyField.val(selectedQty);
    checkForSelectedSku();
    goToViewCart = false;
  }

  /**
   * This function toggles product view between normal and enlarged.
   * @public
   */
  var toggleProductView = function() {
    var detailsContainer = $(".itemPickers");
    detailsContainer.toggleClass("productEnlarged");
  }

  /**
   * Expand or collapse pickers according to the specified boolean flag.
   *
   * @param {boolean} pShowOrHide indicates show or hide should be used.
   * @public
   */
  var expandPickers = function(pShowOrHide) {
    $("li.itemPickers").toggleClass("expanded", pShowOrHide);
  }

  /**
   * Parses JSON-array and initializes the productSKUs-map.<br/><br/> 
   *
   * Every item in JSON-array is the object with properties:
   * <ul>
   *   <li>key - unique key, identifying sku, f.e. "Blue:L" or "xsku2051"</li>
   *   <li>value - map of properties:
   *     <ul>
   *       <li>salePrice    - sale price</li>
   *       <li>productPrice - product price</li>
   *       <li>skuid        - sku repository id</li>
   *       <li>status       - one from (available, preorder, backorder, outofstock)</li>
   *     </ul>
   *   </li>
   * </ul>
   * @param skus JSON array with the SKU key and value.
   * @public
   */
  var registerSKUs = function(skus) {
    for (var i = 0; i < skus.length; i++) {
      var sku = skus[i];
      productSKUs[sku.key] = sku.value;
    }
  }

  /**
   * Adds picker type to the list.
   *
   * @param {string} type picker type, f.e. "color", "size" etc.
   * @public
   */
  var registerPickerType = function(type) {
    pickerTypes.push(type);
  }

  /**
   * List of public "CRSMA.product"
   */
  return {
    // Methods
    "actionHandler"     : actionHandler,
    "emailMeSubmit"     : emailMeSubmit,
    "expandPickers"     : expandPickers,
    "init"              : init,
    "pickerSelect"      : pickerSelect,
    "quantitySelect"    : quantitySelect,
    "registerSKUs"      : registerSKUs,
    "registerPickerType": registerPickerType,
    "toggleProductView" : toggleProductView
  }
}();
