package gullsview;

import java.io.*;
import java.net.*;
import java.util.*;


public class Packer {
	private Console console;
	private java.util.Map<String, Set<String>> restrictions;
	private List<Map> maps;
	
	public Packer(Console console) throws Exception {
		this.console = console;
		this.restrictions = new HashMap<String, Set<String>>();
		this.maps = new ArrayList<Map>();
		this.addRestrictedEntry("FC", "gullsview/FileSystemImpl.class");
		this.addRestrictedEntry("BT", "gullsview/Jsr082Locator.class");
		this.addRestrictedEntry("LAPI", "gullsview/Jsr179Locator.class");
		this.addRestrictedEntry("M3G", "gullsview/M3GMapCanvas.class");
		this.addRestrictedEntry("M3G", "pointer2.png");
		this.addRestrictedEntry("M3G", "compass.png");
		Map world = new Map();
		this.processWorldMap(world);
		this.maps.add(world);
	}
	
	public void addRestrictedEntry(String constraint, String path){
		Set<String> paths = this.restrictions.get(constraint);
		if(paths == null){
			paths = new HashSet<String>();
			this.restrictions.put(constraint, paths);
		}
		paths.add(path);
	}
	
	public boolean isRestricted(String path){
		for(Set<String> entries : this.restrictions.values()){
			if(entries.contains(path)) return true;
		}
		return false;
	}
	
	public boolean isAllowed(String path, String[] constraints){
		for(String constraint : constraints){
			if((this.restrictions.get(constraint)).contains(path)) return true;
		}
		return false;
	}
	
	public void run() throws Exception {
		int count = this.console.inputInt("map-count", null, 1);
		for(int i = 0; i < count; i++){
			Map map = new Map();
			this.processMap(map, i);
			this.maps.add(map);
		}
		String path = (new File(".")).getCanonicalPath();
		for(;;){
			path = this.console.inputString("output-path", null, path);
			try {
				File file = new File(path);
				if(!file.exists()){
					this.console.errorRes("error-not-exist");
				} else if(!file.isDirectory()){
					this.console.errorRes("error-not-directory");
				} else {
					break;
				}
			} catch (Exception e){
				this.console.errorRes("error-incorrect-path", e);
			}
		}
		boolean flagFc = this.console.inputBoolean("enable-fc", null, false);
		boolean flagBt = this.console.inputBoolean("enable-bt", null, false);
		boolean flagLapi = this.console.inputBoolean("enable-lapi", null, false);
		boolean flagM3g = this.console.inputBoolean("enable-m3g", null, false);
		this.console.printRes("start");
		this.filterJars(path, flagFc, flagBt, flagLapi, flagM3g);
		this.console.printRes("finish");
	}
	
	public void close(){
		this.console.close();
	}
	
	private void filterJars(String path, boolean fc, boolean bt, boolean lapi, boolean m3g) throws IOException {
		List<String> constraints = new ArrayList<String>();
		String pathPrefix = path + "/GullsView";
		String pathSuffix = "";
		if(fc){
			pathSuffix += "_FC";
			constraints.add("FC");
		}
		if(bt){
			pathSuffix += "_BT";
			constraints.add("BT");
		}
		if(lapi){
			pathSuffix += "_LAPI";
			constraints.add("LAPI");
		}
		if(m3g){
			pathSuffix += "_M3G";
			constraints.add("M3G");
		}
		String[] sconstraints = new String[constraints.size()];
		constraints.toArray(sconstraints);
		this.filterJar(pathPrefix + pathSuffix, sconstraints, (pathSuffix.length() > 0) ? pathSuffix.substring(1) : "none");
	}
	
	private void filterJar(String pathPrefix, final String[] constraints, final String extensions) throws IOException {
		JarFilter.Filter filter = new JarFilter.Filter(){
			public boolean processEntry(String name){
				return Packer.this.isRestricted(name) ? Packer.this.isAllowed(name, constraints) : true;
			}
			
			public void processManifest(java.util.Map<String, String> values){
				String descId = "MIDlet-Description";
				String desc = values.get(descId);
				if(desc != null) values.put(descId, desc + " (extensions: " + extensions + ")");
			}
			
			public void addEntries(FileDumper fd) throws IOException {
				fd.next("data/maps");
				Packer.this.writeMaps(fd);
				Packer.this.writeResourceImage(fd, "data/world/0_0", "/world/0_0");
				Packer.this.writeResourceImage(fd, "data/world/0_1", "/world/0_1");
				Packer.this.writeResourceImage(fd, "data/world/1_0", "/world/1_0");
				Packer.this.writeResourceImage(fd, "data/world/1_1", "/world/1_1");
			}
		};
		InputStream in = (this.getClass()).getResourceAsStream("/GullsView.mjar");
		String jarFileName = pathPrefix + ".jar";
		OutputStream out = new FileOutputStream(jarFileName);
		JarFilter jf = new JarFilter(in, out, filter);
		java.util.Map<String, String> jad = jf.run();
		File jar = new File(jarFileName);
		jad.put("MIDlet-Jar-URL", jarFileName);
		jad.put("MIDlet-Jar-Size", String.valueOf(jar.length()));
		FileOutputStream fos = new FileOutputStream(pathPrefix + ".jad");
		jf.writeManifest(jad, fos);
		fos.flush();
		fos.close();
	}
	
