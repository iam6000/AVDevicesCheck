package com.example.devicechecker;

import android.media.AudioFormat;

public class AudioDevice {	
	
	static final int frequency = 8000 ;  // 8K ������
	static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;   // 16λ
	static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO ; // ������
	boolean doRecordAndPlay = true ; // ��Button ��������Ϊfalse
		
		
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}

}
