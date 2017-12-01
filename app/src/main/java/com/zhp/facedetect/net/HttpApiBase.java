package com.zhp.facedetect.net;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.being.base.http.AsyncOkHttp;
import com.being.base.http.HttpManager;
import com.being.base.http.TrustUtils;
import com.being.base.http.callback.DownloadCallback;
import com.being.base.http.intercept.HttpLogInterceptorCreator;
import com.being.base.http.intercept.LoadCacheInterceptor;
import com.being.base.http.intercept.RewriteCacheHeaderInterceptor;
import com.being.base.http.retrofit.RetrofitManager;
import com.being.base.http.retrofit.calladapter.CompactCallAdapterFactory;
import com.being.base.http.security.NHHostNameVerifier;
import com.being.base.log.NHLog;
import com.being.base.utils.AuthUtils;
import com.being.base.utils.StorageUtils;
import com.zhp.facedetect.BuildConfig;
import com.zhp.facedetect.youtu.sign.YoutuSign;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Http管理类
 * Created by zhangpeng on 16/1/5.
 */
public class HttpApiBase {

    public static String getSecureBaseUrl() {
        return "http://api.youtu.qq.com/youtu/api/";
    }

//    public static String getBaseUrl() {
//        return "http://" + IPS[HOST];
//    }

    public static void init(Application context) {
        Cache cache = new Cache(StorageUtils.getOwnCacheDirectory(context, "net"), 50 * 1024 * 1024);
        AsyncOkHttp asyncOkHttp = new AsyncOkHttp();
        asyncOkHttp.setCacheDir(cache);
        asyncOkHttp.addInterceptor(new RequestInterceptor());
        if (BuildConfig.DEBUG) {
            asyncOkHttp.addInterceptor(HttpLogInterceptorCreator.create());
        }
        //for retrofit
        if (BuildConfig.DEBUG) {
            RetrofitManager.enableDebug();
        }
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(getSecureBaseUrl());
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.addCallAdapterFactory(new CompactCallAdapterFactory());
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync());

        RetrofitManager.get().initRetrofit(builder, asyncOkHttp.getOkHttpClient());
    }

    private static class RequestInterceptor implements Interceptor {

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();
            HttpUrl httpUrl = request.url();
            Request.Builder requestBuilder = request.newBuilder();
            requestBuilder.url(httpUrl);
            long timeStamp = System.currentTimeMillis() / 1000 + 120;
            StringBuffer signStr = new StringBuffer();
            int result = YoutuSign.appSign(Config.app_id,
                    Config.secret_id,
                    Config.secret_key,
                    timeStamp,
                    "",
                    signStr);
            requestBuilder.addHeader("Authorization", signStr.toString());
            Request finalRequest = requestBuilder.build();
            return chain.proceed(finalRequest);
        }
    }

}
