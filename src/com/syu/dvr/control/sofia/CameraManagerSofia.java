package com.syu.dvr.control.sofia;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.syu.dvr.R;
import com.syu.dvr.TheApp;
import com.syu.dvr.activity.CameraPlayBackActivity;
import com.syu.dvr.activity.MainActivity;
import com.syu.dvr.control.CameraManager;
import com.syu.dvr.control.FileManager;
import com.syu.dvr.factory.App4GGetRecordStatus;
import com.syu.dvr.factory.AppGetCameraSdCard;
import com.syu.dvr.factory.UpSystemTimeForCamera;
import com.syu.dvr.rk.CameraHolder;
import com.syu.dvr.rk.CameraSettings;
import com.syu.dvr.utils.Config;
import com.syu.dvr.utils.LogCatUtils;
import com.syu.dvr.utils.PublicClass;
import com.syu.dvr.utils.RecordingStatus;
import com.syu.dvr.widget.ComInterface.CapturePhotos;
import com.syu.jni.SyuJniNative;
import com.uvc.jni.RainUvc;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.CameraProfile;
import android.media.MediaRecorder;
import android.os.SystemProperties;
import android.view.SurfaceHolder;

@SuppressWarnings("deprecation")
public class CameraManagerSofia extends CameraManager{

	
    public CameraManagerSofia() {
    	mParameters = new Parameters[Config.MAX_SUPPORT_CAMERAS];
 		mCamera = new Camera[Config.MAX_SUPPORT_CAMERAS];
 		mMediaRecorder=new MediaRecorder[Config.MAX_SUPPORT_CAMERAS];
 		mProfile=new CamcorderProfile[Config.MAX_SUPPORT_CAMERAS];
 		isReadyRecoder=new boolean [Config.MAX_SUPPORT_CAMERAS];
 		mCameraIsLock=new boolean[2];
 		mFileManager=new FileManager();
	}
    public synchronized boolean doOpenCamera(final int mCameraId){
		try {
			long type=RainUvc.getPVId("/dev/video5");
        	if (type==2502361858l) {
				TheApp.mIsShengMaiIC=true;
			}else {
				TheApp.mIsShengMaiIC=false;
			}
    		LogCatUtils.showString("摄像头类型==="+type);
			if (isOpenCamare[mCameraId]) {
				return true;
			}
			if (!(isOpenCamare[mCameraId]=open(mCameraId))) {
				return false;
			}
			
		} catch (Exception e) {
			LogCatUtils.showString("=====e.printStackTrace()======"+e.toString());
			e.printStackTrace();
			if (mCamera[mCameraId]!=null) {
				mCamera[mCameraId].release();
				mCamera[mCameraId]=null;
			}
			return false;
		}
		if (!checkCamera()) {
			doCloseCamera(mCameraId);
			return false;
		}
		if (TheApp.mIsShengMaiIC) {
			int mTime=RainUvc.getRecLength();
			if (mTime>0) {
				TheApp.mApp.saveInt("RECORDING_TIME",mTime);
			}
		}else {
			int [] version=getUvcExtenrnCall(Config.SUPPLIER_CAMERA_SUNPLUS,
	    			String.valueOf(9), String.valueOf(81),"");
			if (version!=null&&version.length==16) {
				if (version[10]==4||version[10]==1) {
					RecordingStatus.getInstance().setHasMic(true);
				}else {
					RecordingStatus.getInstance().setHasMic(false);
				}
				int mResolution;
				if (version[7]==3&&version[8]==2&&version[9]==3) {
					mResolution=0;
				}else {
					mResolution=1;
				}
				RecordingStatus.getInstance().setmResolution(mResolution);
			}
			setDefuleValue=getUvcExtenrnCall(Config.SUPPLIER_CAMERA_SUNPLUS,
					String.valueOf(1), String.valueOf(81),"");
			if (setDefuleValue!=null&&(setDefuleValue.length==16)) {
				TheApp.mApp.saveInt("RECORDING_TIME", setDefuleValue[2]);
				if (setDefuleValue[5]==0&&RecordingStatus.getInstance().isHasMic()) {
					RecordingStatus.getInstance().setMisRecordAudio(true);
				}else {
					RecordingStatus.getInstance().setMisRecordAudio(false);
				}
			}
		}
		SystemProperties.set("sys.fyt.dvr","true");
		return true;
	} 
	private boolean checkCamera(){
		if (TheApp.mIsShengMaiIC) {
			String versionV=RainUvc.getPlatformVersion();
			if (!versionV.isEmpty()&&!versionV.contains("FYT")) {
				callback(5);
				return false;
			}
			if (getSystemData()<201612) {
				callback(4);
				return false;
			}
		}else {
			int temp=0;
			int [] version=getUvcExtenrnCall(Config.SUPPLIER_CAMERA_SUNPLUS,
	    			String.valueOf(9), String.valueOf(81),"");
			if (version!=null) {
				for (int i = 0; i < version.length; i++) {
					temp+=version[i];
				}
				if (temp==0) {
					LogCatUtils.showString("不支持");
					callback(5);
					return false;
				}
			}
			int id=SystemProperties.getInt("ro.build.fytmanufacturer", 0);
			if (version!=null&&version.length==16&&version[version.length-2]>2) {
				if (id!=version[version.length-2]) {
					callback(3);
					return false;
				}
			}
			if (getSystemData()<201612) {
				callback(4);
				return false;
			}
		}
		
		return true;
	}
	public void mswitchDivce(){
		doStopPreview(Config.SUPPLIER_CAMERA_SUNPLUS);
		MainActivity.isModeSwitch=true;
		setUvcExtenrnCall(Config.SUPPLIER_CAMERA_SUNPLUS, "0B",
				String.valueOf(0), String.valueOf(0));
	}
	private long getSystemData(){
		String string=SystemProperties.get("ro.build.date","2016-10");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		LogCatUtils.showString("==time===="+string);
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
		LogCatUtils.showString("==time===="+dateString);
		return Long.parseLong(dateString);
	}
	int flag=0;
	@SuppressWarnings("deprecation")
	private boolean open(int cameraId){
		CameraInfo cameraInfo=new CameraInfo();
		for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
			
			Camera.getCameraInfo(i, cameraInfo);
			
			if (cameraInfo.facing==cameraId) {
				if (mCamera[cameraId]==null) {
					try {
						mCamera[cameraId]=Camera.open(cameraId);
						mCamera[cameraId].setErrorCallback(new ErrorCallback() {
							@Override
							public void onError(int error, Camera camera) {
								LogCatUtils.showString("=====error======"+error);
								if (!TheApp.mIsShengMaiIC) {
									stopRecord(Config.SUPPLIER_CAMERA_SUNPLUS);
									setUvcExtenrnCall(Config.SUPPLIER_CAMERA_SUNPLUS, "62", "a5");
								}else {
									doCloseCamera();
									if (CameraPlayBackActivity.activity!=null) {
										CameraPlayBackActivity.activity.finish();
									}else {
										RainUvc.reboot();
									}
								}
							}
						});
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			return initCamera(cameraId);
			}
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public boolean  initCamera(int cameraId){
		if (cameraId < 0 || cameraId >= Camera.getNumberOfCameras()) return false;
		int mProfileSuppor=PublicClass.getInstance().getSupportedVideoQuality(cameraId);
		if (TheApp.mIsShengMaiIC&&!TheApp.mApp.isPlayBack) {
			LogCatUtils.showString("===runTask==");
			App4GGetRecordStatus.getInstance().RunTask();
		}else if(!TheApp.mIsShengMaiIC&&!TheApp.mApp.isPlayBack){
			AppGetCameraSdCard.getInstance().RunTask();
		}
		mProfile[cameraId]=CamcorderProfile.get(cameraId,mProfileSuppor);
		mParameters[cameraId]=mCamera[cameraId].getParameters();
		List<Size> supported = mParameters[cameraId].getSupportedPreviewSizes();
        if (mParameters[cameraId] == null) return false;
	        if (cameraId ==0) {
	            // Set picture size.
	            String pictureSize = TheApp.mApp.getString(
	                    CameraSettings.KEY_BACK_PICTURE_SIZE, null);
	            if (pictureSize == null) {
	                CameraSettings.initialCameraPictureSize(TheApp.mApp, mParameters[cameraId], cameraId);
	            } else {
	                List<Size> sizes = mParameters[cameraId].getSupportedPictureSizes();
	                CameraSettings.setCameraPictureSize(
	                        pictureSize, sizes, mParameters[cameraId]);
	            }
	            int value = PublicClass.getInstance().readExposure();
	            int max = mParameters[cameraId].getMaxExposureCompensation();
	            int min = mParameters[cameraId].getMinExposureCompensation();
	            if (value >= min && value <= max) {
	                mParameters[cameraId].setExposureCompensation(value);
	            } 
				
				String coloreffect = TheApp.mApp.getString(
	                    Config.KEY_COLOR_EFFECT,
	                    TheApp.mApp.getResources().getString(R.string.pref_camera_color_effect_default));
				if (PublicClass.getInstance().isSupported(coloreffect, 
						mParameters[cameraId]
								.getSupportedColorEffects())) {
					mParameters[cameraId].setColorEffect(coloreffect);
					
				}else {
	                coloreffect = mParameters[cameraId].getColorEffect();
	                if (coloreffect == null) {
	                    coloreffect = Parameters.EFFECT_NONE;
	                }
	            }
				String whiteBalance = TheApp.mApp.getString(
	                    Config.KEY_WHITE_BALANCE,
	                    TheApp.mApp.getResources().getString(R.string.pref_camera_whitebalance_default));
	            if (PublicClass.getInstance().isSupported(whiteBalance,
	                    mParameters[cameraId].getSupportedWhiteBalance())) {
	                mParameters[cameraId].setWhiteBalance(whiteBalance);
	            } else {
	                whiteBalance = mParameters[cameraId].getWhiteBalance();
	                if (whiteBalance == null) {
	                    whiteBalance = Parameters.WHITE_BALANCE_AUTO;
	                }
	                
	            }
	        }else {
	        	mProfileSuppor=PublicClass.getInstance().getSupportedVideoQuality(cameraId);
	  			mProfile[cameraId]=CamcorderProfile.get(cameraId,mProfileSuppor);
	  			List<Integer> list = mParameters[cameraId].getSupportedPreviewFrameRates();
	  			mParameters[cameraId].setPreviewFrameRate(list.get(list.size()-1));
	  			
				}
		        int[] fpsRange = PublicClass.getInstance().getMaxPreviewFpsRange(mParameters[cameraId]);
		        if (fpsRange.length > 0) {
		            mParameters[cameraId].setPreviewFpsRange(
		                    fpsRange[Parameters.PREVIEW_FPS_MIN_INDEX],
		                    fpsRange[Parameters.PREVIEW_FPS_MAX_INDEX]);
		        } else {
		            mParameters[cameraId].setPreviewFrameRate(mProfile[cameraId].videoFrameRate);
		        }
		
		        if (cameraId == CameraHolder.instance().getFrontCameraId()) {
		            mParameters[cameraId].setPreviewFrameRate(getFrontPreviewframerate(mPreviewFpsPercent));
		            mParameters[cameraId].set("uvc_dqbuf_size", mProfile[cameraId].videoFrameWidth
		                    + "x" + mProfile[cameraId].videoFrameHeight);
		        }
		
		        List<String> supportedFocus = mParameters[cameraId].getSupportedFocusModes();
		        if (isSupported(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO, supportedFocus)) {
		            mParameters[cameraId].setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
		        }
		
		        mParameters[cameraId].set(PublicClass.getInstance().RECORDING_HINT, PublicClass.getInstance().TRUE);
		
		        String vstabSupported = mParameters[cameraId].get("video-stabilization-supported");
		        if ("true".equals(vstabSupported)) {
		            mParameters[cameraId].set("video-stabilization", "true");
		        }
		        int jpegQuality = CameraProfile.getJpegEncodingQualityParameter(cameraId,
		                CameraProfile.QUALITY_HIGH);
		        mParameters[cameraId].setJpegQuality(jpegQuality);
		
		        if(TheApp.mApp.getBoolean(CameraSettings.KEY_WATER_MARK, true)) {
		            mParameters[cameraId].set("timewater", "true");
		        } else {
		            mParameters[cameraId].set("timewater", "false");
		        }
		       int[]supporPreviewSize=PublicClass.getInstance().getSupporPreviewSize(mParameters[cameraId]);
		       LogCatUtils.showString("===w=="+supporPreviewSize[0]+"===h=="+supporPreviewSize[1]);
		       
		       List<Camera.Size> sizes = mParameters[cameraId].getSupportedPreviewSizes();
		       
//		       LogCatUtils.logsendcast("====分辨率==="+supporPreviewSize[0]+"x"+supporPreviewSize[1]);
		       if (TheApp.mIsShengMaiIC) {
		    	   mParameters[cameraId].setPreviewSize(1280,720);
			 	   mParameters[cameraId].set("uvc_dqbuf_size",1280+"x"+720);
			 	   mParameters[cameraId].setPictureSize(1280,720);
		       }else {
		    	   mParameters[cameraId].setPreviewSize(640,480);
			 	   mParameters[cameraId].set("uvc_dqbuf_size",640+"x"+480);
			 	   mParameters[cameraId].setPictureSize(640,480);
		    	   
		    	  /* mParameters[cameraId].setPreviewSize(1280,720);
			 	   mParameters[cameraId].set("uvc_dqbuf_size",1280+"x"+720);
			 	   mParameters[cameraId].setPictureSize(1280,720);*/
		       }
		 	   if (Integer.valueOf(android.os.Build.VERSION.SDK_INT)>=23) {
		 		   mParameters[cameraId].set("mirror", "true");
		 		   mParameters[cameraId].set("mirror-preview", "true");
		 	   }
		       mCamera[cameraId].setParameters(mParameters[cameraId]);
		       if (!TheApp.mApp.isPlayBack) {
		    	   UpSystemTimeForCamera.getInstance().RunTask();
		       }
		return true;
	}
	
	public void startRecord(int mCameraId){
		LogCatUtils.showString("=====startRecord()==");
		if (TheApp.mIsShengMaiIC&&RainUvc.getMode()==1) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					LogCatUtils.showString("=====RainUvc.startRecord()==");
					RainUvc.startRecord();
				}
			}).start();

		}else {
			setUvcExtenrnCall(mCameraId, String.valueOf(7), String.valueOf(1), String.valueOf(1));
		}
	}
	public void stopRecord(int mCameraId){
		if (TheApp.mIsShengMaiIC) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					RainUvc.stopRecord();
				}
			}).start();

		}else {
			TheApp.mApp.sendBroadCast(0);
			setUvcExtenrnCall(mCameraId, String.valueOf(7), String.valueOf(1), String.valueOf(0));
		}
		
	}
	private int getFrontPreviewframerate(int previewFpsPercent) {
        final int targetFramerate = 30 * previewFpsPercent / 100;
        int fps = targetFramerate;
        if (mFrontSupportedPreviewFrameRates != null
                && mFrontSupportedPreviewFrameRates.size() > 0) {
            int minDiff = Integer.MAX_VALUE;
            for (int framerate : mFrontSupportedPreviewFrameRates) {
                if (Math.abs(framerate - targetFramerate) < minDiff) {
                    fps = framerate;
                    minDiff = Math.abs(framerate - targetFramerate);
                }
            }
        }
        return fps;
    }
	private static boolean isSupported(String value, List<String> supported) {
        return supported == null ? false : supported.indexOf(value) >= 0;
    }
	@Override
	public void systmeUpdata() {
		// TODO Auto-generated method stub
		super.systmeUpdata();
		setUvcExtenrnCall(Config.SUPPLIER_CAMERA_SUNPLUS, "62", "a5");
	}
	@Override
	public void factorySettings() {
		// TODO Auto-generated method stub
		super.factorySettings();
		setUvcExtenrnCall(Config.SUPPLIER_CAMERA_SUNPLUS,
				String.valueOf(61), String.valueOf(1),String.valueOf(1));
	}
	public int [] setUvcExtenrnCall(int mCameraId,String parameter,String parameter2,String parameter3){
		if (mCamera[mCameraId]==null||(!isOpenCamare[mCameraId])) {
			return null;
		}
		mParameters[mCameraId] = mCamera[mCameraId].getParameters();
		mParameters[mCameraId].set("uvc_ioctl_call", getValueOf(parameter)
				+"_"+getValueOf(parameter2)+"_"+getValueOf(parameter3));
		mCamera[mCameraId].setParameters(mParameters[mCameraId]);
		LogCatUtils.showString("parameter=="+parameter+" ,parameter2= "+parameter2+" ,parameter3 = "+parameter3);
		return null;
	}
	
	public void setUvcExtenrnCall(int mCameraId,String parameter,String parameter2){
		LogCatUtils.showString("mCamera["+mCameraId+"]=="+String.valueOf(mCamera[mCameraId])
		+", !isOpenCamare["+mCameraId+"]=="+String.valueOf(!isOpenCamare[mCameraId]));
		if (mCamera[mCameraId]==null||(!isOpenCamare[mCameraId])) {
			return;
		}
		mParameters[mCameraId] = mCamera[mCameraId].getParameters();
		mParameters[mCameraId].set("uvc_ioctl_call", getValueOf(parameter)
				+"_"+getValueOf(parameter2));
		mCamera[mCameraId].setParameters(mParameters[mCameraId]);
		LogCatUtils.showString("=====parameter==parameter2=="+parameter+"  "+parameter2+"==");
	}
	public void setUvcExtenrnCall(int mCameraId,String parameter){
		LogCatUtils.showString("mCamera["+mCameraId+"]=="+String.valueOf(mCamera[mCameraId])
		+", !isOpenCamare["+mCameraId+"]=="+String.valueOf(!isOpenCamare[mCameraId]));
		if (mCamera[mCameraId]==null||(!isOpenCamare[mCameraId])) {
			return;
		}
		mParameters[mCameraId] = mCamera[mCameraId].getParameters();
		mParameters[mCameraId].set("uvc_ioctl_call", getValueOf(parameter));	
		mCamera[mCameraId].setParameters(mParameters[mCameraId]);
		LogCatUtils.showString("=====parameter=="+parameter);
	}
	
	public void setCameraSetting(int mCameraID,int[]value){
		LogCatUtils.showString("mCamera["+mCameraID+"]=="+String.valueOf(mCamera[mCameraID])
		+", !isOpenCamare["+mCameraID+"]=="+String.valueOf(!isOpenCamare[mCameraID]));
		if (value==null||value.length<16) {
			return;
		}
		if (mCamera[mCameraID]==null||(!isOpenCamare[mCameraID])) {
			return;
		}
		String setvalue="";
		for (int i = 0; i < value.length; i++) {
			if (i==value.length-1) {
				setvalue+=value[i];
			}else {
				setvalue+=value[i]+"_"; 
			}
		}
		if (setvalue.isEmpty()) {
			return;
		}
		mParameters[mCameraID] = mCamera[mCameraID].getParameters();
		mParameters[mCameraID].set("uvc_ioctl_call", setvalue);	
		
		mCamera[mCameraID].setParameters(mParameters[mCameraID]);
	}
	
	public void setUvcDqbufSize(int mCameraId,int w,int h){
		if (mCamera[mCameraId]==null||(!isOpenCamare[mCameraId])) {
			return;
		}
		mParameters[mCameraId] = mCamera[mCameraId].getParameters();
		mParameters[mCameraId].set("uvc_dqbuf_size",w+"x"+h );
		
		mCamera[mCameraId].setParameters(mParameters[mCameraId]);
	}
	public void getUvcExtenrnCall(int mCameraId){
		if (mCamera[mCameraId]==null||(!isOpenCamare[mCameraId])) {
			return;
		}
		 mParameters[mCameraId] = mCamera[mCameraId].getParameters();
		String valueuvc=mParameters[mCameraId].get("uvc_ioctl_call");
		
	}
	private boolean mReRequest=false;
	int [] value=null;
	@SuppressWarnings("deprecation")
	public int [] getUvcExtenrnCall(int mCameraId,String parameter,String parameter2,String parameter3){
		value=null;
	    String valueuvc = "";
		if (mCamera[mCameraId]==null||(!isOpenCamare[mCameraId])) {
			LogCatUtils.showString("=== ===");
			return value;
		}
		 mParameters[mCameraId] = mCamera[mCameraId].getParameters();
		 mParameters[mCameraId].set("uvc_ioctl_call", getValueOf(parameter)
				+"_"+getValueOf(parameter2));
		 mCamera[mCameraId].setParameters(mParameters[mCameraId]);
		 mParameters[mCameraId] = mCamera[mCameraId].getParameters();
		 if (Integer.valueOf(parameter2)==83) {
			 valueuvc=SystemProperties.get("sys_graphic.cam_uvc_data.status");
		}else {
			valueuvc=SystemProperties.get("sys_graphic.cam_uvc_data.fyt");
		}
		if (!valueuvc.isEmpty()) {
			value=getAscii(valueuvc);
		}else {
			if (mReRequest) {
				mReRequest=false;
				return value;
			}
			mReRequest=true;
			return getUvcExtenrnCall(mCameraId, parameter, parameter2,"");
		}
		mReRequest=false;
		return value;
	}
	
	
	private int[] getAscii(String cn){
		int []value=null;
		if (cn.isEmpty()) {
			return value;
		}
		byte[] bytes = cn.getBytes();
		if (bytes.length==0) {
			return value;
		}
		value=new int[bytes.length];
		
		for(int i=0;i<bytes.length;i++){
			int temp=bytes[i];
			if (temp==127) {
				temp=0;
			}else if (temp==126) {
				temp=59;
			}else if (temp==125) {
				temp=61;
			}
			value[i]=temp;
		}
		return value;
	  }
	@SuppressWarnings("deprecation")
	public void MyCarCollitionPhoto(int cameraId,final CapturePhotos callball){
		if (mCamera[cameraId]==null||(!isPreview[cameraId])) {
			if (callball==null) {
				return;
			}
			callball.capturePhoto(-1, null);
			return;
		}
		mCamera[cameraId].takePicture(null, null, new PictureCallback() {
			
			@Override
			public void onPictureTaken(byte[] arg0, Camera arg1) {
				try {
					if (arg0!=null&&arg0.length>0) {
						
						bitmap=BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
						if (mFileManager==null) {
							return;
						}
						mFileManager.saveCapturePhotos(bitmap,callball);
					}
					
				} catch (Exception e) {
					if (callball==null) {
						return;
					}
					callball.capturePhoto(-1, null);
				}
				
			}
		});
	}
    
	int []setDefuleValue;
	public synchronized boolean doStartPreView(final int cameraId,SurfaceTexture surfaces, boolean isPlayback){
		
		LogCatUtils.showString("doStartPreView");
		
		if (!isOpenCamare[cameraId]) {
			isRecord[cameraId]=false;
			isPreview[cameraId]=false;
			if (!doOpenCamera(cameraId)) {
				return false;
			}
		}
		try {
			mCamera[cameraId].setPreviewTexture(surfaces);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		SystemProperties.set("sys_graphic.cam_sfEnabled.ver", "true");
		mCamera[cameraId].startPreview();
		isPreview[cameraId]=true;
		
		return true;
	}
	
	public void startPreview(final int cameraID){
		LogCatUtils.showString(" startPreview ");
		if (mCamera[cameraID]!=null&&isOpenCamare[cameraID]) {
			mCamera[cameraID].startPreview();		
			isPreview[cameraID]=true;;
		}
	}
	public boolean startPreview(int cameraId,SurfaceHolder holder){
		if (mCamera[cameraId]!=null&&isOpenCamare[cameraId]) {
			if (!isOpenCamare[cameraId]) {
				isRecord[cameraId]=false;
				isPreview[cameraId]=false;
				if (!doOpenCamera(cameraId)) {
					return false;
				}
			}
			try {
				mCamera[cameraId].setPreviewDisplay(holder);
				SystemProperties.set("sys_graphic.cam_sfEnabled.ver", "true");
				mCamera[cameraId].startPreview();
				isPreview[cameraId]=true;
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		}
		return false;
	}
	@SuppressWarnings("deprecation")
	public void doStopPreview(){
		if (mCamera[0]!=null) {
			
			mCamera[0].stopPreview();
			
		}
		if (mCamera[1]!=null) {
			mCamera[1].stopPreview();
			
		}
	}
	
	@SuppressWarnings("deprecation")
	public void doStopPreview(int cameraId){
		if (mCamera[cameraId]!=null&&isOpenCamare[cameraId]) {
			mCamera[cameraId].stopPreview();
		}
		isPreview[cameraId]=false;
	}
	public boolean mCameraIsUse(int mCameraID){
		if (mCamera[mCameraID]==null) {
			return false;
		}
		if (!isOpenCamare[mCameraID]) {
			return false;
		}
		return true;
		
	}
	@SuppressWarnings("deprecation")
	public void doCloseCamera(int cameraId){
		if (!TheApp.mIsShengMaiIC) {
			TheApp.mApp.sendBroadCast(0);
		}
		UpSystemTimeForCamera.getInstance().stopTimer();
		if (TheApp.mIsShengMaiIC) {
			App4GGetRecordStatus.getInstance().stopTimer();
		}else {
			AppGetCameraSdCard.getInstance().stopTimer();
		}
		if (mCamera[cameraId]!=null) {
			if (isRecord[cameraId]) {
				mCamera[cameraId].stopPreview();
				mCamera[cameraId].release();
				isRecord[cameraId]=false;
				isPreview[cameraId]=false;
				mCamera[cameraId]=null;
			}
			else if(isPreview[cameraId]) {
				mCamera[cameraId].stopPreview();
				mCamera[cameraId].release();
				isPreview[cameraId]=false;
				mCamera[cameraId]=null;
			}else if (isOpenCamare[cameraId]) {
				mCamera[cameraId].release();
				mCamera[cameraId]=null;
				isOpenCamare[cameraId]=false;
			}
			if (mCamera[cameraId]!=null) {
				mCamera[cameraId].release();
				mCamera[cameraId]=null;
			}
		}
		isPreview[cameraId]=false;
		isOpenCamare[cameraId]=false;
		mCamera[cameraId]=null;
	}
	
	@SuppressWarnings("deprecation")
	public void doCloseCamera(){
//		for (int cameraId = 0; cameraId < Camera.getNumberOfCameras(); cameraId++) {
		for(int i=0;i<2;i++){
			doCloseCamera(i);
		}
	}
	public void upDataTime(int cameraID,int i,int[] mTimes) {
		
		if (mCamera[cameraID]==null||mFileManager==null) {
			return;
		}
		mParameters[cameraID]=mCamera[cameraID].getParameters();
		if (mTimes!=null&&mTimes.length==6) {
		mParameters[cameraID].set("uvc_ioctl_call", getValueOf("60")
				+"_"+getValueOf("1")+"_"+mTimes[0]+"_"+mTimes[1]
				+"_"+mTimes[2]+"_"+mTimes[3]
				+"_"+mTimes[4]+"_"+mTimes[5]);	
		mCamera[cameraID].setParameters(mParameters[cameraID]);
		}
	}
	
	
	public synchronized void recordLock(){
		if (TheApp.mIsShengMaiIC) {
			RainUvc.setGSensorStatus(1);
			return;
		}
		setUvcExtenrnCall(Config.SUPPLIER_CAMERA_SUNPLUS,
				String.valueOf(3), String.valueOf(1), String.valueOf(6));
	}
}
