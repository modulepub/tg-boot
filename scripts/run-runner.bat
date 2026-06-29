@echo off
setlocal ENABLEDELAYEDEXPANSION
REM 双击或从 tg-boot 下执行 scripts\run-runner.bat；使用 tg-boot\.tools 内 JDK + Maven。
pushd "%~dp0.."
set "TG_BOOT=%CD%"
popd

set "TOOLS=!TG_BOOT!\.tools"
set "JAVA_HOME=!TOOLS!\jdk-21"

set "MAVEN_HOME="
for /d %%D in ("%TOOLS%\apache-maven-*") do set "MAVEN_HOME=%%~fD"

if "!MAVEN_HOME!"=="" (
  echo Maven not found under "!TOOLS!\apache-maven-*"
  exit /b 1
)

if not exist "!JAVA_HOME!\bin\java.exe" (
  echo JDK not found: "!JAVA_HOME!\bin\java.exe"
  exit /b 1
)

set "PATH=!JAVA_HOME!\bin;!MAVEN_HOME!\bin;%PATH%"
cd /d "!TG_BOOT!\spring-boot-starter-module"
echo JAVA_HOME=!JAVA_HOME!
echo MAVEN_HOME=!MAVEN_HOME!
call mvn.cmd -pl spring-boot-starter-runner -am spring-boot:run -DskipTests
