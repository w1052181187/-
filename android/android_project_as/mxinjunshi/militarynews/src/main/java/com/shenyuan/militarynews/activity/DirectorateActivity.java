package com.shenyuan.militarynews.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chengning.common.base.BaseActivity;
import com.chengning.common.base.BaseStateManager.OnStateChangeListener;
import com.chengning.common.util.DisplayUtil;
import com.chengning.common.util.HttpUtil;
import com.chengning.common.widget.MultiStateView;
import com.chengning.common.widget.MultiStateView.ViewState;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shenyuan.militarynews.App;
import com.shenyuan.militarynews.LoadStateManager;
import com.shenyuan.militarynews.LoadStateManager.LoadState;
import com.shenyuan.militarynews.MyStatusResponseHandler;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.SettingManager;
import com.shenyuan.militarynews.adapter.DirectorateTaskAdapter;
import com.shenyuan.militarynews.beans.data.DirectorateBean;
import com.shenyuan.militarynews.beans.data.DirectorateMemberBean;
import com.shenyuan.militarynews.beans.data.DirectorateTaskBean;
import com.shenyuan.militarynews.utils.Common;
import com.shenyuan.militarynews.utils.JUrl;
import com.chengning.common.util.StatusBarUtil;;
import com.shenyuan.militarynews.utils.UIHelper;
import com.shenyuan.militarynews.utils.Utils;
import com.shenyuan.militarynews.views.LoadFullListView;
import com.shenyuan.militarynews.views.TitleBar;
import com.umeng.analytics.MobclickAgent;

public class DirectorateActivity extends BaseActivity {

	private static final int MSG_UI = 0;

	protected static final int BG_SUCCESS = 1;

	protected static final int HTTP_FAIL = 2;

	private RelativeLayout mAvatarLL;
	private ImageView mAvatar;
	private TextView mName;
	private TextView mRankName;

	private RelativeLayout mRlayout;
	private TextView mPoint;
	private TextView mRank;
	private TextView mUnCompleteNum;
	private TextView mRankShow;
	private TextView mTaskPercent;
	// private Button mAttendanceBtn;

	private LinearLayout mHelpLayoput;

	private LoadFullListView listView;
	private View mListBottomLine;

	private DirectorateTaskAdapter adapter;

	private List<DirectorateTaskBean> mList;

	private DirectorateBean mBean;

	private TitleBar titleBar;

	private LoadStateManager mLoadStateManager;

	private MultiStateView mProgressRefresh;

	@Override
	public void onCreate(Bundle paramBundle) {
		Common.setTheme(getActivity());
		setContentView(R.layout.activity_directorate);
		if(Common.isTrue(SettingManager.getInst().getNightModel())){  
			StatusBarUtil.setBar(this, getResources().getColor(R.color.director_top_bg_color), false);
        }else{  
        	StatusBarUtil.setBar(this, getResources().getColor(R.color.director_top_bg_color), false);
        }
		if (null != paramBundle) {
			mBean = (DirectorateBean) paramBundle.getSerializable("bean");
		}
		super.onCreate(paramBundle);
	}

	@Override
	public void initViews() {

		titleBar = new TitleBar(getActivity(), true);
		titleBar.showDefaultBack();
		titleBar.setTitle("指挥部", getResources().getColor(R.color.white));
		titleBar.setBackgroundColor(getResources()
				.getColor(R.color.transparent));

		mProgressRefresh = (MultiStateView) findViewById(R.id.multiStateView);

		mAvatarLL = (RelativeLayout) findViewById(R.id.directorate_avatar_ll);
		mAvatar = (ImageView) findViewById(R.id.directorate_avatar_img);
		mName = (TextView) findViewById(R.id.directorate_name);
		mRlayout = (RelativeLayout) findViewById(R.id.directorate_poin_or_money_rl);
		mPoint = (TextView) findViewById(R.id.directorate_poin_tv);
		mRankName = (TextView) findViewById(R.id.directorate_rank);
		mRank = (TextView) findViewById(R.id.directorate_rank_tv);
		mUnCompleteNum = (TextView) findViewById(R.id.mid_task_num);
		mHelpLayoput = (LinearLayout) findViewById(R.id.directorate_task_help_ll);
		listView = (LoadFullListView) findViewById(R.id.directorate_listview);
		mListBottomLine = findViewById(R.id.directorate_listview_bottom_line);
		mRankShow = (TextView) findViewById(R.id.directorate_poin_left);
		mTaskPercent = (TextView) findViewById(R.id.directorate_today_task_circle);

	}

