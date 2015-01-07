package com.ilariosanseverino.apploud.service;

import java.util.List;

import android.app.ActivityManager.RunningTaskInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class BackgroundThread extends Thread {
	private long checkFrequency = 5000;
	private BackgroundService owner;
	private String lastPackage, lastApp, currentPackage, currentApp;
	
	public BackgroundThread(BackgroundService owner){
		this.owner = owner;
		lastPackage = currentPackage = owner.getApplication().getPackageName();
		lastApp = currentApp = getAppName(currentPackage);
	}
	
	@Override
	public void run(){
		fillDbWithInstalledApps();
		while(true){
			long loopStartTime = System.currentTimeMillis();
			checkAppChanged();
			long nextTimeout = checkFrequency - (System.currentTimeMillis() - loopStartTime);
			if(nextTimeout <= 0)
				nextTimeout = 1;
			try{
				sleep(nextTimeout);
			} catch(InterruptedException e){
				break;
			}
		}
	}

	private void checkAppChanged(){
		RunningTaskInfo taskInfo = owner.activityManager.getRunningTasks(1).get(0);
		currentPackage = taskInfo.topActivity.getPackageName();
		currentApp = getAppName(currentPackage);
		if(!currentApp.equals(lastApp) || !currentPackage.equals(lastPackage))
			onAppChanged();
	}
	
	private void onAppChanged(){
		//TODO
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
	
	private void fillDbWithInstalledApps(){
		List<PackageInfo> packages = owner.packageManager.getInstalledPackages(0);
		for(PackageInfo pk: packages){
			owner.helper.createRowIfNew(owner.db, pk.packageName, appNameFromPkgInfo(pk));
		}
	}
	
	private String appNameFromPkgInfo(PackageInfo info){
		return info.applicationInfo.loadLabel(owner.packageManager).toString();
	}
}
