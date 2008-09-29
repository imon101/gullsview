package gullsview;

import java.io.*;


public class StdioConsole extends Console {
	private BufferedReader br;
	
	public StdioConsole() throws IOException {
		this.br = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public String input(String id, String question, String def){
		try {
			System.out.print(question + " >>> ");
			String value = this.br.readLine();
			return (value == null) ? def : value;
		} catch (IOException e){
			throw new RuntimeException("Error in input", e);
		}
	}
	
	public void print(String text, String color){
		System.out.println(text);
	}
	
	public void error(String message, Throwable t){
		System.err.println("ERROR: " + message);
		if(t != null) t.printStackTrace();
	}
}


