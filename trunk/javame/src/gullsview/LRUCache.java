package gullsview;

import java.util.*;


public abstract class LRUCache {
	private Object[] keys, values;
	private int[] ages;
	private Hashtable nulls;
	
	protected LRUCache(int size){
		this.keys = new Object[size];
		this.values = new Object[size];
		this.ages = new int[size];
		for(int i = 0; i < size; i++) this.ages[i] = 100;
		this.nulls = new Hashtable();
	}
	
	private void incAges(){
		int count = this.keys.length;
		for(int i = 0; i < count; i++) this.ages[i]++;
	}
	
	public Object get(Object key){
		if(this.nulls.containsKey(key)) return null;
		int count = this.keys.length;
		for(int i = 0; i < count; i++){
			if(key.equals(this.keys[i])){
				this.incAges();
				this.ages[i] = 0;
				return this.values[i];
			}
		}
		Object value = this.fetch(key);
		if(value == null){
			this.nulls.put(key, Boolean.TRUE);
			return null;
		}
		int oldestage = -1;
		int oldest = -1;
		for(int i = 0; i < count; i++){
			int age = this.ages[i];
			if(age > oldestage){
				oldestage = age;
				oldest = i;
			}
		}
		this.incAges();
		this.keys[oldest] = key;
		this.values[oldest] = value;
		this.ages[oldest] = 0;
		return value;
	}
	
	public abstract Object fetch(Object key);
	
	public void clear(){
		for(int i = 0; i < keys.length; i++){
			this.keys[i] = null;
			this.values[i] = null;
		}
	}
}


