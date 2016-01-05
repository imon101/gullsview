This application allows user to offline browse maps on screen of their mobile phone. Also enables position tracking on devices supporting either JSR 179 (Location API) or JSR 082 (Bluetooth API) with external Bluetooth GPS module. Mapping data (rasters, tracks) could be either packed into midlet suite or could be reached on local filesystem (memory card) through JSR 075 FC (Optional File Connection API). Application contains support for sharing user's current position with friends through microblogging service (Twitter.com is currently supported).

![http://gullsview.googlecode.com/svn/trunk/doc/image/k800i.jpg](http://gullsview.googlecode.com/svn/trunk/doc/image/k800i.jpg)

Application running on phone SonyEricsson K800i


## Description ##

Application Gull's View allows user to use mobile device as off-line raster map browser with navigation feature using internal GPS capabilities or external Bluetooth GPS module.

Application itself does not contain any maps, user has to collect maps from other sources - eg. by downloading from some mapping service. Before downloading always read all terms of use or licenses of certain mapping service!

Application requires support of MIDP 2.0 (CLDC 1.1).

Currently are implemented following features:
  * displaying map viewport and continuous moving in map using cursor keys
  * optional fullscreen view
  * optional landscape view (turned by 90 deg)
  * drawing user's own traces and points of interest into map
  * displaying length of every trace
  * visualizing directions to selected target and its distance
  * saving application state when exiting and its restoring during following start
  * temporary pausing application
  * switching permanent display backlight (available only on some SonyEricsson or Nokia devices)
  * location using internal GPS (available on devices with support of JSR-179 Location API)
  * Twitter support used for current position reporting

Experimentally:
  * location using external GPS Bluetooth module (available on devices with support of JSR-82 Bluetooth API) - untested
  * approximate location using currently registered BTS (GSM signal base) (available on some SonyEricsson devices)
  * 3D view

![http://gullsview.googlecode.com/svn/trunk/doc/image/gullsview.png](http://gullsview.googlecode.com/svn/trunk/doc/image/gullsview.png)

Application Gull's View workflow diagram


## Examples of generated mobile applications ##

Basic example without any add-on features (should run on most devices):
[JAD](http://gullsview.googlecode.com/svn/trunk/example/GullsViewExample1.jad)
[JAR](http://gullsview.googlecode.com/svn/trunk/example/GullsViewExample1.jar)

Example for devices with support of JSR-75 (file system):
[JAD](http://gullsview.googlecode.com/svn/trunk/example/GullsViewExample2_FC.jad)
[JAR](http://gullsview.googlecode.com/svn/trunk/example/GullsViewExample2_FC.jar)
[Unpack this archive into device's memory card before running](http://gullsview.googlecode.com/svn/trunk/example/GullsViewExample2_FC_DATA.zip)

Example for devices with support of internal GPS chip (JSR-179):
[JAD](http://gullsview.googlecode.com/svn/trunk/example/GullsViewExample3_LAPI.jad)
[JAR](http://gullsview.googlecode.com/svn/trunk/example/GullsViewExample3_LAPI.jar)

Example for devices with support of Bluetooth API (JSR-82) and with external Bluetooth GPS module:
[JAD](http://gullsview.googlecode.com/svn/trunk/example/GullsViewExample4_BT.jad)
[JAR](http://gullsview.googlecode.com/svn/trunk/example/GullsViewExample4_BT.jar)


## Download ##

Application is hosted on **code.google.com**, download please on this address:
http://code.google.com/p/gullsview/downloads/list


## Instalation and Running ##

Application requires "Java SE Runtime Environment (JRE) 6" (Java SE version 1.6.x) installend on your PC. You can download it [here](http://java.sun.com/javase/downloads).

Application is not installed, only unpack downloaded archive and run file
GullsViewPacker.jar by clicking or from text console using command
```
java -jar GullsViewPacker.jar [-stdio] [-properties FILE] [-swing]
```
where options means:
```
-stdio - runs application in text mode (from text console)
-properties FILE - runs application in batch mode - answers to all questions are read from FILE in Java Properties format
-swing - runs application in graphic mode (default)
```


## Application Startup Message ##

This application will guide you in several steps through the process of new mobile mapping
application generation.

First of all prepare yourself a map raster data in the form of square images - tiles, from which entire map will be assembled.
Tile file names choose with respect to format ROW\_COL.suffix, where ROW is tile row index and COL is tile column index (eg. 10\_12.png, 11\_1.png, etc.).
Left top tile's index is 0:0, file will be therefore named eg. 0\_0.png

It is also good to know how to transform map coordinates into real earth coordinates (latitude, longitude)
This application allows transformation of coordinates in two ways - using Mercator projection or using bilinear transformation:
  * Mercator - is geoid to cylinder projection with pyramidal tile division to 4 sub-tiles with higher resolution, is used eg. by project www.openstreetmap.org or maps.google.com. If your tiles are in this projection, you need to know only these three values: X (column) and Y (row) coordinate of left-top tile and zoom level index (0 means entire Earth in single tile, 1 means whole Earth in 4 tiles, 2 - in 16 tiles, etc. These three values are mostly contained in URL address used for downloading certain tile from tiling server.
  * Bilinear transformation - in this case coordinate is interpolated from inserted Earth coordinates (latitude, longitude) of three map's corners.

Also it is necessary to know parameters of mobile device to which is mapping application targeted.
Support of certain API extensions is important - eg. ability of Java application to access device file system, Bluetooth interface, etc.
If you are sure your device supports JSR-75-FileConnection, it is possible NOT to insert tiles into JAR archive of mobile application, but only copy them into memory card and let mobile application to read them using JSR-75 API.

Attention - certain mobile device vendors do not allow user to enable permanent access to memory card (file system) from Java application and annoy user with is-it-safe dialogs on every file access (on every single tile load).

If you have prepared all map tiles and coordinate information you can start to answer to this wizard's questions.
After wizard's finish in selected directory will be created pair of files suffixed JAD and JAR that are ready to deploy to mobile device by standard ways.


## Licence ##

Copyleft 2008 Tomáš Darmovzal
This application is a free software shared under terms and conditions of GPLv3.


## Notice ##

This application is provided without any data content (raster maps) or with data content shared under license compatible to GPLv3. Creator of certain JAR archive (midlet suite) is responsible for data content distributed with this application. If you suspect data content breaks license or any law valid in your country please uninstall application immediately.


## Mobile Application Controls ##

  * pound `[#]` - switch landscape / portrait view
  * asterisk `[*]` - switch fullscreen mode
  * fire button - places path continuation point
  * `[5]` - display distance from target
  * arrow and numeric keys - controls map scroll
