set "URL=https://download.java.net/java/GA/javafx25.0.2/1ccc8ce474414c94ade008f8833286e8/4/openjfx-25.0.2_windows-x64_bin-sdk.zip"
set "INSTALL_DIR=jfx-windows"

set "TEMP_FILE=%TEMP%\openjfx.zip"
set "EXTRACT_TEMP=%TEMP%\javafx_extracted"

if exist "%INSTALL_DIR%" rmdir /s /q "%INSTALL_DIR%"
mkdir "%INSTALL_DIR%"

if exist "%EXTRACT_TEMP%" rmdir /s /q "%EXTRACT_TEMP%"
mkdir "%EXTRACT_TEMP%"

echo Downloading javafx sdk...
curl -L -o "%TEMP_FILE%" "%URL%"

echo Unzipping javafx sdk...
tar -xf "%TEMP_FILE%" -C "%EXTRACT_TEMP%"

for /d %%d in ("%EXTRACT_TEMP%\*") do (
    xcopy /E /Y "%%d\*" "%INSTALL_DIR%\"
)

del /f /q "%TEMP_FILE%"
rmdir /s /q "%EXTRACT_TEMP%"

echo JavaFX downloaded successfully!