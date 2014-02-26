/*<ORACLECOPYRIGHT>
 * Copyright (C) 1994-2013 Oracle and/or its affiliates. All rights reserved.
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.
 * UNIX is a registered trademark of The Open Group.
 *
 * This software and related documentation are provided under a license agreement 
 * containing restrictions on use and disclosure and are protected by intellectual property laws. 
 * Except as expressly permitted in your license agreement or allowed by law, you may not use, copy, 
 * reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish, 
 * or display any part, in any form, or by any means. Reverse engineering, disassembly, 
 * or decompilation of this software, unless required by law for interoperability, is prohibited.
 *
 * The information contained herein is subject to change without notice and is not warranted to be error-free. 
 * If you find any errors, please report them to us in writing.
 *
 * U.S. GOVERNMENT RIGHTS Programs, software, databases, and related documentation and technical data delivered to U.S. 
 * Government customers are "commercial computer software" or "commercial technical data" pursuant to the applicable 
 * Federal Acquisition Regulation and agency-specific supplemental regulations. 
 * As such, the use, duplication, disclosure, modification, and adaptation shall be subject to the restrictions and 
 * license terms set forth in the applicable Government contract, and, to the extent applicable by the terms of the 
 * Government contract, the additional rights set forth in FAR 52.227-19, Commercial Computer Software License 
 * (December 2007). Oracle America, Inc., 500 Oracle Parkway, Redwood City, CA 94065.
 *
 * This software or hardware is developed for general use in a variety of information management applications. 
 * It is not developed or intended for use in any inherently dangerous applications, including applications that 
 * may create a risk of personal injury. If you use this software or hardware in dangerous applications, 
 * then you shall be responsible to take all appropriate fail-safe, backup, redundancy, 
 * and other measures to ensure its safe use. Oracle Corporation and its affiliates disclaim any liability for any 
 * damages caused by use of this software or hardware in dangerous applications.
 *
 * This software or hardware and documentation may provide access to or information on content, 
 * products, and services from third parties. Oracle Corporation and its affiliates are not responsible for and 
 * expressly disclaim all warranties of any kind with respect to third-party content, products, and services. 
 * Oracle Corporation and its affiliates will not be responsible for any loss, costs, 
 * or damages incurred due to your access to or use of third-party content, products, or services.
 </ORACLECOPYRIGHT>*/


package atg.knowledgebase.adc;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.adc.pipeline.ADCPipelineArgs;
import atg.adc.pipeline.AppendStringProcessor;
import atg.core.i18n.LayeredResourceBundle;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.projects.store.multisite.StoreSitePropertiesManager;
import atg.servlet.ServletUtil;

/**
 * This class incorporates a dynamic javascript tag to the html page to invoke the
 * Knowledge base widget.
 *
 * @author Gayathri Sasidharan
 * 
 * @version //hosting-blueprint/B2CBlueprint/main/KnowledgeBase/src/atg/knowledgebase/adc/KnowledgeBaseProcessor.java#5 $$Change: 791340 $
 * @updated gsasidha
 * 
 */
public class KnowledgeBaseProcessor extends AppendStringProcessor{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/KnowledgeBase/src/atg/knowledgebase/adc/KnowledgeBaseProcessor.java#2 $$Change: 791340 $";

  
  /**
   * Resource bundle name.
   */
  private static final String RESOURCE_BUNDLE_NAME = "atg.knowledgebase.adc.Resources";
  
  /**
   * RQL Query repository by key
   */
  protected final static String RQL_QUERY_BY_KEY = "key = ?0";
  
  /**
   * Repository Exception.
   */
  protected static final String ERROR_REPOSITORY = "knowledge_base.error.repository";
  
  /**
   * Global constants
   */
  public static final String TEXT_PROPERTY_NAME = "text";
  private final String newLine = System.getProperty("line.separator");
   
