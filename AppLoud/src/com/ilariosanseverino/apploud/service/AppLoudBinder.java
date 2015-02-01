package com.ilariosanseverino.apploud.service;

import java.util.ArrayList;

import android.content.SharedPreferences.Editor;
import android.os.Binder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ilariosanseverino.apploud.data.TuningParameter;
import com.ilariosanseverino.apploud.ui.AppListItem;

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

//	public void updateStream(AppListItem item, String streamName, int newValue){
//		service.helper.updateVolume(service.db, item.appName(), item.appPkg(), streamName, newValue);
//	}

	public TuningParameter[] getAppValues(AppListItem item){
		return service.helper.getParameters(service.db, item.appName(), item.appPkg());
	}
	
	public boolean changeThreadActiveStatus(){
		service.threadShouldRun = !service.threadShouldRun;
		service.changeThreadStatus();
		prefEdit.putBoolean(BackgroundService.THREAD_PREF_KEY, service.threadShouldRun);
		prefEdit.commit();
		return service.threadShouldRun;
	}

	public void setParam(AppListItem item, String column, String value){
		Log.i("Binder", "Setto parametro "+column+" al valore "+value);
		service.helper.setColumn(service.db, item.appName(), item.appPkg(), column, value);
	}
}
