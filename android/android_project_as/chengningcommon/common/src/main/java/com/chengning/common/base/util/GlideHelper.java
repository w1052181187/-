package com.chengning.common.base.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.chengning.common.R;
import com.chengning.common.util.DisplayUtil;

import java.security.MessageDigest;

import static android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN;


/**
 * Created by Administrator on 2018/3/27.
 */

public class GlideHelper {

    private static GlideHelper helper;

    public static GlideHelper getInst(){
        if(helper == null){
            synchronized (GlideHelper.class) {
                if(helper == null){
                    helper = new GlideHelper();
                }
            }
        }
        return helper;
    }


    public void onTrimMemory(Context context, int level) {
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(context).clearMemory();
        }
        Glide.get(context).trimMemory(level);
    }

    public void clearCache(Context context) {
        clearCacheDiskSelf(context);
        clearCacheMemory(context);
    }
    // 清除图片磁盘缓存，调用Glide自带方法
    boolean clearCacheDiskSelf(final Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(context).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(context).clearDiskCache();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 清除Glide内存缓存
    public boolean clearCacheMemory(Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(context).clearMemory();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void clear(Activity activity, ImageView imageView) {
        if (isInvalidContext(activity)) {
            return;
        }
        Glide.with(activity).clear(imageView);
    }

    private RequestOptions getRequestOption(){
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.loading).error(R.drawable.loading).fallback(R.drawable.loading);
       return options;
    }

    public void loadImageWithPlace(Activity context, String path, ImageView imageView){
        loadImageWithPlace(context, path, getRequestOption(), imageView);
    }

    public void loadImageWithPlace(Activity context, String path, RequestOptions options, ImageView imageView){
        if (isInvalidContext(context)) {
            return;
        }
        Glide.with(context).load(path)
                .apply(calculateImageWH(options,imageView)).into(imageView);
    }

    public void loadImageWithPlace(Activity context, String path, RequestOptions options, Target target){
        if (isInvalidContext(context)) {
            return;
        }
        Glide.with(context).load(path)
                .apply(options).into(target);
    }

    public void loadImageWithPlace(Activity context, String path, Target target){
        loadImageWithPlace(context, path, getRequestOption(), target);
    }
    public void loadImageWithPlace(Activity context, String path, ImageView imageView, GlideImageType imageType){
        if (isInvalidContext(context)) {
            return;
        }

        if (imageType == GlideImageType.defaulted) {
            loadImageWithPlace(context, path, imageView);
            return;
        }
        Glide.with(context).load(path)
                .apply(calculateImageWH(getRequestOption(),imageView).transform(creatTransform(context,imageType)))
                .into(imageView);
    }

    public RequestOptions calculateImageWH(RequestOptions requestOption, ImageView imageView) {
        if (imageView == null) {
            return requestOption.centerCrop();
        }
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
//        int width = DisplayUtil.getInst().px2dip(params.width);
//        int height = DisplayUtil.getInst().px2dip(params.height);
        int width = params.width;
        int height = params.height;
        return requestOption.override(width, height).centerCrop();
    }

    public static boolean isInvalidContext(Activity activity){
        return  activity == null || activity.isFinishing();
    }

    public BitmapTransformation creatTransform(Context context, GlideImageType imageType) {
        BitmapTransformation transformation = null;
        switch (imageType) {
            case circled:
                transformation = new GlideCircleTransform(context);
                break;
            case rounded:
                transformation = new GlideRoundTransform(context, imageType.getRadius());
                break;
            case covered:
                transformation = new GlideCoveredRoundTransform(context, imageType.getRadius(), imageType.getResId());
                break;
            default:
                break;
        }
        return transformation;
    }

    public enum GlideImageType {
        defaulted,
        circled,
        rounded,
        covered;

        int mRadius;
        int mResId;
        GlideImageType(){

        }

        public int getRadius() {
            return mRadius;
        }

        public void setRadius(int mRadius) {
            this.mRadius = mRadius;
        }

        public int getResId() {
            return mResId;
        }

        public void setResId(int resId) {
            this.mResId = resId;
        }
    }

   public static class PauseOnScrollListener implements AbsListView.OnScrollListener {

        private Context mContext;
        private AbsListView.OnScrollListener mScrollListener;

        public PauseOnScrollListener(Context context, AbsListView.OnScrollListener scrollListener) {
            this.mContext = context;
            this.mScrollListener = scrollListener;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

            final RequestManager glide = Glide.with(mContext);

            if (scrollState == SCROLL_STATE_IDLE) {
                glide.resumeRequests();
            } else {
                glide.pauseRequests();
            }
            mScrollListener.onScrollStateChanged(view,scrollState);
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    //圆形图片
    public class GlideCircleTransform extends BitmapTransformation {

        public GlideCircleTransform(Context context) {
            super(context);
        }
        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return circleCrop(pool, toTransform);
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {
            messageDigest.update(getClass().getName().getBytes());
        }
    }

    Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        // TODO this could be acquired from the pool too
        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        return result;
    }

    //圆角图片
    public class GlideRoundTransform extends BitmapTransformation {

        private float radius = 0f;

        public GlideRoundTransform(Context context) {
           this(context,0);
        }

        public GlideRoundTransform(Context context, int dp) {
            super(context);
            this.radius = dp;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform, radius);
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {
//            messageDigest.update((getClass().getName() + Math.round(radius)).getBytes());
        }
    }

    private Bitmap roundCrop(BitmapPool pool, Bitmap source, float radius) {
        if (source == null) return null;

        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rectF, radius, radius, paint);
        return result;
    }

    //圆角图片
    public class GlideCoveredRoundTransform extends BitmapTransformation {

        private int mResId;
        private Context mContext;
        private int radius = 0;

        public GlideCoveredRoundTransform(Context context) {
            this(context, 4, 0);
        }

        public GlideCoveredRoundTransform(Context context, int dp, int resId) {
            super(context);
            this.mContext = context;
            this.radius = dp;
            this.mResId = resId;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return roundCoveredCrop(mContext, mResId, pool, toTransform, radius);
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {
//            messageDigest.digest((getClass().getName() + mResId + Math.round(radius)).getBytes());
        }

    }

    private static Bitmap roundCoveredCrop(Context context, int resId, BitmapPool pool, Bitmap source, final int radius) {
        if (source == null) return null;

        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rectF, radius, radius, paint);
        Bitmap tempBitmap = BaseCommon.bitmapWithImage(context, result, resId, 1, radius);
        return tempBitmap;
    }
}
