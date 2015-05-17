package com.yogesh.appmanager;

public class UpdateItem {
	private String packageName;
	private long time;
	private int count;
	private int id;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setPackageName(String name) {
		packageName = name;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public void setCount(int c) {
		count = c;
	}
	public String getPackageName() {
		return packageName;
	}
	
	public long getTime() {
		return time;
	}
	
	public int getCount() {
		return count;
	}

}
