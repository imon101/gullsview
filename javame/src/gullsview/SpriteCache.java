package gullsview;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;


public class SpriteCache extends LRUCache {
	private MapCanvas mc;
	
	public SpriteCache(int size, MapCanvas mc){
		super(size);
		this.mc = mc;
	}
	
	public Sprite get(int map, int sx, int sy){
		if((sx < 0) || (sy < 0)) return null;
		return (Sprite) this.get(new Long((((long) map) << 32) | (sx << 16) | sy));
	}
	
	public Object fetch(Object key){
		this.mc.setBusy(true);
		long l = ((Long) key).longValue();
		int map = (int)(l >> 32);
		int sx = ((int)(l >> 16)) & 0xffff;
		int sy = ((int) l) & 0xffff;
		Image image = this.mc.main.getSegment(map, sx, sy);
		if(image == null) return null;
		return new Sprite(image);
	}
}


