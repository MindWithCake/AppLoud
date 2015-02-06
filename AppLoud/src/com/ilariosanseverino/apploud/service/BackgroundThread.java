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
		Log.i("Thread", "Applicazione cambiata: "+app);
		TuningParameter[] params = owner.helper.getParameters(owner.db, app, pack);
		for(TuningParameter param: params)
			param.applyTuning(owner);
		Log.i("Thread", "Ho finito di applicare i parametri di "+app);
	}
	
	@Override
	public void start(){
		owner.threadRunning = true;
		super.start();
	}
	
	@Override
	public void interrupt(){
		owner.threadRunning = false;
		super.interrupt();
	}
}
