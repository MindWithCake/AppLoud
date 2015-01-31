package com.ilariosanseverino.apploud.service;

import android.util.Log;

import com.ilariosanseverino.apploud.data.TuningParameter;

public class BackgroundThread extends AppChangedDaemon {
	private BackgroundService owner;
	
	public BackgroundThread(BackgroundService owner){
		super(owner);
		this.owner = owner;
	}
	
	protected void doOnAppChanged(String app, String pack){
		TuningParameter[] params = owner.helper.getParameters(owner.db, app, pack);
		for(TuningParameter param: params)
			param.applyTuning(owner);
	}
	
	@Override
	public void start(){
		Log.i("BgThread", "Thread started");
		owner.threadRunning = true;
		super.start();
	}
	
	@Override
	public void interrupt(){
		Log.i("BgThread", "Thread fermato");
		owner.threadRunning = false;
		super.interrupt();
	}
}
