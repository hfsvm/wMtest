#!/bin/bash

###############################################################################
# Name: apigw_container.sh
###############################################################################
# Description:
# Build script to create the docker contaniner for API Gateway
# Docker container based on is:micro Image.
###############################################################################
# Input:
# Check help apigw_container.sh -h
# This script is dependent on is_container.sh script
###############################################################################
# Output:
# API Gateway Docker image
###############################################################################

#set -x
#set -e

###############################################################################
# Variable  Declaration
###############################################################################

cmdFlag=0
instanceName=default
updatePathScript=apigw_updatePath.sh
updateNodeScript=apigw_updateNode.sh
SAG_DOCKER_HOME=/opt/softwareag
imageName=is:apigw
isImageName=is:micro
externES=0
keepESdata=0

###############################################################################
# Function   Declaration
###############################################################################

# Usage of this script
usage(){
  echo "Usage:$0 args"
  echo "args:"
  echo "createDockerfile         -   Creates a Dockerfile for API Gateway based on Integration Server instance"
  echo "    --instance.name          name of Integration Server instance for which to create an image - optional, default is the instance named default"
  echo "    --port.list              comma-separated list of ports that need to be exposed in a Docker container - default is 9072"
  echo "    --base.image             name of base Integration Server image upon which this image should be built - default is is:micro"
  echo "    --file.name              filename for the generated Dockerfile - optional, default is Dockerfile_IS_APIGW. Dockerfile is created under Integration Server_directory/instances/{instancename}/packages"
  echo "    --keep.ES.data           Keep Elasticsearch data from installation - default is false"
  echo "    --extern.ES              Use an external Elasticsearch - default is false"
  echo "build                    -   Executes Docker build by using the provided Dockerfile to build an image of API Gateway"
  echo "    --instance.name          name of Integration Server instance for which to build an image - optional, default is the instance named default"
  echo "    --file.name              filename of the Dockerfile for build - optional, default is Dockerfile_IS_APIGW"
  echo "                             build uses the file located in the packages directory of specified instance, specifically: Integration Server_directory/instances/{instancename}/packages"
  echo "    --image.name             name for the generated image for API Gateway - optional, default is is:apigw"
  echo "-v|--version             -   Version of the script"
  echo "-h|--help                -   Show this help message"
  exit 1
}

# Parse input arguments
parseArgs(){
  
  while test $# -ge 1; do
    arg=$1
    shift
    case $arg in
      createDockerfile)
        cmdFlag=1
        fileName=Dockerfile_IS_APIGW
        packageName=WmAPIGateway
        portList=9072
      ;;
      build)
        cmdFlag=2
        fileName=Dockerfile_IS_APIGW
      ;;
      --base.image)
        isImageName=${1}
        shift
      ;;
      --file.name)
        fileName=${1}
        shift
      ;;
      --image.name)
        imageName=${1}
        shift
      ;;      
      --instance.name)
        instanceName=${1}
        shift
      ;;
      --port.list)
        portList=${1}
        shift
      ;;
      --extern.ES)
        externES=1
	  ;;
      --keep.ES.data)
        keepESdata=1
	  ;;
      -h|--help)
        usage
      ;;
      -v|--version)
        echo "$0 version 10.2"
        exit 0
      ;;
      *)
        echo "Unknown: $@"
        usage
      ;;
    esac
  done
}

