# 定义变量
$sourceJarPath = "target\material-0.0.1-SNAPSHOT.jar" # 源 jar 文件路径
$tmpDir = ".\tmp" # 临时目录
$depsInfoFile = "deps.info" # deps.info 文件路径
$releaseDir = "release" # jlink 输出目录

# 函数：记录日志到控制台
function Log-Message($message) {
    Write-Host "$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss'): $message"
}

try {
    # 清理 tmp 目录
    if (Test-Path -Path $tmpDir) {
        Log-Message "Cleaning up the tmp directory."
        Remove-Item -Path $tmpDir -Recurse -Force
    }

    # 创建 tmp 目录
    Log-Message "Creating tmp directory."
    New-Item -ItemType Directory -Path $tmpDir

    # 清理 release 目录
    if (Test-Path -Path $releaseDir) {
        Log-Message "Cleaning up the release directory."
        Remove-Item -Path $releaseDir -Recurse -Force
    }

    # 拷贝 jar 文件到 tmp 目录
    Log-Message "Copying the source jar to tmp directory."
    Copy-Item $sourceJarPath "$tmpDir\material.jar"

    # 使用 jar 命令解压 jar 文件到 tmp 目录
    Log-Message "Unzipping the jar file."
    Set-Location $tmpDir
    jar -xf "material.jar"

    # 执行 jdeps 命令生成 deps.info 文件
    Log-Message "Generating dependencies info."
    jdeps --ignore-missing-deps -q --recursive --multi-release 21 --print-module-deps --class-path 'BOOT-INF/lib/*' .\material.jar > ..\$depsInfoFile

    # 返回上级目录执行 jlink 命令
    Log-Message "Executing jlink to create custom runtime."
    Set-Location ..
    jlink --add-modules $(Get-Content $depsInfoFile) --bind-services --strip-debug --compress zip-9 --no-header-files --no-man-pages --output $releaseDir

    # 将源 jar 文件拷贝到 release 目录
    Log-Message "Copying the original jar to the release directory."
    Copy-Item $sourceJarPath "$releaseDir\material.jar"

    Log-Message "Copying run script to the release directory."
    Copy-Item release-utils\run.ps1 "$releaseDir\run.ps1"

    # 清理 tmp 目录
    Log-Message "Cleaning up the tmp directory after completion."
    Remove-Item -Path $tmpDir -Recurse -Force

    Log-Message "Script completed successfully."
}
catch {
    # 记录异常信息到控制台
    Log-Message "An error occurred: $_"
    # 清理 tmp 目录
    try {
        Log-Message "Attempting to clean up the tmp directory after an error."
        Remove-Item -Path $tmpDir -Recurse -Force
    }
    catch {
        Log-Message "Error cleaning up the tmp directory after an error: $_"
    }
    # 退出脚本并返回错误代码
    exit 1
}