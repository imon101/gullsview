package gullsview;


public abstract class Console {
	public Console(){}
	
	public abstract String input(String id, String question, String def);
	
	public String input(String id, String question){
		return this.input(id, question, null);
	}
	
	public abstract void print(String text, String color);
	
	public void print(String text){
		this.print(text, null);
	}
	
	public abstract void error(String message, Throwable t);
	
	public void error(String message){
		this.error(message, null);
	}
}


