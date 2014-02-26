/**
 * Global Javascript functions.
 * @ignore
 */
CRSMA = window.CRSMA || {};

/**
 * @namespace "Global" Javascript Module of "Commerce Reference Store Mobile Application"
 * @description Holds common functionality related to all parts in CRSMA.
 */
CRSMA.global = function() {
  /**
   * Array of possible search bar states.
   * @private
   */
  var states = [];

  /**
   * Zero-based index of active state.
   * @private
   */
  var activeStateIndex;

  /**
   * Links to jquery selector results for perfomance.
   * @private
   */
  var $blockTitle, $blockNavigation;

  /**
   * Indicates if popup with error message should appear when ajax error is happened.
   * @private
   */
  var SHOW_AJAX_ERROR = true;

  /**
   * "No search results" boolean indicator.
   * @private
   */
  var noSearchResults;

  /**
   * Chain together multiple animations.<br>
   * Usage: chainAnimations([function, duration],...);
   * @private
   */
  var chainAnimations = function() {
    var args = Array.prototype.slice.call(arguments);
    var f = args.shift();
    var delay = args.shift();
    var tail = args;
    var callee = arguments.callee;
    if (f) {
      f();
      if (delay) {
        setTimeout(function() {callee.apply(this, tail);}, delay);
      }
    }
  };

  /**
   * Refreshes badge count on shopping cart.
   *
   * @param {number} pNewCount - New count.
   * @see {@link CRSMA.global.chainAnimations}
   * @public
   */
  var refreshCartBadge = function(pNewCount) {
    if (pNewCount > 0) {
      var $cartBadge = $("#cartBadge");
      $cartBadge.show(); // in case it's currently hidden
      chainAnimations(
        function(){$cartBadge.toggleClass("highlight").toggleClass("textFade");}, 500,
        function(){$cartBadge.text(pNewCount).toggleClass("textFade");}, 900,
        function(){$cartBadge.toggleClass("highlight")}
      );
    }
    saveBadgeData(pNewCount);
  };

  /**
   * Saves count of items in cart and timestamp for the history.back() handling.
   * @public
   */
  var saveBadgeData = function(pNewCount) {
    localStorage.setObject("badgeCount", pNewCount);
    localStorage.setObject("badgeTs", new Date());
  }

  /**
   * Takes count of items in cart and call saveBadgeData()
   * @public
   */
  var saveCartItems = function() {
    var currentBadgeData = $("#cartBadge")[0].innerText;
    saveBadgeData(parseInt(currentBadgeData));
  }

  /**
   * Checks if cart items count or cart content changed
   * and refreshes page if necessary.
   * @public
   */
  var refreshCartDataIfExpired = function(renderTs) {
    if (renderTs) {
      var currentBadgeData = $("#cartBadge")[0].innerText;
      var lsBadgeData = localStorage.getObject("badgeCount");
      var lsBadgeTs = new Date(localStorage.getObject("badgeTs"));

      if (renderTs < lsBadgeTs) {
        var isCartPageOpened = !!$(".cartContainer").length;
        if (isCartPageOpened) {
          location.reload();
        } else {
          refreshCartBadge(lsBadgeData);
        }
      }
    }
  }

  /**
   * Toggle rows (li's) on login/registration pages.
   *
   * @param {string} li ID of &lt;li&gt; html element.
   * @return {boolean} false always.
   * @private
   */
  var loginPageToggle = function(li) {
    var $li = $("#" + li);
    $li.prev("li").toggleClass("turn");
    $li.toggleClass("hidden");
    return false;
  };

  /**
   * Used to toggle rows on the login screen.
   *
   * @param {string} li li ID.
   * @returns {boolean} false in all cases.
   * @see {@link CRSMA.global-loginPageToggle}
   * @public
   */
  var loginPageClick = function(li) {
    if (li === "loginRow") {
      var $regRow = $("#regRow");
      if (!$regRow.hasClass("hidden")) {
        loginPageToggle("regRow");
      }
    } else {
      var $loginRow = $("#loginRow");
      if (!$loginRow.hasClass("hidden")) {
        loginPageToggle("loginRow");
      }
    }
    loginPageToggle(li);

    return false;
  };

  /**
   * Returns cookie value by it's name if it exists or null otherwise.
   *
   * @param {string} pName cookie name.
   * @return {string} cookie value.
   * @public
   */
  var getCookieByName = function(pName) {
    var cookies = document.cookie.split(";");
    for (var i = 0; i < cookies.length; i++) {
      var name = cookies[i].substr(0, cookies[i].indexOf("="));
      var value = cookies[i].substr(cookies[i].indexOf("=") + 1);
      name = name.replace(/^\s+|\s+$/g, "");
      if (name == pName) {
        return unescape(value);
      }
    }
    return null;
  };

  /**
   * Shows or hides html element with 'modalOverlay' id.
   *
   * @param {boolean} showOrHide flag, indicating show (true) or hide (false) should be used.
   * @public
   */
  var toggleModal = function(showOrHide) {
    $("#modalOverlay").toggle(showOrHide);
    // If hiding the modal overlay, also hide all direct children
    if (showOrHide == false) {
      $("#modalOverlay > :not(.shadow)").hide();
    }
  };

  /**
   * Shows 'Contact Us' page.
   *
   * @return {boolean} false in all cases.
   * @public
   */
  var showContactUsPage = function() {
    var $contactInfo = $("#contactInfo").show();
    CRSMA.global.toggleModal(true);
    return false;
  };

  /**
   * Hides popup.
   * @public
   */
  var hidePopup = function() {
    $("#messagePopup").addClass("hidden");
  };

  /**
   * Shows specified message in pop-up in some style (f.e. 'error').
   *
   * @param {string} type message type, f.e. 'error'.
   * @param {string} message message text.
   * @public
   */
  var showPopup = function(type, message) {
    $("#messagePopup").addClass("shown").addClass(type);
    $("#messageText").text(message);
  };

  /**
   * Event Manager object.
   *
   * @namespace EventManager
   * @public
   */
  var EventManager = {
    /**
     * Attaches handler to the specified event in the context of EventManager.
     * @param {string} event name.
     * @param fn function.
     * @public
     */
    subscribe: function(event, fn) {
      $(this).bind(event, fn);
    },
    /**
     * Executes handler, binded previously to the specified event with parameters.
     * @param {string} event name.
     * @param params parameters to pass along to the event handler.
     * @public
     */
    publish: function(event, params) {
      $(this).trigger(event, params);
    }
  };
  
  /**
   * Boolean, indicating if "dataset" is supported by browser?
   * @private
   */
  var isDatasetSupported = document.createElement("div").dataset !== undefined;

  /**
   * Returns value of specified attribute in dataset[] if it's supported
   * or through getAttribute call if 'dataset' isn't supported.<br>
   * Note, that iOS 4.3.5 still doesn't support data-operations from HTML5.
   *
   * @param {string} pName data-property name to access.
   * @returns {string} data value.
   * @public
   */
  var dataAccessor = function(pName) {
    if (isDatasetSupported) {
      return this[0].dataset[pName];
    }
    return this.attr("data-" + pName);
  };


  /**
   * For every element, catched by selector, adds click handler
   * that makes submit of form after some delay.<br>
   * Called when user clicks on any list item, except border & action ones.
   *
   * @param {string} selectorText jquery selector rule.
   * @param {number} pDelay Timeout in msec before submitting form (default - 200ms).
   * @public
   */
  var delayedSubmitSetup = function(selectorText, pDelay) {
    var pDelay = (typeof pDelay === "undefined") ? 200 : pDelay;

    $(selectorText).each(function() {
      var $container = $(this);

      $("li", $container).not("#newItemLI").click(function(event) {
        var target = event.target || event.srcElement;
        if (target.localName === "a") {
          return;
        }

        var $radio = $("input:radio", $(this));
        $radio.attr("checked", "checked");
        $radio.change();

        setTimeout(function() {
          $container.parents("form").submit();
        }, pDelay);
      });
    });
  };

  /**
   * Toggles action buttons for store (map, phone, email).
   *
   * @param caller HTML element, container for store.
   * @public
   */
  var storeLocationClick = function(caller) {
    var $this = $(caller);
    var $storeExt = $this.find('div#storeExt');

    var toggleLocationControls = function ($controlPanel) {
      $controlPanel.children('img').toggleClass('clicked');
      $controlPanel.next('div').children('div').toggleClass('hidden');
      return false;
    }

    if ($storeExt.css('display') == 'none') {
      $this[0].className = "icon-ArrowDown";
      $storeExt.toggle();
      window.setTimeout(toggleLocationControls, 4, $this);
    } else {
      $storeExt.toggle();
      window.setTimeout(toggleLocationControls, 4, $this);
      $this[0].className = "icon-ArrowLeft";
      window.setTimeout(function($controlPanel) {
        $controlPanel.next('div').toggle();
      }, 200, $this); // Give a bit more time to browser for animation and then completely hide controls
    }
  };

  /**
   * Goes to specified url with delay 500 ms.
   *
   * @param {string} url URL.
   * @returns {boolean} true always.
   * @public
   */
  var gotoURL = function(url) {
    setTimeout(
      function() {
        window.location.href = url;
      },
      500);
    return true;
  };

  /**
   * Parses the specified URL or current page URL (if URL is undefined)
   * and returns a dictionary Object that maps decoded parameter names to decoded values.
   *
   * @param {string} url the specified URL with params to map.
   * @return An object with keys and values where keys are the parameter names from the URL.
   * @public
   */
  var getURLParams = function(url) {
    if (!url) {
      url = window.location.href;
    }

    var params = decodeURI(url);
    var indexOfQuestionMark = params.indexOf('?');
    if (indexOfQuestionMark !== -1) { // if this is whole url, not search part
      params = params.slice(indexOfQuestionMark);
    }

    // A state machine to parse the param string in one pass
    var state = false;
    var key = '';
    var value = '';
    var paramsObject = {};
    // The first character will always be '?', so we skip it
    for (var i = 1; i < params.length; i++) {
      var current = params.charAt(i);
      switch (current) {
        case '=':
          state = !state;
          break;
        case '&':
          state = !state;
          paramsObject[key] = value;
          key = value = '';
          break;
        default:
          if (state) {
            value += current;
          } else {
            key += current;
          }
          break;
      }
    }
    if (key) {
      paramsObject[key] = value; // put the last pair on the "paramsObject"
    }
    return paramsObject;
  };

  /**
   * Extracts specified URL (or current if it's absent) with the specified parameter/value
   * and returns encoded URL.
   *
   * @param {string} key the name of the parameter to set (any existing entry for that parameter will be replaced).
   * @param {string} value the desired value for the parameter.
   * @param {string} url the specified URL to add the parameter, if undefined the current page URL is used.
   * @return An encoded URL string.
   * @public
   */
  var addURLParam = function(key, val, url) {
    if (!url) {
      url = window.location.href;
    }
    var params = getURLParams(url);
    params[key] = val;
    var loc = url.slice(0,url.indexOf('?'));
    return encodeURI(loc) + "?" + $.param(params); // JQuery.param makes it to be encoded like the encodeURIComponent function does
  };

  /**
   * Removes 'notLoaded' css class from img element if image is loaded.
   *
   * @param img image element.
   * @returns {boolean} false always.
   * @public
   */
  var removeImgNotLoaded = function(img) {
    $(img).removeClass('notLoaded').removeAttr('onload');
    return false;
  };

  /**
   * Shows the "Loading..." message box.
   * @public
   */
  var showLoadingWindow = function() {
    $("#modalMessageBox").show().addClass("refineOverlay"); // Show spinner and make transparent background of it
    CRSMA.global.toggleModal(true);
    window.scroll(0, 1);
  };

  /**
   * Hides the "Loading..." message box.
   * @public
   */
  var hideLoadingWindow = function() {
    $("#modalMessageBox").hide().removeClass("refineOverlay");
    CRSMA.global.toggleModal(false);
  };

  /**
   * Shows modal dialog to confirm delete operation.<br>
   * The dialog position is calculated based on this container element.
   *
   * @param pOffsetContainer Container with delete link, clicking on it shows modal dialog.
   * @public
   */
  var removeItemDialog = function(pOffsetContainer) {
    var $offsetContainer = $(pOffsetContainer);
    var top = $offsetContainer.offset().top - $("#pageContainer").offset().top;
    var right = $(document).width() - $offsetContainer.offset().left - $offsetContainer.outerWidth() - 1; // -1px for the left border

    $("div.moveDialog div.moveItems").css({top: top, right: right, display: "block"});

    // Hide dialog by clicking outside the dialog items box
    $("div.moveDialog").click(function() {
      $(this).hide();
    });

    $("div.moveDialog").show();
    CRSMA.global.toggleModal(true);
  };

  /**
   * Displays the modal dialog and scrolls it's content to the start.
   * @public
   */
  var toggleCantDeleteAddressDialog = function() {
    $("#modalMessageBox").show();
    CRSMA.global.toggleModal(true);
    window.scroll(0,1);
  };

  /**
   * Deletes param from the specified URL if it exists and returns modified URL.
   *
   * Note, if you plan to change logic inside, check that all functions below draws "true".
   *
   * console.log(CRSMA.global.removeURLParam('http://someurl', 'a')             == "http://someurl");
   * console.log(CRSMA.global.removeURLParam('http://someurl?a=1', 'a')         == "http://someurl");
   * console.log(CRSMA.global.removeURLParam('http://someurl?a=1&b=2', 'b')     == "http://someurl?a=1");
   * console.log(CRSMA.global.removeURLParam('http://someurl?a=1&b=2', 'a')     == "http://someurl?b=2");
   * console.log(CRSMA.global.removeURLParam('http://someurl?a=1&b=2&c=3', 'b') == "http://someurl?a=1&c=3");
   *
   * @param {@string} url URL.
   * @param {@string} param Parameter to remove.
   * @public
   */
  var removeURLParam = function(url, param) {
    if (url) {
      var re = new RegExp('^(.+)(\\?|&)' + param + '=[^\\?&]+(.?)(.*)$', 'i')
      if (re.test(url)) {
          return RegExp.$3
            ? RegExp.$1 + RegExp.$2 + RegExp.$4
            : RegExp.$1
      } else {
        return url;
      }
    }
  };

  /**
   * Deletes parameters from the url.
   *
   * @param {@string} url URL.
   * @param params Array of string parameters to remove.
   * @public
   */
  var removeURLParams = function(url, params) {
    var result = url;
    if (params) {
      for (var i = 0; i < params.length; i++) {
        result = removeURLParam(result, params[i]);
      }
    }
    return result;
  };

  /**
   * Initializes Endeca "MobilePage" cartridge renderer.
   * Creates bar with 2 blocks:
   * the 1-st one contains information about visible content,
   * the 2-nd one hides active content and shows the next content.
   * @public
   */
  var initMobilePage = function() {
    $blockTitle = $("<button role='rowheader'></button>");
    $blockNavigation = $('<a href="javascript:void(0)" onclick="CRSMA.global.toggleSections()"></a>');

    $("#switchBar")
      .addClass("switchBar")
      .append($blockTitle)
      .append($("<button role='button'>").append($blockNavigation))
      .append('<span class="dividerBar"/>');

    var params = CRSMA.global.getURLParams();
    var isRefinementRequest = (params["nav"] != null);
    var isBrowseOrBrandRequest = (params["Ntt"] == null);

    var resultsListCount = 0;
    var searchResults = $("div.searchResults");
    if (searchResults.length > 0) {
      resultsListCount = searchResults[0].dataset["resultsListCount"];
    }
    noSearchResults = (resultsListCount == 0);

    var navGroupsCount = 0;
    var guidedNav = $("div.guidedNavigation");
    var guidedNavExists = (guidedNav.length > 0);
    if (guidedNavExists) {
      navGroupsCount = guidedNav[0].dataset["navigationGroupsCount"];
    }

    var isMainActive = !isRefinementRequest || (navGroupsCount == 0);
    CRSMA.global.registerStates([{
      // ResultList
      id: "main",
      buttonTitle: (isBrowseOrBrandRequest ? getCategoryOrBrandName() : getSearchTerm()),
      buttonNavigation: CRSMA.i18n.getMessage("mobile.js.search.refine"),
      active: isMainActive
    },{
      // Breadcrumbs, GuidedNavigation
      id: "secondary",
      buttonTitle: (guidedNavExists ? (isBrowseOrBrandRequest ? CRSMA.i18n.getMessage("mobile.js.search.shop.by") : CRSMA.i18n.getMessage("mobile.js.search.refine.by")) : ""),
      buttonNavigation: CRSMA.i18n.getMessage("mobile.js.common.done"),
      active: !isMainActive
    }]);
    $("#switchBar").toggleClass("refine", isRefinementRequest);
    
    // check if further subcategories exist among refinements
    var areCategoryRefinements = $('div.refinementFacetGroupContainer[data-is-category="true"]').length > 0;
    
    // Show popup with "No search results" info 
    // if we are on the refinement's page and there are no search results.
    // Note, that we also check if are category facets for further refinements:
    // It's for situation when user selects Root-category, that hasn't any products, but has subcategories, that have own products."
    if (activeStateIndex == 1 /* secondary*/ && !areCategoryRefinements && noSearchResults) {
      $('#noSearchResultsPopup').show();
      CRSMA.global.toggleModal(true);
    }
  };

  /**
   * Registers possible search bar states in the internal var.
   *
   * @param pStates Array of second states (see usage in code).
   * @public
   */
  var registerStates = function(pStates) {
    states = pStates;

    for (var i = 0; i < states.length; i++) {
      if (states[i].active) {
        adjustStateTo(i);
        return;
      }
    }
  };

  /**
   * Refreshes search bar titles according to the new state index.
   * @param {integer} stateIndex New state index.
   *
   * @private
   */
  var adjustStateTo = function(stateIndex) {
    if (typeof activeStateIndex !== "undefined") {
      $('#' + states[activeStateIndex].id).hide();
    }

    var state = states[stateIndex];
    $blockTitle.html(state.buttonTitle);
    $blockNavigation.html(state.buttonNavigation);
    $('#' + state.id).show();

    activeStateIndex = stateIndex;
  };

  /**
   * Recalculates activeStateIndex and adjusts search bar.
   * @public
   */
  var toggleSections = function() {
    adjustStateTo(activeStateIndex == 0 ? 1 : 0);
    $("#switchBar").toggleClass("refine");
  };

  /**
   * Calls specified function with the switched off AJAX error handling.
   * Restores AJAX error handling in the end.
   * @public
   */
  var noAjaxError = function(fn) {
    // Suppress AJAX error handling
    CRSMA.global.SHOW_AJAX_ERROR = false;

    // Function call
    if (fn) {
      fn.call();
    }

    // Restore AJAX error handling
    CRSMA.global.SHOW_AJAX_ERROR = true;
  };
  
  /**
   * Returns selected category on CategoryBrowse page.
   * @private
   */
  var getCategoryOrBrandName = function() {
    //Try to get Category name
    var categoriesAndSubcategories = $('#product_category .crumbContent .crumbContentText span');
    var lastItemIndex = categoriesAndSubcategories.length - 1;
    //If there are no any Categories - try to get Brand name
    if (lastItemIndex == -1 ) {
      categoriesAndSubcategories = $('#product_brand .crumbContent .crumbContentText span');
      lastItemIndex = categoriesAndSubcategories.length - 1;
    }
    //If still have no smth. to show - return empty string 
    return lastItemIndex != -1 ? categoriesAndSubcategories[lastItemIndex].innerText : "";
  };

  /**
   * Returns search term
   * @private
   */
  var getSearchTerm = function() {
    var escapedTerm = CRSMA.global.getURLParams()["Ntt"];
    var unescapedTerm = "";
    if (escapedTerm) {
      unescapedTerm = '\"' + unescape(escapedTerm.replace(/\+/g, " ")) + '\"';
    }
    return unescapedTerm;
  };

  /**
   *  Function sets foxcus on first input field in error state
   *  Makes app more accessible
   *  @public
   */
  var focusOnFirstInputInErrorState = function() {
    var $input = $('.errorState .content input');
    if ($input.length){
      $input[0].focus();
    }
  };
  
  /**
   *  This method initializes the sliders in productsHorizontalList.jsp.
   *  
   *  @public
   */
  var initHorizontalListSliders = function() {
    // Set the width of itemContainer to the width of the entire screen. The slider will then takes care
    // of figuring out the appropriate width for the cells inside it
    var doItemsExist = $(".itemsContainer").length > 0;
    if (doItemsExist) {
      var screenWidth = $(window).width() - 1;
      $(".itemsContainer").css({
        "width": screenWidth
      });
    }

    // Initialize all sliders on the page
    $("div[id^='horizontalContainer']").each(function(){
      var id = $(this).attr("id");
      var numberOfCells = 3;
      CRSMA.sliders.createSlider({
        gridid: "#" + id,
        numberOfCells: numberOfCells,
        onTouchEventException: function(el) {
          $(el).css({
            "height": "70px",
            "overflow": "auto"
          });
        }
      });
      
      // Mark items that are off screen as hidden so that VoiceOver won't read them (accessibility)
      var $cells = $(this).children(".cell");
      $cells.each(function(index) {
        if (index >= numberOfCells) {
          $(this).css({
            display: "none"
          });
        }
      });
    });

    // Now that the slider has been initialized, let itemContainer inherits its parent's width. 
    // This will let the browser figure out the right value when the user switches between portrait 
    // and landscape views
    if (doItemsExist) {
      $(".itemsContainer").css({
        "width": "inherit"
      });
    }
  };
  

  /**
   * List of public "CRSMA.global" 
   */
  return {
    // methods
    'addURLParam'       : addURLParam,
    'dataAccessor'      : dataAccessor,
    'delayedSubmitSetup': delayedSubmitSetup,
    'getCookieByName'   : getCookieByName,
    'getURLParams'      : getURLParams,
    'gotoURL'           : gotoURL,
    'hidePopup'         : hidePopup,
    'initMobilePage'    : initMobilePage,
    'noAjaxError'       : noAjaxError,
    'loginPageClick'    : loginPageClick,
    'refreshCartBadge'  : refreshCartBadge,
    'refreshCartDataIfExpired' : refreshCartDataIfExpired,
    'saveBadgeData'     : saveBadgeData,
    'saveCartItems'     : saveCartItems,
    'registerStates'    : registerStates,
    'removeImgNotLoaded': removeImgNotLoaded,
    'removeItemDialog'  : removeItemDialog,
    'removeURLParam'    : removeURLParam,
    'removeURLParams'   : removeURLParams,
    'showContactUsPage' : showContactUsPage,
    'showPopup'         : showPopup,
    'storeLocationClick': storeLocationClick,
    'toggleCantDeleteAddressDialog' : toggleCantDeleteAddressDialog,
    'toggleModal'       : toggleModal,
    'toggleSections'    : toggleSections,
    'showLoadingWindow' : showLoadingWindow,
    'hideLoadingWindow' : hideLoadingWindow,
    'focusOnFirstInputInErrorState' : focusOnFirstInputInErrorState,
    'initHorizontalListSliders'      : initHorizontalListSliders,

    // properties
    'SHOW_AJAX_ERROR'   : SHOW_AJAX_ERROR,
    'EventManager'      : EventManager
  }
}();


