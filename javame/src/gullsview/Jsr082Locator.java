package gullsview;

import java.io.*;
import javax.microedition.io.*;
import javax.bluetooth.*;


public class Jsr082Locator extends Locator implements Runnable {
	private String btaddr;
	private Thread thread;
	private StreamConnection conn;
	private InputStream is;
	private StringBuffer sb;
	private boolean started;
	private String sentence;
	private boolean checksum;
	private int paramIndex;
	private double latitude, longitude;
	private double orientation;
	
	public void init(){
		this.sb = new StringBuffer();
	}
	
	public void setParameter(String param) throws Exception {
		this.btaddr = param;
	}
	
	public void start() throws Exception {
		if(this.thread != null) throw new Exception("Already started");
		String url = "btspp://" + this.btaddr + ":1;authenticate=false;encrypt=false;master=true";
		this.conn = (StreamConnection) Connector.open(url, Connector.READ);
		this.is = conn.openInputStream();
		this.thread = new Thread(this);
		this.thread.start();
	}
	
	public void stop() throws Exception {
		this.thread.interrupt();
	}
	
	public void run(){
		try {
			this.main.locatorStatusUpdated("available");
			this.sb.setLength(0);
			byte[] buffer = new byte[1024];
			int count;
			while((count = this.is.read(buffer, 0, buffer.length)) >= 0){
				for(int i = 0; i < count; i++){
					char c = (char) buffer[i];
					boolean special = false;
					switch(c){
					case '$':
						this.flush();
						this.dollar();
						break;
					case ',':
						this.flush();
						this.comma();
						break;
					case '*':
						this.flush();
						this.asterisk();
						break;
					case '\r':
						break;
					case '\n':
						this.flush();
						this.newline();
						break;
					default:
						this.sb.append(c);
					}
				}
			}
			this.flush();
			this.main.locatorStatusUpdated("out-of-service");
		} catch (InterruptedIOException e){
			// Noop
		} catch (IOException e){
			this.main.locatorStatusUpdated("out-of-service");
		} finally {
			this.thread = null;
			try {
				this.is.close();
			} catch (Exception e){
				this.main.warning("Cannot close bluetooth locator input stream", e);
			}
			this.is = null;
			try {
				this.conn.close();
			} catch (Exception e){
				this.main.warning("Cannot close bluetooth locator connection", e);
			}
			this.conn = null;
		}
	}
	
	private void flush(){
		if(sb.length() > 0){
			this.token(this.sb.toString());
			this.sb.setLength(0);
		}
	}
	
	private void dollar(){
		this.started = true;
		this.sentence = null;
		this.checksum = false;
		this.paramIndex = 0;
		this.latitude = this.longitude = Double.NaN;
		this.orientation = Double.NaN;
	}
	
	private void comma(){
		// NOOP
	}
	
	private void asterisk(){
		this.checksum = true;
	}
	
	private void newline(){
		if(!this.started) return;
		this.process();
	}
	
	private void token(String value){
		if(!this.started) return;
		if(this.checksum) return;
		if(this.sentence == null){
			this.sentence = value;
System.out.println(this.sentence);
		} else {
			this.param(this.paramIndex++, value);
		}
	}
	
	private void param(int index, String value){
		if("GPGGA".equals(this.sentence)){
			switch(index){
			case 1: this.latitude = this.parseCoord(value); break;
			case 2: if("S".equalsIgnoreCase(value)) this.latitude = -this.latitude; break;
			case 3: this.longitude = this.parseCoord(value); break;
			case 4: if("W".equalsIgnoreCase(value)) this.longitude = -this.longitude; break;
			}
		} else if("GPRMC".equals(this.sentence)){
			switch(index){
			case 2: this.latitude = this.parseCoord(value); break;
			case 3: if("S".equalsIgnoreCase(value)) this.latitude = -this.latitude; break;
			case 4: this.longitude = this.parseCoord(value); break;
			case 5: if("W".equalsIgnoreCase(value)) this.longitude = -this.longitude; break;
			case 7: this.orientation = this.parseOrientation(value); break;
			}
		} else if("GPGLL".equals(this.sentence)){
			switch(index){
			case 0: this.latitude = this.parseCoord(value); break;
			case 1: if("S".equalsIgnoreCase(value)) this.latitude = -this.latitude; break;
			case 2: this.longitude = this.parseCoord(value); break;
			case 3: if("W".equalsIgnoreCase(value)) this.longitude = -this.longitude; break;
			}
		}
	}
	
	private void process(){
		if(Double.isNaN(this.latitude) || Double.isNaN(this.longitude)) return;
System.out.println(this.latitude + " " + this.longitude + " " + this.orientation);
	}
	
	private double parseCoord(String value){
		try {
			double d = Double.parseDouble(value);
			double deg = Math.floor(d / 100);
			double min = d - (deg * 100);
			return deg + (min / 60);
		} catch (Exception e){
			this.main.warning("Cannot parse coord \"" + value + "\"", e);
			return Double.NaN;
		}
	}
	
	private double parseOrientation(String value){
		try {
			return Double.parseDouble(value);
		} catch (Exception e){
			this.main.warning("Cannot parse orientation \"" + value + "\"", e);
			return Double.NaN;
		}
	}
}


