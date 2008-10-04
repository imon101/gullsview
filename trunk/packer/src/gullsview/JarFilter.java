package gullsview;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.util.jar.*;


public class JarFilter {
	private InputStream in;
	private OutputStream out;
	private Filter filter;
	
	public interface Filter {
		public boolean processEntry(String name);
		public void processManifest(java.util.Map map);
	}
	
	public JarFilter(InputStream in, OutputStream out, Filter filter){
		this.in = in;
		this.out = out;
		this.filter = filter;
	}
	
	public java.util.Map run() throws IOException {
		JarInputStream jis = new JarInputStream(this.in);
		Manifest mf = jis.getManifest();
		Attributes atts = mf.getMainAttributes();
		java.util.Map matts = new HashMap();
		for(Object key : atts.keySet()) matts.put(((Attributes.Name) key).toString(), atts.get(key));
		this.filter.processManifest(matts);
		ZipOutputStream zos = new ZipOutputStream(this.out);
		zos.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
		writeManifest(matts, zos);
		JarEntry entry;
		byte[] buffer = new byte[1024];
		int count;
		while((entry = jis.getNextJarEntry()) != null){
			String name = entry.getName();
			if(!this.filter.processEntry(name)) continue;
			JarEntry newEntry = new JarEntry(name);
			zos.putNextEntry(newEntry);
			while((count = jis.read(buffer, 0, buffer.length)) >= 0) zos.write(buffer, 0, count);
		}
		zos.flush();
		zos.close();
		jis.close();
		return matts;
	}
	
	public static void writeManifest(java.util.Map map, OutputStream out) throws IOException {
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
		for(Object key : map.keySet()){
			pw.print((String) key);
			pw.print(": ");
			pw.print((String) map.get(key));
			pw.print("\r\n");
		}
		pw.flush();
	}
}


