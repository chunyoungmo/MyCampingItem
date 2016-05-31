package com.youngmo.chun.mycampingitem.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youngmo.chun.mycampingitem.R;
import com.youngmo.chun.mycampingitem.utils.Util;

import java.util.ArrayList;

/**
 * 이미지 선택 팝업 담당 처리 뷰
 */
public class ImageSelectPopup extends RelativeLayout {
    private final String    TAG = Util.getTagBaseClassName(this);

    public interface ImageSelectPopupListener {
        public abstract void onSelectedItem(int selectedIndex);
        public abstract void onCancel();
    }

    private Context                                 mContext;
    private ImageSelectPopupListener                mListener;
    private TextView                                mTitleTxtView;
    private GridView                                mGridView;
    private ImageSelectAdapter                      mAdapter;
    private ArrayList<Bitmap>                       mImageBitmapArr = new ArrayList<>();
    private int                                     mSelectedIndex;

    public ImageSelectPopup(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ImageSelectPopup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public ImageSelectPopup(Context context, AttributeSet attrs, int defStyleAttr) {
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
        setData();
        setViewUI();
    }

    /**
     * Listener 등록
     * @param listener
     */
    public void setListener(ImageSelectPopupListener listener) {
        mListener = listener;
    }


    /**
     * layout inflate
     */
    private void setViewInflate() {
        LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Util.setGlobalFont(layoutInflater.inflate(R.layout.view_image_select_popup, this));
    }

    /**
     * 참조할 View ID 맵핑
     */
    private void setViewReference() {
        mTitleTxtView = (TextView)findViewById(R.id.image_select_popup_title);
        mGridView = (GridView)findViewById(R.id.image_select_popup_grid_view);
    }

    /**
     * View에 이벤트 등록
     */
    private void setViewEventListener()
    {
        // 팝업 Dim 영역 클릭 시, 이벤트 전달 처리
        findViewById(R.id.image_select_popup_layout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null) {
                    mListener.onCancel();
                }
            }
        });

        // '취소' 버튼 클릭 시, 이벤트 전달 처리
        findViewById(R.id.image_select_popup_cancel_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onCancel();
                }
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedIndex = position;

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
        mAdapter = new ImageSelectAdapter();
        mGridView.setAdapter(mAdapter);
    }

    /**
     * @brief : View들의 UI구성요소 셋팅
     */
    private void setViewUI() {

    }

    public void setMenuItemInfo(String title,  ArrayList<Bitmap> imageBitmapArr) {

        if(Util.isValid(title)) {
            findViewById(R.id.image_select_popup_title_layout).setVisibility(VISIBLE);
            mTitleTxtView.setText(title);
        }
        else {
            findViewById(R.id.image_select_popup_title_layout).setVisibility(GONE);
        }

        mImageBitmapArr = imageBitmapArr;
        mAdapter.notifyDataSetChanged();

        // GridView 높이를 mImageBitmapArr에 따라서 동적 크기 처리
        ViewGroup.LayoutParams params = mGridView.getLayoutParams();
        if(mImageBitmapArr.size() < 3) {
            params.height = (int)getResources().getDimension(R.dimen.dp_165);
        }
        else {
            params.height = (int)getResources().getDimension(R.dimen.dp_340);
        }
        mGridView.setLayoutParams(params);

    }

    private class ImageSelectAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mImageBitmapArr.size();
        }

        @Override
        public Object getItem(int position) {
            return mImageBitmapArr.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;

            if(convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.adapter_image_select_popup_row, null);
                Util.setGlobalFont(convertView);
                viewHolder = new ViewHolder();

                viewHolder.mImage = (ImageView)convertView.findViewById(R.id.image_select_popup_row_image);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.mImage.setImageBitmap(mImageBitmapArr.get(position));

            return convertView;
        }

        private class ViewHolder {
            ImageView   mImage;
        }
    }
}