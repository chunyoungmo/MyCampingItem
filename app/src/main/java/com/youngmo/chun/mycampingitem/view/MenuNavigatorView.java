package com.youngmo.chun.mycampingitem.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.youngmo.chun.mycampingitem.R;
import com.youngmo.chun.mycampingitem.activity.MainActivity;
import com.youngmo.chun.mycampingitem.activity.SettingsActivity;
import com.youngmo.chun.mycampingitem.utils.Util;

/**
 * 메뉴 네비게이션 담당 처리 뷰
 */
public class MenuNavigatorView extends RelativeLayout {
    private final String    TAG = Util.getTagBaseClassName(this);

    public interface MenuNavigatorViewListener {
        public abstract void onPreClickMainMenu();
        public abstract void onClickMainMenu();
        public abstract void onClickSubCommonMenu(SubMenuTag subCommonMenuTag);
        public abstract void onClickSubCustomMenu(SubMenuTag subCustomMenuTag);
    }

    /** 서브 메뉴 Tag 값 정의 */
    public enum SubMenuTag {
        SUB_MENU_TAG_COMMON_1,
        SUB_MENU_TAG_COMMON_2,
        SUB_MENU_TAG_CUSTOM_1,
        SUB_MENU_TAG_CUSTOM_2,
        SUB_MENU_TAG_CUSTOM_3,
    };

    private Context         mContext;
    private ViewGroup       mLayout;
    private Button          mMainMenuBtn;
    private View            mSubCommonMenuView;
    private Button          mSubCommonHomeBtn;
    private Button          mSubCommonSettingBtn;
    private View            mSubMenuView;
    private Button          mSubMenu1Btn;
    private Button          mSubMenu2Btn;
    private Button          mSubMenu3Btn;

    /** 메뉴 오픈 여부 상태 */
    private boolean                     mIsMenuOpen = false;
    /** 메뉴 타입 */
    private MenuNavigatorType           mMenuNavigatorType;
    /** Listener */
    private MenuNavigatorViewListener   mListenr = null;
    /** 메인메뉴 클릭 전에 감지 전달 여부 */
    private boolean                     mIsPreClickDetectMainMenu = false;

    /** 메인, 서브 메뉴 구성 타입 */
    public enum MenuNavigatorType {
        MENU_NAVIGATOR_TYPE_ONLY_MAIN_MENU, // 메인 메뉴만 사용 타입
        MENU_NAVIGATOR_TYPE_MAIN_SUB_MENU   // 메인,서브 메뉴 모두 사용 타입
    }

    public MenuNavigatorView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MenuNavigatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public MenuNavigatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    /**
     * 초기 처리
     */
    private void init() {
        setViewInflate();
        setViewReference();
        setViewEventListener();
    }

    /**
     * Listener 등록
     * @param listener
     */
    public void setListener(MenuNavigatorViewListener listener) {
        mListenr = listener;
    }

    /**
     * Listener 등록
     * @param listener
     * @param isPreClickDetectMainMenu
     */
    public void setListener(MenuNavigatorViewListener listener, boolean isPreClickDetectMainMenu) {
        mListenr = listener;
        mIsPreClickDetectMainMenu = isPreClickDetectMainMenu;
    }

