package gullsview;

import java.io.*;
import java.util.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.lcdui.*;


public class FileSystemImpl implements FileSystem, CommandListener, Runnable {
	private Main main;
	private String mainPath;
	private Hashtable conns;
	private List list;
	private String path;
	private Command okCommand, backCommand;
	
	public FileSystemImpl(){
		this.conns = new Hashtable();
	}
	
	public void init(Main main){
		this.main = main;
	}
	
	public void setParameter(String param) throws Exception {
		this.mainPath = param;
	}
	
	public InputStream openInputStream(String path) throws IOException {
		String uri = "file:///" + this.mainPath + "/" + path;
		try {
			FileConnection fc = (FileConnection) Connector.open(uri, Connector.READ);
			if(fc == null) return null;
			if(!fc.exists()){
				fc.close();
				return null;
			}
			InputStream is = fc.openInputStream();
			this.conns.put(is, fc);
			return is;
		} catch (IOException e){
			throw new IOException("Cannot open FileConnection URI \"" + uri + "\": " + e.toString());
		}
	}
	
	public void closeInputStream(InputStream is) throws IOException {
		if(is == null) return;
		FileConnection fc = (FileConnection) this.conns.get(is);
		this.conns.remove(is);
		is.close();
		fc.close();
	}
	
	public boolean load(Persistable p, String path) throws Exception {
		String uri = "file:///" + this.mainPath + "/" + path;
		FileConnection fc = null;
		DataInputStream dis = null;
		try {
			fc = (FileConnection) Connector.open(uri, Connector.READ);
			if(!fc.exists()) return false;
			dis = fc.openDataInputStream();
			p.load(dis);
			return true;
		} finally {
			if(dis != null) dis.close();
			if(fc != null) fc.close();
		}
	}
	
	public void configure() throws Exception {
		this.list = new List("", List.IMPLICIT);
		this.okCommand = new Command(this.main.getResource("ok"), Command.OK, 1);
		this.backCommand = new Command(this.main.getResource("back"), Command.BACK, 2);
		this.list.addCommand(this.okCommand);
		this.list.addCommand(this.backCommand);
		this.list.setCommandListener(this);
		this.main.show(this.list);
		this.path = this.mainPath;
		if((this.path != null) && (this.path.trim()).length() == 0) this.path = null;
		this.main.call(this);
	}
	
	public void run(){
		try {
			this.fillList(this.list, this.path);
		} catch (Exception e){
			this.main.error("File system fillList() error", e);
		}
	}
	
	private void fillList(List list, String path) throws Exception {
		this.main.info("Selected path: " + this.path);
		String title = this.main.getResource("filesystem-config");
		if(this.path != null){
			int max = 20;
			int len = this.path.length();
			title = (len > max) ? "..." + this.path.substring(len - max, len) : this.path;
		}
		this.list.setTitle(title);
		list.deleteAll();
		if(path == null){
			Enumeration en = FileSystemRegistry.listRoots();
			while(en.hasMoreElements()){
				String root = (String) en.nextElement();
				if(root.endsWith("/")) root = root.substring(0, root.length() - 1);
				list.append(root, null);
			}
		} else {
			list.append(".. (" + this.main.getResource("level-up") + ")", null);
			String uri = "file:///" + path;
			FileConnection fc = (FileConnection) Connector.open(uri, Connector.READ);
			if(fc == null) return;
			Enumeration en = fc.list();
			while(en.hasMoreElements()){
				String name = (String) en.nextElement();
				if(name.endsWith("/"))
					list.append(name.substring(0, name.length() - 1), null);
			}
		}
	}
	
	public void commandAction(Command cmd, Displayable disp){
		if(cmd == List.SELECT_COMMAND){
			String segment = this.list.getString(this.list.getSelectedIndex());
			if(segment.startsWith(".. (")){
				int slash = this.path.lastIndexOf('/', this.path.length() - 2);
				this.path = (slash < 0) ? null : path.substring(0, slash + 1);
			} else {
				if(this.path == null){
					this.path = segment + "/";
				} else {
					this.path += segment + "/";
				}
			}
			this.main.call(this);
		} else if(cmd == this.okCommand){
			if((this.path == null) || !this.path.endsWith("_DATA/")){
				this.main.alert(this.main.getResource("filesystem-alert"));
			} else {
				this.mainPath = this.path;
				this.main.fileSystemConfigured(this.path);
			}
		} else if(cmd == this.backCommand){
			this.main.fileSystemConfigured(null);
		}
	}
}


