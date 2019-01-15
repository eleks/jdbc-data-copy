@echo off

set JAR_DIR=.\lib
mkdir %JAR_DIR% 2> nul
rem download required jars: groovy and jdbc drivers
call :jar http://central.maven.org/maven2/org/codehaus/groovy groovy-all 2.4.5
call :jar http://central.maven.org/maven2/org/mariadb/jdbc mariadb-java-client 2.3.0
call :jar http://central.maven.org/maven2/com/h2database h2 1.4.197
call :jar http://central.maven.org/maven2/com/microsoft/sqlserver mssql-jdbc 7.0.0.jre8

echo export...
rem run groovy
java -cp util;%JAR_DIR%\* groovy.ui.GroovyMain DataCopy.groovy
if errorlevel=1 exit 1

echo SUCCESS
exit 0


:jar
set JAR_BASE=%1
set JAR_NAME=%2
set JAR_VER=%3
if exist %JAR_DIR%\%JAR_NAME%-%JAR_VER%.jar exit /B 0
set JAR_URL=%JAR_BASE%/%JAR_NAME%/%JAR_VER%/%JAR_NAME%-%JAR_VER%.jar
echo download %JAR_URL%
PowerShell -Command "& {Invoke-WebRequest '%JAR_URL%' -Method 'GET' -OutFile '%JAR_DIR%\%JAR_NAME%-%JAR_VER%.jar'}"
if errorlevel=1 exit 1
exit /B 0

