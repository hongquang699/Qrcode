@echo off
title GitHub Sync - Push Code
cd /d "%~dp0"

echo ========================================================
echo        GITHUB SYNCHRONIZATION SERVICE
echo        Target: https://github.com/hongquang699/Qrcode
echo ========================================================
echo.

:: Check Python
where python >nul 2>nul
if %errorlevel% equ 0 (
    set "PY_CMD=python"
) else (
    where py >nul 2>nul
    if %errorlevel% equ 0 (
        set "PY_CMD=py"
    ) else (
        echo [ERROR] Python not found on your system!
        pause
        exit /b 1
    )
)

:: Prompt for optional commit message
set /p msg="Enter commit message (Press Enter for auto-generated timestamp): "

echo.
echo Executing Git Push to GitHub...
echo.

if "%msg%"=="" (
    %PY_CMD% github_service.py
) else (
    %PY_CMD% github_service.py "%msg%"
)

echo.
echo ========================================================
echo Execution finished.
echo ========================================================
pause
