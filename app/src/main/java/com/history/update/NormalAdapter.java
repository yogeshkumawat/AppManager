package com.history.update;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NormalAdapter extends BaseAdapter {

	private Context mContext;
	private List<NormalAppInfo> mAppList = new ArrayList<NormalAppInfo>();
	private LayoutInflater mInflater;
	
	public NormalAdapter(Context context, List<NormalAppInfo> appList) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mAppList = appList;
		mInflater = LayoutInflater.from(mContext);
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
			convertView = mInflater.inflate(R.layout.normal_app_item, null);
		}
		
		ImageView icon = (ImageView) convertView.findViewById(R.id.item_app_icon);
		TextView label = (TextView) convertView.findViewById(R.id.item_app_label);
		
		icon.setImageDrawable(mAppList.get(position).getDrawable());
		
		label.setText(mAppList.get(position).getLabel());
		convertView.setTag(mAppList.get(position).getName());
		
		return convertView;
	}

}
