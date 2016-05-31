package com.youngmo.chun.mycampingitem.activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.youngmo.chun.mycampingitem.R;
import com.youngmo.chun.mycampingitem.db.DataBaseQuery;
import com.youngmo.chun.mycampingitem.manager.DataPreferenceManager;
import com.youngmo.chun.mycampingitem.model.CheckListGroupInfo;
import com.youngmo.chun.mycampingitem.utils.Util;

import java.util.ArrayList;

public class SettingsActivity extends BaseActivity {

    private TextView    mFieldAlarmTimeTxtView;
    private int         mFieldAlarmHour;
    private int         mFieldAlarmMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        loadAdMob();
        init();
    }

    /**
     * @brief : 초기 처리
     */
    private void init() {
        setViewReference();
        setViewEventListener();
        setData();
        setViewUI();
    }

    /**
     * @brief : 참조할 View ID 맵핑
     */
    private void setViewReference() {
        mFieldAlarmTimeTxtView = (TextView)findViewById(R.id.settings_field_alarm_time_text_view);
    }

    /**
     * @brief : View에 이벤트 등록
     */
    private void setViewEventListener() {
        setViewEventListenerTopNaviBack();

        mFieldAlarmTimeTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if(mFieldAlarmHour == hourOfDay && mFieldAlarmMinute == minute) {
                            // 동일한 시간으로 셋팅된 경우는 Skip처리
                            return;
                        }

                        mFieldAlarmHour = hourOfDay;
                        mFieldAlarmMinute = minute;
                        String fieldAlarmTime = String.format("%02d", mFieldAlarmHour) + ":" + String.format("%02d", mFieldAlarmMinute);
                        mFieldAlarmTimeTxtView.setText(fieldAlarmTime);
                        DataPreferenceManager.setFieldAlarmTime(fieldAlarmTime);

                        refreshFieldOneDayAgoAlarm();
                    }
                }, mFieldAlarmHour, mFieldAlarmMinute, true).show();
            }
        });
    }

    /**
     * @brief : 데이터 관련 셋팅 작업
     */
    private void setData() {
        String fieldAlarmTime = DataPreferenceManager.getFieldAlarmTime();
        if(Util.isValid(fieldAlarmTime) && fieldAlarmTime.split(":").length == 2) {
            String[] timeSplit = fieldAlarmTime.split(":");
            mFieldAlarmHour = Integer.valueOf(timeSplit[0]);
            mFieldAlarmMinute = Integer.valueOf(timeSplit[1]);
        }
    }

    /**
     * @brief : View들의 UI구성요소 셋팅
     */
    private void setViewUI() {
        // 상단 네비게이션 타이틀
        ((TextView) findViewById(R.id.top_navigator_bar_title)).setText(getString(R.string.setting));

        // 출정 전날, 체크리스트 상태의 알림 수신 시간
        mFieldAlarmTimeTxtView.setText(String.format("%02d", mFieldAlarmHour) + ":" + String.format("%02d", mFieldAlarmMinute));

        // 버전 정보
        String versionInfo = "Ver" + Util.getAppVersionName(this);
        ((TextView)findViewById(R.id.settings_app_version_text_view)).setText(versionInfo);
    }

    /**
     * @brief : 출정 전날 체크리스트 확인 알림을 변경한 시간에 맞게 해제 후, 재등록
     */
    private void refreshFieldOneDayAgoAlarm() {
        showLoadingDialog();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                ArrayList<CheckListGroupInfo> checkListGroupInfoList = DataBaseQuery.getAllCheckListGroupInfo(mContext);

                for (CheckListGroupInfo checkListGroupInfo : checkListGroupInfoList) {
                    String fieldDate = checkListGroupInfo.getFieldDate();

                    if (Util.isValid(fieldDate)) {
                        if (checkListGroupInfo.isFieldAlarm()) {
                            // 출정 전날 체크리스트 확인 알림을 변경한 시간에 맞게 해제 후, 재등록
                            CheckListGroupRegisterActivity.releaseFieldOneDayAgoAlarm(checkListGroupInfo.getId(), mContext);
                            CheckListGroupRegisterActivity.registeFieldOneDayAgoAlarm(mContext, checkListGroupInfo.getId(), fieldDate);
                        }
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingDialog();
                    }
                });
            }
        };
        new Thread(runnable).start();
    }
}
