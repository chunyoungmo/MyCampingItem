package com.youngmo.chun.mycampingitem.model;

import android.graphics.drawable.Drawable;

public class SingleSelectPopupItemInfo {
    /**< 아이템 명 */
    private String      mName;
    /**< 아이템 Drawable 이미지 */
    private Drawable    mDrawableImage;

    public SingleSelectPopupItemInfo(String name) {
        this.mName = name;
        this.mDrawableImage = null;

    }

    public SingleSelectPopupItemInfo(String name, Drawable drawable) {
        this.mName = name;
        this.mDrawableImage = drawable;
    }


    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public Drawable getDrawableImage() {
        return mDrawableImage;
    }

    public void setDrawableImage(Drawable drawableImage) {
        this.mDrawableImage = drawableImage;
    }
}