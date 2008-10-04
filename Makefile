BTADDR = 00:22:98:1E:AD:91

all: build

build:
	cd javame; make build
	cd packer; make build

runmax:
	java -jar GullsViewPacker.jar -properties resource:/minimal.properties
	cd javame; JAD=GullsView_FC_BT_LAPI_M3G.jad make run

upload:
	obexftp -b $(BTADDR) -B 7 -p GullsView.jar


