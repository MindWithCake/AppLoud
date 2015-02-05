package com.ilariosanseverino.apploud.ui;

import java.util.ArrayList;
import java.util.Locale;

import com.ilariosanseverino.apploud.service.IBackgroundServiceBinder;
import com.ilariosanseverino.apploud.AppListActivity;

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

	public void getFilteredAppList(AppListActivity caller, String regex){
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
}
