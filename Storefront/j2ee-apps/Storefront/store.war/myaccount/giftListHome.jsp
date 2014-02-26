<%--
  This layout page renders all gift lists associated with a profile and site/site group 
  along with form for adding new gift list.
  
  The page includes two main gadgets:
    gadgets/giftListList.jsp - for rendering all gift lists associated with a profile.
    gadgets/giftListAddEdit.jsp - for rendering form for adding a new gift list.
    
  Required parameters:
    None
    
  Optional parameters:
    None
--%>
<dsp:page>

  <dsp:importbean bean="/atg/commerce/gifts/GiftlistFormHandler"/>
  
  <fmt:message var="saveText" key="myaccount_giftListAdd.saveGiftList" />

  <crs:pageContainer divId="atg_store_giftListIntro"
                     index="false" follow="false"
                     bodyClass="atg_store_myAccountPage atg_store_leftCol"
                     selpage="GIFT LISTS">
                     
    <jsp:attribute name="formErrorsRenderer">
      <dsp:include page="gadgets/myAccountErrorMessage.jsp">
        <dsp:param name="formHandler" bean="GiftlistFormHandler" />
        <dsp:param name="submitFieldText" value="${saveText}"/>
      </dsp:include>
    </jsp:attribute>
                     
    <jsp:body>
      <div id="atg_store_contentHeader">
        <h2 class="title">
          <fmt:message key="myaccount_giftlist.title"/>
        </h2>
      </div>
      
      <%-- 
        Include 'My Account' left side menu that provides navigation links 
        between account configuration pages. 
        --%>
      <dsp:include page="gadgets/myAccountMenu.jsp">
        <dsp:param name="selpage" value="GIFT LISTS" />
      </dsp:include>
      
      <div class="atg_store_main atg_store_myAccount">
        <div class="content">

          <%-- 
            Display error messages for GiftlistFormHandler if some errors occurred during
            adding or removing gift list. 
            --%>
          <div id="atg_store_formValidationError">
            <%-- The error message should display a 'save gift list' button as we are adding. --%>            
            <dsp:include page="gadgets/myAccountErrorMessage.jsp">
              <dsp:param name="formHandler" bean="GiftlistFormHandler" />
              <dsp:param name="submitFieldText" value="${saveText}"/>
            </dsp:include>
          </div>

          <%-- 
            Include the list of gift lists, this allows editing
            and removing of a gift list
            --%>
          <dsp:include page="gadgets/giftListList.jsp" />

          <%-- 
            Include the page that allows creating a new gift list
           --%>
          <dsp:form action="giftListHome.jsp" method="post" formid="atg_store_giftListAddForm">
            <dsp:include page="gadgets/giftListAddEdit.jsp">
              <dsp:param name="gadgetTitle" value="myaccount_giftListAdd.addGiftList" />
            </dsp:include>
          </dsp:form>
        </div>
      </div>
    </jsp:body>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/giftListHome.jsp#1 $$Change: 735822 $ --%>
