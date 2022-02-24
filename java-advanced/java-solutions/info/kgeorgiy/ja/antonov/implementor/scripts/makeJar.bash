#!/bin/bash

cd ../../../../../..
#usege [source path::where all .class files are] [manifest path::where path is] [out path::where all .jar goes]
SOURCE_PATH=$1
MANIFEST_PATH=$2
JAR_PATH=$(echo "$SOURCE_PATH" | sed -e 's/\//\./g' | sed -e 's/^..//g')

#KG_PATH_IMPLEMENTOR="info.kgeorgiy.java.advanced.implementor"

jar cfm "$JAR_PATH".jar "$MANIFEST_PATH" "$SOURCE_PATH"/*.class