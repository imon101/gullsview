package gullsview;

import java.util.*;


public class Resources extends ListResourceBundle {
	private static final Object[][] CONTENTS = new Object[][]{
		{"error", "Error"},
		{"error-before", "Following error occured"},
		{"error-after", "Please exit application now"},
		{"error-not-int", "Insert integer value please"},
		{"error-not-double", "Insert number please"},
		{"error-not-boolean", "Insert yes or no please"},
		{"error-incorrect-path", "Incorrect path to directory"},
		{"error-not-exist", "Directory does not exist"},
		{"error-not-directory", "Path is not a directory"},
		{"error-empty", "Empty input is not allowed"},
		{"error-not-coord", "Inserted text cannot be parsed as coordinate"},
		{"error-file-not-exist", "File does not exsist"},
		{"error-not-file", "Path is not a file"},
		{"yes", "yes"},
		{"no", "no"},
		{"title", "Gull's View - Packer"},
		{"accept", "Accept"},
		{"question", "Question"},
		{"answer", "Answer"},
		{"enable-fc", "Enable support of JSR 075 - File Connection? (on devices without this API extension application won't work)"},
		{"enable-bt", "Enable support of JSR 082 - Bluetooth API? (on devices without this API extension application won't work)"},
		{"enable-lapi", "Enable support of JSR 179 - Location API? (on devices without this API extension application won't work)"},
		{"enable-m3g", "Enable support of JSR 184 - Mobile 3D Graphics? (on devices without this API extension application won't work)"},
		{"enable-bts", "Enable support of localisation using BTS? (only works on certain SonyEricsson and Nokia devices)"},
		{"output-path", "Insert directory path for saving generated midlet suite (mobile application - JAR+JAD)"},
		{"output-name", "Insert name of generated midlet suite (mobile application - JAR+JAD)"},
		{"start", "Midlet suite generation started"},
		{"finish", "Finished"},
		{"accept-to-close", "Enter empty input to exit this application"},
		{"world", "World"},
		{"map-count", "Insert map count"},
		{"map-name", "Map identifier (no spaces or special characters)"},
		{"map-title", "Map title"},
		{"map-vendor", "Your name and surname"},
		{"map-scale", "Integer value of map zoom (greater number = higher detail)"},
		{"map-segment", "Size (width = height) of map tile (square map image) in pixels"},
		{"map-xcount", "Number of map tiles in horizontal axis"},
		{"map-ycount", "Number of map tiles in vertical axis"},
		{"map-mercator", "Are mapping data created in Mercator projection? (Mercator is used by eg. project www.openstreetmap.org or maps.google.com)"},
		{"map-segoffsetx", "Insert offset of left top tile at X axis (that is global x-index of map tile internally saved in a file named 0_0)"},
		{"map-segoffsety", "Insert offset of left top tile at Y axis (that is global y-index of map tile internally saved in a file named 0_0)"},
		{"map-lt-lat", "Coordinate of left top map corner - latitude"},
		{"map-lt-lon", "Coordinate of left top map corner - longitude"},
		{"map-rt-lat", "Coordinate of right top map corner - latitude"},
		{"map-rt-lon", "Coordinate of right top map corner - longitude"},
		{"map-lb-lat", "Coordinate of left bottom map corner - latitude"},
		{"map-lb-lon", "Coordinate of left bottom map corner - longitude"},
		{"map-lat", "Default map coordinate - latitude"},
		{"map-lon", "Default map coordinate - longitude"},
		{"map-data-dir", "Insert path to directory with map tiles"},
		{"map-data-format", "Map tile file name format - {0} is substitued by horizontal tile index, {1} is substitued by vertical index. Indices goes from zero."},
		{"map-data-included", "Should be map tiles (images) included into generated mobile JAR archive?"},
		{"map-no-params", "Parameters of map No."},
		{"processing-manifest", "Processing Manifest"},
		{"processing-entry", "Processing JAR entry"},
		{"writing-resource-entry", "Writing JAR entry from internal resources"},
		{"adding-segment-file-to-archive", "Adding tile into archive"},
		{"adding-segment-file-to-dir", "Creating file for moving to device memory card"},
		{"writing-output-start", "Creating midlet suite"},
		{"writing-output-finish", "Midlet suite successfully created"},
		{"copy-data-along", "ATTENTION!!! Copy created directory with map data (ending with suffix _DATA) into device memory card before mobile application deployment!"},
		{"usage-0", "Usage:"},
		{"usage-1", "java -jar GullsViewPacker.jar [-stdio] [-properties FILE] [-swing]"},
		{"usage-2", "where options means:"},
		{"usage-3", "-stdio - runs application in text mode (from text console)"},
		{"usage-4", "-properties FILE - runs application in batch mode - answers to all questions are read from FILE in Java Properties format"},
		{"usage-5", "-swing - runs application in graphic mode (default)"},
		{"overview-0", "This application will guide you in several steps through the process of new mobile mapping application generation."},
		{"overview-1", "First of all prepare yourself a map raster data in the form of square images - tiles, from which entire map will be assembled."},
		{"overview-2", "Tile file names choose with respect to format ROW_COL.suffix, where ROW is tile row index and COL is tile column index (eg. 10_12.png, 11_1.png, etc.)."},
		{"overview-3", "Left top tile's index is 0:0, file will be therefore named eg. 0_0.png"},
		{"overview-4", "It is also good to know how to transform map coordinates into real earth coordinates (latitude, longitude)"},
		{"overview-5", "This application allows transformation of coordinates in two ways - using Mercator projection or using bilinear transformation:"},
		{"overview-6", "1) Mercator - is geoid to cylinder projection with pyramidal tile division to 4 sub-tiles with higher resolution, is used eg. by project www.openstreetmap.org or maps.google.com."},
		{"overview-7", "If your tiles are in this projection, you need to know only these three values: X (column) and Y (row) coordinate of left-top tile and zoom level index (0 means entire Earth in single tile, 1 means whole Earth in 4 tiles, 2 - in 16 tiles, etc."},
		{"overview-8", "These three values are mostly contained in URL address used for downloading certain tile from tiling server."},
		{"overview-9", "2) Bilinear transformation - in this case coordinate is interpolated from inserted Earth coordinates (latitude, longitude) of three map's corners."},
		{"overview-10", "Also it is necessary to know parameters of mobile device to which is mapping application targeted."},
		{"overview-11", "Support of certain API extensions is important - eg. ability of Java application to access device file system, Bluetooth interface, etc."},
		{"overview-12", "If you are sure your device supports JSR-75-FileConnection, it is possible NOT to insert tiles into JAR archive of mobile application, but only copy them into memory card and let mobile application to read them using JSR-75 API."},
		{"overview-13", "Attention - certain mobile device vendors do not allow user to enable permanent access to memory card (file system) from Java application and annoy user with is-it-safe dialogs on every file access (on every single tile load)."},
		{"overview-14", "If you have prepared all map tiles and coordinate information you can start to answer to this wizard's questions."},
		{"overview-15", "After wizard's finish in selected directory will be created pair of files suffixed JAD and JAR that are ready to deploy to mobile device by standard ways."},
	};
	
	public Object[][] getContents(){
		return CONTENTS;
	}
}


