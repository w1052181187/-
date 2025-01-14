package com.cmstop.jstt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentManager;

import com.chengning.common.base.BaseFragmentActivity;
import com.cmstop.jstt.R;
import com.cmstop.jstt.fragment.home.WodeFragment;
import com.cmstop.jstt.utils.Common;

public class MyActivity extends BaseFragmentActivity {

	
	private static Activity mFrom;

	public static void launch(Activity from) {
		mFrom = from;
		Intent intent = new Intent(from, MyActivity.class);
		from.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_my);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public Activity getActivity() {
		return MyActivity.this;
	}

	@Override
	public void initViews() {

	}

	@Override
	public void initDatas() {
		WodeFragment mWodeFragment =  new WodeFragment();
		
		FragmentManager mFragmentManager = getSupportFragmentManager();
		mFragmentManager.beginTransaction()
			.replace(R.id.home_content_layout, mWodeFragment)
			.commitAllowingStateLoss();
	}

	@Override
	public void installListeners() {

	}

	@Override
	public void processHandlerMessage(Message msg) {

	}

}
