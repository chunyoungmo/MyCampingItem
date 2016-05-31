package com.youngmo.chun.mycampingitem.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.youngmo.chun.mycampingitem.MainApplication;
import com.youngmo.chun.mycampingitem.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Util {
    private static String TAG = Util.getTagBaseClassName(Util.class);

    /**
     * @brief : 앱의 버전코드 정보 취득
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @brief : 앱의 버전명 정보 취득
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @brief : 문자열의 유효성 여부 체크
     * @param text
     * @return
     */
    public static boolean isValid(String text) {
        boolean isValid = true;

        if(text == null || text.equals("") || text.length() == 0)
            isValid = false;

        return isValid;
    }

    /**
     * @brief : Class명을 베이스로 Tag 추출
     * @param context
     * @return
     */
    public static String getTagBaseClassName(Context context) {
        final String prefixTag = "MO_";
        final String tag = prefixTag + context.getClass().getSimpleName();

        if(Util.isValid(tag)) {
            if(tag.length() > 23)
                return tag.substring(0, 23);
            else
                return tag;
        }
        else {
            return prefixTag;
        }

    }

    /**
     * @brief : Class명을 베이스로 Tag 추출
     * @param object
     * @return
     */
    public static String getTagBaseClassName(Object object) {
        final String prefixTag = "MO_";
        final String tag = prefixTag + object.getClass().getSimpleName();

        if(Util.isValid(tag)) {
            if(tag.length() > 23)
                return tag.substring(0, 23);
            else
                return tag;
        }
        else {
            return prefixTag;
        }

    }

    /**
     * @brief : dp를 px로 변환
     * @param context
     * @param dp
     * @return
     */
    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * @brief : sp를 px로 변환
     * @param context
     * @param sp
     * @return
     */
    public static int spToPx(Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }


    /**
     * @brief : context에 해당하는 앱이 최상위에서 실행중인지 확인
     * @param context
     * @return true : 최상위에서 실행중 맞음
     *         false : 최상위에서 실행중 아님
     */
    public static boolean isTopRunningProcess(Context context) {
        boolean isTopRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);

//        Log.d(TAG, "isTopRunningProcess PackageName[" + context.getPackageName() + "]");
        String topProcessPackageName;
        if(Build.VERSION.SDK_INT > 20){
            topProcessPackageName = am.getRunningAppProcesses().get(0).processName;
//            Log.d(TAG, "isTopRunningProcess topProcessPackageName:[" + topProcessPackageName + "]");
        }
        else{
            topProcessPackageName = am.getRunningTasks(1).get(0).topActivity.getPackageName();
        }

        isTopRunning = topProcessPackageName.equalsIgnoreCase(context.getPackageName()) ? true : false;
        return isTopRunning;
    }

    /**
     * @brief : context에 해당하는 앱이 실행중인지 확인
     * @param context
     * @return true : 실행중 맞음
     *         false : 실행중 아님
     */
    public static boolean isRunningProcess(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfo = am.getRunningAppProcesses();

        for(ActivityManager.RunningAppProcessInfo info : processInfo) {
            if(context.getPackageName().equalsIgnoreCase(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @brief : 단말 화면 ON/OFF 여부
     * @param context
     * @return true : 단말 화면 ON
     *         false : 단말 화면 OFF
     */
    public static boolean isDeviceScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        boolean screenOn;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return pm.isInteractive();
        } else {
            return pm.isScreenOn();
        }
    }

    /**
     * @brief : 디바이스 스크린 정보 확인용
     * @param activity
     */
    public static void getScreenInfo(Activity activity) {
        // Get the metrics
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int heightPixels = metrics.heightPixels;
        int widthPixels = metrics.widthPixels;
        int densityDpi = metrics.densityDpi;
        float density = metrics.density;
        float scaledDensity = metrics.scaledDensity;
        float xdpi = metrics.xdpi;
        float ydpi = metrics.ydpi;

//        Log.d(TAG, "Info" + "Screen W x H pixels: " + widthPixels + " x " + heightPixels);
//        Log.d(TAG, "Info" + "Screen X x Y dpi: " + xdpi + " x " + ydpi);
//        Log.d(TAG, "Info" + "density = " + density + "  scaledDensity = " + scaledDensity +
//                "  densityDpi = " + densityDpi);
    }

    /**
     * @brief : 푸시 뱃지 카운트 갱신
     * @param context
     * @param count
     */
    public static void updateBadge(Context context, int count) {
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        // 패키지 네임과 클래스 네임 설정
        intent.putExtra("badge_count_package_name", "com.hanafn.systemMonitoring");
        intent.putExtra("badge_count_class_name", "com.hanafn.systemMonitoring.activity.SplashActivity");
        // 업데이트 카운트
        intent.putExtra("badge_count", count);
        context.sendBroadcast(intent);
    }

    /**
     * @brief : HashMap에서 키에 해당하는 값 취득
     * @param hm
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getHahMapValue(HashMap<String, String> hm, String key, String defaultValue) {
        if(hm == null)
            return defaultValue;

        String value = hm.get(key);
        if(value == null)
            return defaultValue;
        else
            return value;
    }

    /**
     * @brief : convert from bitmap to byte array
     * @param bitmap
     * @return
     */
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    /**
     * @brief :  convert from byte array to bitmap
     * @param bytes
     * @return
     */
    public static Bitmap getBitmap(byte[] bytes) {
        if(bytes == null)
            return null;
        else
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    /**
     * @brief : 이미지 파일의 rotate 정보를 리턴한다.
     * @param filePath : 이미지 파일 경로
     * @return
     */
    public static int getRotate(String filePath) {
        int rotate = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            if (orientation == ExifInterface.ORIENTATION_NORMAL) {
                rotate = 0;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                rotate = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                rotate = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                rotate = 270;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rotate;
    }


    /**
     * @brief : 이미지 파일을 Bitmap으로 변경하여 리턴한다.
     * @param context
     * @param filePath
     * @param fileSize
     * @param rotate
     * @return
     */
    public static Bitmap getBitmapFromFile(final Context context, String filePath, int fileSize, int rotate) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        int width_tmp = options.outWidth, height_tmp = options.outHeight;

        int scale = 1;
        while (true) {
            if (width_tmp / 2 < fileSize || height_tmp / 2 < fileSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;

        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        if(bitmap == null) {
            if(context instanceof Activity) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, R.string.damage_image, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        } else {
            Bitmap scaleBitmap = scaleImage(bitmap, rotate, fileSize);
            return scaleBitmap;
        }
    }

    /**
     * @brief : 앨범 갤러리 이미지 파일을 Bitmap으로 변경하여 리턴한다.
     * @param context
     * @param imageUri
     * @param fileSize
     * @return
     */
    public static Bitmap getBitmapFromGallery(final Context context, Uri imageUri, int fileSize) {
        String[] filePath = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(imageUri, filePath, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePath[0]);
        String imagePath = cursor.getString(columnIndex);
        cursor.close();

        return getBitmapFromFile(context, imagePath, fileSize, getRotate(imagePath));

    }

    /**
     * @brief : 카메라 촬영으로 캡쳐된 이미지 파일을 Bitmap으로 변경하여 리턴한다.
     * @param context
     * @param captureImageName
     * @param fileSize
     * @return
     */
    public static Bitmap getBitmapFromCamera(final Context context, String captureImageName, int fileSize) {
        File f = new File(Environment.getExternalStorageDirectory(), captureImageName);

        if (!f.exists()) {
            Toast.makeText(context, R.string.damage_image, Toast.LENGTH_SHORT).show();
            return null;
        }


        return getBitmapFromFile(context, f.getAbsolutePath(), fileSize, getRotate(f.getAbsolutePath()));
    }

    /**
     * @brief : Bitmap 이미지를 scale, rotate 하여 리턴한다.
     * @param bitmap
     * @param rotate
     * @param fileSize
     * @return
     */
    public static Bitmap scaleImage(Bitmap bitmap, int rotate, int fileSize) {
        int width = bitmap.getWidth();
        int heihgt = bitmap.getHeight();

        float xScale = ((float) fileSize) / width;
        float yScale = ((float) fileSize) / heihgt;
        float scale = (xScale <= yScale) ? xScale : yScale;

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        matrix.postRotate(rotate);

        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, heihgt, matrix, true);
        return scaledBitmap;
    }

    public static void showSoftkeyboard(Context context, TextView inputText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput( inputText, 0 );
    }
    public static void hideSoftKeyboard(Context context, TextView inputText, boolean isClearFocus) {
        if(isClearFocus) inputText.clearFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService( Context.INPUT_METHOD_SERVICE );
        imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
    }

    public static void showForceSoftkeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
    public static void hideForceSoftKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService( Context.INPUT_METHOD_SERVICE );
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static void setGlobalFont(View view) {
        if (view != null) {
            if (view instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) view;
                int len = vg.getChildCount();
                for (int i = 0; i < len; i++) {
                    View v = vg.getChildAt(i);
                    if (v instanceof TextView) {
                        ((TextView) v).setTypeface(MainApplication.mTypeface);
                    }
                    setGlobalFont(v);
                }
            }
        }

    }
}
