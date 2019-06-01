package com.history.update;

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
	private List<NormalAppInfo> mAppList;
	private ListView mListView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_uninstalled_list, container, false);
		mListView =  view.findViewById(R.id.uninstalledlist);
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
		mAppList = new ArrayList<>();
		mUninstallItems = new ArrayList<>();
		mDbManager = new DbManager(mContext);
		mUninstallItems = mDbManager.getUnInstallItems();
		for(UninstallItem mUninstallItem : mUninstallItems) {
			    
				NormalAppInfo mNormalAppInfo = new NormalAppInfo();
				mNormalAppInfo.setName(mUninstallItem.getPackageName());
				mNormalAppInfo.setId(mUninstallItem.getId());
				int installed_id = mDbManager.getInstalledIDfromName(mUninstallItem.getPackageName());
				InstallItem item = mDbManager.getInstallItem(installed_id);
				if (installed_id > 0) {
					mNormalAppInfo.setLabel(item.getLabel());

					Drawable d = new BitmapDrawable(getResources(), item.getIcon());
					mNormalAppInfo.setDrawable(d);

					mAppList.add(mNormalAppInfo);
				} else {
					mDbManager.deleteUnInstallRow(mUninstallItem.getId());
				}
				
			
		}
		AppAdapter mAdapter = new AppAdapter(mContext, mAppList,false);
		mListView.setAdapter(mAdapter);
	}

}
