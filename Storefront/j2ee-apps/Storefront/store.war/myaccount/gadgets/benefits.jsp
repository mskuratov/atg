<%--
  This page display benefits available for the 
  registered users.
  
  Required parameters:
    None
    
  Optional parameters:
    None  
 --%>
<dsp:page>
  
  <dsp:importbean bean="/atg/store/droplet/StoreText"/>
  
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" bean="/OriginatingRequest.contextPath"/>
  
  <%-- Benefits section --%>
  <div class="atg_store_registrationBenefits aside">
  
    <%-- Benefits section header --%>
    <h3>
      <fmt:message key="myaccount_registration.Benefits"/>
    </h3>
    
    <%-- List of benefits --%>
    
    <dsp:droplet name="StoreText">
      <dsp:param name="key" value="benefitsList"/>
      
      <dsp:oparam name="outputStart">
        <ul>
      </dsp:oparam>
      
      <dsp:oparam name="output">
        <li>
          <dsp:getvalueof var="message" param="message"/>
          <c:out value="${message}" escapeXml="false"/>
        </li>
      </dsp:oparam>
      
      <dsp:oparam name="outputEnd">
        </ul>
      </dsp:oparam>
    </dsp:droplet>
    
    <%-- Link to the privacy policy popup page --%>
    <fmt:message var="privacyPolicyTitle" key="common.button.privacyPolicyTitle"/>
    <dsp:a href="${contextPath}/company/privacyPolicyPopup.jsp"
           iclass="atg_store_benefitsPopUp" 
           target="popup" title="${privacyPolicyTitle}">
      <fmt:message key="common.button.privacyPolicyText"/>
    </dsp:a>
  </div>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/benefits.jsp#1 $$Change: 735822 $--%>
