package gullsview;

import java.io.*;
import javax.microedition.io.*;


public class Hge100Locator extends NmeaLocator {
	public void open() throws IOException {
		String url = "comm:AT5;baudrate=9600";
		this.conn = (CommConnection) Connector.open(url, Connector.READ_WRITE);
		this.is = conn.openInputStream();
		this.os = conn.openOutputStream();
	}
	
	public void start() throws Exception {
		this.send("STA");
		super.start();
	}
	
	public void stop() throws Exception {
		this.send("STO");
		super.stop();
	}
}


