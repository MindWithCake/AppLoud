package com.ilariosanseverino.apploud.data;

import static android.media.AudioManager.*;
import static com.ilariosanseverino.apploud.data.TuningControl.*;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ilariosanseverino.apploud.service.VolumeFeedback;

public class AudioTuning extends TuningParameter {
	
	private int stream;
	
	private AudioTuning(TuningControl ctrl, String value, int audioStream){
		super(ctrl, value);
		stream = audioStream;
	}

	@Override
	protected boolean doApplyTuning(Context ctx, String val){
		AudioManager am = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
		Log.w("Audio-"+ctrl.column, "mi appresto ad applicare il settaggio "+val);
		if(am.getRingerMode() != AudioManager.RINGER_MODE_NORMAL || !isEnabled(val))
			return false;
		
		int intValue = Integer.parseInt(val);
		Log.w("Audio-"+ctrl.column, "Il settaggio deve essere applicato: "+intValue);
		am.setStreamVolume(stream, intValue, flags(ctx));
		return true;
	}

	@Override
	protected String getCurrentValue(Context ctx){
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
