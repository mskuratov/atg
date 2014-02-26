<%--
  This gadget displays all form handler exceptions wrapped into a single <div> tag.

  Required parameters:
    formHandler
      A form handler bean instance, all exceptions will be taken from this bean.

  Optional parameters:
    divid
      Error messages will be wrapped into a <div> with id attribute set to this parameter value.
--%>

<dsp:page>
  <dsp:getvalueof id="divid" param="divid"/>
  <c:if test="${empty divid}">
    <%-- Set default divid parameter value. --%>
    <c:set var="divid" value="atg_store_errorMsg"/>
  </c:if>

  <dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

  <%--
    This droplet iterates over all form handler's exceptions.

    Input parameters:
      exceptions
        Collection of form handler's exceptions.

    Output parameters:
      message
        Current exception's message.

    Open parameters:
      outputStart
        Rendered before iteration is started.
      output
        Rendered for each iteration row.
      outputEnd
        Rendered after iteration is done.
  --%>
  <dsp:droplet name="ErrorMessageForEach">
    <dsp:param param="formHandler.formExceptions" name="exceptions"/>
    <dsp:oparam name="outputStart">
      <div class="${divid}">
    </dsp:oparam>
    <dsp:oparam name="output">
      <p>
        <dsp:valueof param="message" valueishtml="true"/>
      </p>
    </dsp:oparam>
    <dsp:oparam name="outputEnd">
      </div>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/global/gadgets/errorMessage.jsp#1 $$Change: 735822 $--%>
