package com.ilariosanseverino.apploud.service;

import android.app.ActivityManager.RunningTaskInfo;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ilariosanseverino.apploud.AudioSource;

public class BackgroundThread extends Thread implements OnSharedPreferenceChangeListener {
	private long checkFrequency = 3000;
	private BackgroundService owner;
	private String lastPackage, lastApp, currentPackage, currentApp;
	private AudioManager am;
	private Integer[] originalValues = new Integer[AudioSource.values().length];
	private SharedPreferences prefs;
	
	public BackgroundThread(BackgroundService owner){
		this.owner = owner;
		lastPackage = currentPackage = owner.getApplication().getPackageName();
		lastApp = currentApp = getAppName(currentPackage);
		am = owner.audioManager;
		prefs =  PreferenceManager.getDefaultSharedPreferences(owner);
	}
	
	@Override
	public void run(){
		owner.threadRunning = true;
		prefs.registerOnSharedPreferenceChangeListener(this);
		while(true){
			long loopStartTime = System.currentTimeMillis();
			checkAppChanged();
			long nextTimeout = checkFrequency - (System.currentTimeMillis() - loopStartTime);
			if(nextTimeout <= 0)
				nextTimeout = 1;
			try{
				sleep(nextTimeout);
			} catch(InterruptedException e){
				owner.threadRunning = false;
				break;
			}
		}
		prefs.unregisterOnSharedPreferenceChangeListener(this);
	}

	private void checkAppChanged(){
		RunningTaskInfo taskInfo = owner.activityManager.getRunningTasks(1).get(0);
		currentPackage = taskInfo.topActivity.getPackageName();
		currentApp = getAppName(currentPackage);
		if(!currentApp.equals(lastApp) || !currentPackage.equals(lastPackage)){
			lastApp = currentApp;
			lastPackage = currentPackage;
			onAppChanged();
		}
	}
	
	private void onAppChanged(){
		Integer[] vols = owner.helper.getStreams(owner.db, currentApp, currentPackage);
		int flags = 0;
		for(VolumeFeedback feed: VolumeFeedback.values())
//			flags |= prefs.getBoolean(feed.key, false)? 0 : feed.flag;
		{
			boolean b = prefs.getBoolean(feed.key, false);
			Log.i("BgThread", "La preferenza di "+feed.key+" è "+b);
			if(b)
				flags |= feed.flag;
		}
		
		for(int i = 0; i < vols.length; ++i){
			AudioSource src = AudioSource.values()[i];
			if(vols[i] != null && vols[i].intValue() >= 0){
				if(originalValues[i] == null)
					originalValues[i] = am.getStreamVolume(src.audioStream());
				am.setStreamVolume(src.audioStream(), vols[i], flags);
			}
			else if(originalValues[i] != null){
				am.setStreamVolume(src.audioStream(), originalValues[i], flags);
				originalValues[i] = null;
			}
		}
	}
	
	private String getAppName(String pkg){
		try{
			PackageInfo pkgInfo = owner.packageManager.getPackageInfo(pkg, 0);
			return appNameFromPkgInfo(pkgInfo);
		}
		catch(NameNotFoundException e){
			return null;
		}
	}
	
	private String appNameFromPkgInfo(PackageInfo info){
		return info.applicationInfo.loadLabel(owner.packageManager).toString();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key){
		Log.i("BgThread", "Preferenza cambiata: "+key);
	}
}
