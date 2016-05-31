package com.youngmo.chun.mycampingitem.model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ChunYoungmo on 15. 11. 4..
 */
public class CheckListGroupInfo implements Serializable{
    /**< 체크리스트 그룹 ID (체크리스트 그룹 DB 테이블에서의 PK로 사용됨 - insert 시에 autoincrement로 자동 증가) */
    private int                                 mId;
    /**< 체크리스트 그룹 이미지 */
    private Bitmap                              mGroupImage;
    /**< 체크리스트 그룹 명 */
    private String                              mGroupName;
    /**< 출정 날짜 */
    private String                              mFieldDate;
    /**< 출정 알람 */
    private boolean                             mIsFieldAlarm;
    /**< 정렬 순번 */
    private int                                 mOrderNum;
    /**< 체크리스트 아이템 */
    private ArrayList<CheckListEquipmentInfo>   mArrCheckListEquipInfo = new ArrayList<>();
    /**< 체크리스트 그룹 이미지가 기본제공하는 이미지로 셋팅했는지 여부 */
    private boolean                             mIsDefaultImage;

    public ArrayList<CheckListEquipmentInfo> getArrCheckListEquipInfo() {
        return mArrCheckListEquipInfo;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public Bitmap getGroupImage() {
        return mGroupImage;
    }

    public void setGroupImage(Bitmap groupImage) {
        this.mGroupImage = groupImage;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String groupName) {
        this.mGroupName = groupName;
    }

    public void addAllCheckListEquipInfo(ArrayList<CheckListEquipmentInfo> arrCheckListEquipmentInfo) {
        if(mArrCheckListEquipInfo != null) {
            mArrCheckListEquipInfo.clear();
        }

        for(CheckListEquipmentInfo checkListEquipInfo : arrCheckListEquipmentInfo) {
            mArrCheckListEquipInfo.add(new CheckListEquipmentInfo(checkListEquipInfo.getType(), checkListEquipInfo.getEquipId(), checkListEquipInfo.getEquipName(), checkListEquipInfo.isCheck()));
        }
    }

    public void addCheckListEquipInfo(CheckListEquipmentInfo checkListEquipmentInfo) {
        if(mArrCheckListEquipInfo != null) {
            mArrCheckListEquipInfo.add(checkListEquipmentInfo);
        }
    }
    public void addCheckListEquipInfo(CheckListEquipmentInfo checkListEquipmentInfo, int index) {
        if(mArrCheckListEquipInfo != null) {
            mArrCheckListEquipInfo.add(index, checkListEquipmentInfo);
        }
    }


    public String getFieldDate() {
        return mFieldDate;
    }

    public void setFieldDate(String fieldDate) {
        this.mFieldDate = fieldDate;
    }

    public boolean isFieldAlarm() {
        return mIsFieldAlarm;
    }

    public void setIsFieldAlarm(boolean isFieldAlarm) {
        this.mIsFieldAlarm = isFieldAlarm;
    }


    public void setOrderNum(int orderNum) {
        this.mOrderNum = orderNum;
    }

    public int getOrderNum() {
        return mOrderNum;
    }

    public boolean isDefaultImage() {
        return mIsDefaultImage;
    }

    public void setIsDefaultImage(boolean isDefaultImage) {
        this.mIsDefaultImage = isDefaultImage;
    }

    public void setEquipInfoFromDb(String equipInfoFromDb) {
        // ('장비명' | '장비ID' | '체크여부' \t '장비명' | '장비ID' | '체크여부') 형식을 파싱
        // *'장비ID'가 'NULL'인 경우는 직접입력한 경우, 그 이외는 등록해논 장비에서 가져온 경우!!

        if(mArrCheckListEquipInfo != null && mArrCheckListEquipInfo.size() > 0)
            mArrCheckListEquipInfo.clear();

        String[] arrEquipDivision = equipInfoFromDb.split("\t");
        for(String equipDivison : arrEquipDivision) {
            CheckListEquipmentInfo checkListEquipInfo = new CheckListEquipmentInfo();
            String[] arrEquipInfo = equipDivison.split("\\|");
            if(arrEquipInfo.length == 3) {
                checkListEquipInfo.setEquipName(arrEquipInfo[0]);
                if(arrEquipInfo[1].equals("NULL")) {
                    checkListEquipInfo.setEquipId(-1);
                    checkListEquipInfo.setType(CheckListEquipmentInfo.EquipType.EQUIP_TYPE_INPUT);
                }
                else {
                    checkListEquipInfo.setEquipId(Integer.valueOf(arrEquipInfo[1]));
                    checkListEquipInfo.setType(CheckListEquipmentInfo.EquipType.EQUIP_TYPE_HOLDINGS);
                }

                if(arrEquipInfo[2].equals("Y"))
                    checkListEquipInfo.setIsCheck(true);
                else
                    checkListEquipInfo.setIsCheck(false);

                mArrCheckListEquipInfo.add(checkListEquipInfo);
            }
        }
    }

    /**
     * 체크여부의 체크되어 있지 않은 장비명 리스트 추출
     * @return
     */
    public ArrayList<String> getUncheckEquipNameList() {
        ArrayList<String> uncheckEquipNameArr = new ArrayList<>();

        for(CheckListEquipmentInfo checkListEquipInfo : mArrCheckListEquipInfo) {
            if(!checkListEquipInfo.isCheck()) {
                uncheckEquipNameArr.add(checkListEquipInfo.getEquipName());
            }
        }

        return uncheckEquipNameArr;
    }

    public String getEquipInfoForDb() {
        // ('장비명' | '장비ID' | '체크여부' \t '장비명' | '장비ID' | '체크여부') 형식으로 가공
        // *'장비ID'가 'NULL'인 경우는 직접입력한 경우, 그 이외는 등록해논 장비에서 가져온 경우!!

        String equipInfo = "";
        boolean isFirstSet = true;
        for(CheckListEquipmentInfo checkListEquipInfo : mArrCheckListEquipInfo) {
            String equipId;
            if(checkListEquipInfo.getType() == CheckListEquipmentInfo.EquipType.EQUIP_TYPE_HOLDINGS)
                equipId = Integer.toString(checkListEquipInfo.getEquipId());
            else
                equipId = "NULL";

            boolean isCheck = checkListEquipInfo.isCheck();

            if (isFirstSet) {
                equipInfo += checkListEquipInfo.getEquipName() + "|" + equipId + "|" + (isCheck ? "Y" : "N");
                isFirstSet = false;
            } else {
                equipInfo += "\t" + checkListEquipInfo.getEquipName() + "|" + equipId + "|" + (isCheck ? "Y" : "N");
            }

        }

        return equipInfo;
    }
}
