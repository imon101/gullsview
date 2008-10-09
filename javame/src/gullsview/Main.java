package gullsview;

import java.io.*;
import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.rms.*;



public class Main extends MIDlet implements CommandListener/*, Persistable*/ {
	private static final int ACTION_HIDE_SPLASH = 1;
	private static final int ACTION_SCROLL = 2;
	private static final int ACTION_HIDE_MESSAGE = 3;
	
	private static final int POINT_PATH_START = 1;
	private static final int POINT_PATH_CONT = 2;
	private static final int POINT_POI = 3;
	
	private static final int LOCATOR_NONE = 0;
	private static final int LOCATOR_JSR179 = 1;
	private static final int LOCATOR_JSR082 = 2;
	
	private boolean flagInit = false;
	private boolean flagJsr75FC; // FileConn
	private boolean flagJsr082; // BT
	private boolean flagJsr179; // LAPI
	private boolean flagJsr184; // M3G
	
	private Display display;
	private Hashtable resources;
	private Timer timer;
	private TimerTask scrollTask;
	private Map map;
	private String dataPath;
	private int overlayItemIndex;
	private boolean overlayItemInsert;
	private boolean inOverlayList;
	private double targetLatitude = Double.NaN;
	private double targetLongitude = Double.NaN;
	private int locatorType;
	private Locator locator;
	private FileSystem fileSystem;
	
	private SplashScreen splash;
	private Form aboutForm;
	private MapCanvas canvas;
	private OverlayList overlayList;
	private PointForm pathStartForm;
	private PointForm pathContForm;
	private PointForm poiForm;
	private MapList mapList;
	
	private Command okCommand;
	private Command exitCommand;
	private Command pauseCommand;
	private Command aboutCommand;
	private Command backCommand;
	private Command overlayListCommand;
	private Command pathStartCommand;
	private Command pathContCommand;
	private Command poiCommand;
	private Command editCommand;
	private Command removeCommand;
	private Command moveToCommand;
	private Command setTargetCommand;
	private Command cancelTargetCommand;
	private Command mapSelectCommand;
	private Command switchToMidpCanvasCommand;
	private Command switchToM3gCanvasCommand;
	
