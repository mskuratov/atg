<%--
  Back in Stock Notification email template. The template displays product details for the product
  that is back in stock.
  
  Required parameters:
    skuId
      The ID of the SKU that is back in stock.    
    productId
      The ID of the product that's back in stock.

  Optional parameters:
    locale
      Locale that specifies in which language email should be rendered.
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/multisite/Site"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
  
  <%-- Get sender email address from site configuration --%>
  <dsp:getvalueof var="backInStockFromAddress" bean="Site.backInStockFromAddress" />

  <%-- 
    Lookup for a product with a given ID.
     
    Input parameters:
      id
        ID of product to lookup for.
        
    Output parameters:
      element
        Product repository item with the given ID
        
    Open parameters:
      output
        Rendered if the item was found in the repository                  
  --%>
  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" param="productId"/>
    <dsp:oparam name="output">      
      <%-- Put the found product into the variable so that it can be used outside of the droplet. --%>
      <dsp:getvalueof var="product" param="element"/>
    </dsp:oparam>
  </dsp:droplet>
  
  <dsp:param name="product" value="${product}"/>
  <dsp:getvalueof var="productName" vartype="java.lang.String" param="product.displayName"/>

  <%-- Email subject --%>
  <fmt:message var="emailSubject" key="emailtemplates_backInStockNotify.subject">
    <fmt:param>
      <dsp:valueof bean="Site.name" />
    </fmt:param>
    <fmt:param>${productName}</fmt:param>
  </fmt:message>  

  <crs:emailPageContainer divId="atg_store_backInStockNotifyIntro" 
                          titleKey="emailtemplates_backInStockNotify.title" 
                          messageSubjectString="${emailSubject}"
                          messageFromAddressString="${backInStockFromAddress}">

    <jsp:body>
    
      <%-- Get URL prefix built by emailPageContainer tag--%>
      <dsp:getvalueof var="httpServer" param="httpServer"/>
      
      <%-- Images path prefix built by emailPageContainer tag --%>
      <dsp:getvalueof var="imageRoot" param="imageRoot"/>

      <%-- Set email template parameters --%>
      <dsp:setvalue param="mailingName" value="BackInStockNotify"/>
     
      <dsp:getvalueof var="locale" param="locale"/>

      <table border="0" cellpadding="0" cellspacing="0" width="100%" 
             style="color:#666;font-family:Tahoma,Arial,sans-serif;" 
             summary="" role="presentation">
        <%-- Begin Product Details --%>
        <tr>
          <td style="padding-top:30px;">
            <table border="0" cellpadding="0" cellspacing="0" 
                   style="color:#666;font-family:Verdana,Arial,sans-serif;font-size:14px"
                   summary="" role="presentation">  
              <tr>
                <dsp:droplet name="SKULookup">
                  <dsp:param name="id" param="skuId"/>    
                  <dsp:oparam name="output">
                    <dsp:param name="sku" param="element"/>
                    
                    <%-- Get cross site link for product template --%>
                    <dsp:include page="/global/gadgets/productLinkGenerator.jsp">
                      <dsp:param name="product" param="product"/>
                      <dsp:param name="siteId" bean="/atg/multisite/Site.id"/>
                    </dsp:include>
                    
                    <dsp:getvalueof var="skuType" vartype="java.lang.String" param="sku.type"/>
      
                    <%-- Build fully qualified URL for the SKU. --%>
                    <c:url var="skuUrl" value="${httpServer}${productUrl}">
                      <c:if test="${not empty locale}">
                        <c:param name="locale">${locale}</c:param>
                      </c:if>
                      <c:choose>
                        <%-- If SKU type is 'clothing-sku' add color and size parameters to SKU URL. --%>
                        <c:when test="${skuType == 'clothing-sku'}">
                          <c:param name="selectedColor"><dsp:valueof param="sku.color"/></c:param>
                          <c:param name="selectedSize"><dsp:valueof param="sku.size"/></c:param>
                        </c:when>
                        <%-- If the type is 'furniture-sku' add woodFinish parameter to SKU URL. --%>
                        <c:when test="${skuType == 'furniture-sku'}">
                          <c:param name="selectedWoodFinish"><dsp:valueof param="sku.woodFinish"/></c:param>
                        </c:when>
                      </c:choose>
                    </c:url>
                    
                    <td valign="top" style="width:250px;padding-left:16px;padding-right:16px">                     
                      <%-- Check to see if the SKU has thumbnail image --%>
                      <dsp:getvalueof var="skuSmallImageUrl" param="sku.smallImage.url"/>
                      <c:choose>
                        <c:when test="${not empty skuSmallImageUrl}">
                          <a href="${skuUrl}">
                            <img src="<dsp:valueof param="httpServer"/><dsp:valueof param='sku.smallImage.url'/>" 
                                 width="250" border="0" alt="${productName}">
                            <br />
                          </a>
                        </c:when>
                        <c:otherwise>
                          <%-- The SKU has no image display product's image. --%>
                          <dsp:getvalueof var="productLargeImageUrl" param="product.largeImage.url"/>
                          <c:if test="${not empty productLargeImageUrl}">
                            <a href="${skuUrl}">
                              <img src="<c:out value="${httpServer}"/><dsp:valueof param='product.largeImage.url'/>" 
                                   width="250" border="0" alt="${productName}">
                              <br />
                            </a>
                          </c:if>
                        </c:otherwise>
                      </c:choose>
                    </td>
              
                    <td valign="top" style="width:400px;padding-left:20px">
                      <%-- Product's display name --%>
                      <div style="margin-bottom:8px">
                        <a href="${skuUrl}" 
                           style="color:#0a3d56;font-size:16px;text-decoration:none;font-weight:bold">
                          <dsp:valueof param="product.displayName">
                            <fmt:message key="common.productNameDefault"/>
                          </dsp:valueof>
                        </a>
                      </div>
                      
                      <%-- Product SKU Details --%>
                      <dsp:include page="/emailtemplates/gadgets/backInStockSkuDetails.jsp">
                        <dsp:param name="product" param="product"/>
                        <dsp:param name="sku" param="sku"/>
                      </dsp:include>
                      
                      <%-- Product's long description if not empty. --%>
                      <dsp:getvalueof var="longDescription" param="product.longDescription"/>
                      <c:if test="${not empty longDescription}">
                        <div style="margin-top:8px;line-height:20px">                    
                          <dsp:valueof param="product.longDescription" valueishtml="true"/>
                        </div>
                      </c:if>
                      
                      <%-- 'Add to Cart' button  --%>
                      <div style="margin-top:20px">
                        <table border="0" cellpadding="0" cellspacing="0">
                          <tr>
                            <td>
                              <img src="${imageRoot}content/images/email/button_left.png" 
                                   style="vertical-align:middle;" alt=""/>
                            </td>
                            <td align="center" 
                                style="background-color:#659db7;padding-left:8px;padding-right:8px;text-align:center;font-family:Verdana,arial,sans-serif;font-size:12px;font-weight:bold">
                             
                              <%--
                                Get fully-qualified URL to shopping cart page to where user should be
                                redirected upon click on 'Add to Cart' button.
                               --%>
                              <dsp:include page="/emailtemplates/gadgets/emailSiteLink.jsp">
                                <dsp:param name="path" value="/cart/cart.jsp"/>
                                <dsp:param name="locale" param="locale"/>
                                <dsp:param name="httpServer" value="${httpServer}"/>
                              </dsp:include>
                              
                              <%--
                                Add 'dcs_action' parameter to cart URL with 'additemtocart' value to notify
                                the CommerceCommandServlet to add the specified item to cart.
                               --%>
                              <c:url var="addToCartUrl" value="${siteLinkUrl}" >
                                <c:param name="dcs_action" value="additemtocart"/>
                                <c:param name="url_catalog_ref_id" ><dsp:valueof param="skuId" /></c:param>
                                <c:param name="url_product_id" ><dsp:valueof param="productId" /></c:param>
                                <c:param name="url_quantity" value="1"/>
                              </c:url> 
                              
                              <dsp:a  style="color:#FFFFFF;text-decoration:none" href="${addToCartUrl}">
                                <fmt:message key="emailtemplates_buttons.addToCart" />
                              </dsp:a>
                       
                            </td>
                            <td>
                              <img src="${imageRoot}content/images/email/button_right.png" 
                                   style="vertical-align:middle;" alt=""/>
                            </td>
                          </tr>
                        </table>
                      </div>
                    </td>
                  </dsp:oparam>
                </dsp:droplet>
                
              </tr>
            </table>
          </td> 
        </tr>
        <%-- End of Product Details --%>
      </table>
    </jsp:body>
  </crs:emailPageContainer>

</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/backInStockNotify.jsp#3 $$Change: 794401 $--%>
