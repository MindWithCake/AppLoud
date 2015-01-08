package com.ilariosanseverino.apploud.db;

import android.provider.BaseColumns;

public final class AppVolumeContract {

	private AppVolumeContract(){}

	public static abstract class AppEntry implements BaseColumns {
		public static final String TABLE_NAME = "applist";
		public static final String COLUMN_NAME_APPNAME = "name";
		public static final String COLUMN_NAME_PACKAGE = "package";
		public static final String COLUMN_NAME_MUSIC_STREAM = "music";
		public static final String COLUMN_NAME_NOTIFICATION_STREAM = "notification";
		public static final String COLUMN_NAME_RING_STREAM = "ringtones";
		public static final String COLUMN_NAME_SYSTEM_STREAM = "system";
		
		public static final String COLUMN_TYPE_APPNAME = " TEXT NOT NULL";
		public static final String COLUMN_TYPE_PACKAGE = COLUMN_TYPE_APPNAME;
		public static final String COLUMN_TYPE_RING_STREAM = " INTEGER";
		public static final String COLUMN_TYPE_MUSIC_STREAM = COLUMN_TYPE_RING_STREAM;
		public static final String COLUMN_TYPE_NOTIFICATION_STREAM = COLUMN_TYPE_RING_STREAM;
		public static final String COLUMN_TYPE_SYSTEM_STREAM = COLUMN_TYPE_RING_STREAM;
	}
}
