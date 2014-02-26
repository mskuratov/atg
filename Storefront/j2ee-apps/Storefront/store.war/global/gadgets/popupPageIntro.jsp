<%--  This used to create the intro header for all popup Pages

  This page expects the following input parameters:
  1. divId - ID for the containing div
  2. titleKey (optional) - Resource bundle key for the title
  3. textKey (optional) - Resource bundle key for the introduction text
  4. titleString (optional) - Title text passed from the page being displayed
  5. textString (optional) - Introduction text passed from the page being displayed
  6. useCloseButton(optional)- true or false, Default value - false.  Determines if the close button
  is displayed for use in closing the popup window.
    
  Text is rendered according to the following rules:
   
  Case 1 Both titleKey and titleString are supplied: 
       Renders the value from the resource bundle.  If the key is not valid or has no associated value,
       the value passed in titleString is rendered.
  Case 2 Only titleKey is supplied:
       Renders the value from the resource bundle. If the key is not valid or empty nothing is displayed.
  Case 3 Only titleString is supplied:
       Renders the value as passed. 

  The same rules apply to textKey and textString if they're supplied.
  If neither textKey or textString is supplied, no introductory text is rendered.
  --%>

<dsp:page>
  <dsp:getvalueof id="divId" param="divId"/>
  <dsp:getvalueof id="titleKey" param="titleKey"/>
  <dsp:getvalueof id="textKey" param="textKey"/>
  <dsp:getvalueof id="titleString" param="titleString"/>
  <dsp:getvalueof id="textString" param="textString"/>
  <dsp:getvalueof id="useCloseButton" param="useCloseButton" />
  <c:if test="${empty useCloseButton }">
    <c:set var="useCloseButton" value="false"/>
  </c:if>  
  
  <%-- Displaying title and text --%>
  <div id="${divId}">
    <crs:messageWithDefault key="${titleKey}" string="${titleString}"/>
    <c:if test="${!empty messageText}">
      <h2 class="title">
        ${messageText}
      </h2>
    </c:if>
    <crs:messageWithDefault key="${textKey}" string="${textString}"/>
    <c:if test="${!empty messageText}">

        <p>
          ${messageText}
        </p>
      
    </c:if>
  </div>    
    

  <c:if test="${useCloseButton}">
    <fmt:message var="closeButtonText" key="common.closeWindowText"/>
    <div align="center" id="atg_store_popupCloseButton">
      <dsp:a href="javascript:window.close();" iclass="atg_store_basicButton" onclick="window.close();">
        <span>${closeButtonText}</span>
      </dsp:a>
    </div>
  </c:if>
   
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/popupPageIntro.jsp#1 $$Change: 735822 $ --%>
