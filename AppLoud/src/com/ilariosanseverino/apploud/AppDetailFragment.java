package com.ilariosanseverino.apploud;

import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ilariosanseverino.apploud.ui.AppListItem;
import com.ilariosanseverino.apploud.ui.widgets.IgnorableTuning;

/**
 * A fragment representing a single App detail screen. This fragment is either
 * contained in a {@link AppListActivity} in two-pane mode (on tablets) or a
 * {@link AppDetailActivity} on handsets.
 */
public class AppDetailFragment extends Fragment {
	
	private AppListItem item;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public AppDetailFragment(){}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		savedInstanceState = getArguments();
		if(savedInstanceState.containsKey(AppListActivity.ITEM_ARG))
			item = savedInstanceState.getParcelable(AppListActivity.ITEM_ARG);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.fragment_app_detail,
				container, false);

		((TextView)rootView.findViewById(R.id.app_detail)).setText(
				item != null? item.appName() : "No details available");

		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		AppDetailActivity act = (AppDetailActivity)getActivity();
		AudioManager am = (AudioManager)act.getSystemService(Context.AUDIO_SERVICE);
		View view = getView();
		for(AudioSource src: AudioSource.values()){
			IgnorableTuning tun = (IgnorableTuning)view.findViewById(src.checkId());
			tun.setOnActivationChangedListener(act);
			SeekBar seekBar = (SeekBar)tun.findViewById(src.seekId());
			seekBar.setOnSeekBarChangeListener(act);
			seekBar.setMax(am.getStreamMaxVolume(src.audioStream()));
		}
		for(int i: new int[]{R.id.roto_tuning, R.id.gps_tuning}){
			IgnorableTuning tun = (IgnorableTuning)view.findViewById(i);
			tun.setOnActivationChangedListener(act.new ToggleButtonActivator());
		}
	}
}