$(window).one("load", function() {
  window.scrollTo(0, 1);
  history.replaceState(new Date());
});

/**
 * Adds handling for AJAX Errors.
 */
$(document).ajaxError(function(event, jqXHR, ajaxSettings, thrownError) {
  if (CRSMA.global.SHOW_AJAX_ERROR) {
    CRSMA.global.showPopup("error", CRSMA.i18n.getMessage('mobile.js.ajaxError'));
  }
});

/**
 * Javascript Global Objects extensions.
 */
(function() {
  /**
   * Formats string, replacing parameters {0} {1} etc. by arguments in the specified order. <br>
   * May be used with different quantity of arguments.
   *
   * @example
   * var s = 'May name is {0} {1}';
   * s.format('John', 'Smith'); // My name is John Smith
   */
  String.prototype.format = function() {
    var formatted = ""; // this will be our formatted string
    var split = this.split('');
    var inParam = false; // state variable
    var paramNumber = '';
    for (var i = 0; i < split.length; i++) {
      var current = split[i];
      switch (current) {
        case '{': // begin specifying a parameter
          inParam = true;
          break;
        case '}': // done specifying parameter
          inParam = false;
          // Insert the parameter or, if it doesn't exist, leave it as it is
          var param = arguments[parseInt(paramNumber, 10)] || "{" + paramNumber + "}";
          formatted += param; // Insert the parameter into the formatted string
          paramNumber = ""; // make sure to reset the paramNumber
          break;
        default:
          if (inParam) {
            paramNumber += current;
          } else {
            formatted += current;
          }
          break;
      }
    }
    return formatted;
  };

  /**
   * This adds a setObject method to the Storage interface so that we can add our
   * HTML fragments into local storage. <br>
   *
   * We should be able to do this without doing this, according to the
   * localstorage spec, but no browser has added support for it yet.
   *
   * @param {string} key key
   * @param value any object
   * @memberOf Storage
   * @namespace
   */
  Storage.prototype.setObject = function(key, value) {
    this.setItem(key, JSON.stringify(value));
  };

  /**
   * This adds a getObject method to the Storage interface so that we can get our
   * HTML fragments out of local storage. <br>
   *
   * We should be able to do this without doing this, according to the
   * localstorage spec, but no browser has added support for it yet.
   *
   * @param {string} key key
   * @memberOf Storage
   * @namespace
   */
  Storage.prototype.getObject = function(key) {
    return this.getItem(key) && JSON.parse(this.getItem(key));
  };
})();

