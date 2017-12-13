package com.syu.dvr.control;

import com.syu.dvr.TheApp;
import com.syu.dvr.control.sofia.CameraManagerSofia;
import com.syu.dvr.control.sofia.CameraPreviewSofia;
import com.syu.dvr.control.sofia.MultimediaControlImpl;
import com.syu.dvr.control.zhanxun.CameraManager4G;
import com.syu.dvr.control.zhanxun.CameraPreview4G;
import com.syu.dvr.control.zhanxun.MultimediaControl4gImpl;
import com.syu.dvr.utils.FinalChip;
import com.syu.dvr.utils.LogCatUtils;
import com.syu.dvr.utils.ShareHandler;
import com.syu.dvr.widget.StateSignLayout;

import android.os.SystemProperties;

public class FytDvrTypeControl {
	private CameraPreview mCameraPreview;
	private CameraManager mCManager;
	private MultimediaControl multimediaControl;
	private int platform=0;
	static FytDvrTypeControl control;
	private StateSignLayout mStateLayout;
	public static FytDvrTypeControl getInstance(){
		if (control==null) {
			synchronized (FytDvrTypeControl.class) {
				if (control==null) {
					control=new FytDvrTypeControl();
				}
			}
		}
		return control;
	}
	private int KEY_CHIP_ID=13;
	public void dvrTypeCreat(){
		platform=ShareHandler.getInt(TheApp.mApp.getContentResolver(), KEY_CHIP_ID, 0);
//		platform = SystemProperties.getInt("ro.fyt.platform", 0);
		LogCatUtils.showString(" platform = "+String.valueOf(platform));
		if (platform==FinalChip.CHIP_SG9832) {
			mCManager=new CameraManager4G();
			mCameraPreview = new CameraPreview4G(mCManager);
			multimediaControl=new MultimediaControl4gImpl(mCManager);
		}else/* if (platform==FinalChip.CHIP_RKPX5) */{
			mCManager=new CameraManagerSofia();
			mCameraPreview = new CameraPreviewSofia(mCManager);
			multimediaControl=new MultimediaControlImpl(mCManager);
		}
	}
	public CameraPreview getmCameraPreview() {
		return mCameraPreview;
	}
	public CameraManager getmCManager() {
		return mCManager;
	}
	
	public int getPlatform() {
		return platform;
	}
	public StateSignLayout getmStateLayout() {
		return mStateLayout;
	}
	public void setmStateLayout(StateSignLayout mStateLayout) {
		this.mStateLayout = mStateLayout;
	}
	public MultimediaControl getMultimediaControl() {
		return multimediaControl;
	}
}