  /**
   *  Labels displayed in the widget ui.
   */
  private static final String LABEL_CORRECTION = "knowledgeBase.labelCorrection";
  private static final String LABEL_DOCUMENTS = "knowledgeBase.labelDocuments";
  private static final String LABEL_MORE_RESULTS = "knowledgeBase.labelMoreResults";
  private static final String LABEL_NO_RESULTS = "knowledgeBase.labelNoResults";
  private static final String LABEL_RELATED = "knowledgeBase.labelRelated";
  private static final String LABEL_SEARCH = "knowledgeBase.labelSearch";
  private static final String LABEL_QUERY = "knowledgeBase.labelQuery";
  
  /**
   * Javascript snippet used in the response stream
   */
  String ADD_COMPONENT = "dojo.ready(function(){" + newLine + "if (window.RightNow !== undefined){" + newLine + " RightNow.Client.Controller.addComponent( {";
  String RESPONSE_CALL_BACK = "  if (RightNow && RightNow.Client && RightNow.Client.Event && RightNow.Client.Event.evt_searchResponse){" + newLine + " RightNow.Client.Event.evt_searchResponse.subscribe(atg.store.rightNow.decorateLinksWhenReady, this);" + newLine + "}" + newLine;
  String CROSS_DOMAIN_LISTENER = "if (window.addEventListener){" + newLine +"addEventListener('message', atg.store.rightNow.postMessageListener, false)" + newLine +"}" + newLine + "else {" + newLine + "attachEvent('onmessage', atg.store.rightNow.postMessageListener)" + newLine + "}";
  //---------------------------------------------------------------------------
  //Optional attributes to configure Knowledge Base Widget into CRS
  //---------------------------------------------------------------------------
 
  //----------------------------------------------
  // property: Number_answers
  //----------------------------------------------
  private String mNumber_answers;
 
  /**
   * @return Number of answers to be displayed in the search result
   */
  public String getNumber_answers() { 
    return mNumber_answers;
  }
 
  /**
   * @param pNumber_answers Number of answers to be displayed in the search result
   */
  public void setNumber_answers(String pNumber_answers) {
    mNumber_answers = pNumber_answers;
  }

  //----------------------------------------------
  // property: Correction
  //----------------------------------------------
  private String mCorrection;
 
  /**
   * @return the flag to enable or disable spelling corrections
   */
  public String getCorrection() { 
    return mCorrection;
  }
 
  /**
   * @param pCorrection flag to enable or disable spelling corrections
   */
  public void setCorrection(String pCorrection) {
    mCorrection = pCorrection;
  }
 
  //----------------------------------------------
  // property: Description
  //----------------------------------------------
  private String mDescription;
 
  /**
   * @return the flag to enable or disable answer description
   */
  public String getDescription() { 
    return mDescription;
  }
  
  /**
   * @param pDescription flag to enable or disable answer description
   */
  public void setDescription(String pDescription) {
    mDescription = pDescription;
  }
 
  //----------------------------------------------
  //property: Ext_docs
  //----------------------------------------------
  private String mExt_docs;
 
  /**
   * @return Flag to perform searches against the external document index
   */
  public String getmExt_docs() {
    return mExt_docs;
  }

  /**
   * @param pExt_docs Flag to perform searches against the external document index
   */
  public void setExt_docs(String pExt_docs) {
    mExt_docs = pExt_docs;
  }
 
  //----------------------------------------------
  //property: Label_correction
  //----------------------------------------------
  private String mLabel_correction;
 
  /**
   * @return Displays spelling suggestions
   */
  public String getLabel_correction() {
    return mLabel_correction;
  }

  /**
   * @param pLabel_correction Displays spelling suggestions
   */
  public void setLabel_correction(String pLabel_correction) {
    mLabel_correction = pLabel_correction;
  }
 
  //----------------------------------------------
  //property: Label_documents
  //----------------------------------------------
  private String mLabel_documents;
 
  /**
   * @return Label for recommended documents
   */
  public String getLabel_documents() { 
    return mLabel_documents;
  }
   
  /**
   * @param pLabel_documents Label for recommended documents
   */
  public void setLabel_documents(String pLabel_documents) {
    mLabel_documents = pLabel_documents;
  }
 
  //----------------------------------------------
  //property: Label_more_results
  //----------------------------------------------
  private String mLabel_more_results;
 
  /**
   * @return Label for more results
   */
  public String getLabel_more_results() { 
    return mLabel_more_results;
  }
   
