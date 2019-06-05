package com.history.update;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class InstallFragment extends Fragment {
	
	private Context mContext;
	private NormalAdapter mNormalAdapter;
	private GridView mGridView;
	private AdView mHeaderAdView, mFooterAdView;
	private AdRequest mAdRequest;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_install_list, container, false);
		mGridView = view.findViewById(R.id.installedlist);
		mHeaderAdView = view.findViewById(R.id.admob_header);
		mFooterAdView = view.findViewById(R.id.admob_footer);
		return view;
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
		mNormalAdapter = new NormalAdapter(mContext, Utils.getInstalledItemList());
		mGridView.setAdapter(mNormalAdapter);
		mGridView.setOnItemClickListener(itemClick);

	}
	
	private OnItemClickListener itemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String packageName = (String) view.getTag();
			PackageManager pm = mContext.getPackageManager();
			Intent i = pm.getLaunchIntentForPackage(packageName);
			mContext.startActivity(i);
		}
	};
	
	private void init() {
		mContext = getActivity();
        mAdRequest = new AdRequest.Builder()
                .addTestDevice("6306CDE98430C7B82650E6D9964D6084")
                .build();
        loadAds();
	}

    private void loadAds() {
        mFooterAdView.loadAd(mAdRequest);
        mHeaderAdView.loadAd(mAdRequest);
    }
}
