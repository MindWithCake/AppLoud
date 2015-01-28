package com.ilariosanseverino.apploud;

import android.media.AudioManager;

import com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry;

public enum AudioSource{
	RING (AudioManager.STREAM_RING, AppEntry.COLUMN_NAME_RING_STREAM,
			R.id.ring_tuning, R.id.ring_bar),
	MEDIA (AudioManager.STREAM_MUSIC, AppEntry.COLUMN_NAME_MUSIC_STREAM,
			R.id.media_tuning, R.id.media_bar),
	NOTIFY (AudioManager.STREAM_NOTIFICATION, AppEntry.COLUMN_NAME_NOTIFICATION_STREAM,
			R.id.notify_tuning, R.id.notify_bar),
	SYS (AudioManager.STREAM_SYSTEM, AppEntry.COLUMN_NAME_SYSTEM_STREAM,
			R.id.sys_tuning, R.id.sys_bar);
	
	private final int audiostream, checkid, seekid;
	private final String column;
	
	private AudioSource(int amStream, String dbColumn, int checkBoxId, int seekBarId){
		audiostream = amStream;
		checkid = checkBoxId;
		seekid = seekBarId;
		column = dbColumn;
	}

	public int audioStream(){
		return audiostream;
	}

	public int checkId(){
		return checkid;
	}

	public int seekId(){
		return seekid;
	}

	public String columnName(){
		return column;
	}
}
