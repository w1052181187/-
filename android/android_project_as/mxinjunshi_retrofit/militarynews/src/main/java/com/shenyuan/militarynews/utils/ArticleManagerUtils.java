package com.shenyuan.militarynews.utils;

import android.app.Activity;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chengning.common.app.ActivityInfo.ActivityState;
import com.chengning.common.base.BaseFragmentActivity;
import com.chengning.common.base.BaseResponseBean;
import com.chengning.common.base.IBaseActivity;
import com.chengning.common.base.MyRetrofitResponseCallback;
import com.chengning.common.base.util.RetrofitManager;
import com.chengning.common.util.SerializeUtil;
import com.chengning.common.util.ThreadHelper;
import com.google.gson.Gson;
import com.shenyuan.militarynews.App;
import com.shenyuan.militarynews.Const;
import com.shenyuan.militarynews.Const.LikeAction;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.adapter.ArticleCommentItemParentAdapter;
import com.shenyuan.militarynews.beans.data.CommentItemBean;
import com.shenyuan.militarynews.beans.data.CommentListBean;
import com.shenyuan.militarynews.beans.data.MChannelItemBean;
import com.shenyuan.militarynews.data.access.LocalStateServer;
import com.shenyuan.militarynews.views.CommentReplyDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;

public class ArticleManagerUtils {
	
	private CollectState mCollectState;
	private LikeState mLikeState;
	private HandlerThread mFavHandle;
	
	public void setState(CollectState collectState){
		this.mCollectState = collectState;
	}
	
	public void setState(CollectState collectState, LikeState likeState){
		this.mCollectState = collectState;
		this.mLikeState = likeState;
	}
	
	public void setState(LikeState likeState){
		this.mLikeState = likeState;
	}

	/**
	 * 
	 * @param state 收藏状态
	 *
	 */
     
	public void setCollectState(final boolean state) {
		// 改变收藏图标
		if (mCollectState == null) {
			return;
		}
		mCollectState.setState(state);
		
	}
	
	 /**
     * 取消收藏
     */
    public void cancleCollect(final Activity context, final MChannelItemBean bean, final String channelArticle){
    	if(bean == null){
    		return;
    	}
    	
    	if (App.getInst().isLogin()) {
    		if (!Common.hasNet()) {
	         	Common.showHttpFailureToast(context);
	         	return;
			} 
			cancleCollectByHttp(context, bean);
    		
    	} else {
    		cancleCollectByDB(context, bean, channelArticle);
    	}
    	
    }
    
    /**
     * 未登录取消收藏
     * @param context 
     * @param bean 
     */
    public void cancleCollectByDB(final Activity context, final MChannelItemBean bean, final String channelArticle) {
    	if (mFavHandle == null) {
			mFavHandle = ThreadHelper.creatThread("my_fav_handle");
		}
		
		ThreadHelper.handle(mFavHandle, new Runnable() {
			
			@Override
			public void run() {
				String prefix = "";
				if (TextUtils.equals(channelArticle, Const.CHANNEL_ARTICLE_TUWEN)) {
					prefix = LocalStateServer.PREFIX_CHANNEL_ITEM_PIC;
				} else if (TextUtils.equals(channelArticle, Const.CHANNEL_ARTICLE_NORMAL)) {
					prefix = LocalStateServer.PREFIX_CHANNEL_ITEM;
				}
				LocalStateServer.getInst(context).deleteFavArticle(prefix, bean.getAid());
			}
		});
		setCollectState(false);
	}

	/**
     * 登录取消收藏
	 * @param bean 
	 * @param context 
     */
    public void cancleCollectByHttp(final Activity context, final MChannelItemBean bean) {
    	HashMap params = new HashMap<String, String>();
		params.put("aid", bean.getAid());
		Observable<BaseResponseBean> observable
				= App.getInst().getApiInterface().get(JUrl.URL_CANCEL_FAVORITE_ATRICLES, params);
		RetrofitManager.subcribe(observable, new MyRetrofitResponseCallback() {

			@Override
			public void onDataSuccess(int status, String mod, String message, String data, BaseResponseBean response) {
				//TODO 取消成功
				setCollectState(false);
			}

			@Override
			public void onDataFailure(int status, String mod, String message, String data, BaseResponseBean response) {
				//TODO 取消失败
				UIHelper.showToast(context, message);
			}

			@Override
			public void onError(Throwable t) {
				Common.showHttpFailureToast(context);
				super.onError(t);
			}
		});

	}

