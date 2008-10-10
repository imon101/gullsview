package gullsview;

import java.io.*;
import java.util.*;
import javax.microedition.lcdui.*;


public class MapList extends javax.microedition.lcdui.List implements Persistable {
	private Main main;
	private Vector maps;
	
	public MapList(Main main, String title){
		super(title, Choice.IMPLICIT);
		this.main = main;
		this.maps = new Vector();
	}
	
	public Map getMap(int id){
		return (Map) this.maps.elementAt(id);
	}
	
	public int getMapCount(){
		return this.maps.size();
	}
	
	public int getSelectedMap(){
		return this.getSelectedIndex();
	}
	
	public void load(DataInput in) throws IOException {
		int count = in.readInt();
		for(int i = 0; i < count; i++){
			Map map = new Map();
			map.id = this.maps.size();
			map.load(in);
			this.maps.addElement(map);
		}
	}
	
	public void save(DataOutput out){
		throw new RuntimeException("Unimplemented");
	}
	
	public void update(){
		this.deleteAll();
		int count = this.getMapCount();
		for(int i = 0; i < count; i++){
			Map map = this.getMap(i);
			this.append(map.toString(), null);
		}
	}
}


