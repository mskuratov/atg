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


package atg.projects.store.servlet.pipeline;

import atg.nucleus.ServiceException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import atg.servlet.pipeline.InsertableServletImpl;

import java.io.IOException;

import javax.servlet.ServletException;


/**
 * This pipeline servlet performs switching between a secure sever and a
 * non-secure server. A list of secure paths and the enable property controls
 * the switching. The servlet is configured with a list of URL mappings; if the
 * URL being accessed is in the URL mapping, the request is passed off to the
 * secure server. The nonSecureHostName and secureHostname are default taken
 * from atg/dynamo/Configuration. These can be overwridden at the component
 * level.
 *
 * @author ATG
 */
public class ProtocolSwitchServlet extends InsertableServletImpl {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/servlet/pipeline/ProtocolSwitchServlet.java#2 $$Change: 768606 $";

  //-------------------------------------
  // Constants
  //-------------------------------------

  /**
   * Secure protocol constant.
   */
  public static final String SECURE_PROTOCOL = "https";

  /**
   * Nonsecure protocol constant.
   */
  public static final String NONSECURE_PROTOCOL = "http";

  /** List of secure paths to protect. */
  protected String[] mSecureList = null;

  /** List of paths to ignore. */
  protected String[] mIgnoreList = null;

  /** Host name. */
  protected String mHostName;

  /** Http port. */
  protected int mHttpPort;

  /** Https port. */
  protected int mHttpsPort;

  /** Secure host name. */
  protected String mSecureHostName;

  /**
   * Enabled property.
   */
  protected boolean mEnabled = false;

  /**
   * @return the ignore list.
   */
  public String[] getIgnoreList() {
    return mIgnoreList;
  }

  /**
   * @param pIgnoreList - the ignore list to set.
   */
  public void setIgnoreList(String[] pIgnoreList) {
    mIgnoreList = pIgnoreList;
  }

  /**
   * @return secure host name.
   */
  public String getSecureHostName() {
    return mSecureHostName;
  }

  /**
   * @param pSecureHostName - secure host name.
   */
  public void setSecureHostName(String pSecureHostName) {
    mSecureHostName = pSecureHostName;
  }

  /**
   * @return https port.
   */
  public int getHttpsPort() {
    return mHttpsPort;
  }

  /**
   * @param pHttpsPort - https port.
   */
  public void setHttpsPort(int pHttpsPort) {
    mHttpsPort = pHttpsPort;
  }

  /**
   * @return http port.
   */
  public int getHttpPort() {
    return mHttpPort;
  }

  /**
   * @param pHttpPort - http port.
   */
  public void setHttpPort(int pHttpPort) {
    mHttpPort = pHttpPort;
  }

  /**
   * @return host name.
   */
  public String getHostName() {
    return mHostName;
  }

  /**
   * @param pHostName - host name.
   */
  public void setHostName(String pHostName) {
    mHostName = pHostName;
  }

  /**
   * Set the secure list. This should normally be a directory, but can be the
   * prefix to any portion of a url.
   *
   * @param pSecureList - array of protected urls
   */
  public void setSecureList(String[] pSecureList) {
    mSecureList = pSecureList;
  }

  /**
   * @return list of pages that should be rendered by the secure server.
   */
  public String[] getSecureList() {
    return mSecureList;
  }

  /**
   * @return the enabled status.
   */
  public boolean isEnabled() {
    return mEnabled;
  }

  /**
   * @param pEnabled - the enabled status to set.
   */
  public void setEnabled(boolean pEnabled) {
    mEnabled = pEnabled;
  }