	public void startApp(){
		this.timer = new Timer();
		if(!this.flagInit){
			try {
				this.flagJsr75FC = this.classExists("javax.microedition.io.file.FileConnection") && this.classExists("gullsview.FileSystemImpl");
				this.flagJsr082 = this.classExists("javax.bluetooth.LocalDevice") && this.classExists("gullsview.Jsr082Locator");
				this.flagJsr179 = this.classExists("javax.microedition.location.Location") && this.classExists("gullsview.Jsr179Locator");
				this.flagJsr184 = this.classExists("javax.microedition.m3g.Graphics3D") && this.classExists("gullsview.M3gMapCanvas");
				
				this.display = Display.getDisplay(this);
				
				this.resources = new Hashtable();
				this.initResources();
				this.inOverlayList = false;
				
				this.splash = new SplashScreen();
				this.show(this.splash);
				
				this.okCommand = new Command(this.getResource("ok"), Command.OK, 1);
				this.exitCommand = new Command(this.getResource("exit"), Command.EXIT, 1);
				this.backCommand = new Command(this.getResource("back"), Command.BACK, 1);
				this.overlayListCommand = new Command(this.getResource("overlay"), Command.SCREEN, 1);
				this.pathStartCommand = new Command(this.getResource("path-start"), Command.SCREEN, 2);
				this.pathContCommand = new Command(this.getResource("path-cont"), Command.SCREEN, 3);
				this.poiCommand = new Command(this.getResource("poi"), Command.SCREEN, 4);
				this.cancelTargetCommand = new Command(this.getResource("cancel-target"), Command.SCREEN, 5);
				this.mapSelectCommand = new Command(this.getResource("select-map"), Command.SCREEN, 6);
				this.switchToMidpCanvasCommand = new Command(this.getResource("switch-to-midp-canvas"), Command.SCREEN, 7);
				this.switchToM3gCanvasCommand = new Command(this.getResource("switch-to-m3g-canvas"), Command.SCREEN, 8);
				this.aboutCommand = new Command(this.getResource("about"), Command.SCREEN, 9);
				this.pauseCommand = new Command(this.getResource("pause"), Command.SCREEN, 10);
				this.editCommand = new Command(this.getResource("edit"), Command.ITEM, 1);
				this.moveToCommand = new Command(this.getResource("move-to"), Command.ITEM, 2);
				this.setTargetCommand = new Command(this.getResource("set-target"), Command.ITEM, 3);
				this.removeCommand = new Command(this.getResource("remove"), Command.ITEM, 4);
				
				this.aboutForm = this.createAboutForm();
				this.aboutForm.addCommand(this.backCommand);
				this.aboutForm.setCommandListener(this);
				
				this.overlayList = new OverlayList(this, this.getResource("overlay"));
				this.overlayList.setSelectCommand(null);
				this.overlayList.addCommand(this.editCommand);
				this.overlayList.addCommand(this.pathStartCommand);
				this.overlayList.addCommand(this.pathContCommand);
				this.overlayList.addCommand(this.poiCommand);
				this.overlayList.addCommand(this.moveToCommand);
				this.overlayList.addCommand(this.setTargetCommand);
				this.overlayList.addCommand(this.removeCommand);
				this.overlayList.addCommand(this.backCommand);
				this.overlayList.setCommandListener(this);
				
				String coordFormat = "deg*min#sec";
				String latitudeTitle = this.getResource("latitude") + " (" + coordFormat + ")";
				String longitudeTitle = this.getResource("longitude") + " (" + coordFormat + ")";
				String nameTitle = this.getResource("name");
				
				this.pathStartForm = new PointForm(this, this.getResource("path-start"), latitudeTitle, longitudeTitle, nameTitle);
				this.pathStartForm.appendColorItems(this.getResource("color"), this.getResource("hue"), this.display.getColor(Display.COLOR_FOREGROUND));
				this.pathStartForm.addCommand(this.backCommand);
				this.pathStartForm.addCommand(this.okCommand);
				this.pathStartForm.setCommandListener(this);
				
				this.pathContForm = new PointForm(this, this.getResource("path-cont"), latitudeTitle, longitudeTitle, nameTitle);
				this.pathContForm.addCommand(this.backCommand);
				this.pathContForm.addCommand(this.okCommand);
				this.pathContForm.setCommandListener(this);
				
				this.poiForm = new PointForm(this, this.getResource("poi"), latitudeTitle, longitudeTitle, nameTitle);
				this.poiForm.addCommand(this.backCommand);
				this.poiForm.addCommand(this.okCommand);
				this.poiForm.setCommandListener(this);
				
				this.initCanvas(false);
				
				this.mapList = new MapList(this, this.getResource("select-map"));
				this.mapList.addCommand(this.backCommand);
				this.mapList.setCommandListener(this);
				
				if(this.flagJsr75FC) this.fileSystem = (FileSystem) this.newInstance("gullsview.FileSystemImpl");
				
				this.loadMaps();
				this.setMap(0);
				
				this.schedule(ACTION_HIDE_SPLASH, null, 2000, 0);
				this.flagInit = true;
				this.canvas.render();
				
this.locatorType = LOCATOR_NONE;
				this.initLocator();
				if(this.locator != null) this.locator.start();
			} catch(Exception e){
				this.error("Error in initialization", e);
			}
		} else {
			this.show(this.canvas);
		}
	}
	
	private void initCanvas(boolean m3g){
		if(this.canvas != null) this.canvas.dispose();
		this.canvas = null;
		this.show(null);
		if(!this.flagJsr184) m3g = false;
		String className = m3g ? "gullsview.M3gMapCanvas" : "gullsview.MidpMapCanvas";
		this.canvas = (MapCanvas) this.newInstance(className);
		this.canvas.init(this);
		this.canvas.addCommand(this.exitCommand);
		this.canvas.addCommand(this.overlayListCommand);
		this.canvas.addCommand(this.pathStartCommand);
		this.canvas.addCommand(this.pathContCommand);
		this.canvas.addCommand(this.poiCommand);
		this.canvas.addCommand(this.cancelTargetCommand);
		this.canvas.addCommand(this.aboutCommand);
		this.canvas.addCommand(this.pauseCommand);
		this.canvas.addCommand(this.mapSelectCommand);
		if(m3g){
			this.canvas.addCommand(this.switchToMidpCanvasCommand);
		} else {
			if(this.flagJsr184) this.canvas.addCommand(this.switchToM3gCanvasCommand);
		}
		this.canvas.setCommandListener(this);
	}
	
	public void pauseApp(){
		this.timer.cancel();
		this.timer = null;
	}
	
	public void destroyApp(boolean unconditional){
		this.timer.cancel();
		this.timer = null;
		this.flagInit = false;
	}
	
