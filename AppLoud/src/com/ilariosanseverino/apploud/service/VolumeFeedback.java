package com.ilariosanseverino.apploud.service;

import android.media.AudioManager;

public enum VolumeFeedback{
	VISUAL ("visual_feed", AudioManager.FLAG_SHOW_UI),
	VIBRO ("vibro_feed", AudioManager.FLAG_VIBRATE),
	AUDIO ("audio_feed", AudioManager.FLAG_PLAY_SOUND);
	
	public final String key;
	public final int flag;
	
	private VolumeFeedback(String key, int flag){
		this.key = key;
		this.flag = flag;
	}
}
