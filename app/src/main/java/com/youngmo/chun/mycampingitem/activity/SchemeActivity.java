package com.youngmo.chun.mycampingitem.activity;

import android.content.Intent;
import android.os.Bundle;

import com.youngmo.chun.mycampingitem.MainApplication;
import com.youngmo.chun.mycampingitem.R;

public class SchemeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheme);

        if(MainApplication.topLoadScreen() == null) {   // 앱이 미기동 상태
            Intent launch = new Intent(this, SplashActivity.class);
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launch);
        }
        finish();
    }
}