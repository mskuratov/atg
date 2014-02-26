<%--
  This page caches its container's content in order to improve performance.

  Required parameters:
    container
      Page name to be cached.

  Optional parameters:
    keySuffix
      Specifies a suffix to be used when constructing cache key.
    product
      Specifies a product currently rendered.
    categoryId
      Specifies a currently rendered category.
--%>

<dsp:page>
  <dsp:getvalueof id="product" param="product"/>
  <dsp:getvalueof id="categoryId" param="categoryId"/>
  <dsp:getvalueof id="container" param="container"/>
  <dsp:getvalueof id="keySuffix" param="keySuffix"/>
  
  <dsp:importbean bean="/atg/dynamo/droplet/Cache"/>
  <dsp:importbean bean="/atg/repository/seo/BrowserTyperDroplet"/>
  <dsp:importbean bean="/atg/multisite/Site"/>
  
  <%-- Use the Cache Droplet to provide performance caching. --%>
  <dsp:getvalueof var="productId" vartype="java.lang.String" param="product.repositoryId"/>
  <dsp:getvalueof var="siteId" vartype="java.lang.String" bean="Site.id"/>
  <%--
    This droplet determines user's browser type.

    Input parameters:
      None.

    Output parameters:
      browserType
        Specifies a user's browser type.

    Open parameters:
      output
        Always rendered.
  --%>
  <dsp:droplet name="BrowserTyperDroplet">
    <dsp:oparam name="output">
      <%--
        This droplet caches its output open parameter's contents in order to improve performance.

        Input parameters:
          key
            Specifies a key by which cached content will be stored.

        Output parameters:
          None.

        Open parameters:
          output
            Always rendered. Specifies content to be cached.
      --%>
      <dsp:droplet name="Cache">
        <dsp:param name="key" value="bp_pd_${keySuffix}_${productId}_${param.browserType}_${requestLocale.locale}_${siteId}"/>
        <dsp:oparam name="output">
          <dsp:include page="${container}">
            <dsp:param name="product" param="product"/>
            <dsp:param name="categoryId" param="categoryId"/>
          </dsp:include>
        </dsp:oparam>
      </dsp:droplet> <%-- End cache droplet to cache this part of the page --%>
    </dsp:oparam>
  </dsp:droplet> <%-- BrowserTyperDroplet --%>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/browse/gadgets/cacheProductDisplay.jsp#1 $$Change: 735822 $ --%>
