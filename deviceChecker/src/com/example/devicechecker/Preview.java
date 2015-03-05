package com.example.devicechecker;

import android.app.Activity;
import android.os.Bundle;

public class Preview extends Activity {
	
	// 根据不同的条件，创建不同的Preview类，并且进行preview的展示
	private CameraPreview camPreview ; 
	private V4l2Preview v4l2Preview ; 
	
	
	protected void onCreate(Bundle savedInstanceState) {
		
	}
	
	protected void onDestroy() {
		
	}

}
