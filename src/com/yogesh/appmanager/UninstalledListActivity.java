package com.yogesh.appmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class UninstalledListActivity extends Activity {

	private DbManager mDbManager;
	private Context mContext;
	private List<UninstallItem> mUninstallItems;
	private ApplicationInfo mApplicationInfo;
	private PackageManager mPackageManager;
	private List<NormalAppInfo> mAppList;
	private ListView mListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uninstalled_list);
		mListView = (ListView) findViewById(R.id.uninstalledlist);
		mContext = this;
		mPackageManager = getPackageManager();
		mAppList = new ArrayList<NormalAppInfo>();
		mUninstallItems = new ArrayList<UninstallItem>();
		mDbManager = new DbManager(mContext);
		mUninstallItems = mDbManager.getUnInstallItems();
		for(UninstallItem mUninstallItem : mUninstallItems) {
			    
				NormalAppInfo mNormalAppInfo = new NormalAppInfo();
				mNormalAppInfo.setName(mUninstallItem.getPackageName());
				mNormalAppInfo.setId(mUninstallItem.getId());
				int installed_id = mDbManager.getInstalledIDfromName(mUninstallItem.getPackageName());
				InstallItem item = mDbManager.getInstallItem(installed_id);
				mNormalAppInfo.setLabel(item.getLabel());
				
				Drawable d = new BitmapDrawable(getResources(), item.getIcon());
			    mNormalAppInfo.setDrawable(d);
			    Log.v("yogesh", "Uninsall info name: "+mNormalAppInfo.getName());
			    Log.v("yogesh", "Uninsall info name: "+mNormalAppInfo.getId());
			    Log.v("yogesh", "Uninsall info name: "+mNormalAppInfo.getLabel());
			    
				mAppList.add(mNormalAppInfo);
				
			
		}
		AppAdapter mAdapter = new AppAdapter(mContext, mAppList,false);
		mListView.setAdapter(mAdapter);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.uninstalled_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		return super.onOptionsItemSelected(item);
	}
}
