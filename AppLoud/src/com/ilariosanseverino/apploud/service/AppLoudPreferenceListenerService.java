package com.ilariosanseverino.apploud.service;

import android.app.Service;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public abstract class AppLoudPreferenceListenerService extends Service implements OnSharedPreferenceChangeListener {
	protected boolean threadShouldRun = true;
	protected int flags;
	
	@Override
	public void onCreate(){
		super.onCreate();
		SharedPreferences prefs = PreferenceManager.
				getDefaultSharedPreferences(getApplicationContext());
		decideFlags(prefs);
		prefs.registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key){
		if(key.equals(BackgroundService.THREAD_PREF_KEY)){
			threadShouldRun = pref.getBoolean(key, threadShouldRun);
			changeThreadStatus();
		} 
		else
			decideFlags(pref);
	}
	
	protected void decideFlags(SharedPreferences pref){
		flags = 0;
		for(VolumeFeedback feed: VolumeFeedback.values()){
			if(pref.getBoolean(feed.key, false))
				flags |= feed.flag;
		}
	}
	
	protected abstract void changeThreadStatus();
}
