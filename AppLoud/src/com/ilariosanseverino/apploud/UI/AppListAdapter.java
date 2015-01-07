package com.ilariosanseverino.apploud.UI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppListAdapter extends ArrayAdapter<AppListItem> {
	
	private Context context;

	public AppListAdapter(Context context, int resource){
		this(context, resource, new ArrayList<AppListItem>());
	}

	public AppListAdapter(Context context, int resource, AppListItem[] objects){
		this(context, resource, new ArrayList<AppListItem>(Arrays.asList(objects)));
	}

	public AppListAdapter(Context context, int resource, List<AppListItem> objects){
		super(context, resource, objects);
		this.context = context;
	}
	
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		LayoutInflater inflater = (LayoutInflater) context
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
//		TextView textView = (TextView) rowView.findViewById(R.id.label);
//		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
//		textView.setText(values[position]);
//		// change the icon for Windows and iPhone
//		String s = values[position];
//		if (s.startsWith("iPhone")) {
//			imageView.setImageResource(R.drawable.no);
//		} else {
//			imageView.setImageResource(R.drawable.ok);
//		}
//
//		return rowView;
//	}
}
