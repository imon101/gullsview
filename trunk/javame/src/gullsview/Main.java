package gullsview;

import java.io.*;
import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.rms.*;



public class Main extends MIDlet implements CommandListener, ItemCommandListener, Persistable {
	private static final int ACTION_SHOW_CANVAS = 1;
	private static final int ACTION_SHOW_PREFERENCES = 2;
	private static final int ACTION_SCROLL = 3;
	private static final int ACTION_HIDE_MESSAGE = 4;
	private static final int ACTION_SWITCH_LOCATOR_STATE = 5;
	private static final int ACTION_BACKLIGHT = 6;
	private static final int ACTION_REPORT_POSITION = 7;
	
	private static final int POINT_PATH_START = 1;
	private static final int POINT_PATH_CONT = 2;
	private static final int POINT_POI = 3;
	
	private static final int CANVAS_MIDP = 1;
	private static final int CANVAS_M3G = 2;
	
	public static final int LOCATOR_NONE = 0;
	public static final int LOCATOR_JSR179 = 1;
	public static final int LOCATOR_JSR082 = 2;
	public static final int LOCATOR_BTS = 3;
	
	public boolean flagInit = false;
	public boolean flagJsr75FC; // FileConn
	public boolean flagJsr082; // BT
	public boolean flagJsr179; // LAPI
	public boolean flagJsr184; // M3G
	public boolean flagBtsLocator;
	public boolean flagNokiaBacklight;
	
	private Display display;
	private Hashtable resources;
	private Timer timer;
	private TimerTask scrollTask;
	private Map map;
	private int overlayItemIndex;
	private boolean overlayItemInsert;
	private boolean inOverlayList;
	private double targetLatitude = Double.NaN;
	private double targetLongitude = Double.NaN;
	private int locatorType = LOCATOR_NONE;
	private Locator locator;
	private FileSystem fileSystem;
	private int canvasType = CANVAS_MIDP;
	private String fileSystemParam;
	private String locatorParam;
	private boolean locatorStarted = false;
	private String loadedMapName;
	private double loadedLatitude;
	private double loadedLongitude;
	private boolean loadedLandscape;
	private boolean loadedFullscreen;
	private boolean backlight;
	private Twitter twitter;
	private String twitterUser;
	private String twitterPass;
	
	private SplashScreen splash;
	private Form aboutForm;
	private MapCanvas canvas;
	private OverlayList overlayList;
	private PointForm pathStartForm;
	private PointForm pathContForm;
	private PointForm poiForm;
	private MapList mapList;
	private PreferenceForm preferenceForm;
	private TextBox twitterTextBox;
	
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
	private Command startLocatorCommand;
	private Command stopLocatorCommand;
	private Command preferenceCommand;
	private Command startBacklightCommand;
	private Command stopBacklightCommand;
	private Command reportPositionCommand;
	