	public void commandAction(Command cmd, Displayable disp){
		if(cmd == this.exitCommand){
			this.notifyDestroyed();
		} else if(cmd == this.pauseCommand){
			this.show(null);
			this.notifyPaused();
		} else if(cmd == Alert.DISMISS_COMMAND){
			this.notifyDestroyed();
		} else if(cmd == this.aboutCommand){
			this.show(this.aboutForm);
		} else if(cmd == this.backCommand){
			if(disp instanceof PointForm){
				this.show(this.inOverlayList ? (Displayable) this.overlayList : (Displayable) this.canvas);
			} else {
				this.inOverlayList = false;
				this.show(this.canvas);
			}
		} else if(cmd == this.overlayListCommand){
			this.inOverlayList = true;
			this.show(this.overlayList);
		} else if((cmd == this.pathStartCommand) || (cmd == this.pathContCommand) || (cmd == this.poiCommand)){
			PointForm pf = null;
			if(cmd == this.pathStartCommand){
				pf = this.pathStartForm;
			} else if(cmd == this.pathContCommand){
				pf = this.pathContForm;
			} else if(cmd == this.poiCommand){
				pf = this.poiForm;
			}
			String[] coords = this.getPositionStr();
			pf.setLocation(coords[0], coords[1]);
			pf.setName("");
			if(cmd == this.pathStartCommand) pf.setHue(0);
			if(disp == this.canvas){
				this.overlayItemIndex = -1;
			} else if(disp == this.overlayList){
				this.overlayItemIndex = this.overlayList.getSelectedIndex() + 1;
			}
			this.overlayItemInsert = true;
			this.show(pf);
		} else if(cmd == this.okCommand){
			if(disp instanceof PointForm){
				PointForm pf = (PointForm) disp;
				String latStr = pf.getLocationLatitude();
				String lonStr = pf.getLocationLongitude();
				double lat, lon;
				try {
					lat = this.parseCoord(latStr);
					lon = this.parseCoord(lonStr);
				} catch (Exception e){
					this.alert(this.getResource("wrong-coord-format"));
					return;
				}
				String name = pf.getName();
				int hue = 0;
				int color = 0;
				int type = 0;
				if(disp == this.pathStartForm){
					hue = pf.getHue();
					color = pf.getColor();
					type = OverlayList.TYPE_PATH_START;
				} else if(disp == this.pathContForm){
					type = OverlayList.TYPE_PATH_CONT;
				} else if(disp == this.poiForm){
					type = OverlayList.TYPE_POI;
				}
				this.overlayList.saveItem(this.overlayItemIndex, this.overlayItemInsert, type, name, latStr, lonStr, lat, lon, hue, color);
				this.updateOverlay();
				this.show(this.inOverlayList ? (Displayable) this.overlayList : (Displayable) this.canvas);
			}
		} else if(cmd == this.editCommand){
			int index = this.overlayList.getSelectedIndex();
			if(index == -1){
				this.alert(this.getResource("select-item"));
				return;
			}
			this.overlayItemIndex = index;
			this.overlayItemInsert = false;
			PointForm pf = null;
			switch(this.overlayList.getItemType(index)){
			case OverlayList.TYPE_PATH_START: pf = this.pathStartForm; pf.setHue(this.overlayList.getItemHue(index)); break;
			case OverlayList.TYPE_PATH_CONT: pf = this.pathContForm; break;
			case OverlayList.TYPE_POI: pf = this.poiForm; break;
			}
			pf.setName(this.overlayList.getItemName(index));
			pf.setLocation(this.overlayList.getItemLatitudeStr(index), this.overlayList.getItemLongitudeStr(index));
			this.show(pf);
		} else if(cmd == this.removeCommand){
			int index = this.overlayList.getSelectedIndex();
			if(index == -1){
				this.alert(this.getResource("select-item"));
				return;
			}
			this.overlayList.removeItem(index);
			this.updateOverlay();
		} else if(cmd == this.moveToCommand){
			int index = this.overlayList.getSelectedIndex();
			if(index == -1){
				this.alert(this.getResource("select-item"));
				return;
			}
			this.setPosition(this.overlayList.getItemLatitude(index), this.overlayList.getItemLongitude(index));
			this.inOverlayList = false;
			this.show(this.canvas);
		} else if(cmd == this.setTargetCommand){
			int index = this.overlayList.getSelectedIndex();
			if(index == -1){
				this.alert(this.getResource("select-item"));
				return;
			}
			this.targetLatitude = this.overlayList.getItemLatitude(index);
			this.targetLongitude = this.overlayList.getItemLongitude(index);
			this.updateTarget();
			this.inOverlayList = false;
			this.show(this.canvas);
		} else if(cmd == this.cancelTargetCommand){
			this.targetLatitude = this.targetLongitude = Double.NaN;
			this.updateTarget();
			this.canvas.repaint();
		} else if(cmd == this.mapSelectCommand){
			this.show(this.mapList);
		} else if(cmd == List.SELECT_COMMAND){
			if(disp == this.mapList){
				this.setMap(this.mapList.getSelectedMap());
				this.inOverlayList = false;
				this.show(this.canvas);
			}
		} else if(cmd == this.switchToMidpCanvasCommand){
			this.initCanvas(false);
			this.setMap(this.mapList.getSelectedMap());
			this.show(this.canvas);
		} else if(cmd == this.switchToM3gCanvasCommand){
			this.initCanvas(true);
			this.setMap(this.mapList.getSelectedMap());
			this.show(this.canvas);
		}
	}
	
