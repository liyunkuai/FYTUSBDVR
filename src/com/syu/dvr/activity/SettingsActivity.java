package com.syu.dvr.activity;

import com.syu.dvr.R;
import com.syu.dvr.TheApp;
import com.syu.dvr.control.CameraManager;
import com.syu.dvr.control.FytDvrTypeControl;
import com.syu.dvr.utils.LogCatUtils;
import com.syu.dvr.utils.ProgressDialog;
import com.syu.dvr.utils.RecordingStatus;
import com.syu.dvr.widget.SettingButton;
import com.syu.dvr.widget.SettingLayout;
import com.syu.dvr.widget.SettingSwitch;
import com.syu.dvr.widget.StateSignLayout;
import com.syu.dvr.widget.TimeSetting;
import com.uvc.jni.RainUvc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

public class SettingsActivity extends BaseActivity{
	private TimeSetting mTimeSetting;
	private SettingLayout mResolutionSeting;
	private SettingLayout mFrequenceSeting;
	private SettingLayout mCollisionSetting;
	private SettingSwitch mAudioSwitch;
	private SettingSwitch mHdrSwitch;
	private SettingButton mSysUpgrade;
	private SettingButton mVersion;
	private SettingButton mAboutSoftware;
	private String versionVvalue="";
	private int mRecLength=0;
	private int mFrequence=0;
	private int mResolution=0;
	private int mCollision=0;
	private int temp;
	private int [] mRecLengths=new int[]{1,3,5};
	private int [] setDefuleValue;
	private int []setValue=new int [16];
	private boolean isHandleFish=false;
	private boolean isAudioRec;
	private boolean isHDRenable;
	private IntentFilter mFilter;
	private CameraManager mCManager;
	private StateSignLayout mLayout;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.layout_setting);
		mCManager=FytDvrTypeControl.getInstance().getmCManager();
		mFilter = new IntentFilter();
		mFilter.addAction("com.syu.dvr.settingactivity.finish");
		mFilter.addAction("com.syu.dvr.settingclick");
		mFilter.addAction("com.syu.dvr.factory_settings");
		registerReceiver(mUsbCameraHotPlug, mFilter);
		initSettingView();
	}
	private void initSettingView() {
		mTimeSetting=(TimeSetting)findViewById(R.id.time_setting_layout);
		mResolutionSeting=(SettingLayout)findViewById(R.id.resolution_setting_layout);
		mFrequenceSeting=(SettingLayout)findViewById(R.id.strobe_setting_layout);
		mCollisionSetting=(SettingLayout)findViewById(R.id.collision_setting_layout);
		mAudioSwitch=(SettingSwitch)findViewById(R.id.audo_setting_layout);
		mHdrSwitch=(SettingSwitch)findViewById(R.id.hdr_setting_layout);
		mAboutSoftware=(SettingButton)findViewById(R.id.about_software_layout);
		mSysUpgrade=(SettingButton)findViewById(R.id.system_upgrade_layout);
		mVersion=(SettingButton)findViewById(R.id.version_information_layout);
		initSettingData();
		if (TheApp.mApp.clientForeign()) {
			mSysUpgrade.setVisibility(View.GONE);
		}
	}
	private void initSettingData() {
		
		LogCatUtils.showString("=========");
		
		if (TheApp.mIsShengMaiIC) {
			temp=RainUvc.getRecLength();
			for (int i = 0; i < mRecLengths.length; i++) {
				if (mRecLengths[i]==temp) {
					mRecLength=i;
					break;
				}
			}
			mFrequence=RainUvc.getFrequence();
			mResolution=RainUvc.getResolution();
			isAudioRec=(RainUvc.getAudioRecordStatus()==1);
			versionVvalue=RainUvc.getPlatformVersion();
			mCollisionSetting.setVisibility(View.GONE);
			mHdrSwitch.setVisibility(View.GONE);
			mAudioSwitch.setVisibility(View.GONE);
			if (!TextUtils.isEmpty(versionVvalue)&&versionVvalue.equals("1080P")) {
				mResolutionSeting.setVisibility(View.VISIBLE);
			}else {
				mResolutionSeting.setVisibility(View.GONE);
			}
		}else if (mCManager!=null){
			setDefuleValue=mCManager.getUvcExtenrnCall(TheApp.getDeviceId(),
	    			String.valueOf(1), String.valueOf(81),"");
	    	if (setDefuleValue==null) {
				return;
			}
	    	setValue[0]=1;
	    	setValue[1]=1;
	    	LogCatUtils.showString("==setDefuleValue.length=="+setDefuleValue.length);
	    	if (setDefuleValue.length!=16) {
				return;
			}
	    	for (int i = 2; i < 16; i++) {
	    		setValue[i]=setDefuleValue[i];
	    		LogCatUtils.showString("==setDefuleValue["+i+"]=="+setDefuleValue[i]);
			}
	    	TheApp.mApp.saveInt("RECORDING_TIME", setDefuleValue[2]);
	    	for (int i = 0; i < mRecLengths.length; i++) {
				if (mRecLengths[i]==setDefuleValue[2]) {
					mRecLength=i;
					break;
				}
			}
			int [] version=getversion();
			int versionid=-1;
			if (version!=null&&version.length>11) {
				versionid=version[10];
			}
			if (versionid==4||versionid==1) {
				RecordingStatus.getInstance().setHasMic(true);
				mAudioSwitch.setVisibility(View.VISIBLE);
			}else {
				RecordingStatus.getInstance().setHasMic(false);
				mAudioSwitch.setVisibility(View.GONE);
			}
			if (0<=setDefuleValue[4]&&setDefuleValue[4]<=3) {
				mCollisionSetting.setVisibility(View.VISIBLE);
				mCollision=setDefuleValue[4];
			}else {
				mCollisionSetting.setVisibility(View.GONE);
			}
			LogCatUtils.showString("===setDefuleValue[5]==0==="+setDefuleValue[5]);
			if ((setDefuleValue[5]==0)&&RecordingStatus.getInstance().isHasMic()) {
				isAudioRec=true;
			}else {
				isAudioRec=false;
			}
			if (version[7]==3&&version[8]==2&&version[9]==3) {
				mResolution=0;
			}else {
				mResolution=1;
			}
			LogCatUtils.showString("===isAudioRec==="+String.valueOf(isAudioRec));
			
			if (setDefuleValue[11]==0) {
				isHDRenable=false;
			}else if (setDefuleValue[11]==1) {
				isHDRenable=true;
			}
			mFrequenceSeting.setVisibility(View.GONE);
			mResolutionSeting.setVisibility(View.GONE);
		}
		if (TheApp.mApp.clientForeign()) {
			mAboutSoftware.setVisibility(View.GONE);
		}
		
		if (mTimeSetting.getVisibility()==View.VISIBLE) {
			mTimeSetting.updataSummary(mRecLength,setValue);
		}
		if (mResolutionSeting.getVisibility()==View.VISIBLE) {
			mResolutionSeting.updataSummary(mResolution,setValue);
		}
		if (mFrequenceSeting.getVisibility()==View.VISIBLE) {
			mFrequenceSeting.updataSummary(mFrequence,setValue);
		}
		if (mCollisionSetting.getVisibility()==View.VISIBLE) {
			mCollisionSetting.updataSummary(mCollision,setValue);
			
		}
		if (mAudioSwitch.getVisibility()==View.VISIBLE) {
			mAudioSwitch.updataLayout(isAudioRec,setValue);
			mLayout=FytDvrTypeControl.getInstance().getmStateLayout();
			if (mLayout !=null) {
				mLayout.setAudioRecord(isAudioRec);
			}
		}
		mLayout=FytDvrTypeControl.getInstance().getmStateLayout();
		if (mLayout !=null) {
			mLayout.setResolution(mCollision);
		}
		if (mHdrSwitch.getVisibility()==View.VISIBLE) {
			
			mHdrSwitch.updataLayout(isHDRenable,setValue);
		}
		RecordingStatus.getInstance().setMisRecordAudio(isAudioRec);
		RecordingStatus.getInstance().setmResolution(mResolution);
		mVersion.setVersionVvalue(versionVvalue);
	}
	private int [] getversion(){
		if (mCManager==null) {
			return null;
		}
		int [] version=mCManager.getUvcExtenrnCall(TheApp.getDeviceId(),
    			String.valueOf(9), String.valueOf(81),"");
		if (version==null||version.length<16) {
			return null;
		}
		versionVvalue="";
		for (int i = 0; i < version.length; i++) {
			versionVvalue+=String.valueOf(version[i]);
		}
		if (TextUtils.isEmpty(versionVvalue)) {
			return null;
		}
		LogCatUtils.showString("==versionVvalue=="+versionVvalue);
		return version;
	}
	private void settingClick(){
		if (isHandleFish) {
			TheApp.mApp.MyTosat(R.string.str_dvr_handler);
			return;
		}
		isHandleFish=true;
		ProgressDialog.getInstance().progressShow(R.string.setting_camera);
		if (mCManager!=null) {
			mCManager.setCameraSetting(TheApp.getDeviceId(), setValue);	
		}
		handler.sendEmptyMessageDelayed(0, 1000);
	}
	private Handler handler=new Handler(Looper.getMainLooper()){
		public void handleMessage(android.os.Message msg) {
			initSettingData();
			ProgressDialog.getInstance().progressdismiss();
			isHandleFish=false;
			if (mCManager!=null) {
				mCManager.startPreview(TheApp.getDeviceId());
			}
		};
	};
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		finish();
		return true;
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		finish();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mUsbCameraHotPlug);
	}
	private BroadcastReceiver mUsbCameraHotPlug = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	String action=intent.getAction();
            if(intent.getFlags() == 32785) {
            	finish();
            }else if (action!=null&&action.contains("com.syu.dvr.settingclick")) {
				settingClick();
			}else if (action!=null&&action.contains("com.syu.dvr.factory_settings")) {
				handler.sendEmptyMessageDelayed(0, 1000);
			}
        }
    };
}