	public void startApp(){
		this.timer = new Timer();
		if(!this.flagInit){
			try {
				this.flagJsr75FC = this.classExists("javax.microedition.io.file.FileConnection") && this.classExists("gullsview.FileSystemImpl");
				this.flagJsr082 = this.classExists("javax.bluetooth.LocalDevice") && this.classExists("gullsview.Jsr082Locator");
				this.flagJsr179 = this.classExists("javax.microedition.location.Location") && this.classExists("gullsview.Jsr179Locator");
				this.flagJsr184 = this.classExists("javax.microedition.m3g.Graphics3D") && this.classExists("gullsview.M3gMapCanvas");
				this.flagBtsLocator = this.classExists("gullsview.BtsLocator");
				this.flagNokiaBacklight = this.classExists("com.nokia.mid.ui.DirectGraphics");
				
				boolean loaded = false;
				try {
					this.info("Loading preferences");
					loaded = this.recordStoreLoad(this, "preferences");
					this.info("Preferences loaded");
				} catch (Exception e){
					this.warning("Cannot load preferences", e);
				}
				
				this.display = Display.getDisplay(this);
				
				this.resources = new Hashtable();
				this.initResources();
				this.inOverlayList = false;
				
				this.splash = new SplashScreen();
				this.show(this.splash);
				
				this.okCommand = new Command(this.getResource("ok"), Command.OK, 1);
				this.exitCommand = new Command(this.getResource("exit"), Command.EXIT, 1);
				this.backCommand = new Command(this.getResource("back"), Command.BACK, 1);
				
				this.mapSelectCommand = new Command(this.getResource("select-map"), Command.SCREEN, 1);
				this.startBacklightCommand = new Command(this.getResource("start-backlight"), Command.SCREEN, 2);
				this.stopBacklightCommand = new Command(this.getResource("stop-backlight"), Command.SCREEN, 2);
				this.startLocatorCommand = new Command(this.getResource("start-locator"), Command.SCREEN, 3);
				this.stopLocatorCommand = new Command(this.getResource("stop-locator"), Command.SCREEN, 3);
				this.overlayListCommand = new Command(this.getResource("overlay"), Command.SCREEN, 4);
				this.pathStartCommand = new Command(this.getResource("path-start"), Command.SCREEN, 5);
				this.pathContCommand = new Command(this.getResource("path-cont"), Command.SCREEN, 6);
				this.poiCommand = new Command(this.getResource("poi"), Command.SCREEN, 7);
				this.cancelTargetCommand = new Command(this.getResource("cancel-target"), Command.SCREEN, 8);
				this.switchToMidpCanvasCommand = new Command(this.getResource("switch-to-midp-canvas"), Command.SCREEN, 9);
				this.switchToM3gCanvasCommand = new Command(this.getResource("switch-to-m3g-canvas"), Command.SCREEN, 10);
				this.preferenceCommand = new Command(this.getResource("preferences"), Command.SCREEN, 11);
				this.reportPositionCommand = new Command(this.getResource("report-position"), Command.SCREEN, 12);
				this.aboutCommand = new Command(this.getResource("about"), Command.SCREEN, 13);
				this.pauseCommand = new Command(this.getResource("pause"), Command.SCREEN, 14);
				
				this.editCommand = new Command(this.getResource("edit"), Command.ITEM, 1);
				this.moveToCommand = new Command(this.getResource("move-to"), Command.ITEM, 2);
				this.setTargetCommand = new Command(this.getResource("set-target"), Command.ITEM, 3);
				this.removeCommand = new Command(this.getResource("remove"), Command.ITEM, 4);
				
				this.preferenceForm = new PreferenceForm(this);
				if(this.flagJsr082 || this.flagJsr179){
					this.preferenceForm.appendLocatorTypeChoice(this.flagJsr082, this.flagJsr179, this.flagBtsLocator, this.locatorType);
					if(this.flagJsr082) this.preferenceForm.appendLocatorParam(this.locatorParam);
				}
				if(this.flagJsr75FC) this.preferenceForm.appendFileSystemParam(this.fileSystemParam);
				this.preferenceForm.appendTwitterCredentials(this.twitterUser, this.twitterPass);
				this.preferenceForm.addCommand(this.okCommand);
				this.preferenceForm.setCommandListener(this);
				
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
				
				this.initCanvas(this.canvasType);
				
				this.mapList = new MapList(this, this.getResource("select-map"));
				this.mapList.addCommand(this.backCommand);
				this.mapList.setCommandListener(this);
				
				this.twitterTextBox = new TextBox(this.getResource("twitter-text"), "", 25, TextField.ANY);
				this.twitterTextBox.addCommand(this.okCommand);
				this.twitterTextBox.setCommandListener(this);
				
				this.twitter = new Twitter();
				if(loaded) this.twitter.setCredentials(this.twitterUser, this.twitterPass);
				
				if(this.flagJsr75FC){
					this.fileSystem = (FileSystem) this.newInstance("gullsview.FileSystemImpl");
					this.fileSystem.init(this);
					this.fileSystem.setParameter(this.fileSystemParam);
				}
				
				this.loadMaps();
				
				this.mapList.setSelectedMap(this.loadedMapName);
				this.setMap(this.mapList.getSelectedMap());
				
				boolean overlayLoaded = false;
				try {
					this.info("Loading overlay");
					overlayLoaded = this.recordStoreLoad(this.overlayList, "overlay");
					this.info("Loaded " + this.overlayList.getItemCount() + " overlay items");
				} catch (Exception e){
					this.warning("Cannot load overlay", e);
				}
				this.updateOverlay();
				
				this.updateTarget();
				
				this.schedule((loaded || this.preferenceForm.isEmpty()) ? ACTION_SHOW_CANVAS : ACTION_SHOW_PREFERENCES, null, 2000);
				
				this.flagInit = true;
				
				this.canvas.setLandscape(this.loadedLandscape);
				this.canvas.setFullscreen(this.loadedFullscreen);
				this.setPosition(this.loadedLatitude, this.loadedLongitude);
				this.canvas.render();
				
				this.initLocator();
				if(this.locator != null){
					this.locator.setParameter(this.locatorParam);
					if(this.locatorStarted) this.locator.start();
				}
				this.updateLocatorCommands();
			} catch(Exception e){
				this.error("Error in initialization", e);
			}
		} else {
			this.show(this.canvas);
		}
		if(this.backlight) this.startBacklight();
	}
	
