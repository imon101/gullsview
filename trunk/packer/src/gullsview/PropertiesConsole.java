package gullsview;

import java.util.*;


public class PropertiesConsole extends Console {
	private Properties properties;
	
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


