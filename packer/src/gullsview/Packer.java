package gullsview;


public class Packer {
	private Console console;
	
	public Packer(Console console) throws Exception {
		this.console = console;
	}
	
	public void run(){
		this.console.print(this.console.input("ahoj", "Ahoj"));
	}
}


