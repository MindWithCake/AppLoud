package com.ilariosanseverino.apploud.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ilariosanseverino.apploud.db.AppSQLiteHelper;

public class BackgroundService extends Service {
	public final static String THREAD_PREF_KEY = "pref_thread_status";
	protected PackageManager packageManager;
	protected ActivityManager activityManager;
	protected AudioManager audioManager;
	protected boolean threadRunning = false;
	protected boolean threadShouldRun = true;
	
	private IBinder binder;
	private BackgroundThread thread;
	private RingerModeReceiver recv = new RingerModeReceiver();

	protected AppSQLiteHelper helper;
	protected SQLiteDatabase db;
	
	@Override
	public void onCreate(){
		super.onCreate();
		helper = new AppSQLiteHelper(this);
		binder = new AppLoudBinder(this);
		db = helper.getWritableDatabase();
		packageManager = getPackageManager();
		activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
		audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		registerReceiver(recv, new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION));
	}
	
	@Override
	public void onDestroy(){
		Log.i("Svc", "Servizio distrutto, shouldRun è "+threadShouldRun);
		thread.interrupt();
		try{
			thread.join();
		}
		catch(InterruptedException e){}
		finally{
			unregisterReceiver(recv);
			super.onDestroy();
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		threadShouldRun = PreferenceManager.
				getDefaultSharedPreferences(this.getApplicationContext()).
				getBoolean(THREAD_PREF_KEY, true);
		Log.i("Svc", "Servizio startato, should run è "+threadShouldRun);
		new FillerThread(this, helper, db, packageManager).start();
		thread = new BackgroundThread(this);
		changeThreadStatus();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent){
		return binder;
	}
	
	protected void changeThreadStatus(){
		Log.i("Service", "change status: should run = "+threadShouldRun);
		if(!threadShouldRun)
			thread.interrupt();
		else if(audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
			thread.interrupt();
		else if(!threadRunning)
			(thread = new BackgroundThread(this)).start();
	}
	
	private class RingerModeReceiver extends BroadcastReceiver{
		public void onReceive(Context context, Intent intent){
			changeThreadStatus();
		}
	}
}
