package com.example.devicechecker;

import java.util.ArrayList;
import java.util.List; 
import java.io.File;

import com.example.devicechecker.AndroidAudio.RecordPlayThread;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class TinyAlsaAudio extends AudioDevice {
	
	
	static int CardId = -1 ; 
	static int DeviceId = -1 ; 	
	// 虚拟机上没有这个路径？？？
	static String path = "/dev/snd/";
	static boolean doDevicesScan = true ; 
	String CaptureDevice = null; 
	String PlayDevice = null; 
	Context mContext;
	
	
	List<String> lastScanResult = new ArrayList<String>();
	List<String> curScanResult  = new ArrayList<String>();
	List<String> targetScanResult = new ArrayList<String>() ;
	
	public TinyAlsaAudio(Context context)
	{
		System.out.println("new TinyAlsaAudio 2222!!!!!!!!!!!!!!!!");	
		mContext = context ;
	}
	
	//一次扫描设备/dev/snd目录下的pcm设备，记录每个pcm设备名称，并更新cur	
	public void ScanProcess()
	{
		File file = new File(path);
		File[] fileList = file.listFiles();
		if(fileList == null)
		{
			 System.out.println("path error");			
		}
		
		// 若 cur为空，则直接填满
		if(curScanResult.isEmpty())
		{
			for(int i = 0 ; i < fileList.length; i++)
			{
				if(fileList[i].getName().startsWith("pcm"))
				{
					curScanResult.add(fileList[i].getName());
				}
				
			}
		}
		else  // 先clean  然后填满
		{
			curScanResult.clear();
			for(int i = 0 ; i < fileList.length; i++)
			{
				if(fileList[i].getName().startsWith("pcm"))
				{
					curScanResult.add(fileList[i].getName());
				}
			}
		}
		
		// 判断 cur 与last 确定是否查找到devices 变化
		// 若lastScanReuslt未初始化 则替换last，
		if( lastScanResult.isEmpty() )
		{
			lastScanResult.addAll(curScanResult);
		}
		else if(lastScanResult.size() == curScanResult.size())
		{
			//大小不变，认为list相同，不需要做任何修改
		}
		else //  大小不同， 剔除重复，目标pcm  入 targetlist
		{
			if(lastScanResult.size() > curScanResult.size()){
				lastScanResult.removeAll(curScanResult);
				targetScanResult.addAll(lastScanResult);
			}
			else
			{
				curScanResult.removeAll(lastScanResult);
				targetScanResult.addAll(curScanResult);
			}
			
			// 查找目标pcm设备名称
			for(int i = 0 ; i < targetScanResult.size(); ++i){
				System.out.println("get pcm devices " + i +"\t" + targetScanResult.get(i));
				// name pcmCxDxc is capture devices, pcmCxDxp is play devices
				if(targetScanResult.get(i).endsWith("c"))
				{
					CaptureDevice = targetScanResult.get(i);
					continue ;
				}
				if(targetScanResult.get(i).endsWith("p"))
				{
					PlayDevice = targetScanResult.get(i);
					continue ;
				}
				System.out.println("Get CapuureDevice is" + CaptureDevice);
				System.out.println("Get PlayDevices is " + PlayDevice);
			}				
			// 跳出循环		
			doDevicesScan = false ;			
		}		
		
	}	
	
	public boolean checkDevicePath()
	{
		File file = new File(path);
		File[] fileList = file.listFiles();
		if(fileList == null)
		{
			 System.out.println("path error");		
			 return false ;
		}
		return true ;
	}
	
	// start 操作 
	public void start()
	{
		System.out.println("start TinyAlsaAudio 3333!!!!!!!!!!!!!!!!");	
		
		//起一个线程开始扫描设备
		new  devicesScanThread().start();

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("USB设备操作");
		builder.setMessage("请完成一次摄像头拔插操作！\n 若摄像头已插上，请拔掉，再插上\n 若摄像头未插上，请直接插上\n");
		builder.setCancelable(false);
		builder.setPositiveButton("已完成插拔操作,并已插上摄像头", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub								
				// stop The Scan thread 
				doDevicesScan = false ; 
				System.out.println("CheckDevices Success" );					
			
			}
		});
		
		builder.create().show();
				
		// 检测到设备
		if(CaptureDevice != null){
			
			// 调用jni层接口打开设备 并播放采集到的声音
			// 播放需要使用android层播放，因为，播放pcm设备被android系统长期占用
		}		
		
	}
	
	// do devices scan 
	// 起一个线程一直扫描 path内的设备，扫描模块 后期独立成class 同时供video使用
	class devicesScanThread extends Thread {
		public void run() {
			try {				 
				if(!checkDevicePath())
				{
					doDevicesScan = false ;
				}
				//begin scan until all get two different pcmdevice list 
				while (doDevicesScan) {							
						ScanProcess();
					}					
				} catch (Throwable t) {
					Toast.makeText(mContext, t.getMessage(), 1000);
				}
			}
		};
	
	

}
