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

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import atg.core.util.NumberTable;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.nucleus.naming.ParameterName;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;


/**
 * <p>
 * This droplet performs a look up in the repository to obtain localized resource text message templates either
 * by matching on a specific key to obtain a single resource text message template or by matching on a tag to
 * obtain zero or more resource text messages 'tagged' with this value. It is not possible to find a match on
 * both key and tag values, if a key is supplied that is the value used to find a match.
 * The 'key' property is a unique value in the repository so only a single message template will match on any
 * particular key value. The 'tag' property is not a unique value across message templates but there can only 
 * be one tag per text resource. So a text message template can be tagged as 'news' but not as both 'news' and
 * 'latest-news'.
 * </p>
 *
 * <p>
 * When performing a key match we firstly try to find a site specific resource text message template by matching
 * on a site version of the supplied key value or if no match is found we try to find a match on the supplied
 * key value. If no match is found in the repository for either key versions then:
 * 
 * <ul>
 *   <li>if the default text input parameter is not empty then this is used as the message text</li>
 *   <li>if the default text input parameter is empty and the default text property is not empty then
 *   the default text property is used as the message text</li>
 *   <li>otherwise the message text is set as an empty string</li>
 * </ul>
 * No default text is returned when performing a tag match. In this case an empty string array is returned.
 * </p>
 *
 * <p>
 * The message templates are then parsed to substitute any format pattern specifiers with the corresponding
 * entry value obtained from either the 'args' input parameter map or from the request input parameters.
 * </p>
 * 
 * <p>
 * The message template can consist of static text elements and zero or more format pattern specifiers. A format
 * pattern specifier is defined as a region of text enclosed by '{' and '}' characters. This allows the page
 * coder to create dynamic message content based on the message template and substituting values for the format
 * pattern specifiers at runtime.
 * </p>
 * 
 * <p>
 * For example,
 *   The quick {color} fox {action} over the {object}.
 *
 * with matching format pattern values as follows
 *   color=brown
 *   action=jumps
 *   object=lazy dog
 *
 * resolves as
 *  The quick brown fox jumps over the lazy dog.
 *
 * The message template can contain '\\', '\{', '\}' escape sequences. This allows '\', '{', and '}' characters
 * to appear in the message and not be treated as format specifier pattern delimiters.
 * If the message template was defined as
 *   The \{ quick {color} fox \} {action} over the {object}.
 *
 * this resolves as
 *   The { quick brown fox } jumps over the lazy dog.
 *
 * Unknown escape sequences ignore the '\' character, so
 *   The \q quick {color} fox {action} over the {object}.
 *
 * will resolve as
 *   The q quick brown fox jumps over the lazy dog.
 *
 * Text within the format pattern delimiters is taken literally and will ignore escape sequences, so
 *   The quick {c\qolor\} fox {action} over the {object}.
 *
 * will pick up 'c\qolor\' as a format pattern specifier.
 *
 * If multiple parameters matching the same format pattern specifier are passed to the droplet then it is the
 * last parameter which will be used. So passing in a 'color' parameter twice with values 'brown' & 'black' will
 * insert 'black' into the example above.
 *
 * Any parameter name/values or map entries passed into the droplet but which do not match on a format pattern
 * specifier in the message template are ignored.
 *
 * Any format pattern specifiers present in the message template but which do not have a corresponding value passed
 * into the droplet remain untouched in the resulting message.
 *
 * The droplet does not perform any strong verification on the message template form so
 *   The quick {color fox jumps over the lazy dog.
 *
 * is a valid template, there is no malformed exception for the unclosed format pattern.
 * As there is no format pattern specifier sequence recognized in the template, the message resolves as
 *   The quick {color fox jumps over the lazy dog.
 * </p>
 * 
 * <p>
 * The droplet takes the following input parameters:
 * 
 * <ul>
 *   <li>key (optional, required if 'tag' attribute not supplied)
 *   The key code to use when looking up the resource message text in the repository.
 *
 *   <li>tag (optional, required if 'key' attribute not supplied)
 *   The tag to use when looking up the resource message text in the repository..
 *
 *   <li>args (optional)
 *   A map object consisting of a number of entries to use when populating the format pattern specifiers embedded
 *   in the message template text. The format pattern specifiers in the message template are used to match on
 *   the map key values and the corresponding map entry value is substituted for the format pattern in the resulting
 *   message text.
 *
 *   <li>arg name (optional)
 *   An arbitrary number of parameters to use when populating the format pattern specifiers embedded in the message
 *   template text. The actual 'arg' name is not defined; the format pattern specifiers in the message template are
 *   used as possible parameter names and the parameter value is substituted for the format pattern in the resulting
 *   message text.
 *
 *   <li>defaultText (optional)
 *   The default text to output when a message for the given key could not be found.
 * </ul>  
 * </p>
 * 
 * <p>
 * <ul>
 * The droplet renders the following open parameters:
 *   <li>output
 *   message - the localized resource message text. If parameter is list,
 *   this parameter renders for every list entry.
 *   <li>outputStart - if the item of type storeTextList, this parameter renders
 *   before list iteration
 *   <li>outputEnd - if the item of type storeTextList, this parameter renders
 *   after list iteration
 *   <li>empty - if no items found matching given key or tag
 *   <li>error
 *   message - error message if a problem occurred processing the request
 * </ul>  
 * </p>
 * 
 * <p>
 * Example:
 * <pre>
 * {@code
 *  <dsp:importbean bean="/atg/store/droplet/StoreText"/>
 *
 *  <!-- Key handling --!>
 *  <c:set var="key" value="company_aboutUs.aboutUs"/>
 *  <c:set var="storeName" value="ATG Store"/>
 *
 *  <dsp:droplet name="StoreText">
 *    <dsp:param name="key" value="${key}"/>
 *    <dsp:param name="storeName" value="${storeName}"/>

 *    <dsp:oparam name="output">
 *      <dsp:getvalueof var="message" param="message"/>
 *      <c:out value="[${key}: ${message}]" escapeXml="false"/>
 *    </dsp:oparam>
 *    <dsp:oparam name="error">
 *      <dsp:getvalueof var="message" param="message"/>
 *      <c:out value="[Error: ${key}: ${message}]" escapeXml="false"/>
 *    </dsp:oparam>
 *  </dsp:droplet>
 * 
 *  <!-- Tag handling --!>
 *  <c:set var="tag" value="news"/>
 *
 *  <dsp:droplet name="StoreText">
 *    <dsp:param name="tag" value="${tag}"/>
 *
 *    <dsp:oparam name="output">
 *      <dsp:getvalueof var="messages" param="messages"/>
 *      <c:out value="[${tag}:"/>
 *      <c:forEach items="${messages}"
 *                 var="message">
 *        <c:out value="  ${message}" escapeXml="false"/>
 *      </c:forEach>
 *      <c:out value="]"/>
 *    </dsp:oparam>
 *    <dsp:oparam name="error">
 *      <dsp:getvalueof var="message" param="message"/>
 *      <c:out value="[Error: ${key}: ${message}]" escapeXml="false"/>
 *    </dsp:oparam>
 *  </dsp:droplet>
 *  
 *  <!-- List handling --!>
 *  <dsp:droplet name="StoreText">
      <dsp:param name="key" value="benefitsList"/>
      <dsp:oparam name="outputStart">
        <ul>
      </dsp:oparam>
      
      <dsp:oparam name="output">
        <li>
          <dsp:valueof param="message"/>
        </li>
      </dsp:oparam>
      
      <dsp:oparam name="outputEnd">
        </ul>
      </dsp:oparam>
    </dsp:droplet>
 *  }
 * </pre>
 * </p>
 * 
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/StoreTextDroplet.java#3 $$Change: 788278 $
 * @updated $DateTime: 2013/02/05 00:41:33 $$Author: jsiddaga $
 */
