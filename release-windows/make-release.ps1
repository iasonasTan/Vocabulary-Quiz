param (
    [Parameter(Mandatory=$false, Position=0)]
    [string]$JAVAFX,

    [Parameter(Mandatory=$false, Position=1)]
    [string]$JAVA
)

# Mimics 'set -e' by stopping the script if an error occurs
$ErrorActionPreference = "Stop"

Write-Host "Creating release..."

Write-Host "[0/7] Validating data."
if ([string]::IsNullOrWhiteSpace($JAVA) -or [string]::IsNullOrWhiteSpace($JAVAFX)) {
    Write-Host "Invalid arguments."
    Write-Host "`$1 (Position 0) Must be the path to a JavaFX SDK instance."
    Write-Host "`$2 (Position 1) Must be the path to a Java JDK instance."
    Write-Host "Aborting."
    exit
}

Write-Host "[1/7] Adapting folders and files."
if (Test-Path "temp") {
    Remove-Item -Recurse -Force "temp"
}
New-Item -ItemType Directory -Path "temp" | Out-Null

Write-Host "[2/7] Generating a runtime."
$jlinkExe = Join-Path $JAVA "bin\jlink.exe"
& $jlinkExe `
    --module-path "$env:JAVA_HOME\jmods" `
    --add-modules java.base,java.desktop,java.scripting,java.xml,jdk.unsupported `
    --strip-debug `
    --no-man-pages `
    --no-header-files `
    --compress=zip-9 `
    --output "temp\runtime"

Write-Host "[3/7] Copying libraries."
# Silently continue if libs directory is empty or missing
Copy-Item -Path "..\libs\*" -Destination "temp\" -Recurse -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path "temp\jfx" | Out-Null
Copy-Item -Path "$JAVAFX\lib\*" -Destination "temp\jfx\" -Recurse

Write-Host "[4/7] Shrinking libraries."
# Define the exact files to KEEP
$KeepFiles = @{
    "javafx.base.jar"            = $true
    "javafx.controls.jar"        = $true
    "javafx.fxml.jar"            = $true
    "javafx.graphics.jar"        = $true
    "javafx.properties"          = $true
    "javafx-swt.jar"             = $true
    "jdk.jsobject.jar"           = $true
    "jfx.incubator.input.jar"    = $true
    "jfx.incubator.richtext.jar" = $true

    # Native Windows libraries (.dll instead of .so, dropping the 'lib' prefix)
    "decora_sse.dll"             = $true
    "fxplugins.dll"              = $true
    "glass.dll"                  = $true
    "gstreamer-lite.dll"         = $true
    "javafx_font_freetype.dll"   = $true
    "javafx_font.dll"            = $true
    "javafx_iio.dll"             = $true
    "prism_common.dll"           = $true
    "prism_es2.dll"              = $true
    "prism_sw.dll"               = $true
    "prism_d3d.dll"              = $true # Windows Direct3D pipeline
}

# Iterate through files in the temp/jfx folder
Get-ChildItem -Path "temp\jfx" -File | ForEach-Object {
    if (-not $KeepFiles.ContainsKey($_.Name)) {
        Write-Host "[-] Removing: $($_.Name)"
        Remove-Item $_.FullName -Force
    } else {
        Write-Host "[+] Keeping : $($_.Name)"
    }
}

Write-Host "[5/7] Copying files."
Copy-Item -Path "..\app\build\libs\app.jar" -Destination "temp\"

Write-Host "[6/7] Adding run script."
# We assume you have a run.bat for Windows equivalent to run.sh
Copy-Item -Path ".\assets\run.bat" -Destination ".\temp\"

Write-Host "[7/7] Zipping release."
if (Test-Path "..\generated") {
    Remove-Item -Recurse -Force "..\generated"
}
New-Item -ItemType Directory -Path "..\generated" | Out-Null

# Compress-Archive handles the native zip creation
Compress-Archive -Path "temp\*" -DestinationPath "..\generated\Vocabulary-Quiz-Windows.zip" -Force

Write-Host "Done!"
