package net.yiersan.table.api;


import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaydenxiao.common.BuildConfig;
import com.jaydenxiao.common.baseapp.BaseApplication;
import com.jaydenxiao.common.commonutils.LogUtils;
import com.jaydenxiao.common.commonutils.NetWorkUtils;
import com.jaydenxiao.common.commonutils.Util;

import net.yiersan.table.app.AppApplication;
import net.yiersan.table.utils.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * des:retorfit api
 * Created by xsf
 * on 2016.06.15:47
 */
public class Api {

    public final static String PARAM_ANDROID = "device";
    public final static String PARAM_VERSION = "version";
    public final static String PARAM_APPNAME = "appName";
    public final static String PARAM_DEVICEID = "deviceId";
    public final static String PARAM_ADMINID = "adminId";
    public final static String PARAM_ADMINNAME = "adminName";

    public final static String HEADER_TOKEN = "token";
    public final static String HEADER_UTOKEN = "utoken";



    //读超时长，单位：毫秒
    public static final int READ_TIME_OUT = 10000;
    //连接时长，单位：毫秒
    public static final int CONNECT_TIME_OUT = 10000;
    public Retrofit retrofit;
    public ApiService movieService;
    public OkHttpClient okHttpClient;
    private static SparseArray<Api> sRetrofitManager = new SparseArray<>(HostType.TYPE_COUNT);

    /*************************缓存设置*********************/
/*
   1. noCache 不使用缓存，全部走网络

    2. noStore 不使用缓存，也不存储缓存

    3. onlyIfCached 只使用缓存

    4. maxAge 设置最大失效时间，失效则不使用 需要服务器配合

    5. maxStale 设置最大失效时间，失效则不使用 需要服务器配合 感觉这两个类似 还没怎么弄清楚，清楚的同学欢迎留言

    6. minFresh 设置有效时间，依旧如上

    7. FORCE_NETWORK 只走网络

    8. FORCE_CACHE 只走缓存*/

    /**
     * 设缓存有效期为两天
     */
    private static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;
    /**
     * 查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
     * max-stale 指示客户机可以接收超出超时期间的响应消息。如果指定max-stale消息的值，那么客户机可接收超出超时期指定值之内的响应消息。
     */
    private static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    /**
     * 查询网络的Cache-Control设置，头部Cache-Control设为max-age=0
     * (假如请求了服务器并在a时刻返回响应结果，则在max-age规定的秒数内，浏览器将不会发送对应的请求到服务器，数据由缓存直接返回)时则不会使用缓存而请求服务器
     */
    private static final String CACHE_CONTROL_AGE = "max-age=0";


