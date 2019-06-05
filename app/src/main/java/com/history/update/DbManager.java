package com.history.update;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

public class DbManager extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 2;
	 
    // Database Name
    private static final String DATABASE_NAME = "AppManager";
 
    // tables name
    private static final String TABLE_UNINSTALL = "uninstallTable";
    private static final String TABLE_INSTALLED = "installTable";
    private static final String TABLE_UPDATE= "updatelTable";
 
    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_PACKAGE_NAME = "name";
    private static final String KEY_COUNT = "count";
    private static final String KEY_TIME = "time";
    private static final String KEY_LABEL = "label";
    private static final String KEY_ICON = "icon";
    
	public DbManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_UPDATE_TABLE = "CREATE TABLE " + TABLE_UPDATE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_PACKAGE_NAME + " TEXT,"
                + KEY_TIME + " TEXT," +KEY_COUNT +" INTEGER"+")";
		
		String CREATE_UNINSTALL_TABLE = "CREATE TABLE " + TABLE_UNINSTALL + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_PACKAGE_NAME + " TEXT,"
                + KEY_TIME + " TEXT" +")";
		
		String CREATE_INSTALL_TABLE = "CREATE TABLE " + TABLE_INSTALLED + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_PACKAGE_NAME + " TEXT,"
                + KEY_LABEL + " TEXT," + KEY_ICON +" BLOB"+")";
        db.execSQL(CREATE_UPDATE_TABLE);
        db.execSQL(CREATE_UNINSTALL_TABLE);
        db.execSQL(CREATE_INSTALL_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_UPDATE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_UNINSTALL);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTALLED);        // Create tables again
        onCreate(db);
	}
	
	public void insertUninstallEntry(UninstallItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (isAlreadyInUnInstallDB(item.getPackageName())) {
            //Remove older entry
            int id = getUnInstallIDFromName(item.getPackageName());
            deleteUnInstallRow(id);
        }
        ContentValues values = new ContentValues();
        values.put(KEY_PACKAGE_NAME, item.getPackageName());
        values.put(KEY_TIME, item.getTime());

        // Inserting Row
        db.insert(TABLE_UNINSTALL, null, values);
        //db.close();

    }
	public void insertUpdateEntry(UpdateItem item) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_PACKAGE_NAME, item.getPackageName());
	    values.put(KEY_TIME, item.getTime());
	    values.put(KEY_COUNT, item.getCount());
	    // Inserting Row
	    db.insert(TABLE_UPDATE, null, values);
	    db.close();
	}
	
	public void insertInstallItemList(List<InstallItem> mInstalledList) {
		SQLiteDatabase db = this.getWritableDatabase();
		for (InstallItem mItem : mInstalledList) {
            removeIfInUninstallItem(mItem.getPackageName());
			if (!mIsPackgeAlreadyInstall(mItem.getPackageName())) {
				ContentValues values = new ContentValues();
				values.put(KEY_PACKAGE_NAME, mItem.getPackageName());
				values.put(KEY_LABEL, mItem.getLabel());

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				mItem.getIcon().compress(CompressFormat.PNG, 0, outputStream);
				values.put(KEY_ICON, outputStream.toByteArray());

				db.insert(TABLE_INSTALLED, null, values);

			}
		}
	}

	private void removeIfInUninstallItem(String packageName) {
		if (isAlreadyInUnInstallDB(packageName)) {
			int id = getUnInstallIDFromName(packageName);
			deleteUnInstallRow(id);
		}
	}

	public void insertInstallItem(InstallItem mInstallItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (!mIsPackgeAlreadyInstall(mInstallItem.getPackageName())) {
            ContentValues values = new ContentValues();
            values.put(KEY_PACKAGE_NAME, mInstallItem.getPackageName());
            values.put(KEY_LABEL, mInstallItem.getLabel());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            mInstallItem.getIcon().compress(CompressFormat.PNG, 0, outputStream);
            values.put(KEY_ICON, outputStream.toByteArray());

            db.insert(TABLE_INSTALLED, null, values);
        }
    }
	private boolean mIsPackgeAlreadyInstall(String packagename) {
		int id = -1;
		id = getInstalledIDfromName(packagename);
		if(id > 0)
			return true;
		else
			return false;
	}

	public boolean isAlreadyInUnInstallDB(String packageName) {
	    int id = -1;
	    id = getUnInstallIDFromName(packageName);
	    if (id > 0)
	        return true;
	    else
	        return false;
    }

    public int getUnInstallIDFromName(String packageName) {
        int id = -1;
        String selectQuery = "SELECT "+KEY_ID+" FROM " + TABLE_UNINSTALL +" WHERE "+KEY_PACKAGE_NAME+" LIKE \""+packageName+"\"";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if(cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public InstallItem getInstallItem(int id) {
		InstallItem item = new InstallItem();
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_INSTALLED, new String[] { KEY_ID,
				KEY_PACKAGE_NAME, KEY_LABEL, KEY_ICON }, KEY_ID + " = ? ",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			int item_id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
			String packageName = cursor.getString(cursor
					.getColumnIndex(KEY_PACKAGE_NAME));
			String label = cursor.getString(cursor.getColumnIndex(KEY_LABEL));
			byte[] byteData = cursor.getBlob(cursor.getColumnIndex(KEY_ICON));
			Bitmap image = BitmapFactory.decodeByteArray(byteData, 0,
					byteData.length);

			item.setId(item_id);
			item.setPackageName(packageName);
			item.setLable(label);
			item.setIcon(image);
		}
		//cursor.close();
		return item;
	}
	
	public int getInstalledIDfromName(String name) {
		int id = -1;
		String selectQuery = "SELECT "+KEY_ID+" FROM " + TABLE_INSTALLED +" WHERE "+KEY_PACKAGE_NAME+" LIKE \""+name+"\"";
		SQLiteDatabase db = this.getWritableDatabase();
	    try {
			Cursor cursor = db.rawQuery(selectQuery, null);
			if(cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return id;
	}
	
	public int getUpdateIDfromName(String name) {
		int id = -1;
		String selectQuery = "SELECT "+KEY_ID+" FROM " + TABLE_UPDATE +" WHERE "+KEY_PACKAGE_NAME+" LIKE \""+name+"\"";
		SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	    if(cursor != null && cursor.getCount() > 0) {
	    	cursor.moveToFirst();
	    	id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
	    }
	    return id;
	}
	public List<UpdateItem> getUpdateItems() {
		List<UpdateItem> items = new ArrayList<UpdateItem>();
		String selectQuery = "SELECT  * FROM " + TABLE_UPDATE;
		 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
	        do {
	            UpdateItem item = new UpdateItem();
	            item.setPackageName(cursor.getString(cursor.getColumnIndex(KEY_PACKAGE_NAME)));
	            item.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
	            String time = cursor.getString(cursor.getColumnIndex(KEY_TIME));
	            item.setTime(Long.parseLong(time));
	            
	            item.setCount(cursor.getInt(cursor.getColumnIndex(KEY_COUNT)));
	            
	            items.add(item);
	        } while (cursor.moveToNext());
	    }
	    //if (cursor != null)
	        //cursor.close();
	    // return list
	    return items;
	}
	
	public List<UninstallItem> getUnInstallItems() {
		List<UninstallItem> items = new ArrayList<UninstallItem>();
		String selectQuery = "SELECT  * FROM " + TABLE_UNINSTALL;
		 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	            UninstallItem item = new UninstallItem();
	            item.setPackageName(cursor.getString(cursor.getColumnIndex(KEY_PACKAGE_NAME)));
	            item.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
	            String time = cursor.getString(cursor.getColumnIndex(KEY_TIME));
	            item.setTime(Long.parseLong(time));
	            
	            items.add(item);
	        } while (cursor.moveToNext());
	    }
	 
	    //cursor.close();
	    // return list
	    return items;
	}
	/*public void updateTimeofUninstallItem(String time, UninstallItem item) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_PACKAGE_NAME, contact.getName());
	    values.put(KEY_, contact.getPhoneNumber());
	 
	    // updating row
	    return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
	            new String[] { String.valueOf(contact.getID()) });
	}*/
	public int updateTimeofUpdateItem(String time, int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_TIME, time);
	 
	    // updating row
	    return db.update(TABLE_UPDATE, values, KEY_ID + " = ?",
	            new String[] { String.valueOf(id) });
	}
	public int updateCount(int count, int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_COUNT, count);
	    // updating row
	    return db.update(TABLE_UPDATE, values, KEY_ID + " = ? ",
	            new String[] { String.valueOf(id) });
	}
	public int getUpdateCount(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		 
        Cursor cursor = db.query(TABLE_UPDATE, new String[] { KEY_COUNT }, KEY_ID + " = ? ",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        int count = cursor.getInt(cursor.getColumnIndex(KEY_COUNT));
        //cursor.close();
		return count;
		
	}
	public String getUpdatedTime(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		 
        Cursor cursor = db.query(TABLE_UPDATE, new String[] { KEY_TIME }, KEY_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        String time = cursor.getString(cursor.getColumnIndex(KEY_TIME));
        //cursor.close();
		return time;
		
	}
	
	public String getUninstallTime(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		 
        Cursor cursor = db.query(TABLE_UNINSTALL, new String[] { KEY_TIME }, KEY_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            String time = cursor.getString(cursor.getColumnIndex(KEY_TIME));
            //cursor.close();
            return time;
        }
        return "-1";
	}
	
	public void deleteUnInstallRow(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_UNINSTALL, KEY_ID + " = ?",
	            new String[] { String.valueOf(id) });
	    //db.close();
	}

    public void deleteUpdateItemRow(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_UPDATE, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        //db.close();
    }
	
	public void exportDatabse(String databaseName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + "com.history.update" + "//databases//"
                        + databaseName + "";
                String backupDBPath = "backupname.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileInputStream fis = new FileInputStream(currentDB);
                    FileChannel src = fis.getChannel();
                    FileOutputStream fos = new FileOutputStream(backupDB);
                    FileChannel dst = fos.getChannel();
                    dst.transferFrom(src, 0, src.size());
                    Log.v("BOSS", "database copied");
                    src.close();
                    dst.close();
                    fis.close();
                    fos.close();
                }
            }
        } catch (Exception e) {

        }
    }

}
