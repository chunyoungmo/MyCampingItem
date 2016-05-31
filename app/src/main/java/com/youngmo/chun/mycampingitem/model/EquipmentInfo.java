package com.youngmo.chun.mycampingitem.model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by ChunYoungmo on 15. 11. 5..
 */
public class EquipmentInfo implements Serializable {
    /**< 장비 ID (장비 정보 DB 테이블에서의 PK로 사용됨 - insert 시에 autoincrement로 자동 증가) */
    private int     mId;
    /**< 장비 명 */
    private String mName;
    /**< 장비 제조사 */
    private String mMaker;
    /**< 장비 구입 년월 */
    private String mPurchaseDate;
    /**< 장비 구입 가격 */
    private String mPurchasePrice;
    /**< 장비 구입 수량 */
    private String mPurchaseAmount;
    /**< 장비 카테고리 */
    private String mCategory;
    /**< 장비 카테고리 ID */
    private int mCategoryId;
    /**< 장비 사진-1 */
    private Bitmap mPicture1;
    /**< 장비 사진-2 */
    private Bitmap mPicture2;
    /**< 장비 사진-3 */
    private Bitmap mPicture3;
    /**< 비고 */
    private String mComment;

    public EquipmentInfo() {


    }

    public void setId(int id) {
        this.mId = id;
    }

    public void setComment(String comment) {
        this.mComment = comment;
    }

    public void setCategory(String category) {
        this.mCategory = category;
    }

    public void setCategoryId(int categoryId) {
        this.mCategoryId = categoryId;
    }

    public void setMaker(String maker) {
        this.mMaker = maker;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setPicture1(Bitmap picture) {
        this.mPicture1 = picture;
    }
    public void setPicture2(Bitmap picture) {
        this.mPicture2 = picture;
    }
    public void setPicture3(Bitmap picture) {
        this.mPicture3 = picture;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.mPurchaseDate = purchaseDate;
    }

    public void setPurchasePrice(String purchasePrice) {
        this.mPurchasePrice = purchasePrice;
    }

    public void setPurchaseAmount(String purchaseAmount) {
        this.mPurchaseAmount = purchaseAmount;
    }

    public int getId() {
        return mId;
    }

    public String getCategory() {
        return mCategory;
    }

    public int getCategoryId() {
        return mCategoryId;
    }

    public String getComment() {
        return mComment;
    }

    public String getMaker() {
        return mMaker;
    }
    public String getName() {

        return mName;
    }

    public Bitmap getPicture1() {
        return mPicture1;
    }
    public Bitmap getPicture2() {
        return mPicture2;
    }
    public Bitmap getPicture3() {
        return mPicture3;
    }

    public String getPurchaseDate() {
        return mPurchaseDate;
    }

    public String getPurchasePrice() {
        return mPurchasePrice;
    }

    public String getPurchaseAmount() {
        return mPurchaseAmount;
    }

}
