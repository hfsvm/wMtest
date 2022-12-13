#!/bin/sh
##############################################################################
#                                                                            #
#  apigw-backup-packages.sh                                                  #
#                                                                            #
##############################################################################

CURR_DIR="$PWD"
export CURR_DIR
TENANT_DIR="$(dirname  "$(dirname  "$(dirname  "$(dirname "$PWD")")")")"
JAR_DIR="${TENANT_DIR}/packages/WmAPIGateway/code/jars"
CLI="$(dirname $PWD)"
JAVA_EXE="$(dirname "$(dirname "$(dirname $TENANT_DIR)")")/jvm/jvm/bin/java"

CLASSPATH=
CLASSPATH=$JAR_DIR/apigateway-utility.jar:$CLASSPATH
CLASSPATH=$JAR_DIR/ant-1.10.2.jar:$CLASSPATH
CLASSPATH=$JAR_DIR/ant-launcher-1.8.2.jar:$CLASSPATH
CLASSPATH=$CLI/lib/log4j-api-2.11.1.jar:$CLASSPATH
CLASSPATH=$CLI/lib/log4j-core-2.11.1.jar:$CLASSPATH

export CLASSPATH

CLASS="com.softwareag.apigateway.utility.command.backup.packages.BackupISPackages"
export CLASS

EXECUTABLE="${JAVA_EXE}"
EXECUTABLE="$EXECUTABLE -cp ${CLASSPATH} ${CLASS}"
$EXECUTABLE "$@"
returnCode=$?

exit $returnCode
