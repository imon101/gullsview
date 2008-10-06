package gullsview;

import java.io.*;
import java.util.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;


public class FileSystemImpl implements FileSystem {
	private String mainPath;
	private Hashtable conns;
	
	public FileSystemImpl(){
		this.conns = new Hashtable();
this.mainPath = "c:/other/gullsview";
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
}


