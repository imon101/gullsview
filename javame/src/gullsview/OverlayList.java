package gullsview;

import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;


public class OverlayList extends javax.microedition.lcdui.List implements Persistable {
	public static final int TYPE_PATH_START = 1;
	public static final int TYPE_PATH_CONT = 2;
	public static final int TYPE_POI = 3;
	
	private Main main;
	private Vector items;
	private Image pathStartImage, pathContImage, poiImage;
	
	public OverlayList(Main main, String title){
		super(title, Choice.IMPLICIT);
		this.main = main;
		this.items = new Vector();
		this.pathStartImage = this.main.getResImage("/path_start.png");
		this.pathContImage = this.main.getResImage("/path_cont.png");
		this.poiImage = this.main.getResImage("/poi_icon.png");
	}
	
	public int getItemCount(){
		return this.items.size();
	}
	
	public void clear(){
		this.items.removeAllElements();
	}
	
	public int getItemType(int index){
		return ((Integer)((this.getItem(index))[0])).intValue();
	}
	
	public String getItemName(int index){
		return (String)((this.getItem(index))[1]);
	}
	
	public String getItemLatitudeStr(int index){
		return (String)((this.getItem(index))[2]);
	}
	
	public String getItemLongitudeStr(int index){
		return (String)((this.getItem(index))[3]);
	}
	
	public double getItemLatitude(int index){
		return ((Double)((this.getItem(index))[4])).doubleValue();
	}
	
	public double getItemLongitude(int index){
		return ((Double)((this.getItem(index))[5])).doubleValue();
	}
	
	public int getItemHue(int index){
		return ((Integer)((this.getItem(index))[6])).intValue();
	}
	
	public int getItemColor(int index){
		return ((Integer)((this.getItem(index))[7])).intValue();
	}
	
	private Object[] getItem(int index){
		return (Object[]) this.items.elementAt(index);
	}
	
	public void saveItem(int index, boolean add, int type, String name, String latStr, String lonStr, double lat, double lon, int hue, int color){
		Object[] item = new Object[]{
			new Integer(type),
			name,
			latStr,
			lonStr,
			new Double(lat),
			new Double(lon),
			new Integer(hue),
			new Integer(color)
		};
		String message = (name == null) ? "" : name + " [" + latStr + " : " + lonStr + "]";
		Image image = null;
		switch(type){
		case TYPE_PATH_START: image = this.pathStartImage; break;
		case TYPE_PATH_CONT: image = this.pathContImage; break;
		case TYPE_POI: image = this.poiImage; break;
		}
		if(add){
			if((index < 0) || (index >= this.items.size())){
				this.append(message, image);
				this.items.addElement(item);
			} else {
				this.insert(index, message, image);
				this.items.insertElementAt(item, index);
			}
		} else {
			this.set(index, message, image);
			this.items.setElementAt(item, index);
		}
	}
	
	public void removeItem(int index){
		this.delete(index);
		this.items.removeElementAt(index);
	}
	
	public void save(DataOutput out) throws IOException {
		int count = getItemCount();
		out.writeInt(count);
		for(int i = 0; i < count; i++){
			out.writeInt(getItemType(i));
			out.writeUTF(getItemName(i));
			out.writeUTF(getItemLatitudeStr(i));
			out.writeUTF(getItemLongitudeStr(i));
			out.writeDouble(getItemLatitude(i));
			out.writeDouble(getItemLongitude(i));
			out.writeInt(getItemHue(i));
			out.writeInt(getItemColor(i));
		}
	}
	
	public void load(DataInput in) throws IOException {
		int count = in.readInt();
		this.clear();
		for(int i = 0; i < count; i++){
			int type = in.readInt();
			String name = in.readUTF();
			String latStr = in.readUTF();
			String lonStr = in.readUTF();
			double lat = in.readDouble();
			double lon = in.readDouble();
			int hue = in.readInt();
			int color = in.readInt();
			this.saveItem(-1, true, type, name, latStr, lonStr, lat, lon, hue, color);
		}
	}
	
	public double getPathLength(int index){
		if(this.getItemType(index) != TYPE_PATH_START) return Double.NaN;
		double plat = this.getItemLatitude(index);
		double plon = this.getItemLongitude(index);
		double ret = 0;
		int count = this.getItemCount();
		for(int i = index + 1; i < count; i++){
			int type = this.getItemType(i);
			if(type == TYPE_PATH_START) break;
			if(type == TYPE_POI) continue;
			double lat = this.getItemLatitude(i);
			double lon = this.getItemLongitude(i);
			ret += Map.distance(plat, plon, lat, lon);
			plat = lat;
			plon = lon;
		}
		return ret;
	}
}


