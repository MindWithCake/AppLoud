package com.ilariosanseverino.apploud.data;

import android.content.Context;

public class GpsParameter extends TuningParameter {

	protected GpsParameter(String value){
		super(TuningControl.GPS, value);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean doApplyTuning(Context ctx, String val){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected String getActualValue(Context ctx){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isParameterEnabled(){
		// TODO Auto-generated method stub
		return false;
	}

}
