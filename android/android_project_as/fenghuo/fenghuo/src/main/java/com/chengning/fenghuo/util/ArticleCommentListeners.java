package com.chengning.fenghuo.util;

import java.util.ArrayList;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.fenghuo.App;
import com.chengning.fenghuo.Consts;
import com.chengning.fenghuo.LoginManager;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.activity.UserInfoActivity;
import com.chengning.fenghuo.data.bean.BaseArticlesBean;
import com.chengning.fenghuo.data.bean.CommentItemBean;
import com.chengning.fenghuo.data.bean.DynamicItemBean;
import com.chengning.fenghuo.util.ArticleManagerUtils.DeleteListener;
import com.chengning.fenghuo.util.ArticleManagerUtils.DataStateListener;
import com.chengning.fenghuo.util.PoPupWindowUtils.PopupLocation;
import com.chengning.fenghuo.widget.ArticleCommentClickDialog;
import com.chengning.fenghuo.widget.CommentInputDialog;
import com.chengning.fenghuo.widget.DynamicCommentClickDialog;
import com.chengning.fenghuo.widget.DynamicCommentInputDialog;
import com.chengning.fenghuo.widget.DynamicCommentInputDialog.DialogCommentListener;

/**
 * 文章或者讨论评论部分相关的监听（用户名、头像、回复、赞、举报、删除等等）
 * @author Administrator
 *
 *
 */
public class ArticleCommentListeners {

	private  Activity mContext;
	
	private ArticleManagerUtils mArticleManagerUtils;

	private DynamicCommentOnClickListener dynamicCommentListener;

	public static class CommentOnClickListener implements OnClickListener{

		private Activity activity;

		public CommentOnClickListener(Activity context) {
			this.activity = context;
		}

		@Override
		public void onClick(View v) {
//				if (LoginManager.getInst().checkLoginWithNotice(mContext)) {
			BaseArticlesBean bean = (BaseArticlesBean) v.getTag();
			CommentInputDialog dialog = new CommentInputDialog();
			dialog.setBean((BaseFragmentActivity)activity, bean, Consts.ArticleCommentType.REPLY);
			dialog.setIsThreeReply(true);
			dialog.show(((BaseFragmentActivity)activity).getSupportFragmentManager(), CommentInputDialog.class.getSimpleName());
//				}
		}

	}

	public static class ReportOnClickListener implements OnClickListener{

		private Activity activity;
		private ArticleManagerUtils mUtils;

		public ReportOnClickListener(Activity context, ArticleManagerUtils utils) {
			this.activity = context;
			this.mUtils = utils;
		}

		@Override
		public void onClick(View v) {
			CommentItemBean bean = (CommentItemBean) v.getTag();
			mUtils.getReport((BaseFragmentActivity) activity, bean.getTid());
		}

	}

	public static class NameOnClickListener implements OnClickListener{

		private Activity activity;

		public NameOnClickListener(Activity context) {
			this.activity = context;
		}
		@Override
		public void onClick(View v) {
			BaseArticlesBean bean = (BaseArticlesBean) v.getTag();
			UserInfoActivity.launch(activity, bean.getNickname());
		}
	}

	public static class CommentOnItemListener implements OnItemClickListener {

		private Activity activity;
		private ArticleManagerUtils mUtils;

