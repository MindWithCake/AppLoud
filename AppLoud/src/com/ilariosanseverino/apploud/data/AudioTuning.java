package com.ilariosanseverino.apploud.data;

import static android.media.AudioManager.STREAM_MUSIC;
import static android.media.AudioManager.STREAM_NOTIFICATION;
import static android.media.AudioManager.STREAM_RING;
import static android.media.AudioManager.STREAM_SYSTEM;
import static com.ilariosanseverino.apploud.data.TuningControl.MUSIC;
import static com.ilariosanseverino.apploud.data.TuningControl.NOTY;
import static com.ilariosanseverino.apploud.data.TuningControl.RINGER;
import static com.ilariosanseverino.apploud.data.TuningControl.SYS;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_NAME_MUSIC_STREAM;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_NAME_NOTIFICATION_STREAM;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_NAME_RING_STREAM;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_NAME_SYSTEM_STREAM;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;

import com.ilariosanseverino.apploud.service.VolumeFeedback;

public class AudioTuning extends TuningParameter {
	
	private int stream;
	
	private AudioTuning(TuningControl ctrl, String value, int audioStream){
		super(ctrl, value);
		stream = audioStream;
	}

	@Override
	protected boolean doApplyTuning(Context ctx, String val){
		if(!isEnabled(val))
			return false;
		
		int intValue = Integer.parseInt(val);
		AudioManager am = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
		am.setStreamVolume(stream, intValue, flags(ctx));
		return true;
	}

	@Override
	protected String getActualValue(Context ctx){
		AudioManager am = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
		int vol = am.getStreamVolume(stream);
		return Integer.toString(vol);
	}

	@Override
	public boolean isParameterEnabled(){
		return isEnabled(getValue());
	}
	
	private boolean isEnabled(String val){
		return (val != null && Integer.parseInt(val) >= 0);
	}
	
	private int flags(Context ctx){
		SharedPreferences prefs = PreferenceManager.
				getDefaultSharedPreferences(ctx.getApplicationContext());
		int flag = 0;
		for(VolumeFeedback feed: VolumeFeedback.values()){
			if(prefs.getBoolean(feed.key, false))
				flag |= feed.flag;
		}
		return flag;
	}
	
	static class AudioTuningFactory{
		public static AudioTuning makeTuning(String streamName, String val){
			if(streamName == null)
				return null;
			
			switch(streamName){
			case COLUMN_NAME_MUSIC_STREAM:
				return new AudioTuning(MUSIC, val, STREAM_MUSIC);
			case COLUMN_NAME_NOTIFICATION_STREAM:
				return new AudioTuning(NOTY, val, STREAM_NOTIFICATION);
			case COLUMN_NAME_RING_STREAM:
				return new AudioTuning(RINGER, val, STREAM_RING);
			case COLUMN_NAME_SYSTEM_STREAM:
				return new AudioTuning(SYS, val, STREAM_SYSTEM);
			default:
				return null;
			}
		}
	}
}
