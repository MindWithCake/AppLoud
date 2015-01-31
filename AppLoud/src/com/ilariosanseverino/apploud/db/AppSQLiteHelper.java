package com.ilariosanseverino.apploud.db;

import static android.provider.BaseColumns._ID;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_NAME_APPNAME;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_NAME_GPS;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_NAME_MUSIC_STREAM;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_NAME_NOTIFICATION_STREAM;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_NAME_PACKAGE;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_NAME_RING_STREAM;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_NAME_ROTATION;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_NAME_SYSTEM_STREAM;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_TYPE_APPNAME;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_TYPE_GPS;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_TYPE_MUSIC_STREAM;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_TYPE_NOTIFICATION_STREAM;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_TYPE_PACKAGE;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_TYPE_RING_STREAM;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_TYPE_ROTATION;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.TABLE_NAME;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ilariosanseverino.apploud.AudioSource;
import com.ilariosanseverino.apploud.data.TuningFactory;
import com.ilariosanseverino.apploud.data.TuningParameter;
import com.ilariosanseverino.apploud.ui.AppListItem;

public class AppSQLiteHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 9;
	public static final String DATABASE_NAME = "AppVolList.db";
	
	private static final String SQL_CREATE = "create table "+TABLE_NAME+
		" (" +_ID + " INTEGER PRIMARY KEY," +
		COLUMN_NAME_APPNAME+COLUMN_TYPE_APPNAME+","+
		COLUMN_NAME_PACKAGE+COLUMN_TYPE_PACKAGE+","+
		COLUMN_NAME_MUSIC_STREAM+COLUMN_TYPE_MUSIC_STREAM+","+
		COLUMN_NAME_NOTIFICATION_STREAM+COLUMN_TYPE_NOTIFICATION_STREAM+","+
		COLUMN_NAME_RING_STREAM+COLUMN_TYPE_RING_STREAM+","+
		COLUMN_NAME_SYSTEM_STREAM+COLUMN_TYPE_RING_STREAM+","+
		COLUMN_NAME_ROTATION+COLUMN_TYPE_ROTATION+","+
		COLUMN_NAME_GPS+COLUMN_TYPE_GPS+","+
		" UNIQUE("+COLUMN_NAME_APPNAME+", "+COLUMN_NAME_PACKAGE+"))";
	
    private final String SELECT_APP = COLUMN_NAME_APPNAME+"=? AND "+COLUMN_NAME_PACKAGE+"=?";

	public AppSQLiteHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		if(oldVersion < 8){
			db.execSQL("ALTER TABLE "+TABLE_NAME+" ADD COLUMN "+
					COLUMN_NAME_ROTATION+COLUMN_TYPE_ROTATION+" DEFAULT 0");
			db.execSQL("ALTER TABLE "+TABLE_NAME+" ADD COLUMN "+
					COLUMN_NAME_GPS+COLUMN_TYPE_GPS+" DEFAULT 0");
		}
		else if(oldVersion < 9){
			//TODO probabilmente va bene così
		}
		else{
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}
	
	public ArrayList<AppListItem> toAppList(SQLiteDatabase db){
		final String[] cols = {
				COLUMN_NAME_APPNAME,
				COLUMN_NAME_PACKAGE};
		ArrayList<AppListItem> toReturn = new ArrayList<AppListItem>();
		Cursor cursor = db.query(TABLE_NAME, cols, null,
				null, null, null, COLUMN_NAME_APPNAME);
		while(cursor.moveToNext()){
			AppListItem item = new AppListItem(cursor.getString(0), cursor.getString(1));
			toReturn.add(item);
		}
		return toReturn;
	}
	
	public void createRowIfNew(SQLiteDatabase db, String pkgName, String appName){
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_NAME_APPNAME, appName);
		cv.put(COLUMN_NAME_PACKAGE, pkgName);
		db.insertWithOnConflict(TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
	}
	
	public void updateVolume(SQLiteDatabase db, String appName, String appPkg, String stream, Integer volume){
		Log.i("Helper", "Aggiornato volume");
		ContentValues cv = new ContentValues();
		if(volume != null)
			cv.put(stream, volume);
		else
			cv.putNull(stream);
		db.update(TABLE_NAME, cv, SELECT_APP, new String[]{appName, appPkg});
	}
	
	public void setStreamEnabled(SQLiteDatabase db, String appName, String appPkg, String stream, boolean enabled){
		String[] selectionArgs = {appName, appPkg};
		Cursor cursor = db.query(TABLE_NAME, new String[]{stream}, SELECT_APP,
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
		final int STREAMS_NUMBER = 4;
		Integer[] ret = new Integer[STREAMS_NUMBER];
		String[] cols = new String[STREAMS_NUMBER];
		for(AudioSource src: AudioSource.values())
			cols[src.ordinal()] = src.columnName();
		Cursor cursor = db.query(TABLE_NAME, cols, SELECT_APP,
				new String[]{name, pkg}, null, null, null, null);
		if(cursor.moveToFirst()){
			for(int i = 0; i < STREAMS_NUMBER; ++i)
				ret[i] = cursor.isNull(i)? null : cursor.getInt(i);
		}
		return ret;
	}
	
	public TuningParameter[] getParameters(SQLiteDatabase db, String name, String pkg){
		String[] cols = new String[]{
			COLUMN_NAME_RING_STREAM, COLUMN_NAME_MUSIC_STREAM,
			COLUMN_NAME_NOTIFICATION_STREAM, COLUMN_NAME_SYSTEM_STREAM,
			COLUMN_NAME_ROTATION};
		TuningParameter[] tunings = new TuningParameter[cols.length];
		
		Cursor cursor = db.query(TABLE_NAME, cols, SELECT_APP,
				new String[]{name, pkg}, null, null, null, null);
		if(!cursor.moveToFirst()){
			return null;
		}
		
		for(int i = 0; i < cursor.getColumnCount(); ++i){
			String column = cursor.getColumnName(i);
			String value = cursor.getString(i);
			tunings[i] = TuningFactory.buildParameter(column, value);
		}
		
		return tunings;
	}
}