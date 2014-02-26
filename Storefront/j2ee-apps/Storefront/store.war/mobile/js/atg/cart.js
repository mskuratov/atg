CRSMA = window.CRSMA || {};

/**
 * @namespace "Shopping Cart" Javascript Module of "Commerce Reference Store Mobile Application"
 * @description Holds common functionality related to the shopping cart actions.
 */
CRSMA.cart = function() {
  /**
   * Map for existing bundles.
   * @private
   */
  var itemBlockBundlesMap = {};

  /**
   * Current bundle(viewBlock + editBlock)
   * @private
   */
  var currentBundle;

  /**
   * Returns object with required functions to show remove/share dialog.
   *
   * @param {string} showDialogName the dialog function name (one of the showRemoveDialog/showShareDialog).
   * @private
   */
  var getDialogDescriptor = function(showDialogName) {
    var dialogValue = ".moveDialog";
    var pointerValue = ".moveDialog .moveItems";
    if (showDialogName == "showShareDialog") {
      dialogValue = ".shareDialog";
      pointerValue = ".shareDialog .shareItems";
    }
    return {
      dialog: dialogValue,
      pointer: pointerValue
    }
  }

  /**
   * Shows the dialog for the specified commerce item.
   *
   * @param anchorObject HTML element to calculate dialog position.
   * @param e "click" event object.
   * @param {string} showDialogName dialog function name (one of the showRemoveDialog/showShareDialog).
   * @private
   */
  var showDialogImpl = function(anchorElement, e, showDialogName) {
    e.preventDefault();
    e.stopPropagation();

    var $anchor = $(anchorElement);
    var top = $anchor.offset().top;
    var parenttop = $("#pageContainer").offset().top;
    var right = $(document).width() - ($anchor.offset().left + $anchor.outerWidth());
    var descriptor = getDialogDescriptor(showDialogName);

    $(descriptor.pointer).css({top: top - parenttop, right: right, display: "block"});
    $(descriptor.dialog).show();
    CRSMA.global.toggleModal(true);
  };

  /**
   * Creates view/edit bundle object.
   *
   * @param $viewBlockParam jquery selector result.
   * @private
   */
  var createItemViewEditBundle = function($viewBlockParam) {
    var $viewBlock = $viewBlockParam;
    // Extend viewBlock to access "dataset" properties
    $.extend($viewBlock, {dataAttribute: CRSMA.global.dataAccessor});
    var $editBlock = $viewBlock.next("div.cartItemEdit");

    var $link2ProductDetail4Edit = $("a.productDetailPageLink", $editBlock);
    var ciId = $viewBlock.attr("id").substring("cartItem_".length);

    // SKU properties (concatenate prop1 + ", " + prop2)
    var propertiesHtml = $viewBlock.dataAttribute("skuproperty1");
    var property2 = $viewBlock.dataAttribute("skuproperty2");
    if (propertiesHtml.length > 0 && property2.length > 0) {
      propertiesHtml = propertiesHtml + ", ";
    }
    propertiesHtml = propertiesHtml + property2;
    $("span.properties", $editBlock).html(propertiesHtml);

    // Link to product detail page - for "Edit"
    $link2ProductDetail4Edit.attr("href", $viewBlock.dataAttribute("productpageurl4edit"));

    // Link to product detail page - for "Share"
    var $link2Share = $("#shareLink");
    $link2Share.attr("href", $viewBlock.dataAttribute("productpageurl4share"));

    // "onclick" handler
    $editBlock.click(function() {
      viewEditBundle.toViewMode();
    });

    $("a.moveLink", $editBlock).click(function(e) {
      showDialogImpl(this, e, "showRemoveDialog");
    });
    $("a.shareLink", $editBlock).click(function(e) {
      showDialogImpl(this, e, "showShareDialog");
    });

    // Disable links inside of the item view block
    $("a", $viewBlock).click(function(e) {
      e.preventDefault();
    });

    /**
     * Object for using in edit, view operations.
     *
     * @private
     */
    var viewEditBundle = {
      cartItemId: ciId,
      toViewMode: function() {
        $editBlock.hide();
        $viewBlock.show();
        currentBundle = null;
      },
      toEditMode: function() {
        $viewBlock.hide();
        $editBlock.show();
        if (currentBundle) {
          currentBundle.toViewMode();
        }
        currentBundle = this;
      }
    }

    // Add "viewEditBundle" to map
    itemBlockBundlesMap[ciId] = viewEditBundle;
    return viewEditBundle;
  };

  /**
   * Returns bundle: viewBlock + editBlock.
   *
   * @param $viewBlockParam jQuery selector result.
   * @return html element, presenting viewBlock & editBlock.
   * @private
   */
  var getOrCreateItemViewEditBundle = function($viewBlockParam) {
    var ciId = $viewBlockParam.attr("id").substring("cartItem_".length);
    var viewEditBundle = itemBlockBundlesMap[ciId];
    if (viewEditBundle === undefined) {
      viewEditBundle = createItemViewEditBundle($viewBlockParam);
    }
    return viewEditBundle;
  };

  /**
   * Switches cart item to edit mode.
   *
   * @param pBlock HTML element, presenting cart item.
   * @public
   */
  var showCartItemEditBox = function(pBlock) {
    var $viewBlock = $(pBlock);
    var bundle = getOrCreateItemViewEditBundle($viewBlock);
    bundle.toEditMode();
  };

  /**
   * Sends promotion coupon code to server.
   * @public
   */
  var applyCoupon = function() {
    var $couponCode = $("#promotionCodeInput");
    $couponCode.closest("form").submit();
  };

  /**
   * Apply coupon if user presses <i>Enter</i> on the Coupon field and the
   * coupon value has been changed.
   * @public
   */
  var applyNewCouponOnEnter = function() {
    if (event.keyCode == "13") {
      var enteredCouponCode = $("#promotionCodeInput").attr("value");
      var previousCouponCode = $("#promotionPreviousCode").attr("value");
      if (enteredCouponCode != previousCouponCode) {
        CRSMA.cart.applyCoupon();
      }
    }
  };

  /**
   * Performs "Removes current item from the Shopping Cart" action.
   * @public
   */
  var removeCurrentCartItem = function() {
    $("#removeItemFromOrder").attr("value", currentBundle.cartItemId);
    var formCart = $(document.forms["cartContent"]);
    history.replaceState(new Date());
    formCart.submit();
  };

  /**
   * List of public "CRSMA.cart".
   */
  return {
    // Methods
    "applyCoupon"                        : applyCoupon,
    "applyNewCouponOnEnter"              : applyNewCouponOnEnter,
    "removeCurrentCartItem"              : removeCurrentCartItem,
    "showCartItemEditBox"                : showCartItemEditBox,
  };
}(); // END CRSMA.cart
