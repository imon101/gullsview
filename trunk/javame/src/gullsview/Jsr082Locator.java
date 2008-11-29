package gullsview;

import java.io.*;
import javax.microedition.io.*;
import javax.bluetooth.*;


public class Jsr082Locator extends NmeaLocator {
	private String btaddr;
	
	public void setParameter(String param) throws Exception {
		this.btaddr = param;
	}
	
	public void open() throws IOException {
		String url = "btspp://" + this.btaddr + ":1;authenticate=false;encrypt=false;master=true";
		this.conn = (StreamConnection) Connector.open(url, Connector.READ);
		this.is = conn.openInputStream();
	}
}