	/**
	 * 收藏
	 * @param context
	 * @param bean
	 * @param channelArticle
	 */
    public void doCollect(final Activity context, final MChannelItemBean bean, String channelArticle){
    	if(null == bean){
    		return;
    	}
    	handleEventAnalytics(context, "artilce_favorite", bean.getAid());
    	if (App.getInst().isLogin()) {
    		if (!Common.hasNet()) {
	         	Common.showHttpFailureToast(context);
	         	return;
			} 
			doCollectByHttp(context, bean);
    		
    	} else {
    		doCollectByDB(context, bean, channelArticle);
    		UIHelper.showToast(context, context.getResources().getString(R.string.article_collect_hint));
    	}
    	
    }
    
    /**
	 * 处理自定义事件统计
	 * @param mContext
	 * @param eventId
	 * @param aid
	 */
	protected void handleEventAnalytics(Activity mContext, String eventId, String aid) {
		Map<String, String> mHashMap = new HashMap<String, String>();
		mHashMap.put("article_id", aid);
		MobclickAgent.onEvent(mContext, eventId, mHashMap);
	}

	/**
	 * 未登录收藏
	 * @param context
	 * @param bean
	 * @param channelArticle
	 */
	public void doCollectByDB(final Activity context, final MChannelItemBean bean, final String channelArticle) {
		if (mFavHandle == null) {
			mFavHandle = ThreadHelper.creatThread("my_fav_handle");
		}
		
		ThreadHelper.handle(mFavHandle, new Runnable() {
			
			@Override
			public void run() {
				String prefix = "";
				if (TextUtils.equals(channelArticle, Const.CHANNEL_ARTICLE_TUWEN)) {
					prefix = LocalStateServer.PREFIX_CHANNEL_ITEM_PIC;
				} else if (TextUtils.equals(channelArticle, Const.CHANNEL_ARTICLE_NORMAL)) {
					prefix = LocalStateServer.PREFIX_CHANNEL_ITEM;
				}
				LocalStateServer.getInst(context).saveFavArticle(prefix, bean.getAid(), SerializeUtil.serialize(bean));
			}
		});
		setCollectState(true);
	}

	/**
	 * 登录之后收藏
	 * @param context
	 * @param bean
	 */
	public void doCollectByHttp(final Activity context, final MChannelItemBean bean) {
		HashMap params = new HashMap<String, String>();
		params.put("aid", bean.getAid());
		Observable<BaseResponseBean> observable
				= App.getInst().getApiInterface().get(JUrl.URL_COLLECT_FAVORITE_ATRICLES, params);
		RetrofitManager.subcribe(observable, new MyRetrofitResponseCallback() {

			@Override
			public void onDataSuccess(int status, String mod, String message, String data, BaseResponseBean response) {
				setCollectState(true);
				UIHelper.showToast(context, context.getResources().getString(R.string.article_collect_hint));
			}

			@Override
			public void onDataFailure(int status, String mod, String message, String data, BaseResponseBean response) {
				//TODO 收藏失败
				UIHelper.showToast(context, message);
			}

			@Override
			public void onError(Throwable t) {
				Common.showHttpFailureToast(context);
				super.onError(t);
			}
		});

	}
	
	public void destroyFavHandleThread(){
		ThreadHelper.destory(mFavHandle);
	}
	
	
	/**
     * 赞
     */
    public void doLikeByHttp(final Activity activity, final String aid, LikeAction action) {

		HashMap params = new HashMap<String, String>();
    	switch (action) {
			case GOOD :
				handleEventAnalytics(activity, "artilce_digg", "ding", aid);
				params.put("action", "good");
				break;
			case BAD :
				handleEventAnalytics(activity, "artilce_digg", "cai", aid);
				params.put("action", "bad");
				break;

			default :
				break;
		}
		params.put("id", aid);
    	
		if(LocalStateServer.getInst(activity).isLike(LocalStateServer.PREFIX_CHANNEL_ITEM, aid) ||
				LocalStateServer.getInst(activity).isTread(LocalStateServer.PREFIX_CHANNEL_ITEM, aid)){
   		 	UIHelper.showToast(activity, "您已经顶或踩过了");
		} else {
			if (mLikeState == null) {
				return;
			}
	    	mLikeState.init();

			Observable<BaseResponseBean> observable
					= App.getInst().getApiInterface().get(JUrl.GET_GOOD_BAD, params);
			RetrofitManager.subcribe(observable, new MyRetrofitResponseCallback() {

				@Override
				public void onDataSuccess(int status, String mod, String message, String data, BaseResponseBean response) {
					mLikeState.success(data
					);
					LocalStateServer.getInst(activity).setLikeStateTrue(LocalStateServer.PREFIX_CHANNEL_ITEM, aid);
				}

				@Override
				public void onDataFailure(int status, String mod, String message, String data, BaseResponseBean response) {
					mLikeState.failure();
					switch (status) {
						case -1:
							UIHelper.showToast(activity, "您已经顶或踩过了");
//						UIHelper.showToast(activity, "您已经顶或踩过了");
							LocalStateServer.getInst(activity).setLikeStateTrue(LocalStateServer.PREFIX_CHANNEL_ITEM, aid);
							break;

						default:
							break;
					}
				}

				@Override
				public void onError(Throwable t) {
					Common.showHttpFailureToast(activity);
					mLikeState.failure();
					super.onError(t);
				}
			});

		}
	}
	
