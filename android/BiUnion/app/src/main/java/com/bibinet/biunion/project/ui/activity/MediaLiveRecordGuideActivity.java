package com.bibinet.biunion.project.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alivc.component.custom.AlivcLivePushCustomDetect;
import com.alivc.component.custom.AlivcLivePushCustomFilter;
import com.alivc.live.detect.TaoFaceFilter;
import com.alivc.live.filter.TaoBeautyFilter;
import com.alivc.live.pusher.AlivcAudioAACProfileEnum;
import com.alivc.live.pusher.AlivcEncodeModeEnum;
import com.alivc.live.pusher.AlivcFpsEnum;
import com.alivc.live.pusher.AlivcLivePushCameraTypeEnum;
import com.alivc.live.pusher.AlivcLivePushConfig;
import com.alivc.live.pusher.AlivcLivePushError;
import com.alivc.live.pusher.AlivcLivePushErrorListener;
import com.alivc.live.pusher.AlivcLivePushInfoListener;
import com.alivc.live.pusher.AlivcLivePusher;
import com.alivc.live.pusher.AlivcPreviewOrientationEnum;
import com.alivc.live.pusher.AlivcQualityModeEnum;
import com.alivc.live.pusher.AlivcResolutionEnum;
import com.alivc.live.pusher.SurfaceStatus;
import com.bibinet.biunion.R;
import com.bibinet.biunion.project.application.BiUnionApplication;
import com.bibinet.biunion.project.application.Constants;
import com.bibinet.biunion.project.application.PathConfig;
import com.bibinet.biunion.project.models.BaseModel;
import com.bibinet.biunion.project.models.MediaLiveAddModel;
import com.bibinet.biunion.project.models.MediaLiveFinishModel;
import com.bibinet.biunion.project.models.MediaRecordAddModel;
import com.bibinet.biunion.project.net.BaseRetrofitCallBack;
import com.bibinet.biunion.project.net.MyRetrofitResponseCallback;
import com.bibinet.biunion.project.net.firing.MediaLiveFiring;
import com.bibinet.biunion.project.net.ready.MediaLiveReady;
import com.bibinet.biunion.project.ui.custom.MediaLiveRecordView;
import com.bibinet.biunion.project.ui.custom.PushBeautyDialog;
import com.bibinet.biunion.project.ui.dialog.WaitDialog;
import com.bibinet.biunion.project.ui.expand.TitleActivity;
import com.bibinet.biunion.project.ui.pop.MediaLiveMenuPop;
import com.bibinet.biunion.project.utils.DensityUtil;
import com.bibinet.biunion.project.utils.FileUtils;
import com.bibinet.biunion.project.utils.ImagePickHelper;
import com.bibinet.biunion.project.utils.ImageUtils;
import com.bibinet.biunion.project.utils.SharedPresUtils;
import com.bibinet.biunion.project.utils.StatusBarUtil;
import com.bibinet.biunion.project.utils.ToastUtils;
import com.google.gson.Gson;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static com.bibinet.biunion.project.net.MyRetrofitResponseCallback.CODE_1111;
import static com.bibinet.biunion.project.net.MyRetrofitResponseCallback.CODE_SUCCESS;

public class MediaLiveRecordGuideActivity extends TitleActivity {

    private static final String LIVE_COVER = "live_cover.jpg";//直播封面
    private static final int MSG_FAIL = 1;
    private static final int MSG_CHECK_NAME_SUCCESS = 7;
//    private static final int MSG_CHECK_NAME_FAIL = 8;
    private final String url = "rtmp://live.bibenet.com/bitbid/";
    final int MSG_SUCCESS = 0;
    public static final int MSG_FINISH_LIVE = 2;
    final int MSG_FINISH_LIVE_SUCCESS = 3;
    private static final int MSG_RECORD_START = 4;
    private static final int MSG_RECORD_START_FAIL = 5;
    private static final int MSG_RECORD_SUCCESS = 6;
    private String objectId;
    private String liveUrl;

