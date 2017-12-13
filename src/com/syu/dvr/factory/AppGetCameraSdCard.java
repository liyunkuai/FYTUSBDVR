package com.syu.dvr.factory;

import java.util.Timer;
import java.util.TimerTask;

import com.syu.dvr.TheApp;
import com.syu.dvr.control.CameraManager;
import com.syu.dvr.control.FytDvrTypeControl;
import com.syu.dvr.utils.LogCatUtils;
import com.syu.dvr.utils.PublicClass;
import com.syu.dvr.utils.RecordingStatus;
import com.syu.dvr.widget.StateSignLayout;

import android.content.Intent;

public class AppGetCameraSdCard extends RunTimeFactory{
	
	private static AppGetCameraSdCard getCameraSDcard;
	private  MyTimerTask task;
	private  Timer timer;
	private CameraManager mCManager;
	private StateSignLayout mLayout;
	public static AppGetCameraSdCard getInstance(){
		if (getCameraSDcard==null) {
			synchronized (AppGetCameraSdCard.class) {
				if (getCameraSDcard==null) {
					getCameraSDcard=new AppGetCameraSdCard();
				}
			}
		}
		return getCameraSDcard;
	}
	
	@Override
	public void RunTask() {
		timer=new Timer(AppGetCameraSdCard.class.toString());
		if (task!=null) {
			task.cancel();
			task=null;
		}
		task=new MyTimerTask();
		timer.schedule(task, 1, 1000);
		
	}

	@Override
	public void stopTimer() {
		if (task!=null) {
			task.cancel();
			task=null;
		}
		mToLauncher(0);
		sdcardLast=-1;
		RecordStatus=-1;
		mFileStatus=-1;
	}
	
	int sdcardLast=-1;
	int RecordStatus=-1;
	int mFileStatus=-1;
	
	private void mToLauncher(int i){
		Intent intent=new Intent();
		intent.setClassName("com.fyt.widget","com.fyt.widget.DvrService");
		intent.setAction("com.fyt.launcher.dvr");
		intent.putExtra("DVR", i);
		TheApp.mApp.startService(intent);
	}
	class MyTimerTask extends TimerTask{
		@Override
		public void run() {
			mLayout=FytDvrTypeControl.getInstance().getmStateLayout();
			mCManager=FytDvrTypeControl.getInstance().getmCManager();
			
			if (mCManager==null) {
				return;
			}
			int[] value=mCManager.getUvcExtenrnCall(TheApp.getDeviceId(),String.valueOf(7), 
					String.valueOf(83),"");
	    	if (value==null||value.length<16){
				return ;
			}
	    	for (int i = 0; i < value.length; i++) {
	    		if (i==0) {
					RecordingStatus.getInstance().setmRecordingStatus(value[i]);
					if (value[i]!=RecordStatus) {
						RecordStatus=value[i];
						TheApp.mApp.sendBroadCast(value[i]);
						mToLauncher(value[i]);
						if (mLayout !=null) {
							mLayout.setRecordStatus(value[i]==1);
						}
					}
				}
	    		if (i==1) {
	    			RecordingStatus.getInstance().setmFileStatus(value[i]);
					if (mFileStatus!=value[i]) {
						LogCatUtils.showString(" value[i] = "+value[i]);
						mFileStatus=value[i];
						if (mFileStatus==1) {
							PublicClass.getInstance().showStatuToast(4);
						}
						if (mLayout !=null) {
							
							mLayout.setRecordLock(mFileStatus==1);
						}
					}
				}
	    		
	    		if (i==2) {
	    			RecordingStatus.getInstance().setmPhotoStatus(value[i]);
				}
	    		if (i==3) {
	    			RecordingStatus.getInstance().setmScardStatus(value[i]);
					if (sdcardLast!=value[i]) {
						PublicClass.getInstance().showStatuToast(value[i]);
						sdcardLast=value[i];
						if (mLayout !=null) {
							mLayout.setScardStatus(sdcardLast==0);
						}
					}
				}
	    		if (i==4) {
	    			RecordingStatus.getInstance().setmFileLock(value[i]);
				}
			}
		}
	}
}
