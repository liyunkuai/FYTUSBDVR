package com.syu.dvr.control.sofia;

import com.syu.dvr.R;
import com.syu.dvr.TheApp;
import com.syu.dvr.activity.CalendarActivity;
import com.syu.dvr.control.CameraManager;
import com.syu.dvr.control.MultimediaControl;
import com.syu.dvr.control.StartActivity;
import com.syu.dvr.observ.Watched;
import com.syu.dvr.observ.WatchedUsb;
import com.syu.dvr.utils.Config;
import com.syu.dvr.utils.PublicClass;
import com.syu.dvr.utils.RecordingStatus;
import com.uvc.jni.RainUvc;

import android.app.Dialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class MultimediaControlImpl extends MultimediaControl{
	private CameraManager mCManager;
	private  RecordingStatus status;
	public MultimediaControlImpl(CameraManager manager) {
		super(manager);
		// TODO Auto-generated constructor stub
		this.mCManager=manager;
	}
	@Override
	public void shutter() {
		// TODO Auto-generated method stub
		status=RecordingStatus.getInstance();
		if (status.getIsSdWriteError()>0) {
			TheApp.mApp.MyTosat(R.string.non_writable);
			return;
		}
		if (status.isSdSpaceFull()) {
			TheApp.mApp.MyTosat(R.string.space_is_full);
			return;
		}
		if (status.getmPhotoStatus()==1) {
			TheApp.mApp.MyTosat(R.string.str_text_lock);
			return ;
		}
		if (PublicClass.getInstance().recordStatuToast(status)) {
			if (TheApp.mIsShengMaiIC) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						RainUvc.snapshot();
					}
				}).start();

				TheApp.mApp.MyTosat(R.string.str_text_photo);
			
			}else if (mCManager !=null){
				mCManager.getUvcExtenrnCall(Config.SUPPLIER_CAMERA_SUNPLUS, 
						String.valueOf(4), String.valueOf(1), String.valueOf(1));
				TheApp.mApp.MyTosat(R.string.str_text_photo);
			}
		}
	}

	@Override
	public void startRecord() {
		// TODO Auto-generated method stub
		status=RecordingStatus.getInstance();
		if (status==null || mCManager ==null) {
			return;
		}
		if (!PublicClass.getInstance().recordStatuToast(status)) {
			return;
		}
		if (status.getIsSdWriteError()>0) {
			TheApp.mApp.MyTosat(R.string.non_writable);
			return;
		}
		if (status.getmRecordingStatus()==1) {
			mCManager.stopRecord(Config.SUPPLIER_CAMERA_SUNPLUS);
		}else {
			if (status.isSdSpaceFull()) {
				TheApp.mApp.MyTosat(R.string.space_is_full);
				return;
			}
			mCManager.startRecord(TheApp.getDeviceId());
		}
	}

	@Override
	public void lock() {
		// TODO Auto-generated method stub
		status=RecordingStatus.getInstance();
		if (status==null || mCManager ==null) {
			return;
		}
		if (status.getmRecordingStatus()!=1) {
			TheApp.mApp.MyTosat(R.string.str_dvr_lock);
			return;
		}
		if (status.getmFileLock()==1) {
			TheApp.mApp.MyTosat(R.string.str_text_flock);
			return;
		}
		if (TheApp.mIsShengMaiIC) {
			RainUvc.setGSensorStatus(1);
			return;
		}
		mCManager.setUvcExtenrnCall(Config.SUPPLIER_CAMERA_SUNPLUS,
				String.valueOf(3), String.valueOf(1), String.valueOf(6));
	}

	@Override
	public void dvrSetting() {
		// TODO Auto-generated method stub
		status=RecordingStatus.getInstance();
		if (status.getmRecordingStatus()==1) {
			TheApp.mApp.MyTosat(R.string.str_needs_stop_record);
			return;
		}
		WatchedUsb.getInstance().notifyWatchers(7);
	}

	@Override
	public void playblack() {
		status=RecordingStatus.getInstance();
		if (!PublicClass.getInstance().recordStatuToast(status)) {
			return;
		}
		if (status.getmRecordingStatus()==1) {
			TheApp.mApp.MyTosat(R.string.str_needs_stop_record);
			return;
		}
		if (TheApp.mIsShengMaiIC) {
			
			if (mCManager==null) {
				return ;
			}
			mCManager.stopRecord(Config.SUPPLIER_CAMERA_SUNPLUS);
			mCManager.doStopPreview();
			if (RainUvc.setMode(2)>=0) {
				StartActivity.startAcitivity(CalendarActivity.class);
			}else {
				TheApp.mApp.MyTosat(R.string.mode_switch_failure);
				mCManager.startPreview(Config.SUPPLIER_CAMERA_SUNPLUS);
			}
		}else {
			SettingDialog();
		}
	}

	@Override
	public void dvrHelp() {
		// TODO Auto-generated method stub
		PublicClass.getInstance().NoCameraWarning(6).show();
	}
	public  Dialog SettingDialog(){
		final Dialog mDialog;
		status=RecordingStatus.getInstance();
		mDialog=new Dialog(TheApp.mApp,R.style.add_dialog);
    	int []size;
    	size=PublicClass.getInstance().getWindowManeger();
    	if (size==null||size.length!=2) {
    		size=new int[]{1024,600};
		}
		ViewGroup.LayoutParams params= new ViewGroup.LayoutParams(size[0]/2, size[1]/2);
    	View view=LinearLayout.inflate(TheApp.mApp, R.layout.sele_filemanager, null);
    	mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    	mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	mDialog.setContentView(view,params);
		initHandlerDialog(mDialog);
		mDialog.show();
		return mDialog;
	}
	private void initHandlerDialog(final Dialog mDialog) {
		mDialog.findViewById(R.id.file_mode).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (status.getmRecordingStatus()==1) {
					TheApp.mApp.MyTosat(R.string.str_needs_stop_record);
					return;
				}
				if (mCManager!=null) {
					
					if (StartActivity.startAcitivity("com.syu.filemanager", "com.syu.fromDvr")) {
						mCManager.mswitchDivce();
					}
				}
				mDialog.dismiss();
			}
		});
		mDialog.findViewById(R.id.calendar_mode).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (status.getmRecordingStatus()==1) {
					TheApp.mApp.MyTosat(R.string.str_needs_stop_record);
					return;
				}
				StartActivity.startAcitivity(CalendarActivity.class);
				mCManager.mswitchDivce();
				mDialog.dismiss();
			}
		});
	}
}
