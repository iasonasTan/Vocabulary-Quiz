#!/bin/bash
set -e

# $1 Must be the path to a JavaFX-sdk instance.

echo "Creating release..."

if [[ $1 == "" ]] then
	echo "\$1 Must be the path to a JavaFX-sdk instance."
	echo "Aborting."
	exit
fi

mkdir -p temp

./generate-runtime.sh temp

./shrink_jfx.sh $1 temp

echo "Copying main jar..."
cp app/build/libs/app.jar temp

echo "Copying libs..."
cp libs/* temp

echo "Creating run script..."
touch temp/run.sh
chmod +x temp/run.sh
cp script-run-sh.txt temp/run.sh

mkdir -p release
cd temp

echo "Zipping release..."
zip -r ../release/VocabularyHelper.zip .

echo "Done! [release generated at ./release/VocabularyHelper.zip]"
