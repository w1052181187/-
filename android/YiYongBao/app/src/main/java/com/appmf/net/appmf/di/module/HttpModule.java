package com.appmf.net.appmf.di.module;

import com.appmf.net.appmf.core.http.api.GeeksApis;
import com.appmf.net.appmf.di.qualifier.AppmfUrl;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author wyg
 * @date 2018/11/28
 */

@Module
public class HttpModule {

    @Singleton
    @Provides
    GeeksApis provideGeeksApi(@AppmfUrl Retrofit retrofit) {
        return retrofit.create(GeeksApis.class);
    }

    @Singleton
    @Provides
    @AppmfUrl
    Retrofit provideGeeksRetrofit(Retrofit.Builder builder, OkHttpClient client) {
        return createRetrofit(builder, client, GeeksApis.HOST);
    }

    @Singleton
    @Provides
    Retrofit.Builder provideRetrofitBuilder() {
        return new Retrofit.Builder();
    }

    @Singleton
    @Provides
    OkHttpClient.Builder provideOkHttpBuilder() {
        return new OkHttpClient.Builder();
    }

    @Singleton
    @Provides
    OkHttpClient provideClient(OkHttpClient.Builder builder) {
//        if (BuildConfig.DEBUG) {
//            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
//            builder.addInterceptor(loggingInterceptor);
//        }
//        File cacheFile = new File(Constants.PATH_CACHE);
//        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
//        Interceptor cacheInterceptor = chain -> {
//            Request request = chain.request();
//            if (!CommonUtils.isNetworkConnected()) {
//                request = request.newBuilder()
//                        .cacheControl(CacheControl.FORCE_CACHE)
//                        .build();
//            }
//            Response response = chain.proceed(request);
//            if (CommonUtils.isNetworkConnected()) {
//                int maxAge = 0;
//                // 有网络时, 不缓存, 最大保存时长为0
//                response.newBuilder()
//                        .header("Cache-Control", "public, max-age=" + maxAge)
//                        .removeHeader("Pragma")
//                        .build();
//            } else {
//                // 无网络时，设置超时为4周
//                int maxStale = 60 * 60 * 24 * 28;
//                response.newBuilder()
//                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
//                        .removeHeader("Pragma")
//                        .build();
//            }
//            return response;
//        };
//        //设置缓存
//        builder.addNetworkInterceptor(cacheInterceptor);
//        builder.addInterceptor(cacheInterceptor);
//        builder.cache(cache);
        //设置超时
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);
        //cookie认证
//        builder.cookieJar(new CookiesManager());
        return builder.build();
    }

    private Retrofit createRetrofit(Retrofit.Builder builder, OkHttpClient client, String url) {
        return builder
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
