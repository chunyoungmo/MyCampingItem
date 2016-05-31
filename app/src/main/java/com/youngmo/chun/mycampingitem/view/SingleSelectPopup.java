package com.youngmo.chun.mycampingitem.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youngmo.chun.mycampingitem.R;
import com.youngmo.chun.mycampingitem.model.SingleSelectPopupItemInfo;
import com.youngmo.chun.mycampingitem.utils.Util;

import java.util.ArrayList;

/**
 * Single 선택 팝업 담당 처리 뷰
 */
public class SingleSelectPopup extends RelativeLayout {
    private final String    TAG = Util.getTagBaseClassName(this);

    public interface SingleSelectPopupListener {
        public abstract void onSelectedItem(int selectedIndex);
        public abstract void onCancel();
    }

    /** Type 정의 */
    public enum Type {
        /** 일반 타입 */
        TYPE_NORMAL,
        /** 일반 타입 + 이미지 */
        TYPE_NORMAL_ADD_IMAGE,
        /** 선택상태 표시 타입 */
        TYPE_SELECTED_STATUS,
        /** 선택상태 표시 타입 + 이미지 */
        TYPE_SELECTED_STATUS_ADD_IMAGE,
    }

    private Context                                 mContext;
    private SingleSelectPopupListener               mListener;
    private TextView                                mTitleTxtView;
    private ListView                                mListView;
    private SingleSelectAdapter                     mAdapter;
    private ArrayList<SingleSelectPopupItemInfo>    mMenuItemArr = new ArrayList<>();
    private int                                     mSelectedIndex;
    private Type                                    mType;

    public SingleSelectPopup(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public SingleSelectPopup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public SingleSelectPopup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    /**
     * 초기 처리
     */
    private void init() {
        mType = Type.TYPE_NORMAL;
        setViewInflate();
        setViewReference();
        setViewEventListener();
        setData();
        setViewUI();
    }

    /**
     * Listener 등록
     * @param listener
     */
    public void setListener(SingleSelectPopupListener listener) {
        mListener = listener;
    }


    /**
     * layout inflate
     */
    private void setViewInflate() {
        LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Util.setGlobalFont(layoutInflater.inflate(R.layout.view_single_select_popup, this));
    }

    /**
     * 참조할 View ID 맵핑
     */
    private void setViewReference() {
        mTitleTxtView = (TextView)findViewById(R.id.single_select_popup_title);
        mListView = (ListView)findViewById(R.id.single_select_popup_list_view);
    }

    /**
     * View에 이벤트 등록
     */
    private void setViewEventListener()
    {
        // 팝업 Dim 영역 클릭 시, 이벤트 전달 처리
        findViewById(R.id.single_select_popup_layout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null) {
                    mListener.onCancel();
                }
            }
        });

        // '취소' 버튼 클릭 시, 이벤트 전달 처리
        findViewById(R.id.single_select_popup_cancel_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onCancel();
                }
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SingleSelectAdapter.ViewHolder viewHolder = (SingleSelectAdapter.ViewHolder) view.getTag();

                mSelectedIndex = position;
                viewHolder.mSelectBtn.setSelected(true);

                if (mListener != null) {
                    mListener.onSelectedItem(mSelectedIndex);
                }

            }
        });
    }

    /**
     * @brief : 데이터 관련 셋팅 작업
     */
    private void setData() {
        mAdapter = new SingleSelectAdapter();
        mListView.setAdapter(mAdapter);
    }

    /**
     * @brief : View들의 UI구성요소 셋팅
     */
    private void setViewUI() {

    }

    public void setMenuItemInfo(Type type, String title,  ArrayList<SingleSelectPopupItemInfo> menuItemArr, int selectedIndex) {
        mType = type;

        if(Util.isValid(title)) {
            findViewById(R.id.single_select_popup_title_layout).setVisibility(VISIBLE);
            mTitleTxtView.setText(title);
        }
        else {
            findViewById(R.id.single_select_popup_title_layout).setVisibility(GONE);
        }

        mMenuItemArr = menuItemArr;
        mSelectedIndex = selectedIndex;
        mAdapter.notifyDataSetChanged();

        // ListView 높이를 menuItemArr에 따라서 동적 크기 처리
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        if(menuItemArr.size() < 8) {
            // dp_40 은 list의 셀 높이임, dp_5 은 스크롤 뷰가 생기지 않을 정도의 여분 추가함
            params.height = ((int)getResources().getDimension(R.dimen.dp_40) * menuItemArr.size()) + (int)getResources().getDimension(R.dimen.dp_5);
        }
        else {
            params.height = (int)getResources().getDimension(R.dimen.dp_320);
        }
        mListView.setLayoutParams(params);

    }

    private class SingleSelectAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mMenuItemArr.size();
        }

        @Override
        public Object getItem(int position) {
            return mMenuItemArr.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;

            if(convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.adapter_single_select_popup_row, null);
                Util.setGlobalFont(convertView);
                viewHolder = new ViewHolder();

                viewHolder.mImage = (ImageView)convertView.findViewById(R.id.single_select_popup_row_image);
                viewHolder.mTitleTxtView = (TextView)convertView.findViewById(R.id.single_select_popup_row_title_text_view);
                viewHolder.mSelectBtn = (Button)convertView.findViewById(R.id.single_select_popup_row_radio_button);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            // UI 컨포넌트 표시 체크 처리
            switch (mType) {
                case TYPE_NORMAL:
                    viewHolder.mImage.setVisibility(GONE);
                    viewHolder.mSelectBtn.setVisibility(GONE);
                    break;

                case TYPE_NORMAL_ADD_IMAGE:
                    viewHolder.mImage.setVisibility(VISIBLE);
                    viewHolder.mSelectBtn.setVisibility(GONE);
                    break;

                case TYPE_SELECTED_STATUS:
                    viewHolder.mImage.setVisibility(GONE);
                    viewHolder.mSelectBtn.setVisibility(VISIBLE);
                    break;

                case TYPE_SELECTED_STATUS_ADD_IMAGE:
                    viewHolder.mImage.setVisibility(VISIBLE);
                    viewHolder.mSelectBtn.setVisibility(VISIBLE);
                    break;
            }

            SingleSelectPopupItemInfo itemInfo = mMenuItemArr.get(position);
            if(mType == Type.TYPE_NORMAL_ADD_IMAGE || mType == Type.TYPE_SELECTED_STATUS_ADD_IMAGE) {
                Drawable drawable = itemInfo.getDrawableImage();
                if(drawable == null) {
                    viewHolder.mImage.setVisibility(GONE);
                }
                else {
                    viewHolder.mImage.setVisibility(VISIBLE);
                    viewHolder.mImage.setImageDrawable(drawable);
                }
            }

            viewHolder.mTitleTxtView.setText(itemInfo.getName());

            if(mType == Type.TYPE_SELECTED_STATUS) {
                if (mSelectedIndex == position) {
                    viewHolder.mSelectBtn.setSelected(true);
                }
            }

            return convertView;
        }

        private class ViewHolder {
            ImageView   mImage;
            TextView    mTitleTxtView;
            Button      mSelectBtn;
        }
    }
}