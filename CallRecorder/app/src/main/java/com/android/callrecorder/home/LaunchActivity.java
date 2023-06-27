package com.android.callrecorder.home;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.callrecorder.R;
import com.android.callrecorder.base.BaseActivity;
import com.android.callrecorder.databinding.ActivityLaunchBinding;
import com.android.callrecorder.login.LoginActivity;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import zuo.biao.library.ui.AlertDialog;

public class LaunchActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    private ActivityLaunchBinding binding;

    private String[] perms = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CONTACTS,};
    // requestCode
    static final int RC_PERMISSION = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLaunchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.getRoot().postDelayed(new Runnable() {
            @Override
            public void run() {
                requestPermissions();

            }
        },2000);

    }


    private void goLoginOrHome(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void requestPermissions() {
        if (EasyPermissions.hasPermissions(this, perms)) {
            // 已获取权限
            goLoginOrHome();
        } else {
            new AlertDialog(this, "温馨提示", getString(R.string.permission_request),
                    true, 0, new AlertDialog.OnDialogButtonClickListener() {
                @Override
                public void onDialogButtonClick(int requestCode, boolean isPositive) {
                    if (isPositive){
                        requestPermissionsReal();
                    }else {
                        goLoginOrHome();
                    }
                }
            }).show();

//            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
//                    .setTitleText("温馨提示")
//                    .setContentText(getString(R.string.permission_request))
//                    .setCancelText(getString(R.string.cancel))
//                    .setConfirmText(getString(R.string.confirm))
//                    .showCancelButton(true)
//                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sweetAlertDialog) {
//                            requestPermissionsReal();
//                        }
//                    })
//                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sDialog) {
//                            sDialog.cancel();
//                            goLoginOrHome();
//                        }
//                    })
//                    .show();
        }
    }


    private void requestPermissionsReal() {
        // 没有权限，现在去获取
        EasyPermissions.requestPermissions(this, "申请权限", RC_PERMISSION, perms);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 将返回结果转给EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        // 一些权限被授予
        Toast.makeText(this, "允许----" + perms, Toast.LENGTH_SHORT).show();
        goLoginOrHome();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // 一些权限被禁止
        Toast.makeText(this, "禁止----" + perms, Toast.LENGTH_SHORT).show();
        goLoginOrHome();
    }

}