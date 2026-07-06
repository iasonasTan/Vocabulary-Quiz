@echo off

REM %1 Must be the path to a JavaFX-sdk instance.

echo Creating release...

if "%1"=="" (
    echo "Error: Must provide the path to a JavaFX-sdk instance."
    echo "Usage: .\create-release.bat C:\path\to\javafx-sdk"
    echo "Aborting."
    exit /b
)

mkdir temp 2>nul

call .\generate-runtime.bat temp

call .\shrink_jfx.bat "%~1" temp

echo Copying main jar...
copy "app\build\libs\app.jar" "temp\" /Y

echo Copying libs...
if exist libs\ copy "libs\*" "temp\" /Y

echo Creating run script...
:: (Note: chmod doesn't exist on Windows, so we omit it. Windows doesn't need +x execution flags anyway)
copy "script-run-bat.txt" "temp\run.bat" /Y

mkdir release 2>nul

echo Zipping release...
:: We wrap PowerShell's Compress-Archive so it runs perfectly inside this Batch file
powershell -Command "Compress-Archive -Path .\temp\* -DestinationPath .\release\VocabularyHelper.zip -Force"

echo Done! [release generated at ./release/VocabularyHelper.zip]