    //构造方法私有
    private Api(int hostType) {
        //开启Log
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //缓存
        File cacheDir = BaseApplication.getAppContext().getCacheDir();
        File cacheFile = new File(cacheDir, "cache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb
        //增加头部信息
        Interceptor headerInterceptor =new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request build = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .build();
                return chain.proceed(build);
            }
        };

        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(READ_TIME_OUT, TimeUnit.MILLISECONDS)
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.MILLISECONDS)
                .addInterceptor(mRewriteCacheControlInterceptor)
                .addNetworkInterceptor(mRewriteCacheControlInterceptor)
                .addInterceptor(headerInterceptor)
                .addInterceptor(logInterceptor)
                .cache(cache)
                .build();

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").serializeNulls().create();
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(ApiConstants.getHost(hostType))
                .build();
        movieService = retrofit.create(ApiService.class);
    }


    /**
     * @param hostType
     */
    public static ApiService getDefault(int hostType) {
        Api retrofitManager = sRetrofitManager.get(hostType);
        if (retrofitManager == null) {
            retrofitManager = new Api(hostType);
            sRetrofitManager.put(hostType, retrofitManager);
        }
        return retrofitManager.movieService;
    }


    /**
     * 根据网络状况获取缓存的策略
     */
    @NonNull
    public static String getCacheControl() {
        return NetWorkUtils.isNetConnected(BaseApplication.getAppContext()) ? CACHE_CONTROL_AGE : CACHE_CONTROL_CACHE;
    }

    /**
     * 云端响应头拦截器，用来配置缓存策略
     * Dangerous interceptor that rewrites the server's cache-control header.
     */
    private final Interceptor mRewriteCacheControlInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String cacheControl = request.cacheControl().toString();
            if (!NetWorkUtils.isNetConnected(BaseApplication.getAppContext())) {
                request = request.newBuilder()
                        .cacheControl(TextUtils.isEmpty(cacheControl)?CacheControl.FORCE_NETWORK:CacheControl.FORCE_CACHE)
                        .build();
            }
            Response originalResponse = chain.proceed(request);
            if (NetWorkUtils.isNetConnected(BaseApplication.getAppContext())) {
                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置

                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + CACHE_STALE_SEC)
                        .removeHeader("Pragma")
                        .build();
            }
        }
    };




    private Interceptor mLogInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request.Builder originalBuilder = request.newBuilder();
            try {
                if(BuildConfig.DEBUG) {
                    Log.w("RetrofitHelper", request.method()+"=="+request.url().toString());
                }
                return chain.proceed(originalBuilder.build());
            }catch (UnknownHostException e){
                throw new TimeOutException(e,request.url().toString());
            }catch (ConnectException e){
                throw new TimeOutException(e,request.url().toString());
            }
        }
    };


    private Interceptor mParamInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request.Builder originalBuilder = originalRequest.newBuilder();
            if("GET".equals(originalRequest.method())) {
                intoCommonParamGET(originalRequest,originalBuilder);
            }else{
                intoCommonParamPOST(originalRequest,originalBuilder);
            }
            return chain.proceed(originalBuilder.build());
        }

        /**
         * GET 请求插入公共参数
         * @param request
         * @param builder
         */
        private void intoCommonParamGET(Request request,Request.Builder builder){
            HttpUrl.Builder httpUrlBuilder = request.url().newBuilder();
            httpUrlBuilder.addQueryParameter(PARAM_ANDROID, "Android")
                    .addQueryParameter(PARAM_VERSION, Util.getVersionName(AppApplication.getAppContext()))
                    .addQueryParameter(PARAM_APPNAME, "yi23")
                    .addQueryParameter(PARAM_DEVICEID, Util.getIMEI(AppApplication.getAppContext()));
            if (ApiConstants.getInstacne().isLogin()) {
                httpUrlBuilder.addQueryParameter(PARAM_ADMINID, ApiConstants.getInstacne().getAdminid());
                httpUrlBuilder.addQueryParameter(PARAM_ADMINNAME, ApiConstants.getInstacne().getUsername());
            }
            builder.url(httpUrlBuilder.build());
        }

        /**
         * BODY插入公共参数
         * @param request
         * @param builder
         */
        private void intoCommonParamPOST(Request request,Request.Builder builder){
            FormBody.Builder newBody = new FormBody.Builder();
            newBody.add(PARAM_ANDROID,"Android");
            newBody.add(PARAM_VERSION,Util.getVersionName(AppApplication.getAppContext()));
            newBody.add(PARAM_APPNAME,"yi23");
            newBody.add(PARAM_DEVICEID,Util.getIMEI(AppApplication.getAppContext()));
            if (ApiConstants.getInstacne().isLogin()) {
                newBody.add(PARAM_ADMINID, ApiConstants.getInstacne().getAdminid());
                newBody.add(PARAM_ADMINNAME, ApiConstants.getInstacne().getUsername());
            }

            StringBuffer sb = new StringBuffer();
            sb.append(bodyToString(newBody.build()));
            if(request.body() != null){
                sb.append("&");
                sb.append(bodyToString(request.body()));
            }
            if("PUT".equals(request.method())) {
                builder.put(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), sb.toString()));
            }else {
                MediaType mediaType = request.body().contentType();
                if (mediaType != null && "multipart".equals(mediaType.type())) {
                    return;
                } else {
                    builder.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), sb.toString()));
                }
            }
        }

        private String bodyToString(final RequestBody request){
            try {
                final RequestBody copy = request;
                final Buffer buffer = new Buffer();
                if(copy != null)
                    copy.writeTo(buffer);
                else
                    return "";
                return buffer.readUtf8();
            }
            catch (final IOException e) {
                return "did not work";
            }
        }
    };

    private class HttpLogger implements HttpLoggingInterceptor.Logger {
        private StringBuilder mMessage = new StringBuilder();

        @Override
        public void log(String message) {
            // 请求或者响应开始
            if (message.startsWith("--> POST")) {
                mMessage.setLength(0);
            }
            // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
            if ((message.startsWith("{") && message.endsWith("}"))
                    || (message.startsWith("[") && message.endsWith("]"))) {
                message = JsonUtil.formatJson(message);
            }
            mMessage.append(message.concat("\n"));
            // 请求或者响应结束，打印整条日志
            if (message.startsWith("<-- END HTTP")) {
                LogUtils.logd(mMessage.toString());
            }
        }
    }
}