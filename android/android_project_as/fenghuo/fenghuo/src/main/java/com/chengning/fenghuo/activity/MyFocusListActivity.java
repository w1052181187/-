package com.chengning.fenghuo.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chengning.common.base.BaseActivity;
import com.chengning.common.util.HttpUtil;
import com.chengning.fenghuo.Consts;
import com.chengning.fenghuo.MyJsonHttpResponseHandler;
import com.chengning.fenghuo.R;
import com.chengning.fenghuo.adapter.MyFocusExpandableListAdapter;
import com.chengning.fenghuo.data.bean.UserInfoBean;
import com.chengning.fenghuo.util.Common;
import com.chengning.fenghuo.util.JUrl;
import com.chengning.fenghuo.util.UIHelper;
import com.chengning.fenghuo.widget.TitleBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woozzu.android.widget.IndexableExpandableListView;

@SuppressLint("ResourceAsColor")
public class MyFocusListActivity extends BaseActivity{
  
	private static final String TAG = MyFocusListActivity.class.getSimpleName();
	
	public static final int RESULT_CODE_IS_CHANGE = 3511;
	
	private static final int INIT_NEWSDATA_UI = 1;
	
	private TitleBar mTitleBar;
	private View mInput;
	private EditText mInputEditText;
	private ImageButton mInputClearButton;
	private IndexableExpandableListView mPullListView;        
	private TextView mTipsText;
	
	private List<UserInfoBean> mDataList;
	private List<UserInfoBean> mAllList;
	private List<UserInfoBean> mMatchedList;
	private BaseExpandableListAdapter mAdapter;
	
	private Activity mContext;
	
	private String mSearchStr;
	private boolean mIsChange;
	
	public static void launchForResult(Activity from){
		Intent intent = new Intent(from, MyFocusListActivity.class);
		from.startActivity(intent);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Common.setTheme(getActivity());
		this.setContentView(R.layout.activity_my_focus_list);
		super.onCreate(savedInstanceState); 
	}
	
	//在 子类 oncreate 之前运行
	@Override
	public void initViews() {
		mContext = this;
		mInput = findViewById(R.id.search_input);
		mInputEditText = (EditText) findViewById(R.id.search_inputedit);
		mInputClearButton = (ImageButton) findViewById(R.id.search_inputclear);
		mTipsText = (TextView) findViewById(R.id.tips_txt);
		mPullListView = (IndexableExpandableListView) findViewById(R.id.list);
//		mPullListView.addHeaderView(View.inflate(this, R.layout.header_activity_at_suggest, null));
		
		mTitleBar = new TitleBar(mContext, true);
		mTitleBar.setTitle("我关注的");
		mTitleBar.showDefaultBack();
//		mTitleBar.setRightButton("更新", R.color.title_bar_second_text_highlight);
	}
	
