package com.yogesh.appmanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class UninstallFragment extends Fragment{
	
	private DbManager mDbManager;
	private Context mContext;
	private List<UninstallItem> mUninstallItems;
	private ApplicationInfo mApplicationInfo;
	private PackageManager mPackageManager;
	private List<NormalAppInfo> mAppList;
	private ListView mListView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_uninstalled_list, container, false);
		mListView = (ListView) view.findViewById(R.id.uninstalledlist);
		return view;
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.v("yogesh", "Uninstall: onStart");
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Log.v("yogesh", "UnInstalled: activity created");
		mContext = getActivity();
		mPackageManager = mContext.getPackageManager();
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

}
