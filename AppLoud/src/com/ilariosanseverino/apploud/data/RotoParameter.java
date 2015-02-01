package com.ilariosanseverino.apploud.data;

import static android.provider.Settings.System.*;
import static com.ilariosanseverino.apploud.data.TuningControl.*;
import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

public class RotoParameter extends TuningParameter {
	
	public RotoParameter(String value){
		super(ROTO, value);
	}

	@Override
	protected boolean doApplyTuning(Context ctx, String value){
		if(value == null)
			return false;
		
		int active = 0;
		switch(value){
		case "ON": active = 1; //DON'T BREAK!!!
		case "OFF":
			Settings.System.putInt(ctx.getContentResolver(), ACCELEROMETER_ROTATION, active);
			Log.i("Roto", "Rotazione applicata: "+value+" con valore "+active);
			return true;
		default:
			return false;
		}
	}

	@Override
	protected String getActualValue(Context ctx){
		try{
			int val = Settings.System.getInt(ctx.getContentResolver(), ACCELEROMETER_ROTATION);
			return val == 0? "OFF" : "ON";
		}
		catch(SettingNotFoundException e){
			return null;
		}
	}

	@Override
	public boolean isParameterEnabled(){
		return getValue() != null;
	}
}
