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
	
	public void init(){
		this.sb = new StringBuffer();
	}
	
	public void setParameter(String param) throws Exception {
		this.btaddr = param;
	}
	
	public void start() throws Exception {
		if(this.thread != null) throw new Excpeption("Already started");
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
		} catch (InterruptedException e){
			// Noop
		} finally {
			this.thread = null;
			this.is.close();
			this.is = null;
			this.conn.close();
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
		} else {
			this.param(value);
		}
	}
	
	private void param(String value){
		
	}
	
	private void process(){
		
	}
}


