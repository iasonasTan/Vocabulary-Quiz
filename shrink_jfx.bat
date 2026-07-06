@echo off
setlocal enabledelayedexpansion

:: %1 Must be a path to a JavaFX-sdk instance.
:: %2 Must be the working relative path.

cd /d "%~2"

echo Copying jfx lib...
mkdir jfx 2>nul
cd jfx

xcopy "%~1\*" . /Y /Q /E

echo Shrinking jfx lib...

for %%F in (*) do (
    set "FILE=%%F"
    set "KEEP=0"

    if "%%F"=="lib\javafx.base.jar" set "KEEP=1"
    if "%%F"=="lib\javafx.controls.jar" set "KEEP=1"
    if "%%F"=="lib\javafx.fxml.jar" set "KEEP=1"
    if "%%F"=="lib\javafx.graphics.jar" set "KEEP=1"
    if "%%F"=="lib\javafx.properties" set "KEEP=1"
    if "%%F"=="lib\javafx-swt.jar" set "KEEP=1"
    if "%%F"=="lib\jdk.jsobject.jar" set "KEEP=1"
    if "%%F"=="lib\jfx.incubator.input.jar" set "KEEP=1"
    if "%%F"=="lib\jfx.incubator.richtext.jar" set "KEEP=1"

    if "%%F"=="bin\decora_sse.dll" set "KEEP=1"
    if "%%F"=="bin\fxplugins.dll" set "KEEP=1"
    if "%%F"=="bin\glass.dll" set "KEEP=1"
    if "%%F"=="bin\gstreamer-lite.dll" set "KEEP=1"
    if "%%F"=="bin\javafx_font_fontconfig.dll" set "KEEP=1"
    if "%%F"=="bin\javafx_font.dll" set "KEEP=1"
    if "%%F"=="bin\javafx_iio.dll" set "KEEP=1"
    if "%%F"=="bin\prism_common.dll" set "KEEP=1"
    if "%%F"=="bin\prism_es2.dll" set "KEEP=1"
    if "%%F"=="bin\prism_sw.dll" set "KEEP=1"

    if "!KEEP!"=="1" (
        echo [+] Keeping : %%F
    ) else (
        echo [-] Removing: %%F
        del "%%F"
    )
)

echo Optimization complete!
endlocal