    @BindView(R.id.act_media_live_record_guide_rl)
    View mGuideRl;
    @BindView(R.id.act_media_live_record_guide_bg)
    ImageView mGuideBg;
    @BindView(R.id.act_media_live_record_layout)
    View mLiveRecordLayout;
    @BindView(R.id.act_media_live_record_guide_preview_view)
    SurfaceView mPreviewView;

    @BindView(R.id.act_media_live_record_guide_address)
    TextView mAdressTv;

    @BindView(R.id.act_media_live_record_guide_cover_rl)
    View mCoverView;
    @BindView(R.id.act_media_live_record_guide_title_ll)
    View mTitleView;

    @BindView(R.id.act_media_live_record_guide_title)
    EditText mTitleEt;
    @BindView(R.id.act_media_live_record_guide_next)
    TextView mNextTv;
    @BindView(R.id.act_media_live_record_guide_start)
    TextView mStartTv;

    private AlivcLivePusher mAlivcLivePusher = null;
    private AlivcLivePushConfig mAlivcLivePushConfig;
    private File mLiveCover;
    private MediaLiveRecordView liveRecordView;
    private ImagePickHelper pickHelper;
    private TaoBeautyFilter taoBeautyFilter;
    private TaoFaceFilter taoFaceFilter;
    private MediaLiveFiring liveFiring;
    private boolean isStartPush = false;
    private boolean isPause;
    private String streamName;
    private MediaLiveReady mediaLiveReady;
    private WaitDialog waitDialog;
    private String title;
    private String userId;

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity,MediaLiveRecordGuideActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected int getTitleLayoutId() {
        return R.layout.activity_media_live_record_guide;
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

        ViewGroup.LayoutParams params = mPreviewView.getLayoutParams();
        params.height = DensityUtil.getScreenHeight(this);
        mPreviewView.setLayoutParams(params);
        waitDialog = new WaitDialog(this);

        mediaLiveReady = new MediaLiveReady();
        mLiveCover = FileUtils.creatFile(PathConfig.cachePathImage, LIVE_COVER);
        pickHelper = new ImagePickHelper(this, mLiveCover);
        mAdressTv.setText(BiUnionApplication.getInstance().getCurLocation());
        mPreviewView.getHolder().addCallback(mCallback);

        initPushConfig();
    }

