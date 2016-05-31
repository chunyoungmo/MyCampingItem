package com.youngmo.chun.mycampingitem.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.youngmo.chun.mycampingitem.activity.SchemeActivity;
import com.youngmo.chun.mycampingitem.utils.Util;

public class AlarmNotificationEventReceiver extends BroadcastReceiver{
    private final String    TAG = Util.getTagBaseClassName(this);

    public static final String ACTION_NOTIFICATION_CANCEL  = "com.youngmo.chun.mycampingitem.notification_cancel";
    public static final String ACTION_NOTIFICATION_CLICK   = "com.youngmo.chun.mycampingitem.notification_click";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        int requestCode = intent.getExtras().getInt("requestCode");
//        Log.d(TAG, "onReceive requestCode" + requestCode);

        if(action.equals(ACTION_NOTIFICATION_CANCEL)) {
//            Log.d(TAG, "onReceive ACTION_NOTIFICATION_CANCEL");
        }
        else if(action.equals(ACTION_NOTIFICATION_CLICK)) {
//            Log.d(TAG, "onReceive ACTION_NOTIFICATION_CLICK");

            Intent clickIntent = new Intent(context, SchemeActivity.class);
            clickIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(clickIntent);
        }
    }

}
