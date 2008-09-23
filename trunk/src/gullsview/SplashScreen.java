package gullsview;

import java.io.*;
import javax.microedition.lcdui.*;


public class SplashScreen extends Canvas {
	private Image image;
	
	public SplashScreen() throws IOException {
		this.image = Image.createImage((this.getClass()).getResourceAsStream("/splash.png"));
	}
	
	public void paint(Graphics g){
		int w = this.getWidth();
		int h = this.getHeight();
		g.setColor(0xffffff);
		g.fillRect(0, 0, w, h);
		g.drawImage(this.image, w / 2, h / 2, Graphics.VCENTER | Graphics.HCENTER);
	}
}