public class StoreTextDroplet extends DynamoServlet {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/droplet/StoreTextDroplet.java#3 $$Change: 788278 $";


  /*
   * Input parameter names
   */
  private final static String KEY_ATTRIBUTE_NAME         = "key";
  private final static String TAG_ATTRIBUTE_NAME         = "tag";
  private final static String ARGS_ATTRIBUTE_NAME        = "args";
  private final static String DEFAULTTEXT_ATTRIBUTE_NAME = "defaultText";

  /*
   * RQL queries
   */
  /**
   * RQL Query repository by site or key
   */
  protected final static String RQL_QUERY_BY_SITE_OR_KEY = "key = ?0 OR key = ?1";
  /**
   * RQL Query repository by key
   */
  protected final static String RQL_QUERY_BY_KEY = "key = ?0";
  /**
   * RQL Query repository by site or tag
   */
  protected final static String RQL_QUERY_BY_SITE_OR_TAG = "tag = ?0 OR tag = ?1";
  /**
   * RQL Query repository by tag
   */
  protected final static String RQL_QUERY_BY_TAG = "tag = ?0";
  
  /*
   * Message oparam output values
   */
  private final static String  MESSAGE  = "message";
  private final static String  ITEM = "item";
  private final static String  SIZE = "size";
  private final static String  INDEX = "index";
  private final static String  COUNT = "count";
  