	/**
	 * 处理事件统计
	 * @param eventId
	 * @param eventtype
	 */
	private void handleEventAnalytics(Activity activity, String eventId, String eventtype, String aid) {
		Map<String, String> mHashMap = new HashMap<String, String>();
		mHashMap.put("type", eventtype);
		mHashMap.put("article_id", aid);
		MobclickAgent.onEvent(activity, eventId, mHashMap);
	}
	
	public static interface LikeState{
		void init();
		void success(String data);
		void failure();
	}
	
	public static interface CollectState {
		void setState(boolean state);
	}
	public static interface DeleteListener {
		void deleteSuccess(String data);
	}
	
	
	
	/** 文章评论相关部分方法 、类、接口等  */

	/**
	 * 处理点击更多回复
	 * @param context
	 * @param bean
	 * @param page
	 * @param listener
	 */
	public static void handleMoreReplys(final Activity context, CommentItemBean bean, final int page,
			final MoreReplysListener listener) {
		HashMap params = new HashMap<String, String>();
		params.put("rid", bean.getId()); 
//		params.put("maxid", bean.getList().get(0).getId());
		params.put("page", page);

		Observable<BaseResponseBean> observable
				= App.getInst().getApiInterface().get(JUrl.URL_GET_COMMENT_PARENT_LIST, params);
		RetrofitManager.subcribe(observable, new MyRetrofitResponseCallback() {

			@Override
			public void onDataSuccess(int status, String mod, String message, String data, BaseResponseBean response) {
				CommentListBean bean = new Gson().fromJson(data,CommentListBean.class);
				listener.moreReplysSuccess(bean);
			}

			@Override
			public void onDataFailure(int status, String mod, String message, String data, BaseResponseBean response) {
				UIHelper.showToast(context, message);
			}

			@Override
			public void onError(Throwable t) {
				Common.handleHttpFailure(context, t);
				super.onError(t);
			}
		});

	}
	
	public static void updateViewMoreState(ItemState state, TextView more, int rawCount, ArticleCommentItemParentAdapter adapter){
		if(state.isFirst){
			state.isFirst = false;
			if(rawCount == 0){
				state.moreState = MoreState.None;
			}else if(rawCount <= 3){
				state.moreState = MoreState.Pack;
			}else{
				state.moreState = MoreState.More;
			}
		}
		int listCount = adapter.getCount();
		if(rawCount > 3){
			if(rawCount > listCount){
				state.moreState = MoreState.More;
			}else{
				state.moreState = MoreState.Pack;
			}
		}
	}
	
