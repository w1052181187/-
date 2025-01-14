package com.cmstop.jstt.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.leolin.shortcutbadger.ShortcutBadger;

import org.apache.http.conn.ConnectTimeoutException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chengning.common.base.util.BaseCommon;
import com.chengning.common.update.UpdateDownLoadService;
import com.cmstop.jstt.App;
import com.cmstop.jstt.R;
import com.cmstop.jstt.SettingManager;
import com.cmstop.jstt.views.EmojiTextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class Common extends BaseCommon{
	private static final String TAG = Common.class.getSimpleName();
	
	public static final long TIME_WAIT_COMPLETE = 10;
	public static final long TIME_WAIT_REFRESH = 20;
	
	public static final int TRUE = 1;
	public static final int FALSE = 0;

	public static boolean hasNet(){
		return IsHaveInternet(App.getInst()) != NetType.NET_UNCONECTION;
	}
	
	public static void showHttpFailureToast(Context context){
    	UIHelper.showToast(context, Common.hasNet() ? context.getString(R.string.server_fail) : context.getString(R.string.intnet_fail));
	}

	/**
	 * 请求http失败处理
	 * 
	 * @param context
	 * @param throwable
	 */
	public static void handleHttpFailure(Activity context, Throwable throwable) {
		if (context == null) {
			return;
		}
		
		if (hasNet()) {
			if (throwable != null
					&& throwable.getClass().isInstance(
							new ConnectTimeoutException())) {
				UIHelper.showToast(context, R.string.intent_timeout);
			} else {
				UIHelper.showToast(context, R.string.server_fail);
			}
		} else {
			UIHelper.showToast(context, R.string.intnet_fail);
		}
	}
	
	public static boolean isTargetTimeBefore(long oldTime){
		return isTargetTimeBefore(oldTime, 30 * 60 * 1000);
	}
	
	public static boolean isTargetTimeBefore(long oldTime, long interval){
		long c = System.currentTimeMillis();
		long i = Math.abs(c - oldTime);
		return i >= interval;
	}
	
	public static boolean isFirstRun(){
		//加入版本控制（升级后也要引导下载）
		return SPHelper.getInst().getBoolean(getVersionCode(App.getInst()) + SPHelper.KEY_IS_FIRST_RUN, true);
	}
	
	public static void setIsFirstRun(boolean isFirstRun){
		SPHelper.getInst().saveBoolean(getVersionCode(App.getInst()) + SPHelper.KEY_IS_FIRST_RUN, isFirstRun);
	}
	
	/**
	 * 判断应用是否已安装
	 * 
	 * @param context
	 * @param packageName 包名
	 * @return
	 */ 
	public static boolean isInstalled(Context context, String packageName) { 
	    boolean hasInstalled = false; 
	    PackageManager pm = context.getPackageManager(); 
	    List<PackageInfo> list = pm 
	            .getInstalledPackages(PackageManager.PERMISSION_GRANTED); 
	    for (PackageInfo p : list) { 
	        if (packageName != null && packageName.equals(p.packageName)) { 
	            hasInstalled = true; 
	            break; 
	        } 
	    } 
	    return hasInstalled; 
	}
	
	public static boolean isUmengChannelNeedNoticeDownload(Context context){
		return isUmengChannelXiaomi(context) || isUmengChannelVivo(context);
	}
	
	public static boolean isUmengChannelXiaomi(Context context){
		return "xiaomi".equals(getAppMetaData(context, "UMENG_CHANNEL"));
	}
	
	public static boolean isUmengChannelVivo(Context context){
		return "vivo".equals(getAppMetaData(context, "UMENG_CHANNEL"));
	}

	/**
	 * 获取application中指定的meta-data
	 * 
	 * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
	 */
	public static String getAppMetaData(Context ctx, String key) {
		if (ctx == null || TextUtils.isEmpty(key)) {
			return null;
		}
		String resultData = null;
		try {
			PackageManager packageManager = ctx.getPackageManager();
			if (packageManager != null) {
				ApplicationInfo applicationInfo = packageManager
						.getApplicationInfo(ctx.getPackageName(),
								PackageManager.GET_META_DATA);
				if (applicationInfo != null) {
					if (applicationInfo.metaData != null) {
						resultData = applicationInfo.metaData.getString(key);
					}
				}

			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return resultData;
	}
	
	public static String getUmengChannel(Context context){
		return getAppMetaData(context, "UMENG_CHANNEL");
	}
	
	/**
	 * 下载APK
	 * @param context
	 * @param url
	 */
	public static void downloadApk(Context context, String url, String title) {
		UpdateDownLoadService.startDownloadApkService(context, url, title);
	}
	
	/**
	 * 设置主题
	 * @param activity
	 */
	public static void setTheme(Activity activity){
		if(isTrue(SettingManager.getInst().getNightModel())){  
            activity.setTheme(R.style.NightTheme);  
        }else{  
            activity.setTheme(R.style.DayTheme);  
        }
	}
	
	public static void saveImageToGallery(final Context context, final String url){
		if(TextUtils.isEmpty(url)){
			Toast.makeText(context, "无效的图片", Toast.LENGTH_SHORT).show();
			return;
		}
		ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				Toast.makeText(context, "无效的图片", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				saveBitmapToGallery(context, arg2, url);
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
			}
		});
	}

	public static void saveBitmapToGallery(Context context, Bitmap bmp, String url) {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(context, "未找到sd卡，图片无法存储", Toast.LENGTH_SHORT).show();
			return;
		}
		if(TextUtils.isEmpty(url) || bmp == null || bmp.getWidth() <= 0 || bmp.getHeight() <= 0){
			Toast.makeText(context, "无效的图片", Toast.LENGTH_SHORT).show();
			return;
		}
		// 图片目录
		File appDir = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/xinjunshi", "xinjunshi_image");
		if (!appDir.exists()) {
			// 创建目录
			appDir.mkdirs();
		}
		String imageName = url.substring(url.lastIndexOf("/"), url.length());
		File file = new File(appDir, imageName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 通知图库更新
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
		Toast.makeText(context, "图片已保存至" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
	}
	
	public static StringBuffer convertArraylistToString(ArrayList<String> list){
		StringBuffer str = new StringBuffer();		
		for(int i=0;i<list.size();i++){
			str.append(list.get(i));
		}		
		return str;
	}
	
	public static void handleTextViewReaded(Context context, View root, int id, boolean isReaded){
		View v = root.findViewById(id);
		if(v != null && v instanceof TextView){
			handleTextViewReaded(context, (TextView)v, isReaded);
		}
	}
	
	public static void handleTextViewReaded(Context context, TextView view, boolean isReaded){
		
		if (isReaded) {
			view.setTextColor(Common.isTrue(SettingManager.getInst().getNightModel()) ? 
					context.getResources().getColor(R.color.night_tab_sec_color) : context.getResources().getColor(R.color.content_readed_color));
		} else {
			view.setTextColor(Common.isTrue(SettingManager.getInst().getNightModel()) ?
		    		context.getResources().getColor(R.color.night_text_color) : context.getResources().getColor(R.color.common_text_title));
		}
		
	}
	
	public static Bitmap bitmapWithImage(Context context, Bitmap bitmap, int resId, float scale, int roundPixels) {
		if(bitmap == null){
			return null;
		}
		try {
	        int width, height;
	        height = bitmap.getHeight();
	        width = bitmap.getWidth();
	        
	        int tW = (int) (width * scale);
	        int tH = (int) (height * scale);
	        
	        Matrix matrix = new Matrix(); 
	        matrix.postScale(scale, scale);
	        Bitmap scaleBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	        Canvas c = new Canvas(scaleBitmap);
	        Paint paint = new Paint();
	        paint.setAntiAlias(true);
	        
	        // image
	        Bitmap imgBitmap = BitmapFactory.decodeResource(context.getResources(), resId);
	        c.drawBitmap(imgBitmap, (tW / 2 - imgBitmap.getWidth() / 2), (tH / 2 - imgBitmap.getHeight() / 2), paint);

	        Bitmap outBitmap;
	        if(roundPixels == 0){
	        	outBitmap = scaleBitmap;
	        }else{
	        	outBitmap = Bitmap.createBitmap(tW, tH, Bitmap.Config.ARGB_8888);
	            Canvas c2 = new Canvas(outBitmap);
	            Paint paint2 = new Paint();
	            Rect srcRectF = new Rect(0, 0, tW, tH);
	            RectF destRectF = new RectF(0, 0, tW, tH);
	            paint2.setAntiAlias(true);
	            c2.drawARGB(0, 0, 0, 0);
	            paint2.setColor(-16777216);
	            c2.drawRoundRect(destRectF, roundPixels, roundPixels, paint2);
	            paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
	            c2.drawBitmap(scaleBitmap, srcRectF, destRectF, paint2);
	        }
	        
	        return outBitmap;
		}
        catch (Exception e) {
			e.printStackTrace();
		}
        catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
        return bitmap;
    }
	
	public static Bitmap getViewBitmap_RGB565(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;    
    }
	
	public static Bitmap getViewBitmap_ARGB8888(View view, int width, int height) {
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(new Canvas(bitmap));
        return bitmap;    
    }
	
	/**
	 * 创建文件
	 * 
	 * @param path
	 * @param fileName
	 * @return
	 */
	public static File creatFile(String path, String fileName) {
		File file = new File(checkFileDir(path), fileName);

		return file;
	}
	
	/**
	 * 检查目录是否存在，不存在则创建
	 * 
	 * @param path
	 * @return
	 */
	public static File checkFileDir(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}
	
//	 * 获取版本号
//	 3  * @return 当前应用的版本号
//	 4  */
//	 5 public String getVersion() {
//	 6     try {
//	 7         PackageManager manager = this.getPackageManager();
//	 8         PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
//	 9         String version = info.versionName;
//	10         return this.getString(R.string.version_name) + version;
//	11     } catch (Exception e) {
//	12         e.printStackTrace();
//	13         return this.getString(R.string.can_not_find_version_name);
//	14     }
//	15 }
	
	/**
	 * @Methods: getFileSize
	 * @Description: 获得文件大小
	 * @param f
	 * 
	 * @return
	 * @throws Exception
	 * @throws
	 */
	public static long getFileSize(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}
	
	public static int clearCacheFolder(File dir, long numDays) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, numDays);
					}

					if (child.lastModified() < numDays) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}
	
	/**
	 * date 转时间戳
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static long formatYYMMDDHHMMSStoLong(String date) throws ParseException {
		Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.parse(date);
		Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
		.parse("1970-01-01 08:00:00");
		// 单位为s
		long l = Math.abs(date1.getTime() - date2.getTime()) / 1000;
		return l;
	}
	
	public static String getDateCompareNow(long timeInt) {
		long timeLong = timeInt * 1000;
		Date currentTime = new Date();
		Date beginTime = new Date(timeLong);

		Calendar c1 = Calendar.getInstance();
		c1.setTime(currentTime);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(beginTime);
		int h1 = c1.get(Calendar.HOUR_OF_DAY);
		int h2 = c2.get(Calendar.HOUR_OF_DAY);
		int m1 = c1.get(Calendar.MINUTE);
		int m2 = c2.get(Calendar.MINUTE);
		int s1 = c1.get(Calendar.SECOND);
		int s2 = c2.get(Calendar.SECOND);

		if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR)) {
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			String temp = sdf2.format(beginTime);
			return temp;
		} else if (c1.get(Calendar.DAY_OF_YEAR) != c2.get(Calendar.DAY_OF_YEAR)) {
			SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd");
			String temp = sdf2.format(beginTime);
			return temp;
		} else if (h1 != h2) {
			if (h1 > h2) {
				return handleHour(currentTime, beginTime);
			} else {
				SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
				String temp = sdf2.format(beginTime);
				return temp;
			}
		} else if (m1 != m2) {
			if (m1 > m2) {
				return String.valueOf(m1 - m2) + "分钟前";
			} else {
				SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
				String temp = sdf2.format(beginTime);
				return temp;
			}
		} else if (s1 != s2) {
			return "刚刚";
		} else {
			return "刚刚";
		}
	}
	
	private static String handleHour(Date currentTime, Date beginTime) {
		String ftime = "";
		int hour = (int)((currentTime.getTime() - beginTime.getTime())/3600000);
        if(hour == 0) {
            ftime = Math.max((currentTime.getTime() - beginTime.getTime()) / 60000,1)+"分钟前";
        } else
            ftime = hour+"小时前";
        return ftime;
	}
	
	public static void handleRole(TextView role, String honor) {
		
		if (TextUtils.isEmpty(honor)) {
			role.setText("");
			return;
		}
		SpannableStringBuilder roleBuilder = new SpannableStringBuilder();
		roleBuilder.append(" (");
		roleBuilder.append(honor);
		roleBuilder.append(")");
		role.setText(roleBuilder);
	}
	
	/**
	 * 桌面显示红点
	 * @param context
	 * @param badgeCount
	 */
	public static void showBadge(Context context, int badgeCount){
		int count = SPHelper.getInst().getInt(SPHelper.BADGE_KEY_NEW_ARTICLE_COUNT);
		count += badgeCount;
        boolean success = ShortcutBadger.applyCount(context, count);
        if (success && badgeCount != 0) {
        	SPHelper.getInst().saveInt(SPHelper.BADGE_KEY_NEW_ARTICLE_COUNT,count);
        }
	}
	
	/**
	 * 桌面取消红点
	 * @param context
	 * @param badgeCount
	 */
	public static void cancleBadge(Context context){
		if (context == null || SPHelper.getInst().getInt(SPHelper.BADGE_KEY_NEW_ARTICLE_COUNT) == 0) {
			return;
		}
        ShortcutBadger.removeCount(context);
        
        SPHelper.getInst().saveInt(SPHelper.BADGE_KEY_NEW_ARTICLE_COUNT, 0);
        
	}
	
	public static String formatTimestmpByJs(long timestmp){
		String str = null;
		if(JsRhino.getInst().hasEvaluateJs()){
			// 已经解析过js，调用js函数
			str = JsRhino.getInst().runFunction("getdateformat", new String[]{String.valueOf(timestmp)});
		}else{
			String js = SPHelper.getInst().getString(SPHelper.KEY_DATEFORMAT);
			if(TextUtils.isEmpty(js)){
				// 无js，默认格式
				str = formatTimeHoursMinutesBefore(timestmp);
			}else{
				// 解析js
				JsRhino.getInst().evaluateJs(js, App.getInst().getApplicationContext());
				str = JsRhino.getInst().runFunction("getdateformat", new Object[]{timestmp});
			}
		}
		return str;
	}
	
	public static String getDateMMDDHHMMNotNullWithYYMMDDHHMMSS(String dateStr){
        if(TextUtils.isEmpty(dateStr)){
            return dateStr;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date now = new Date();
            Date date = sdf.parse(dateStr);
            String curYear = new SimpleDateFormat("yyyy").format(now);
            String year = new SimpleDateFormat("yyyy").format(date);
            String curDay = new SimpleDateFormat("yyyy MM-dd").format(now);
            String day = new SimpleDateFormat("yyyy MM-dd").format(date);
            if (curYear.equals(year)) {
                 if(curDay.equals(day)) {
                    return new SimpleDateFormat("HH:mm").format(date);
                }else{
                    return new SimpleDateFormat("MM-dd").format(date);
                }
            } else {
                return new SimpleDateFormat("yyyy-MM-dd").format(date);
            }
            
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr;
        }
    }
	
	
	private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
	private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
	private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

	/***
	 * 判断是不是miui系统
	 * @return
	 */
	public static boolean isMIUI() {
		try {
			final BuildProperties prop = BuildProperties.newInstance();
			return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
					|| prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
					|| prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
		} catch (final IOException e) {
			return false;
		}

    }
	
	/**
	 * 过滤输入法表情
	 * @param et
	 */
	public static void setProhibitEmoji(EditText et) {  
	    InputFilter[] filters = { getInputFilterProhibitEmoji() };  
	    et.setFilters(filters);  
	} 
	
	public static InputFilter getInputFilterProhibitEmoji() {  
	    InputFilter filter = new InputFilter() {  
	        @Override  
	        public CharSequence filter(CharSequence source, int start, int end,  
	                                   Spanned dest, int dstart, int dend) {  
	            StringBuffer buffer = new StringBuffer();  
	            for (int i = start; i < end; i++) {  
	                char codePoint = source.charAt(i);  
	                if (!getIsEmoji(codePoint)) {  
	                    buffer.append(codePoint);  
	                } else {  
	                    i++;  
	                    continue;  
	                }  
	            }  
	            if (source instanceof Spanned) {  
	                SpannableString sp = new SpannableString(buffer);  
	                TextUtils.copySpansFrom((Spanned) source, start, end, null,  
	                        sp, 0);  
	                return sp;  
	            } else {  
	                return buffer;  
	            }  
	        }  
	    };  
	  
	    return filter;  
	}  
	
	public static boolean getIsEmoji(char codePoint) {  
	    if ((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)  
	            || (codePoint == 0xD)  
	            || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))  
	            || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))  
	            || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)))  
	        return false;  
	    return true;  
	} 
	
	public static String repalcePushEmoji(String str) {
		Matcher emotionMatcher = Pattern.compile(EmojiTextView.EMOJI_COMPILE_STR).matcher(str);
		try {
			while (emotionMatcher.find()) {
				
				String key = emotionMatcher.group(0);
	
				str = str.replace(key, "[表情]");
			}
		} catch(Exception e) {
		}
		return str;
	}
	
}