	@Override
	public void initDatas() {

		mLoadStateManager = new LoadStateManager();
		mLoadStateManager.setOnStateChangeListener(new OnStateChangeListener<LoadState>() {

			@Override
			public void OnStateChange(LoadState state, Object obj) {
				switch (state) {
				case Init:
					mProgressRefresh.setViewState(ViewState.LOADING);
					break;
				case Success:
					mProgressRefresh.setViewState(ViewState.CONTENT);
					break;
				case Failure:
					mProgressRefresh.setViewState(ViewState.ERROR);
					break;
				default:
					break;
				}
			}

		});

		mLoadStateManager.setState(LoadState.Init);
		if (null != mBean) {
			Message message = getHandler().obtainMessage(MSG_UI, mBean);
			getHandler().sendMessage(message);
		} else {
			getDirectorateMsg(getActivity());
		}

	}

	@SuppressLint("NewApi")
	@Override
	public void processHandlerMessage(Message msg) {

		switch (msg.what) {
		case MSG_UI:
			mBean = (DirectorateBean) msg.obj;
			if (null != mBean) {
				Utils.setCircleImage(mBean.getMember().getFace(),
						mAvatar);
				// username rolename
				mName.setText(mBean.getMember().getUname());
				// user medal

				StringBuilder mTask = new StringBuilder();
				mTask.append("今日任务  ").append(mBean.getMember().getTask_out()).append("/")
						.append(mBean.getMember().getTask());
				mTaskPercent.setText(mTask);
				mRank.setText(mBean.getMember().getRank());
				mPoint.setText(mBean.getMember().getScores());
				mRankName.setText(mBean.getMember().getHonor());
				try {
					int sum = Integer.parseInt(mBean.getMember().getTask());
					int done = Integer
							.parseInt(mBean.getMember().getTask_out());
					mUnCompleteNum.setText((sum - done) + "");
				} catch (Exception e) {
					e.printStackTrace();
				}
				mList = new ArrayList<DirectorateTaskBean>();
				mList = mBean.getTask_list();
				adapter = new DirectorateTaskAdapter(getActivity(), mList);
				adapter.setIcon(mList);
				listView.setAdapter(adapter);
				mLoadStateManager.setState(LoadState.Success);
			}

			break;
		case HTTP_FAIL:

			mLoadStateManager.setState(LoadState.Failure);

		default:
			break;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

		savedInstanceState.putSerializable("bean", mBean);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		mBean = (DirectorateBean) savedInstanceState.getSerializable("bean");
		super.onRestoreInstanceState(savedInstanceState);
	}

	/**
	 * 获取指挥部信息
	 * 
	 * @param context
	 * @param id
	 */
	private void getDirectorateMsg(final Activity context) {
		HttpUtil.get(JUrl.SITE + JUrl.URL_DIRECTORATE_INFO, new MyStatusResponseHandler() {
					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						Common.showHttpFailureToast(getActivity());
						getHandler().obtainMessage(HTTP_FAIL).sendToTarget();
					};

					@Override
					public void onDataSuccess(int status, String mod,
							String message, String data, JSONObject obj) {
						JSONObject jsonObj;
						try {
							jsonObj = new JSONObject(data);

							Gson gson = new Gson();
							DirectorateMemberBean bean = gson.fromJson(
									jsonObj.optString("member"),
									DirectorateMemberBean.class);

							ArrayList<DirectorateTaskBean> taskBeans = gson
									.fromJson(
											jsonObj.optJSONArray("task_list")
													.toString(),
											new TypeToken<ArrayList<DirectorateTaskBean>>() {
											}.getType());
							DirectorateBean directorateBean = new DirectorateBean();
							directorateBean.setMember(bean);
							directorateBean.setTask_list(taskBeans);
							getHandler().obtainMessage(MSG_UI, directorateBean)
									.sendToTarget();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onDataFailure(int status, String mod,
							String message, String data, JSONObject obj) {
						if (-99 == status) {
							UIHelper.showToast(getActivity(), "请先登录.");
						}
						getHandler().obtainMessage(HTTP_FAIL).sendToTarget();
					}
				});

	}

	@Override
	public void installListeners() {

		mProgressRefresh.setRefreshOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mLoadStateManager.setState(LoadState.Init);
				getDirectorateMsg(getActivity());
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				DirectorateTaskDetailActivity.launch(getActivity(),
						mList.get(position).getAction(), position + 1);
			}
		});
		mRankShow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (App.getInst().isLogin()) {
					startActivity(new Intent(getActivity(),
							RankIntroductionActivity.class));
				} else {
					UIHelper.showToast(getActivity(), R.string.no_login_tip);
				}
			}
		});
	}

	@Override
	public void uninstallListeners() {

	}

	public static void launch(Activity context) {
		
		MobclickAgent.onEvent(context, "ucenter_zhihuibu");
		Intent intent = new Intent(context, DirectorateActivity.class);
		context.startActivity(intent);
	}

	@Override
	public Activity getActivity() {
		return DirectorateActivity.this;
	}

}
