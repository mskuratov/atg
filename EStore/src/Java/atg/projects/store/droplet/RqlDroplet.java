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



package atg.projects.store.droplet;

import atg.core.util.StringUtils;

import atg.nucleus.naming.ParameterName;

import atg.projects.store.logging.LogUtils;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import atg.repository.rql.RqlStatement;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import javax.transaction.*;


/**
 * <p>
 * This droplet is used in favor of the ATG RQLQueryForEach droplet. There are
 * two reasons for this. The first is that this droplet must be configured
 * from outside the JSP to prevent the setting of an RQL query string in the
 * JSP template. The second is that this droplet returns the result set (array
 * of repository items) which allows other droplets to loop through them
 * appropriately (Range for example). It also wraps the results in a
 * transaction, something RQLQueryForEach does not do.
 * </p>
 *
 * <p>
 * The repository, itemDescriptorName, and queryRql properties must be set on
 * the nucleus component that uses this class. For example:
 * <pre>
 *
 *  $class=com.awedirect.base.droplet.RqlDroplet
 *  transactionManager=/atg/dynamo/transaction/TransactionManager
 *  repository=/atg/commerce/catalog/ProductCatalog
 *  itemDescriptorName=promotionRelationship
 *  queryRql=contractType.code = ?0
 *
 * </pre>
 * </p>
 *
 * <p>
 * this droplet takes the following parameters
 *
 * <dl>
 * <dt>
 * numRQLParams
 * </dt>
 * <dd>
 * The parameter that defines the URL, relative or absolute, to which this page
 * that called the servlet will be redirected.
 * </dd>
 * </dl>
 *
 *
 * <dl>
 * <dt>
 * param#
 * </dt>
 * <dd>
 * The parameter.
 * </dd>
 * </dl>
 *
 *
 * <dl>
 * <dt>
 * rqlQuery
 * </dt>
 * <dd>
 * The parameter that defines a rqlQuery
 * </dd>
 * </dl>
 * </p>
 *
 * <p>
 * <b>Example </b><br>
 * <pre>
 *
 *
 *  &lt;dsp:droplet name=&quot;RqlDroplet&quot;&gt;
 *    &lt;param name=&quot;numRQLParams&quot; value=&quot;1&quot;&gt;
 *    &lt;param name=&quot;param0&quot; value=&quot;atg.com&quot;&gt;
 *    &lt;dsp:oparam name=&quot;output&quot;&gt;
 *      &lt;dsp:droplet name=&quot;ForEach&quot;&gt;
 *        &lt;dsp:param name=&quot;array&quot; param=&quot;items&quot;/&gt;
 *        &lt;dsp:oparam name=&quot;output&quot;&gt;
 *          &lt;dsp:getvalueof id=&quot;url&quot; idtype=&quot;java.lang.string&quot; param=&quot;element.url&quot;&gt;
 *          &lt;dsp:a page=&quot;&lt;=url&gt;&quot;&gt;
 *            &lt;dsp:valueof param=&quot;element.name&quot;/&gt;
 *          &lt;/dsp:a&gt;&lt;br&gt;
 *          &lt;/dsp:getvalueof&gt;
 *        &lt;/dsp:oparam&gt;
 *      &lt;/dsp:droplet&gt;
 *    &lt;/dsp:oparam&gt;
 *  &lt;/dsp:droplet&gt;
 *
 *
 * </pre>
 * </p>
 *
 * <p>
 * Parameters: <br>
 * &nbsp;&nbsp; <tt>empty</tt>- Rendered if no results found. <br>
 * &nbsp;&nbsp; <tt>output</tt>- Rendered on successful query. <br>
 * &nbsp;&nbsp;&nbsp;&nbsp; <tt>items</tt>- Array of RepositoryItem object
 * that match the query. <br>
 * </p>
 *
 * @author ATG
 * @version $Revision: #3 $
 */
public class RqlDroplet extends DynamoServlet {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/RqlDroplet.java#3 $$Change: 788278 $";

  /**
   * XA failure message.
   */
  public static final String XA_FAILURE = "Failure during transaction commit";

  /**
   * Output parameter name.
   */
  public static final ParameterName OUTPUT = ParameterName.getParameterName("output");

  /**
   * Empty parameter name.
   */
  public static final ParameterName EMPTY = ParameterName.getParameterName("empty");

  /**
   * Query RQL parameter name.
   */
  public static final ParameterName QUERY_RQL = ParameterName.getParameterName("queryRQL");

  /**
   * Number of RQL parameters parameter name.
   */
  public static final ParameterName NUMBER_OF_RQL_PARAMS = ParameterName.getParameterName("numRQLParams");

  /**
   * Parameter prefix.
   */
  public static final String PARAM_PREFIX = "param";

  /**
   * Items name.
   */
  public static final String ITEMS = "items";

