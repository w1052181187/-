package com.shenyuan.militarynews.activity;

import java.net.URI;

import org.apache.http.Header;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.chengning.common.base.BaseActivity;
import com.chengning.common.update.UpdateUtil;
import com.chengning.common.util.HttpCacheControl;
import com.chengning.common.util.HttpCacheControl.CacheControlMaxAgeListener;
import com.chengning.common.util.HttpUtil;
import com.google.gson.Gson;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.shenyuan.militarynews.App;
import com.shenyuan.militarynews.Const;
import com.shenyuan.militarynews.LoginManager;
import com.shenyuan.militarynews.MyStatusResponseHandler;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.beans.data.AdStartBean;
import com.shenyuan.militarynews.utils.Common;
import com.shenyuan.militarynews.utils.JUrl;
import com.shenyuan.militarynews.utils.SPHelper;
import com.xiaomi.mipush.sdk.MiPushClient;

public class SplashActivity extends BaseActivity{

	private static final String TAG = SplashActivity.class.getSimpleName();
	
	private static final int MSG_TIME = 1;
	
	private static final long TIME_DELAY = 1000;

	private static final int AD_SUCCESS = 2;

	private static final int REQUEST_PERMISSION = 0;

	protected static final int MSG_INIT_PUSH_TUNNEL = 4;
	
	protected static final String TAG_GETUI_PUSH_ON = "getui_push_on";
	protected static final String TAG_GETUI_PUSH_OFF = "getui_push_off";

	private static final int TAG_LC_PUSH_ON = 1;
	private static final int TAG_LC_PUSH_OFF = 0;
	
	private ImageView mImage;
	private ImageView mTimerImage;
	
	private int mIndex = 3;

	protected String adUrl;

	protected String imgUrl;
	
	protected String mall_url;
	
	protected String order_url;
	
	protected String mall_logout_url;
	
	protected String title;

	private DisplayImageOptions options;

	protected String defaultImage;

	protected boolean isAdDispaly = false;
	
	private boolean isShowing = false;
	
	private HttpCacheControl mCacheControl;
	private String video_url;

	@Override
	public void onCreate(Bundle savedInstanceState){
		setContentView(R.layout.activity_splash); 
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void initViews() {
		mImage = (ImageView) findViewById(R.id.splash_image);
		mTimerImage = (ImageView) findViewById(R.id.splash_timer);
	}

	@Override
	public void initDatas() {
// 		getHandler().sendMessageDelayed(getHandler().obtainMessage(MSG_TIME), TIME_DELAY);
 		
 		String defaultSavedImage = SPHelper.getInst().getString(SPHelper.KEY_AD_START_IMAGE);
		if(TextUtils.isEmpty(defaultSavedImage)){
			defaultSavedImage = "drawable://" + R.drawable.splash_pic;
			SPHelper.getInst().saveString(SPHelper.KEY_AD_START_IMAGE, defaultSavedImage);
		}
		defaultImage = defaultSavedImage;
		adUrl = SPHelper.getInst().getString(SPHelper.KEY_AD_START_URL);
		JUrl.SITE = SPHelper.getInst().getString(SPHelper.KEY_CACHE_SITE, JUrl.DEFAULT_SITE);
 		options = new DisplayImageOptions.Builder() 
		.showStubImage(R.color.transparent)
		.showImageForEmptyUri(R.color.transparent)
		.showImageOnFail(R.color.transparent)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)  
		.displayer(new FadeInBitmapDisplayer(200))
//		.postProcessor(new com.shenyuan.project.util.SyBitmapProcessor())
		.build(); 
 		
 		
 		if (Common.hasNet()) {
 			mCacheControl = new HttpCacheControl();
 			mCacheControl.init(App.getInst(), JUrl.URL_CHANGE_SITE, new CacheControlMaxAgeListener() {
				
				@Override
				public void success() {
					getStartAd();
				}
				
				@Override
				public void failure() {
					getSite();
				}
			});
 			
// 			getSite();
 		} else {
 			getHandler().obtainMessage(AD_SUCCESS, defaultImage).sendToTarget();
 		}
 		
	}

