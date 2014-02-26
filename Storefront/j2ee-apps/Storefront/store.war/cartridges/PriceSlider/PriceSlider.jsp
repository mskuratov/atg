<%-- 
  This page lays out the elements that make up the PriceSlider.
    
  Required Parameters:
    contentItem
      The content parameter represents an unselected dimension refinement.
   
  Optional Parameters:

--%>
<dsp:page>
  
  <dsp:getvalueof var="contextPath" vartype="java.lang.String"  bean="/OriginatingRequest.contextPath"/>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  
  <dsp:getvalueof var="contentItem" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/> 
  
  <%-- 
    The slider is a range filter not an Endeca dimension refinement so we need
    to use a custom method to determine if we should render the control.
  --%>
  <dsp:getvalueof var="enabled" value="${contentItem.enabled}"/>
  <c:if test="${enabled}">

    <div class="atg_store_facetsGroup_options_catsub">
      <h5><fmt:message key="common.price"/></h5>
      <div class="atg_store_priceSliderSelectedRange"><span id="minPrice"><dsp:include page="/global/gadgets/formattedPrice.jsp"><dsp:param name="price" value="${contentItem.sliderMin}"/></dsp:include></span> - <span id="maxPrice"><dsp:include page="/global/gadgets/formattedPrice.jsp"><dsp:param name="price" value="${contentItem.sliderMax}"/></dsp:include></span></div>

      <script type="text/javascript">
        dojo.ready(function(){

        // min selectable price... i.e. first value on slider range
        var minPrice = ${contentItem.sliderMin};  
      
        // max selectable price... i.e. last value on slider range
        var maxPrice = ${contentItem.sliderMax}; 

        // the number of "stop" points for the slider handles
        // ... ensure that slider moves in increments of 1
        var discreteValues = (maxPrice - minPrice) + 1;

        // the position of the min handle, if user has not
        // previously selected a min value then set to minPrice (start)
        var lowerBound = parseInt("${contentItem.filterCrumb.lowerBound}", 10) || minPrice;

        // the position of the max handle, if user has not
        // previously selected a max value then set to maxPrice (end)
        var upperBound = parseInt("${contentItem.filterCrumb.upperBound}", 10) || maxPrice;

        // lower and upper bound value sanity checks
        // ... ensure that the position of the min and
        // max handles are within the allowable range
        if (lowerBound < minPrice){
          lowerBound = minPrice;
        }
        if (upperBound > maxPrice){
          upperBound = maxPrice;
        }

        // extend HorizontalRangerSlider with
        // custom properties and methods
        dojo.extend(dojox.form.HorizontalRangeSlider, {
          isMouseDown: false,
          connections: new Array(),
          currentMinValue: lowerBound,
          currentMaxValue: upperBound,
          hasValueChanged: false,
          setMouseDownAttr: function(){
            this.isMouseDown = true;
          },
          toggleMinPriceLabel: function(){
            dojo.query("#minPrice").toggleClass("highlight");
          },
          toggleMaxPriceLabel: function(){
            dojo.query("#maxPrice").toggleClass("highlight");
          },
          toggleBothMinAndMaxPriceLabel: function(){
            this.toggleMinPriceLabel();
            this.toggleMaxPriceLabel();
          },
          updateOnScreenValues: function(newMinPrice, newMaxPrice){
            // NOTE: assuming min/max label text is currency symbol followed by number
            // or number followed by currency symbol. eg $120 or 120€ The regex below
            // replaces only the number part and decimal/comma formatting of the string
            // with selected value, leaving the currency symbol in its current position
            
            // get a reference to the onscreen minPrice and maxPrice labels
            // these labels contain the price formatted with currency symbol
            var minPriceElement = dojo.byId("minPrice");
            var maxPriceElement = dojo.byId("maxPrice");
            
            // update the min and max values, removing all formatting but currency symbol
            var minWithCurrencySymbol = dojo.trim(minPriceElement.innerHTML.replace(/[\d\.,]+/,  newMinPrice));
            var maxWithCurrencySymbol = dojo.trim(maxPriceElement.innerHTML.replace(/[\d\.,]+/,  newMaxPrice));

            // update the on screen min and max labels
            minPriceElement.innerHTML = minWithCurrencySymbol + " "; // append a single space to handle an IE8 bug
            maxPriceElement.innerHTML = maxWithCurrencySymbol;

            // update the min handle, range bar and max hande tooltip text
            dojo.attr(this.sliderHandle, "title", minWithCurrencySymbol);
            dojo.attr(this.progressBar, "title", minWithCurrencySymbol + " - " + maxWithCurrencySymbol);
            dojo.attr(this.sliderHandleMax, "title", maxWithCurrencySymbol);

          },
          addConnections: function(){
            // focus event on the min selector handle
            this.connections.push(
              dojo.connect(this.sliderHandle, "focus", this, this.toggleMinPriceLabel)
            );

            // blur event on the min selector handle
            this.connections.push(
              dojo.connect(this.sliderHandle, "blur", this, this.toggleMinPriceLabel)
            );

            // focus event on the max selector handle
            this.connections.push(
              dojo.connect(this.sliderHandleMax, "focus", this, this.toggleMaxPriceLabel)
            );

            // blur event on the max selector handle
            this.connections.push(
              dojo.connect(this.sliderHandleMax, "blur", this, this.toggleMaxPriceLabel)
            );

            // focus event on the range bar handle
            this.connections.push(
              dojo.connect(this.progressBar, "focus", this, this.toggleBothMinAndMaxPriceLabel)
            );

            // blur event on the range bar handle
            this.connections.push(
              dojo.connect(this.progressBar, "blur", this, this.toggleBothMinAndMaxPriceLabel)
            );
          
            // NOTE: onMouseDown event is not firing on the slider 
            // widget (Dojo bug?) so wiring up mouseDown event handlers
            // for min handle, range bar and max handle using dojo.connect

            // mouse down on the min selector handle  
            this.connections.push(
              dojo.connect(this.sliderHandle, "mousedown", this, this.setMouseDownAttr)
            );

            // mouse down on the range bar handle  
            this.connections.push(
              dojo.connect(this.progressBar, "mousedown", this, this.setMouseDownAttr)
            );

            // mouse down on the max selector handle  
            this.connections.push(
              dojo.connect(this.sliderHandleMax, "mousedown", this, this.setMouseDownAttr)
            );

            // need to handle scenario where the mouse
            // button is released when the mouse
            // is not over the widget, so trap mouse
            // up event on the page document element
            this.connections.push(
              dojo.connect(dojo.doc, "mouseup", this, function(evt){
                if (this.isMouseDown){
                  // the user has preiously clicked on the price
                  // slider widget, so regardless of where they
                  // released the mouse the widget's mouse up
                  // event handler needs to be called
                  this.onMouseUp();
                }
              })
            );
          },
          deleteConnections: function(){
            dojo.forEach(connections, dojo.disconnect);
          }
        });

        var hRangeSlider = new dojox.form.HorizontalRangeSlider({
          name: "rangeSlider",
          value: [lowerBound,upperBound],
          minimum: minPrice,
          maximum: maxPrice,
          intermediateChanges: true,
          discreteValues: discreteValues,
          showButtons: false,
          pageIncrement: 10,
          style: "width:140px;margin-left:27px;height:26px;",
          onChange: function(value){

            /* START FIX */
            if(!dojo.isArray(value)){
              // the slider widget value should contain an array of
              // two values (the selected min value and the selected max value)
              // however, under certain circumstances this.value changes from
              // an array of two values to an integer, which results in an
              // unhandled exception. If the value does not contain an array
              // simply reset to the last known good values.
              this.value = value = [this.currentMinValue, this.currentMaxValue];
            }
            /* END FIX */

            this.updateOnScreenValues(value[0], value[1]);

            // store the selected min and max values
            this.currentMinValue =  value[0];
            this.currentMaxValue = value[1];
          
            // We only want to post the request when a lower or upper value has actually changed.
            // For example, when the user moves a lower or upper slider to another value, then back 
            // to the original value in a single movement, don't post.
            if ((lowerBound != this.currentMinValue) || (upperBound != this.currentMaxValue)) {
              this.hasValueChanged = true;
            }
            else {
              this.hasValueChanged = false;
            }
          },
          onMouseUp: function(){
            if (this.hasValueChanged){

              // build new url with the selected min and max prices
              // then load the new url

              var uri = decodeURIComponent(window.location.href.replace(/\+/g,  " "));
              var query;

              if (uri.indexOf("?")==-1){
                // url DOES NOT contain any querystring parameters
                // simply add new price range querystring parameter
                query = {
                  Nf: "${contentItem.priceProperty}|BTWN+" + this.currentMinValue + "+" + this.currentMaxValue
                };
              }
              else{
                // url DOES contain querystring parameters
                // keep existing querystring parameters AND
                // add (or replace) price range querystring
                query = dojo.queryToObject(uri.substring(uri.indexOf("?") + 1, uri.length));

                query.Nf = "${contentItem.priceProperty}|BTWN+" + this.currentMinValue + "+" + this.currentMaxValue;
              
                // remove existing querystring values from the url string
                uri = uri.substring(0, uri.indexOf("?"));
              }

              // append querystring parameters to the url
              uri += "?" + dojo.objectToQuery(query);

              // disable the price range widget to prevent
              // any further user interaction until the page
              // has been reloaded
              this.set('disabled', 'true');

              // about to reload page, so remove any existing event handlers
              this.deleteConnections;

              // reload the page
              window.location.href = uri;
            }
          },
          postCreate: function(){
            // postCreate on the superclass should be called automatically - but it isn't.
            // Perhaps an issue with how the required Dojo scripts are being pulled in.
            // Regardless, calling directly: 
            dojox.form.HorizontalRangeSlider.prototype.postCreate.apply(this, arguments);

            // hook up event handlers
            this.addConnections();

            this.updateOnScreenValues(this.currentMinValue, this.currentMaxValue);
          }
        }, "rangeSlider");
      });
      </script>
      <div id="rangeSlider"></div>
    </div>
  </c:if>

</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cartridges/PriceSlider/PriceSlider.jsp#4 $$Change: 794795 $--%>
