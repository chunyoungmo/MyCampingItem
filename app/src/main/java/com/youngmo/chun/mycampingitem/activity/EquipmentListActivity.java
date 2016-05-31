package com.youngmo.chun.mycampingitem.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.youngmo.chun.mycampingitem.MainApplication;
import com.youngmo.chun.mycampingitem.R;
import com.youngmo.chun.mycampingitem.db.DataBaseQuery;
import com.youngmo.chun.mycampingitem.define.GlobalDefine;
import com.youngmo.chun.mycampingitem.define.IntentExtraNameDefine;
import com.youngmo.chun.mycampingitem.define.LocalEventDefine;
import com.youngmo.chun.mycampingitem.model.EquipmentInfo;
import com.youngmo.chun.mycampingitem.model.SingleSelectPopupItemInfo;
import com.youngmo.chun.mycampingitem.utils.Util;
import com.youngmo.chun.mycampingitem.view.MenuNavigatorView;
import com.youngmo.chun.mycampingitem.view.SingleSelectPopup;

import java.util.ArrayList;

public class EquipmentListActivity extends BaseActivity {

    /** Mode 정의 */
    public enum EquipmentListMode {
        /** 기본 Default Mode */
        MODE_DEFAULT,
        /** 삭제 Mode */
        MODE_DELETE,
    };

    private ViewGroup                   mActivityLayout;
    private MenuNavigatorView           mMenuNavigatorView;
    private SingleSelectPopup           mCategorySelectPopup = null;
    private int                         mCategorySelectedIndex = 0;
    private TextView                    mCategoryTxtView;
    private ListView                    mListView;
    private EquipmentListAdapter        mAdapter;
    private EquipmentListMode           mMode;

