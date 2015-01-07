package com.ilariosanseverino.apploud.UI;

import android.os.Parcel;
import android.os.Parcelable;

public class AppListItem implements Parcelable {
	private String appName;
	private String appPkg;
	
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
		this(pkg+"."+name);
	}
	
	private AppListItem(Parcel parcel){
		this(parcel.readString());
	}
	
	public AppListItem(String qualifiedName){
		int dot = qualifiedName.lastIndexOf('.');
		appPkg = qualifiedName.substring(0, dot);
		appName = qualifiedName.substring(dot+1);
	}

	public String getAppName(){
		return appName;
	}

	public void setAppName(String appName){
		this.appName = appName;
	}
	
	public String getAppPkg(){
		return appPkg;
	}

	public void setAppPkg(String appPkg){
		this.appPkg = appPkg;
	}
	
	public String toString(){
		return /*appPkg+"."+*/appName;
	}

	@Override
	public int describeContents(){
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags){
		dest.writeString(appPkg+"."+appName);
	}
}
