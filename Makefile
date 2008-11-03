VERSION=0.9
DIST_SOURCES=GullsViewPacker.jar doc LICENSE README Makefile javame/GullsView.mf javame/Makefile javame/res javame/src packer/GullsViewPacker.mf packer/Makefile packer/res packer/src
BTADDR = 00:22:98:1E:AD:91
UPLOADJAR = GullsView.jar


all: info build

info:
	###################################################################
	# BUILD INSTRUCTIONS
	###################################################################
	# This makefile contains following targets:
	# - build  - builds whole application (both JavaME and Packer)
	# - runmin - creates example midlet suite with only basic features
	#            and runs it in emulator
	# - runmax - creates example midlet suite with all features and
	#            runs it in emulator
	# - clean  - cleans all build binaries and temporary files
	###################################################################

build:
	cd javame; make build
	cd packer; make build

run:
	java -jar GullsViewPacker.jar

runmin:
	java -jar GullsViewPacker.jar -properties resource:/minimal.properties
	cd javame; JAD=GullsView.jad make run

runmax:
	java -jar GullsViewPacker.jar -properties resource:/maximal.properties
	cd javame; JAD=GullsView_FC_BT_LAPI_M3G.jad make run

clean:
	cd javame; make clean
	cd packer; make clean
	rm -f *.jar *.jad
	rm -f *.zip

upload:
	##################################################
	# You must have installed 'obexftp' bluez utility
	# to upload midlet suite into your mobile device.
	# Also set BTADDR constant in this Makefile.
	# Current BTADDR is: $(BTADDR)
	##################################################
	obexftp -b $(BTADDR) -B 7 -p $(UPLOADJAR)

dist: clean build
	zip -r GullsViewPacker_$(VERSION).zip $(DIST_SOURCES) -x '.svn/*' -x '*/.svn/*' -x '*/*/.svn/*'


