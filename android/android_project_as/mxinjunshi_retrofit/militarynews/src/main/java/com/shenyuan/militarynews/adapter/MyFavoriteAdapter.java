package com.shenyuan.militarynews.adapter;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.chengning.common.base.BaseTypeListAdapter;
import com.chengning.common.base.BaseViewHolder;
import com.chengning.common.base.util.GlideHelper;
import com.chengning.common.util.DisplayUtil;
import com.shenyuan.militarynews.Const;
import com.shenyuan.militarynews.R;
import com.shenyuan.militarynews.SettingManager;
import com.shenyuan.militarynews.activity.MyFavoriteActivity.DeleteListener;
import com.shenyuan.militarynews.beans.data.MChannelItemBean;
import com.shenyuan.militarynews.utils.Common;

import java.util.List;

public class MyFavoriteAdapter extends BaseTypeListAdapter {
	
//	private static final int TYPE_ONE_PIC = 0;
//	private static final int TYPE_THREE_PIC = 1;
	private GlideHelper.GlideImageType mImageType;
	private GlideHelper.GlideImageType mImageTypeWithImage;

	private boolean isDeleteFlag = false;
	private DeleteListener listener;
	private int mImageThreeSmallWidth;
	private int mImageThreeSmallHeight;
	private int mImageOneSmallWidth;
	private int mImageOneSmallHeight;
	
	public MyFavoriteAdapter(Activity activity, List list, DeleteListener deleteListener, boolean isDeleteFlag) {
		super(activity, list);
		this.isDeleteFlag = isDeleteFlag;
		this.listener = deleteListener;
		initImageType();
		DisplayUtil.getInst().init(activity);
		int gapWidth = activity.getResources().getDimensionPixelSize(R.dimen.common_horizontal_margin);
		int gapThreeSmall = activity.getResources().getDimensionPixelSize(R.dimen.common_horizontal_margin_three_small); 
		float imageLayoutWidth = (float)(DisplayUtil.getInst().getScreenWidth() - gapWidth * 2);
		
		mImageThreeSmallWidth = ((int)imageLayoutWidth - gapThreeSmall * 2) / 3;
		mImageThreeSmallHeight = (int)(mImageThreeSmallWidth / 1.5);
		
		mImageOneSmallWidth = mImageThreeSmallWidth;
		mImageOneSmallHeight = mImageThreeSmallHeight;
	}

	private void initImageType() {
		mImageType = GlideHelper.GlideImageType.defaulted;
		mImageTypeWithImage = GlideHelper.GlideImageType.covered;
		mImageTypeWithImage.setRadius(0);
		mImageTypeWithImage.setResId(R.drawable.video_list_icon);
	}

	public enum ChannelItemType {
		/**
		 * OneSmall，1个小图，默认type，type必须从0开始
		 */
		OneSmall(0, Const.NEWS_TYPE_COMMON, Const.NEWS_TYPE_COMMON_NO_PIC),	// 1个小图
		ThreeSmall(1, Const.NEWS_TYPE_THREE_SMALL_PIC, Const.NEWS_TYPE_ONE_BIG_TWO_SMALL_PIC),// 3个小图
		;

		private int type;
		private int showType[];

		ChannelItemType(int type, int... showType){
			this.type = type;
			this.showType = showType;
		}

		public int getType(){
			return type;
		}

		public int[] getShowType(){
			return showType;
		}

		public static ChannelItemType parseType(int type){
			ChannelItemType it = ChannelItemType.OneSmall;
			for(ChannelItemType c : values()){
				if(c.type == type){
					it = c;
					break;
				}
			}
			return it;
		}

		public static ChannelItemType parseShowType(int showType){
			ChannelItemType it = ChannelItemType.OneSmall;
			for(ChannelItemType c : values()){
				for(int i : c.showType){
					if(i == showType){
						it = c;
						break;
					}
				}
			}
			return it;
		}
	}

	@Override
	public int getItemViewType(int position) {
		if(Common.isTrue(SettingManager.getInst().getNoPicModel())){
			// 无图模式固定样式
			return ChannelItemType.OneSmall.getType();
		}else{
			MChannelItemBean bean = (MChannelItemBean) getItem(position);
			if(bean == null){
				return ChannelItemType.OneSmall.getType();
			}else if (Const.CHANNEL_ARTICLE_TUWEN == bean.getChannel()) {
				return ChannelItemType.ThreeSmall.getType();
			}else {
				return ChannelItemType.parseShowType(bean.getNews_show_type()).getType();
			}
		}
	}

	@Override
	public View buildLayoutView(int position, int type) {
		View view = null;
		ChannelItemType ct = ChannelItemType.parseType(type);
		switch (ct) {
			default:
			case OneSmall:
				view = View.inflate(getContext(), R.layout.item_my_favorite_one_pic, null);
				break;
			case ThreeSmall:
				view = View.inflate(getContext(), R.layout.item_my_favorite_three_pic, null);
				break;
		}
		return view;
	}

