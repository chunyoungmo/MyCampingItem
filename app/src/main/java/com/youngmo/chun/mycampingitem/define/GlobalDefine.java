package com.youngmo.chun.mycampingitem.define;

public class GlobalDefine {
    /**< Release모드 여부 */
    public static final boolean isRelease = true;
    /**< AdMob 표시 여부 */
    public static final boolean isVisibleAdmob = false;


    ////////////////////////////////////////////////////////////////
    // startActivityForResult return request code 정의
    // code prefix : ACTIVITY_REQUEST_CODE_[화면 클래스 명(activity는 뺴고)]
    // cf) EquipmentRegisterActivity.class => ACTIVITY_RESULT_CODE_EQUIPMENT_REGISTER
    ////////////////////////////////////////////////////////////////
    public static final int ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_ALBUM_1           = 1000;
    public static final int ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_ALBUM_2           = 1001;
    public static final int ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_ALBUM_3           = 1002;
    public static final int ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_CAMERA_1          = 1003;
    public static final int ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_CAMERA_2          = 1004;
    public static final int ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_REGISTER_CAMERA_3          = 1005;
    public static final int ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_DETAIL_MODIFY              = 1006;
    public static final int ACTIVITY_REQUEST_CODE_CALL_EQUIPMENT_LIST_REGISTRATION          = 1007;
    public static final int ACTIVITY_REQUEST_CODE_CALL_CHECK_LIST_MANAGEMENT_REGISTRATION   = 1008;
    public static final int ACTIVITY_REQUEST_CODE_CALL_CHECK_LIST_GROUP_REGISTER_ALBUM      = 1009;
    public static final int ACTIVITY_REQUEST_CODE_CALL_CHECK_LIST_GROUP_REGISTER_CAMERA     = 1010;
}
