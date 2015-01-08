package com.ilariosanseverino.apploud;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ilariosanseverino.apploud.UI.AppListItem;

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
	public AppDetailFragment(){
	}

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
				item != null? item.getAppName() : "No details available");

		return rootView;
	}
}