    /**
     * 메인, 서브 메뉴 구성을 위한 정보 셋팅
     * @param type
     * @param mainMenu
     * @param subMenu1
     * @param subMenu2
     * @param subMenu3
     */
    public void setMenuNavigatorInfo(MenuNavigatorType type, String mainMenu, String subMenu1, String subMenu2, String subMenu3) {
        mMenuNavigatorType = type;

        if(mMenuNavigatorType == MenuNavigatorType.MENU_NAVIGATOR_TYPE_ONLY_MAIN_MENU) {
            if(Util.isValid(mainMenu)) {
                mMainMenuBtn.setText(mainMenu);
                mMainMenuBtn.setTag(mainMenu);
            }
        }
        else {
            if(Util.isValid(mainMenu)) {
                mMainMenuBtn.setText(mainMenu);
                mMainMenuBtn.setTag(mainMenu);
            }

            mSubCommonSettingBtn.setTag(SubMenuTag.SUB_MENU_TAG_COMMON_1);
            mSubCommonHomeBtn.setTag(SubMenuTag.SUB_MENU_TAG_COMMON_2);
            mSubMenu1Btn.setTag(SubMenuTag.SUB_MENU_TAG_CUSTOM_1);
            mSubMenu2Btn.setTag(SubMenuTag.SUB_MENU_TAG_CUSTOM_2);
            mSubMenu3Btn.setTag(SubMenuTag.SUB_MENU_TAG_CUSTOM_3);

            if(Util.isValid(subMenu1)) {
                mSubMenu1Btn.setText(subMenu1);
                mSubMenu1Btn.setVisibility(VISIBLE);
            }
            else {
                mSubMenu1Btn.setVisibility(GONE);
            }

            if(Util.isValid(subMenu2)) {
                mSubMenu2Btn.setText(subMenu2);
                mSubMenu2Btn.setVisibility(VISIBLE);
            }
            else {
                mSubMenu2Btn.setVisibility(GONE);
            }

            if(Util.isValid(subMenu3)) {
                mSubMenu3Btn.setText(subMenu3);
                mSubMenu3Btn.setVisibility(VISIBLE);
            }
            else {
                mSubMenu3Btn.setVisibility(GONE);
            }
        }

        mLayout.setClickable(false);
    }

    /**
     * 메인, 서브 메뉴 구성을 위한 정보 셋팅
     * @param type
     * @param mainMenuImg
     * @param subMenu1Img
     * @param subMenu2Img
     * @param subMenu3Img
     */
    public void setMenuNavigatorInfo(MenuNavigatorType type, Drawable mainMenuImg, Drawable subMenu1Img, Drawable subMenu2Img, Drawable subMenu3Img) {
        mMenuNavigatorType = type;

        if(mMenuNavigatorType == MenuNavigatorType.MENU_NAVIGATOR_TYPE_ONLY_MAIN_MENU) {
            if(mainMenuImg != null)
                mMainMenuBtn.setBackground(mainMenuImg);
        }
        else {
            if (mainMenuImg != null)
                mMainMenuBtn.setBackground(mainMenuImg);

            mSubCommonSettingBtn.setTag(SubMenuTag.SUB_MENU_TAG_COMMON_1);
            mSubCommonHomeBtn.setTag(SubMenuTag.SUB_MENU_TAG_COMMON_2);
            mSubMenu1Btn.setTag(SubMenuTag.SUB_MENU_TAG_CUSTOM_1);
            mSubMenu2Btn.setTag(SubMenuTag.SUB_MENU_TAG_CUSTOM_2);
            mSubMenu3Btn.setTag(SubMenuTag.SUB_MENU_TAG_CUSTOM_3);

            if(subMenu1Img != null) {
                mSubMenu1Btn.setBackground(subMenu1Img);
                mSubMenu1Btn.setVisibility(VISIBLE);
            }
            else {
                mSubMenu1Btn.setVisibility(GONE);
            }

            if(subMenu2Img != null) {
                mSubMenu2Btn.setBackground(subMenu2Img);
                mSubMenu2Btn.setVisibility(VISIBLE);
            }
            else {
                mSubMenu2Btn.setVisibility(GONE);
            }

            if(subMenu3Img != null) {
                mSubMenu3Btn.setBackground(subMenu3Img);
                mSubMenu3Btn.setVisibility(VISIBLE);
            }
            else {
                mSubMenu3Btn.setVisibility(GONE);
            }
        }

        mLayout.setClickable(false);
    }

    /**
     * layout inflate
     */
    private void setViewInflate() {
        LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Util.setGlobalFont(layoutInflater.inflate(R.layout.view_menu_navigator, this));
    }

    /**
     * 참조할 View ID 맵핑
     */
    private void setViewReference() {
        mLayout = (ViewGroup) findViewById(R.id.menu_navigator_layout);
        mMainMenuBtn = (Button)findViewById(R.id.menu_navigator_main_button);
        mSubCommonMenuView = findViewById(R.id.menu_navigator_sub_common_menu_layout);
        mSubCommonHomeBtn = (Button)findViewById(R.id.menu_navigator_sub_common_menu_home);
        mSubCommonSettingBtn = (Button)findViewById(R.id.menu_navigator_sub_common_menu_setting);
        mSubMenuView = findViewById(R.id.menu_navigator_sub_menu_layout);
        mSubMenu1Btn = (Button)findViewById(R.id.menu_navigator_sub_menu1);
        mSubMenu2Btn = (Button)findViewById(R.id.menu_navigator_sub_menu2);
        mSubMenu3Btn = (Button)findViewById(R.id.menu_navigator_sub_menu3);

    }

