package com.syu.dvr.uihandle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.syu.dvr.R;
import com.syu.dvr.TheApp;
import com.syu.dvr.control.FytDvrTypeControl;
import com.syu.dvr.control.MultimediaControl;
import com.syu.dvr.factory.HideLayout;
import com.syu.dvr.utils.FinalChip;
import com.syu.dvr.utils.HandleConstant;
import com.syu.dvr.utils.PublicClass;
import com.syu.dvr.utils.RecordingStatus;
import com.syu.dvr.widget.StateSignLayout;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class MainActivityUiHandle extends HideLayout{
	
	private Button photo;
	private Button mRecoder;
	private Button mSetting;
	private Button mPlayblak;
	private Button mAudio;
	private Button mHelp;
	private Button lock;
	private LinearLayout mHideRecordLayout;
	private LinearLayout mLockShutterLayout;
	private StateSignLayout stateSignLayout;
	private int handleTiemSMH=0;
	private int handleTiemSS=0;
	private int getSystemTimeSMH=0;
	private int getSystemTimeSS=0;
	public MyClick oClick; 
	private MultimediaControl control;
	public boolean layoutShow=false;
	private boolean isRecording=false;
	public void registerUiLayout(View layout){
		initView(layout);
		initListener();
		initData();
		FytDvrTypeControl.getInstance().setmStateLayout(stateSignLayout);
	}
	private void initData() {
		// TODO Auto-generated method stub
		
		RecordingStatus	status=RecordingStatus.getInstance();
		setRecordButBg(status.getmRecordingStatus()==1);
		stateSignLayout.setRecordStatus(status.getmRecordingStatus()==1);
		stateSignLayout.setRecordLock(status.getmFileStatus()==1);
		stateSignLayout.setScardStatus(status.getmScardStatus()==0);
		stateSignLayout.setAudioRecord(status.isMisRecordAudio());
		stateSignLayout.setResolution(status.getmResolution());
		stateSignLayout.recordCallbace(new RecordCallback() {
			
			@Override
			public void callback(boolean i) {
				// TODO Auto-generated method stub
				setRecordButBg(i);
			}
		});
	}
	private void initView(View view) {
		mHideRecordLayout=(LinearLayout) view.findViewById(R.id.hide_record_layout);
		mRecoder=(Button)view.findViewById(R.id.start);
		photo=(Button)view.findViewById(R.id.shutter);
		mSetting=(Button)view.findViewById(R.id.setting);
		mPlayblak=(Button)view.findViewById(R.id.playblack);
		lock=(Button) view.findViewById(R.id.lock);
		mAudio=(Button) view.findViewById(R.id.audio_record);
		mAudio.setVisibility(View.GONE);
		mHelp=(Button) view.findViewById(R.id.help);
		mLockShutterLayout=(LinearLayout) view.findViewById(R.id.lock_shutter_layout);
		stateSignLayout = (StateSignLayout) view.findViewById(R.id.state_sign_layout);
		if (PublicClass.getInstance().isVerticalScreen()) {
			RelativeLayout.LayoutParams params=(android.widget.RelativeLayout.LayoutParams) stateSignLayout.getLayoutParams();
			params.topMargin=72;
			params=(LayoutParams) mLockShutterLayout.getLayoutParams();
			params.bottomMargin=154;
			params=(LayoutParams) mHideRecordLayout.getLayoutParams();
			params.bottomMargin=154;
		}
		RunTask();
		if (FytDvrTypeControl.getInstance().getPlatform()==FinalChip.CHIP_SG9832
				&&TheApp.mIsShengMaiIC) {
			mHelp.setVisibility(View.VISIBLE);
		}else {
			mHelp.setVisibility(View.GONE);
		}
	}
	private void initListener() {
		oClick=new MyClick();
		photo.setOnClickListener(oClick);
		mSetting.setOnClickListener(oClick);
		mPlayblak.setOnClickListener(oClick);
		mRecoder.setOnClickListener(oClick);
		lock.setOnClickListener(oClick);
		mAudio.setOnClickListener(oClick);
		mHelp.setOnClickListener(oClick);
	}
	@Override
	public void hideLayout(int id) {
		super.hideLayout(id);
		handler.sendEmptyMessage(id);
	}
	public void setRecordButBg(boolean mRocord){
		TheApp.mApp.sendBroadCast(0);
		if (mRocord) {
			isRecording=true;
			mRecoder.setBackgroundResource(R.drawable.d_uicommon_btncamcorderstop);
		}else{
			isRecording=false;
			mRecoder.setBackgroundResource(R.drawable.d_uicommon_btncamcorder);
		}
	}
	public Handler handler =new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HandleConstant.SHOW_LAYOUT:
				layoutShow=(mHideRecordLayout.getVisibility()==0);
				if (!layoutShow) {
					mHideRecordLayout.setVisibility(View.VISIBLE);
					RunTask();
				}else {
					mHideRecordLayout.setVisibility(View.GONE);
				}
				break;
			case HandleConstant.HIDE_MENU:
				mHideRecordLayout.setVisibility(View.GONE);
				break;
			default:
				break;
			}
			
		}
	};
	class MyClick implements OnClickListener{
		@Override
		public void onClick(View view) {
			
			if (!isCheckeEnable()) {
				TheApp.mApp.MyTosat(R.string.str_dvr_handler);
				return;
			}
			RunTask();
			control = FytDvrTypeControl.getInstance().getMultimediaControl();
			if (control==null) {
				return;
			}
			switch (view.getId()){
			case R.id.shutter:
				if (control!=null) {
					control.shutter();
					TheApp.mApp.MyTosat(R.string.str_text_photo);
				}
				break;
			case R.id.start:
				if (control!=null) {
					control.startRecord();
				}
				break;
			case R.id.lock:
				if (control !=null) {
					control.lock();
				}
				break;
			case R.id.setting:
				if (control!=null) {
					control.dvrSetting();
				}
				
				break;
			case R.id.playblack:
				
				control.playblack();
				break;
			case R.id.audio_record:
				
				break;
			case R.id.help:
				control.dvrHelp();
				break;
			default:
				break;
			
			}
		}
	}
	private boolean isCheckeEnable(){
    	boolean isCheckeEnable=false;
    	SimpleDateFormat sdf=new SimpleDateFormat("HHmmssSSS", Locale.getDefault());
    	String tempTime= sdf.format(new Date());
    	getSystemTimeSMH=Integer.valueOf(tempTime.substring(0, 6));
    	getSystemTimeSS=Integer.valueOf(tempTime.substring(6));
    	if (((Math.abs(getSystemTimeSMH-handleTiemSMH)*1000+getSystemTimeSS)-handleTiemSS)>1000) {
    		handleTiemSMH=getSystemTimeSMH;
    		handleTiemSS=getSystemTimeSS;
			isCheckeEnable= true;
		}else {
			isCheckeEnable=false;
		}
    	return isCheckeEnable;
    }
	public interface RecordCallback{
		public void callback(boolean i);
	}
}
