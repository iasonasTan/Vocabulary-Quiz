#!/bin/bash

# Exit on any error
set -e

# $1 Must be a path to a JavaFX-sdk instance.
# $2 Must be the working relative path.

cd $2

echo "Copying jfx lib..."
mkdir jfx
cd jfx
cp "$1"/lib/* .

#echo "==================================="
#echo " Removing Unused JavaFX Components "
#echo "==================================="

echo "Shrinking jfx lib..."

# 1. Define the exact files we want to KEEP based on your list
declare -A KEEP_FILES

# JARs and configuration files to keep
KEEP_FILES["javafx.base.jar"]=1
KEEP_FILES["javafx.controls.jar"]=1
KEEP_FILES["javafx.fxml.jar"]=1
KEEP_FILES["javafx.graphics.jar"]=1
KEEP_FILES["javafx.properties"]=1
KEEP_FILES["javafx-swt.jar"]=1
KEEP_FILES["jdk.jsobject.jar"]=1
KEEP_FILES["jfx.incubator.input.jar"]=1
KEEP_FILES["jfx.incubator.richtext.jar"]=1

# Native libraries (.so) to keep
KEEP_FILES["libdecora_sse.so"]=1
KEEP_FILES["libfxplugins.so"]=1
KEEP_FILES["libglassgtk3.so"]=1
KEEP_FILES["libglass.so"]=1
KEEP_FILES["libgstreamer-lite.so"]=1
KEEP_FILES["libjavafx_font_freetype.so"]=1
KEEP_FILES["libjavafx_font_pango.so"]=1
KEEP_FILES["libjavafx_font.so"]=1
KEEP_FILES["libjavafx_iio.so"]=1
KEEP_FILES["libprism_common.so"]=1
KEEP_FILES["libprism_es2.so"]=1
KEEP_FILES["libprism_sw.so"]=1

# 2. Loop through every item in the current directory
for file in *; do
    # Skip directories and skip the script itself
    if [ -d "$file" ] || [ "$file" == "shrink_jfx.sh" ]; then
        continue
    fi

    # Check if the file is in our safe list
    if [ -z "${KEEP_FILES[$file]}" ]; then
        echo "[-] Removing: $file"
        rm "$file"
    else
        echo "[+] Keeping : $file"
    fi
done

#echo "========================"
#echo " Optimization complete! "
#echo "========================"
