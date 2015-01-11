package com.ilariosanseverino.apploud.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public abstract class BackgroundConnection implements ServiceConnection {

	@Override
	public void onServiceConnected(ComponentName name, IBinder service){
		Log.i("Connection", "Servizio connesso");
		setBinder((IBackgroundServiceBinder)service);
		doOnServiceConnected();
	}

	@Override
	public void onServiceDisconnected(ComponentName name){
		Log.i("Connection", "Servizio interrotto");
		setBinder(null);
	}

	public abstract void doOnServiceConnected();
	
	public abstract void setBinder(IBackgroundServiceBinder binder);
}
