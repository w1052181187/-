package com.chengning.yiqikantoutiao.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.widget.BaseAdapter;

import com.chengning.yiqikantoutiao.SettingManager;
import com.chengning.yiqikantoutiao.base.AbstractChannelItemListFragment;
import com.chengning.yiqikantoutiao.data.bean.BaseChannelItemBean;
import com.chengning.yiqikantoutiao.util.JUrl;
import com.chengning.yiqikantoutiao.util.SPHelper;

import java.util.List;

/**
 * @description 视频
 * @author wyg
 *
 */
public class VideoChanelFragment extends AbstractChannelItemListFragment {
	
	public static final String KEY_BEAN = "bean";
	private BaseAdapter mAdapter;
	
	@Override
	public String buildTAG() {
		return VideoChanelFragment.class.getSimpleName();
	}

	@Override
	public String buildChannel() {
		return "tab_video";
	}

	@Override
	public String buildUrl(int tarPage) {
		String url = JUrl.appendPage(JUrl.SITE + JUrl.URL_GET_VIDEO_CHANNEL, tarPage);
		url = appendMaxid(url, tarPage);
		return url.toString();
	}

	@Override
	public String buildChannelSlide() {
		return buildChannel() + "_slide";
	}
	@Override
	public BaseAdapter buildAdapter(Activity activity,
                                    List<BaseChannelItemBean> list) {
		mAdapter = super.buildAdapter(activity, list);
		return mAdapter;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (SettingManager.getInst().getNoPicModel() != SPHelper.getInst().getInt(SPHelper.KEY_HOME_VIDEO_NO_PIC_MODEL)) {
			if (null != mAdapter) {
				mAdapter.notifyDataSetChanged();
			}
			SPHelper.getInst().saveInt(SPHelper.KEY_HOME_VIDEO_NO_PIC_MODEL,
					SettingManager.getInst().getNoPicModel());
		}
	}
}