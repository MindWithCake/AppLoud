package com.ilariosanseverino.apploud.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public abstract class AppChangedDaemon extends Thread {
	private Context ctx;
	private long checkFrequency = 3000;
	private String lastPackage, lastApp, currentPackage, currentApp;
	private ActivityManager actMan;
	
	public AppChangedDaemon(Context context){
		ctx = context;
		lastPackage = currentPackage = ctx.getPackageName();
		lastApp = currentApp = getAppName(currentPackage);
		actMan = (ActivityManager)ctx.getSystemService(Context.ACTIVITY_SERVICE);
	}
	
	@Override
	public final void run(){
		while(true){
			long loopStartTime = System.currentTimeMillis();
			RunningTaskInfo taskInfo = actMan.getRunningTasks(1).get(0);
			currentPackage = taskInfo.topActivity.getPackageName();
			currentApp = getAppName(currentPackage);
			
			if(!(currentApp.equals(lastApp) && currentPackage.equals(lastPackage))){
				lastApp = currentApp;
				lastPackage = currentPackage;
				doOnAppChanged(currentApp, currentPackage);
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
	
	private String getAppName(String pkg){
		try{
			return appNameFromPkgInfo(ctx.getPackageManager().getPackageInfo(pkg, 0));
		}
		catch(NameNotFoundException e){
			return null;
		}
	}
	
	private String appNameFromPkgInfo(PackageInfo info){
		return info.applicationInfo.loadLabel(ctx.getPackageManager()).toString();
	}
	
	protected abstract void doOnAppChanged(String app, String pack);
}
