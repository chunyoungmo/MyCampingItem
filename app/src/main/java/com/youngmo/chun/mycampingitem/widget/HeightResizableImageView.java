
package com.youngmo.chun.mycampingitem.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @Class : HeightResizableImageView
 * @brief : 이미지를 width에 맞게 height를 변경 시킨다. (비율 유지됨)
 */

public class HeightResizableImageView extends ImageView {

	/**
	 * 생성자
	 * @brief : 
	 * @param context 
	 */
	public HeightResizableImageView(Context context) {
		super(context);
	}

	/**
	 * 생성자
	 * @brief : 
	 * @param context
	 * @param attrs 
	 */
	public HeightResizableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 생성자
	 * @brief : 
	 * @param context
	 * @param attrs
	 * @param defStyle 
	 */
	public HeightResizableImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Drawable d = getDrawable();
		if (d != null) { 
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
			setMeasuredDimension(width, height);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}