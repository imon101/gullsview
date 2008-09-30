WTK = /opt/wtk-2.5.2
PREVERIFY = $(WTK)/bin/preverify
EMULATOR = $(WTK)/bin/emulator
CLASSPATH = $(WTK)/lib/cldcapi11.jar:$(WTK)/lib/midpapi20.jar:$(WTK)/lib/jsr082.jar:$(WTK)/lib/jsr179.jar:$(WTK)/lib/jsr75.jar:$(WTK)/lib/jsr184.jar

PROJECT = GullsView
SOURCES = gullsview/*.java


all: build

build: jar

javac:
	rm -rf classes
	mkdir classes
	cd src; javac -d ../classes -bootclasspath $(CLASSPATH) -encoding UTF-8 -source 1.3 -target 1.1 $(SOURCES)

preverify: javac
	rm -rf preverified
	mkdir preverified
	$(PREVERIFY) -d preverified -classpath $(CLASSPATH) classes

jar: preverify
	cd preverified; jar -cfm ../$(PROJECT).mjar ../$(PROJECT).mf *
	cd res; jar -uf ../$(PROJECT).mjar *

arch:
	mkdir -p archive
	tar c src res $(PROJECT).mf Makefile | gzip > archive/$(PROJECT)_`date '+%Y-%m-%d_%H-%M-%S'`.tgz


