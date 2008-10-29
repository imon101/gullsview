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
	public boolean mercator;
	public int segoffsetx, segoffsety;
	public double locax, locay, locbx, locby, loccx, loccy;
	public double realax, realay, realbx, realby, realcx, realcy;
	
	public String dataDir;
	public String dataFormat;
	public boolean dataIncluded;
	
	public void save(DataOutput out) throws IOException {
		out.writeUTF(this.name);
		out.writeUTF(this.title);
		out.writeUTF(this.vendor);
		out.writeUTF(this.secchunk);
		out.writeInt(this.scale);
		out.writeInt(this.segment);
		out.writeInt(this.xcount);
		out.writeInt(this.ycount);
		out.writeDouble(this.defaultx);
		out.writeDouble(this.defaulty);
		out.writeBoolean(this.mercator);
		out.writeInt(this.segoffsetx);
		out.writeInt(this.segoffsety);
		out.writeDouble(this.locax);
		out.writeDouble(this.locay);
		out.writeDouble(this.locbx);
		out.writeDouble(this.locby);
		out.writeDouble(this.loccx);
		out.writeDouble(this.loccy);
		out.writeDouble(this.realax);
		out.writeDouble(this.realay);
		out.writeDouble(this.realbx);
		out.writeDouble(this.realby);
		out.writeDouble(this.realcx);
		out.writeDouble(this.realcy);
	}
}


