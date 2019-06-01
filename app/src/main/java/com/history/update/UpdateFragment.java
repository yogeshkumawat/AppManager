package com.history.update;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class UpdateFragment extends Fragment{

	private DbManager mDbManager;
	private Context mContext;
	private List<UpdateItem> mUpdateItems;
	private ApplicationInfo mApplicationInfo;
	private PackageManager mPackageManager;
	private List<NormalAppInfo> mAppList;
	private ListView mListView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_updated_list, container, false);
		mListView = view.findViewById(R.id.updatelist);
		
		return view;
	}
	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		mPackageManager = mContext.getPackageManager();
		mAppList = new ArrayList<>();
		mUpdateItems = new ArrayList<>();
		mDbManager = new DbManager(mContext);
		mUpdateItems = mDbManager.getUpdateItems();
		for(UpdateItem mUpdateItem : mUpdateItems) {
			
			try {
				mApplicationInfo = mPackageManager.getApplicationInfo(mUpdateItem.getPackageName(), 0);
				if (mApplicationInfo != null && isCategoryLauncher(mApplicationInfo.packageName)) {
					NormalAppInfo mNormalAppInfo = new NormalAppInfo();
					mNormalAppInfo.setName(mUpdateItem.getPackageName());
					mNormalAppInfo.setLabel(mPackageManager.getApplicationLabel(mApplicationInfo).toString());
					mNormalAppInfo.setDrawable(mPackageManager.getApplicationIcon(mApplicationInfo));
					mNormalAppInfo.setId(mUpdateItem.getId());
					mAppList.add(mNormalAppInfo);

				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		AppAdapter mAdapter = new AppAdapter(mContext, mAppList, true);
		mListView.setAdapter(mAdapter);
	}

	private boolean isCategoryLauncher(String packageName) {
	    if (mPackageManager != null) {
	        Intent i = mPackageManager.getLaunchIntentForPackage(packageName);
	        if (i != null) {
	            Set<String> categories = i.getCategories();
	            if (categories != null && categories.size() > 0) {
	                return categories.contains(Intent.CATEGORY_LAUNCHER);
                }
            }
            //return if intent is null
            return false;
        }
        return true;
    }
}