	/**
	 * 获取域名
	 */
	private void getSite() {
		HttpUtil.get(JUrl.URL_CHANGE_SITE, new JsonHttpResponseHandler() {
			
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				String oldDomain = URI.create(JUrl.SITE).getHost();
				JUrl.SITE = response.optString("api_url");
				SPHelper.getInst().saveString(SPHelper.KEY_CACHE_SITE, JUrl.SITE);
				mCacheControl.saveCacheControlMaxAge(JUrl.URL_CHANGE_SITE, headers);
				LoginManager.getInst().updateCookie(oldDomain);
				getStartAd();
				super.onSuccess(statusCode, headers, response);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				getStartAd();
				super.onFailure(statusCode, headers, responseString, throwable);
			}
			
		});
	}
	
	private void getStartAd() {
		AsyncHttpClient client = HttpUtil.getClient();
		client.setTimeout(3000);
		AVInstallation avInstallation = AVInstallation.getCurrentInstallation();
		RequestParams params = new RequestParams();
		params.put("type", "android");
		if (Common.isMIUI()) {
			//小米推送唯一标识（暂定）
			params.put("regid", MiPushClient.getRegId(getActivity()));
		}
		params.put("devicetoken", avInstallation.getInstallationId());
		params.put("objectid", avInstallation.getObjectId());
		params.put("version", UpdateUtil.getVersionName(getActivity()));
		params.put("channel", Common.getUmengChannel(getActivity()));
		
		client.post(JUrl.SITE + JUrl.URL_GET_AD_START, params, new MyStatusResponseHandler() {
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				getHandler().obtainMessage(AD_SUCCESS, defaultImage).sendToTarget();
			}
			
			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				Gson gson = new Gson();
				AdStartBean bean = gson.fromJson(data, AdStartBean.class);

				SPHelper.getInst().saveString(SPHelper.KEY_GUIDE_DOWNLOAD_IMAGE_URL, bean.getBoot_image());
				SPHelper.getInst().saveString(SPHelper.KEY_GUIDE_DOWNLOAD_URL, bean.getDownload_url());
				SPHelper.getInst().saveInt(SPHelper.KEY_GUIDE_DOWNLOAD_SHOW, bean.getIs_boot());
				SPHelper.getInst().saveString(SPHelper.KEY_GUIDE_DOWNLOAD_NAME, bean.getAppid());
				SPHelper.getInst().saveString(SPHelper.KEY_GUIDE_DOWNLOAD_PACKAGE_NAME, bean.getApp_marking());
				SPHelper.getInst().saveString(SPHelper.KEY_DATEFORMAT, bean.getDateformat());
				
				//是否轮询查看最新文章个数
				SPHelper.getInst().saveInt(SPHelper.BADGE_KEY_NEW_ARTICLE_IS_POLLING, bean.getIs_polling());
				//轮询查看最新文章时间
				SPHelper.getInst().saveInt(SPHelper.BADGE_KEY_NEW_ARTICLE_POLLING_TIME, bean.getCheck_news_interval());
				
				
				adUrl = bean.getUrl();
				imgUrl = bean.getImage();
				mall_url = bean.getMall_url();
				video_url = bean.getVideo_url();
				order_url = bean.getOrder_url();
				mall_logout_url=bean.getMall_logout_url();

				String imageSaveStr = SPHelper.getInst().getString(SPHelper.KEY_AD_START_IMAGE);
				if(bean != null && !TextUtils.isEmpty(bean.getImage())
						&& !imageSaveStr.equals(bean.getImage())){
					// has change
					SPHelper.getInst().saveString(SPHelper.KEY_AD_START_IMAGE, bean.getImage());
					SPHelper.getInst().saveString(SPHelper.KEY_AD_START_URL, bean.getUrl());
					
					defaultImage = bean.getImage();
//					getHandler().obtainMessage(AD_SUCCESS, defaultImage).sendToTarget();
				}else{
					if(!TextUtils.isEmpty(imageSaveStr)){
						defaultImage = imageSaveStr;
//						Message msg = getHandler().obtainMessage(AD_SUCCESS, imageSaveStr);
//						msg.sendToTarget();
					}
//					else{
//						Message msg = getHandler().obtainMessage(AD_SUCCESS, defaultImage);
//						msg.sendToTarget();
//					}
				}

				if(!TextUtils.isEmpty(video_url)){
					SPHelper.getInst().saveString(SPHelper.KEY_VIDEO_URL, video_url);
				}

				if(!TextUtils.isEmpty(mall_url)){
					SPHelper.getInst().saveString(SPHelper.KEY_MALL_URL, mall_url);
				}
				if(!TextUtils.isEmpty(order_url)){
					SPHelper.getInst().saveString(SPHelper.KEY_ORDER_URL, order_url);
				}
				if(!TextUtils.isEmpty(mall_logout_url)){
					SPHelper.getInst().saveString(SPHelper.KEY_MALL_LOGOUT_URL, mall_logout_url);
				}
				getHandler().obtainMessage(AD_SUCCESS, defaultImage).sendToTarget();
				
				if (Common.isMIUI()) {
					initeXiaomiPush(getActivity());
				} else {
					getHandler().obtainMessage(MSG_INIT_PUSH_TUNNEL, bean.getTunnel()).sendToTarget();
				}
				
				//测试
//				getHandler().obtainMessage(MSG_INIT_PUSH_CHANNEL, 1).sendToTarget();
			}
			
			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {
				getHandler().obtainMessage(AD_SUCCESS, defaultImage).sendToTarget();
			}
		});
	}
	

	@Override
	public void installListeners() {
		mTimerImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startHomeActiivty();
			}
		});
		mImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(adUrl)) {
					AdDetailActivity.launch(getActivity(), AdDetailActivity.TYPE_AD, adUrl, imgUrl);
					finish();
				}
				
			}
		});
		
	}

	@Override
	public void onResume() {
		super.onResume();
		isShowing = true;
		getHandler().sendMessageDelayed(getHandler().obtainMessage(MSG_TIME), TIME_DELAY);
	}

	@Override
	public void onPause() {
		super.onPause();
		isShowing = false;
 		getHandler().removeMessages(MSG_TIME);
	}

	@Override
	public void processHandlerMessage(Message msg) {
		switch(msg.what){
			case MSG_TIME:
				if(mIndex > 0){
					mTimerImage.setBackgroundResource(R.drawable.timer_bg);
					switch (mIndex) {
					case 1:
						mTimerImage.setImageResource(R.drawable.timer_1);
						break;
					case 2:
						mTimerImage.setImageResource(R.drawable.timer_2);
						break;
					case 3:
						mTimerImage.setImageResource(R.drawable.timer_3);
						break;
					default:
						break;
					}
					mIndex--;
			 		getHandler().removeMessages(MSG_TIME);
			 		getHandler().sendMessageDelayed(getHandler().obtainMessage(MSG_TIME), TIME_DELAY);
				}else{
					startHomeActiivty();
				}
				break;
			case AD_SUCCESS:
				displayImage((String)msg.obj, mImage, options);
				
				AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
				anim.setDuration(1000);
				anim.setInterpolator(new DecelerateInterpolator());
				mImage.startAnimation(anim);
				break;
			case MSG_INIT_PUSH_TUNNEL :
				int channel = (Integer) msg.obj;
				initPushChannel(channel);
				break;
			default :
				break;
		}
	}
	
	private void initPushChannel(int channel) {
		SPHelper.getInst().saveInt(SPHelper.KEY_PUSH_TUNNEL, channel);
		switch (channel) {
		case Const.PUSH_TUNNEL_LEANCLOUD:
			initLeancloud(getActivity());
			break;
		case Const.PUSH_TUNNEL_GETUI:
			initGetuiPush();
			break;

		default:
			break;
		}
	}
	
	/**
	 * 初始化个推推送
	 */
	private void initGetuiPush() {
		PackageManager pkgManager = getPackageManager();
        // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
        boolean sdCardWritePermission =
                pkgManager.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName()) == PackageManager.PERMISSION_GRANTED;


        // read phone state用于获取 imei 设备信息
        boolean phoneSatePermission =
                pkgManager.checkPermission(android.Manifest.permission.READ_PHONE_STATE, getPackageName()) == PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= 23 && !sdCardWritePermission || !phoneSatePermission) {
            requestPermission();
        } else {
            // SDK初始化，第三方程序启动时，都要进行SDK初始化工作
            initPush(getActivity());
        }
	}
	
	private void initPush(Context context) {
		PushManager.getInstance().initialize(context.getApplicationContext());
		PushManager.getInstance().turnOnPush(getActivity());
		PushManager.getInstance().bindAlias(context, AVInstallation.getCurrentInstallation().getInstallationId());
		switchState(TAG_GETUI_PUSH_ON);
		
		//关闭leancloud推送
		saveLeacloudInstallation(TAG_LC_PUSH_OFF);
		
		//关闭小米推送
		MiPushClient.unregisterPush(context);
	}

	private void switchState(String state) {
		Tag[] tagParam = new Tag[1];
	    Tag t = new Tag();
	    t.setName(state);
	    tagParam[0] = t;
		PushManager.getInstance().setTag(getActivity(), tagParam, System.currentTimeMillis() +"");
	}

	private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_PHONE_STATE},
                REQUEST_PERMISSION);

    }

    @SuppressLint("NewApi")
	@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if ((grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            	 initPush(getActivity());
            } else {
            	 initPush(getActivity());
            }
        } else {
            onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
	
	/**
	 * 初始化小米推送
	 * @param context 
	 */
	private void initeXiaomiPush(Context context) {
		MiPushClient.registerPush(context, Const.XIAOMI_APP_ID, Const.XIAOMI_APP_KEY);
		
		//关闭leancloud推送
		saveLeacloudInstallation(TAG_LC_PUSH_OFF);
		
		//关闭个推推送
		switchState(TAG_GETUI_PUSH_OFF);
		PushManager.getInstance().stopService(context);
		PushManager.getInstance().turnOffPush(getActivity());
		
	}
	
	/**
	 * 初始化leacloud推送
	 * @param context 
	 */
	private void initLeancloud(Context context) {
		// AVOSCloud.setDebugLogEnabled(true);
		
		PushService.setDefaultPushCallback(this, HomeActivity.class);
		// 订阅频道，当该频道消息到来的时候，打开对应的 Activity
		PushService.subscribe(context, "public",
				HomeActivity.class);
		PushService.subscribe(context, "private",
				HomeActivity.class);
		PushService.subscribe(context, "protected",
				HomeActivity.class);

		saveLeacloudInstallation(TAG_LC_PUSH_ON);
		
		//关闭小米推送
		MiPushClient.unregisterPush(context);
		
		//关闭个推推送
		switchState(TAG_GETUI_PUSH_OFF);
		PushManager.getInstance().stopService(context);
		PushManager.getInstance().turnOffPush(getActivity());
		
	}
	
	private void saveLeacloudInstallation(final int pushState) {
		AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
			public void done(AVException e) {
				if (e == null) {
					// 保存成功
					changeLCPushState(pushState);
				} else {
					// 保存失败，输出错误信息
					e.printStackTrace();
				}
			}
		});
	}
	
	protected void changeLCPushState(int pushState) {
		AVObject install = new AVObject("_Installation");
		install.put("isOn", pushState);
		install.put("deviceType", "android");
		install.put("installationId", AVInstallation.getCurrentInstallation()
				.getInstallationId());
		install.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException e) {
			}
		});
	}
	
	private void displayImage(String imageUrl, ImageView view, DisplayImageOptions options){
		ImageLoader.getInstance().displayImage(imageUrl, view, options, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				isAdDispaly = true;
				if(isShowing){
					getHandler().sendMessage(getHandler().obtainMessage(MSG_TIME));
				}
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				isAdDispaly = true;
				if(isShowing){
					getHandler().sendMessage(getHandler().obtainMessage(MSG_TIME));
				}
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				isAdDispaly = true;
				if(isShowing){
					getHandler().sendMessage(getHandler().obtainMessage(MSG_TIME));
				}
			}
		});
	}	
	public void startHomeActiivty() {
 		getHandler().removeMessages(MSG_TIME);
 		
		Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
		SplashActivity.this.startActivity(intent);
		SplashActivity.this.finish();
	}

}
