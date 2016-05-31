package com.youngmo.chun.mycampingitem.model;

public class MultiSelectPopupItemInfo {
    /**< 아이템 명 */
    private String  mName;
    /**< 아이템 선택 여부 상태 */
    private boolean mIsSelected;
    /**< Custom Object */
    private Object  mCustomObject = null;


    public MultiSelectPopupItemInfo(boolean isSelected, String name, Object customObject) {
        this.mIsSelected = isSelected;
        this.mName = name;
        this.mCustomObject = customObject;
    }

    public MultiSelectPopupItemInfo(String name, Object customObject) {
        this.mIsSelected = false;
        this.mName = name;
        this.mCustomObject = customObject;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setIsSelected(boolean mIsSelected) {
        this.mIsSelected = mIsSelected;
    }

    public Object getCustomObject() {
        return mCustomObject;
    }
}