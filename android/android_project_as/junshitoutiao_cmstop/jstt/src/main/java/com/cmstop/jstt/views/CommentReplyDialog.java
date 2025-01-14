package com.cmstop.jstt.views;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.avos.avoscloud.AVInstallation;
import com.chengning.common.app.ActivityInfo.ActivityState;
import com.chengning.common.base.BaseDialogFragment;
import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.base.IBaseActivity;
import com.chengning.common.util.HttpUtil;
import com.cmstop.jstt.Const.LikeAction;
import com.cmstop.jstt.MyStatusResponseHandler;
import com.cmstop.jstt.R;
import com.cmstop.jstt.beans.data.CommentItemBean;
import com.cmstop.jstt.utils.ArticleManagerUtils;
import com.cmstop.jstt.utils.ArticleManagerUtils.LikeState;
import com.cmstop.jstt.utils.CommentCheckUtil;
import com.cmstop.jstt.utils.Common;
import com.cmstop.jstt.utils.JUrl;
import com.cmstop.jstt.utils.UIHelper;
import com.loopj.android.http.RequestParams;

public class CommentReplyDialog extends BaseDialogFragment {
	
	private static final String TAG = CommentReplyDialog.class.getSimpleName();
	
	private View mView;
	private View mCancel;
	private View mReply;
	private View mReport;
	
	private CommentItemBean mBean;
	private View mLike;
	private View mCopy;
	private View mDelete;
	private LikeState mLikeListener;

	private Runnable mDeleteRunnable;

	@Override
	public BaseFragmentActivity getContext() {
		return (BaseFragmentActivity)getActivity();
	}

	@Override
	public View configContentView() {
		mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_comment_reply, null);
		return mView;
	}

	@Override
	public void initView() {
		mCancel = mView.findViewById(R.id.dialog_comment_reply_cancel);
		mReply = mView.findViewById(R.id.dialog_comment_reply_reply);
		mReport = mView.findViewById(R.id.dialog_comment_reply_report);
		mLike = mView.findViewById(R.id.dialog_comment_reply_like);
		mCopy = mView.findViewById(R.id.dialog_comment_reply_copy);
		mDelete = mView.findViewById(R.id.dialog_comment_reply_delete);
	}

	@Override
	public void initData() {
		mBean = (CommentItemBean) getArguments().getSerializable("bean");

	}

	@Override
	public void initListener() {
		mView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismissAllowingStateLoss();
			}
		});
		mCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismissAllowingStateLoss();
			}
		});
		mReply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(getContext() instanceof IBaseActivity && ((IBaseActivity)getContext()).getActivityInfo().getActivityState() == ActivityState.SaveInstanceStated){
					return;
				}
				dismissAllowingStateLoss();
//				if(CommentCheckUtil.checkLoginNotNeededOfComment(getContext(),true,mBean)){
					CommentReplyInputDialog dialog = new CommentReplyInputDialog();
					dialog.setData(mBean);
					dialog.showAllowingStateLoss((BaseFragmentActivity) getContext(), getFragmentManager(), CommentReplyInputDialog.class.getSimpleName());
//				}
			}
		});
		mReport.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				report();
			}
		});
		
		//设置赞
		mLike.setTag(mBean);
		
		LikeState mLikeState = new ArticleManagerUtils.LikeState(){

			@Override
			public void success(String data) {
				mLike.setEnabled(true);
				if (mLikeListener != null) {
					mLikeListener.success(data);
				}
			}
			
			@Override
			public void init() {
				mLike.setEnabled(false);
				if (mLikeListener != null) {
					mLikeListener.init();
				}
			}
			
			@Override
			public void failure() {
				mLike.setEnabled(true);
				if (mLikeListener != null) {
					mLikeListener.failure();
				}
			}
			
		};
		final ArticleManagerUtils mArticleManagerUtils = new ArticleManagerUtils();
		mArticleManagerUtils.setState(mLikeState);
		mLike.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CommentItemBean bean = (CommentItemBean) v.getTag();
				mArticleManagerUtils.doCommentLikeByHttp(getContext(), bean.getId(), LikeAction.GOOD);
				dismissAllowingStateLoss();
			}
		});
		
		mCopy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
		        // 将文本内容放到系统剪贴板里。
		        cm.setText(mBean.getMsg());
		        UIHelper.showToast(getContext(), R.string.comment_copy_hint);
		        dismissAllowingStateLoss();
			}
		});
		
		if (mDeleteRunnable == null) {
			mDelete.setVisibility(View.GONE);
		} else {
			mDelete.setVisibility(View.VISIBLE);
			mDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					deleteComment(mBean, mDeleteRunnable);
					dismissAllowingStateLoss();
				}
			});
		}
    }
	
	/**
	 * 删除评论
	 * @param bean
	 * @param mDeleteRunnable 
	 */
	protected void deleteComment(CommentItemBean bean, final Runnable mDeleteRunnable) {
		mDelete.setEnabled(false);
		if(!Common.hasNet()){
         	Common.showHttpFailureToast(getContext());
         	mDelete.setEnabled(true);
         	return;
		}
		RequestParams params = new RequestParams();
		params.put("id", bean.getId());
		HttpUtil.get(JUrl.SITE + JUrl.URl_DELETE_MY_COMMENT, params , new MyStatusResponseHandler() {
			public void onFailure(int statusCode, Header[] headers,
	    			Throwable throwable, JSONObject error) {

	         	Common.showHttpFailureToast(getContext());
	         	mDelete.setEnabled(true);
	         };
         
			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				mDeleteRunnable.run();
				mDelete.setEnabled(true);
			}
			
			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {
				UIHelper.showToast(getContext(), message);
				mDelete.setEnabled(true);
			}
		});
	}
	
	public void setData(CommentItemBean bean){
		Bundle args = new Bundle();
		args.putSerializable("bean", bean);
		setArguments(args);
	}
	
	public void setListener(LikeState state) {
		this.mLikeListener = state;
	}
	public void setDeleteListener(Runnable r) {
		this.mDeleteRunnable = r;
	}
	
	private void report(){
		mReport.setEnabled(false);
		if(!Common.hasNet()){
         	Common.showHttpFailureToast(getContext());
         	mReport.setEnabled(true);
         	return;
		}
		String url = JUrl.SITE + JUrl.URL_GET_COMMENT_REPORT;
		RequestParams params = new RequestParams();
		params.put("id", mBean.getId());
		params.put("devicetoken", AVInstallation
				.getCurrentInstallation()
				.getInstallationId());
		HttpUtil.getClient().post(url, params, new MyStatusResponseHandler() {
			
	         public void onFailure(int statusCode, Header[] headers,
		    			Throwable throwable, JSONObject error) {

	         	Common.showHttpFailureToast(getContext());
	         	mReport.setEnabled(true);
	         };
			
			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				UIHelper.showToast(getContext(), message);
				dismissAllowingStateLoss();
				mReport.setEnabled(true);
			}
			
			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {
	             
	             UIHelper.showToast(getContext(), message);
	             mReport.setEnabled(true);
			}
		});
	}
	
}
