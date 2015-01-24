package com.ilariosanseverino.apploud.service;

import android.app.ActivityManager.RunningTaskInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.util.Log;

import com.ilariosanseverino.apploud.AudioSource;

public class BackgroundThread extends Thread {
	private long checkFrequency = 3000;
	private BackgroundService owner;
	private String lastPackage, lastApp, currentPackage, currentApp;
	private AudioManager am;
	private Integer[] originalValues = new Integer[AudioSource.values().length];
	
	public BackgroundThread(BackgroundService owner){
		this.owner = owner;
		lastPackage = currentPackage = owner.getApplication().getPackageName();
		lastApp = currentApp = getAppName(currentPackage);
		am = owner.audioManager;
	}
	
	@Override
	public void run(){
		while(true){
			long loopStartTime = System.currentTimeMillis();
			RunningTaskInfo taskInfo = owner.activityManager.getRunningTasks(1).get(0);
			currentPackage = taskInfo.topActivity.getPackageName();
			currentApp = getAppName(currentPackage);
			
			if(!(currentApp.equals(lastApp) && currentPackage.equals(lastPackage))){
				lastApp = currentApp;
				lastPackage = currentPackage;
				Integer[] vols = owner.helper.getStreams(owner.db, currentApp, currentPackage);
				
				for(int i = 0; i < vols.length; ++i){
					AudioSource src = AudioSource.values()[i];
					if(vols[i] != null && vols[i].intValue() >= 0){
						if(originalValues[i] == null)
							originalValues[i] = am.getStreamVolume(src.audioStream());
						am.setStreamVolume(src.audioStream(), vols[i], owner.flags);
					}
					else if(originalValues[i] != null){
						am.setStreamVolume(src.audioStream(), originalValues[i], owner.flags);
						originalValues[i] = null;
					}
				}
			}
			
			long nextTimeout = checkFrequency - (System.currentTimeMillis() - loopStartTime);
			if(nextTimeout <= 0)
				nextTimeout = 1;
			try{
				sleep(nextTimeout);
			} catch(InterruptedException e){
				Log.i("BgThread", "interruzione catchata");
				return;
			}
		}
	}
	
	@Override
	public void start(){
		Log.i("BgThread", "Thread started");
		owner.threadRunning = true;
		super.start();
	}
	
	@Override
	public void interrupt(){
		Log.i("BgThread", "Thread fermato");
		owner.threadRunning = false;
		super.interrupt();
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
}
