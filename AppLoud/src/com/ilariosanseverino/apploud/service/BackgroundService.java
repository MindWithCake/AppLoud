package com.ilariosanseverino.apploud.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

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
		try{
			thread.interrupt();
			thread.join();
		}
		catch(InterruptedException ignored){}
		catch(NullPointerException e){
			Log.i("Svc", "background thread is null");
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Log.w("Svc", "Start command called with id "+startId);
		SharedPreferences pref = PreferenceManager.
				getDefaultSharedPreferences(this.getApplicationContext());
		threadShouldRun = pref.getBoolean(THREAD_PREF_KEY, true);
		decideFlags(pref);
		new FillerThread(this, helper, db).start();
		Log.w("Svc", "Boot completed, starto thread");
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

	public static class EventReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent){
			Log.w("Svc", "broadcast received");
			if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
				context.startService(new Intent(context, BackgroundService.class));
		}
	}
}