  /**
   * @param pLabel_more_results Label for more results
   */
  public void setLabel_more_results(String pLabel_more_results) {
    mLabel_more_results = pLabel_more_results;
  }
 
  //----------------------------------------------
  //property: Label_no_results
  //----------------------------------------------
  private String mLabel_no_results;
 
  /**
   * @return Displays when no results are found
   */
  public String getLabel_no_results() { 
    return mLabel_no_results;
  }
   
  /**
   * @param pLabel_no_results Displays when no results are found
   */
  public void setLabel_no_results(String pLabel_no_results) {
    mLabel_no_results = pLabel_no_results;
  }
  
  //----------------------------------------------
  //property: Label_related_searches
  //----------------------------------------------
  private String mLabel_related_searches;
 
  /**
   * @return Displays the message when suggested searches are found
   */
  public String getLabel_related_searches() { 
    return mLabel_related_searches;
  }
   
  /**
   * @param pLabel_related_searches Displays the message when suggested searches are found
   */
  public void setLabel_related_searches(String pLabel_related_searches) {
    mLabel_related_searches = pLabel_related_searches;
  }
 
  //----------------------------------------------
  //property: Label_search_button
  //----------------------------------------------
  private String mLabel_search_button;
 
  /**
   * @return Label for search button
   */
  public String getLabel_search_button() { 
    return mLabel_search_button;
  }
   
  /**
   * @param pLabel_search_button Label for search button
   */
  public void setLabel_search_button(String pLabel_search_button) {
    mLabel_search_button = pLabel_search_button;
  }
  
  //----------------------------------------------
  //property: Navigation
  //----------------------------------------------
  private String mNavigation;
 
  /**
   * @return The flag to enable or disable more results link
   */
  public String getNavigation() { 
    return mNavigation;
  }
   
  /**
   * @param pNavigation The flag to enable or disable more results link
   */
  public void setNavigation(String pNavigation) {
    mNavigation = pNavigation;
  }
 
  //----------------------------------------------
  //property: Payload_size
  //----------------------------------------------
  private String mPayload_size;
 
  /**
   * @return Maximum content size for Content-Sensing functionality
   */
  public String getPayload_size() { 
    return mPayload_size;
  }
   
  /**
   * @param pPayload_size Maximum content size for Content-Sensing functionality
   */
  public void setPayload_size(String pPayload_size) {
    mPayload_size = pPayload_size;
  }
 
  //----------------------------------------------
  //property: Persist_prodcat
  //----------------------------------------------
  private String mPersist_prodcat;
 
  /**
   * @return Persist the product and category specified through to subsequent searches
   */
  public String getPersist_prodcat() { 
    return mPersist_prodcat;
  }
   
  /**
   * @param pPersist_prodcat Persist the product and category specified through to subsequent searches
   */
  public void setPersist_prodcat(String pPersist_prodcat) {
    mPersist_prodcat = pPersist_prodcat;
  }
 
  //----------------------------------------------
  //property: Preprocess
  //----------------------------------------------
  private String mPreprocess;
 
  /**
   * @return The handler for page preprocessing
   */
  public String getPreprocess() { 
    return mPreprocess;
  }
   
  /**
   * @param pPreprocess The handler for page preprocessing
   */
  public void setPreprocess(String pPreprocess) {
    mPreprocess = pPreprocess;
  }

  //----------------------------------------------
  //property: Q
  //----------------------------------------------
  private String mQ;
 
  /**
   * @return Keyword for search query
   */
  public String getQ() { 
    return mQ;
  }
   
  /**
   * @param pQ Keyword for search query
   */
  public void setQ(String pQ) {
    mQ = pQ;
  }
 
  //----------------------------------------------
  //property: Recommended
  //----------------------------------------------
  private String mRecommended;
  
  /**
   * @return A flag to enable or disable recommended documents
   */
  public String getRecommended() { 
    return mRecommended;
  }
   
  /**
   * @param pRecommended A flag to enable or disable recommended documents
   */
  public void setRecommended(String pRecommended) {
    mRecommended = pRecommended;
  }

  //----------------------------------------------
  //property: Related
  //----------------------------------------------
  private String mRelated;
 
