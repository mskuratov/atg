<%--
  This JSP renders the product comparisons page. It can be navigated to by 
  selecting the comparisons link on a product detail page or on the cart page.
  Its used to compare properties and features of different products that currently
  exist in the product comparisons list.
  
  Required Properties:
    None
  
  Optional Properties:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest"/>
  <dsp:importbean bean="/atg/store/collections/filter/ColorSorter"/>
  <dsp:importbean bean="/atg/store/collections/filter/SizeSorter"/>
  <dsp:importbean bean="/atg/commerce/catalog/comparison/ProductList"/>
  <dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
  <dsp:importbean bean="/atg/dynamo/droplet/multisite/SharingSitesDroplet" />

  <%-- This is a top level page, so wrap the content in the pageContainer tag --%>
  <crs:pageContainer divId="atg_store_productComparisonsIntro" titleKey=""
                     index="false" follow="false" bodyClass="atg_store_comparison"
                     selpage="COMPARISONS">
    
    <%-- URL used by 'remove item' forms --%>
    <c:url value="productComparisons.jsp" var="comparisonsUrl" scope="page"/>
        
    <%-- Hero image title --%>
    <div id="atg_store_contentHeader">
      <h2 class="title">
        <fmt:message key="browse_productComparisons.title"/>
      </h2>
    </div>
    
    <%-- Get the items in the product list --%>
    <dsp:getvalueof var="productListItems" bean="ProductList.items"/>
    
    <c:choose>
      <%-- We have products in the comparisons list so render them --%>
      <c:when test="${not empty productListItems}">
        <div id="atg_store_productComparisons">            
          <table summary="" role="presentation">            
            <%-- Render Product Images --%>
            <tr>
              <c:forEach var="comparisonListItem" items="${productListItems}" varStatus="status">
                <td>
                  <div align="center">           
                    <table summary="" role="presentation">  
                      <tr>
                        <dsp:param name="comparisonListItem" value="${comparisonListItem}" />
                        <td class="image">
                          <dsp:include page="/browse/gadgets/productImgCart.jsp">
                            <dsp:param name="product" param="comparisonListItem.product" />
                            <dsp:param name="siteId" param="comparisonListItem.siteId"/>
                          </dsp:include>
                        </td>                       
                      </tr>
        
                      <%-- Render product names --%>
                      <tr>             
                        <dsp:param name="comparisonListItem" value="${comparisonListItem}" />
                        <td class="atg_store_comparisonsTitle">
                          <dsp:include page="/browse/gadgets/productName.jsp">
                            <dsp:param name="product" param="comparisonListItem.product" />
                            <dsp:param name="siteId" param="comparisonListItem.siteId"/>
                          </dsp:include>
                        </td>         
                      </tr>
            
                      <%-- Render product prices --%>     
                      <tr>             
                        <dsp:param name="comparisonListItem" value="${comparisonListItem}" />
                        <td class="atg_store_productPrice">
                          <dsp:include page="/global/gadgets/priceRange.jsp">
                            <dsp:param name="product" param="comparisonListItem.product" />                            
                          </dsp:include>                        
                        </td>
                      </tr>
            
                      <%-- Action buttons --%>
                      <tr>    
                        <dsp:param name="comparisonListItem" value="${comparisonListItem}" />
                        <td class="atg_store_comparisonActions">
                          <%--
                            If there is one childSku for this particular product render
                            the Add to Cart button, otherwise render the View Product 
                            button which will take the user to the product details page
                          --%>
                          <dsp:getvalueof var="childSKUs" param="comparisonListItem.product.childSKUs" />
                          <c:choose>
                          <%-- One Sku belonging to this product --%>
                          <c:when test="${fn:length(childSKUs) == 1}">
                            <%-- Size is one, show Add to Cart --%>
                            <dsp:include page="/comparisons/gadgets/productAddToCart.jsp">
                              <dsp:param name="siteId" param="comparisonListItem.siteId"/>
                              <dsp:param name="product" param="comparisonListItem.product"/>
                              <dsp:param name="sku" param="comparisonListItem.product.childSKUs[0]"/>
                              <dsp:param name="displayAvailability" value="true"/>
                            </dsp:include>
                          </c:when>
                          <%-- Size is not one, show View Details --%>
                          <c:otherwise>                          
                            <dsp:getvalueof var="productLink" param="comparisonListItem.productLink" />
                            <c:if test="${not empty productLink}">
                              <%-- Product Template is set --%>
                              <dsp:a href="${productLink}" iclass="atg_store_basicButton">
                                <span class="atg_store_comparison_viewDetailsButtonSpan">
                                  <fmt:message key="common.viewDetails"/>
                                </span>
                              </dsp:a>
                            </c:if>
                          </c:otherwise>
                        </c:choose>

                        <%-- 
                          A remove button for a single product. When clicked the form is submitted and
                          will remove a single product from the comparisons list whose productId is set
                          in the form handlers productId property. The form must have a unique name as
                          there is a remove button for each product. 
                        --%>
                        <dsp:form name="remove_${comparisonListItem.product.repositoryId}" 
                                  action="${comparisonsUrl}" method="post" formid="removeProduct">
                          <dsp:input bean="ProductListHandler.removeProductSuccessURL" type="hidden"
                                     value="${comparisonsUrl}" />
                          <dsp:input bean="ProductListHandler.removeProductErrorURL" type="hidden"
                                     value="${comparisonsUrl}" />
                          <dsp:input bean="ProductListHandler.productID" type="hidden"
                                     paramvalue="comparisonListItem.product.repositoryId"/>
                          <dsp:input bean="ProductListHandler.siteID" type="hidden"
                                     paramvalue="comparisonListItem.siteId"/>
                          <dsp:input bean="ProductListHandler.skuID" type="hidden"
                                     paramvalue="comparisonListItem.sku.repositoryId" />
                          <dsp:input bean="ProductListHandler.categoryID" type="hidden"
                                     paramvalue="comparisonListItem.category.repositoryId" />
                          <%-- Remove button --%>
                          <fmt:message var="removeButtonText" key="common.button.removeText" />
                            <dsp:input bean="ProductListHandler.removeProduct" type="submit"
                                     value="${removeButtonText}" 
                                     iclass="atg_store_textButton atg_store_compareRemove"/>
                      </dsp:form>
                    </td>           
                  </tr>
            
                  <%--
                    Render the products site name if the current site does not match the
                    products site, this name indicates the site the product was added from
                  --%>
                  <tr>
                      <dsp:param name="comparisonListItem" value="${comparisonListItem}" />
                      <td class="atg_store_compareSite">
                        <dsp:getvalueof var="currentSiteId" vartype="java.lang.String" 
                                        bean="/atg/multisite/Site.id"/>
                        <dsp:getvalueof var="elementSiteId" vartype="java.lang.String" 
                                        param="comparisonListItem.siteId"/>
                        <c:if test="${currentSiteId != elementSiteId}">
                          <dsp:include page="/global/gadgets/siteIndicator.jsp">
                            <dsp:param name="mode" value="name"/>              
                            <dsp:param name="siteId" value="${elementSiteId}"/>
                            <dsp:param name="product" value="${comparisonListItem.product}"/>
                          </dsp:include>
                        </c:if>
                      </td>               
                    </tr>
                  </table>
                </div>
              </c:forEach> 
            
            </tr>
            
            <tr>
              <td class="atg_store_tableLine" colspan="${fn:length(productListItems)}"/>
            </tr>
            
            <%-- Render all product properties --%>
            <dsp:include page="/comparisons/gadgets/productChildDisplay.jsp">
              <dsp:param name="properties" 
                         bean="/atg/commerce/catalog/CatalogTools.propertyToLabelMap"/>
            </dsp:include>

            <%-- Render product features --%>
            <dsp:include page="/comparisons/gadgets/productFeatures.jsp"/>          
            
          </table>
        </div>
        
        <%-- 'Remove All' and 'Continue Shopping' buttons  --%>
        <div class="atg_store_formActions">
          <div id="atg_store_productComparisonsRemoveAll">
            
            <%-- 
              Remove All button. When this form is submitted all items in the product comparisons
              list are removed 
            --%>
            <dsp:form name="removeall" action="${comparisonsUrl}" method="post" formid="removeAll">
              <fmt:message var="removeAllButtonText" key="common.button.removeAllText"/>
              <dsp:input bean="ProductListHandler.clearList" type="submit" 
                         value="${removeAllButtonText}" 
                         iclass="atg_store_textButton atg_store_compareRemove"/>
            </dsp:form>
            
            <%-- 
              Continue Shopping button. The continueShopping button generates a page parameter
              called continueShoppingURL which the shopper is directed to upon clicking the button
            --%>
            <crs:continueShopping>
              <div class="atg_store_formActions">
                <fmt:message var="linkTitle" key="common.button.continueShoppingText"/>
                <a class="atg_store_basicButton" href="${continueShoppingURL}" title="${linkTitle}">
                  <span>
                    <dsp:valueof value="${linkTitle}"/>
                  </span>
                </a>
              </div>
            </crs:continueShopping>
          </div>
        </div>
      </c:when>
      
      <%-- No items in the comparisons list --%>  
      <c:otherwise>
        <crs:messageContainer titleKey="browse_productComparison.productNotSelectedTitle" 
                              messageKey="browse_productComparison.productNotSelectedMsg">
          <jsp:body>
            <crs:continueShopping>
              <div class="atg_store_formActions">
                <fmt:message var="linkTitle" key="common.button.continueShoppingText"/>
                <a class="atg_store_basicButton" href="${continueShoppingURL}" title="${linkTitle}">
                  <span>
                    <dsp:valueof value="${linkTitle}"/>
                  </span>
                </a>
              </div>
            </crs:continueShopping>
          </jsp:body>
        </crs:messageContainer>
      </c:otherwise>
      
    </c:choose>
  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/comparisons/productComparisons.jsp#3 $$Change: 788278 $--%>
