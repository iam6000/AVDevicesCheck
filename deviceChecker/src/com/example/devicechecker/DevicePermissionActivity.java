package com.example.devicechecker;

import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.TextView;

public class DevicePermissionActivity extends Activity {	
	protected Context mContext;
	
	private DeviceScan mDeviceScaner; 
	
	private DeviceErrorMsg pcmResult ; 
	private DeviceErrorMsg videoResult ; 
	
	private TextView mPcmTextView ; 
	private TextView mVideoTestView ;
	private TextView mICNameView;
	private TextView mAndroidCameraView ; 
	private TextView mAudioRecordView ; 
	private TextView mAudioTrackView ; 
	
	
	private String mICResult ; 
	private String mTVType ; 
	private String mPcmDevicesResult ; 
	private String mVideoDevicesResult ;
	private String mAndroidCameraResult ; 
	private String mAudioRecordResult ; 
	private String mAudioTrackResult ; 

	
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.checkresult);
		mContext = DevicePermissionActivity.this;
		mPcmTextView = (TextView)findViewById(R.id.AudioName);
		mVideoTestView = (TextView)findViewById(R.id.VideoName);
		mICNameView = (TextView)findViewById(R.id.ICName);
		mAndroidCameraView = (TextView)findViewById(R.id.AndroidVideoName);
		mAudioRecordView = (TextView)findViewById(R.id.AudioRecordName);
		mAudioTrackView = (TextView)findViewById(R.id.AudioTrackName);
		
		
		mDeviceScaner = new DeviceScan(DevicePermissionActivity.this);
		mDeviceScaner.beginScan() ; 
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("USB�豸����");
		builder.setMessage("�����һ������ͷ�β������\n ������ͷ�Ѳ��ϣ���ε����ٲ���\n ������ͷδ���ϣ���ֱ�Ӳ���\n");
		builder.setCancelable(false);	

		
		builder.setPositiveButton("����ɲ�β���,���Ѳ�������ͷ", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub								
				// stop The Scan thread 
				mDeviceScaner.setdoPcmDevicesScan(false); 
				mDeviceScaner.setdoVideoDevicesScan(false);
				System.out.println("Devices Operated Success" );
				
				// show it wait the confirm button is down 
				if(mDeviceScaner.getPcmDevices() != null )
				{
					System.out.println("start checkPCMPermission");	
					pcmResult = mDeviceScaner.checkPCMPermission(mDeviceScaner.getPcmDevices());
					// �ж�Result����
					if(pcmResult != null)
					{
						if(!pcmResult.isErrorHappen())
						{
							pcmResult.ErrorMsg = "Is useAble!";
						}
						mPcmDevicesResult = pcmResult.getErrorMSG();
					}
				}
				else 
				{
					mPcmDevicesResult = "�޷���⵽��Ƶ�豸";
				}
				
				if(mDeviceScaner.getVideoDevices() != null )
				{
					System.out.println("start checkVideoPermission");	
					// �޷��ҵ���Ƶ�豸
					videoResult = mDeviceScaner.checkVideoPermission(mDeviceScaner.getVideoDevices());
					if(videoResult != null)
					{
						
						if(!videoResult.isErrorHappen())
						{
							videoResult.ErrorMsg = "Is useAble";
						}
						mVideoDevicesResult =  mDeviceScaner.getVideoDevices()  + "\t     " +  videoResult.getErrorMSG();
					}			
				}
				else 
				{
					mVideoDevicesResult = "�޷���⵽��Ƶ�豸";
				}
							
								
				// check Android Devices
				mDeviceScaner.checkAndroidCamera(); 
				mDeviceScaner.checkAndoridTrack() ; 
				mDeviceScaner.checkAndroidRecord() ;
				
				
				if(mDeviceScaner.CameraMsg.getErrorMSG() != null )
				{
					mAndroidCameraResult = mDeviceScaner.CameraMsg.getErrorMSG() ;
				}
				else 
				{
					mAndroidCameraResult = "404 wowowowowo";
				}
				
				if(mDeviceScaner.AudioRecordMsg.getErrorMSG() != null)
				{
					mAudioRecordResult = mDeviceScaner.AudioRecordMsg.getErrorMSG() ;
				}
				else 
				{
					mAudioRecordResult = "404 wowowowo"; 
				}
				
				if(mDeviceScaner.AudioTrackMsg.getErrorMSG() != null)
				{
					mAudioTrackResult = mDeviceScaner.AudioTrackMsg.getErrorMSG() ; 
				}
				else 
				{
					mAudioTrackResult = "404 wowowow";
				}
			
				
				// Check IC TYPE ; 
				mICResult = android.os.Build.DEVICE;
				//mTVType = android.os.Build.USER;			
				
				mICNameView.setText(mICResult);
				mPcmTextView.setText(mPcmDevicesResult);
				mVideoTestView.setText(mVideoDevicesResult);	
				mAndroidCameraView.setText(mAndroidCameraResult);
				mAudioRecordView.setText(mAudioRecordResult);
				mAudioTrackView.setText(mAudioTrackResult);						
							
				// д���ļ�
				FileWriter fw  = null ;
				String filename = getDataPath(mContext,"checkResult.txt");
				try {					
					fw = new FileWriter(filename,true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
				
				// �����д���ļ�
				if(fw != null)
				{
					String platInfo = "ƽ̨��Ϣ�� " + mICResult + "\n";
					String cameraInfo = "Camera��Ϣ: \n"  
									 + "AndroidCamera�� " + mAndroidCameraResult + "\n" 
									 + "LinuxCamera: " + mVideoDevicesResult + "\n";
					String audioInfo = "Audio��Ϣ: \n"
									+"AndroidRecord :" + mAudioRecordResult + "\n" 
									+ "AndroidTrack :" + mAudioTrackResult + "\n"
									+ "TinyAlsa :"  + mPcmDevicesResult + "\n" ;
					try {
						fw.write(platInfo);
						fw.write(cameraInfo);
						fw.write(audioInfo);
						fw.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
					
				}
				
			}
		});		
		builder.create().show();	
		
	}
	
	private String getDataPath(Context context, String fileName) {		
		if (context != null && fileName != null) {
			return context.getFilesDir() + "/" + fileName;
		}
		return null;
	}
	
	
	protected void onDestroy() {
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
		// ��ת��ԭactivity ��������ǰactivity
		Intent intent = new Intent();
		intent.setClass(this,manualActivity.class);
		startActivity(intent);
		this.finish();
	}
	
}