    /**
     * View에 이벤트 등록
     */
    private void setViewEventListener() {
        mLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // do nothing... (밑에 깔려있는 UI 동작 막기 위함)
                clickMainMenu();
            }
        });

        mMainMenuBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mIsPreClickDetectMainMenu) {
                    if(mListenr != null) {
                        mListenr.onPreClickMainMenu();
                    }
                }
                else {
                    clickMainMenu();
                }
            }
        });

        mSubCommonHomeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListenr != null) {
                    mListenr.onClickSubCommonMenu((SubMenuTag) mSubCommonHomeBtn.getTag());
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goToHomeScreen();
                    }
                }, 200);
            }
        });

        mSubCommonSettingBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListenr != null) {
                    mListenr.onClickSubCommonMenu((SubMenuTag) mSubCommonSettingBtn.getTag());
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goToSettingScreen();
                    }
                }, 200);
            }
        });

        mSubMenu1Btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSubCustomMenu((SubMenuTag) view.getTag());
            }
        });
        mSubMenu2Btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSubCustomMenu((SubMenuTag) view.getTag());
            }
        });
        mSubMenu3Btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSubCustomMenu((SubMenuTag) view.getTag());
            }
        });
    }

    private void clickSubCustomMenu(SubMenuTag menuTag) {
        if(mListenr != null) {
            mListenr.onClickSubCustomMenu(menuTag);
        }
    }


    /**
     * 메인 메뉴 버튼 클릭 처리
     */
    private void clickMainMenu() {
        // 리스너를 통해서 메인 메뉴 버튼 클릭 이벤트 전달
        if (mListenr != null) {
            mListenr.onClickMainMenu();
        }

        if(mMenuNavigatorType == MenuNavigatorType.MENU_NAVIGATOR_TYPE_ONLY_MAIN_MENU) {
            return;
        }
        toggleMainMenu();
    }

    public void clickMainMenuByForce() {
        if(mMenuNavigatorType == MenuNavigatorType.MENU_NAVIGATOR_TYPE_ONLY_MAIN_MENU) {
            return;
        }
        toggleMainMenu();
    }

    private void toggleMainMenu() {
        Animation an;
        if (mIsMenuOpen) {
            an = new RotateAnimation(0.0f, 90.0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        }
        else {
            an = new RotateAnimation(0.0f, -90.0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        }

        an.setDuration(300);
        an.setRepeatCount(0);
        an.setFillAfter(false);
        an.setFillEnabled(true);
        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mIsMenuOpen) {
                    mMainMenuBtn.setRotation(-90.0f);
                    showSubMenu();
                } else {
                    mMainMenuBtn.setRotation(0.0f);
                    hideSubMenu();
                }
                mMainMenuBtn.clearAnimation();
            }
        });

        mMainMenuBtn.startAnimation(an);
        mIsMenuOpen = !mIsMenuOpen;
    }

    /**
     * 서브 메뉴 보이기
     */
    private void showSubMenu() {
        mLayout.setClickable(true);
        mSubCommonMenuView.setVisibility(VISIBLE);
        mSubMenuView.setVisibility(VISIBLE);
    }

    /**
     * 서브 메뉴 숨기기
     */
    private void hideSubMenu() {
        mLayout.setClickable(false);
        mSubCommonMenuView.setVisibility(GONE);
        mSubMenuView.setVisibility(GONE);
    }

    /**
     * 메뉴 오픈 여부 상태
     * @return
     */
    public boolean isMenuOpen() {
        return mIsMenuOpen;
    }

    /**
     * @brief : 홈 화면으로 이동 처리
     */
    private void goToHomeScreen() {
        Intent intent = new Intent(mContext, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    /**
     * @brief : 설정 화면으로 이동 처리
     */
    private void goToSettingScreen() {
        Intent intent = new Intent(mContext, SettingsActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mContext.startActivity(intent);
    }


}
