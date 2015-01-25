package com.ilariosanseverino.apploud.ui;

import android.os.Parcel;
import android.os.Parcelable;

public class AppListItem implements Parcelable {
	private final String appname;
	private final String apppkg;
	
	public static final Parcelable.Creator<AppListItem> CREATOR = 
			new Parcelable.Creator<AppListItem>() {
		public AppListItem createFromParcel(Parcel in) {
			return new AppListItem(in);
		}

        public AppListItem[] newArray(int size) {
            return new AppListItem[size];
        }
	};

	public AppListItem(String name, String pkg){
		this(pkg+" "+name);
	}
	
	private AppListItem(Parcel parcel){
		this(parcel.readString());
	}
	
	public AppListItem(String qualifiedName){
		int separator = qualifiedName.indexOf(' ');
		apppkg = qualifiedName.substring(0, separator);
		appname = qualifiedName.substring(separator+1);
	}

	public String appName(){
		return appname;
	}
	
	public String appPkg(){
		return apppkg;
	}
	
	public String toString(){
		return apppkg+"."+appname;
	}

	@Override
	public int describeContents(){
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags){
		dest.writeString(apppkg+" "+appname);
	}
}
