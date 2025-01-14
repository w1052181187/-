package com.shenyuan.militarynews.activity;

import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.base.IForceListenRefresh.OnRefreshStateListener;
import com.chengning.common.base.IForceListenRefresh.RefreshState;
import com.chengning.common.update.UpdateVersionUtil;
import com.chengning.common.util.DisplayUtil;
import com.chengning.common.util.HomeWatcher;
import com.chengning.common.util.HomeWatcher.OnHomePressedListener;
import com.chengning.common.util.HttpUtil;
import com.google.gson.Gson;
import com.shenyuan.militarynews.App;
import com.shenyuan.militarynews.Const;
import com.shenyuan.militarynews.Const.PointActionType;
import com.shenyuan.militarynews.LoginManager;
import com.shenyuan.militarynews.MyStatusResponseHandler;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.SettingManager;
import com.shenyuan.militarynews.base.IForceListenRefreshExtend;
import com.shenyuan.militarynews.beans.data.DirectoratePointBean;
import com.shenyuan.militarynews.beans.data.LoginBean;
import com.shenyuan.militarynews.fragment.home.ZiXunFragment;
import com.shenyuan.militarynews.utils.Common;
import com.shenyuan.militarynews.utils.JUrl;
import com.shenyuan.militarynews.utils.SPHelper;
import com.shenyuan.militarynews.utils.TaskUpdateUtil;
import com.shenyuan.militarynews.utils.UIHelper;
import com.shenyuan.militarynews.utils.UmengShare;
import com.shenyuan.militarynews.utils.Utils;
import com.shenyuan.militarynews.views.FirstRunPage;
import com.umeng.analytics.MobclickAgent;

public class HomeSingleActivity extends BaseFragmentActivity implements OnHomePressedListener {
	
	public static final String ACTION_FINISHHOME = App.class.getPackage().getName() + ".action_finishhome";
	public static final int TASK_SUCCESS = 1;
	
//	private TitleBar mTitleBar;
	private ImageButton mTopLeftBtn;
	private ImageButton mTopRightBtn;
	private RelativeLayout mUserLayout;
	private ImageView mUserHead;
	private ImageView mRefresh;
	private ArrayList<Fragment> mFragmentsList;
	private HomeWatcher mHomeWatcher;
    private boolean mIsHomePressed;
    private boolean mIsHomeStop;
    private BroadcastReceiver finishReceiver;
    
	private long lastTime = 0;

	protected int mCurIndex = 0;
	private boolean mIsFromPush;
	
	private Fragment mLastFragment;
	private RefreshState mRefreshState;
	private ZiXunFragment mZiXunFragment;
	private int mNightModel;
	
