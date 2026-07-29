#!/bin/bash
set -e

JAVAFX=$1 # $1 Must be the path to a JavaFX SDK instance.
JAVA=$2   # $2 Must be the path to a Java JDK instance.

echo "Creating release..."

echo "[0/7] Validating data."
if [[ $JAVA == "" || $JAVAFX == "" ]] ; then
  echo "Invalid arguments."
	echo "\$1 Must be the path to a JavaFX SDK instance."
	echo "\$2 Must be the path to a Java JDK instance."
	echo "Aborting."
	exit
fi

echo "[1/7] Adapting folders and files."
if [[ -d "temp" ]] ; then
  rm -rf temp/
fi
mkdir temp/

echo "[2/7] Generating a runtime."
"$JAVA"/bin/jlink \
  --module-path "$JAVA_HOME/jmods" \
  --add-modules java.base,java.desktop,java.scripting,java.xml,jdk.unsupported \
  --strip-debug \
  --no-man-pages \
  --no-header-files \
  --compress=zip-9 \
  --output "temp/runtime"

echo "[3/7] Copying libraries."
cp ../libs/* "temp/"
mkdir "temp/jfx"

cd "temp/jfx"
cp "$JAVAFX"/lib/* .

echo "[4/7] Shrinking libraries."

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

for file in *; do
    if [ -d "$file" ] ; then
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
cd ../..

echo "[5/7] Copying files."
cp ../app/build/libs/app.jar temp/

echo "[6/7] Adding run script."
cp ./assets/run.sh ./temp/
chmod +x ./temp/run.sh

echo "[7/7] Zipping release."
if [[ -d "../generated" ]] ; then
  rm -rf ../generated
fi
mkdir ../generated
cd temp
zip -r ../../generated/Vocabulary-Quiz-Linux.zip .

echo "Done!"
