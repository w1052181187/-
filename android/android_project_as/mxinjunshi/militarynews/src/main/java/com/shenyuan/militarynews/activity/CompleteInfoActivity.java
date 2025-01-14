package com.shenyuan.militarynews.activity;

import org.apache.http.Header;
import de.greenrobot.event.EventBus;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.avos.avoscloud.AVInstallation;
import com.chengning.common.base.BaseActivity;
import com.chengning.common.util.HttpUtil;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.shenyuan.militarynews.App;
import com.shenyuan.militarynews.MyStatusResponseHandler;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.SettingManager;
import com.shenyuan.militarynews.beans.data.LoginBean;
import com.shenyuan.militarynews.event.LoginResultEvent;
import com.shenyuan.militarynews.utils.Common;
import com.shenyuan.militarynews.utils.JUrl;
import com.chengning.common.util.StatusBarUtil;;
import com.shenyuan.militarynews.utils.UIHelper;
import com.shenyuan.militarynews.utils.Utils;
import com.shenyuan.militarynews.views.TitleBar;


public class CompleteInfoActivity extends BaseActivity {
	
	private ImageView mHead;
	private TitleBar mTitleBar;
	private EditText mNickName;
	private EditText mPwd;
	private Button mRegister;
	
	private String mStrNick;
	private String mStrPwd;
	
