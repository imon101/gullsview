package gullsview;

import javax.microedition.location.*;


public class Jsr179Locator extends Locator implements Runnable {
	private Object locationProvider;
	private int state;
	private int timeout = 1000;
	private Thread thread;
	
	public void init() throws Exception {
		Criteria criteria = new Criteria();
		this.locationProvider = LocationProvider.getInstance(criteria);
		this.state = LocationProvider.OUT_OF_SERVICE;
	}
	
	public void start() throws Exception {
		if(this.thread != null) throw new Exception("Cannot start Locator thread, another thread already running");
		this.thread = new Thread(this);
		this.thread.start();
	}
	
	public void stop() throws Exception {
		this.thread.interrupt();
		this.thread = null;
	}
	
	public void run(){
		LocationProvider lp = (LocationProvider) this.locationProvider;
		for(;;){
			try {
				Location location = lp.getLocation(-1);
				QualifiedCoordinates coord = location.getQualifiedCoordinates();
				this.locationUpdated(coord.getLatitude(), coord.getLongitude());
			} catch (Exception e){
				e.printStackTrace();
			}
			int state = lp.getState();
			if(state != this.state){
				this.providerStateChanged(state);
				this.state = state;
			}
			try {
				Thread.sleep(this.timeout);
			} catch (InterruptedException e){
				break;
			}
		}
	}
	
	public void locationUpdated(double lat, double lon){
		System.out.println("Location: " + lat + " : " + lon);
		this.main.locatorPositionUpdated(lat, lon);
	}
	
	public void providerStateChanged(int state){
		String text;
		switch(state){
		case LocationProvider.AVAILABLE: text = "available"; break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE: text = "temporarily-unavailable"; break;
		case LocationProvider.OUT_OF_SERVICE: text = "out-of-service"; break;
		default: return;
		}
		this.main.locatorStatusUpdated(text);
	}
}


