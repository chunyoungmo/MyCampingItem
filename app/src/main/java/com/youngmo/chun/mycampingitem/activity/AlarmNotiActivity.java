package com.youngmo.chun.mycampingitem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.youngmo.chun.mycampingitem.R;
import com.youngmo.chun.mycampingitem.define.IntentExtraNameDefine;
import com.youngmo.chun.mycampingitem.utils.Util;

public class AlarmNotiActivity extends BaseActivity {

    private TextView    mAlarmMessageTxtView;
    private String      mAlarmMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_noti);
        loadAdMob();
        init();
        getIntentData(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getIntentData(intent);
    }

    @Override
    public void onBackPressed() {
        // 백버튼 처리 막음!!
    }

    /**
     * @brief : 초기 처리
     */
    private void init() {
        setWindowFlag();
        setViewReference();
        setViewEventListener();
    }

    /**
     * @brief : 참조할 View ID 맵핑
     */
    private void setViewReference() {
        mAlarmMessageTxtView = (TextView) findViewById(R.id.alarm_noti_message_tv);
    }

    /**
     * @brief : View에 이벤트 등록
     */
    private void setViewEventListener() {
        // '확인' 버튼
        findViewById(R.id.alarm_noti_confirm_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void getIntentData(Intent intent) {
        mAlarmMessage = intent.getStringExtra(IntentExtraNameDefine.ALARM_NOTI_MESSAGE);

        if(Util.isValid(mAlarmMessage)) {
            mAlarmMessageTxtView.setText(mAlarmMessage);
        }
    }

    private void setWindowFlag()
    {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED); // 화면이 잠겨있을 때 보여주기
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);   // 화면 켜기
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); // 키잠금 해제하기

    }

}
