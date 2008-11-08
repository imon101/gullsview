package gullsview;


public abstract class Locator {
	protected Main main;
	
	public void setOwner(Main main){
		this.main = main;
	}
	
	public void setParameter(String param) throws Exception {}
	
	public abstract void init() throws Exception;
	
	public abstract void start() throws Exception;
	
	public abstract void stop() throws Exception;
	
	protected void updatePosition(double lat, double lon){
		this.main.locatorPositionUpdated(lat, lon);
	}
	
	protected void updateStatus(String text){
		this.main.locatorStatusUpdated(text);
	}
}


