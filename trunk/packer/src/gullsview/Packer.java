package gullsview;

import java.io.*;
import java.net.*;
import java.util.*;


public class Packer {
	private Console console;
	private java.util.Map<String, Set<String>> restrictions;
	
	public Packer(Console console) throws Exception {
		this.console = console;
		this.restrictions = new HashMap<String, Set<String>>();
this.addRestrictedEntry("FC", "gullsview/");
		this.addRestrictedEntry("BT", "gullsview/Jsr082Locator.class");
		this.addRestrictedEntry("LAPI", "gullsview/Jsr179Locator.class");
		this.addRestrictedEntry("M3G", "gullsview/M3GMapCanvas.class");
		this.addRestrictedEntry("M3G", "pointer2.png");
		this.addRestrictedEntry("M3G", "compass.png");
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
}


