package com.yogesh.appmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class UpdatedListActivity extends Activity {

	private DbManager mDbManager;
	private Context mContext;
	private List<UpdateItem> mUpdateItems;
	private ApplicationInfo mApplicationInfo;
	private PackageManager mPackageManager;
	private List<NormalAppInfo> mAppList;
	private ListView mListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updated_list);
		mListView = (ListView) findViewById(R.id.updatelist);
		mContext = this;
		mPackageManager = getPackageManager();
		mAppList = new ArrayList<NormalAppInfo>();
		mUpdateItems = new ArrayList<UpdateItem>();
		mDbManager = new DbManager(mContext);
		mUpdateItems = mDbManager.getUpdateItems();
		Log.v("yogesh", "Updated item size: "+mUpdateItems.size());
		for(UpdateItem mUpdateItem : mUpdateItems) {
			
			try {
				mApplicationInfo = mPackageManager.getApplicationInfo(mUpdateItem.getPackageName(), 0);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(mApplicationInfo != null) {
				NormalAppInfo mNormalAppInfo = new NormalAppInfo();
				mNormalAppInfo.setName(mUpdateItem.getPackageName());
				mNormalAppInfo.setLabel(mPackageManager.getApplicationLabel(mApplicationInfo).toString());
				mNormalAppInfo.setDrawable(mPackageManager.getApplicationIcon(mApplicationInfo));
				mNormalAppInfo.setId(mUpdateItem.getId());
				mAppList.add(mNormalAppInfo);
				
			}
		}
		AppAdapter mAdapter = new AppAdapter(mContext, mAppList, true);
		mListView.setAdapter(mAdapter);
		mDbManager.exportDatabse("AppManager");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.updated_list, menu);
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
