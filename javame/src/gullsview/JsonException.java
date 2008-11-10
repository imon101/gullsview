package gullsview;


public class JsonException extends Exception {
	private int line, column;
	
	public JsonException(int line, int column, String message){
		super(message);
		this.line = line;
		this.column = column;
	}
	
	public String toString(){
		return (this.getClass()).getName() + " at line " + this.line + ", column " + this.column +": " + super.getMessage();
	}
}


