@echo off
rem  ###########################################################################
rem                                                                            #
rem  apigw-usage.bat                                                        #
rem                                                                            #
rem  This script is designed to work with Java VM's that conform to the        #
rem  command-line conventions of Sun Microsystems (TM) Java Development Kit    #
rem  or Java Runtime Environment.                                              #
rem                                                                            #
rem  ###########################################################################

SET CURR_DIR=%~dp0

IF "%JRE_HOME%" == "" (
    set JAVA_EXE="%CURR_DIR%\..\..\..\..\..\..\..\jvm\jvm\jre\bin\java"
) ELSE (
    SET JAVA_EXE="%JRE_HOME%\bin\java"
)

SET CLASSPATH=""
SET CLASSPATH=%CURR_DIR%..\..\code\jars\apigateway-utility.jar;%CLASSPATH%
SET CLASSPATH=%CURR_DIR%..\..\code\jars\apigateway-runtime.jar;%CLASSPATH%
SET CLASSPATH=%CURR_DIR%..\..\code\jars\apigateway-runtime-base.jar;%CLASSPATH%
SET CLASSPATH=%CURR_DIR%..\..\code\jars\apigateway-runtime-provider.jar;%CLASSPATH%
SET CLASSPATH=%CURR_DIR%..\..\code\jars\apigateway-is.jar;%CLASSPATH%
SET CLASSPATH=%CURR_DIR%..\..\code\jars\apigateway-core.jar;%CLASSPATH%
SET CLASSPATH=%CURR_DIR%..\..\code\jars\apigateway-cloud.jar;%CLASSPATH%
SET CLASSPATH=%CURR_DIR%..\..\code\jars\static\apigateway-api.jar;%CLASSPATH%
SET CLASSPATH=%CURR_DIR%\..\..\..\..\..\..\..\common\lib\ext\jackson-databind.jar;%CLASSPATH%
SET CLASSPATH=%CURR_DIR%\..\..\..\..\..\..\..\common\lib\ext\jackson-annotations.jar;%CLASSPATH%
SET CLASSPATH=%CURR_DIR%\..\..\..\..\..\..\..\common\lib\ext\jackson-core.jar;%CLASSPATH%
SET CLASSPATH=%CURR_DIR%\..\..\..\..\..\..\..\common\lib\ext\jackson-dataformat-yaml.jar;%CLASSPATH%

FOR /R %CURR_DIR%\..\..\cli\lib %%a in (*.jar) DO CALL :AddToPath %%a

%JAVA_EXE% -Dcom.softwareag.install.root=%CURR_DIR%\..\..\..\..\..\..\..\ com.softwareag.apigateway.utility.command.usage.APIGatewayUsage %*

:AddToPath
SET CLASSPATH=%1;%CLASSPATH%
goto :EOF