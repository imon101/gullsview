package gullsview;

import java.io.*;
import java.util.*;


public class PropertiesConsole extends Console {
	private Properties properties;
	
	public PropertiesConsole(String path) throws IOException {
		this(new File(path));
	}
	
	public PropertiesConsole(File file) throws IOException {
		this(new FileInputStream(file));
	}
	
	public PropertiesConsole(InputStream is) throws IOException {
		this(new Properties());
		this.properties.load(is);
		is.close();
	}
	
	public PropertiesConsole(Properties properties){
		this.properties = properties;
	}
	
	protected boolean retry(){
		return false;
	}
	
	public String input(String id, String question, String def){
		System.out.println("QUESTION: " + question);
		String value = this.properties.getProperty(id);
		System.out.println("PROPERTY: \"" + id + "\" [" + def + "] = " + value);
		if(value != null) return value;
		if(def == null) throw new RuntimeException("Cannot find property " + id);
		return def;
	}
	
	public void print(String text, String color){
		System.out.println(">>> " + text);
	}
	
	public void error(String message, Throwable t){
		System.err.println("ERROR: " + message);
		if(t != null) t.printStackTrace();
		System.exit(1);
	}
}


