package gullsview;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;


public class MidpMapCanvas extends MapCanvas {
	private int scrollx, scrolly;
	private SpriteCache cache;
	private Sprite busySprite, pointerSprite, poiSprite;
	private int rshiftx, rshifty;
	private Image bgImage;
	private Font font;
	
	public void init(Main main){
		super.init(main);
		this.busySprite = new Sprite(this.main.getResImage("/busy.png"));
		this.pointerSprite= new Sprite(this.main.getResImage("/pointer.png"));
		this.poiSprite = new Sprite(this.main.getResImage("/poi.png"));
		this.bgImage = this.main.getResImage("/bg.jpg");
		this.font = Font.getDefaultFont();
	}
	
	public void dispose(){
		this.cache.clear();
	}
	
	public void setSegment(int segment, int xsegcount, int ysegcount){
		super.setSegment(segment, xsegcount, ysegcount);
		int count = ((this.maxWidth / this.segment) + 2) * ((this.maxHeight / this.segment) + 2);
		this.cache = new SpriteCache(count, this);
	}
	
	protected void scrollCommand(int dx, int dy, boolean pressed){
		int orgscrollx = this.scrollx;
		int orgscrolly = this.scrolly;
		if(pressed){
			this.scrollx += dx;
			this.scrolly += dy;
		} else {
			this.scrollx -= dx;
			this.scrolly -= dy;
		}
		if(this.scrollx < -1) this.scrollx = -1;
		if(this.scrollx > 1) this.scrollx = 1;
		if(this.scrolly < -1) this.scrolly = -1;
		if(this.scrolly > 1) this.scrolly = 1;
		if(((this.scrollx != 0) || (this.scrolly != 0)) && ((orgscrollx == 0) && (orgscrolly == 0)))
			this.main.startScroll();
	}
	
	public void prefetch(){
		this.processSegments(null);
	}
	
	public boolean scroll(){
		int step = 20;
		int shiftx = this.scrollx * step;
		int shifty = this.scrolly * step;
		int coef = 6;
		rshiftx = ((rshiftx * coef) + shiftx) / (coef + 1);
		rshifty = ((rshifty * coef) + shifty) / (coef + 1);
		this.cx += rshiftx;
		this.cy += rshifty;
		this.correctPosition();
		return (rshiftx != 0) || (rshifty != 0);
	}
	
	protected void paint2(Graphics g){
		if(this.busy){
			this.drawSprite(g, this.busySprite, 10, 10);
			return;
		}
		
		int hw = this.width / 2;
		int hh = this.height / 2;
		
		this.processSegments(g);
		
		this.drawRidge(
			g,
			hw - this.cx - 7,
			hh - this.cy - 7,
			this.xsegcount * this.segment + 10,
			this.ysegcount * this.segment + 10,
			0xeeb457,
			0xb23a00,
			0x471700
		);
		
		if((this.targetx >= 0) && (this.targety != 0)){
			g.setColor(0x000000);
			this.drawLine(g, hw, hh, this.targetx - this.cx + hw, this.targety - this.cy + hh);
			g.setColor(0xaaaa00);
			g.setStrokeStyle(Graphics.DOTTED);
			this.drawLine(g, hw, hh, this.targetx - this.cx + hw, this.targety - this.cy + hh);
			g.setStrokeStyle(Graphics.SOLID);
		}
		
		this.drawOverlay(g);
		
		this.drawSprite(g, this.pointerSprite, hw, hh);
		
		this.drawHLabel(g, 3, 3, this.message, false);
	}
	
	private void processSegments(Graphics g){
		int ystart = this.cy - (this.height / 2);
		int ystartseg = this.div(ystart, this.segment);
		int yendseg = this.div(ystart + this.height - 1, this.segment);
		int yshift = - this.mod(ystart, this.segment);
		
		int xstart = this.cx - (this.width / 2);
		int xstartseg = this.div(xstart, this.segment);
		int xendseg = this.div(xstart + this.width - 1, this.segment);
		int xshift = - this.mod(xstart, this.segment);
		
		int y = yshift;
		for(int yseg = ystartseg; yseg <= yendseg; yseg++){
			int x = xshift;
			for(int xseg = xstartseg; xseg <= xendseg; xseg++){
				Sprite sprite = this.cache.get(this.map, xseg, yseg);
				if(g != null) this.drawSprite(g, sprite, x, y);
				x += this.segment;
			}
			y += this.segment;
		}
	}
	
	private void drawSprite(Graphics g, Sprite s, int x, int y){
		int rx, ry;
		if(this.landscape){
			rx = this.getWidth() - y;
			ry = x;
		} else {
			rx = x;
			ry = y;
		}
		if(s == null){
			g.setColor(0x001144);
			if(this.landscape) rx = rx - this.segment;
			// g.fillRect(rx, ry, this.segment, this.segment);
			g.drawImage(this.bgImage, rx, ry, 0);
		} else {
			s.setTransform(this.landscape ? Sprite.TRANS_ROT90 : Sprite.TRANS_NONE);
			s.setPosition(rx - (this.landscape ? s.getHeight() : 0), ry);
			s.paint(g);
		}
	}
	
