package com.ilariosanseverino.apploud.service;

import java.util.ArrayList;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.ilariosanseverino.apploud.UI.AppListItem;
import com.ilariosanseverino.apploud.db.AppSQLiteHelper;

public class BackgroundService extends Service {
	protected PackageManager packageManager;
	protected ActivityManager activityManager;
	
	private final IBinder binder = new AppLoudBinder();
	private BackgroundThread thread = null;

	protected AppSQLiteHelper helper;
	protected SQLiteDatabase db;
	
	@Override
	public void onCreate(){
		Log.d("Bg", "Servizio creato");
		super.onCreate();
		helper = new AppSQLiteHelper(this);
		db = helper.getWritableDatabase();
		packageManager = getPackageManager();
		activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		thread.interrupt();
	}
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId){
		Log.d("Bg", "Chiamato start command");
		(thread = new BackgroundThread(this)).start();
		Log.d("Bg", "Thread avviato da startCommand");
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent){
		Log.d("Bg", "onBind chiamata");
		if(thread == null)
			return null;
		return binder;
	}
	
	private void quit(){
		Log.d("Bg", "Richiesta di quittaggio");
		stopSelf();
	}
	
	public class AppLoudBinder extends Binder implements IBackgroundServiceBinder {
		@Override
		public void quitService(){
			BackgroundService.this.quit();
		}

		@Override
		public ArrayList<AppListItem> getAppList(){
			return helper.toAppList(db);
		}

		@Override
		public void updateStream(AppListItem item, String streamName, int newValue){
			helper.updateVolume(db, item.appName(), item.appPkg(), streamName, newValue);
		}

		@Override
		public void setStreamEnabled(AppListItem item, String stream, boolean enabled){
			helper.setStreamEnabled(db, item.appName(), item.appPkg(), stream, enabled);
		}

		@Override
		public Integer[] getStreamValues(AppListItem item){
			return helper.getStreams(db, item.appName(), item.appPkg());
		}
	}
}
