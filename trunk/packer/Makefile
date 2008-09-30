PROJECT = GullsViewPacker
SOURCES = gullsview/*.java


all: bnr

bnr: build run

build: jar

javac:
	rm -rf classes
	mkdir -p classes
	cd src; javac -d ../classes $(SOURCES)

jar: javac
	cd classes; jar -cfm ../$(PROJECT).jar ../$(PROJECT).mf *
	jar -uf $(PROJECT).jar -C ../javame GullsView.mjar

run:
	java -jar $(PROJECT).jar -stdio


