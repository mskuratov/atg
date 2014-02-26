<%-- 
  This gadget displays the Return Progress bar.
  We display the following stages of return process:
    - Select
    - Confirm
    

  Required parameters:
    currentStage
      Specifies a current return progress stage that should be highlighted

  Optional parameters:
    None.
--%>

<dsp:page>

  <dsp:getvalueof var="currentStage" vartype="java.lang.String" param="currentStage" />
  
   <ol class="atg_store_returnItemSelectionProgress">
     <li class="select${currentStage == 'select' ? ' current' : ''}">
       <span class="atg_store_stageNumber">1</span>
       <span class="atg_store_checkoutStageName">
         <fmt:message key="myaccount_returnProgress.select"/>
       </span>
     </li>

     <li class="confirm${currentStage == 'confirm' ? ' current' : ''}">
       <span class="atg_store_stageNumber">2</span>
       <span class="atg_store_checkoutStageName">
         <fmt:message key="myaccount_returnProgress.confirm"/>
       </span>
     </li>
     
   </ol>
    
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/myaccount/gadgets/returnProgress.jsp#1 $$Change: 788278 $--%>