  private final static ParameterName OUTPUT  = ParameterName.getParameterName( "output" );
  private final static ParameterName OUTPUT_START  = ParameterName.getParameterName( "outputStart" );
  private final static ParameterName OUTPUT_END  = ParameterName.getParameterName( "outputEnd" );
  private final static ParameterName EMPTY  = ParameterName.getParameterName( "empty" );

  /*
   * Error message oparam output values
   */
  private final static String  ERROR_MESSAGE = "message";
  private final static ParameterName ERROR   = ParameterName.getParameterName( "error" );

  /*
   * Format characters relevant within the context of the resource text message template.
   */
  private final static char ESCAPE_CHAR = '\\';
  private final static char START_FORMAT_PATTERN_CHAR = '{';
  private final static char END_FORMAT_PATTERN_CHAR   = '}';
  
  /*
   * StoreTextRepository constants
   */
  public static final String TEXT_PROPERTY_NAME = "text";
  public static final String TYPE_PROPERTY_NAME = "type";
  public static final String LIST_PROPERTY_NAME = "list";
  
  public static final String LIST_TYPE = "textList";

  /*
   * Global constants
   */
  private final static String SITE_ID = "siteId";
  private final static String ERROR_MESSAGE_TXT = "";

  /**
   * Modes for matching repository resource text templates
   */
  private enum Mode { BY_KEY, BY_TAG, UNKNOWN };


  /**
   * The default text string to use if the repository does not contain a text resource message
   * with a matching key value. The default text template can contain '{key}' format pattern
   * specifiers with the '{key}' pattern being populated in the resulting text with the 'key' value.
   */
  private String mDefaultTextTemplate = "";

  /**
   * Sets the defaultTextTemplate
   * @param pDefaultTextTemplate the defaultTextTemplate to set
   */
  public void setDefaultTextTemplate( String pDefaultTextTemplate ) {    
    String defaultTextTemplate = pDefaultTextTemplate;
    if ( defaultTextTemplate == null ) {
      defaultTextTemplate = "";
    }

    mDefaultTextTemplate = defaultTextTemplate;
  }

  /**
   * Gets the defaultTextTemplate
   * @return the defaultTextTemplate
   */
  public String getDefaultTextTemplate() {
    return( mDefaultTextTemplate );
  }

  /**
   * Gets defaultText by key
   * @param pKey the key
   * @return the defaultText
   */
  public String getDefaultText( String pKey ) {
    Map args = new HashMap();

    String defaultText = "";

    args.put( KEY_ATTRIBUTE_NAME,
              pKey );

    defaultText = parseTemplate( getDefaultTextTemplate(),
                                 args );

    return( defaultText );
  }


  /**
   * The template string defining the site specific key format. This can contain '{siteId}' and '{key}'
   * format pattern specifiers with the '{siteId}' pattern being populated by the current site id
   * if any, and the '{key}' pattern being populated in the with the 'key' value.
   * Defaults to {siteId}.{key}
   */
  private String mSiteKeyTemplate = "{" + SITE_ID + "}.{" + KEY_ATTRIBUTE_NAME + "}";

  /**
   * @param pSiteKeyTemplate the siteKeyTemplate to set
   */
  public void setSiteKeyTemplate( String pSiteKeyTemplate ) {
    String siteKeyTemplate = pSiteKeyTemplate;
    if ( siteKeyTemplate == null ) {
      siteKeyTemplate = "";
    }

    mSiteKeyTemplate = siteKeyTemplate;
  }

