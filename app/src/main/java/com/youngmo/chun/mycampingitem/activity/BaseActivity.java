package com.youngmo.chun.mycampingitem.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.youngmo.chun.mycampingitem.MainApplication;
import com.youngmo.chun.mycampingitem.R;
import com.youngmo.chun.mycampingitem.define.GlobalDefine;
import com.youngmo.chun.mycampingitem.utils.Util;
import com.youngmo.chun.mycampingitem.widget.LoadingDialog;

/**
 * Created by ChunYoungmo on 15. 10. 29..
 */
public class BaseActivity extends Activity {
    protected final String TAG = Util.getTagBaseClassName(this);

    /** Context */
    protected Context           mContext = null;
    /** LoadingDialog */
    private LoadingDialog       mLoadingDialog = null;
    /** LoadingDialog 카운트 */
    private int                 mLoadingDialogCount = 0;
    /** 이벤트 응답 처리 Receiver */
    private BroadcastReceiver   mBroadcastReceiver = null;

    private String[]            mExceptionLoadScreenClassNames = {"SchemeActivity"};

    /** AdMob */
    private AdView              mAdView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        addLoadScreen();
        initLoadingDialog();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setGlobalFont(getWindow().getDecorView());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mAdView != null)
            mAdView.resume();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);

        if(mAdView != null)
            mAdView.pause();

        super.onPause();
    }


    @Override
    protected void onDestroy() {
        if(mAdView != null)
            mAdView.destroy();

        mContext = null;
        removeLoadScreen();
        destoryLoadingDialog();
        unregistLocalEvent();
        super.onDestroy();
    }


    private void setGlobalFont(View view) {
        if (view != null) {
            if(view instanceof ViewGroup){
                ViewGroup vg = (ViewGroup)view;
                int vgCnt = vg.getChildCount();
                for(int i=0; i < vgCnt; i++){
                    View v = vg.getChildAt(i);
                    if(v instanceof TextView){
                        ((TextView) v).setTypeface(MainApplication.mTypeface);
                    }
                    setGlobalFont(v);
                }
            }
        }
    }

    /**
     * @brief : AdMob 로드 처리 - AdMob을 사용할 각화면의 onCreate에서 호출해야함!
     */
    protected void loadAdMob() {
        if(GlobalDefine.isVisibleAdmob) {
            findViewById(R.id.admob_layout).setVisibility(View.VISIBLE);
            mAdView = (AdView) findViewById(R.id.admob_banner_adview);

            AdRequest adRequest;
            if(GlobalDefine.isRelease) {
                adRequest = new AdRequest.Builder().build();
            }
            else {
                adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                            .addTestDevice("F77E1C869A2835FACA28FD1ACB096A26")  // Note5 Device
                            .addTestDevice("26B1BA76E4CEB2D4BE3EB87ACDF9A882")  // Note2 Device
                            .addTestDevice("25CC9C4690750588447D4DC47C1C55D2")  // S5 Device
                            .build();
            }

            mAdView.loadAd(adRequest);
        }
        else {
            findViewById(R.id.admob_layout).setVisibility(View.GONE);
        }
    }

    /**
     * @brief : 상단 네비게이션 Back처리 셋팅
     */
    protected void setViewEventListenerTopNaviBack() {
        View topNaviBackView = findViewById(R.id.top_navigator_bar_back_layout);
        if(topNaviBackView != null) {
            topNaviBackView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void addLoadScreen() {
        String className = getClass().getSimpleName();

        for(String exceptionLoadScreenClassName:mExceptionLoadScreenClassNames) {
            if(exceptionLoadScreenClassName.equals(className)) {
                return;
            }
        }

        MainApplication.addLoadScreen(this);
    }

    private void removeLoadScreen() {
        String className = getClass().getSimpleName();

        for(String exceptionLoadScreenClassName:mExceptionLoadScreenClassNames) {
            if(exceptionLoadScreenClassName.equals(className)) {
                return;
            }
        }

        MainApplication.removeLoadScreen(this);
    }

    /**
     * @brief : LoadingDialog 초기화
     */
    private void initLoadingDialog() {
        mLoadingDialog = new LoadingDialog(this);
        mLoadingDialog.setLayoutId(R.layout.dialog_loading);
        mLoadingDialog.setCancelable(false);
    }

    /**
     * @brief : LoadingDialog 표시
     */
    public void showLoadingDialog() {
        if(mLoadingDialog != null) {
            if(mLoadingDialog.isShowing()) {
                ++mLoadingDialogCount;
            } else {
                mLoadingDialog.show();
            }
        }
    }

    /**
     * @brief : LoadingDialog 삭제
     */
    protected void hideLoadingDialog() {
        if(mLoadingDialog != null){
            if(mLoadingDialog.isShowing() && mLoadingDialogCount <= 0) {
                mLoadingDialog.dismiss();
                mLoadingDialogCount = 0;
            } else {
                if(mLoadingDialogCount > 0) {
                    --mLoadingDialogCount;
                }
            }
        }
    }

    /**
     * @brief : LoadingDialog 해제
     */
    protected void destoryLoadingDialog() {
        if(mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            mLoadingDialogCount = 0;
            mLoadingDialog = null;
        }
    }

    /**
     * @brief : 로컬 이벤트 등록
     * @param eventName : 등록 이벤트 명(LocalEventDefine)
     */
    protected void registLocalEvent(String eventName) {
        if(mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    onReceivedLocalEvent(context, intent.getAction(), intent);
                }
            };
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(eventName));
    }

    /**
     * @brief : 로컬 이벤트 해제
     */
    private void unregistLocalEvent() {
        if(mBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        }
    }

    /**
     * @brief : registLocalEvent을 통해 등록한 이벤트에 대해서 이벤트를 받을 때 호출
     *          - 각 화면단에서 override하여 해당 이벤트에 대한 응답 처리 진행
     * @param context
     * @param eventName
     * @param intent
     */
    protected void onReceivedLocalEvent(Context context, String eventName, Intent intent) {
        // 각 화면단에서 override하여 해당 이벤트에 대한 응답 처리 진행
    }
}
