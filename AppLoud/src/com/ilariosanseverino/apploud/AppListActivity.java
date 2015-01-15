package com.ilariosanseverino.apploud;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.ilariosanseverino.apploud.UI.AppListDataModel;
import com.ilariosanseverino.apploud.UI.AppListItem;
import com.ilariosanseverino.apploud.service.BackgroundConnection;
import com.ilariosanseverino.apploud.service.BackgroundService;
import com.ilariosanseverino.apploud.service.IBackgroundServiceBinder;

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
public class AppListActivity extends FragmentActivity implements AppListFragment.Callbacks {
	private boolean mTwoPane; //Whether or not the activity is in two-pane mode
	private Intent serviceIntent;
	private AppListDataModel dataModel;
	private IBackgroundServiceBinder binder;
	private final BackgroundConnection connection = new BackgroundConnection(){
		@Override
		public void doOnServiceConnected(){
			Log.i("ListActivity", "servizio della lista connesso");
			dataModel = new AppListDataModel(binder);
			FragmentManager fragmentManager = getSupportFragmentManager();
			setContentView(R.layout.activity_app_list);

			if(findViewById(R.id.app_detail_container) != null){
				mTwoPane = true;
				((AppListFragment)fragmentManager.findFragmentById(R.id.app_list)).setActivateOnItemClick(true);

				AppDetailFragment detFrag = (AppDetailFragment)fragmentManager.findFragmentByTag(DETAIL_FRAG_TAG);
				if (detFrag == null) {
					detFrag = new AppDetailFragment();
					Bundle args = new Bundle();
					args.putParcelable(ITEM_ARG, new AppListItem("com.ilariosanseverino.apploud AppLoud"));
					detFrag.setArguments(args);
					fragmentManager.beginTransaction().replace(R.id.app_detail_container,
							detFrag, DETAIL_FRAG_TAG).commit();
				}
			}

			AppListFragment listFrag = (AppListFragment)fragmentManager.findFragmentByTag(LIST_FRAG_TAG);
			if(listFrag == null){
				listFrag = new AppListFragment();
				Bundle arguments = new Bundle();
				arguments.putParcelableArrayList(LIST_ARG, dataModel.getAppList());
				listFrag.setArguments(arguments);
				fragmentManager.beginTransaction().replace(R.id.app_list, listFrag, LIST_FRAG_TAG).commit();
			}
			unbindService(connection);
		}

		@Override
		public void setBinder(IBackgroundServiceBinder binder){
			AppListActivity.this.binder = binder;
		}
	};
	
	public final static String DETAIL_FRAG_TAG = "Detail";
	public final static String LIST_FRAG_TAG = "List";
	public final static String ITEM_ARG = "appItem";
	public final static String LIST_ARG = "apps";

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		serviceIntent = new Intent(this, BackgroundService.class);
		Log.d("Activity", "avvio il servizio: "+startService(serviceIntent));
		bindService(serviceIntent, connection, BIND_ABOVE_CLIENT|BIND_AUTO_CREATE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * Callback method from {@link AppListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(long id){
		if(mTwoPane){
			Bundle arguments = new Bundle();
			arguments.putParcelable(ITEM_ARG, dataModel.getAppList().get((int)id));
			AppDetailFragment fragment = new AppDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().replace(
					R.id.app_detail_container, fragment).commit();
		}
		else{
			Intent detailIntent = new Intent(this, AppDetailActivity.class);
			AppListItem item = dataModel.getAppList().get((int)id);
			detailIntent.putExtra(ITEM_ARG, item);
			startActivity(detailIntent);
		}
	}
}
