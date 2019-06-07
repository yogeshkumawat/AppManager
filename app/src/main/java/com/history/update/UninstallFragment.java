package com.history.update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class UninstallFragment extends Fragment {

    private DbManager mDbManager;
    private Context mContext;
    private List<UninstallItem> mUninstallItems;
    private List<NormalAppInfo> mAppList;
    private ListView mListView;
    private AdView mHeaderAdView, mFooterAdView;
    private AdRequest mAdRequest;
    private TextView tvNoData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_uninstalled_list, container, false);
        mListView = view.findViewById(R.id.uninstalledlist);
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
        mAppList = new ArrayList<>();
        mUninstallItems = new ArrayList<>();
        mDbManager = new DbManager(mContext);
        mUninstallItems = mDbManager.getUnInstallItems();
        if (mUninstallItems != null && mUninstallItems.size() > 0) {
            tvNoData.setVisibility(View.GONE);
            for (UninstallItem mUninstallItem : mUninstallItems) {

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

            Collections.sort(mAppList, new Comparator<NormalAppInfo>() {
                @Override
                public int compare(NormalAppInfo o1, NormalAppInfo o2) {
                    return o1.getLabel().compareToIgnoreCase(o2.getLabel());
                }
            });
            AppAdapter mAdapter = new AppAdapter(mContext, mAppList, false);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String packageName = (String) view.getTag();
                    if (!TextUtils.isEmpty(packageName)) {
                        openPlayStoreLink(packageName);
                    }
                }
            });
            mListView.setAdapter(mAdapter);
        } else {
            tvNoData.setVisibility(View.VISIBLE);
        }
        mAdRequest = new AdRequest.Builder()
                .addTestDevice("6306CDE98430C7B82650E6D9964D6084")
                .build();
        loadAds();
    }

    private void openPlayStoreLink(String appPackageName) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void loadAds() {
        mFooterAdView.loadAd(mAdRequest);
        mHeaderAdView.loadAd(mAdRequest);
    }

}
