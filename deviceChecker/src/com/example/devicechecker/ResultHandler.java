// 用于处理测试结果，上传至服务器，导出测试文件，删除测试文件等
package com.example.devicechecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ResultHandler {
	
	Context mContext; 
	private String mServerUrl = "http://nj.videochat.skysrt.com:8080/skyvoip/cliparams/commit";
	private String mFileName = "checkResult.txt";
	String TAG = "ResultHandler";

	public ResultHandler(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}
	
	
	// 上传Result 到服务器
	public boolean  upLoadResult()
	{			
		if( !isWifiConnected(mContext) && !isNetworkConnected(mContext) )
		{
			return false ;
		}
		
		System.out.println(" try to upLoadResult !!!!!!!!!!!!!!!!");	
		// 创建httpClient 
		HttpClient client = new DefaultHttpClient();
		// 创建HttpPost实例
		HttpPost post = new HttpPost(mServerUrl);
		String fullFilePath =  getDataPath(mContext,mFileName);
		// 打开记录文件 
		File resultFile = null ;
		try{
			resultFile = new File(fullFilePath);
		}catch(Exception e){
			System.out.println(" can not open file!!!!"); 
			return false ; 			
		}
		// 创建ContentBody
		ContentBody contentBody = new FileBody(resultFile);
		//MultipartEntity 存储一个contentBody
		MultipartEntity multipartEntity = new MultipartEntity();
		multipartEntity.addPart("cliparamsfile", contentBody);
		post.setEntity(multipartEntity);
		HttpResponse response = null;
		try {
			response = client.execute(post);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if (resultFile != null) {
				resultFile.delete();
			}
			e.printStackTrace();
			return false ;
		}
		
		Log.d(TAG, "after client.execute(post)");
		
		if (response != null) {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				Log.d(TAG, "HttpStatus.SC_OK");
				if (resultFile != null) {
					resultFile.delete();
				}
			}
		}
		Log.e(TAG, "end");		
		return true ;
     		
	}
	
	
	// 导出测试数据到U盘
	public boolean exportResult()
	{
		String fullFileName = getDataPath(mContext, mFileName);
		try{
			File resultFile = new File(fullFileName);
			if(resultFile.exists())
			{
				
			}
		}catch(Exception e){
			return false ; 
		}
		
		
		return false ;
	}
	
	// 删除测试文件，因为文件使用append方式，在应用退出时，删除本次测试结果
	public void removeResultFile()
	{
		String fullFileName = getDataPath(mContext, mFileName);
		try{
			File resultFile = new File(fullFileName);
			if(resultFile.exists())
			{
				// remove it 
				resultFile.delete();
			}
		}catch(Exception e)
		{
			// do something
		}
		
		return ;
	}
	
	
	private String getDataPath(Context context, String fileName) {		
		if (context != null && fileName != null) {
			return context.getFilesDir() + "/" + fileName;
		}
		return null;
	}
	
	// 判断是否已连接有线
	public boolean isNetworkConnected(Context context) { 
		if (context != null) { 
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
					.getSystemService(Context.CONNECTIVITY_SERVICE); 
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
		if (mNetworkInfo != null) { 
			return mNetworkInfo.isAvailable(); 
			} 
		} 
			return false; 
	}
	
	// 判断是否已连接无线
	public boolean isWifiConnected(Context context) { 
		if (context != null) { 
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
					.getSystemService(Context.CONNECTIVITY_SERVICE); 
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager 
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
		if (mWiFiNetworkInfo != null) { 
			return mWiFiNetworkInfo.isAvailable(); 
			} 
		} 
		return false; 
	}
	
	

}