/**
 * @namespace "Internationalization" module of "Commerce Reference Store Mobile Application"
 * @description Holds methods for handling of string resources on different languages.   
 */
CRSMA.i18n = function() {
  /**
   * Map for localization messages.
   *
   * @private
   */
  var messages = {};

  /**
   * Returns registered message by key.
   * If there are no such message key is returned.
   *
   * @example
   * CRSMA.i18n.getMessage('agent007'); // My name is {1}, {0} {1}
   * CRSMA.i18n.getMessage('agent007', 'James', 'Bond'); // 'My name is Bond, James Bond'
   *
   * @param {string} key - key.
   * @return {string} localized message.
   * @public
   */
  var getMessage = function(key) {
    var message = messages[key];
    if (!message) {
      return key;
    }
    if (arguments.length > 1) {
      message = message.format(Array.prototype.slice.call(arguments, 1));
    }
    return message;
  };

  /**
   * Registers localized message with specified key.
   *
   * @param data - object with messages('key' : 'value').
   * @public
   */
  var register = function(data) {
    for (var key in data) {
      if (data.hasOwnProperty(key)) {
        messages[key] = data[key];
      }
    }
  };

  return {
    // methods
    'getMessage' : getMessage,
    'register'   : register
  }
}();

/* Handling history.back added */
window.onpopstate = function(event) {
  CRSMA.global.refreshCartDataIfExpired(event.state);
};
