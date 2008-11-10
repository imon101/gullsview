package gullsview;

import javax.microedition.location.*;


public class Jsr179Locator extends Locator implements LocationListener {
	private LocationProvider locationProvider;
	
	public void init() throws Exception {
		Criteria criteria = new Criteria();
		this.locationProvider = LocationProvider.getInstance(criteria);
	}
	
	public void start(){
		this.locationProvider.setLocationListener(this, -1, -1, -1);
		this.setStarted(true);
	}
	
	public void stop(){
		this.locationProvider.setLocationListener(null, -1, -1, -1);
		this.setStarted(false);
	}
	
	public void locationUpdated(LocationProvider lp, Location location){
		QualifiedCoordinates coord = location.getQualifiedCoordinates();
		double lat = coord.getLatitude();
		double lon = coord.getLongitude();
		this.updatePosition(lat, lon);
	}
	
	public void providerStateChanged(LocationProvider lp, int state){
		String text;
		switch(state){
		case LocationProvider.AVAILABLE: text = "available"; break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE: text = "temporarily-unavailable"; break;
		case LocationProvider.OUT_OF_SERVICE: text = "out-of-service"; break;
		default: return;
		}
		this.updateStatus(text);
	}
}


