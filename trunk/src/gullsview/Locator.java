package gullsview;


public abstract class Locator {
	protected Main main;
	
	public void setOwner(Main main){
		this.main = main;
	}
	
	public abstract void init() throws Exception;
	
	public abstract void start() throws Exception;
	
	public abstract void stop() throws Exception;
}


