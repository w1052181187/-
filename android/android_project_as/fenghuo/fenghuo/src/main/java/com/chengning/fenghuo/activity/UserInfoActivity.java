package com.chengning.fenghuo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.Browser;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.base.SimpleFragmentPagerAdapter;
import com.chengning.common.util.HttpUtil;
import com.chengning.common.widget.extend.TabTextViewPageIndicator;
import com.chengning.common.widget.extend.TabTextViewPageIndicator.OnAddTabListener;
import com.chengning.common.widget.extend.TabTextViewPageIndicator.TabView;
import com.chengning.fenghuo.App;
import com.chengning.fenghuo.Consts;
import com.chengning.fenghuo.LoginManager;
import com.chengning.fenghuo.MyJsonHttpResponseHandler;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.SettingManager;
import com.chengning.fenghuo.data.access.UserinfoOtherServer;
import com.chengning.fenghuo.data.bean.UserInfoBean;
import com.chengning.fenghuo.fragment.UserDynamicFragment;
import com.chengning.fenghuo.fragment.UserInfoFragment;
import com.chengning.fenghuo.util.ArticleManagerUtils;
import com.chengning.fenghuo.util.Common;
import com.chengning.fenghuo.util.JUrl;
import com.chengning.fenghuo.util.UIHelper;
import com.chengning.fenghuo.util.Utils;
import com.chengning.fenghuo.widget.TitleBar;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

public class UserInfoActivity extends BaseFragmentActivity {

	private static final int MSG_UI_TOP = 1;
	private static final int MSG_UI_CONTENT = 2;

	private static final int BG_SUCCESS = 3;

	private static final int MSG_UI_FAILURE = 4;

	private FragmentManager mFragmentManager;

	private RelativeLayout mUserTopLayout;
	private View mBtnLayout;
	private Button mSendMsgBtn;
	private Button mFocusBtn;
	private ImageView mUserTopBg;
	private ImageView mUserImage;
	private TextView mUserName;
	private TextView mUserRelitionCountFollow;
	private TextView mUserRelitionCountFans;
//	private View mSendMsgLayout;
//	private View mFocusLayout;
//	private TextView mFocus;
	private TabTextViewPageIndicator mIndicator;
	private ViewPager mPager;
	private SimpleFragmentPagerAdapter mAdapter;

	private UserInfoBean mTargetUserInfoBean;

	protected LayerDrawable bg;

	private TitleBar titleBar;

//	private View mBottomLayout;

	private boolean isFollow;

	private App app;

	private boolean isColumnist;

	private ArrayList<String> strs;

	private ArrayList<Fragment> fragmentsList;
	private int lastNightModel;
	private String mUid;
	private String userName;
	private AppBarLayout mAppBar;
	protected int height;
	private AppBarLayout.OnOffsetChangedListener mOffsetListener;