	private void initCanvas(int type){
		if(this.canvas != null) this.canvas.dispose();
		this.canvas = null;
		// this.show(null);
		if(!this.flagJsr184) type = CANVAS_MIDP;
		String className = (type == CANVAS_M3G) ? "gullsview.M3gMapCanvas" : "gullsview.MidpMapCanvas";
		this.canvas = (MapCanvas) this.newInstance(className);
		this.canvas.init(this);
		this.canvas.addCommand(this.exitCommand);
		this.canvas.addCommand(this.overlayListCommand);
		/*
		this.canvas.addCommand(this.pathStartCommand);
		this.canvas.addCommand(this.pathContCommand);
		this.canvas.addCommand(this.poiCommand);
		*/
		this.canvas.addCommand(this.cancelTargetCommand);
		this.canvas.addCommand(this.aboutCommand);
		this.canvas.addCommand(this.pauseCommand);
		this.canvas.addCommand(this.mapSelectCommand);
		if(type == CANVAS_M3G){
			this.canvas.addCommand(this.switchToMidpCanvasCommand);
		} else {
			if(this.flagJsr184) this.canvas.addCommand(this.switchToM3gCanvasCommand);
		}
		if(!this.preferenceForm.isEmpty()) this.canvas.addCommand(this.preferenceCommand);
		if(this.flagNokiaBacklight) this.updateBacklightCommands();
		this.canvas.addCommand(this.reportPositionCommand);
		this.canvas.setCommandListener(this);
		this.canvasType = type;
	}
	
	public void pauseApp(){
		this.timer.cancel();
		this.timer = null;
	}
	
	public void destroyApp(boolean unconditional){
		try {
			this.info("Saving preferences");
			this.recordStoreSave(this, "preferences");
		} catch (Exception e){
			this.warning("Cannot save preferences", e);
		}
		try {
			this.info("Saving overlay (" + this.overlayList.getItemCount() + " items)");
			this.recordStoreSave(this.overlayList, "overlay");
		} catch (Exception e){
			this.warning("Cannot save overlay", e);
		}
		this.timer.cancel();
		this.timer = null;
		this.flagInit = false;
	}
	