  /**
   * @return A flag to enable or disable suggested searches
   */
  public String getRelated() { 
    return mRelated;
  }
   
  /**
   * @param pRelated A flag to enable or disable suggested searches
   */
  public void setRelated(String pRelated) {
    mRelated = pRelated;
  }

  //----------------------------------------------
  //property: Search_box
  //----------------------------------------------
  private String mSearch_box;
 
  /**
   * @return A flag to enable or disable the keyword search box
   */
  public String getSearch_box() { 
    return mSearch_box;
  }
   
  /**
   * @param pSearch_box A flag to enable or disable the keyword search box
   */
  public void setSearch_box(String pSearch_box) {
    mSearch_box = pSearch_box;
  }

  //----------------------------------------------
  //property:Target
  //----------------------------------------------
  private String mTarget;
 
  /**
   * @return Defines the target where the linked document will be opened. It should be a valid
   * target value of the HTML anchor tag
   */
  public String getTarget() { 
    return mTarget;
  }
   
  /**
   * @param pTarget Defines the target where the linked document will be opened. It should be a 
   * valid target value of the HTML anchor tag
   */
  public void setTarget(String pTarget) {
    mTarget = pTarget;
  }

  //----------------------------------------------
  //property: Truncate_size
  //----------------------------------------------
  private String mTruncate_size;
 
  /**
   * @return  Number of characters to truncate to if the column is answers.solution or answers.description
   */
  public String getTruncate_size() { 
    return mTruncate_size;
  }
   
  /**
   * @param pTruncate_size  Number of characters to truncate to if the column is answers.solution or answers.description
   */
  public void setTruncate_size(String pTruncate_size) {
    mTruncate_size = pTruncate_size;
  }
 
  //----------------------------------------------
  //property:C
  //----------------------------------------------
  private String mC;
 
  /**
   * @return Category hierarchy in a comma separated chain. Multiple categories may be specified with a semicolon
   */
  public String getC() { 
    return mC;
  }
   
  /**
   * @param pC Category hierarchy in a comma separated chain. Multiple categories may be specified with a semicolon
   */
  public void setC(String pC) {
    mC = pC;
  }

  //----------------------------------------------
  //property:Context
  //----------------------------------------------
  private String mContext;

  /**
   * @return Comma-separated context HTML elements for preprocessor. The content of the elements will be used for content-sensing
   */
  public String getContext() { 
    return mContext;
  }
  
  /**
   * @param pContext Comma-separated context HTML elements for preprocessor. The content of the elements will be used for content-sensing
   */
  public void setContext(String pContext) {
    mContext = pContext;
  }
 
  //----------------------------------------------
  //property:P
  //----------------------------------------------
  private String mP;

  /**
   * @return Product hierarchy in a comma separated chain.
   */
  public String getP() { 
    return mP;
  }
  
  /**
   * @param pP Product hierarchy in a comma separated chain.
   */
  public void setP(String pP) {
    mP = pP;
  }

  //---------------------------------------------------------------------------
  // property: div_id
  //---------------------------------------------------------------------------
  private String mDiv_id;
  
  /**
   * @return The div element on your page where you want the Rightnow knowledge base search to appear
   */
  public String getDiv_id() { 
    return mDiv_id;
  }

  /**
   * The div element on your page where you want the Rightnow knowledge base search to appear
   * @param pDiv_id
   */
  public void setDiv_id(String pDiv_id) {
    mDiv_id = pDiv_id;
  }

  //---------------------------------------------------------------------------
  // property: instance_id
  //---------------------------------------------------------------------------
  private String mInstance_id;
  
  /**
   * @return mInstance_id The widget instance id for the Rightnow knowledge base search
   */
  public String getInstance_id() {
    return mInstance_id;
  }

  /**
   * The widget instance id for the Rightnow knowledge base search
   * @param pInstance_id
   */
  public void setInstance_id(String pInstance_id) {
    mInstance_id = pInstance_id;
  }
  //---------------------------------------------------------------------------
  // property: module
  //---------------------------------------------------------------------------
  private String mModule;
  
  /**
   * @return mModule The widget module name for the Rightnow knowledge base search
   */
  public String getmModule() {
    return mModule;
  }

