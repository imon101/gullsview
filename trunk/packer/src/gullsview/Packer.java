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
		this.maps.add(this.createWorldMap());
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
	
	private Map createWorldMap(){
		Map map = new Map();
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
		map.realax = -180;
		map.realay = 90;
		map.realbx = 180;
		map.realby = 90;
		map.realcx = -180;
		map.realcy = -90;
		return map;
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
}


