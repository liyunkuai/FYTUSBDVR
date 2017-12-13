package com.syu.dvr.view;

import java.util.Timer;
import java.util.TimerTask;

import com.syu.codec.Codec.InitCallback;
import com.syu.codec.PreviewView;
import com.syu.dvr.TheApp;
import com.syu.dvr.control.CameraManager;
import com.syu.dvr.control.CameraPreview;
import com.syu.dvr.control.FytDvrTypeControl;
import com.syu.dvr.observ.WatchedUsb;
import com.syu.dvr.observ.WatchedUsb.Watcher;
import com.syu.dvr.server.CollisionVideoService;
import com.syu.dvr.utils.LogCatUtils;
import com.syu.jni.SyuJniNative;
import com.uvc.jni.RainUvc;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

public class WindowManageView extends View implements Watcher{
	
	private LinearLayout remoteView=null;
	
	private WindowManager mWindowManager = null;
	private LayoutParams mLayoutParams = null;
	public  TextureView mView;
	public static boolean isLoad =false;
	public View post_recod;
	public View front_recod;
	public boolean isShow=false;
	public int addTime=0;
	public int clickTiem=0;
	private Context mContext;
	private PreviewView mSurfaceView;
	private CameraManager mCManager;
	private CameraPreview mCameraPreview;
	
	public WindowManageView(Context context) {
		super(context);
	}
	private void init(Context context) {
		mCManager=FytDvrTypeControl.getInstance().getmCManager();
		mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		mLayoutParams = new LayoutParams();
		
		mLayoutParams.format = PixelFormat.RGBA_8888;
		mLayoutParams.type =WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW+3;
		mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
		
		mLayoutParams.width=1;
		mLayoutParams.height=1;
		mLayoutParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | 
				LayoutParams.FLAG_NOT_FOCUSABLE|LayoutParams.FLAG_NOT_FOCUSABLE|
				LayoutParams.FLAG_HARDWARE_ACCELERATED;
		remoteView=new LinearLayout(context);
		mCameraPreview=FytDvrTypeControl.getInstance().getmCameraPreview();
	}
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		return false;
	}
	private CollisionVideoService service;
	private boolean mCollision;
	public void registerWatch(CollisionVideoService service, boolean mCollision){
		this.service=service;
		this.mCollision=mCollision;
		RainUvc.setMode(1);
		RunTask();
		SyuJniNative.getInstance().syu_jni_command(152, null, null);
		WatchedUsb.getInstance().addWatched(this,true);
	}
	private synchronized void addView() {
		init(TheApp.mApp);
		mCount=0;
		if ((remoteView==null)||(remoteView.getParent()!=null)) {
			return ;
		}
		mWindowManager.addView(remoteView, mLayoutParams);
		isLoad = true;
		LogCatUtils.showString("===addview==");
		return;
	}
	public synchronized void removeView() {
		WatchedUsb.getInstance().removeWatched(this);
		removeSurfaceview();
		mSurfaceView=null;
		stopTimer();
		return;
	}
	private void removeSurfaceview(){
		handle.removeMessages(1);
		if (mSurfaceView!=null&&mCManager!=null) {
			mCManager.doStopPreview(TheApp.getDeviceId());
			mSurfaceView.setVisibility(INVISIBLE);
		}
		if (remoteView!=null) {
			remoteView.removeAllViews();
			if (remoteView.getParent() != null) {
				mWindowManager.removeView(remoteView);
				LogCatUtils.showString("====removeView=====");
			}
			isLoad = false;
		}
	}
	LinearLayout.LayoutParams bParams;
	@Override
	public void updata(int updata) {
		LogCatUtils.showString("updata=="+updata);
		if (updata==1) {
			addView();
			remoteView.addView(mCameraPreview.initData(),new LinearLayout.LayoutParams(1, 1));
		}else if (updata==0) {
			mCount=0;
			removeSurfaceview();
		}else if (updata==5&&isLoad) {
			handle.sendEmptyMessageDelayed(1, 2000);
		}
	}
	private  MyTimerTask task;
	private  Timer timer;
	private int mCount=0;
	public void RunTask() {
		timer=new Timer(CollisionVideoService.class.toString());
		if (task!=null) {
			task.cancel();
			task=null;
		}
		mCount=0;
		task=new MyTimerTask();
		timer.schedule(task, 1000, 1000);
	}
	public void stopTimer() {
		if (task!=null) {
			task.cancel();
			task=null;
		}
		if (timer!=null) {
			timer.cancel();
			timer=null;
		}
		mCount=0;
		handle.removeMessages(1);
		LogCatUtils.showString("****************************\n");
	}
	class MyTimerTask extends TimerTask{
		@Override
		public void run() {
		mCount++;
		LogCatUtils.showString("===mCount==="+mCount);
		if (mCount==60) {
			if (service==null) {
				return;
			}
			service.doPictureHandle(-1, null, mCollision);
			handle.sendEmptyMessage(0);
			}
		}
	}
	private Handler handle =new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				removeView();
				break;
			case 1:
				LogCatUtils.showString("  dopicture mcount  "+mCount);
				if (service!=null&&mCount>2) {
					service.doPicture(mCollision);
					stopTimer();
				}else {
					sendEmptyMessageDelayed(1, 1000);
				}
				break;
			default:
				break;
			}
		}
	};
}
