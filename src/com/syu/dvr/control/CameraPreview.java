package com.syu.dvr.control;

import com.syu.dvr.observ.WatchedUsb;

import android.view.View;

public abstract class CameraPreview {
	
	public abstract View initData();
	public abstract void onDestroyTextureView();
	public CameraPreview(CameraManager cameraManager){};
	public void addHandlerUI(){
		WatchedUsb.getInstance().notifyWatchers(5);
	}
	public void showLayout() {
		// TODO Auto-generated method stub
		WatchedUsb.getInstance().notifyWatchers(6);
	}
}
