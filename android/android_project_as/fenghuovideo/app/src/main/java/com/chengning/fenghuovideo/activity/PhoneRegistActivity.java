package com.chengning.fenghuovideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVInstallation;
import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.util.HttpUtil;
import com.chengning.fenghuovideo.Consts;
import com.chengning.fenghuovideo.LoginManager;
import com.chengning.fenghuovideo.MyJsonHttpResponseHandler;
import com.chengning.fenghuovideo.R;
import com.chengning.fenghuovideo.util.Common;
import com.chengning.fenghuovideo.util.JUrl;
import com.chengning.fenghuovideo.util.UIHelper;
import com.chengning.fenghuovideo.widget.TitleBar;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

public class PhoneRegistActivity extends BaseFragmentActivity {

	private PhoneRegistActivity mContext;
	private int  mPhone_CountDown = 0;
	private Thread mCounDownThread;
	private TextView mCodePushBtn;
	private EditText mCodeEt;
	private EditText mNumberEt;
	private Button mSubmitBtn;
	private int mAction;
	private boolean  mIsCountDownRun = false;
	private String mRegist_phone = "";
	
	private static final int CountDownResult = 1;

	private int mActionFrom;
	private TitleBar mTitleBar;
	
	private StringBuffer mCount = new StringBuffer();
	private CheckBox mJumpPhoneBox;
	private View mJumpPhoneRl;
	private String mUserData;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_phone_regist);
		super.onCreate(savedInstanceState);
 
	}
	
	@Override
	public void onDestroy() {
		UIHelper.removePD();
		super.onDestroy();
	}
	
	@SuppressWarnings("deprecation")
	public void GetCodeViewChange(boolean b) {
		if (b) { 
			mPhone_CountDown = 0;  
			mIsCountDownRun = false; 
			mCodePushBtn.setText(R.string.get_regist_phone_key);
			mCodePushBtn.setTextColor(this.getResources().getColorStateList(R.color.white));
			mCodePushBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.shape_getcodebtn_bg_stated));
		} else {
			mPhone_CountDown = 60;  
			mIsCountDownRun = true;
			mCounDownThread = new Thread(new CountdownThread()); 
			mCounDownThread.start(); 
			mCodePushBtn.setText(R.string.get_regist_phone_key);
			mCodePushBtn.setTextColor(mContext.getResources().getColorStateList(R.color.regist_text_pushing));
			mCodePushBtn.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.shape_timecodebtn_bg));
		}
		
	}
	

	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) {
		case CountDownResult:
			mPhone_CountDown--;
			if(mPhone_CountDown>0) { 
				mCount.setLength(0);
				mCount.append(mPhone_CountDown).append("秒后重新获取");
				mCodePushBtn.setText(mCount.toString());   
			} else { 
				GetCodeViewChange(true);
			}
			break;   
		}
	}

	@Override
	public void initViews() {
		mContext = this;
		mTitleBar = new TitleBar(mContext, true);
		mTitleBar.showDefaultBack();
		
		mCodePushBtn = (TextView)this.findViewById(R.id.phoneregist_codepush_btn);
		mCodeEt = (EditText)this.findViewById(R.id.phoneregist_code_edit);
		mNumberEt = (EditText)this.findViewById(R.id.phoneregist_number_edit);
		mSubmitBtn = (Button)this.findViewById(R.id.phoneregist_submit_btn); 
		mJumpPhoneBox = (CheckBox) this.findViewById(R.id.phone_regist_cbox);
		mJumpPhoneRl = this.findViewById(R.id.phone_regist_jump_phone_rl);
		
	}

	@Override
	public void initDatas() {
		Intent intent = this.getIntent();
		mAction = intent.getIntExtra("action", 0);
		mActionFrom = intent.getIntExtra("action_from",0);
//		mActionParam = (RequestParams) intent.getSerializableExtra("action_param");
		mUserData = intent.getStringExtra("user_data");
		setTitleBarTitle(mAction);
		GetCodeViewChange(true);
	}

	private void setTitleBarTitle(int action) {
		switch (action) {
		case JUrl.Action_Login:
			mTitleBar.setTitle("手机验证");
			mSubmitBtn.setText("下一步");
			break;
		case JUrl.Action_Bind:
			int mBindParam = getIntent().getIntExtra("bind_param", 0);
			switch (mBindParam) {
			case 1:
				mTitleBar.setTitle("手机绑定");
				break;
			case 2:
				mTitleBar.setTitle("更换绑定手机");
				break;
			default:
				break;
			}
			
			mSubmitBtn.setText("完成");
			break;
		case JUrl.Action_LoginAfterBindPhone:
			mJumpPhoneRl.setVisibility(View.VISIBLE);
			mTitleBar.setTitle("手机验证");
			mSubmitBtn.setText("完成注册");
			break; 

		default:
			break;
		}
	}
	
	@Override
	public void installListeners() {
		
		mCodePushBtn.setOnClickListener(new OnClickListener(){ 
			@Override
			public void onClick(View v) {
			 
				if (Common.hasNet()) {
					if (!TextUtils.isEmpty(mNumberEt.getText())) {
						if (mPhone_CountDown == 0) { 
							GetCodeViewChange(false);
							GetPhoneKey(mNumberEt.getText().toString());
						} else {
							UIHelper.showToast(getActivity(), "60秒内仅发送一次，请稍候再试！");
						}
					} else {
						UIHelper.showToast(getActivity(), "手机号不能为空");
					}
				} else {
					UIHelper.showToast(getActivity(), R.string.intnet_fail);
				} 
			} 
		});
		mSubmitBtn.setOnClickListener(new OnClickListener(){ 
			@Override
			public void onClick(View v) {
				if (Common.hasNet()) {
					
					String code = mCodeEt.getText().toString();
					String phone = mNumberEt.getText().toString();
					switch(mAction) {
					case JUrl.Action_Login:
						RegistPhone(phone, code);
						break;
					case JUrl.Action_Bind:
						BindPhone(phone, code);
						break;
					case JUrl.Action_LoginAfterBindPhone:
						LoginAfterBindPhone(phone, code);
						break; 
					} 
				} else {
					UIHelper.showToast(getActivity(), R.string.intnet_fail);
				} 
			} 
		});
		
		mJumpPhoneRl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mJumpPhoneBox.setChecked(!mJumpPhoneBox.isChecked());
			}
		});
		
	}
	
	/**
	 * 绑定手机
	 * @param phone
	 * @param code
	 */
	public void BindPhone(final String phone, String code) {
		
		if( code == null || code.equals("") || phone == null || phone.equals("")){
			UIHelper.showToast(getActivity(), "手机号码和验证码不能为空！");
			return;
		}
		
		UIHelper.addPD(mContext, getResources().getString(R.string.handle_hint));
		RequestParams params = new RequestParams(); 
		params.put("sms_bind_num", phone); 
		params.put("sms_bind_key", code); 
		HttpUtil.post(mContext, JUrl.SITE + JUrl.URL_BIND_PHONE_BYUID, params, new MyJsonHttpResponseHandler() {
	         @Override
	         public void onFailure(int statusCode, Header[] headers,
   	        		 Throwable throwable,JSONObject errorResponse) {
	             
	             Common.handleHttpFailure(mContext, throwable);
	         }
	         
			@Override
			public void onFinish() {
				UIHelper.removePD();
				super.onFinish();
			}

			@Override
			public void onDataSuccess(int status, String mod,
					String message, String data, JSONObject obj) {
				mRegist_phone =  phone;
				Intent i = new Intent();
				i.putExtra("result", mRegist_phone);
				setResult(Consts.BIND_RESULT_CODE, i);
				mContext.finish();
			}

			@Override
			public void onDataFailure(int status, String mod,
					String message, String data, JSONObject obj) {
				UIHelper.showToast(getActivity(), message);   
				UIHelper.removePD();
			}
		}); 
	}

	private void LoginAfterBindPhone(String phone, String code) {
		RequestParams mActionParam = new RequestParams();
		if (null != mJumpPhoneBox && mJumpPhoneBox.isChecked()) {
			LoginManager.getInst().getUserInfo(getActivity(), mUserData);
		} else {
			if( code == null || code.equals("") || phone == null || phone.equals("")){
				UIHelper.showToast(getActivity(), "手机号码和验证码不能为空！");
				return;
			}
			mActionParam.put("sms_bind_num", phone);
			mActionParam.put("sms_bind_key", code);
			UIHelper.addPD(mContext, getResources().getString(R.string.handle_hint));
			
//			if (isSelfAvatar) {
//				File file = Common.creatFile(JUrl.FilePathTemp, mFinalFace);
//				try {
//					mActionParam.put("face", file);
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}
//			}
			
			String url = "";
			switch(mActionFrom) {
				case JUrl.Login_From_WeiXin:
					url=JUrl.SITE + JUrl.Bind_Edit_WeiXin;
					break;
				case JUrl.Login_From_QQ:
					url=JUrl.SITE + JUrl.Bind_Edit_QQ;
					break;
				case JUrl.Login_From_Sina:
					url=JUrl.SITE + JUrl.Bind_Edit_Sina;
					break;
				default:
					break;
			}

			mActionParam.put("devicetype", "2");
			mActionParam.put("devicetoken", AVInstallation.getCurrentInstallation().getInstallationId());
			mActionParam.put("objectid", AVInstallation.getCurrentInstallation().getObjectId());
			HttpUtil.post(mContext, url, mActionParam, new MyJsonHttpResponseHandler() {
				@Override
				public void onFailure(int statusCode, Header[] headers,
									  Throwable throwable,JSONObject errorResponse) {
					Common.handleHttpFailure(mContext, throwable);
				}
				public void onFinish() {
					UIHelper.removePD();
				}
				
				@Override
				public void onDataSuccess(int status, String mod,
						String message, String data, JSONObject obj) {
					LoginManager.getInst().getUserInfo(getActivity(), mUserData);
				}
				@Override
				public void onDataFailure(int status, String mod,
						String message, String data, JSONObject obj) {
					UIHelper.showToast(getActivity(), message);
				}
			});
		}
		
	}
	
	/**
	 * 手机注册
	 * @param phone
	 * @param code
	 */
	private void RegistPhone(String phone, String code) {
		
		if( code == null || code.equals("") || phone == null || phone.equals("")){
			UIHelper.showToast(getActivity(), "手机号码和验证码不能为空！");
			return;
		}
		
		UIHelper.addPD(mContext, getResources().getString(R.string.handle_hint));
		RequestParams params = new RequestParams();
		params.put("sms_bind_num", phone); 
		params.put("sms_bind_key", code); 
		
		HttpUtil.post(mContext, JUrl.SITE + JUrl.URL_REGISTER_PHONE, params, new MyJsonHttpResponseHandler() {
	    	 
	         public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
	             UIHelper.removePD();
	             Common.handleHttpFailure(mContext, throwable);
	         }
	         
	         @Override
	         public void onFinish() {
	        	 UIHelper.removePD();
	         }
	         
			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {

				UIHelper.removePD();  
				String phone = mNumberEt.getText().toString(); 
				
				Bundle bundle = new Bundle();
				bundle.putString("phone", phone);
				Intent intent = new Intent(); 
				intent.putExtra("login_param", bundle);
				intent.putExtra("login_from", JUrl.Login_From_Phone);
				intent.setClass(mContext, AccountEditActivity.class);
				mContext.startActivity(intent); 
			}

			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {
				UIHelper.showToast(getActivity(), message);   
				UIHelper.removePD();
			}
		});  
	}
	
	/**
	 * 获取验证码
	 * @param phone
	 */
	public void GetPhoneKey(String phone) {  
		mPhone_CountDown = 60;
		mCodePushBtn.setText(mPhone_CountDown + "秒后重新获取");  
		mCodePushBtn.setBackgroundResource(R.drawable.shape_timecodebtn_bg);

		RequestParams params = new RequestParams();
		params.put("sms_bind_num", phone); 
		
		HttpUtil.post(mContext, JUrl.SITE + JUrl.URL_GET_PHONE_VERCODE, params, new MyJsonHttpResponseHandler() {
	    	 // 成功后返回一多个json
	         public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
	             Common.handleHttpFailure(mContext, throwable);
	             GetCodeViewChange(true);
	         }

			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				UIHelper.showToast(getActivity(), message);
			}

			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {
				UIHelper.showToast(getActivity(), message);
				GetCodeViewChange(true);
			}
		});
		
	}

	@Override
	public void uninstallListeners() {
		
	}
	
	 public class CountdownThread implements Runnable{      // thread  
	        @Override  
	        public void run(){  
	            while(mIsCountDownRun){  
	                try{  
	                    Thread.sleep(1000);     // sleep 1000ms   
	                    Message message = getHandler().obtainMessage(CountDownResult, null);
						message.sendToTarget();
	                }catch (Exception e) {  
	                }  
	            }  
	        }  
	    }

	@Override
	public BaseFragmentActivity getActivity() {
		return PhoneRegistActivity.this;
	}  
	
}
