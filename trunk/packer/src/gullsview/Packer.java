package gullsview;

import java.io.*;
import java.util.*;


public class Packer {
	private Console console;
	private Map<String, Set<String>> restrictions;
	
	public Packer(Console console) throws Exception {
		this.console = console;
		this.restrictions = new HashMap<String, Set<String>>();
		this.addRestrictedEntry("JSR179", "gullsview/Jsr179Locator.class");
	}
	
	public void addRestrictedEntry(String constraint, String path){
		Set<String> paths = this.restrictions.get(constraint);
		if(paths == null){
			paths = new HashSet<String>();
			this.restrictions.put(constraint, paths);
		}
		paths.add(path);
	}
	
	public void run() throws Exception {
		this.filterJar("GullsView_BT", new String[]{ "JSR082" });
	}
	
	private void filterJar(String pathPrefix, String[] constraints) throws IOException {
		JarFilter.Filter filter = new JarFilter.Filter(){
			public boolean processEntry(String name){
				return name.equals("gullsview/Main.class");
			}
			
			public void processManifest(Map values){
			}
		};
		InputStream in = (this.getClass()).getResourceAsStream("/GullsView.mjar");
		String jarFileName = pathPrefix + ".jar";
		OutputStream out = new FileOutputStream(jarFileName);
		JarFilter jf = new JarFilter(in, out, filter);
		Map jad = jf.run();
		File jar = new File(jarFileName);
		jad.put("MIDlet-Jar-URL", jarFileName);
		jad.put("MIDlet-Jar-Size", String.valueOf(jar.length()));
		FileOutputStream fos = new FileOutputStream(pathPrefix + ".jad");
		jf.writeManifest(jad, fos);
		fos.flush();
		fos.close();
	}
}


