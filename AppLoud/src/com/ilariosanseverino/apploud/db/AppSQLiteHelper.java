package com.ilariosanseverino.apploud.db;

import static android.provider.BaseColumns._ID;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.*;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ilariosanseverino.apploud.data.TuningFactory;
import com.ilariosanseverino.apploud.data.TuningParameter;
import com.ilariosanseverino.apploud.ui.AppListItem;

public class AppSQLiteHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 11;
	public static final String DATABASE_NAME = "AppVolList.db";
	
	private static final String creationString(String tablename){
		return "create table "+tablename+
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
	}
	
    private final String SELECT_APP = COLUMN_NAME_APPNAME+"=? AND "+COLUMN_NAME_PACKAGE+"=?";

	public AppSQLiteHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
        db.execSQL(creationString(TABLE_NAME));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		if(oldVersion < 8){
			db.execSQL("ALTER TABLE "+TABLE_NAME+" ADD COLUMN "+
					COLUMN_NAME_ROTATION+COLUMN_TYPE_ROTATION+" DEFAULT OFF");
			db.execSQL("ALTER TABLE "+TABLE_NAME+" ADD COLUMN "+
					COLUMN_NAME_GPS+COLUMN_TYPE_GPS+" DEFAULT OFF");
		}
		else if(oldVersion < 11){
			db.execSQL(creationString("temptable"));
			db.execSQL("INSERT INTO temptable SELECT * FROM "+TABLE_NAME);
			db.execSQL("DROP TABLE "+TABLE_NAME);
			db.execSQL("ALTER TABLE temptable RENAME TO "+TABLE_NAME);
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
	
	public void setColumn(SQLiteDatabase db, String appName, String appPkg, String column, String value){
		Log.i("Helper", "Salvo valore "+value+" nella colonna "+column);
		ContentValues cv = new ContentValues();
		if(value != null)
			cv.put(column, value);
		else
			cv.putNull(column);
		db.update(TABLE_NAME, cv, SELECT_APP, new String[]{appName, appPkg});
	}
	
	public TuningParameter[] getParameters(SQLiteDatabase db, String name, String pkg){
		String[] cols = new String[]{
			COLUMN_NAME_RING_STREAM, COLUMN_NAME_MUSIC_STREAM,
			COLUMN_NAME_NOTIFICATION_STREAM, COLUMN_NAME_SYSTEM_STREAM,
			COLUMN_NAME_ROTATION, COLUMN_NAME_GPS};
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