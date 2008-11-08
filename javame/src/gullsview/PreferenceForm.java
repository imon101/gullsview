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
	private TextField twitterUserField;
	private TextField twitterPassField;
	
	public PreferenceForm(Main main){
		super(main.getResource("preferences"));
		this.main = main;
		this.empty = true;
	}
	
	public boolean isEmpty(){
		return this.empty;
	}
	
	public void appendLocatorTypeChoice(boolean jsr082, boolean jsr179, boolean bts, int locatorType){
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
	
	public void appendTwitterCredentials(String user, String pass){
		this.twitterUserField = new TextField(this.main.getResource("twitter-user"), (user != null) ? user : "", 50, TextField.ANY);
		this.append(this.twitterUserField);
		this.twitterPassField = new TextField(this.main.getResource("twitter-pass"), (pass != null) ? pass : "", 50, TextField.PASSWORD);
		this.append(this.twitterPassField);
		this.empty = false;
	}
	
	public String getTwitterUser(){
		return (this.twitterUserField != null) ? this.twitterUserField.getString() : null;
	}
	
	public String getTwitterPass(){
		return (this.twitterPassField != null) ? this.twitterPassField.getString() : null;
	}
	
	public void setTwitterCredentials(String user, String pass){
		this.twitterUserField.setString((user != null) ? user : "");
		this.twitterPassField.setString((pass != null) ? pass : "");
	}
}


