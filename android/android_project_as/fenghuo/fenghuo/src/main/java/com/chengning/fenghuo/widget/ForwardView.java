package com.chengning.fenghuo.widget;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.data.bean.DynamicItemBean;

public class ForwardView extends RelativeLayout { 
	
	private View mLayout;
	// top
	private ImageView mUserImage;
	private TextView mUserName;
	private DynamicTextView mTime;   
	private ImageLoader imageLoader = ImageLoader.getInstance();   
	
	private Activity mContext; 
	private LayoutInflater mInflater;
	
	private DynamicItemBean mBean;
	private DynamicTextView mComment; 
	private String mCommentStr;
	
	public ForwardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ForwardView(Activity context,DynamicItemBean bean,String comment){
		super(context);
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mLayout = mInflater.inflate(R.layout.item_forward, null);
		
		mUserImage = (ImageView)mLayout.findViewById(R.id.item_forward_top_user_image);
		mUserName = (TextView)mLayout.findViewById(R.id.item_forward_top_user_name);
		mTime = (DynamicTextView)mLayout.findViewById(R.id.item_forward_top_time);
		mComment = (DynamicTextView)mLayout.findViewById(R.id.item_forward_comment);
		mBean = bean;
		mCommentStr = comment;
		if(mBean!=null)
			update(); 
	} 
	
	public View getView(){
		return mLayout;
	}
	
	public void update(){ 
		String imgstr = "";
		if(mBean.getImage_list()!=null && mBean.getImage_list().size()>0 &&
				mBean.getImage_list().get(0)!=null&&mBean.getImage_list().get(0).getImage_small()!=null)
		{
			imgstr = mBean.getImage_list().get(0).getImage_small();
		}
		if(!imgstr.equals(""))
		{
			imageLoader.displayImage(imgstr, mUserImage);
		}else
		{
			mUserImage.setVisibility(View.GONE);
		} 
		
		
		mUserName.setText("@"+mBean.getNickname());
		String content = getContentText(mBean.getContent());
		mTime.setContent(content);  
		mComment.setContent(mCommentStr);
	}
	
	private String getContentText(List<String> contents){
		String content = null;
		if(contents != null && contents.size() > 0){
			StringBuilder sb = new StringBuilder();
			for(String s : contents){
				sb.append(s);
			}
			content = sb.toString();
		}
		return content;
	} 
	  
}
