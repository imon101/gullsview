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
	}
	
	public void stop() throws Exception {
		if(this.thread == null) throw new Exception("Not started");
		this.thread.interrupt();
		this.thread = null;
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
						this.main.setMessage("Cell " + cellid + ":" + lac, 5000);
						return;
					}
if((cellid == this.cellid) && ((lac & 0xff00) == (this.lac & 0xff00))){
tlat = lat;
tlon = lon;
}
				}
			}
if(!Double.isNaN(tlat) && !Double.isNaN(tlon)){
this.updatePosition(tlat, tlon);
this.main.setMessage("~ " + this.cellid + ":" + this.lac, 5000);
return;
}
			this.updateStatus("out-of-service");
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
		return this.getIntProperty("com.sonyericsson.net.cellid", 16);
	}
	
	private int getLAC(){
		return this.getIntProperty("com.sonyericsson.net.lac", 16);
	}
}


