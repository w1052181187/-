package com.cmstop.jstt.fragment.home;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.cmstop.jstt.Const;
import com.cmstop.jstt.IIsRecom;
import com.cmstop.jstt.R;
import com.cmstop.jstt.SettingManager;
import com.cmstop.jstt.activity.HomeSingleActivity;
import com.cmstop.jstt.ad.AdDataDummy.NativeResponseDummy;
import com.cmstop.jstt.adapter.AdChannelItemRecommendAdapter;
import com.cmstop.jstt.beans.data.MChannelBean;
import com.cmstop.jstt.beans.data.MChannelItemBean;
import com.cmstop.jstt.data.access.ChannelItemListServer;
import com.cmstop.jstt.data.access.LocalStateServer;
import com.cmstop.jstt.event.SwitchNavEvent;
import com.cmstop.jstt.utils.Common;
import com.cmstop.jstt.utils.JUrl;
import com.cmstop.jstt.utils.SPHelper;
import com.cmstop.jstt.utils.Utils;
import com.cmstop.jstt.views.AdBigPicView;
import com.cmstop.jstt.views.HomeNewContentPopup;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import de.greenrobot.event.EventBus;

public abstract class AbstractChannelItemListFragment extends BasePageListFragment<MChannelItemBean> {
	
	private int mLastNoPicMode;

	private OnRecommendHttpListener mListener;

