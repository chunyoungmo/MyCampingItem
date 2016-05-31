package com.youngmo.chun.mycampingitem.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.youngmo.chun.mycampingitem.MainApplication;
import com.youngmo.chun.mycampingitem.R;
import com.youngmo.chun.mycampingitem.db.DataBaseHelper;
import com.youngmo.chun.mycampingitem.db.DataBaseQuery;
import com.youngmo.chun.mycampingitem.model.CategoryInfo;
import com.youngmo.chun.mycampingitem.utils.Util;

public class SplashActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }

    private void init() {
        Util.getScreenInfo(this);
        initDataBase();
        setViewUI();
    }

    /**
     * @brief : 데이터베이스 초기 처리
     */
    private void initDataBase() {
        if(!DataBaseHelper.isExistDBFile(this)) {   // DB 파일이 생성 안되어 있는 경우
            // 디폴트 카테고리 데이터 셋팅 처리
            String[] defaultCategory = {
                    getString(R.string.category_name_tent),
                    getString(R.string.category_name_tarp),
                    getString(R.string.category_name_table),
                    getString(R.string.category_name_chair)
            };

            // getWritableDatabase함수 호출 시점에 DB파일이 생성됨
            DataBaseQuery.insertCategory(this, defaultCategory);
        }

        new GetDBCategoryTask().execute();
    }

    /**
     * @brief : View들의 UI구성요소 셋팅
     */
    private void setViewUI() {
        TextView versionText = (TextView)findViewById(R.id.splash_version_text_view);
        String versionInfo =  Util.getAppVersionName(this);
        versionText.setText(versionInfo);
    }

    /**
     * @brief : 메인 화면으로 이동 처리
     */
    private void goToMainScreen() {
        // 0.3초 딜레이 후, 메인 화면으로 이동(0.5초간 Splash 화면 보여줌)
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideLoadingDialog();
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        }, 500);
    }

    /**
     * @brief : 카테고리 정보 DB 정보 추출 AsyncTask
     */
    private class GetDBCategoryTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
//            Log.d(TAG, "GetDBCategoryTask onPreExecute");
            showLoadingDialog();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Boolean result = true;
            try {
                MainApplication.setCategoryInfoArr(DataBaseQuery.getAllCategory(mContext));
            } catch (Exception e) {
                result = false;
                e.printStackTrace();
            }
            for (CategoryInfo categoryInfo : MainApplication.getCategoryInfoArr()) {
//                Log.d(TAG, "category info id:[" + categoryInfo.getId() + "] name:[" + categoryInfo.getName() + "]");
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
//            Log.d(TAG, "GetDBCategoryTask onPostExecute");
            new GetDBEquipmentTask().execute();
            hideLoadingDialog();
        }
    }

    /**
     * @brief : 장비 정보 DB 정보 추출 AsyncTask
     */
    private class GetDBEquipmentTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
//            Log.d(TAG, "GetDBEquipmentTask onPreExecute");
            showLoadingDialog();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
//            Log.d(TAG, "GetDBEquipmentTask doInBackground start");
            Boolean result = true;
            try {
               MainApplication.setEquipmentInfoArr(DataBaseQuery.getAllEquipmentInfo(mContext));
            } catch (Exception e) {
                result = false;
                e.printStackTrace();
            }

//            for (EquipmentInfo equipmentInfo : MainApplication.getEquipmentInfoArr()) {
//                Log.d(TAG, "equipment info name:[" + equipmentInfo.getName() + "] maker:[" + equipmentInfo.getMaker() + "]");
//            }
//            Log.d(TAG, "GetDBEquipmentTask doInBackground end");

            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
//            Log.d(TAG, "GetDBEquipmentTask onPostExecute");
            new GetDBCheckListGroupTask().execute();
            hideLoadingDialog();
        }
    }

    /**
     * @brief : 체크리스트 그룹 정보 DB 정보 추출 AsyncTask
     */
    private class GetDBCheckListGroupTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
//            Log.d(TAG, "GetDBCheckListGroupTask onPreExecute");
            showLoadingDialog();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
//            Log.d(TAG, "GetDBCheckListGroupTask doInBackground start");
            Boolean result = true;
            try {
                MainApplication.setCheckListGroupInfoArr(DataBaseQuery.getAllCheckListGroupInfo(mContext));
            } catch (Exception e) {
                result = false;
                e.printStackTrace();
            }

//            for (CheckListGroupInfo checkListGroupInfo : MainApplication.getCheckListGroupInfoArr()) {
//                Log.d(TAG, "checkListGroupInfo name:[" + checkListGroupInfo.getGroupName() + "]");
//                Log.d(TAG, "checkListGroupInfo EquipInfoForDb:[" + checkListGroupInfo.getEquipInfoForDb() + "]");
//                Log.d(TAG, "checkListGroupInfo FieldDate:[" + checkListGroupInfo.getFieldDate() + "]");
//                Log.d(TAG, "checkListGroupInfo OrderNum:[" + checkListGroupInfo.getOrderNum() + "]");
//            }
//            Log.d(TAG, "GetDBCheckListGroupTask doInBackground end");

            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
//            Log.d(TAG, "GetDBCheckListGroupTask onPostExecute");
            goToMainScreen();
        }
    }
}
