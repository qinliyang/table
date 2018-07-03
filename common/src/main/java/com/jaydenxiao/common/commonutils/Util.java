package com.jaydenxiao.common.commonutils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wyf on 16/3/9.
 */
public class Util {

    private static final String DEVICE_ID_KEY = "DEVICE_ID_KEY";

    public static String getUUID(){
        return generateDeviceId();
    }

    private static long generateValue() {
        UUID uuid = UUID.randomUUID();
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        long v = msb ^ lsb;
        if (v < 0) {
            return -v;
        }
        return v;
    }

    private static String generateDeviceId() {
        String deviceId;
        //generate a new device id
        long[] randomID = {generateValue(), generateValue(), generateValue(), generateValue()};

        ByteBuffer b = ByteBuffer.allocate(32);
        for (int i = 0; i < randomID.length; i++) {
            b.putLong((i * 8), randomID[i]);
        }

        byte[] data = b.array();
        deviceId = Base64.encodeToString(data, Base64.NO_WRAP | Base64.URL_SAFE | Base64.NO_PADDING);
        return deviceId;
    }

    public static int dip2px(Context context, float dipValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, context.getResources().getDisplayMetrics());
    }

    public static int px2dp(Context c, int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, px, c.getResources().getDisplayMetrics());
    }


    public static int String2Int(String str){
        try{
            return Integer.parseInt(str);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public static void hideSoftInput(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()){
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void hideSoftInputManager(Activity mActivity) {
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()){
            imm.hideSoftInputFromWindow(mActivity.getWindow().getDecorView().getWindowToken(),0);
        }
    }

    public static String getSignType() {
        return "sign_type=\"RSA\"";
    }

    public static boolean notEmpty(List list){
        if(list == null || list.size() ==0){
            return false;
        }
        return true;
    }

    public static boolean notEmpty(JSONArray ary){
        if(ary == null || ary.length() ==0){
            return false;
        }
        return true;
    }

    public static String setTextCard(String str){
        if(TextUtils.isEmpty(str) || str.length() != 11){
            return str;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(str.substring(0,3));
        sb.append("-");
        sb.append(str.substring(3,7));
        sb.append("-");
        sb.append(str.substring(7,11));
        return sb.toString();
    }
    public static String getIMEI(Context context){
        try{
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if(manager != null){
                return manager.getDeviceId();
            }

        }catch (Exception e){

        }
        return "";
    }

    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
    //获取底部导航条高度
    public static int getNavigationBarHeight(Context mActivity) {
        Resources resources = mActivity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height","dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    //获取状态条高度
    public static int getStatusBarHeight(Context mActivity) {
        Resources resources = mActivity.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen","android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isActivity(Context context){
        if(context == null || !(context instanceof Activity) || ((Activity) context).isFinishing()){
            return false;
        }
        return true;
    }


    public static boolean isPhoneNumber(String str){
        Pattern p = Pattern.compile("^[1][0-9]{10}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean isIpPort(String str){
        Pattern p = Pattern.compile("^(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9]):\\d{0,5}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 打开已经安装好的apk
     */
    public static void openApk(Context context, String url) {
        PackageManager manager = context.getPackageManager();
        // 这里的是你下载好的文件路径
        PackageInfo info = manager.getPackageArchiveInfo(url, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            Intent intent = manager.getLaunchIntentForPackage(info.applicationInfo.packageName);
            context.startActivity(intent);
        }
    }

    public static boolean hasSDCard(){
        try {
            String status = Environment.getExternalStorageState();
            return status.equals(Environment.MEDIA_MOUNTED);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 电话号码验证
     *
     * @param  str
     * @return 验证通过返回true
     */
    public static boolean isPhone(String str) {
        Pattern p1 = null,p2 = null;
        Matcher m = null;
        boolean b = false;
        p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
        if(str.length() >9)
        {   m = p1.matcher(str);
            b = m.matches();
        }else{
            m = p2.matcher(str);
            b = m.matches();
        }
        return b;
    }

    /*
     检查apk是否已经安装
     */
    public static boolean isAppInstalled(Context context, String packageName){
        boolean installed = false;
        try{
            synchronized (context) {
                context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
                installed = true;
            }
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            installed = false;
        }
        return installed;
    }

    /*
        获取已安装apk的版本号
     */
    public static String getPackTime(Context context){
        try{
            synchronized (context){
               return context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.getString("RELEASE_TIME");
            }
        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return "2016-5-4 13:06:06";
    }

    /*
        获取已安装apk的版本号
     */
    public static int getAPKVersion(Context context, String packageName){
        PackageInfo info = null;
        try{
            synchronized (context){
                info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            }
            return info.versionCode;
        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return 0;
    }

    /*
        获取当前应用的版本号
     */
    public static int getVersion(Context context){
        PackageInfo info = null;
        try{
            synchronized (context){
                info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            }
            return info.versionCode;
        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    /*
        获取当前应用的版本号名字
     */
    public static String getVersionName(Context context){
        PackageInfo info = null;
        try{
            synchronized (context){
                info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            }
            return info.versionName;
        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "0";
    }

    /**
     * app是否在运行
     * @param ctx
     * @param packageName
     * @return
     */
    public static boolean isAppRunning(Context ctx, String packageName)
    {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();

        if(runningAppProcesses!=null)
        {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {

                if(runningAppProcessInfo.processName.startsWith(packageName))
                {
                    return true;
                }
            }
        }

        return false;
    }




    public final static String getMD5(String s){
        char hexDigits[] = { '0', '1', '2', '3', '4',
                '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F' };
        try{
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);

            byte[] md = mdInst.digest();

            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for(int i = 0; i < j; i++){
                byte temp = md[i];
                str[k++] = hexDigits[temp >>> 4 & 0xf];
                str[k++] = hexDigits[temp & 0xf];
            }
            return new String(str);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isCardID(String id) {
        if (id.length() != 18)
            return false;
        /*
         * 1、将前面的身份证号码17位数分别乘以不同的 系数。 从第一位到第十七位的系数分别为：
         * 7－9－10－5－8－4－2－1－6－3－7－9－10－5－8－4－2。 将这17位数字和系数相乘的结果相加。
         */
        int[] w = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
        int sum = 0;
        for (int i = 0; i < w.length; i++) {
            sum += (id.charAt(i) - '0') * w[i];
        }
        // 用加出来和除以11，看余数是多少？
        char[] ch = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };
        // return ch[sum%11]==
        // (id.charAt(17)=='x'?'X': id.charAt(17));
        int c = sum % 11;
        /*
         * 分别对应的最后 一位身份证的号码为 1－0－X－9－8－7－6－5－4－3－2。
         */
        char code = ch[c];
        char last = id.charAt(17);
        last = last == 'x' ? 'X' : last;
        return last == code;
    }

    public static Bitmap getScreenshot(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

    /**
     * 获取本机ip
     * @return
     */
    public static String getIP(Context application) {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) application.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                            return inetAddress.getHostAddress().toString();
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        } else {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String ip = intToIp(ipAddress);
            return ip;
        }
        return "127.0.0.1";
    }

    public static String getRealPathFromURI(Context context, Uri uri) {
        Cursor cursor = null;
        if (uri.getScheme().compareTo("content") == 0) {
            try {
                String[] proj = { MediaStore.Images.Media.DATA };
                cursor = context.getContentResolver().query(uri,  proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (uri.getScheme().compareTo("file") == 0) {
            return new File(uri.getPath()).getAbsolutePath();
        }
        return null;
    }


    private static String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

}
