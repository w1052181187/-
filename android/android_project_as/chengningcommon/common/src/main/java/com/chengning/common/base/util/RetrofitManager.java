package com.chengning.common.base.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.chengning.common.base.BaseResponseBean;
import com.chengning.common.base.CookieBean;
import com.chengning.common.base.MyRetrofitResponseCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.SerializableCookie;

import org.apache.http.cookie.Cookie;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    public static final String COOKIE_PREF = "cookies_prefs";
    public static final int SP_MODE = Context.MODE_PRIVATE;

    private static RetrofitManager mRetrofitManager;
    private Retrofit mRetrofit;
    private Retrofit.Builder retrofitBuilder;
    private OkHttpClient.Builder clientBuilder;

    public OkHttpClient.Builder getClientBuilder() {
        return clientBuilder;
    }

    public void initRetrofit(Context context, String baseUrl, Interceptor... interceptors) {
        clientBuilder = getDefaultClientBuilder();
        clientBuilder.cookieJar(new CookieManager(context));
        for (Interceptor interceptor : interceptors) {
            clientBuilder.addInterceptor(interceptor);
        }
        retrofitBuilder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(clientBuilder.build());
        mRetrofit = retrofitBuilder.build();
    }

    public void setRetrofit(Retrofit mRetrofit) {
        this.mRetrofit = mRetrofit;
    }

    public Retrofit.Builder getRetrofitBuilder() {
        return retrofitBuilder;
    }

    public <T> T createService(Class<T> reqServer){
        return mRetrofit.create(reqServer);
    }

    public static RetrofitManager getInst() {
        if( mRetrofitManager == null){
            synchronized (RetrofitManager.class) {
                if( mRetrofitManager == null){
                    mRetrofitManager = new RetrofitManager();
                }
            }
        }
        return  mRetrofitManager;
    }

    public OkHttpClient.Builder getDefaultClientBuilder() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.readTimeout(10, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(10, TimeUnit.SECONDS);
        httpClientBuilder.connectTimeout(10, TimeUnit.SECONDS);
        httpClientBuilder.retryOnConnectionFailure(true);
        return httpClientBuilder;
    }

    public static void subcribe(Observable<BaseResponseBean> observable, MyRetrofitResponseCallback callback) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback);
    }

    public static class HeaderInterceptor implements Interceptor {

        private HashMap<String, String> headers;

        public HeaderInterceptor(HashMap<String, String> headers) {
            this.headers = headers;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder request = chain.request()
                    .newBuilder();
            Set<String> set = headers.keySet();
            for (String  key : set) {
                request.removeHeader(key);
                request.addHeader(key, headers.get(key));
            }
            return chain.proceed(request.build());
        }
    }

    public void clearCookies(Context context) {
        SharedPreferences sp = context.getSharedPreferences(COOKIE_PREF, SP_MODE);
        clearSharedPreferences(sp);

        //asynchttp 网络请求框架的cookie
        SharedPreferences cookiePrefs = context.getSharedPreferences("CookiePrefsFile", 0);
        clearSharedPreferences(cookiePrefs);
    }

    public void clearSharedPreferences(SharedPreferences sp) {
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().apply();
    }

    public static class CookieManager implements CookieJar {

        private Context context;
        private SharedPreferences sp;
        private List<okhttp3.Cookie> cookies;

        public CookieManager(Context context) {
            this.context = context;
            initSp(context);
        }

        private void initSp(Context context) {
            sp = context.getSharedPreferences(COOKIE_PREF, SP_MODE);
        }

        @Override
        public void saveFromResponse(HttpUrl url, List<okhttp3.Cookie> cookies) {
            saveCookies(url.toString(), url.host(), cookies);
        }

        public void saveCookies(final String url, final String host, List<okhttp3.Cookie> cookies) {
            List<okhttp3.Cookie> newSavedCookies = new ArrayList<>();
            List<okhttp3.Cookie> oldCookies = new ArrayList<>();
            if (BaseCommon.isListEmpty(cookies)) {
                return;
            }
            if (!BaseCommon.isListEmpty(this.cookies)) {
                newSavedCookies.addAll(this.cookies);
            }
            boolean isChanged = false;
            for (okhttp3.Cookie cookie : cookies) {
                if (!TextUtils.equals(cookie.value(), "deleted")) {
                    oldCookies.clear();
                    for (okhttp3.Cookie saveCookie : newSavedCookies) {
                        if (TextUtils.equals(saveCookie.name(), cookie.name())) {
                            oldCookies.add(saveCookie);
                        }
                    }
                    newSavedCookies.removeAll(oldCookies);
                    newSavedCookies.add(cookie);
                    isChanged = true;
                }
            }

            if (BaseCommon.isListEmpty(newSavedCookies) || !isChanged) {
                return;
            }

//            for (okhttp3.Cookie cookie : saveCookies) {
//                CookieBean bean = new CookieBean();
//                bean.setName(cookie.name());
//                bean.setValue(cookie.value());
//                bean.setDomain(cookie.domain());
//                bean.setExpiresAt(cookie.expiresAt());
//                list.add(bean);
//            }

            Gson gson = new Gson();
            String cookieStr = gson.toJson(newSavedCookies);

            SharedPreferences.Editor editor = sp.edit();

            if (!TextUtils.isEmpty(host)) {
                editor.putString(host, cookieStr);
            }
            editor.commit();
        }

        @Override
        public List<okhttp3.Cookie> loadForRequest(HttpUrl url) {
            this.cookies = getCookies(url.toString(),url.host());
            return cookies;
        }

        public List<okhttp3.Cookie> getCookies(String url, String host) {
            String decodeCookies = "";
            List<okhttp3.Cookie> cookies;
            decodeCookies = sp.getString(host, "");
            cookies = handleCookie(decodeCookies);
            return cookies;
        }

        List<okhttp3.Cookie> handleCookie(String decodeCookies) {
            String cookie = decodeCookies;
            ArrayList<okhttp3.Cookie> cookies = new Gson().fromJson(cookie,new TypeToken<ArrayList<okhttp3.Cookie>>(){}.getType());
//            ArrayList<CookieBean> list = new ArrayList<>();
//            list = new Gson().fromJson(cookie,new TypeToken<ArrayList<CookieBean>>(){}.getType());
//            if (!BaseCommon.isListEmpty(list)) {
//                for (CookieBean bean : list) {
//                    okhttp3.Cookie.Builder builder = new okhttp3.Cookie.Builder();
//                    builder.domain(bean.getDomain());
//                    builder.name(bean.getName());
//                    builder.value(bean.getValue());
//                    builder.expiresAt(bean.getExpiresAt());
//                    cookies.add(builder.build());
//                }
//            }
            return cookies != null
                    ? Collections.unmodifiableList(cookies)
                    : Collections.<okhttp3.Cookie>emptyList();
        }
    }

    //********* asynchttp 网络请求框架的cookie ***************
    public ArrayList<CookieBean> getAsyncHttpCookies(Context context, String host) {
        SharedPreferences cookiePrefs = context.getSharedPreferences("CookiePrefsFile", 0);
        ArrayList<CookieBean> cookies = new ArrayList<CookieBean>();
        String storedCookieNames = cookiePrefs.getString("names", (String) null);
        if (storedCookieNames != null) {
            String[] cookieNames = TextUtils.split(storedCookieNames, ",");
            String[] arr$ = cookieNames;
            int len$ = cookieNames.length;
            Date expiryDate;
            for (int i$ = 0; i$ < len$; ++i$) {
                String name = arr$[i$];
                String encodedCookie = cookiePrefs.getString("cookie_" + name, (String) null);
                if (encodedCookie != null) {
                    Cookie decodedCookie = decodeCookie(encodedCookie);
                    if (decodedCookie != null && BaseCommon.domainMatch(host, decodedCookie.getDomain())) {
                        CookieBean bean = new CookieBean();
                        bean.setDomain(decodedCookie.getDomain());
                        bean.setName(decodedCookie.getName());
                        bean.setValue(decodedCookie.getValue());
                        expiryDate = decodedCookie.getExpiryDate();
                        if (expiryDate != null) {
                            long time = expiryDate.getTime();
                            bean.setExpiresAt(expiryDate.getTime());
                        }
                        cookies.add(bean);
                    }
                }
            }
        }
        return cookies;
    }

    protected static Cookie decodeCookie(String cookieString) {
        byte[] bytes = hexStringToByteArray(cookieString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Cookie cookie = null;

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            cookie = ((SerializableCookie)objectInputStream.readObject()).getCookie();
        } catch (IOException var6) {
            Log.d("PersistentCookieStore", "IOException in decodeCookie", var6);
        } catch (ClassNotFoundException var7) {
            Log.d("PersistentCookieStore", "ClassNotFoundException in decodeCookie", var7);
        }

        return cookie;
    }

    protected static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for(int i = 0; i < len; i += 2) {
            data[i / 2] = (byte)((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
    //********* asynchttp 网络请求框架的cookie ***************

}
