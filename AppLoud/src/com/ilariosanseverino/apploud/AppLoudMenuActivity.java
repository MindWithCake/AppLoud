package com.ilariosanseverino.apploud;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ilariosanseverino.apploud.UI.SettingsFragment;
import com.ilariosanseverino.apploud.service.BackgroundConnection;
import com.ilariosanseverino.apploud.service.BackgroundService;
import com.ilariosanseverino.apploud.service.IBackgroundServiceBinder;

public abstract class AppLoudMenuActivity extends Activity {
	protected IBackgroundServiceBinder binder;
	private static boolean serviceOn = true;
	protected  final BackgroundConnection connection = new BackgroundConnection(){
		@Override
		public void doOnServiceConnected(){
			AppLoudMenuActivity.this.doOnServiceConnected();
		}

		@Override
		public void setBinder(IBackgroundServiceBinder binder){
			AppLoudMenuActivity.this.binder = binder;
		}
	};
	
	protected abstract void doOnServiceConnected();
	protected abstract int getContainerID();
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
		case R.id.action_settings:
//			startActivity(new Intent(this, SettingsFragment.class));
			 getFragmentManager().beginTransaction().replace(
					 getContainerID(), new SettingsFragment()).addToBackStack(null).commit();
			 return true;
//		case R.id.action_stop_svc:
		case R.id.action_start_svc:
			if(binder != null){
				binder.changeThreadActiveStatus();
				serviceOn = !serviceOn;
				invalidateOptionsMenu();
			}
			return true;
		case R.id.action_show_info:
			//TODO
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		if(serviceOn)
			menu.findItem(R.id.action_start_svc).setIcon(R.drawable.ic_action_pause);
		else
			menu.findItem(R.id.action_start_svc).setIcon(R.drawable.ic_action_play);
		return true;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		serviceOn = PreferenceManager.
				getDefaultSharedPreferences(this.getApplicationContext()).
				getBoolean(BackgroundService.THREAD_PREF_KEY, true);
	}
}
