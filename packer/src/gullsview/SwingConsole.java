package gullsview;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class SwingConsole extends Console {
	private JFrame frame;
	
	public SwingConsole(){
		this.frame = new JFrame(this.r("title"));
		this.frame.setSize(500, 600);
		this.frame.setVisible(true);
	}
	
	public String input(String id, String question, String def){
return null;
	}
	
	public void print(String text, String color){
	}
	
	public void error(String message, Throwable t){
	}
}


