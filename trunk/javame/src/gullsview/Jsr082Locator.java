package gullsview;

import javax.bluetooth.*;


public class Jsr082Locator extends Locator {
	private String btaddr;
	
	public void init(){
		
	}
	
	public void setParameter(String param) throws Exception {
		this.btaddr = param;
	}
	
	public void start() throws Exception {}
	
	public void stop() throws Exception {}
}


