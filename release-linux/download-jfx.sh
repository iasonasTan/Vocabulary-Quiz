#!/bin/bash

URL="https://download.java.net/java/GA/javafx25.0.2/1ccc8ce474414c94ade008f8833286e8/4/openjfx-25.0.2_linux-x64_bin-sdk.tar.gz"
INSTALL_DIR="jfx-linux"

mkdir -p $INSTALL_DIR
echo "Downloading JavaFX SDK..."
wget -qO- $URL | tar -xz -C $INSTALL_DIR --strip-components=1

echo "JavaFX 25.0.2 installed to $INSTALL_DIR"