	private String mStrThirdNick;
	private String mStrThirdFace;
	private String mStrSinaUid;
	private String mStrWxId;
	private String mStrAction;
	private String mStrQqId;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_completeinfo);
		if(Common.isTrue(SettingManager.getInst().getNightModel())){  
			StatusBarUtil.setBar(this, getResources().getColor(R.color.night_bg_color), false);
        }else{  
        	StatusBarUtil.setBar(this, getResources().getColor(R.color.normalstate_bg), true);
        }
		super.onCreate(savedInstanceState);
	}

	@Override
	public void initViews() {
		mTitleBar = new TitleBar(getActivity(), true);
		mTitleBar.showDefaultBack();
		mTitleBar.setTitle(getResources().getString(R.string.completeinfo));
		
		mHead = (ImageView) findViewById(R.id.ci_iv_head);
		mNickName = (EditText) findViewById(R.id.ci_et_nickname);
		mPwd = (EditText) findViewById(R.id.ci_et_pwd);
		mRegister = (Button) findViewById(R.id.ci_btn_next);
	}

	@Override
	public void initDatas() {
		mStrAction = getIntent().getAction();
		if (TextUtils.equals(mStrAction, LoginActivity.ACTION_SINA)) {
			mStrSinaUid = getIntent().getStringExtra(LoginActivity.SINAUID);
		}
		if (TextUtils.equals(mStrAction, LoginActivity.ACTION_WX)) {
			mStrWxId = getIntent().getStringExtra(LoginActivity.WXID);
		} else if (TextUtils.equals(mStrAction, LoginActivity.ACTION_QQ)) {
			mStrQqId = getIntent().getStringExtra(LoginActivity.QQ_OPENID);
		}
		mStrThirdNick = getIntent().getStringExtra(LoginActivity.THIRDNICK);
		mStrThirdFace = getIntent().getStringExtra(LoginActivity.THIRDFACE);
		if (!TextUtils.isEmpty(mStrThirdNick)) {
			mNickName.setText(mStrThirdNick);
			mNickName.setSelection(mStrThirdNick.length());
		}
		Utils.setCircleImage(mStrThirdFace, mHead);
	}

	@Override
	public void installListeners() {
		mRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(TextUtils.equals(mStrAction, LoginActivity.ACTION_QQ))
					registerQQ();
				else if(TextUtils.equals(mStrAction, LoginActivity.ACTION_SINA))
					registerSINA();
				else if(TextUtils.equals(mStrAction, LoginActivity.ACTION_WX))
					registerWX();
			}
		});
	}
	
	private void registerQQ(){
		mStrNick = mNickName.getText().toString().trim();
		mStrPwd = mPwd.getText().toString().trim();
		if(TextUtils.isEmpty(mStrNick)){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.please_input_nickname));
			return;
		}
		if(TextUtils.isEmpty(mStrPwd)){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.please_input_password));
			return;
		}
		if(mStrPwd.length() < 6){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.set_password));
			return;
		}
		UIHelper.addPD(getActivity(), "正在注册...");
		RequestParams params = new RequestParams();
		params.put("nickname", mStrNick);
		params.put("password", mStrPwd);
		params.put("openid", mStrQqId);
		params.put("face", mStrThirdFace);
		appendOtherInfo(params);
		HttpUtil.post(JUrl.SITE + JUrl.URL_BIND_QQ_REGIST, params, new MyStatusResponseHandler() {
			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				UIHelper.removePD();
				UIHelper.showToast(getActivity(), message);
				Intent intent = new Intent();
				intent.putExtra("result", data);
				setResult(LoginActivity.RESULTCODE_PHONEVERIFY, intent);
				finish();
			}
			
			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {
				UIHelper.removePD();
				UIHelper.showToast(getActivity(), message);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				UIHelper.removePD();
				Common.showHttpFailureToast(getActivity());
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}
	
	private void registerSINA(){
		mStrNick = mNickName.getText().toString().trim();
		mStrPwd = mPwd.getText().toString().trim();
		if(TextUtils.isEmpty(mStrNick)){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.please_input_nickname));
			return;
		}
		if(TextUtils.isEmpty(mStrPwd)){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.please_input_password));
			return;
		}
		if(mStrPwd.length() < 6){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.set_password));
			return;
		}
		UIHelper.addPD(getActivity(), "正在注册...");
		RequestParams params = new RequestParams();
		params.put("nickname", mStrNick);
		params.put("password", mStrPwd);
		params.put("uname", mStrThirdNick);
		params.put("face", mStrThirdFace);
		params.put("bind_uid", mStrSinaUid);
		appendOtherInfo(params);
		HttpUtil.post(JUrl.SITE + JUrl.URL_BIND_SINA_REGIST, params, new MyStatusResponseHandler() {
			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				UIHelper.removePD();
				UIHelper.showToast(getActivity(), message);
				Intent intent = new Intent();
				intent.putExtra("result", data);
				setResult(LoginActivity.RESULTCODE_PHONEVERIFY, intent);
				finish();
			}
			
			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {
				UIHelper.removePD();
				UIHelper.showToast(getActivity(), message);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				UIHelper.removePD();
				Common.showHttpFailureToast(getActivity());
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}
	
	private void registerWX(){
		mStrNick = mNickName.getText().toString().trim();
		mStrPwd = mPwd.getText().toString().trim();
		if(TextUtils.isEmpty(mStrNick)){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.please_input_nickname));
			return;
		}
		if(TextUtils.isEmpty(mStrPwd)){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.please_input_password));
			return;
		}
		if(mStrPwd.length() < 6){
			UIHelper.showToast(getActivity(), getResources().getString(R.string.set_password));
			return;
		}
		UIHelper.addPD(getActivity(), "正在注册...");
		RequestParams params = new RequestParams();
		params.put("nickname", mStrNick);
		params.put("password", mStrPwd);
		params.put("unionid", mStrWxId);
		params.put("face", mStrThirdFace);
		appendOtherInfo(params);
		HttpUtil.post(JUrl.SITE + JUrl.URL_BIND_WX_REGIST, params, new MyStatusResponseHandler() {
			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				UIHelper.removePD();
				UIHelper.showToast(getActivity(), message);
				Gson gson = new Gson();
				LoginBean mBean = gson.fromJson(data, LoginBean.class);
				App.getInst().saveLoginBean(mBean);
				EventBus.getDefault().post(new LoginResultEvent());
//				startActivity(new Intent(getActivity(), AccountCenterActivity.class));
				startActivity(new Intent(getActivity(), PhoneVerifyActivity.class));
				finish();
			}
			
			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {
				UIHelper.removePD();
				UIHelper.showToast(getActivity(), message);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				UIHelper.removePD();
				Common.showHttpFailureToast(getActivity());
				super.onFailure(statusCode, headers, throwable, errorResponse);
			}
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putString("action", mStrAction);
		if(TextUtils.equals(mStrAction, LoginActivity.ACTION_SINA)){
			savedInstanceState.putString(LoginActivity.SINAUID, mStrSinaUid);
		}
        if(TextUtils.equals(mStrAction, LoginActivity.ACTION_WX)){
            savedInstanceState.putString(LoginActivity.WXID, mStrWxId);
        } else if(TextUtils.equals(mStrAction, LoginActivity.ACTION_QQ)){
            savedInstanceState.putString(LoginActivity.QQ_OPENID, mStrQqId);
        }
        savedInstanceState.putString(LoginActivity.THIRDNICK, mStrThirdNick);
        savedInstanceState.putString(LoginActivity.THIRDFACE, mStrThirdFace);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mStrAction = savedInstanceState.getString("action");
		if(TextUtils.equals(mStrAction, LoginActivity.ACTION_SINA)){
			mStrSinaUid = savedInstanceState.getString(LoginActivity.SINAUID);
		}
		if(TextUtils.equals(mStrAction, LoginActivity.ACTION_WX)){
			mStrWxId = savedInstanceState.getString(LoginActivity.WXID);
		}else if(TextUtils.equals(mStrAction, LoginActivity.ACTION_QQ)){
		    mStrQqId = savedInstanceState.getString(LoginActivity.QQ_OPENID);
		}
		mStrThirdNick = savedInstanceState.getString(LoginActivity.THIRDNICK);
        mStrThirdFace = savedInstanceState.getString(LoginActivity.THIRDFACE);
	}

	@Override
	public void processHandlerMessage(Message msg) {

	}
	
	private void appendOtherInfo(RequestParams params){
		params.put("devicetype", 1);
		params.put("devicetoken", AVInstallation.getCurrentInstallation().getInstallationId());
		params.put("objectid", AVInstallation.getCurrentInstallation().getObjectId());
		params.put("version", Common.getVersionName(getActivity()));
	}
	
	@Override
	public Activity getActivity() {
		return this;
	}

}
