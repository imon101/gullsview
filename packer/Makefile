PROJECT = GullsViewPacker
SOURCES = gullsview/*.java


all: build

build: jar
	cp $(PROJECT).jar ..

javac:
	rm -rf classes
	mkdir -p classes
	cd src; javac -d ../classes -Xlint:unchecked $(SOURCES)

jar: javac
	cd classes; jar -cfm ../$(PROJECT).jar ../$(PROJECT).mf *
	jar -uf $(PROJECT).jar -C ../javame GullsView.mjar

run:
	java -jar $(PROJECT).jar -stdio

clean:
	rm -rf classes
	rm -f $(PROJECT).jar


