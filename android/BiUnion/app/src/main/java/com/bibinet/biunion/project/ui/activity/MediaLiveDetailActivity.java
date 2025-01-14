package com.bibinet.biunion.project.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.MediaPlayer;
import com.bibinet.biunion.R;
import com.bibinet.biunion.project.adapter.MediaLiveChatMesageAdapter;
import com.bibinet.biunion.project.application.Constants;
import com.bibinet.biunion.project.models.BaseModel;
import com.bibinet.biunion.project.models.MediaLiveCommentModel;
import com.bibinet.biunion.project.models.MediaLiveMsgItemModel;
import com.bibinet.biunion.project.models.MediaLivePlayDetailModel;
import com.bibinet.biunion.project.models.MediaRecordAddModel;
import com.bibinet.biunion.project.net.BaseRetrofitCallBack;
import com.bibinet.biunion.project.net.MyRetrofitResponseCallback;
import com.bibinet.biunion.project.net.ready.MediaLiveReady;
import com.bibinet.biunion.project.ui.dialog.WaitDialog;
import com.bibinet.biunion.project.ui.expand.TitleActivity;
import com.bibinet.biunion.project.utils.Common;
import com.bibinet.biunion.project.utils.ImageUtils;
import com.bibinet.biunion.project.utils.StatusBarUtil;
import com.bibinet.biunion.project.utils.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.OnClick;

import static com.bibinet.biunion.project.application.Constants.LiveType.TYPE_SPECTATOR;
import static com.bibinet.biunion.project.net.MyRetrofitResponseCallback.CODE_3333;

public class MediaLiveDetailActivity extends TitleActivity{

    private static final int MSG_CLOSE = 1;
    private static final int MSG_CLOSE_SUCCESS = 2;
    private static final int MSG_COMMENT_SUCCESS = 3;
    private static final int MSG_COMMENT_FAIL = 4;
    private static final int MSG_QUERY_SUCCESS = 5;
    private static final int MSG_QUERY_TIME = 6;
    private static final int MSG_CHECK_LIVE_STATUS_TIME = 7;
    private static final int MSG_HANDLE_CHECK_RESULT = 8;
    final int MSG_SUCCESS = 0;

    @BindView(R.id.act_media_live_detail_recycler)
    RecyclerView mRecyclerView;
    @BindView(R.id.act_media_live_detail_avatar)
    ImageView mAvatarImg;
    @BindView(R.id.act_media_live_detail_username)
    TextView mUsernameTv;
    @BindView(R.id.act_media_live_detail_reply_edit)
    EditText mCommentEt;
    @BindView(R.id.act_media_live_detail_video_player)
    SurfaceView mSurfaceView;

    private MediaLiveChatMesageAdapter mAdapter;
    AliVcMediaPlayer mPlayer;
    private SurfaceHolder.Callback mCallback;
    private MediaLiveReady mediaLiveReady;
    private String mObjectId;
    private String mUserId;
    private String mUserName;
    ArrayList<MediaLiveMsgItemModel> list;
    private boolean isCommentStart;
    private Thread mTimeThread;
    private WaitDialog waitDialog;

    public static void launch(Context context, String objectId) {
        Intent intent = new Intent(context, MediaLiveDetailActivity.class);
        intent.putExtra(Constants.MEDIA_RECORD_DETAIL_DATA_RECORD_PBJECTID, objectId);
        context.startActivity(intent);
    }

    @Override
    protected int getTitleLayoutId() {
        return R.layout.activity_media_live_detail;
    }

    @Override
    protected int getTitleString() {
        return 0;
    }

