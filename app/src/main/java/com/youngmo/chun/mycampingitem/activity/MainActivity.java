package com.youngmo.chun.mycampingitem.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.youngmo.chun.mycampingitem.R;
import com.youngmo.chun.mycampingitem.manager.AlertManager;

public class MainActivity extends BaseActivity {
    private ViewGroup   mEquipmentMngLayout;
    private ViewGroup   mChecklistMngLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadAdMob();
        init();
    }

    @Override
    public void onBackPressed() {
        createDialogAppExit();
    }

    /**
     * @brief : 초기 처리
     */
    private void init() {
        setViewReference();
        setViewEventListener();
    }

    /**
     * @brief : 참조할 View ID 맵핑
     */
    private void setViewReference() {
        mEquipmentMngLayout = (ViewGroup) findViewById(R.id.main_holdings_equipment_management_layout);
        mChecklistMngLayout = (ViewGroup) findViewById(R.id.main_smart_checklist_management_layout);
    }

    /**
     * @brief : View에 이벤트 등록
     */
    private void setViewEventListener() {
        mEquipmentMngLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEquipmentListScreen();
            }
        });

        mChecklistMngLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCheckListManagementScreen();
            }
        });
    }


    /**
     * @brief : 장비 리스트 화면으로 이동 처리
     */
    private void goToEquipmentListScreen() {
        Intent intent = new Intent(mContext, EquipmentListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    /**
     * @brief : 체크 리스트 관리 화면으로 이동 처리
     */
    private void goToCheckListManagementScreen() {
        Intent intent = new Intent(mContext, CheckListManagementActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    /**
     * @brief : 앱 종료 팝업 처리
     */
    private void createDialogAppExit() {
        AlertManager.alertMessageDefault2Button(this, R.string.app_exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }, null, true);
    }
}