  /**
   * @return the siteKeyTemplate
   */
  public String getSiteKeyTemplate() {
    return( mSiteKeyTemplate );
  }

  /**
   * @param pKey the key to get
   * @return the siteKey
   */
  public String getSiteKey( String pKey ) {
    Map args = new HashMap();

    String siteKey = "";

    String siteId = SiteContextManager.getCurrentSiteId();


    if ( !StringUtils.isEmpty(siteId) ) {
      args.put( SITE_ID,
                siteId );

      args.put( KEY_ATTRIBUTE_NAME,
                pKey );

      siteKey = parseTemplate( getSiteKeyTemplate(),
                               args );
    }
    else {
      siteKey = "";
    }

    return( siteKey );
  }


  /**
   * The template string defining the site specific tag format. This can contain '{siteId}' and '{tag}'
   * format pattern specifiers with the '{siteId}' pattern being populated by the current site id
   * if any, and the '{tag}' pattern being populated in the with the 'tag' value.
   * Defaults to {siteId}.{tag}
   */
  private String mSiteTagTemplate = "{" + SITE_ID + "}.{" + TAG_ATTRIBUTE_NAME + "}";

  /**
   * @param pSiteTagTemplate the siteTagTemplate to set
   */
  public void setSiteTagTemplate( String pSiteTagTemplate ) {
    String siteTagTemplate = pSiteTagTemplate;
    
    if ( siteTagTemplate == null ) {
      siteTagTemplate = "";
    }

    mSiteTagTemplate = siteTagTemplate;
  }

  /**
   * @return the siteTagTemplate
   */
  public String getSiteTagTemplate() {
    return( mSiteTagTemplate );
  }

