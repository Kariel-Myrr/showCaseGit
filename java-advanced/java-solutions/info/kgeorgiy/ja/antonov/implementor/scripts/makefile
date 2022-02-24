all: makeJar

SOURCE_PATH = ./info/kgeorgiy/ja/antonov/implementor
JAVA_PATH = $(SOURCE_PATH)/main
CLASS_PATH = $(SOURCE_PATH)/main
MANIFEST_PATH = ./info/kgeorgiy/ja/antonov/implementor/scripts/MANIFEST.txt

JAR_PATH=$(echo "$CLASS_PATH" | sed -e 's/\//\./g' | sed -e 's/^..//g').jar

MY_PATH = ./

compile:
	./compile.bash $(JAVA_PATH) $(MY_PATH)

jar: compile
	./makeJar.bash $(CLASS_PATH) $(MANIFEST_PATH)
	./cleanClass.bash $(JAVA_PATH)

run:
	java -jar $(JAVA_PATH)

cleanClass:
	./cleanClass.bash $(CLASS_PATH)

cleanAll: cleanClass
	./cleanJar.bash $(MY_PATH) $(CLASS_PATH)

start:
	chmod u+x *.bash
	./start.bash
