package com.ilariosanseverino.apploud;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.ilariosanseverino.apploud.ui.AppListAdapter;
import com.ilariosanseverino.apploud.ui.AppListItem;

/**
 * A list fragment representing a list of Apps. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link AppDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class AppListFragment extends ListFragment {
	
	private ArrayList<AppListItem> appList = new ArrayList<AppListItem>();

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(long id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks(){
		@Override
		public void onItemSelected(long id){
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public AppListFragment(){
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		savedInstanceState = getArguments();
		if (savedInstanceState != null && savedInstanceState.containsKey(AppListActivity.LIST_ARG))
			appList = savedInstanceState.getParcelableArrayList(AppListActivity.LIST_ARG);          
		
		setListAdapter(new AppListAdapter(getActivity(), appList));
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		
		if(!(activity instanceof Callbacks))
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach(){
		super.onDetach();
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id){
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected(id);
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		if(mActivatedPosition != ListView.INVALID_POSITION)
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick){
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(activateOnItemClick?
				ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
	}
}
