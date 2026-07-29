@echo off

start "" ".\runtime\bin\javaw.exe" ^
    --module-path ".;jfx\lib" ^
    --enable-native-access=javafx.graphics ^
    --add-modules JeJFX,JeLib.core,JeLib.io,javafx.base,javafx.graphics,javafx.controls,javafx.fxml ^
    -jar app.jar
