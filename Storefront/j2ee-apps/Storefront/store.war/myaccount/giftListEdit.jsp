<%--
  Layout page for updating gift list information.
  
  Required parameters:
    giftlistId
      ID of giftList to be edited.
      
  Optional parameters:
    None.
--%>
<dsp:page>

  <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>

  <%-- Set title for submit button --%>
  <fmt:message  var="saveText" key="common.button.saveChanges"/>
  
  <crs:pageContainer divId="atg_store_giftListIntro"
                     index="false" follow="false"
                     bodyClass="atg_store_myAccountPage atg_store_leftCol"
                     selpage="GIFT LISTS">
                     
    <jsp:attribute name="formErrorsRenderer">
      <%-- Display error messages if any above the accessibility navigation--%>  
      <dsp:include page="gadgets/myAccountErrorMessage.jsp">
        <dsp:param name="formHandler" bean="GiftlistFormHandler" />
        <dsp:param name="submitFieldText" value="${saveText}"/>
      </dsp:include>
    </jsp:attribute>                 
    
    <jsp:body>
      <div id="atg_store_contentHeader"> 
        <h2 class="title">
          <fmt:message key="myaccount_giftListAdd.editGiftList"/>
        </h2>
      </div>
      
      <%-- 
        Include 'My Account' left side menu that provides navigation links 
        between account configuration pages.
       --%>
      <dsp:include page="gadgets/myAccountMenu.jsp">
        <dsp:param name="selpage" value="GIFT LISTS"/>
      </dsp:include>
      
      <div class="atg_store_main atg_store_myAccount">

        <%-- 
          Display error messages for GiftlistFormHandler if some errors occurred during
          adding or removing gift list.
         --%>
        <div id="atg_store_formValidationError">
          <%--
            The error message should display a 'save changes' button as we are editing. 
           --%>
          <dsp:include page="gadgets/myAccountErrorMessage.jsp">
            <dsp:param name="formHandler" bean="GiftlistFormHandler" />
            <dsp:param name="submitFieldText" value="${saveText}"/>
          </dsp:include>
        </div>

        <%-- Edit gift list form --%>
        <dsp:form formid="giftlist" action="giftListEdit.jsp" method="post">
          
          <%-- Edit gift list form inputs --%>
          <dsp:include page="gadgets/giftListAddEdit.jsp" >
            <dsp:param name="giftlistId" param="giftlistId" />
          </dsp:include>

          <%-- List of gift list items with possibility to remove them or add to cart. --%>
          <dsp:include page="/myaccount/gadgets/giftListManage.jsp">
            <dsp:param name="giftlistId" param="giftlistId"/>
          </dsp:include>
        </dsp:form>
      </div>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/giftListEdit.jsp#1 $$Change: 735822 $ --%>