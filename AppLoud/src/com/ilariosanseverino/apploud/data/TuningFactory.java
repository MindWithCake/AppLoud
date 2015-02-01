package com.ilariosanseverino.apploud.data;

import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.*;

public class TuningFactory {
	
	private TuningFactory(){}
	
	public static TuningParameter buildParameter(String source, String value){
		if(source == null)
			return null;
		switch(source){
		case COLUMN_NAME_MUSIC_STREAM:
		case COLUMN_NAME_NOTIFICATION_STREAM:
		case COLUMN_NAME_RING_STREAM:
		case COLUMN_NAME_SYSTEM_STREAM:
			return AudioTuning.AudioTuningFactory.makeTuning(source, value);
		case COLUMN_NAME_ROTATION:
			return new RotoParameter(value);
		case COLUMN_NAME_GPS:
			return new GpsParameter(value);
		default:
			return null;
		}
	}
	
	public static TuningParameter buildParameter(String source){
		return buildParameter(source, null);
	}
}
