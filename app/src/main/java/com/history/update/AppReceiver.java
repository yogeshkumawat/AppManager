package com.history.update;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

public class AppReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("BOSS","Action: "+intent.getAction());
		DbManager mDbManager = new DbManager(context);
        PackageManager packageManager = context.getPackageManager();
		Uri uri;
		String packageName;
		long currentTime;
		switch(intent.getAction()) {
			case Intent.ACTION_PACKAGE_ADDED:
			try {
			    uri = intent.getData();
			    packageName = uri.toString().split(":")[1];
                Log.v("BOSS", "package name: " + packageName);
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
                InstallItem installItem = new InstallItem();
                installItem.setPackageName(packageName);
                installItem.setLable(packageManager.getApplicationLabel(applicationInfo).toString());
                installItem.setIcon(Utils.drawableToBitmap(packageManager.getApplicationIcon(applicationInfo)));
                mDbManager.insertInstallItem(installItem);
                removeIfInUninstallItem(mDbManager, packageName);
            } catch (Exception e) {

            }

			break;
		case Intent.ACTION_PACKAGE_FULLY_REMOVED:
		    uri = intent.getData();
		    packageName = uri.toString().split(":")[1];
			Log.v("BOSS", "package name: "+packageName);
			currentTime = System.currentTimeMillis();
			UninstallItem mUninstallItem = new UninstallItem();
			mUninstallItem.setPackageName(packageName);
			mUninstallItem.setTime(currentTime);
			mDbManager.insertUninstallEntry(mUninstallItem);
			
			break;
		case Intent.ACTION_PACKAGE_REPLACED:
			boolean mIsAlreadyExist = false;
			uri = intent.getData();
			packageName = uri.toString().split(":")[1];
			Log.v("BOSS", "package name: "+packageName);
			currentTime = System.currentTimeMillis();
			UpdateItem mUpdateItem = new UpdateItem();
			mUpdateItem.setPackageName(packageName);
			mUpdateItem.setTime(currentTime);
			List<UpdateItem> mUpdateItemsList = new ArrayList<UpdateItem>();
			mUpdateItemsList = mDbManager.getUpdateItems();
			if(mUpdateItemsList.size() > 0) {
				for(UpdateItem item : mUpdateItemsList) {
					if(item.getPackageName().equals(packageName)) {
						mIsAlreadyExist = true;
						break;
					}
				}
			}
			if(mIsAlreadyExist) {
				int id = mDbManager.getUpdateIDfromName(mUpdateItem.getPackageName());
				int c = mDbManager.getUpdateCount(id);
				int update_count = mDbManager.updateCount(c+1, id);
				int update = mDbManager.updateTimeofUpdateItem(String.valueOf(currentTime), id);
			}
			else {
				mUpdateItem.setCount(1);
				mDbManager.insertUpdateEntry(mUpdateItem);
			}
			break;
		}
	}

    private void removeIfInUninstallItem(DbManager dbManager, String packageName) {
	    if (dbManager.isAlreadyInUnInstallDB(packageName)) {
	        int id = dbManager.getUnInstallIDFromName(packageName);
	        dbManager.deleteUnInstallRow(id);
        }
    }

}
