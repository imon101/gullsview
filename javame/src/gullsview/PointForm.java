package gullsview;

import javax.microedition.lcdui.*;


public class PointForm extends Form implements ItemStateListener {
	private static final int STEPS = 25;
	
	private Main main;
	private TextField latitudeItem;
	private TextField longitudeItem;
	private TextField nameItem;
	private ColorItem colorItem;
	private Gauge colorGaugeItem;
	
	public PointForm(Main main, String title, String latitudeTitle, String longitudeTitle, String nameTitle){
		super(title);
		this.main = main;
		this.latitudeItem = new TextField(latitudeTitle, "", 50, TextField.ANY);
		this.longitudeItem = new TextField(longitudeTitle, "", 50, TextField.ANY);
		this.nameItem = new TextField(nameTitle, "", 50, TextField.ANY);
		this.append(this.latitudeItem);
		this.append(this.longitudeItem);
		this.append(this.nameItem);
		this.setItemStateListener(this);
	}
	
	public void appendColorItems(String colorTitle, String gaugeTitle, int borderColor){
		this.colorItem = new ColorItem(colorTitle, 0x000000, borderColor, this.getWidth());
		this.colorGaugeItem = new Gauge(gaugeTitle, true, STEPS, 0);
		this.append(this.colorItem);
		this.append(this.colorGaugeItem);
		this.setHue(0);
	}
	
	public void setLocation(String latitude, String longitude){
		this.latitudeItem.setString(latitude);
		this.longitudeItem.setString(longitude);
	}
	
	public String getLocationLatitude(){
		return this.latitudeItem.getString();
	}
	
	public String getLocationLongitude(){
		return this.longitudeItem.getString();
	}
	
	private void setColorHue(int hue){
		this.colorItem.setHSV(hue, 255, 255);
	}
	
	public void setHue(int hue){
		this.setColorHue(hue);
		this.colorGaugeItem.setValue(hue * STEPS / 255);
	}
	
	public int getHue(){
		return this.colorGaugeItem.getValue() * 255 / STEPS;
	}
	
	public int getColor(){
		return this.colorItem.hsvToRgb(this.getHue(), 255, 255);
	}
	
	public void itemStateChanged(Item item){
		if(item == colorGaugeItem){
			this.setColorHue(this.colorGaugeItem.getValue() * 255 / STEPS);
		}
	}
	
	public void setName(String name){
		this.nameItem.setString(name);
	}
	
	public String getName(){
		return this.nameItem.getString();
	}
}


