<%--
  "SearchBox" cartridge renderer (mobile version).
  Renders a search box which allows the user to query for search results.

  Optional parameters:
    Ntt 
      Search term (Endeca parameter).
--%>
<dsp:page>
  <%-- Request parameters - to variables --%>
  <dsp:getvalueof var="valueNtt" param="Ntt"/>

  <%-- "Search" block --%>
  <div class="searchBar">
    <%-- ========== "Search" form ========== --%>
    <dsp:form action="${siteContextPath}${navigationActionPath}" name="searchForm" id="searchForm">
      <input type="hidden" name="Dy" value="1"/>
      <input type="hidden" name="Nty" value="1"/>

      <fmt:message var="hintText" key="mobile.common.button.searchText"/>
      <input id="searchText" class="searchText" name="Ntt" type="text" autocomplete="off" placeholder="${hintText}" autocorrect="off"
        aria-label="${hintText}" value="${not empty valueNtt ? valueNtt : ''}" onfocus="CRSMA.search.searchOnFocus()"/>
    </dsp:form>
  </div>
  <c:if test="${contentItem.autoSuggestEnabled}">
    <script>
      $(document).ready(function(event) {
        CRSMA.autosuggest.init({
          searchTextId: "searchText",
          minAutoSuggestInputLength: "${contentItem.minAutoSuggestInputLength}",
          autoSuggestServiceUrl: "${siteBaseURL}/assembler",
          collection: "${contentItem.contentCollection}",
          containerClass: "dimSearchSuggContainer",
          siteContextPath: "${siteContextPath}"
        });
      });
    </script>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/mobile/cartridges/SearchBox/SearchBox.jsp#3 $$Change: 788278 $--%>