	@Override
	public int getViewTypeCount() {
		if(Common.isTrue(SettingManager.getInst().getNoPicModel())){
			return 1;
		}else{
			return ChannelItemType.values().length;
		}
	}

	@Override
	public int buildLayoutId(int position, int type) {
		return 0;
	}

	@Override
	public void handleLayout(View convertView, int position, Object obj,
			int type) {
		MChannelItemBean bean= (MChannelItemBean) obj;
		
		View currentLayout;
		ImageView delete;
		ImageView img;
		TextView titleTag;
		TextView title;
		TextView date;
		TextView cmt;
		ImageView img2 = null;
		ImageView img3 = null;

		currentLayout = convertView;
		titleTag = BaseViewHolder.get(convertView, R.id.item_favorite_title);
		final ChannelItemType itemType = ChannelItemType.parseType(type);
		switch (itemType) {
		default:
			case OneSmall:
			date = BaseViewHolder.get(currentLayout, R.id.item_favorite_date);
			cmt = BaseViewHolder.get(currentLayout, R.id.item_favorite_cmt);
			date.setText(Common.dateCompareNow(bean.getPubDate()));
			cmt.setText(bean.getAuthor());
//			date.setVisibility(!isDeleteFlag ? View.VISIBLE : View.GONE);
//			cmt.setVisibility(!isDeleteFlag ? View.VISIBLE : View.GONE);
			break;
			case ThreeSmall:
			img2 = BaseViewHolder.get(currentLayout, R.id.item_my_favorite_image2);
			img3 = BaseViewHolder.get(currentLayout, R.id.item_my_favorite_image3);
			break;
		}
		
		title = BaseViewHolder.get(currentLayout, R.id.item_favorite_title);
		delete = BaseViewHolder.get(currentLayout, R.id.item_favorite_delete);
		img = BaseViewHolder.get(currentLayout, R.id.item_favorite_img);
		
		if (Common.isTrue(SettingManager.getInst().getNoPicModel())) {
			GlideHelper.getInst().clear(getContext(), img);
			img.setVisibility(View.GONE);
			if (itemType == ChannelItemType.ThreeSmall) {
                GlideHelper.getInst().clear(getContext(), img2);
                GlideHelper.getInst().clear(getContext(), img3);
				img2.setVisibility(View.GONE);
				img3.setVisibility(View.GONE);
			}
		} else {
			switch (itemType) {
				case OneSmall:
					updateSize(img, mImageOneSmallWidth, mImageOneSmallHeight);
					if (!TextUtils.isEmpty(bean.getImage())) {
						img.setVisibility(View.VISIBLE);
						GlideHelper.getInst().loadImageWithPlace(getContext(), bean.getImage(), img, getImageTypes(bean));
					} else {
						GlideHelper.getInst().clear(getContext(), img);
						img.setVisibility(View.GONE);
					}
					if (Common.isTrue(SettingManager.getInst().getNightModel())) {
						img.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
					}
					break;
				case ThreeSmall:
					updateSize(img, mImageThreeSmallWidth, mImageThreeSmallHeight);
					updateSize(img2, mImageThreeSmallWidth, mImageThreeSmallHeight);
					updateSize(img3, mImageThreeSmallWidth, mImageThreeSmallHeight);
					img.setVisibility(View.VISIBLE);
					img2.setVisibility(View.VISIBLE);
					img3.setVisibility(View.VISIBLE);
					GlideHelper.getInst().loadImageWithPlace(getContext(), bean.getImage_arr().get(0), img, mImageType);
					GlideHelper.getInst().loadImageWithPlace(getContext(), bean.getImage_arr().get(1), img2, mImageType);
					GlideHelper.getInst().loadImageWithPlace(getContext(), bean.getImage_arr().get(2), img3, mImageType);
					if (Common.isTrue(SettingManager.getInst().getNightModel())) {
						img.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
						img2.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
						img3.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
					}
					break;
				default:
					break;
			}
			
		}
		title.setText(bean.getTitle());
		titleTag.setTag(bean);
		delete.setOnClickListener(listener);
		delete.setTag(position);
		delete.setVisibility(isDeleteFlag ? View.VISIBLE : View.GONE);
		
	}

	public void setDelFlag(boolean b) {
		isDeleteFlag = b;
	}
	
	private void updateSize(View v, int width, int height){
		LayoutParams lp = v.getLayoutParams();
		lp.width = width;
		lp.height = height;
		v.setLayoutParams(lp);
	}
	private GlideHelper.GlideImageType getImageTypes(MChannelItemBean bean){
		if(Const.CHANNEL_ARTICLE_VIDEO.equals(bean.getChannel())){
			return mImageTypeWithImage;
		}else{
			return mImageType;
		}
	}
}
