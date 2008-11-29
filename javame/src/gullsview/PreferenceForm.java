package gullsview;

import java.util.*;
import javax.microedition.lcdui.*;


public class PreferenceForm extends Form {
	public Command fileSystemConfigCommand;
	
	private Main main;
	private boolean empty;
	private ChoiceGroup locator;
	private Vector locatorTypes;
	private TextField locatorParamField;
	private TextField fileSystemParamField;
	private StringItem fileSystemConfigButton;
	private TextField mblogUserField;
	private TextField mblogPassField;
	
	public PreferenceForm(Main main){
		super(main.getResource("preferences"));
		this.main = main;
		this.empty = true;
	}
	
	public boolean isEmpty(){
		return this.empty;
	}
	
	public void appendLocatorTypeChoice(boolean jsr082, boolean jsr179, boolean bts, boolean hge100, int locatorType){
		this.locator = new ChoiceGroup(this.main.getResource("locator"), Choice.EXCLUSIVE);
		this.locatorTypes = new Vector();
		this.locator.append(this.main.getResource("locator-none"), null);
		this.locatorTypes.addElement(new Integer(Main.LOCATOR_NONE));
		if(jsr082){
			this.locator.append(this.main.getResource("locator-jsr082"), null);
			this.locatorTypes.addElement(new Integer(Main.LOCATOR_JSR082));
		}
		if(jsr179){
			this.locator.append(this.main.getResource("locator-jsr179"), null);
			this.locatorTypes.addElement(new Integer(Main.LOCATOR_JSR179));
		}
		if(bts){
			this.locator.append(this.main.getResource("locator-bts"), null);
			this.locatorTypes.addElement(new Integer(Main.LOCATOR_BTS));
		}
		if(hge100){
			this.locator.append(this.main.getResource("locator-hge100"), null);
			this.locatorTypes.addElement(new Integer(Main.LOCATOR_HGE100));
		}
		for(int i = 0; i < this.locatorTypes.size(); i++){
			if(((Integer) this.locatorTypes.elementAt(i)).intValue() == locatorType)
				this.locator.setSelectedIndex(i, true);
		}
		this.append(this.locator);
		this.empty = false;
	}
	
	public int getLocatorType(){
		if(this.locator == null) return Main.LOCATOR_NONE;
		return ((Integer) this.locatorTypes.elementAt(this.locator.getSelectedIndex())).intValue();
	}
	
	public void appendLocatorParam(String locatorParam){
		this.locatorParamField = new TextField(this.main.getResource("locator-param"), locatorParam != null ? locatorParam : "", 20, TextField.ANY);
		this.append(this.locatorParamField);
		this.empty = false;
	}
	
	public String getLocatorParam(){
		return (this.locatorParamField != null) ? this.locatorParamField.getString() : null;
	}
	
	public void appendFileSystemParam(String fileSystemParam){
		this.fileSystemParamField = new TextField(this.main.getResource("filesystem-param"), fileSystemParam != null ? fileSystemParam : "", 100, TextField.ANY);
		this.append(this.fileSystemParamField);
		this.fileSystemConfigButton = new StringItem(this.main.getResource("filesystem-config"), null, Item.BUTTON);
		this.fileSystemConfigCommand = new Command(this.main.getResource("filesystem-config"), Command.ITEM, 0);
		this.fileSystemConfigButton.addCommand(this.fileSystemConfigCommand);
		this.fileSystemConfigButton.setItemCommandListener(this.main);
		this.append(this.fileSystemConfigButton);
		this.empty = false;
	}
	
	public String getFileSystemParam(){
		return (this.fileSystemParamField != null) ? this.fileSystemParamField.getString() : null;
	}
	
	public void setFileSystemParam(String param){
		this.fileSystemParamField.setString(param);
	}
	
	public void appendMBlogCredentials(String user, String pass){
		this.mblogUserField = new TextField(this.main.getResource("mblog-user"), (user != null) ? user : "", 50, TextField.ANY);
		this.append(this.mblogUserField);
		this.mblogPassField = new TextField(this.main.getResource("mblog-pass"), (pass != null) ? pass : "", 50, TextField.PASSWORD);
		this.append(this.mblogPassField);
		this.empty = false;
	}
	
	public String getMBlogUser(){
		return (this.mblogUserField != null) ? this.mblogUserField.getString() : null;
	}
	
	public String getMBlogPass(){
		return (this.mblogPassField != null) ? this.mblogPassField.getString() : null;
	}
	
	public void setMBlogCredentials(String user, String pass){
		this.mblogUserField.setString((user != null) ? user : "");
		this.mblogPassField.setString((pass != null) ? pass : "");
	}
}


