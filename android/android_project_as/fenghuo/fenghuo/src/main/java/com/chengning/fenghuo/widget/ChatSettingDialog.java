package com.chengning.fenghuo.widget;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.chengning.common.base.BaseDialogFragment;
import com.chengning.common.util.ThreadHelper;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.activity.ChatReportActivity;
import com.chengning.fenghuo.data.access.ChatConversationDA;
import com.chengning.fenghuo.data.access.ChatMessageDA;
import com.chengning.fenghuo.data.bean.ChatConversationBean;

public class ChatSettingDialog extends BaseDialogFragment {
	
	private View mSetting;

	private View mTop;
	private View mConfirm;
	
	private TextView mClearHistory;

	private TextView mReport;

	protected boolean isFavBtnClick;

	private ChatConversationBean mBean;
	private ClearHistoryListener mListener;

	private HandlerThread mChatThread;
	

	@Override
	public void initData() {
		mBean = (ChatConversationBean) getArguments().getSerializable("bean") ;
		mReport.setText("举报");
		mClearHistory.setText("删除聊天记录");
		mClearHistory.setTextColor(getResources().getColor(R.color.common_item_tag_text));
		
	}
	
	@Override
	public void initView() {
		mTop = mSetting.findViewById(R.id.dynamic_setting_top);
		mClearHistory = (TextView)mSetting.findViewById(R.id.dynamic_setting_collect);
		mReport = (TextView)mSetting.findViewById(R.id.dynamic_setting_report);
		mConfirm = mSetting.findViewById(R.id.dynamic_setting_confirm);
	}
	
	@Override
	public void initListener() {
		mTop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismissAllowingStateLoss();
			}
		});
		
		mConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismissAllowingStateLoss();
			}
		});
		
		mClearHistory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				deleteHistory();
				mListener.success();
				dismissAllowingStateLoss();
				
			}
		});
		
		mReport.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				handleReport();
				dismissAllowingStateLoss();
			}
		});
	}
	
	/**
	 * 删除聊天记录
	 */
	protected void deleteHistory() {
		if (null == mBean) {
			return;
		}
		if (mChatThread == null) {
			mChatThread = ThreadHelper.creatThread("my_chat");
		}
		ThreadHelper.handle(mChatThread, new Runnable() {
			
			@Override
			public void run() {
				ChatMessageDA.getInst(getContext()).clearAllMsg(mBean.getUid(), mBean.getCon_id());
				
				ChatConversationDA.getInst(getContext()).updateLatestMessage(mBean, null);
			}
		});
		
	}

	/**
	 * 处理举报
	 */
	protected void handleReport() {
		if (mBean == null || TextUtils.isEmpty(mBean.getUid())) {
			return;
		}
		ArrayList<String> list = mBean.getCon_members();
		String uid = "";
		for (String id : list) {
			if (!mBean.getUid().equals(id)) {
				uid = id;
				break;
			}
		}
		ChatReportActivity.launch(getContext(),uid);
	}
	
	public void setData(ChatConversationBean bean){
		Bundle args = new Bundle();
		args.putSerializable("bean", bean);
		setArguments(args);
	}
	
	public void setListener(ClearHistoryListener listener){
		this.mListener = listener;
	}
	
	public void shouldRefreshUI(){
		if(mSetting != null){
			mSetting.postInvalidate();
		}
	}
	
	public interface ClearHistoryListener {
		public void success();
	}

	@Override
	public View configContentView() {
		mSetting = LayoutInflater.from(getContext()).inflate(R.layout.dialog_dynamic_setting, null);
		return mSetting;
	}
	
	@Override
	public void onDestroy() {
		ThreadHelper.destory(mChatThread);
		super.onDestroy();
	}

}
