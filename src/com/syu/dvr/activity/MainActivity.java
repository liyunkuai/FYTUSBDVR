package com.syu.dvr.activity;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import com.syu.dvr.R;
import com.syu.dvr.TheApp;
import com.syu.dvr.control.CameraManager;
import com.syu.dvr.control.CameraManager.CameraManagerCallback;
import com.syu.dvr.control.CameraPreview;
import com.syu.dvr.control.FytDvrTypeControl;
import com.syu.dvr.factory.ActiviytIsOpen;
import com.syu.dvr.observ.WatchedUsb;
import com.syu.dvr.observ.WatchedUsb.Watcher;
import com.syu.dvr.uihandle.MainActivityUiHandle;
import com.syu.dvr.upgrade.UpgradeManager;
import com.syu.dvr.utils.HandleConstant;
import com.syu.dvr.utils.LogCatUtils;
import com.syu.dvr.utils.ProgressDialog;
import com.syu.dvr.utils.PublicClass;
import com.syu.dvr.widget.NoCameraCarousel;
import com.syu.dvr.widget.StateSignLayout;
import com.syu.jni.SyuJniNative;
import com.uvc.jni.RainUvc;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements Watcher{
	public static MainActivity activity=null;
	private FrameLayout mLinearLayout;
	private TextView mWaning;
	private LinearLayout.LayoutParams mLayoutParams;
	private RelativeLayout.LayoutParams rParams;
	private LinearLayout mFromLayout;
	private RelativeLayout relativeLayout;
	public static boolean isModeSwitch=false; 
	public boolean isonStop=false;
	private View mRecodLayout;
	private MyHandler handler;
	private Dialog mWarningDialog;
	private CameraPreview mCameraPreview;
	private CameraManager mCManager;
	private MainActivityUiHandle uiHandle;
	private CameraManagerCallbackImpl callbackImpl;
	private MyTimerTask task;
	private Timer timer;
	private int mCount=0;
	private StateSignLayout mSignLayout;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(initView());
		activity=MainActivity.this;
		initData();
	}
	private void initData() {
		
		uiHandle=new MainActivityUiHandle();
		mCameraPreview=FytDvrTypeControl.getInstance().getmCameraPreview();
		mCManager = FytDvrTypeControl.getInstance().getmCManager();
		handler=new MyHandler(this);
		callbackImpl=new CameraManagerCallbackImpl();
		if (TheApp.mApp.clientForeign()) {
			mWarningDialog=PublicClass.getInstance().NoCameraWarning(2);
		}else {
			mWarningDialog=new NoCameraCarousel(activity, R.style.usb_camera_no_find);
		}
	}
	private View initView() {
		LayoutInflater fInflater=LayoutInflater.from(TheApp.mApp);
		mRecodLayout=fInflater.inflate(R.layout.layout_main, null);
		mSignLayout=(StateSignLayout) mRecodLayout.findViewById(R.id.state_sign_layout);
		mWaning=(TextView)mRecodLayout.findViewById(R.id.waning);
		mLinearLayout = new FrameLayout(this);
		mLinearLayout.setBackgroundColor(getResources().getColor(R.color.black));
		relativeLayout=new RelativeLayout(this);
		rParams=new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT,
				android.widget.RelativeLayout.LayoutParams.MATCH_PARENT);
		mLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mLayoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mFromLayout=new LinearLayout(this);
		mFromLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		relativeLayout.addView(mFromLayout);
		mLinearLayout.addView(relativeLayout, rParams);
		if (PublicClass.getInstance().isVerticalScreen()) {
			mLayoutParams.bottomMargin=122;
			mLayoutParams.topMargin=72;
		}
		return mLinearLayout;
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		isModeSwitch=false;
		if (!TheApp.mApp.clientForeign()) {
			UpgradeManager.getInstance().BackStageNetWordCheck();
		}
		if (TheApp.mApp.windowManageView!=null) {
			LogCatUtils.showString("==removeView=====");
			TheApp.mApp.windowManageView.removeView();
		}
		if (mCManager!=null) {
			mCManager.registerCallback(callbackImpl);
		}
		ProgressDialog.getInstance().progressShow(R.string.camera_loading);
		ProgressDialog.getInstance().enableCanceledOnTouchOutside(true);
		previewOverTimeRunTask();
		Bundle outparam=new Bundle();
		SyuJniNative.getInstance().syu_jni_command(12, null, outparam);
		if (outparam!=null) {
			if (outparam.getInt("param0",-1)==1) {
				Dialog dialog=PublicClass.getInstance().NoCameraWarning(1);
				dialog.show();
				return;
			}
		}
		isonStop=false;
		handler.sendEmptyMessage(0);
		handler.sendEmptyMessageDelayed(3, 500);
		LogCatUtils.showString("   onStart  " );
	}
	private void addHandlerUI(){
		previewOverTimestopTimer();
		uiHandle.registerUiLayout(mRecodLayout);
		handler.postDelayed(new Runnable() {
			public void run() {
				if (mRecodLayout!=null&&mRecodLayout.getParent()==null&&mLinearLayout!=null) {
					mLinearLayout.addView(mRecodLayout);
				}
				if (mWarningDialog!=null) {
					mWarningDialog.cancel();
				}
				ProgressDialog.getInstance().progressdismiss();
			}
		}, 1*1000);
	}
	private static  class MyHandler extends Handler{
		
		private WeakReference<MainActivity>mReference;
		
		public MyHandler(MainActivity activity){
			mReference=new WeakReference<MainActivity>(activity);
		}
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			MainActivity activity=mReference.get();
			if (activity==null) {
				return;
			}
			switch (msg.what) {
			case 0:
				activity.mSwitchCamera();
				break;
			case 1:
				break;
			case 2:
				activity.waningCancel();
				break;
			case 3:
				if (activity!=null) {
					WatchedUsb.getInstance().addWatched(activity,true);
				}
				break;
			case 4:
				activity.finish();
				break;
			case 5:
				PublicClass.getInstance().NoCameraWarning(5).show();
				break;
			default:
				break;
			}
			
		}
	}
	private void waningCancel(){
		if (mWarningDialog!=null&&!mWaning.isShown()) {
			mWarningDialog.cancel();
			mWarningDialog.show();
			ProgressDialog.getInstance().progressdismiss();
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		handler.sendEmptyMessageDelayed(4, 600);
		return true;
	}
	int retValue=0;
	private void mSwitchCamera(){
		if (TheApp.mIsShengMaiIC) {
			retValue=RainUvc.setMode(1);
		}else {
			SyuJniNative.getInstance().syu_jni_command(152, null, null);
		}
	}
	@Override
	protected void onStop() {
		super.onStop();
		LogCatUtils.showString(" onStop()  ");
		ProgressDialog.getInstance().progressdismiss();
		if (PublicClass.getInstance().getmFormatsdDialog()!=null) {
			PublicClass.getInstance().getmFormatsdDialog().dismiss();
		}
		WatchedUsb.getInstance().removeWatched(activity);
		previewOverTimestopTimer();
		if (mWaning!=null) {
			mWaning.setVisibility(View.GONE);
		}
		isonStop=true;
		TheApp.mApp.sendBroadCast(1);
		outCamera();
		if (mWarningDialog!=null) {
			mWarningDialog.cancel();
		}
		if (mCManager!=null) {
			mCManager.unregisterCallback(callbackImpl);
		}
		ActiviytIsOpen.getInstance().setOpen(false);
	}
	@Override
	protected void onDestroy() {
		LogCatUtils.showString(" onDestroy()  ");
		activity=null;
		super.onDestroy();
	}
	@Override
	public void updata(int updata) {
		LogCatUtils.showString("  updata  "+updata);
		if (updata==0) {
			outCamera();
			TheApp.mApp.setmI2Cerror(false);
			TheApp.mApp.setCheckfailure(false);
			if (!isonStop&&!isModeSwitch) {
				TheApp.mApp.MyTosat(R.string.str_camera_exit);
			}
			if (mWaning!=null) {
				mWaning.setVisibility(View.GONE);
			}
		}else if (updata==1) {
			initData(mCameraPreview.initData());
		}else if (updata==2) {
			if (TheApp.mApp.ismI2Cerror()) {
				mWaning.setVisibility(View.VISIBLE);
				mWaning.setText(R.string.i2c_error);
			}else if (TheApp.mApp.isCheckfailure()) {
				mWaning.setVisibility(View.VISIBLE);
				mWaning.setText(R.string.check_failure);
			}
		}else if (updata==4) {
			mWaning.setVisibility(View.VISIBLE);
			mWaning.setText(R.string.shake_failed);
		}else if (updata==5) {
			addHandlerUI();
		}else if (updata==6) {
			uiHandle.handler.sendEmptyMessage(HandleConstant.SHOW_LAYOUT);
		}else if (updata==7) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivityForResult(intent, 100);
		}
	}
	
	private void initData(View child) {
		if (mFromLayout!=null) {
			mFromLayout.removeAllViews();
		}
		mFromLayout.addView(child,mLayoutParams);
	}
	public synchronized void outCamera(){
		if (mSignLayout!=null) {
			mSignLayout.settingSignout();
		}
		mCameraPreview.onDestroyTextureView();
		if ((mRecodLayout!=null)&&(mRecodLayout.getParent()!=null)) {
			mLinearLayout.removeView(mRecodLayout);
		}
		mFromLayout.removeAllViews();
		FytDvrTypeControl.getInstance().setmStateLayout(null);
		LogCatUtils.showString("  outCamera() ");
	}
	public interface StartPreViewCallBack{
		void PreviewCallback();
	}
	
	private void previewOverTimeRunTask(){
		timer=new Timer("previewOverTimeRunTask");
		if (task!=null) {
			task.cancel();
		}
		task=new MyTimerTask();
		timer.schedule(task, 10, 1000);
	}
	private void previewOverTimestopTimer() {
		if (task!=null) {
			task.cancel();
			task=null;
		}
		if (timer !=null) {
			timer.cancel();
		}
		mCount=0;
	}
	class MyTimerTask extends TimerTask{

		@Override
		public void run() {
			mCount++;
			if (mCount==20) {
				handler.sendEmptyMessage(2);
				previewOverTimestopTimer();
			}
		}
	}
	class CameraManagerCallbackImpl implements CameraManagerCallback{
		@Override
		public void cameraCallback(int value) {
			// TODO Auto-generated method stub
			LogCatUtils.showString(" value "+ String.valueOf(value));
			Message message = Message.obtain();
			message.what=5;
			message.arg1=value;
			handler.sendMessage(message);
		}
	}
}
