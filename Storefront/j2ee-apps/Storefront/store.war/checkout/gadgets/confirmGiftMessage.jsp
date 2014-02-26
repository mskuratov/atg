<%--
  This gadget renders a gift message row on the Order Details page.

  Required parameters:
    order
      Specifies an order, whose gift message should be displayed.

  Optional parameters:
    isCurrent
      Flags, if this gadget is a part of Confirmation page.
    hideSiteIndicator
      Flags, if this gadget should not render a site indicator's empty cell.
--%>

<dsp:page>
  <dsp:getvalueof var="order" param="order"/>
  <dsp:getvalueof var="isCurrent" param="isCurrent"/>
  <dsp:getvalueof var="containsGiftMessage" vartype="java.lang.String" param="order.containsGiftMessage"/>
  <dsp:getvalueof var="hideSiteIndicator" vartype="java.lang.Boolean" param="hideSiteIndicator"/>

  <%-- Display contents, only if the order possesses the gift message. --%>
  <c:if test='${containsGiftMessage == "true"}'>
    <tr>
      <%-- Should we display site indicator's placement? --%>
      <c:if test="${empty hideSiteIndicator or not hideSiteIndicator}">
        <td class="site">
        </td>
      </c:if>

      <%-- Gift message's icon. --%>
      <td class="image">
        <img src="/crsdocroot/content/images/GN_GiftNote.jpg" alt="<fmt:message key='checkout_confirmGiftMessage.reviewGiftMessage'/>">
      </td>

      <%-- Display gift message details. --%>
      <fmt:message var="giftNote" key="checkout_confirmGiftMessage.reviewGiftMessage"/>
      <td class="atg_store_confirmGiftMessage item" scope="row" abbr="${giftNote}">
        <span class="itemName"><c:out value="${giftNote}"/></span>  
        <ul>
          <li class="atg_store_messageTo">
            <span class="atg_store_giftNoteLabel"><fmt:message key="common.to"/>:</span>
            <span class="atg_store_giftNoteInfo"><dsp:valueof param="order.specialInstructions.giftMessageTo"/></span>
          </li>
          <li class="atg_store_messageFrom">
            <span class="atg_store_giftNoteLabel"><fmt:message key="common.from"/>:</span>
            <span class="atg_store_giftNoteInfo"><dsp:valueof param="order.specialInstructions.giftMessageFrom"/></span>
          </li>
          <li class="atg_store_giftMessage">
            <span class="atg_store_giftNoteLabel"><fmt:message key="common.text"/>:</span>
            <span class="atg_store_giftNoteInfo"><dsp:valueof param="order.specialInstructions.giftMessage"/></span>
          </li>
        </ul>

        <%-- If it's a part of Confirmation page, display a link to 'Edit Gift Message' page. --%>
        <c:if test="${isCurrent}">  
         
          <fmt:message var="editMessageTitle" key="checkout_confirmGiftMessage.button.editMessageTitle"/>
          
          <dsp:a page="/checkout/giftMessage.jsp" iclass="atg_store_actionEdit" title="${editMessageTitle}">
            <dsp:param name="editMessage" value="true" />
            <fmt:message key="common.button.editText"/>
          </dsp:a>
        
        </c:if>    
      </td>

      <%-- Display gift message's quantity (always 1) and price (always free). --%>
      <td colspan="2" class="atg_store_quantityPrice price">
        <div class="atg_store_itemQty">
          <span class="quantity">
            1 <fmt:message key="common.atRateOf"/>
          </span>
          <span class="price">
            <p class="price">
              <span>
                <fmt:message key="common.FREE"/>
              </span>
            </p>
          </span>
        </div>
      </td>

      <td class="total">
        <p class="price">
          <fmt:message key="common.equals"/> <fmt:message key="common.FREE"/>
        </p>
      </td>
    </tr>
  </c:if>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/checkout/gadgets/confirmGiftMessage.jsp#3 $$Change: 788810 $--%>
