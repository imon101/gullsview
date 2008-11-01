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
	public double deflat, deflon;
	public boolean mercator;
	public int segoffsetx, segoffsety;
	public double bax, bay, bbx, bby, bcx, bcy;
	public double balat, balon, bblat, bblon, bclat, bclon;
	
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
		out.writeDouble(this.deflat);
		out.writeDouble(this.deflon);
		out.writeBoolean(this.mercator);
		out.writeInt(this.segoffsetx);
		out.writeInt(this.segoffsety);
		out.writeDouble(this.bax);
		out.writeDouble(this.bay);
		out.writeDouble(this.bbx);
		out.writeDouble(this.bby);
		out.writeDouble(this.bcx);
		out.writeDouble(this.bcy);
		out.writeDouble(this.balat);
		out.writeDouble(this.balon);
		out.writeDouble(this.bblat);
		out.writeDouble(this.bblon);
		out.writeDouble(this.bclat);
		out.writeDouble(this.bclon);
	}
}