	private void drawRidge(Graphics g, int x, int y, int width, int height, int light, int normal, int dark){
		this.draw3DRect(g, x, y, width, height, light, dark);
		this.draw3DRect(g, x + 1, y + 1, width - 2, height - 2, light, dark);
		this.draw3DRect(g, x + 2, y + 2, width - 4, height - 4, normal, normal);
		this.draw3DRect(g, x + 3, y + 3, width - 6, height - 6, normal, normal);
		this.draw3DRect(g, x + 4, y + 4, width - 8, height - 8, normal, normal);
		this.draw3DRect(g, x + 5, y + 5, width - 10, height - 10, dark, light);
		this.draw3DRect(g, x + 6, y + 6, width - 12, height - 12, dark, light);
	}
	
	private void draw3DRect(Graphics g, int x, int y, int width, int height, int ltcolor, int rbcolor){
		g.setColor(ltcolor);
		this.drawLine(g, x, y, x + width, y);
		this.drawLine(g, x, y, x, y + height);
		g.setColor(rbcolor);
		this.drawLine(g, x + width, y, x + width, y + height);
		this.drawLine(g, x, y + height, x + width, y + height);
	}
	
	private void drawWideLine(Graphics g, int x1, int y1, int x2, int y2, int width){
		if(width == 0) return;
		if(width == 1){
			g.drawLine(x1, y1, x2, y2);
			return;
		}
		int deltax = x2 - x1;
		int deltay = y2 - y1;
		int len = (int) Math.sqrt((deltax * deltax) + (deltay * deltay));
		if(len == 0) return;
		int mx = -(deltay * width) / (2 * len);
		int my = (deltax * width) / (2 * len);
		int ax = x1 + mx;
		int ay = y1 + my;
		int bx = x1 - mx;
		int by = y1 - my;
		int cx = x2 + mx;
		int cy = y2 + my;
		int dx = x2 - mx;
		int dy = y2 - my;
		g.fillTriangle(ax, ay, bx, by, cx, cy);
		g.fillTriangle(bx, by, cx, cy, dx, dy);
	}
	
	private void drawLine(Graphics g, int x1, int y1, int x2, int y2){
		this.drawLine(g, x1, y1, x2, y2, 1);
	}
	
	private void drawLine(Graphics g, int x1, int y1, int x2, int y2, int width){
		if(this.landscape){
			int w = this.getWidth();
			this.drawWideLine(g, w - y1, x1, w - y2, x2, width);
		} else {
			this.drawWideLine(g, x1, y1, x2, y2, width);
		}
	}
	
	private void drawHLabel(Graphics g, int x, int y, String text, boolean bottom){
		if((text == null) || ((text.trim()).length() == 0)) return;
		int fh = this.font.getHeight();
		int sw = this.font.stringWidth(text);
		int vgap = 3;
		int hgap = 6;
		int tw = sw + (2 * hgap);
		int th = fh + (2 * vgap);
		if(bottom) y -= th;
		g.setFont(this.font);
		g.setColor(0xcc3300);
		g.fillRect(x, y, tw, th);
		g.setColor(0xffffff);
		g.fillRect(x + 2, y + 2, tw - 4, th - 4);
		g.setColor(0xcc3300);
		g.drawString(text, x + hgap, y + vgap, Graphics.TOP | Graphics.LEFT);
	}
	
	private void drawLabel(Graphics g, int x, int y, String text){
		if(this.landscape){
			this.drawHLabel(g, this.getWidth() - y, x, text, true);
		} else {
			this.drawHLabel(g, x, y, text, true);
		}
	}
	
	private void mapToScreen(int x, int y, int[] out){
		out[0] = x - this.cx + (this.width / 2);
		out[1] = y - this.cy + (this.height / 2);
	}
	
	private void drawOverlay(Graphics g){
		int[] tmp1 = new int[2];
		int[] tmp2 = new int[2];
		if((this.lineCoords != null) && (this.lineColors != null)){
			int count = this.lineColors.length;
			for(int i = 0; i < count; i++){
				g.setColor(this.lineColors[i]);
				int offset = i * 4;
				this.mapToScreen(this.lineCoords[offset], this.lineCoords[offset + 1], tmp1);
				this.mapToScreen(this.lineCoords[offset + 2], this.lineCoords[offset + 3], tmp2);
				this.drawLine(g, tmp1[0], tmp1[1], tmp2[0], tmp2[1], 6);
			}
		}
		int poihw = this.poiSprite.getWidth() / 2;
		int poihh = this.poiSprite.getHeight() / 2;
		if((this.pointCoords != null) && (this.pointLabels != null)){
			int count = this.pointLabels.length;
			for(int i = 0; i < count; i++){
				int offset = i * 2;
				this.mapToScreen(this.pointCoords[offset], this.pointCoords[offset + 1], tmp1);
				String label = this.pointLabels[i];
				if((label != null) && ((label.trim()).length() > 0))
					this.drawLabel(g, tmp1[0], tmp1[1], label);
				this.drawSprite(g, this.poiSprite, tmp1[0] - poihw, tmp1[1] - poihh);
			}
		}
	}
}


