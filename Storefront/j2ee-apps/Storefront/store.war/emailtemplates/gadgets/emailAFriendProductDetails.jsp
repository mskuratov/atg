<%--
  This page displays the details related to the product referred.

  Required parameters:
    product
      The product repository item to display
    httpServer
      The URL prefix with protocol, host and port
    imageRoot
      The images URL prefix.  
      
  Optional parameters:
    productUrl
      The fully-qualified URL to the product's page.
--%>
<dsp:page>

  <dsp:importbean bean="/atg/dynamo/droplet/Compare"/>

  <dsp:getvalueof var="productUrl" param="productUrl"/>  
  <dsp:getvalueof var="productThumbnailImageUrl" param="product.smallImage.url"/>

  <table border="0" cellpadding="0" cellspacing="0" 
         style="padding-top:30px;color:#666;font-family:Tahoma,Arial,sans-serif;font-size:14px;"
         summary="" role="presentation">
    <tr>
      <fmt:message var="getDetailsTitleText" key="common.button.viewDetailsTitle"/>      
      
      <%-- Check whether product URL is empty --%>
      <c:choose>
        <c:when test="${not empty productUrl}" >
          <%--
            Product URL is not empty, display product image and display name as links to
            the product page.
           --%>
          <td style="padding-right: 10px;">
            <%-- Check whether product has thumbnail image. --%>
            <c:if test="${not empty productThumbnailImageUrl}">
              <%-- Thumbnail image exists --%> 
              <dsp:a page="${productUrl}">
                <dsp:getvalueof var="imageAltText" param="product.displayName" />
                <img src='<dsp:valueof param="httpServer" /><dsp:valueof value="${productThumbnailImageUrl}"/>' 
                     title="${getDetailsTitleText}" alt="${imageAltText}" border="0"/>
              </dsp:a>
            </c:if>
          </td>
          <td style="vertical-align: top;">
            <div style="margin-bottom: 8px;">
              <dsp:a href="${productUrl}" 
                     style="color:#0a3d56;font-size:16px;text-decoration:none;font-weight:bold">
                <dsp:valueof param="product.displayName" />
              </dsp:a>
            </div>
        </c:when>
        <c:otherwise>
          <%-- Product URL is empty, do not display product image and display name as links. --%>
          <td style="padding-right: 10px">

            <%-- Check whether product has thumbnail image. --%>            
            <c:if test="${not empty productThumbnailImageUrl}">
              <%-- Thumbnail image exists --%> 
              <img src='<dsp:valueof param="httpServer" /><dsp:valueof param="product.smallImage.url"/>' 
                   title="${getDetailsTitleText}" border="0" alt="${imageAltText}" border="0"/>
            </c:if>
              
          </td>
          <td style="vertical-align: top;">
            <div style="color:#0a3d56;font-size:16px;text-decoration:none;font-weight:bold">
              <dsp:valueof param="product.displayName" />
            </div>
        </c:otherwise>
      </c:choose>

      <%-- Product's price --%> 
      <div style="margin-bottom: 5px;">
        
        <dsp:getvalueof var="childSKUs" param="product.childSKUs"/>
        <c:set var="totalSKUs" value="${fn:length(childSKUs)}"/>
        
        <%-- Check whether product has a single SKU. --%>
        <dsp:droplet name="Compare">
          <dsp:param name="obj1" value="${totalSKUs}" converter="number" />
          <dsp:param name="obj2" value="1" converter="number" />
          <dsp:oparam name="equal">
            <%-- Product has only one SKU, display price for this SKU. --%>
            <span style="color:#b75a00;font-size:16px;font-weight:bold">
              <dsp:include page="/emailtemplates/gadgets/priceLookup.jsp">
                <dsp:param name="sku" param="product.childSKUs[0]" />
                <dsp:param name="product" param="product" />
              </dsp:include>
            </span>
          </dsp:oparam>
          <dsp:oparam name="default">
            <%-- Product has more than 1 SKU, display Price Range --%>
            <span style="font-size:16px;font-weight:bold">
              <dsp:include page="/emailtemplates/gadgets/priceRange.jsp">
                <dsp:param name="product" param="product" />
              </dsp:include>
            </span>
          </dsp:oparam>
        </dsp:droplet> 
        <%-- End Compare droplet to see if the product has a single sku --%>
      </div>
      
      <%-- Long Description of Product. --%>
      <div style="margin-top:14px;margin-bottom:5px;">
        <dsp:valueof param="product.longDescription" valueishtml="true"></dsp:valueof>
      </div>

      <%-- View details button that takes user to the product page.  --%>
      <div style="margin-top:20px;">
        <table border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td>
              <img src="<dsp:valueof param="imageRoot"/>content/images/email/button_left.png" style="vertical-align:middle;" alt=""/>
            </td>
            <td align="center" 
                style="background-color:#659db7;padding-left:8px;padding-right:8px;text-align:center;font-family:Verdana,arial,sans-serif;font-size:12px;font-weight:bold">
              <a style="color:#FFFFFF;text-decoration:none" href="${productUrl}">
                <fmt:message key="emailtemplates_buttons.viewDetails" />
              </a>
            </td>
            <td>
              <img src="<dsp:valueof param="imageRoot"/>content/images/email/button_right.png" style="vertical-align:middle;" alt=""/>
            </td>
          </tr>
        </table>
      </div>
        
      </td>
    </tr>
  </table>
  <p style="line-height:75px;margin:0px; padding:0px">&nbsp;</p>
</dsp:page>

<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/emailtemplates/gadgets/emailAFriendProductDetails.jsp#3 $$Change: 794401 $--%>