    @Override
    protected void onTitleCreate(Bundle savedInstanceState) {
        setTitleBackgroundColor(R.color.black);
        StatusBarUtil.setLightBar(this, false);
        hideTitleBar();

        waitDialog = new WaitDialog(this);

        mObjectId = getIntent().getStringExtra(Constants.MEDIA_RECORD_DETAIL_DATA_RECORD_PBJECTID);
        mediaLiveReady = new MediaLiveReady();

        mUserId = Constants.loginresultInfo.getUser().getUserId();
        mUserName = Constants.loginresultInfo.getUser().getEnterprise().getContactName();
        waitDialog.open();
        mediaLiveReady.getLivePlayUrl(mObjectId, mUserId, new MyRetrofitResponseCallback<MediaLivePlayDetailModel>() {
            @Override
            protected void onRequestFail(String resMessage) {

            }

            @Override
            protected void onRequestSuccess(MediaLivePlayDetailModel model) {
                mHandler.obtainMessage(MSG_SUCCESS, model).sendToTarget();
            }

        });

        list = new ArrayList<>();
        MediaLiveMsgItemModel firstComment = new MediaLiveMsgItemModel();
        firstComment.setUserName("欢迎各位招标行业相关人员");
        list.add(firstComment);

        mAdapter = new MediaLiveChatMesageAdapter(this, list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        initLive();
        initListenner();
    }

    private void initListenner() {

        mCommentEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    handled = true;

                    /*隐藏软键盘*/
//                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    if (inputMethodManager.isActive()) {
//                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//                    }
                    onSendClick();
                }
                return handled;
            }
        });
    }

    private void initLive() {
        mCallback = new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
                holder.setKeepScreenOn(true);
                // Important: surfaceView changed from background to front, we need reset surface to mediaplayer.
                // 对于从后台切换到前台,需要重设surface;部分手机锁屏也会做前后台切换的处理
                if (mPlayer != null) {
                    mPlayer.setVideoSurface(mSurfaceView.getHolder().getSurface());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                if (mPlayer != null) {
                    mPlayer.setSurfaceChanged();
                }

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                surfaceHolder.removeCallback(mCallback);
            }
        };

        mSurfaceView.getHolder().addCallback(mCallback);
        mPlayer = new AliVcMediaPlayer(this, mSurfaceView);
        initLiveListener();
        isCommentStart = true;
        mTimeThread = new Thread(){
            @Override
            public void run() {
                do {
                    try {
                        mHandler.sendEmptyMessage(MSG_CHECK_LIVE_STATUS_TIME);
                        mHandler.sendEmptyMessage(MSG_QUERY_TIME);// 每隔5秒发送一个msg给mHandler
                        Thread.sleep(5000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (isCommentStart);
            }
        };

        mTimeThread.start();
    }

    private boolean isAdded;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS:
                    waitDialog.close();
                    MediaLivePlayDetailModel model = (MediaLivePlayDetailModel) msg.obj;
                    if (model == null) {
                       return;
                    }
                    mPlayer.prepareAndPlay(model.getEncryptionPlayUrl());
                   if (TextUtils.isEmpty(model.getUserName())) {
                       mUsernameTv.setVisibility(View.GONE);
                   } else {
                       mUsernameTv.setVisibility(View.VISIBLE);
                       mUsernameTv.setText(model.getUserName());
                   }

                    if (!TextUtils.isEmpty(model.getUserLogo())){
                        RequestOptions options = new RequestOptions().override(100).circleCrop();
                        Glide.with(mAvatarImg).asBitmap()
                                .load(ImageUtils.base64ToBitmap(model.getUserLogo())).apply(options).into(mAvatarImg);
                    } else {
                        Glide.with(mAvatarImg).load(R.mipmap.wode_toux).into(mAvatarImg);
                    }

                    break;
                case MSG_CLOSE:
                    handlerLeaveLive(mObjectId, mUserId);
                    break;
                case MSG_CLOSE_SUCCESS:
                    finish();
                    break;
                case MSG_COMMENT_SUCCESS:
                    mCommentEt.getText().clear();
                    break;
                case MSG_QUERY_SUCCESS:
                    MediaLiveCommentModel commentQueryModel = (MediaLiveCommentModel) msg.obj;

                    if (commentQueryModel != null) {
                        LinkedList<MediaLiveMsgItemModel> comments = commentQueryModel.getAppComments();
                        if (!Common.isListEmpty(comments)) {
                            isAdded = false;
                            if (Common.isListEmpty(list) || list.size() == 1) {
                                list.addAll(comments);
                                isAdded = true;
                            } else {
                                MediaLiveMsgItemModel lastModel = list.get(list.size() - 1);
                                for (MediaLiveMsgItemModel comment : comments) {
                                    if (Integer.valueOf(comment.getObjectId()) > Integer.valueOf(lastModel.getObjectId())) {
                                        list.add(comment);
                                        isAdded = true;
                                    }
                                }
                            }
                            if (isAdded) {
                                mAdapter.notifyDataSetChanged();
                                mRecyclerView.smoothScrollToPosition(list.size() - 1);
                            }
                        }
                    }
                    break;
                case MSG_COMMENT_FAIL:
                    break;
                case MSG_QUERY_TIME:
                    queryComment();
                    break;
                case MSG_CHECK_LIVE_STATUS_TIME:
                    checkLivStatus();
                    break;
                case MSG_HANDLE_CHECK_RESULT:
                    BaseModel checkResult = (BaseModel) msg.obj;
                    if (TextUtils.equals(checkResult.getResCode(), CODE_3333)) {
                        MediaLiveFinishActivity.launch(MediaLiveDetailActivity.this, TYPE_SPECTATOR);
                        finish();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void checkLivStatus() {
        mediaLiveReady.checkLiveStatus(mObjectId, new BaseRetrofitCallBack() {

            @Override
            public void onRequestSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try{
                        BaseModel model = new Gson().fromJson(result, BaseModel.class);
                        mHandler.obtainMessage(MSG_HANDLE_CHECK_RESULT, model).sendToTarget();
                    } catch (Exception e){

                    }

                }
            }

            @Override
            protected void onRequestFail(String resMessage) {

            }
        });
    }

    private void queryComment() {
        mediaLiveReady.handleLiveComment(mUserName, mUserId, "", mObjectId, "1",
                new MyRetrofitResponseCallback<MediaLiveCommentModel>() {
            @Override
            protected void onRequestFail(String resMessage) {
            }

            @Override
            protected void onRequestSuccess(MediaLiveCommentModel model) {
                mHandler.obtainMessage(MSG_QUERY_SUCCESS, model).sendToTarget();
            }

        });

    }

    private void handlerLeaveLive(final String mObjectId, String mUserId) {
        if (TextUtils.isEmpty(mObjectId) || TextUtils.isEmpty(mUserId)) {
            finish();
            return;
        }
        mediaLiveReady.leaveLivePlay(mObjectId, mUserId, new MyRetrofitResponseCallback<MediaRecordAddModel>() {
            @Override
            protected void onRequestFail(String resMessage) {

            }

            @Override
            protected void onRequestSuccess(MediaRecordAddModel model) {
                mHandler.obtainMessage(MSG_CLOSE_SUCCESS, model).sendToTarget();
            }

        });
    }

    private void initLiveListener() {
        mPlayer.setErrorListener(new MediaPlayer.MediaPlayerErrorListener() {
            @Override
            public void onError(int i, String msg) {
                //错误发生时触发，错误码见接口文档
                Log.e("live_detail_err","code:"+ i + "_msg:" + msg);
            }
        });
    }

    @OnClick(R.id.act_media_live_detail_close)
    void onCloseClick() {

        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder
                .setMessage("确定要退出观看？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mHandler.sendEmptyMessage(MSG_CLOSE);
                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    void onSendClick() {
        String content = mCommentEt.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showShort("内容为空，请重新输入！");
            return;
        }

        mediaLiveReady.handleLiveComment(mUserName, mUserId, content, mObjectId, "0", new MyRetrofitResponseCallback<MediaLiveCommentModel>() {
            @Override
            protected void onRequestFail(String resMessage) {
                mHandler.sendEmptyMessage(MSG_COMMENT_FAIL);
            }

            @Override
            protected void onRequestSuccess(MediaLiveCommentModel model) {
                mHandler.obtainMessage(MSG_COMMENT_SUCCESS, model).sendToTarget();
            }

        });
    }

    @Override
    public void onBackPressedSupport() {
        onCloseClick();
    }

    @Override
    protected void onResume() {
//        if (mPlayer != null && !mPlayer.isPlaying()) {
//            mPlayer.resume();
//        }
        super.onResume();
    }

    @Override
    protected void onPause() {
//        if (mPlayer != null && mPlayer.isPlaying()) {
//            mPlayer.pause();
//        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.destroy();
            mPlayer = null;
        }
        if (mSurfaceView != null) {
            mSurfaceView.getHolder().removeCallback(mCallback);
            mSurfaceView = null;
        }

        if (isCommentStart) {
            if (mTimeThread != null && mTimeThread .isAlive()) {
                mTimeThread .interrupt();
                mTimeThread  = null;
            }
            isCommentStart = false;
        }
        super.onDestroy();
    }

}
