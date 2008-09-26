package gullsview;

import javax.microedition.lcdui.*;
import javax.microedition.m3g.*;


public class TextureCache extends LRUCache {
	private MapCanvas mc;
	
	public TextureCache(int size, MapCanvas mc){
		super(size);
		this.mc = mc;
	}
	
	public Texture2D get(int map, int sx, int sy){
		if((sx < 0) || (sy < 0)) return null;
		return (Texture2D) this.get(new Long((((long) map) << 32) | (sx << 16) | sy));
	}
	
	public Object fetch(Object key){
		this.mc.setBusy(true);
		long l = ((Long) key).longValue();
		int map = (int)(l >> 32);
		int sx = ((int)(l >> 16)) & 0xffff;
		int sy = ((int) l) & 0xffff;
		Image image = this.mc.main.getSegment(map, sx, sy);
		if(image == null) return null;
		Image2D image2 = new Image2D(Image2D.RGB, image);
		Texture2D tex = new Texture2D(image2);
		tex.setBlending(Texture2D.FUNC_REPLACE);
		return tex;
	}
}


