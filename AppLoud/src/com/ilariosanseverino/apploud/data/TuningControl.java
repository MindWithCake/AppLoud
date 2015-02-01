package com.ilariosanseverino.apploud.data;

import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.*;

import com.ilariosanseverino.apploud.R.id;

public enum TuningControl{
	RINGER(COLUMN_NAME_RING_STREAM, id.ring_tuning, id.ring_bar, "Ring_key"),
	NOTY(COLUMN_NAME_NOTIFICATION_STREAM, id.notify_tuning, id.notify_bar, "Notify_key"),
	MUSIC(COLUMN_NAME_MUSIC_STREAM, id.media_tuning, id.media_bar,"Music_key"),
	SYS(COLUMN_NAME_SYSTEM_STREAM, id.sys_tuning, id.sys_bar, "Sys_key"),
	ROTO(COLUMN_NAME_ROTATION, id.roto_tuning, id.roto_toggle, "Roto_key"),
	GPS(COLUMN_NAME_GPS, id.gps_tuning, id.gps_toggle, "GPS_key");
	
	public final String column;
	public final int widgetId;
	public final int customViewId;
	protected final String prefKey;
	
	private TuningControl(String dbcol, int widget, int view, String key){
		column = dbcol;
		widgetId = widget;
		customViewId = view;
		prefKey = key;
	}
}
