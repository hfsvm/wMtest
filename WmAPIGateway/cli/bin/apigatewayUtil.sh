#!/bin/sh
##############################################################################
#                                                                            #
#  apigatewayUtil.sh                                                         #
#                                                                            #
##############################################################################


if [ "$1" = "-help" ]; then
echo ""
echo " Usage: apigatewayUtil.sh [COMMANDS AND OPTIONS]"
echo " COMMANDS LIST::                                 "
echo " -------------------------------------------     "
echo " configure manageRepo    Command to configure repository "
echo "   options                                               "
echo "      -tenant           Specify name of the tenant (optional)            "
echo "      -file             File path to read repository details (optional)  "
echo " delete  manageRepo       Command to delete repository                   "
echo "    options                                                              "
echo "      -tenant           Specify tenant name(optional)                    "
echo "  list manageRepo         Command to list all repositories               "
echo " create backup            Command to create backup                       "
echo "    options                                                              "
echo "       -tenant          Specify tenant name  (optional)                  "
echo "      -name             Specify backup file name  (optional)             "
echo "      -include          Specify one of the following backup type in case of partial backup: 1.'analytics' to backup runtime data  2.'assets' to backup assets.(optional) Example partial backup -include assets   "

echo "  list backup             Command to list backup files in repository     "
echo "   options                                                               "
echo "      -tenant           Specify name of the tenant (optional)            "
echo " delete backup            Command to delete the backup file              "
echo "   options                                                               "
echo "      -tenant           Specify tenant name  (optional)                  "
echo "       -name            Specify backup file name                         "
echo " restore backup           Command to restore the backup file             "
echo "    options                                                              "
echo "      -tenant           Specify tenant name  (optional)                  "
echo "      -name             Specify backup file name                         "
echo "       -sync            Select true to make process synchronous, else select false to make it asynchronous. The default value is true.(optional) "
echo "  status backup           Command to get the status of the backup file   "
echo "    options                                                              "
echo "       -tenant           Specify tenant name  (optional)                 "
echo "       -name             Specify backup file name                        "
echo " configure fs_path  -path c://sample//APIGATEWAY  Command to update Elasticsearch File system path "
echo "   options                                                               "
echo "      -path             File system location                             "
exit
fi


TENANT_DIR="$(dirname  "$(dirname  "$(dirname  "$(dirname "$PWD")")")")"
JAR_DIR="${TENANT_DIR}/packages/WmAPIGateway/code/jars"
CLI="${TENANT_DIR}/packages/WmAPIGateway/cli"
CLOUD_CONFIG="${TENANT_DIR}/packages/WmAPIGateway/config/resources/cloud/"
JAVA_DIR="$(dirname "$(dirname "$(dirname $TENANT_DIR)")")/jvm/jvm";
IS_DIR="$(dirname "$(dirname "$(dirname $TENANT_DIR)")")";
COMMON_LIB_DIR="$(dirname "$(dirname "$(dirname $TENANT_DIR)")")/common/lib";
COMMON_RUNTIME_DIR="$(dirname "$(dirname "$(dirname $TENANT_DIR)")")/common/runtime";
IS_LIB_DIR="$(dirname "$(dirname $TENANT_DIR)")/lib";

echo "Current Path: $PWD"

for file in `ls $JAR_DIR/*.jar`
do
   CLASSPATH=$file:$CLASSPATH
done
for file in `ls $JAR_DIR/static/*.jar`
do
   CLASSPATH=$file:$CLASSPATH
done

for file in `ls $CLI/lib/*.jar`
do
   CLASSPATH=$file:$CLASSPATH
done

for file in `ls $COMMON_LIB_DIR/ext/jackson*.jar`
do
   CLASSPATH=$file:$CLASSPATH
done

for file in `ls $COMMON_LIB_DIR/*.jar`
do
   CLASSPATH=$file:$CLASSPATH
done

CLASSPATH=$IS_DIR/IntegrationServer/lib/wm-isserver.jar:$CLASSPATH

for file in `ls $COMMON_RUNTIME_DIR/bundles/tes/eclipse/plugins/net.sf.ehcache*.jar`
do
   CLASSPATH=$file:$CLASSPATH
done

for file in `ls $COMMON_RUNTIME_DIR/bundles/platform/eclipse/plugins/*.jar`
do
    CLASSPATH=$file:$CLASSPATH
done

for file in `ls $IS_LIB_DIR/jars/*.jar`
do
   CLASSPATH=$file:$CLASSPATH
done

CLASSPATH="$COMMON_LIB_DIR/wm-isclient.jar":$CLASSPATH

CLASSPATH="$CLOUD_CONFIG":$CLASSPATH

export CLASSPATH


JAVA_EXE="$JAVA_DIR/bin/java"




$JAVA_EXE -Dinstance.dir=$TENANT_DIR -Dcom.softwareag.install.root=$IS_DIR com/softwareag/apigateway/utility/initializer/APIGatewayUtilityStartup   $*
returnCode=$?

exit $returnCode
