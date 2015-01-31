package com.ilariosanseverino.apploud.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public abstract class TuningParameter {
	protected String value;
	protected final String originalValueKey;
	
	public TuningParameter(String key, String value){
		this.value = value;
		originalValueKey = key;
	}

	public final void applyTuning(Context ctx){
		SharedPreferences pref = ctx.getSharedPreferences(
				"original_values", Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(originalValueKey, getActualValue(ctx));
		if(doApplyTuning(ctx, value))
			editor.apply();
		else if(pref.contains(originalValueKey))
			doApplyTuning(ctx, pref.getString(originalValueKey, null));
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
