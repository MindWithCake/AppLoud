package com.ilariosanseverino.apploud;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.ilariosanseverino.apploud.data.TuningParameter;
import com.ilariosanseverino.apploud.service.BackgroundService;
import com.ilariosanseverino.apploud.ui.AppListItem;
import com.ilariosanseverino.apploud.ui.widgets.IgnorableTuning;

public class AppDetailActivity extends AppLoudMenuActivity implements OnSeekBarChangeListener {
	private Intent serviceIntent;
	private AppListItem item;
	private AppDetailActivator activator;
	
	@Override
	public void doOnServiceConnected(){
		activator = new AppDetailActivator(binder, item);
		AppDetailActivity act = AppDetailActivity.this;
		TuningParameter[] params = binder.getAppValues(item);
		for(TuningParameter p: params){
			IgnorableTuning tun = (IgnorableTuning)act.findViewById(p.ctrl.widgetId);
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

//	@Override
//	public void onActivationChanged(View v, boolean active){
//		for(AudioSource src: AudioSource.values()){
//			if(v.getId() == src.checkId()){
//				SeekBar bar = (SeekBar)v.findViewById(src.seekId());
//				bar.setEnabled(active);
//				if(binder != null)
//					binder.setStreamEnabled(item, src.columnName(), active);
//				return;
//			}
//		}
//	}

	@Override
	public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser){
		if(binder == null || !fromUser)
			return;
		for(AudioSource src: AudioSource.values()){
			if(src.seekId() == seekBar.getId()){
				binder.updateStream(item, src.columnName(), progress);
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
	
//	protected class ToggleButtonActivator implements OnActivationChangedListener{
//		public void onActivationChanged(View v, boolean active){
//			ToggleButton tbut;
//			switch(v.getId()){
//			case R.id.roto_tuning:
//				tbut = (ToggleButton)v.findViewById(R.id.roto_toggle);
//				tbut.setActivated(active);
//				tbut.setClickable(active);
//				//TODO attiva/disattiva rotazione
//				break;
//			case R.id.gps_tuning:
//				tbut = (ToggleButton)v.findViewById(R.id.gps_toggle);
//				tbut.setActivated(active);
//				tbut.setClickable(active);
//				//TODO attiva/disattiva rotazione
//				break;
//			}
//		}
//	}
}
