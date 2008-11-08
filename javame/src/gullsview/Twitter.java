package gullsview;

import java.io.*;
import javax.microedition.io.*;


public class Twitter {
	private static final String BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	private static final String HEX = "0123456789ABCDEF";
	
	private String user, pass;
	
	public void setCredentials(String user, String pass){
		this.user = user;
		this.pass = pass;
	}
	
	private boolean isEmpty(String str){
		return (str == null) || (str.trim()).length() == 0;
	}
	
	public boolean areCredentialsSet(){
		return !this.isEmpty(this.user) && !this.isEmpty(this.pass);
	}
	
	public static String urlEncode(byte[] bytes){
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < bytes.length; i++){
			int b = bytes[i] & 0xff;
			if(b == ' '){
				sb.append('+');
			} else if(((b >= '0') && (b <= '9')) || ((b >= 'a') && (b <= 'z')) || ((b >= 'A') && (b <= 'Z'))){
				sb.append((char) b);
			} else {
				sb.append('%');
				sb.append(HEX.charAt(b >> 4));
				sb.append(HEX.charAt(b & 0xf));
			}
		}
		return sb.toString();
	}
	
	private static String base64Encode(byte[] in){
		StringBuffer sb = new StringBuffer();
		int acc = 0;
		int stack = 0;
		int count = in.length;
		for(int i = 0; i < count; i++){
			int b = in[i] & 0xff;
			acc <<= 8;
			acc |= b;
			stack += 8;
			while(stack > 6){
				int index = (acc >> (stack - 6)) & 0x3f;
				sb.append(BASE64_CHARS.charAt(index));
				stack -= 6;
			}
		}
		if(stack > 0) sb.append(BASE64_CHARS.charAt((acc << (6 - stack)) & 0x3f));
		if(sb.length() % 4 != 0){
			int padding = 4 - (sb.length() % 4);
			for(int i = 0; i < padding; i++) sb.append('=');
		}
		return sb.toString();
	}
	
	public void send(String message) throws Exception {
		if(!this.areCredentialsSet()) return;
		String data = "status=" + urlEncode(message.getBytes("UTF-8"));
		HttpConnection conn = null;
		OutputStream os = null;
		InputStream is = null;
		try {
			conn = (HttpConnection) Connector.open("http://twitter.com/statuses/update.json");
			conn.setRequestMethod(HttpConnection.POST);
			String token = base64Encode((this.user + ":" + this.pass).getBytes("UTF-8"));
			conn.setRequestProperty("Authorization", "Basic " + token);
			os = conn.openOutputStream();
			os.write(data.getBytes("UTF-8"));
			os.flush();
			os.close();
			os = null;
			int code = conn.getResponseCode();
			if((code != 200) && (code != 302))
				throw new Exception("Unexpected response code " + code + ": " + conn.getResponseMessage());
			is = conn.openInputStream();
			this.pump(is, System.out, 1024);
			System.out.println();
		} finally {
			if(os != null) os.close();
			if(is != null) is.close();
			if(conn != null) conn.close();
		}
	}
	
	private void pump(InputStream in, OutputStream out, int size) throws IOException {
		byte[] buffer = new byte[size];
		int count;
		while((count = in.read(buffer, 0, size)) >= 0)
			out.write(buffer, 0, count);
		out.flush();
	}
}


