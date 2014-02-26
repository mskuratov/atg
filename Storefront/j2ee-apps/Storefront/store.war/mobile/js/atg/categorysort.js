/**
 * Functionality related to the sorting in a category landing page.
 * @ignore
 */
CRSMA = window.CRSMA || {};

/**
 * @namespace "Category Sort" Javascript Module of "Commerce Reference Store Mobile Application"
 * @description Holds common functionality related to the sorting in a category landing page.
 */
CRSMA.categorysort = function() {
  /**
   * Possible values for the sort key.
   * @private
   */
  var SORT_KEY = {
    TOP_PICKS: "",
    NAME: "displayName",
    PRICE: "price"
  };

  /**
   * Possible values for the sort order.
   * @private
   */
  var SORT_ORDER = {
    ASC: "ascending",
    DESC: "descending"
  };
  
  
  /**
   * This methods sets the appropriate value for the sort input element.
   *
   * @param $currentSortKey   The current sorting key for the list.
   * @private
   */
  var sortBy = function(currentSortKey) {
    var previousSort = $("#sort").val();
    var previousSortCriteria = previousSort.split(":");

    switch (currentSortKey) {
      case SORT_KEY.NAME:
        if (previousSortCriteria[0] == SORT_KEY.NAME) {
          if (previousSortCriteria[1] == SORT_ORDER.ASC) {
            $("#sort").val(SORT_KEY.NAME + ":" + SORT_ORDER.DESC);
          } else {
            $("#sort").val(SORT_KEY.NAME + ":" + SORT_ORDER.ASC);
          }
        } else {
          $("#sort").val(SORT_KEY.NAME + ":" + SORT_ORDER.ASC);
        }
        break;

      case SORT_KEY.PRICE:
        if (previousSortCriteria[0] == SORT_KEY.PRICE) {
          if (previousSortCriteria[1] == SORT_ORDER.ASC) {
            $("#sort").val(SORT_KEY.PRICE + ":" + SORT_ORDER.DESC);
          } else {
            $("#sort").val(SORT_KEY.PRICE + ":" + SORT_ORDER.ASC);
          }
        } else {
          $("#sort").val(SORT_KEY.PRICE + ":" + SORT_ORDER.ASC);
        }
        break;

      default:
        $("#sort").val("");
    }
    
    $("#sortByForm").submit();
  };
  
  /**
   * Initializes the actions for each of the sorting buttons.
   * @public
   */
  var initSortingActions = function() {
    $("#nameSort").click(function() {sortBy(SORT_KEY.NAME);});
    $("#priceSort").click(function() {sortBy(SORT_KEY.PRICE);});
    
    $("#topPicksSort").click(function(e) {
      // disable the Top Picks button if the list is already sorted by Top Picks
      if ($("#sort").val() == "")
        e.preventDefault(); 
      else
        sortBy(SORT_KEY.TOP_PICKS);
    });
  };
  
  
  /**
   * List of public "CRSMA.categorysort".
   */
  return {
    // Methods
    "initSortingActions"  : initSortingActions
  };

}(); // END CRSMA.categorysort
