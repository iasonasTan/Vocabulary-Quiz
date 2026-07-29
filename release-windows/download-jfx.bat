set "URL=https://java.net"
set "INSTALL_DIR=jfx-windows"
set "TEMP_FILE=%TEMP%\openjfx.zip"
set "EXTRACT_TEMP=%TEMP%\javafx_extracted"

if not exist "%INSTALL_DIR%" mkdir "%INSTALL_DIR%"
if exist "%EXTRACT_TEMP%" rmdir /s /q "%EXTRACT_TEMP%"
mkdir "%EXTRACT_TEMP%"

echo Downloading javafx sdk...
curl -L -o "%TEMP_FILE%" "%URL%"

echo Unzipping javafx sdk...
tar -xf "%TEMP_FILE%" -C "%EXTRACT_TEMP%"

for /d %%i in ("%EXTRACT_TEMP%\*") do move "%%i\*" "%INSTALL_DIR%\"

del /f /q "%TEMP_FILE%"
rmdir /s /q "%EXTRACT_TEMP%"

echo JavaFX downloaded successfully!