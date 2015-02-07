package com.ilariosanseverino.apploud;

import java.util.ArrayList;
import java.util.Map;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ilariosanseverino.apploud.service.BackgroundService;
import com.ilariosanseverino.apploud.service.FillerThread;
import com.ilariosanseverino.apploud.ui.AppListDataModel;
import com.ilariosanseverino.apploud.ui.AppListItem;

/**
 * An activity representing a list of Apps. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link AppDetailActivity} representing item details. On tablets, the activity
 * presents the list of items and item details side-by-side using two vertical
 * panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link AppListFragment} and the item details (if present) is a
 * {@link AppDetailFragment}.
 * <p>
 * This activity also implements the required {@link AppListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class AppListActivity extends AppLoudMenuActivity implements AppListFragment.Callbacks {
	private boolean mTwoPane; //Whether or not the activity is in two-pane mode
	private AppListDataModel dataModel;
	private ProgressDialog progr;
	public final static String DETAIL_FRAG_TAG = "Detail";
	public final static String LIST_FRAG_TAG = "List";
	public final static String ITEM_ARG = "appItem";
	public final static String LIST_ARG = "apps";
//	public static final String DISPLAY_INDEX = "com.ilariosanseverino.apploud.AppListActivity index";
	private String searchString = null;
	private ArrayList<AppListItem> filteredItems = null;

	private final BroadcastReceiver dbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(FillerThread.DB_FILLED_EVENT.equals(intent.getAction())){
				Log.i("Activity", "onBroadcastReceive");
				bindBackgroundService();
				LocalBroadcastManager.getInstance(AppListActivity.this).unregisterReceiver(dbReceiver);
			}
		}
	};

	@Override
	protected void doOnServiceConnected(){
		Log.i("Activity", "onServiceConnected");
		dataModel = new AppListDataModel(binder);
		if(searchString != null)
			dataModel.filterData(AppListActivity.this, searchString);
		Log.w("Activity", "datamodel non è null");

		FragmentManager fragmentManager = getFragmentManager();
		setContentView(R.layout.activity_app_list);

		if(findViewById(R.id.app_detail_container) != null){
			mTwoPane = true;
			((AppListFragment)fragmentManager.findFragmentById(R.id.app_list)).
			setActivateOnItemClick(true);

			AppDetailFragment detFrag = (AppDetailFragment)fragmentManager.
					findFragmentByTag(DETAIL_FRAG_TAG);
			if (detFrag == null) {
				detFrag = new AppDetailFragment();
				Bundle args = new Bundle();
				args.putParcelable(ITEM_ARG,
						new AppListItem("com.ilariosanseverino.apploud AppLoud"));
				detFrag.setArguments(args);
				fragmentManager.beginTransaction().replace(R.id.app_detail_container,
						detFrag, DETAIL_FRAG_TAG).commit();
			}
		}

		showFragment(fragmentManager.findFragmentByTag(LIST_FRAG_TAG),
				dataModel.getAppList());

		/*Intent indexIntent = new Intent(DISPLAY_INDEX);
		LocalBroadcastManager.getInstance(AppListActivity.this).
		sendBroadcast(indexIntent);*/

		unbindService(connection);

		if(progr.isShowing())
			progr.dismiss();
	}

	private void showFragment(Fragment recycleFrag, ArrayList<AppListItem> content){
		FragmentManager fragmentManager = getFragmentManager();
		if(recycleFrag == null){
			recycleFrag = new AppListFragment();
			Bundle arguments = new Bundle();
			arguments.putParcelableArrayList(LIST_ARG, content);
			recycleFrag.setArguments(arguments);
		}
		fragmentManager.beginTransaction().replace(R.id.app_list, recycleFrag, LIST_FRAG_TAG).commit();
	}

	public void showFilteredResult(ArrayList<AppListItem> items){
		filteredItems = items;
		showFragment(null, filteredItems);
		searchString = null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		Log.i("Activity", "onCreate");
		super.onCreate(/*savedInstanceState*/null);

		Intent intent = getIntent();
		if(Intent.ACTION_SEARCH.equals(intent.getAction()))
			searchString = intent.getStringExtra(SearchManager.QUERY);

		if(savedInstanceState == null){
			Log.i("Activity", "onCreate - ex novo");
			LocalBroadcastManager.getInstance(this).registerReceiver(dbReceiver,
					new IntentFilter(FillerThread.DB_FILLED_EVENT));
			startService(new Intent(this, BackgroundService.class));
		}
		else{
			Log.i("Activity", "onCreate - riciclo");
			bindBackgroundService();
		}
		
		progr = ProgressDialog.show(this,
				getResources().getString(R.string.progress_load_title),
				getResources().getString(R.string.progress_load_summ));
	}

	@Override
	public void onPause(){
		Log.i("Activity", "onPause");
		super.onPause();
	}
	
	@Override
	public void onResume(){
		Log.i("Activity", "onResume");
		super.onResume();
	}
	
	@Override
	public void onDestroy(){
		Log.i("Activity", "onDestroy");
		super.onDestroy();
	}

	/**
	 * Callback method from {@link AppListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(long id){
		AppListItem item = filteredItems != null?
				filteredItems.get((int)id) : dataModel.getAppList().get((int)id);
		
		if(mTwoPane){
			Bundle arguments = new Bundle();
			arguments.putParcelable(ITEM_ARG, item);
			AppDetailFragment fragment = new AppDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction().replace(
					R.id.app_detail_container, fragment).commit();
		}
		else{
			Intent detailIntent = new Intent(this, AppDetailActivity.class);
			detailIntent.putExtra(ITEM_ARG, item);
			startActivity(detailIntent);
		}
	}

	@Override
	protected int getContainerID(){
		return R.id.app_list;
	}

	@Override
	public Map<String, Integer> getIndexMap(){
		if(dataModel != null)
			return dataModel.getMapIndex();
		Log.w("Activity", "datamodel è null e lo ritorno al frammento");
		return null;
	}
}
