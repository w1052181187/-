package com.chengning.fenghuo.widget;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chengning.fenghuo.R;

/**
 * @description 升级弹窗
 * @author wangyungang
 * @date 2015.7.24 17:09
 */
public class UpgradeDialog extends DialogFragment {

	private View mBg;
	private View mTopBg;
	private TextView contentTv;
	private TextView confirmTv;
	private View mCloseBtn;
	private Activity mContext;
	private SpannableStringBuilder content;
	private DialogOnClickListener mListener;
	private View mView;
	private View mEmpty;
	protected boolean hasMeasured;
	
	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		
		initView();
		initData();
	}

	public void setData(SpannableStringBuilder nameBuilder, final DialogOnClickListener listener){
		
		content = nameBuilder;
		mListener = listener;
	}

	
	private void initData() {
		if (null != content) {
			contentTv.setText(content);
		} else {
			contentTv.setText("");
		}
	}

	private void initView() {
		mEmpty = mView.findViewById(R.id.dialog_upgrade_empty);
		mBg = mView.findViewById(R.id.dialog_upgrade_bg);
		mTopBg = mView.findViewById(R.id.dialog_upgrade_bg_rl);
		mCloseBtn = mView.findViewById(R.id.dialog_upgrade_close_btn);
		contentTv = (TextView) mView.findViewById(R.id.dialog_upgrade_content);
		confirmTv = (TextView) mView.findViewById(R.id.dialog_upgrade_confirm_btn);
		
		
		mTopBg.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			
			@Override
			public boolean onPreDraw() {
				if (hasMeasured == false) {

					LayoutParams layoutParams = mBg.getLayoutParams();
					layoutParams.width = mTopBg.getMeasuredWidth();
					mBg.setLayoutParams(layoutParams);
                    hasMeasured = true;

                }
				return true;
			}
		});
		
		mEmpty.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				dismissAllowingStateLoss();
			}
		});
		
		mCloseBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismissAllowingStateLoss();
			}
		});
		
		confirmTv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.onConfirmClick();
				dismissAllowingStateLoss();
			}

		});
	}



	@Override
	public void onAttach(Activity activity) {
		mContext = activity;
		super.onAttach(activity);
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.dialog_upgrade, container);
		return mView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setStyle(DialogFragment.STYLE_NORMAL,
				android.R.style.Theme_Translucent_NoTitleBar);
	}
	
	public static interface DialogOnClickListener   {
		void onConfirmClick();
	}
	
}
