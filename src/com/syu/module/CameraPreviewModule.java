package com.syu.module;

import android.view.View;
import android.widget.LinearLayout;

public class CameraPreviewModule {
	private LinearLayout mFromLayout;
	private LinearLayout.LayoutParams mLayoutParams;
	private View mRecodLayout;
	public LinearLayout getmFromLayout() {
		return mFromLayout;
	}
	public void setmFromLayout(LinearLayout mFromLayout) {
		this.mFromLayout = mFromLayout;
	}
	public LinearLayout.LayoutParams getmLayoutParams() {
		return mLayoutParams;
	}
	public void setmLayoutParams(LinearLayout.LayoutParams mLayoutParams) {
		this.mLayoutParams = mLayoutParams;
	}
	public View getmRecodLayout() {
		return mRecodLayout;
	}
	public void setmRecodLayout(View mRecodLayout) {
		this.mRecodLayout = mRecodLayout;
	}
	
}
