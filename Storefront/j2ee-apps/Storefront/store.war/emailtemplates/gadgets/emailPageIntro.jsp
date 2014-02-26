<%-- 
  This gadget renderes text according to the following rules:
   
  Case 1. Both titleKey and titleString are supplied: 
    Renders the value from the resource bundle.  If the key is not valid or has no associated value,
    the value passed in titleString is rendered.
  Case 2. Only titleKey is supplied:
    Renders the value from the resource bundle. If the key is not valid or empty nothing is displayed.
  Case 3. Only titleString is supplied:
    Renders the value as passed. 

  The same rules apply to textKey and textString if they're supplied.
  If neither textKey or textString is supplied, no introductory text is rendered.
  
  Required parameters:
    divId
      ID for the containing DIV
  
  Optional parameters (one of them is required):
    titleKey
      Resource bundle key for the title
    textKey
      Resource bundle key for the introduction text
    titleString
      Title text passed from the page being displayed
    textString
      Introduction text passed from the page being displayed
--%>
<dsp:page>
  
  <dsp:getvalueof id="divId" param="divId"/>
  <dsp:getvalueof id="titleKey" param="titleKey"/>
  <dsp:getvalueof id="textKey" param="textKey"/>
  <dsp:getvalueof id="titleString" param="titleString"/>
  <dsp:getvalueof id="textString" param="textString"/>
  
  <div id="${divId}">
    <%--
      If the 'titleKey' resource key is not passed use a title that is supplied 
      through 'titleString' parameter.
     --%>
    <crs:messageWithDefault key="${titleKey}" string="${titleString}"/>
    
    <%-- Display Page Title if it's not empty--%>
    <c:if test="${!empty messageText}">
      <div style="font-family:Tahoma,Arial,sans-serif;font-size:20px;color:#0a3d56;padding-bottom:30px;">
        ${messageText}
      </div>
    </c:if>
    
    <%-- Display introduction text if not empty--%>    
    <crs:messageWithDefault key="${textKey}" string="${textString}"/>
    <c:if test="${!empty messageText}">
      <%-- Begin Page Message --%>
      <div style="color:#666;font-family:Verdana,Arial,sans-serif;font-size:14px">
        ${messageText}
      </div>
    </c:if>
  </div>
    
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/emailPageIntro.jsp#1 $$Change: 735822 $ --%>
