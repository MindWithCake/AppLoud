package com.ilariosanseverino.apploud.service;

import java.util.ArrayList;

import com.ilariosanseverino.apploud.UI.AppListItem;

public interface IBackgroundServiceBinder {
	public void quitService();
	
	public ArrayList<AppListItem> getAppList();
}
