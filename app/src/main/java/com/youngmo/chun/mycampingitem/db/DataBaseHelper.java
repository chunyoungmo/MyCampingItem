package com.youngmo.chun.mycampingitem.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.youngmo.chun.mycampingitem.utils.Util;

import java.io.File;

/**
 * Created by ChunYoungmo on 15. 10. 30..
 */
public class DataBaseHelper extends SQLiteOpenHelper{
    private static String TAG = Util.getTagBaseClassName(Util.class);

    private static DataBaseHelper mDataBaseHelper;
    /**< DataBase 명 */
    public static final String DATABASE_NAME = "mycamping.db";
//    public static final String DATABASE_NAME = "/sdcard/MyCamping/mycamping.db";  // 디버깅용 Path
    /**< DataBase version */
    private static final int DATABASE_VERSION = 1;
    /**< Context */
    private Context mContext;

    public static DataBaseHelper getInstance(Context context) {
//        Log.d(TAG, "DataBaseHelper getInstance");
        if(mDataBaseHelper == null) {
//            Log.d(TAG, "DataBaseHelper getInstance 2");
//            if(isExistDBFile(context))
//                Log.d(TAG, "DataBaseHelper isExistDBFile TRUE");
//            else
//                Log.d(TAG, "DataBaseHelper isExistDBFile FALSE");


            mDataBaseHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        return mDataBaseHelper;
    }


    public static boolean isExistDBFile(Context context) {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        if(dbFile.exists())
            return true;
        else
            return false;
    }


    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        Log.d(TAG, "DataBaseHelper onCreate");
        db.execSQL(DataBases.CreateDB._CREATE_CATEGORY);
        db.execSQL(DataBases.CreateDB._CREATE_EQUIPMENT_INFO);
        db.execSQL(DataBases.CreateDB._CREATE_CHECKLIST_GROUP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB._CREATE_CATEGORY);
//        db.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB._CREATE_EQUIPMENT_INFO);
//        onCreate(db);
    }
}
