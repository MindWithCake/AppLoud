package com.ilariosanseverino.apploud.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ilariosanseverino.apploud.R;

public class AppListAdapter extends ArrayAdapter<AppListItem> {
	
	private LayoutInflater inflater;
	private PackageManager pm;

	public AppListAdapter(Context context){
		this(context, new ArrayList<AppListItem>());
	}

	public AppListAdapter(Context context, AppListItem[] objects){
		this(context, new ArrayList<AppListItem>(Arrays.asList(objects)));
	}

	public AppListAdapter(Context context, List<AppListItem> objects){
		super(context, R.layout.app_list_element, objects);
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		pm = context.getPackageManager();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AppRowViewHolder holder;
		
		if(convertView == null){
			convertView = inflater.inflate(R.layout.app_list_element, parent, false);
			holder = new AppRowViewHolder();
			holder.appText = (TextView) convertView.findViewById(R.id.appNameLine);
			holder.packageText = (TextView) convertView.findViewById(R.id.packageLine);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		}
		else
			holder = (AppRowViewHolder)convertView.getTag();

		AppListItem item = getItem(position);
		
		holder.appText.setText(item.appName());
		holder.packageText.setText(item.appPkg());
		try{
			holder.icon.setImageDrawable(pm.getApplicationIcon(item.appPkg()));
		}
		catch(NameNotFoundException e){
			Log.d("Adapter", "package non trovato: "+item.appPkg());
			holder.icon.setImageDrawable(null);
		}
		
		return convertView;
	}
	
	static class AppRowViewHolder {
		TextView appText;
		TextView packageText;
		ImageView icon;
	}
}
