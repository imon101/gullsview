package gullsview;

import javax.microedition.lcdui.*;


public class ColorItem extends CustomItem {
	private int color;
	private int borderColor;
	private int width;
	
	public ColorItem(String title, int color, int borderColor, int width){
		super(title);
		this.color = color;
		this.borderColor = borderColor;
		this.width = width;
	}
	
	public void paint(Graphics g, int w, int h){
		g.setColor(this.color);
		g.fillRect(0, 0, w, h);
		g.setColor(this.borderColor);
		g.drawRect(0, 0, w - 1, h - 1);
	}
	
	private void hsvToRgb(double h, double s, double v, double[] rgb){
		if(s == 0){
			rgb[0] = rgb[1] = rgb[2] = v;
			return;
		}
		double vh = h * 6;
		double vi = Math.floor(vh);
		double v1 = v * (1 - s);
		double v2 = v * (1 - s * (vh - vi));
		double v3 = v * (1 - s * (1 - (vh - vi)));
		switch((int) vi){
		case 0: rgb[0] = v; rgb[1] = v3; rgb[2] = v1; break;
		case 1: rgb[0] = v2; rgb[1] = v; rgb[2] = v1; break;
		case 2: rgb[0] = v1; rgb[1] = v; rgb[2] = v3; break;
		case 3: rgb[0] = v1; rgb[1] = v2; rgb[2] = v; break;
		case 4: rgb[0] = v3; rgb[1] = v1 ; rgb[2] = v; break;
		default: rgb[0] = v; rgb[1] = v1 ; rgb[2] = v2;
		} 
	}
	
	private int colord2i(double r, double g, double b){
		int ret = ((int)(r * 0xff)) << 16;
		ret |= ((int)(g * 0xff)) << 8;
		ret |= (int)(b * 0xff);
		return ret;
	}
	
	private static final double[] tmprgb = new double[3];
	
	public synchronized int hsvToRgb(int h, int s, int v){
		this.hsvToRgb(h / 255d, s / 255d, v / 255d, tmprgb);
		return this.colord2i(tmprgb[0], tmprgb[1], tmprgb[2]);
	}
	
	public void setHSV(int h, int s, int v){
		this.color = this.hsvToRgb(h, s, v);
		this.repaint();
	}
	
	public int getPrefContentWidth(int height){
		return this.width < this.getMinContentWidth() ? this.getMinContentWidth() : this.width;
	}
	
	public int getPrefContentHeight(int width){
		return this.getMinContentHeight();
	}
	
	public int getMinContentWidth(){
		return 50;
	}
	
	public int getMinContentHeight(){
		return 30;
	}
}


