@echo off
title Jvav1 ������
setlocal EnableExtensions EnableDelayedExpansion

:: ----------- ����·�� -----------
set "NORMAL_DIR=%ProgramFiles%\Jvav\Jvav1"
set "SUPER_DIR=%ProgramFiles%\Jvav\SUPER"
set "BAT_NORMAL=jvav1.bat"
set "BAT_SUPER=jvavc.bat"
set "JAR_NAME=Jvav1.jar"
set "REG_KEY=HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment"

:: ----------- ������Ա -----------
fltmc >nul 2>&1
if errorlevel 1 (
    echo ��Ҫ����ԱȨ�ޣ�
    echo ��رձ����ڣ�Ȼ���Ҽ����Թ���Ա������С��˽ű���
    pause & exit /b
)

:: ----------- ���˵� -----------
:menu
cls
echo.
echo    =============================
echo            Jvav1 ������
echo    =============================
echo.
echo    [1] ��װ / ���� jvav1
echo    [2] �� jvav1 ��Ϊ��������������װ
echo    [3] ��ѯ jvav1 �汾
echo    [4] ��ѯ�������������汾
echo    [5] ж�� jvav1
echo    [6] ж�س�����������
echo    [7] �˳�
echo.
set /p choice= ��ѡ�� 1-7��
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
echo [1] ���ڰ�װ / ���� jvav1...
if exist "%~dp0%BAT_NORMAL%" (set "SRC_BAT=%~dp0%BAT_NORMAL%") else (
    echo   ��ǰĿ¼δ�ҵ� %BAT_NORMAL%��
    pause & goto :menu
)
if exist "%~dp0%JAR_NAME%" (set "SRC_JAR=%~dp0%JAR_NAME%") else (
    echo   ��ǰĿ¼δ�ҵ� %JAR_NAME%��
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
echo    jvav1 ��װ��ɣ������´򿪿���̨��Ȼ������ jvavc1 ����ʹ�á�
pause
goto :menu

:: --------------------------------------------------
:install_super
cls
echo.
echo �����������������������������������������
echo  ��������װ����������������������� jvavc ����ָ�� %SUPER_DIR%
echo  �⽫����ϵͳ�ڿ��ܴ��ڵ����� jvavc ���ߣ�
echo  ��������ȷ֪���Լ�����ʲô�������밴 N ���أ�
echo �����������������������������������������
set /p confirm= ������(Y/N)��
if /i "%confirm%"=="Y" goto :do_super
goto :menu

:do_super
echo.
echo [2] ���ڽ� jvav1 ��Ϊ��������������װ...
if exist "%~dp0%BAT_SUPER%" (set "SRC_BAT=%~dp0%BAT_SUPER%") else (
    echo   ��ǰĿ¼δ�ҵ� %BAT_SUPER%��
    pause & goto :menu
)
if exist "%~dp0%JAR_NAME%" (set "SRC_JAR=%~dp0%JAR_NAME%") else (
    echo   ��ǰĿ¼δ�ҵ� %JAR_NAME%��
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
echo    �� jvav1 ��Ϊ��������������װ��ɣ������´򿪿���̨��Ȼ������ jvavc ����ʹ�á�
pause
goto :menu

:: --------------------------------------------------
:version_normal
cls
echo.
if not exist "%NORMAL_DIR%\%JAR_NAME%" (
    echo    jvav1 ��δ��װ��
) else (
    echo    jvav1 �Ѱ�װ��jar �汾��
    powershell -nop -c "&{(Get-Item '%NORMAL_DIR%\%JAR_NAME%').VersionInfo.FileVersionRaw}"
)
pause
goto :menu

:: --------------------------------------------------
:version_super
cls
echo.
if not exist "%SUPER_DIR%\%JAR_NAME%" (
    echo    ��������������δ��װ��
) else (
    echo    �������������Ѱ�װ��jar �汾��
    powershell -nop -c "&{(Get-Item '%SUPER_DIR%\%JAR_NAME%').VersionInfo.FileVersionRaw}"
)
pause
goto :menu

:: --------------------------------------------------
:uninstall_normal
cls
echo.
echo [5] ����ж�� jvav1...
for /f "skip=2 tokens=2*" %%A in ('reg query "%REG_KEY%" /v Path 2^>nul') do set "OLD_P=%%B"
set "NEW_P=%OLD_P:;%NORMAL_DIR%=%"
if not "%OLD_P%"=="%NEW_P%" setx PATH "%NEW_P%" /M >nul
if exist "%NORMAL_DIR%" rd /s /q "%NORMAL_DIR%"
echo    jvav1 ж����ɣ�
pause
goto :menu

:: --------------------------------------------------
:uninstall_super
cls
echo.
echo �����������������������������������������
echo  ������ж�ء����������������������Ƴ� jvavc ���
echo  �⽫����ϵͳ���޷���ʹ�� jvavc��
echo  ��������ȷ֪���Լ�����ʲô�������밴 N ���أ�
echo �����������������������������������������
set /p confirm= ����ж����(Y/N)��
if /i "%confirm%"=="Y" goto :do_unsuper
goto :menu

:do_unsuper
echo.
echo [6] ����ж�س�����������...
for /f "skip=2 tokens=2*" %%A in ('reg query "%REG_KEY%" /v Path 2^>nul') do set "OLD_P=%%B"
set "NEW_P=%OLD_P:;%SUPER_DIR%=%"
if not "%OLD_P%"=="%NEW_P%" setx PATH "%NEW_P%" /M >nul
if exist "%SUPER_DIR%" rd /s /q "%SUPER_DIR%"
echo    ������������ж����ɣ�
pause
goto :menu