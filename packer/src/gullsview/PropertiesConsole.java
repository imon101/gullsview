package gullsview;

import java.io.*;
import java.util.*;


public class PropertiesConsole extends Console {
	private Properties properties;
	
	public PropertiesConsole(String path) throws IOException {
		this(new File(path));
	}
	
	public PropertiesConsole(File file) throws IOException {
		this(new Properties());
		FileInputStream fis = new FileInputStream(file);
		this.properties.load(fis);
		fis.close();
	}
	
	public PropertiesConsole(Properties properties){
		this.properties = properties;
	}
	
	public String input(String id, String question, String def){
		String value = this.properties.getProperty(id);
		return (value == null) ? def : value;
	}
	
	public void print(String text, String color){
		// NOOP
	}
	
	public void error(String message, Throwable t){
		System.err.println("ERROR: " + message);
		if(t != null) t.printStackTrace();
	}
}


