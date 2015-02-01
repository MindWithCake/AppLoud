package com.ilariosanseverino.apploud.data;

import android.content.Context;
import android.content.SharedPreferences;

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
		String backup = getActualValue(ctx);
		
		if(doApplyTuning(ctx, value))
			pref.edit().putString(ctrl.prefKey, backup).apply();
		else if(pref.contains(ctrl.prefKey)){
			doApplyTuning(ctx, pref.getString(ctrl.prefKey, null));
			pref.edit().remove(ctrl.prefKey).apply();
		}
	}

	protected abstract boolean doApplyTuning(Context ctx, String val);

	protected abstract String getActualValue(Context ctx);

	public String getValue(){
		return value;
	}
	
	public abstract boolean isParameterEnabled();
}
