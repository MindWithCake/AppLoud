package com.ilariosanseverino.apploud;

import static com.ilariosanseverino.apploud.AppListActivity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
	private final static int LIST_SCROLL_TRESHOLD = 50;
	private ArrayList<AppListItem> appList = new ArrayList<AppListItem>();
	private int height;
	private Map<String, Integer> indexMap = null; //TODO 

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets. */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks. */
	private Callbacks mCallbacks = sDummyCallbacks;

	/** The current activated item position. Only used on tablets. */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections. */
	public interface Callbacks {
		/** Callback for when an item has been selected. */
		public void onItemSelected(long id);
		public Map<String, Integer> getIndexMap();
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity. */
	private static Callbacks sDummyCallbacks = new Callbacks(){
		@Override public void onItemSelected(long id){}
		@Override public Map<String, Integer> getIndexMap(){
			return null;
		}
	};
	
//	private final BroadcastReceiver indexReceiver = new IndexReceiver();

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public AppListFragment(){}

	@Override
	public void onCreate(Bundle savedInstanceState){
		Log.i("Fragment", "onCreate");
		super.onCreate(savedInstanceState);

		savedInstanceState = getArguments();
		if (savedInstanceState != null && savedInstanceState.containsKey(LIST_ARG)){
			appList = savedInstanceState.getParcelableArrayList(LIST_ARG);
		}

		setListAdapter(new AppListAdapter(getActivity(), appList));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		Log.i("Fragment", "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_list, container, false);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		height = displaymetrics.heightPixels;
		height -= getActivity().getActionBar().getHeight();
		height -= getResources().getDimensionPixelSize(
				getResources().getIdentifier("status_bar_height", "dimen", "android"));
		
		if(indexMap != null)
			drawIndex(rootView);
		
		return rootView;
	}
	
	private void drawIndex(View rootView){
		LinearLayout indexLayout = (LinearLayout)rootView.findViewById(R.id.side_index);
		List<String> indexList = new ArrayList<String>(indexMap.keySet());
		int elemH = (height - indexList.size()) / indexList.size();
		LayoutInflater inflater = getActivity().getLayoutInflater();

		TextView textView;
		for(String index: indexList) {
			textView = (TextView)inflater.inflate(R.layout.side_index_item, null);
			textView.setText(index);
			textView.setClickable(true);
			textView.setOnClickListener(new IndexClickListener());
			textView.setHeight(elemH);
			indexLayout.addView(textView);
		}
	}

	@Override
	public void onAttach(Activity act){
		Log.i("Fragment", "onAttach");
		super.onAttach(act);
		if(!(act instanceof Callbacks))
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		
		mCallbacks = (Callbacks)act;
		indexMap = mCallbacks.getIndexMap();
		/*if(indexMap == null){
			LocalBroadcastManager.getInstance(getActivity()).registerReceiver(indexReceiver,
					new IntentFilter(AppListActivity.DISPLAY_INDEX));
		}*/
	}

	@Override
	public void onDetach(){
		Log.i("Fragment", "onDetach");
		super.onDetach();
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id){
		super.onListItemClick(listView, view, position, id);
		mCallbacks.onItemSelected(id);
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		Log.i("Fragment", "onSave");
		super.onSaveInstanceState(outState);
		if(mActivatedPosition != ListView.INVALID_POSITION)
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		outState.putParcelableArrayList(LIST_ARG, appList);
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

	private class IndexClickListener implements OnClickListener{
		public void onClick(View v){
			TextView selectedIndex = (TextView)v;
			int index = mCallbacks.getIndexMap().get(selectedIndex.getText());
			ListView lv = getListView();
			
			int currentIndex = lv.getFirstVisiblePosition();
			if(index - currentIndex < -LIST_SCROLL_TRESHOLD) //scroll up
				getListView().setSelection(index + LIST_SCROLL_TRESHOLD);
			else if(index - currentIndex > LIST_SCROLL_TRESHOLD)
				getListView().setSelection(index - LIST_SCROLL_TRESHOLD);
			
			getListView().smoothScrollToPositionFromTop(index, 10, 500);
		}
	}
	
	/*private class IndexReceiver extends BroadcastReceiver{
		public void onReceive(Context context, Intent intent){
			Log.i("ListFrag", "Broadcast di indice pronto ricevuto");
			LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(indexReceiver);
//			drawIndex(getView());
		}
	}*/
}
