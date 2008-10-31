package gullsview;

import java.io.*;


public class Map {
	private static final int[] iout = new int[2];
	private static final double[] dout = new double[2];
	
	public int id;
	
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
		this.mercator = in.readBoolean();
		this.segoffsetx = in.readInt();
		this.segoffsety = in.readInt();
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
	
	public synchronized boolean insideGlobal(double lon, double lat){
		this.toLocal(lon, lat, iout);
		return this.insideLocal(iout[0], iout[1]);
	}
	
	public boolean insideLocal(int x, int y){
		return (x >= 0) && (x < (this.xcount * this.segment)) && (y >= 0) && (y < (this.ycount * this.segment));
	}
	
	public void toLocal(double lon, double lat, int[] out){
		if(this.mercator){
			this.mercatorToLocal(lon, lat, out);
		} else {
			this.bilinearToLocal(lon, lat, out);
		}
	}
	
	public void toGlobal(int x, int y, double[] out){
		if(this.mercator){
			this.mercatorToGlobal(x, y, out);
		} else {
			this.bilinearToGlobal(x, y, out);
		}
	}
	
	private static double arcgd(double fi){
		return PoorMath.log(Math.tan((Math.PI / 4) + (fi / 2)));
	}
	
	private static double gd(double d){
		return (PoorMath.atan(PoorMath.exp(d)) - (Math.PI / 4)) * 2;
	}
	
	private void mercatorToLocal(double lon, double lat, int[] out){
		final double latmax = 85.0511287798066;
		if(lat < -latmax) lat = -latmax;
		if(lat > latmax) lat = latmax;
		double nx = lon / 180;
		double ny = arcgd(Math.toRadians(lat)) / Math.PI;
		int n = 1 << this.scale;
		double x = n * ((nx + 1) / 2);
		double y = n * ((1 - ny) / 2);
		out[0] = (int)((x - this.segoffsetx) * this.segment);
		out[1] = (int)((y - this.segoffsety) * this.segment);
	}
	
	private void mercatorToGlobal(int x, int y, double[] out){
		double _x = (x / (double) this.segment) + this.segoffsetx;
		double _y = (y / (double) this.segment) + this.segoffsety;;
		int n = 1 << this.scale;
		double nx = _x * 2 / n - 1;
		double ny = 1 - (_y * 2 / n);
		double lon = nx * 180;
		double lat = Math.toDegrees(gd(ny * Math.PI));
		out[0] = lon;
		out[1] = lat;
	}
	
	public synchronized void bilinearToLocal(double x, double y, int[] out){
		bilinear(x, y, dout,
			this.realax, this.realay, this.realbx, this.realby, this.realcx, this.realcy,
			this.locax, this.locay, this.locbx, this.locby, this.loccx, this.loccy
		);
		out[0] = (int) dout[0];
		out[1] = (int) dout[1];
	}
	
	public void bilinearToGlobal(int x, int y, double[] out){
		bilinear(x, y, out,
			this.locax, this.locay, this.locbx, this.locby, this.loccx, this.loccy,
			this.realax, this.realay, this.realbx, this.realby, this.realcx, this.realcy
		);
	}
	
	private static void bilinear(
		double inx, double iny, double[] out,
		double inax, double inay, double inbx, double inby, double incx, double incy,
		double outax, double outay, double outbx, double outby, double outcx, double outcy
	){
		double adx = inx - inax;
		double ady = iny - inay;
		double abx = inbx - inax;
		double aby = inby - inay;
		double acx = incx - inax;
		double acy = incy - inay;
		double t = ((abx * ady) - (aby * adx)) / ((acy * abx) - (acx * aby));
		double s = ((acx * ady) - (acy * adx)) / ((aby * acx) - (abx * acy));
		double x = outax + ((outbx - outax) * s) + ((outcx - outax) * t);
		double y = outay + ((outby - outay) * s) + ((outcy - outay) * t);
		out[0] = x;
		out[1] = y;
	}
	
	public String toString(){
		return this.title + ":" + this.scale + ((vendor.length() > 0) ? " (" + this.vendor + ")" : "");
	}
}


