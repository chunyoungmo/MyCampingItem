package com.youngmo.chun.mycampingitem.activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.youngmo.chun.mycampingitem.MainApplication;
import com.youngmo.chun.mycampingitem.R;
import com.youngmo.chun.mycampingitem.db.DataBaseQuery;
import com.youngmo.chun.mycampingitem.define.GlobalDefine;
import com.youngmo.chun.mycampingitem.define.IntentExtraNameDefine;
import com.youngmo.chun.mycampingitem.manager.AlertManager;
import com.youngmo.chun.mycampingitem.manager.DataPreferenceManager;
import com.youngmo.chun.mycampingitem.model.CheckListEquipmentInfo;
import com.youngmo.chun.mycampingitem.model.CheckListGroupInfo;
import com.youngmo.chun.mycampingitem.model.EquipmentInfo;
import com.youngmo.chun.mycampingitem.model.MultiSelectPopupItemInfo;
import com.youngmo.chun.mycampingitem.model.SingleSelectPopupItemInfo;
import com.youngmo.chun.mycampingitem.receiver.FieldAlarmReceiver;
import com.youngmo.chun.mycampingitem.utils.RecycleUtils;
import com.youngmo.chun.mycampingitem.utils.Util;
import com.youngmo.chun.mycampingitem.view.ImageSelectPopup;
import com.youngmo.chun.mycampingitem.view.InputPopup;
import com.youngmo.chun.mycampingitem.view.MultiSelectPopup;
import com.youngmo.chun.mycampingitem.view.SingleSelectPopup;
import com.youngmo.chun.mycampingitem.widget.RoundImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CheckListGroupRegisterActivity extends BaseActivity {
    private int   THEME_IMAGE_SIZE;
    private final String CAMERA_CAPTURE_IMAGE_NAME  = "temp.jpg";

    private static final String INSTANCE_SAVE_KEY_GROUP_BITMAP = "groupBitmap";

    /** Mode 정의 */
    public enum CheckListGroupRegMode {
        /** 등록 Mode */
        MODE_REGISTRATION,
        /** 보기 Mode */
        MODE_VIEW,
        /** 수정 Mode */
        MODE_MODIFY,
    };

    private CheckListGroupRegMode   mMode;
    private ViewGroup               mActivityLayout;
    private TextView                mTopNaviTitelTxtView;
    private Button                  mTopNaviMenuBtn;
    private SingleSelectPopup       mGroupImageSelectPopup = null;
    private ImageSelectPopup        mGroupDefaultImageSelectPopup = null;
    private SingleSelectPopup       mEquipDivisionSelectPopup = null;
    private InputPopup              mCheckListInputPopup = null;
    private MultiSelectPopup        mHoldingsEquipListSelectPopup = null;
    private CheckListGroupInfo      mCheckListGroupInfo = new CheckListGroupInfo();
    private RoundImageView          mGroupImage;
    private EditText                mGroupNameEditTxt;
    private TextView                mGroupNameTxtView;
    private ViewGroup               mCheckListEditLayout;
    private Button                  mCheckListAddBtn;
    private Button                  mCheckListEditBtn;
    private ListView                mCheckListView;
    private CheckListAdapter        mCheckListAdapter;
    private TextView                mFieldDateTxtView;
    private Button                  mFieldDateClearBtn;
    private Button                  mFieldAlarmBtn;
    private TextView                mFieldAlarmTxtView;
    private ViewGroup               mListLayout;
    private ViewGroup               mExpandLayout;
    private ViewGroup               mExpandContentLayout;

    private int                     mCheckListGroupIndex;
    private int                     mCheckListGroupOrderNum;

    private int                     mOriginalListViewHeight;
    private boolean                 mIsThemeImg;
    private boolean                 mIsRecycleThemeImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list_group_register);
        loadAdMob();
        init();
    }

    @Override
    public void onBackPressed() {
        if(mGroupImageSelectPopup != null) {
            mActivityLayout.removeView(mGroupImageSelectPopup);
            mGroupImageSelectPopup = null;
        }
        else if(mGroupDefaultImageSelectPopup != null) {
            mActivityLayout.removeView(mGroupDefaultImageSelectPopup);
            mGroupDefaultImageSelectPopup = null;
        }
        else if(mEquipDivisionSelectPopup != null) {
            mActivityLayout.removeView(mEquipDivisionSelectPopup);
            mEquipDivisionSelectPopup = null;
        }
        else if(mCheckListInputPopup != null) {
            mActivityLayout.removeView(mCheckListInputPopup);
            mCheckListInputPopup = null;
        }
        else if(mHoldingsEquipListSelectPopup != null) {
            mActivityLayout.removeView(mHoldingsEquipListSelectPopup);
            mHoldingsEquipListSelectPopup = null;
        }
        else if(mCheckListAdapter.mIsEditMode) {
            setViewUICheckList(false);
        }
        else if(mMode == CheckListGroupRegMode.MODE_MODIFY) {
            createDialogModificationCancel();
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
        setData(true);
        setViewUI();
        THEME_IMAGE_SIZE = (int)getResources().getDimension(R.dimen.dp_180);
    }

    /**
     * @brief : 참조할 View ID 맵핑
     */
    private void setViewReference() {
        mTopNaviTitelTxtView = (TextView)findViewById(R.id.top_navigator_bar_title);
        mTopNaviMenuBtn = (Button)findViewById(R.id.top_navigator_bar_menu_button);
        mActivityLayout = (ViewGroup)findViewById(R.id.check_list_group_register_layout);
        mGroupImage = (RoundImageView)findViewById(R.id.check_list_group_register_theme_image);
        mGroupNameEditTxt = (EditText)findViewById(R.id.check_list_group_register_theme_group_name_edit_text);
        mGroupNameTxtView = (TextView)findViewById(R.id.check_list_group_register_theme_group_name_text_view);
        mCheckListEditLayout = (ViewGroup)findViewById(R.id.check_list_group_register_check_list_edit_layout);
        mCheckListAddBtn = (Button)findViewById(R.id.check_list_group_register_add_button);
        mCheckListEditBtn = (Button)findViewById(R.id.check_list_group_register_edit_button);
        mCheckListView = (ListView)findViewById(R.id.check_list_group_register_list_view);
        mFieldDateTxtView = (TextView)findViewById(R.id.check_list_group_register_field_date_text_view);
        mFieldDateClearBtn = (Button)findViewById(R.id.check_list_group_register_field_date_clear);
        mFieldAlarmBtn = (Button)findViewById(R.id.check_list_group_register_field_alarm_button);
        mFieldAlarmTxtView = (TextView)findViewById(R.id.check_list_group_register_field_alarm_text_view);
        mListLayout = (ViewGroup)findViewById(R.id.check_list_group_register_list_layout);
        mExpandLayout = (ViewGroup)findViewById(R.id.check_list_group_register_check_list_expand_layout);
        mExpandContentLayout = (ViewGroup)findViewById(R.id.check_list_group_register_check_list_expand_contents_layout);
    }

    /**
     * @brief : View에 이벤트 등록
     */
    private void setViewEventListener() {
//        setViewEventListenerTopNaviBack();
        findViewById(R.id.top_navigator_bar_back_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMode == CheckListGroupRegMode.MODE_MODIFY)
                    createDialogModificationCancel();
                else
                    finish();
            }
        });

        // 그룹 이미지 이벤트
        mGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMode != CheckListGroupRegMode.MODE_VIEW)
                    showGroupImageSelectPopup();
            }
        });

        // 체크리스트 추가 이벤트
        mCheckListAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEquipDivisionSelectPopup == null) {
                    mEquipDivisionSelectPopup = new SingleSelectPopup(mContext);
                    mEquipDivisionSelectPopup.setListener(new SingleSelectPopup.SingleSelectPopupListener() {
                        @Override
                        public void onSelectedItem(int selectedIndex) {
                            if (mEquipDivisionSelectPopup != null) {
                                boolean isRemovePopup = true;
                                if (selectedIndex == 0) {       // '직접 입력' 선택의 경우
                                    showCheckListInputPopup();
                                } else if (selectedIndex == 1) {   // '보유 장비리스트에서 가져오기' 선택의 경우

                                    if (MainApplication.getEquipmentInfoArr().size() > 0) {
                                        if (!showHoldingsEquipmentListPopup()) {
                                            isRemovePopup = false;
                                            Toast.makeText(mContext, getString(R.string.no_longer_registe_holdings_equipment), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        isRemovePopup = false;
                                        Toast.makeText(mContext, getString(R.string.no_holdings_equipment), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                if (isRemovePopup) {
                                    mActivityLayout.removeView(mEquipDivisionSelectPopup);
                                    mEquipDivisionSelectPopup = null;
                                }
                            }
                        }

                        @Override
                        public void onCancel() {
                            if (mEquipDivisionSelectPopup != null) {
                                mActivityLayout.removeView(mEquipDivisionSelectPopup);
                                mEquipDivisionSelectPopup = null;
                            }
                        }
                    });

                    mActivityLayout.addView(mEquipDivisionSelectPopup);
                    ArrayList<SingleSelectPopupItemInfo> arrItem = new ArrayList<>();
                    arrItem.add(new SingleSelectPopupItemInfo(getString(R.string.direct_input)));
                    arrItem.add(new SingleSelectPopupItemInfo(getString(R.string.get_holdings_equipment_list)));

                    mEquipDivisionSelectPopup.setMenuItemInfo(SingleSelectPopup.Type.TYPE_NORMAL, null, arrItem, 0);

                    // 키보드 내리기
                    Util.hideSoftKeyboard(mContext, mGroupNameEditTxt, true);
                }
            }
        });

        // 체크리스트 삭제 이벤트
        mCheckListEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckListGroupInfo.getArrCheckListEquipInfo().size() <= 0) {
                    Toast.makeText(mContext, getString(R.string.no_check_list), Toast.LENGTH_SHORT).show();
                    return;
                }

                setViewUICheckList(true);

                // 키보드 내리기
                Util.hideSoftKeyboard(mContext, mGroupNameEditTxt, true);
            }
        });

        mCheckListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO:
                // do nothing... 나중에 아이템 정보 팝업 형식으로 업그레이드 하는 방안 검토~
            }
        });

        // 체크리스트 편집 뷰의 닫기 버튼 이벤트
        findViewById(R.id.check_list_group_register_check_list_expand_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewUICheckList(false);
            }
        });

        mExpandLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setViewUICheckList(false);
            }
        });

        // 체크리스트 편집 뷰의 '전체 삭제' 버튼 이벤트
        findViewById(R.id.check_list_group_register_check_list_expand_all_delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckListAdapter.deleteAllCheckListItem();
            }
        });


        // '출정 날짜' 클릭 이벤트 처리
        mFieldDateTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = mFieldDateTxtView.getText().toString();

                int year, month, day;

                GregorianCalendar calendar = new GregorianCalendar();
                if(Util.isValid(date) && date.split("\\.").length == 3) {
                    String[] dateSplit = date.split("\\.");

                    year = Integer.valueOf(dateSplit[0]);
                    month = Integer.valueOf(dateSplit[1]) - 1;
                    day = Integer.valueOf(dateSplit[2]);
                }
                else {

                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                }

                new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        String setDate = String.format("%d.%d.%d", year, monthOfYear + 1, dayOfMonth);

                        mFieldDateTxtView.setText(setDate);
                        mFieldDateClearBtn.setVisibility(View.VISIBLE);
                        mFieldAlarmBtn.setSelected(true);
                    }
                }, year, month, day).show();

                // 키보드 내리기
                Util.hideSoftKeyboard(mContext, mGroupNameEditTxt, true);
            }
        });

        // '출정 날짜 클리어' 클릭 이벤트 처리
        mFieldDateClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFieldDateClearBtn.setVisibility(View.GONE);
                mFieldDateTxtView.setText("");
                mFieldAlarmBtn.setSelected(false);
            }
        });

        // '출정 알림'클릭 이벤트 처리(버튼 영역)
        mFieldAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFiedlAlarmToggleCheck();
            }
        });

        // '출정 알림'클릭 이벤트 처리(텍스트 영역)
        mFieldAlarmTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFiedlAlarmToggleCheck();
            }
        });

        // 상단 네비게이션에 버튼 이벤트 처리
        mTopNaviMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mMode) {
                    case MODE_REGISTRATION: // 등록,수정의 '완료' 경우
                    case MODE_MODIFY:
                        if (isValidRequiredItem()) {
                            registeCheckListGroup();
                        }
                        break;

                    case MODE_VIEW: // '수정'을 통해서  수정 모드로 변환하는 경우
                        mMode = CheckListGroupRegMode.MODE_MODIFY;
                        mIsRecycleThemeImg = false;
                        setViewUI();
                        mCheckListAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });
    }

    /**
     * @brief : 데이터 관련 셋팅 작업
     * @param isIntentData
     */
    private void setData(boolean isIntentData) {
        if(isIntentData) {
            mMode = (CheckListGroupRegMode) getIntent().getSerializableExtra(IntentExtraNameDefine.REGISTRATION_MODE);
            mCheckListGroupIndex = getIntent().getIntExtra(IntentExtraNameDefine.CHECK_LIST_GROUP_INDEX, -1);
            mCheckListGroupOrderNum = getIntent().getIntExtra(IntentExtraNameDefine.CHECK_LIST_GROUP_ORDER_NUM, -1);
        }

        if(mMode == CheckListGroupRegMode.MODE_REGISTRATION)
            mIsThemeImg = false;
        else
            mIsThemeImg = true;


        if(mMode != CheckListGroupRegMode.MODE_REGISTRATION && mCheckListGroupIndex >= 0) {

            // Deey Copy 처리
            CheckListGroupInfo checkListGroupInfo = MainApplication.getCheckListGroupInfoArr().get(mCheckListGroupIndex);
            mCheckListGroupInfo.setId(checkListGroupInfo.getId());
            mCheckListGroupInfo.setGroupImage(checkListGroupInfo.getGroupImage());
            mCheckListGroupInfo.setGroupName(checkListGroupInfo.getGroupName());
            mCheckListGroupInfo.setFieldDate(checkListGroupInfo.getFieldDate());
            mCheckListGroupInfo.setIsFieldAlarm(checkListGroupInfo.isFieldAlarm());
            mCheckListGroupInfo.setOrderNum(checkListGroupInfo.getOrderNum());
            mCheckListGroupInfo.setIsDefaultImage(checkListGroupInfo.isDefaultImage());
            mCheckListGroupInfo.addAllCheckListEquipInfo(checkListGroupInfo.getArrCheckListEquipInfo());
        }

        mCheckListAdapter = new CheckListAdapter();
        mCheckListAdapter.mIsEditMode = false;
        mCheckListView.setAdapter(mCheckListAdapter);
        ViewGroup.LayoutParams params = mCheckListView.getLayoutParams();
        mOriginalListViewHeight = params.height;
    }

    /**
     * @brief : View들의 UI구성요소 셋팅
     */
    private void setViewUI() {
        setViewUINavi();
        setViewUIContents();
    }

    /**
     * @brief : 상단 네비게이션 영역 UI 표시 처리
     */
    private void setViewUINavi() {
        // 상단 네비게이션 영역 관련
        switch (mMode) {
            case MODE_REGISTRATION:
                mTopNaviTitelTxtView.setText(getString(R.string.check_list_group_registration));
                mTopNaviMenuBtn.setVisibility(View.VISIBLE);
                mTopNaviMenuBtn.setText(getString(R.string.completion));
                break;

            case MODE_VIEW:
                mTopNaviTitelTxtView.setText(getString(R.string.check_list_group));
                mTopNaviMenuBtn.setVisibility(View.VISIBLE);
                mTopNaviMenuBtn.setText(getString(R.string.modify));
                break;

            case MODE_MODIFY:
                mTopNaviTitelTxtView.setText(getString(R.string.check_list_group_modification));
                mTopNaviMenuBtn.setVisibility(View.VISIBLE);
                mTopNaviMenuBtn.setText(getString(R.string.completion));
                break;
        }
    }

    /**
     * @brief : View들의 UI구성요소에 컨텐츠 데이터 표시
     */
    private void setViewUIContents() {
        if (mCheckListGroupInfo == null) return;
        if (mMode == CheckListGroupRegMode.MODE_REGISTRATION) {
            mGroupNameEditTxt.setVisibility(View.VISIBLE);
            mGroupNameTxtView.setVisibility(View.GONE);
        }
        else {

            // 테마 그룹 이미지
            Bitmap groupImage = mCheckListGroupInfo.getGroupImage();
            if (groupImage != null) {
                setGroupThemeImage(groupImage, true);
            }

            // 테마 그룹 명
            String groupName = mCheckListGroupInfo.getGroupName();
            if (Util.isValid(groupName)) {
                mGroupNameEditTxt.setText(groupName);
                mGroupNameTxtView.setText(groupName);
            }

            // 출정 날짜
            String fiedlDate = mCheckListGroupInfo.getFieldDate();
            if (Util.isValid(fiedlDate)) {
                mFieldDateTxtView.setText(fiedlDate);
            }

            // 출정 알람 여부
            mFieldAlarmBtn.setSelected(mCheckListGroupInfo.isFieldAlarm());

            if (mMode == CheckListGroupRegMode.MODE_VIEW) { // 보기 Mode
                mGroupNameEditTxt.setVisibility(View.GONE);
                mGroupNameTxtView.setVisibility(View.VISIBLE);

                mGroupNameEditTxt.setFocusable(false);
                mGroupNameEditTxt.setFocusableInTouchMode(false);
                mFieldDateTxtView.setClickable(false);
                mFieldDateClearBtn.setVisibility(View.GONE);
                mFieldAlarmBtn.setClickable(false);
                mFieldAlarmTxtView.setClickable(false);

                mCheckListEditLayout.setVisibility(View.GONE);
//                mCheckListEditLayout.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_out));
            } else {    // 수정 Mode
                mGroupNameEditTxt.setVisibility(View.VISIBLE);
                mGroupNameTxtView.setVisibility(View.GONE);

                mGroupNameEditTxt.setFocusable(true);
                mGroupNameEditTxt.setFocusableInTouchMode(true);
                mFieldDateTxtView.setClickable(true);

                if (Util.isValid(fiedlDate))
                    mFieldDateClearBtn.setVisibility(View.VISIBLE);
                else
                    mFieldDateClearBtn.setVisibility(View.GONE);

                mFieldAlarmBtn.setClickable(true);
                mFieldAlarmTxtView.setClickable(true);

                mCheckListEditLayout.setVisibility(View.VISIBLE);
//                mCheckListEditLayout.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_in));
            }
        }
    }


    private void setViewUICheckList(boolean isEditMode) {
        if(isEditMode) {
            mExpandLayout.setVisibility(View.VISIBLE);
            mListLayout.removeView(mCheckListView);
            mExpandContentLayout.addView(mCheckListView);

            mCheckListAdapter.mIsEditMode = true;
            // 높이 사이즈 재조정 필요
            // ListView 높이를 mCheckListGroupInfo.getArrCheckListEquipInfo()에 따라서 동적 크기 처리
            ViewGroup.LayoutParams params = mCheckListView.getLayoutParams();
            if(mCheckListGroupInfo.getArrCheckListEquipInfo().size() < 10) {
                // dp_34 은 list의 셀 높이임, dp_5 은 스크롤 뷰가 생기지 않을 정도의 여분 추가함
                params.height = ((int)getResources().getDimension(R.dimen.dp_34) * mCheckListGroupInfo.getArrCheckListEquipInfo().size()) + (int)getResources().getDimension(R.dimen.dp_5);
            }
            else {
                params.height = (int)getResources().getDimension(R.dimen.dp_340);
            }
            findViewById(R.id.check_list_group_register_check_list_expand_close_button).bringToFront();
        }
        else {
            mExpandLayout.setVisibility(View.GONE);
            mExpandContentLayout.removeView(mCheckListView);
            mListLayout.addView(mCheckListView);

            mCheckListAdapter.mIsEditMode = false;

            // 높이 사이즈를 원래 크기로 재조정
            ViewGroup.LayoutParams params = mCheckListView.getLayoutParams();
            params.height = mOriginalListViewHeight;
        }
        mCheckListAdapter.notifyDataSetChanged();
    }

    /**
     * @brief : 출정 알림 버튼 체크 토글 처리
     */
    private void setFiedlAlarmToggleCheck() {
        boolean isSelected = mFieldAlarmBtn.isSelected();
        mFieldAlarmBtn.setSelected(!isSelected);
    }

    /**
     * @brief : 그룹이미지 선택을 위한 팝업 표시
     */
    private void showGroupImageSelectPopup() {
        if (mGroupImageSelectPopup == null) {
            mGroupImageSelectPopup = new SingleSelectPopup(mContext);
            mGroupImageSelectPopup.setListener(new SingleSelectPopup.SingleSelectPopupListener() {
                @Override
                public void onSelectedItem(int selectedIndex) {
                    if (mGroupImageSelectPopup != null) {
                        mActivityLayout.removeView(mGroupImageSelectPopup);
                        mGroupImageSelectPopup = null;

                        switch (selectedIndex) {
                            case 0: // '앱 기본 제공 사진 업로드' 의 경우
                                showGroupDefaultImageSelectPopup();
                                break;

                            case 1: // '앨범 사진 업로드' 의 경우
                                callAlbum();
                                break;

                            case 2: // '카메라 촬영 업로드' 의 경우
                                callCamera();
                                break;
                        }
                    }
                }

                @Override
                public void onCancel() {
                    if (mGroupImageSelectPopup != null) {
                        mActivityLayout.removeView(mGroupImageSelectPopup);
                        mGroupImageSelectPopup = null;
                    }
                }
            });
            mActivityLayout.addView(mGroupImageSelectPopup);

            ArrayList<SingleSelectPopupItemInfo> popupItemArr = new ArrayList<>();
            popupItemArr.add(new SingleSelectPopupItemInfo(getString(R.string.default_theme_image_upload)));
            popupItemArr.add(new SingleSelectPopupItemInfo(getString(R.string.image_album_upload)));
            // TODO:하위 디바이스에서 대응하기 귀찮아서 막음
//            popupItemArr.add(new SingleSelectPopupItemInfo(getString(R.string.image_camera_upload)));
            mGroupImageSelectPopup.setMenuItemInfo(SingleSelectPopup.Type.TYPE_NORMAL, getString(R.string.image_registration), popupItemArr, -1);

            // 키보드 내리기
            Util.hideSoftKeyboard(mContext, mGroupNameEditTxt, true);
        }
    }

    /**
     * @brief : 기본 제공 그룹이미지 선택을 위한 팝업 표시
     */
    private void showGroupDefaultImageSelectPopup() {
        if (mGroupDefaultImageSelectPopup == null) {
            mGroupDefaultImageSelectPopup = new ImageSelectPopup(mContext);
            mGroupDefaultImageSelectPopup.setListener(new ImageSelectPopup.ImageSelectPopupListener() {
                @Override
                public void onSelectedItem(int selectedIndex) {
                    if (mGroupDefaultImageSelectPopup != null) {
                        mActivityLayout.removeView(mGroupDefaultImageSelectPopup);
                        mGroupDefaultImageSelectPopup = null;

                        if(selectedIndex == 0) {
                            setGroupThemeImage(((BitmapDrawable)ContextCompat.getDrawable(mContext, R.drawable.img_theme_bg_01)).getBitmap(), false);
                        }
                        else if(selectedIndex == 1) {
                            setGroupThemeImage(((BitmapDrawable)ContextCompat.getDrawable(mContext, R.drawable.img_theme_bg_02)).getBitmap(), false);
                        }
                        else if(selectedIndex == 2) {
                            setGroupThemeImage(((BitmapDrawable)ContextCompat.getDrawable(mContext, R.drawable.img_theme_bg_03)).getBitmap(), false);
                        }
                        else if(selectedIndex == 3) {
                            setGroupThemeImage(((BitmapDrawable)ContextCompat.getDrawable(mContext, R.drawable.img_theme_bg_04)).getBitmap(), false);
                        }
                        mCheckListGroupInfo.setIsDefaultImage(true);
                    }
                }

                @Override
                public void onCancel() {
                    if (mGroupDefaultImageSelectPopup != null) {
                        mActivityLayout.removeView(mGroupDefaultImageSelectPopup);
                        mGroupDefaultImageSelectPopup = null;
                    }
                }
            });
            mActivityLayout.addView(mGroupDefaultImageSelectPopup);

            ArrayList<Bitmap> popupItemArr = new ArrayList<>();
            popupItemArr.add(((BitmapDrawable)ContextCompat.getDrawable(mContext, R.drawable.img_theme_bg_01)).getBitmap());
            popupItemArr.add(((BitmapDrawable)ContextCompat.getDrawable(mContext, R.drawable.img_theme_bg_02)).getBitmap());
            popupItemArr.add(((BitmapDrawable)ContextCompat.getDrawable(mContext, R.drawable.img_theme_bg_03)).getBitmap());
            popupItemArr.add(((BitmapDrawable) ContextCompat.getDrawable(mContext, R.drawable.img_theme_bg_04)).getBitmap());
            mGroupDefaultImageSelectPopup.setMenuItemInfo(getString(R.string.default_theme_image), popupItemArr);

        }
    }

    /**
     * @brief : 체크리스트를 직접 입력하기위한 팝업 표시
     */
    private void showCheckListInputPopup() {
        if(mCheckListInputPopup == null) {
            mCheckListInputPopup = new InputPopup(mContext, null, new InputPopup.InputPopupListener() {
                @Override
                public void onInputText(final String inputText) {
                    if (mCheckListInputPopup != null) {
                        mActivityLayout.removeView(mCheckListInputPopup);
                        mCheckListInputPopup = null;
                    }

                    if(Util.isValid(inputText)) {
                        CheckListEquipmentInfo checkListEquipmentInfo = new CheckListEquipmentInfo(CheckListEquipmentInfo.EquipType.EQUIP_TYPE_INPUT,
                                -1, inputText, false);
                        mCheckListGroupInfo.addCheckListEquipInfo(checkListEquipmentInfo, 0);
                        mCheckListAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancel() {
                    if (mCheckListInputPopup != null) {
                        mActivityLayout.removeView(mCheckListInputPopup);
                        mCheckListInputPopup = null;
                    }
                }
            });
            mActivityLayout.addView(mCheckListInputPopup);
        }
    }

    /**
     * @brief : 보유 장비리스트 팝업 표시
     */
    private boolean showHoldingsEquipmentListPopup() {
        if(mHoldingsEquipListSelectPopup == null) {

            // 이미 추가되어 있는 장비인지 확인하여 처리
            ArrayList<MultiSelectPopupItemInfo> arrItem = new ArrayList<>();
            ArrayList<EquipmentInfo> arrEquipInfo = MainApplication.getEquipmentInfoArr();
            boolean isSet = true;
            for(EquipmentInfo equipInfo : arrEquipInfo) {
                isSet = true;

                if(mCheckListGroupInfo.getArrCheckListEquipInfo().size() == 0 ) {
                    arrItem.add(new MultiSelectPopupItemInfo(equipInfo.getName(), equipInfo));
                }
                else {
                    for (CheckListEquipmentInfo checkListEquipInfo : mCheckListGroupInfo.getArrCheckListEquipInfo()) {
                        if (checkListEquipInfo.getEquipId() == equipInfo.getId()) {
                            isSet = false;
                            break;
                        }
                    }
                    if(isSet) {
                        arrItem.add(new MultiSelectPopupItemInfo(equipInfo.getName(), equipInfo));
                    }
                }
            }

            if(arrItem.size()== 0) return false;

            mHoldingsEquipListSelectPopup = new MultiSelectPopup(mContext);
            mHoldingsEquipListSelectPopup.setListener(new MultiSelectPopup.MultiSelectPopupListener() {
                @Override
                public void onConfirm(ArrayList<MultiSelectPopupItemInfo> itemInfoArr) {
                    if (mHoldingsEquipListSelectPopup != null) {
                        mActivityLayout.removeView(mHoldingsEquipListSelectPopup);
                        mHoldingsEquipListSelectPopup = null;

                        int addCount = 0;
                        for(int i=0; i<itemInfoArr.size(); i++) {
                            MultiSelectPopupItemInfo itemInfo = itemInfoArr.get(i);
                            if(itemInfo.isSelected()) {
                                EquipmentInfo equipInfo = (EquipmentInfo)itemInfo.getCustomObject();
                                CheckListEquipmentInfo checkListEquipmentInfo = new CheckListEquipmentInfo(CheckListEquipmentInfo.EquipType.EQUIP_TYPE_HOLDINGS,
                                        equipInfo.getId(), equipInfo.getName(), false);
                                mCheckListGroupInfo.addCheckListEquipInfo(checkListEquipmentInfo, 0);
                                ++addCount;
                            }
                        }

                        if(addCount > 0) {
                            mCheckListAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onCancel() {
                    if (mHoldingsEquipListSelectPopup != null) {
                        mActivityLayout.removeView(mHoldingsEquipListSelectPopup);
                        mHoldingsEquipListSelectPopup = null;
                    }
                }
            });

            mActivityLayout.addView(mHoldingsEquipListSelectPopup);

            mHoldingsEquipListSelectPopup.setMenuItemInfo(MultiSelectPopup.Type.TYPE_NORMAL, null, arrItem);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * @brief :
     * @param bitmap
     */
    private void setGroupThemeImage(Bitmap bitmap, boolean isRecycle) {
        if(mIsThemeImg && mIsRecycleThemeImg)
            RecycleUtils.imageViewRecycle(mGroupImage);

        mIsRecycleThemeImg = isRecycle;

        mGroupImage.setImageBitmap(bitmap);
        mIsThemeImg = true;
    }

    /**
     * @brief : 앨범 사진 호출 처리
     */
    private void callAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        int requestCode = GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_CHECK_LIST_GROUP_REGISTER_ALBUM;

        startActivityForResult(intent, requestCode);
    }

    /**
     * @brief : 앨범 사진으로 부터 이미지 셋팅 처리
     * @param imageUri
     */
    private void setAlbumThemeImage(Uri imageUri) {
        Bitmap bitmap = Util.getBitmapFromGallery(this, imageUri, THEME_IMAGE_SIZE);
        if(bitmap == null) return;

        setGroupThemeImage(bitmap, true);
        mCheckListGroupInfo.setIsDefaultImage(false);
    }


    /**
     * @brief : 카메라 촬영 호출 처리
     */
    private void callCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(android.os.Environment.getExternalStorageDirectory(), CAMERA_CAPTURE_IMAGE_NAME);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

        int requestCode = GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_CHECK_LIST_GROUP_REGISTER_CAMERA;

        startActivityForResult(intent, requestCode);
    }

    /**
     * @brief : 카메라 사진으로 부터 이미지 셋팅 처리
     */
    private void setCameraThemeImage() {
        Bitmap bitmap = Util.getBitmapFromCamera(this, CAMERA_CAPTURE_IMAGE_NAME, THEME_IMAGE_SIZE);
        if(bitmap == null) return;

        setGroupThemeImage(bitmap, true);
        mCheckListGroupInfo.setIsDefaultImage(false);
    }

    /**
     * @brief : 필수 입력 항목에 대한 유효성 체크
     * @return
     */
    private boolean isValidRequiredItem() {

        Bitmap themeBitmap = null;
        if(mGroupImage.getDrawable() instanceof BitmapDrawable) {
            BitmapDrawable drawable = (BitmapDrawable) mGroupImage.getDrawable();
            themeBitmap = drawable.getBitmap();
        }

        if(!mIsThemeImg || themeBitmap == null) {
            Toast.makeText(this, getString(R.string.required_theme_image), Toast.LENGTH_LONG).show();
            return false;
        }

        if(!Util.isValid(mGroupNameEditTxt.getText().toString())) {
            guideInputRequiredItem(mGroupNameEditTxt, getString(R.string.group_theme_name));
            return false;
        }
        if(mCheckListGroupInfo.getArrCheckListEquipInfo().size() == 0) {
            Toast.makeText(this, getString(R.string.required_check_list), Toast.LENGTH_LONG).show();
            return false;
        }

        boolean isFieldAlarm = mFieldAlarmBtn.isSelected();
        String fieldDate =  mFieldDateTxtView.getText().toString();
        if(isFieldAlarm && !Util.isValid(fieldDate))
        {
            guideInputRequiredItem(mFieldDateTxtView, getString(R.string.field_date));
            return false;
        }
        return true;
    }

    /**
     * @brief : 필수 입력 항목에 대한 가이드 표시
     * @param view
     * @param itemName
     */
    private void guideInputRequiredItem(View view, String itemName) {
        if(view != null) {
            view.requestFocus();
        }
        Toast.makeText(this, itemName + " " + getString(R.string.required_field_input), Toast.LENGTH_LONG).show();
    }

    /**
     * @brief : 체크리스트 그룹 등록 처리
     */
    private void registeCheckListGroup() {
        showLoadingDialog();
        String groupName = mGroupNameEditTxt.getText().toString();
        String fieldDate = mFieldDateTxtView.getText().toString();
        boolean isFieldAlarm = mFieldAlarmBtn.isSelected();

        // 그룹 테마 이미지
        Bitmap themeBitmap = null;
        if(mGroupImage.getDrawable() instanceof BitmapDrawable) {
            BitmapDrawable drawable = (BitmapDrawable) mGroupImage.getDrawable();
            themeBitmap = drawable.getBitmap();
        }
        if(themeBitmap != null)
            mCheckListGroupInfo.setGroupImage(themeBitmap);

        // 그룹 명
        if(Util.isValid(groupName))
            mCheckListGroupInfo.setGroupName(groupName);

        // 출정 날짜
        if(Util.isValid(fieldDate))
            mCheckListGroupInfo.setFieldDate(fieldDate);
        else
            mCheckListGroupInfo.setFieldDate("");

        // 출정 알림
        mCheckListGroupInfo.setIsFieldAlarm(isFieldAlarm);

        // 정렬 순번
        mCheckListGroupInfo.setOrderNum(mCheckListGroupOrderNum);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mMode == CheckListGroupRegMode.MODE_REGISTRATION) {   // 등록 Mode
                    // CheckList 그룹 등록 처리
                    if (DataBaseQuery.insertCheckListGroupInfo(mContext, mCheckListGroupInfo)) {
                        MainApplication.setCheckListGroupInfoArr(DataBaseQuery.getAllCheckListGroupInfo(mContext));

                        // 등록의 경우에 id는 DB insert후에 생기므로 id 추출을 위함
                        CheckListGroupInfo updateGroupInfo = DataBaseQuery.getCheckListGroupInfoWithOrderNum(mContext, String.valueOf(mCheckListGroupOrderNum));
                        registeFieldAlarm(updateGroupInfo.getId());
                        setResult(RESULT_OK);
                    } else {
                        setResult(RESULT_CANCELED);
                    }
                } else if (mMode == CheckListGroupRegMode.MODE_VIEW) {      // 보기 Mode
                    // do nothing...
                } else if (mMode == CheckListGroupRegMode.MODE_MODIFY) {    // 수정 Mode
                    // CheckList 그룹 수정 처리
                    if (DataBaseQuery.updateCheckListGroupInfo(mContext, mCheckListGroupInfo)) {
                        MainApplication.setCheckListGroupInfoArr(DataBaseQuery.getAllCheckListGroupInfo(mContext));
                        registeFieldAlarm(mCheckListGroupInfo.getId());
                        setResult(RESULT_OK);
                    } else {
                        setResult(RESULT_CANCELED);
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingDialog();
                        if(mMode == CheckListGroupRegMode.MODE_MODIFY) {

                            // 수정의 경우, 보기 Mode로 변경하여 화면 갱신
                            mMode = CheckListGroupRegMode.MODE_VIEW;
                            setData(false);
                            setViewUI();
                        }
                        else {
                            finish();
                        }
                    }
                });
            }
        };
        new Thread(runnable).start();
    }

    /**
     * @brief : 출정 알림 등록
     */
    private void registeFieldAlarm(int groupId) {
        // 기존 알림이 있는 경우도 있으니 해제 후, 처리
        releaseFieldAlarm(groupId, mContext);

        String fieldDate = mFieldDateTxtView.getText().toString();

        if(Util.isValid(fieldDate)) {
            // 출정 당일 오전 9시에 출정 enjoy 알림을 주기 위해 등록
            registeFieldDayAlarm(mContext, groupId, fieldDate);

            if(mCheckListGroupInfo.isFieldAlarm()) {
                // 출정 전날 설정된 시간(Default는 오후 8시)에 체크리스트 확인 알림을 주기 위해 등록
                registeFieldOneDayAgoAlarm(mContext, groupId, fieldDate);
            }
        }
    }

    /**
     * @brief : 출정 알림 등록 (출정 당일 출정 enjoy 알림용)
     * @param context
     * @param groupId
     * @param fieldDate
     */
    public static void registeFieldDayAlarm(Context context, int groupId, String fieldDate) {
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(context.getApplicationContext(), FieldAlarmReceiver.class);
        String strUri = FieldAlarmReceiver.SCHEME_FIELD_ALARM + "://" + FieldAlarmReceiver.HOST_FIELD_DAY + "?" + FieldAlarmReceiver.PARAM_KEY_ALARM_ID +"=" + groupId;
        intent.setData(Uri.parse(strUri));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);

        int year, month, day;
        String[] fieldDateSplit = fieldDate.split("\\.");
        year = Integer.valueOf(fieldDateSplit[0]);
        month = Integer.valueOf(fieldDateSplit[1]) - 1;
        day = Integer.valueOf(fieldDateSplit[2]);

        Calendar fieldDayCalendar = Calendar.getInstance();
        // 출정일, 오전 9시에 알림 셋팅
        fieldDayCalendar.set(year, month, day, 9, 0);
//        fieldDayCalendar.set(year, month, day, 13, 45);

        Date fieldDay1 = fieldDayCalendar.getTime();
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//        Log.d("MO_", "registeFieldDayAlarm field day:" + formatter1.format(fieldDay1));

        // 현재보다 과거로 셋팅한 경우 등록하지 않도록 처리
        Calendar curCalendar = Calendar.getInstance();
        Date curDay = curCalendar.getTime();
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//        Log.d("MO_", "registeFieldDayAlarm cur day:" + formatter2.format(curDay));


        if(fieldDayCalendar.getTimeInMillis() - curCalendar.getTimeInMillis() <= 0) {
           return;
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alarmMgr.setAlarmClock(new AlarmManager.AlarmClockInfo(fieldDayCalendar.getTimeInMillis(), pendingIntent), pendingIntent);
        }
        else {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, fieldDayCalendar.getTimeInMillis(), pendingIntent);
        }

        Date fieldDay = fieldDayCalendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//        Log.d("MO_", "registeFieldDayAlarm field day:" + formatter.format(fieldDay) + " id:" + groupId);
    }

    /**
     * @brief : 출정 알림 등록 (출정 전날 체크리스트 확인 알림용)
     * @param context
     * @param groupId
     * @param fieldDate
     */
    public static void registeFieldOneDayAgoAlarm(Context context, int groupId, String fieldDate) {
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(context.getApplicationContext(), FieldAlarmReceiver.class);
        String strUri = FieldAlarmReceiver.SCHEME_FIELD_ALARM + "://" + FieldAlarmReceiver.HOST_ONE_DAY_AGO + "?" + FieldAlarmReceiver.PARAM_KEY_ALARM_ID +"=" + groupId;
        intent.setData(Uri.parse(strUri));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);

        int year, month, day;
        String[] fieldDateSplit = fieldDate.split("\\.");
        year = Integer.valueOf(fieldDateSplit[0]);
        month = Integer.valueOf(fieldDateSplit[1]) - 1;
        day = Integer.valueOf(fieldDateSplit[2]);

        // default는 오후 8시
        int hour = 20;
        int minute = 0;
        String fieldAlarmTime = DataPreferenceManager.getFieldAlarmTime();
        if(Util.isValid(fieldAlarmTime) && fieldAlarmTime.split(":").length == 2) {
            String[] timeSplit = fieldAlarmTime.split(":");
            hour = Integer.valueOf(timeSplit[0]);
            minute = Integer.valueOf(timeSplit[1]);
        }

        Calendar oneDayAgoCalendar = Calendar.getInstance();
        // 출정 전일, 설정된 시간에 맞게 알림 셋팅
        oneDayAgoCalendar.set(year, month, day, hour, minute);
        oneDayAgoCalendar.add(Calendar.DATE, -1);

        Date fieldDay1 = oneDayAgoCalendar.getTime();
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//        Log.d("MO_", "registeFieldOneDayAgoAlarm one day ago:" + formatter1.format(fieldDay1));

        // 현재보다 과거로 셋팅한 경우 등록하지 않도록 처리
        Calendar curCalendar = Calendar.getInstance();
        Date curDay = curCalendar.getTime();
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//        Log.d("MO_", "registeFieldOneDayAgoAlarm cur day:" + formatter2.format(curDay));

        if(oneDayAgoCalendar.getTimeInMillis() - curCalendar.getTimeInMillis() <= 0) {
            return;
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alarmMgr.setAlarmClock(new AlarmManager.AlarmClockInfo(oneDayAgoCalendar.getTimeInMillis(), pendingIntent), pendingIntent);
        }
        else {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, oneDayAgoCalendar.getTimeInMillis(), pendingIntent);
        }

        Date oneDayAgo = oneDayAgoCalendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
