package com.ilariosanseverino.apploud.service;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;

import com.ilariosanseverino.apploud.db.AppSQLiteHelper;

public class FillerThread extends Thread {
	private AppSQLiteHelper helper;
	private PackageManager pm;
	private SQLiteDatabase db;
	private Context ctx;
	public final static String DB_FILLED_EVENT = "fillerthread-db-full";
	
	public FillerThread(Context ctx, AppSQLiteHelper helper, 
			SQLiteDatabase db){
		this.helper = helper;
		this.db = db;
		this.ctx = ctx;
		this.pm = ctx.getPackageManager();
	}
	
	@Override
	public void run(){
		List<PackageInfo> packages = pm.getInstalledPackages(0);
		for(PackageInfo pk: packages){
			String appName = pk.applicationInfo.loadLabel(pm).toString();
			helper.createRowIfNew(db, pk.packageName, appName);
		}
		
		Intent intent = new Intent(DB_FILLED_EVENT);
		LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);
	}
}
