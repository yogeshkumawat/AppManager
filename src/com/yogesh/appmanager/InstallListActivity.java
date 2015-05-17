package com.yogesh.appmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

public class InstallListActivity extends Activity {

	private Context mContext;
	private PackageManager mPackageManager;
	private List<NormalAppInfo> mAppList;
	private NormalAdapter mNormalAdapter;
	private GridView mGridView;
	private DbManager mDbManager;
	private List<InstallItem> Installeditems;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_install_list);
		initView();
		getApplicationList();
		
		insetIntoDB(Installeditems);
		mNormalAdapter = new NormalAdapter(mContext, mAppList);
		mGridView.setAdapter(mNormalAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.install_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		return super.onOptionsItemSelected(item);
	}
	
	private void initView() {
		mGridView = (GridView) findViewById(R.id.installedlist);
		mContext = this;
		mAppList = new ArrayList<NormalAppInfo>();
		Installeditems = new ArrayList<InstallItem>();
		mPackageManager = getPackageManager();
		mDbManager = new DbManager(mContext);
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
	    	item.setIcon(drawableToBitmap(ri.activityInfo.loadIcon(mPackageManager)));
	    	Installeditems.add(item);
	    	mAppList.add(app);
	    }
	}
	
	private void insetIntoDB(List<InstallItem> items) {
		mDbManager.insertInstallItemList(items);
	}
	
		public static Bitmap drawableToBitmap (Drawable drawable) {
		    if (drawable instanceof BitmapDrawable) {
		        return ((BitmapDrawable)drawable).getBitmap();
		    }

		    int width = drawable.getIntrinsicWidth();
		    width = width > 0 ? width : 1;
		    int height = drawable.getIntrinsicHeight();
		    height = height > 0 ? height : 1;

		    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		    Canvas canvas = new Canvas(bitmap);
		    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		    drawable.draw(canvas);

		    return bitmap;
	}
}
