package com.chengning.fenghuo.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;

import com.chengning.fenghuo.R;

public class CommentAboutMePopup implements OnClickListener{
	
	public static final PopupDataType DEFAULT_DATA_TYPE = PopupDataType.AboutMe;
	
	private View mAllView;
	private View mAboutMeView;
	private View mMyCommentView;
	private PopupWindow mWindow;
	
	private PopupDataType mFilter;
	private OnDataTypeChangeListener mListener;
	
	public static enum PopupDataType{
		// title 所有评论，评论我的，我的评论
		All(1, "所有评论"),
		AboutMe(2, "评论我的"),
		MyComment(3, "我的评论"),
		;
		
		private int id;
		private String str;
		
		private PopupDataType(int id, String str){
			this.id = id;
			this.str = str;
		}
		
		public int getId(){
			return id;
		}
		
		public String getStr(){
			return str;
		}
	}

	public CommentAboutMePopup(Context context,  OnDataTypeChangeListener listener){
		this.mListener = listener;
		
		// view
		View contentView = LayoutInflater.from(context).inflate(R.layout.popupwindow_comment_about_me, null);
		mAllView = contentView.findViewById(R.id.layout_1);
		mAboutMeView = contentView.findViewById(R.id.layout_2);
		mMyCommentView = contentView.findViewById(R.id.layout_3);
		
		mWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// 需要设置一下此参数，点击外边可消失 
		mWindow.setBackgroundDrawable(new BitmapDrawable()); 
        //设置点击窗口外边窗口消失 
		mWindow.setOutsideTouchable(true); 
        // 设置此参数获得焦点，否则无法点击 
		mWindow.setFocusable(true); 
		
		// data
		mAllView.setTag(PopupDataType.All);
		mAboutMeView.setTag(PopupDataType.AboutMe);
		mMyCommentView.setTag(PopupDataType.MyComment);
		
		mAllView.setOnClickListener(this);
		mAboutMeView.setOnClickListener(this);
		mMyCommentView.setOnClickListener(this);
		
		changeFilter(DEFAULT_DATA_TYPE, true);
	}
	
	public PopupDataType getFilter(){
		return mFilter;
	}
	
	public void show(View anchor){
		mWindow.showAsDropDown(anchor);
	}

	private void changeFilter(PopupDataType filter, boolean isInit){
		if(filter != mFilter || isInit){
			mAllView.setSelected(false);
			mAboutMeView.setSelected(false);
			mMyCommentView.setSelected(false);
			
			mFilter = filter;
			switch (mFilter) {
			case All:
				mAllView.setSelected(true);
				break;
			case AboutMe:
				mAboutMeView.setSelected(true);
				break;
			case MyComment:
				mMyCommentView.setSelected(true);
				break;
			}
			
			if(!isInit){
				mListener.onChange(filter);
			}
		}
		mWindow.dismiss();
	}

	@Override
	public void onClick(View v) {
		PopupDataType filter = (PopupDataType) v.getTag();
		changeFilter(filter, false);
	}
	
	public static interface OnDataTypeChangeListener{
		public void onChange(PopupDataType type);
	}
	
}
