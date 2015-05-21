package com.yogesh.appmanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
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
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_updated_list, container, false);
		mListView = (ListView) view.findViewById(R.id.updatelist);
		
		return view;
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.v("yogesh", "update: onStart");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Log.v("yogesh", "update: activity created");
		mContext = getActivity();
		mPackageManager = mContext.getPackageManager();
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
		//mDbManager.exportDatabse("AppManager");
	}
}
