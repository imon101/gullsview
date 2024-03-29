package gullsview;

import java.util.*;


public abstract class Console {
	private ResourceBundle resources;
	
	public Console(){
		this.resources = ResourceBundle.getBundle("gullsview.Resources");
	}
	
	public String r(String id){
		if(id == null) throw new NullPointerException("resource id is null");
		try {
			return this.resources.getString(id);
		} catch (MissingResourceException e){
			return "!" + id;
		}
	}
	
	protected boolean retry(){
		return true;
	}
	
	public abstract String input(String id, String question, String def);
	
	public String inputString(String id, String resid, String def){
		if(resid == null) resid = id;
		String question = this.r(resid);
		String s = this.input(id, question, def);
		if(s == null) throw new RuntimeException("End of input");
		this.print();
		return s;
	}
	
	public int inputInt(String id, String resid, int def){
		try {
			String s = this.inputString(id, resid, String.valueOf(def));
			return s != null ? Integer.parseInt(s) : def;
		} catch (NumberFormatException e){
			this.errorRes("error-not-int");
			return this.retry() ? this.inputInt(id, resid, def) : def;
		}
	}
	
	public double inputDouble(String id, String resid, double def){
		try {
			String s = this.inputString(id, resid, String.valueOf(def));
			return s != null ? Double.parseDouble(s) : def;
		} catch (NumberFormatException e){
			this.errorRes("error-not-double");
			return this.retry() ? this.inputDouble(id, resid, def) : def;
		}
	}
	
	public boolean inputBoolean(String id, String resid, boolean def){
		String strue = this.r("yes");
		String sfalse = this.r("no");
		String sdef = def ? strue : sfalse;
		String s = this.inputString(id, resid, sdef);
		if(s == null) return def;
		if(strue.equalsIgnoreCase(s)){
			return true;
		} else if(sfalse.equalsIgnoreCase(s)){
			return false;
		} else {
			this.errorRes("error-not-boolean");
			return this.retry() ? this.inputBoolean(id, resid, def) : def;
		}
	}
	
	public abstract void print(String text, String color);
	
	public void print(){
		this.print("");
	}
	
	public void print(String text){
		this.print(text, null);
	}
	
	public void printRes(String resid, String color){
		this.print(this.r(resid), color);
	}
	
	public void printRes(String resid){
		this.printRes(resid, null);
	}
	
	public void printSeparator(){
		this.print("=-=-=-=-=-=-=-=-=-=-=-=-=");
	}
	
	public abstract void error(String message, Throwable t);
	
	public void error(String message){
		this.error(message, null);
	}
	
	public void errorRes(String resid, Throwable t){
		this.error(this.r(resid), t);
	}
	
	public void errorRes(String resid){
		this.errorRes(resid, null);
	}
	
	public void fatalError(String message){
		this.fatalError(message, null);
	}
	
	public void fatalError(String message, Throwable t){
		this.printSeparator();
		this.errorRes("error-before");
		this.error(message, t);
		this.errorRes("error-after");
		this.printSeparator();
	}
	
	public void fatalErrorRes(String res, Throwable t){
		this.fatalError(this.r(res), t);
	}
	
	public void close(){}
}


