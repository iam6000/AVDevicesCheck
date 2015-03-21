package com.example.devicechecker;

import java.io.File;
import java.io.RandomAccessFile;

import android.media.AudioRecord;
import android.os.Environment;
import android.util.Log;

/**
 * @author sunhong
 * 
 */



public class AudioRecordWrapper {
	
	// native func to send Data to TinyAlsaAudio
	//public native void  doSendPcm(byte[] data, int length);
	
	
	private AudioRecord mAudioRecord;

	public static int getMinBufferSize(int sampleRateInHz, int channelConfig,
			int audioFormat) {
		Log.e("AudioRecordWrapper", "getMinBufferSize");
		return AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig,
				audioFormat);
	}

	/*
	 * public void init(int audioSource, int sampleRateInHz, int channelConfig,
	 * int audioFormat, int bufferSizeInBytes) { Log.e("init", "init");
	 * mAudioRecord = new AudioRecord(audioSource, sampleRateInHz,
	 * channelConfig, audioFormat, bufferSizeInBytes); }
	 */
	public AudioRecordWrapper(int audioSource, int sampleRateInHz,
			int channelConfig, int audioFormat, int bufferSizeInBytes) {
		Log.e("AudioRecordWrapper", "AudioRecordWrapper");
		mAudioRecord = new AudioRecord(audioSource, sampleRateInHz,
				channelConfig, audioFormat, bufferSizeInBytes);
	}

	public int getState() {
		Log.e("AudioRecordWrapper", "getState");
		return mAudioRecord.getState();
	}

	public int read(byte[] audioData, int offsetInBytes, int sizeInBytes) {
		Log.e("AudioRecordWrapper", "read");
		int ret;
		ret = mAudioRecord.read(audioData, offsetInBytes, sizeInBytes);
		// call native func send pcm data to native level
		//doSendPcm(audioData,sizeInBytes);
		WritePcmdataToFile("audio.pcm", audioData);
		return ret;
	}

	public void startRecording() {
		Log.e("AudioRecordWrapper", "startRecording");
		mAudioRecord.startRecording();
	}

	private boolean WritePcmdataToFile(String fileName, byte[] data) {
		try {
			{
				File targetFile = new File("/skydir/" + fileName);
				// if (targetFile.exists()) {
				// targetFile.delete();
				// }
				RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");
				long fileLength = raf.length();
				raf.seek(fileLength);
				raf.write(data);
				raf.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

}
