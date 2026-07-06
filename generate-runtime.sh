#!/bin/bash

# $1 must be the destination of the runtime

echo "Generating runtime..."

"$JAVA_HOME/bin/jlink" \
  --module-path "$JAVA_HOME/jmods" \
  --add-modules java.base,java.desktop,java.scripting,java.xml \
  --strip-debug \
  --no-man-pages \
  --no-header-files \
  --compress=zip-9 \
  --output "$1/runtime"
