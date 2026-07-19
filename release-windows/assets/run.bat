@echo off

start "" ".\runtime\bin\javaw.exe" ^
    --module-path ".;jfx\lib" ^
    --enable-native-access=javafx.graphics ^
    --add-modules JeJFX,javafx.base,javafx.graphics,javafx.controls,javafx.fxml ^
    -jar app.jar
