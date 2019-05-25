package com.history.update;

import android.graphics.drawable.Drawable;

public class NormalAppInfo {
	private String name;
	private String label;
	private Drawable icon;
	private int id;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public String getLabel() {
		return label;
	}
	public Drawable getDrawable() {
		return icon;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public void setDrawable(Drawable icon) {
		this.icon = icon;
	}

}
