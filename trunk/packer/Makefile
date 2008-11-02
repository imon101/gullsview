PROJECT = GullsViewPacker
SOURCES = gullsview/*.java


all: build run

build: jar
	cp $(PROJECT).jar ..

javac:
	rm -rf classes
	mkdir -p classes
	cd src; javac -d ../classes -encoding UTF-8 -Xlint:unchecked $(SOURCES)

jar: javac
	cd classes; jar -cfm ../$(PROJECT).jar ../$(PROJECT).mf *
	jar -uf $(PROJECT).jar -C ../javame GullsView.mjar
	cd res; jar -uf ../$(PROJECT).jar *

run:
	java -jar $(PROJECT).jar

clean:
	rm -rf classes
	rm -f $(PROJECT).jar


