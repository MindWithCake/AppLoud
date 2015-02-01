package com.ilariosanseverino.apploud.service;

import java.util.ArrayList;

import com.ilariosanseverino.apploud.data.TuningParameter;
import com.ilariosanseverino.apploud.ui.AppListItem;

public interface IBackgroundServiceBinder {
	public void quitService();
	
	public ArrayList<AppListItem> getAppList();
	
	public void updateStream(AppListItem item, String streamName, int newValue);
	
	public void setParam(AppListItem item, String column, String value);
	
	public TuningParameter[] getAppValues(AppListItem item);
	
	public boolean changeThreadActiveStatus();
}
