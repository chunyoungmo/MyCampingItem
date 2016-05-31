package com.youngmo.chun.mycampingitem.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.youngmo.chun.mycampingitem.R;

public class LoadingDialog extends Dialog {
    private Context             mContext;
    private int                 mLayoutId;
    private AnimationDrawable   mAniDraw = null;
    private ImageView           mLoadingImgView = null;

    public LoadingDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mLayoutId);

        mLoadingImgView = (ImageView)findViewById(R.id.loading_image_view);
    }

    public void setLayoutId(int layoutId) {
        this.mLayoutId = layoutId;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mLoadingImgView != null) {
            mAniDraw = (AnimationDrawable)mLoadingImgView.getBackground();
            if(mAniDraw != null) {
                mAniDraw.start();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAniDraw != null) {
            mAniDraw.stop();
        }
    }
}
