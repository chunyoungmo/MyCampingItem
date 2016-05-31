package com.youngmo.chun.mycampingitem;

import android.app.Application;
import android.graphics.Typeface;

import com.youngmo.chun.mycampingitem.activity.BaseActivity;
import com.youngmo.chun.mycampingitem.manager.DataPreferenceManager;
import com.youngmo.chun.mycampingitem.model.CategoryInfo;
import com.youngmo.chun.mycampingitem.model.CheckListGroupInfo;
import com.youngmo.chun.mycampingitem.model.EquipmentInfo;
import com.youngmo.chun.mycampingitem.utils.Util;

import java.util.ArrayList;

public class MainApplication extends Application {

    private static String TAG = Util.getTagBaseClassName(MainApplication.class);
    private static ArrayList<BaseActivity>          mLoadScreenList = null;
    private static ArrayList<CategoryInfo>          mCategoryInfoArr = new ArrayList<>();
    private static ArrayList<EquipmentInfo>         mEquipmentInfoArr = new ArrayList<>();
    private static ArrayList<CheckListGroupInfo>    mCheckListGroupInfoArr = new ArrayList<>();

    public static final Typeface mTypeface = Typeface.SANS_SERIF;
//    public static final Typeface mTypeface = Typeface.DEFAULT_BOLD;


    @Override
    public void onCreate() {
        super.onCreate();

        // 초기화 작업
        mLoadScreenList = new ArrayList<>();
        DataPreferenceManager.init(this, getString(R.string.app_name));
        String fieldAlarmTime = DataPreferenceManager.getFieldAlarmTime();
        if(!Util.isValid(fieldAlarmTime)) {
            // 출정 전, 체크리스트 알림 시간 Default 셋팅(오후 8시)
            DataPreferenceManager.setFieldAlarmTime("20:00");
        }

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static BaseActivity topLoadScreen() {
        if(mLoadScreenList != null && mLoadScreenList.size() > 0) {
            return mLoadScreenList.get(mLoadScreenList.size() - 1);
        }
        else {
            return null;
        }
    }

    public static void addLoadScreen(BaseActivity activity) {
        if(mLoadScreenList != null) {
            mLoadScreenList.add(activity);
        }
    }

    public static void removeLoadScreen(BaseActivity activity) {
        if(mLoadScreenList != null) {
            for(int i=0; i<mLoadScreenList.size(); ++i) {
                BaseActivity baseActivity = mLoadScreenList.get(i);
                if(baseActivity == activity) {
                    mLoadScreenList.remove(activity);
                }
            }
        }
    }

    public static boolean isLoadScreen(BaseActivity activity) {
        boolean isLoad = false;
        if(mLoadScreenList != null) {
            for(int i=0; i<mLoadScreenList.size(); ++i) {
                BaseActivity baseActivity = mLoadScreenList.get(i);
                if(baseActivity == activity) {
                    isLoad = true;
                }
            }
        }

        return isLoad;
    }

    public static boolean isLoadScreen(String className) {
//        Log.d(TAG, "isLoadScreen className:" + className);
        boolean isLoad = false;
        if(mLoadScreenList != null) {
            for(int i=0; i<mLoadScreenList.size(); ++i) {
                BaseActivity baseActivity = mLoadScreenList.get(i);
//                Log.d(TAG, "isLoadScreen :" + baseActivity.getClass().getSimpleName());

                if(baseActivity.getClass().getSimpleName().equals(className)) {
                    isLoad = true;
                }
            }
        }

        return isLoad;
    }


    public static ArrayList<CategoryInfo> getCategoryInfoArr() {
        return mCategoryInfoArr;
    }

    public static ArrayList<String> getCategoryNameArr() {
        ArrayList<String> categoryNameArr = new ArrayList<>();
        for (CategoryInfo categoryInfo : mCategoryInfoArr) {
            categoryNameArr.add(categoryInfo.getName());
        }

        return categoryNameArr;
    }

    public static void setCategoryInfoArr(ArrayList<CategoryInfo> categoryInfoArr) {
        mCategoryInfoArr.clear();
        mCategoryInfoArr.addAll(categoryInfoArr);
    }


    public static ArrayList<EquipmentInfo> getEquipmentInfoArr() {
        return mEquipmentInfoArr;
    }

    public static ArrayList<EquipmentInfo> getEquipmentInfoArr(int categoryId) {
        ArrayList<EquipmentInfo> equipmentInfoArr = new ArrayList<>();

        for(EquipmentInfo equipInfo : mEquipmentInfoArr) {
            if(equipInfo.getCategoryId() == categoryId) {
                equipmentInfoArr.add(equipInfo);
            }
        }

        return equipmentInfoArr;
    }

    public static void setEquipmentInfoArr(ArrayList<EquipmentInfo> equipmentInfoArr) {
        mEquipmentInfoArr.clear();
        mEquipmentInfoArr.addAll(equipmentInfoArr);
    }

    public static void setEquipmentInfo(EquipmentInfo equipmentInfo, int index) {
        if(mEquipmentInfoArr.size() > index) {
            mEquipmentInfoArr.set(index, equipmentInfo);
        }
    }

    public static ArrayList<CheckListGroupInfo> getCheckListGroupInfoArr() {
        return mCheckListGroupInfoArr;
    }

    public static void setCheckListGroupInfoArr(ArrayList<CheckListGroupInfo> checkListGroupInfoArr) {
        mCheckListGroupInfoArr.clear();
        mCheckListGroupInfoArr.addAll(checkListGroupInfoArr);
    }

}
