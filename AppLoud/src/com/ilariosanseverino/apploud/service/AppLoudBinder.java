package com.ilariosanseverino.apploud.service;

import java.util.ArrayList;

import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.preference.PreferenceManager;

import com.ilariosanseverino.apploud.UI.AppListItem;

public class AppLoudBinder extends Binder implements IBackgroundServiceBinder {
	private BackgroundService service;
	private Editor prefEdit;
	
	public AppLoudBinder(BackgroundService service){
		this.service = service;
		prefEdit =  PreferenceManager.
				getDefaultSharedPreferences(service.getApplicationContext()).
				edit();
	}
	
	public void quitService(){
		service.stopSelf();
	}

	public ArrayList<AppListItem> getAppList(){
		return service.helper.toAppList(service.db);
	}

	public void updateStream(AppListItem item, String streamName, int newValue){
		service.helper.updateVolume(service.db, item.appName(), item.appPkg(), streamName, newValue);
	}

	public void setStreamEnabled(AppListItem item, String stream, boolean enabled){
		service.helper.setStreamEnabled(service.db, item.appName(), item.appPkg(), stream, enabled);
	}

	public Integer[] getStreamValues(AppListItem item){
		return service.helper.getStreams(service.db, item.appName(), item.appPkg());
	}
	
	public boolean changeThreadActiveStatus(){
		service.threadShouldRun = !service.threadShouldRun;
		service.changeThreadStatus();
		prefEdit.putBoolean(BackgroundService.THREAD_PREF_KEY, service.threadShouldRun);
		prefEdit.commit();
		return service.threadShouldRun;
	}
}
