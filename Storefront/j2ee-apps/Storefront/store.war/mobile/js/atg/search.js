/**
 * "Search" Javascript functions.
 * @ignore
 */
CRSMA = window.CRSMA || {};

/**
 * @namespace "Search" Javascript Module of "Commerce Reference Store Mobile Application"
 * @description Holds functionality related to search actions in CRSMA.
 */
CRSMA.search = function() {
  /**
   * Pins and range elements.
   * @private
   */
  var $leftPinElem, $rightPinElem, $rangeElem;

  /**
   * PriceSlider ranges.
   * @private
   */
  var fullRange, activeRange;

  /**
   * Timeout identifier for PriceSlider delay submit.
   * @private
   */
  var sliderSubmitTimeoutId;

  /**
   * Timeout identifier for "Search" field, "onBlur" handler.
   * @private
   */
  var searchBlurTimeoutId;

  /**
   * PriceSlider elements.
   * @private
   */
  var $minPriceContainer, $maxPriceContainer;

  /**
   * PriceSlider dimensions.
   * @private
   */
  var containerWidth, pinWidth, valueToCoordinatesRatio, activeOffset;
  var previousOffset = 0, layoutStrategy;

  /**
   * PriceSlider property name.
   * @private
   */
  var pricePropertyName;

  /**
   * PriceSlider container.
   * @private
   */
  var container;

  /**
   * Flag for "Search" field. Default to false.
   * @private
   */
  var isSearchInit = false;

  /**
   * "Search" field element.
   * @private
   */
  var $searchField;

  /**
   * Indicator of remove Breadcrumb request state.
   * Not allows to click on another crumb during request sending for previous
   */
  var removeRequestInProgress = false;

  /**
   * Indicator of add Refinement request state.
   * Not allows to click on another refinement during request sending for previous
   */
  var addRequestInProgress = false;

  /**
   * Validates range.
   *
   * @param {object} range - Slider range.
   * @private
   */
  var validateAndFixRange = function(range) {
    var validRange = {
      min : fullRange.min,
      max : fullRange.max
    };
    if (range) {
      if (range.min && range.min > fullRange.min) {
        validRange.min = range.min;
      }
      if (range.min > fullRange.max) {
        validRange.min = fullRange.max;
      }

      if ((range.max || range.max == 0) && range.max < fullRange.max) {
        validRange.max = range.max;
      }
      if ((range.max || range.max == 0) && range.max < fullRange.min) {
        validRange.max = fullRange.min;
      }
      if (validRange.max < validRange.min) {
        validRange.max = validRange.min;
      }
    }
    return validRange;
  };

  /**
   * Converts slider range to slider offset.
   *
   * @param {object} range - Slider range.
   * @return {object} offset - Slider offset.
   * @private
   */
  var rangeToOffset = function(range) {
    var rangeLength = range.max - range.min;
    var offset = {
      left : Math.round((range.min - fullRange.min) * valueToCoordinatesRatio),
      right : Math.round((fullRange.max - range.max) * valueToCoordinatesRatio)
    };

    return offset;
  };

  /**
   * Converts slider offset to slider range.
   *
   * @param {object} offset - Slider offset.
   * @return {object} range - Slider range.
   * @private
   */
  var offsetToRange = function(offset) {
    var offsetLength = offset.right - offset.left;
    var range = {
      min : Math.round(offset.left / valueToCoordinatesRatio),
      max : Math.round((containerWidth - offset.right) / valueToCoordinatesRatio)
    };

    return range;
  };

  /**
   * Setting layout of elements.
   * @private
   */
  var layoutElements = function() {
    var offset = activeOffset;
    $leftPinElem.css({left: offset.left + "px", right: ""});
    $rightPinElem.css({left: "", right: offset.right + "px"});
    $rangeElem.css({left: offset.left + "px", width: containerWidth - offset.left - offset.right + "px"});
  };

  /**
   * Swaps pins if overlapsed.
   *
   * @param left - Slider left offset.
   * @param right - Slider right offset.
   * @private
   */
  var adjustRangeIfOverlapses = function(left, right) {
    if (left + right >= containerWidth) {
      var swap = left;
      left = containerWidth - right;
      right = containerWidth - swap;

      swap = $leftPinElem;
      $leftPinElem = $rightPinElem;
      $rightPinElem = swap;
      layoutStrategy = arguments.callee.caller === adjustOffsetForRightPinMove ? adjustOffsetForLeftPinMove : adjustOffsetForRightPinMove;
    }
    activeOffset.left = left;
    activeOffset.right = right > 0 ? right : 0;
  };

  /**
   * Adjusts offset for left pin.
   *
   * @param delta - Offset delta.
   * @private
   */
  var adjustOffsetForLeftPinMove = function(delta) {
    var newLeft = activeOffset.left + delta;
    if (newLeft < 0) {
      activeOffset.left = 0;
      return false;
    }
    adjustRangeIfOverlapses(newLeft, activeOffset.right);
    return true;
  };

  /**
   * Adjusts offset for right pin.
   *
   * @param delta - Offset delta.
   * @private
   */
  var adjustOffsetForRightPinMove = function(delta) {
    var newRight = activeOffset.right - delta;
    if (newRight < 0) {
      newRight = 0;
      return false;
    }
    adjustRangeIfOverlapses(activeOffset.left, newRight);

    return true;
  };

  /**
   * Adjusts offset for range move.
   *
   * @param delta - Offset delta.
   * @private
   */
  var adjustOffsetForActiveRangeMove = function(delta) {
    if ((activeOffset.left + delta) > 0 && (activeOffset.right - delta) > 0) {
      return adjustOffsetForLeftPinMove(delta) && adjustOffsetForRightPinMove(delta);
    }
    return false;
  };

  /**
   * Returns clientX(touch and click events difference hide).
   *
   * @param {object} event - Slider event.
   * @private
   */
  var getClientXValue = function(event) {
    var clientXvalue = event.clientX;
    if (typeof clientXvalue === "undefined" || clientXvalue == null) {
      clientXvalue = event.originalEvent.touches[0].clientX;
    }
    return clientXvalue;
  };

  /**
   * Slider movement handler.
   *
   * @param {object} event - Slider event.
   * @private
   */
  var movementHandler = function(event) {
    if (layoutStrategy && layoutStrategy(getClientXValue(event) - previousOffset)) {
      previousOffset = getClientXValue(event);
      layoutElements();
    }
    displayPriceRange();
  };

  /**
   * Shows new calculated price range when slider moved.
   * @private
   */
  var displayPriceRange = function() {
    var range = offsetToRange(activeOffset);
    $minPriceContainer.text(range.min);
    $maxPriceContainer.text(range.max);
    $leftPinElem.attr("aria-valuenow", range.min);
    $rightPinElem.attr("aria-valuenow", range.max);
  }

  /**
   * Handler for slider end movement.
   *
   * @param {object} event - Slider event.
   * @private
   */
  var movementEndHandler = function(event) {
    layoutStrategy = undefined;
    $(document).unbind("touchmove mousemove", movementHandler)
               .unbind("touchend mouseup", movementEndHandler);
    delaySubmit(1000);
  };

  /**
   * Submit price range facet.
   *
   * @param {object} range - Price range.
   * @private
   */
  var submitPriceRange = function(range) {
    uri = document.location.pathname;
    var delim = "?";
    var parametersObject = getQueryParamsObject(window.location.search.substring(1));
    parametersObject["Nf"] = pricePropertyName + "|BTWN+" + range.min + "+" + range.max;
    parametersObject["nav"] = "true";

    searchString = decodeURIComponent(jQuery.param(parametersObject));
    document.location = uri + delim + searchString;
  };

  /**
   * Waits and submit price range (one request for several price range modifications).
   *
   * @param delayParam - Time to wait.
   * @private
   */
  var delaySubmit = function(delayParam) {
    if (sliderSubmitTimeoutId) {
      clearTimeout(sliderSubmitTimeoutId);
    }
    sliderSubmitTimeoutId = setTimeout(function() {
      var range = offsetToRange(activeOffset);
      submitPriceRange(range);
      sliderSubmitTimeoutId = null;
    }, delayParam);
  };

  /**
   * Handler for slider end movement.
   *
   * @param {object} event - Slider event.
   * @private
   */
  var movementStartHandler = function(event) {
    event.preventDefault();
    if ($leftPinElem.get(0) === event.target) {
      layoutStrategy = adjustOffsetForLeftPinMove;
    }
    if ($rightPinElem.get(0) === event.target) {
      layoutStrategy = adjustOffsetForRightPinMove;
    }
    if ($rangeElem.get(0) === event.target) {
      layoutStrategy = adjustOffsetForActiveRangeMove;
    }
    previousOffset = getClientXValue(event);
    $(document).bind("touchmove mousemove", movementHandler)
               .bind("touchend mouseup", movementEndHandler);
  };

  /**
   * Allows to get width property of hidden element.
   *
   * @param {object} - Element.
   * @private
   */
  var getWidth = function($item) {
    var props = {visibility : "hidden", display : "block"};
    var $hiddenParents = $item.parents().andSelf().not(":visible");
    var oldProps = [];
    $hiddenParents.each(function() {
      var old = {};
      for (var name in props) {
        old[name] = this.style[name];
        this.style[name] = props[name];
      }
      oldProps.push(old);
    });

    var width = $item.outerWidth();
    $hiddenParents.each(function(i) {
      var old = oldProps[i];
      for (var name in props) {
        this.style[name] = old[name];
      }
    });
    return width;
  };

  /**
   * Constructor for price range slider.
   *
   * @param {object} containerElement - Slider container.
   * @param {object} passedFullRange - Slider price full range.
   * @param {object} passedActiveRange - Slider price initial range.
   * @public
   */
  var initRangeFilter = function(containerElement, passedFullRange, passedActiveRange, pricePropertyNameParam) {
    pricePropertyName = pricePropertyNameParam;
    fullRange = passedFullRange;
    container = containerElement;

    $leftPinElem = $(".range-min-value", container);
    $rightPinElem = $(".range-max-value", container);
    $rangeElem = $(".active-range", container);
    var $priceContainers = $(".price", container);
    $minPriceContainer = $($priceContainers.get(0));
    $maxPriceContainer = $($priceContainers.get(1));

    $leftPinElem.add($rightPinElem)
                .add($rangeElem)
                .bind("touchstart mousedown", movementStartHandler);

    pinWidth = $leftPinElem.outerWidth();
    activeRange = validateAndFixRange(passedActiveRange);

    $leftPinElem.attr("aria-valuemin", fullRange.min);
    $leftPinElem.attr("aria-valuemax", fullRange.max);
    $rightPinElem.attr("aria-valuemin", fullRange.min);
    $rightPinElem.attr("aria-valuemax", fullRange.max);

    drawSlider();

    // When device orientation changed - slider should be recalculated and redrawn
    $(window).bind("orientationchange", function() {
      drawSlider();
    });
  };

  /**
   * Draws slider for active range.
   * @private
   */
  var drawSlider = function() {
    containerWidth = getWidth(container.find(".slider-wrapper")) - pinWidth;
    valueToCoordinatesRatio = containerWidth / (fullRange.max - fullRange.min);

    $leftPinElem.attr("aria-valuenow", activeRange.min);
    $rightPinElem.attr("aria-valuenow", activeRange.max);

    activeOffset = rangeToOffset(activeRange);
    layoutElements();
    displayPriceRange();
  };

  /**
   * Adds/removes "searchFocus" class to element with "header" id and initializes search-related elements
   * if they are not yet initialized.
   * @public
   */
  var searchOnFocus = function() {
    initSearchIfNotInited();
    $("#header").addClass("searchFocus");
  };

  /**
   * Initializes bindings for "Multisite" menu processing.
   * @private
   */
  var initSearchIfNotInited = function() {
    if (!isSearchInit) {
      $searchField = $("#searchText");
      $("#searchForm").submit(function() {
        return !isSearchFieldEmpty();
      });
      $searchField.bind("blur", searchOnBlur);
      isSearchInit = true;
    }
  }

  /**
   * Checks if "Search" field is empty.
   * @private
   */
  var isSearchFieldEmpty = function() {
    return ($searchField.attr('value').length == 0);
  }

  /**
   * Applies facet and adding animation.
   * @param {object} pDimValueLI - Facet line (LI tag).
   * @param {object} pNavLink - Link for applying.
   * @public
   */
  var applyFacet = function(pDimValueLI, pNavLink) {
    if (!addRequestInProgress) {
      var $li = $(pDimValueLI);

      // When first animation(scaling) ends - make changes for other lines
      $li.bind("webkitTransitionEnd", function() {
        var liHeightOffset = $li.height() * -1;
        if ($li.is(":last-child")) {
          $li.prev().css({"margin-bottom" : liHeightOffset});
          return;
        }
        if ($li.is(":first-child")) {
          $li.next().css({"border-top" : "none"});
        }
        $li.next().css({"margin-top" : liHeightOffset});
      });

      // When all animation sequence ends - apply facet
      $li.bind("webkitAnimationEnd", function() {
        // Hide dimension container when the last dimension value is selected in RefinementMenu
        if ($li.parent().children("li").length == 1) {
          $li.parent().parent().hide();
        }

        document.location = setNavState(pNavLink);
      });

      // Start animation
      $li.addClass("animateScaleAndFlyUp");
      addRequestInProgress = true;
    }
  };

  /**
   * Checks if navigation state parameter is set and sets it if not.
   *
   * @param pNavLink  - String URL to check and setup if needed.
   * @return String URL
   * @public
   */
  var setNavState = function(pNavLink) {
    var params = getQueryParamsObject(pNavLink);
    if (params["nav"]) {
      return pNavLink;
    }
    return pNavLink + "&nav=true";
  };

  /**
   * Turns String URL into query parameters object.
   * @param pQueryString - String URL to be transformed
   * @return parameters object (object[paramName] = paramValue)
   * @private
   */
  var getQueryParamsObject = function(pQueryString) {
    var parametersObject = {};
    if (pQueryString.length != 0) {
      var paramsArray = pQueryString.split("&");
      for (var i = 0, len = paramsArray.length; i < len; i++) {
        var paramArray = paramsArray[i].split("=");
        parametersObject[paramArray[0]] = unescape(paramArray[1]);
      }
    }
    return parametersObject;
  }

  /**
   * Removes crumb and adding animation.
   *
   * @param {object} object - Line with crumb.
   * @param {object} link - Link for remove.
   * @public
   */
  var removeCrumb = function(object, link) {
    if (!removeRequestInProgress) {
      var $li = $(object);
      // Clone Li and remove original - allows to avoid overlapping during flight
      var $cloneLi = $li.clone();
      var of = $li.offset();
      $cloneLi.css({position: "absolute", top: of.top, left: of.left, margin: 0, width: $li.width(), height: $li.height()})
          .appendTo($li.parent());
      $li.stop(true).remove();
      // Calculate flightDestinationCoordinates according to crumb category if no category - fly to the bottom of the screen
      var flightDestinationTopOffset = 350;
      var crumbCategoryName = $cloneLi.attr("id");
      var $ulCategory = $("#ul_" + crumbCategoryName);
      if ($ulCategory.length > 0) {
        var ulCategoryTopOffset = $ulCategory.offset().top;
        var ulCategoryheight = $ulCategory.height();
        // Calculating destination to the center of the list
        flightDestinationTopOffset = ulCategoryTopOffset + ulCategoryheight / 2;
      }
      // When all animation sequence ends - remove crumb
      $cloneLi.bind("webkitAnimationEnd", function() {
        document.location = link;
      });
      // Start animation
      $cloneLi.addClass("animateScaleAndFlyDown");
      $cloneLi.css({"-webkit-transition-property": "top", "-webkit-transition-duration": "1.5s", "top": flightDestinationTopOffset});
      removeRequestInProgress = true;
    }
  };

  /**
   * Disables crumb subCategories.
   *
   * @param {object} object - Anchor with crumb.
   * @param {object} link - Link for remove.
   * @public
   */
  var disableSubCategoryCrumbs = function(object, link) {
    var $crumbLink = $(object);
    $crumbLink.nextAll().addClass("disabledCrumb");
    event.stopPropagation();
    setTimeout(function() {document.location = link;}, 200);
  };

  /**
   * Clears "Search" field and set focus to it.
   * @private
   */
  var clearSearchField = function() {
    $searchField.attr('value', '');
    focusOnSearchField();
    CRSMA.autosuggest.hide();
    $('.clearSearchBtn').hide();
  };

  /**
   * "Search" field, "onBlur" handler.
   * @private
   */
  var searchOnBlur = function() {
    if (isSearchFieldEmpty() || !CRSMA.autosuggest.areSuggestionsShown()) {
      $("#header").removeClass("searchFocus");
      CRSMA.autosuggest.hide();
    }
  };

  /**
   * Sets focus on "Search" field.
   * @private
   */
  var focusOnSearchField = function() {
    $searchField.focus();
  };

  /**
   * @public
   */
  var getItems = function(url) {
    $.ajax({
      type : "GET",
      dataType : "html",
      url : url,
      success : function(data) {
        // Find "Show more" link
        var $showMore = $("ul.searchResults > li.moreResults");

        // Remember previous sibling for "Show More"
        var $last = $showMore.prev();

        $showMore.remove();

        // Check new items in response data
        var $newFeed = $(data).find("li#searchItem, li.moreResults");

        // Add new items to the current list
        $newFeed.insertAfter($last);
        updatePaginationInfo();
      }
    });
  };

  /**
   * @public
   */
  var updatePaginationInfo = function() {
    var itemsEndCount = $("li#searchItem").size();

    var $paginationInfo = $("#paginationInfo");
    var pagiantionTextArray = $paginationInfo[0].innerText.split(" ");
    pagiantionTextArray[2] = itemsEndCount;
    $paginationInfo[0].innerText = pagiantionTextArray.join(" ");
  };

  /**
   * @public
   */
  var initSortByTopPicks = function() {
    $("#topPicksURL").click(function(e) {
      // Disable the Top Picks hyperlink if the list is already sorted by Top Picks
      if ($("#currentSort").val() == "topPicks")
        e.preventDefault(); 
    });
  };

  /**
   * List of "CRSMA.search" public methods.
   */
  return {
    "searchOnFocus"            : searchOnFocus,
    "applyFacet"               : applyFacet,
    "removeCrumb"              : removeCrumb,
    "disableSubCategoryCrumbs" : disableSubCategoryCrumbs,
    "initRangeFilter"          : initRangeFilter,
    "initSortByTopPicks"       : initSortByTopPicks,
    "clearSearchField"         : clearSearchField,
    "getItems"                 : getItems,
    "updatePaginationInfo"     : updatePaginationInfo,
    "setNavState"              : setNavState
  }
}();