	private boolean classExists(String name){
		try {
			return Class.forName(name) != null;
		} catch (Exception e){
			return false;
		}
	}
	
	private Object newInstance(String className){
		try {
			return (Class.forName(className)).newInstance();
		} catch (Exception e){
			this.warning("Cannot create instance of " + className, e);
			throw new RuntimeException("Cannot create instance of " + className + ": " + e.toString());
		}
	}
	
	private void alert(String message){
		Alert alert = new Alert(this.getResource("alert"), message, null, AlertType.ERROR);
		this.show(alert);
	}
	
	private void error(String message, Exception e){
		String text = "ERROR: " + message;
		if(e != null) text += " " + e.toString();
		System.err.println(text);
		e.printStackTrace();
		StringBuffer sb = new StringBuffer();
		sb.append(this.getResource("error-text"));
		sb.append(":\n");
		sb.append(message);
		sb.append(" - ");
		sb.append(e.toString());
		Alert alert = new Alert(this.getResource("error"), sb.toString(), null, AlertType.ERROR);
		int time = 10000;
		alert.setTimeout(time);
		alert.setIndicator(new Gauge(null, false, time, 0));
		alert.setCommandListener(this);
		this.timer.cancel();
		this.show(alert);
		try {
			Thread.sleep(time);
		} catch (Exception e2){}
		this.notifyDestroyed();
	}
	
	private void warning(String message, Exception e){
		String text = "WARNING: " + message;
		if(e != null) text += " " + e.toString();
		System.err.println(text);
	}
	
	private void info(String message){
		System.out.println("INFO: " + message);
	}
	
	private void show(Displayable disp){
		this.display.setCurrent(disp);
	}
	
	public String getResource(String name){
		String text = (String) this.resources.get(name);
		return (text != null) ? text : "!" + name;
	}
	
	private TimerTask schedule(final int action, final Object data, long delay, long period){
		TimerTask task = new TimerTask(){
			public void run(){
				Main.this.event(action, data, this);
			}
		};
		if(period > 0){
			this.timer.schedule(task, delay, period);
		} else {
			this.timer.schedule(task, delay);
		}
		return task;
	}
	
	private void event(int action, Object data, TimerTask task){
		switch(action){
		case ACTION_HIDE_SPLASH:
			this.show(this.canvas);
			this.splash = null;
			break;
		case ACTION_SCROLL:
			if((this.scrollTask != null) && (this.scrollTask != task)) task.cancel();
			if(!this.canvas.scroll()){
				task.cancel();
				this.scrollTask = null;
			}
			this.canvas.render();
			break;
		case ACTION_HIDE_MESSAGE:
			String message = (String) data;
			if(message == null) return;
			if(message.equals(this.canvas.getMessage())) this.canvas.setMessage(null);
			break;
		}
	}
	
	private void setResource(String name, String text){
		this.resources.put(name, text);
	}
	
