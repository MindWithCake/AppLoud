package com.ilariosanseverino.apploud.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ilariosanseverino.apploud.R;
import com.ilariosanseverino.apploud.service.BackgroundService;

public class PlayPauseActivity extends Activity {
	private boolean play;
	private SharedPreferences pref;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_start_stop);
	    pref = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
	    play = pref.getBoolean(BackgroundService.THREAD_PREF_KEY, true);
	    setButtonIcon();
	}
	
	public void changeServiceState(View view){
		play = !play;
		pref.edit().putBoolean(BackgroundService.THREAD_PREF_KEY, play).commit();
		setButtonIcon();
	}
	
	private void setButtonIcon(){
		Button button = (Button)findViewById(R.id.play_button);
		int element = play? R.drawable.ic_action_pause : R.drawable.ic_action_play;
		button.setCompoundDrawablesWithIntrinsicBounds(element, 0, 0, 0);
		element = play? R.string.stop_menu_title : R.string.start_menu_title;
		button.setText(element);
		element = play? R.string.status_on : R.string.status_off;
		((TextView)findViewById(R.id.status_label_2)).setText(element);
	}
}