    private void initPushConfig() {
        if (mAlivcLivePushConfig == null) {
            mAlivcLivePushConfig = new AlivcLivePushConfig();//初始化推流配置类
            mAlivcLivePushConfig.setResolution(AlivcResolutionEnum.RESOLUTION_540P);//分辨率540P，最大支持720P
            mAlivcLivePushConfig.setFps(AlivcFpsEnum.FPS_20); //建议用户使用20fps
            mAlivcLivePushConfig.setEnableBitrateControl(true); // 打开码率自适应，默认为true
            mAlivcLivePushConfig.setPreviewOrientation(AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT); // 默认为竖屏，可设置home键向左或向右横屏。
            mAlivcLivePushConfig.setAudioProfile(AlivcAudioAACProfileEnum.AAC_LC);//设置音频编码模式
//            mAlivcLivePushConfig.setQualityMode(AlivcQualityModeEnum.QM_RESOLUTION_FIRST);
            mAlivcLivePushConfig.setEnableAutoResolution(true); // 打开分辨率自适应，默认为false
            mAlivcLivePushConfig.setVideoEncodeMode(AlivcEncodeModeEnum.Encode_MODE_HARD);
            mAlivcLivePushConfig.setCameraType(AlivcLivePushCameraTypeEnum.CAMERA_TYPE_FRONT);
            mAlivcLivePushConfig.setQualityMode(AlivcQualityModeEnum.QM_CUSTOM);
            mAlivcLivePushConfig.setTargetVideoBitrate(1200); //目标码率1200Kbps
            mAlivcLivePushConfig.setMinVideoBitrate(400); //最小码率400Kbps
            mAlivcLivePushConfig.setInitialVideoBitrate(900); //初始码率900Kbps

            mAlivcLivePushConfig.setBeautyOn(true);
            mAlivcLivePushConfig.setBeautyWhite(48);
            mAlivcLivePushConfig.setBeautyBuffing(40);
            mAlivcLivePushConfig.setBeautyRuddy(29);
            mAlivcLivePushConfig.setBeautyCheekPink(25);
            mAlivcLivePushConfig.setBeautyThinFace(22);
            mAlivcLivePushConfig.setBeautyShortenFace(59);
            mAlivcLivePushConfig.setBeautyBigEye(24);
            SharedPresUtils.setFanzhuanOn(this, false);
            SharedPresUtils.setRecordOn(this, false);
            SharedPresUtils.setBeautyOn(this, true);

        }
        if (mAlivcLivePusher == null) {
            mAlivcLivePusher = new AlivcLivePusher();
        }

        try {
            mAlivcLivePusher.init(this, mAlivcLivePushConfig);
            /*是否支持自动对焦*/
            mAlivcLivePusher.isCameraSupportAutoFocus();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
//            showDialog(this, e.getMessage());
        } catch (IllegalStateException e) {
            e.printStackTrace();
//            showDialog(this, e.getMessage());
        }
        mAlivcLivePusher.setCustomDetect(new AlivcLivePushCustomDetect() {
            @Override
            public void customDetectCreate() {
                taoFaceFilter = new TaoFaceFilter(getApplicationContext());
                taoFaceFilter.customDetectCreate();
            }

            @Override
            public long customDetectProcess(long data, int width, int height, int rotation, int format, long extra) {
                if(taoFaceFilter != null) {
                    return taoFaceFilter.customDetectProcess(data, width, height, rotation, format, extra);
                }
                return 0;
            }

            @Override
            public void customDetectDestroy() {
                if(taoFaceFilter != null) {
                    taoFaceFilter.customDetectDestroy();
                }
            }
        });
        mAlivcLivePusher.setCustomFilter(new AlivcLivePushCustomFilter() {
            @Override
            public void customFilterCreate() {
                taoBeautyFilter = new TaoBeautyFilter();
                taoBeautyFilter.customFilterCreate();
            }

            @Override
            public void customFilterUpdateParam(float fSkinSmooth, float fWhiten, float fWholeFacePink, float fThinFaceHorizontal, float fCheekPink, float fShortenFaceVertical, float fBigEye) {
                if (taoBeautyFilter != null) {
                    taoBeautyFilter.customFilterUpdateParam(fSkinSmooth, fWhiten, fWholeFacePink, fThinFaceHorizontal, fCheekPink, fShortenFaceVertical, fBigEye);
                }
            }

            @Override
            public void customFilterSwitch(boolean on)
            {
                if(taoBeautyFilter != null) {
                    taoBeautyFilter.customFilterSwitch(on);
                }
            }

            @Override
            public int customFilterProcess(int inputTexture, int textureWidth, int textureHeight, long extra) {
                if (taoBeautyFilter != null) {
                    return taoBeautyFilter.customFilterProcess(inputTexture, textureWidth, textureHeight, extra);
                }
                return inputTexture;
            }

            @Override
            public void customFilterDestroy() {
                if (taoBeautyFilter != null) {
                    taoBeautyFilter.customFilterDestroy();
                }
                taoBeautyFilter = null;
            }
        });

        initLivePushListener();
    }

