package com.ilariosanseverino.apploud.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public abstract class TuningParameter {
	private String value;
	protected final String originalValueKey;
	
	public TuningParameter(String key, String value){
		this.value = value;
		originalValueKey = key;
	}

	public final void applyTuning(Context ctx){
		SharedPreferences pref = ctx.getSharedPreferences(
				"original_values", Context.MODE_PRIVATE);
		String backup = getActualValue(ctx);
		Log.i("Tuning-"+originalValueKey, "Salvato actual value: "+backup);
		
		if(doApplyTuning(ctx, value)){
			Log.i("Tuning-"+originalValueKey, "Parametro applicato "+value);
			pref.edit().putString(originalValueKey, backup).apply();
		}
		else if(pref.contains(originalValueKey)){
			Log.i("Tuning-"+originalValueKey, "Ho un valore salvato");
			doApplyTuning(ctx, pref.getString(originalValueKey, null));
			pref.edit().remove(originalValueKey).apply();
		}
		else
			Log.i("Tuning-"+originalValueKey, "Niente da applicare e niente da ripristinare");
	}

	protected abstract boolean doApplyTuning(Context ctx, String val);

	protected abstract String getActualValue(Context ctx);

	public String getValue(){
		return value;
	}

	public void setValue(String newValue){
		this.value = newValue;
	}
}
