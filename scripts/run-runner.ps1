# 使用仓库内 tg-boot/.tools（JDK + Maven）启动 spring-boot-starter-runner。
# PowerShell：在 tg-boot 根下执行 .\scripts\run-runner.ps1，或直接：powershell -File scripts\run-runner.ps1

$ErrorActionPreference = "Stop"

# $PSCommandPath 在 -File 执行时即为当前脚本绝对路径（比仅依赖 $PSScriptRoot 更可靠）
$scriptPath =
if (-not [string]::IsNullOrWhiteSpace((Get-Variable -Name PSCommandPath -Scope Script -ErrorAction SilentlyContinue).Value)) {
    $PSCommandPath
}
elseif (-not [string]::IsNullOrWhiteSpace($MyInvocation.MyCommand.Path)) {
    $MyInvocation.MyCommand.Path
}
else {
    throw "无法解析脚本路径，请使用 powershell -File scripts\run-runner.ps1 方式运行。"
}

$ScriptsDir = Split-Path -LiteralPath $scriptPath -Parent
$TgBootRoot = Split-Path -LiteralPath $ScriptsDir -Parent

$ToolsRoot = Join-Path $TgBootRoot ".tools"
$JavaHome = Join-Path $ToolsRoot "jdk-21"

if (-not (Test-Path -LiteralPath $ToolsRoot)) {
    throw "找不到 .tools 目录: $ToolsRoot"
}

$mavenDirs = @(Get-ChildItem -LiteralPath $ToolsRoot -Directory -Filter "apache-maven-*" -ErrorAction SilentlyContinue)
$mavenHome = $null
if ($mavenDirs.Count -gt 0) {
    $mavenHome = $mavenDirs[0].FullName
}
else {
    $explicit = Join-Path $ToolsRoot "apache-maven-3.9.9"
    if (Test-Path -LiteralPath $explicit) {
        $mavenHome = $explicit
    }
}
if (-not $mavenHome) {
    throw "未在 $ToolsRoot 下找到 apache-maven-* ，请解压 Maven。"
}

$MavenBin = Join-Path $mavenHome "bin"
$mvnExe = Join-Path $MavenBin "mvn.cmd"

if (-not (Test-Path -LiteralPath (Join-Path $JavaHome "bin\java.exe"))) {
    throw "未找到 JDK: $(Join-Path $JavaHome 'bin\java.exe')"
}
if (-not (Test-Path -LiteralPath $mvnExe)) {
    throw "未找到 Maven: $mvnExe"
}

$env:JAVA_HOME = $JavaHome
$env:Path = "$(Join-Path $JavaHome 'bin');$MavenBin;$env:Path"

$ModuleDir = Join-Path $TgBootRoot "spring-boot-starter-module"
Set-Location -LiteralPath $ModuleDir

Write-Host "JAVA_HOME=$($env:JAVA_HOME)"
Write-Host "Maven=$mvnExe"
Write-Host "ModuleDir=$ModuleDir"

& $mvnExe @("-pl", "spring-boot-starter-runner", "-am", "spring-boot:run", "-DskipTests")
