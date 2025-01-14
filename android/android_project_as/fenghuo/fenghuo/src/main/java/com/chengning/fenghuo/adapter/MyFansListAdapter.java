package com.chengning.fenghuo.adapter;

import java.util.List;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.chengning.common.base.BasePageListAdapter;
import com.chengning.common.base.BaseViewHolder;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.SettingManager;
import com.chengning.fenghuo.data.bean.UserInfoBean;
import com.chengning.fenghuo.util.Common;
import com.chengning.fenghuo.util.Utils;

public class MyFansListAdapter extends BasePageListAdapter {

	private OnClickListener mFollowClick;
	
	public MyFansListAdapter(Activity activity, List list, OnClickListener listener) {
		super(activity, list);
		mFollowClick = listener;
	}

	@Override
	public int buildLayoutId() {
		return R.layout.item_my_fans;
	}

	@Override
	public void handleLayout(View convertView, int position, Object obj) {
		TextView title;
		TextView desc;
        ImageView image;
        TextView count;
        TextView follow; 
		title = BaseViewHolder.get(convertView, R.id.follow_fans_user_name);
		desc = BaseViewHolder.get(convertView, R.id.follow_fans_desc);
		count = BaseViewHolder.get(convertView, R.id.follow_fans_fans_count);
		image = BaseViewHolder.get(convertView, R.id.follow_fans_user_image);
		follow = BaseViewHolder.get(convertView, R.id.follow_fans_list_btn);
		
		UserInfoBean bean = (UserInfoBean)obj;
		if(bean != null){
			if(!TextUtils.isEmpty(bean.getFace_original())) {
				Utils.setCircleImage(bean.getFace_original(), image);
			} else {
				Utils.showFace(bean.getFace(), image);
			}
			if (Common.isTrue(SettingManager.getInst().getNightModel())) {
				image.setColorFilter(getContext().getResources().getColor(R.color.night_img_color), PorterDuff.Mode.MULTIPLY);
			}
			
			Common.handleUserNameDisplay(getContext(), bean, title, false);
			desc.setText(bean.getAboutme());
			count.setText("粉丝: " + Common.getReadableNumber(bean.getFans_count()));
			title.setTag( bean);

			if(bean.getIs_follow() == 0) {
				follow.setSelected(true);
			} else {
				follow.setSelected(false);
			}
			follow.setText(!follow.isSelected() ? getContext().getString(R.string.str_userinfo_already_focused)
					: getContext().getString(R.string.str_columnist_not_focused));
			follow.setOnClickListener(mFollowClick);
			follow.setTag(bean);
		}
	}
	
}
