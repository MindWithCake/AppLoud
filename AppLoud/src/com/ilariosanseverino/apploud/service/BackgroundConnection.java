package com.ilariosanseverino.apploud.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public abstract class BackgroundConnection implements ServiceConnection {

	@Override
	public void onServiceConnected(ComponentName name, IBinder service){
		setBinder((IBackgroundServiceBinder)service);
		doOnServiceConnected();
	}

	@Override
	public void onServiceDisconnected(ComponentName name){
		setBinder(null);
	}

	public abstract void doOnServiceConnected();
	
	public abstract void setBinder(IBackgroundServiceBinder binder);
}
