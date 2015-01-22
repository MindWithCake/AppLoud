package com.ilariosanseverino.apploud;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.Menu;
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
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
		case R.id.action_settings:
			 getFragmentManager().beginTransaction().replace(
					 R.id.app_list, new SettingsFragment()).addToBackStack(null).commit();
			 return true;
		case R.id.action_stop_svc:
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
	public boolean onPrepareOptionsMenu (Menu menu){
		if(serviceOn){
			menu.findItem(R.id.action_start_svc).setVisible(false);
			menu.findItem(R.id.action_stop_svc).setVisible(true);
		}
		else {
			menu.findItem(R.id.action_stop_svc).setVisible(false);
			menu.findItem(R.id.action_start_svc).setVisible(true);
		}
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
