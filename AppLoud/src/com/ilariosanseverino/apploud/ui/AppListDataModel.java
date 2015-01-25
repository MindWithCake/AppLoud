package com.ilariosanseverino.apploud.ui;

import java.util.ArrayList;

import com.ilariosanseverino.apploud.service.IBackgroundServiceBinder;

public class AppListDataModel {
	private ArrayList<AppListItem> appList;
	
	public AppListDataModel(IBackgroundServiceBinder binder){
		refreshAppList(binder);
	}

	public ArrayList<AppListItem> getAppList(){
		return appList;
	}

	public void refreshAppList(IBackgroundServiceBinder binder){
		appList = binder != null? binder.getAppList() : new ArrayList<AppListItem>();
	}
}