  /**
   * @param pTag the tag to get
   * @return the siteTag
   */
  public String getSiteTag( String pTag ) {
    Map args = new HashMap();

    String siteTag = "";

    String siteId = SiteContextManager.getCurrentSiteId();


    if ( !StringUtils.isEmpty(siteId) ) {
      args.put( SITE_ID,
                siteId );

      args.put( TAG_ATTRIBUTE_NAME,
                pTag );

      siteTag = parseTemplate( getSiteTagTemplate(),
                               args );
    }
    else {
      siteTag = "";
    }

    return( siteTag );
  }


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
    return( mRepository );
  }


  /**
   * The item descriptor to use when retrieving the text resource 
   * message strings from the repository.
   */
  private String mItemDescriptorName = null;

  /**
   * @param pItemDescriptorName the itemDescriptorName to set
   */
  public void setItemDescriptorName( String pItemDescriptorName ) {
    mItemDescriptorName = pItemDescriptorName;
  }

  /**
   * @return the mItemDescriptorName
   */
  public String getItemDescriptorName() {
    return( mItemDescriptorName );
  }


  /**
   * Returns either a site specific resource text message template string by matching on the site version
   * of the supplied key value, or if no site specific resource text template is found then returns the
   * resource text template matching on the supplied key value.
   * An empty string is returned if no match for either is found.
   *
   * @param pKey The key value to match the resource text on.
   *
   * @return The text resource template string matched on the key value or an
   *          empty string if no match is found.
   *
   * @throws RepositoryException If there was an error accessing the repository.
   */
  private String getMessageTemplateByKey( String pKey )
    throws RepositoryException {

    String messageTemplate = null;
    RepositoryItem messageItem = getMessageItemByKey(pKey);
    if(messageItem != null && messageItem.getItemDescriptor().hasProperty(TEXT_PROPERTY_NAME)) {
      messageTemplate = (String)messageItem.getPropertyValue(TEXT_PROPERTY_NAME);
    }
    
    return StringUtils.isEmpty(messageTemplate) ? "" : messageTemplate; 
  }
  
  /**
   * Returns site specific repository item by matching on the site version
   * of the supplied key value, or if no site specific resource text template
   * is found then returns the repository item matching on the supplied key value.
   * 
   * @param pKey The key value to match the resource text on.
   * @return repository item with resource template string matched on the key value
   * or null if no match is found. 
   * 
   * @throws RepositoryException If there was an error accessing the repository.
   */
  private RepositoryItem getMessageItemByKey(String pKey)
    throws RepositoryException {
    String itemDescriptorName = getItemDescriptorName();

    Repository repository = getRepository();

    RepositoryView repositoryView = repository.getView( itemDescriptorName );

    String key = pKey;
    String siteKey = getSiteKey( pKey );

    RepositoryItem messageItem = null;


    if ( !StringUtils.isEmpty(siteKey) ) {
      /*
       * Look up the repository for a match on either the site specific version
       * of the supplied key or on the supplied key itself
       */      
      RqlStatement rqlStatement = RqlStatement.parseRqlStatement( RQL_QUERY_BY_SITE_OR_KEY );

      Object[] params = new Object[] { siteKey, key };

      RepositoryItem[] items = rqlStatement.executeQuery( repositoryView,
                                                          params );

      if ( items != null
           && items.length > 0 ) {
        if ( items.length > 1
             && ((String)items[0].getPropertyValue(KEY_ATTRIBUTE_NAME)).length() >= ((String)items[1].getPropertyValue(KEY_ATTRIBUTE_NAME)).length() ) {
          messageItem = items[0];
        }
        else if ( items.length > 1
                  && ((String)items[1].getPropertyValue(KEY_ATTRIBUTE_NAME)).length() > ((String)items[0].getPropertyValue(KEY_ATTRIBUTE_NAME)).length() ) {
          messageItem = items[1];
        }
        else {
          messageItem = items[0];
        }
      }
    }
    else {
      /*
       * Look up the repository for a match on the supplied key
       */
      RqlStatement rqlStatement = RqlStatement.parseRqlStatement( RQL_QUERY_BY_KEY );

      Object[] params = new Object[] { key };

      RepositoryItem[] items = rqlStatement.executeQuery( repositoryView,
                                                          params );

      if ( items != null
           && items.length > 0 ) {
        messageItem = items[0];
      }
    }

    return messageItem;
  }
  
  /**
   * Returns all repository items matching on the site version 
   * of the supplied tag value and also the supplied tag value.
   * 
   * @param pTag The tag value to match the resource text on.
   * @return Array of repository items matched on the tag value or null if no match is found.
   * 
   * @throws RepositoryException If there was an error accessing the repository.
   */
  private RepositoryItem[] getMessageItemsByTag(String pTag)
    throws RepositoryException {
    String itemDescriptorName = getItemDescriptorName();

    Repository repository = getRepository();

    RqlStatement rqlStatement = null;

    Object[] params = null;

    String tag = pTag;
    String siteTag = getSiteTag( pTag );

    RepositoryItem[] messageItems = null;

    /*
     * Look up the repository for a match on the supplied tag
     */
    RepositoryView repositoryView = repository.getView( itemDescriptorName );


    if ( !StringUtils.isEmpty(siteTag)
         && !StringUtils.isEmpty(tag) ) {
      rqlStatement = RqlStatement.parseRqlStatement( RQL_QUERY_BY_SITE_OR_TAG );

      params = new Object[] { siteTag, tag };
    }

    if ( StringUtils.isEmpty(siteTag)
         && !StringUtils.isEmpty(tag) ) {
      rqlStatement = RqlStatement.parseRqlStatement( RQL_QUERY_BY_TAG );

      params = new Object[] { tag };
    }

    if ( !StringUtils.isEmpty(siteTag)
         && !StringUtils.isEmpty(tag) ) {
      
      if (rqlStatement != null) {
        messageItems = rqlStatement.executeQuery( repositoryView, params );
      }
    }

    return messageItems;
  }


  /**
   * Returns all resource text message template strings matching on the site version of the supplied tag
   * value and also the supplied tag value.
   * An empty string array of length 0 is returned if no match is found.
   *
   * @param pTag The tag value to match the resource text on.
   *
   * @return Array of text resource template strings matched on the tag value or an
   *          empty array if no match is found.
   *
   * @throws RepositoryException If there was an error accessing the repository.
   */
  private String[] getMessageTemplatesByTag( String pTag )
    throws RepositoryException {

    String tag = pTag;
    String siteTag = getSiteTag( pTag );

    String[] messageTemplates = new String[0];

    if ( StringUtils.isEmpty(siteTag)
         && StringUtils.isEmpty(tag) ) {
      messageTemplates = new String[0];
    }
    else {
      RepositoryItem[] items = getMessageItemsByTag(pTag);

      if ( items != null ) {
        messageTemplates = new String[items.length];

        for ( int i = 0;
              i < items.length;
              ++i ) {
          messageTemplates[i] = (String)items[i].getPropertyValue( TEXT_PROPERTY_NAME );

          if ( messageTemplates[i] == null ) {
            messageTemplates[i] = "";
          }
        }
      }
      else {
        messageTemplates = new String[0];
      }
    }

    return( messageTemplates );
  }


  /**
   * Formats the template string; parses the template and substitutes any recognized format patterns
   * with matched values from the 'pArgs' map or 'pRequest' parameters.
   *
   * @param pTemplate The template to parse.
   *
   * @param pArgs Map containing key/entry values for the template format pattern name/values.
   *
   * @param pRequest HTTP request containing parameters for the template format pattern name/values.
   *
   * @return The template with format patterns populated.
   */
  private String parseTemplate( String pTemplate,
                                Map pArgs,
                                DynamoHttpServletRequest pRequest ) {
    char ch = '\0';

    StringBuffer formatPattern = new StringBuffer();

    StringBuffer result = new StringBuffer();

    Set keySet = null;

    Enumeration requestParameterNames = null;

    String key = "";


    if ( pArgs != null ) {
      keySet = pArgs.keySet();
    }

    /*
     * Iterate over the template characters populating any format pattern specifiers encountered.
     */
    for ( int i = 0;
          pTemplate != null && i < pTemplate.length();
          ++i ) {
      ch = pTemplate.charAt( i );

      if ( ch == ESCAPE_CHAR ) {
        /*
         * We've encountered the escape character. 
         * If the next character is either the start or end format pattern character, or the
         * escape character then treat this as a normal character and add to result text.
         * Otherwise ignore the escape character for unrecognized escape sequences.
         */
        if ( i + 1 < pTemplate.length() ) {
          switch ( pTemplate.charAt(i + 1) ) {
            case START_FORMAT_PATTERN_CHAR:
              result.append( START_FORMAT_PATTERN_CHAR );
              ++i;
              break;

            case END_FORMAT_PATTERN_CHAR:
              result.append( END_FORMAT_PATTERN_CHAR );
              ++i;
              break;

            case ESCAPE_CHAR:
              result.append( ESCAPE_CHAR );
              ++i;
              break;

            default:
              /*
               * Ignore the escape character for unrecognized escape sequences.
               */
              break;
          }
        }
      }
      else if ( ch == START_FORMAT_PATTERN_CHAR ) {
        /*
         * We've possibly encountered a format pattern string.
         * Firstly try to find a match in the map of arguments, if no match is found then try to
         * find a match in the request parameters.
         */
        boolean formatPatternFound = false;

        /*
         * Iterate over the map entries trying to match the format pattern with a map key.
         * If a match is found substitute the corresponding map entry value for the format pattern.
         */
        if ( keySet != null ) {
          for ( Iterator iterator = keySet.iterator();
                !formatPatternFound && iterator.hasNext(); ) {

            key = (String)iterator.next();

            formatPattern = new StringBuffer();
            formatPattern.append( START_FORMAT_PATTERN_CHAR ).append( key ).append( END_FORMAT_PATTERN_CHAR );

            if ( pTemplate.startsWith( new String(formatPattern), i) ) {
              result.append( pArgs.get(key) );

              i = pTemplate.indexOf( END_FORMAT_PATTERN_CHAR, i );

              formatPatternFound = true;
            }
          }
        }

        /*
         * If the format pattern has no match in the map of arguments try to find a match in the request parameters.
         * Iterate over the request parameters trying to match the format pattern with a parameter name.
         * If a match is found substitute the corresponding parameter value for the format pattern.
         */
        if ( !formatPatternFound
             && pRequest != null ) {
          requestParameterNames = pRequest.getParameterNames();
            
          while ( !formatPatternFound
                  && requestParameterNames != null
                  && requestParameterNames.hasMoreElements() ) {

            key = (String)requestParameterNames.nextElement();

            formatPattern = new StringBuffer();
            formatPattern.append( START_FORMAT_PATTERN_CHAR ).append( key ).append( END_FORMAT_PATTERN_CHAR );

            if ( pTemplate.startsWith(new String(formatPattern), i) ) {
              result.append( (String)pRequest.getLocalParameter(key) );

              i = pTemplate.indexOf( END_FORMAT_PATTERN_CHAR, i );

              formatPatternFound = true;
            }
          }
        }

        /*
         * There was no format pattern match, simply add the format pattern start character
         * to the result text and continue searching for other matches.
         */
        if ( !formatPatternFound ) {
          result.append( ch );
        }

      }
      else {
        /*
         * We've encountered a normal character, so add this to the result text
         * and continue searching for format pattern matches.
         */
        result.append( ch );
      }
    }

    return( new String(result) );
  }


  /**
   * @see parseTemplate(String, Map, DynamoHttpServletRequest)
   * 
   * Formats the template string; parses the template and substitutes any recognized format patterns
   * with matched values from the 'pArgs' map parameter.
   *
   * @param pTemplate The template to parse.
   *
   * @param pArgs Map containing key/entry values for the template format pattern name/values.
   *
   * @return The template with format patterns populated.
   */
  private String parseTemplate( String pTemplate,
                                Map pArgs ) {
    return( parseTemplate(pTemplate,
                          pArgs,
                          null) );
  }
  
  /**
   * Receives and validates the request input parameters.
   * Performs a look up in the repository based on the supplied 'key' parameter value to obtain localized
   * resource text message template. If a match is found the message template is parsed to substitute any
   * format pattern specifiers with the corresponding entry value obtained from either the 'args' input
   * parameter map or the request input parameters.
   * If no match is found for the 'key' in the repository then:
   *   if the default text input parameter is not empty then this is used as the message text
   *   if the default text input parameter is empty and the default text property is not empty then
   *     the default text property is used as the message text
   *   otherwise the message text is set as an empty string
   *
   * @param pRequest - HTTP request
   * @param pResponse - HTTP response
   *
   * @throws ServletException if an error occurs
   * @throws IOException if an error occurs
   */
  public void service( DynamoHttpServletRequest pRequest,
                       DynamoHttpServletResponse pResponse )
    throws ServletException,
           IOException {

    try {
      String keyParamValue = (String)pRequest.getLocalParameter( KEY_ATTRIBUTE_NAME );
      String tagParamValue = (String)pRequest.getLocalParameter( TAG_ATTRIBUTE_NAME );
      Map argsParamValue = (Map)pRequest.getObjectParameter( ARGS_ATTRIBUTE_NAME );
      String defaultTextParamValue = (String)pRequest.getLocalParameter( DEFAULTTEXT_ATTRIBUTE_NAME );

      String messageTemplate = "";
      String[] messageTemplates = null;
      RepositoryItem messageItem = null;
      RepositoryItem[] messageItems = null;

      String message = "";
      String[] messages = null;

      Mode mode = Mode.UNKNOWN;
      

      /*
       * Obtain the resource text message template.
       */
      if ( !StringUtils.isEmpty(keyParamValue) ) {
        if ( isLoggingDebug() ) {
          logDebug( "Searching for resource text template matching key value: " + keyParamValue );
        }

        mode = Mode.BY_KEY;
      }
      else if ( !StringUtils.isEmpty(tagParamValue) ) {
        if ( isLoggingDebug() ) {
          logDebug( "Searching for resource text templates matching tag value: " + tagParamValue );
        }

        mode = Mode.BY_TAG;
      }
      else {
        if ( isLoggingDebug() ) {
          logDebug( "No supplied key or tag value" );
        }

        mode = Mode.UNKNOWN;
        
        messageTemplate = "";
        messageTemplates = new String[0];
      }


      /*
       * Obtain the resource text message matching the key.
       */
      if ( mode == Mode.BY_KEY ) {
        messageTemplate = getMessageTemplateByKey( keyParamValue );
        messageItem = getMessageItemByKey(keyParamValue);
        
        if ( !StringUtils.isEmpty(messageTemplate)) {
          /*
           * Construct the message by populating any format patterns specified in the message template.
           */
          if ( isLoggingDebug() ) {
            logDebug( "Found matching resource text template: " + messageTemplate );
          }

          message = parseTemplate( messageTemplate,
                                   argsParamValue,
                                   pRequest );

          if ( isLoggingDebug() ) {
            logDebug( "Formatted resource text message: " + message );
          }
        }
        else if ( !StringUtils.isEmpty(defaultTextParamValue) ) {
          if ( isLoggingDebug() ) {
            logDebug( "No matching resource text template found, using supplied default text: " + defaultTextParamValue );
          }

          message = defaultTextParamValue;
        }
        else if ( !StringUtils.isEmpty(getDefaultTextTemplate()) ) {
          if ( isLoggingDebug() ) {
            logDebug( "No matching resource text template found, using default text property: " + defaultTextParamValue );
          }

          message = getDefaultText( keyParamValue );

          if ( isLoggingDebug() ) {
            logDebug( "Formatted default text property: " + message );
          }
        }
        else {
          if ( isLoggingDebug() ) {
            logDebug( "No matching resource text template or default text found, using empty string" );
          }

          message = "";
        }
        
        messages = new String[1];
        messages[0] = message;
        
        messageItems = new RepositoryItem[1];
        messageItems[0] = messageItem;
        
        /*
         * If messageItem of type list, retrieve all linked items and build
         * array or parsed messages  
         */
        if (messageItem != null) {
          Integer itemType = (Integer) messageItem.getPropertyValue(TYPE_PROPERTY_NAME);
          if (itemType.intValue() == 3) {
            List linkedMessages = (List) messageItem.getPropertyValue(LIST_PROPERTY_NAME);
            messages = new String[linkedMessages.size()];
            messageItems = new RepositoryItem[linkedMessages.size()];
            linkedMessages.toArray(messageItems);
            
            int i = 0;
            
            for(Object linkedMessage : linkedMessages) {
              if(((RepositoryItem)linkedMessage).getItemDescriptor().hasProperty(TEXT_PROPERTY_NAME)) {
                messageTemplate = (String) ((RepositoryItem)linkedMessage).getPropertyValue(TEXT_PROPERTY_NAME);
                messages[i++] = parseTemplate(messageTemplate, argsParamValue, pRequest);
              }
            }
          }
        }
      }
      
      /*
       * Obtain all resource text messages matching the tag.
       */
      if ( mode == Mode.BY_TAG ) {
       
        messageTemplates = getMessageTemplatesByTag( tagParamValue );
        messageItems = getMessageItemsByTag(tagParamValue);
        messages = new String[messageTemplates.length];
        

        for (int i = 0; messageTemplates != null && i < messageTemplates.length; ++i) {
          /*
           * Construct the message by populating any format patterns specified
           * in the message template.
           */
          if (isLoggingDebug()) {
            logDebug("Found matching resource text template: "
                + messageTemplates[i]);
          }

          if (!StringUtils.isEmpty(messageTemplates[i])) {
            message = parseTemplate(messageTemplates[i], argsParamValue,
                pRequest);
            messages[i] = message;
          } else {
            messages[i] = "";
          }

          if (isLoggingDebug()) {
            logDebug("Formatted resource text message: " + message);
          }
        }
      }
      
      /*
       * Process droplet outputs
       */
      if(messages != null && messages.length > 0) {
        int length = messages.length;
        pRequest.setParameter(SIZE, NumberTable.getInteger(length));
        
        pRequest.serviceLocalParameter(OUTPUT_START, pRequest, pResponse);
        
        for (int i = 0; i < length; i++) {
          pRequest.setParameter(COUNT, NumberTable.getInteger(i + 1));
          pRequest.setParameter(INDEX, NumberTable.getInteger(i));
          
          if (messageItems != null) {
            pRequest.setParameter(ITEM, messageItems[i]);
          }
          else {
            pRequest.setParameter(ITEM, null);
          }
          
          pRequest.setParameter(MESSAGE, messages[i]);
          pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
        }
        
        pRequest.serviceLocalParameter(OUTPUT_END, pRequest, pResponse);
        
      } else {
        pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse );
      }
    }  
    catch( Exception exception ) {
      if ( isLoggingError() ) {
        logError("Error accessing the repository: ", exception );
      }

      /*
       * Render the error message oparam output
       */
      pRequest.setParameter( ERROR_MESSAGE,
                             ERROR_MESSAGE_TXT );

      pRequest.serviceLocalParameter( ERROR,
                                      pRequest,
                                      pResponse );
    }
  }
}