	public static void launch(Activity from, String nickName) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(
				Consts.BASE_SCHEME + "userinfo://@%s", nickName)));
		intent.putExtra(Browser.EXTRA_APPLICATION_ID, from.getPackageName());
		from.startActivity(intent);
	}

	public static void launchByUid(Activity from, String uid) {
		Intent intent = new Intent(from, UserInfoActivity.class);
		intent.putExtra("uid", uid);
		from.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		this.setContentView(R.layout.activity_user_info);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void initViews() {
		mFragmentManager = getSupportFragmentManager();

		titleBar = new TitleBar(getActivity(), true);
		titleBar.setBackText("返回", getResources().getColor(R.color.white),
				getResources().getDrawable(R.drawable.nav_back_white));
		titleBar.setBackTextBackgroundColor(getResources().getColor(
				R.color.transparent));
		titleBar.setBackgroundColor(getResources()
				.getColor(R.color.transparent));

		mAppBar = (AppBarLayout) findViewById(R.id.user_info_appbar);
		mUserTopLayout = (RelativeLayout) findViewById(R.id.user_profile_top);
		mBtnLayout = findViewById(R.id.user_profile_btn_layout);
		mSendMsgBtn = (Button) findViewById(R.id.user_profile_sendmsg);
		mFocusBtn = (Button) findViewById(R.id.user_profile_focus);
		mUserTopBg = (ImageView) findViewById(R.id.user_profile_top_bg);
		mUserImage = (ImageView) findViewById(R.id.user_profile_user_image);
		mUserName = (TextView) findViewById(R.id.user_profile_user_name_text);
		mUserRelitionCountFollow = (TextView) findViewById(R.id.user_profile_relition_count_follow_text);
		mUserRelitionCountFans = (TextView) findViewById(R.id.user_profile_relition_count_fans_text);

//		mBottomLayout = findViewById(R.id.user_profile_at_chat_layout);
//		mSendMsgLayout = findViewById(R.id.user_profile_at_user_btn_layout);
//		mFocusLayout = findViewById(R.id.user_profile_focus_btn_layout);
//		mFocus = (TextView) findViewById(R.id.user_profile_focus_btn);

		mIndicator = (TabTextViewPageIndicator) findViewById(R.id.user_profile_indicator);
		mPager = (ViewPager) findViewById(R.id.user_profile_pager);
		
		
	}

	@Override
	public void initDatas() {

//		if (!LoginManager.getInst().checkLoginWithNotice(getActivity())) {
//			finish();
//			return;
//		}

		Uri data = getIntent().getData();
		mUid = getIntent().getStringExtra("uid");
		if (data != null) {
			String d = data.toString();
			int index = d.lastIndexOf("/");
			userName = d.substring(index + 1);
			if (userName.indexOf("@") == 0)
				userName = userName.substring(1);
			if (userName.contains(":")) {
				String[] strings = userName.split(":");
				if (0 < strings.length) {
					userName = strings[0];
				}
			}

			initUserInfoByNickname(userName);
		} else if (!TextUtils.isEmpty(mUid)) {
			initUserInfoByUid(mUid);
		} else {
			finish();
			return;
		}

	}

	/**
	 * 初始化用户信息
	 * 
	 * @param userName
	 */
	private void initUserInfoByNickname(String userName) {
		// TODO 判断是否是登录用户
		initData();
		titleBar.setTitle(userName, getResources().getColor(R.color.white));
		if (null != app.getUserInfoBean()
				&& userName.equals(app.getUserInfoBean().getNickname())) {
			mTargetUserInfoBean = app.getUserInfoBean();
		} else {
			mTargetUserInfoBean = UserinfoOtherServer.getInst(getActivity())
					.queryTargetNickname(userName);
		}

		getNewsListByHttpByNickname(userName);

	}

	private void initUserInfoByUid(String uid) {

		initData();
		if (null != app.getUserInfoBean()
				&& uid.equals(app.getUserInfoBean().getUid())) {
			mTargetUserInfoBean = app.getUserInfoBean();
		} else {
			mTargetUserInfoBean = UserinfoOtherServer.getInst(getActivity())
					.queryTargetUid(uid);
		}
		getNewsListByHttp(uid);

	}

	private void initData() {
		app = App.getInst();

		strs = new ArrayList<String>();
		fragmentsList = new ArrayList<Fragment>();
		mAdapter = new SimpleFragmentPagerAdapter(mFragmentManager,
				fragmentsList, strs);

		mPager.setAdapter(mAdapter);
		mPager.setOffscreenPageLimit(0);
		mIndicator.setViewPager(mPager);
		mIndicator.setOnPageChangeListener(pageChangeListener);

		Message msg = getHandler().obtainMessage(MSG_UI_TOP,
				mTargetUserInfoBean);
		msg.sendToTarget();
		lastNightModel = SettingManager.getInst().getNightModel();
	}

	@Override
	public void installListeners() {

		mIndicator.setOnAddTabListener(new OnAddTabListener() {

			@Override
			public TabView onAddTab(Context context) {
				TabView tabView = mIndicator.new TabView(getActivity());
				if (!Common.isTrue(SettingManager.getInst().getNightModel())) {
					tabView.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.news_tab_indicator_white));
				} else {
					tabView.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.news_tab_indicator_night));
				}

				return tabView;
			}
		});

		mUserRelitionCountFollow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
		mUserRelitionCountFans.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});

