package com.youngmo.chun.mycampingitem.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.youngmo.chun.mycampingitem.MainApplication;
import com.youngmo.chun.mycampingitem.R;
import com.youngmo.chun.mycampingitem.db.DataBaseQuery;
import com.youngmo.chun.mycampingitem.define.GlobalDefine;
import com.youngmo.chun.mycampingitem.define.IntentExtraNameDefine;
import com.youngmo.chun.mycampingitem.define.LocalEventDefine;
import com.youngmo.chun.mycampingitem.manager.AlertManager;
import com.youngmo.chun.mycampingitem.model.CategoryInfo;
import com.youngmo.chun.mycampingitem.model.EquipmentInfo;
import com.youngmo.chun.mycampingitem.model.SingleSelectPopupItemInfo;
import com.youngmo.chun.mycampingitem.utils.RecycleUtils;
import com.youngmo.chun.mycampingitem.utils.Util;
import com.youngmo.chun.mycampingitem.view.InputPopup;
import com.youngmo.chun.mycampingitem.view.SingleSelectPopup;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class EquipmentRegisterActivity extends BaseActivity implements View.OnFocusChangeListener {
    private final int    UPLOAD_IMAGE_SIZE          = 600;
    private final String CAMERA_CAPTURE_IMAGE_NAME  = "temp.jpg";


    private static final String INSTANCE_SAVE_KEY_UPLOAD_BITMAP_1 = "uploadBitmap1";
    private static final String INSTANCE_SAVE_KEY_UPLOAD_BITMAP_2 = "uploadBitmap2";
    private static final String INSTANCE_SAVE_KEY_UPLOAD_BITMAP_3 = "uploadBitmap3";

    /** Mode 정의 */
    public enum EquipmentRegMode {
        /** 등록 Mode */
        MODE_REGISTRATION,
        /** 수정 Mode */
        MODE_MODIFY,
    };

    private enum UploadNum{
        UPLOAD_1,
        UPLOAD_2,
        UPLOAD_3
    };

    private ViewGroup           mActivityLayout;
    private TextView            mTopNaviTitelTxtView;
    private Button              mTopNaviMenuBtn;
    private ImageView           mUpload1ImgView;
    private ImageView           mUploadDelete1ImgView;
    private ImageView           mUpload2ImgView;
    private ImageView           mUploadDelete2ImgView;
    private ImageView           mUpload3ImgView;
    private ImageView           mUploadDelete3ImgView;

    private TextView            mCategoryTxtView;
    private EditText            mProductNameEditTxt;
    private EditText            mProductMakerEditTxt;
    private TextView            mPurchaseDateTxtView;
    private EditText            mPurchasePriceEditTxt;
    private EditText            mPurchaseAmountEditTxt;
    private EditText            mCommentEditTxt;

    private EditText            mCurFocusEditTxt;
    private SingleSelectPopup   mCategorySelectPopup = null;
    private int                 mCategorySelectedIndex = -1;
    private SingleSelectPopup   mImgAttachTypeSelectPopup = null;
    private InputPopup          mCategoryInputPopup = null;

    private EquipmentRegMode    mMode;
    private int                 mEquipmentInfoIndex;
    private boolean             mIsSetUpload1 = false;
    private boolean             mIsSetUpload2 = false;
    private boolean             mIsSetUpload3 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_register);
        loadAdMob();
        init();
    }

    @Override
    public void onBackPressed() {
        if(mCategorySelectPopup != null) {
            mActivityLayout.removeView(mCategorySelectPopup);
            mCategorySelectPopup = null;
        }
        else if(mCategoryInputPopup != null) {
            mActivityLayout.removeView(mCategoryInputPopup);
            mCategoryInputPopup = null;

        }
        else if(mImgAttachTypeSelectPopup != null) {
            mActivityLayout.removeView(mImgAttachTypeSelectPopup);
            mImgAttachTypeSelectPopup = null;
        }
        else if(mMode == EquipmentRegMode.MODE_MODIFY) {
            createDialogModificationCancel();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
//        if(mMode == EquipmentRegMode.MODE_REGISTRATION) {
//            RecycleUtils.imageViewRecycle(mUpload1ImgView);
//            RecycleUtils.imageViewRecycle(mUpload2ImgView);
//            RecycleUtils.imageViewRecycle(mUpload3ImgView);
//        }

        super.onDestroy();
    }

    /**
     * 초기 처리
     */
    private void init() {
        setViewReference();
        setViewEventListener();
        setData();
        setViewUI();
    }

    /**
     * 참조할 View ID 맵핑
     */
    private void setViewReference() {
        mTopNaviTitelTxtView = (TextView)findViewById(R.id.top_navigator_bar_title);
        mTopNaviMenuBtn = (Button)findViewById(R.id.top_navigator_bar_menu_button);
        mActivityLayout = (ViewGroup)findViewById(R.id.equipment_register_layout);
        mUpload1ImgView = (ImageView)findViewById(R.id.equipment_register_upload_1);
        mUploadDelete1ImgView = (ImageView)findViewById(R.id.equipment_register_upload_delete_1);
        mUpload2ImgView = (ImageView)findViewById(R.id.equipment_register_upload_2);
        mUploadDelete2ImgView = (ImageView)findViewById(R.id.equipment_register_upload_delete_2);
        mUpload3ImgView = (ImageView)findViewById(R.id.equipment_register_upload_3);
        mUploadDelete3ImgView = (ImageView)findViewById(R.id.equipment_register_upload_delete_3);
        mCategoryTxtView = (TextView)findViewById(R.id.equipment_register_category_text_view);
        mProductNameEditTxt = (EditText)findViewById(R.id.equipment_register_product_name_edit_text);
        mProductMakerEditTxt = (EditText)findViewById(R.id.equipment_register_product_maker_edit_text);
        mPurchaseDateTxtView = (TextView)findViewById(R.id.equipment_register_purchase_date_text_view);
        mPurchasePriceEditTxt = (EditText)findViewById(R.id.equipment_register_purchase_price_edit_text);
        mPurchaseAmountEditTxt = (EditText)findViewById(R.id.equipment_register_purchase_amount_edit_text);
        mCommentEditTxt = (EditText)findViewById(R.id.equipment_register_comment_edit_text);
    }

    /**
     * View에 이벤트 등록
     */
    private void setViewEventListener() {
//        setViewEventListenerTopNaviBack();
        findViewById(R.id.top_navigator_bar_back_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMode == EquipmentRegMode.MODE_MODIFY)
                    createDialogModificationCancel();
                else
                    finish();
            }
        });

        mUpload1ImgView.setTag(UploadNum.UPLOAD_1);
        mUpload1ImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUploadDelete1ImgView.getVisibility() == View.VISIBLE) {
                    onClickDeleteUpload(view, (UploadNum) view.getTag());
                } else {
                    onClickUpload(view, (UploadNum) view.getTag());
                }
            }
        });
        mUpload2ImgView.setTag(UploadNum.UPLOAD_2);
        mUpload2ImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUploadDelete2ImgView.getVisibility() == View.VISIBLE) {
                    onClickDeleteUpload(view, (UploadNum) view.getTag());
                } else {
                    onClickUpload(view, (UploadNum) view.getTag());
                }
            }
        });
        mUpload3ImgView.setTag(UploadNum.UPLOAD_3);
        mUpload3ImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUploadDelete3ImgView.getVisibility() == View.VISIBLE) {
                    onClickDeleteUpload(view, (UploadNum) view.getTag());
                } else {
                    onClickUpload(view, (UploadNum) view.getTag());
                }
            }
        });

        mProductNameEditTxt.setOnFocusChangeListener(this);
        mProductMakerEditTxt.setOnFocusChangeListener(this);
        mPurchasePriceEditTxt.setOnFocusChangeListener(this);
        mPurchaseAmountEditTxt.setOnFocusChangeListener(this);
        mCommentEditTxt.setOnFocusChangeListener(this);

        // '카테고리' 클릭 이벤트 처리
        mCategoryTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategorySelectPopup();
            }
        });

        // '구입 날짜' 클릭 이벤트 처리
        mPurchaseDateTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = mPurchaseDateTxtView.getText().toString();

                int year, month, day;

                GregorianCalendar calendar = new GregorianCalendar();
                if (Util.isValid(date) && date.split("\\.").length == 3) {
                    String[] dateSplit = date.split("\\.");

                    year = Integer.valueOf(dateSplit[0]);
                    month = Integer.valueOf(dateSplit[1]) - 1;
                    day = Integer.valueOf(dateSplit[2]);
                } else {

                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                }

                new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        String curDate = String.format("%d.%d.%d", year, monthOfYear + 1, dayOfMonth);

                        mPurchaseDateTxtView.setText(curDate);
                    }
                }, year, month, day).show();

                // 키보드 내리기
                if(mCurFocusEditTxt != null) {
                    Util.hideSoftKeyboard(mContext, mCurFocusEditTxt, true);
                }
            }
        });

        // '구입 가격'입력에 대한 이벤트 처리
        mPurchasePriceEditTxt.addTextChangedListener(new NumberFormatEditTextWatcher(mPurchasePriceEditTxt));

        // '구입 수량'입력에 대한 이벤트 처리
        mPurchaseAmountEditTxt.addTextChangedListener(new NumberFormatEditTextWatcher(mPurchaseAmountEditTxt));

        // '메모' 클릭 이벤트 처리
        mCommentEditTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (view.getId() == R.id.equipment_register_comment_edit_text) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        // 상단 네비게이션에 등록,수정에 대한 '완료'버튼 이벤트 처리
        mTopNaviMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidRequiredItem()) {
                    registeEquipment();
                }

            }
        });
    }

    /**
     * @brief : 데이터 관련 셋팅 작업
     */
    private void setData() {
        mMode = (EquipmentRegMode)getIntent().getSerializableExtra(IntentExtraNameDefine.REGISTRATION_MODE);
        mEquipmentInfoIndex = getIntent().getIntExtra(IntentExtraNameDefine.EQUIPMENT_INFO_INDEX, -1);
    }

    /**
     * @brief : View들의 UI구성요소 셋팅
     */
    private void setViewUI() {
        setViewUINavi();

        if(mMode == EquipmentRegMode.MODE_MODIFY) {
            if(mEquipmentInfoIndex >= 0) {
                EquipmentInfo equipmentInfo = MainApplication.getEquipmentInfoArr().get(mEquipmentInfoIndex);

                // 이미지
                if(equipmentInfo.getPicture1() != null) {
                    mUpload1ImgView.setImageBitmap(equipmentInfo.getPicture1());
                    mIsSetUpload1 = true;
                }
                else {
                    mIsSetUpload1 = false;
                }

                if(equipmentInfo.getPicture2() != null) {
                    mUpload2ImgView.setImageBitmap(equipmentInfo.getPicture2());
                    mIsSetUpload2 = true;
                }
                else {
                    mIsSetUpload2 = false;
                }

                if(equipmentInfo.getPicture3() != null) {
                    mUpload3ImgView.setImageBitmap(equipmentInfo.getPicture3());
                    mIsSetUpload3 = true;
                }
                else {
                    mIsSetUpload3 = false;
                }

                // 카테고리
                if(Util.isValid(equipmentInfo.getCategory())) {
                    mCategoryTxtView.setText(equipmentInfo.getCategory());

                    int index = 0;
                    for(CategoryInfo categoryInfo : MainApplication.getCategoryInfoArr()) {
                        if(categoryInfo.getName().equals(equipmentInfo.getCategory())) {
                            mCategorySelectedIndex = index;
                            break;
                        }
                        index++;
                    }
                }

                // 제품 명
                if(Util.isValid(equipmentInfo.getName()))
                    mProductNameEditTxt.setText(equipmentInfo.getName());

                // 제조 회사
                if(Util.isValid(equipmentInfo.getMaker()))
                    mProductMakerEditTxt.setText(equipmentInfo.getMaker());

                // 구입 날짜
                if(Util.isValid(equipmentInfo.getPurchaseDate())) {
                    mPurchaseDateTxtView.setText(equipmentInfo.getPurchaseDate());
                }

                // 구입 가격
                if(Util.isValid(equipmentInfo.getPurchasePrice()))
                    mPurchasePriceEditTxt.setText(equipmentInfo.getPurchasePrice());

                // 구입 수량
                if(Util.isValid(equipmentInfo.getPurchaseAmount()))
                    mPurchaseAmountEditTxt.setText(equipmentInfo.getPurchaseAmount());

                // 메모
                if(Util.isValid(equipmentInfo.getComment()))
                    mCommentEditTxt.setText(equipmentInfo.getComment());

            }
            else {
                finish();
            }
        }

        updateUploadStatus();
    }

    /**
     * @brief : 상단 네비게이션 영역 UI 표시 처리
     */
    private void setViewUINavi() {
        // 상단 네비게이션 영역 관련
        switch (mMode) {
            case MODE_REGISTRATION:
                mTopNaviTitelTxtView.setText(getString(R.string.equipment_registration));
                mTopNaviMenuBtn.setVisibility(View.VISIBLE);
                mTopNaviMenuBtn.setText(getString(R.string.completion));
                break;

            case MODE_MODIFY:
                mTopNaviTitelTxtView.setText(getString(R.string.equipment_modify));
                mTopNaviMenuBtn.setVisibility(View.VISIBLE);
                mTopNaviMenuBtn.setText(getString(R.string.completion));
                break;
        }
    }

    /**
     * @brief : 카테고리 선택을 위한 팝업 표시
     */
    private void showCategorySelectPopup() {
        if (mCategorySelectPopup == null) {
            mCategorySelectPopup = new SingleSelectPopup(mContext);
            mCategorySelectPopup.setListener(new SingleSelectPopup.SingleSelectPopupListener() {
                @Override
                public void onSelectedItem(int selectedIndex) {
                    if (mCategorySelectPopup != null) {
                        mCategorySelectedIndex = selectedIndex-1;
                        mActivityLayout.removeView(mCategorySelectPopup);
                        mCategorySelectPopup = null;

                        if(mCategorySelectedIndex == -1) {   // -1인 경우는 "카테고리 추가"의 경우
                            showCategoryInputPopup();
                        }
                        else {
                            ArrayList<CategoryInfo> categoryInfoArr = MainApplication.getCategoryInfoArr();
                            CategoryInfo categoryInfo = categoryInfoArr.get(mCategorySelectedIndex);
                            mCategoryTxtView.setText(categoryInfo.getName());

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
            arrPopupItem.add(0, new SingleSelectPopupItemInfo(getString(R.string.add_category), ContextCompat.getDrawable(mContext, R.drawable.btn_add)));

            mCategorySelectPopup.setMenuItemInfo(SingleSelectPopup.Type.TYPE_NORMAL_ADD_IMAGE, getString(R.string.category), arrPopupItem, mCategorySelectedIndex);

            // 키보드 내리기
            if(mCurFocusEditTxt != null)
                Util.hideSoftKeyboard(mContext, mCurFocusEditTxt, true);
        }
    }

    /**
     * @brief : 카테고리를 추가하기 위한 팝업 표시
     */
    private void showCategoryInputPopup() {
        if(mCategoryInputPopup == null) {
            mCategoryInputPopup = new InputPopup(mContext, null, new InputPopup.InputPopupListener() {
                @Override
                public void onInputText(final String inputText) {
                    if (mCategoryInputPopup != null) {
                        mActivityLayout.removeView(mCategoryInputPopup);
                        mCategoryInputPopup = null;
                    }

                    if(Util.isValid(inputText)) {
                        // 이미 동일한 카테고리가 있는지 체크!!
                        ArrayList<String> categoryNameArr = MainApplication.getCategoryNameArr();
                        for(String categoryName : categoryNameArr) {
                            if(categoryName.equals(inputText)) {
                                Toast.makeText(mContext, getString(R.string.already_exist_category), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        // 카테고리에 추가하고 카테고리 팝업 다시 띄움
                        showLoadingDialog();

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {

                                DataBaseQuery.insertCategory(mContext, inputText);
                                MainApplication.setCategoryInfoArr(DataBaseQuery.getAllCategory(mContext));
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideLoadingDialog();
                                        showCategorySelectPopup();
                                    }
                                });
                            }
                        };
                        new Thread(runnable).start();
                    }
                }

                @Override
                public void onCancel() {
                    if (mCategoryInputPopup != null) {
                        mActivityLayout.removeView(mCategoryInputPopup);
                        mCategoryInputPopup = null;
                    }
                }
            });
            mActivityLayout.addView(mCategoryInputPopup);
        }
    }

    private void onClickDeleteUpload(View view, final UploadNum uploadNum) {
        AlertManager.alertMessageDefault2Button(this, R.string.delete_upload_image, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteUploadImage(uploadNum);
            }
        }, null, true);

        // 키보드 내리기
        if(mCurFocusEditTxt != null)
            Util.hideSoftKeyboard(mContext, mCurFocusEditTxt, true);
    }

    /**
     * @brief : 이미지 업로드 선택 처리
     * @param view
     * @param uploadNum
     */
    private void onClickUpload(View view, final UploadNum uploadNum) {
        if (mImgAttachTypeSelectPopup == null) {
            mImgAttachTypeSelectPopup = new SingleSelectPopup(mContext);
            mImgAttachTypeSelectPopup.setListener(new SingleSelectPopup.SingleSelectPopupListener() {
                @Override
                public void onSelectedItem(int selectedIndex) {
                    if (mImgAttachTypeSelectPopup != null) {
                        mActivityLayout.removeView(mImgAttachTypeSelectPopup);
                        mImgAttachTypeSelectPopup = null;

                        switch (selectedIndex) {
                            case 0: // '앨범 사진 업로드' 의 경우
                                callAlbum(uploadNum);
                                break;

                            case 1: // '카메라 촬영 업로드' 의 경우
                                callCamera(uploadNum);
                                break;
                        }
                    }
                }

                @Override
                public void onCancel() {
                    if (mImgAttachTypeSelectPopup != null) {
                        mActivityLayout.removeView(mImgAttachTypeSelectPopup);
                        mImgAttachTypeSelectPopup = null;
                    }
                }
            });
            mActivityLayout.addView(mImgAttachTypeSelectPopup);

            ArrayList<SingleSelectPopupItemInfo> popupItemArr = new ArrayList<>();
            popupItemArr.add(new SingleSelectPopupItemInfo(getString(R.string.image_album_upload)));
            popupItemArr.add(new SingleSelectPopupItemInfo(getString(R.string.image_camera_upload)));
            mImgAttachTypeSelectPopup.setMenuItemInfo(SingleSelectPopup.Type.TYPE_NORMAL, getString(R.string.image_attach), popupItemArr, -1);

            // 키보드 내리기
            if(mCurFocusEditTxt != null)
                Util.hideSoftKeyboard(mContext, mCurFocusEditTxt, true);
        }
    }


    /**
     * @brief : 앨범 사진 호출 처리
     * @param uploadNum
     */
    private void callAlbum(UploadNum uploadNum) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        int requestCode = GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_ALBUM_1;

        switch (uploadNum) {
            case UPLOAD_1:
                requestCode = GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_ALBUM_1;
                break;

            case UPLOAD_2:
                requestCode = GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_ALBUM_2;
                break;

            case UPLOAD_3:
                requestCode = GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_ALBUM_3;
                break;
        }

        startActivityForResult(intent, requestCode);
    }

    /**
     * @brief : 앨범 사진으로 부터 이미지 셋팅 처리
     * @param imageUri
     * @param uploadNum
     */
    private void setAlbumUploadImage(Uri imageUri, UploadNum uploadNum) {
        Bitmap bitmap = Util.getBitmapFromGallery(this, imageUri, UPLOAD_IMAGE_SIZE);

        if(bitmap == null) return;

        switch (uploadNum) {
            case UPLOAD_1:
                if(mMode == EquipmentRegMode.MODE_REGISTRATION) {
                    RecycleUtils.imageViewRecycle(mUpload1ImgView);
                }
                mUpload1ImgView.setImageBitmap(bitmap);
                mIsSetUpload1 = true;
                break;

            case UPLOAD_2:
                if(mMode == EquipmentRegMode.MODE_REGISTRATION) {
                    RecycleUtils.imageViewRecycle(mUpload2ImgView);
                }
                mUpload2ImgView.setImageBitmap(bitmap);
                mIsSetUpload2 = true;
                break;

            case UPLOAD_3:
                if(mMode == EquipmentRegMode.MODE_REGISTRATION) {
                    RecycleUtils.imageViewRecycle(mUpload3ImgView);
                }
                mUpload3ImgView.setImageBitmap(bitmap);
                mIsSetUpload3 = true;
                break;
        }

        updateUploadStatus();
    }

    /**
     * @brief : 카메라 촬영 호출 처리
     * @param uploadNum
     */
    private void callCamera(UploadNum uploadNum) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(android.os.Environment.getExternalStorageDirectory(), CAMERA_CAPTURE_IMAGE_NAME);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

        int requestCode = GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_CAMERA_1;

        switch (uploadNum) {
            case UPLOAD_1:
                requestCode = GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_CAMERA_1;
                break;

            case UPLOAD_2:
                requestCode = GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_CAMERA_2;
                break;

            case UPLOAD_3:
                requestCode = GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_CAMERA_3;
                break;
        }

        startActivityForResult(intent, requestCode);
    }

    /**
     * @brief : 카메라 사진으로 부터 이미지 셋팅 처리
     * @param uploadNum
     */
    private void setCameraUploadImage(UploadNum uploadNum) {
        Bitmap bitmap = Util.getBitmapFromCamera(this, CAMERA_CAPTURE_IMAGE_NAME, UPLOAD_IMAGE_SIZE);
        if(bitmap == null) return;

        switch (uploadNum) {
            case UPLOAD_1:
                if(mMode == EquipmentRegMode.MODE_REGISTRATION) {
                    RecycleUtils.imageViewRecycle(mUpload1ImgView);
                }
                mUpload1ImgView.setImageBitmap(bitmap);
                mIsSetUpload1 = true;
                break;

            case UPLOAD_2:
                if(mMode == EquipmentRegMode.MODE_REGISTRATION) {
                    RecycleUtils.imageViewRecycle(mUpload2ImgView);
                }
                mUpload2ImgView.setImageBitmap(bitmap);
                mIsSetUpload2 = true;
                break;

            case UPLOAD_3:
                if(mMode == EquipmentRegMode.MODE_REGISTRATION) {
                    RecycleUtils.imageViewRecycle(mUpload3ImgView);
                }
                mUpload3ImgView.setImageBitmap(bitmap);
                mIsSetUpload3 = true;
                break;
        }

        updateUploadStatus();
    }

    /**
     * @brief : Uploade 이미지 삭제 처리
     * @param uploadNum
     */
    private void deleteUploadImage(UploadNum uploadNum) {
        switch (uploadNum) {
            case UPLOAD_1:
                mUploadDelete1ImgView.setVisibility(View.GONE);
                break;

            case UPLOAD_2:
                mUploadDelete2ImgView.setVisibility(View.GONE);
                break;

            case UPLOAD_3:
                mUploadDelete2ImgView.setVisibility(View.GONE);
                break;
        }

        if(mUploadDelete1ImgView.getVisibility() == View.GONE) {    // 이미지 1을 삭제하는 경우
            if(mMode == EquipmentRegMode.MODE_REGISTRATION) {
                RecycleUtils.imageViewRecycle(mUpload1ImgView);
            }
            mUpload1ImgView.setImageBitmap(null);
            mIsSetUpload1 = false;

            if(mUploadDelete2ImgView.getVisibility() == View.VISIBLE) {
                // 이미지 2를 1로 이동.
                if(mUpload2ImgView.getDrawable() instanceof BitmapDrawable) {
                    BitmapDrawable drawable = (BitmapDrawable)mUpload2ImgView.getDrawable();
                    if(drawable.getBitmap() != null) {
                        mUpload1ImgView.setImageBitmap(drawable.getBitmap());
                        mIsSetUpload1 = true;
                        mUpload2ImgView.setImageBitmap(null);
                        mIsSetUpload2 = false;
                        mUpload2ImgView.setEnabled(true);
                        mUploadDelete2ImgView.setVisibility(View.GONE);
                    }
                }
            }
        }

        if(mUploadDelete2ImgView.getVisibility() == View.GONE) {    // 이미지 2를 삭제하는 경우
            if(mMode == EquipmentRegMode.MODE_REGISTRATION) {
                RecycleUtils.imageViewRecycle(mUpload2ImgView);
            }
            mUpload2ImgView.setImageBitmap(null);
            mIsSetUpload2 = false;

            if(mUploadDelete3ImgView.getVisibility() == View.VISIBLE) {
                // 이미지 3을 2로 이동.
                if(mUpload3ImgView.getDrawable() instanceof BitmapDrawable) {
                    BitmapDrawable drawable = (BitmapDrawable)mUpload3ImgView.getDrawable();
                    if(drawable.getBitmap() != null) {
                        mUpload2ImgView.setImageBitmap(drawable.getBitmap());
                        mIsSetUpload2 = true;
                        mUpload3ImgView.setImageBitmap(null);
                        mIsSetUpload3 = false;
                        mUpload3ImgView.setEnabled(true);
                        mUploadDelete3ImgView.setVisibility(View.GONE);
                    }
                }
            }
        }

        if(mUploadDelete3ImgView.getVisibility() == View.GONE) {    // 이미지 3을 삭제하는 경우
            if(mMode == EquipmentRegMode.MODE_REGISTRATION) {
                RecycleUtils.imageViewRecycle(mUpload3ImgView);
            }
            mUpload3ImgView.setImageBitmap(null);
            mIsSetUpload3 = false;
        }

        updateUploadStatus();

    }

    /**
     * @brief : 이미지 Upload 상태 갱신
     */
    private void updateUploadStatus() {
        Bitmap upload1Bitmap = null;
        Bitmap upload2Bitmap = null;
        Bitmap upload3Bitmap = null;

        if(mIsSetUpload1 && mUpload1ImgView.getDrawable() instanceof BitmapDrawable) {
            BitmapDrawable drawable = (BitmapDrawable) mUpload1ImgView.getDrawable();
            upload1Bitmap = drawable.getBitmap();
        }
        if(mIsSetUpload2 && mUpload2ImgView.getDrawable() instanceof BitmapDrawable) {
            BitmapDrawable drawable = (BitmapDrawable) mUpload2ImgView.getDrawable();
            upload2Bitmap = drawable.getBitmap();
        }
        if(mIsSetUpload3 && mUpload3ImgView.getDrawable() instanceof BitmapDrawable) {
            BitmapDrawable drawable = (BitmapDrawable) mUpload3ImgView.getDrawable();
            upload3Bitmap = drawable.getBitmap();
        }

        if(upload1Bitmap != null && upload2Bitmap != null && upload3Bitmap != null) {
            mUpload1ImgView.setEnabled(true);
            mUploadDelete1ImgView.setVisibility(View.VISIBLE);
            mUpload2ImgView.setEnabled(true);
            mUploadDelete2ImgView.setVisibility(View.VISIBLE);
            mUpload3ImgView.setEnabled(true);
            mUploadDelete3ImgView.setVisibility(View.VISIBLE);
        }
        else if(upload1Bitmap != null && upload2Bitmap != null && upload3Bitmap == null) {
            mUpload1ImgView.setEnabled(true);
            mUploadDelete1ImgView.setVisibility(View.VISIBLE);
            mUpload2ImgView.setEnabled(true);
            mUploadDelete2ImgView.setVisibility(View.VISIBLE);
            mUpload3ImgView.setEnabled(true);
            mUploadDelete3ImgView.setVisibility(View.GONE);
        }
        else if(upload1Bitmap != null && upload2Bitmap == null && upload3Bitmap == null) {
            mUpload1ImgView.setEnabled(true);
            mUploadDelete1ImgView.setVisibility(View.VISIBLE);
            mUpload2ImgView.setEnabled(true);
            mUploadDelete2ImgView.setVisibility(View.GONE);
            mUpload3ImgView.setEnabled(false);
            mUploadDelete3ImgView.setVisibility(View.GONE);
        }
        else if(upload1Bitmap == null && upload2Bitmap == null && upload3Bitmap == null) {
            mUpload1ImgView.setEnabled(true);
            mUploadDelete3ImgView.setVisibility(View.GONE);
            mUpload2ImgView.setEnabled(false);
            mUploadDelete2ImgView.setVisibility(View.GONE);
            mUpload3ImgView.setEnabled(false);
            mUploadDelete3ImgView.setVisibility(View.GONE);
        }
        else {
            mUpload1ImgView.setEnabled(false);
            mUpload2ImgView.setEnabled(false);
            mUpload3ImgView.setEnabled(false);
        }
    }


    /**
     * @brief : 필수 입력 항목에 대한 유효성 체크
     * @return
     */
    private boolean isValidRequiredItem() {
        if(!Util.isValid(mCategoryTxtView.getText().toString())) {
            guideInputRequiredItem(null, getString(R.string.category)+ " " + getString(R.string.required_field_select));
            return false;
        }
        if(!Util.isValid(mProductNameEditTxt.getText().toString())) {
            guideInputRequiredItem(mProductNameEditTxt, getString(R.string.product_name) + " " + getString(R.string.required_field_input));
            return false;
        }
        if(!Util.isValid(mProductMakerEditTxt.getText().toString())) {
            guideInputRequiredItem(mProductNameEditTxt, getString(R.string.product_maker) + " " + getString(R.string.required_field_input));
            return false;
        }

        return true;
    }

    /**
     * @brief : 필수 입력 항목에 대한 가이드 표시
     * @param view
     * @param guideMessage
     */
    private void guideInputRequiredItem(View view, String guideMessage) {
        if(view != null) {
            view.requestFocus();
        }
        Toast.makeText(this, guideMessage, Toast.LENGTH_SHORT).show();
    }

    /**
     * @brief : 장비 등록 처리
     */
    private void registeEquipment() {
        String name = mProductNameEditTxt.getText().toString();
        String maker = mProductMakerEditTxt.getText().toString();
        String purDate = mPurchaseDateTxtView.getText().toString();
        String purPrice = mPurchasePriceEditTxt.getText().toString();
        String purAmount = mPurchaseAmountEditTxt.getText().toString();
        String comment = mCommentEditTxt.getText().toString();

        ArrayList<CategoryInfo> categoryInfoArr = MainApplication.getCategoryInfoArr();
        CategoryInfo categoryInfo = categoryInfoArr.get(mCategorySelectedIndex);

//        Log.d(TAG, "registeEquipment name:" + name + "\nmaker:" + maker + "\npurDate:" + purDate + "\npurPrice:" + purPrice
//                    + "\npurAmount:" + purAmount + "\ncategory:" + categoryInfo.getName() + "\ncateId:" + categoryInfo.getId());
        final EquipmentInfo equipmentInfo = new EquipmentInfo();

        if(mMode == EquipmentRegMode.MODE_MODIFY) {
            EquipmentInfo equipInfo = MainApplication.getEquipmentInfoArr().get(mEquipmentInfoIndex);
            // 장비 ID (장비 정보 DB 테이블에서의 PK로 사용됨 - insert 시에 autoincrement로 자동 증가)
            equipmentInfo.setId(equipInfo.getId());
        }

        // 장비 명
        if(Util.isValid(name))
            equipmentInfo.setName(name);
        // 장비 제조사
        if(Util.isValid(maker))
            equipmentInfo.setMaker(maker);
        // 장비 구입 년월
        if(Util.isValid(purDate))
            equipmentInfo.setPurchaseDate(purDate);
        // 장비 구입 가격
        if(Util.isValid(purPrice))
            equipmentInfo.setPurchasePrice(purPrice);
        // 장비 구입 수량
        if(Util.isValid(purAmount))
            equipmentInfo.setPurchaseAmount(purAmount);
        // 장비 카테고리
        if(Util.isValid(categoryInfo.getName()))
            equipmentInfo.setCategory(categoryInfo.getName());
        // 장비 카테고리 ID
        equipmentInfo.setCategoryId(categoryInfo.getId());

        Bitmap upload1Bitmap = null;
        Bitmap upload2Bitmap = null;
        Bitmap upload3Bitmap = null;

        if(mUpload1ImgView.getDrawable() instanceof BitmapDrawable) {
            BitmapDrawable drawable = (BitmapDrawable) mUpload1ImgView.getDrawable();
            upload1Bitmap = drawable.getBitmap();
        }
        if(mUpload2ImgView.getDrawable() instanceof BitmapDrawable) {
            BitmapDrawable drawable = (BitmapDrawable) mUpload2ImgView.getDrawable();
            upload2Bitmap = drawable.getBitmap();
        }
        if(mUpload3ImgView.getDrawable() instanceof BitmapDrawable) {
            BitmapDrawable drawable = (BitmapDrawable) mUpload3ImgView.getDrawable();
            upload3Bitmap = drawable.getBitmap();
        }

        // 장비 사진-1
        if(upload1Bitmap != null)
            equipmentInfo.setPicture1(upload1Bitmap);
        // 장비 사진-2
        if(upload2Bitmap != null)
            equipmentInfo.setPicture2(upload2Bitmap);
        // 장비 사진-3
        if(upload3Bitmap != null)
            equipmentInfo.setPicture3(upload3Bitmap);

        // 비고
        if(Util.isValid(comment))
            equipmentInfo.setComment(comment);


        if(equipmentInfo != null) {
            showLoadingDialog();

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (mMode == EquipmentRegMode.MODE_REGISTRATION) {
                        // 장비 등록 처리
                        if(DataBaseQuery.insertEquipmentInfo(mContext, equipmentInfo)) {
                            MainApplication.setEquipmentInfoArr(DataBaseQuery.getAllEquipmentInfo(mContext));
                            setResult(RESULT_OK);
                        }
                        else {
                            setResult(RESULT_CANCELED);
                        }
                    } else {
                        // 장비 수정 처리
                        if(DataBaseQuery.updateEquipmentInfo(mContext, equipmentInfo)) {
                            MainApplication.setEquipmentInfoArr(DataBaseQuery.getAllEquipmentInfo(mContext));
                            // 장비 정보 갱신이 필요함을 로컬 이벤트로 전송
                            Intent localIntent = new Intent(LocalEventDefine.EQUIPMENT_INFO_REFRESH_REQUEST);
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(localIntent);

                            setResult(RESULT_OK);
                        }
                        else {
                            setResult(RESULT_CANCELED);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideLoadingDialog();
                            finish();
                        }
                    });
                }
            };
            new Thread(runnable).start();
        }
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
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus) {
            mCurFocusEditTxt = (EditText)v;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        Bitmap upload1Bitmap = null;
        Bitmap upload2Bitmap = null;
        Bitmap upload3Bitmap = null;

        if(mIsSetUpload1 && mUpload1ImgView.getDrawable() instanceof BitmapDrawable) {
            BitmapDrawable drawable = (BitmapDrawable) mUpload1ImgView.getDrawable();
            upload1Bitmap = drawable.getBitmap();
        }
        if(mIsSetUpload2 && mUpload2ImgView.getDrawable() instanceof BitmapDrawable) {
            BitmapDrawable drawable = (BitmapDrawable) mUpload2ImgView.getDrawable();
            upload2Bitmap = drawable.getBitmap();
        }
        if(mIsSetUpload3 && mUpload3ImgView.getDrawable() instanceof BitmapDrawable) {
            BitmapDrawable drawable = (BitmapDrawable) mUpload3ImgView.getDrawable();
            upload3Bitmap = drawable.getBitmap();
        }

        if(upload1Bitmap != null) {
            outState.putParcelable(INSTANCE_SAVE_KEY_UPLOAD_BITMAP_1, upload1Bitmap);
        }
        if(upload2Bitmap != null) {
            outState.putParcelable(INSTANCE_SAVE_KEY_UPLOAD_BITMAP_2, upload2Bitmap);
        }
        if(upload3Bitmap != null) {
            outState.putParcelable(INSTANCE_SAVE_KEY_UPLOAD_BITMAP_3, upload3Bitmap);
        }

        EquipmentInfo equipmentInfo = new EquipmentInfo();

        equipmentInfo.setName(mProductNameEditTxt.getText().toString());
        equipmentInfo.setMaker(mProductMakerEditTxt.getText().toString());
        equipmentInfo.setPurchaseDate(mPurchaseDateTxtView.getText().toString());
        equipmentInfo.setPurchasePrice(mPurchasePriceEditTxt.getText().toString());
        equipmentInfo.setPurchaseAmount(mPurchaseAmountEditTxt.getText().toString());
        equipmentInfo.setComment(mCommentEditTxt.getText().toString());
        equipmentInfo.setCategory(mCategoryTxtView.getText().toString());
//        equipmentInfo.setPicture1(upload1Bitmap);
//        equipmentInfo.setPicture2(upload2Bitmap);
//        equipmentInfo.setPicture3(upload3Bitmap);

        outState.putSerializable("saveInstance", equipmentInfo);


        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Bitmap upload1Bitmap = null;
        Bitmap upload2Bitmap = null;
        Bitmap upload3Bitmap = null;

        upload1Bitmap = savedInstanceState.getParcelable(INSTANCE_SAVE_KEY_UPLOAD_BITMAP_1);
        upload2Bitmap = savedInstanceState.getParcelable(INSTANCE_SAVE_KEY_UPLOAD_BITMAP_2);
        upload3Bitmap = savedInstanceState.getParcelable(INSTANCE_SAVE_KEY_UPLOAD_BITMAP_3);

        if(upload1Bitmap != null) {
            mUpload1ImgView.setImageBitmap(upload1Bitmap);
            mIsSetUpload1 = true;
        }
        else {
            mUpload1ImgView.setImageBitmap(null);
            mIsSetUpload1 = false;
        }

        if(upload2Bitmap != null) {
            mUpload2ImgView.setImageBitmap(upload2Bitmap);
            mIsSetUpload2 = true;
        }
        else {
            mUpload2ImgView.setImageBitmap(null);
            mIsSetUpload2 = false;
        }

        if(upload3Bitmap != null) {
            mUpload3ImgView.setImageBitmap(upload3Bitmap);
            mIsSetUpload3 = true;
        }
        else {
            mUpload3ImgView.setImageBitmap(null);
            mIsSetUpload3 = false;
        }

        updateUploadStatus();


        EquipmentInfo equipmentInfo = (EquipmentInfo)savedInstanceState.getSerializable("saveInstance");

//        // 이미지
//        if(equipmentInfo.getPicture1() != null) {
//            mUpload1ImgView.setImageBitmap(equipmentInfo.getPicture1());
//        }
//        if(equipmentInfo.getPicture2() != null) {
//            mUpload2ImgView.setImageBitmap(equipmentInfo.getPicture2());
//        }
//        if(equipmentInfo.getPicture3() != null) {
//            mUpload3ImgView.setImageBitmap(equipmentInfo.getPicture3());
//        }

        // 카테고리
        if(Util.isValid(equipmentInfo.getCategory())) {
            mCategoryTxtView.setText(equipmentInfo.getCategory());

            int index = 0;
            for(CategoryInfo categoryInfo : MainApplication.getCategoryInfoArr()) {
                if(categoryInfo.getName().equals(equipmentInfo.getCategory())) {
                    mCategorySelectedIndex = index;
                    break;
                }
                index++;
            }
        }

        // 제품 명
        if(Util.isValid(equipmentInfo.getName()))
            mProductNameEditTxt.setText(equipmentInfo.getName());

        // 제조 회사
        if(Util.isValid(equipmentInfo.getMaker()))
            mProductMakerEditTxt.setText(equipmentInfo.getMaker());

        // 구입 날짜
        if(Util.isValid(equipmentInfo.getPurchaseDate())) {
            mPurchaseDateTxtView.setText(equipmentInfo.getPurchaseDate());
        }

        // 구입 가격
        if(Util.isValid(equipmentInfo.getPurchasePrice()))
            mPurchasePriceEditTxt.setText(equipmentInfo.getPurchasePrice());

        // 구입 수량
        if(Util.isValid(equipmentInfo.getPurchaseAmount()))
            mPurchaseAmountEditTxt.setText(equipmentInfo.getPurchaseAmount());

        // 메모
        if(Util.isValid(equipmentInfo.getComment()))
            mCommentEditTxt.setText(equipmentInfo.getComment());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_ALBUM_1:
                setAlbumUploadImage(data.getData(), UploadNum.UPLOAD_1);
                break;

            case GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_ALBUM_2:
                setAlbumUploadImage(data.getData(), UploadNum.UPLOAD_2);
                break;

            case GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_ALBUM_3:
                setAlbumUploadImage(data.getData(), UploadNum.UPLOAD_3);
                break;

            case GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_CAMERA_1:
                setCameraUploadImage(UploadNum.UPLOAD_1);
                break;

            case GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_CAMERA_2:
                setCameraUploadImage(UploadNum.UPLOAD_2);
                break;

            case GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_CAMERA_3:
                setCameraUploadImage(UploadNum.UPLOAD_3);
                break;
        }
    }

    /**
     * 콤마 숫자 입력 포멧 처리를 위한 TextWatcher
     */
    private class NumberFormatEditTextWatcher implements TextWatcher {
        EditText    editText;
        String      strAmount = "";

        public NumberFormatEditTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!charSequence.toString().equals(strAmount)) {
                strAmount = makeStringComma(charSequence.toString().replace(",", ""));
                editText.setText(strAmount);
                Editable e = (Editable) editText.getText();
                Selection.setSelection(e, strAmount.length());
            }
        }

        private String makeStringComma(String str) {
            if (str.length() == 0)
                return "";

            try {
                long value = Long.parseLong(str);
                DecimalFormat format = new DecimalFormat("###,###");
                return format.format(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return "";
            }
        }
    }
}