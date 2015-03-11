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

import android.content.Context;
import android.util.Log;

public class ResultHandler {
	
	Context mContext; 
	private String mServerUrl = "http://nj.videochat.skysrt.com:8080/skyvoip/cliparams/commit ";
	private String mFileName = "checkResult.txt";

	public ResultHandler(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}
	
	
	// 上传Result 到服务器
	public boolean  upLoadResult()
	{	
		System.out.println(" try to upLoadResult !!!!!!!!!!!!!!!!");	
		String BOUNDARY = "---------------------------7db1c523809b2";
        // 分割线  
        File file = new File(mFileName);          
        
        //http://host:port/xxx/xxx/xxx/param?param1=xxx&param2=xxx
        String host = mServerUrl + "param" + "?filename=checkResult.txt&fileType=txt";
        // 用来解析主机名和端口  
       // URL url = new URL(urlString);  
        try   
        {  
            byte[] after = ("--" + BOUNDARY + "--\r\n").getBytes("UTF-8");  
              
            // 构造URL和Connection  
            URL url = new URL(host);              
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
              
            // 设置HTTP协议的头属性  
            conn.setRequestMethod("POST");  
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);  
            conn.setRequestProperty("Content-Length", String.valueOf(file.length()));  
            conn.setRequestProperty("HOST", url.getHost());  
            conn.setDoOutput(true);  
              
            // 得到Connection的OutputStream流，准备写数据  
            OutputStream out = conn.getOutputStream();  
            String fullFileName = getDataPath(mContext,mFileName);
            InputStream in = new FileInputStream(fullFileName);  
              
              
            // 写文件数据。因为服务器地址已经带有参数了，所以这里只要直接写入文件部分就可以了。  
            byte[] buf = new byte[1024];  
            int len;  
            while ((len = in.read(buf)) != -1)  
            {  
                out.write(buf, 0, len);  
            }               
  
            // 数据结束标志，整个HTTP报文就构造结束了。  
            //out.write(after);  
  
            Log.d("carter", "queryParam 返回码为: " + conn.getResponseCode());  
            Log.d("carter", "queryParam 返回信息为: " + conn.getResponseMessage());  
            boolean success= conn.getResponseCode() == 200; 
            in.close();  
            out.close();         
            conn.disconnect();  
            return success ;              
        }  
        catch (MalformedURLException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();          
            return false ;
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
            return false;
        }       
     		
	}
	
	
	// 导出测试数据到U盘
	public boolean exportResult()
	{
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

}
