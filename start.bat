@echo off
echo === Khoi dong emulator ===
start "Emulator" cmd /k emulator -avd Pixel_8a_API_34

echo === Cho emulator boot xong ===
adb wait-for-device
:waitboot
for /f %%i in ('adb shell getprop sys.boot_completed') do set BOOT=%%i
if not "%BOOT%"=="1" (
    timeout /t 3 >nul
    goto waitboot
)
echo Emulator da san sang!

echo === Khoi dong Appium ===
start "Appium" cmd /k appium

echo Xong! Mo IntelliJ de chay test.
pause