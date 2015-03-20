package com.ilariosanseverino.apploud;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.ilariosanseverino.apploud.service.BackgroundConnection;
import com.ilariosanseverino.apploud.service.BackgroundService;
import com.ilariosanseverino.apploud.service.IBackgroundServiceBinder;
import com.ilariosanseverino.apploud.ui.PlayPauseActivity;
import com.ilariosanseverino.apploud.ui.SettingsFragment;

public abstract class AppLoudMenuActivity extends Activity {
	protected IBackgroundServiceBinder binder;
	private Intent playPauseIntent;

	protected  final BackgroundConnection connection = new BackgroundConnection(){
		public void doOnServiceConnected(){
			AppLoudMenuActivity.this.doOnServiceConnected();
		}
		public void setBinder(IBackgroundServiceBinder binder){
			AppLoudMenuActivity.this.binder = binder;
		}
	};
	
	protected abstract void doOnServiceConnected();
	protected abstract int getContainerID();
	
	protected void bindBackgroundService(){
		Intent intent = new Intent(this, BackgroundService.class);
		bindService(intent, connection, BIND_ABOVE_CLIENT);
	}
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
		case R.id.action_settings:
			 getFragmentManager().beginTransaction().
			 		replace(getContainerID(), new SettingsFragment()).
			 		addToBackStack(null).commit();
			 return true;
		case R.id.action_start_svc:
			startActivity(playPauseIntent);
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

		MenuItem searchMenuItem = menu.findItem(R.id.action_search);
		SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView)searchMenuItem.getActionView();
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    searchView.setIconifiedByDefault(false);
	    
		playPauseIntent = new Intent(this, PlayPauseActivity.class);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		boolean play = PreferenceManager.
				getDefaultSharedPreferences(this.getApplicationContext()).
				getBoolean(BackgroundService.THREAD_PREF_KEY, true);
		int draw = play? R.drawable.ic_action_play : R.drawable.ic_action_pause;
		menu.findItem(R.id.action_start_svc).setIcon(draw);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		invalidateOptionsMenu();
	}
}