//        Log.d("MO_", "registeFieldOneDayAgoAlarm one day ago:" + formatter.format(oneDayAgo) + " id:" + groupId);
    }

    /**
     * @brief : 출정 알림 해제 (출정 전날,당일 모두 해제 처리)
     * @param groupId
     * @param context
     */
    public static void releaseFieldAlarm(int groupId, Context context) {
//        Log.d("MO_", "releaseFieldAlarm");
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        Intent intent_1 = new Intent(context.getApplicationContext(), FieldAlarmReceiver.class);
        String strUri_1 = FieldAlarmReceiver.SCHEME_FIELD_ALARM + "://" + FieldAlarmReceiver.HOST_FIELD_DAY + "?" + FieldAlarmReceiver.PARAM_KEY_ALARM_ID +"=" + groupId;
        intent_1.setData(Uri.parse(strUri_1));
        PendingIntent pendingIntent_1 = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent_1, 0);
        alarmMgr.cancel(pendingIntent_1);

        Intent intent_2 = new Intent(context.getApplicationContext(), FieldAlarmReceiver.class);
        String strUri_2 = FieldAlarmReceiver.SCHEME_FIELD_ALARM + "://" + FieldAlarmReceiver.HOST_ONE_DAY_AGO + "?" + FieldAlarmReceiver.PARAM_KEY_ALARM_ID +"=" + groupId;
        intent_2.setData(Uri.parse(strUri_2));
        PendingIntent pendingIntent_2 = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent_2, 0);
        alarmMgr.cancel(pendingIntent_2);
    }

    /**
     * @brief : 출정 알림 해제 (출정 전날 해제 처리)
     * @param groupId
     * @param context
     */
    public static void releaseFieldOneDayAgoAlarm(int groupId, Context context) {
//        Log.d("MO_", "releaseFieldOneDayAgoAlarm");
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(context.getApplicationContext(), FieldAlarmReceiver.class);
        String strUri = FieldAlarmReceiver.SCHEME_FIELD_ALARM + "://" + FieldAlarmReceiver.HOST_ONE_DAY_AGO + "?" + FieldAlarmReceiver.PARAM_KEY_ALARM_ID +"=" + groupId;
        intent.setData(Uri.parse(strUri));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);
        alarmMgr.cancel(pendingIntent);
    }

    /**
     * @brief : 수정 취소 팝업
     */
    private void createDialogModificationCancel() {
        AlertManager.alertMessageDefault2Button(this, R.string.cancel_modification, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }, getString(R.string.yes), null, getString(R.string.no), true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_CHECK_LIST_GROUP_REGISTER_ALBUM:
                setAlbumThemeImage(data.getData());
                break;

            case GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_CHECK_LIST_GROUP_REGISTER_CAMERA:
                setCameraThemeImage();
                break;
        }
    }


    private class CheckListAdapter extends BaseAdapter {
        boolean mIsEditMode = false;

        @Override
        public int getCount() {
            return mCheckListGroupInfo.getArrCheckListEquipInfo().size();
        }

        @Override
        public Object getItem(int position) {
            return mCheckListGroupInfo.getArrCheckListEquipInfo().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;

            if(convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.adapter_check_list_row, null);
                Util.setGlobalFont(convertView);
                viewHolder = new ViewHolder();

                viewHolder.mRowLayout = (ViewGroup)convertView.findViewById(R.id.check_list_row_layout);
                viewHolder.mRowDivideView = convertView.findViewById(R.id.check_list_row_divide_view);
                viewHolder.mCheckLayout = (ViewGroup)convertView.findViewById(R.id.check_list_row_check_layout);
                viewHolder.mCheckBtn = (Button)convertView.findViewById(R.id.check_list_row_check_button);
                viewHolder.mDeleteLayout = (ViewGroup)convertView.findViewById(R.id.check_list_row_delete_layout);
                viewHolder.mDeleteBtn = (Button)convertView.findViewById(R.id.check_list_row_delete_button);
                viewHolder.mItemTxtView =  (TextView)convertView.findViewById(R.id.check_list_row_item_text_view);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            CheckListEquipmentInfo checkListEquipInfo = mCheckListGroupInfo.getArrCheckListEquipInfo().get(position);
            viewHolder.mItemTxtView.setText(checkListEquipInfo.getEquipName());

            if(checkListEquipInfo.isCheck())
                viewHolder.mCheckBtn.setSelected(true);
            else
                viewHolder.mCheckBtn.setSelected(false);

            if(mMode == CheckListGroupRegMode.MODE_VIEW)
                viewHolder.mCheckBtn.setEnabled(false);
            else
                viewHolder.mCheckBtn.setEnabled(true);

            if(mIsEditMode) {
                viewHolder.mRowLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border_table_contents_delete));
                viewHolder.mRowDivideView.setVisibility(View.GONE);
                viewHolder.mCheckLayout.setVisibility(View.GONE);
                viewHolder.mCheckBtn.setVisibility(View.GONE);
                viewHolder.mDeleteLayout.setVisibility(View.VISIBLE);
                viewHolder.mDeleteBtn.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.mRowLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.border_table_contents));
                viewHolder.mRowDivideView.setVisibility(View.VISIBLE);
                viewHolder.mCheckLayout.setVisibility(View.VISIBLE);
                viewHolder.mCheckBtn.setVisibility(View.VISIBLE);
                viewHolder.mDeleteLayout.setVisibility(View.GONE);
                viewHolder.mDeleteBtn.setVisibility(View.GONE);
            }


            final int _position = position;
            viewHolder.mCheckLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mIsEditMode) {
                        setToggleCheck(_position);
                    }
                }
            });
            viewHolder.mCheckBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mIsEditMode) {
                        setToggleCheck(_position);
                    }
                }
            });
            viewHolder.mDeleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mIsEditMode) {
                        deleteCheckListItem(_position);
                    }
                }
            });
            viewHolder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mIsEditMode) {
                        deleteCheckListItem(_position);
                    }
                }
            });

            return convertView;
        }

        private void setToggleCheck(int position) {
            if(mMode == CheckListGroupRegMode.MODE_VIEW) return;

            CheckListEquipmentInfo checkListEquipInfo = mCheckListGroupInfo.getArrCheckListEquipInfo().get(position);
            checkListEquipInfo.setIsCheck(!checkListEquipInfo.isCheck());

            mCheckListAdapter.notifyDataSetChanged();
        }

        private void deleteCheckListItem(int position) {
            mCheckListGroupInfo.getArrCheckListEquipInfo().remove(position);
            mCheckListAdapter.notifyDataSetChanged();
        }

        private void deleteAllCheckListItem() {
            mCheckListGroupInfo.getArrCheckListEquipInfo().clear();
            mCheckListAdapter.notifyDataSetChanged();
        }

        private class ViewHolder {
            ViewGroup   mRowLayout;
            View        mRowDivideView;
            ViewGroup   mCheckLayout;
            Button      mCheckBtn;
            ViewGroup   mDeleteLayout;
            Button      mDeleteBtn;
            TextView    mItemTxtView;
        }
    }
}