	private void initResources(){
		String locale = System.getProperty("microedition.locale");
		if(locale == null) locale = "en";
		int dot = locale.indexOf('.');
		if(dot >= 0) locale = locale.substring(0, dot);
		int separator = locale.indexOf('_');
		if(separator < 0) separator = locale.indexOf('-');
		if(separator >= 0) locale = locale.substring(0, separator);
		this.info("Locale: " + locale);
		if("cs".equals(locale)){
			this.setResource("exit", "Ukončit");
			this.setResource("pause", "Pozastavit");
			this.setResource("error", "Chyba");
			this.setResource("error-text", "V aplikaci došlo k následující chybě a bude proto ukončena");
			this.setResource("about", "O aplikaci");
			this.setResource("back", "Zpět");
			this.setResource("author", "Autor");
			this.setResource("license", "Licence");
			this.setResource("license-text", "Tato aplikace je šířena zdarma včetně zdrojových kódů za podmínek licence");
			this.setResource("notice", "Upozornění");
			this.setResource("notice-text", "Tato aplikace je autorem poskytována výhradně bez datového obsahu (bez rastrových mapových podkladů) nebo s datovým obsahem šířeným pod licencí kompatibilní s GPLv3.");
			this.setResource("controls", "Ovládání");
			this.setResource("controls-text", "křížek [#] - přepínání pohledu na výšku / na šířku\nhvězdička [*] - přepínání zobrazení přes celý displej\nhlavní tlačítko [FIRE] - umístění bodu pokračování trasy\nsměrové šipky a číselná tlačítka - ovládání pohybu mapy");
			this.setResource("name", "Název");
			this.setResource("latitude", "Souřadnice - šířka");
			this.setResource("longitude", "Souřadnice - délka");
			this.setResource("color", "Barva");
			this.setResource("hue", "Nastavení odstínu");
			this.setResource("path-start", "Začátek trasy");
			this.setResource("path-cont", "Pokračování trasy");
			this.setResource("poi", "Zajímavý bod");
			this.setResource("overlay", "Trasy a místa");
			this.setResource("ok", "OK");
			this.setResource("wrong-coord-format", "Špatný formát souřadnic");
			this.setResource("alert", "Upozornění");
			this.setResource("edit", "Editovat");
			this.setResource("remove", "Odstranit");
			this.setResource("select-item", "Nejdříve vyberte položku ze seznamu");
			this.setResource("move-to", "Přemístit sem");
			this.setResource("set-target", "Nastavit cíl");
			this.setResource("cancel-target", "Zrušit cíl");
			this.setResource("available", "Dostupné");
			this.setResource("temporarily-unavailable", "Dočasně nedostupné");
			this.setResource("out-of-service", "Nefunkční");
			this.setResource("select-map", "Výběr mapy");
			this.setResource("switch-to-midp-canvas", "Klasické zobrazení");
			this.setResource("switch-to-m3g-canvas", "3D zobrazení");
		} else {
			this.setResource("exit", "Exit");
			this.setResource("pause", "Pause");
			this.setResource("error", "Error");
			this.setResource("error-text", "Following error occured whence application is going to exit");
			this.setResource("about", "About");
			this.setResource("back", "Back");
			this.setResource("author", "Author");
			this.setResource("license", "License");
			this.setResource("license-text", "This application is a free software shared under terms and conditions of");
			this.setResource("notice", "Notice");
			this.setResource("notice-text", "This application is provided without any data content (raster maps) or with data content shared under license compatible to GPLv3.");
			this.setResource("controls", "Controls");
			this.setResource("controls-text", "pound [#] - switch landscape / portrait view\nasterisk [*] - switch fullscreen mode\nfire button - places path continuation point\narrow and numeric keys - controls map scroll");
			this.setResource("name", "Name");
			this.setResource("latitude", "Latitude");
			this.setResource("longitude", "Longitude");
			this.setResource("color", "Color");
			this.setResource("hue", "Set color hue");
			this.setResource("path-start", "Path start");
			this.setResource("path-cont", "Path continuation");
			this.setResource("poi", "Point of interest");
			this.setResource("overlay", "Paths and places");
			this.setResource("ok", "OK");
			this.setResource("wrong-coord-format", "Wrong coordinate format");
			this.setResource("alert", "Attention");
			this.setResource("edit", "Edit");
			this.setResource("remove", "Remove");
			this.setResource("select-item", "First select an item");
			this.setResource("move-to", "Go to");
			this.setResource("set-target", "Set target");
			this.setResource("cancel-target", "Cancel target");
			this.setResource("available", "Available");
			this.setResource("temporarily-unavailable", "Temporarily unavailable");
			this.setResource("out-of-service", "Out of service");
			this.setResource("select-map", "Map selection");
			this.setResource("switch-to-midp-canvas", "Classic view");
			this.setResource("switch-to-m3g-canvas", "3D view");
		}
		// this.setResource("", "");
	}
	
