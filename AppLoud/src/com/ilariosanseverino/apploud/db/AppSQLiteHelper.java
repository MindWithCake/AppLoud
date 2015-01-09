package com.ilariosanseverino.apploud.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.AudioManager;
import android.util.Pair;

import com.ilariosanseverino.apploud.UI.AppListItem;
import com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry;

public class AppSQLiteHelper extends SQLiteOpenHelper {
	
	private static final String SQL_CREATE = "create table "+AppEntry.TABLE_NAME+
		" (" +AppEntry._ID + " INTEGER PRIMARY KEY," +
		AppEntry.COLUMN_NAME_APPNAME+AppEntry.COLUMN_TYPE_APPNAME+","+
		AppEntry.COLUMN_NAME_PACKAGE+AppEntry.COLUMN_TYPE_PACKAGE+","+
		AppEntry.COLUMN_NAME_MUSIC_STREAM+AppEntry.COLUMN_TYPE_MUSIC_STREAM+","+
		AppEntry.COLUMN_NAME_NOTIFICATION_STREAM
			+AppEntry.COLUMN_TYPE_NOTIFICATION_STREAM+","+
		AppEntry.COLUMN_NAME_RING_STREAM+AppEntry.COLUMN_TYPE_RING_STREAM+","+
		AppEntry.COLUMN_NAME_SYSTEM_STREAM+AppEntry.COLUMN_TYPE_RING_STREAM+","+
		" UNIQUE("+AppEntry.COLUMN_NAME_APPNAME+", "+AppEntry.COLUMN_NAME_PACKAGE+"))";
	
	private final ArrayList<Pair<Integer, String>> streams = 
			new ArrayList<Pair<Integer, String>>(4);
	
	public static final String DATABASE_NAME = "AppVolList.db";
    public static final int DATABASE_VERSION = 4;

	public AppSQLiteHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		streams.add(new Pair<Integer, String>(
				AudioManager.STREAM_MUSIC, AppEntry.COLUMN_NAME_MUSIC_STREAM));
		streams.add(new Pair<Integer, String>(
				AudioManager.STREAM_NOTIFICATION, AppEntry.COLUMN_NAME_NOTIFICATION_STREAM));
		streams.add(new Pair<Integer, String>(
				AudioManager.STREAM_RING, AppEntry.COLUMN_NAME_RING_STREAM));
		streams.add(new Pair<Integer, String>(
				AudioManager.STREAM_SYSTEM, AppEntry.COLUMN_NAME_SYSTEM_STREAM));
	}

	@Override
	public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL("DROP TABLE IF EXISTS " + AppEntry.TABLE_NAME);
		onCreate(db);
	}
	
	public static ArrayList<AppListItem> toAppList(SQLiteDatabase db){
		final String[] cols = {
				AppEntry.COLUMN_NAME_APPNAME,
				AppEntry.COLUMN_NAME_PACKAGE};
		ArrayList<AppListItem> toReturn = new ArrayList<AppListItem>();
		Cursor cursor = db.query(AppEntry.TABLE_NAME, cols, null,
				null, null, null, AppEntry.COLUMN_NAME_APPNAME);
		while(cursor.moveToNext()){
			AppListItem item = new AppListItem(cursor.getString(0), cursor.getString(1));
			toReturn.add(item);
		}
		return toReturn;
	}
	
	public void createRowIfNew(SQLiteDatabase db, String pkgName, String appName){
		ContentValues cv = new ContentValues();
		cv.put(AppEntry.COLUMN_NAME_APPNAME, appName);
		cv.put(AppEntry.COLUMN_NAME_PACKAGE, pkgName);
		db.insertWithOnConflict(AppEntry.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
	}
}