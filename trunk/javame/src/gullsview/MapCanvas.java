package gullsview;

import java.io.*;
import javax.microedition.lcdui.*;


public abstract class MapCanvas extends Canvas {
	protected Main main;
	protected boolean fullscreen;
	protected boolean landscape;
	protected int width, height;
	protected int cx, cy;
	protected int maxWidth, maxHeight;
	protected int segment, xsegcount, ysegcount;
	protected int[] lineCoords;
	protected int[] lineColors;
	protected int[] pointCoords;
	protected String[] pointLabels;
	protected int targetx = -1;
	protected int targety = -1;
	protected String message;
	protected int map;
	protected boolean busy;
	private boolean painting;
	
	public void init(Main main){
		this.main = main;
		this.fullscreen = false;
		this.landscape = false;
		this.setFullScreenMode(true);
		this.maxWidth = this.getWidth();
		this.maxHeight = this.getHeight();
		this.setFullScreenMode(false);
		this.updateDim();
		if(this.maxWidth < this.getWidth()) this.maxWidth = this.getWidth();
		if(this.maxHeight < this.getHeight()) this.maxHeight = this.getHeight();
		this.busy = false;
		this.painting = false;
	}
	
	public abstract void dispose();
	
	public void setSegment(int segment, int xsegcount, int ysegcount){
		this.segment = segment;
		this.xsegcount = xsegcount;
		this.ysegcount = ysegcount;
	}
	
	public void setPosition(int cx, int cy){
		this.cx = cx;
		this.cy = cy;
		this.correctPosition();
		if(this.isShown()) this.render();
	}
	
	public int getPositionX(){
		return this.cx;
	}
	
	public int getPositionY(){
		return this.cy;
	}
	
	protected void correctPosition(){
		if(this.cx < 0) this.cx = 0;
		if(this.cx >= this.xsegcount * this.segment) this.cx = (this.xsegcount * this.segment) - 1;
		if(this.cy < 0) this.cy = 0;
		if(this.cy >= this.ysegcount * this.segment) this.cy = (this.ysegcount * this.segment) - 1;
	}
	
	public void setMap(int map){
		this.map = map;
	}
	
	public void setOverlay(int[] lineCoords, int[] lineColors, int[] pointCoords, String[] pointLabels){
		this.lineCoords = lineCoords;
		this.lineColors = lineColors;
		this.pointCoords = pointCoords;
		this.pointLabels = pointLabels;
	}
	
	public void setTarget(int x, int y){
		this.targetx = x;
		this.targety = y;
	}
	
	public void cancelTarget(){
		this.targetx = -1;
		this.targety = -1;
	}
	
	public void setMessage(String message){
		this.message = message;
		if(this.isShown()) this.repaint();
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public void setBusy(boolean on){
		if(this.painting) return;
		boolean prev = this.busy;
		this.busy = on;
		if(on && !prev){
			this.repaint();
			this.serviceRepaints();
		}
	}
	
	protected void keyPressed(int code){
		this.key(code, true);
	}
	
	protected void keyReleased(int code){
		this.key(code, false);
	}
	
	private void key(int code, boolean pressed){
		switch(code){
		case KEY_NUM2:
			this.scrollCommandRaw(0, -1, pressed);
			break;
		case KEY_NUM8:
			this.scrollCommandRaw(0, 1, pressed);
			break;
		case KEY_NUM4:
			this.scrollCommandRaw(-1, 0, pressed);
			break;
		case KEY_NUM6:
			this.scrollCommandRaw(1, 0, pressed);
			break;
		case KEY_NUM1:
			this.scrollCommandRaw(-1, -1, pressed);
			break;
		case KEY_NUM3:
			this.scrollCommandRaw(1, -1, pressed);
			break;
		case KEY_NUM7:
			this.scrollCommandRaw(-1, 1, pressed);
			break;
		case KEY_NUM9:
			this.scrollCommandRaw(1, 1, pressed);
			break;
		case KEY_STAR:
			if(!pressed) return;
			this.setFullscreen(!this.fullscreen);
			break;
		case KEY_POUND:
			if(!pressed) return;
			this.setLandscape(!this.landscape);
			break;
		default:
			int action = this.getGameAction(code);
			switch(action){
			case UP:
				this.scrollCommandRaw(0, -1, pressed);
				break;
			case DOWN:
				this.scrollCommandRaw(0, 1, pressed);
				break;
			case LEFT:
				this.scrollCommandRaw(-1, 0, pressed);
				break;
			case RIGHT:
				this.scrollCommandRaw(1, 0, pressed);
				break;
			case FIRE:
				if(pressed) this.main.addTracePoint();
				this.repaint();
				break;
			}
		}
	}
	
	public boolean isLandscape(){
		return this.landscape;
	}
	
	public void setLandscape(boolean on){
		this.landscape = on;
		this.updateDim();
		this.repaint();
	}
	
	public boolean isFullscreen(){
		return this.fullscreen;
	}
	
	public void setFullscreen(boolean on){
		this.fullscreen = on;
		this.setFullScreenMode(this.fullscreen);
		this.updateDim();
		this.repaint();
	}
	
	private void scrollCommandRaw(int dx, int dy, boolean pressed){
		this.scrollCommand(this.landscape ? dy : dx, this.landscape ? -dx : dy, pressed);
	}
	
	public void render(){
		this.prefetch();
		this.setBusy(false);
		this.repaint();
		this.serviceRepaints();
	}
	
	protected void updateDim(){
		this.width = this.landscape ? this.getHeight() : this.getWidth();
		this.height = this.landscape ? this.getWidth() : this.getHeight();
	}
	
	public void paint(Graphics g){
		this.painting = true;
		try {
			this.paint2(g);
		} finally {
			this.painting = false;
		}
	}
	
	protected int div(int a, int b){
		return a < 0 ? (a / b) - 1 : a / b;
	}
	
	protected int mod(int a, int b){
		return a < 0 ? (a % b) + b : a % b;
	}
	
	protected abstract void paint2(Graphics g);
	
	protected abstract void scrollCommand(int dx, int dy, boolean pressed);
	
	public abstract boolean scroll();
	
	public abstract void prefetch();
}


