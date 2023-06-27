package com.android.callrecorder.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    private static Context context;


    public static void init(Context ctx){
        context = ctx;
    }

    public static void showToastLong(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
    public static void showToastLong(int msgId){
        Toast.makeText(context, msgId,Toast.LENGTH_LONG).show();
    }

    public static void showToast(String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static void showToast(int msgId){
        Toast.makeText(context,msgId,Toast.LENGTH_SHORT).show();
    }
}