	@Override
	public void initDatas() {
		mDataList = new ArrayList<UserInfoBean>();
		mMatchedList = new ArrayList<UserInfoBean>();
		mIsChange = false;
		
		// IndexScroller
		mPullListView.setFastScrollEnabled(true);

		showEmpty();
		
//		if(!App.getInst().isFollowChange()){
//			if(App.getInst().getFollowList() == null 
//					|| App.getInst().getFollowList().size() == 0){
//				App.getInst().setFollowList(ContactsServer.getInst(mContext).queryUserInfoAll());
//			}
//		}
		
		getHandler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
//				mPullListView.setRefreshing();
//				if(App.getInst().isFollowChange()
//						|| App.getInst().getFollowList() == null
//						|| App.getInst().getFollowList().size() == 0)
//				{
//	    			List<UserInfoBean> list = ContactsServer.getInst(mContext).queryUserInfoAll();
//	    			if(list == null || list.size() == 0 || App.getInst().isFollowChange()){
//	    				UIHelper.addPD(mContext, "加载中...");
//						getNewsListByHttp();
//	    			}else{
//	    				mAllList = list;
//	    				App.getInst().setFollowList(list);
//	               		sendListMessage(INIT_NEWSDATA_UI, list);
//	    			}
//				}else{
//    				mAllList = App.getInst().getFollowList();
//               		sendListMessage(INIT_NEWSDATA_UI, App.getInst().getFollowList());
//				}
			}
		}, Consts.TIME_WAIT_REFRESH);
		
	}

	public void update(String str){
		mSearchStr = str;
		if(TextUtils.isEmpty(str)){
       		sendListMessage(INIT_NEWSDATA_UI, mAllList);
		}else{
			// 本地搜索结果
			if(mAllList != null && mAllList.size() != 0){
				showTips(false);
				
				mMatchedList.clear();
				for(UserInfoBean bean : mAllList){
					if(bean.getNickname().contains(str)){
						bean.setLocal_match_index(bean.getNickname().indexOf(str));
						mMatchedList.add(bean);
					}else if( bean.getNickname_pinyin().contains(str.toUpperCase())){
						bean.setLocal_match_index(bean.getNickname_pinyin().indexOf(str.toUpperCase()));
						mMatchedList.add(bean);
					}
				}
           		sendListMessage(INIT_NEWSDATA_UI, mMatchedList);
			}
		}
	}
	 
	@Override
	public void installListeners() {   
//		mTitleBar.setRightButtonOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				UIHelper.addPD(mContext, "加载中...");
//				
//            	mInputEditText.setText("");  
//            	mInputClearButton.setVisibility(View.GONE);  
//            	
//				getNewsListByHttp();
//			}
//		});
		mInputEditText.addTextChangedListener(new TextWatcher() {  
  
            @Override  
            public void onTextChanged(CharSequence s, int start, int before,  
                    int count) {  
  
            }  
  
            @Override  
            public void beforeTextChanged(CharSequence s, int start, int count,  
                    int after) {  
            }  
  
            @Override  
            public void afterTextChanged(Editable s) {  
            	String str = mInputEditText.getText().toString();
                int textLength = mInputEditText.getText().length();  
                if (textLength > 0) {  
                	mInputClearButton.setVisibility(View.VISIBLE);  
                } else {  
                	mInputClearButton.setVisibility(View.GONE);  
                } 
                
            	update(str);
            }  
        });  
  
		mInputClearButton.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	mInputEditText.setText("");  
            	mInputClearButton.setVisibility(View.GONE);  
            }  
        });  
		mInputEditText.setOnKeyListener(new OnKeyListener() {
  
            @Override  
            public boolean onKey(View arg0, int keyCode, KeyEvent event) {  
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                	String str = mInputEditText.getText().toString();
                	
                    if(TextUtils.isEmpty(str)){
                    	UIHelper.showToast(mContext, "请输入您想要搜索的内容");
                    	
                    	return false;
                    }else{
            			InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE); 
            	        imm.hideSoftInputFromWindow(mInputEditText.getWindowToken(),0);

                    	update(str);
                    }
                }
                return false;  
            }  
        });
