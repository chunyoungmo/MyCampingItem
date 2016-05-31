package com.youngmo.chun.mycampingitem.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youngmo.chun.mycampingitem.R;
import com.youngmo.chun.mycampingitem.utils.Util;

/**
 * 입력 팝업 담당 처리 뷰
 */
public class InputPopup extends RelativeLayout {
    private final String    TAG = Util.getTagBaseClassName(this);

    public interface InputPopupListener {
        public abstract void onInputText(String inputText);
        public abstract void onCancel();
    }

    private Context             mContext;
    private InputPopupListener  mListener;
    private TextView            mTitleTxtView;
    public EditText             mInputEditTxt;
    private String              mTitle;
    private String              mWillEditStr;


    public InputPopup(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public InputPopup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public InputPopup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    public InputPopup(Context context, String title, InputPopupListener listener) {
        super(context);
        mContext = context;
        mTitle = title;
        mListener = listener;
        init();
    }

    public InputPopup(Context context, String title, String willEditStr, InputPopupListener listener) {
        super(context);
        mContext = context;
        mTitle = title;
        mWillEditStr = willEditStr;
        mListener = listener;
        init();
    }

    /**
     * 초기 처리
     */
    private void init() {
        setViewInflate();
        setViewReference();
        setViewEventListener();
        setData();
        setViewUI();
    }


    /**
     * layout inflate
     */
    private void setViewInflate() {
        LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Util.setGlobalFont(layoutInflater.inflate(R.layout.view_input_popup, this));
    }

    /**
     * 참조할 View ID 맵핑
     */
    private void setViewReference() {
        mTitleTxtView = (TextView)findViewById(R.id.input_popup_title);
        mInputEditTxt = (EditText)findViewById(R.id.input_popup_edit_text);
    }

    /**
     * View에 이벤트 등록
     */
    private void setViewEventListener()
    {
        // 팝업 Dim 영역 클릭 시, 이벤트 전달 처리
        findViewById(R.id.input_popup_layout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.hideSoftKeyboard(mContext, mInputEditTxt, true);
                if(mListener != null) {
                    mListener.onCancel();
                }
            }
        });

        // '확인' 버튼 클릭 시, 이벤트 전달 처리
        findViewById(R.id.input_popup_confirm_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideSoftKeyboard(mContext, mInputEditTxt, true);
                if(mListener != null) {
                    mListener.onInputText(mInputEditTxt.getText().toString());
                }
            }
        });
    }

    /**
     * @brief : 데이터 관련 셋팅 작업
     */
    private void setData() {

    }

    /**
     * @brief : View들의 UI구성요소 셋팅
     */
    private void setViewUI() {
        if(Util.isValid(mTitle)) {
            findViewById(R.id.input_popup_title_layout).setVisibility(VISIBLE);
            mTitleTxtView.setText(mTitle);
        }
        else {
            findViewById(R.id.input_popup_title_layout).setVisibility(GONE);
        }

        mInputEditTxt.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if(Util.isValid(mWillEditStr)) {
                    mInputEditTxt.setText(mWillEditStr);
                    mInputEditTxt.setSelection(mWillEditStr.length());
                }

                mInputEditTxt.requestFocus();
                Util.showSoftkeyboard(mContext, mInputEditTxt);
            }
        }, 100);
    }
}