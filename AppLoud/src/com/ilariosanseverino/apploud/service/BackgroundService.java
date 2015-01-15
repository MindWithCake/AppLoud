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
import android.util.Log;

import com.ilariosanseverino.apploud.db.AppSQLiteHelper;

public class BackgroundService extends Service {
	protected PackageManager packageManager;
	protected ActivityManager activityManager;
	protected AudioManager audioManager;
	protected boolean threadRunning = false;
	
	private IBinder binder;
	private BackgroundThread thread = null;

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
		IntentFilter filter=new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);
		registerReceiver(new RingerModeReceiver(), filter);
	}
	
	@Override
	public void onDestroy(){
		thread.interrupt();
		try{
			thread.join();
		}
		catch(InterruptedException e){}
		super.onDestroy();
	}
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId){
		new FillerThread(helper, db, packageManager).start();
		thread = new BackgroundThread(this);
		if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)
			thread.start();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent){
		if(thread == null)
			return null;
		return binder;
	}
	
	private class RingerModeReceiver extends BroadcastReceiver{
		public void onReceive(Context context, Intent intent){
			if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL){
				if(!threadRunning)
					thread.start();
				Log.i("Service", "thread avviato per ringer mode ON");
			} else if(threadRunning){
				thread.interrupt();
				Log.i("Service", "Thread fermato per ringer mode OFF");
			}
		}
	}
}
