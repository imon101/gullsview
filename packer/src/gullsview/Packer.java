package gullsview;

import java.io.*;
import java.util.*;


public class Packer {
	private Console console;
	
	public Packer(Console console) throws Exception {
		this.console = console;
	}
	
	public void run() throws Exception {
		JarFilter.Filter filter = new JarFilter.Filter(){
			public boolean processEntry(String name){
				return name.equals("gullsview/Main.class");
			}
			
			public void processManifest(Map values){
				values.put("jedna", "dva");
			}
		};
		InputStream in = (this.getClass()).getResourceAsStream("/GullsView.mjar");
		OutputStream out = new FileOutputStream("test.jar");
		JarFilter jf = new JarFilter(in, out, filter);
		jf.run();
	}
}


