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

public class InstallFragment extends Fragment {
	
	private Context mContext;
	private PackageManager mPackageManager;
	private List<NormalAppInfo> mAppList;
	private NormalAdapter mNormalAdapter;
	private GridView mGridView;
	private DbManager mDbManager;
	private List<InstallItem> Installeditems;
	private ProgressDialog pDialog;
	private Handler mHandler;
	private static final int INFLATE_LIST = 1;
	private static final int DISMISS_LOADING_DIALOG = 2;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_install_list, container, false);
		mGridView = view.findViewById(R.id.installedlist);
		return view;
		
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.v("yogesh", "Installed: onStart");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Log.v("yogesh", "Fragment Installed on activityCreated");
		mHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {             
	            case INFLATE_LIST:
	                mNormalAdapter = new NormalAdapter(mContext, mAppList);
	                mGridView.setAdapter(mNormalAdapter);
	                mGridView.setOnItemClickListener(itemClick);
	                break;
	            case DISMISS_LOADING_DIALOG:
	            	if(pDialog != null && pDialog.isShowing())
	            		pDialog.dismiss();
	                break;
	            }
	            super.handleMessage(msg);
	        }
	    }; 
		initView();
		new GetAppLicationTask().execute();
		
	}
	
	private OnItemClickListener itemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			String packageName = (String) view.getTag();
			PackageManager pm = mContext.getPackageManager();
			Intent i = pm.getLaunchIntentForPackage(packageName);
			mContext.startActivity(i);
		}
	};
	
	private void initView() {
		
		mContext = getActivity();
		mAppList = new ArrayList<NormalAppInfo>();
		Installeditems = new ArrayList<InstallItem>();
		mPackageManager = mContext.getPackageManager();
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
	    	mHandler.sendEmptyMessage(INFLATE_LIST);
	    	if(mAppList.size() == 20)
	    		mHandler.sendEmptyMessage(DISMISS_LOADING_DIALOG);
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
		
		class GetAppLicationTask extends AsyncTask<Void, Void, String> {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				pDialog = new ProgressDialog(mContext);
				pDialog.setTitle("Please wait");
				pDialog.setMessage("loading apps...");
				pDialog.show();
			}
			
			@Override
			protected String doInBackground(Void... arg0) {
				// TODO Auto-generated method stub
				String result = null;
				getApplicationList();
				insetIntoDB(Installeditems);
				
				return result;
			}
			
			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if(pDialog != null && pDialog.isShowing())
            		pDialog.dismiss();
			}
			
		}
		
		
}
