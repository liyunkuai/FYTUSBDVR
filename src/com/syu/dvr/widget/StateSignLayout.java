package com.syu.dvr.widget;

import com.syu.dvr.R;
import com.syu.dvr.TheApp;
import com.syu.dvr.factory.RecordPromptFactory;
import com.syu.dvr.uihandle.MainActivityUiHandle.RecordCallback;
import com.syu.dvr.utils.LogCatUtils;
import com.syu.dvr.utils.RecordingStatus;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class StateSignLayout extends LinearLayout{
	private ImageView mConnect;
	private ImageView mSdcard;
	private ImageView mSoundRec;
	private ImageView mRecordTime;
	private ImageView mLock;
	private ImageView mResolution;
	private ImageView mRecordImage;
	private TextView mRecordTips;
	private RelativeLayout.LayoutParams params;
	private LinearLayout mParamsLayout;
	private RecordCallback callback;
	public StateSignLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		initLayout(context);
	}
	private void initLayout(Context context) {
		params=new RelativeLayout.LayoutParams(40,40);
		mRecordImage=new ImageView(context);
		params.setMargins(30, 0, 0, 0);
		addView(mRecordImage, params);
		
		params=new RelativeLayout.LayoutParams(200,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		mRecordTips=new TextView(context);
		mRecordTips.setText(R.string.recording_tips);
		params.setMargins(70, 0, 0, 0);
		mRecordTips.setTextSize(14);
		mRecordTips.setVisibility(GONE);
		addView(mRecordTips, params);
		
		params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		mParamsLayout=new LinearLayout(context);
		mParamsLayout.setOrientation(LinearLayout.HORIZONTAL);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
		mParamsLayout.setGravity(Gravity.RIGHT);
		addView(mParamsLayout, params);
	
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		mConnect=new ImageView(context);
		mConnect.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.power_connection));
		mParamsLayout.addView(mConnect, params);
		
		mSdcard=new ImageView(context);
		mSdcard.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_sdcard));
		mParamsLayout.addView(mSdcard, params);
		
		
		mSoundRec=new ImageView(context);
		mSoundRec.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sound_no_recording));
		mParamsLayout.addView(mSoundRec, params);
		if (!RecordingStatus.getInstance().isHasMic()) {
			mSoundRec.setVisibility(GONE);
		}
		
		mLock=new ImageView(context);
		mLock.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.video_locking_bg));
		mParamsLayout.addView(mLock, params);
		
		
		mRecordTime=new ImageView(context);
		mRecordTime.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.one_record_time));
		mParamsLayout.addView(mRecordTime, params);
		
		mResolution=new ImageView(context);
		mResolution.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mresolution_720));
		mParamsLayout.addView(mResolution, params);
	}
	public void setRecordTime(){
		int mTime=TheApp.mApp.getInt("RECORDING_TIME", 1);
		if (mTime==1) {
			mRecordTime.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.one_record_time));
		}else if (mTime==3) {
			mRecordTime.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.three_record_time));
		}else if (mTime==5) {
			mRecordTime.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.five_record_time));
		}
	}
	public  void setRecordStatus(boolean show){
		if (show) {
			handler.sendEmptyMessage(2);
			RecordPromptFactory.getInstance().RunTask(this);
		}else{
			handler.sendEmptyMessage(3);
			RecordPromptFactory.getInstance().stopTimer();
		}
	}
	public void recordCallbace(RecordCallback callback){
		this.callback=callback;
	}
	public void showPrompt(boolean show){
		handler.sendEmptyMessage(show?0:1);
	}
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				mRecordImage.setBackgroundResource(R.drawable.circle);
				break;
			case 1:
				mRecordImage.setBackgroundResource(R.drawable.crilmin);
				break;
			case 2:
				mRecordTime.setVisibility(VISIBLE);
				mRecordTips.setVisibility(VISIBLE);
				setRecordTime();
				settingSignout();
				if (callback!=null) {
					callback.callback(true);
				}
				break;
			case 3:
				mRecordTime.setVisibility(GONE);
				mRecordTips.setVisibility(GONE);
				mRecordImage.setBackgroundResource(R.drawable.circle2);
				if (callback!=null) {
					callback.callback(false);
				}
				break;
			case 4:
				mSoundRec.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sound_recording));
				break;
			case 5:
				mSoundRec.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sound_no_recording));
				break;
			case 6:
				mResolution.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mresolution_1080));
				break;
			case 7:
				mResolution.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mresolution_720));
				break;
			case 8:
				mSdcard.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_sdcard));
				break;
			case 9:
				mSdcard.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.has_sdcard));
				break;
			case 10:
				mLock.setVisibility(View.GONE);
				break;
			case 11:
				mLock.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		};
	};
	public void setAudioRecord(boolean value){
		if (value) {
			handler.sendEmptyMessage(4);
		}else{
			handler.sendEmptyMessage(5);
		}
	}
	public void setResolution(int status){
		LogCatUtils.showString("===status  = "+String.valueOf(status));
		if (mSoundRec.getVisibility()!=VISIBLE) {
			return;
		}
		if (status==0) {
			handler.sendEmptyMessage(6);
		}else {
			handler.sendEmptyMessage(7);
		}
	}
	public void setScardStatus(boolean sdcard){
		if (!sdcard) {
			handler.sendEmptyMessage(8);
		}else {
			handler.sendEmptyMessage(9);
		}
	}
	public void setRecordLock(boolean mrLock){
		if (!mrLock) {
			handler.sendEmptyMessage(10);
		}else  {
			handler.sendEmptyMessage(11);
		}
	}
	public void settingSignout(){
		Intent intent = new Intent();
		intent.setAction("com.syu.dvr.settingactivity.finish");
		intent.setFlags(32785);
		TheApp.mApp.sendBroadcast(intent);
	}
}
