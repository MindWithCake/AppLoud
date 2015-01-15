package com.ilariosanseverino.apploud.service;

import java.util.List;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;

import com.ilariosanseverino.apploud.db.AppSQLiteHelper;

public class FillerThread extends Thread {
	private AppSQLiteHelper helper;
	private PackageManager pm;
	private SQLiteDatabase db;
	
	public FillerThread(AppSQLiteHelper helper, SQLiteDatabase db, PackageManager pm){
		this.helper = helper;
		this.db = db;
		this.pm = pm;
	}
	
	@Override
	public void run(){
		List<PackageInfo> packages = pm.getInstalledPackages(0);
		for(PackageInfo pk: packages){
			String appName = pk.applicationInfo.loadLabel(pm).toString();
			helper.createRowIfNew(db, pk.packageName, appName);
		}
	}
}
