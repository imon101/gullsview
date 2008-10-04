package gullsview;

import java.io.*;
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
		this.filterJars(".", true, true, true, true);
		this.filterJars(".", false, false, false, false);
	}
	
	private void filterJars(String path, boolean fc, boolean bt, boolean lapi, boolean m3g) throws IOException {
		List<String> constraints = new ArrayList<String>();
		String pathPrefix = path + "/GullsView";
		if(fc){
			pathPrefix += "_FC";
			constraints.add("FC");
		}
		if(bt){
			pathPrefix += "_BT";
			constraints.add("BT");
		}
		if(lapi){
			pathPrefix += "_LAPI";
			constraints.add("LAPI");
		}
		if(m3g){
			pathPrefix += "_M3G";
			constraints.add("M3G");
		}
		String[] sconstraints = new String[constraints.size()];
		constraints.toArray(sconstraints);
		this.filterJar(pathPrefix, sconstraints);
	}
	
	private void filterJar(String pathPrefix, final String[] constraints) throws IOException {
		JarFilter.Filter filter = new JarFilter.Filter(){
			public boolean processEntry(String name){
				return Packer.this.isRestricted(name) ? Packer.this.isAllowed(name, constraints) : true;
			}
			
			public void processManifest(java.util.Map values){
			}
		};
		InputStream in = (this.getClass()).getResourceAsStream("/GullsView.mjar");
		String jarFileName = pathPrefix + ".jar";
		OutputStream out = new FileOutputStream(jarFileName);
		JarFilter jf = new JarFilter(in, out, filter);
		java.util.Map jad = jf.run();
		File jar = new File(jarFileName);
		jad.put("MIDlet-Jar-URL", jarFileName);
		jad.put("MIDlet-Jar-Size", String.valueOf(jar.length()));
		FileOutputStream fos = new FileOutputStream(pathPrefix + ".jad");
		jf.writeManifest(jad, fos);
		fos.flush();
		fos.close();
	}
}