  /**
   * Repository.
   */
  private Repository mRepository;

  /**
   * Item descriptor name.
   */
  private String mItemDescriptorName;

  /**
   * Query RQL.
   */
  private String mQueryRql;

  /**
   * List of RQL paramters.
   */
  private List mRqlParameters;

  /**
   * Transaction manager.
   */
  private TransactionManager mTransactionManager;

  /**
   * Statement map.
   */
  private Map mStatementMap = new HashMap();
  
  /**
   * Boolean that specifies whether resulted items should be returned as array
   * or as collection. False by default.
   */
  private boolean mResultAsCollection = false;

  /**
   *
   * @return Repository.
   */
  public Repository getRepository() {
    return mRepository;
  }

  /**
   *
   * @param pRepository - repository.
   */
  public void setRepository(Repository pRepository) {
    mRepository = pRepository;
  }

  /**
   *
   * @return item descriptor name.
   */
  public String getItemDescriptorName() {
    return mItemDescriptorName;
  }

  /**
   *
   * @param pItemDescriptorName - item descriptor name.
   */
  public void setItemDescriptorName(String pItemDescriptorName) {
    mItemDescriptorName = pItemDescriptorName;
  }

  /**
   *
   * @return RQL query text.
   */
  public String getQueryRql() {
    return mQueryRql;
  }

  /**
   *
   * @param pQueryRql - RQL query text.
   */
  public void setQueryRql(String pQueryRql) {
    mQueryRql = pQueryRql;
  }

  /**
   *
   * @return list of RQL query parameters.
   */
  public List getRqlParameters() {
    return mRqlParameters;
  }

  /**
   *
   * @param pRqlParameters - list of RQL query parameters.
   */
  public void setRqlParameters(List pRqlParameters) {
    mRqlParameters = pRqlParameters;
  }

  /**
   *
   * @return transaction manager.
   */
  public TransactionManager getTransactionManager() {
    return mTransactionManager;
  }

  /**
   *
   * @param pTransactionManager - transaction manager.
   */
  public void setTransactionManager(TransactionManager pTransactionManager) {
    mTransactionManager = pTransactionManager;
  }

  /**
   *
   * @return statement map.
   */
  public Map getStatementMap() {
    return mStatementMap;
  }
  
  /**
  *
  * @return  boolean that specifies whether result should be returned as collection
  *          or as array.
  */
 public boolean isResultAsCollection() {
   return mResultAsCollection;
 }

 /**
  *
  * @param pResultAsCollection boolean that specifies whether result should be returned as collection
  *   or as array.
  */
 public void setResultAsCollection(boolean pResultAsCollection) {
   mResultAsCollection = pResultAsCollection;
 }

