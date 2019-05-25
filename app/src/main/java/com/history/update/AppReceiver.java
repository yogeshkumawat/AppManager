package com.history.update;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class AppReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.v("yogesh","Action: "+intent.getAction());
		DbManager mDbManager = new DbManager(context);
		switch(intent.getAction()) {
		
		case Intent.ACTION_PACKAGE_FULLY_REMOVED:
			Uri uri1 = intent.getData();
			String packageName = uri1.toString().split(":")[1];
			Log.v("yogesh", "package name: "+packageName);
			long currentTime = System.currentTimeMillis();
			UninstallItem mUninstallItem = new UninstallItem();
			mUninstallItem.setPackageName(packageName);
			mUninstallItem.setTime(currentTime);
			mDbManager.insertUninstallEntry(mUninstallItem);
			
			break;
		case Intent.ACTION_PACKAGE_REPLACED:
			boolean mIsAlreadyExist = false;
			Uri uri2 = intent.getData();
			String packageName2 = uri2.toString().split(":")[1];
			Log.v("yogesh", "package name: "+packageName2);
			long currentTime2 = System.currentTimeMillis();
			UpdateItem mUpdateItem = new UpdateItem();
			mUpdateItem.setPackageName(packageName2);
			mUpdateItem.setTime(currentTime2);
			List<UpdateItem> mUpdateItemsList = new ArrayList<UpdateItem>();
			mUpdateItemsList = mDbManager.getUpdateItems();
			if(mUpdateItemsList.size() > 0) {
				for(UpdateItem item : mUpdateItemsList) {
					if(item.getPackageName().equals(packageName2)) {
						Log.v("yogesh", "Alredy exist");
						mIsAlreadyExist = true;
						break;
					}
				}
			}
			if(mIsAlreadyExist) {
				int id = mDbManager.getUpdateIDfromName(mUpdateItem.getPackageName());
				Log.v("yogesh", "My db id is: "+id);
				int c = mDbManager.getUpdateCount(id);
				Log.v("yogesh", "My count is: "+c);
				int update_count = mDbManager.updateCount(c+1, id);
				int update = mDbManager.updateTimeofUpdateItem(String.valueOf(currentTime2), id);
				Log.v("yogesh", "count updation result: "+update+":"+update_count);
			}
			else {
				mUpdateItem.setCount(1);
				mDbManager.insertUpdateEntry(mUpdateItem);
			}
			break;
		}
	}

}
