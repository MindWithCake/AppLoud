package com.ilariosanseverino.apploud.service;

import java.util.ArrayList;
import android.os.Binder;
import com.ilariosanseverino.apploud.UI.AppListItem;

public class AppLoudBinder extends Binder implements IBackgroundServiceBinder {
	private BackgroundService service;
	
	public AppLoudBinder(BackgroundService service){
		this.service = service;
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
}
