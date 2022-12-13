#!/bin/sh
##############################################################################
#                                                                            #
#   apigw-pkg-mgt.sh                                                        #
#                                                                            #
#  This script is designed to work with Java VM's that conform to the        #
#  command-line conventions of Sun Microsystems (TM) Java Development Kit    #
#  or Java Runtime Environment.                                              #
#                                                                            #
##############################################################################
set -m
cd `dirname $0`
CURR_DIR=`pwd`

if [ "x$JRE_HOME" = "x" ]; then
    JAVA_EXE=$CURR_DIR/../../../../../../../jvm/jvm/jre/bin/java
else
    JAVA_EXE=$JRE_HOME/bin/java
fi

CLASSPATH=""
JAR_DIR="${CURR_DIR}/../../cli/lib"
for file in $(ls $JAR_DIR/*.jar); do
    CLASSPATH=$file:${CLASSPATH}
done

CLASSPATH="${CURR_DIR}/../../code/jars/apigateway-utility.jar":${CLASSPATH}
CLASSPATH="${CURR_DIR}/../../code/jars/apigateway-runtime.jar":${CLASSPATH}
CLASSPATH="${CURR_DIR}/../../code/jars/apigateway-runtime-base.jar":${CLASSPATH}
CLASSPATH="${CURR_DIR}/../../code/jars/apigateway-runtime-provider.jar":${CLASSPATH}
CLASSPATH="${CURR_DIR}/../../code/jars/apigateway-is.jar":${CLASSPATH}
CLASSPATH="${CURR_DIR}/../../code/jars/apigateway-core.jar":${CLASSPATH}
CLASSPATH="${CURR_DIR}/../../code/jars/apigateway-cloud.jar":${CLASSPATH}
CLASSPATH="${CURR_DIR}/../../code/jars/static/apigateway-api.jar":${CLASSPATH}
CLASSPATH="${CURR_DIR}/../lib/log4j-1.2.17.jar":${CLASSPATH}
CLASSPATH="${CURR_DIR}/../../../../../../../common/lib/ext/jackson-databind.jar":${CLASSPATH}
CLASSPATH="${CURR_DIR}/../../../../../../../common/lib/ext/jackson-annotations.jar":${CLASSPATH}
CLASSPATH="${CURR_DIR}/../../../../../../../common/lib/ext/jackson-core.jar":${CLASSPATH}
CLASSPATH="${CURR_DIR}/../../../../../../../common/lib/ext/jackson-dataformat-yaml.jar":${CLASSPATH}

$JAVA_EXE -cp $CLASSPATH -Dcom.softwareag.install.root=${CURR_DIR}/../../../../../../../ com.softwareag.apigateway.utility.command.usage.APIGatewayUsage $*
