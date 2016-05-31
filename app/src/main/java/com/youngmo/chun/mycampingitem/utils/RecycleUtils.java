package com.youngmo.chun.mycampingitem.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

public class RecycleUtils {
	private RecycleUtils() {
	};

	public static void recursiveRecycle(View root) {
		recursiveRecycle(root, false);
	}
	
	@SuppressLint("NewApi")
	public static void recursiveRecycle(View root, boolean isRecycleImageView) {
		if (root == null)
			return;
		
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			root.setBackgroundDrawable(null);
		} else {
			root.setBackground(null);
		}
		
		
		if (root instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) root;
			int count = group.getChildCount();
			for (int i = 0; i < count; i++) {
				recursiveRecycle(group.getChildAt(i), isRecycleImageView);
			}

			if (!(root instanceof AdapterView)) {
				group.removeAllViews();
			}
		}

		if (root instanceof ImageView) {
			if(isRecycleImageView) {
				RecycleUtils.imageViewRecycle((ImageView)root);
			}
			((ImageView) root).setImageDrawable(null);
			
		}

		root = null;

		return;
	}
	
	public static void imageViewRecycle(ImageView imageView) {
		if(imageView !=null && imageView.getDrawable() instanceof BitmapDrawable) {
			BitmapDrawable drawable = (BitmapDrawable)imageView.getDrawable();
			if(drawable.getBitmap() != null) {
				Bitmap bitmap = drawable.getBitmap();
				bitmapRecycle(bitmap);
			}
		}
	}
	
	public static void bitmapRecycle(Bitmap bitmap) {
		if(bitmap !=null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
		
	}
	
}
