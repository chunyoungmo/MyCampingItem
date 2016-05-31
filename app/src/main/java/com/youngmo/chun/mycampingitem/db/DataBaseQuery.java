package com.youngmo.chun.mycampingitem.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.youngmo.chun.mycampingitem.model.CategoryInfo;
import com.youngmo.chun.mycampingitem.model.CheckListGroupInfo;
import com.youngmo.chun.mycampingitem.model.EquipmentInfo;
import com.youngmo.chun.mycampingitem.utils.Util;

import java.util.ArrayList;

/**
 * Created by ChunYoungmo on 15. 10. 30..
 */
public class DataBaseQuery {

    private static String TAG = Util.getTagBaseClassName(DataBaseQuery.class);

    ////////////////////////////////////////////////////////////////////////////
    // 카테고리 테이블관련 Query
    ////////////////////////////////////////////////////////////////////////////

    /**
     * 카테고리 테이블에 카테고리명 배열 추가
     * @param context
     * @param categoryNames
     */
    public static void insertCategory(Context context, String[] categoryNames) {
        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();

        for(String name : categoryNames) {
            values.put(DataBases.CreateDB._CATEGORY_NAME, name); // 카테고리 명
            db.insert(DataBases.CreateDB._CATEGORY_TABLENAME, null, values);
        }
        db.close();
    }

    /**
     * 카테고리 테이블에 카테고리명 추가
     * @param context
     * @param categoryName
     */
    public static void insertCategory(Context context, String categoryName) {
        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB._CATEGORY_NAME, categoryName); // 카테고리 명

