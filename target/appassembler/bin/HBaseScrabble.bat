@REM ----------------------------------------------------------------------------
@REM  Copyright 2001-2006 The Apache Software Foundation.
@REM
@REM  Licensed under the Apache License, Version 2.0 (the "License");
@REM  you may not use this file except in compliance with the License.
@REM  You may obtain a copy of the License at
@REM
@REM       http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM  Unless required by applicable law or agreed to in writing, software
@REM  distributed under the License is distributed on an "AS IS" BASIS,
@REM  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM  See the License for the specific language governing permissions and
@REM  limitations under the License.
@REM ----------------------------------------------------------------------------
@REM
@REM   Copyright (c) 2001-2006 The Apache Software Foundation.  All rights
@REM   reserved.

@echo off

set ERROR_CODE=0

:init
@REM Decide how to startup depending on the version of windows

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

@REM -- 4NT shell
if "%eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set CMD_LINE_ARGS=%*
goto WinNTGetScriptDir

@REM The 4NT Shell from jp software
:4NTArgs
set CMD_LINE_ARGS=%$
goto WinNTGetScriptDir

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of arguments (up to the command line limit, anyway).
set CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto Win9xGetScriptDir
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto Win9xApp

:Win9xGetScriptDir
set SAVEDIR=%CD%
%0\
cd %0\..\.. 
set BASEDIR=%CD%
cd %SAVEDIR%
set SAVE_DIR=
goto repoSetup

:WinNTGetScriptDir
set BASEDIR=%~dp0\..

:repoSetup


if "%JAVACMD%"=="" set JAVACMD=java

if "%REPO%"=="" set REPO=%BASEDIR%\lib

set CLASSPATH="%BASEDIR%"\etc;"%REPO%"\hadoop-common-2.5.0-cdh5.3.5.jar;"%REPO%"\hadoop-annotations-2.5.0-cdh5.3.5.jar;"%REPO%"\guava-11.0.2.jar;"%REPO%"\commons-cli-1.2.jar;"%REPO%"\commons-math3-3.1.1.jar;"%REPO%"\xmlenc-0.52.jar;"%REPO%"\commons-httpclient-3.1.jar;"%REPO%"\commons-codec-1.4.jar;"%REPO%"\commons-io-2.4.jar;"%REPO%"\commons-net-3.1.jar;"%REPO%"\commons-collections-3.2.1.jar;"%REPO%"\servlet-api-2.5.jar;"%REPO%"\jetty-6.1.26.cloudera.4.jar;"%REPO%"\jetty-util-6.1.26.cloudera.4.jar;"%REPO%"\jersey-core-1.9.jar;"%REPO%"\jersey-json-1.9.jar;"%REPO%"\jettison-1.1.jar;"%REPO%"\jaxb-impl-2.2.3-1.jar;"%REPO%"\jaxb-api-2.2.2.jar;"%REPO%"\stax-api-1.0-2.jar;"%REPO%"\activation-1.1.jar;"%REPO%"\jackson-jaxrs-1.8.3.jar;"%REPO%"\jackson-xc-1.8.3.jar;"%REPO%"\jersey-server-1.9.jar;"%REPO%"\asm-3.1.jar;"%REPO%"\jasper-compiler-5.5.23.jar;"%REPO%"\jasper-runtime-5.5.23.jar;"%REPO%"\jsp-api-2.1.jar;"%REPO%"\commons-el-1.0.jar;"%REPO%"\commons-logging-1.1.3.jar;"%REPO%"\log4j-1.2.17.jar;"%REPO%"\jets3t-0.9.0.jar;"%REPO%"\httpclient-4.1.2.jar;"%REPO%"\httpcore-4.1.2.jar;"%REPO%"\java-xmlbuilder-0.4.jar;"%REPO%"\commons-lang-2.6.jar;"%REPO%"\commons-configuration-1.6.jar;"%REPO%"\commons-digester-1.8.jar;"%REPO%"\commons-beanutils-1.7.0.jar;"%REPO%"\commons-beanutils-core-1.8.0.jar;"%REPO%"\slf4j-api-1.7.5.jar;"%REPO%"\slf4j-log4j12-1.7.5.jar;"%REPO%"\jackson-core-asl-1.8.8.jar;"%REPO%"\jackson-mapper-asl-1.8.8.jar;"%REPO%"\avro-1.7.6-cdh5.3.5.jar;"%REPO%"\paranamer-2.3.jar;"%REPO%"\snappy-java-1.0.5.jar;"%REPO%"\protobuf-java-2.5.0.jar;"%REPO%"\gson-2.2.4.jar;"%REPO%"\hadoop-auth-2.5.0-cdh5.3.5.jar;"%REPO%"\apacheds-kerberos-codec-2.0.0-M15.jar;"%REPO%"\apacheds-i18n-2.0.0-M15.jar;"%REPO%"\api-asn1-api-1.0.0-M20.jar;"%REPO%"\api-util-1.0.0-M20.jar;"%REPO%"\curator-framework-2.6.0.jar;"%REPO%"\jsch-0.1.42.jar;"%REPO%"\curator-client-2.6.0.jar;"%REPO%"\curator-recipes-2.6.0.jar;"%REPO%"\jsr305-1.3.9.jar;"%REPO%"\zookeeper-3.4.5-cdh5.3.5.jar;"%REPO%"\commons-compress-1.4.1.jar;"%REPO%"\xz-1.0.jar;"%REPO%"\hbase-client-0.98.6-cdh5.3.5.jar;"%REPO%"\hbase-protocol-0.98.6-cdh5.3.5.jar;"%REPO%"\netty-3.6.6.Final.jar;"%REPO%"\htrace-core-2.04.jar;"%REPO%"\findbugs-annotations-1.3.9-1.jar;"%REPO%"\junit-4.11.jar;"%REPO%"\hamcrest-core-1.3.jar;"%REPO%"\hbase-common-0.98.6-cdh5.3.5.jar;"%REPO%"\hadoop-core-2.5.0-mr1-cdh5.3.5.jar;"%REPO%"\hsqldb-1.8.0.10.jar;"%REPO%"\core-3.1.1.jar;"%REPO%"\HBase-1.0-SNAPSHOT.jar

set ENDORSED_DIR=
if NOT "%ENDORSED_DIR%" == "" set CLASSPATH="%BASEDIR%"\%ENDORSED_DIR%\*;%CLASSPATH%

if NOT "%CLASSPATH_PREFIX%" == "" set CLASSPATH=%CLASSPATH_PREFIX%;%CLASSPATH%

@REM Reaching here means variables are defined and arguments have been captured
:endInit

%JAVACMD% %JAVA_OPTS%  -classpath %CLASSPATH% -Dapp.name="HBaseScrabble" -Dapp.repo="%REPO%" -Dapp.home="%BASEDIR%" -Dbasedir="%BASEDIR%" HBaseScrabble %CMD_LINE_ARGS%
if %ERRORLEVEL% NEQ 0 goto error
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
set ERROR_CODE=%ERRORLEVEL%

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set CMD_LINE_ARGS=
goto postExec

:endNT
@REM If error code is set to 1 then the endlocal was done already in :error.
if %ERROR_CODE% EQU 0 @endlocal


:postExec

if "%FORCE_EXIT_ON_ERROR%" == "on" (
  if %ERROR_CODE% NEQ 0 exit %ERROR_CODE%
)

exit /B %ERROR_CODE%