	public void save(DataOutput out) throws IOException {
		out.writeInt(this.canvasType);
		out.writeDouble(this.targetLatitude);
		out.writeDouble(this.targetLongitude);
		out.writeUTF(this.fileSystemParam != null ? this.fileSystemParam : "");
		out.writeInt(this.locatorType);
		out.writeUTF(this.locatorParam != null ? this.locatorParam : "");
		out.writeBoolean(this.locatorStarted);
		out.writeUTF(this.map.name);
		double[] latlon = new double[2];
		this.getPosition(latlon);
		out.writeDouble(latlon[0]);
		out.writeDouble(latlon[1]);
		out.writeBoolean(this.canvas.isLandscape());
		out.writeBoolean(this.canvas.isFullscreen());
		out.writeUTF(this.twitterUser != null ? this.twitterUser : "");
		out.writeUTF(this.twitterPass != null ? this.twitterPass : "");
	}
	
	public void load(DataInput in) throws IOException {
		this.canvasType = in.readInt();
		if((this.canvasType == CANVAS_M3G) && !this.flagJsr184) this.canvasType = CANVAS_MIDP;
		this.targetLatitude = in.readDouble();
		this.targetLongitude = in.readDouble();
		this.fileSystemParam = in.readUTF();
		this.locatorType = in.readInt();
		if((this.locatorType == LOCATOR_JSR179) && !this.flagJsr179) this.locatorType = LOCATOR_NONE;
		if((this.locatorType == LOCATOR_JSR082) && !this.flagJsr082) this.locatorType = LOCATOR_NONE;
		this.locatorParam = in.readUTF();
		this.locatorStarted = in.readBoolean();
		this.loadedMapName = in.readUTF();
		this.loadedLatitude = in.readDouble();
		this.loadedLongitude = in.readDouble();
		this.loadedLandscape = in.readBoolean();
		this.loadedFullscreen = in.readBoolean();
		this.twitterUser = in.readUTF();
		this.twitterPass = in.readUTF();
	}
	
