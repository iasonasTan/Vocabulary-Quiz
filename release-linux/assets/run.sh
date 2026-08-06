#!/bin/bash

cd -- "$(dirname -- "$(realpath "$0")")" || exit 1

./runtime/bin/java \
  --module-path ".:jfx" \
  --enable-native-access=javafx.graphics \
	--add-modules JeJFX,JeLib.core,JeLib.io,javafx.base,javafx.graphics,javafx.controls,javafx.fxml \
	-jar app.jar
