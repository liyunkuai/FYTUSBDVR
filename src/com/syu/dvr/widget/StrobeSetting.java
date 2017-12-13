package com.syu.dvr.widget;

import com.syu.dvr.R;
import com.syu.dvr.TheApp;
import com.syu.dvr.utils.LogCatUtils;
import com.uvc.jni.RainUvc;

import android.content.Context;
import android.util.AttributeSet;

public class StrobeSetting extends SettingLayout{
	public StrobeSetting(Context context, AttributeSet attrs) {
		super(context, attrs);
		text= context.getResources().getString(R.string.strobe);
		mTittle.setText(text);
		mValues=context.getResources().getStringArray(R.array.strobe_setting);
	}
	@Override
	public void updataSummary(int value, int[] setValue) {
		// TODO Auto-generated method stub
		super.updataSummary(value,setValue);
		LogCatUtils.showString("====value=="+value);
		LogCatUtils.showString("  value.size = "+mValues.length);
		for (int i = 0; i < mRadioButtons.size(); i++) {
			mRadioButtons.get(i).setChecked(false);
			if (mRadioButtons.size()>value) {
				mRadioButtons.get(value).setChecked(true);
			}
		}
		if (value>=mValues.length) {
			return;
		}else{
			mSummary.setText(mValues[value]);
			this.mAbleValue=value;
		}
	}
	@Override
	public void updataSetting(int value) {
		// TODO Auto-generated method stub
		super.updataSetting(value);
		int ret=RainUvc.setFrequence(value);
		if (ret>=0) {
			updataSummary(value,null);
		}
	}
}
