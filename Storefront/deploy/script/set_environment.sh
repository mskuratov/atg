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

# Endeca Environment Variables
#
#
# Application/deployment variables

# ENDECA_PROJECT_DIR specifies the path of the deployed application
# e.g. ENDECA_PROJECT_DIR=/localdisk/apps/myapp
# obtain a reference to the running script
EXECUTING_SCRIPT=${0}
# all scripts are assumed to run from /control, so project dir is one up
ENDECA_PROJECT_DIR=`dirname ${EXECUTING_SCRIPT}`/..
# make sure that this is an absolute path
ABS_PATH=`(cd $ENDECA_PROJECT_DIR 2>/dev/null && pwd ;)`
ENDECA_PROJECT_DIR=$ABS_PATH
export ENDECA_PROJECT_DIR

# ENDECA_PROJECT_NAME specifies the project name that will be used, for
# example, as the JCD job prefix for jobs defined in the project's 
# Job Control Daemon (JCD).
# e.g. ENDECA_PROJECT_NAME=myapp
ENDECA_PROJECT_NAME=@@PROJECT_NAME@@
export ENDECA_PROJECT_NAME

# set application specific properties from environment.properties
source "${ENDECA_PROJECT_DIR}/config/script/parse_properties.sh" "${ENDECA_PROJECT_DIR}/config/script/environment.properties"

if [ ! -d "$CAS_ROOT" ] ; then
    echo "No CAS install folder found at $CAS_ROOT. Please install CAS."
    exit 1
fi

# Endeca software variables

# These variables specify the location of the Endeca software on this system
# and add Endeca-specific paths to the Perl, path and Java environment variables.

# All variables are set relative to the ENDECA_ROOT environment variable. Variables
# should be updated to reflect the location of your Endeca install. Instructions
# can be found in the core product documentation. You may also retrieve these
# variable settings from the installer_sh.ini or installer_csh.ini file
# (e.g. /usr/endeca/4.7.4/i86pc-linux/setup/installer_sh.ini)

# ENDECA_ROOT specifies the root directory of the installed Endeca software
# e.g. ENDECA_ROOT=/usr/endeca/4.7.4/i86pc-linux
ENDECA_ROOT=@@ENDECA_ROOT@@
export ENDECA_ROOT

PERLLIB=$ENDECA_ROOT/lib/perl:$ENDECA_ROOT/lib/perl/Control:$ENDECA_ROOT/perl/lib:$ENDECA_ROOT/perl/lib/site_perl:$PERLLIB
export PERLLIB

PERL5LIB=$ENDECA_ROOT/lib/perl:$ENDECA_ROOT/lib/perl/Control:$ENDECA_ROOT/perl/lib:$ENDECA_ROOT/perl/lib/site_perl:$PERL5LIB
export PERL5LIB

PATH=$ENDECA_ROOT/bin:$ENDECA_ROOT/perl/bin:$ENDECA_ROOT/tools/server/bin:$ENDECA_ROOT/utilities:$PATH
export PATH

JAVA_HOME=$ENDECA_ROOT/j2sdk:$JAVA_HOME
export JAVA_HOME
