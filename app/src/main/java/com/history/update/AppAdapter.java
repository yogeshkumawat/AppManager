package com.history.update;

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
	private List<NormalAppInfo> mAppList;
	private LayoutInflater mInflater;
	private DbManager mDbManager;
	private boolean mIsUpdateAdapter;
	
	public AppAdapter(Context context, List<NormalAppInfo> appList, boolean mIsupdate) {
		mContext = context;
		mAppList = appList;
		mInflater = LayoutInflater.from(mContext);
		mDbManager = new DbManager(mContext);
		mIsUpdateAdapter = mIsupdate;
	}
	@Override
	public int getCount() {
		return mAppList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mAppList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.app_item, null);
		}
		ImageView icon =  convertView.findViewById(R.id.item_app_icon);
		TextView label =  convertView.findViewById(R.id.item_app_label);
		TextView tv_count =  convertView.findViewById(R.id.item_app_update_count);
		TextView tv_time =  convertView.findViewById(R.id.item_app_update_time);
		TextView uninstallORUpdateText =  convertView.findViewById(R.id.update_uninstall_tv);
		
		
		convertView.setTag(mAppList.get(position).getName());
		if(mIsUpdateAdapter) {
			icon.setImageDrawable(mAppList.get(position).getDrawable());
			
			label.setText(mAppList.get(position).getLabel());
			int count = mDbManager.getUpdateCount(mAppList.get(position).getId());
			String time = mDbManager.getUpdatedTime(mAppList.get(position).getId());
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			String date = df.format(Long.parseLong(time));
			tv_count.setText("(Updated "+count+" times)");
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
