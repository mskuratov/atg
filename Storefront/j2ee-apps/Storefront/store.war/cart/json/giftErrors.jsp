<%@ page contentType="application/json; charset=UTF-8" import="atg.servlet.*,atg.nucleus.*"%>

<%--
  This page renders any GiftWithPurchaseFormHandler formExceptions.
  
  Required Parameters:
    None.
--%>

<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
  
  <json:object>
    <json:property name="error" value="true"/>
    <%-- The real error URL that the server would have redirected to if the request wasn't AJAX --%>
    <json:property name="errorUrl" escapeXml="false">
      <dsp:valueof bean="/atg/commerce/promotion/GiftWithPurchaseFormHandler.makeGiftSelectionErrorURL" valueishtml="true"/>
    </json:property>
    <json:array name="errors">   
      <%--  
        ErrorMessageForEach droplet takes a list of exceptions and renders the text of 
        an error message for each exception, by translating the message and propertyName 
        properties of the exception.
        
        Input Parameters:
          exceptions
            The list of all exceptions that have occurred.
        
        Open Parameters:
          output
            This parameter is rendered for once for each element in the array.
        
        Output Parameters:
          message
            The translated error message.
      --%>
      <dsp:droplet name="ErrorMessageForEach">
        <dsp:param param="/atg/commerce/promotion/GiftWithPurchaseFormHandler.formExceptions" name="exceptions"/>        
        <dsp:oparam name="output">
          <json:property>
            <%-- Render Formhandler Errors --%>
            <dsp:valueof param="message" valueishtml="true"/>
          </json:property>
        </dsp:oparam>
      </dsp:droplet>

    </json:array>
  </json:object>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/cart/json/giftErrors.jsp#1 $$Change: 735822 $--%>
