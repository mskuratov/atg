/**
 * Search auto-suggest module, specific for typeahead dimension search.
 * @ignore
 */
CRSMA = window.CRSMA || {};

/**
 * @namespace "Auto-suggest" Javascript Module of "Commerce Reference Store Mobile Application"
 * @description Search auto-suggest module, specific for typeahead dimension search.
 */
CRSMA.autosuggest = function() {
  /**
   * Is "auto-suggest" panel active?
   * @private
   */
  var autosuggestActive = true;
  /**
   * "Auto-suggest" module options.
   * @private
   */
  var autosuggestOptions;
  /**
   * Search text element, last value.
   * @private
   */
  var lastValue = "";
  /**
   * Search text jQuery element.
   * @private
   */
  var $searchText;
  /**
   * "Auto-suggest" module html container element (jQuery).
   * @private
   */
  var $autosuggestContainer;
  /**
   * @private
   */
  var timeOutId;
  
  /**
   * XMLHttpRequest object, used while the last auto-suggest ajax request 
   * @private
   */
  var xhr;
  
  /**
   * Flag that allows to check if autosuggest is shown
   */
  var suggestionsShown = false;

  /**
   * Hide the search suggestion box.
   * @public
   */
  var hide = function() {
    $autosuggestContainer.hide();
    autosuggestActive = false;
  };

  /**
   * Show the search suggestion box.
   * @private
   */
  var show = function() {
    if ($autosuggestContainer.is(":hidden")) {
      $autosuggestContainer.show();
      autosuggestActive = true;
    }
  };

  /**
   * Activate the search suggestion box.
   * @private
   */
  var handleRequest = function() {
    var callback = function() {
      var text = $.trim($searchText.val());
      if (text != lastValue) {
        if (text.length >= autosuggestOptions.minAutoSuggestInputLength) {
          requestData();
        } else {
          hide();
        }
      }
      lastValue = text;
    };

    if (timeOutId) {
      clearTimeout(timeOutId);
    }
    timeOutId = setTimeout(callback, autosuggestOptions.delay);
  };

  /**
   * Send Ajax to backend service to request data.
   * @private
   */
  var requestData = function() {
    /* Note, that if user types search terms quickly and the speed of the connection is slow, may be the situation 
     * when auto-suggest request should be posted but the previous one hasn't been processed yet.
     * We should abort the previous request because we needn't already in it's results.
     */
    if (xhr && xhr.readyState != 4) {
      CRSMA.global.noAjaxError(
        function() {
          /* Note, that 'abort' call is wrapped in CRSMA.global.noAjaxError, that eliminates popup, appearing in ajaxError situation.
           * See $(document).ajaxError(...) handler for details in global.js */
          xhr.abort();
        }
      );
      xhr = null;
    }

    xhr = $.ajax({
      url: composeUrl(),
      dataType: "json",
      async: true,
      success: function(data) {
        showSearchResult(data);
      }
    });
  };

  /**
   * Search suggestion is search term sensitive. So it will take the search
   * term applied on current page and add it into the Ajax request url.
   * @private
   */
  var composeUrl = function() {
    var url = autosuggestOptions.autoSuggestServiceUrl;
    var searchTerm = $.trim($searchText.val());

    if (url.indexOf("?") == -1) {
      url += "?";
    } else {
      url += "&";
    }
    url += "format=json&Dy=1&assemblerContentCollection=" + autosuggestOptions.collection + "&Ntt=" + searchTerm + "*";

    return url;
  };

  /**
   * Show the search results in the suggestion box.
   * @param data - auto-suggest data
   * @private
   */
  var showSearchResult = function(data) {
    var htmlResult = processSearchResult(data);
    if (htmlResult != null) {
      $autosuggestContainer.html(htmlResult);
      show();
      suggestionsShown = true;
    } else {
      // Hide the result box if there is no result
      hide();
      suggestionsShown = false;
    }
  };

  /**
   * Generate rendering HTML according to data.
   * @param data - auto-suggest data
   * @private
   */
  var processSearchResult = function(data) {
    var dimSearchResult = null;
    var autoSuggestCartridges = data.contents[0].autoSuggest;

    // If no data returned, returns null
    if (autoSuggestCartridges == null || autoSuggestCartridges.length == 0) {
      return null;
    }

    // Find the dim search result in the cartridge list, only consider one cartridge
    // For auto-suggest dimension search
    for (var j = 0; j < autoSuggestCartridges.length; j++) {
      var cartridge = autoSuggestCartridges[j];
      if (cartridge['@type'] == "DimensionSearchAutoSuggestItem") {
        // Find dim search result
        dimSearchResult = cartridge;
        break;
      }
    }

    if (dimSearchResult != null) {
      return generateHtmlContent(dimSearchResult);
    }
    return null;
  };

  /** 
   * Generates HTML content for seachResultItem
   * @param dimSearchResult - DimensionSearchAutoSuggestItem
   * @private
   */
  var generateHtmlContent = function(dimSearchResult) {
    var newContent = null;

    // Contains dimension search results
    if (dimSearchResult != null && dimSearchResult.dimensionSearchGroups != null && dimSearchResult.dimensionSearchGroups.length > 0) {
      newContent = "<div>";

      var dimSearchGroupList = dimSearchResult.dimensionSearchGroups;

      for (var i = 0; i < dimSearchGroupList.length; i++) {
        var dimResultGroup = dimSearchGroupList[i];

        // Output dim result of this group here
        for (var j = 0; j < dimResultGroup.dimensionSearchValues.length; j++) {
          var dimResult = dimResultGroup.dimensionSearchValues[j];
          var action = dimResult.contentPath + dimResult.navigationState;
          var text = dimResult.label;
          var ancestors = dimResult.ancestors;
          var ancestorsStr = "";
          if (ancestors != null && ancestors.length > 0) {
            for (var n = 0; n < ancestors.length; n++) {
              ancestorsStr += ancestors[n].label + " > ";
            }
          }

          newContent = newContent + '<div class="dimResult" role="link" onclick="window.location=\'' + autosuggestOptions.siteContextPath
            + CRSMA.global.removeURLParams(action, ['format', 'assemblerContentCollection'])
            + '\'"><div class="suggestion">'
            + ancestorsStr + text + '</div></div>';
        }
      }
      newContent = newContent + "</div>";
    }

    return newContent;
  };

  /**
   * Init method (constructor).
   * @param options - The options to be applied.
   */
  var init = function(options) {
    var settings = $.extend({ /* default settings */
      searchTextId              : "searchText",
      minAutoSuggestInputLength : 3,
      displayImage              : false,
      delay                     : 250,
      autoSuggestServiceUrl     : "",
      collection                : "",
      siteContextPath           : "",
      containerClass            : "dimSearchSuggContainer"
    }, options || {});

    autosuggestOptions = settings;
    var $elt = $("#" + settings.searchTextId);
    $searchText = $elt;
    $autosuggestContainer = $('<div class="' + autosuggestOptions.containerClass + '"></div>');

    // Append the container to the current page
    $(".searchBar").after($autosuggestContainer);

    /**
     * Capture the keyboard events.
     */
    $elt.keydown(function(e) {
      handleRequest();
    });

  };
  
  /**
   * Returns current state of AutosuggestPanel
   * @return boolean is panel shown.
   * @public
   */
  var areSuggestionsShown = function() {
    return suggestionsShown;
  };

  /**
   * List of public "CRSMA.autosuggest"
   */
  return {
    // Methods
    "init"                : init,
    "hide"                : hide,
    "areSuggestionsShown" : areSuggestionsShown
  }
}();
