#!/bin/bash

./runtime/bin/java \
	--module-path ".:jfx" \
    --enable-native-access=javafx.graphics \
	--add-modules JeJFX,javafx.base,javafx.graphics,javafx.controls,javafx.fxml \
	-jar app.jar
