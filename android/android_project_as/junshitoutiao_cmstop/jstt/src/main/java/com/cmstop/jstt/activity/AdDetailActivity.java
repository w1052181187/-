package com.cmstop.jstt.activity;

import java.util.List;

import org.apache.http.cookie.Cookie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.chengning.common.base.BaseFragmentActivity;
import com.cmstop.jstt.App;
import com.cmstop.jstt.R;
import com.cmstop.jstt.SettingManager;
import com.cmstop.jstt.utils.Common;
import com.cmstop.jstt.utils.JUrl;
import com.cmstop.jstt.utils.SPHelper;
import com.chengning.common.util.StatusBarUtil;
import com.cmstop.jstt.utils.UmengShare;
import com.cmstop.jstt.views.PicArticleDialog;
import com.loopj.android.http.PersistentCookieStore;

public class AdDetailActivity extends BaseFragmentActivity {
	
	public final static String TYPE_ARTICLE = "article";
	public final static String TYPE_AD = "ad";
	
	private ImageView iv_ad_close;
	private TextView tv_ad_title;
	private WebView view;
	private String url;
	private String imgUrl;
	private String mTitle;
	private ImageView back;
	private ImageView forward;
	private ImageView refresh;
	private ImageView close;
	private ImageView share;
	private String mType;
	
	private CookieManager cookieManager;
	
	public static void launch(Activity from, String type, String url, String imgUrl){
		Intent intent = new Intent(from, AdDetailActivity.class);
		intent.putExtra("type", type);
		intent.putExtra("adurl", url);
		intent.putExtra("imgurl", imgUrl);
		from.startActivity(intent);
	}
	
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
    	savedInstanceState.putString("type", mType);
    	savedInstanceState.putString("adurl", url);
    	savedInstanceState.putString("imgurl", imgUrl);
    }
 
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
    	mType  = savedInstanceState.getString("type");
    	url  = savedInstanceState.getString("adurl");
    	imgUrl  = savedInstanceState.getString("imgurl");
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_ad_webview);
		if(Common.isTrue(SettingManager.getInst().getNightModel())){  
			StatusBarUtil.setBar(this, getResources().getColor(R.color.night_bg_color), false);
        }else{  
        	StatusBarUtil.setBar(this, getResources().getColor(R.color.mainbgcolor), true);
        }
		super.onCreate(savedInstanceState);
		
		mType = this.getIntent().getExtras().getString("type");
		url = this.getIntent().getExtras().getString("adurl");
		imgUrl = this.getIntent().getExtras().getString("imgurl");
		
		//这里进行判断是否为商品(是的话，带入cookie)..
		if(!TextUtils.isEmpty(SPHelper.getInst().getString(SPHelper.KEY_MALL_URL))){
			if(url.contains(SPHelper.getInst().getString(SPHelper.KEY_MALL_URL)) || url.contains(JUrl.URL_DEFAULT_SHOPURL)){
				CookieSyncManager.createInstance(getActivity());    
			    cookieManager = CookieManager.getInstance();
				
				if(App.getInst().isLogin()){
					PersistentCookieStore myCookieStore = new PersistentCookieStore(App.getInst());
					List<Cookie> cookies = myCookieStore.getCookies();  
					if(!Common.isListEmpty(cookies)){
					    cookieManager.removeAllCookie();  
					    cookieManager.setAcceptCookie(true);  
					    for (int i = 0; i < cookies.size(); i++) {  
					        String cookieString = cookies.get(i).getName() + "=" + cookies.get(i).getValue() +  
					                ";domain="+ cookies.get(i).getDomain();    
					        cookieManager.setCookie(cookies.get(i).getDomain(), cookieString);    
					    }  
					    CookieSyncManager.getInstance().sync();  
					}
				}else{
					//TODO 根据需求决定是直接清除cookie还是直接传给后台
					String cookie = cookieManager.getCookie(url);
					cookieManager.setCookie(url, cookie);
				}
			}
		}
		
		iv_ad_close = (ImageView) findViewById(R.id.ad_close);
		tv_ad_title = (TextView) findViewById(R.id.ad_title);
		view = (WebView) this.findViewById(R.id.ad_webview);
		back = (ImageView) this.findViewById(R.id.back);
		forward = (ImageView) this.findViewById(R.id.forward);
		refresh = (ImageView) this.findViewById(R.id.refresh);
		close = (ImageView) this.findViewById(R.id.close);
		share = (ImageView) this.findViewById(R.id.share);
		view.setWebChromeClient(new myWebChromeClient());
		view.setWebViewClient(new myWebViewClient());
		view.getSettings().setJavaScriptEnabled(true);
		view.loadUrl(url);
		this.setClickListener();

	}	
	private class myWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

	}
	private class myWebChromeClient extends WebChromeClient{
		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			tv_ad_title.setText(title);
			mTitle = title;
		}
	}
	private void setClickListener(){
		iv_ad_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleActivityClose(mType);
			}
		});
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(view.canGoBack()){
					view.goBack();
				}
			}
		});
		forward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (view.canGoForward()) {
					view.goForward();
				}
			}
		});
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				view.reload();
			}
		});
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleActivityClose(mType);
			}
		});
		share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PicArticleDialog dialog = new PicArticleDialog();
				dialog.setData(url, imgUrl, mTitle);
				dialog.showAllowingStateLoss(AdDetailActivity.this, getSupportFragmentManager(),
						PicArticleDialog.class.getSimpleName());
			}
		});
	}
	
	protected void handleActivityClose(String type) {
		if (TextUtils.equals(type, TYPE_AD)) {
			nextActivity();
		}
		finish();
	}

	public void nextActivity() {
		HomeSingleActivity.launch(this);
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		UmengShare.getInstance().onActivityResult(arg0, arg1, arg2);
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		handleActivityClose(mType);
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public void initViews() {
		
	}

	@Override
	public void initDatas() {
		
	}

	@Override
	public void installListeners() {
	}

	@Override
	public void processHandlerMessage(Message msg) {
	}
}
