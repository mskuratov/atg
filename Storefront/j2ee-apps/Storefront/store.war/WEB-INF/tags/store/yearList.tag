<%-- 
  This tag generates the list of years in between the specified range.
  The 'yearListRenderer' fragment is invoked for rendering the year list.
 
  Page scope variables set:
    year 
      Value of year to be displayed.
    selected 
      Whether value of year is matched with the value of 'selectedYear' value
    
  Required attributes:
    bean 
      Bean name with path to be used for '<dsp:select' bean.

  Optional attributes:
    startYear 
      Starting year from where list should begin. 
      If not specified then starting year would be the current year.
      If negative value is specified then starting year would be current year.                   
    numberOfYears 
      Total numbers of years to generated.
      If value is not specified or value is <= 0 then the default value is taken.             
    selectedYear 
      Year which is to be shown as selected
      If 'selectedYear' is not lying in between the year list then an additional year with a value the
        same as 'selectedYear' would be added  at appropriate place and also shown as selected.
      If 'selectedYear' <= 0 then current year would shown as selected.
    selectRequired 
      This attribute is used for '<dsp:select required="true"'.
      If 'selectRequired' is true then 'dsp:select' 'iclassRequired' attribute would be set as required.
      Default to false. 
    id 
      This attribute is used for '<dsp:select id="${id}'.
    nodefault 
      This attribute is for '<dsp:select nodefault="true"'.
      If nodefault is true then selectedYear functionality will be disabled; Defaults to false.           
    title 
      This attribute is used for '<dsp:select title="${title}"'.
    iclass 
      This attribute is to be used for '<dsp:select iclass="${iclass}'.
    onchange 
      Specifies the value of onchange attribute for the select tag: <dsp:select onchange="${onchange}' .../>.
  --%>

<%@include file="/includes/taglibs.jspf"%>
<%@include file="/includes/context.jspf"%>

<%@ attribute name="startYear" required="false" type="java.lang.Integer" %>
<%@ attribute name="numberOfYears" required="false" type="java.lang.Integer" %>
<%@ attribute name="selectedYear" required="false" type="java.lang.Integer" %>
<%@ attribute name="bean" required="true" %>
<%@ attribute name="selectRequired" required="false" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="nodefault" required="false" type="java.lang.Boolean" %>
<%@ attribute name="title" required="false" %>
<%@ attribute name="yearString" required="false" %>
<%@ attribute name="iclass" required="false" %>
<%@ attribute name="onchange" required="false" %>

<%@ variable name-given="year" variable-class="java.lang.Integer" %>
<%@ variable name-given="selected" variable-class="java.lang.Boolean" %>


<dsp:page>
  <dsp:getvalueof id="year" bean="${bean}" />
  <c:if test="${not empty year}">
    <c:set var="selectedYear" value="${year}" />
  </c:if>

  <%-- 
    Set 'nodefault' as false by default and set 'iclassRequired' as required if 'selectRequired' is true.
  --%>
  <c:choose>
    <c:when test="${selectRequired == 'true'}">
      <c:set var="isRequired" value="true" />
    </c:when>

    <c:otherwise>
      <c:set var="isRequired" value="false" />
    </c:otherwise>
  </c:choose>
  
  <dsp:select bean="${bean}" required="${isRequired}" nodefault="${nodefault}"
              id="${id}" iclass="${iclass}" title="${title}" onchange="${onchange}">
    <c:if test="${empty yearString || yearString == 'true'}">
      <dsp:option><fmt:message key="common.year"/></dsp:option>
    </c:if>

    <c:set var="defaultNoYears" value="10"/>

    <jsp:useBean id="currDate" class="java.util.Date"/>

    <fmt:formatDate var="currYear" value="${currDate}" type="DATE" pattern="yyyy"/>

    <%--
      Calculate the actual year from where actual year list should begin.
    --%>
    <c:choose>
      <c:when test="${startYear < 0}">
        <c:set var="listStartYear" value="${currYear + startYear}" />
      </c:when>

      <c:when test="${startYear > 0}">
        <c:set var="listStartYear" value="${startYear}" />
      </c:when>
      <c:otherwise>
        <c:set var="listStartYear" value="${currYear}" />     
      </c:otherwise>
    </c:choose>

    <%-- Calculate the actual year at which year list should end. --%>
    <c:set var="endYear" value="${numberOfYears > 0 ? ((listStartYear + numberOfYears)-1) : ((listStartYear + defaultNoYears)-1)}"/>

    <%-- Calculate the valid value of selectedYear. --%>
    <c:set var="yearSelected" value="${selectedYear <=0 ? currYear : selectedYear}"/>

    <%-- 
      Check if the selected year is a year before the start year of the list. 
    --%>
    <c:if test="${yearSelected < listStartYear}">
      <c:set var="year" value="${yearSelected}"/>
      <c:set var="selected" value="${true}"/>
      <c:choose>
        <c:when test="${selected && !nodefault}">
          <dsp:option value="${year}" selected="true"><c:out value="${year}"/></dsp:option>
        </c:when>
        <c:otherwise>
          <dsp:option value="${year}"><c:out value="${year}"/></dsp:option>
        </c:otherwise>
      </c:choose>
    </c:if>
   
    <%--
      Populate the year list.
    --%>   
    <c:forEach begin="${listStartYear}" end="${endYear}" var="year">
      <c:set var="selected" value="${yearSelected == year ? true : false}"/>

      <c:choose>
        <c:when test="${selected && !nodefault}">
          <dsp:option value="${year}" selected="true"><c:out value="${year}"/></dsp:option>
        </c:when>
        <c:otherwise>
          <dsp:option value="${year}"><c:out value="${year}"/></dsp:option>
        </c:otherwise>
      </c:choose>
    </c:forEach>

    <%--
      Check if the selected year is a year after the end year of year list.
    --%>
    <c:if test="${yearSelected > endYear}">
      <c:set var="year" value="${yearSelected}"/>
      <c:set var="selected" value="${true}"/>

      <c:choose>
        <c:when test="${selected && !nodefault}">
          <dsp:option value="${year}" selected="true"><c:out value="${year}"/></dsp:option>
        </c:when>
        <c:otherwise>
          <dsp:option value="${year}"><c:out value="${year}"/></dsp:option>
        </c:otherwise>
      </c:choose>
    </c:if>
  </dsp:select>
</dsp:page>

<%-- @version $Id$$Change$ --%>