  /**
   * Service method.
   * @param pRequest DynamoHttpServletRequest
   * @param pResponse DynamoHttpServletResponse
   *
   * @throws ServletException if an error occurs
   * @throws IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    RepositoryItem[] results = null;

    Repository repository = getRepository();
    String descName = getItemDescriptorName();

    // Check to see if queryRQL was passed in from page
    String query = (String)pRequest.getLocalParameter(QUERY_RQL);

    if (query == null) {
      query = getQueryRql();
    }

    Object[] queryParams = getQueryParameters(pRequest, pResponse);

    if (repository == null && isLoggingDebug()) {      
      String err = "No repository for this droplet used on page " + pRequest.getRequestURI();
      logDebug(LogUtils.formatMajor(err));
    }

    if (StringUtils.isEmpty(descName) && isLoggingDebug()) {
      String err = "No item descriptor name for this droplet used on page " + pRequest.getRequestURI();
      logDebug(LogUtils.formatMajor(err));    
    }

    if (StringUtils.isEmpty(query) && isLoggingDebug()) {     
      String err = "No query for this droplet used on page " + pRequest.getRequestURI();
      logDebug(LogUtils.formatMajor(err));
    }

    try {
      // parse the rql statement, storing the statement in
      // the map for performance
      RqlStatement statement = (RqlStatement) getStatementMap().get(query);

      if (statement == null) {
        statement = RqlStatement.parseRqlStatement(query);
        getStatementMap().put(query, statement);
      }

      if (isLoggingDebug()) {
        logDebug("Querying with statement " + statement);
      }

      results = performQuery(repository, descName, statement, queryParams);
    } catch (RepositoryException re) {
      if (isLoggingError()) {
        String err = "RepositoryException using a droplet on a page occurred.";
        logError(LogUtils.formatMajor(err), re);
      }
    }

    if (isLoggingDebug()) {
      int numResults = 0;

      if (results != null) {
        numResults = results.length;
      }

      logDebug("Found " + numResults + " items for query");
    }

    if ((results == null) || (results.length <= 0)) {
      pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
    } else {
      if (isResultAsCollection()){
        //return items as collection        
        Collection <RepositoryItem> resultedCollections = new ArrayList <RepositoryItem>(results.length);
        for(RepositoryItem item : results){
          resultedCollections.add(item);
        }
        pRequest.setParameter(ITEMS, resultedCollections);
      }else{
        //return items as array
        pRequest.setParameter(ITEMS, results);
      } 
      pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
    }
  }

  /**
   * Get query parameters.
   *
   * @param pRequest - http request
   * @param pResponse - http response
   *
   * @return array of query parameters
   *
   * @throws ServletException if error occurs
   * @throws IOException if an error occurs
   */
  protected Object[] getQueryParameters(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    // Stores the number of rql parameters as a string
    String numRQLParamsString = (String)pRequest.getLocalParameter(NUMBER_OF_RQL_PARAMS);

    // The integer representation of the number of RQL parameters
    int numRQLParams = 0;

    // The object parameter array
    Object[] params = null;

    // If the string representation of the number of RQL parameters
    // is null then we will assume no substitution and use the default
    // supplied by getRqlParamters
    if (null == numRQLParamsString) {
      if (isLoggingDebug()) {
        logDebug("No parameter quantity indicated checking rqlParameters property");
      }

      // No parameter query use property setting
      List defaultParams = getRqlParameters();

      // build the query params
      int size = 0;

      if (params != null) {
        size = defaultParams.size();
      }

      Object[] queryParams = new Object[size];

      for (int i = 0; i < size; i++) {
        queryParams[i] = defaultParams.get(i);
      }
    } else {
      // Convert the string representation of the number of rql
      // parameters into an integer. If we are unable to then we
      // probably got a number format exception which we should
      // report as an error
      try {
        numRQLParams = Integer.parseInt(numRQLParamsString);
      } catch (NumberFormatException n) {
        if (isLoggingError()) {
          // Log this specific error
          logError("The numRQLParams passed is not a number on the page ", n);
        }

        // Return gracefully
        return null;
      }

      if (isLoggingDebug()) {
        logDebug("numRQLParams passed is " + numRQLParams);
      }

      // Create an array of parameters for the number of parameters
      // specified
      params = new Object[numRQLParams];

      // The temporary parameter value holder
      String paramValue = null;

      // Now loop through all the parameters defined and grab their values
      for (int i = 0; i < numRQLParams; i++) {
        // Grab each parameter valu and dump it into the params array
        paramValue = (String) pRequest.getObjectParameter(PARAM_PREFIX + i);

        // If debug log is turned on
        if (isLoggingDebug()) {
          // Get all the parameters of interest
          logDebug(PARAM_PREFIX + i + "=" + paramValue);
        }

        params[i] = paramValue;
      }
    }

    return params;
  }

  /**
   * <p>
   * Performs the query against the view of the particular repository.
   * </p>
   *
   * @param pRepository - repository
   * @param pViewName - view name
   * @param pStatement - statement
   * @param pParams - parameters
   *
   * @return selected data array
   *
   * @throws RepositoryException if an error occurs
   */
  protected RepositoryItem[] performQuery(Repository pRepository, String pViewName, RqlStatement pStatement,
    Object[] pParams) throws RepositoryException {
    RepositoryItem[] items = null;

    // begin transaction
    Transaction trx = ensureTransaction();

    try {
      // execute query
      RepositoryView view = pRepository.getView(pViewName);
      items = pStatement.executeQuery(view, pParams);
    } finally {
      if (trx != null) {
        try {
          trx.commit();
        } catch (RollbackException exc) {
          if (isLoggingError()) {
            logError(LogUtils.formatMajor(XA_FAILURE), exc);
          }
        } catch (HeuristicMixedException exc) {
          if (isLoggingError()) {
            logError(LogUtils.formatMajor(XA_FAILURE), exc);
          }
        } catch (HeuristicRollbackException exc) {
          if (isLoggingError()) {
            logError(LogUtils.formatMajor(XA_FAILURE), exc);
          }
        } catch (SystemException exc) {
          if (isLoggingError()) {
            logError(LogUtils.formatMajor(XA_FAILURE), exc);
          }
        }
      }
    }

    return items;
  }

  /**
   * Attempts to get current transaction from TransactionManager. If no
   * existing transaction, attempts to start one.
   *
   * @return transaction 
   */
  private Transaction ensureTransaction() {
    TransactionManager trxMgr = getTransactionManager();

    try {
      Transaction trx = trxMgr.getTransaction();

      if (trx == null) {
        trxMgr.begin();

        return trxMgr.getTransaction();
      } else {
        // transaction already exists, don't start and don't commit
        return null;
      }
    } catch (NotSupportedException exc) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("failure getting transaction: "), exc);
      }
    } catch (SystemException exc) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("failure getting transaction: "), exc);
      }
    }

    return null;
  }
}
