package com.syu.dvr.widget;

import com.syu.dvr.R;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class SettingSwitch extends LinearLayout{
	
	private Switch mSwitch;
	public TextView mTittle;
	public String text ="";
	public int []setValue;
	private boolean mDirive;
	private boolean isAudioRec;
	private LinearLayout layout;
	private LinearLayout.LayoutParams params;
	private Context context;
	public SettingSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context=context;
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.setting_attrs);
		mDirive=typedArray.getBoolean(R.styleable.setting_attrs_isdirive, true);
		initLayout(context);
	}
	public SettingSwitch(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public void sendBroadcast(){
		Intent intent = new Intent();
		intent.setAction("com.syu.dvr.settingclick");
		context.sendBroadcast(intent);
	}
	private void initLayout(Context context){
		setOrientation(LinearLayout.VERTICAL);
		LayoutInflater fInflater=LayoutInflater.from(context);
		layout=(LinearLayout) fInflater.inflate(R.layout.item_switch_layout, null);
		addView(layout);
		mTittle=(TextView) layout.findViewById(R.id.item_title);
		mSwitch=(Switch) layout.findViewById(R.id.record_audio_change);
		mTittle.setText(text);
		if (mDirive) {
			View diri=new View(context);
			diri.setBackgroundResource(R.drawable.divide_view);
			params=new LinearLayout.LayoutParams
					(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.topMargin=10;
			params.leftMargin=5;
			params.rightMargin=5;
			addView(diri, params);
		}
		mSwitch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		mSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked!=isAudioRec) {
					updataSwitchCheck(isChecked);
				}
			}
		});
	}
	public void updataSwitchCheck(boolean isAudioRec){
		
	}
	public void updataLayout(boolean isAudioRec,int []setValue) {
		this.setValue=setValue;
		this.isAudioRec=isAudioRec;
		if (isAudioRec) {
			mSwitch.setChecked(true);
		}else {
			mSwitch.setChecked(false);
		}
	}
}
	