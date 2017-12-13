package com.syu.dvr.widget;

import com.syu.dvr.R;
import com.syu.dvr.TheApp;

import android.content.Context;
import android.util.AttributeSet;

public class HdrSetting extends SettingSwitch{

	public HdrSetting(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTittle.setText("HDR");
	}
	@Override
	public void updataSwitchCheck(boolean isAudioRec) {
		// TODO Auto-generated method stub
		super.updataSwitchCheck(isAudioRec);
		if (TheApp.mIsShengMaiIC) {
			
		}else {
			if (!isAudioRec) {
				setValue[11]=0;
			}else{
				setValue[11]=1;
			}
			sendBroadcast();
		}
	}
}
