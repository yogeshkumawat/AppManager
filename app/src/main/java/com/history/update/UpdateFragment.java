package com.history.update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class UpdateFragment extends Fragment{

	private DbManager mDbManager;
	private Context mContext;
	private List<UpdateItem> mUpdateItems;
	private ApplicationInfo mApplicationInfo;
	private PackageManager mPackageManager;
	private List<NormalAppInfo> mAppList;
	private ListView mListView;
	private AdView mHeaderAdView, mFooterAdView;
	private AdRequest mAdRequest;
	private TextView tvNoData;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_updated_list, container, false);
		mListView = view.findViewById(R.id.updatelist);
		mHeaderAdView = view.findViewById(R.id.admob_header);
		mFooterAdView = view.findViewById(R.id.admob_footer);
		tvNoData = view.findViewById(R.id.no_data_tv);
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
		if (mUpdateItems != null && mUpdateItems.size() > 0) {
            tvNoData.setVisibility(View.GONE);
            for (UpdateItem mUpdateItem : mUpdateItems) {

                try {
                    mApplicationInfo = mPackageManager.getApplicationInfo(mUpdateItem.getPackageName(), 0);
                    if (mApplicationInfo != null && isCategoryLauncher(mApplicationInfo.packageName)) {
                        NormalAppInfo mNormalAppInfo = new NormalAppInfo();
                        mNormalAppInfo.setName(mUpdateItem.getPackageName());
                        mNormalAppInfo.setLabel(mPackageManager.getApplicationLabel(mApplicationInfo).toString());
                        mNormalAppInfo.setDrawable(mPackageManager.getApplicationIcon(mApplicationInfo));
                        mNormalAppInfo.setId(mUpdateItem.getId());
                        mAppList.add(mNormalAppInfo);

                    } else {
                        if (mApplicationInfo == null) {
                            mDbManager.deleteUpdateItemRow(mUpdateItem.getId());
                        }
                    }
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                    mDbManager.deleteUpdateItemRow(mUpdateItem.getId());
                }
            }

            Collections.sort(mAppList, new Comparator<NormalAppInfo>() {
                @Override
                public int compare(NormalAppInfo o1, NormalAppInfo o2) {
                    return o1.getLabel().compareToIgnoreCase(o2.getLabel());
                }
            });
            AppAdapter mAdapter = new AppAdapter(mContext, mAppList, true);
            mListView.setAdapter(mAdapter);
        }
        else {
		    tvNoData.setVisibility(View.VISIBLE);
        }
        mAdRequest = new AdRequest.Builder()
                .addTestDevice("6306CDE98430C7B82650E6D9964D6084")
                .build();
        loadAds();
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

    private void loadAds() {
        mFooterAdView.loadAd(mAdRequest);
        mHeaderAdView.loadAd(mAdRequest);
    }
}