    private void initLivePushListener() {
        /**
         * 设置推流错误事件
         *
         * @param errorListener 错误监听器
         */
        mAlivcLivePusher.setLivePushErrorListener(new AlivcLivePushErrorListener() {
            @Override
            public void onSystemError(AlivcLivePusher livePusher, AlivcLivePushError error) {
                if(error != null) {
                    //添加UI提示或者用户自定义的错误处理
                    Log.e("livePush",error
                            .getCode() + error.getMsg());
                    mAlivcLivePusher.restartPush();
                }
            }
            @Override
            public void onSDKError(AlivcLivePusher livePusher, AlivcLivePushError error) {
                if(error != null) {
                    //添加UI提示或者用户自定义的错误处理
                }
            }
        });

//        mAlivcLivePusher.setLivePushNetworkListener(new AlivcLivePushNetworkListener() {
//            @Override
//            public void onNetworkPoor(AlivcLivePusher pusher) {
//                //网络差通知
//            }
//            @Override
//            public void onNetworkRecovery(AlivcLivePusher pusher) {
//                //网络恢复通知
//            }
//            @Override
//            public void onReconnectStart(AlivcLivePusher pusher) {
//                //重连开始通知
//            }
//            @Override
//            public void onReconnectFail(AlivcLivePusher pusher) {
//                //重连失败通知
//            }
//            @Override
//            public void onReconnectSucceed(AlivcLivePusher pusher) {
//                //重连成功通知
//            }
//            @Override
//            public void onSendDataTimeout(AlivcLivePusher pusher) {
//                //发送数据超时通知
//            }
//            @Override
//            public void onConnectFail(AlivcLivePusher pusher) {
//                //连接失败通知
//            }
//
//            @Override
//            public String onPushURLAuthenticationOverdue(AlivcLivePusher alivcLivePusher) {
//                return null;
//            }
//
//            @Override
//            public void onSendMessage(AlivcLivePusher alivcLivePusher) {
//
//            }
//        });

        mAlivcLivePusher.setLivePushInfoListener(new AlivcLivePushInfoListener() {
            @Override
            public void onPreviewStarted(AlivcLivePusher pusher) {
                //预览开始通知
            }
            @Override
            public void onPreviewStoped(AlivcLivePusher pusher) {
                //预览结束通知
            }
            @Override
            public void onPushStarted(AlivcLivePusher pusher) {
                //推流开始通知
                isStartPush = true;
                Log.e("livePush","start");
            }
            @Override
            public void onPushPauesed(AlivcLivePusher pusher) {
                //推流暂停通知
            }
            @Override
            public void onPushResumed(AlivcLivePusher pusher) {
                //推流恢复通知
            }
            @Override
            public void onPushStoped(AlivcLivePusher pusher) {
                //推流停止通知
                Log.e("livePush","stop");
            }
            @Override
            public void onPushRestarted(AlivcLivePusher pusher) {
                //推流重启通知
            }
            @Override
            public void onFirstFramePreviewed(AlivcLivePusher pusher) {
                //首帧渲染通知
            }
            @Override
            public void onDropFrame(AlivcLivePusher pusher, int countBef, int countAft) {
                //丢帧通知
            }
            @Override
            public void onAdjustBitRate(AlivcLivePusher pusher, int curBr, int targetBr) {
                //调整码率通知
            }
            @Override
            public void onAdjustFps(AlivcLivePusher pusher, int curFps, int targetFps) {
                //调整帧率通知
            }
        });
    }

    private SurfaceStatus mSurfaceStatus = SurfaceStatus.UNINITED;
//    private boolean mAsync = false;

    SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            if(mSurfaceStatus == SurfaceStatus.UNINITED || mSurfaceStatus == SurfaceStatus.DESTROYED) {
                mSurfaceStatus = SurfaceStatus.CREATED;
                if(mAlivcLivePusher != null) {
                    try {
//                        if (mAsync) {
//                            mAlivcLivePusher.startPreviewAysnc(mPreviewView);
//                        } else {
                            mAlivcLivePusher.startPreview(mPreviewView);
//                        }

//                        if(mAlivcLivePushConfig.isExternMainStream()) {
//                            startYUV(getApplicationContext());
//                        }
                    } catch (IllegalArgumentException e) {
                        e.toString();
                    } catch (IllegalStateException e) {
                        e.toString();
                    }
                }
            }
//            else if(mSurfaceStatus == SurfaceStatus.DESTROYED) {
//                mSurfaceStatus = SurfaceStatus.RECREATED;
//            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            mSurfaceStatus = SurfaceStatus.CHANGED;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            mSurfaceStatus = SurfaceStatus.DESTROYED;
            if(mAlivcLivePusher != null) {
                try {
                    mAlivcLivePusher.stopPreview();
                } catch (IllegalArgumentException e) {
                    e.toString();
                } catch (IllegalStateException e) {
                    e.toString();
                }
            }
        }
    };
    
    @Override
    protected void onResume() {
        super.onResume();
        resumePush();
    }

    private void resumePush() {
        if(isStartPush && mAlivcLivePusher != null) {
            try {
                if(isPause) {
                    mAlivcLivePusher.resume();
                    isPause = false;
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePush();
    }

    private void pausePush() {
        if(isStartPush) {
            try {
                if(mAlivcLivePusher != null) {
                    mAlivcLivePusher.pause();
                    isPause = true;
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        destroyPush();
        super.onDestroy();
    }

    void destroyPush(){
        if(mAlivcLivePusher != null) {
            try {
                if(isStartPush) {
                    if (liveRecordView != null) {
                        liveRecordView.onDestroy();
                    }

                        if (mAlivcLivePusher.isPushing()) {
                            mAlivcLivePusher.stopPush();
                        }

                }
                if (mPreviewView != null) {
                    mPreviewView.getHolder().removeCallback(mCallback);
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } finally {
                mAlivcLivePusher.destroy();
                mAlivcLivePusher = null;
                mPreviewView = null;
            }

        }

    }

    @OnClick(R.id.act_media_live_record_guide_close)
    void onCloseClick() {
        finish();
    }

    @OnClick(R.id.act_media_live_record_guide_cover_camer)
    void onCamerClick() {
        pickHelper.handleCameraClick(this);
    }

    @OnClick(R.id.act_media_live_record_guide_cover_photo)
    void onPhotoClick() {
        pickHelper.handleAbulmClick(this);
    }

    @OnClick(R.id.act_media_live_record_guide_next)
    void onNextClick() {
        if (mLiveCover.length() == 0) {
            ToastUtils.showLong("请先选择封面！");
            return;
        }
        mCoverView.setVisibility(View.GONE);
        mNextTv.setVisibility(View.GONE);
        mTitleView.setVisibility(View.VISIBLE);
        mStartTv.setVisibility(View.VISIBLE);
        mStartTv.setEnabled(true);
//        GlideHelper.getInst().loadImageWithPlace(this, Uri.fromFile(mLiveCover).toString(), mGuideBg);
    }

    @OnClick(R.id.act_media_live_record_guide_start)
    void onStartClick() {
        handleLauchActivty();
    }

    private boolean mRecordFlag = false;
    private boolean mHasRecord = false;
    private MediaLiveMenuPop.OnRecordHanlderListener mRecordListener;
    private MediaLiveMenuPop.OnLiveMenuPopListener mLiveListener = new MediaLiveMenuPop.OnLiveMenuPopListener() {
        @Override
        public void onDismiss() {
            if (liveRecordView != null) {
                liveRecordView.onDismiss();
            }
        }

        @Override
        public void onHandleRecord(boolean isRecord, MediaLiveMenuPop.OnRecordHanlderListener recordListener) {
            mRecordListener = recordListener;
            mRecordFlag = isRecord;
            if (isFirstRecord) {
                configRecord(streamName);
            } else {
                startRecord(isRecord, streamName);
            }
        }

        @Override
        public void onHandleMeiyan(boolean isMeiyan) {
//            configMeiyan();
            mAlivcLivePusher.setBeautyOn(isMeiyan);
        }

        @Override
        public void onHandleFanzhuan(boolean isFanzhuan) {
            mAlivcLivePusher.switchCamera();
        }
    };

    private void configRecord(String streamName) {
        mediaLiveReady.addLiveRecord(objectId, streamName, new MyRetrofitResponseCallback<MediaRecordAddModel>() {
            @Override
            protected void onRequestFail(String resMessage) {
                mHandler.sendEmptyMessage(MSG_RECORD_START_FAIL);
            }

            @Override
            protected void onRequestSuccess(MediaRecordAddModel model) {
                mHandler.sendEmptyMessage(MSG_RECORD_START);
            }

        });

    }

    private void configMeiyan() {
        PushBeautyDialog pushBeautyDialog = PushBeautyDialog.newInstance(SharedPresUtils.isBeautyOn(this));
        pushBeautyDialog.setAlivcLivePusher(mAlivcLivePusher);
        pushBeautyDialog.show(getSupportFragmentManager(), "beautyDialog");
    }

    private boolean isFirstRecord = true;
    private int count = 0;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS:
                    MediaLiveAddModel model = (MediaLiveAddModel) msg.obj;
                    if (TextUtils.equals(model.getResCode(), CODE_SUCCESS)) {
                        liveRecordView.setLiveMenuListener(mLiveListener);
                        objectId = model.getAppLive() != null ? model.getAppLive().getObjectId() : "" ;
                        liveRecordView.setObjectId(objectId);
                        if (model.getAppLive() == null || TextUtils.isEmpty(model.getAppLive().getEncryptionPlugUrl())) {
                            ToastUtils.showShort("无法获取直播地址，请重试");
                            break;
                        }
                        liveUrl = model.getAppLive().getEncryptionPlugUrl();
                        mAlivcLivePusher.startPush(liveUrl);
                        mStartTv.setEnabled(true);
                        waitDialog.close();
                    } else if (TextUtils.equals(model.getResCode(), CODE_1111)) {
                        ToastUtils.showShort(model.getResMessage());
                        finish();
                    } else {
                        mHandler.obtainMessage(MSG_FAIL, model.getResMessage()).sendToTarget();
                    }

                    break;
                case MSG_FAIL:
                    count++;
                    if (count > 3 ) {
                        ToastUtils.showShort("开启直播失败");
                        waitDialog.close();
                        mStartTv.setEnabled(true);
                        finish();
                    } else {
                        handleAddLive();
                    }
                    break;
                case MSG_FINISH_LIVE:
                    liveFinish();
                    break;
                case MSG_FINISH_LIVE_SUCCESS:
                    waitDialog.close();
                    MediaLiveFinishModel finishModel  = (MediaLiveFinishModel) msg.obj;
                    finishModel.setRecorded(mHasRecord);
                    MediaLiveFinishActivity.launch(MediaLiveRecordGuideActivity.this, Constants.LiveType.TYPE_ANCHOT, finishModel);
                    finish();
                    break;
                case MSG_RECORD_START:
                    startRecord(mRecordFlag, streamName);
                    break;
                case MSG_RECORD_START_FAIL:
                    if (isFirstRecord || mRecordFlag) {
                        ToastUtils.showShort("无法开始录制，请检查网络后重试！");
                    } else {
                        ToastUtils.showShort("无法暂停录制，请检查网络后重试！");
                    }
                    mRecordListener.onFail();
                    isFirstRecord = true;
                    break;
                case MSG_RECORD_SUCCESS:
                    if (isFirstRecord) {
                        liveRecordView.startRecord();
                        isFirstRecord = false;
                        mHasRecord = true;
                    } else {
                        liveRecordView.resumeRecord(mRecordFlag);
                        SharedPresUtils.setBeautyOn(MediaLiveRecordGuideActivity.this, mRecordFlag);
                    }
                    break;
                case MSG_CHECK_NAME_SUCCESS:
                    BaseModel checkNameModel = (BaseModel) msg.obj;
                    if (TextUtils.equals(checkNameModel.getResCode(), CODE_SUCCESS)) {
                        startLiveView();
                    }
//                    else if (TextUtils.equals(checkNameModel.getResCode(), CODE_1111)) {
//                        ToastUtils.showShort(checkNameModel.getResMessage());
//                        finish();
//                    }
                    else {
                        ToastUtils.showShort(checkNameModel.getResMessage());
                    }

                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void startRecord(boolean mRecordFlag, String streamName) {
        mediaLiveReady.startOrStopLiveRecord(mRecordFlag ? "start" : "stop", streamName, new MyRetrofitResponseCallback<MediaRecordAddModel>() {
            @Override
            protected void onRequestFail(String resMessage) {
                mHandler.sendEmptyMessage(MSG_RECORD_START_FAIL);
            }

            @Override
            protected void onRequestSuccess(MediaRecordAddModel model) {
                mHandler.sendEmptyMessage(MSG_RECORD_SUCCESS);
            }

        });
    }

    private void liveFinish() {
        if (mRecordFlag) {
            ToastUtils.showShort("停止录制之后才可以关闭！");
            return;
        }
        if (TextUtils.isEmpty(objectId)) {
            finish();
            return;
        }
        waitDialog.open();
        mediaLiveReady.finishLive(objectId, new MyRetrofitResponseCallback<MediaLiveFinishModel>() {
            @Override
            protected void onRequestFail(String resMessage) {

            }

            @Override
            protected void onRequestSuccess(MediaLiveFinishModel model) {
                mHandler.obtainMessage(MSG_FINISH_LIVE_SUCCESS,model).sendToTarget();
            }

        });
    }

    private void handleLauchActivty() {
        title = mTitleEt.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            ToastUtils.showShort("请输入直播标题！");
            return;
        }
        if (Constants.loginresultInfo == null || Constants.loginresultInfo.getUser() == null) {
            ToastUtils.showShort("请先登录！");
            return;
        }

        checkLiveTitle(title);

    }

    private void startLiveView() {
        mGuideRl.setVisibility(View.GONE);
        mLiveRecordLayout.setVisibility(View.VISIBLE);
        liveRecordView = new MediaLiveRecordView(MediaLiveRecordGuideActivity.this, mLiveRecordLayout);
        liveRecordView.setData(mHandler, Constants.loginresultInfo.getUser());
        userId = Constants.loginresultInfo.getUser().getUserId();
        streamName = "AppLive_" + userId + "_" + System.currentTimeMillis() + "_sd";
        liveUrl = url + streamName;
        mStartTv.setEnabled(false);
        waitDialog.open();
        handleAddLive();
    }

    private void handleAddLive() {

        mediaLiveReady.addLivePushUrl(mLiveCover.getAbsolutePath(), title, userId, liveUrl, new BaseRetrofitCallBack() {

            @Override
            public void onRequestSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try{
                        MediaLiveAddModel model = new Gson().fromJson(result, MediaLiveAddModel.class);
                        mHandler.obtainMessage(MSG_SUCCESS, model).sendToTarget();
                    } catch (Exception e){

                    }

                }
            }

            @Override
            protected void onRequestFail(String resMessage) {

            }

        });
    }

    private void checkLiveTitle(String title) {
        mediaLiveReady.checkLiveName(title, new BaseRetrofitCallBack() {
        @Override
        public void onRequestSuccess(String result) {
            if (!TextUtils.isEmpty(result)) {
                try{
                    BaseModel model = new Gson().fromJson(result, BaseModel.class);
                    mHandler.obtainMessage(MSG_CHECK_NAME_SUCCESS, model).sendToTarget();
                } catch (Exception e){

                }

            }
        }

            @Override
            protected void onRequestFail(String resMessage) {

            }
        });
    }

    @Override
    public void onBackPressedSupport() {
        if (isStartPush) {
            liveRecordView.handleLiveClose(this);
        } else {
            super.onBackPressedSupport();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        pickHelper.handleActivityResult(requestCode, resultCode, data, new ImagePickHelper.IfImagePickhelperCallback() {
            @Override
            public void cropSuccess(File file) {
                updateImage(file);
            }
        });
        super.onActivityResult(requestCode, resultCode, data);
    }

    //上传封面
    private void updateImage(File file) {
        Bitmap bitmap = ImageUtils.getScreenBitmap(this, file.getAbsolutePath());
        if (bitmap != null) {
//            ImageUtils.compressBmpFromBmp(bitmap,
//                    file);
            mGuideBg.setImageBitmap(bitmap);
            mNextTv.setEnabled(true);
            mLiveCover = file;
        } else {
            mNextTv.setEnabled(false);
        }
    }

}
