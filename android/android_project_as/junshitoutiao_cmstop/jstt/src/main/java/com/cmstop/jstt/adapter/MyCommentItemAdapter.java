package com.cmstop.jstt.adapter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.chengning.common.base.BasePageListAdapter;
import com.chengning.common.base.BaseViewHolder;
import com.cmstop.jstt.Const.LikeAction;
import com.cmstop.jstt.R;
import com.cmstop.jstt.SettingManager;
import com.cmstop.jstt.beans.data.CommentItemBean;
import com.cmstop.jstt.beans.data.MChannelItemBean;
import com.cmstop.jstt.fragment.home.MyCommentFragment;
import com.cmstop.jstt.utils.ArticleManagerUtils;
import com.cmstop.jstt.utils.ArticleManagerUtils.CommentItemOnClickListener;
import com.cmstop.jstt.utils.ArticleManagerUtils.LikeState;
import com.cmstop.jstt.utils.Common;
import com.cmstop.jstt.utils.Utils;
import com.cmstop.jstt.views.EmojiTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyCommentItemAdapter extends BasePageListAdapter {

	private boolean mIsMyCmt;

	public MyCommentItemAdapter(Activity activity, List list, String type) {
		super(activity, list);
		this.mIsMyCmt = init(type);
	}

	private boolean init(String type) {
		if (TextUtils.equals(type, MyCommentFragment.COMMENT_TYPE_MY)) {
			return true;
		} else if (TextUtils.equals(type, MyCommentFragment.COMMENT_TYPE_CMT_MY)) {
			return false;
		}
		return true;
	}

	@Override
	public int buildLayoutId() {
		return R.layout.item_my_comment;
	}

	@Override
	public void handleLayout(View convertView, final int position, Object obj) {
		ImageView image = BaseViewHolder.get(convertView, R.id.article_comment_item_image);
		TextView name = BaseViewHolder.get(convertView, R.id.article_comment_item_name);
		TextView city = BaseViewHolder.get(convertView, R.id.article_comment_item_city);
		TextView time = BaseViewHolder.get(convertView, R.id.article_comment_item_time);
		final TextView like = BaseViewHolder.get(convertView, R.id.article_comment_item_zan);
		TextView role = BaseViewHolder.get(convertView, R.id.article_comment_item_role);
		ImageView iVip = BaseViewHolder.get(convertView, R.id.article_comment_item_vip);
		
		//父级评论
		
		View pView = BaseViewHolder.get(convertView, R.id.my_comment_item_content_list_layout);
//		TextView pTv = BaseViewHolder.get(convertView, R.id.my_comment_item_content_list_tv);
		TextView pName = BaseViewHolder.get(convertView, R.id.my_comment_item_content_list_name);
		TextView pCity = BaseViewHolder.get(convertView, R.id.my_comment_item_content_list_city);
		TextView pTime = BaseViewHolder.get(convertView, R.id.my_comment_item_content_list_time);
		final TextView pLike = BaseViewHolder.get(convertView, R.id.my_comment_item_content_list_zan);
		TextView pRole = BaseViewHolder.get(convertView, R.id.my_comment_item_content_list_role);
		EmojiTextView pContent = BaseViewHolder.get(convertView, R.id.my_comment_item_content_list_content);
		
		EmojiTextView content = BaseViewHolder.get(convertView, R.id.my_comment_item_content);
		
		TextView contentParent = BaseViewHolder.get(convertView, R.id.my_comment_item_content_parent);
		
		final CommentItemBean bean = (CommentItemBean) obj;
		
		if(!TextUtils.isEmpty(bean.getFace())){
			image.setVisibility(View.VISIBLE);
			Utils.setCircleImage(bean.getFace(), image);
			
			if (Common.isTrue(SettingManager.getInst().getNightModel())) {
				image.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
			}
		}else{
			image.setVisibility(View.INVISIBLE);
		}
		String username = bean.getUsername();
		name.setText((TextUtils.isEmpty(username) ? "新军事网友" : username));
		name.setTag(bean);
		city.setText(bean.getIp());
		try {
			time.setText(Common.formatTimeHoursMinutesBefore(Common.formatYYMMDDHHMMSStoLong(bean.getDtime())));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		String good = bean.getGood();
		like.setText(TextUtils.isEmpty(good) ? "0" : good);
		like.setTag(bean);
		
		LikeState mLikeState = new ArticleManagerUtils.LikeState(){

			@Override
			public void success(String data) {
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(data);
					like.setText(String.valueOf(jsonObject.optJSONObject("list").optString("data")));
					like.setEnabled(true);
				} catch (JSONException e) {
					like.setEnabled(false);
				}
				
			}
			
			@Override
			public void init() {
				like.setEnabled(false);
			}
			
			@Override
			public void failure() {
				like.setEnabled(true);
			}
			
		};
		final ArticleManagerUtils mArticleManagerUtils = new ArticleManagerUtils();
		mArticleManagerUtils.setState(mLikeState);
		like.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CommentItemBean bean = (CommentItemBean) v.getTag();
				mArticleManagerUtils.doCommentLikeByHttp(getContext(), bean.getId(), LikeAction.GOOD);
			}
		});
		
		Common.handleRole(role, bean.getHonor());
		
		if(!TextUtils.isEmpty(bean.getRewards_img())){
			iVip.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(bean.getRewards_img(), iVip);
		}else{
			iVip.setVisibility(View.GONE);
		}
		
		content.setText(bean.getMsg());
		contentParent.setText("原文：" + bean.getArctitle());
		
		convertView.setOnClickListener(new CommentItemOnClickListener(getContext(), bean, mLikeState, 
				!mIsMyCmt ? null : new Runnable() {
					
					@Override
					public void run() {
						updateList(position);
					}
				}));
		
		contentParent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				handleContentClick(bean);
			}
		});
		
		// (ArticleCommentItemAdapter listView)
		if (Common.isTrue(SettingManager.getInst().getNightModel())) {
			pView.getBackground().setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
		}
