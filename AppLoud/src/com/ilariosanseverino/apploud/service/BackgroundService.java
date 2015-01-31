package com.ilariosanseverino.apploud.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ilariosanseverino.apploud.db.AppSQLiteHelper;

public class BackgroundService extends AppLoudPreferenceListenerService {
	public final static String THREAD_PREF_KEY = "pref_thread_status";
	private AudioManager audioManager;
	protected boolean threadRunning = false;
	
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
		audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		registerReceiver(recv, new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION));
	}
	
	@Override
	public void onDestroy(){
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
			Log.i("Svc", "Ricevuto cambio di ringer mode");
			changeThreadStatus();
		}
	}
}
