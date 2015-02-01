package com.ilariosanseverino.apploud;

import static com.ilariosanseverino.apploud.data.TuningControl.*;
import android.view.View;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import com.ilariosanseverino.apploud.data.TuningControl;
import com.ilariosanseverino.apploud.data.TuningParameter;
import com.ilariosanseverino.apploud.service.IBackgroundServiceBinder;
import com.ilariosanseverino.apploud.ui.AppListItem;
import com.ilariosanseverino.apploud.ui.widgets.IgnorableTuning;
import com.ilariosanseverino.apploud.ui.widgets.OnActivationChangedListener;

public class AppDetailActivator implements OnActivationChangedListener {
	
	private final IBackgroundServiceBinder binder;
	private final AppListItem item;
	private View custom = null;
	TuningParameter[] params;
	
	public AppDetailActivator(IBackgroundServiceBinder binder, AppListItem item){
		this.binder = binder;
		this.item = item;
		params = binder.getAppValues(item);
	}

	@Override
	public void onActivationChanged(IgnorableTuning v, boolean active){
		TuningControl ctrl = null;
		String valueToSet = null;
		switch(v.getId()){
		case R.id.ring_tuning:
			ctrl = RINGER; // DON'T BREAK!!!
		case R.id.notify_tuning:
			if(ctrl == null)
				ctrl = NOTY; // DON'T BREAK!!!
		case R.id.media_tuning:
			if(ctrl == null)
				ctrl = MUSIC; // DON'T BREAK!!!
		case R.id.sys_tuning:
			if(ctrl == null)
				ctrl = SYS;
			valueToSet = audioValue(v, ctrl, active);
			break;
		case R.id.roto_tuning:
			ctrl = ROTO;
			if(active) {
				custom = v.findViewById(ctrl.customViewId);
				valueToSet = ((ToggleButton)custom).isChecked()? "ON" : "OFF";
			}
			break;
		case R.id.gps_tuning:
			if(active)
				v.setEnabled(false);
			else
				v.findViewById(R.id.gps_toggle).setEnabled(false);
			return;
		default:
			throw new IllegalArgumentException("Widget sconosciuta: "+v.getId());
		}
		custom.setEnabled(active);
		binder.setParam(item, ctrl.column, valueToSet);
	}
	
	private String audioValue(IgnorableTuning v, TuningControl ctrl, boolean active){
		custom = v.findViewById(ctrl.customViewId);
		SeekBar bar = (SeekBar)custom;
		int barValue = active? bar.getProgress() : -bar.getProgress();
		return !active && barValue == 0? null : Integer.toString(barValue);
	}
}
