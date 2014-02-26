<%--
  This page renders link to KnowledgeBase Widget. The link on selection,  will
  display a div containing a search box allowing the user to perform a  search on the KnowledgeBase
  contents and it also lists the default set of search results relevant to the current product selected
  and each of which on selection opens up in a dialog  box providing detailed description of the
  result selected.
    
  Required Parameters:
    None
  
  Optional Parameters:
    None
--%>
<dsp:page>

<%--
    ComponentExists droplet conditionally renders one of its output parameters
    depending on whether or not a specified Nucleus path currently refers to a
    non-null object.  It it used to query whether a particular component has been
    instantiated. If the KnowledgeBaseProcessor component has not been instantiated
    KnowledgeBase div container will not be included.
      
    Input Parameters:
      path - The path to a component
       
    Open Parameters:
      true
        Rendered if the component 'path' has been instantiated.
      false
        Rendered if the component 'path' has not been instantiated. 
  --%>

  <dsp:importbean bean="/atg/dynamo/droplet/ComponentExists"/>  
  
  <%-- Performs a check to see if the specified component exists or not --%>
  <dsp:droplet name="ComponentExists" path="/atg/adc/KnowledgeBaseProcessor"> 
  
    <dsp:oparam name="true">
   
    <%-- The div contains the link to the KnowledgeBase --%>
    <div id="atg_store_rightnowKnowledgebaseContainer"> 
    
      <div class="linkToKnowledgeBase">
         <a href="javascript:atg.store.rightNow.toggleKnowledgebaseDisplay()" id="showKnowledgebaseLink" class="hideKnowledgebase">
        
            <%-- Get the locale specific text corresponding to the link to KnowledgeBase from the storetext repository --%>
            <crs:outMessage key="knowledgeBase.linkText"/>

        </a>
      </div>

      <div id="knowledgebase" class="knowledgebase hideKnowledgebase">
      </div>
      
      <%--  Each search result link opens up in a dialog box --%>
      <div id="knowledgebaseItem" dojoType="dojox.widget.Dialog" modal="false">
      
        <a href="javascript:closeDialog();" onfocus="dojo.byId('knowledgebaseItemIframe').focus();" class="knowledgebaseCloseLink">close</a>
        
        <iframe src="" id="knowledgebaseItemIframe" class="knowledgebaseItemIframe" frameborder="0" fname="RightNow Knowledge Base" title="Knowledge Base Frame"></iframe>
        
        <a href="javascript:closeDialog();" onfocus="dojo.byId('knowledgebaseItemIframe').focus();" class="knowledgebaseCloseLink">close</a>
        
      </div>
      
    </div> 
    
    </dsp:oparam>
      
   </dsp:droplet>  
</dsp:page>
<%-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/Storefront/j2ee/store.war/navigation/gadgets/knowledgeBase.jsp#2 $$Change: 795424 $--%>