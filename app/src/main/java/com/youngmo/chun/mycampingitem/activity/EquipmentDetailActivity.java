package com.youngmo.chun.mycampingitem.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.youngmo.chun.mycampingitem.MainApplication;
import com.youngmo.chun.mycampingitem.R;
import com.youngmo.chun.mycampingitem.define.GlobalDefine;
import com.youngmo.chun.mycampingitem.define.IntentExtraNameDefine;
import com.youngmo.chun.mycampingitem.model.EquipmentInfo;
import com.youngmo.chun.mycampingitem.utils.RecycleUtils;
import com.youngmo.chun.mycampingitem.utils.Util;

public class EquipmentDetailActivity extends BaseActivity {

    private Button      mTopNaviMenuBtn;
    private int         mEquipmentInfoIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_detail);
        loadAdMob();
        init();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * @brief : 초기 처리
     */
    private void init() {
        setViewReference();
        setViewEventListener();
        setData();
        setViewUI(false);
    }

    /**
     * @brief : 참조할 View ID 맵핑
     */
    private void setViewReference() {
        mTopNaviMenuBtn = (Button)findViewById(R.id.top_navigator_bar_menu_button);
    }

    /**
     * @brief : View에 이벤트 등록
     */
    private void setViewEventListener() {
        setViewEventListenerTopNaviBack();
        // 상단 네비게이션에 '수정' 버튼 이벤트 처리
        mTopNaviMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEquipmentModifyScreen();
            }
        });
    }
    /**
     * @brief : 데이터 관련 셋팅 작업
     */
    private void setData() {
        mEquipmentInfoIndex = getIntent().getIntExtra(IntentExtraNameDefine.EQUIPMENT_INFO_INDEX, -1);

    }

    /**
     * @brief : View들의 UI구성요소 셋팅
     * @param isRefresh
     */
    private void setViewUI(boolean isRefresh) {
        if(!isRefresh) {
            ((TextView) findViewById(R.id.top_navigator_bar_title)).setText(getString(R.string.equipment_detail));
            mTopNaviMenuBtn.setVisibility(View.VISIBLE);
            mTopNaviMenuBtn.setText(getString(R.string.modify));

        }

        EquipmentInfo equipmentInfo = MainApplication.getEquipmentInfoArr().get(mEquipmentInfoIndex);

        String name = equipmentInfo.getName();
        String maker = equipmentInfo.getMaker();
        String purchaseDate = equipmentInfo.getPurchaseDate();
        String purchasePrice = equipmentInfo.getPurchasePrice();
        String purchaseAmount = equipmentInfo.getPurchaseAmount();
        String category = equipmentInfo.getCategory();
        int categoryId = equipmentInfo.getCategoryId();
        Bitmap picture1 = equipmentInfo.getPicture1();
        Bitmap picture2 = equipmentInfo.getPicture2();
        Bitmap picture3 = equipmentInfo.getPicture3();
        String comment = equipmentInfo.getComment();


        ((TextView)findViewById(R.id.equipment_detail_category_and_product_name_text_view)).setText(category + " | " + name);
        ((TextView)findViewById(R.id.equipment_detail_product_maker_text_view)).setText(Util.isValid(maker) ? maker : getString(R.string.hyphen));
        ((TextView)findViewById(R.id.equipment_detail_purchase_date_text_view)).setText(Util.isValid(purchaseDate) ? purchaseDate : getString(R.string.hyphen));
        ((TextView)findViewById(R.id.equipment_detail_purchase_price_text_view)).setText(Util.isValid(purchasePrice) ? purchasePrice : getString(R.string.hyphen));
        ((TextView)findViewById(R.id.equipment_detail_purchase_amount_text_view)).setText(Util.isValid(purchaseAmount) ? purchaseAmount : getString(R.string.hyphen));
        if(Util.isValid(comment))
            ((TextView)findViewById(R.id.equipment_detail_comment_text_view)).setText(comment);
        else
            findViewById(R.id.equipment_detail_comment_text_view).setVisibility(View.GONE);

        ImageView image1 = (ImageView) findViewById(R.id.equipment_detail_picture_1_image_view);
        ImageView image2 = (ImageView) findViewById(R.id.equipment_detail_picture_2_image_view);
        ImageView image3 = (ImageView) findViewById(R.id.equipment_detail_picture_3_image_view);

        if(isRefresh) {
            RecycleUtils.imageViewRecycle(image1);
            image1.setImageBitmap(null);
            RecycleUtils.imageViewRecycle(image2);
            image2.setImageBitmap(null);
            RecycleUtils.imageViewRecycle(image3);
            image3.setImageBitmap(null);
        }

        if(picture1 != null) {
            image1.setImageBitmap(picture1);
            findViewById(R.id.equipment_detail_picture_1_layout).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.equipment_detail_picture_1_layout).setVisibility(View.GONE);
        }

        if(picture2 != null) {
            image2.setImageBitmap(picture2);
            findViewById(R.id.equipment_detail_picture_2_layout).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.equipment_detail_picture_2_layout).setVisibility(View.GONE);
        }

        if(picture3 != null) {
            image3.setImageBitmap(picture3);
            findViewById(R.id.equipment_detail_picture_3_layout).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.equipment_detail_picture_3_layout).setVisibility(View.GONE);
        }
    }

    /**
     * @brief : 장비 수정 화면으로 이동 처리
     */
    private void goToEquipmentModifyScreen() {
        Intent intent = new Intent(mContext, EquipmentRegisterActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(IntentExtraNameDefine.REGISTRATION_MODE, EquipmentRegisterActivity.EquipmentRegMode.MODE_MODIFY);
        intent.putExtra(IntentExtraNameDefine.EQUIPMENT_INFO_INDEX, mEquipmentInfoIndex);
        startActivityForResult(intent, GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_DETAIL_MODIFY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK) {
            return;
        }

        if(requestCode == GlobalDefine.ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_DETAIL_MODIFY) {
            // 수정된 내용으로 갱신
            setViewUI(true);
        }
    }
}
