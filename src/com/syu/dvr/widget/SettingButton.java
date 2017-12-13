package com.syu.dvr.widget;

import com.syu.dvr.R;
import com.syu.dvr.TheApp;
import com.syu.dvr.activity.SystemUpgrade;
import com.syu.dvr.control.CameraManager;
import com.syu.dvr.control.FytDvrTypeControl;
import com.syu.dvr.control.StartActivity;
import com.syu.dvr.server.BootReceiver;
import com.syu.dvr.utils.Config;
import com.syu.dvr.utils.LogCatUtils;
import com.syu.dvr.utils.ProgressDialog;
import com.syu.dvr.utils.PublicClass;
import com.syu.dvr.utils.RecordingStatus;
import com.uvc.jni.RainUvc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingButton extends LinearLayout{
	
	private Button button ;
	private TextView mTittle;
	private String text ="";
	private LinearLayout layout;
	private LinearLayout.LayoutParams params;
	private Context context;
	private int cmdtyple;
	private TextView cancle;
	private TextView message;
	private TextView mSure;
	private  Dialog mFormatsdDialog;
	private String versionVvalue;
	private CameraManager mCManager;
	public SettingButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context=context;
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.setting_attrs);
		text= typedArray.getString(R.styleable.setting_attrs_text);
		cmdtyple=typedArray.getInteger(R.styleable.setting_attrs_cmdtyple, -1);
		LogCatUtils.showString(" cmdtyple "+cmdtyple);
		mCManager=FytDvrTypeControl.getInstance().getmCManager();
		initLayout(context);
	}
	public SettingButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	private void initLayout(Context context){
		setOrientation(LinearLayout.VERTICAL);
		LayoutInflater fInflater=LayoutInflater.from(context);
		layout=(LinearLayout) fInflater.inflate(R.layout.item_button_layout, null);
		addView(layout);
		mTittle=(TextView) layout.findViewById(R.id.item_title);
		button=(Button) layout.findViewById(R.id.item_guide_button);
		mTittle.setText(text);
		
		View diri=new View(context);
		diri.setBackgroundResource(R.drawable.divide_view);
		params=new LinearLayout.LayoutParams
				(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.topMargin=10;
		params.leftMargin=5;
		params.rightMargin=5;
		addView(diri, params);
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cmdtyple>0) {
					dialogHandler(cmdtyple);
				}else if (cmdtyple==0) {
					RecordingStatus status=RecordingStatus.getInstance();
					if (status.getmScardStatus()==0) {
						dialogHandler(0);
					}
					if (status.getmScardStatus()==1) {
						dialogHandler(2);
					}
				}
			}
		});
	}
	private void dialogHandler(final int mcount){
		int []size;
    	size=PublicClass.getInstance().getWindowManeger();
    	if (size==null||size.length!=2) {
    		size=new int[]{1024,600};
		}
		if (mFormatsdDialog==null) {
			ViewGroup.LayoutParams params= new ViewGroup.LayoutParams(size[0]*2/5, size[1]*2/5);
			View view =LinearLayout.inflate(TheApp.mApp, R.layout.format_sd_layout, null);
			mFormatsdDialog=new Dialog(TheApp.mApp,R.style.add_dialog);
			mFormatsdDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			mFormatsdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mFormatsdDialog.setContentView(view,params);
			cancle=(TextView) mFormatsdDialog.findViewById(R.id.dialog_cancle);
			message=(TextView) mFormatsdDialog.findViewById(R.id.dialog_messege);
			mTittle=(TextView) mFormatsdDialog.findViewById(R.id.dialog_tittle);
			mSure=(TextView) mFormatsdDialog.findViewById(R.id.dialog_sure);
			message.setMovementMethod(ScrollingMovementMethod.getInstance());
		}
		cancle.setVisibility(View.VISIBLE);
		cancle.setText(R.string.str_tip_cancle);
		message.setText(R.string.str_format_message_two);
		mTittle.setText(R.string.str_format_title);
		mSure.setText(R.string.str_tip_sure);
		message.setTextColor(TheApp.mApp.getResources().getColor(R.color.red));
		message.setGravity(Gravity.CENTER_VERTICAL);
		if (mcount==1) {
			mTittle.setText(R.string.str_format_title_two);
			message.setText(R.string.str_delete_all_file);
		}
		if (mcount==2) {
			message.setText(R.string.str_tip_no_sdcard);
			cancle.setVisibility(View.GONE);
		}
		if (mcount==3) {
			mTittle.setText(R.string.str_text_factory_setting);
			message.setText(R.string.str_clear_all_setting);
		}
		if (mcount==4) {
			mTittle.setText(R.string.str_system_upgrade);
			message.setText(R.string.system_upgrade);
		}
		if (mcount==5) {
			mTittle.setText(R.string.str_version_information);
			message.setText(TextUtils.isEmpty(versionVvalue)?"":versionVvalue);
			cancle.setVisibility(View.GONE);
			message.setTextColor(TheApp.mApp.getResources().getColor(R.color.white));
		}
		if (mcount==6) {
			mTittle.setText(R.string.about_software);
			String messe=TheApp.mApp.getString(R.string.about_software_message);
			String []mVersions=TheApp.mApp.getVersions();
			if (mVersions!=null&&mVersions.length>1) {
				messe=messe.replace("@#", TextUtils.isEmpty(mVersions[0])?"":"V"+mVersions[0]);
			}
			String[] mSup=TheApp.mApp.getResources().getStringArray(R.array.fyt_sup_usb_dvr);
			String mString="";
			if (mSup!=null&&mSup.length>0) {
				for (int i = 0; i < mSup.length; i++) {
					if (i==0) {
						mString+=mSup[i];
					}else {
						mString+=" . "+mSup[i];
					}
				}
			}
			if (!TextUtils.isEmpty(mString)) {
				messe=messe.replace("*@", mString);
			}
			cancle.setText(R.string.version_update);
			mSure.setText(R.string.the_official_website);
			message.setTextColor(TheApp.mApp.getResources().getColor(R.color.white));
			message.setText(messe);
		}
		cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mcount==6) {
					if (!PublicClass.getInstance().networkAvailable(TheApp.mApp)) {
						TheApp.mApp.MyTosat(R.string.netword_abnormal);
					}else {
						Intent intent=new Intent(BootReceiver.ACTION_UPGRADE);
						TheApp.mApp.sendBroadcast(intent);
					}
				}
				mFormatsdDialog.dismiss();
			}
		});
		mSure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LogCatUtils.showString("===count=="+mcount);
				if (mcount==0) {
					dialogHandler(1);
				}else if (mcount==1) {
					if (mCManager==null) {
						return;
					}
					if (TheApp.mIsShengMaiIC) {
						int ret=RainUvc.formatTFCard();
					}else {
						
						mCManager.getUvcExtenrnCall(Config.SUPPLIER_CAMERA_SUNPLUS,"a", "1", "1");
					}
					ProgressDialog.getInstance().progressShow(R.string.str_format_title_two);
					handler.sendEmptyMessageDelayed(2, 2*1000);
				}else if (mcount==3) {
					if (mCManager==null) {
						return;
					}
					if (TheApp.mIsShengMaiIC) {
						int ret=RainUvc.loadDefaultParam();
						if (ret>=0) {
							TheApp.mApp.MyTosat(R.string.factory_settings_fish);
						}
					}else {
						mCManager.doStopPreview(TheApp.getDeviceId());
						mCManager.factorySettings();
						handler.sendEmptyMessageDelayed(1, 2*1000);
						ProgressDialog.getInstance().progressShow(R.string.factory_settings);
					}
				}else if (mcount==4) {
					StartActivity.startAcitivity(SystemUpgrade.class);
				}else if (mcount==6) {
					if (!PublicClass.getInstance().networkAvailable(TheApp.mApp)) {
						TheApp.mApp.MyTosat(R.string.netword_abnormal);
					}else {
						Uri uri = Uri.parse("http://www.carsql.com");    
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);    
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						TheApp.mApp.startActivity(intent);  
					}
				}
				if (mcount!=0) {
					mFormatsdDialog.dismiss();
				}
			}
		});
		mFormatsdDialog.setCanceledOnTouchOutside(true);
		mFormatsdDialog.show();
	}
	private Handler handler=new Handler(Looper.getMainLooper()){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				TheApp.mApp.MyTosat(R.string.factory_settings_fish);
				sendBroadcast("com.syu.dvr.factory_settings");
				break;
			case 2:
				ProgressDialog.getInstance().progressdismiss();
				TheApp.mApp.MyTosat(R.string.fromat_sd_finish);
				
				break;
			default:
				break;
			}
			
		};
	};
	public void setVersionVvalue(String versionVvalue) {
		this.versionVvalue = versionVvalue;
	}
	private void sendBroadcast(String action){
		Intent intent = new Intent();
		intent.setAction(action);
		context.sendBroadcast(intent);
	}
}
	