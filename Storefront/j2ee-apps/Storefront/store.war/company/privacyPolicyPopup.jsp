<%--
  This page displays Privacy Policy as a popup. 

  Required Parameters:
    None
    
  Optional Parameters:
    None

--%>
<dsp:page>
  <fmt:message var="pageTitle" key="company_privacy.title"/>
  
  <crs:popupPageContainer pageTitle="${pageTitle}">
    <jsp:body>
            
      <fmt:message var="introText" key="company_privacyPolicyPopup.text">
        <fmt:param>
          <crs:outMessage key="common.storeName"/>
        </fmt:param>
      </fmt:message>
      
      <dsp:include page="/global/gadgets/popupPageIntro.jsp">
        <dsp:param name="divId" value="atg_store_privacyPolicy"/>
        <dsp:param name="titleKey" value="company_privacy.title"/>
        <dsp:param name="textString" value="${introText}"/>
        <dsp:param name="useCloseButton" value="true"/>
      </dsp:include>

    </jsp:body>
  </crs:popupPageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/company/privacyPolicyPopup.jsp#1 $$Change: 735822 $ --%>
