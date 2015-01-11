package com.ilariosanseverino.apploud.service;

import java.util.ArrayList;

import com.ilariosanseverino.apploud.UI.AppListItem;

public interface IBackgroundServiceBinder {
	public void quitService();
	
	public ArrayList<AppListItem> getAppList();
	
	public void updateStream(AppListItem item, String streamName, int newValue);
	
	public void setStreamEnabled(AppListItem item, String streamName, boolean enabled);
	
	public Integer[] getStreamValues(AppListItem item);
}
