package com.ilariosanseverino.apploud.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import com.ilariosanseverino.apploud.AppListActivity;
import com.ilariosanseverino.apploud.service.IBackgroundServiceBinder;

public class AppListDataModel {
	private ArrayList<AppListItem> appList;
	private Map<String, Integer> mapIndex;

	public AppListDataModel(IBackgroundServiceBinder binder){
		mapIndex = new LinkedHashMap<String, Integer>();
		refreshAppList(binder);
	}

	public ArrayList<AppListItem> getAppList(){
		return appList;
	}

	public void refreshAppList(IBackgroundServiceBinder binder){
		appList = binder != null? binder.getAppList() : new ArrayList<AppListItem>();
		for (AppListItem item: appList) {
            String index = item.appName().substring(0, 1).toUpperCase(Locale.getDefault());
            if (mapIndex.get(index) == null)
                mapIndex.put(index, appList.indexOf(item));
        }
	}

	public void filterData(AppListActivity caller, String regex){
		new FilterThread(caller, regex).start();
	}

	private class FilterThread extends Thread{
		private AppListActivity displayActivity;
		private String regex;

		public FilterThread(AppListActivity activity, String regex){
			displayActivity = activity;
			this.regex = regex.toLowerCase(Locale.getDefault());
		}

		@Override
		public void run(){
			if(regex.isEmpty()){
				displayActivity.runOnUiThread(new UIRunner(appList));
				return;
			}

			ArrayList<AppListItem> ret = new ArrayList<AppListItem>();
			for(AppListItem item: appList){
				String name = item.appName().toLowerCase(Locale.getDefault());
				if(name.startsWith(regex))
					ret.add(item);
				else{
					name = item.appPkg().toLowerCase(Locale.getDefault());
					for(String str: name.split("[.]")){
						if(str.startsWith(regex)){
							ret.add(item);
							break;
						}
					}
				}
			}

			displayActivity.runOnUiThread(new UIRunner(ret));
		}

		private class UIRunner implements Runnable{
			private ArrayList<AppListItem> arg;

			public UIRunner(ArrayList<AppListItem> argument){
				arg = argument;
			}

			public void run(){
				displayActivity.showFilteredResult(arg);
			}
		}
	}

	public Map<String, Integer> getMapIndex(){
		return mapIndex;
	}
}
