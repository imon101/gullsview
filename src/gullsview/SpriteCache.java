package gullsview;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;


public class SpriteCache extends LRUCache {
	private MapCanvas mc;
	
	private class Key {
		public Map map;
		public int sx, sy;
		
		public Key(Map map, int sx, int sy){
			this.map = map;
			this.sx = sx;
			this.sy = sy;
		}
		
		public boolean equals(Object o){
			if((o == null) || !(o instanceof Key)) return false;
			Key k = (Key) o;
			return (k.map == this.map) && (k.sx == this.sx) && (k.sy == this.sy);
		}
		
		public int hashCode(){
			return this.sx | this.sy | this.map.hashCode();
		}
	}
	
	public SpriteCache(int size, MapCanvas mc){
		super(size);
		this.mc = mc;
	}
	
	public Sprite get(Map map, int sx, int sy){
		return (Sprite) this.get(new Key(map, sx, sy));
	}
	
	public Object fetch(Object key){
		this.mc.setBusy(true);
		Key k = (Key) key;
		Image image = this.mc.main.getSegment(k.map, k.sx, k.sy);
		if(image == null) return null;
		return new Sprite(image);
	}
}


