#!/bin/bash

cd ../../../../../..

#usege [source path::where all .java are] [out path::where all .class goes]

SOURCE_PATH=$1
OUT_PATH=$2

KG_PATH_IMPLEMENTOR="info.kgeorgiy.java.advanced.implementor"

javac -cp $KG_PATH_IMPLEMENTOR.jar -d "$OUT_PATH" "$SOURCE_PATH"/*.java

