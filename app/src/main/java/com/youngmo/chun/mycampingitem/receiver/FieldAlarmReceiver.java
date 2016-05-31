package com.youngmo.chun.mycampingitem.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.youngmo.chun.mycampingitem.R;
import com.youngmo.chun.mycampingitem.activity.AlarmNotiActivity;
import com.youngmo.chun.mycampingitem.db.DataBaseQuery;
import com.youngmo.chun.mycampingitem.define.IntentExtraNameDefine;
import com.youngmo.chun.mycampingitem.model.CheckListGroupInfo;
import com.youngmo.chun.mycampingitem.utils.Util;

import java.util.ArrayList;

public class FieldAlarmReceiver extends BroadcastReceiver {
    public static final String SCHEME_FIELD_ALARM   = "fieldAlarm";
    public static final String HOST_FIELD_DAY       = "fieldDay";
    public static final String HOST_ONE_DAY_AGO     = "oneDayAgo";
    public static final String PARAM_KEY_ALARM_ID   = "alarmId";


    private final String    TAG = Util.getTagBaseClassName(this);

    public FieldAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri uriData = intent.getData();
        String scheme = uriData.getScheme();
        String host = uriData.getHost();
        String alarmId = uriData.getQueryParameter(PARAM_KEY_ALARM_ID);

//        Log.d(TAG, "FieldAlarmReceiver onReceive!! Uri scheme:" + scheme + " host:" + host + " alarmId:" + alarmId);


        if(!Util.isValid(scheme) || !scheme.equals(SCHEME_FIELD_ALARM)) return;

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "WakeLock");
        wakeLock.acquire(500);

        if(host.equals(HOST_FIELD_DAY)) {   // 출정 당일의 경우
            sendNotificationFieldDay(context, alarmId);
        }
        else if(host.equals(HOST_ONE_DAY_AGO)) {    // 출정 전일의 경우
            sendNotificationFieldOneDayAgo(context, alarmId);
        }
    }

    /**
     * @brief : 출정 알림 Noti 표시 (출정 당일 출정 enjoy 알림용)
     * @param context
     */
    private void sendNotificationFieldDay(Context context, String alarmId) {
        // alarmId를 기반으로 notifyId 생성
        int notifyId = Integer.valueOf(alarmId + "0");
        sendNotificationBar(context, context.getString(R.string.enjoy_camping), notifyId);
        showAlarmNotificationPopup(context, context.getString(R.string.enjoy_camping));
    }

    /**
     * @brief : 출정 알림 Noti 표시 (출정 전날 체크리스트 확인 알림용)
     * @param context
     * @param alarmId
     */
    private void sendNotificationFieldOneDayAgo(Context context, String alarmId) {
        String message = context.getString(R.string.item_not_ready);
        String item = "";

        // 체크리스트 아이템 확인 여부를 체크해서 체크되지 않은 아이템들에 대해서 알림
        CheckListGroupInfo checkListGroupInfo = DataBaseQuery.getCheckListGroupInfoWithId(context, alarmId);
        if(checkListGroupInfo != null) {
            ArrayList<String> uncheckEquipNameArr = checkListGroupInfo.getUncheckEquipNameList();
            if(uncheckEquipNameArr != null && uncheckEquipNameArr.size() > 0) {
                for(int i=0; i<uncheckEquipNameArr.size(); i++) {
                    String equipName = uncheckEquipNameArr.get(i);
                    if(i==0)
                        item += equipName;
                    else
                        item += ", " + equipName;

                }
            }
        }

        if(Util.isValid(item)) {
            message += item;
        }

        // alarmId를 기반으로 notifyId 생성
        int notifyId = Integer.valueOf(alarmId + "1");
        sendNotificationBar(context, message, notifyId);
        showAlarmNotificationPopup(context, message);
    }

    private void sendNotificationBar(Context context, String message, int notifyId) {
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(context);
        notiBuilder.setSmallIcon(R.drawable.ic_notification);
        notiBuilder.setContentTitle(context.getResources().getString(R.string.app_name));
        notiBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        notiBuilder.setContentText(message);
        notiBuilder.setAutoCancel(true);
        notiBuilder.setOnlyAlertOnce(true);
        notiBuilder.setSound(defaultSoundUri);
        notiBuilder.setVibrate(new long[]{200, 200, 500, 300});
        notiBuilder.setTicker(message);     // 상단 Noti 영역에 메세지 보임 처리
////                .setNumber(DataPreferenceManager.getInteger(DataPreferenceManager.BADGE_COUNT, 0)); // 동일 Noti ID로 처리한 경우에, 상단 Noti 영역에 메세지 개수 표시

        Intent clickIntent = new Intent(context, AlarmNotificationEventReceiver.class);
        clickIntent.putExtra("requestCode", notifyId);
        clickIntent.setAction(AlarmNotificationEventReceiver.ACTION_NOTIFICATION_CLICK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notifyId, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notiBuilder.setContentIntent(pendingIntent);

        Intent cancelIntent = new Intent(context, AlarmNotificationEventReceiver.class);
        cancelIntent.putExtra("requestCode", notifyId);
        cancelIntent.setAction(AlarmNotificationEventReceiver.ACTION_NOTIFICATION_CANCEL);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(context, notifyId, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notiBuilder.setDeleteIntent(cancelPendingIntent);

        NotificationManager notiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notiManager.notify(notifyId, notiBuilder.build());
    }

    private void showAlarmNotificationPopup(Context context, String message) {
        try {
            Intent popupIntent = new Intent(context, AlarmNotiActivity.class);
            popupIntent.putExtra(IntentExtraNameDefine.ALARM_NOTI_MESSAGE, message);

            popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, popupIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
