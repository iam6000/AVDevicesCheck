package com.example.devicechecker;

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
	
	private String mICResult ; 
	private String mPcmDevicesResult ; 
	private String mVideoDevicesResult ;

	
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.checkresult);
		mContext = DevicePermissionActivity.this;
		mPcmTextView = (TextView)findViewById(R.id.AudioName);
		mVideoTestView = (TextView)findViewById(R.id.VideoName);
		mICNameView = (TextView)findViewById(R.id.ICName);
		
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
						mVideoDevicesResult =  mDeviceScaner.getVideoDevices()  + "\t     " +  videoResult.getErrorMSG();
					}			
				}
				else 
				{
					mVideoDevicesResult = "�޷���⵽��Ƶ�豸";
				}
				
				
				// Check IC TYPE ; 
				mICResult = android.os.Build.DEVICE;
				
				mICNameView.setText(mICResult);
				mPcmTextView.setText(mPcmDevicesResult);
				mVideoTestView.setText(mVideoDevicesResult);						
				
			}
		});		
		builder.create().show();	
		
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
