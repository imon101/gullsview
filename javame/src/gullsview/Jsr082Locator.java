package gullsview;

import java.io.*;
import javax.microedition.io.*;
import javax.bluetooth.*;


public class Jsr082Locator extends Locator {
	private String btaddr;
	
	public void init(){
		
	}
	
	public void setParameter(String param) throws Exception {
		this.btaddr = param;
	}
	
	public void start() throws Exception {
this.btaddr = "0000000DECAF";
		String url = "btspp://" + this.btaddr + ":1;authenticate=false;encrypt=false;master=true";
		StreamConnection conn = (StreamConnection) Connector.open(url/*, Connector.READ*/);
//		InputStream is = conn.openInputStream();
OutputStream os = conn.openOutputStream();
byte[] buffer = "Ahoj".getBytes();
os.write(buffer, 0, buffer.length);
	}
	
	public void stop() throws Exception {}
}