  /**
   * The widget module name for the Rightnow knowledge base search
   * @param pModule
   */
  public void setModule(String pModule) {
    mModule = pModule;
  }

  //---------------------------------------------------------------------------
  // property: type
  //---------------------------------------------------------------------------
  private String mType;
  
  /**
   * @return mType The widget type id for the Rightnow knowledge base search
   */
  public String getType() {
    return mType;
  }

  /**
   * The widget type id for the Rightnow knowledge base search
   * @param pType
   */
  public void setType(String pType) {
    mType = pType;
  }
  
  //---------------------------------------------------------------------------
  // property: sitePropertiesManager
  //---------------------------------------------------------------------------
  protected StoreSitePropertiesManager mSitePropertiesManager;
 
  /**
   * @return the sitePropertiesManager
   */
  public StoreSitePropertiesManager getSitePropertiesManager() {
    return mSitePropertiesManager;
  }

  /**
   * @param pSitePropertiesManager the sitePropertiesManager to set
   */
  public void setSitePropertiesManager(StoreSitePropertiesManager pSitePropertiesManager) {
    mSitePropertiesManager = pSitePropertiesManager;
  }
  
  //---------------------------------------------------------------------------
  // property: siteContextPath
  //---------------------------------------------------------------------------
  protected String mSiteContextPath;
  
  /**
   * @return the siteContextPath
   */
  public String getSiteContextPath() {
    return mSiteContextPath;
  }

  /**
   * @param pSiteContextPath the siteContextPath to set
   */
  public void setSiteContextPath(String pSiteContextPath) {
    mSiteContextPath = pSiteContextPath;
  }
  
  //---------------------------------------------------------------------------
  // property: repository
  //---------------------------------------------------------------------------
  /**
   * The repository containing the text resource message strings.
   */
  private Repository mRepository = null;

  /**
   * @param pRepository the repository to set
   */
  public void setRepository( Repository pRepository ) {
    mRepository = pRepository;
  }

  /**
   * @return the mRepository
   */
  public Repository getRepository() {
    return mRepository;
  }
  
  //---------------------------------------------------------------------------
  // property: itemDescriptorName
  //---------------------------------------------------------------------------
  /**
   * The item descriptor to use when retrieving the text resource 
   * message strings from the repository.
   */
  private String mItemDescriptorName = null;

  /**
   * @param pItemDescriptorName the itemDescriptorName to set
   */
  public void setitemDescriptorName( String pItemDescriptorName) {
    mItemDescriptorName = pItemDescriptorName;
  }

  /**
   * @return the mItemDescriptorName
   */
  public String getitemDescriptorName() {
    return  mItemDescriptorName;
  }
  
  //-----------------------------------
  // METHODS
  //-----------------------------------
  
  /**
   * This method creates a list of optional parameters configured for the knowledge 
   * base widget.
   * It also fetches the locale specific labels from the store text repository to be 
   * displayed in the widget.
   * 
   * @return optionalParamsList The list of optional parameters to configure the widget
   */
  public List<String> createOptionalParamsList() {
    List<String> optionalParamsList = new ArrayList<String>();
            
    optionalParamsList.add(getNumber_answers());
    optionalParamsList.add(getCorrection());
    optionalParamsList.add(getDescription());
    optionalParamsList.add(getmExt_docs());
   
    optionalParamsList.add(getLabel_correction()+getStoreText(LABEL_CORRECTION));
    optionalParamsList.add(getLabel_documents()+getStoreText(LABEL_DOCUMENTS));
    optionalParamsList.add(getLabel_more_results()+getStoreText(LABEL_MORE_RESULTS));
    optionalParamsList.add(getLabel_no_results()+getStoreText(LABEL_NO_RESULTS));
    optionalParamsList.add(getLabel_related_searches()+getStoreText(LABEL_RELATED));
    optionalParamsList.add(getLabel_search_button()+getStoreText(LABEL_SEARCH));
    optionalParamsList.add(getQ()+getStoreText(LABEL_QUERY));
    
    
    optionalParamsList.add(getNavigation());
    optionalParamsList.add(getPayload_size());
    optionalParamsList.add(getPersist_prodcat());
    optionalParamsList.add(getPreprocess());
    optionalParamsList.add(getRecommended());
    optionalParamsList.add(getRelated());
    optionalParamsList.add(getSearch_box());
    optionalParamsList.add(getTarget());
    optionalParamsList.add(getTruncate_size());
    optionalParamsList.add(getC());
    optionalParamsList.add(getContext());
    optionalParamsList.add(getP());
    
    return optionalParamsList;
  }