		public CommentOnItemListener(Activity context, ArticleManagerUtils utils) {
			this.activity = context;
			this.mUtils = utils;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
			TextView textView = (TextView)view.findViewById(R.id.item_article_comment_item_content);
			final CommentItemBean bean = (CommentItemBean) parent.getTag();
			final PoPupWindowUtils popUtil = new PoPupWindowUtils();
			popUtil.setOnLeftClick(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mUtils.getReport(activity, bean.getHuifu_list().get(position).getTid());
					popUtil.dismiss();
				}
			});
			popUtil.setOnRightClick(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (LoginManager.getInst().checkLoginWithNotice(activity)) {
						CommentInputDialog dialog = new CommentInputDialog();
						dialog.setBean((BaseFragmentActivity)activity, bean.getHuifu_list().get(position));
						dialog.show(((BaseFragmentActivity)activity).getSupportFragmentManager(), CommentInputDialog.class.getSimpleName());
					}
					popUtil.dismiss();
				}
			});
			popUtil.showPopupWindow(activity, textView, R.layout.popupwindow_comment, PopupLocation.TOP);
		}
	}

	public static class LikeOnClickListener implements OnClickListener{

		private DataStateListener mState;
		private Activity activity;
		private ArticleManagerUtils mUtils;

		public LikeOnClickListener(Activity context, ArticleManagerUtils utils, DataStateListener state) {
			this.activity = context;
			this.mUtils = utils;
			this.mState = state;
		}

		@Override
		public void onClick(View v) {
//			if (LoginManager.getInst().checkLoginWithNotice(activity)) {
			BaseArticlesBean bean = (BaseArticlesBean) v.getTag();
			mUtils.doLike(activity, bean, mState);
//			}
		}

	}

	public static class NotLikeOnClickListener implements OnClickListener{

		private Activity activity;
		private ArticleManagerUtils mUtils;
		private DataStateListener mState;

		public NotLikeOnClickListener(Activity context, ArticleManagerUtils utils, DataStateListener state) {
			this.activity = context;
			this.mUtils = utils;
			this.mState = state;
		}

		@Override
		public void onClick(View v) {
//			if (LoginManager.getInst().checkLoginWithNotice(activity)) {
			BaseArticlesBean bean = (BaseArticlesBean) v.getTag();
			mUtils.doNotLike(activity, bean, mState);
//			}
		}

	}

	/**
	 * 文章评论长按
	 * @author Administrator
	 *
	 */
	public static class ArticleCommentOnLongClickListener implements OnLongClickListener{

		private Activity activity;
		private DeleteListener listener;
		private boolean isOnlyCopy = false;
		private String copyContent;

		public ArticleCommentOnLongClickListener(Activity activity,
												 DeleteListener listener, String copyContent, boolean isOnlyCopy) {
			init(activity,listener,copyContent,isOnlyCopy);
		}

		public ArticleCommentOnLongClickListener(Activity activity,
												 DeleteListener listener, String copyContent) {
			init(activity,listener,copyContent,false);
		}

		private void init(Activity activity,
						  DeleteListener listener, String copyContent, boolean isOnlyCopy) {
			this.activity = activity;
			this.listener = listener;
			this.copyContent = copyContent;
			this.isOnlyCopy = isOnlyCopy;
		}

		@Override
		public boolean onLongClick(View v) {
			BaseArticlesBean bean = (BaseArticlesBean) v.getTag();

			ArticleCommentClickDialog commentClickDialog = new ArticleCommentClickDialog();
			commentClickDialog.setData(activity, bean, copyContent, false, listener, isOnlyCopy);
			commentClickDialog.show(((BaseFragmentActivity) activity).getSupportFragmentManager(),
					ArticleCommentClickDialog.class.getSimpleName());
			return true;
		}

	}

	
	/**
	 * 讨论评论点击
	 * @author Administrator
	 *
	 */
	public static class DynamicCommentOnClickListener implements OnClickListener{
		
		private Activity activity;
		private DeleteListener listener;
		private DialogCommentListener commentListener;
		
		DynamicCommentOnClickListener(Activity activity) {
			this.activity = activity;
		}
		
		public DynamicCommentOnClickListener(Activity activity,
				DeleteListener listener , DialogCommentListener commentListener) {
			this.activity = activity;
			this.listener = listener;
			this.commentListener = commentListener;
		}

		@Override
		public void onClick(View v) {
			// TODO 跳到用户界面
			if (LoginManager.getInst().checkLoginWithNotice(activity)) {
				BaseArticlesBean bean = (BaseArticlesBean) v.getTag();
				
				if (bean.getUid().equals(App.getInst().getUserInfoBean().getUid())) {
					DynamicCommentClickDialog commentClickDialog = new DynamicCommentClickDialog();
					String contentTxt = "";
					ArrayList<String> content = null;
					if (bean instanceof CommentItemBean) {
						content = ((CommentItemBean)bean).getContent();
					} else if (bean instanceof DynamicItemBean) {
						content = ((DynamicItemBean)bean).getContent();
					}
					if (!Common.isListEmpty(content)
							&& !TextUtils.isEmpty(content.get(0))) {
						contentTxt = content.get(0);
						
					}
					commentClickDialog.setData(activity, bean, contentTxt, true, listener);
					commentClickDialog.show(((BaseFragmentActivity) activity).getSupportFragmentManager(),
							DynamicCommentClickDialog.class.getSimpleName());
					return;
				}
				DynamicCommentInputDialog dialog = new DynamicCommentInputDialog();
				dialog.setBean(bean);
				dialog.setCommentCallback(commentListener);
				dialog.show(((BaseFragmentActivity)activity).getSupportFragmentManager(), CommentInputDialog.class.getSimpleName());
			}
		}
		
	}

	/**
	 * 讨论评论长按
	 * @author Administrator
	 *
	 */
	public static class DynamicCommentOnLongClickListener implements OnLongClickListener{
		
		private Activity activity;
		private DeleteListener listener;
		private boolean isOnlyCopy = false;
		private String copyContent;
		
		public DynamicCommentOnLongClickListener(Activity activity,
				DeleteListener listener, String copyContent, boolean isOnlyCopy) {
			init(activity,listener,copyContent,isOnlyCopy);
		}
		
		public DynamicCommentOnLongClickListener(Activity activity,
				DeleteListener listener, String copyContent) {
			init(activity,listener,copyContent,false);
		}

		private void init(Activity activity,
				DeleteListener listener, String copyContent, boolean isOnlyCopy) {
			this.activity = activity;
			this.listener = listener;
			this.copyContent = copyContent;
			this.isOnlyCopy = isOnlyCopy;
		}

		@Override
		public boolean onLongClick(View v) {
				BaseArticlesBean bean = (BaseArticlesBean) v.getTag();
				
				DynamicCommentClickDialog commentClickDialog = new DynamicCommentClickDialog();
				commentClickDialog.setData(activity, bean, copyContent,
						isOnlyCopy ? false : bean.getUid().equals(App.getInst().getUserInfoBean().getUid()), listener, isOnlyCopy);
				commentClickDialog.show(((BaseFragmentActivity) activity).getSupportFragmentManager(),
						DynamicCommentClickDialog.class.getSimpleName());
			return true;
		}
		
	}
	
	public interface CircleCommentHandleSuccessListener {
		/**
		 * 赞更新数据库回调
		 * @param bean
		 * @param pos
		 */
		void digSuccess(DynamicItemBean bean, int pos);
		/**
		 * 评论更新数据库回调
		 * @param bean
		 * @param pos
		 */
		void commentSuccess(DynamicItemBean bean, int pos);
		/**
		 * 删除圈子更新数据库回调
		 * @param bean
		 * @param pos
		 */
		void deleteSuccess(DynamicItemBean bean, int pos);
	}
	
}
