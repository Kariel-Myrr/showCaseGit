#!/bin/bash

cd ../../../../../..

JAR_DIR=$1
SOURCE_PATH=$2
JAR_NAME=$(echo "$SOURCE_PATH" | sed -e 's/\//\./g' | sed -e 's/^..//g')

rm -f "$JAR_DIR/$JAR_NAME.jar"