	public void commandAction(Command cmd, Displayable disp){
		if(cmd == this.exitCommand){
			this.destroyApp(true);
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
			} else if(disp == this.preferenceForm){
				this.fileSystemParam = this.preferenceForm.getFileSystemParam();
				try {
					this.fileSystem.setParameter(this.fileSystemParam);
				} catch (Exception e){
					this.warning("Unable to change file system parameter", e);
				}
				int locatorType = this.preferenceForm.getLocatorType();
				String locatorParam = this.preferenceForm.getLocatorParam();
				try {
					this.changeLocator(locatorType, locatorParam);
				} catch (Exception e){
					this.warning("Unable to change locator", e);
				}
				this.twitterUser = this.preferenceForm.getTwitterUser();
				this.twitterPass = this.preferenceForm.getTwitterPass();
				this.twitter.setCredentials(this.twitterUser, this.twitterPass);
				this.show(this.canvas);
			} else if(disp == this.twitterTextBox){
				this.show(this.canvas);
				this.schedule(ACTION_REPORT_POSITION, null);
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
				this.updateOverlay();
				this.updateTarget();
				this.inOverlayList = false;
				this.show(this.canvas);
			}
		} else if(cmd == this.switchToMidpCanvasCommand){
			this.changeCanvas(CANVAS_MIDP);
		} else if(cmd == this.switchToM3gCanvasCommand){
			this.changeCanvas(CANVAS_M3G);
		} else if(cmd == this.startLocatorCommand){
			this.schedule(ACTION_SWITCH_LOCATOR_STATE, Boolean.TRUE);
		} else if(cmd == this.stopLocatorCommand){
			this.schedule(ACTION_SWITCH_LOCATOR_STATE, Boolean.FALSE);
		} else if(cmd == this.preferenceCommand){
			this.show(this.preferenceForm);
		} else if(cmd == this.startBacklightCommand){
			this.startBacklight();
		} else if(cmd == this.stopBacklightCommand){
			this.stopBacklight();
		} else if(cmd == this.reportPositionCommand){
			this.twitterTextBox.setString("");
			this.show(this.twitterTextBox);
		}
	}
	
	public void commandAction(Command cmd, Item item){
		if(cmd == this.preferenceForm.fileSystemConfigCommand){
			try {
				this.fileSystem.configure();
			} catch (Exception e){
				this.error("Cannot configure file system", e);
			}
		}
	}
	
	private void changeCanvas(int type){
		int cx = this.canvas.getPositionX();
		int cy = this.canvas.getPositionY();
		this.initCanvas(type);
		this.canvas.setSegment(this.map.segment, this.map.xcount, this.map.ycount);
		this.canvas.setMap(this.map.id);
		this.canvas.setPosition(cx, cy);
		this.show(this.canvas);
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
	
	public void alert(String message){
		Alert alert = new Alert(this.getResource("alert"), message, null, AlertType.ERROR);
		this.show(alert);
	}
	
	public void error(String message, Exception e){
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
	
	public void warning(String message, Exception e){
		String text = "WARNING: " + message;
		if(e != null) text += " " + e.toString();
		System.err.println(text);
		if(e != null) e.printStackTrace();
	}
	
	public void info(String message){
		System.out.println("INFO: " + message);
	}
	
	public void show(Displayable disp){
		this.display.setCurrent(disp);
	}
	
	public String getResource(String name){
		if(this.resources == null) return "!!" + name;
		String text = (String) this.resources.get(name);
		return (text != null) ? text : "!" + name;
	}
	
	private TimerTask schedule(final int action, final Object data){
		return this.schedule(action, data, 1);
	}
	
	private TimerTask schedule(final int action, final Object data, long delay){
		return this.schedule(action, data, delay, -1);
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
		case ACTION_SHOW_CANVAS:
			this.show(this.canvas);
			this.splash = null;
			break;
		case ACTION_SHOW_PREFERENCES:
			this.show(this.preferenceForm);
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
		case ACTION_SWITCH_LOCATOR_STATE:
			this.switchLocatorState(((Boolean) data).booleanValue());
			break;
		case ACTION_BACKLIGHT:
			if(!this.backlight){
				task.cancel();
				break;
			}
			this.updateBacklight(true);
			break;
		case ACTION_REPORT_POSITION:
			this.reportPosition();
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
			this.setResource("notice-text", "Tato aplikace je autorem poskytována výhradně bez datového obsahu (bez rastrových mapových podkladů) nebo s datovým obsahem šířeným pod licencí kompatibilní s GPLv3. Za datový obsah distribuovaný společně s aplikací nese zodpovědnost tvůrce konkrétního JAR balíčku (midlet suite). Pokud máte podezření, že datový obsah je šířený v rozporu s licencí či autorským zákonem, aplikaci ze svého zařízení odinstalujte.");
			this.setResource("controls", "Ovládání");
			this.setResource("controls-text", "křížek [#] - přepínání pohledu na výšku / na šířku\nhvězdička [*] - přepínání zobrazení přes celý displej\nhlavní tlačítko [FIRE] - umístění bodu pokračování trasy\n[5] - zobrazení vzdálenosti od cíle\nsměrové šipky a číselná tlačítka - ovládání pohybu mapy");
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
			this.setResource("start-locator", "Spustit lokátor");
			this.setResource("stop-locator", "Ukončit lokátor");
			this.setResource("locator-error", "Chyba lokátoru");
			this.setResource("preferences", "Nastavení");
			this.setResource("locator", "Lokátor");
			this.setResource("locator-none", "Žádný");
			this.setResource("locator-jsr082", "Externí GPS přes Bluetooth");
			this.setResource("locator-jsr179", "Vestavěná GPS");
			this.setResource("locator-bts", "Podle GSM vysílačů");
			this.setResource("locator-param", "Bluetooth adresa GPS");
			this.setResource("filesystem-param", "Cesta k adresáři s daty");
			this.setResource("filesystem-config", "Konfigurovat adresář s daty");
			this.setResource("filesystem-alert", "Název adresáře musí končit příponou \"_DATA\"");
			this.setResource("start-backlight", "Zapnout trvalé podsvícení");
			this.setResource("stop-backlight", "Vypnout trvalé podsvícení");
			this.setResource("backlight-warning", "Podsvícení žere baterku!!!");
			this.setResource("level-up", "o úroveň výš");
			this.setResource("distance", "Vzdálenost");
			this.setResource("report-position", "Nahlásit polohu");
			this.setResource("error-report-position", "Chyba při pokusu o nahlášení polohy");
			this.setResource("position-reported", "Poloha úspěšně nahlášena");
			this.setResource("twitter-user", "Twitter - uživatelské jméno");
			this.setResource("twitter-pass", "Twitter - heslo");
			this.setResource("twitter-text", "Kde jsem?");
			this.setResource("", "");
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
			this.setResource("notice-text", "This application is provided without any data content (raster maps) or with data content shared under license compatible to GPLv3. Creator of certain JAR archive (midlet suite) is responsible for data content distributed with this application. If you suspect data content breaks license or any law valid in your country please uninstall application immediately.");
			this.setResource("controls", "Controls");
			this.setResource("controls-text", "pound [#] - switch landscape / portrait view\nasterisk [*] - switch fullscreen mode\nfire button - places path continuation point\n[5] - display distance from target\narrow and numeric keys - controls map scroll");
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
			this.setResource("start-locator", "Start locator");
			this.setResource("stop-locator", "Stop locator");
			this.setResource("locator-error", "Locator error");
			this.setResource("preferences", "Preferences");
			this.setResource("locator", "Locator");
			this.setResource("locator-none", "None");
			this.setResource("locator-jsr082", "GPS over Bluetooth");
			this.setResource("locator-jsr179", "Built-in GPS");
			this.setResource("locator-bts", "Using BTS");
			this.setResource("locator-param", "GPS Bluetooth address");
			this.setResource("filesystem-param", "Path to data directory");
			this.setResource("filesystem-config", "Configure data directory");
			this.setResource("filesystem-alert", "Directory name must ends with suffix \"_DATA\"");
			this.setResource("start-backlight", "Start backlight");
			this.setResource("stop-backlight", "Stop backlight");
			this.setResource("backlight-warning", "Backlight eats up battery!!!");
			this.setResource("level-up", "up one level");
			this.setResource("distance", "Distance");
			this.setResource("report-position", "Report position");
			this.setResource("error-report-position", "Error when reporting position");
			this.setResource("position-reported", "Position successfully reported");
			this.setResource("twitter-user", "Twitter - username");
			this.setResource("twitter-pass", "Twitter - password");
			this.setResource("twitter-text", "Where am I?");
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
			this.error("Cannot get image", e);
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
		double lat, lon;
		if(orgmap == null){
			lat = map.deflat;
			lon = map.deflon;
		} else {
			double[] latlon = new double[2];
			orgmap.toGlobal(this.canvas.getPositionX(), this.canvas.getPositionY(), latlon);
			lat = latlon[0];
			lon = latlon[1];
			if(!this.map.insideGlobal(lat, lon)){
				lat = map.deflat;
				lon = map.deflon;
			}
		}
		this.setPosition(lat, lon);
	}
	
	private void setPosition(double lat, double lon){
		this.info("Position: " + lat+ ", " + lon);
		int[] xy = new int[2];
		this.map.toLocal(lat, lon, xy);
		this.canvas.setPosition(xy[0], xy[1]);
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
	
	private void appendForm(Form form, String title, String text){
		StringItem si = new StringItem(title + "\n", text + "\n \n");
		form.append(si);
	}
	
	private Form createAboutForm(){
		Form form = new Form(this.getResource("about"));
		// form.append(new ImageItem("", this.getResImage("/stripe.png"), ImageItem.LAYOUT_CENTER, ""));
		this.appendForm(form, this.getResource("notice"), this.getResource("notice-text"));
		this.appendForm(form, this.getResource("author"), "Copyleft 2008 - Tomáš Darmovzal");
		this.appendForm(form, "E-mail", "tomas.darmovzal@gmail.com");
		this.appendForm(form, "URL", "http://darmovzal.nuabi.com");
		this.appendForm(form, this.getResource("license"), this.getResource("license-text") + " GPLv3");
		this.appendForm(form, this.getResource("controls"), this.getResource("controls-text"));
		return form;
	}
	
	public void startScroll(){
		this.scrollTask = this.schedule(this.ACTION_SCROLL, null, 10, 40);
	}
	
	private void getPosition(double[] latlon){
		this.map.toGlobal(this.canvas.getPositionX(), this.canvas.getPositionY(), latlon);
	}
	
	private String[] getPositionStr(){
		double[] rcoords = new double[2];
		this.getPosition(rcoords);
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
		int[] xy = new int[2];
		int x, y;
		int count = this.overlayList.getItemCount();
		for(int i = 0; i < count; i++){
			lat = this.overlayList.getItemLatitude(i);
			lon = this.overlayList.getItemLongitude(i);
			this.map.toLocal(lat, lon, xy);
			x = xy[0];
			y = xy[1];
			name = this.overlayList.getItemName(i);
			if((name != null) && ((name.trim()).length() == 0)) name = null;
			switch(this.overlayList.getItemType(i)){
			case OverlayList.TYPE_PATH_START:
				color = this.overlayList.getItemColor(i);
				String length = this.getDistanceStr(this.overlayList.getPathLength(i));
				name = (name == null) ? length : name + " [" + length + "]";
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
			int[] xy = new int[2];
			this.map.toLocal(this.targetLatitude, this.targetLongitude, xy);
			this.canvas.setTarget(xy[0], xy[1]);
		}
	}
	
	public void addTracePoint(){
		double[] latlon = new double[2];
		this.map.toGlobal(this.canvas.getPositionX(), this.canvas.getPositionY(), latlon);
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
	
	private void changeLocator(int locatorType, String locatorParam) throws Exception {
		if(locatorType != this.locatorType){
			if((this.locator != null) && this.locatorStarted){
				try {
					this.locator.stop();
				} catch (Exception e){
					this.warning("Cannot stop locator to change it", e);
				}
			}
			this.locator = null;
			this.locatorType = locatorType;
			this.initLocator();
		}
		this.locatorParam = locatorParam;
		if(this.locator != null){
			this.locator.setParameter(this.locatorParam);
			if(this.locatorStarted) this.locator.start();
		}
		this.updateLocatorCommands();
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
		case LOCATOR_BTS:
			if(this.flagBtsLocator) className = "gullsview.BtsLocator";
			break;
		}
		if(className == null) return;
		this.info("Initializing locator " + className);
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
		this.info("Locator initialized");
	}
	
	public void setMessage(String message, int timeout){
		this.canvas.setMessage(message);
		this.schedule(ACTION_HIDE_MESSAGE, message, timeout);
	}
	
	public void locatorPositionUpdated(double lat, double lon){
		this.setPosition(lat, lon);
	}
	
	public void locatorStatusUpdated(String status){
		this.setMessage("GPS: " + this.getResource(status), 3000);
	}
	
	private void updateLocatorCommands(){
		this.canvas.removeCommand(this.startLocatorCommand);
		this.canvas.removeCommand(this.stopLocatorCommand);
		if(this.locator == null) return;
		this.canvas.addCommand(this.locatorStarted ? this.stopLocatorCommand : this.startLocatorCommand);
	}
	
	private void switchLocatorState(boolean start){
		try {
			if(start){
				this.locator.start();
			} else {
				this.locator.stop();
			}
			this.locatorStarted = start;
			this.updateLocatorCommands();
		} catch (Exception e){
			this.warning("Error switching locator state", e);
			this.setMessage(this.getResource("locator-error"), 3000);
		}
	}
	
	private void startBacklight(){
		this.backlight = true;
		this.schedule(ACTION_BACKLIGHT, null, 5000, 5000);
		this.updateBacklightCommands();
		this.setMessage(this.getResource("backlight-warning"), 5000);
		this.updateBacklight(true);
	}
	
	private void stopBacklight(){
		this.backlight = false;
		this.updateBacklightCommands();
		// this.updateBacklight(false);
	}
	
	private void updateBacklight(boolean on){
		com.nokia.mid.ui.DeviceControl.setLights(0, on ? 100 : 0);
	}
	
	private void updateBacklightCommands(){
		this.canvas.removeCommand(this.startBacklightCommand);
		this.canvas.removeCommand(this.stopBacklightCommand);
		this.canvas.addCommand(this.backlight ? this.stopBacklightCommand : this.startBacklightCommand);
	}
	
	public void call(Runnable runnable){
		this.display.callSerially(runnable);
	}
	
	public void fileSystemConfigured(String fileSystemParam){
		if(fileSystemParam != null) this.preferenceForm.setFileSystemParam(fileSystemParam);
		this.show(this.preferenceForm);
	}
	
	private String getDistanceStr(double dist){
		return (dist > 1000) ? (Math.floor(dist / 10) / 100) + "km" : ((int) dist) + "m";
	}
	
	public void showTargetDistance(){
		if(Double.isNaN(this.targetLatitude) || Double.isNaN(this.targetLongitude)) return;
		double[] latlon = new double[2];
		this.getPosition(latlon);
		double dist = Map.distance(this.targetLatitude, this.targetLongitude, latlon[0], latlon[1]);
		this.setMessage(this.getResource("distance") + ": " + this.getDistanceStr(dist), 5000);
	}
	
	private static String doubleToString(double d, int decimal){
		boolean neg = d < 0;
		d = Math.abs(d);
		long intp = (long) Math.floor(d);
		double rest = d - intp;
		StringBuffer sb = new StringBuffer();
		if(neg) sb.append('-');
		sb.append(intp);
		if(decimal > 0){
			sb.append('.');
			for(int i = 0; i < decimal; i++){
				rest *= 10;
				long digit = ((long) rest) % 10;
				sb.append(digit);
			}
		}
		return sb.toString();
	}
	
	private String getTimeAsString(long millis){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(millis));
		StringBuffer sb = new StringBuffer();
		sb.append(cal.get(Calendar.YEAR));
		sb.append('/');
		sb.append(cal.get(Calendar.MONTH) + 1);
		sb.append('/');
		sb.append(cal.get(Calendar.DAY_OF_MONTH));
		sb.append(' ');
		sb.append(cal.get(Calendar.HOUR));
		sb.append(':');
		sb.append(cal.get(Calendar.MINUTE));
		sb.append(':');
		sb.append(cal.get(Calendar.SECOND));
		return sb.toString();
	}
	
	private void reportPosition(){
		try {
			double[] latlon = new double[2];
			this.getPosition(latlon);
			String lat = doubleToString(latlon[0], 6);
			String lon = doubleToString(latlon[1], 6);
			String url = "http://maps.google.com/?ie=UTF8&ll=" + Twitter.urlEncode((lat + "," + lon).getBytes("UTF-8"));
			String time = this.getTimeAsString(System.currentTimeMillis());
			String text = this.twitterTextBox.getString();
			String message = text + " | " + time + " | " + lat + ":" + lon + " | " + url;
System.out.println(message.length() + ": " + message);
			this.twitter.send(message);
			this.setMessage(this.getResource("position-reported"), 5000);
		} catch (Exception e){
			this.alert(this.getResource("error-report-position") + ": " + e.toString());
		}
	}
}