//		mSendMsgLayout.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				sendMsg(mTargetUserInfoBean);
//
//			}
//		});
//		mFocusLayout.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				changeFollow(mTargetUserInfoBean);
//			}
//		});
		mSendMsgBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendMsg(mTargetUserInfoBean);
			}
		});
		mFocusBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changeFollow(mTargetUserInfoBean);
			}
		});
		
		final int minHeight = getResources().getDimensionPixelSize(R.dimen.titile_bar_height);
		mOffsetListener = new AppBarLayout.OnOffsetChangedListener() {
			
			@Override
			public void onOffsetChanged(AppBarLayout arg0, int arg1) {
				
				height = mUserTopLayout.getMeasuredHeight() - minHeight + arg1;
				if (height == 0) {
					setTitleBar(true);
				} else {
					setTitleBar(false);
				}
			}
		};
		mAppBar.addOnOffsetChangedListener(mOffsetListener);
		
	}

	/**
	 * 发送消息
	 * 
	 * @param bean
	 */
	protected void sendMsg(final UserInfoBean bean) {
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		intent.putExtra(Consts.CHAT_ENTRY, Consts.ChatAction.USER_INFO);
		intent.putExtra(Consts.MEMBER_ID, bean.getUid());
		intent.putExtra("name", bean.getNickname());
		startActivity(intent);
	}

	@Override
	public void uninstallListeners() {

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public void onDestroy() {
		super.onDestroy();
	}

	public void onResume() {
		super.onResume();
		if (SettingManager.getInst().getNightModel() != lastNightModel) {
			finish();
			if (!TextUtils.isEmpty(userName)) {
				launch(getActivity(), userName);
			} else if (!TextUtils.isEmpty(mUid)) {
				launchByUid(getActivity(), mUid);
			}
			
		}
	}

	public void onPause() {
		super.onPause();
	}

	@SuppressLint("NewApi")
	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) {
		case MSG_UI_TOP:

			mTargetUserInfoBean = (UserInfoBean) msg.obj;
			if (null != mTargetUserInfoBean) {
				handleTopData();
			}

			break;
		case MSG_UI_CONTENT:
			mTargetUserInfoBean = (UserInfoBean) msg.obj;
			if (null != mTargetUserInfoBean) {
				handleContent();
			}
			break;
		case BG_SUCCESS:
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				mUserTopLayout.setBackground(bg);
			} else {
				mUserTopLayout.setBackgroundDrawable(bg);
			}
			bg = null;
			break;
		case MSG_UI_FAILURE:
			break;
		default:
			break;
		}
	}

	private void handleContent() {
		handleTopData();
		addFragments(isColumnist);
		if (app.isLogin()
				&& app.getUserInfoBean().getUid()
						.equals(mTargetUserInfoBean.getUid())) {
			LoginManager.getInst().saveUserInfo(mTargetUserInfoBean);
		} else {
			if (UserinfoOtherServer.getInst(getActivity()).queryTargetUid(
					mTargetUserInfoBean.getUid()) == null) {
				UserinfoOtherServer.getInst(getActivity()).insertOne(
						mTargetUserInfoBean);
			} else {
				UserinfoOtherServer.getInst(getActivity()).updateTargetUid(
						mTargetUserInfoBean);
			}
		}
	}

	private void handleTopData() {
		isColumnist = Common.isTrue(mTargetUserInfoBean.getSubscribe());
		titleBar.setTitle(mTargetUserInfoBean.getNickname(), getResources()
				.getColor(R.color.white));
		setBackground(mUserTopLayout, mTargetUserInfoBean);
		Utils.setCircleImage(mTargetUserInfoBean.getFace(), mUserImage);
		// UserVip.showFace(mTargetUserInfoBean, mUserImage, true);
		// UserVip.showVip(mTargetUserInfoBean.getVip_pic(), mUserVipImage);

		if (!isColumnist) {
			Common.handleUserNameDisplay(getActivity(), mTargetUserInfoBean,
					mUserName, true);
		} else {
			mUserName.setText(mTargetUserInfoBean.getNickname());
		}
		
		mUserName.setCompoundDrawablesWithIntrinsicBounds(null, null,
				Utils.getGenderDrawable(mTargetUserInfoBean.getGender()), null);

		mUserRelitionCountFollow.setText("关注 "
				+ mTargetUserInfoBean.getFollow_count());
		mUserRelitionCountFans.setText("粉丝 "
				+ mTargetUserInfoBean.getFans_count());

		// TODO 关注
		handleBottomFollowVisibility(mTargetUserInfoBean);
	}

	/**
	 * 处理发送消息和关注是否可见
	 * 
	 * @param uBean
	 */
	private void handleBottomFollowVisibility(UserInfoBean uBean) {
		if (app.isLogin()
				&& app.getUserInfoBean().getNickname()
						.equals(uBean.getNickname())) {
//			mBottomLayout.setVisibility(View.GONE);
			mBtnLayout.setVisibility(View.GONE);
			return;
		}
		mBtnLayout.setVisibility(View.VISIBLE);
//		mBottomLayout.setVisibility(View.GONE);
		isFollow = Common.isTrue(mTargetUserInfoBean.getIs_follow());
		handleFollowState(isFollow);
	}

	/**
	 * 设置用户头像模糊背景
	 * 
	 * @param layout
	 * @param bean
	 */
	private void setBackground(final RelativeLayout layout, UserInfoBean bean) {
		ImageLoader.getInstance().displayImage(bean.getProfile_image(), mUserTopBg);
		
//		ImageLoader.getInstance().loadImage(bean.getFace(),
//				new SimpleImageLoadingListener() {
//
//					@Override
//					public void onLoadingComplete(String imageUri, View view,
//							final Bitmap loadedImage) {
//						mUserTopLayout.getViewTreeObserver()
//								.addOnPreDrawListener(new OnPreDrawListener() {
//
//									private boolean hasMeasured;
//
//									@Override
//									public boolean onPreDraw() {
//										if (hasMeasured == false) {
//											DisplayUtil.getInst().init(
//													getActivity());
//											int with = DisplayUtil.getInst()
//													.getScreenWidth();
//											int height = mUserTopLayout
//													.getMeasuredHeight();
//											Bitmap bmp = ImageUtils.bigBitmap(
//													loadedImage, with, height);
//											bg = Utils.blur(getActivity(), bmp,
//													mUserTopLayout);
//											bmp.recycle();
//											getHandler().obtainMessage(
//													BG_SUCCESS, bg)
//													.sendToTarget();
//											hasMeasured = true;
//										}
//										return true;
//									}
//								});
//
//						super.onLoadingComplete(imageUri, view, loadedImage);
//					}
//
//				});
	}

	/**
	 * 从json 获得数据
	 * 
	 * @param userName
	 */
	private void getNewsListByHttpByNickname(String userName) {
		String url = JUrl.SITE + JUrl.Get_USER_INFO_BY_NICKNAME
				+ URLEncoder.encode(userName);
		HttpUtil.get(getActivity(), url, null, new MyJsonHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				Common.handleHttpFailure(getActivity(), throwable);
				getHandler().obtainMessage(MSG_UI_FAILURE).sendToTarget();
				UIHelper.removePD();
			}

			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				Gson gson = new Gson();
				UserInfoBean bean = gson.fromJson(data, UserInfoBean.class);

				Message msg = getHandler().obtainMessage(MSG_UI_CONTENT, bean);
				msg.sendToTarget();

				UIHelper.removePD();
			}

			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {

				UIHelper.removePD();

				new AlertDialog.Builder(getActivity())
						.setTitle("注意")
						.setMessage("抱歉，" + message)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										finish();
									}
								}).setCancelable(false).create().show();
			};
		});
	}

	/**
	 * 从json 获得数据
	 */
	private void getNewsListByHttp(String uid) {
		String url = JUrl.SITE + JUrl.Get_USER_INFO + uid;
		HttpUtil.get(getActivity(), url, null, new MyJsonHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				Common.handleHttpFailure(getActivity(), throwable);
				getHandler().obtainMessage(MSG_UI_FAILURE).sendToTarget();
				UIHelper.removePD();
			}

			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				Gson gson = new Gson();
				UserInfoBean bean = gson.fromJson(data, UserInfoBean.class);

				Message msg = getHandler().obtainMessage(MSG_UI_CONTENT, bean);
				msg.sendToTarget();
				UIHelper.removePD();
			}

			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {

				UIHelper.removePD();

				new AlertDialog.Builder(getActivity())
						.setTitle("注意")
						.setMessage("抱歉，" + message)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										finish();
									}
								}).setCancelable(false).create().show();
			};
		});
	}

	void addFragments(boolean isColumnist) {
		strs.clear();
		strs.add("资料");
		if (isColumnist) {
			strs.add("文章");
		}
		strs.add("圈子");

		UserInfoFragment aFragment = UserInfoFragment.newInstance(mTargetUserInfoBean);
		UserDynamicFragment bFragment = UserDynamicFragment.newInstace(
				null != mTargetUserInfoBean ? mTargetUserInfoBean.getUid()
						: null, false);
		fragmentsList.clear();
		fragmentsList.add(aFragment);
		if (isColumnist) {
			UserDynamicFragment cFragment = UserDynamicFragment.newInstace(
					null != mTargetUserInfoBean ? mTargetUserInfoBean.getUid()
							: null, true);
			fragmentsList.add(cFragment);
		}

		fragmentsList.add(bFragment);
		mAdapter.notifyDataSetChanged();
		mIndicator.notifyDataSetChanged();
	}
	private void changeFollow(UserInfoBean bean) {

		ArticleManagerUtils.followUser(getActivity(), bean.getUid(),
				new Runnable() {

					@Override
					public void run() {
						isFollow = !isFollow;
						handleFollowState(isFollow);
					}
				});
	}

	/**
	 * 处理关注状态
	 * 
	 * @param isFollow
	 */
	protected void handleFollowState(boolean isFollow) {
//		mFocus.setText(isFollow ? getString(R.string.str_userinfo_already_focused)
//				: getString(R.string.str_userinfo_not_focused));
		mFocusBtn.setText(isFollow ? getString(R.string.str_userinfo_already_focused)
				: getString(R.string.str_userinfo_not_focused));
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			if (arg0 == 0) {
				fragmentsList.get(arg0).setUserVisibleHint(true);
			} else {
				fragmentsList.get(0).setUserVisibleHint(false);
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};


	/**
	 * 设置titleBar
	 * 
	 * @param b
	 */
	protected void setTitleBar(boolean b) {
		if (mTargetUserInfoBean == null) {
			return;
		}
		if (b) {
			titleBar.setBackgroundColor(Common.isTrue(SettingManager.getInst()
					.getNightModel()) ? getResources().getDrawable(
					R.drawable.nav_bg_night) : getResources().getDrawable(
					R.drawable.nav_bg));
			titleBar.setBackText("返回",
					getResources().getColor(R.color.title_bar_title_text),
					getResources().getDrawable(R.drawable.nav_back_white));
			titleBar.setTitle(
					mTargetUserInfoBean.getNickname(),
					Common.isTrue(SettingManager.getInst().getNightModel()) ? getResources()
							.getColor(R.color.night_text_color)
							: getResources().getColor(
									R.color.title_bar_title_text));
		} else {
			titleBar.setBackgroundColor(getResources().getColor(
					R.color.transparent));
			titleBar.setBackText("返回", getResources().getColor(R.color.white),
					getResources().getDrawable(R.drawable.nav_back_white));
			titleBar.setTitle(mTargetUserInfoBean.getNickname(), getResources()
					.getColor(R.color.white));
		}
	}

	@Override
	public Activity getActivity() {
		return UserInfoActivity.this;
	}

}
