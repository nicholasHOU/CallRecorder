package com.android.callrecorder.widget.floatwindow;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.android.callrecorder.home.MainActivity;
import com.android.callrecorder.home.PhoneListenerService;

/**
 * FloatUtils
 *
 * @author maxinliang
 * @date 2021/3/24 11:23 AM
 */
public class FloatUtils {

    public static void openFloatWindow(Context context){
        Intent intent = new Intent(context, FloatWindowService.class);
        intent.setAction(FloatWindowService.ACTION_FOLLOW_TOUCH);
        Intent callIntent = new Intent(context, PhoneListenerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
            context.startForegroundService(callIntent);
        } else {
            context.startService(intent);
            context.startService(callIntent);
        }
    }

    public static void closeFloatWindowService(Context context){
        Intent intent = new Intent(context, FloatWindowService.class);
        intent.setAction(FloatWindowService.ACTION_KILL);
        context.startService(intent);
    }

//    public static void checkPerMissionFloatWindow(Context context) {
//        if (!FloatWindowUtilManager.getInstance().checkPermission(context)) {// 悬浮窗权限是否开启
//            //如果未开启，弹窗
//            showFloatWindowDialog(context);
//        }
//    }
//    private static void showFloatWindowDialog(Context context) {
//        new CommonDialogBuilder(context)
//                .setTitle("您现在无法开启视频导购连接服务")
//                .setContent("请点击前往开启悬浮窗权限")
//                .setPositiveName("去设置")
//                .setNegativeName("取消")
//                .setTouchOutSideCancelable(false)
//                .setCancelable(false)
//                .setmPositiveCallBack((dialog, which) -> {
//                    FloatWindowUtilManager.getInstance().applyPermission(context);
//                    dialog.dismiss();
//                })
//                .setmNegativeCallBack((dialog, which) -> {
//                    dialog.dismiss();
//                })
//                .create().show();
//    }

//    public static void checkPerMission(Context context){
//        boolean permission = FloatWindowParamManager.checkPermission(context);
//        if (permission && !RomUtils.isVivoRom()) {
//            ToastUtil.showToast("可以添加悬浮窗");
//            Intent intent = new Intent(context, FloatWindowService.class);
//            intent.setAction(FloatWindowService.ACTION_CHECK_PERMISSION_AND_TRY_ADD);
//            context.startService(intent);
//        } else {
//            ToastUtil.showToast("没有悬浮窗权限");
//            showOpenPermissionDialog(context);
//        }
//    }
//
//    private static void showOpenPermissionDialog(Context context) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("没有悬浮窗权限");
//        builder.setMessage("前往打开悬浮窗权限吗？");
//        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                FloatWindowParamManager.tryJumpToPermissionPage(context);
//
//                Intent intent = new Intent(context, FloatWindowService.class);
//                intent.setAction(FloatWindowService.ACTION_CHECK_PERMISSION_AND_TRY_ADD);
//                context.startService(intent);
//            }
//        });
//
//        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        builder.show();
//    }
//
//    public static void showOpenPermissionDialog2(Activity context) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("悬浮窗已尝试打开(右下角)");
//        builder.setMessage("如未打开，请前往应用权限管理页，打开悬浮窗权限！！！");
//        builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        builder.create().show();
//    }
}
