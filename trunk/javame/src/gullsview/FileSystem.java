package gullsview;

import java.io.*;


public interface FileSystem {
	public InputStream openInputStream(String path) throws IOException;
	public void closeInputStream(InputStream is) throws IOException;
	public boolean load(Persistable p, String path) throws Exception;
	public void setParameter(String param) throws Exception;
}


