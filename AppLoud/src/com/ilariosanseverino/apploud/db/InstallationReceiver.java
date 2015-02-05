package com.ilariosanseverino.apploud.db;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class InstallationReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent){
		
		Log.i("Receiver", "Evento ricevuto");
		
		if(!Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction()))
			return;
		
		AppSQLiteHelper helper = new AppSQLiteHelper(context);
		String pkg  = intent.getDataString();
		PackageManager pm = context.getPackageManager();
		ApplicationInfo ai;
		try {
		    ai = pm.getApplicationInfo(pkg, 0);
		} catch (final NameNotFoundException e) {
		    ai = null;
		}
		String app = ai == null? "" : pm.getApplicationLabel(ai).toString();
		
		SQLiteDatabase db = helper.getWritableDatabase();
		helper.createRowIfNew(db, pkg, app);
	}
}