	public static void launch(Activity from){
		Intent intent = new Intent(from, HomeSingleActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		from.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_home);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void initViews() {
		mTopLeftBtn = (ImageButton) findViewById(R.id.top_btn_left);
		mTopRightBtn = (ImageButton) findViewById(R.id.top_btn_right);
		mUserLayout = (RelativeLayout) findViewById(R.id.top_user_btn_layout);
		mUserHead = (ImageView) findViewById(R.id.top_iv_head);
		mRefresh = (ImageView) findViewById(R.id.top_refresh);

	}

	@Override
	public void initDatas() {
		mCurIndex = SPHelper.getInst().getInt(SPHelper.KEY_HOME_TAB_INDEX_CACHE);
		
		DisplayUtil.getInst().init(getActivity());
    	SPHelper.getInst().saveInt(SPHelper.KEY_HOME_NIGHT_MODEL, SettingManager.getInst().getNightModel());
		
		addFragment();
		getHandler().postDelayed(new Runnable() {	
			@Override
			public void run() {
				UpdateVersionUtil.checkUpdate(getActivity(), Const.UPDATE_APP_KEY, false);
			}
		}, 2000);
		
		FirstRunPage firstRunPage = new FirstRunPage(getActivity());
		
		// 用户反馈通知
//		new FeedbackAgent(getActivity()).sync();
	}

	private void addFragment() {
		mZiXunFragment =  new ZiXunFragment();
		mFragmentsList = new ArrayList<Fragment>();
		mFragmentsList.add(mZiXunFragment);

		FragmentManager mFragmentManager = getSupportFragmentManager();
		mFragmentManager.beginTransaction()
			.replace(R.id.home_content_layout, mZiXunFragment)
			.commitAllowingStateLoss();
		
		changeIndex(0);
	}
	
	private void changeIndex(int index){
		int oldIndex = mCurIndex;
    	mCurIndex = index;
		 
		if(mLastFragment != null && mLastFragment instanceof IForceListenRefreshExtend){
			((IForceListenRefreshExtend)mLastFragment).setOnRefreshStateListener(null);
		}
		Fragment f = mFragmentsList.get(index);
		mLastFragment = f;
		if(f instanceof IForceListenRefreshExtend){
			setRefreshState(((IForceListenRefreshExtend)f).getRefreshState());
			((IForceListenRefreshExtend) f).setOnRefreshStateListener(mControlListener);

			((IForceListenRefreshExtend) f).forceSetPageSelected(true);
			
			if(oldIndex == index){
				((IForceListenRefreshExtend) f).forceRefresh();
			}
		}else{
			refreshComplete();
		}
	}

	@Override
	public void installListeners() {
		mTopLeftBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				AccountCenterActivity.launch(getActivity());
			}
		});
		mUserLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				AccountCenterActivity.launch(getActivity());
			}
		});
		mTopRightBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LoginManager.getInst().isLogin()) {
					DirectorateActivity.launch(getActivity());
				}else{
				    startActivity(new Intent(getActivity(),LoginActivity.class)
				    		.putExtra(LoginActivity.FROM, LoginActivity.NORMAL));
				}
			}
		});
		mRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Fragment f = mFragmentsList.get(mCurIndex);
				if(f instanceof IForceListenRefreshExtend){
					((IForceListenRefreshExtend) f).forceRefresh();
				}else{
					refreshComplete();
				}
			}
		});
		finishReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				finish();
			}
		};
		IntentFilter intentFileter = new IntentFilter(ACTION_FINISHHOME);
		registerReceiver(finishReceiver, intentFileter);
	}

	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) {
		case TASK_SUCCESS:
			if(App.getInst().isLogin()){
				LoginBean mBean = (LoginBean) msg.obj;
				if(mBean.getCredits_rule()!=null){
					DirectoratePointBean pointBean = TaskUpdateUtil.convertLoginToPoint(mBean);
					try {
						if(pointBean != null){
							TaskUpdateUtil.showHints(getActivity(), pointBean, PointActionType.LOGIN);		
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}else {
				if (!Common.isListEmpty(App.getInst().getLoginBean().getCredits_rule())) {
					DirectoratePointBean pointBean = TaskUpdateUtil.convertLoginToPoint(App.getInst().getLoginBean());
					try {
						if (pointBean != null) {
							TaskUpdateUtil.showHints(getActivity(),pointBean, PointActionType.LOGIN);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 手动制空credits_rules
					LoginBean mLoginBean = new LoginBean();
					mLoginBean = App.getInst().getLoginBean();
					mLoginBean.setCredits_rule(null);
					App.getInst().saveLoginBean(mLoginBean);
				}

			}
			
			break;

		default:
			break;
		}
	}
	
	public void onNav(String channel){
		for(Fragment f: mFragmentsList){
			if(f instanceof ZiXunFragment){
				((ZiXunFragment)f).onNav(channel);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(this);
        mHomeWatcher.startWatch();
		
        mNightModel = SettingManager.getInst().getNightModel();
		if (mNightModel != SPHelper.getInst().getInt(SPHelper.KEY_HOME_NIGHT_MODEL)) {
        	SPHelper.getInst().saveInt(SPHelper.KEY_HOME_NIGHT_MODEL, mNightModel);
        	finish();
        	launch(getActivity());
        }
        
        updateHead();
        
        if(App.getInst().isLogin()){
            initEveryLogin();
        }
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}


	@Override
	public void onPause() {
		super.onPause();
		mHomeWatcher.setOnHomePressedListener(null);
        mHomeWatcher.stopWatch();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(finishReceiver);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if(mIsHomeStop){
			Fragment f = mFragmentsList.get(mCurIndex);
			if(null != f && f instanceof IForceListenRefreshExtend){
				((IForceListenRefreshExtend) f).forceCheckRefresh();
			}
		}
		mIsHomePressed = false;
		mIsHomeStop = false;
	}

	@Override
	protected void onStop() {
		super.onStop();
		mIsHomeStop = mIsHomePressed;
		SPHelper.getInst().saveInt(SPHelper.KEY_HOME_TAB_INDEX_CACHE, mCurIndex);
	}

	@Override
	public BaseFragmentActivity getActivity() {
		return this;
	}
	
	@Override
	public void onHomePressed() {
		mIsHomePressed = true;
	}

	@Override
	public void onHomeLongPressed() {
		mIsHomePressed = true;
	}
	
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState){
//        super.onSaveInstanceState(savedInstanceState);
    	savedInstanceState.putBoolean("push", mIsFromPush);
    }
 
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
//        super.onRestoreInstanceState(savedInstanceState);
    	mIsFromPush = savedInstanceState.getBoolean("push",false);
    }

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		UmengShare.getInstance().onActivityResult(arg0, arg1, arg2);
	}
	
	@Override
	public void onBackPressed() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastTime < 1500) {
			finish();
		} else {
			lastTime = currentTime;
			UIHelper.showToast(this, "再按一次退出");
		}	
		return;
	}

	
	private void setRefreshState(RefreshState state){
		this.mRefreshState = state;
		switch (mRefreshState) {
		case Refreshing:
			mControlListener.onStart();
			break;
		case RefreshComplete:
			mControlListener.onFinish();
			break;
		default:
			break;
		}
	}
	
	private OnRefreshStateListener mControlListener = new OnRefreshStateListener() {
		
		@Override
		public void onStart() {
			mRefreshState = RefreshState.Refreshing;
			refreshing();
		}
		
		@Override
		public void onFinish() {
			mRefreshState = RefreshState.RefreshComplete;
			refreshComplete();
		}
	};
	
	private void updateHead(){
		if (App.getInst().isLogin()) {
			mTopLeftBtn.setVisibility(View.GONE);
			mUserLayout.setVisibility(View.VISIBLE);
			Utils.setCircleImage(App.getInst().getLoginBean().getFace(), mUserHead);
			if (Common.isTrue(SettingManager.getInst().getNightModel())) {
				mUserHead.setColorFilter(getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
			}
			
		} else {
			mTopLeftBtn.setVisibility(View.VISIBLE);
			mUserLayout.setVisibility(View.GONE);
		}
	}
	
	private void refreshing(){
		mRefresh.setEnabled(false);
		Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
		animation.setInterpolator(new LinearInterpolator());
		mRefresh.startAnimation(animation);
	}
	
	private void refreshComplete(){
		mRefresh.setEnabled(true);
		mRefresh.clearAnimation();
	}

	private void initEveryLogin(){
		if (Common.hasNet()) {
			MobclickAgent.onEvent(this, "user_login");
			//cookie登录请求每日登录
			HttpUtil.get(JUrl.SITE + JUrl.URL_GET_LOGIN_EVERYDAY,new MyStatusResponseHandler(){

				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, JSONObject errorResponse) {
					Common.handleHttpFailure(getActivity(), throwable);
//					getHandler().obtainMessage(HTTP_FAIL).sendToTarget();
				}

				@Override
		        public void onFinish() {
		        }

				@Override
				public void onDataSuccess(int status, String mod, String message,
						String data, JSONObject obj) {
//					UIHelper.removePD();
					Gson gson = new Gson();
					LoginBean mBean = gson.fromJson(data, LoginBean.class);
					getHandler().obtainMessage(TASK_SUCCESS, mBean).sendToTarget();
	              
				}

				@Override
				public void onDataFailure(int status, String mod, String message,
						String data, JSONObject obj) {
				}
				
			});
		}
	}

}