//		mPullListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				UserInfoBean u = (UserInfoBean) view.findViewById(R.id.user_name).getTag();
//				setResultAtUser(u);
//			}
//		});
//		mPullListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true)); 
//		mPullListView.setOnRefreshListener(new OnRefreshListener(){  
//			@Override
//			public void onRefresh(PullToRefreshBase refreshView) { 
//				getNewsListByHttp();
//			}
//		}); 
		mPullListView.setOnGroupClickListener(new OnGroupClickListener() {  
            
            @Override  
            public boolean onGroupClick(ExpandableListView parent, View v,  
                    int groupPosition, long id) {  
                return true;  
            }  
        });
		mPullListView.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				final UserInfoBean u = (UserInfoBean) v.findViewById(R.id.user_name).getTag();
				UserInfoActivity.launch(mContext, u.getNickname());
				return true;
			}
		});
	}
	
	@Override
	public void uninstallListeners() {

	}

	public void setResultIsChange(){
	    Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("is_change", mIsChange);
        //设置返回数据
        mContext.setResult(RESULT_CODE_IS_CHANGE, intent);
	}
	
	@Override
    public void onBackPressed() {
        setResultIsChange();
        super.onBackPressed();
    }
	
	@Override
	public void onStart() {
		super.onStart(); 
	}
	
	@Override
	public void onStop(){
		super.onStop();
	}

	public void onDestroy() { 
		super.onDestroy();
	}
 
	public void onResume() {
		super.onResume();
	}
	
	public void onPause() {
		super.onPause();
	}
	
	public void sendListMessage(int what, List list){
		int listSize = 0;
		if(list != null){
			listSize = list.size();
		}
		Message message = getHandler().obtainMessage(what, list);
		message.arg1 = listSize;
	    message.sendToTarget();  
	}
	
	private void showEmpty(){
		mAdapter = new MyFocusExpandableListAdapter(mContext, mDataList, addBtnOnClickListener, false, null);
		mPullListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}
	
	private void showTips(boolean show){
		if(show){
			String str = null;
			str = "还没有关注";
			mTipsText.setText(str);
			mTipsText.setVisibility(View.VISIBLE);
			mPullListView.setVisibility(View.GONE);
		}else{
			mTipsText.setVisibility(View.GONE);
			mPullListView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void processHandlerMessage(Message msg) {
		switch (msg.what) 
		{
		case INIT_NEWSDATA_UI: 
			List<UserInfoBean> list = (List<UserInfoBean>) msg.obj;  
			int listSize = msg.arg1;

			if (list != null && list.size() > 0) 
			{ 
				mDataList.clear();
				mDataList.addAll(list);

				mAdapter = new MyFocusExpandableListAdapter(mContext, mDataList, addBtnOnClickListener, false, null);
				mPullListView.setAdapter(mAdapter);
				mPullListView.setIndexScrollerAlwaysVisible(true);
				
				mAdapter.notifyDataSetChanged();
				for(int i = 0; i < mAdapter.getGroupCount(); i++){  
					mPullListView.expandGroup(i);  
				}
				showTips(false);
			}
			else  
			{
				showTips(true);
			}

			UIHelper.removePD();
			break;
		}
	}

	private void getNewsListByHttp() {

		String url = JUrl.SITE + JUrl.URL_GET_MILITARY_FANS_FOLLOW;
		HttpUtil.get(mContext, url, null, new MyJsonHttpResponseHandler() {
			
			@Override
	        public void onFailure(int statusCode, Header[] headers,Throwable throwable,JSONObject errorResponse) { 
	             
	            Common.handleHttpFailure(getActivity(),throwable);
	        };

			@Override
	        public void onFinish() { 
	        };

			@Override
			public void onDataSuccess(int status, String mod, String message,
					String data, JSONObject obj) {
				// TODO Auto-generated method stub
				Gson gson = new Gson();
            	List<UserInfoBean> list = gson.fromJson(data,new TypeToken<List<UserInfoBean>>(){}.getType());

//    			App.getInst().setFollowList(list);
//    			App.getInst().setFollowChange(false);
//    			ContactsServer.getInst(mContext).deleteUserInfoAll();
//    			ContactsServer.getInst(mContext).insertUserInfoList(list);

				mAllList = list;
           		sendListMessage(INIT_NEWSDATA_UI, list);
			}

			@Override
			public void onDataFailure(int status, String mod, String message,
					String data, JSONObject obj) {
				UIHelper.showToast(mContext, message);
			}; 
	    }); 
	}
	
	
	private OnClickListener addBtnOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
		}
	};

	@Override
	public Activity getActivity() {
		return MyFocusListActivity.this;
	}
}
