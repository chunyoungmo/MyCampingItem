package com.youngmo.chun.mycampingitem.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.youngmo.chun.mycampingitem.MainApplication;
import com.youngmo.chun.mycampingitem.R;
import com.youngmo.chun.mycampingitem.db.DataBaseQuery;
import com.youngmo.chun.mycampingitem.define.GlobalDefine;
import com.youngmo.chun.mycampingitem.define.IntentExtraNameDefine;
import com.youngmo.chun.mycampingitem.model.CheckListGroupInfo;
import com.youngmo.chun.mycampingitem.utils.Util;
import com.youngmo.chun.mycampingitem.view.MenuNavigatorView;

import java.util.ArrayList;
import java.util.Calendar;

public class CheckListManagementActivity extends BaseActivity {

    /** Mode 정의 */
    public enum CheckListManageMode {
        /** 기본 Default Mode */
        MODE_DEFAULT,
        /** 삭제 Mode */
        MODE_DELETE,
    };

    private GridView                    mGridViw;
    private CheckListManagementAdapter  mAdapter;
    private MenuNavigatorView           mMenuNavigatorView;
    private CheckListManageMode         mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list_management);
        loadAdMob();
        init();
    }

    @Override
    public void onBackPressed() {
        if(mMode == CheckListManageMode.MODE_DELETE) {
            // 삭제 모드 해제 처리
            mMode = CheckListManageMode.MODE_DEFAULT;
            mAdapter.notifyDataSetChanged();
        }
        else if(mMenuNavigatorView.isMenuOpen()) {
            mMenuNavigatorView.clickMainMenuByForce();
        }
        else {
            super.onBackPressed();
        }
    }

    /**
     * @brief : 초기 처리
     */
    private void init() {
        setViewReference();
        setViewEventListener();
        setData();
        setMenuNavigatorInfo();
        setViewUI();

//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        int screenWidth = metrics.widthPixels;
//        int totalMarginWidth = (int)getResources().getDimension(R.dimen.dp_4) + (int)getResources().getDimension(R.dimen.dp_4) + (int)getResources().getDimension(R.dimen.dp_10);
//        int groupRowWidth = (screenWidth - totalMarginWidth)/2;
//        int groupRowHeight = (int)getResources().getDimension(R.dimen.dp_180);
//
//
//        Log.d(TAG, "CheckListManagementActivity groupRow Wid:" + groupRowWidth + "H:" +groupRowHeight);
    }

    /**
     * @brief : 참조할 View ID 맵핑
     */
    private void setViewReference() {
        mGridViw = (GridView)findViewById(R.id.check_list_management_grid_view);
        mMenuNavigatorView = (MenuNavigatorView)findViewById(R.id.check_list_management_menu_navigator_view);
    }

    /**
     * @brief : View에 이벤트 등록
     */
    private void setViewEventListener() {
        setViewEventListenerTopNaviBack();
        mGridViw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mMode == CheckListManageMode.MODE_DEFAULT) { // 일반 모드의 경우
                    if (position == 0) {
                        // 체크리스트 그룹 등록 화면으로 이동
                        goToCheckListGroupRegisterScreen(MainApplication.getCheckListGroupInfoArr().size() + 1);
                    } else {
                        CheckListGroupInfo checkListGroupInfo = MainApplication.getCheckListGroupInfoArr().get(position - 1);
                        // 체크리스트 그룹 보기 화면으로 이동
                        goToCheckListGroupViewScreen(position - 1, checkListGroupInfo.getOrderNum());
                    }
                } else {  // 삭제 모드의 경우
                    if (position == 0) return;

                    final CheckListGroupInfo checkListGroupInfo = MainApplication.getCheckListGroupInfoArr().get(position - 1);
                    if (checkListGroupInfo != null) {
                        showLoadingDialog();

                        // 출정 알림 설정되어 있는 경우, 알미 해제 처리
                        if (checkListGroupInfo.isFieldAlarm()) {
                            int groupId = checkListGroupInfo.getId();
                            CheckListGroupRegisterActivity.releaseFieldAlarm(groupId, mContext);
                        }

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                DataBaseQuery.deleteCheckListGroupInfo(mContext, checkListGroupInfo);
                                MainApplication.setCheckListGroupInfoArr(DataBaseQuery.getAllCheckListGroupInfo(mContext));

                                // 삭제하고 남은 그룹들 OrderNum 재셋팅 처리
                                ArrayList<CheckListGroupInfo> arrCheckListGroupInfo = MainApplication.getCheckListGroupInfoArr();
                                int orderNum = arrCheckListGroupInfo.size();
                                for (CheckListGroupInfo checkListGroupInfo : arrCheckListGroupInfo) {
                                    checkListGroupInfo.setOrderNum(orderNum);
                                    --orderNum;
                                }

                                DataBaseQuery.updateCheckListGroupInfo(mContext, arrCheckListGroupInfo);
                                MainApplication.setCheckListGroupInfoArr(DataBaseQuery.getAllCheckListGroupInfo(mContext));

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideLoadingDialog();
                                        if(MainApplication.getCheckListGroupInfoArr().size() == 0) {
                                            mMode = CheckListManageMode.MODE_DEFAULT;
                                        }
                                        mAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        };
                        new Thread(runnable).start();
                    }
                }
            }
        });
    }
    /**
     * @brief : 데이터 관련 셋팅 작업
     */
    private void setData() {
        mMode = CheckListManageMode.MODE_DEFAULT;

        mAdapter = new CheckListManagementAdapter();
        mGridViw.setAdapter(mAdapter);
    }

    /**
     * @brief : 메뉴 네비게이션 정보 셋팅
     */
    private void setMenuNavigatorInfo() {
        mMenuNavigatorView.setMenuNavigatorInfo(MenuNavigatorView.MenuNavigatorType.MENU_NAVIGATOR_TYPE_MAIN_SUB_MENU,
                null, ContextCompat.getDrawable(mContext, R.drawable.ic_navi_menu_add), ContextCompat.getDrawable(mContext, R.drawable.ic_navi_menu_delete), null);
        mMenuNavigatorView.setListener(new MenuNavigatorView.MenuNavigatorViewListener() {
            @Override
            public void onPreClickMainMenu() {
                if(mMode == CheckListManageMode.MODE_DELETE) {
                    mMode = CheckListManageMode.MODE_DEFAULT;
                    mAdapter.notifyDataSetChanged();
                }
                else {
                    mMenuNavigatorView.clickMainMenuByForce();
                }
            }

            @Override
            public void onClickMainMenu() {
                // do nothing...
            }

            @Override
            public void onClickSubCommonMenu(MenuNavigatorView.SubMenuTag subCommonMenuTag) {
                mMenuNavigatorView.clickMainMenuByForce();
            }

            @Override
            public void onClickSubCustomMenu(MenuNavigatorView.SubMenuTag subCustomMenuTag) {

                switch (subCustomMenuTag) {
                    case SUB_MENU_TAG_CUSTOM_1: // '추가'의 경우
                        mMenuNavigatorView.clickMainMenuByForce();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 체크리스트 그룹 등록 화면으로 이동
                                goToCheckListGroupRegisterScreen(MainApplication.getCheckListGroupInfoArr().size() + 1);
                            }
                        }, 200);
                        break;

                    case SUB_MENU_TAG_CUSTOM_2: // '삭제'의 경우
                        if (MainApplication.getCheckListGroupInfoArr().size() > 0) {
                            Toast.makeText(mContext,getString(R.string.release_delete_mode_guide), Toast.LENGTH_SHORT).show();
                            mMenuNavigatorView.clickMainMenuByForce();
                            mMode = CheckListManageMode.MODE_DELETE;
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(mContext, getString(R.string.delete_mode_check_list_group_invalid), Toast.LENGTH_SHORT).show();
                        }

                        break;

                }
            }
        }, true);

    }

    /**
     * @brief : View들의 UI구성요소 셋팅
     */
    private void setViewUI() {
        ((TextView) findViewById(R.id.top_navigator_bar_title)).setText(getString(R.string.my_check_list));
    }

    /**
     * @brief : 체크리스트 그룹 등록 화면으로 이동 처리
     */
    private void goToCheckListGroupRegisterScreen(int orderNum) {
        Intent intent = new Intent(mContext, CheckListGroupRegisterActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(IntentExtraNameDefine.REGISTRATION_MODE, CheckListGroupRegisterActivity.CheckListGroupRegMode.MODE_REGISTRATION);
        intent.putExtra(IntentExtraNameDefine.CHECK_LIST_GROUP_INDEX, -1);
        intent.putExtra(IntentExtraNameDefine.CHECK_LIST_GROUP_ORDER_NUM, orderNum);
        startActivityForResult(intent, GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_CHECK_LIST_MANAGEMENT_REGISTRATION);
    }

    /**
     * @brief : 체크리스트 그룹 보기 화면으로 이동 처리
     */
    private void goToCheckListGroupViewScreen(int groupIndex, int orderNum) {
        Intent intent = new Intent(mContext, CheckListGroupRegisterActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(IntentExtraNameDefine.REGISTRATION_MODE, CheckListGroupRegisterActivity.CheckListGroupRegMode.MODE_VIEW);
        intent.putExtra(IntentExtraNameDefine.CHECK_LIST_GROUP_INDEX, groupIndex);
        intent.putExtra(IntentExtraNameDefine.CHECK_LIST_GROUP_ORDER_NUM, orderNum);
        startActivityForResult(intent, GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_CHECK_LIST_MANAGEMENT_REGISTRATION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK) {
            return;
        }

        // 체크리스트 그룹 등록 화면에서 등록,수정 완료되어 돌아온 경우
        if(requestCode == GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_CHECK_LIST_MANAGEMENT_REGISTRATION) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class CheckListManagementAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return MainApplication.getCheckListGroupInfoArr().size()+1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;

            if(convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.adapter_check_list_group_row, null);
                Util.setGlobalFont(convertView);
                viewHolder = new ViewHolder();
                viewHolder.mThemeAddLayout = (ViewGroup)convertView.findViewById(R.id.check_list_group_row_theme_add_layout);
                viewHolder.mThemeLayout = (ViewGroup)convertView.findViewById(R.id.check_list_group_row_theme_layout);
                viewHolder.mGroupDeleteLayout = (ViewGroup)convertView.findViewById(R.id.check_list_group_row_delete_layout);
                viewHolder.mGroupDelete = (ImageButton)convertView.findViewById(R.id.check_list_group_row_delete);
                viewHolder.mThemeIamge = (ImageView)convertView.findViewById(R.id.check_list_group_row_theme_image);
                viewHolder.mThemeName = (TextView)convertView.findViewById(R.id.check_list_group_row_theme_name);
                viewHolder.mFieldData = (TextView)convertView.findViewById(R.id.check_list_group_row_field_date);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            if(position == 0) { // 테마 추가
                ViewGroup.LayoutParams params = viewHolder.mThemeAddLayout.getLayoutParams();
                viewHolder.mThemeAddLayout.setVisibility(View.VISIBLE);
                viewHolder.mThemeLayout.setVisibility(View.GONE);
            }
            else {
                viewHolder.mThemeAddLayout.setVisibility(View.GONE);
                viewHolder.mThemeLayout.setVisibility(View.VISIBLE);

                CheckListGroupInfo checkListGroupInfo = MainApplication.getCheckListGroupInfoArr().get(position - 1);

                Bitmap groupImage = checkListGroupInfo.getGroupImage();
                if(groupImage != null) {
                    viewHolder.mThemeIamge.setImageBitmap(groupImage);
                }

                if(checkListGroupInfo.isDefaultImage()) {
                    viewHolder.mThemeIamge.setBackgroundColor(getResources().getColor(R.color.TransparentWhite));
                    viewHolder.mThemeIamge.setScaleType(ImageView.ScaleType.FIT_XY);
                }
                else {
                    viewHolder.mThemeIamge.setBackgroundColor(getResources().getColor(R.color.Black));
                    viewHolder.mThemeIamge.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                }

                viewHolder.mThemeName.setText(checkListGroupInfo.getGroupName());
                String fieldDate = checkListGroupInfo.getFieldDate();
                if(Util.isValid(fieldDate)) {
                    int dDay = getDday(fieldDate);
                    if(dDay == 0) {
                        viewHolder.mFieldData.setVisibility(View.VISIBLE);
                        viewHolder.mFieldData.setTextColor(getResources().getColor(R.color.Red));
                        viewHolder.mFieldData.setText(getString(R.string.field_d_day) + " : " + getString(R.string.today));
                    }
                    else if(dDay > 0) {
                        viewHolder.mFieldData.setVisibility(View.VISIBLE);
                        viewHolder.mFieldData.setTextColor(getResources().getColor(R.color.White));
                        viewHolder.mFieldData.setText(getString(R.string.field_d_day) + " : " + dDay);
                    }
                    else {
                        viewHolder.mFieldData.setVisibility(View.GONE);
                        viewHolder.mFieldData.setText("");
                    }
                }
                else {
                    viewHolder.mFieldData.setVisibility(View.GONE);
                    viewHolder.mFieldData.setText("");
                }
                if(mMode == CheckListManageMode.MODE_DEFAULT) { // 일반 모드의 경우
                    viewHolder.mGroupDeleteLayout.setVisibility(View.GONE);
                    viewHolder.mGroupDelete.setVisibility(View.GONE);
                }
                else {  // 삭제 모드의 경우
                    viewHolder.mGroupDeleteLayout.setVisibility(View.VISIBLE);
                    viewHolder.mGroupDelete.setVisibility(View.VISIBLE);
                    viewHolder.mGroupDeleteLayout.bringToFront();
                    viewHolder.mGroupDelete.bringToFront();
                    viewHolder.mGroupDelete.setFocusable(false);
                    viewHolder.mGroupDelete.setFocusableInTouchMode(false);
                }
            }

            final int pos = position;
            viewHolder.mGroupDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGridViw.performItemClick(v, pos, 0);
                }
            });

            return convertView;
        }

        private int getDday(String fieldDate) {
            if(!Util.isValid(fieldDate)) return -1;

            String[] fieldDateSplit = fieldDate.split("\\.");
            int year = Integer.valueOf(fieldDateSplit[0]);
            int month = Integer.valueOf(fieldDateSplit[1]) - 1;
            int day = Integer.valueOf(fieldDateSplit[2]);
            Calendar calendar = Calendar.getInstance();
            long currentTime = calendar.getTimeInMillis()/(24*60*60*1000);
            calendar.set(year, month, day);
            long fieldTime = calendar.getTimeInMillis()/(24*60*60*1000);

            return (int)(fieldTime - currentTime);
        }

        private class ViewHolder {
            ViewGroup   mThemeAddLayout;
            ViewGroup   mThemeLayout;
            ViewGroup   mGroupDeleteLayout;
            ImageButton mGroupDelete;
            ImageView   mThemeIamge;
            TextView    mThemeName;
            TextView    mFieldData;
        }
    }
}