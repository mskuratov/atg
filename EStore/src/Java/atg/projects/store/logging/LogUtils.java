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


package atg.projects.store.logging;


/**
 * <p>This class provides static methods to format error messages to meet
 * the error logging requirements.  The logging requirements
 * are as follows:
 * <p>Each error message must be on a single line of wrapped text.
 * The error messages will be formatted as follows:
 * <p><b>Dynamo Criticality&lt;FS&gt;Date&lt;FS&gt;Time&lt;fs&gt;Store
 * Prefix&lt;fs&gt;Store Criticality&lt;fs&gt;Error message</b>
 * <p>&lt;fs&gt; = field separator.  Totality recommends using 4 spaces as a
 * field separator. It's preferred to have a field separator between the
 * date and time fields.
 * <p>For example:<br>
 * **** Error Sun Jun 24 2003    21:08:05 PDT    BP    Critical
 * Process XYZ unable to connect to the database
 * <p>The parsing will look for the critically, and then parse the message
 * accordingly
 * <p>Please use the following levels of critically:
 * <ul>
 * <li>Critical:  An error message that requires a system administrator
 * to fix something immediately.
 * <li>Major:  An error message that requires a system administrator
 * to perform some sort of investigation.
 * <li>Minor:  An error message that doesn't require action by a
 * system administrator. These error messages are used to corroborate Critical and Major messages.
 * </ul>
 * <p>This class contains static methods to convert strings to the desired
 * criticality
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/logging/LogUtils.java#2 $
 */
public class LogUtils {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.2/EStore/src/atg/projects/store/logging/LogUtils.java#2 $$Change: 768606 $";

  /**
   * The field separator.
   */
  public static final String FS = "\t";
  
  /**
   * The application prefix
   */
  public static final String APP = "CRS";

  /**
   * The critical prefix.
   */
  public static final String CRITICAL_PREFIX = FS + APP + FS + "Critical" + FS;

  /**
   * The majore prefix.
   */
  public static final String MAJOR_PREFIX = FS + APP + FS + "Major" + FS;

  /**
   * The minor prefix.
   */
  public static final String MINOR_PREFIX = FS + APP + FS + "Minor" + FS;

  /**
   * Private constructor.
   */
  private LogUtils() {
  }

  /**
   * Formats a string by prefixing it with the critical error
   * prefix.
   * @param pString String to format
   * @return formatted string
   */
  public static String formatCritical(String pString) {
    return CRITICAL_PREFIX + pString;
  }

  /**
   * Formats a string by prefixing it with the major error
   * prefix.
   * @param pString String to format
   * @return formatted string
   */
  public static String formatMajor(String pString) {
    return MAJOR_PREFIX + pString;
  }

  /**
   * Formats a string by prefixing it with the minor error
   * prefix.
   * @param pString String to format
   * @return formatted string
   */
  public static String formatMinor(String pString) {
    return MINOR_PREFIX + pString;
  }
}