  /**
   * Called in the pipeline when a request needs to be processed. The
   * following conditions cause the request to be passed off to the next
   * servlet in the pipeline:
   * <li>If a protocol switch is not required Otherwise, a call is made to
   * ProtocolSwitchURLGenerator to generate a URL for the protocol to be
   * switched to by checking if the requested URI is in the secure list.
   *
   * @param pRequest
   *            The servlet's request
   * @param pResponse
   *            The servlet's response
   * @exception ServletException
   *                if an error occurred while processing the servlet request
   * @exception IOException
   *                if an error occurred while reading or writing the servlet
   *                request
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws IOException, ServletException {
    if (mEnabled) {
      // get the URI w/o the query string to check is it is in the
      // list of secure pages
      String pathWithoutQueryString = pRequest.getRequestURI();

      // Also get the path w/ query string to be used in the redirect
      String path = pRequest.getRequestURIWithQueryString();

      if (isLoggingDebug()) {
        logDebug("Performing protocol switch on " + path);
      }

      // check if the URI requested is in the list of pages to ignore
      if (isCurrentPathInIgnoreList(pathWithoutQueryString)) {
        if (isLoggingDebug()) {
          logDebug("Ignoring " + path);
        }

        passRequest(pRequest, pResponse);
      } else {
        // check if the URI requested is in the list of pages to
        // be rendered by the secure server
        boolean uriRequireSecure = isCurrentPathInSecureList(pathWithoutQueryString);

        // Determine what the current scheme is, HTTP or HTTPS
        boolean isSchemeSecure = false;

        if (isLoggingDebug()) {
          logDebug("Scheme is " + pRequest.getScheme());
        }

        if ((pRequest.getScheme() != null) && pRequest.getScheme().equalsIgnoreCase(SECURE_PROTOCOL)) {
          isSchemeSecure = true;
        }

        // if we need to be on a secure page but are currently on a
        // non-secure
        // server
        if (mEnabled && uriRequireSecure && !isSchemeSecure) {
          // get the URL to the secure server
          String redirectURL = getSecureUrl(path);

          if (isLoggingDebug()) {
            logDebug("Page is secure but is on non-secure server");
            logDebug("Redirecting to: " + redirectURL);
            logDebug("Encoded URL: " + pResponse.encodeRedirectURL(redirectURL));
          }

          if (pRequest.getMethod().equals("POST")) {
            throw new ServletException("cannot switch on post");
          }

          pResponse.sendRedirect(pResponse.encodeRedirectURL(redirectURL));
        }
        // if we need to be on a non-secure page but are currently on a
        // secure
        // server
        else if (!uriRequireSecure && isSchemeSecure) {
          // get the URL to the non-secure server
          String redirectURL = getNonSecureUrl(path);

          if (isLoggingDebug()) {
            logDebug("Page is non-secure but is on secure server");
            logDebug("Redirecting to: " + redirectURL);
            logDebug("Encoded URL: " + pResponse.encodeRedirectURL(redirectURL));
          }

          if (pRequest.getMethod().equals("POST")) {
            throw new ServletException("cannot switch on post");
          }

          pResponse.sendRedirect(pResponse.encodeRedirectURL(redirectURL));
        } else {
          if (isLoggingDebug()) {
            logDebug("No need to change");
          }

          passRequest(pRequest, pResponse);
        }
      }
    } else {
      passRequest(pRequest, pResponse);
    }
  }

  /**
   * Method used check if the requested URI is part of the secure list. We
   * assume that the secure list contains paths with the context root if
   * needed. This allows us to protect servlets that aren't under the context
   * root as well.
   *
   * @param pPath
   *            The requested URI without query parameters
   * @return true if the requested URI is in the secure list, false otherwise.
   */
  protected boolean isCurrentPathInSecureList(String pPath) {
    boolean isSecure = false;

    if ((mSecureList != null) && (mSecureList.length > 0)) {
      for (int i = 0; i < mSecureList.length; i++) {
        if (isLoggingDebug()) {
          logDebug("matching " + mSecureList[i] + " against " + pPath);
        }

        if (pPath.startsWith(mSecureList[i])) {
          return true;
        }
      }
    }

    return isSecure;
  }

  /**
   * Method used check if the requested URI is part of the ignored list. We
   * assume that the ignored list contains paths with the context root if
   * needed. This allows us to protect servlets that aren't under the context
   * root as well.
   *
   * @param pPath
   *            The requested URI without query parameters
   * @return true if the requested URI is in the secure list, false otherwise.
   */
  protected boolean isCurrentPathInIgnoreList(String pPath) {
    boolean isIgnore = false;

    if ((mIgnoreList != null) && (mIgnoreList.length > 0)) {
      for (int i = 0; i < mIgnoreList.length; i++) {
        if (isLoggingDebug()) {
          logDebug("matching " + mIgnoreList[i] + " against " + pPath);
        }

        if (pPath.startsWith(mIgnoreList[i])) {
          return true;
        }
      }
    }

    return isIgnore;
  }

  /**
   * Build a nonSecure url. If passed /cmo/user/foo/, this will create
   * http://localhost:8840/cmo/user/foo/ (if localhost is the configured
   * hostname)
   *
   * @param pUrl - url to build full url for
   * @return nonsecure URL
   */
  protected String getNonSecureUrl(String pUrl) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(NONSECURE_PROTOCOL);

    int httpPort = getHttpPort();
    buffer.append("://");

    buffer.append(getHostName());

    if (httpPort != 0) {
      buffer.append(":" + httpPort);
    }

    buffer.append(pUrl);

    return buffer.toString();
  }

  /**
   * Build a Secure url. If passed /cmo/user/foo/, this will create
   * https://localhost:8843/cmo/user/foo/ (if localhost is the configured
   * hostname)
   *
   * @param pUrl - url to build full url for
   * @return secure URL
   */
  protected String getSecureUrl(String pUrl) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(SECURE_PROTOCOL);

    int httpsPort = getHttpsPort();
    buffer.append("://");

    // XXX - make property
    buffer.append(getSecureHostName());

    if (httpsPort != 0) {
      buffer.append(":" + httpsPort);
    }

    buffer.append(pUrl);

    return buffer.toString();
  }

  //-------------------------------------
  // GenericService overrides
  //-------------------------------------

  /**
   * Called after the service has been created, placed into the naming
   * hierarchy, and initialized with its configured property values. Makes
   * sure all the required properties have been set in the .properties file.
   *
   * @exception ServiceException
   *                if the service had a problem starting up
   */
  public void doStartService() throws ServiceException {
    if (getSecureHostName() == null) {
      throw new ServiceException("secureHostName not set");
    }

    if (getHostName() == null) {
      throw new ServiceException("hostName not set");
    }

    if (getHttpPort() == 0) {
      throw new ServiceException("httpPort cannot be zero");
    }

    if (getHttpsPort() == 0) {
      throw new ServiceException("httpsPort cannot be zero");
    }

    super.doStartService();
  }
}
