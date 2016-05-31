package com.youngmo.chun.mycampingitem.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.youngmo.chun.mycampingitem.R;

/**
 * Created by ChunYoungmo on 15. 9. 23..
 */
public class AlertManager {

    /**
     * @brief : 기본 AlertDialog 처리 (1버튼: 확인)
     * @param activity : AlertDialog 보여질 화면
     * @param messageId : 메세지 Resource ID
     * @param confirmListener : 취소 동작 처리 Listenrer
     * @param isCancelable : 하드웨어 back 키로 동작 여부
     * @return
     */
    public static AlertDialog.Builder alertMessageDefault1Button(Activity activity, int messageId, DialogInterface.OnClickListener confirmListener, boolean isCancelable) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setMessage(messageId);
        dialog.setPositiveButton(activity.getString(R.string.confirm), confirmListener);
        dialog.setCancelable(isCancelable);
        dialog.show();

        return dialog;
    }

    /**
     * @brief : 기본 AlertDialog 처리 (1버튼: 확인)
     * @param activity : AlertDialog 보여질 화면
     * @param message : 메세지 문자열
     * @param confirmListener : 취소 동작 처리 Listenrer
     * @param isCancelable : 하드웨어 back 키로 동작 여부
     * @return
     */
    public static AlertDialog.Builder alertMessageDefault1Button(Activity activity, String message, DialogInterface.OnClickListener confirmListener, boolean isCancelable) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setMessage(message);
        dialog.setPositiveButton(activity.getString(R.string.confirm), confirmListener);
        dialog.setCancelable(isCancelable);
        dialog.show();

        return dialog;
    }

    public static AlertDialog.Builder alertMessageDefault1Button(Activity activity, String message, DialogInterface.OnClickListener positiveListener, String positive, boolean isCancelable) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setMessage(message);
        dialog.setPositiveButton(positive, positiveListener);
        dialog.setCancelable(isCancelable);
        dialog.show();

        return dialog;
    }

    /**
     * @brief : 기본 AlertDialog 처리 (2버튼: 취소, 확인)
     * @param activity : AlertDialog 보여질 화면
     * @param messageId : 메세지 Resource ID
     * @param confirmListener : 확인 동작 처리 Listenrer
     * @param cancelListener : 취소 동작 처리 Listenrer
     * @param isCancelable : 하드웨어 back 키로 동작 여부
     * @return
     */
    public static AlertDialog.Builder alertMessageDefault2Button(Activity activity, int messageId, DialogInterface.OnClickListener confirmListener, DialogInterface.OnClickListener cancelListener, boolean isCancelable) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setMessage(messageId);
        dialog.setPositiveButton(activity.getString(R.string.confirm), confirmListener);
        dialog.setNegativeButton(activity.getString(R.string.cancel), cancelListener);
        dialog.setCancelable(isCancelable);
        dialog.show();

        return dialog;
    }

    public static AlertDialog.Builder alertMessageDefault2Button(Activity activity, int messageId, DialogInterface.OnClickListener positiveListener, String positive, DialogInterface.OnClickListener negativeListener, String negative, boolean isCancelable) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setMessage(messageId);
        dialog.setPositiveButton(positive, positiveListener);
        dialog.setNegativeButton(negative, negativeListener);
        dialog.setCancelable(isCancelable);
        dialog.show();

        return dialog;
    }

    /**
     * @brief : 기본 AlertDialog 처리 (2버튼: 취소, 확인)
     * @param activity : AlertDialog 보여질 화면
     * @param message : 메세지 문자열
     * @param confirmListener : 확인 동작 처리 Listenrer
     * @param cancelListener : 취소 동작 처리 Listenrer
     * @param isCancelable : 하드웨어 back 키로 동작 여부
     * @return
     */
    public static AlertDialog.Builder alertMessageDefault2Button(Activity activity, String message, DialogInterface.OnClickListener confirmListener, DialogInterface.OnClickListener cancelListener, boolean isCancelable) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setMessage(message);
        dialog.setPositiveButton(activity.getString(R.string.confirm), confirmListener);
        dialog.setNegativeButton(activity.getString(R.string.cancel), cancelListener);
        dialog.setCancelable(isCancelable);
        dialog.show();

        return dialog;
    }
}