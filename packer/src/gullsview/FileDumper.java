package gullsview;

import java.io.*;


public interface FileDumper {
	public void next(String path) throws IOException;
	public void write(byte[] buffer, int offset, int length) throws IOException;
	public void close() throws IOException;
}


