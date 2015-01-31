package com.ilariosanseverino.apploud.data;

import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import static android.provider.Settings.System.ACCELEROMETER_ROTATION;

public class RotoParameter extends TuningParameter {
	
	public RotoParameter(String value){
		super("Rotation_value", value);
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
}
