package gullsview;

import java.io.*;


public class Map {
	private final int[] tmpxy = new int[2];
	private final double[] dout = new double[2];
	
	public int id;
	
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
	
	public void load(DataInput in) throws IOException {
		this.name = in.readUTF();
		this.title = in.readUTF();
		this.vendor = in.readUTF();
		this.secchunk = in.readUTF();
		this.scale = in.readInt();
		this.segment = in.readInt();
		this.xcount = in.readInt();
		this.ycount = in.readInt();
		this.deflat = in.readDouble();
		this.deflon = in.readDouble();
		this.mercator = in.readBoolean();
		this.segoffsetx = in.readInt();
		this.segoffsety = in.readInt();
		this.bax = in.readDouble();
		this.bay = in.readDouble();
		this.bbx = in.readDouble();
		this.bby = in.readDouble();
		this.bcx = in.readDouble();
		this.bcy = in.readDouble();
		this.balat = in.readDouble();
		this.balon = in.readDouble();
		this.bblat = in.readDouble();
		this.bblon = in.readDouble();
		this.bclat = in.readDouble();
		this.bclon = in.readDouble();
	}
	
	public boolean insideGlobal(double lat, double lon){
		synchronized(this.tmpxy){
			this.toLocal(lat, lon, tmpxy);
			return this.insideLocal(tmpxy[0], tmpxy[1]);
		}
	}
	
	public boolean insideLocal(int x, int y){
		return (x >= 0) && (x < (this.xcount * this.segment)) && (y >= 0) && (y < (this.ycount * this.segment));
	}
	
	public void toLocal(double lat, double lon, int[] xy){
		if(this.mercator){
			this.mercatorToLocal(lat, lon, xy);
		} else {
			this.bilinearToLocal(lat, lon, xy);
		}
	}
	
	public void toGlobal(int x, int y, double[] latlon){
		if(this.mercator){
			this.mercatorToGlobal(x, y, latlon);
		} else {
			this.bilinearToGlobal(x, y, latlon);
		}
	}
	
	private static double arcgd(double fi){
		return PoorMath.log(Math.tan((Math.PI / 4) + (fi / 2)));
	}
	
	private static double gd(double d){
		return (PoorMath.atan(PoorMath.exp(d)) - (Math.PI / 4)) * 2;
	}
	
	private void mercatorToLocal(double lat, double lon, int[] xy){
		final double latmax = 85.0511287798066;
		if(lat < -latmax) lat = -latmax;
		if(lat > latmax) lat = latmax;
		double nx = lon / 180;
		double ny = arcgd(Math.toRadians(lat)) / Math.PI;
		int n = 1 << this.scale;
		double x = n * ((nx + 1) / 2);
		double y = n * ((1 - ny) / 2);
		xy[0] = (int)((x - this.segoffsetx) * this.segment);
		xy[1] = (int)((y - this.segoffsety) * this.segment);
	}
	
	private void mercatorToGlobal(int x, int y, double[] latlon){
		double _x = (x / (double) this.segment) + this.segoffsetx;
		double _y = (y / (double) this.segment) + this.segoffsety;;
		int n = 1 << this.scale;
		double nx = _x * 2 / n - 1;
		double ny = 1 - (_y * 2 / n);
		double lon = nx * 180;
		double lat = Math.toDegrees(gd(ny * Math.PI));
		latlon[0] = lat;
		latlon[1] = lon;
	}
	
	private void bilinearToLocal(double x, double y, int[] latlon){
		synchronized(this.dout){
			bilinear(x, y, dout,
				this.balat, this.balon, this.bblat, this.bblon, this.bclat, this.bclon,
				this.bax, this.bay, this.bbx, this.bby, this.bcx, this.bcy
			);
			latlon[0] = (int) dout[0];
			latlon[1] = (int) dout[1];
		}
	}
	
	private void bilinearToGlobal(int x, int y, double[] latlon){
		bilinear(x, y, latlon,
			this.bax, this.bay, this.bbx, this.bby, this.bcx, this.bcy,
			this.balat, this.balon, this.bblat, this.bblon, this.bclat, this.bclon
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