createDockerfile() {

# Create the APIGW container Dockerfile
  CHOWN="--chown=1724:1724"
  ./is_container.sh createPackageDockerfile -Dimage.createUser=true -Dpackage.list=$packageName -Dimage.name=$isImageName -Dinstance.name=$instanceName -Dfile.name=$fileName

  sed -i -e '/^ENTRYPOINT/d' \
         -e "s#^COPY ${CHOWN} ./$packageName/#COPY ${CHOWN} ./IntegrationServer/instances/\${INSTANCE_NAME}/packages/$packageName#g" \
  ${IS_HOME}/instances/$instanceName/packages/$fileName

  if [ $externES -ne 0 ]
  then
      echo "ENV EXTERN_ELASTICSEARCH true" >> ${IS_HOME}/instances/$instanceName/packages/$fileName
  else
      echo "RUN echo intercopy echo 1" >> ${IS_HOME}/instances/$instanceName/packages/$fileName
      echo "COPY ${CHOWN} ./InternalDataStore ${SAG_DOCKER_HOME}/InternalDataStore/" >> ${IS_HOME}/instances/$instanceName/packages/$fileName
	  if [ $keepESdata -eq 0 ]
	  then
	      echo "RUN rm -rf ${SAG_DOCKER_HOME}/InternalDataStore/data" >> ${IS_HOME}/instances/$instanceName/packages/$fileName
	  fi
  fi
  
cat << EOF >> ${IS_HOME}/instances/$instanceName/packages/$fileName
RUN echo intercopy echo 2
COPY ${CHOWN} ./common/runtime/bundles/spm/eclipse/plugins ${SAG_DOCKER_HOME}/common/runtime/bundles/spm/eclipse/plugins/
RUN echo intercopy echo 3
COPY ${CHOWN} ./common/lib/ext/* ${SAG_DOCKER_HOME}/common/lib/ext/
RUN echo intercopy echo 4
COPY ${CHOWN} ./profiles/IS_\${INSTANCE_NAME}/bin/$updatePathScript ${SAG_DOCKER_HOME}/profiles/IS_\${INSTANCE_NAME}/bin/
COPY ${CHOWN} ./profiles/IS_\${INSTANCE_NAME}/bin/$updateNodeScript ${SAG_DOCKER_HOME}/profiles/IS_\${INSTANCE_NAME}/bin/
COPY ${CHOWN} ./profiles/IS_\${INSTANCE_NAME}/configuration ${SAG_DOCKER_HOME}/profiles/IS_\${INSTANCE_NAME}/configuration
COPY ${CHOWN} ./profiles/IS_\${INSTANCE_NAME}/apigateway  ${SAG_DOCKER_HOME}/profiles/IS_\${INSTANCE_NAME}/apigateway
COPY ${CHOWN} ./profiles/IS_\${INSTANCE_NAME}/workspace ${SAG_DOCKER_HOME}/profiles/IS_\${INSTANCE_NAME}/workspace

RUN find ${SAG_DOCKER_HOME} -name clusteruuid.dat -exec rm -rf {} \;

RUN chmod a+x ${SAG_DOCKER_HOME}/profiles/IS_\${INSTANCE_NAME}/bin/*.sh && ${SAG_DOCKER_HOME}/profiles/IS_\${INSTANCE_NAME}/bin/$updatePathScript; \\
    sed -i '/console.sh/i . ${SAG_DOCKER_HOME}/profiles/IS_\$INSTANCE_NAME/bin/$updateNodeScript' ${SAG_DOCKER_HOME}/IntegrationServer/bin/startContainer.sh

RUN chmod a+x ${SAG_DOCKER_HOME}/profiles/IS_\${INSTANCE_NAME}/apigateway/filebeat/filebeat_apigateway;

RUN sed -i '/apigw.console.log/c\apigw.console.log = true' /opt/softwareag/profiles/IS_\${INSTANCE_NAME}/apigateway/config/uiconfiguration.properties


HEALTHCHECK --interval=200s CMD curl -f http://localhost:5555/rest/apigateway/health

USER 1724

ENTRYPOINT ["${SAG_DOCKER_HOME}/IntegrationServer/bin/startContainer.sh"]

EXPOSE `echo $portList | tr "," " "`
EOF
}

buildImage() {

# Build Docker image based on APIGW Docker file 
  docker build -f ${IS_HOME}/instances/$instanceName/packages/$fileName -t $imageName ${WM_HOME}
  #rm -f ${WM_HOME}/.dockerignore
  #rm -f ${WM_HOME}/profiles/IS_${instanceName}/bin/$updatePathScript
  #rm -f ${WM_HOME}/profiles/IS_${instanceName}/bin/$updateNodeScript
}

updatePathScript() {

echo "Info: Create ${WM_HOME}/profiles/IS_${instanceName}/bin/$updatePathScript"
cat << EOF > ${WM_HOME}/profiles/IS_${instanceName}/bin/$updatePathScript
#!/bin/bash
#Software AG Installer API Gateway Image script created on @ `date` 

SAG_HOME=${SAG_DOCKER_HOME}

replacePathsInFiles() {
    for file in \`find \${SAG_HOME} -type f -exec grep -il "\${SAG_HOME_ORIG}" {} \; | grep -vE "\.log|\.jar"\`
    do
       echo "$updatePathScript: update the path in \$file"
       sed -i "s!\${SAG_HOME_ORIG}/!\${SAG_HOME}/!g" \$file
    done
}

if [ "${WM_HOME}" != "${WM_HOME_FULL}" ]
then
    SAG_HOME_ORIG=${WM_HOME_FULL}
    replacePathsInFiles
fi
SAG_HOME_ORIG=${WM_HOME}
replacePathsInFiles
EOF
}

updateNodeScript() {

echo "Info: Create ${WM_HOME}/profiles/IS_${instanceName}/bin/$updateNodeScript"
cat << EOF > ${WM_HOME}/profiles/IS_${instanceName}/bin/$updateNodeScript
#!/bin/bash

NODENAME=\`hostname\`
SAG_HOME=${SAG_DOCKER_HOME}
STATFILE=/tmp/init_container
LOGFILE=\${SAG_HOME}/IntegrationServer/instances/${instanceName}/logs/updateNode.log

if [ ! -d "\${SAG_HOME}/IntegrationServer/instances/${instanceName}/logs" ]
then
    mkdir "\${SAG_HOME}/IntegrationServer/instances/${instanceName}/logs"
fi

touch \$LOGFILE

if [ -f \$STATFILE ]
then
   :
else   
   \${JAVA_HOME}/bin/java -cp \${SAG_HOME}/IntegrationServer/instances/${instanceName}/packages/WmAPIGateway/bin/lib/apigateway-tools.jar com.softwareag.apigateway.tools.docker.ModifyExternalProperties ${instanceName} | tee \${LOGFILE}
   
   touch \$STATFILE
fi
EOF

}

createDockerIgnore() {

echo "Info: Create ${WM_HOME}/.dockerignore"
cat << EOF >> ${WM_HOME}/.dockerignore
#Software AG Installer API Gateway Image script created on @ `date` 
**/cache/*
**/logs/*
**/org.eclipse.osgi
profiles/IS_${instanceName}/apigateway/dashboard/bin/core.*
profiles/IS_${instanceName}/logs/*
EOF

}

###############################################################################
# Main Declaration
###############################################################################

main(){

   cd `dirname $0`
   SCRIPTDIR=`pwd`
   DOCKER_DIR=${SCRIPTDIR}/
   IS_HOME=${DOCKER_DIR}..
   cd ${IS_HOME}/..
   WM_HOME=`pwd`
   WM_HOME_FULL=`pwd -P`
   cd $SCRIPTDIR
   
   COMMON_LIB_EXT_DIR=${WM_HOME}/common/lib/ext

   . ${WM_HOME}/install/bin/setenv.sh

   # Parse input arguments
   parseArgs "$@"
   
   case $cmdFlag in
      1)
        updatePathScript
        updateNodeScript
        createDockerfile
        createDockerIgnore
      ;;
      2)
        buildImage
      ;; 
      *)
        echo "Error: Please enter a valid option."
        usage
      ;;
    esac
}

# Call the main function with all arguments passed in...
main "$@"
