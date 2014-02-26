<%--
  This tag renders checkout progress bar (if needed) and page title.
  It also renders a checkout order details area. Then this tag renders its body.

  Required attributes:
    title
      Page title to be displayed.
    currentStage
      Specifies a stage of the checkout progress. Different stages display different Checkout Progress bars.
      OOTB valid values are 'giftMessage', 'login', 'shipping', 'billing', 'confirm' and 'success'.

  Optional attributes:
    formErrorsRenderer
      JSP fragment for rendering errors, invoked before rendering tag's body. 
--%>

<%@ include file="/includes/taglibs.jspf" %>
<%@ include file="/includes/context.jspf" %>
<%@ attribute name="currentStage" %>
<%@ attribute name="title" %>
<%@ attribute name="formErrorsRenderer" fragment="true"%>

<div id="atg_store_contentHeader">
  <div id="atg_store_checkoutProgressContainer">
    <h2 class="title">${title}</h2>

    <dsp:include page="/checkout/gadgets/checkoutProgress.jsp">
      <dsp:param name="currentStage" value="${currentStage}"/>
    </dsp:include>
  </div>
</div>

<%-- Display any error messages. --%>
<jsp:invoke fragment="formErrorsRenderer"/>

<jsp:doBody/>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/main/storefront/j2ee/storefront.war/global/gadgets/pageIntro.jsp#1 $$Change: 524649 $ --%>