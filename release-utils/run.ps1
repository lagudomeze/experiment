# 定义变量
$jarName = "material.jar"
$javaExe = ".\bin\java"  # 假设 java 可执行文件位于 release 目录下的 bin 文件夹中
$jarPath = ".\$jarName"
$javaOpts = @("-server", "-Xms2g", "-Xmx2g", "-XX:+UseZGC", "-XX:+ZGenerational")
$springOpts = "-Dspring.security.csrf=false"
$loggingOpts = "-Dlogging.level.org.springframework.security=debug"

# 记录日志到控制台
Write-Host "Starting the application at $(Get-Date)"

# 执行 Java 应用程序并持续运行
try {
    & $javaExe -jar ($javaOpts + $springOpts, $loggingOpts, $jarPath)
}
catch {
    Write-Host "Error starting the application: $_" -ForegroundColor Red
    exit 1
}

# 如果 Java 进程退出，捕获退出代码
$host.UI.RawUI.ForegroundColor = "Red"
$host.UI.RawUI.CursorPosition = New-Object System.Management.Automation.Host.Coordinates(0, $host.UI.RawUI.CursorPosition.Y)
$exitCode = $LASTEXITCODE
Write-Host "The application has stopped with exit code $exitCode"
exit $exitCode