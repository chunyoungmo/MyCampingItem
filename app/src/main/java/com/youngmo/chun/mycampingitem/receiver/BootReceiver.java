package com.youngmo.chun.mycampingitem.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.youngmo.chun.mycampingitem.activity.CheckListGroupRegisterActivity;
import com.youngmo.chun.mycampingitem.db.DataBaseQuery;
import com.youngmo.chun.mycampingitem.model.CheckListGroupInfo;
import com.youngmo.chun.mycampingitem.utils.Util;

import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {

    private final String    TAG = Util.getTagBaseClassName(this);

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

//        Log.d(TAG, "Boot Receive");
//        Toast.makeText(context, "Boot Received!", Toast.LENGTH_LONG).show();

        // 재부팅으로 인해서 알림 재등록 처리
        ArrayList<CheckListGroupInfo> checkListGroupInfoList = DataBaseQuery.getAllCheckListGroupInfo(context);

        for(CheckListGroupInfo checkListGroupInfo : checkListGroupInfoList) {
            String fieldDate = checkListGroupInfo.getFieldDate();

            if(Util.isValid(fieldDate)) {
                // 출정 당일 오전 9시에 출정 enjoy 알림을 주기 위해 등록
                CheckListGroupRegisterActivity.registeFieldDayAlarm(context, checkListGroupInfo.getId(), fieldDate);

                if(checkListGroupInfo.isFieldAlarm()) {
                    // 출정 전날 사용자 설정 시간에(디폴트:오후 8시)에 체크리스트 확인 알림을 주기 위해 등록
                    CheckListGroupRegisterActivity.registeFieldOneDayAgoAlarm(context, checkListGroupInfo.getId(), fieldDate);
                }
            }
        }
    }
}
