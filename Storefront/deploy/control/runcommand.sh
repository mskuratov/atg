#!/bin/sh
#
# Copyright 2011, 2012, Oracle and/or its affiliates. All rights reserved.
# Oracle and Java are registered trademarks of Oracle and/or its 
# affiliates. Other names may be trademarks of their respective owners.
# UNIX is a registered trademark of The Open Group.
# 
# This software and related documentation are provided under a license 
# agreement containing restrictions on use and disclosure and are 
# protected by intellectual property laws. Except as expressly permitted 
# in your license agreement or allowed by law, you may not use, copy, 
# reproduce, translate, broadcast, modify, license, transmit, distribute, 
# exhibit, perform, publish, or display any part, in any form, or by any 
# means. Reverse engineering, disassembly, or decompilation of this 
# software, unless required by law for interoperability, is prohibited.
# The information contained herein is subject to change without notice 
# and is not warranted to be error-free. If you find any errors, please 
# report them to us in writing.
# U.S. GOVERNMENT END USERS: Oracle programs, including any operating 
# system, integrated software, any programs installed on the hardware, 
# and/or documentation, delivered to U.S. Government end users are 
# "commercial computer software" pursuant to the applicable Federal 
# Acquisition Regulation and agency-specific supplemental regulations. 
# As such, use, duplication, disclosure, modification, and adaptation 
# of the programs, including any operating system, integrated software, 
# any programs installed on the hardware, and/or documentation, shall be 
# subject to license terms and license restrictions applicable to the 
# programs. No other rights are granted to the U.S. Government.
# This software or hardware is developed for general use in a variety 
# of information management applications. It is not developed or 
# intended for use in any inherently dangerous applications, including 
# applications that may create a risk of personal injury. If you use 
# this software or hardware in dangerous applications, then you shall 
# be responsible to take all appropriate fail-safe, backup, redundancy, 
# and other measures to ensure its safe use. Oracle Corporation and its 
# affiliates disclaim any liability for any damages caused by use of this 
# software or hardware in dangerous applications.
# This software or hardware and documentation may provide access to or 
# information on content, products, and services from third parties. 
# Oracle Corporation and its affiliates are not responsible for and 
# expressly disclaim all warranties of any kind with respect to 
# third-party content, products, and services. Oracle Corporation and 
# its affiliates will not be responsible for any loss, costs, or damages 
# incurred due to your access to or use of third-party content, products, 
# or services.


WORKING_DIR=`dirname ${0} 2>/dev/null`

. "${WORKING_DIR}/../config/script/set_environment.sh"

if [ -z "${ENDECA_ROOT}" ] ; then
  echo "ERROR: ENDECA_ROOT is not set."
  exit 1
fi

# *******************************************************
# Determine location of java executable
if [ -x "${ENDECA_ROOT}/j2sdk/bin/java" ] ; then 
	JAVA="${ENDECA_ROOT}/j2sdk/bin/java"
else
	echo "WARNING: ${ENDECA_ROOT}j2sdk/bin/java does not exist"
	if [ ! "${JAVA_HOME}" = "" ] ; then
		echo "WARNING: Using java in ${JAVA_HOME}/bin/java"
		JAVA=${JAVA_HOME}/bin/java
	else
		echo "WARNING: Using local setting for java"
		JAVA="java"
	fi
fi

WORKING_DIR=`dirname ${0} 2>/dev/null`

APP_CONFIG_XML=${WORKING_DIR}/../config/script/AppConfig.xml
if [ ! -f "${APP_CONFIG_XML}" ]; then
  echo "ERROR: Cannot find file: ${APP_CONFIG_XML}"
  exit 1
fi

# Do not include orawsdl.jar from ENDECA_ROOT, as it is not compatible with CXF.  
# We use instead the version in RS_JAVA_LIB_DIR.
CLASSPATH=${ENDECA_ROOT}/lib/java/eacclient.jar
CLASSPATH=${CLASSPATH}:${ENDECA_ROOT}/lib/java/jaxrpc.jar
CLASSPATH=${CLASSPATH}:${ENDECA_ROOT}/lib/java/mail.jar
CLASSPATH=${CLASSPATH}:${ENDECA_ROOT}/lib/java/saaj.jar
CLASSPATH=${CLASSPATH}:${ENDECA_ROOT}/lib/java/activation.jar
CLASSPATH=${CLASSPATH}:${ENDECA_ROOT}/lib/java/axis.jar
CLASSPATH=${CLASSPATH}:${ENDECA_ROOT}/lib/java/commons-discovery-0.2.jar
CLASSPATH=${CLASSPATH}:${ENDECA_ROOT}/lib/java/commons-logging-1.0.4.jar

# Add the script folder of the application to the classpath 
CLASSPATH=${CLASSPATH}:${WORKING_DIR}/../config/script

# Add all zip and jar files that are in the ../config/lib/java directory
for i in ${WORKING_DIR}/../config/lib/java/*.jar; do
   CLASSPATH=${CLASSPATH}:$i
done
for i in ${WORKING_DIR}/../config/lib/java/*.zip; do
   CLASSPATH=${CLASSPATH}:$i
done

# support for recovering of failed partial update runs
for i in ${RS_JAVA_LIB_DIR}/*.jar; do
   CLASSPATH=${CLASSPATH}:$i
done

JAVA_ARGS="${JAVA_ARGS} -Djava.util.logging.config.file=${WORKING_DIR}/../config/script/logging.properties"

CONTROLLER_ARGS="--app-config AppConfig.xml" 

OVERRIDE_PROPERTIES="${WORKING_DIR}/../config/script/environment.properties" 
if [ -f "${OVERRIDE_PROPERTIES}" ]; then 
  CONTROLLER_ARGS="${CONTROLLER_ARGS} --config-override environment.properties" 
fi 

"${JAVA}" ${JAVA_ARGS} -cp "${CLASSPATH}" com.endeca.soleng.eac.toolkit.Controller ${CONTROLLER_ARGS} $*