	private String getIp(){
		try {
			URL url = new URL("http://whatismyip.org");
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String ip = br.readLine();
			br.close();
			return ((ip != null) && (ip.length() < 20)) ? ip : null;
		} catch (Exception e){
			return null;
		}
	}
	
	private void processMap(Map map, int index){
		map.name = this.inputString(index, "name", "");
		map.title = this.inputString(index, "title", "");
		map.vendor = this.inputString(index, "vendor", "");
		String ip = this.getIp();
		map.secchunk = (ip != null) ? ip : "";
		map.scale = this.inputInt(index, "scale", 0);
		map.segment = this.inputInt(index, "segment", 256);
		while(map.xcount <= 0) map.xcount = this.inputInt(index, "xcount", 1);;
		while(map.ycount <= 0) map.ycount = this.inputInt(index, "ycount", 1);;
		map.locax = 0;
		map.locay = 0;
		map.locbx = map.segment * map.xcount;
		map.locby = 0;
		map.loccx = 0;
		map.loccy = map.segment * map.ycount;
		map.realay = this.inputCoord(index, "lt-lat", 0);
		map.realax = this.inputCoord(index, "lt-lon", 0);
		map.realby = this.inputCoord(index, "rt-lat", 0);
		map.realbx = this.inputCoord(index, "rt-lon", 0);
		map.realcy = this.inputCoord(index, "lb-lat", 0);
		map.realcx = this.inputCoord(index, "lb-lon", 0);
		map.defaulty = this.inputCoord(index, "lat", (map.realay + map.realcy) / 2);
		map.defaultx = this.inputCoord(index, "lon", (map.realax + map.realbx) / 2);
	}
	
	private void processWorldMap(Map map){
		map.name = "world";
		map.title = this.console.r("world");
		map.vendor = "";
		map.secchunk = "";
		map.scale = 0;
		map.segment = 256;
		map.xcount = 2;
		map.ycount = 2;
		map.defaultx = 0;
		map.defaulty = 0;
		map.locax = 0;
		map.locay = 0;
		map.locbx = map.segment * map.xcount;
		map.locby = 0;
		map.loccx = 0;
		map.loccy = map.segment * map.ycount;
		map.realax = 90;
		map.realay = -180;
		map.realbx = 90;
		map.realby = 180;
		map.realcx = -90;
		map.realcy = -180;
	}
	
	private void writeMaps(FileDumper fd) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		this.writeMaps(baos);
		byte[] buffer = baos.toByteArray();
		fd.write(buffer, 0, buffer.length);
	}
	
	private void writeMaps(OutputStream os) throws IOException {
		DataOutputStream dos = new DataOutputStream(os);
		dos.writeInt(maps.size());
		for(Map map : maps) map.save(dos);
		dos.flush();
	}
	
	private void writeResourceImage(FileDumper fd, String path, String resource) throws IOException {
		fd.next(path);
		InputStream is = (this.getClass()).getResourceAsStream(resource);
		if(is == null) throw new IOException("Cannot find resource \"" + resource + "\"");
		byte[] buffer = new byte[1024];
		int count;
		while((count = is.read(buffer, 0, buffer.length)) > 0) fd.write(buffer, 0, count);
	}
	
	private String inputString(int index, String id, String def){
		String ret;
		for(;;){
			ret = this.console.inputString("map-" + index + "-" + id, "map-" + id, def);
			ret = ret.trim();
			if(ret.length() > 0) break;
			this.console.errorRes("error-empty");
		}
		return ret;
	}
	
	private int inputInt(int index, String id, int def){
		return this.console.inputInt("map-" + index + "-" + id, "map-" + id, def);
	}
	
	private double inputCoord(int index, String id, double def){
		for(;;){
			String str = this.inputString(index, id, this.formatCoord(def));
			try {
				return this.parseCoord(str);
			} catch (Exception e){
				this.console.errorRes("error-not-coord");
			}
		}
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
}


