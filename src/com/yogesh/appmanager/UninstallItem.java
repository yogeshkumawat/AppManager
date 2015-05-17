package com.yogesh.appmanager;

public class UninstallItem {
	
	private String packageName;
	private long time;
	private int id;

	public void setPackageName(String name) {
		packageName = name;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public long getTime() {
		return time;
	}
	
	public int getId() {
		return id;
	}
}
