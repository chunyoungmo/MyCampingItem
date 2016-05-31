package com.youngmo.chun.mycampingitem.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youngmo.chun.mycampingitem.R;
import com.youngmo.chun.mycampingitem.model.MultiSelectPopupItemInfo;
import com.youngmo.chun.mycampingitem.utils.Util;

import java.util.ArrayList;

/**
 * 멀티 선택 팝업 담당 처리 뷰
 */
public class MultiSelectPopup extends RelativeLayout {
    private final String    TAG = Util.getTagBaseClassName(this);

    public interface MultiSelectPopupListener {
        public abstract void onConfirm(ArrayList<MultiSelectPopupItemInfo> itemInfoArr);
        public abstract void onCancel();
    }

    /** Type 정의 */
    public enum Type {
        /** 일반 타입 */
        TYPE_NORMAL,
        /** 선택상태 표시 타입 */
        TYPE_SELECTED_STATUS,
    }

    private Context                             mContext;
    private MultiSelectPopupListener            mListener;
    private TextView                            mTitleTxtView;
    private ListView                            mListView;
    private MultiSelectAdapter                  mAdapter;
    private ArrayList<MultiSelectPopupItemInfo> mItemInfoArr = new ArrayList<>();
    private Type                                mType;

    public MultiSelectPopup(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MultiSelectPopup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public MultiSelectPopup(Context context, AttributeSet attrs, int defStyleAttr) {
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
    public void setListener(MultiSelectPopupListener listener) {
        mListener = listener;
    }


    /**
     * layout inflate
     */
    private void setViewInflate() {
        LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Util.setGlobalFont(layoutInflater.inflate(R.layout.view_multi_select_popup, this));
    }

    /**
     * 참조할 View ID 맵핑
     */
    private void setViewReference() {
        mTitleTxtView = (TextView)findViewById(R.id.multi_select_popup_title);
        mListView = (ListView)findViewById(R.id.multi_select_popup_list_view);
    }

    /**
     * View에 이벤트 등록
     */
    private void setViewEventListener()
    {
        // 팝업 Dim 영역 클릭 시, 이벤트 전달 처리
        findViewById(R.id.multi_select_popup_layout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null) {
                    mListener.onCancel();
                }
            }
        });

        // '취소' 버튼 클릭 시, 이벤트 전달 처리
        findViewById(R.id.multi_select_popup_cancel_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onCancel();
                }
            }
        });

        // '확인' 버튼 클릭 시, 이벤트 전달 처리
        findViewById(R.id.multi_select_popup_confirm_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.onConfirm(mItemInfoArr);
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MultiSelectAdapter.ViewHolder viewHolder = (MultiSelectAdapter.ViewHolder) view.getTag();
                setToggleCheck(viewHolder.mSelectBtn, position);
            }
        });
    }

    /**
     * @brief : 데이터 관련 셋팅 작업
     */
    private void setData() {
        mAdapter = new MultiSelectAdapter();
        mListView.setAdapter(mAdapter);
    }

    /**
     * @brief : View들의 UI구성요소 셋팅
     */
    private void setViewUI() {

    }

    public void setMenuItemInfo(Type type, String title, ArrayList<MultiSelectPopupItemInfo> itemInfoArr) {
        mType = type;

        if(Util.isValid(title)) {
            findViewById(R.id.multi_select_popup_title_layout).setVisibility(VISIBLE);
            mTitleTxtView.setText(title);
        }
        else {
            findViewById(R.id.multi_select_popup_title_layout).setVisibility(GONE);
        }

        mItemInfoArr = itemInfoArr;
        mAdapter.notifyDataSetChanged();

        // ListView 높이를 menuItemArr에 따라서 동적 크기 처리
        ViewGroup.LayoutParams params = mListView.getLayoutParams();
        if(mItemInfoArr.size() < 6) {
            // dp_40 은 list의 셀 높이임, dp_5 은 스크롤 뷰가 생기지 않을 정도의 여분 추가함
            params.height = ((int)getResources().getDimension(R.dimen.dp_40) * mItemInfoArr.size()) + (int)getResources().getDimension(R.dimen.dp_5);
        }
        else {
            params.height = (int)getResources().getDimension(R.dimen.dp_240);
        }
        mListView.setLayoutParams(params);
    }

    private void setToggleCheck(Button selectBtn, int itemPosition) {
        boolean isSelected = selectBtn.isSelected();

        mItemInfoArr.get(itemPosition).setIsSelected(!isSelected);
        mAdapter.notifyDataSetChanged();
    }

    private class MultiSelectAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mItemInfoArr.size();
        }

        @Override
        public Object getItem(int position) {
            return mItemInfoArr.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;

            final MultiSelectPopupItemInfo itemInfo = mItemInfoArr.get(position);

            if(convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.adapter_multi_select_popup_row, null);
                Util.setGlobalFont(convertView);
                viewHolder = new ViewHolder();

                viewHolder.mTitleTxtView = (TextView)convertView.findViewById(R.id.multi_select_popup_row_title_text_view);
                viewHolder.mSelectBtn = (Button)convertView.findViewById(R.id.multi_select_popup_row_check_button);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            final int itemPosition = position;
            viewHolder.mSelectBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setToggleCheck((Button)v, itemPosition);

                }
            });

            viewHolder.mTitleTxtView.setText(itemInfo.getName());

            if (itemInfo.isSelected())
                viewHolder.mSelectBtn.setSelected(true);
            else
                viewHolder.mSelectBtn.setSelected(false);

            return convertView;
        }

        private class ViewHolder {
            TextView    mTitleTxtView;
            Button      mSelectBtn;
        }
    }
}