/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.shenyuan.militarynews;

import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.chengning.common.base.BaseApp;
import com.chengning.common.base.util.BaseUmengShare.QQPlatform;
import com.chengning.common.base.util.BaseUmengShare.SinaPlatform;
import com.chengning.common.base.util.BaseUmengShare.WeixinPlatform;
import com.chengning.common.base.util.GlideHelper;
import com.chengning.common.base.util.RetrofitManager;
import com.google.gson.Gson;
import com.shenyuan.militarynews.RetrofitRequestInterface.ApiInterface;
import com.shenyuan.militarynews.beans.data.LoginBean;
import com.shenyuan.militarynews.utils.Common;
import com.shenyuan.militarynews.utils.JUrl;
import com.shenyuan.militarynews.utils.SPHelper;
import com.shenyuan.militarynews.utils.UmengShare;
import com.umeng.commonsdk.UMConfigure;

import java.net.URI;
import java.util.HashMap;

import okhttp3.Interceptor;

import static com.chengning.common.base.util.RetrofitManager.COOKIE_PREF;
import static com.chengning.common.base.util.RetrofitManager.SP_MODE;

public class App extends BaseApp {

	private static App xjsContext;

	private AppDetail mAppDetail;

	private boolean isNightModelChange = false;
	private ApiInterface apiInterface;

	public static App getInst() {
		return xjsContext;
	}

	@Override
	public void onCreate() {
		super.onCreate();



		xjsContext = this;
		mAppDetail = new AppDetail();

		initBaiduStat();

		initUmengShare();

//		MobclickAgent.setDebugMode(true);

		initPush();
		initAli();

		try {
			initPushChannel();
		} catch (Exception e) {
			e.printStackTrace();
		}

		initRetrofit();
	}

	public void initRetrofit() {

		JUrl.SITE = SPHelper.getInst().getString(SPHelper.KEY_CACHE_SITE, JUrl.SITE);

		//xinjunshi UA
		HashMap header = new HashMap<String, String>();
		header.put("User-Agent", Common.getUAAndroid(this, Const.USER_AGENT_PREFIX ));
		Interceptor headerInterceptor = new RetrofitManager.HeaderInterceptor(header);
		RetrofitManager.getInst().initRetrofit(getApplicationContext(), JUrl.SITE, headerInterceptor);
		apiInterface = RetrofitManager.getInst().createService(ApiInterface.class);

		initCookies(JUrl.SITE);

	}

	/**
	 * 把原框架的cookie保存到现有框架上
	 * @param SITE
	 */
	private void initCookies(String SITE) {
		String host = URI.create(SITE).getHost();
		String domain = Common.getRootDomain(host);
		SharedPreferences sp = getSharedPreferences(COOKIE_PREF, SP_MODE);

		SharedPreferences.Editor editor = sp.edit();
		Gson gson = new Gson();
		String cookieStr = gson.toJson(RetrofitManager.getInst().getAsyncHttpCookies(this, host));
//		String cookieStr = SerializeUtil.serialize(RetrofitManager.getInst().getAsyncHttpCookies(this, host));
		if (!TextUtils.isEmpty(cookieStr)) {
			editor.putString(domain, cookieStr);
		}
		editor.apply();


	}

	private void initUmengShare() {
		/**
		 * 初始化common库
		 * 参数1:上下文，不能为空
		 * 参数2:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
		 * 参数3:Push推送业务的secret 不使用push传空即可
		 */
		UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);
		UmengShare.getInstance().init(getApplicationContext(), 
				new WeixinPlatform(Const.WX_APP_ID, Const.WX_APP_SECRET),
				new SinaPlatform(Const.WEIBO_APP_KEY, Const.WEIBO_APP_SECRET, Const.WEIBO_REDIRECT_URL),
				new QQPlatform(Const.QQ_APP_ID, Const.QQ_APP_KEY));
	}

	private void initPushChannel() {
		initLeancloud();
	}

	public void initPush() {
		PushMsgManager.getInstance().init(this);
	}

	public AppDetail getAppDetail() {
		return mAppDetail;
	}

	public void initBaiduStat() {
		String umengChannel = Common.getUmengChannel(this);
		StatService.setAppChannel(this, umengChannel, true);
	}
	
	private void initAli(){
		// 用户反馈
		FeedbackAPI.init(this, Const.ALI_APP_KEY, Const.ALI_APP_SECRET);
		
		// 可以设置反馈消息自定义参数，方便在反馈后台查看自定义数据，参数是json对象，里面所有的数据都可以由开发者自定义
		// FeedbackAPI. setAppExtInfo(JSONObject extInfo)
	}

	/**
	 * 初始化leacloud推送
	 */
	private void initLeancloud() {
		AVOSCloud.initialize(this, Const.LEANCLOUD_APP_ID,
				Const.LEANCLOUD_APP_KEY);

		// 设置默认打开的 Activity
//		PushService.setDefaultPushCallback(this, HomeActivity.class);

		saveLeacloudInstallation();
		
	}

	private void saveLeacloudInstallation() {
		AVInstallation.getCurrentInstallation().saveInBackground(
				new SaveCallback() {
					public void done(AVException e) {
						if (e == null) {
							// 保存成功
							String installationId = AVInstallation
									.getCurrentInstallation()
									.getInstallationId();
							// 关联 installationId 到用户表等操作……
							Log.i(App.class.getSimpleName(), "installationId: "
									+ installationId);
							changeLCVersion();
						} else {
							// 保存失败，输出错误信息
							e.printStackTrace();
						}
					}
				});
	}
	
	private void changeLCVersion() {
		if (SPHelper.getInst().getInt(SPHelper.KEY_LEANCLOUD_DATA_VERSION) == Const.LEANCLOUD_DATA_VERSION) {
			return;
		}
		// @杨胜利，指定接收对应版本的推送。
		// leancloud里的 _Installation 表里新建了一个字段 version ，
		// APP每次启动烽火，都更新这个表的这个字段。
		AVObject install = new AVObject("_Installation");
		install.put("dataVersion", Const.LEANCLOUD_DATA_VERSION);
		install.put("deviceType", "android");
		install.put("installationId", AVInstallation.getCurrentInstallation()
				.getInstallationId());
		install.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException e) {
				if (e == null) {
					Log.d("APP", Const.LEANCLOUD_DATA_VERSION
							+ " LEANCLOUD_DATA_VERSION success");

					SPHelper.getInst().saveInt(
							SPHelper.KEY_LEANCLOUD_DATA_VERSION,
							Const.LEANCLOUD_DATA_VERSION);
				} else {
					Log.d("APP", Const.LEANCLOUD_DATA_VERSION
							+ " LEANCLOUD_DATA_VERSION failed e: " + e);
				}
			}
		});
	}


	public boolean isNightModelChange() {
		return isNightModelChange;
	}

	public void setNightModelChange(boolean isNightModelChange) {
		this.isNightModelChange = isNightModelChange;
	}

	public LoginBean getLoginBean() {
		return LoginManager.getInst().getLoginDbBean().getUserinfo();
	}

	public void saveLoginBean(LoginBean loginBean) {
		LoginManager.getInst().saveLoginBean(loginBean);
	}

	public void clearLoginBean() {
		LoginManager.getInst().clearData();
	}

	public boolean isLogin() {
		return LoginManager.getInst().isLogin();
	}

	public ApiInterface getApiInterface() {
		return apiInterface;
	}
}
