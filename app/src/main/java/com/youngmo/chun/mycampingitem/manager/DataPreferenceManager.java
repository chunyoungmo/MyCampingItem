package com.youngmo.chun.mycampingitem.manager;

import android.content.Context;
import android.content.SharedPreferences;

public class DataPreferenceManager {

    // SharedPreferences에 저장할 때 key값 정의
    /** 출정 전, 체크리스트 알림 시간 Key값 */
    private static final String FIELD_ALARM_TIME            = "field_alarm_time";

    private static SharedPreferences preference = null;
    private static SharedPreferences.Editor editor = null;
    private static Context context;

    public static void init(Context context, String name) {
        DataPreferenceManager.context = context;
        preference = PreferenceControl.init(DataPreferenceManager.context, "." + name, Context.MODE_PRIVATE);
        editor = preference.edit();
    }

    /**
     * @brief : 출정 전, 체크리스트 알림 시간 저장
     * @param time
     */
    public static void setFieldAlarmTime(String time) {
        setString(FIELD_ALARM_TIME, time);
    }

    /**
     * @brief : 출정 전, 체크리스트 알림 시간 반환
     * @return
     */
    public static String getFieldAlarmTime() {
        return getString(FIELD_ALARM_TIME);
    }



    private static boolean getBoolean( String name ) {
        return preference.getBoolean( name , false );
    }

    private static boolean getBoolean( String name, boolean defValue ) {
        return preference.getBoolean( name , defValue );
    }

    private static String getString( String name ) {
        return preference.getString( name , "" );
    }

    private static int getInteger( String name , int defValue) {
        return preference.getInt( name , defValue );
    }

    private static void setString( String name , String value) {
        PreferenceControl.put( editor , name, value);
    }

    private static void setBoolean( String name , boolean value) {
        PreferenceControl.put( editor, name, value);
    }
    private static void setInteger( String name , int value) {
        PreferenceControl.put( editor, name, value);
    }

    public static void setClear() {
        PreferenceControl.clear(editor);
    }
}

class PreferenceControl {
    public static SharedPreferences init(Context context , String name , int no) {
        return context.getSharedPreferences(name, no);
    }

    public static void put(SharedPreferences.Editor editor , String name , String value) {
        editor.putString(name, value);
        commit(editor);
    }

    public static void put(SharedPreferences.Editor editor , String name , int value) {
        editor.putInt(name, value);
        commit(editor);
    }

    public static void put(SharedPreferences.Editor editor , String name , Long value) {
        editor.putLong(name, value);
        commit(editor);
    }

    public static void put(SharedPreferences.Editor editor , String name , Float value) {
        editor.putFloat(name, value);
        commit(editor);
    }

    public static void put(SharedPreferences.Editor editor , String name , Boolean value) {
        editor.putBoolean(name, value);
        commit(editor);
    }

    public static void commit(SharedPreferences.Editor editor) {
        editor.commit();
    }

    public static void clear(SharedPreferences.Editor editor) {
        editor.clear();
    }

    public static String get(SharedPreferences pref , String name , String value) {
        return pref.getString(name, value);
    }

    public static int get(SharedPreferences pref , String name , int value) {
        return pref.getInt(name, value);
    }

    public static Long get(SharedPreferences pref , String name , Long value) {
        return pref.getLong(name, value);
    }

    public static Float get(SharedPreferences pref , String name , Float value) {
        return pref.getFloat(name, value);
    }

    public static Boolean get(SharedPreferences pref , String name , Boolean value) {
        return pref.getBoolean(name, value);
    }
}

