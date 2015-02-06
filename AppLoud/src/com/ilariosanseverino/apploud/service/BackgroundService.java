package com.ilariosanseverino.apploud.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.ilariosanseverino.apploud.db.AppSQLiteHelper;

public class BackgroundService extends AppLoudPreferenceListenerService {
	public final static String THREAD_PREF_KEY = "pref_thread_status";
	protected boolean threadRunning = false;
	
	private IBinder binder;
	private BackgroundThread thread;

	protected AppSQLiteHelper helper;
	protected SQLiteDatabase db;
	
	@Override
	public void onCreate(){
		super.onCreate();
		helper = new AppSQLiteHelper(this);
		binder = new AppLoudBinder(this);
		db = helper.getWritableDatabase();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		thread.interrupt();
		try{
			thread.join();
		}
		catch(InterruptedException e){}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		SharedPreferences pref = PreferenceManager.
				getDefaultSharedPreferences(this.getApplicationContext());
		threadShouldRun = pref.getBoolean(THREAD_PREF_KEY, true);
		decideFlags(pref);
		new FillerThread(this, helper, db).start();
		thread = new BackgroundThread(this);
		changeThreadStatus();
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent){
		return binder;
	}
	
	@Override
	protected void changeThreadStatus(){
		if(!threadShouldRun)
			thread.interrupt();
		else if(!threadRunning)
			(thread = new BackgroundThread(this)).start();
	}
}
