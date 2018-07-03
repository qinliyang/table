package com.jaydenxiao.common.commonutils;


import com.jaydenxiao.common.baseapp.AppConfig;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

/**
 */
public class LogUtils {
    /**
     * 在application调用初始化
     */
    public static void logInit(boolean debug) {
        if (debug) {
            Logger.init(AppConfig.DEBUG_TAG)                 // default PRETTYLOGGER or use just init()
                    .hideThreadInfo()                // default 2
                    .logLevel(LogLevel.FULL)        // default LogLevel.FULL
                    .methodOffset(2);                // default 0
        } else {
            Logger.init()                 // default PRETTYLOGGER or use just init()
                    .methodCount(3)                 // default 2
                    .hideThreadInfo()               // default shown
                    .logLevel(LogLevel.NONE)        // default LogLevel.FULL
                    .methodOffset(2);
        }
    }
    public static void logd(String tag,String message) {
            Logger.d(tag,message);
    }
    public static void logd(String message) {
            Logger.d(message);
    }
    public static void loge(Throwable throwable, String message, Object... args) {
            Logger.e(throwable, message, args);
    }

    public static void loge(String message, Object... args) {
            Logger.e(message, args);
    }

    public static void logi(String message, Object... args) {
            Logger.i(message, args);
    }
    public static void logv(String message, Object... args) {
            Logger.v(message, args);
    }
    public static void logw(String message, Object... args) {
            Logger.v(message, args);
    }
    public static void logwtf(String message, Object... args) {
            Logger.wtf(message, args);
    }

    public static void logjson(String message) {
            Logger.json(message);
    }
    public static void logxml(String message) {
            Logger.xml(message);
    }
}