	public Image getResImage(String path){
		this.info("Loading image " + path);
		return getImage((this.getClass()).getResourceAsStream(path));
	}
	
	private Image getImage(InputStream is){
		if(is == null) return null;
		Image image = null;
		try {
			image = Image.createImage(is);
			is.close();
		} catch (Exception e){
			this.error(null, e);
		}
		return image;
	}
	
	public Image fileSystemGetImage(String path){
		if(this.fileSystem == null) return null;
		InputStream is = null;
		try {
			is = this.fileSystem.openInputStream(path);
			return this.getImage(is);
		} catch (Exception e){
			this.warning("Cannot open file", e);
			return null;
		} finally {
			try {
				this.fileSystem.closeInputStream(is);
			} catch (Exception e){
				this.warning("Error closing file", e);
			}
		}
	}
	
	public void loadMaps(){
		try {
			String path = "maps";
			this.resourceLoad(this.mapList, "/data/" + path);
			if(this.fileSystem != null){
				try {
					this.fileSystem.load(this.mapList, path);
				} catch (Exception e){
					this.warning("Unable to load filesystem maps", e);
				}
			}
			this.info("Loaded " + this.mapList.getMapCount() + " maps");
			this.mapList.update();
		} catch (Exception e){
			this.error("Cannot load maps", e);
		}
	}
	
	private void setMap(int mapid){
		Map orgmap = this.map;
		this.map = this.mapList.getMap(mapid);
		this.canvas.setSegment(this.map.segment, this.map.xcount, this.map.ycount);
		this.canvas.setMap(mapid);
		double rx, ry;
		if(orgmap == null){
			rx = map.defaultx;
			ry = map.defaulty;
		} else {
			double[] rcoords = new double[2];
			this.mapToReal(orgmap, this.canvas.getPositionX(), this.canvas.getPositionY(), rcoords);
			rx = rcoords[0];
			ry = rcoords[1];
			if(!this.insideMap(this.map, rx, ry)){
				rx = map.defaultx;
				ry = map.defaulty;
			}
		}
		this.setPosition(rx, ry);
	}
	
	private void setPosition(double latitude, double longitude){
		this.info("Position: " + latitude + ", " + longitude);
		int[] mcoords = new int[2];
		this.realToMap(this.map, latitude, longitude, mcoords);
		this.canvas.setPosition(mcoords[0], mcoords[1]);
	}
	
	public Image getSegment(int mapid, int x, int y){
		Map map = this.mapList.getMap(mapid);
		String name = map.name + "/" + x + "_" + y;
		Image ret = this.getResImage("/data/" + name);
		if(ret != null) return ret;
		if(this.fileSystem != null){
			try {
				return this.fileSystemGetImage(name);
			} catch (Exception e){
				this.warning("Error loading image from filesystem", e);
			}
		}
		return null;
	}
	
	private Form createAboutForm(){
		Form form = new Form(this.getResource("about"));
		// form.append(new ImageItem("", this.getResImage("/stripe.png"), ImageItem.LAYOUT_CENTER, ""));
		form.append(new StringItem(this.getResource("notice"), this.getResource("notice-text")));
		form.append(new StringItem(this.getResource("author"), "Copyleft 2008 - Tomáš Darmovzal"));
		form.append(new StringItem("E-mail", "tomas.darmovzal@gmail.com", Item.HYPERLINK));
		form.append(new StringItem("URL", "http://darmovzal.nuabi.com", Item.HYPERLINK));
		form.append(new StringItem(this.getResource("license"), this.getResource("license-text") + " GPLv3"));
		form.append(new StringItem(this.getResource("controls"), this.getResource("controls-text")));
		return form;
	}
	
	public void startScroll(){
		this.scrollTask = this.schedule(this.ACTION_SCROLL, null, 10, 40);
	}
	
	private boolean insideMap(Map map, double x, double y){
		int[] coords = new int[2];
		this.realToMap(map, x, y, coords);
		int mx = coords[0];
		int my = coords[1];
		return (mx >= 0) && (mx < (map.xcount * map.segment)) && (my >= 0) && (my < (map.ycount * map.segment));
	}
	
	private static final double[] dout = new double[2];
	
	public synchronized void realToMap(Map map, double x, double y, int[] out){
		this.translate(x, y, dout,
			map.realax, map.realay, map.realbx, map.realby, map.realcx, map.realcy,
			map.locax, map.locay, map.locbx, map.locby, map.loccx, map.loccy
		);
		out[0] = (int) dout[0];
		out[1] = (int) dout[1];
	}
	
