package com.syu.dvr.factory;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.syu.dvr.R;
import com.syu.dvr.TheApp;
import com.syu.dvr.activity.MainActivity;
import com.syu.dvr.control.CameraManager;
import com.syu.dvr.control.FytDvrTypeControl;
import com.syu.dvr.utils.LogCatUtils;
import com.syu.dvr.utils.PublicClass;
import com.syu.dvr.utils.RecordingStatus;
import com.syu.dvr.widget.StateSignLayout;
import com.uvc.jni.RainUvc;

public class App4GGetRecordStatus extends RunTimeFactory{
	
	private static App4GGetRecordStatus getCameraSDcard;
	private  MyTimerTask task;
	private  Timer timer;
	private CameraManager mCManager;
	private StateSignLayout mLayout;
	public static App4GGetRecordStatus getInstance(){
		if (getCameraSDcard==null) {
			synchronized (App4GGetRecordStatus.class) {
				if (getCameraSDcard==null) {
					getCameraSDcard=new App4GGetRecordStatus();
				}
			}
		}
		return getCameraSDcard;
	}
	@Override
	public void RunTask() {
		timer=new Timer(App4GGetRecordStatus.class.toString());
		if (task!=null) {
			task.cancel();
			task=null;
		}
		task=new MyTimerTask();
		timer.schedule(task, 1000, 1000);
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
	int mAudioLast=-1;
	int mResolutionLast=-1;
	int mTFStatus;
	int value;
	int mGSensorStatus;
	int audio;
	int mResolution;
	int mSdcard;
	int mError;
	int misHasMic;
	
	
	public void setRecordStatus() {
		mToLauncher(RecordStatus);
	}
	private void mToLauncher(int i){
		Intent intent=new Intent();
		intent.setClassName("com.fyt.widget","com.fyt.widget.DvrService");
		intent.setAction("com.fyt.launcher.dvr");
		intent.putExtra("DVR", i);
		TheApp.mApp.startService(intent);
	}
	private RecordingStatus status;
	class MyTimerTask extends TimerTask{
		@Override
		public void run() {
			mLayout=FytDvrTypeControl.getInstance().getmStateLayout();
			mCManager=FytDvrTypeControl.getInstance().getmCManager();
			if (mCManager==null) {
				return;
			}
			status=RecordingStatus.getInstance();
			value=RainUvc.getVideoRecordStatus();
	    	if (value>=0){
				status.setmRecordingStatus(value);
				if (value!=RecordStatus) {
					LogCatUtils.showString("===RecordStatus=="+RecordStatus);
					TheApp.mApp.sendBroadCast(value);
					RecordStatus=value;
					if (mLayout!=null) {
						mLayout.setRecordStatus(value==1);
					}
					mToLauncher(value);
				}
	    	}
			mTFStatus=RainUvc.getTFCardStatus();
			if (mTFStatus>=0) {
				status.setmScardStatus(mTFStatus>1?2:Math.abs(mTFStatus-1));
				if (sdcardLast!=mTFStatus) {
					if (sdcardLast!=-1) {
						PublicClass.getInstance().showStatuToast((mTFStatus==1)?0:1);
					}
					sdcardLast=mTFStatus;
					if (mLayout !=null) {
						mLayout.setScardStatus(mTFStatus==1);
					}
				}
			}
			mGSensorStatus=RainUvc.getGSensorStatus();
			if (mGSensorStatus>=0) {
				status.setmFileStatus(mGSensorStatus);
				if (mFileStatus!=mGSensorStatus) {
					mFileStatus=mGSensorStatus;
					if (mGSensorStatus==1) {
						PublicClass.getInstance().showStatuToast(4);
					}
					if (mLayout !=null) {
						mLayout.setRecordLock(mGSensorStatus==1);
					}
				}
			}
			audio=RainUvc.getAudioRecordStatus();
			if (audio>=0) {
				status.setMisRecordAudio(audio==1?true:false);
				if (audio!=mAudioLast) {
					mAudioLast=audio;
					if (mLayout !=null) {
						mLayout.setAudioRecord(audio==1);
					}
				}
			}
			mResolution=RainUvc.getResolution();
			if (mResolution>=0) {	
				status.setmResolution(mResolution);
				if (mResolution!=mResolutionLast) {
					mResolutionLast=mResolution;
					if (mLayout !=null) {
						mLayout.setResolution(mResolution);
					}
				}
			}
			
			mSdcard=RainUvc.isSdSpaceFull();
			status.setSdSpaceFull(mSdcard>0?true:false);
			mError=RainUvc.isSdWriteError();
			status.setIsSdWriteError(mError);
			misHasMic=RainUvc.doHasMic();
			status.setHasMic(misHasMic>0?true:false);
		}
	}
}
