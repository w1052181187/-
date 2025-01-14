package com.chengning.fenghuo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chengning.common.base.BasePageListAdapter;
import com.chengning.common.base.BaseViewHolder;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.data.bean.DirectorateTaskBean;

public class DirectorateTaskAdapter extends BasePageListAdapter {

	private List<Drawable> iconList = new ArrayList<Drawable>();
	private StringBuffer buffer = new StringBuffer();
	public DirectorateTaskAdapter(Activity activity, List list) {
		super(activity, list);
	}

	@Override
	public int buildLayoutId() {
		return R.layout.item_directorate_task;
	}

	@Override
	public void handleLayout(View convertView, int position, Object obj) {
		TextView name = BaseViewHolder.get(convertView, R.id.item_directorate_task_name);
		TextView progress = BaseViewHolder.get(convertView, R.id.item_directorate_task_complete_progress);
		TextView complete = BaseViewHolder.get(convertView, R.id.item_directorate_task_complete);
		
		DirectorateTaskBean bean = (DirectorateTaskBean) obj;
		
		name.setText(bean.getRulename());
		Drawable drawable = null;
		if (TextUtils.equals(bean.getAction(),"login")) {
			drawable = getContext().getResources().getDrawable(R.drawable.zhihuibu_mrdl_icon);
		} else if (TextUtils.equals(bean.getAction(),"reply")) {
			drawable = getContext().getResources().getDrawable(R.drawable.zhihuibu_pinglun_icon);
		} else if (TextUtils.equals(bean.getAction(),"topic")) {
			drawable = getContext().getResources().getDrawable(R.drawable.zhihuibu_share_icon);
		} else {
			drawable = getContext().getResources().getDrawable(R.drawable.zhihuibu_mrdl_icon);
		}
		if (drawable != null) {
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			name.setCompoundDrawables(drawable, null, null, null);
		}

		buffer.setLength(0);
		buffer.append(bean.getCyclenum()).append("/").append(bean.getRewardnum());
		progress.setText(buffer.toString());
		
		if ("完成".equals(bean.getComplete())) {
			complete.setTextColor(getContext().getResources().getColor(R.color.home_bottom_txt));
		}
		complete.setText(bean.getComplete());
		complete.setTag(bean);
	}

}
