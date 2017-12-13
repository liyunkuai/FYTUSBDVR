package com.syu.dvr.widget;

import com.syu.dvr.R;
import com.syu.dvr.TheApp;
import com.syu.dvr.utils.LogCatUtils;
import com.uvc.jni.RainUvc;

import android.content.Context;
import android.util.AttributeSet;

public class AudoSetting extends SettingSwitch{

	public AudoSetting(Context context, AttributeSet attrs) {
		super(context, attrs);
		text= context.getResources().getString(R.string.str_text_start_audo);
		mTittle.setText(text);
	}
	@Override
	public void updataSwitchCheck(boolean isAudioRec) {
		// TODO Auto-generated method stub
		super.updataSwitchCheck(isAudioRec);
		if (TheApp.mIsShengMaiIC) {
			int ret=RainUvc.recordAudio(isAudioRec?1:0);
			if (ret>=0) {
				updataLayout(isAudioRec,setValue);
			}
		}else {
			if (isAudioRec) {
				setValue[5]=0;
			}else {
				setValue[5]=1;
			}
			LogCatUtils.showString("===setValue[5]=="+setValue[5]);
			sendBroadcast();
		}
	}
}
