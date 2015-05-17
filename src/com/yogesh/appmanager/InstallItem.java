package com.yogesh.appmanager;

import android.graphics.Bitmap;

public class InstallItem {

		private String packageName;
		private String label;
		private int id;
		private Bitmap icon;

		public void setPackageName(String name) {
			packageName = name;
		}
		
		public void setLable(String label) {
			this.label = label;
		}
		
		public void setId(int id) {
			this.id = id;
		}
		
		public void setIcon(Bitmap bmp) {
			icon = bmp;
		}
		
		public String getPackageName() {
			return packageName;
		}
		
		public String getLabel() {
			return label;
		}
		
		public int getId() {
			return id;
		}
		
		public Bitmap getIcon() {
			return icon;
		}


}
