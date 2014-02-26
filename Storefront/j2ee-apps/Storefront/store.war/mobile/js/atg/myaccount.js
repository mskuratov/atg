/**
 * MyAccount Javascript functions.
 * @ignore
 */
CRSMA = window.CRSMA || {};

/**
 * @namespace "MyAccount" Javascript Module of "Commerce Reference Store Mobile Application"
 * @description Holds functionality related to myaccount pages. 
 */
CRSMA.myaccount = function() {
  /**
   * This function shows states dropdown for selected country.
   *
   * @param {string} country a selected country.
   * @param {string} state [optional] if specified then state will be preselected in the dropdown shown.
   * @private
   */
  var toggleState = function(country, state) {
    var $currentState = $("select.state:visible");
    var $newState = $("select.state[data-country='" + country + "']");
    // Important to add/remove the 'name' attribute from all selects;
    // otherwise, the URL becomes polluted with empty values
    var nameAttr = $currentState.attr("name");
    $("select.state").removeAttr("name");
    $currentState.val("").addClass("default").toggle();
    $newState.toggleClass("default", !(state && state != ""));
    $newState.val(state).attr('name', nameAttr).toggle();    
  };

  /**
   * Applies 'default' css style to select element if selected option is default one.
   *
   * @param e select element
   * @public
   */
  var changeDropdown = function(e) {
    // We have to copy over the class from the 'option' to the 'select'
    // because Webkit will not honor styles applied to an option element
    var $this = $(e.currentTarget);
    $this.toggleClass("default", $("option:selected", $this).is("option:first-child"));
  };

  /**
   * This function is for the "Day of Birth" (DOB) account property editor.
   * Repopulate the "Day" dropdown, if either the "Month" or "Year" dropdown has changed.
   * Leap years are taken into account.
   *
   * @param e select element. The "Month" or "Year" dropdown.
   * @public
   */
  var changeDOBDropdown = function(e) {
    CRSMA.myaccount.changeDropdown(event);
    var $this = $(e.currentTarget);
    var idTarget = $this.attr("id");
    if (idTarget == "DOB_MonthSelect" || idTarget == "DOB_YearSelect") {
      var dayValue = $("#DOB_DaySelect").val();
      var day = (dayValue != "") ? parseInt(dayValue) : 0;
      var monthValue = $("#DOB_MonthSelect").val();
      var month = (monthValue != "") ? parseInt(monthValue) : 0;
      var $yearElement = $("#DOB_YearSelect");
      var yearValue =$yearElement.val();
      var year = (yearValue != "") ? parseInt(yearValue) : 0;
      
      // update the style for the year depending on whether a number has been selected or not
      if (year > 0) {
        $yearElement.removeClass("default");
      } else {
        $yearElement.addClass("default");
      }

      // Calculate the number of days in a new changed month, taking leap years into account
      if (month > 0 && year > 0) {
        var numberOfDaysInMonth;
        switch (month) {
          case 2:
            if (year % 4 == 0 && (!(year % 100 == 0) || year % 400 == 0)) {
              numberOfDaysInMonth = 29; // Leap year
            } else {
              numberOfDaysInMonth = 28;
            }
            break;
          case 4:
          case 6:
          case 9:
          case 11:
            numberOfDaysInMonth = 30;
            break;
          default:
            numberOfDaysInMonth = 31;
        }

        // Repopulate the "Day" dropdown keeping the default option 
        var dayNew = (day > numberOfDaysInMonth) ? numberOfDaysInMonth : day;
        var $dayDropdown = $("#DOB_DaySelect");
        var $options = $("select[id='DOB_DaySelect'] option");
        var $defaultOption = $options.filter(":first");
        $options.remove();
        
        $defaultOption.appendTo($dayDropdown);
        for (var i = 1; i <= numberOfDaysInMonth; i++) {
          if (dayNew == i) {
            $("<option value=\"" + i + "\" selected=\"true\">" + i + "</option>").appendTo($dayDropdown);
          } else {
            $("<option value=\"" + i + "\">" + i + "</option>").appendTo($dayDropdown);
          }
        }
      }
    }
  };

  /**
   * Sets specified order id and submits a form.
   *
   * @param {string} orderId ID of order
   * @public
   */
  var loadOrderDetails = function(orderId) {
    $("#orderId").attr("value", orderId);
    $("#loadOrderDetailForm").submit();
  };

  /**
   * Displays the full CRS modal redirect dialog. The link to the full CRS in the
   * dialog will contain the orderId parameter.
   *
   * @param orderId an 'orderId' parameter value to add to the link to the full CRS site
   * @public
   */
  var toggleRedirectWithOrderId = function(orderId) {
    var $modalDialog = $("#modalMessageBox");
    var link = $("a", $modalDialog);
    link.attr("href", CRSMA.global.addURLParam("orderId", orderId, link.attr("href")));
    $modalDialog.show();
    CRSMA.global.toggleModal(true);
    window.scroll(0, 1);
  };

  /**
   * Attaches value from email input to the url value (email parameter)
   * 
   * @param {string} emailFieldId id of email input tag
   * @param {string} urlId id of url hidden tag
   * @public
   */
  var copyEmailToUrl = function(emailFieldId, urlId) {
    $('#' + urlId).val($('#' + urlId).val() + '&email=' + $('#' + emailFieldId).val());
  };

  /**
   * This function is a handler for country dropdown change event. It toggles
   * state dropdown and invokes standard handler.
   *
   * @param event change event
   * @public
   */
  var selectCountry = function(event) {
    toggleState($(event.currentTarget).val());
    CRSMA.myaccount.changeDropdown(event);
  };

  /**
   * This function is a handler for state dropdown change event. It toggles state
   * dropdown and populate appropriate country in country dropdown.
   *
   * @param event change event
   * @public
   */
  var selectState = function(event) {
    var $countrySelect = $("#countrySelect");
    var state = $(event.currentTarget).val();
    var country = $("option:selected", event.currentTarget).attr("data-country");
    $("select.state:not(:visible)").removeAttr("name");
    if (country && country != "") {
      $countrySelect.val(country).removeClass("default");
      toggleState(country, state);
    }
    CRSMA.myaccount.changeDropdown(event);
  };

  return {
    'changeDropdown'            : changeDropdown,
    'changeDOBDropdown'         : changeDOBDropdown,
    'copyEmailToUrl'            : copyEmailToUrl,
    'loadOrderDetails'          : loadOrderDetails,
    'selectCountry'             : selectCountry,
    'selectState'               : selectState, 
    'toggleRedirectWithOrderId' : toggleRedirectWithOrderId
  }
}();