//				final ArrayList<CommentItemBean> list = bean.getParents_list();
		final ArrayList<CommentItemBean> cList = new ArrayList<CommentItemBean>();
		if(!Common.isListEmpty(bean.getList())){
			cList.addAll(bean.getList());
		}
		
		CommentItemBean pBean = bean.getUpper_info();
		
		if(pBean == null){
			pView.setVisibility(View.GONE);
		}else{
			pView.setVisibility(View.VISIBLE);
			
			//TODO 设置父级
			String pusername = pBean.getUsername();
			pName.setText((TextUtils.isEmpty(pusername) ? "新军事网友" : pusername));
			pName.setTag(pBean);
			pCity.setText(pBean.getIp());
			pTime.setText(Common.getDateMMDDHHMMNotNull(pBean.getDtime()));
			
			String pGood = pBean.getGood();
			pLike.setText(TextUtils.isEmpty(pGood) ? "0" : pGood);
			pLike.setTag(pBean);
			
			LikeState mpLikeState = new ArticleManagerUtils.LikeState(){

				@Override
				public void success(String data) {
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(data);
						pLike.setText(String.valueOf(jsonObject.optJSONObject("list").optString("data")));
						pLike.setEnabled(true);
					} catch (JSONException e) {
						pLike.setEnabled(false);
					}
				}
				
				@Override
				public void init() {
					pLike.setEnabled(false);
				}
				
				@Override
				public void failure() {
					pLike.setEnabled(true);
				}
				
			};
			final ArticleManagerUtils mpArticleManagerUtils = new ArticleManagerUtils();
			mpArticleManagerUtils.setState(mpLikeState);
			pLike.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					CommentItemBean bean = (CommentItemBean) v.getTag();
					mpArticleManagerUtils.doCommentLikeByHttp(getContext(), bean.getId(), LikeAction.GOOD);
				}
			});
			
			Common.handleRole(pRole, pBean.getHonor());
			
			pContent.setText(pBean.getMsg());
			pView.setOnClickListener(new CommentItemOnClickListener(getContext(), pBean, mpLikeState, mIsMyCmt ? null : new Runnable() {
				
				@Override
				public void run() {
					updateList(position);
				}
			}));
		}
	}
	
	/**
	 * 删除后更新列表
	 * @param position
	 */
	protected void updateList(int position) {
		List list = getList();
		list.remove(position);
		setList(list);
		notifyDataSetChanged();
	}

	/**
	 * 评论的文章题目点击
	 * @param cBean
	 */
	protected void handleContentClick(CommentItemBean cBean) {
		MChannelItemBean bean = new MChannelItemBean();
		bean.setAid(cBean.getAid());
//		bean.setAuthor(cBean.getAuthor());
//		bean.setPubDate(cBean.getPubDate());
//		bean.setTitle(cBean.getArctitle());
//		bean.setImage(cBean.getImage());
		bean.setChannel(cBean.getChannel());
		Utils.handleBeanClick(getContext(), bean);
		
	}

}
