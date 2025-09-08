@echo off
title Jvav1 管理器
setlocal EnableExtensions EnableDelayedExpansion

:: ----------- 基础路径 -----------
set "NORMAL_DIR=%ProgramFiles%\Jvav\Jvav1"
set "SUPER_DIR=%ProgramFiles%\Jvav\SUPER"
set "BAT_NORMAL=jvav1.bat"
set "BAT_SUPER=jvavc.bat"
set "JAR_NAME=Jvav1.jar"
set "REG_KEY=HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment"

:: ----------- 检查管理员 -----------
fltmc >nul 2>&1
if errorlevel 1 (
    echo 需要管理员权限！
    echo 请关闭本窗口，然后【右键→以管理员身份运行】此脚本。
    pause & exit /b
)

:: ----------- 主菜单 -----------
:menu
cls
echo.
echo    =============================
echo            Jvav1 管理器
echo    =============================
echo.
echo    [1] 安装 / 更新 jvav1
echo    [2] 将 jvav1 作为超级环境变量安装
echo    [3] 查询 jvav1 版本
echo    [4] 查询超级环境变量版本
echo    [5] 卸载 jvav1
echo    [6] 卸载超级环境变量
echo    [7] 退出
echo.
set /p choice= 请选择 1-7：
if "%choice%"=="1" goto :install_normal
if "%choice%"=="2" goto :install_super
if "%choice%"=="3" goto :version_normal
if "%choice%"=="4" goto :version_super
if "%choice%"=="5" goto :uninstall_normal
if "%choice%"=="6" goto :uninstall_super
if "%choice%"=="7" exit
goto :menu

:: --------------------------------------------------
:install_normal
cls
echo.
echo [1] 正在安装 / 更新 jvav1...
if exist "%~dp0%BAT_NORMAL%" (set "SRC_BAT=%~dp0%BAT_NORMAL%") else (
    echo   当前目录未找到 %BAT_NORMAL%！
    pause & goto :menu
)
if exist "%~dp0%JAR_NAME%" (set "SRC_JAR=%~dp0%JAR_NAME%") else (
    echo   当前目录未找到 %JAR_NAME%！
    pause & goto :menu
)
if not exist "%NORMAL_DIR%" mkdir "%NORMAL_DIR%"
copy /Y "%SRC_BAT%" "%NORMAL_DIR%\%BAT_NORMAL%" >nul
copy /Y "%SRC_JAR%" "%NORMAL_DIR%\%JAR_NAME%" >nul

for /f "skip=2 tokens=2*" %%A in ('reg query "%REG_KEY%" /v Path 2^>nul') do set "OLD_P=%%B"
echo ;%OLD_P%; | find /i ";%NORMAL_DIR%;" >nul && goto :installed_normal
set "NEW_P=%OLD_P%;%NORMAL_DIR%"
setx PATH "%NEW_P%" /M >nul
:installed_normal
echo    jvav1 安装完成！请重新打开控制台，然后输入 jvavc1 即可使用。
pause
goto :menu

:: --------------------------------------------------
:install_super
cls
echo.
echo ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
echo  您即将安装“超级环境变量”——会把 jvavc 命令指向 %SUPER_DIR%
echo  这将覆盖系统内可能存在的其他 jvavc 工具！
echo  除非您明确知道自己在做什么，否则请按 N 返回！
echo ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
set /p confirm= 继续吗？(Y/N)：
if /i "%confirm%"=="Y" goto :do_super
goto :menu

:do_super
echo.
echo [2] 正在将 jvav1 作为超级环境变量安装...
if exist "%~dp0%BAT_SUPER%" (set "SRC_BAT=%~dp0%BAT_SUPER%") else (
    echo   当前目录未找到 %BAT_SUPER%！
    pause & goto :menu
)
if exist "%~dp0%JAR_NAME%" (set "SRC_JAR=%~dp0%JAR_NAME%") else (
    echo   当前目录未找到 %JAR_NAME%！
    pause & goto :menu
)
if not exist "%SUPER_DIR%" mkdir "%SUPER_DIR%"
copy /Y "%SRC_BAT%" "%SUPER_DIR%\%BAT_SUPER%" >nul
copy /Y "%SRC_JAR%" "%SUPER_DIR%\%JAR_NAME%" >nul

for /f "skip=2 tokens=2*" %%A in ('reg query "%REG_KEY%" /v Path 2^>nul') do set "OLD_P=%%B"
echo ;%OLD_P%; | find /i ";%SUPER_DIR%;" >nul && goto :installed_super
set "NEW_P=%OLD_P%;%SUPER_DIR%"
setx PATH "%NEW_P%" /M >nul
:installed_super
echo    将 jvav1 作为超级环境变量安装完成！请重新打开控制台，然后输入 jvavc 即可使用。
pause
goto :menu

:: --------------------------------------------------
:version_normal
cls
echo.
if not exist "%NORMAL_DIR%\%JAR_NAME%" (
    echo    jvav1 尚未安装。
) else (
    echo    jvav1 已安装，jar 版本：
    powershell -nop -c "&{(Get-Item '%NORMAL_DIR%\%JAR_NAME%').VersionInfo.FileVersionRaw}"
)
pause
goto :menu

:: --------------------------------------------------
:version_super
cls
echo.
if not exist "%SUPER_DIR%\%JAR_NAME%" (
    echo    超级环境变量尚未安装。
) else (
    echo    超级环境变量已安装，jar 版本：
    powershell -nop -c "&{(Get-Item '%SUPER_DIR%\%JAR_NAME%').VersionInfo.FileVersionRaw}"
)
pause
goto :menu

:: --------------------------------------------------
:uninstall_normal
cls
echo.
echo [5] 正在卸载 jvav1...
for /f "skip=2 tokens=2*" %%A in ('reg query "%REG_KEY%" /v Path 2^>nul') do set "OLD_P=%%B"
set "NEW_P=%OLD_P:;%NORMAL_DIR%=%"
if not "%OLD_P%"=="%NEW_P%" setx PATH "%NEW_P%" /M >nul
if exist "%NORMAL_DIR%" rd /s /q "%NORMAL_DIR%"
echo    jvav1 卸载完成！
pause
goto :menu

:: --------------------------------------------------
:uninstall_super
cls
echo.
echo ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
echo  您即将卸载“超级环境变量”——会移除 jvavc 命令！
echo  这将导致系统内无法再使用 jvavc！
echo  除非您明确知道自己在做什么，否则请按 N 返回！
echo ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
set /p confirm= 继续卸载吗？(Y/N)：
if /i "%confirm%"=="Y" goto :do_unsuper
goto :menu

:do_unsuper
echo.
echo [6] 正在卸载超级环境变量...
for /f "skip=2 tokens=2*" %%A in ('reg query "%REG_KEY%" /v Path 2^>nul') do set "OLD_P=%%B"
set "NEW_P=%OLD_P:;%SUPER_DIR%=%"
if not "%OLD_P%"=="%NEW_P%" setx PATH "%NEW_P%" /M >nul
if exist "%SUPER_DIR%" rd /s /q "%SUPER_DIR%"
echo    超级环境变量卸载完成！
pause
goto :menu