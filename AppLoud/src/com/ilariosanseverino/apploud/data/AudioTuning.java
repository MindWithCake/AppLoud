package com.ilariosanseverino.apploud.data;

import static android.media.AudioManager.STREAM_MUSIC;
import static android.media.AudioManager.STREAM_NOTIFICATION;
import static android.media.AudioManager.STREAM_RING;
import static android.media.AudioManager.STREAM_SYSTEM;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_NAME_MUSIC_STREAM;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_NAME_NOTIFICATION_STREAM;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_NAME_RING_STREAM;
import static com.ilariosanseverino.apploud.db.AppVolumeContract.AppEntry.COLUMN_NAME_SYSTEM_STREAM;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ilariosanseverino.apploud.service.VolumeFeedback;

public class AudioTuning extends TuningParameter {
	
	private int stream;
	
	private AudioTuning(String key, int audioStream){
		this(key, null, audioStream);
	}
	
	private AudioTuning(String key, String value, int audioStream){
		super(key, value);
		stream = audioStream;
	}

	@Override
	protected boolean doApplyTuning(Context ctx, String val){
		Log.i("AudioTun", "Applicando valore "+val);
		
		if(val == null){
			Log.i("AudioTun", "il valore è null, ritorno false");
			return false;
		}
		
		Log.i("AudioTun", "Val non è null");
		
		int intValue = Integer.parseInt(val);
		if(intValue < 0){
			Log.i("AudioTun", "il valore "+intValue+" è negativo, ritorno false");
			return false;
		}
		
		Log.i("AudioTun", "Applicazione avvenuta, ritorno true");
		
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
		public static AudioTuning makeTuning(String streamName){
			return makeTuning(streamName, null);
		}
		
		public static AudioTuning makeTuning(String streamName, String val){
			if(streamName == null)
				return null;
			
			switch(streamName){
			case COLUMN_NAME_MUSIC_STREAM:
				return new AudioTuning("Music_stream_value", val, STREAM_MUSIC);
			case COLUMN_NAME_NOTIFICATION_STREAM:
				return new AudioTuning("Notify_dtream_value", val, STREAM_NOTIFICATION);
			case COLUMN_NAME_RING_STREAM:
				return new AudioTuning("Ring_stream_value", val, STREAM_RING);
			case COLUMN_NAME_SYSTEM_STREAM:
				return new AudioTuning("Sys_stream_value", val, STREAM_SYSTEM);
			default:
				return null;
			}
		}
	}
}
