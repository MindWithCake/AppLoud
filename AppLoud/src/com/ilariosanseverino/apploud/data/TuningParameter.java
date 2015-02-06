package com.ilariosanseverino.apploud.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public abstract class TuningParameter {
	private final String value;
	public final TuningControl ctrl;
	
	protected TuningParameter(TuningControl ctrl, String value){
		this.value = value;
		this.ctrl = ctrl;
	}

	public final void applyTuning(Context ctx){
		SharedPreferences pref = ctx.getSharedPreferences(
				"original_values", Context.MODE_PRIVATE);
		String backup = getCurrentValue(ctx);
		
		Log.i("Tuning-"+ctrl.column, "Parametro di backup: "+backup);
		
		if(doApplyTuning(ctx, value)){
			Log.i("Tuning-"+ctrl.column, "Ho applicato il settaggio "+value);
			if(!pref.contains(ctrl.prefKey)){
				Log.i("Tuning-"+ctrl.column, "Nessun backup presente, salvo "+value);
				pref.edit().putString(ctrl.prefKey, backup).apply();
			}
		}
		else if(pref.contains(ctrl.prefKey)){
			String val = pref.getString(ctrl.prefKey, null);
			Log.i("Tuning-"+ctrl.column, "Ripristino il valore originale: "+val);
			if(doApplyTuning(ctx, val))
				pref.edit().remove(ctrl.prefKey).apply();
		}
	}

	protected abstract boolean doApplyTuning(Context ctx, String val);

	protected abstract String getCurrentValue(Context ctx);

	public String getValue(){
		return value;
	}
	
	public abstract boolean isParameterEnabled();
}
