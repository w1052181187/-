package com.shenyuan.militarynews.ad;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;

import com.chengning.common.base.util.BaseCommon;
import com.shenyuan.militarynews.ad.AdDataDummy.BaiduNativeNetworkDummyListener;
import com.shenyuan.militarynews.utils.Common;

public class AdDataTengxunDummy {
	
	private static boolean isTengxunAdJarExists = true;
	
	public static void getDataBySDKDummy(final Context context, final String adPlaceId, final BaiduNativeNetworkDummyListener listener){
//		// old code begin
//		//初始化并加载广告
//		public void loadAD() {
//		    if (nativeAD == null) {
//		NativeAdListener listener2 = new NativeAdListener() ;
//		      this.nativeAD = new NativeAD(this, Constants.APPID, Constants.NativePosID, this);
//		    }
//		    int count = 1; // 一次拉取的广告条数：范围1-10
//		    nativeAD.loadAD(count);
//		}
//		/**
//		  * 展示原生广告时，一定要先调用onExposured接口曝光广告，否则将无法调用onClicked点击接口
//		  */
//		public void showAD() {
//		$.id(R.id.img_logo).image((String) adItem.getIconUrl(), false, true);
//		$.id(R.id.img_poster).image(adItem.getImgUrl(), false, true);
//		$.id(R.id.text_name).text((String) adItem.getTitle());
//		$.id(R.id.text_desc).text((String) adItem.getDesc());
//		$.id(R.id.btn_download).text(getADButtonText());
//		adItem.onExposured(this.findViewById(R.id.nativeADContainer));
//		$.id(R.id.btn_download).clicked(new OnClickListener() {
//		          @Override
//		          public void onClick(View view) {
//		            adItem.onClicked(view);
//		          }
//		    });
//		}
//
//		//Update 广告状态
//		@Override
//		public void onADStatusChanged(NativeADDataRef arg0) {
//		    $.id(R.id.btn_download).text(getADButtonText());
//		}
//		// old code end
		if(!isTengxunAdJarExists){
			return;
		}
		Class<?> tnClass = null;
		try {
			tnClass = Class.forName("com.qq.e.ads.nativ.NativeAD");
		} catch (Exception e) {
			e.printStackTrace();
			isTengxunAdJarExists = false;
			return;
		}
		try {
			// NativeAdListener
			Class<?> tnnlClass = Class.forName("com.qq.e.ads.nativ.NativeAD$NativeAdListener");
			TengxunNativeNetworkReflectListener tnnrListener = new TengxunNativeNetworkReflectListener(listener);
			Object tnnrlInst = Proxy.newProxyInstance(tnClass.getClassLoader(), new Class[]{tnnlClass}, tnnrListener);
			Object tnInst = tnClass.getDeclaredConstructors()[0].newInstance(context,
					BaseCommon.getAppMetaData(context, "CHENGNING_GDT_APPID"), adPlaceId, tnnrlInst);
			// makeRequest
			Method[] methods = tnClass.getMethods();
			for(Method m : methods){
				if("loadAD".equals(m.getName()) 
						&& m.getParameterTypes() != null 
						&& m.getParameterTypes().length == 1){
					// 一次拉取的广告条数：范围1-10
					int count = 10; 
					m.invoke(tnInst, count);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static class TengxunNativeNetworkReflectListener implements InvocationHandler{
		private BaiduNativeNetworkDummyListener listener;
		
		TengxunNativeNetworkReflectListener(BaiduNativeNetworkDummyListener listener){
			this.listener = listener;
		}

		@Override
		public Object invoke(Object obj, Method method, Object[] aobj) throws Throwable {
			if("onADLoaded".equals(method.getName())){
				if(aobj != null && aobj[0] != null && List.class.isInstance(aobj[0])){
					List list = (List) aobj[0];
					ArrayList<AdDataDummy.NativeResponseDummy> dummyList = new ArrayList<AdDataDummy.NativeResponseDummy>();
					if(!Common.isListEmpty(list)){
						for(Object lo : list){
							NativeResponseDummy nd = new NativeResponseDummy(lo);
							dummyList.add(nd);
						}
					}
					listener.onNativeLoad(dummyList);
				}
			}
			return null;
		}
		
	}
	
	/**
//	isAPP()	返回是否为APP广告
//	getAPPScore()	获取应用评级
//	getAPPStatus()	获取应用状态，0：未开始下载；1：已安装；2：需要更新;4:下载中;8:下载完成;16:下载失败
//	getTitle()	标题，短文字,14字以内
//	getDesc()	描述，长文字,30字以内
//	getIconUrl()	获取Icon图片地址
//	getImgUrl()	获取大图地址
//	equals(Object obj)	是否为同一个广告对象，在广告的状态改变回调onADStatusChanged中，可以用这个方法来判断返回的对象是否为当前正在展示的广告对象。
//	equalsAdData(NativeADDataRef adDataRef)	是否为同一条广告素材，如果返回true，说明两个广告对象中包含的大图、小图、文字等素材是相同的。注意：即使两个不同广告对象的素材相同，但它们仍然是两条可以分别曝光、点击、计费的广告。
//	onClicked(View v)	广告点击时调用，v为被点击的view组件，注意此接口必须在调用onExposured接口后再调用
//	onExposured(View v)	广告曝光时调用，v为展示广告的组件
//	getDownloadCount()	获取APP类广告的下载数
//	getProgress()	获取APP类广告下载中的下载进度
//	getAPPPrice()
	*/
	public static class NativeResponseDummy extends AdDataDummy.NativeResponseDummy{
		private Object adBean;
		
		public NativeResponseDummy(Object adBean){
			super(adBean);
			this.adBean = adBean;
		}
		
		public void recordImpression(View view){
//			adBean.recordImpression(view);
			try {
				adBean.getClass().getMethod("onExposured", View.class).invoke(adBean, view);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void handleClick(View view){
//			adBean.handleClick(view);
			try {
				adBean.getClass().getMethod("onClicked", View.class).invoke(adBean, view);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//		public abstract void handleClick(View view, int i);

		public String getTitle(){
//			return adBean.getTitle();
			return invokeMethod(adBean, "getDesc", String.class);
		}

		public String getDesc(){
//			return adBean.getDesc();
			return invokeMethod(adBean, "getTitle", String.class);
		}

		public String getIconUrl(){
//			return adBean.getIconUrl();
			return invokeMethod(adBean, "getIconUrl", String.class);
		}

		public String getImageUrl(){
//			return adBean.getImageUrl();
			return invokeMethod(adBean, "getImgUrl", String.class);
		}
//
		public List<String> getMultiPicUrls(){
//			return adBean.getMultiPicUrls();
			return invokeMethod(adBean, "getImgList", List.class);
		}

		public int getAdPatternType(){
//			return adBean.getImageUrl();
//			public static final int NATIVE_2IMAGE_2TEXT = 1;
//			public static final int NATIVE_VIDEO = 2;
//			public static final int NATIVE_3IMAGE = 3;
			return invokeMethod(adBean, "getAdPatternType", Integer.class);
		}

	}
	
	private static <T> T invokeMethod(Object obj, String methodName, Class<T> c){
		try {
			Object ret = obj.getClass().getMethod(methodName).invoke(obj);
			if(ret != null && c.isInstance(ret)){
				return (T) ret;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
