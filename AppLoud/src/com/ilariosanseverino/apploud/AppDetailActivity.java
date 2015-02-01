package com.ilariosanseverino.apploud;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;

import com.ilariosanseverino.apploud.data.TuningControl;
import com.ilariosanseverino.apploud.data.TuningParameter;
import com.ilariosanseverino.apploud.service.BackgroundService;
import com.ilariosanseverino.apploud.ui.AppListItem;
import com.ilariosanseverino.apploud.ui.widgets.IgnorableTuning;

import static com.ilariosanseverino.apploud.data.TuningControl.*;

public class AppDetailActivity extends AppLoudMenuActivity implements OnSeekBarChangeListener, OnCheckedChangeListener {
	private Intent serviceIntent;
	private AppListItem item;
	private AppDetailActivator activator;
	
	private void initTuner(IgnorableTuning t, TuningParameter p){
		View v = t.findViewById(p.ctrl.customViewId);
		switch(p.ctrl){
		case RINGER: case NOTY: case MUSIC: case SYS:
			int prog = 0;
			if(p.getValue() != null)
				prog = Integer.parseInt(p.getValue());
			((SeekBar)v).setProgress(prog);
			((SeekBar)v).setOnSeekBarChangeListener(this);
			break;
		case ROTO: case GPS:
			((ToggleButton)v).setChecked("ON".equals(p.getValue()));
			((ToggleButton)v).setOnCheckedChangeListener(this);
			break;
		default:
			throw new IllegalArgumentException("Tuning sconosciuto: "+p.ctrl.column);
		}
	}
	
	@Override
	public void doOnServiceConnected(){
		activator = new AppDetailActivator(binder, item);
		AppDetailActivity act = AppDetailActivity.this;
		TuningParameter[] params = binder.getAppValues(item);
		for(TuningParameter p: params){
			IgnorableTuning tun = (IgnorableTuning)act.findViewById(p.ctrl.widgetId);
			initTuner(tun, p);
			tun.setOnActivationChangedListener(activator);
			tun.setEnabled(p.isParameterEnabled());
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		serviceIntent = new Intent(this, BackgroundService.class);
		bindService(serviceIntent, connection, BIND_AUTO_CREATE);
		Log.i("Details", "Chiamato bind service");
		setContentView(R.layout.activity_app_detail);
		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// For more information, see the Fragments API guide at:
		// http://developer.android.com/guide/components/fragments.html
		if(savedInstanceState == null)
			savedInstanceState = new Bundle();
		
		item = getIntent().getParcelableExtra(AppListActivity.ITEM_ARG);
		savedInstanceState.putParcelable(AppListActivity.ITEM_ARG, item);
		AppDetailFragment fragment = new AppDetailFragment();
		fragment.setArguments(savedInstanceState);
		getFragmentManager().beginTransaction().
				replace(R.id.app_detail_container, fragment).commit();
	}
	
	@Override
	protected void onDestroy(){
		if(binder != null)
			unbindService(connection);
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem){
		int id = menuItem.getItemId();
		if(id == android.R.id.home){
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			NavUtils.navigateUpTo(this, new Intent(this, AppListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(menuItem);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser){
		Log.i("DetailAct", "Progress changed: "+progress+" "+fromUser);
		if(binder == null || !fromUser)
			return;
		for(TuningControl ctrl: TuningControl.values()){
			if(ctrl.customViewId == seekBar.getId()){
				binder.setParam(item, ctrl.column, Integer.toString(progress));
				return;
			}
		}
	}

	@Override public void onStartTrackingTouch(SeekBar seekBar){}
	@Override public void onStopTrackingTouch(SeekBar seekBar){}

	@Override
	protected int getContainerID(){
		return R.id.app_detail_container;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
		if(buttonView.getId() == ROTO.customViewId)
			binder.setParam(item, ROTO.column, isChecked? "ON" : "OFF");
		else if(buttonView.getId() == GPS.customViewId)
			Log.e("DetailAct", "Premuto bottone GPS!");
		else
			throw new IllegalArgumentException("Bottone misterioso checkato: "+buttonView.getId());
	}
}