	public void mapToReal(Map map, int x, int y, double[] out){
		this.translate(x, y, out,
			map.locax, map.locay, map.locbx, map.locby, map.loccx, map.loccy,
			map.realax, map.realay, map.realbx, map.realby, map.realcx, map.realcy
		);
	}
	
	private void translate(
		double inx, double iny, double[] out,
		double inax, double inay, double inbx, double inby, double incx, double incy,
		double outax, double outay, double outbx, double outby, double outcx, double outcy
	){
		double adx = inx - inax;
		double ady = iny - inay;
		double abx = inbx - inax;
		double aby = inby - inay;
		double acx = incx - inax;
		double acy = incy - inay;
		double t = ((abx * ady) - (aby * adx)) / ((acy * abx) - (acx * aby));
		double s = ((acx * ady) - (acy * adx)) / ((aby * acx) - (abx * acy));
		double x = outax + ((outbx - outax) * s) + ((outcx - outax) * t);
		double y = outay + ((outby - outay) * s) + ((outcy - outay) * t);
		out[0] = x;
		out[1] = y;
	}
	
	private String[] getPositionStr(){
		double[] rcoords = new double[2];
		this.mapToReal(this.map, this.canvas.getPositionX(), this.canvas.getPositionY(), rcoords);
		String[] coords = new String[2];
		coords[0] = this.formatCoord(rcoords[0]);
		coords[1] = this.formatCoord(rcoords[1]);
		return coords;
	}
	
	private String formatCoord(double coord){
		int sgn = coord < 0 ? -1 : 1;
		coord = Math.abs(coord);
		int deg = (int) Math.floor(coord);
		double mrest = (coord - deg) * 60;
		int min = (int) Math.floor(mrest);
		double srest = (mrest - min) * 60;
		int sec = (int) Math.floor(srest);
		int msec = (int) Math.floor((srest - sec) * 1000);
		String smsec = String.valueOf(msec);
		while(smsec.length() < 3) smsec = "0" + smsec;
		return (sgn * deg) + "*" + min + "#" + sec + "." + smsec;
	}
	
	private double parseCoord(String str){
		String sdeg = null;
		String smin = null;
		String ssec = null;
		int asterisk = str.indexOf('*');
		if(asterisk < 0){
			sdeg = str;
		} else {
			sdeg = str.substring(0, asterisk);
			String srest = str.substring(asterisk + 1);
			int pound = srest.indexOf('#');
			if(pound < 0){
				smin = srest;
			} else {
				smin = srest.substring(0, pound);
				ssec = srest.substring(pound + 1);
			}
		}
		double deg = Double.parseDouble(sdeg);
		double min = Double.parseDouble(smin);
		double sec = Double.parseDouble(ssec);
		return deg + (min / 60) + (sec / 3600);
	}
	
	private void updateOverlay(){
		Vector lineCoordsVector = new Vector();
		Vector lineColorsVector = new Vector();
		Vector pointCoordsVector = new Vector();
		Vector pointLabelsVector = new Vector();
		double lat, lon;
		String name;
		int color = 0;
		int prevx = 0;
		int prevy = 0;
		boolean started = false;
		int[] coords = new int[2];
		int x, y;
		int count = this.overlayList.getItemCount();
		for(int i = 0; i < count; i++){
			lat = this.overlayList.getItemLatitude(i);
			lon = this.overlayList.getItemLongitude(i);
			this.realToMap(this.map, lat, lon, coords);
			x = coords[0];
			y = coords[1];
			name = this.overlayList.getItemName(i);
			if((name != null) && ((name.trim()).length() == 0)) name = null;
			switch(this.overlayList.getItemType(i)){
			case OverlayList.TYPE_PATH_START:
				color = this.overlayList.getItemColor(i);
				started = true;
				prevx = x;
				prevy = y;
				break;
			case OverlayList.TYPE_PATH_CONT:
				if(started){
					lineCoordsVector.addElement(new Integer(prevx));
					lineCoordsVector.addElement(new Integer(prevy));
					lineCoordsVector.addElement(new Integer(x));
					lineCoordsVector.addElement(new Integer(y));
					lineColorsVector.addElement(new Integer(color));
				}
				prevx = x;
				prevy = y;
				break;
			}
			pointCoordsVector.addElement(new Integer(x));
			pointCoordsVector.addElement(new Integer(y));
			pointLabelsVector.addElement(name);
		}
		
		Enumeration en;
		int j;
		int[] lineCoords = new int[lineCoordsVector.size()];
		en = lineCoordsVector.elements();
		j = 0;
		while(en.hasMoreElements()) lineCoords[j++] = ((Integer) en.nextElement()).intValue();
		int[] lineColors = new int[lineColorsVector.size()];
		en = lineColorsVector.elements();
		j = 0;
		while(en.hasMoreElements()) lineColors[j++] = ((Integer) en.nextElement()).intValue();
		int[] pointCoords = new int[pointCoordsVector.size()];
		en = pointCoordsVector.elements();
		j = 0;
		while(en.hasMoreElements()) pointCoords[j++] = ((Integer) en.nextElement()).intValue();
		String[] pointLabels = new String[pointLabelsVector.size()];
		en = pointLabelsVector.elements();
		j = 0;
		while(en.hasMoreElements()) pointLabels[j++] = (String) en.nextElement();
		
		this.canvas.setOverlay(lineCoords, lineColors, pointCoords, pointLabels);
	}
	
