package com.history.update;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class SplashScreenActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private Context mContext;
    private PackageManager mPackageManager;
    private ArrayList<InstallItem> mInstallItem;
    private DbManager mDbManager;
    private ArrayList<NormalAppInfo> mAppList;
    private AdView mFooterAdView;
    private AdRequest mAdRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        init();
        initView();
        loadAds();
        new GetAppLicationTask().execute();
    }

    private void loadAds() {
        mFooterAdView.loadAd(mAdRequest);
    }

    private void init() {
        mContext = this;
        mPackageManager = getPackageManager();
        mInstallItem = new ArrayList<>();
        mDbManager = new DbManager(mContext);
        mAppList = new ArrayList<>();
        Fabric.with(this, new Crashlytics());
        MobileAds.initialize(this, getResources().getString(R.string.adMob_app_id));
        mAdRequest = new AdRequest.Builder()
                .addTestDevice("6306CDE98430C7B82650E6D9964D6084")
                //.addTestDevice("YOUR TEST DEVICE")
                .build();
    }

    private void initView() {
        mProgressBar = findViewById(R.id.progess_bar);
        mFooterAdView = findViewById(R.id.admob_footer);
    }

    private void getApplicationList() {
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableApps = mPackageManager.queryIntentActivities(i, 0);
        for(ResolveInfo ri : availableApps) {
            NormalAppInfo app = new NormalAppInfo();
            InstallItem item = new InstallItem();
            app.setLabel(ri.loadLabel(mPackageManager).toString());
            item.setLable(ri.loadLabel(mPackageManager).toString());

            app.setName(ri.activityInfo.packageName);
            item.setPackageName(ri.activityInfo.packageName);

            app.setDrawable(ri.activityInfo.loadIcon(mPackageManager));
            item.setIcon(Utils.drawableToBitmap(ri.activityInfo.loadIcon(mPackageManager)));
            mInstallItem.add(item);
            mAppList.add(app);
        }
    }

    class GetAppLicationTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            String result = null;
            getApplicationList();
            insetIntoDB(mInstallItem);
            Utils.setInstalledItemList(mAppList);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(mProgressBar != null)
                mProgressBar.setVisibility(View.GONE);
            startAppActivity();
            finish();
        }

    }

    private void insetIntoDB(List<InstallItem> items) {
        mDbManager.insertInstallItemList(items);
    }

    private void startAppActivity() {
        Intent i = new Intent(mContext, AppActivity.class);
        startActivity(i);
    }
}
