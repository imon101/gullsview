package gullsview;

import java.io.*;


public class BtsLocator extends Locator implements Runnable {
	private Thread thread;
	private int cellid;
	private int lac;
	private int timeout;
	private String database;
	
	public void init(){
		this.timeout = 60000;
		this.database = "locator";
	}
	
	public void start() throws Exception {
		if(this.thread != null) throw new Exception("Already started");
		this.cellid = this.lac = -1;
		this.thread = new Thread(this);
		this.thread.start();
		this.setStarted(true);
	}
	
	public void stop() throws Exception {
		if(this.thread == null) throw new Exception("Not started");
		this.thread.interrupt();
		this.thread = null;
		this.setStarted(false);
	}
	
	public void run(){
		try {
			for(;;){
				this.checkBts();
				try {
					Thread.sleep(this.timeout);
				} catch (InterruptedException e){
					break;
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			this.setStarted(false);
		}
	}
	
	private void checkBts() throws Exception {
		this.main.info("Checking BTS");
		int cellid = this.getCellId();
		int lac = this.getLAC();
		if((cellid <= 0) || (lac <= 0)) return;
		if((cellid == this.cellid) && (lac == this.lac)) return;
		this.cellid = cellid;
		this.lac = lac;
		this.reportBts();
	}
	
	private void reportBts() throws Exception {
		this.main.info("Finding BTS position - started");
		String resource = "/" + this.database + ".bts";
		InputStream is = (this.getClass()).getResourceAsStream(resource);
		if(is == null) throw new Exception("BTS database " + resource + " does not exist");
		DataInputStream dis = new DataInputStream(is);
		try {
double tlat = Double.NaN;
double tlon = Double.NaN;
int tcellid = 0, tlac = 0;
			int count = dis.readInt();
			for(int i = 0; i < count; i++){
				// String title = dis.readUTF();
				double lat = dis.readDouble();
				double lon = dis.readDouble();
				int cellcount = dis.readInt();
				for(int j = 0; j < cellcount; j++){
					int cellid = dis.readInt();
					int lac = dis.readInt();
					if((cellid == this.cellid) && (lac == this.lac)){
						this.updatePosition(lat, lon);
						this.main.setMessage("[" + Integer.toString(cellid, 16) + ":" + Integer.toString(lac, 16) + "]", 5000);
						return;
					}
if((cellid == this.cellid) && ((lac & 0xff00) == (this.lac & 0xff00))){
tlat = lat;
tlon = lon;
tcellid = cellid;
tlac = lac;
}
				}
			}
if(!Double.isNaN(tlat) && !Double.isNaN(tlon)){
this.updatePosition(tlat, tlon);
this.main.setMessage("[" + Integer.toString(this.cellid, 16) + ":" + Integer.toString(this.lac, 16) + "] ~ " + Integer.toString(tcellid, 16) + ":" + Integer.toString(tlac, 16), 5000);
return;
}
			this.updateStatus("out-of-service");
this.main.setMessage(this.cellid + ":" + this.lac);
		} finally {
			dis.close();
			this.main.info("Finding BTS position - finished");
		}
	}
	
	private int getIntProperty(String name, int radix){
		String s = System.getProperty(name);
		if(s == null) return -1;
		try {
			return Integer.parseInt(s, radix);
		} catch (Exception e){
			return -2;
		}
	}
	
	private int getCellId(){
		int cellid = -1;
		cellid = this.getIntProperty("com.sonyericsson.net.cellid", 16);
		if(cellid < 0) cellid = this.getIntProperty("com.nokia.mid.cellid", 16);
		if(cellid < 0) cellid = this.getIntProperty("Cell-ID", 16);
		return cellid;
	}
	
	private int getLAC(){
		int lac = -1;
		lac = this.getIntProperty("com.sonyericsson.net.lac", 16);
		if(lac < 0) lac = this.getIntProperty("com.nokia.mid.lac", 16);
		return lac;
	}
}