        db.insert(DataBases.CreateDB._CATEGORY_TABLENAME, null, values);
        db.close();
    }


    /**
     * 카테고리 테이블에 카테고리명 업데이트(편집)
     * @param context
     * @param categoryInfo
     * @return true : 삭제 성공
     *         false : 삭제 실패
     */
    public static boolean updateCategory(Context context, CategoryInfo categoryInfo) {
        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB._CATEGORY_NAME, categoryInfo.getName());

        // updating row
        int updateRowNum = db.update(DataBases.CreateDB._CATEGORY_TABLENAME, values, BaseColumns._ID + " = ?",
                new String[]{String.valueOf(categoryInfo.getId())});
        db.close();

        return updateRowNum > 0;
    }


    /**
     * 카테고리 테이블에 카테고리명 삭제
     * @param context
     * @param categoryInfo
     * @return true : 삭제 성공
     *         false : 삭제 실패
     */
    public static boolean deleteCategory(Context context, CategoryInfo categoryInfo) {
        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWritableDatabase();

        int deleteRowNum = db.delete(DataBases.CreateDB._CATEGORY_TABLENAME, BaseColumns._ID + " = ?",
                new String[]{String.valueOf(categoryInfo.getId())});
        db.close();

        return deleteRowNum > 0;
    }

    /**
     * 카테고리 테이블에서 카테고리 정보 가져오기
     * @param context
     * @return
     */
    public static ArrayList<CategoryInfo> getAllCategory(Context context) {
        ArrayList<CategoryInfo> categoryList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + DataBases.CreateDB._CATEGORY_TABLENAME;

        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                CategoryInfo categoryInfo = new CategoryInfo(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)),
                                                            cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._CATEGORY_NAME)));
                categoryList.add(categoryInfo);
            } while (cursor.moveToNext());
        }

        db.close();
        return categoryList;
    }

    /**
     * 카테고리명이 카테고리테이블에 존재하는지 확인
     * @param context
     * @param categoryName
     * @return true : 카테고리명 존재
     *         false : 카테고리명 존재 안함
     */
    public static boolean isExistCategoryName(Context context, String categoryName) {
        boolean isExist = true;
        SQLiteDatabase db = DataBaseHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.query(DataBases.CreateDB._CATEGORY_TABLENAME,
                new String[]{DataBases.CreateDB._CATEGORY_NAME},
                DataBases.CreateDB._CATEGORY_NAME + "=?",
                new String[]{categoryName},
                null, null, null, null);

        if (cursor == null || cursor.getCount() == 0)
            isExist = false;

        db.close();
        return isExist;
    }

    ////////////////////////////////////////////////////////////////////////////
    // 장비 정보 테이블관련 Query
    ////////////////////////////////////////////////////////////////////////////
    /**
     * 장비 정보 테이블에 장비 정보 추가
     * @param context
     * @param equipmentInfo
     * @return
     */
    public static boolean insertEquipmentInfo(Context context, EquipmentInfo equipmentInfo) {
        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB._EQUIPMENT_INFO_NAME, equipmentInfo.getName()); // 장비 명
        values.put(DataBases.CreateDB._EQUIPMENT_INFO_MAKER, equipmentInfo.getMaker()); // 장비 제조사
        values.put(DataBases.CreateDB._EQUIPMENT_INFO_PURCHASE_DATE, equipmentInfo.getPurchaseDate()); // 장비 구입 년월
        values.put(DataBases.CreateDB._EQUIPMENT_INFO_PURCHASE_PRICE, equipmentInfo.getPurchasePrice()); // 장비 구입 가격
        values.put(DataBases.CreateDB._EQUIPMENT_INFO_PURCHASE_AMOUNT, equipmentInfo.getPurchaseAmount()); // 장비 구입 수량
        values.put(DataBases.CreateDB._EQUIPMENT_INFO_CATEGORY, equipmentInfo.getCategory()); // 장비 카테고리
        values.put(DataBases.CreateDB._EQUIPMENT_INFO_CATEGORY_ID, equipmentInfo.getCategoryId()); // 장비 카테고리 ID
        if(equipmentInfo.getPicture1() != null)
            values.put(DataBases.CreateDB._EQUIPMENT_INFO_PICTURE_1, Util.getBytes(equipmentInfo.getPicture1())); // 장비 사진
        if(equipmentInfo.getPicture2() != null)
            values.put(DataBases.CreateDB._EQUIPMENT_INFO_PICTURE_2, Util.getBytes(equipmentInfo.getPicture2())); // 장비 사진
        if(equipmentInfo.getPicture3() != null)
            values.put(DataBases.CreateDB._EQUIPMENT_INFO_PICTURE_3, Util.getBytes(equipmentInfo.getPicture3())); // 장비 사진
        values.put(DataBases.CreateDB._EQUIPMENT_INFO_COMMENT, equipmentInfo.getComment()); // 비고

        long insertedRow = db.insert(DataBases.CreateDB._EQUIPMENT_INFO_TABLENAME, null, values);

        db.close();

        return (insertedRow == -1 ? false : true);
    }

    /**
     * 장비 정보 테이블에 장비정보 업데이트(편집)
     * @param context
     * @param equipmentInfo
     * @return true : 삭제 성공
     *         false : 삭제 실패
     */
    public static boolean updateEquipmentInfo(Context context, EquipmentInfo equipmentInfo) {
        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB._EQUIPMENT_INFO_NAME, equipmentInfo.getName()); // 장비 명
        values.put(DataBases.CreateDB._EQUIPMENT_INFO_MAKER, equipmentInfo.getMaker()); // 장비 제조사
        values.put(DataBases.CreateDB._EQUIPMENT_INFO_PURCHASE_DATE, equipmentInfo.getPurchaseDate()); // 장비 구입 년월
        values.put(DataBases.CreateDB._EQUIPMENT_INFO_PURCHASE_PRICE, equipmentInfo.getPurchasePrice()); // 장비 구입 가격
        values.put(DataBases.CreateDB._EQUIPMENT_INFO_PURCHASE_AMOUNT, equipmentInfo.getPurchaseAmount()); // 장비 구입 수량
        values.put(DataBases.CreateDB._EQUIPMENT_INFO_CATEGORY, equipmentInfo.getCategory()); // 장비 카테고리
        values.put(DataBases.CreateDB._EQUIPMENT_INFO_CATEGORY_ID, equipmentInfo.getCategoryId()); // 장비 카테고리 ID
        byte[] imageClear = null;
        if(equipmentInfo.getPicture1() != null)
            values.put(DataBases.CreateDB._EQUIPMENT_INFO_PICTURE_1, Util.getBytes(equipmentInfo.getPicture1())); // 장비 사진
        else
            values.put(DataBases.CreateDB._EQUIPMENT_INFO_PICTURE_1, imageClear); // 장비 사진

        if(equipmentInfo.getPicture2() != null)
            values.put(DataBases.CreateDB._EQUIPMENT_INFO_PICTURE_2, Util.getBytes(equipmentInfo.getPicture2())); // 장비 사진
        else
            values.put(DataBases.CreateDB._EQUIPMENT_INFO_PICTURE_2, imageClear); // 장비 사진

        if(equipmentInfo.getPicture3() != null)
            values.put(DataBases.CreateDB._EQUIPMENT_INFO_PICTURE_3, Util.getBytes(equipmentInfo.getPicture3())); // 장비 사진
        else
            values.put(DataBases.CreateDB._EQUIPMENT_INFO_PICTURE_3, imageClear); // 장비 사진

        values.put(DataBases.CreateDB._EQUIPMENT_INFO_COMMENT, equipmentInfo.getComment()); // 비고

        int updateRowNum = db.update(DataBases.CreateDB._EQUIPMENT_INFO_TABLENAME, values, BaseColumns._ID + " = ?",
                new String[]{String.valueOf(equipmentInfo.getId())});
        db.close();

        return updateRowNum > 0;
    }

    /**
     * 장비 정보 테이블에 장비정보 삭제
     * @param context
     * @param equipmentInfo
     * @return true : 삭제 성공
     *         false : 삭제 실패
     */
    public static boolean deleteEquipmentInfo(Context context, EquipmentInfo equipmentInfo) {
        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWritableDatabase();

        int deleteRowNum = db.delete(DataBases.CreateDB._EQUIPMENT_INFO_TABLENAME, BaseColumns._ID + " = ?",
                new String[]{String.valueOf(equipmentInfo.getId())});
        db.close();

        return deleteRowNum > 0;
    }

    /**
     * 장비 정보 테이블에서 장비정보 가져오기
     * @param context
     * @return
     */
    public static ArrayList<EquipmentInfo> getAllEquipmentInfo(Context context) {
        ArrayList<EquipmentInfo> equipmentInfoList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + DataBases.CreateDB._EQUIPMENT_INFO_TABLENAME;

        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                EquipmentInfo equipmentInfo = new EquipmentInfo();
                equipmentInfo.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
                equipmentInfo.setName(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_NAME)));
                equipmentInfo.setMaker(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_MAKER)));
                equipmentInfo.setPurchaseDate(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_PURCHASE_DATE)));
                equipmentInfo.setPurchasePrice(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_PURCHASE_PRICE)));
                equipmentInfo.setPurchaseAmount(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_PURCHASE_AMOUNT)));
                equipmentInfo.setCategory(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_CATEGORY)));
                equipmentInfo.setCategoryId(cursor.getInt(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_CATEGORY_ID)));
                byte[] blob1 = cursor.getBlob(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_PICTURE_1));
                equipmentInfo.setPicture1(Util.getBitmap(blob1));
                byte[] blob2 = cursor.getBlob(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_PICTURE_2));
                equipmentInfo.setPicture2(Util.getBitmap(blob2));
                byte[] blob3 = cursor.getBlob(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_PICTURE_3));
                equipmentInfo.setPicture3(Util.getBitmap(blob3));
                equipmentInfo.setComment(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_COMMENT)));
                equipmentInfoList.add(equipmentInfo);
            } while (cursor.moveToNext());
        }

        db.close();
        return equipmentInfoList;
    }

    /**
     * 장비 정보 테이블에서 장비정보 가져오기
     * @param context
     * @param categoryId : 카테고리 ID
     * @return
     */
    public static ArrayList<EquipmentInfo> getAllEquipmentInfo(Context context, String categoryId) {
//        Log.d(TAG, "------------------- categoryId:" + categoryId);
        ArrayList<EquipmentInfo> equipmentInfoList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + DataBases.CreateDB._EQUIPMENT_INFO_TABLENAME + " WHERE " + DataBases.CreateDB._EQUIPMENT_INFO_CATEGORY_ID + " = ?";

        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{categoryId});

        if (cursor.moveToFirst()) {
            do {
                EquipmentInfo equipmentInfo = new EquipmentInfo();
                equipmentInfo.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
                equipmentInfo.setName(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_NAME)));
                equipmentInfo.setMaker(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_MAKER)));
                equipmentInfo.setPurchaseDate(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_PURCHASE_DATE)));
                equipmentInfo.setPurchasePrice(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_PURCHASE_PRICE)));
                equipmentInfo.setPurchaseAmount(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_PURCHASE_AMOUNT)));
                equipmentInfo.setCategory(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_CATEGORY)));
                equipmentInfo.setCategoryId(cursor.getInt(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_CATEGORY_ID)));
                byte[] blob1 = cursor.getBlob(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_PICTURE_1));
                equipmentInfo.setPicture1(Util.getBitmap(blob1));
                byte[] blob2 = cursor.getBlob(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_PICTURE_2));
                equipmentInfo.setPicture2(Util.getBitmap(blob2));
                byte[] blob3 = cursor.getBlob(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_PICTURE_3));
                equipmentInfo.setPicture3(Util.getBitmap(blob3));
                equipmentInfo.setComment(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_COMMENT)));
                equipmentInfoList.add(equipmentInfo);
            } while (cursor.moveToNext());
        }

        db.close();
        return equipmentInfoList;
    }

    public static EquipmentInfo getEquipmentInfo(Context context, String id) {

        String selectQuery = "SELECT  * FROM " + DataBases.CreateDB._EQUIPMENT_INFO_TABLENAME + " WHERE " + DataBases.CreateDB._ID + " = ?";

        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{id});

        EquipmentInfo equipmentInfo = null;
        if (cursor.moveToFirst()) {
            equipmentInfo = new EquipmentInfo();
            equipmentInfo.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
            equipmentInfo.setName(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_NAME)));
            equipmentInfo.setMaker(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_MAKER)));
            equipmentInfo.setPurchaseDate(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_PURCHASE_DATE)));
            equipmentInfo.setPurchasePrice(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_PURCHASE_PRICE)));
            equipmentInfo.setPurchaseAmount(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_PURCHASE_AMOUNT)));
            equipmentInfo.setCategory(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_CATEGORY)));
            equipmentInfo.setCategoryId(cursor.getInt(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_CATEGORY_ID)));
            byte[] blob1 = cursor.getBlob(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_PICTURE_1));
            equipmentInfo.setPicture1(Util.getBitmap(blob1));
            byte[] blob2 = cursor.getBlob(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_PICTURE_2));
            equipmentInfo.setPicture2(Util.getBitmap(blob2));
            byte[] blob3 = cursor.getBlob(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_PICTURE_3));
            equipmentInfo.setPicture3(Util.getBitmap(blob3));
            equipmentInfo.setComment(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._EQUIPMENT_INFO_COMMENT)));
        }

        db.close();
        return equipmentInfo;
    }

    ////////////////////////////////////////////////////////////////////////////
    // CheckList 그룹 테이블관련 Query
    ////////////////////////////////////////////////////////////////////////////
    /**
     * CheckList 그룹 테이블에 추가
     * @param context
     * @param checkListGroupInfo
     * @return
     */
    public static boolean insertCheckListGroupInfo(Context context, CheckListGroupInfo checkListGroupInfo) {
        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        if(checkListGroupInfo.getGroupImage() != null)
            values.put(DataBases.CreateDB._CHECKLIST_GROUP_IMAGE, Util.getBytes(checkListGroupInfo.getGroupImage())); // 그룹 사진
        values.put(DataBases.CreateDB._CHECKLIST_GROUP_IMAGE_IS_DEFAULT, (checkListGroupInfo.isDefaultImage() ? 1 : 0)); // 그룹 사진 기본 제공 이미지 사용 여부
        values.put(DataBases.CreateDB._CHECKLIST_GROUP_NAME, checkListGroupInfo.getGroupName()); // 그룹 명
        values.put(DataBases.CreateDB._CHECKLIST_GROUP_FIELD_DATE, checkListGroupInfo.getFieldDate()); // 출정 날짜
        values.put(DataBases.CreateDB._CHECKLIST_GROUP_FIELD_ALARM, (checkListGroupInfo.isFieldAlarm() ? 1 : 0)); // 출정 알람
        values.put(DataBases.CreateDB._CHECKLIST_GROUP_ORDER_NUM, checkListGroupInfo.getOrderNum()); // 정렬 순서 번호
        values.put(DataBases.CreateDB._CHECKLIST_GROUP_EQUIP_LIST, checkListGroupInfo.getEquipInfoForDb()); // 장비 정보 리스트

        long insertedRow = db.insert(DataBases.CreateDB._CHECKLIST_GROUP_TABLENAME, null, values);

        db.close();

        return (insertedRow == -1 ? false : true);
    }

    /**
     * CheckList 그룹 테이블에 CheckList 그룹정보 업데이트(편집)
     * @param context
     * @param checkListGroupInfo
     * @return true : 삭제 성공
     *         false : 삭제 실패
     */
    public static boolean updateCheckListGroupInfo(Context context, CheckListGroupInfo checkListGroupInfo) {
        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        if(checkListGroupInfo.getGroupImage() != null)
            values.put(DataBases.CreateDB._CHECKLIST_GROUP_IMAGE, Util.getBytes(checkListGroupInfo.getGroupImage())); // 그룹 사진
        values.put(DataBases.CreateDB._CHECKLIST_GROUP_IMAGE_IS_DEFAULT, (checkListGroupInfo.isDefaultImage() ? 1 : 0)); // 그룹 사진 기본 제공 이미지 사용 여부
        values.put(DataBases.CreateDB._CHECKLIST_GROUP_NAME, checkListGroupInfo.getGroupName()); // 그룹 명
        values.put(DataBases.CreateDB._CHECKLIST_GROUP_FIELD_DATE, checkListGroupInfo.getFieldDate()); // 출정 날짜
        values.put(DataBases.CreateDB._CHECKLIST_GROUP_FIELD_ALARM, (checkListGroupInfo.isFieldAlarm() ? 1 : 0)); // 출정 알람
        values.put(DataBases.CreateDB._CHECKLIST_GROUP_ORDER_NUM, checkListGroupInfo.getOrderNum()); // 정렬 순서 번호
        values.put(DataBases.CreateDB._CHECKLIST_GROUP_EQUIP_LIST, checkListGroupInfo.getEquipInfoForDb()); // 장비 정보 리스트

        int updateRowNum = db.update(DataBases.CreateDB._CHECKLIST_GROUP_TABLENAME, values, BaseColumns._ID + " = ?",
                new String[]{String.valueOf(checkListGroupInfo.getId())});
        db.close();

        return updateRowNum > 0;
    }

    /**
     * CheckList 그룹 테이블에 CheckList 그룹정보 업데이트(편집)
     * @param context
     * @param arrCheckListGroupInfo
     * @return
     */
    public static void updateCheckListGroupInfo(Context context, ArrayList<CheckListGroupInfo> arrCheckListGroupInfo) {
        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWritableDatabase();

        for(CheckListGroupInfo checkListGroupInfo : arrCheckListGroupInfo) {
            ContentValues values = new ContentValues();
            if(checkListGroupInfo.getGroupImage() != null)
                values.put(DataBases.CreateDB._CHECKLIST_GROUP_IMAGE, Util.getBytes(checkListGroupInfo.getGroupImage())); // 그룹 사진
            values.put(DataBases.CreateDB._CHECKLIST_GROUP_IMAGE_IS_DEFAULT, (checkListGroupInfo.isDefaultImage() ? 1 : 0)); // 그룹 사진 기본 제공 이미지 사용 여부
            values.put(DataBases.CreateDB._CHECKLIST_GROUP_NAME, checkListGroupInfo.getGroupName()); // 그룹 명
            values.put(DataBases.CreateDB._CHECKLIST_GROUP_FIELD_DATE, checkListGroupInfo.getFieldDate()); // 출정 날짜
            values.put(DataBases.CreateDB._CHECKLIST_GROUP_FIELD_ALARM, (checkListGroupInfo.isFieldAlarm() ? 1 : 0)); // 출정 알람
            values.put(DataBases.CreateDB._CHECKLIST_GROUP_ORDER_NUM, checkListGroupInfo.getOrderNum()); // 정렬 순서 번호
            values.put(DataBases.CreateDB._CHECKLIST_GROUP_EQUIP_LIST, checkListGroupInfo.getEquipInfoForDb()); // 장비 정보 리스트

            db.update(DataBases.CreateDB._CHECKLIST_GROUP_TABLENAME, values, BaseColumns._ID + " = ?",
                    new String[]{String.valueOf(checkListGroupInfo.getId())});
        }
        db.close();
    }

    /**
     * CheckList 그룹 테이블에 CheckList 그룹정보 삭제
     * @param context
     * @param checkListGroupInfo
     * @return true : 삭제 성공
     *         false : 삭제 실패
     */
    public static boolean deleteCheckListGroupInfo(Context context, CheckListGroupInfo checkListGroupInfo) {
        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWritableDatabase();

        int deleteRowNum = db.delete(DataBases.CreateDB._CHECKLIST_GROUP_TABLENAME, BaseColumns._ID + " = ?",
                new String[]{String.valueOf(checkListGroupInfo.getId())});
        db.close();

        return deleteRowNum > 0;
    }

    /**
     * CheckList 그룹 테이블에서 CheckList 그룹정보 가져오기
     * @param context
     * @return
     */
    public static ArrayList<CheckListGroupInfo> getAllCheckListGroupInfo(Context context) {
        ArrayList<CheckListGroupInfo> checkListGroupInfoList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + DataBases.CreateDB._CHECKLIST_GROUP_TABLENAME +
                " ORDER BY " + DataBases.CreateDB._CHECKLIST_GROUP_ORDER_NUM + " DESC";

        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                CheckListGroupInfo checkListGroupInfo = new CheckListGroupInfo();

                checkListGroupInfo.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
                byte[] blob = cursor.getBlob(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_IMAGE));
                checkListGroupInfo.setGroupImage(Util.getBitmap(blob));
                checkListGroupInfo.setIsDefaultImage(cursor.getInt(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_IMAGE_IS_DEFAULT)) == 1 ? true : false);
                checkListGroupInfo.setGroupName(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_NAME)));
                checkListGroupInfo.setFieldDate(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_FIELD_DATE)));
                checkListGroupInfo.setIsFieldAlarm(cursor.getInt(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_FIELD_ALARM)) == 1 ? true : false);
                checkListGroupInfo.setOrderNum(cursor.getInt(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_ORDER_NUM)));
                checkListGroupInfo.setEquipInfoFromDb(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_EQUIP_LIST)));

                checkListGroupInfoList.add(checkListGroupInfo);
            } while (cursor.moveToNext());
        }

        db.close();
        return checkListGroupInfoList;
    }

    /**
     * CheckList 그룹 테이블에서 _CHECKLIST_GROUP_ORDER_NUM와 동일한 CheckList 그룹정보 가져오기
     * @param context
     * @param orderNum
     * @return
     */
    public static CheckListGroupInfo getCheckListGroupInfoWithOrderNum(Context context, String orderNum) {

        String selectQuery = "SELECT  * FROM " + DataBases.CreateDB._CHECKLIST_GROUP_TABLENAME + " WHERE " + DataBases.CreateDB._CHECKLIST_GROUP_ORDER_NUM + " = ?";

        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{orderNum});

        CheckListGroupInfo checkListGroupInfo = null;
        if (cursor.moveToFirst()) {
            checkListGroupInfo = new CheckListGroupInfo();
            checkListGroupInfo.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
            byte[] blob = cursor.getBlob(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_IMAGE));
            checkListGroupInfo.setGroupImage(Util.getBitmap(blob));
            checkListGroupInfo.setIsDefaultImage(cursor.getInt(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_IMAGE_IS_DEFAULT)) == 1 ? true : false);
            checkListGroupInfo.setGroupName(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_NAME)));
            checkListGroupInfo.setFieldDate(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_FIELD_DATE)));
            checkListGroupInfo.setIsFieldAlarm(cursor.getInt(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_FIELD_ALARM)) == 1 ? true : false);
            checkListGroupInfo.setOrderNum(cursor.getInt(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_ORDER_NUM)));
            checkListGroupInfo.setEquipInfoFromDb(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_EQUIP_LIST)));
        }

        db.close();
        return checkListGroupInfo;
    }

    /**
     * CheckList 그룹 테이블에서 _ID와 동일한 CheckList 그룹정보 가져오기
     * @param context
     * @param id
     * @return
     */
    public static CheckListGroupInfo getCheckListGroupInfoWithId(Context context, String id) {

        String selectQuery = "SELECT  * FROM " + DataBases.CreateDB._CHECKLIST_GROUP_TABLENAME + " WHERE " + DataBases.CreateDB._ID + " = ?";

        SQLiteDatabase db = DataBaseHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{id});

        CheckListGroupInfo checkListGroupInfo = null;
        if (cursor.moveToFirst()) {
            checkListGroupInfo = new CheckListGroupInfo();
            checkListGroupInfo.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
            byte[] blob = cursor.getBlob(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_IMAGE));
            checkListGroupInfo.setGroupImage(Util.getBitmap(blob));
            checkListGroupInfo.setIsDefaultImage(cursor.getInt(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_IMAGE_IS_DEFAULT)) == 1 ? true : false);
            checkListGroupInfo.setGroupName(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_NAME)));
            checkListGroupInfo.setFieldDate(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_FIELD_DATE)));
            checkListGroupInfo.setIsFieldAlarm(cursor.getInt(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_FIELD_ALARM)) == 1 ? true : false);
            checkListGroupInfo.setOrderNum(cursor.getInt(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_ORDER_NUM)));
            checkListGroupInfo.setEquipInfoFromDb(cursor.getString(cursor.getColumnIndex(DataBases.CreateDB._CHECKLIST_GROUP_EQUIP_LIST)));
        }

        db.close();
        return checkListGroupInfo;
    }
}