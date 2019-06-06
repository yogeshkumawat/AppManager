package com.history.update;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.ActionBar;
import android.app.ActionBar.TabListener;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class AppActivity extends FragmentActivity implements TabListener {

	private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    // Tab titles
    private String[] tabs = { "Installed", "Updated", "Uninstalled" };
    private InterstitialAd mInterstitialAd;
    private AdRequest mAdRequest;
    private int countToShowInterstitialAd = 0;
    private AdView mAdViewForExitDialog;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app);
		
		viewPager = findViewById(R.id.pager);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(3);

        mAdViewForExitDialog = (AdView) LayoutInflater.from(this).inflate(R.layout.adview, null);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabs.length; i++) {
            tabLayout.getTabAt(i).setText(tabs[i]);
        }
        startService(new Intent(this, AppReceiverService.class));

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        	 
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                countToShowInterstitialAd++;
                if(countToShowInterstitialAd == Utils.COUNT_TO_SHOW_FULL_AD) {
                    countToShowInterstitialAd = 0;
                    showFullAd();
                }
            }
 
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
 
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ad_id_interstitial));

        mAdRequest = new AdRequest.Builder()
                .addTestDevice("6306CDE98430C7B82650E6D9964D6084")
                .build();

        mAdViewForExitDialog.loadAd(mAdRequest);
    }

    private void showFullAd() {
        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(mAdRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.v("BOSS","Ad Fail To Load: "+i);
            }
        });
    }

    @Override
	protected void onResume() {
	    super.onResume();
	}

    @Override
    protected void onPause() {
	    super.onPause();
    }

    @Override
	protected void onStop() {
		super.onStop();
	}

    @Override
    protected void onDestroy() {
	    super.onDestroy();
    }

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition(), true);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showExitDialog();
    }

    private void showExitDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_custom_view);
        dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.text);
        text.setText("Exit From App?");
        final RelativeLayout dialogRl = dialog.findViewById(R.id.custom_dialog_container);
        dialogRl.addView(mAdViewForExitDialog, 0);
        dialog.setCanceledOnTouchOutside(false);

        Button dialogButton = dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialogRl.removeViewAt(0);
            }
        });

        Button dialogButtonCancel = dialog.findViewById(R.id.dialogButtonCancel);
        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                dialogRl.removeViewAt(0);
            }
        });

        dialog.show();
    }
}
