package gullsview;

import java.io.*;


public class Map {
	public String name;
	public String title;
	public String vendor;
	public String secchunk;
	public int scale;
	public int segment;
	public int xcount, ycount;
	public double defaultx, defaulty;
	public double locax, locay, locbx, locby, loccx, loccy;
	public double realax, realay, realbx, realby, realcx, realcy;
	
	public void load(DataInput in) throws IOException {
		this.name = in.readUTF();
		this.title = in.readUTF();
		this.vendor = in.readUTF();
		this.secchunk = in.readUTF();
		this.scale = in.readInt();
		this.segment = in.readInt();
		this.xcount = in.readInt();
		this.ycount = in.readInt();
		this.defaultx = in.readDouble();
		this.defaulty = in.readDouble();
		this.locax = in.readDouble();
		this.locay = in.readDouble();
		this.locbx = in.readDouble();
		this.locby = in.readDouble();
		this.loccx = in.readDouble();
		this.loccy = in.readDouble();
		this.realax = in.readDouble();
		this.realay = in.readDouble();
		this.realbx = in.readDouble();
		this.realby = in.readDouble();
		this.realcx = in.readDouble();
		this.realcy = in.readDouble();
	}
	
	public String toString(){
		return this.title + ((vendor.length() > 0) ? " (" + this.vendor + ")" : "");
	}
}


