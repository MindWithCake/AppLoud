package com.ilariosanseverino.apploud.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ilariosanseverino.apploud.AudioSource;
import com.ilariosanseverino.apploud.UI.AppListItem;
import com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry;

public class AppSQLiteHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 7;
	public static final String DATABASE_NAME = "AppVolList.db";
	
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
	
    private final int STREAMS_NUMBER = AudioSource.values().length;
    private final String SELECT_APP =  AppEntry.COLUMN_NAME_APPNAME+"=? AND "+AppEntry.COLUMN_NAME_PACKAGE+"=?";

	public AppSQLiteHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
	
	public ArrayList<AppListItem> toAppList(SQLiteDatabase db){
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
	
	public void updateVolume(SQLiteDatabase db, String appName, String appPkg, String stream, Integer volume){
		Log.i("Helper", "Aggiornato volume");
		ContentValues cv = new ContentValues();
		if(volume != null)
			cv.put(stream, volume);
		else
			cv.putNull(stream);
		int cols = db.update(AppEntry.TABLE_NAME, cv, SELECT_APP, new String[]{appName, appPkg});
		Log.d("Helper", "aggiornate "+cols+" righe");
	}
	
	public void setStreamEnabled(SQLiteDatabase db, String appName, String appPkg, String stream, boolean enabled){
		String[] selectionArgs = {appName, appPkg};
		Cursor cursor = db.query(AppEntry.TABLE_NAME, new String[]{stream}, SELECT_APP,
				selectionArgs, null, null, null, null);
		if(!cursor.moveToFirst())
			return; //TODO forse dovrei segnalare l'errore
		Integer valueToStore = cursor.getInt(0);
		if(enabled)
			valueToStore = cursor.isNull(0)? 0 : Math.abs(valueToStore);
		else
			valueToStore = valueToStore == 0? null : -Math.abs(valueToStore);
		Log.i("Helper", "StreamEnabled aggiornerà volume: "+valueToStore);
		updateVolume(db, appName, appPkg, stream, valueToStore);
	}
	
	public Integer[] getStreams(SQLiteDatabase db, String name, String pkg){
		Integer[] ret = new Integer[STREAMS_NUMBER];
		String[] cols = new String[STREAMS_NUMBER];
		for(AudioSource src: AudioSource.values())
			cols[src.ordinal()] = src.columnName();
		Cursor cursor = db.query(AppEntry.TABLE_NAME, cols, SELECT_APP,
				new String[]{name, pkg}, null, null, null, null);
		if(cursor.moveToFirst()){
			for(int i = 0; i < STREAMS_NUMBER; ++i)
				ret[i] = cursor.isNull(i)? null : cursor.getInt(i);
		}
		return ret;
	}
}