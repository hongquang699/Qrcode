@echo off
title QR Code Generator
cd /d "%~dp0"

where python >nul 2>nul
if %errorlevel% equ 0 (
    start "" python app_gui.py
    exit /b 0
)

where py >nul 2>nul
if %errorlevel% equ 0 (
    start "" py app_gui.py
    exit /b 0
)

echo [ERROR] Python not found on your system!
echo Please install Python and make sure "Add Python to PATH" is checked.
pause
exit /b 1
