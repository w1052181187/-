package com.chengning.yiqikantoutiao.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.util.SerializeUtil;
import com.chengning.common.widget.MultiStateView;
import com.chengning.yiqikantoutiao.App;
import com.chengning.yiqikantoutiao.Consts;
import com.chengning.yiqikantoutiao.LoadStateManager;
import com.chengning.yiqikantoutiao.LoginManager;
import com.chengning.yiqikantoutiao.R;
import com.chengning.yiqikantoutiao.adapter.ArticleCommentItemAdapter;
import com.chengning.yiqikantoutiao.base.BaseListBean;
import com.chengning.yiqikantoutiao.base.BasePageListActivity;
import com.chengning.yiqikantoutiao.data.bean.BaseArticlesBean;
import com.chengning.yiqikantoutiao.data.bean.CommentItemBean;
import com.chengning.yiqikantoutiao.data.bean.CommentListBean;
import com.chengning.yiqikantoutiao.event.CommentSuccessEvent;
import com.chengning.yiqikantoutiao.util.ArticleCommentListeners;
import com.chengning.yiqikantoutiao.util.ArticleManagerUtils;
import com.chengning.yiqikantoutiao.util.ArticleManagerUtils.DataStateListener;
import com.chengning.yiqikantoutiao.util.Common;
import com.chengning.yiqikantoutiao.util.JUrl;
import com.chengning.yiqikantoutiao.util.Utils;
import com.chengning.yiqikantoutiao.widget.ArticleShareDialog;
import com.chengning.yiqikantoutiao.widget.CommentInputDialog;
import com.chengning.yiqikantoutiao.widget.DeleteArticleCommentDialog;
import com.chengning.yiqikantoutiao.widget.DeleteDynamicDialog;
import com.chengning.yiqikantoutiao.widget.RemovePhoneBindDialog;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class ArticleReplyDetailActivity extends BasePageListActivity{

 	private final static int DATA_SUCCESS = 100;

	private View mView;
	private ImageView mCloseImg;
	private TextView mTitle;
	private ImageButton mLikeBtn;
	private ImageButton mShareBtn;
	private TextView mInputTv;

	//header
	private View mHeader;
	private ImageView mHeaderAvatar;
	private TextView mHeaderName;
	private TextView mHeaderLike;
	private TextView mHeaderContent;
	private TextView mHeaderTime;
	private View mHeaderReport;

	private BaseArticlesBean mArticleBean;
	private ArticleManagerUtils mArticleManagerUtils;
	private TextView mHeaderAllCmt;
	private View mFooter;
	private ArticleCommentItemAdapter mAdapter;
	private View mHeaderDelete;
	private DataStateListener mLikeState;

	public static void launch(Activity from, BaseArticlesBean bean) {
		Intent intent = new Intent(from, ArticleReplyDetailActivity.class);
		intent.putExtra("bean", bean);
		from.startActivity(intent);
	}

	@Override
	public BaseFragmentActivity buildContext() {
		return this;
	}

	@Override
	public String buildUrl() {
		String url = JUrl.SITE + JUrl.URL_GET_REPLY_DETAIL;
		url = JUrl.appendTid(url,mArticleBean.getRoottid());
		url = JUrl.append(url,"replyid",mArticleBean.getTid());
		return url;
	}

	@Override
	public BaseAdapter buildAdapter(FragmentActivity activity, List list) {
		mAdapter = new ArticleCommentItemAdapter(activity, list, true);
		return mAdapter;
	}


	@Override
	public String buildMaxId(List list) {
		return ((CommentItemBean)list.get(0)).getTid();
	}

	@Override
	public String configTitle() {
		return "";
	}

	@Override
	public String configNoData() {
		return "";
	}

	@Override
	public View configContentView() {
		mView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_article_reply_detail, null);
		return mView;
	}

	@Override
	public void initExtraView() {
		mCloseImg = (ImageView) mView.findViewById(R.id.reply_detail_close);
		mTitle = (TextView) mView.findViewById(R.id.reply_detail_title);
		mLikeBtn = (ImageButton) mView.findViewById(R.id.reply_detail_bottom_toolbar_like);
		mShareBtn = (ImageButton) mView.findViewById(R.id.reply_detail_bottom_toolbar_share);
		mInputTv = (TextView) mView.findViewById(R.id.reply_detail_bottom_toolbar_input);
		initHeaderView();
		initFooterView();
	}

	@Override
	public void initExtraData() {
		mArticleBean = (BaseArticlesBean)getIntent().getSerializableExtra("bean");

		Uri data = getIntent().getData();
		if (data != null) {
			String d = data.toString();
			String beanStr = d.substring(Consts.REPLY_DETAIL_SCHEME.length());
			mArticleBean = (BaseArticlesBean) SerializeUtil.deSerialize(beanStr);
		}

		if (mArticleBean == null) {
			finish();
		}

		getTitleBar().setVisibility(View.GONE);
		getPullListView().setMode(PullToRefreshBase.Mode.DISABLED);
		 mLikeState = new DataStateListener() {

			@Override
			public void success(Object... objects) {
				if (objects == null) {
					return;
				}
				String digCounts = (String) objects[0];
				mLikeBtn.setEnabled(true);
				setLikeState(true, digCounts);
			}

			@Override
			public void init() {
				mLikeBtn.setEnabled(false);
			}

			@Override
			public void failure() {
				mLikeBtn.setEnabled(true);
			}
		};

		mArticleManagerUtils = new ArticleManagerUtils();

		int replys = mArticleBean.getReplys();
		mTitle.setText(replys == 0 ? "暂无回复" : replys + "条回复");
		initHeaderData(getActivity(), mArticleBean);
		EventBus.getDefault().register(getActivity());
	}

	private void initHeaderView() {
		mHeader = LayoutInflater.from(getActivity()).inflate(R.layout.header_reply_detail,null);
		mHeaderAvatar = (ImageView)mHeader.findViewById(R.id.reply_detail_top_avatar);
		mHeaderName = (TextView)mHeader.findViewById(R.id.reply_detail_top_name);
		mHeaderLike = (TextView)mHeader.findViewById(R.id.reply_detail_top_like);
		mHeaderContent = (TextView)mHeader.findViewById(R.id.reply_detail_top_content);
		mHeaderTime = (TextView)mHeader.findViewById(R.id.reply_detail_top_time);
		mHeaderReport = mHeader.findViewById(R.id.reply_detail_top_report);
		mHeaderDelete = mHeader.findViewById(R.id.reply_detail_top_delete);
		mHeaderAllCmt = (TextView)mHeader.findViewById(R.id.reply_detail_top_all_cmt);
	}


	/**
	 * 处理点赞
	 * @param context
	 * @param bean
	 */
	private void handleLikeClick(Activity context, BaseArticlesBean bean) {
		mArticleManagerUtils.doLike(context, bean, mLikeState);
	}

	/**
	 * 初始化顶部header
	 * @param context
	 * @param bean
	 */
	private void initHeaderData(Activity context, BaseArticlesBean bean) {
		ImageLoader.getInstance().displayImage(bean.getFace(), mHeaderAvatar);
		Common.handleUserNameDisplay(getActivity(), bean, mHeaderName);
		int digCount = bean.getDigcounts();
		setLikeState(Common.isTrue(bean.getIs_dig()), digCount == 0 ? "赞" : String.valueOf(digCount));
		mHeaderContent.setText(Utils.handleDynamicContentConvert(bean));
		mHeaderTime.setText(Common.dateCompareNow(bean.getDateline()));
		boolean isSelf = App.getInst().isLogin() &&
				LoginManager.getInst().getLoginUserBean().getUserinfo().getUid().equals(bean.getUid());
		mHeaderReport.setVisibility(isSelf ? View.GONE: View.VISIBLE);
		mHeaderDelete.setVisibility(isSelf ? View.VISIBLE : View.GONE);
	}

	private void setLikeState(boolean isDig, String digCounts) {
		mHeaderLike.setSelected(isDig);
		mHeaderLike.setText(digCounts);
		mLikeBtn.setSelected(isDig);
	}

	private void initFooterView() {
		mFooter = LayoutInflater.from(getActivity()).inflate(R.layout.footer_reply_detail,null);
	}

	@Override
	public void initExtraListener() {

		mAdapter.setCommentDeleteListener(new ArticleCommentItemAdapter.ArticleCommentDeleteListener() {
			@Override
			public void deleteSuccess(BaseArticlesBean bean) {
				if (Common.isListEmpty(mAdapter.getList())) {
					setAllCmt(false);
					getPullListView().getRefreshableView().removeFooterView(mFooter);
				}
			}
		});

		mCloseImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mLikeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleLikeClick(getActivity(),mArticleBean);
			}

		});
		mShareBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mArticleBean) {
					ArticleShareDialog dialog = new ArticleShareDialog();
					dialog.setBean(mArticleBean);
					dialog.show(getActivity().getSupportFragmentManager(),
							ArticleShareDialog.class.getSimpleName());
				}
			}
		});

		mInputTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mArticleBean) {
					CommentInputDialog dialog = new CommentInputDialog();
					dialog.setBean(getActivity(), mArticleBean, Consts.ArticleCommentType.REPLY);
					dialog.show((getActivity()).getSupportFragmentManager(), CommentInputDialog.class.getSimpleName());

				}
			}
		});

		//初始化header监听
		mHeaderLike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleLikeClick(getActivity(),mArticleBean);
			}
		});

		mHeaderReport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mArticleManagerUtils.getReport(getActivity(), mArticleBean.getTid());
			}
		});

		mHeaderContent.setTag(mArticleBean);
		mHeaderContent.setOnLongClickListener(new ArticleCommentListeners.ArticleCommentOnLongClickListener(getActivity(),
				null, mHeaderContent.getText().toString(), true));

		mHeaderDelete.setTag(mArticleBean);
		mHeaderDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleDelete(v);
			}
		});
	}

	private void handleDelete(final View v) {
		DeleteArticleCommentDialog dialog = new DeleteArticleCommentDialog();
		dialog.setData(new RemovePhoneBindDialog.RemovePhoneBindOkLitener() {

			@Override
			public void confirm() {
				BaseArticlesBean bean = (BaseArticlesBean) v.getTag();
				new ArticleManagerUtils().deleteDynamic(getActivity(), bean.getTid(), new ArticleManagerUtils.DeleteListener() {
					@Override
					public void deleteSuccess(String data) {
						finish();
					}
				});
			}

			@Override
			public void cancle() {
			}
		});
		dialog.show(((FragmentActivity) getActivity()).getSupportFragmentManager(),
				DeleteDynamicDialog.class.getSimpleName());
	}

	@Override
	public void handleItemClick(AdapterView parent, View view, int position, long id) {

	}
	@Override
	public BaseListBean<CommentItemBean> handleHttpSuccess(Gson gson,
														   String data) {
		CommentListBean bean = gson.fromJson(data, CommentListBean.class);
		handleData(getActivity(), bean);
		return bean;
	}

	private void setAllCmt(boolean isHasData) {
		mHeaderAllCmt.setText(isHasData ? "全部评论" : "抢先评论");
		if (!isHasData) {
			mHeaderAllCmt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CommentInputDialog dialog = new CommentInputDialog();
					dialog.setBean(getActivity(), mArticleBean, Consts.ArticleCommentType.REPLY);
					dialog.show(getActivity().getSupportFragmentManager(), CommentInputDialog.class.getSimpleName());
				}
			});
		}

		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mHeaderAllCmt.getLayoutParams();
		int left = isHasData ? getResources().getDimensionPixelOffset(R.dimen.comment_reply_allcmt_margin_left) :
				getResources().getDimensionPixelOffset(R.dimen.comment_reply_nocmt_margin_left);
		lp.leftMargin = left;
		mHeaderAllCmt.setLayoutParams(lp);
	}


	private void handleData(final Activity context, CommentListBean bean) {
		getPullListView().getRefreshableView().removeHeaderView(mHeader);
		getPullListView().getRefreshableView().removeFooterView(mFooter);
		getPullListView().getRefreshableView().addHeaderView(mHeader,null,false);
		if (bean != null && !Common.isListEmpty(bean.getList())) {
			setAllCmt(true);
			if (bean.getTotal_page() == 1) {
				getPullListView().getFootView().removeAllViews();
				getPullListView().getRefreshableView().addFooterView(mFooter, null, false);
			}

		}
	}

	@Override
	public void handleNoData() {
		getPullListView().setVisibility(View.VISIBLE);
		setAllCmt(false);
		super.handleNoData();
	}

	@Subscribe
	public void onEventMainThread(CommentSuccessEvent event) {
		if (event == null || event.getType() != Consts.ArticleCommentType.REPLY) {
			return;
		}
		CommentItemBean bean = event.getmBean();
		List list = mAdapter.getList();
		if (bean != null) {
			if (!Common.isListEmpty(list)) {
				list.add(0, bean);

			} else {
				list.add(bean);
			}
			mAdapter.setList(list);
			mAdapter.notifyDataSetChanged();
			setAllCmt(!Common.isListEmpty(list));

		}

	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(getActivity());
		super.onDestroy();
	}

	@Override
	public void handleHttpFailure(LoadStateManager mLoadStateManager, MultiStateView mMultiStateView, int status, String message, String data) {
		if (status == 1) {
			mLoadStateManager.setState(LoadStateManager.LoadState.NoData);
			mMultiStateView.setEmptyHint(message);
			mInputTv.setEnabled(false);
			mLikeBtn.setEnabled(false);
			mShareBtn.setEnabled(false);
		}
	}
}
