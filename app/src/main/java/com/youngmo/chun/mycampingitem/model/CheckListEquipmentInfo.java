package com.youngmo.chun.mycampingitem.model;

import java.io.Serializable;

/**
 * Created by ChunYoungmo on 15. 11. 4..
 */
public class CheckListEquipmentInfo implements Serializable{

    /** 장비 타입 정의 */
    public enum EquipType {
        /** 체크리스트를 등록 장비목록에서 가져온 경우 */
        EQUIP_TYPE_HOLDINGS,
        /** 체크리스트를 직접 입력해서 등록한 경우 */
        EQUIP_TYPE_INPUT,
    };

    private EquipType   mType;
    /**< 장비 ID (장비 정보 DB 테이블에서의 ID) - EQUIP_TYPE_HOLDINGS 인 경우에만 사용 */
    private int         mEquipId;
    /**< 장비 명 */
    private String      mEquipName;
    private boolean     mIsCheck;

    public CheckListEquipmentInfo() {
    }

    public CheckListEquipmentInfo(EquipType type, int equipId, String equipName, boolean isCheck) {
        this.mType = type;
        this.mEquipId = equipId;
        this.mEquipName = equipName;
        this.mIsCheck = isCheck;
    }

    public int getEquipId() {
        return mEquipId;
    }

    public void setEquipId(int equipId) {
        this.mEquipId = equipId;
    }

    public String getEquipName() {
        return mEquipName;
    }
    public void setEquipName(String equipName) {
        this.mEquipName = equipName;
    }

    public boolean isCheck() {
        return mIsCheck;
    }
    public void setIsCheck(boolean isCheck) {
        this.mIsCheck = isCheck;
    }

    public EquipType getType() {
        return mType;
    }
    public void setType(EquipType type) {
        this.mType = type;
    }
}
