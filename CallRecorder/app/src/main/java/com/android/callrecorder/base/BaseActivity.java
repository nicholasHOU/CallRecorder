package com.android.callrecorder.base;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.callrecorder.home.LaunchActivity;
import com.android.callrecorder.login.LoginActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void goLogin() {
        if (this.getClass().isAssignableFrom(LaunchActivity.class) ){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

}