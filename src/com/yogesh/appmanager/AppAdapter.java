package com.yogesh.appmanager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppAdapter extends BaseAdapter{

	private Context mContext;
	private List<NormalAppInfo> mAppList = new ArrayList<NormalAppInfo>();
	private LayoutInflater mInflater;
	private DbManager mDbManager;
	private boolean mIsUpdateAdapter;
	
	public AppAdapter(Context context, List<NormalAppInfo> appList, boolean mIsupdate) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mAppList = appList;
		mInflater = LayoutInflater.from(mContext);
		mDbManager = new DbManager(mContext);
		mIsUpdateAdapter = mIsupdate;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mAppList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mAppList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.app_item, null);
		}
		Log.v("yogesh", "Position: "+position);
		ImageView icon = (ImageView) convertView.findViewById(R.id.item_app_icon);
		TextView label = (TextView) convertView.findViewById(R.id.item_app_label);
		TextView tv_count = (TextView) convertView.findViewById(R.id.item_app_update_count);
		TextView tv_time = (TextView) convertView.findViewById(R.id.item_app_update_time);
		TextView uninstallORUpdateText = (TextView) convertView.findViewById(R.id.update_uninstall_tv);
		
		
		convertView.setTag(mAppList.get(position).getName());
		if(mIsUpdateAdapter) {
			icon.setImageDrawable(mAppList.get(position).getDrawable());
			
			label.setText(mAppList.get(position).getLabel());
			int count = mDbManager.getUpdateCount(mAppList.get(position).getId());
			Log.v("yogesh", "Count from db is "+count);
			String time = mDbManager.getUpdatedTime(mAppList.get(position).getId());
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			String date = df.format(Long.parseLong(time));
			tv_count.setText("Updated "+count+" times");
			tv_time.setText(date);
			
		}
		else {
			label.setText(mAppList.get(position).getLabel());
			icon.setImageDrawable(mAppList.get(position).getDrawable());
			uninstallORUpdateText.setText("Uninstalled");
			tv_count.setVisibility(View.GONE);
			String time = mDbManager.getUninstallTime(mAppList.get(position).getId());
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			String date = df.format(Long.parseLong(time));
			tv_time.setText(date);
		}
		
		
		return convertView;
	}

}
