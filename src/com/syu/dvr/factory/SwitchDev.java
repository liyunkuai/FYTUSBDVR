package com.syu.dvr.factory;

import com.syu.dvr.TheApp;
import com.syu.dvr.control.CameraManager;
import com.syu.dvr.control.FytDvrTypeControl;
import com.syu.dvr.utils.Config;

import android.os.Handler;
import android.os.Looper;

public class SwitchDev {
	private static SwitchDev switchDev;
	private boolean isSwitch=false;
	private boolean mSwitchfu=false;
	private CameraManager mCManager;
	public SwitchDev() {
		
	}
	public static SwitchDev getInstance() {
		if (switchDev==null) {
			synchronized (SwitchDev.class) {
				if (switchDev==null) {
					switchDev=new SwitchDev();
				}
			}
		}
		return switchDev;
	}
	
	public void setSwitch(boolean mSwitch) {
		this.isSwitch = mSwitch;
		mSwitchfu=false;
		mCManager=FytDvrTypeControl.getInstance().getmCManager();
		handler.postDelayed(new Runnable() {
			public void run() {
				if (isSwitch&&(!mSwitchfu)&&(mCManager!=null)) {
					if (mCManager!=null) {
						mCManager.getUvcExtenrnCall(TheApp.getDeviceId(), "0B",
						String.valueOf(0), String.valueOf(0));
					}
				}
				isSwitch=false;
				mSwitchfu=false;
			}
		}, 10*1000);
	}
	
	public void setmSwitchfu(boolean mSwitchfu) {
		this.mSwitchfu = mSwitchfu;
	}
	private Handler handler=new Handler(Looper.getMainLooper());

}
