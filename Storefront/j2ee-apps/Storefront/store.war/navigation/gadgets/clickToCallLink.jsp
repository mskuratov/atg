<%--
  This page renders Click To Call links. A user is able to click on one of this
  links to display a popup which will initiate live communication with an agent.
    
  Required Parameters:
    pageName
      The name of the page type including this jsp
  
  Optional Parameters:
    None
--%>
<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/ComponentExists"/>  
  <%--
    If the Click To Call feature is disabled, this entire div can be safely
    bypassed. We can test if Click To Call is installed by the existence
    of the clicktoconnect Configuration component.
  --%>
  <dsp:droplet name="ComponentExists" path="/atg/clicktoconnect/Configuration">
    
    <dsp:oparam name="true">    
      <%-- 
        In order to display the ClickToCall button we need to add an anchor in the place we
        want the ClickToCall button to be rendered. This anchor is the HTML div below. Its id
        attribute must match the 'Relative to Layer ID' property of the WebCare link
      --%>
      <dsp:getvalueof var="pageName" param="pageName"/>
      <div id="atg_store_c2c_${pageName}">&nbsp;</div>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/navigation/gadgets/clickToCallLink.jsp#1 $$Change: 735822 $--%>