package com.youngmo.chun.mycampingitem.db;

import android.provider.BaseColumns;

/**
 * Created by ChunYoungmo on 15. 10. 29..
 */
public final class DataBases {

    public static final class CreateDB implements BaseColumns {
        /////////////////////////////////////////////////
        // 카테고리 테이블
        /////////////////////////////////////////////////
        /**< 카테고리 테이블 명 */
        public static final String _CATEGORY_TABLENAME  = "category";
        /**< 카테고리 명 */
        public static final String _CATEGORY_NAME       = "name";
        /**< 카테고리 테이블 생성 구문 */
        public static final String _CREATE_CATEGORY     =
                "create table "+_CATEGORY_TABLENAME+"("
                        +_ID+" integer primary key autoincrement, "
                        +_CATEGORY_NAME+" text not null );";

        /////////////////////////////////////////////////
        // 장비 정보 테이블
        /////////////////////////////////////////////////
        /**< 장비 정보 테이블 명 */
        public static final String _EQUIPMENT_INFO_TABLENAME        = "equipment";
        /**< 장비 명 */
        public static final String _EQUIPMENT_INFO_NAME             = "name";
        /**< 장비 제조사 */
        public static final String _EQUIPMENT_INFO_MAKER            = "maker";
        /**< 장비 구입 년월 */
        public static final String _EQUIPMENT_INFO_PURCHASE_DATE    = "purchase_date";
        /**< 장비 구입 가격 */
        public static final String _EQUIPMENT_INFO_PURCHASE_PRICE   = "purchase_price";
        /**< 장비 구입 수량 */
        public static final String _EQUIPMENT_INFO_PURCHASE_AMOUNT  = "purchase_amount";
        /**< 장비 카테고리 */
        public static final String _EQUIPMENT_INFO_CATEGORY         = "category";
        /**< 장비 카테고리 ID */
        public static final String _EQUIPMENT_INFO_CATEGORY_ID      = "category_id";
        /**< 장비 사진1 */
        public static final String _EQUIPMENT_INFO_PICTURE_1        = "picture_1";
        /**< 장비 사진2 */
        public static final String _EQUIPMENT_INFO_PICTURE_2        = "picture_2";
        /**< 장비 사진3 */
        public static final String _EQUIPMENT_INFO_PICTURE_3        = "picture_3";
        /**< 비고 */
        public static final String _EQUIPMENT_INFO_COMMENT          = "comment";
        /**< 장비 정보 테이블 생성 구문 */
        public static final String _CREATE_EQUIPMENT_INFO           =
                "create table "+_EQUIPMENT_INFO_TABLENAME+"("
                        +_ID+" integer primary key autoincrement, "
                        +_EQUIPMENT_INFO_NAME+" text not null, "
                        +_EQUIPMENT_INFO_MAKER+" text not null, "
                        +_EQUIPMENT_INFO_PURCHASE_DATE+" text, "
                        +_EQUIPMENT_INFO_PURCHASE_PRICE+" text, "
                        +_EQUIPMENT_INFO_PURCHASE_AMOUNT+" text, "
                        +_EQUIPMENT_INFO_CATEGORY+" text not null, "
                        +_EQUIPMENT_INFO_CATEGORY_ID+" integer, "
                        +_EQUIPMENT_INFO_PICTURE_1+"  blob, "
                        +_EQUIPMENT_INFO_PICTURE_2+"  blob, "
                        +_EQUIPMENT_INFO_PICTURE_3+"  blob, "
                        +_EQUIPMENT_INFO_COMMENT+" text );";

        /////////////////////////////////////////////////
        // CheckList 그룹 테이블
        /////////////////////////////////////////////////
        /**< CheckList 그룹 테이블 명 */
        public static final String _CHECKLIST_GROUP_TABLENAME           = "checklist_group";
        /**< 그룹 명 */
        public static final String _CHECKLIST_GROUP_NAME                = "name";
        /**< 그룹 이미지 */
        public static final String _CHECKLIST_GROUP_IMAGE               = "image";
        /**< 그룹 이미지의 디폴트 이미지 사용 여부 */
        public static final String _CHECKLIST_GROUP_IMAGE_IS_DEFAULT    = "is_default_image";
        /**< 출정 날짜 */
        public static final String _CHECKLIST_GROUP_FIELD_DATE          = "field_date";
        /**< 출정 알람 */
        public static final String _CHECKLIST_GROUP_FIELD_ALARM         = "field_alarm";
        /**< 정렬 순서 번호 */
        public static final String _CHECKLIST_GROUP_ORDER_NUM           = "order_num";
        /**< 장비 정보 리스트 ('장비명' | '장비ID' | '체크여부' \t '장비명' | '장비ID' | '체크여부') */
        // '장비ID'가 'NULL'인 경우는 직접입력한 경우, 그 이외는 등록해논 장비에서 가져온 경우
        public static final String _CHECKLIST_GROUP_EQUIP_LIST = "equip_list";

        /**< CheckList 그룹 테이블 생성 구문 */
        public static final String _CREATE_CHECKLIST_GROUP =
                "create table "+_CHECKLIST_GROUP_TABLENAME+"("
                        +_ID+" integer primary key autoincrement, "
                        +_CHECKLIST_GROUP_NAME+" text not null, "
                        +_CHECKLIST_GROUP_IMAGE+"  blob, "
                        +_CHECKLIST_GROUP_IMAGE_IS_DEFAULT+" integer default 0, "
                        +_CHECKLIST_GROUP_FIELD_DATE+" text not null, "
                        +_CHECKLIST_GROUP_FIELD_ALARM+" integer default 0, "
                        +_CHECKLIST_GROUP_ORDER_NUM+" integer, "
                        +_CHECKLIST_GROUP_EQUIP_LIST+" text not null );";
    }
}