	private PullToRefreshListView mPull;
	private LinearLayout mPullHeader;
	private int refreshCount;
	private boolean mIsHomeRecom = false;
	
	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			mPull.setRefreshing();
		}
	};

	public boolean isHomeRecom() {
		return mIsHomeRecom;
	}

	public void setHomeRecom(boolean isHomeRecom) {
		this.mIsHomeRecom = isHomeRecom;
		BaseAdapter adapter = getAdapter();
		if(adapter != null && adapter instanceof AdChannelItemRecommendAdapter){
			((AdChannelItemRecommendAdapter)adapter).setHomeRecom(this.mIsHomeRecom);
		}
	}

	@Override
	public BaseAdapter buildAdapter(Activity activity,
			List<MChannelItemBean> list) {
		AdChannelItemRecommendAdapter adapter = new AdChannelItemRecommendAdapter(activity, list, new MoreOnClickListenner());
		adapter.setIsRecom(Common.isTrue(SPHelper.getInst().getInt(SPHelper.PREFIX_KEY_CHANNEL_IS_RECOM + buildChannel())));
		adapter.setHomeRecom(isHomeRecom());
		adapter.setRefreshListener(listener);
		adapter.setChannel(buildChannel());
		if (TextUtils.equals("exclusive", buildChannel())) {
			adapter.setDividerExclusive(true);
		}
		return adapter;
	}

	@Override
	public void configPullToRefreshListView_FootLoad(PullToRefreshListView v) {
		mPull = v;
		mPullHeader = new LinearLayout(getContext());
		mPull.getRefreshableView().addHeaderView(mPullHeader);
	}

	@Override
	public List<MChannelItemBean> onHttpSuccess(Gson gson, String data,
		MChannelBean channelBean, int tarPage) {
		ArrayList<MChannelItemBean> list = channelBean.getItem();
		if(tarPage == JUrl.PAGE_START){
			List<MChannelItemBean> oldList = ChannelItemListServer.getInst(getContext()).queryChannelItemList(buildChannel(), JUrl.PAGE_START);
			int isRecom = channelBean.getIs_recom();
			if(!Common.isListEmpty(oldList)){
				int newCount = HomeNewContentPopup.calcuateNewCount(oldList, list);
				newCount += HomeNewContentPopup.calcuateNewCount(ChannelItemListServer.getInst(getContext())
						.queryChannelItemList(buildChannelSlide(), JUrl.PAGE_START), channelBean.getSlide());
				HomeNewContentPopup.handleHttpListData(getContext(), getRootView(), getHandler(), newCount);
				
			}
			String oldMaxid = SPHelper.getInst().getString(SPHelper.PREFIX_KEY_CHANNEL_MAXID + buildChannel());
//			String newMaxid = !Common.isListEmpty(channelBean.getItem()) ? channelBean.getItem().get(0).getAid() : null;
			String newMaxid = AdChannelItemRecommendAdapter.getNewMaxIdNoAd(list);
			String maxid = Common.isTrue(isRecom) ? oldMaxid : newMaxid;
			SPHelper.getInst().saveInt(SPHelper.PREFIX_KEY_CHANNEL_MAXPAGE + buildChannel(), channelBean.getMaxpage());
        	SPHelper.getInst().saveInt(SPHelper.PREFIX_KEY_CHANNEL_IS_RECOM + buildChannel(), isRecom);
        	SPHelper.getInst().saveString(SPHelper.PREFIX_KEY_CHANNEL_MAXID + buildChannel(), maxid);
        	SPHelper.getInst().saveLong(SPHelper.PREFIX_TIME_UPDATE_CHANNEL + buildChannel(), System.currentTimeMillis());

        	if ("recommend".equals(buildChannel())) {
				//保存文章最大id（用于桌面红点机制）
				SPHelper.getInst().saveString(SPHelper.BADGE_KEY_BADGE_ID, maxid);
        	}
        	
    		if (0 == isRecom) {
    			refreshCount = 0;
    		} else{
    			refreshCount++;
    		}
        	
        	if(getAdapter() instanceof IIsRecom){
        		((IIsRecom)getAdapter()).setIsRecom(Common.isTrue(isRecom));
        	}
        	
			// header slide
			ChannelItemListServer.getInst(getContext()).setChannelAndPage(channelBean.getSlide(), buildChannelSlide(), JUrl.PAGE_START);
			ChannelItemListServer.getInst(getContext()).clearChannel(buildChannelSlide());
			ChannelItemListServer.getInst(getContext()).insertChannelItemList(channelBean.getSlide(), buildChannelSlide(), JUrl.PAGE_START);
			
			if(mListener != null){
				mListener.onHttpSucess(channelBean);
			}
			
			//置顶的数据
			ArrayList<MChannelItemBean> top = channelBean.getTop();
			if (!Common.isListEmpty(top)) {
				list.addAll(0, top);
			}
		}
		
		return list;
	}
	
	@Override
	public void onInitNewsDataUI(){
		// header slide
		List<MChannelItemBean> listSlide = ChannelItemListServer.getInst(getContext()).queryChannelItemList(buildChannelSlide(), JUrl.PAGE_START);
		setHeader(listSlide);
	}
	
	@Override
	public void onHttpFailure(){
		if(mListener != null){
			mListener.onHttpFailure();
		}
	}
	
	@Override
	public void onGetNewsByDB(int page, List list){
	}

	@Override
	public void onListItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		View tagView = view.findViewById(R.id.item_channel_item_content_layout);
		Object tag = tagView.getTag();
		if(tag instanceof MChannelItemBean){
			Common.handleTextViewReaded(getContext(), view, R.id.item_channel_item_title_one_small, true);
			Common.handleTextViewReaded(getContext(), view, R.id.item_channel_item_title_one_big_two_small, true);
			Common.handleTextViewReaded(getContext(), view, R.id.item_channel_item_title_one_big, true);
			Common.handleTextViewReaded(getContext(), view, R.id.item_channel_item_title_three_small, true);
			Common.handleTextViewReaded(getContext(), view, R.id.item_channel_item_title_two_big_1, true);
			Common.handleTextViewReaded(getContext(), view, R.id.item_channel_item_title_two_big_2, true);
			
			MChannelItemBean bean = (MChannelItemBean) tag;
			handleBeanClick(bean, position);
		}else if(tag instanceof NativeResponseDummy){
			NativeResponseDummy adBean = (NativeResponseDummy) tag;
			adBean.handleClick(view);
		}
	}
	
	public void handleBeanClick(MChannelItemBean bean, int position){
		if(bean == null){
			return;
		}
		LocalStateServer.getInst(getContext()).setReadStateRead(LocalStateServer.PREFIX_CHANNEL_ITEM, bean.getAid());

		String area = mIsHomeRecom && position >= 0 && position < 30 ? "new30" : "normal";
		if (!TextUtils.isEmpty(bean.getTz_nav())) {
//			((HomeSingleActivity) getContext()).onNav(bean.getTz_nav());
			SwitchNavEvent event = new SwitchNavEvent();
			event.setNavName(bean.getTz_nav());
			EventBus.getDefault().post(event);
			return;
		}
		Utils.handleBeanClick(getContext(), bean, area);
	}
	
	public String buildChannelSlide(){
		return buildChannel() + "_slide";
	}
	
	private class ImageOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Common.handleTextViewReaded(getContext(), v, R.id.item_channel_item_title_one_small, true);
			
			MChannelItemBean bean = (MChannelItemBean) v.getTag();
			handleBeanClick(bean, -1);
		}
	}
	
	private void setHeader(List<MChannelItemBean> list){
		if(!Common.isListEmpty(list)){
			mLastNoPicMode = SettingManager.getInst().getNoPicModel();
			if(!Common.isTrue(SettingManager.getInst().getNoPicModel())){
				mPullHeader.removeAllViews();
				View mSlideView = new AdBigPicView(getContext(), list, new ImageOnClickListener()).InitbigPic();
				mPullHeader.addView(mSlideView);
			}else{
				List headList = getDataList();
				if (!Common.isListEmpty(headList)) {
					for(int i = 0; i < list.size(); i++){
						if(!TextUtils.isEmpty(list.get(i).getAd_place_id())){
							// ad
							list.get(i).setNews_show_type(Const.NEWS_TYPE_COMMON_AD);
						}
					}
					headList.addAll(0, list);
					getAdapter().notifyDataSetChanged();
				}
			}
		}else{
			mPullHeader.removeAllViews();
		}
	}
	
	public void onStart(){
		super.onStart();
		if (SettingManager.getInst().getNoPicModel() != mLastNoPicMode){
			mLastNoPicMode = SettingManager.getInst().getNoPicModel();
			List<MChannelItemBean> listSlide = ChannelItemListServer.getInst(getContext()).queryChannelItemList(buildChannelSlide(), JUrl.PAGE_START);
			setHeader(listSlide);
			getAdapter().notifyDataSetChanged();
		}
	}
	
	public String appendMaxid(String url, int tarPage){
		String cidUrl = url;
		String maxid = SPHelper.getInst().getString(SPHelper.PREFIX_KEY_CHANNEL_MAXID + buildChannel());
		// TODO(appendMaxid maxid 为空时需要处理)
//		if(TextUtils.isEmpty(maxid)){
//			List<MChannelItemBean> list = getDataList();
//			if(!Common.isListEmpty(list)){
//				maxid = list.get(0).getAid();
//			}
//		}
		if(!TextUtils.isEmpty(maxid)){
			// 首页时使用newid，其他页使用maxid
			String mUrl = JUrl.appendNewid(url, maxid);
			if (refreshCount >= 3) {
				refreshCount = 0;
				mUrl = url;
			}
			cidUrl = tarPage == JUrl.PAGE_START ? mUrl : JUrl.appendMaxid(url, maxid);
		}
		return cidUrl;
	}
	
	class MoreOnClickListenner implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			boolean isNav = false;
			String channel = (String) v.getTag();
			if(TextUtils.isEmpty(channel )){
				isNav = false;
			}else {
				isNav = true;
			}
			
			if (isNav) {
				SwitchNavEvent event = new SwitchNavEvent();
				event.setNavName(channel);
				EventBus.getDefault().post(event);
			}
		}
		
	}
	
	public void setOnRecommendHttpListener(OnRecommendHttpListener listener){
		this.mListener = listener;
	}

	public static interface OnRecommendHttpListener{
		public void onHttpSucess(MChannelBean bean);
		public void onHttpFailure();
//		public void onNav(String nav);
	}

}
