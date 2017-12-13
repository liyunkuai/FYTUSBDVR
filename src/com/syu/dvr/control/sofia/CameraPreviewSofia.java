package com.syu.dvr.control.sofia;

import com.syu.dvr.TheApp;
import com.syu.dvr.control.CameraManager;
import com.syu.dvr.control.CameraPreview;
import com.syu.dvr.utils.Config;

import android.graphics.SurfaceTexture;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;

public class CameraPreviewSofia extends CameraPreview{
	private CameraManager mCManager;
	public CameraPreviewSofia(CameraManager cameraManager) {
		super(cameraManager);
		// TODO Auto-generated constructor stub
		this.mCManager=cameraManager;
	}
	private TextureView mTextView;
	@Override
	public View initData() {
		mTextView=new TextureView(TheApp.mApp);
		mTextView.setSurfaceTextureListener(new MySurListen(1));
		mTextView.setId(0x123);
		mTextView.setAlpha(0);
		mTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showLayout();
			}
		});
		return mTextView;
	}
	@Override
	public void onDestroyTextureView() {
		// TODO Auto-generated method stub
		mCManager.doStopPreview(Config.SUPPLIER_CAMERA_SUNPLUS);
		if (mTextView!=null) {
			mTextView.setVisibility(View.INVISIBLE);
		}
	}
	public class MySurListen implements SurfaceTextureListener{
		private int Id;
	
		public MySurListen(int Id) {
			this.Id=Id;
	}
	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int arg1,
			int arg2) {
			if (mCManager==null) {
				return;
			}
			boolean isPreView=mCManager.doStartPreView(Id, surface,false);
			if (!isPreView) {
				return;
			}
			addHandlerUI();
			TheApp.mApp.mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					
					if (mTextView!=null) {
						mTextView.setAlpha(1);
					}
				}
			}, 1000);
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
		if (arg0!=null) {
			arg0.release();
		}
		return false;
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
			int arg2) {
		
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
		
		}
	
	}

}
