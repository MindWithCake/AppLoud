package com.ilariosanseverino.apploud;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.ilariosanseverino.apploud.service.BackgroundService;
import com.ilariosanseverino.apploud.ui.AppListItem;

public class AppDetailActivity extends AppLoudMenuActivity implements OnClickListener, OnSeekBarChangeListener {
	private Intent serviceIntent;
	private AppListItem item;
	
	@Override
	public void doOnServiceConnected(){
		AppDetailActivity act = AppDetailActivity.this;
		Integer[] volumes = binder.getStreamValues(item);
		AudioSource[] src = AudioSource.values();
		for(int i = 0; i < volumes.length; ++i){
			CheckBox box = (CheckBox)act.findViewById(src[i].checkId());
			if(volumes[i] == null || volumes[i].intValue() < 0){
				box.setChecked(true);
				box.invalidate();
				volumes[i] = volumes[i] == null? 0 : -volumes[i];
			}
			SeekBar bar = (SeekBar)act.findViewById(src[i].seekId());
			bar.setProgress(volumes[i]);
			bar.setEnabled(!box.isChecked());
			bar.invalidate();
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
			// For more details, see the Navigation pattern on Android Design:
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			NavUtils.navigateUpTo(this, new Intent(this, AppListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(menuItem);
	}
	
	@Override
	public void onClick(View v){
		if(binder == null)
			return;
		boolean shouldEnable = !((CheckBox)v).isChecked();
		for(AudioSource src: AudioSource.values()){
			if(v.getId() == src.checkId()){
				findViewById(src.seekId()).setEnabled(shouldEnable);
				binder.setStreamEnabled(item, src.columnName(), shouldEnable);
				return;
			}
		}
	}

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
}
