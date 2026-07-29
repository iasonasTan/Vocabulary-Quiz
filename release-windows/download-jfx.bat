$URL = "https://download.java.net/java/GA/javafx25.0.2/1ccc8ce474414c94ade008f8833286e8/4/openjfx-25.0.2_windows-x64_bin-sdk.zip"
$INSTALL_DIR = "jfx-windows"
$TEMP_FILE = "$env:TEMP\openjfx.zip"

if (!(Test-Path $INSTALL_DIR)) {
    New-Item -ItemType Directory -Force -Path $INSTALL_DIR | Out-Null
}

Write-Host "Downloading javafx sdk..."
Invoke-WebRequest -Uri $URL -OutFile $TEMP_FILE

Write-Host "Unzipping javafx sdk..."
tar -xzf $TEMP_FILE -C $INSTALL_DIR --strip-components=1

Remove-Item $TEMP_FILE

Write-Host "JavaFX downloaded successfully!"