    private ArrayList<EquipmentInfo>    mEquipmentInfoArr = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_list);
        loadAdMob();
        init();
    }

    @Override
    public void onBackPressed() {
        if(mMode == EquipmentListMode.MODE_DELETE) {
            // 삭제 모드 해제 처리
            mMode = EquipmentListMode.MODE_DEFAULT;
            mAdapter.notifyDataSetChanged();
        }
        else if(mCategorySelectPopup != null) {
            mActivityLayout.removeView(mCategorySelectPopup);
            mCategorySelectPopup = null;
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
        // 장비 정보 갱신이 필요한 경우, 이벤트를 받기 위해 등록
        registLocalEvent(LocalEventDefine.EQUIPMENT_INFO_REFRESH_REQUEST);

        setViewReference();
        setViewEventListener();
        setData();
        setMenuNavigatorInfo();
        setViewUI();
    }

    /**
     * @brief : 참조할 View ID 맵핑
     */
    private void setViewReference() {
        mActivityLayout = (ViewGroup)findViewById(R.id.equipment_list_layout);
        mMenuNavigatorView = (MenuNavigatorView)findViewById(R.id.equipment_list_menu_navigator_view);
        mCategoryTxtView = (TextView)findViewById(R.id.equipment_list_category_text_view);
        mListView = (ListView)findViewById(R.id.equipment_list_list_view);
    }

    /**
     * @brief : View에 이벤트 등록
     */
    private void setViewEventListener() {
        setViewEventListenerTopNaviBack();
        mCategoryTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMode == EquipmentListMode.MODE_DEFAULT && mCategorySelectPopup == null) {
                    mCategorySelectPopup = new SingleSelectPopup(mContext);
                    mCategorySelectPopup.setListener(new SingleSelectPopup.SingleSelectPopupListener() {
                        @Override
                        public void onSelectedItem(int selectedIndex) {
                            if (mCategorySelectPopup != null) {
                                mActivityLayout.removeView(mCategorySelectPopup);
                                mCategorySelectPopup = null;
                                mCategorySelectedIndex = selectedIndex;

                                if(mCategorySelectedIndex == 0) {   // 0인 경우는 "전체"의 경우
                                    mCategoryTxtView.setText(getString(R.string.category_total));
                                    updateEquipmentInfoArr(true, true);
                                }
                                else {
                                    mCategoryTxtView.setText(MainApplication.getCategoryInfoArr().get(mCategorySelectedIndex-1).getName());
                                    updateEquipmentInfoArr(false, true);
                                }
                            }
                        }

                        @Override
                        public void onCancel() {
                            if (mCategorySelectPopup != null) {
                                mActivityLayout.removeView(mCategorySelectPopup);
                                mCategorySelectPopup = null;
                            }
                        }
                    });
                    mActivityLayout.addView(mCategorySelectPopup);
                    ArrayList<String> categoryNameArr = MainApplication.getCategoryNameArr();
                    ArrayList<SingleSelectPopupItemInfo> arrPopupItem = new ArrayList<>();
                    for(String categoryName : categoryNameArr) {
                        arrPopupItem.add(new SingleSelectPopupItemInfo(categoryName));
                    }

                    arrPopupItem.add(0, new SingleSelectPopupItemInfo(getString(R.string.category_total)));

                    mCategorySelectPopup.setMenuItemInfo(SingleSelectPopup.Type.TYPE_SELECTED_STATUS, getString(R.string.category), arrPopupItem, mCategorySelectedIndex);
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mMode == EquipmentListMode.MODE_DEFAULT) {
                    // 장비 상세 화면으로 이동
                    goToEquipmentDetailScreen(position);
                }
                else {
                    final EquipmentInfo equipmentInfo = MainApplication.getEquipmentInfoArr().get(position);
                    if(equipmentInfo != null) {
                        showLoadingDialog();

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                DataBaseQuery.deleteEquipmentInfo(mContext, equipmentInfo);
                                MainApplication.setEquipmentInfoArr(DataBaseQuery.getAllEquipmentInfo(mContext));
                                if(mCategorySelectedIndex == 0) {   // 0인 경우는 "전체"의 경우
                                    updateEquipmentInfoArr(true, false);
                                }
                                else {
                                    updateEquipmentInfoArr(false, false);
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideLoadingDialog();
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
        mMode = EquipmentListMode.MODE_DEFAULT;
        mCategorySelectedIndex = 0;
        mAdapter = new EquipmentListAdapter();
        mListView.setAdapter(mAdapter);
        updateEquipmentInfoArr(true, true);
    }

    private void updateEquipmentInfoArr(boolean isTotal, boolean isListRefresh) {
        if(mEquipmentInfoArr != null && mEquipmentInfoArr.size() > 0)
            mEquipmentInfoArr.clear();

        if(isTotal) {
            mEquipmentInfoArr.addAll(MainApplication.getEquipmentInfoArr());
        }
        else {
            // "전체" 카테고리는 임의로 넣은 거라서 실제 인덱스는 "전체"를 제외하기위해 -1 해줌
            int categoryId = MainApplication.getCategoryInfoArr().get(mCategorySelectedIndex-1).getId();
            mEquipmentInfoArr.addAll(MainApplication.getEquipmentInfoArr(categoryId));
        }

        if(isListRefresh)
            mAdapter.notifyDataSetChanged();
    }


    /**
     * @brief : 메뉴 네비게이션 정보 셋팅
     */
    private void setMenuNavigatorInfo() {
        mMenuNavigatorView.setMenuNavigatorInfo(MenuNavigatorView.MenuNavigatorType.MENU_NAVIGATOR_TYPE_MAIN_SUB_MENU,
                null, ContextCompat.getDrawable(mContext,R.drawable.ic_navi_menu_add), ContextCompat.getDrawable(mContext, R.drawable.ic_navi_menu_delete), null);
        mMenuNavigatorView.setListener(new MenuNavigatorView.MenuNavigatorViewListener() {
            @Override
            public void onPreClickMainMenu() {
                if(mMode == EquipmentListMode.MODE_DELETE) {
                    // 삭제 모드 해제 처리
                    mMode = EquipmentListMode.MODE_DEFAULT;
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
                                goToEquipmentRegisterScreen();
                            }
                        }, 200);
                        break;

                    case SUB_MENU_TAG_CUSTOM_2: // '삭제'의 경우
                        if(MainApplication.getEquipmentInfoArr().size() > 0) {
                            Toast.makeText(mContext,getString(R.string.release_delete_mode_guide), Toast.LENGTH_SHORT).show();
                            mMenuNavigatorView.clickMainMenuByForce();
                            mMode = EquipmentListMode.MODE_DELETE;
                            mAdapter.notifyDataSetChanged();
                        }
                        else {
                            Toast.makeText(mContext, getString(R.string.delete_mode_equipment_invalid), Toast.LENGTH_SHORT).show();
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
        ((TextView) findViewById(R.id.top_navigator_bar_title)).setText(getString(R.string.holdings_equipment_list));
    }

    /**
     * @brief : 장비 상세 화면으로 이동 처리
     */
    private void goToEquipmentDetailScreen(int position) {
        Intent intent = new Intent(mContext, EquipmentDetailActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        // 실제 정보의 인덱스 뽑기
        EquipmentInfo equipmentInfo = mEquipmentInfoArr.get(position);
        ArrayList<EquipmentInfo> arrEquipmentInfo = MainApplication.getEquipmentInfoArr();

        int index = 0;
        for(EquipmentInfo equipInfo : arrEquipmentInfo) {
            if(equipInfo.getId() == equipmentInfo.getId()) {
                break;
            }
            index++;
        }

        intent.putExtra(IntentExtraNameDefine.EQUIPMENT_INFO_INDEX, index);
        startActivity(intent);
    }

    /**
     * @brief : 장비 등록 화면으로 이동 처리
     */
    private void goToEquipmentRegisterScreen() {
        Intent intent = new Intent(mContext, EquipmentRegisterActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(IntentExtraNameDefine.REGISTRATION_MODE, EquipmentRegisterActivity.EquipmentRegMode.MODE_REGISTRATION);
        startActivityForResult(intent, GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_LIST_REGISTRATION);
    }

    @Override
    protected void onReceivedLocalEvent(Context context, String eventName, Intent intent) {
        super.onReceivedLocalEvent(context, eventName, intent);

        if(Util.isValid(eventName)) {
            // 장비 정보 갱신된 경우, 이벤트 수신 된 경우
            if(eventName.equals(LocalEventDefine.EQUIPMENT_INFO_REFRESH_REQUEST)) {
                if(mCategorySelectedIndex == 0) {   // 0인 경우는 "전체"의 경우
                    updateEquipmentInfoArr(true, true);
                }
                else {
                    updateEquipmentInfoArr(false, true);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK) {
            return;
        }

        // 장비 등록 화면에서 등록 완료되어 돌아온 경우
        if(requestCode == GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_LIST_REGISTRATION) {
            if(mCategorySelectedIndex == 0) {   // 0인 경우는 "전체"의 경우
                updateEquipmentInfoArr(true, true);
            }
            else {
                updateEquipmentInfoArr(false, true);
            }
        }
    }


    private class EquipmentListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mEquipmentInfoArr.size();
        }

        @Override
        public Object getItem(int position) {
            return mEquipmentInfoArr.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;

            if(convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.adapter_equipment_list_row, null);
                Util.setGlobalFont(convertView);
                viewHolder = new ViewHolder();

                viewHolder.mDeleteImgView = (ImageView)convertView.findViewById(R.id.equipment_list_row_delete_image_view);
                viewHolder.mThumbnailImgView = (ImageView)convertView.findViewById(R.id.equipment_list_row_thumbnail_image_view);
                viewHolder.mNameTxtView = (TextView)convertView.findViewById(R.id.equipment_list_row_name_text_view);
                viewHolder.mMakerTxtView = (TextView)convertView.findViewById(R.id.equipment_list_row_maker_text_view);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            EquipmentInfo equipmentInfo = mEquipmentInfoArr.get(position);
            if (equipmentInfo.getPicture1() != null) {
                viewHolder.mThumbnailImgView.setVisibility(View.VISIBLE);
                viewHolder.mThumbnailImgView.setImageBitmap(equipmentInfo.getPicture1());
            }
            else {
                viewHolder.mThumbnailImgView.setVisibility(View.GONE);
            }

            if(mCategorySelectedIndex == 0) { // 0인 경우는 "전체"의 경우
                viewHolder.mNameTxtView.setText(equipmentInfo.getCategory() + " | " + equipmentInfo.getName());
            }
            else {
                viewHolder.mNameTxtView.setText(equipmentInfo.getName());
            }
            viewHolder.mMakerTxtView.setText(equipmentInfo.getMaker());

            if(mMode == EquipmentListMode.MODE_DEFAULT) {
                viewHolder.mDeleteImgView.setVisibility(View.GONE);
            }
            else {
                viewHolder.mDeleteImgView.setVisibility(View.VISIBLE);
            }


            return convertView;
        }

        private class ViewHolder {
            ImageView   mDeleteImgView;
            ImageView   mThumbnailImgView;
            TextView    mNameTxtView;
            TextView    mMakerTxtView;
        }
    }
}