	public static void updateViewMore(ItemState state, TextView more, LinearLayout listLayout){
		switch (state.moreState) {
		case None:
			listLayout.setVisibility(View.GONE);
			more.setVisibility(View.GONE);
			break;
		default:
			listLayout.setVisibility(state.moreState == MoreState.Expand ? View.GONE : View.VISIBLE);
			more.setText(state.moreState.str);
			more.setCompoundDrawablesWithIntrinsicBounds(0, 0, state.moreState.drawableRightId, 0);
			more.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	/**
	 * 增加回复内容视图
	 * @param start
	 * @param adapter
	 * @param listView
	 * @param list 
	 * @param bean 
	 */
	public static void addReplyView(int start, ArticleCommentItemParentAdapter adapter, LinearLayout listView, ArrayList<CommentItemBean> list, CommentItemBean bean) {
		for(int i = start; i < adapter.getCount(); i++){
			View v = adapter.getView(i, null, null);
			listView.addView(v);
		}
	}
	
	public static interface MoreReplysListener {
		public void moreReplysSuccess(CommentListBean bean);
	}
	
	public static class ItemState{
		private boolean isFirst = true;
		public MoreState moreState = MoreState.None;
		private CommentItemBean bean;
		private ArrayList list;
		
	}
	
	public static enum MoreState{
		None("", 0),
		/** 查看回复 */
		Expand("查看回复", R.drawable.btn_an),
		/** 收起 */
		Pack("收起", R.drawable.btn_pack),
		/** 查看更多回复 */
		More("查看更多回复", R.drawable.btn_an),
		;
		
		private String str;
		private int drawableRightId; 
		
		MoreState(String str, int id){
			this.str = str;
			this.drawableRightId = id;
		}
	}
	
	/** 文章评论相关部分方法   */
	
	
	/**
	 * 评论赞或踩
	 * @param activity
	 * @param id 评论id
	 * @param action 
	 */
	 public void doCommentLikeByHttp(final Activity activity, final String id, LikeAction action) {
		 HashMap params = new HashMap<String, String>();
	    	switch (action) {
				case GOOD :
					params.put("action", "good");
					break;
				case BAD :
					params.put("action", "bad");
					break;

				default :
					break;
			}
		 params.put("id", id);
		 
			if(LocalStateServer.getInst(activity).isLike(LocalStateServer.PREFIX_CHANNEL_ITEM, id) ||
					LocalStateServer.getInst(activity).isTread(LocalStateServer.PREFIX_CHANNEL_ITEM, id)){
	   		 	UIHelper.showToast(activity, "您已经顶或踩过了");
			} else {
				if (mLikeState == null) {
					return;
				}
		    	mLikeState.init();

				Observable<BaseResponseBean> observable
						= App.getInst().getApiInterface().post(JUrl.URL_DO_COMMENT_GOOD_BAD, params);
				RetrofitManager.subcribe(observable, new MyRetrofitResponseCallback() {

					@Override
					public void onDataSuccess(int status, String mod, String message, String data, BaseResponseBean response) {
						mLikeState.success(data);
						LocalStateServer.getInst(activity).setLikeStateTrue(LocalStateServer.PREFIX_CHANNEL_ITEM, id);
					}

					@Override
					public void onDataFailure(int status, String mod, String message, String data, BaseResponseBean response) {
						mLikeState.failure();
						UIHelper.showToast(activity, message);
					}

					@Override
					public void onError(Throwable t) {
						Common.showHttpFailureToast(activity);
						mLikeState.failure();
						super.onError(t);
					}
				});

			}
		}
	 
	 public static class CommentItemOnClickListener implements OnClickListener{
		 //回调（是否需要更新赞图标显示数量）
			private LikeState state;
			private CommentItemBean bean;
			private Activity context;
			private String aid;
//			private boolean mIsCommentChild;
			//是否需要删除
			private Runnable mDelRunable;

			
			public CommentItemOnClickListener(Activity context, CommentItemBean bean, Runnable r) {
				super();
				this.context = context;
				this.bean = bean;
//				this.mIsCommentChild = isCommentChild;
				this.state = null;
				this.mDelRunable = r;
			}
			
			public CommentItemOnClickListener(Activity context, CommentItemBean bean, LikeState state, Runnable r) {
				super();
				this.context = context;
				this.bean = bean;
//				this.mIsCommentChild = isCommentChild;
				this.state = state;
				this.mDelRunable = r;
			}

			@Override
			public void onClick(View view) {
//				View v = view.findViewById(R.id.article_comment_item_name);
//				CommentItemBean bean = (CommentItemBean) v.getTag();
				if (context instanceof IBaseActivity && 
						((IBaseActivity)context).getActivityInfo().getActivityState() == ActivityState.SaveInstanceStated) {
					return;
				}
				CommentReplyDialog dialog = new CommentReplyDialog();
				dialog.setData(bean);
				if (state != null) {
					//回调（是否需要更新赞图标显示数量）
					dialog.setListener(state);
				}
				if (mDelRunable != null) {
					dialog.setDeleteListener(mDelRunable);
				}
				dialog.showAllowingStateLoss((BaseFragmentActivity) context, ((BaseFragmentActivity) context)
						.getSupportFragmentManager(), CommentReplyDialog.class.getSimpleName());
			}
			
		}

}
