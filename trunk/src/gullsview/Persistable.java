package gullsview;

import java.io.*;


public interface Persistable {
	public void load(DataInput in) throws IOException;
	public void save(DataOutput out) throws IOException;
}