  /**
   * This method will append both the mandatory and optional parameters to the
   * response stream.It also appends the locale specific url to the knowledge 
   * base widget site and a responseCallBack script to the response stream.
   * 
   * @param pArgs the current ADC pipeline arguments
   * 
   * @return an ADC processor status code
   */
  public int updateADCData(ADCPipelineArgs pArgs) { 
    
    StringBuffer sb = new StringBuffer(ADD_COMPONENT);
    
    sb.append(newLine + getDiv_id());
    sb.append("," + newLine + getInstance_id());
    sb.append("," + newLine + getmModule());
    sb.append("," + newLine + getType());
    
    //optional parameters
    Iterator<String> itr =  createOptionalParamsList().iterator();
    
    while (itr.hasNext()) {
      Object param = itr.next();
      if(param instanceof String && param!=null)
      sb.append("," + newLine + param);
    }
    
    sb.append(newLine + "}, \"" + getLocaleUrl() + "/ci/ws/get\"");
    sb.append(newLine + ");" + newLine);

    sb.append("RightNow.Client.Controller.startFetching();" + newLine);

    sb.append("if(dojo.byId(\"showKnowledgebaseLink\")){" + newLine + "dojo.removeClass(\"showKnowledgebaseLink\", \"hideKnowledgebase\");" + newLine + "}" + newLine);
    
    sb.append(RESPONSE_CALL_BACK);
   
    sb.append("}" + newLine + "});" + newLine);
    
    sb.append(CROSS_DOMAIN_LISTENER);
    
    setValue(sb.toString());
    
    return super.updateADCData(pArgs);

  }
  
  /**
   * This method gets locale specific id url of the 
   * knowledge base widget from the SiteRepository
   * 
   * @return locale specific url 
   */ 
  public String  getLocaleUrl() {
    
    //getting a reference to the current site
    Site currentSite = SiteContextManager.getCurrentSiteContext().getSite();
    String url =  (String)currentSite.getPropertyValue(getSitePropertiesManager().getRightNowUrlsPropertyName());
    
    return url;
  }

  /**
   * This method gets locale specific knowledge base 
   * widget ui labels from the SiteRepository
   * 
   * @param textKey the store text key of the label
   * 
   * @return locale specific label 
   * 
   * @throws RepositoryException if error occurs
   */
  public String getStoreText(String textKey) {

     RepositoryItem[] items = null;
     RepositoryItem textItem = null;
     Object[] params = new Object[] { textKey };
     Repository repository = getRepository();
     
     String itemDescriptorName = getitemDescriptorName();
     String knowledgeBaseText="";
     
     RepositoryView repositoryView = null;
     RqlStatement rqlStatement = null;
     
     ResourceBundle resourceBundle = LayeredResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, ServletUtil.getUserLocale());
     try {
     repositoryView = repository.getView( itemDescriptorName);
     rqlStatement = RqlStatement.parseRqlStatement( RQL_QUERY_BY_KEY );
     items = rqlStatement.executeQuery( repositoryView, params); 
      
     if ( items != null && items.length > 0 ) {
       textItem = items[0];
     }
      
     if(textItem != null && textItem.getItemDescriptor().hasProperty(TEXT_PROPERTY_NAME) ) {
       knowledgeBaseText = (String)textItem.getPropertyValue(TEXT_PROPERTY_NAME);
     }
     } catch(RepositoryException e) {
     
     if (isLoggingDebug()) {
        logWarning(resourceBundle.getString(ERROR_REPOSITORY));
      }
     throw new RuntimeException(resourceBundle.getString(ERROR_REPOSITORY)+e);
     }

    return "\""+knowledgeBaseText+"\"";
    
   }
}
