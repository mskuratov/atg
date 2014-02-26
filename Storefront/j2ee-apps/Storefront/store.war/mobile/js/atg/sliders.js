/**
 * Oracle ATG modified the touchslider.js code from
 * https://github.com/zgrossbart/jstouchslide in the creation of this file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * @ignore 
 */
CRSMA = window.CRSMA || {};

/**
 * @namespace "Sliders" javascript Module of "CRS Mobile Application".
 * @description Holds functionality related to sliders.
 * It's based on <a href="https://github.com/zgrossbart/jstouchslide">"touchslider.js"</a> plugin code,
 * that was adjusted for CRS-M needs.<br>
 *
 * Provides single method {@link CRSMA.sliders-createSlider} with multiple options.
 */
CRSMA.sliders = function() {
  /**
   * Base slider object, based on 'jstouchslide' code.
   * @private
   */
  var baseSlider = {
    /**
     * This function just binds the touch functions to the grid.
     * It is very important to stop the default, stop the propagation and return false.
     * If we don't, then the touch events might cause the regular browser behavior for touches
     * and the screen will start sliding around.
     *
     * @param {string} pGridId
     * @param {number} pTouchSensitivity
     * @param {number} pListWidth
     * @param {number} pColWidth
     * @param {number} pParentWidth
     * @public
     */
    makeTouchable: function(/*string*/pGridId, /*int*/pTouchSensitivity, /*int*/pListWidth, /*int*/pColWidth, /*int*/pParentWidth) {
      var sliderObject = this;

      this.parentContainerWidth = pParentWidth;
      this.width = pListWidth;
      this.colWidth = pColWidth;

      var postTouchMoveApply = null;
      if (sliderObject.postTouchMove != null) {
        /** @ignore JSdoc tag doesn't add function below to the jsdoc report */
        postTouchMoveApply = function() {
          sliderObject.postTouchMove.apply(sliderObject, arguments);
        };
      }
      var postTouchEndApply = null;
      if (sliderObject.postTouchEnd != null) {
        /** @ignore JSdoc tag doesn't add function below to the jsdoc report */
        postTouchEndApply = function() {
          sliderObject.postTouchEnd.apply(sliderObject, arguments);
        };
      }

      $(pGridId).each(function() {
        sliderObject.cells = $(this).children(".cell");

        this.ontouchstart = function(pEvent) {
          sliderObject.touchStart($(this), pEvent, pTouchSensitivity);
          return true;
        };
        this.ontouchend = function(pEvent) {
          pEvent.preventDefault();
          pEvent.stopPropagation();
          if (sliderObject.sliding) {
            sliderObject.sliding = false;
            sliderObject.touchEnd($(this), pEvent, pTouchSensitivity, postTouchEndApply);
            return false;
          } else {
            // We never slide so we can just return true and perform the default touch end
            return true;
          }
        };
        this.ontouchmove = function(pEvent) {
          return sliderObject.touchMove($(this), pEvent, postTouchMoveApply);
        };
      });
    },

    /**
     * A helper to cut off the 'px' at the end of the "left" CSS attribute and parse the result as a number.
     *
     * @param p$Elem jQuery element to get and parse "left" CSS attribute.
     * @public
     */
    getLeft: function(/*JQuery*/p$Elem) {
      var attrLeft = p$Elem.css("left");
      return parseInt(attrLeft.substring(0, attrLeft.length - 2), 10);
    },

    /**
     * When the touch starts, we add our sliding class a record a few variables about where the touch started.
     * We also record the start time so we can do momentum.
     *
     * @param p$Elem JQuery element
     * @param pEvent event object
     * @public
     */
    touchStart: function(/*JQuery*/p$Elem, /*event*/pEvent) {
      $.each(this.cells, function() {
        $(this).show();
      });

      p$Elem.css("-webkit-transition-duration", "0");

      this.startX = pEvent.targetTouches[0].clientX;
      this.startY = pEvent.targetTouches[0].clientY;
      this.startLeft = this.getLeft(p$Elem);
      this.touchStartTime = new Date().getTime();
      this.touching = true;
    },

    /**
     * When the touch ends we need to adjust the grid for momentum and to snap to the grid.
     * We also need to make sure they didn't drag farther than the end of the list in either direction.
     *
     * @param p$Elem jQuery object
     * @param pEvent event object
     * @param {number} pTouchSensitivity
     * @param pCallBack function
     * @public
     */
    touchEnd: function(/*JQuery*/p$Elem, /*event*/pEvent, /*int*/pTouchSensitivity, /*function*/pCallBack) {
      if (this.getLeft(p$Elem) > 0) {
        // This means they dragged to the right past the first item
        this.doSlide(p$Elem, 0, "2s", null);

        p$Elem.parent().removeClass("sliding");
        this.startX = null;
        this.startY = null;
        this.touching = false;
      } else if ((Math.abs(this.getLeft(p$Elem)) + p$Elem.parent().width()) > this.width) {
        // This means they dragged to the left past the last item
        if ((this.width - p$Elem.parent().width()) > 0) {
          this.doSlide(p$Elem, "-" + (this.width - p$Elem.parent().width()), "2s", null);
        } else {
          this.doSlide(p$Elem, 0, "2s", null);
        }
        p$Elem.parent().removeClass("sliding");
        this.startX = null;
        this.startY = null;
        this.touching = false;
      } else {
        // This means they were just dragging within the bounds of the grid
        // and we just need to handle the momentum and snap to the grid
        this.slideMomentum(p$Elem, pEvent, pTouchSensitivity, pCallBack);
      }
    },

    /**
     * If the user drags their finger really fast we want to push the slider
     * a little farther since they were pushing a large amount.
     *
     * @param p$Elem jQuery element
     * @param pEvent event object
     * @param {number} pTouchSensitivity
     * @param pCallBack function
     * @public
     */
    slideMomentum: function(/*jQuery*/p$Elem, /*event*/pEvent, /*int*/pTouchSensitivity, /*function*/pCallBack) {
      var slideAdjust = (new Date().getTime() - this.touchStartTime) * 10;
      var leftValue = this.getLeft(p$Elem);
      var abs = Math.abs;

      // Calculate the momentum by taking the amount of time they were sliding and comparing
      // it to the distance they slide. If they slide a small distance quickly or a large
      // distance slowly then they have almost no momentum.
      // If they slide a long distance fast then they have a lot of momentum.
      var changeX = pTouchSensitivity * (abs(this.startLeft) - abs(leftValue));
      slideAdjust = Math.round(changeX / slideAdjust);
      var newLeft = slideAdjust + leftValue;

      // We need to calculate the closest column so we can figure out where to snap the grid to
      var t = newLeft % this.colWidth;
      if (abs(t) > (this.colWidth / 2)) {
        // Show the next cell
        newLeft -= (this.colWidth - abs(t));
      } else {
        // Stay on the current cell
        newLeft -= t;
      }

      var newLeftValue;
      if (this.slidingLeft) {
        // Sliding to the left
        var maxLeft = parseInt("-" + (this.width - this.parentContainerWidth), 10);
        newLeftValue = Math.max(maxLeft, newLeft);
      } else {
        // Sliding to the right
        newLeftValue = Math.min(0, newLeft);
      }
      this.doSlide(p$Elem, newLeftValue, "0.5s", pCallBack);

      p$Elem.parent().removeClass("sliding");
      this.startX = null;
      this.startY = null;
      this.touching = false;
    },

    /**
     * Slides the elem to the left position over a certain duration.
     *
     * @param p$Elem jQuery element to slide to the left.
     * @param {number} pX Number of pixels to slide to the left.
     * @param {string} pDuration Transition duration, msec.
     * @param pCallBack Function to call if not null.
     * @public
     */
    doSlide: function(/*jQuery*/p$Elem, /*int*/pX, /*string*/pDuration, /*function*/pCallBack) {
      p$Elem.css({
        left: pX + "px",
        "-webkit-transition-property": "left",
        "-webkit-transition-duration": pDuration
      });

      if (pCallBack != null) {
        pCallBack(pX, pDuration);
      }
    },

    /**
     * While they are actively dragging we just need to adjust the position of the grid
     * using the place they started and the amount they've moved.
     *
     * @param p$Elem jQuery element
     * @param pEvent event object
     * @param pCallBack callback function
     * @public
     */
    touchMove: function(/*JQuery*/p$Elem, /*event*/pEvent, /*function*/pCallBack) {
      var dx = this.startX - pEvent.targetTouches[0].clientX;
      var dy = this.startY - pEvent.targetTouches[0].clientY;
      // This is faster than calling Math.abs()
      var absDX = dx < 0 ? -dx : dx;
      var absDY = dy < 0 ? -dy : dy;
      // We need to guess so to whether the user is trying to go up and down or left or right
      if (absDX > 0 && absDX > absDY) {
        if (!this.sliding) {
          p$Elem.parent().addClass("sliding");
        }

        this.sliding = true;
        // We guess that you are sliding left or right so we need to disable the default touch actions,
        // so the page doesn't move
        pEvent.preventDefault();
        pEvent.stopPropagation();

        var leftValue;
        if (this.startX > pEvent.targetTouches[0].clientX) {
          // Sliding to the left
          leftValue = (dx - this.startLeft);
          p$Elem.css("left", "-" + leftValue + "px");
          this.slidingLeft = true;
          // We only want to attempt to re-highlight the center cell if we have moved enough that it has changed
          if (pCallBack != null && absDX >= this.colWidth) {
            pCallBack(-1 * leftValue);
          }
        } else {
          // Sliding to the right
          leftValue = (pEvent.targetTouches[0].clientX - this.startX + this.startLeft);
          p$Elem.css("left", leftValue + "px");
          this.slidingLeft = false;
          if (pCallBack != null && absDX >= this.colWidth) {
            pCallBack(leftValue);
          }
        }
        return false;
      }
      return true;
    },

    /**
     * Start by creating the sliding grid out of the specified element.
     * Look for each child with a class of cell when we create the slide panel.
     */
    createSlidePanel : function(/*string*/gridid, /*string*/cellPrefix, /*int*/numberOfCellsToDisplay, /*int*/touchSensitivity,  /*function*/onTouchEventException) {
      var parentContainerWidth = $(gridid).parent().width();
      var cellWidth = parentContainerWidth / numberOfCellsToDisplay;
      var thisObject = this;

      $(gridid).each(function() {
        $(this).css({
          'position': 'relative',
          'left': '0'
        });

        var x = 0;
        var $cells = $(this).children(".cell");
        $cells.each(function(index) {
          $(this).css({
            width: cellWidth + "px",
            height: "90%",
            position: "absolute",
            left: x + "px"
          });

          x += cellWidth;
        });

        if (numberOfCellsToDisplay == 1) {
          $cells.slice(1).hide();
        }

        try {
          document.createEvent("TouchEvent");
          // Now that we've finished the layout we'll make our panel respond to all of the touch events
          thisObject.makeTouchable(gridid, touchSensitivity, x, cellWidth, parentContainerWidth);
        } catch (e) {
          // Then we aren't on a device that supports touch
          onTouchEventException.call();
        }
      });
    }
  };

  /**
   * Creates slider with the specified options.<br>
   *
   * Options below may be specified:
   * <ul>
   *   <li><b>gridid</b> - string, identifying html container</li>
   *   <li><b>cellPrefix</b>  - string, empty by default</li>
   *   <li><b>numberOfCells</b> - number of cell to display, 4 by default</li>
   *   <li><b>touchSensitivity</b> - number (3000 by default)</li>
   *   <li><b>onTouchEventException</b> - callback function, that is called if device doesn't
   *       support 'Touch' event. Empty by default.
   *       Usually is used for changing of html-container CSS styles</li>
   *   <li><b>extension</b> - object with functions, that are added (or overrided) to the {@link CRSMA.sliders-baseSlider}</li>
   * </ul> 
   *
   * Example:
   *
   * @param options object with parameters
   * @example
   * CRSMA.sliders.createSlider({
   *   gridid  :  '#relatedItemsContainer',
   *   onTouchEventException : function(el) {
   *     $(el).css({
   *       'height'  : '70px',
   *       'overflow': 'auto'
   *     });
   *   }
   * });
   *
   * @public
   */
  var createSlider = function(options) {
    var settings = $.extend({/* default settings */
      gridid                : '',
      cellPrefix            : '',
      numberOfCells         : 4,
      touchSensitivity      : 3000,
      onTouchEventException : function(/*html element*/el) {/* empty handler for devices, not supporting Touch events */},
      extension             : {},
    }, options || {});

    var sliderClass = Object.create(baseSlider, settings.extension);
    sliderClass.createSlidePanel(settings.gridid, settings.cellPrefix, settings.numberOfCells,
        settings.touchSensitivity, settings.onTouchEventException);
    return sliderClass;
  };

  /**
   * List of "CRSMA.sliders" public methods.
   */
  return {
    'createSlider' : createSlider
  }
}();
