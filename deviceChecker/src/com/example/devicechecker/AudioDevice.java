package com.example.devicechecker;

import android.media.AudioFormat;

public class AudioDevice {	
	
	static final int frequency = 8000 ;  // 8K 采样率
	static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;   // 16位
	static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO ; // 单声道
	boolean doRecordAndPlay = true ; // 由Button 触发更改为false
		
		
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}

}
