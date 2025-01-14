package com.chengning.fenghuo.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.chengning.fenghuo.PushManager;

public class PushUserDataService extends Service {

	public PushManager mPushManager;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
	    super.onCreate();
	    mPushManager = PushManager.getInstance();
	    mPushManager.init(getApplicationContext());
	}
	@Override  
    public void onDestroy() {
		mPushManager.destroy();
        super.onDestroy();
    } 

	@SuppressLint("NewApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	

}