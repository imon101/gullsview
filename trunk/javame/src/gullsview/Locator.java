package gullsview;


public abstract class Locator {
	protected Main main;
	private double lastLat, lastLon;
	private long lastPositionTime;
	private String lastStatus;
	private long lastStatusTime;
	private boolean started;
	
	public void setOwner(Main main){
		this.main = main;
		this.started = false;
	}
	
	public void setParameter(String param) throws Exception {}
	
	public abstract void init() throws Exception;
	
	public abstract void start() throws Exception;
	
	public abstract void stop() throws Exception;
	
	protected void updatePosition(double lat, double lon){
		this.lastLat = lat;
		this.lastLon = lon;
		this.lastPositionTime = System.currentTimeMillis();
		this.main.locatorPositionUpdated(lat, lon);
	}
	
	protected void updateStatus(String text){
		this.lastStatus = text;
		this.lastStatusTime = System.currentTimeMillis();
		this.main.locatorStatusUpdated(text);
	}
	
	public double getLastLatitude(){
		return this.lastLat;
	}
	
	public double getLastLongitude(){
		return this.lastLon;
	}
	
	public long getLastPositionTime(){
		return this.lastPositionTime;
	}
	
	public boolean isStarted(){
		return this.started;
	}
	
	protected void setStarted(boolean on){
		this.started = on;
	}
}


