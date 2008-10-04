WTK = /opt/wtk-2.5.2
PREVERIFY = $(WTK)/bin/preverify
EMULATOR = $(WTK)/bin/emulator
CLASSPATH = $(WTK)/lib/cldcapi11.jar:$(WTK)/lib/midpapi20.jar:$(WTK)/lib/jsr082.jar:$(WTK)/lib/jsr179.jar:$(WTK)/lib/jsr75.jar:$(WTK)/lib/jsr184.jar

PROJECT = GullsView
SOURCES = gullsview/*.java


all: build

build: buildinfo jar

buildinfo:
	####################################################################
	# BUILD INSTRUCTIONS
	####################################################################
	# This makefile expects Sun Wireless Toolkit v. 2.5.2 installed
	# at following path:
	# $(WTK)
	# May as well work with other WTK wersions (untested though).
	# If your WTK path differs from $(WTK)
	# please change it inside this makefile (javame/Makefile)
	####################################################################

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

run:
	if [ -z "$(JAD)" ]; then echo *** Please set JAD environment variable ***; exit 1; fi
	$(EMULATOR) -Xdescriptor:$(JAD) -Xdomain:manufacturer

clean:
	rm -rf classes
	rm -rf preverified
	rm -f $(PROJECT).mjar