	private void updateTarget(){
		if(Double.isNaN(this.targetLatitude) || Double.isNaN(this.targetLongitude)){
			this.canvas.cancelTarget();
		} else {
			int[] coords = new int[2];
			this.realToMap(this.map, this.targetLatitude, this.targetLongitude, coords);
			this.canvas.setTarget(coords[0], coords[1]);
		}
	}
	
	public void addTracePoint(){
		double[] latlon = new double[2];
		this.mapToReal(this.map, this.canvas.getPositionX(), this.canvas.getPositionY(), latlon);
		this.overlayList.saveItem(-1, true, OverlayList.TYPE_PATH_CONT, "", this.formatCoord(latlon[0]), this.formatCoord(latlon[1]), latlon[0], latlon[1], 0, 0);
		this.updateOverlay();
	}
	
	private boolean recordStoreLoad(Persistable p, String name) throws Exception {
		try {
			RecordStore rs = RecordStore.openRecordStore(name, false);
			if(rs.getNumRecords() < 1) return false;
			byte[] data = rs.getRecord(1);
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
			p.load(dis);
			dis.close();
			return true;
		} catch (RecordStoreNotFoundException e){
			return false;
		} catch (Exception e){
			this.warning("Error while loading persistable from RS", e);
			throw new Exception("Error while loading persistable from RecordStore: " + e.toString());
		}
	}
	
	private void recordStoreSave(Persistable p, String name) throws Exception {
		try {
			RecordStore rs = RecordStore.openRecordStore(name, true);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			p.save(dos);
			dos.flush();
			dos.close();
			byte[] data = baos.toByteArray();
			if(rs.getNumRecords() > 0){
				rs.setRecord(1, data, 0, data.length);
			} else {
				rs.addRecord(data, 0, data.length);
			}
		} catch (Exception e){
			this.warning("Error while saving persistable into RS", e);
			throw new Exception("Error while saving persistable into RecordStore: " + e.toString());
		}
	}
	
	private boolean resourceLoad(Persistable p, String resource) throws Exception {
		InputStream is = (this.getClass()).getResourceAsStream(resource);
		if(is == null) return false;
		DataInputStream dis = new DataInputStream(is);
		p.load(dis);
		dis.close();
		return true;
	}
	
	private void initLocator() throws Exception {
		String className = null;
		switch(this.locatorType){
		case LOCATOR_JSR179:
			if(this.flagJsr179) className = "gullsview.Jsr179Locator";
			break;
		case LOCATOR_JSR082:
			if(this.flagJsr082) className = "gullsview.Jsr082Locator";
			break;
		}
		if(className == null) return;
		try {
			this.locator = (Locator) this.newInstance(className);
			this.locator.setOwner(this);
			try {
				this.locator.init();
			} catch (Exception e){
				this.warning("Error initializing Locator", e);
				this.locatorStatusUpdated("out-of-service");
				return;
			}
		} catch (Exception e){
			this.warning("Error initializing Locator " + className, e);
			throw new Exception("Error initializing locator: " + e.toString());
		}
	}
	
	private void setMessage(String message, int timeout){
		this.canvas.setMessage(message);
		this.schedule(ACTION_HIDE_MESSAGE, message, timeout, -1);
	}
	
	public void locatorPositionUpdated(double lat, double lon){
		this.setPosition(lat, lon);
	}
	
	public void locatorStatusUpdated(String status){
		this.setMessage("GPS: " + this.getResource(status), 3000);
	}
}


