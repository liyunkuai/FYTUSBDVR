package com.syu.dvr.control;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.syu.codec.PreviewView;
import com.syu.dvr.control.CameraManager.CameraManagerCallback;
import com.syu.dvr.utils.Config;
import com.syu.dvr.widget.ComInterface.CapturePhotos;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;

public class CameraManager {
	public CamcorderProfile[] mProfile;
	public boolean[] isOpenCamare;
	public boolean []isRecord;
	public Camera[] mCamera;
	public Parameters[] mParameters;
	public MediaRecorder[] mMediaRecorder;
	public Bitmap bitmap=null;
	public boolean []isPreview;
	public File[] mRecordFile;	
	public boolean mCameraIsLock[];
	public boolean [] initSuccess;
	public boolean [] isReadyRecoder;
	public List<Integer> mFrontSupportedPreviewFrameRates;
	public int mPreviewFpsPercent = 100;
	public FileManager mFileManager;
	public int flag=0;
	public boolean mReRequest=false;
	public int [] value=null;
	public int mOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;
	public int []setDefuleValue;
	public List<CameraManagerCallback> callbacks=new ArrayList<CameraManagerCallback>();
	public PreviewView mPreviewView;
	private CameraManagerCallback callback;
	public CameraManager() {
		mRecordFile=new File[Config.MAX_SUPPORT_CAMERAS];
        isOpenCamare=new boolean[Config.MAX_SUPPORT_CAMERAS];
        isRecord=new boolean[Config.MAX_SUPPORT_CAMERAS];
        isPreview=new boolean[Config.MAX_SUPPORT_CAMERAS];
	}
	public void mswitchDivce(){}
	
	public void removePreviewView(int mCameraID) {
	}
	public void setmPreviewView(PreviewView mPreviewView,int mCameraID) {
	}
	public boolean doStartPreView(final int cameraId,SurfaceTexture surfaces, boolean isPlayback){
		return false;
	}
	public synchronized boolean doOpenCamera(final int mCameraId){
		return false;
	}
	public void startRecord(int mCameraId){
	}
	public void stopRecord(int mCameraId){
	}
	public void setCameraSetting(int mCameraID,int[]value){
	}
	public synchronized void recordLock(){
	}
	public void systmeUpdata(){}
	public void factorySettings(){}
	public int [] setUvcExtenrnCall(int mCameraId,String parameter,String parameter2,String parameter3){
		return null;
	}
	public synchronized int [] getUvcExtenrnCall(int mCameraId,String parameter,String parameter2,String parameter3){
		return null;
	}
	public void MyCarCollitionPhoto(int cameraId,final CapturePhotos callball){
	}
	public void startPreview(final int cameraID){
	}
	public boolean startPreview(int cameraId,SurfaceHolder holder){
		return false;
	}
	public void doStopPreview(int cameraId){
	}
	public void doStopPreview(){
	}
	public void doCloseCamera(int cameraId){
	}
	public void doCloseCamera(){
	}
	public void upDataTime(int cameraID,int i,int[] mTimes) {
	}
	public boolean mCameraIsUse(int mCameraID){
		return false;
	}
	public void callback(int value){
		if (callback!=null) {
			callback.cameraCallback(value);
		}
	}
	public void registerCallback(CameraManagerCallback callback){
		this.callback=callback;
	}
	public void unregisterCallback(CameraManagerCallback callback){
		this.callback=null;
	}
	public interface CameraManagerCallback{
		public void cameraCallback(int value);
	}
	public  int getValueOf(String value){
		if (value.isEmpty()) {
			return -1;
		}
		return Integer.valueOf(value, 16);
	}
}
