package com.syu.dvr.control.zhanxun;

import com.syu.codec.Codec.InitCallback;
import com.syu.codec.PreviewView;
import com.syu.dvr.TheApp;
import com.syu.dvr.control.CameraManager;
import com.syu.dvr.control.CameraPreview;
import com.syu.dvr.observ.WatchedUsb;
import com.syu.dvr.utils.LogCatUtils;

import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;

public class CameraPreview4G extends CameraPreview{
	private CameraManager mCManager;
	public CameraPreview4G(CameraManager cameraManager) {
		super(cameraManager);
		// TODO Auto-generated constructor stub
		this.mCManager=cameraManager;
	}
	private PreviewView mSurfaceView;
	
	@Override
	public View initData() {
		// TODO Auto-generated method stub
		LogCatUtils.showString("======initdata===");
		mSurfaceView=new PreviewView(TheApp.mApp);
		mSurfaceView.setCallback(new com.syu.codec.PreviewView.Callback() {
			
			@Override
			public void onDestroyed(int type) {
				mCManager.removePreviewView(type);
			}
			
			@Override
			public void onCreated(int type, SurfaceHolder holder) {
				mCManager.startPreview(type, null);
			}
		});
		mSurfaceView.setType(TheApp.getDeviceId());
		mSurfaceView.setInitCallback(new InitCallback() {
			@Override
			public void onCallback(boolean result) {
				LogCatUtils.showString(" setInitCallback = "+String.valueOf(result));
				if (result) {
					mCManager.setmPreviewView(mSurfaceView, TheApp.getDeviceId());
					addHandlerUI();
				}
			}
		});
		mSurfaceView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showLayout();
			}
		});
		return mSurfaceView;
	}
	@Override
	public void onDestroyTextureView() {
		// TODO Auto-generated method stub
		mCManager.doStopPreview(TheApp.getDeviceId());
		if (mSurfaceView!=null) {
			mSurfaceView.setVisibility(View.INVISIBLE);
		}
	}
	
}
