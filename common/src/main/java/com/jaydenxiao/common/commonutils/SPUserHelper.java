package com.jaydenxiao.common.commonutils;

import android.content.Context;
import android.content.SharedPreferences;



/**
 * Created by wyf on 16/3/18.
 */
public class SPUserHelper {
    private static final String SHARED_PATH = "user";
    private static SPUserHelper instance;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public static SPUserHelper getInstance(Context context) {
        if (instance == null && context != null) {
            instance = new SPUserHelper(context);
        }
        return instance;
    }


    private SPUserHelper(Context context) {
        sp = context.getSharedPreferences(SHARED_PATH, Context.MODE_PRIVATE);
        editor = sp.edit();
    }


    public void clearData(){
        sp.edit().clear().commit();
    }

//    public void SaveUser(UserBean userBean){
//        if(userBean == null)return;
//        editor = sp.edit();
//        editor.putString("adminid",userBean.adminid);
//        editor.putString("username",userBean.username);
//        editor.putString("realname",userBean.realname);
//        editor.apply();
//    }


    public String getStringValue(String key) {
        if (key != null && !key.equals("")) {
            return sp.getString(key, null);
        }
        return null;
    }

    public void putStringValue(String key, String value) {
        if (key != null && !key.equals("")) {
            editor = sp.edit();
            editor.putString(key, value);
            editor.commit();
        }
    }
}
